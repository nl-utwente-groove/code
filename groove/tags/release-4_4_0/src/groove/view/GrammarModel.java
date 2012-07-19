/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id$
 */
package groove.view;

import static groove.trans.ResourceKind.CONTROL;
import static groove.trans.ResourceKind.HOST;
import static groove.trans.ResourceKind.PROLOG;
import static groove.trans.ResourceKind.RULE;
import static groove.trans.ResourceKind.TYPE;
import groove.control.CtrlFactory;
import groove.graph.DefaultGraph;
import groove.graph.GraphInfo;
import groove.graph.GraphRole;
import groove.graph.LabelStore;
import groove.graph.TypeGraph;
import groove.io.store.SystemStore;
import groove.io.store.SystemStoreFactory;
import groove.prolog.GrooveEnvironment;
import groove.trans.DefaultHostGraph;
import groove.trans.GraphGrammar;
import groove.trans.ResourceKind;
import groove.trans.Rule;
import groove.trans.SystemProperties;
import groove.util.Groove;
import groove.view.aspect.AspectGraph;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Grammar model based on a backing system store.
 */
public class GrammarModel implements Observer {
    /**
     * Constructs a grammar model from a rule system store. The start graph name
     * is the default one.
     * @see Groove#DEFAULT_START_GRAPH_NAME
     */
    public GrammarModel(SystemStore store) {
        this.store = store;
        setStartGraph(Groove.DEFAULT_START_GRAPH_NAME);
        for (ResourceKind resource : ResourceKind.all(false)) {
            syncResource(resource);
        }
    }

    /** Returns the name of the rule system. */
    public String getName() {
        return this.store.getName();
    }

    /** Returns the backing system store. */
    public SystemStore getStore() {
        return this.store;
    }

    /** Returns the system properties of this grammar model. */
    public SystemProperties getProperties() {
        SystemProperties result = this.store.getProperties();
        // take care that, if there is no explicit control program name,
        // either the control program name is set to the 
        // default if such a control program exists, or the useControl property
        // is set to false
        if (!this.controlPropertyAdjusted && result.isUseControl()
            && result.getControlName() == null) {
            if (getDefaultControlName() == null) {
                result.setUseControl(false);
            } else {
                result.setControlName(getDefaultControlName());
            }
            this.controlPropertyAdjusted = true;
        }
        return result;
    }

    /** Returns all names of grammar resources of a given kind. */
    public Set<String> getNames(ResourceKind kind) {
        if (kind == ResourceKind.PROPERTIES) {
            return null;
        } else if (kind.isTextBased()) {
            return getStore().getTexts(kind).keySet();
        } else {
            return getStore().getGraphs(kind).keySet();
        }
    }

    /** Returns the map from resource names to resource models of a given kind. */
    public Map<String,ResourceModel<?>> getResourceMap(ResourceKind kind) {
        return this.resourceMap.get(kind);
    }

    /** Returns the collection of resource models of a given kind. */
    public Collection<ResourceModel<?>> getResourceSet(ResourceKind kind) {
        return this.resourceMap.get(kind).values();
    }

    /** Returns a named resource model of a given kind. */
    public ResourceModel<?> getResource(ResourceKind kind, String name) {
        return getResourceMap(kind).get(name);
    }

    /**
     * Returns the control model associated with a given (named) control program.
     * @param name the name of the control program to return the model of;
     * @return the corresponding control program model, or <code>null</code> if
     *         no program by that name exists
     */
    public ControlModel getControlModel(String name) {
        return (ControlModel) getResource(CONTROL, name);
    }

    /**
     * Returns the name of the control program to be used if control is 
     * enabled, or {@code null} otherwise. This is taken
     * from the system properties.
     */
    public String getActiveControlName() {
        return getProperties().isUseControl()
                ? getProperties().getControlName() : null;
    }

    /**
     * Returns the control model set for the grammar.
     * @return the control model for the grammar, or <code>null</code> if there
     *         is no control program loaded.
     */
    public ControlModel getActiveControlModel() {
        return isUseControl() ? getControlModel(getActiveControlName()) : null;
    }

    /**
     * Returns the graph model for a given graph name.
     * @return the graph model for graph <code>name</code>, or <code>null</code>
     *         if there is no such graph.
     */
    public HostModel getHostModel(String name) {
        return (HostModel) getResourceMap(HOST).get(name);
    }

    /**
     * Returns the prolog model associated with a given (named) prolog program.
     * @param name the name of the prolog program to return the model of;
     * @return the corresponding prolog model, or <code>null</code> if
     *         no program by that name exists
     */
    public PrologModel getPrologModel(String name) {
        return (PrologModel) getResourceMap(PROLOG).get(name);
    }

