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
import groove.control.CtrlAut;
import groove.explore.Exploration;
import groove.graph.GraphInfo;
import groove.graph.GraphRole;
import groove.graph.TypeGraph;
import groove.graph.TypeNode;
import groove.gui.layout.JVertexLayout;
import groove.gui.layout.LayoutMap;
import groove.io.store.SystemStore;
import groove.io.store.SystemStoreFactory;
import groove.prolog.GrooveEnvironment;
import groove.trans.GraphGrammar;
import groove.trans.HostGraph;
import groove.trans.QualName;
import groove.trans.ResourceKind;
import groove.trans.Rule;
import groove.trans.SystemProperties;
import groove.trans.SystemProperties.Key;
import groove.util.Groove;
import groove.view.aspect.AspectEdge;
import groove.view.aspect.AspectGraph;
import groove.view.aspect.AspectNode;

import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Grammar model based on a backing system store.
 */
public class GrammarModel implements Observer {
    /**
     * Constructs a grammar model from a rule system store, using the start
     * graph(s) that are stored in the grammar properties.
     */
    public GrammarModel(SystemStore store) {
        this.store = store;
        for (ResourceKind resource : ResourceKind.all(false)) {
            syncResource(resource);
        }
        this.startGraphs = new HashSet<String>();
        reloadStartGraphs(false);
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
        return this.store.getProperties();
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

    /** Returns a named graph-based resource model of a given kind. */
    public GraphBasedModel<?> getGraphResource(ResourceKind kind, String name) {
        assert kind.isGraphBased() : String.format(
            "Resource kind %s is not graph-based", kind);
        return (GraphBasedModel<?>) getResourceMap(kind).get(name);
    }

    /** Returns a named text-based resource model of a given kind. */
    public TextBasedModel<?> getTextResource(ResourceKind kind, String name) {
        assert kind.isTextBased() : String.format(
            "Resource kind %s is not text-based", kind);
        return (TextBasedModel<?>) getResourceMap(kind).get(name);
    }

    /** Returns a named resource model of a given kind. */
    public ResourceModel<?> getResource(ResourceKind kind, String name) {
        assert name != null;
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
    public Set<String> getControlNames() {
        return getProperties().getControlNames();
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
    public CompositeTypeModel getTypeModel() {
        if (this.typeModel == null) {
            this.typeModel = new CompositeTypeModel(this);
        }
        return this.typeModel;
    }

    /**
     * Lazily creates the composite control model for this grammar.
     */
    public CompositeControlModel getControlModel() {
        if (this.controlModel == null) {
            this.controlModel = new CompositeControlModel(this);
        }
        return this.controlModel;
    }

    /**
     * Lazily creates the type graph for this grammar.
     * @return the explicit or implicit type graph of the grammar
     */
    public TypeGraph getTypeGraph() {
        return getTypeModel().getTypeGraph();
    }

    /**
     * Checks if the given (host graph) name is (a component of) the currently
     * selected start graph.  
     */
    public boolean isStartGraphComponent(String name) {
        return this.startGraphs.contains(name);
    }

    /** Gets the names of the graphs that currently make up the start graph. */
    public Set<String> getStartGraphs() {
        return Collections.unmodifiableSet(this.startGraphs);
    }

    /**
     * Loads or reloads the start graphs from the properties file. The boolean
     * flag indicates whether or not {@link #invalidate} should be called
     * afterwards.
     */
    private void reloadStartGraphs(boolean invalidate) {
        // do nothing if there is an external start graph
        if (this.externalStartGraph) {
            return;
        }
        // else, reload names from property file
        this.startGraphs.clear();
        Set<String> startGraphNames = getProperties().getStartGraphNames();
        if (!startGraphNames.isEmpty()) {
            this.startGraphs.addAll(startGraphNames);
        } else {
            if (getResource(ResourceKind.HOST, Groove.DEFAULT_START_GRAPH_NAME) != null) {
                this.startGraphs.add(Groove.DEFAULT_START_GRAPH_NAME);
            }
        }
        // invalidate grammar if necessary
        if (invalidate) {
            this.startGraphModel = null;
            invalidate();
        }
    }

    /** 
     * Loads or reloads the start graphs from the properties file, and
     * recomputes the model associated with it.
     */
    public void reloadStartGraphs() {
        reloadStartGraphs(true);
    }

    /** 
     * Sets the start graphs, but does not change the associated grammar
     * property in the system store.
     * @see #localSetStartGraph
     */
    public void localSetStartGraphs(Set<String> names) {
        if (names == null) {
            return;
        }
        this.startGraphs.clear();
        this.startGraphs.addAll(names);
        this.startGraphModel = null;
        this.externalStartGraph = false;
        invalidate();
    }

    /**
     * Sets the start graphs to a single graph, but does not change the
     * associated property in the system store.
     * @see #localSetStartGraphs
     */
    public void localSetStartGraph(String name) {
        if (name == null) {
            return;
        }
        this.startGraphs.clear();
        this.startGraphs.add(name);
        this.startGraphModel = null;
        this.externalStartGraph = false;
        invalidate();
    }

    /**
     * Removes the given names from the start graphs, and propagates the change
     * to the grammar properties.
     */
    public void removeStartGraphs(Set<String> names) throws IOException {
        if (names != null) {
            if (this.startGraphs.removeAll(names)) {
                this.startGraphModel = null;
                this.externalStartGraph = false;
                getProperties().setStartGraphNames(this.startGraphs);
                getStore().putProperties(getProperties());
                invalidate();
            }
        }
    }

    /**
     * Renames a start graph, and propagates the change to the grammar
     * properties.
     */
    public void renameStartGraph(String oldName, String newName)
        throws IOException {
        if (this.startGraphs.remove(oldName)) {
            this.startGraphs.add(newName);
            getProperties().setStartGraphNames(this.startGraphs);
            getStore().putProperties(getProperties());
            this.startGraphModel = null;
            this.externalStartGraph = false;
            invalidate();
        }
    }

    /**
     * Sets the start graphs to a single graph, and propagates the change to
     * the grammar properties.
     */
    public void setStartGraph(String name) throws IOException {
        if (name == null) {
            return;
        }
        this.startGraphs.clear();
        this.startGraphs.add(name);
        getProperties().setStartGraphNames(this.startGraphs);
        getStore().putProperties(getProperties());
        this.startGraphModel = null;
        this.externalStartGraph = false;
        invalidate();
    }

    /**
     * Toggles the given names in the start graphs, and propagates the change
     * to the grammar properties.
     */
    public void toggleStartGraphs(Set<String> names) throws IOException {
        if (names != null) {
            for (String name : names) {
                if (!this.startGraphs.remove(name)) {
                    this.startGraphs.add(name);
                }
            }
            this.startGraphModel = null;
            this.externalStartGraph = false;
            getProperties().setStartGraphNames(this.startGraphs);
            getStore().putProperties(getProperties());
            invalidate();
        }
    }

    /**
     * Returns the start graph of this grammar model.
     * @return the start graph model, or <code>null</code> if no start graph is
     *         set.
     */

    public HostModel getStartGraphModel() {
        if (this.startGraphModel == null && !this.startGraphs.isEmpty()) {
            this.startGraphModel = combineGraphs(this.startGraphs);
        }
        return this.startGraphModel;
    }

    /**
     * Sets the start graph to a given graph. This implies that the start graph
     * is not one of the graphs stored in the rule system.
     * @param startGraph the new start graph; may not be {@code null}
     * @throws IllegalArgumentException if <code>startGraph</code> does not have
     *         a graph role
     */
    public HostModel setStartGraph(AspectGraph startGraph) {
        assert startGraph != null;
        if (startGraph.getRole() != GraphRole.HOST) {
            throw new IllegalArgumentException(String.format(
                "Prospective start graph '%s' is not a graph", startGraph));
        }
        this.startGraphModel = new HostModel(this, startGraph);
        this.externalStartGraph = true;
        invalidate();
        return this.startGraphModel;
    }

    /** Collects and returns the permanent errors of the rule models. */
    public FormatErrorSet getErrors() {
        if (this.errors == null) {
            initGrammar();
        }
        return this.errors;
    }

    /** Indicates if this grammar model has errors. */
    public boolean hasErrors() {
        return !getErrors().isEmpty();
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
        this.errors.throwException();
        return this.grammar;
    }

    /** Initialises the {@link #grammar} and {@link #errors} fields. */
    private void initGrammar() {
        this.errors = new FormatErrorSet();
        try {
            this.grammar = computeGrammar();
        } catch (FormatException exc) {
            this.errors.addAll(exc.getErrors());
        }
        getPrologEnvironment();
        for (ResourceModel<?> prologModel : getResourceSet(PROLOG)) {
            for (FormatError error : prologModel.getErrors()) {
                this.errors.add("Error in prolog program '%s': %s",
                    prologModel.getFullName(), error, prologModel);
            }
        }
        // check if all resource names are valid identifiers
        for (ResourceKind kind : ResourceKind.all(false)) {
            for (ResourceModel<?> model : getResourceSet(kind)) {
                if (!QualName.isValid(model.getFullName(), null, null)) {
                    this.errors.add(new FormatError(kind.getName() + " name '"
                        + model.getFullName() + "' "
                        + "is an illegal identifier", model));
                }
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
                    result.add(((RuleModel) ruleModel).toResource());
                }
            } catch (FormatException exc) {
                // do not add this rule
            }
        }
        return result;
    }

    /** Checks if this grammar has rules (maybe with errors). */
    public boolean hasRules() {
        return !getResourceSet(RULE).isEmpty();
    }

    /**
     * Computes a graph grammar from this model.
     * @throws FormatException if there are syntax errors in the model
     */
    private GraphGrammar computeGrammar() throws FormatException {
        GraphGrammar result = new GraphGrammar(getName());
        FormatErrorSet errors = new FormatErrorSet();
        // Construct the composite type graph
        result.setTypeGraph(getTypeGraph());
        errors.addAll(getTypeModel().getErrors());
        // set rules
        for (ResourceModel<?> ruleModel : getResourceSet(RULE)) {
            try {
                // only add the enabled rules
                if (ruleModel.isEnabled()) {
                    result.add(((RuleModel) ruleModel).toResource());
                }
            } catch (FormatException exc) {
                for (FormatError error : exc.getErrors()) {
                    errors.add("Error in rule '%s': %s",
                        ruleModel.getFullName(), error, ruleModel.getSource());
                }
            }
        }
        // set control
        CtrlAut control = getControlModel().toResource();
        if (result.hasMultiplePriorities() && !control.isDefault()) {
            errors.add("Disable either rule priorities or the explicit control programs");
        }
        result.setCtrlAut(control);
        // set properties
        result.setProperties(getProperties());
        // set start graph
        if (getStartGraphModel() == null) {
            if (this.startGraphs.isEmpty()) {
                errors.add("No start graph set");
            } else {
                errors.add("Start graph '%s' cannot be loaded",
                    this.startGraphs);
            }
        } else {
            FormatErrorSet startGraphErrors;
            try {
                HostGraph startGraph = getStartGraphModel().toResource();
                result.setStartGraph(startGraph);
                startGraphErrors = GraphInfo.getErrors(startGraph);
            } catch (FormatException exc) {
                startGraphErrors = exc.getErrors();
            }
            for (FormatError error : startGraphErrors) {
                errors.add("Error in start graph: %s", error,
                    getStartGraphModel().getSource());
            }
        }
        // Set the Prolog environment.
        result.setPrologEnvironment(this.getPrologEnvironment());
        errors.throwException();
        assert result.getCtrlAut() != null : "Grammar must have control";
        result.setFixed();
        return result;
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
                if (model.isEnabled()) {
                    try {
                        this.prologEnvironment.loadProgram(prologModel.getProgram());
                        prologModel.clearErrors();
                    } catch (FormatException e) {
                        prologModel.setErrors(e.getErrors());
                    }
                }
            }
        }
        return this.prologEnvironment;
    }

