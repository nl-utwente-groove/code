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
package groove.grammar.model;

import static groove.grammar.model.ResourceKind.CONTROL;
import static groove.grammar.model.ResourceKind.GROOVY;
import static groove.grammar.model.ResourceKind.HOST;
import static groove.grammar.model.ResourceKind.PROLOG;
import static groove.grammar.model.ResourceKind.RULE;
import static groove.grammar.model.ResourceKind.TYPE;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import groove.explore.ExploreType;
import groove.grammar.Grammar;
import groove.grammar.GrammarKey;
import groove.grammar.GrammarProperties;
import groove.grammar.QualName;
import groove.grammar.Recipe;
import groove.grammar.aspect.AspectGraph;
import groove.grammar.host.HostGraph;
import groove.grammar.type.TypeGraph;
import groove.graph.GraphInfo;
import groove.graph.GraphRole;
import groove.io.store.EditType;
import groove.io.store.SystemStore;
import groove.prolog.GrooveEnvironment;
import groove.util.ChangeCount;
import groove.util.ChangeCount.Tracker;
import groove.util.Groove;
import groove.util.Version;
import groove.util.parse.FormatError;
import groove.util.parse.FormatErrorSet;
import groove.util.parse.FormatException;

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
        this.changeCount = new ChangeCount();
        String grammarVersion = store.getProperties()
            .getGrammarVersion();
        boolean noActiveStartGraphs = store.getProperties()
            .getActiveNames(HOST)
            .isEmpty();
        if (Version.compareGrammarVersions(grammarVersion, Version.GRAMMAR_VERSION_3_2) < 0
            && noActiveStartGraphs) {
            setLocalActiveNames(HOST, QualName.name(Groove.DEFAULT_START_GRAPH_NAME));
        }
        for (ResourceKind resource : ResourceKind.all(false)) {
            syncResource(resource);
        }
    }

    /** Returns the name of the rule system. */
    public String getName() {
        return this.store.getName();
    }

    /**
     * Returns a string that can be used to identify the grammar model.
     * The ID is composed from grammar name and start graph name(s);
     */
    public String getId() {
        return Grammar.buildId(getName(),
            getStartGraphModel() == null ? null : getStartGraphModel().getQualName()
                .toString());
    }

    /** Returns the backing system store. */
    public SystemStore getStore() {
        return this.store;
    }

    /** Returns the system properties of this grammar model. */
    public GrammarProperties getProperties() {
        GrammarProperties result = this.localProperties;
        if (result == null) {
            result = this.store.getProperties();
        }
        return result;
    }

    /**
     * Sets a local properties object.
     * This circumvents the stored properties.
     * @param properties a local properties object; if {@code null}, the
     * properties are reset to the stored properties
     * @throws FormatException if the properties object is not {@code null}
     * and does not satisfy {@link GrammarProperties#check(GrammarModel)}
     */
    public void setProperties(GrammarProperties properties) throws FormatException {
        if (properties != null) {
            properties.check(this);
        }
        this.localProperties = properties;
        for (ResourceKind kind : ResourceKind.all(false)) {
            syncResource(kind);
        }
        invalidate();
    }

    /** Returns all names of grammar resources of a given kind. */
    public Set<QualName> getNames(ResourceKind kind) {
        if (kind == ResourceKind.PROPERTIES) {
            return null;
        } else if (kind.isTextBased()) {
            return getStore().getTexts(kind)
                .keySet();
        } else {
            return getStore().getGraphs(kind)
                .keySet();
        }
    }

    /** Returns the map from resource names to resource models of a given kind. */
    public Map<QualName,? extends NamedResourceModel<?>> getResourceMap(ResourceKind kind) {
        return this.resourceMap.get(kind);
    }

    /** Returns the collection of resource models of a given kind. */
    public Collection<NamedResourceModel<?>> getResourceSet(ResourceKind kind) {
        return this.resourceMap.get(kind)
            .values();
    }

    /** Returns a named graph-based resource model of a given kind. */
    public GraphBasedModel<?> getGraphResource(ResourceKind kind, QualName name) {
        assert kind.isGraphBased() : String.format("Resource kind %s is not graph-based", kind);
        return (GraphBasedModel<?>) getResourceMap(kind).get(name);
    }

    /** Returns a named text-based resource model of a given kind. */
    public TextBasedModel<?> getTextResource(ResourceKind kind, QualName name) {
        assert kind.isTextBased() : String.format("Resource kind %s is not text-based", kind);
        return (TextBasedModel<?>) getResourceMap(kind).get(name);
    }

    /** Indicates if this grammar model has a named resource model of a given kind. */
    public boolean hasResource(ResourceKind kind, QualName name) {
        return getResource(kind, name) != null;
    }

    /** Returns a named resource model of a given kind. */
    public NamedResourceModel<?> getResource(ResourceKind kind, QualName name) {
        assert name != null;
        return getResourceMap(kind).get(name);
    }

    /**
     * Returns the set of resource names of the active resources of a given kind.
     * These are the names stored as active, but can be overridden locally in
     * the grammar model.
     * @see #setLocalActiveNames(ResourceKind, Collection)
     */
    public Set<QualName> getActiveNames(ResourceKind kind) {
        // first check for locally stored names
        Set<QualName> result = this.localActiveNamesMap.get(kind);
        if (result == null) {
            // if there are none, check for active names in the store
            result = this.storedActiveNamesMap.get(kind);
        }
        result.retainAll(getNames(kind));
        return Collections.unmodifiableSet(result);
    }

    /**
     * Returns the set of resource names of the local active resources of a given kind.
     * @see #setLocalActiveNames(ResourceKind, Collection)
     */
    public Set<QualName> getLocalActiveNames(ResourceKind kind) {
        // first check for locally stored names
        Set<QualName> result = this.localActiveNamesMap.get(kind);
        if (result == null) {
            return null;
        }
        return Collections.unmodifiableSet(result);
    }

    /**
     * Convenience method for calling {@link #setLocalActiveNames(ResourceKind, Collection)}.
     */
    public void setLocalActiveNames(ResourceKind kind, QualName... names) {
        setLocalActiveNames(kind, Arrays.asList(names));
    }

    /**
     * Locally sets the active names of a given resource kind in the grammar model.
     * This overrides (but does not change) the stored names.
     * @param kind the kind for which to set the active names
     * @param names non-{@code null} set of active names
     * @see #getActiveNames(ResourceKind)
     */
    public void setLocalActiveNames(ResourceKind kind, Collection<QualName> names) {
        assert names != null;// && !names.isEmpty();
        this.localActiveNamesMap.put(kind, new TreeSet<>(names));
        this.resourceChangeCounts.get(kind)
            .increase();
        invalidate();
    }

    /** Removes the locally set active names of a given resource kind. */
    public void resetLocalActiveNames(ResourceKind kind) {
        this.localActiveNamesMap.remove(kind);
    }

    /**
     * Returns the graph model for a given graph name.
     * @return the graph model for graph <code>name</code>, or <code>null</code>
     *         if there is no such graph.
     */
    public HostModel getHostModel(QualName name) {
        return (HostModel) getResourceMap(HOST).get(name);
    }

    /**
     * Returns the control model associated with a given (named) control program.
     * @param name the name of the control program to return the model of;
     * @return the corresponding control program model, or <code>null</code> if
     *         no program by that name exists
     */
    public ControlModel getControlModel(QualName name) {
        return (ControlModel) getResource(CONTROL, name);
    }

    /**
     * Returns the prolog model associated with a given (named) prolog program.
     * @param name the name of the prolog program to return the model of;
     * @return the corresponding prolog model, or <code>null</code> if
     *         no program by that name exists
     */
    public PrologModel getPrologModel(QualName name) {
        return (PrologModel) getResourceMap(PROLOG).get(name);
    }

    /**
     * Returns the rule model for a given rule name.
     * @return the rule model for rule <code>name</code>, or <code>null</code> if
     *         there is no such rule.
     */
    public RuleModel getRuleModel(QualName name) {
        return (RuleModel) getResourceMap(RULE).get(name);
    }

    /**
     * Returns the type graph model for a given graph name.
     * @return the type graph model for type <code>name</code>, or
     *         <code>null</code> if there is no such graph.
     */
    public TypeModel getTypeModel(QualName name) {
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
     * Returns the start graph of this grammar model.
     * @return the start graph model, or <code>null</code> if no start graph is
     *         set.
     */

    public HostModel getStartGraphModel() {
        if (this.startGraphModel == null) {
            TreeMap<QualName,AspectGraph> graphMap = new TreeMap<>();
            for (QualName name : getActiveNames(HOST)) {
                graphMap.put(name, getStore().getGraphs(HOST)
                    .get(name));
            }
            AspectGraph startGraph = AspectGraph.mergeGraphs(graphMap.values());
            if (startGraph != null) {
                this.startGraphModel = new HostModel(this, startGraph);
            }
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
    public void setStartGraph(AspectGraph startGraph) {
        assert startGraph != null;
        if (startGraph.getRole() != GraphRole.HOST) {
            throw new IllegalArgumentException(
                String.format("Prospective start graph '%s' is not a graph", startGraph));
        }
        this.startGraphModel = new HostModel(this, startGraph);
        this.isExternalStartGraphModel = true;
        this.resourceChangeCounts.get(HOST)
            .increase();
        invalidate();
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
     * Returns a fresh change tracker for the overall grammar model.
     */
    public Tracker createChangeTracker() {
        return this.changeCount.createTracker();
    }

    /**
     * Returns a fresh change tracker for a given resource kind.
     */
    public Tracker createChangeTracker(ResourceKind kind) {
        return this.resourceChangeCounts.get(kind)
            .createTracker();
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
    public Grammar toGrammar() throws FormatException {
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
        for (NamedResourceModel<?> prologModel : getResourceSet(PROLOG)) {
            for (FormatError error : prologModel.getErrors()) {
                this.errors.add("Error in prolog program '%s': %s",
                    prologModel.getQualName(),
                    error,
                    prologModel);
            }
        }
        // check if all resource names are valid identifiers
        for (ResourceKind kind : ResourceKind.all(false)) {
            for (NamedResourceModel<?> model : getResourceSet(kind)) {
                this.errors.addAll(model.getQualName()
                    .getErrors());
            }
        }
    }

    /** Checks if this grammar has rules (maybe with errors). */
    public boolean hasRules() {
        return !getResourceSet(RULE).isEmpty();
    }

    /**
     * Computes a graph grammar from this model.
     * @throws FormatException if there are syntax errors in the model
     */
    private Grammar computeGrammar() throws FormatException {
        Grammar result = new Grammar();
        FormatErrorSet errors = new FormatErrorSet();
        // Construct the composite type graph
        result.setTypeGraph(getTypeGraph());
        errors.addAll(getTypeModel().getErrors());
        // set rules
        for (NamedResourceModel<?> ruleModel : getResourceSet(RULE)) {
            try {
                // only add the enabled rules
                if (ruleModel.isEnabled()) {
                    result.add(((RuleModel) ruleModel).toResource());
                }
            } catch (FormatException exc) {
                for (FormatError error : exc.getErrors()) {
                    errors.add("Error in rule '%s': %s",
                        ruleModel.getQualName(),
                        error,
                        ruleModel.getSource());
                }
            }
        }
        // set control
        try {
            result.setControl(getControlModel().toResource());
            for (Recipe recipe : getControlModel().getRecipes()) {
                result.add(recipe);
            }
        } catch (FormatException e) {
            errors.addAll(e.getErrors());
        }
        // set properties
        try {
            getProperties().check(this);
            result.setProperties(getProperties());
        } catch (FormatException e) {
            errors.addAll(e.getErrors());
        }
        // set start graph
        if (getStartGraphModel() == null) {
            Set<QualName> startGraphNames = getActiveNames(HOST);
            if (startGraphNames.isEmpty()) {
                errors.add("No start graph set");
            } else {
                errors.add("Start graphs '%s' cannot be loaded", startGraphNames);
            }
        } else {
            Collection<FormatError> startGraphErrors;
            try {
                HostGraph startGraph = getStartGraphModel().toResource();
                result.setStartGraph(startGraph);
                startGraphErrors = GraphInfo.getErrors(startGraph);
            } catch (FormatException exc) {
                startGraphErrors = exc.getErrors();
            }
            for (FormatError error : startGraphErrors) {
                errors.add(error, getStartGraphModel().getSource());
            }
        }
        // Set the Prolog environment.
        result.setPrologEnvironment(this.getPrologEnvironment());
        errors.throwException();
        assert result.getControl() != null : "Grammar must have control";
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
            for (NamedResourceModel<?> model : getResourceSet(PROLOG)) {
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
        this.changeCount.increase();
        this.grammar = null;
        this.errors = null;
        if (!this.isExternalStartGraphModel) {
            this.startGraphModel = null;
        }
    }

    @Override
    public void update(Observable source, Object obj) {
        SystemStore.Edit edit = (SystemStore.Edit) obj;
        if (edit.getType() != EditType.LAYOUT) {
            Set<ResourceKind> change = edit.getChange();
            for (ResourceKind resource : change) {
                syncResource(resource);
            }
            invalidate();
        }
    }

    /**
     * Synchronises the resources in the grammar model with the underlying store.
     * @param kind the kind of resources to be synchronised
     */
    private void syncResource(ResourceKind kind) {
        // register a change in this resource, regardless of what actually happens.
        // This might possibly be refined
        this.resourceChangeCounts.get(kind)
            .increase();
        switch (kind) {
        case PROLOG:
            this.prologEnvironment = null;
            break;
        case PROPERTIES:
            return;
        default:
            // proceed
        }
        // update the set of resource models
        Map<QualName,NamedResourceModel<?>> modelMap = this.resourceMap.get(kind);
        Map<QualName,? extends Object> sourceMap;
        if (kind.isGraphBased()) {
            sourceMap = getStore().getGraphs(kind);
        } else {
            sourceMap = getStore().getTexts(kind);
        }
        // restrict the resources to those whose names are in the store
        modelMap.keySet()
            .retainAll(sourceMap.keySet());
        // collect the new active names
        SortedSet<QualName> newActiveNames = new TreeSet<>();
        if (kind != RULE && kind != ResourceKind.CONFIG) {
            newActiveNames.addAll(getProperties().getActiveNames(kind));
        }
        // now synchronise the models with the sources in the store
        for (Map.Entry<QualName,? extends Object> sourceEntry : sourceMap.entrySet()) {
            QualName name = sourceEntry.getKey();
            Object source = sourceEntry.getValue();
            NamedResourceModel<?> model = modelMap.get(name);
            if (model == null || model.getSource() != source) {
                modelMap.put(name, model = createModel(kind, name));
                // collect the active rules
            }
            if (kind == GROOVY
                || kind == RULE && GraphInfo.isEnabled((AspectGraph) model.getSource())) {
                newActiveNames.add(name);
            }
        }
        // update the active names set
        Set<QualName> oldActiveNames = this.storedActiveNamesMap.get(kind);
        if (!oldActiveNames.equals(newActiveNames)) {
            oldActiveNames.clear();
            oldActiveNames.addAll(newActiveNames);
            resetLocalActiveNames(kind);
        }
    }

    /** Callback method to create a model for a named resource. */
    private NamedResourceModel<?> createModel(ResourceKind kind, QualName name) {
        NamedResourceModel<?> result = null;
        if (kind.isGraphBased()) {
            AspectGraph graph = getStore().getGraphs(kind)
                .get(name);
            if (graph != null) {
                result = createGraphModel(graph);
            }
        } else {
            assert kind.isTextBased();
            String text = getStore().getTexts(kind)
                .get(name);
            if (text != null) {
                switch (kind) {
                case CONTROL:
                    result = new ControlModel(this, name, text);
                    break;
                case PROLOG:
                    result = new PrologModel(this, name, text);
                    break;
                case GROOVY:
                    result = new GroovyModel(this, name, text);
                    break;
                case CONFIG:
                    result = new ConfigModel(this, name, text);
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
     * Returns the default exploration, based on the {@link GrammarKey#EXPLORATION}
     * value in the system properties.
     */
    public ExploreType getDefaultExploreType() {
        return getProperties().getExploreType();
    }

    /** Mapping from resource kinds and names to resource models. */
    private final Map<ResourceKind,SortedMap<QualName,NamedResourceModel<?>>> resourceMap =
        new EnumMap<>(ResourceKind.class);
    /**
     * Mapping from resource kinds to sets of names of active resources of that kind.
     * For {@link ResourceKind#RULE} this is determined by inspecting the active rules;
     * for all other resources, it is stored in the grammar properties.
     * @see #localActiveNamesMap
     */
    private final Map<ResourceKind,SortedSet<QualName>> storedActiveNamesMap =
        new EnumMap<>(ResourceKind.class);
    /**
     * Mapping from resource kinds to sets of names of active resources of that kind.
     * Where non-{@code null}, the values in this map override the {@link #storedActiveNamesMap}.
     */
    private final Map<ResourceKind,SortedSet<QualName>> localActiveNamesMap =
        new EnumMap<>(ResourceKind.class);
    /** The store backing this model. */
    private final SystemStore store;
    /** Counter of the number of invalidations of the grammar. */
    private final ChangeCount changeCount;
    private final Map<ResourceKind,ChangeCount> resourceChangeCounts =
        new EnumMap<>(ResourceKind.class);
    /** Local properties; if {@code null}, the stored properties are used. */
    private GrammarProperties localProperties;
    /** Flag to indicate if the start graph is external. */
    private boolean isExternalStartGraphModel = false;
    /** Possibly empty list of errors found in the conversion to a grammar. */
    private FormatErrorSet errors;
    /** The graph grammar derived from the rule models. */
    private Grammar grammar;
    /** The prolog environment derived from the system store. */
    private GrooveEnvironment prologEnvironment;
    /** The start graph of the grammar. */
    private HostModel startGraphModel;
    /** The type model composed from the individual elements. */
    private CompositeTypeModel typeModel;
    /** The control model composed from the individual control programs. */
    private CompositeControlModel controlModel;

    {
        for (ResourceKind kind : ResourceKind.values()) {
            this.resourceMap.put(kind, new TreeMap<>());
            this.storedActiveNamesMap.put(kind, new TreeSet<>());
            this.resourceChangeCounts.put(kind, new ChangeCount());
        }
    }

    /**
     * Creates an instance based on a store located at a given URL.
     * @param url the URL to load the grammar from
     * @throws IllegalArgumentException if no store can be created from the
     *         given URL
     * @throws IOException if a store can be created but not loaded
     */
    static public GrammarModel newInstance(URL url) throws IllegalArgumentException, IOException {
        SystemStore store = SystemStore.newStore(url);
        store.reload();
        GrammarModel result = store.toGrammarModel();
        return result;
    }

    /**
     * Creates an instance based on a given file.
     * @param file the file to load the grammar from
     * @throws IOException if the store exists  does not contain a grammar
     */
    static public GrammarModel newInstance(File file) throws IOException {
        return newInstance(file, false);
    }

    /**
     * Creates an instance based on a given file.
     * @param file the file to load the grammar from
     * @param create if <code>true</code> and <code>file</code> does not yet
     *        exist, attempt to create it.
     * @throws IOException if an error occurred while creating the store, or
     * if the store exists but does not contain a grammar
     */
    static public GrammarModel newInstance(File file, boolean create) throws IOException {
        SystemStore store = SystemStore.newStore(file, create);
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
        public static boolean apply(Set<String> set, Manipulation manipulation, String selected) {
            Set<String> temp = new HashSet<>();
            temp.add(selected);
            return apply(set, manipulation, temp);
        }
    }
}