    /**
     * Returns the rule model for a given rule name.
     * @return the rule model for rule <code>name</code>, or <code>null</code> if
     *         there is no such rule.
     */
    public RuleModel getRuleModel(String name) {
        return (RuleModel) getResourceMap(RULE).get(name);
    }

    /**
     * Returns the type graph model for a given graph name.
     * @return the type graph model for type <code>name</code>, or
     *         <code>null</code> if there is no such graph.
     */
    public TypeModel getTypeModel(String name) {
        return (TypeModel) getResourceMap(TYPE).get(name);
    }

    /**
     * Lazily creates the composite type model for this grammar.
     */
    public CompositeTypeModel getActiveTypeModel() {
        if (this.typeModel == null) {
            this.typeModel = new CompositeTypeModel(this);
        }
        return this.typeModel;
    }

    /**
     * Lazily creates the type graph for this grammar.
     * Returns {@code null} there is no type selected, or the type model has errors.
     */
    public TypeGraph getTypeGraph() {
        TypeGraph result = null;
        CompositeTypeModel model = getActiveTypeModel();
        if (model.isEnabled()) {
            try {
                result = model.toResource();
            } catch (FormatException e) {
                // the type graph is null
            }
        }
        return result;
    }

    /**
     * Returns the name of the start graph, if it is one of the graphs stored
     * with the rule system.
     * @return the name of the start graph, or <code>null</code> if the start
     *         graph is not one of the graphs stored with the rule system
     */
    public String getStartGraphName() {
        return this.startGraphName;
    }

    /**
     * Returns the start graph of this grammar model.
     * @return the start graph model, or <code>null</code> if no start graph is
     *         set.
     */

    public HostModel getStartGraphModel() {
        if (this.startGraph == null && this.startGraphName != null) {
            this.startGraph = getHostModel(this.startGraphName);
        }
        return this.startGraph;
    }

    /**
     * Sets the start graph to a given graph, or to <code>null</code>. This
     * implies the start graph is not one of the graphs stored in the rule
     * system; correspondingly, the start graph name is set to <code>null</code>
     * .
     * @param startGraph the new start graph; if <code>null</code>, the start
     *        graph is unset
     * @throws IllegalArgumentException if <code>startGraph</code> does not have
     *         a graph role
     * @see #setStartGraph(String)
     */
    public void setStartGraph(AspectGraph startGraph) {
        assert startGraph != null;
        if (startGraph.getRole() != GraphRole.HOST) {
            throw new IllegalArgumentException(String.format(
                "Prospective start graph '%s' is not a graph", startGraph));
        }
        this.startGraph = new HostModel(this, startGraph);
        this.startGraphName = null;
        invalidate();
    }

    /**
     * Sets the start graph to a given graph, or to <code>null</code>. This
     * implies the start graph is not one of the graphs stored in the rule
     * system; correspondingly, the start graph name is set to <code>null</code>
     * .
     * @param name the new start graph; if <code>null</code>, the start
     *        graph is unset
     * @throws IllegalArgumentException if <code>startGraph</code> does not have
     *         a graph role
     * @see #setStartGraph(String)
     */
    public void setStartGraph(String name) {
        assert name != null;
        this.startGraphName = name;
        invalidate();
    }

    /** Unsets the start graph. */
    public void removeStartGraph() {
        this.startGraph = null;
    }

    /** Collects and returns the permanent errors of the rule models. */
    public List<FormatError> getErrors() {
        if (this.errors == null) {
            initGrammar();
        }
        return this.errors;
    }

    /** Indicates if this grammar model has errors. */
    public boolean hasErrors() {
        return !getErrors().isEmpty();
    }

    /** Returns the labels occurring in this grammar model. */
    public final LabelStore getLabelStore() {
        return getActiveTypeModel().getLabelStore();
    }

    /** 
     * Returns the modification count of the model.
     * The modification count is increased any time the grammar is invalidated
     * due to a change in the store or start graph.
     * This method can be used by clients to check if the grammar has been invalidated.
     */
    public int getModificationCount() {
        return this.modificationCount;
    }