    /**
     * Resets the {@link #grammar} and {@link #errors} objects, making sure that
     * they are regenerated at a next call of {@link #toGrammar()}.
     * Also explicitly recomputes the start graph model.
     */
    private void invalidate() {
        this.modificationCount++;
        this.grammar = null;
        this.errors = null;
        getStartGraphModel(); // recompute start graph model, if necessary
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

    /**
     * Returns the default exploration, based on the {@link Key#EXPLORATION}
     * value in the system properties.
     */
    public Exploration getDefaultExploration() {
        if (getProperties().getExploration() == null) {
            return null;
        } else {
            try {
                return Exploration.parse(getProperties().getExploration());
            } catch (FormatException e) {
                return null;
            }
        }
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
    /** The start graph of the grammar. */
    private HostModel startGraphModel;
    /** Flag to indicate if the start graph is external. */
    private boolean externalStartGraph = false;
    /** Names of the host graphs that make up the current start graph. */
    private final Set<String> startGraphs;
    /** Possibly empty list of errors found in the conversion to a grammar. */
    private FormatErrorSet errors;
    /** The graph grammar derived from the rule models. */
    private GraphGrammar grammar;
    /** The prolog environment derived from the system store. */
    private GrooveEnvironment prologEnvironment;
    /** The type model composed from the individual elements. */
    private CompositeTypeModel typeModel;
    /** The control model composed from the individual control programs. */
    private CompositeControlModel controlModel;

    /**
     * Creates an instance based on a store located at a given URL.
     * @param url the URL to load the grammar from
     * @throws IllegalArgumentException if no store can be created from the
     *         given URL
     * @throws IOException if a store can be created but not loaded
     */
    static public GrammarModel newInstance(URL url)
        throws IllegalArgumentException, IOException {
        SystemStore store = SystemStoreFactory.newStore(url);
        store.reload();
        GrammarModel result = store.toGrammarModel();
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
        SystemStore store = SystemStoreFactory.newStore(file, create);
        store.reload();
        GrammarModel result = store.toGrammarModel();
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

    // ========================================================================
    // AUXILIARIES FOR COMBINING/MAINTAINING START GRAPHS
    // ========================================================================

    /**
     * Computes the union of multiple selected start graphs.
     */
    private HostModel combineGraphs(Set<String> unsortedNames) {

        // Do not compute if errors exist in grammar.
        if (hasErrors()) {
            return null;
        }

        // Sort the names.
        TreeSet<String> names = new TreeSet<String>(unsortedNames);

        // Get the host models, and compute their layout boundaries.
        List<HostModel> hostModels = new ArrayList<HostModel>();
        List<Point.Double> dimensions = new ArrayList<Point.Double>();
        double globalMaxX = 0;
        double globalMaxY = 0;
        int nr_names = names.size();
        for (String name : names) {
            ResourceModel<?> model = getResource(ResourceKind.HOST, name);
            if (model == null) {
                String msg = "Start graph '" + name + "' does not exist.";
                this.errors.add(new FormatError(msg));
                continue;
            }
            if (model.hasErrors()) {
                String msg = "Start graph '" + name + "' has errors.";
                this.errors.add(new FormatError(msg, model.getSource()));
                continue;
            }
            HostModel hostModel = (HostModel) model;
            hostModels.add(hostModel);
            AspectGraph graph = hostModel.getSource();
            if (nr_names < 2) {
                continue; // don't compute layout if 0 or 1 graphs are enabled
            }
            LayoutMap<AspectNode,AspectEdge> layoutMap =
                graph.getInfo().getLayoutMap();
            double maxX = 0;
            double maxY = 0;
            for (AspectNode node : graph.nodeSet()) {
                JVertexLayout layout = layoutMap.nodeMap().get(node);
                if (layout != null) {
                    maxX =
                        Math.max(maxX, layout.getBounds().getX()
                            + layout.getBounds().getWidth());
                    maxY =
                        Math.max(maxY, layout.getBounds().getY()
                            + layout.getBounds().getHeight());
                }
            }
            dimensions.add(new Point.Double(maxX, maxY));
            globalMaxX = Math.max(globalMaxX, maxX);
            globalMaxY = Math.max(globalMaxY, maxY);
        }

        // Do not combine if errors exist, or only 0 or 1 graphs are enabled.
        if (hasErrors() || nr_names == 0) {
            return null;
        }
        if (nr_names == 1) {
            return hostModels.iterator().next();
        }

        // Create the combined graph.
        StringBuilder result = new StringBuilder();
        Iterator<String> iterator = names.iterator();
        result.append(iterator.next());
        while (iterator.hasNext()) {
            result.append("_");
            result.append(iterator.next());
        }
        AspectGraph combined =
            new AspectGraph(result.toString(), GraphRole.HOST);
        LayoutMap<AspectNode,AspectEdge> layoutMap =
            new LayoutMap<AspectNode,AspectEdge>();

        // Local bookkeeping.
        int nodeNr = 0;
        int index = 0;
        double offsetX = 0;
        double offsetY = 0;
        Map<AspectNode,AspectNode> nodeMap =
            new HashMap<AspectNode,AspectNode>();
        Map<String,AspectNode> sharedNodes = new HashMap<String,AspectNode>();
        Map<String,TypeNode> sharedTypes = new HashMap<String,TypeNode>();

        // Copy the graphs one by one into the combined graph.
        for (HostModel hostModel : hostModels) {
            AspectGraph graph = hostModel.getSource();
            nodeMap.clear();
            // Copy the nodes.
            for (AspectNode node : graph.nodeSet()) {
                AspectNode fresh;
                if (node.getId() != null) {
                    String id = node.getId().getContentString();
                    TypeNode myType =
                        hostModel.getTypeMap().nodeMap().get(node);
                    if (sharedNodes.containsKey(id)) {
                        if (!sharedTypes.get(id).equals(myType)) {
                            this.errors.add(new FormatError(
                                "Cannot merge start graphs: node identity '"
                                    + id + "' is used with different types ["
                                    + myType.toString() + " and "
                                    + sharedTypes.get(id) + "]."));
                            return null;
                        }
                        nodeMap.put(node, sharedNodes.get(id));
                        continue;
                    } else {
                        fresh = node.clone(nodeNr++);
                        sharedNodes.put(id, fresh);
                        sharedTypes.put(id, myType);
                    }
                } else {
                    fresh = node.clone(nodeNr++);
                }
                layoutMap.copyNodeWithOffset(fresh, node,
                    graph.getInfo().getLayoutMap(), offsetX, offsetY);
                nodeMap.put(node, fresh);
                combined.addNode(fresh);
            }
            // Copy the edges.
            for (AspectEdge edge : graph.edgeSet()) {
                AspectEdge fresh =
                    new AspectEdge(nodeMap.get(edge.source()), edge.label(),
                        nodeMap.get(edge.target()));
                layoutMap.copyEdgeWithOffset(fresh, edge,
                    graph.getInfo().getLayoutMap(), offsetX, offsetY);
                combined.addEdge(fresh);
            }
            // Move the offsets.
            if (globalMaxX > globalMaxY) {
                offsetY = offsetY + dimensions.get(index).getY() + 50;
            } else {
                offsetX = offsetX + dimensions.get(index).getX() + 50;
            }
            index++;
        }

        // Finalize combined graph.
        GraphInfo.setLayoutMap(combined, layoutMap);
        combined.setFixed();
        return new HostModel(this, combined);
    }

    // ========================================================================
    // ENUM: MANIPULATION
    // ========================================================================

    /**
     * A {@link Manipulation} distinguishes between different kinds of set
     * update operations that can be applied to the set of selected start
     * graphs.
     */
    public static enum Manipulation {
        /** Add elements to the set. */
        ADD,
        /** Remove elements from the set. */
        REMOVE,
        /** Clears set, and adds all elements. */
        SET,
        /** Add elements that are not part of the set, and removes others. */
        TOGGLE;

        /** 
         * Apply a manipulation action. The boolean return value indicates if
         * the set was changed as a result of this operation.
         */
        public static boolean apply(Set<String> set, Manipulation manipulation,
                Set<String> selected) {
            switch (manipulation) {
            case ADD:
                return set.addAll(selected);
            case REMOVE:
                return set.removeAll(selected);
            case SET:
                boolean changed = set.equals(selected);
                set.clear();
                set.addAll(selected);
                return changed;
            case TOGGLE:
                for (String text : selected) {
                    if (!set.remove(text)) {
                        set.add(text);
                    }
                }
                return !selected.isEmpty();
            default:
                return false;
            }
        }

        /**
         * Convenience method for applying a manipulation on a singleton
         * value. Inefficient.
         */
        public static boolean apply(Set<String> set, Manipulation manipulation,
                String selected) {
            Set<String> temp = new HashSet<String>();
            temp.add(selected);
            return apply(set, manipulation, temp);
        }
    }
}