    /**
     * Converts the grammar model to a real grammar. With respect to control, we
     * recognise the following cases:
     * <ul>
     * <li>Control is enabled (which is the default case), but no control name
     * is set in the properties. Then we look for a control program by the name
     * of <code>control</code>; if that does not exist, we look for a control
     * program by the name of the grammar. If that does not exist either,
     * control is assumed to be disabled; the control name is implicitly set to
     * <code>control</code>.
     * <li>Control is enabled, and an explicit control name is set in the
     * properties. If a control program by that name exists, it is used. If no
     * such program exists, an error is raised.
     * <li>Control is disabled, but a control name is set. If a control program
     * by that name exists, it may be displayed but will not be used. If no such
     * control program exists, an error should be raised.
     * <li>Control is disabled, and no control name is set. No control will be
     * used; the control name is implicitly set to <code>control</code>.
     * </ul>
     */
    public GraphGrammar toGrammar() throws FormatException {
        if (this.errors == null) {
            initGrammar();
        }
        if (this.errors.isEmpty()) {
            return this.grammar;
        } else {
            throw new FormatException(this.errors);
        }
    }

    /**
     * Indicates if control is explicitly enabled.
     */
    public boolean isUseControl() {
        return getActiveControlName() != null;
    }

    /** 
     * Returns the default control name.
     * The first choice is {@link Groove#DEFAULT_CONTROL_NAME};
     * if that does not exist, the next choice is the grammar name.
     * If that does not exist either, the name is {@code null}.
     */
    // second default name: grammar name
    private String getDefaultControlName() {
        String result = null;
        if (getResourceMap(CONTROL).containsKey(Groove.DEFAULT_CONTROL_NAME)) {
            result = Groove.DEFAULT_CONTROL_NAME;
        } else if (getResourceMap(CONTROL).containsKey(getName())) {
            result = getName();
        }
        return result;
    }

    /** Initialises the {@link #grammar} and {@link #errors} fields. */
    private void initGrammar() {
        this.errors = new ArrayList<FormatError>();
        try {
            this.grammar = computeGrammar();
        } catch (FormatException exc) {
            this.errors.addAll(exc.getErrors());
        }
        getPrologEnvironment();
        for (ResourceModel<?> prologModel : getResourceSet(PROLOG)) {
            for (FormatError error : prologModel.getErrors()) {
                this.errors.add(new FormatError(
                    "Error in prolog program '%s': %s", prologModel.getName(),
                    error, prologModel));
            }
        }
    }

    /** Returns the set of enabled rules that do not have errors. */
    public Set<Rule> getRules() {
        Set<Rule> result = new HashSet<Rule>();
        // set rules
        for (ResourceModel<?> ruleModel : getResourceSet(RULE)) {
            try {
                // only add the enabled rules
                if (ruleModel.isEnabled()) {
                    result.add(((RuleModel) ruleModel).toRule());
                }
            } catch (FormatException exc) {
                // do not add this rule
            }
        }
        return result;
    }

    /**
     * Computes a graph grammar from this model.
     * @throws FormatException if there are syntax errors in the model
     */
    private GraphGrammar computeGrammar() throws FormatException {
        GraphGrammar result = new GraphGrammar(getName());
        List<FormatError> errors = new ArrayList<FormatError>();
        // Construct the composite type graph
        try {
            result.setType(getActiveTypeModel().toResource(),
                getActiveTypeModel().getTypeGraphMap());
        } catch (FormatException exc) {
            errors.addAll(exc.getErrors());
        }
        // set a label store (get it from the type graph if that exists)
        if (result.getType() == null) {
            result.setLabelStore(getLabelStore());
            errors.addAll(getLabelStore().getErrors());
        }
        // set rules
        for (ResourceModel<?> ruleModel : getResourceSet(RULE)) {
            try {
                // only add the enabled rules
                if (ruleModel.isEnabled()) {
                    result.add(((RuleModel) ruleModel).toRule());
                }
            } catch (FormatException exc) {
                for (FormatError error : exc.getErrors()) {
                    errors.add(new FormatError("Error in rule '%s': %s",
                        ruleModel.getName(), error, ruleModel.getSource()));
                }
            }
        }
        // set control
        if (isUseControl()) {
            ControlModel controlModel = getControlModel(getActiveControlName());
            if (controlModel == null) {
                errors.add(new FormatError(
                    "Control program '%s' cannot be found",
                    getActiveControlName()));
            } else if (result.hasMultiplePriorities()) {
                errors.add(new FormatError(
                    "Rule priorities and control programs are incompatible, please disable either."));
            } else {
                try {
                    result.setCtrlAut(controlModel.toCtrlAut());
                } catch (FormatException exc) {
                    for (FormatError error : exc.getErrors()) {
                        errors.add(new FormatError(
                            "Error in control program '%s': %s",
                            getActiveControlName(), error, controlModel));
                    }
                }
            }
        } else {
            result.setCtrlAut(CtrlFactory.instance().buildDefault(result));
        }
        // set properties
        result.setProperties(getProperties());
        // set start graph
        if (getStartGraphModel() == null) {
            if (getStartGraphName() == null) {
                errors.add(new FormatError("No start graph set"));
            } else {
                errors.add(new FormatError("Start graph '%s' cannot be loaded",
                    getStartGraphName()));
            }
        } else {
            List<FormatError> startGraphErrors;
            try {
                DefaultHostGraph startGraph = getStartGraphModel().toResource();
                result.setStartGraph(startGraph);
                startGraphErrors = GraphInfo.getErrors(startGraph);
            } catch (FormatException exc) {
                startGraphErrors = exc.getErrors();
            }
            for (FormatError error : startGraphErrors) {
                errors.add(new FormatError("Error in start graph: %s", error,
                    getStartGraphModel().getSource()));
            }
        }
        try {
            result.setFixed();
        } catch (FormatException exc) {
            errors.addAll(exc.getErrors());
        }
        if (errors.isEmpty()) {
            assert result.getCtrlAut() != null : "Grammar must have control";
            return result;
        } else {
            throw new FormatException(errors);
        }
    }

    /** 
     * Creates a Prolog environment that produces its standard output
     * on a the default {@link GrooveEnvironment} output stream.
     */
    public GrooveEnvironment getPrologEnvironment() {
        if (this.prologEnvironment == null) {
            this.prologEnvironment = new GrooveEnvironment(null, null);
            for (ResourceModel<?> model : getResourceSet(PROLOG)) {
                PrologModel prologModel = (PrologModel) model;
                try {
                    this.prologEnvironment.loadProgram(prologModel.getProgram());
                    prologModel.clearErrors();
                } catch (FormatException e) {
                    prologModel.setErrors(e.getErrors());
                }
            }
        }
        return this.prologEnvironment;
    }

    /**
     * Resets the {@link #grammar} and {@link #errors} objects, making sure that
     * they are regenerated at a next call of {@link #toGrammar()}.
     */
    private void invalidate() {
        this.modificationCount++;
        this.grammar = null;
        this.errors = null;
        this.controlPropertyAdjusted = false;
        if (this.startGraphName != null) {
            this.startGraph = null;
        }
    }

    @Override
    public void update(Observable source, Object edit) {
        Set<ResourceKind> change = ((SystemStore.Edit) edit).getChange();
        for (ResourceKind resource : change) {
            syncResource(resource);
        }
        invalidate();
    }

    /**
     * Synchronises the resources in the grammar model with the underlying store.
     * @param resource the kind of resources to be synchronised
     */
    private void syncResource(ResourceKind resource) {
        switch (resource) {
        case PROLOG:
            this.prologEnvironment = null;
            break;
        case PROPERTIES:
            return;
        }
        // update the set of resource models
        Map<String,ResourceModel<?>> modelMap = this.resourceMap.get(resource);
        Map<String,? extends Object> sourceMap;
        if (resource.isGraphBased()) {
            sourceMap = getStore().getGraphs(resource);
        } else {
            sourceMap = getStore().getTexts(resource);
        }
        // restrict the resources to those whose names are in the store
        modelMap.keySet().retainAll(sourceMap.keySet());
        // now synchronise the models with the sources in the store
        for (Map.Entry<String,? extends Object> sourceEntry : sourceMap.entrySet()) {
            String name = sourceEntry.getKey();
            ResourceModel<?> model = modelMap.get(name);
            if (model == null || model.getSource() != sourceEntry.getValue()) {
                modelMap.put(name, createModel(resource, name));
            }
        }
    }

    /** Callback method to create a model for a named resource. */
    private ResourceModel<?> createModel(ResourceKind kind, String name) {
        ResourceModel<?> result = null;
        if (kind.isGraphBased()) {
            AspectGraph graph = getStore().getGraphs(kind).get(name);
            if (graph != null) {
                result = createGraphModel(graph);
            }
        } else {
            assert kind.isTextBased();
            String text = getStore().getTexts(kind).get(name);
            if (text != null) {
                switch (kind) {
                case CONTROL:
                    result = new ControlModel(this, name, text);
                    break;
                case PROLOG:
                    result = new PrologModel(this, name, text);
                    break;
                default:
                    assert false;
                }
            }
        }
        return result;
    }

    /**
     * Creates a graph-based resource model for a given graph.
     */
    public GraphBasedModel<?> createGraphModel(AspectGraph graph) {
        GraphBasedModel<?> result = null;
        switch (graph.getRole()) {
        case HOST:
            result = new HostModel(this, graph);
            break;
        case RULE:
            result = new RuleModel(this, graph);
            break;
        case TYPE:
            result = new TypeModel(this, graph);
            break;
        default:
            assert false;
        }
        return result;
    }

    /** Mapping from resource kinds and names to resource models. */
    private final Map<ResourceKind,SortedMap<String,ResourceModel<?>>> resourceMap =
        new EnumMap<ResourceKind,SortedMap<String,ResourceModel<?>>>(
            ResourceKind.class);
    {
        for (ResourceKind kind : EnumSet.allOf(ResourceKind.class)) {
            this.resourceMap.put(kind, new TreeMap<String,ResourceModel<?>>());
        }
    }

    /** The store backing this model. */
    private final SystemStore store;
    /** Counter of the number of invalidations of the grammar. */
    private int modificationCount;
    /** 
     * Flag indicating that the system properties
     * have been adjusted to reflect the presence of a control program
     * with a default name.
     * @see #getProperties()
     */
    private boolean controlPropertyAdjusted;
    /** The start graph of the grammar. */
    private HostModel startGraph;
    /**
     * Name of the current start graph, if it is one of the graphs in this rule
     * system; <code>null</code> otherwise.
     */
    private String startGraphName;
    /** Possibly empty list of errors found in the conversion to a grammar. */
    private List<FormatError> errors;
    /** The graph grammar derived from the rule models. */
    private GraphGrammar grammar;
    /** The prolog environment derived from the system store. */
    private GrooveEnvironment prologEnvironment;
    /** The type model composed from the individual elements. */
    private CompositeTypeModel typeModel;

    /**
     * Creates an instance based on a store located at a given URL.
     * @param url the URL to load the grammar from
     * @throws IllegalArgumentException if no store can be created from the
     *         given URL
     * @throws IOException if a store can be created but not loaded
     */
    static public GrammarModel newInstance(URL url)
        throws IllegalArgumentException, IOException {
        return newInstance(url, url.getQuery());
    }

    /**
     * Creates an instance based on a store located at a given URL, with a given
     * start graph.
     * @param url the URL to load the grammar from
     * @param startGraphName the start graph name; if <code>null</code>, the
     *        default start graph name is used
     * @throws IllegalArgumentException if no store can be created from the
     *         given URL
     * @throws IOException if a store can be created but not loaded
     */
    static public GrammarModel newInstance(URL url, String startGraphName)
        throws IllegalArgumentException, IOException {
        SystemStore store = SystemStoreFactory.newStore(url);
        store.reload();
        GrammarModel result = store.toGrammarModel();
        if (startGraphName != null) {
            if (result.getNames(HOST).contains(startGraphName)) {
                result.setStartGraph(startGraphName);
            } else {
                DefaultGraph plainGraph = Groove.loadGraph(startGraphName);
                if (plainGraph == null) {
                    throw new IOException(String.format(
                        "Cannot load start graph %s", startGraphName));
                } else {
                    result.setStartGraph(AspectGraph.newInstance(plainGraph));
                }
            }
        }
        return result;
    }

    /**
     * Creates an instance based on a given file.
     * @param file the file to load the grammar from
     * @param create if <code>true</code> and <code>file</code> does not yet
     *        exist, attempt to create it.
     * @throws IOException if an error occurred while creating the store
     */
    static public GrammarModel newInstance(File file, boolean create)
        throws IOException {
        return newInstance(file, null, create);
    }

    /**
     * Creates an instance based on a given file and start graph name.
     * @param file the file to load the grammar from
     * @param startGraphName the start graph name; if <code>null</code>, the
     *        default start graph name is used
     * @param create if <code>true</code> and <code>file</code> does not yet
     *        exist, attempt to create it.
     * @throws IOException if an error occurred while creating the store
     */
    static public GrammarModel newInstance(File file, String startGraphName,
            boolean create) throws IOException {
        SystemStore store = SystemStoreFactory.newStore(file, create);
        store.reload();
        GrammarModel result = store.toGrammarModel();
        if (startGraphName != null) {
            result.setStartGraph(startGraphName);
        }
        return result;
    }

    /**
     * Creates an instance based on a given location, which is given either as a
     * URL or as a filename.
     * @param location the location to load the grammar from
     * @throws IllegalArgumentException if no store can be created from the
     *         given location
     * @throws IOException if a store can be created but not loaded
     */
    static public GrammarModel newInstance(String location)
        throws IllegalArgumentException, IOException {
        try {
            return newInstance(new URL(location));
        } catch (IllegalArgumentException exc) {
            return newInstance(new File(location), false);
        } catch (IOException exc) {
            return newInstance(new File(location), false);
        }
    }
}