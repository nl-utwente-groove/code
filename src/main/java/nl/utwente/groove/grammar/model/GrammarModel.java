/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
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
package nl.utwente.groove.grammar.model;

import static nl.utwente.groove.grammar.model.ResourceKind.CONTROL;
import static nl.utwente.groove.grammar.model.ResourceKind.GROOVY;
import static nl.utwente.groove.grammar.model.ResourceKind.HOST;
import static nl.utwente.groove.grammar.model.ResourceKind.PROLOG;
import static nl.utwente.groove.grammar.model.ResourceKind.RULE;
import static nl.utwente.groove.grammar.model.ResourceKind.TYPE;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import nl.utwente.groove.explore.ExploreType;
import nl.utwente.groove.grammar.Grammar;
import nl.utwente.groove.grammar.GrammarKey;
import nl.utwente.groove.grammar.GrammarProperties;
import nl.utwente.groove.grammar.GrammarSource;
import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.grammar.Recipe;
import nl.utwente.groove.grammar.Rule;
import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.host.HostGraph;
import nl.utwente.groove.grammar.type.TypeGraph;
import nl.utwente.groove.graph.GraphInfo;
import nl.utwente.groove.graph.GraphRole;
import nl.utwente.groove.io.store.EditType;
import nl.utwente.groove.io.store.SystemStore;
import nl.utwente.groove.prolog.GrooveEnvironment;
import nl.utwente.groove.util.ChangeCount;
import nl.utwente.groove.util.ChangeCount.Tracker;
import nl.utwente.groove.util.Exceptions;
import nl.utwente.groove.util.Factory;
import nl.utwente.groove.util.Groove;
import nl.utwente.groove.util.Version;
import nl.utwente.groove.util.parse.FormatError;
import nl.utwente.groove.util.parse.FormatErrorSet;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Grammar model based on a backing system store.
 */
public class GrammarModel implements PropertyChangeListener {
    /**
     * Constructs a grammar model from a rule system store, using the start
     * graph(s) that are stored in the grammar properties.
     */
    public GrammarModel(SystemStore store) {
        this.source = store;
        this.changeCount = new ChangeCount();
        String grammarVersion = store.getProperties().getGrammarVersion();
        boolean noActiveStartGraphs = store.getProperties().getActiveNames(HOST).isEmpty();
        if (Version.compareGrammarVersions(grammarVersion, Version.GRAMMAR_VERSION_3_2) < 0
            && noActiveStartGraphs) {
            setLocalActiveNames(HOST, QualName.name(Groove.DEFAULT_START_GRAPH_NAME));
        }
        syncResources(ResourceKind.all(true));
    }

    /** Returns the name of the rule system. */
    public String getName() {
        return this.source.getName();
    }

    /**
     * Returns a string that can be used to identify the grammar model.
     * The ID is composed from grammar name and start graph name(s);
     */
    public String getId() {
        return Grammar
            .buildId(getName(), getStartGraphModel() == null
                ? null
                : getStartGraphModel().getQualName().toString());
    }

    /** Returns the backing system store. */
    public GrammarSource getStore() {
        return this.source;
    }

    /** The source backing this model. */
    private final GrammarSource source;

    /** Returns the system properties of this grammar model. */
    public GrammarProperties getProperties() {
        GrammarProperties result = this.localProperties;
        if (result == null) {
            result = this.source.getProperties();
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
        syncResources(ResourceKind.all(false));
        invalidate();
    }

    /** Returns all names of grammar resources of a given kind. */
    public Set<QualName> getNames(ResourceKind kind) {
        if (kind == ResourceKind.PROPERTIES) {
            return null;
        } else if (kind.isTextBased()) {
            return getStore().getTexts(kind).keySet();
        } else {
            return getStore().getGraphs(kind).keySet();
        }
    }

    /** Returns the map from resource names to resource models of a given kind. */
    public Map<QualName,? extends NamedResourceModel<?>> getResourceMap(ResourceKind kind) {
        return this.resourceMap.get(kind);
    }

    /** Returns the collection of resource models of a given kind. */
    public Collection<NamedResourceModel<?>> getResourceSet(ResourceKind kind) {
        return this.resourceMap.get(kind).values();
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
     * Returns a version of the stored graph of a given type and name
     * as used in the appropriate model.
     */
    public AspectGraph getGraph(ResourceKind kind, QualName name) {
        assert kind.isGraphBased();
        var resource = getGraphResource(kind, name);
        return resource == null
            ? null
            : resource.getSource();
        //        var map = this.typedGraphs.get(kind);
        //        // always test typeTracker for staleness, otherwise we'll do this twice
        //        if (map == null | this.typeTracker.isStale()) {
        //            this.typedGraphs.put(kind, map = new HashMap<>());
        //        }
        //        assert map != null;
        //        AspectGraph result = map.get(name);
        //        if (result == null) {
        //            result = getStore().getGraphs(kind).get(name);
        //            if (result != null) {
        //                result = toTypedGraph(result);
        //                map.put(name, result);
        //            }
        //        }
        //        return result;
    }

    /**
     * Returns a version of the stored text of a given type and name
     * ready for use in the grammar.
     */
    public String getText(ResourceKind kind, QualName name) {
        assert kind.isTextBased();
        var resource = getTextResource(kind, name);
        return resource == null
            ? null
            : resource.getSource();
    }

    /** Returns the list of active graphs of a given resource kind, alphabetically ordered by (qualified) name. */
    public List<AspectGraph> getActiveGraphs(ResourceKind kind) {
        List<AspectGraph> result = new ArrayList<>();
        getActiveNames(kind).stream().map(n -> getGraph(kind, n)).forEach(result::add);
        return result;
    }

    /** Returns the list of active texts of a given resource kind, alphabetically ordered by (qualified) name. */
    public List<String> getActiveTexts(ResourceKind kind) {
        List<String> result = new ArrayList<>();
        getActiveNames(kind).stream().map(n -> getText(kind, n)).forEach(result::add);
        return result;
    }

    /**
     * Returns the set of resource names of the active resources of a given kind.
     * These are the names stored as active, but can be overridden locally in
     * the grammar model.
     * @see #setLocalActiveNames(ResourceKind, Collection)
     */
    public SortedSet<QualName> getActiveNames(ResourceKind kind) {
        // first check for locally stored names
        SortedSet<QualName> result = this.localActiveNamesMap.get(kind);
        if (result == null) {
            // if there are none, check for active names in the store
            result = this.storedActiveNamesMap.get(kind);
        }
        result.retainAll(getNames(kind));
        return Collections.unmodifiableSortedSet(result);
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
        this.resourceChangeCounts.get(kind).increase();
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

    /** Helper method to retrieve the set of error-free, enabled rules. */
    Collection<Rule> getRules() {
        var ruleModels = getResourceSet(RULE);
        var result = new ArrayList<Rule>(ruleModels.size());
        // set rules
        for (ResourceModel<?> model : ruleModels) {
            RuleModel ruleModel = (RuleModel) model;
            try {
                if (GraphInfo.isEnabled(ruleModel.getSource())) {
                    result.add(ruleModel.toResource());
                }
            } catch (FormatException exc) {
                // do not add this rule
            }
        }
        return result;
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
            AspectGraph startGraph = AspectGraph.mergeGraphs(getActiveGraphs(HOST));
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
            throw Exceptions.illegalArg("Prospective start graph '%s' is not a graph", startGraph);
        }
        this.startGraphModel = new HostModel(this, startGraph);
        this.isExternalStartGraphModel = true;
        this.resourceChangeCounts.get(HOST).increase();
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

    /** Possibly empty list of errors found in the conversion to a grammar. */
    private FormatErrorSet errors;

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
        return this.resourceChangeCounts.get(kind).createTracker();
    }

    private final Map<ResourceKind,ChangeCount> resourceChangeCounts
        = new EnumMap<>(ResourceKind.class);

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
        if (DEBUG) {
            System.out.println("Building grammar");
        }
        this.errors = new FormatErrorSet();
        try {
            this.grammar = computeGrammar();
        } catch (FormatException exc) {
            this.errors.addAll(exc.getErrors());
        }
        getPrologEnvironment();
        for (NamedResourceModel<?> prologModel : getResourceSet(PROLOG)) {
            for (FormatError error : prologModel.getErrors()) {
                this.errors
                    .add("Error in prolog program '%s': %s", prologModel.getQualName(), error,
                         prologModel);
            }
        }
        // check if all resource names are valid identifiers
        for (ResourceKind kind : ResourceKind.all(false)) {
            for (NamedResourceModel<?> model : getResourceSet(kind)) {
                this.errors.addAll(model.getQualName().getErrors());
            }
        }
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
                // only add the active rules
                if (ruleModel.isActive()) {
                    result.add(((RuleModel) ruleModel).toResource());
                }
            } catch (FormatException exc) {
                for (FormatError error : exc.getErrors()) {
                    errors
                        .add("Error in rule '%s': %s", ruleModel.getQualName(), error,
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
        var startGraphNames = getActiveNames(HOST);
        var startGraphModel = getStartGraphModel();
        if (startGraphModel == null) {
            if (startGraphNames.isEmpty()) {
                errors.add("No start graph set");
            } else {
                errors.add("Start graphs '%s' cannot be loaded", startGraphNames);
            }
        } else {
            FormatErrorSet startGraphErrors;
            try {
                HostGraph startGraph = startGraphModel.toResource();
                result.setStartGraph(startGraph);
                startGraphErrors = startGraph.getErrors();
            } catch (FormatException exc) {
                startGraphErrors = exc.getErrors();
            }
            String prefix = startGraphNames.size() > 1
                ? "combined start graph"
                : "start graph '" + startGraphModel.getName() + "'";
            var activeHostGraphs = getActiveGraphs(HOST);
            for (FormatError error : startGraphErrors) {
                errors.add("Error in %s: %s", prefix, error, activeHostGraphs);
            }
        }
        // Set the Prolog environment.
        result.setPrologEnvironment(this.getPrologEnvironment());
        errors.throwException();
        assert result.getControl() != null : "Grammar must have control";
        result.setFixed();
        return result;
    }

    /** The graph grammar derived from the rule models. */
    private Grammar grammar;

    /** Checks if this grammar has rules (maybe with errors). */
    public boolean hasRules() {
        return !getResourceSet(RULE).isEmpty();
    }

    /**
     * Creates a Prolog environment that produces its standard output
     * on a the default {@link GrooveEnvironment} output stream.
     */
    public GrooveEnvironment getPrologEnvironment() {
        return this.prologEnvironment.get();
    }

    /**
     *
     */
    private GrooveEnvironment computePrologEnvironment() {
        var result = new GrooveEnvironment(null, null);
        for (NamedResourceModel<?> model : getResourceSet(PROLOG)) {
            PrologModel prologModel = (PrologModel) model;
            if (model.isActive()) {
                try {
                    result.loadProgram(prologModel.getProgram());
                    prologModel.clearErrors();
                } catch (FormatException e) {
                    prologModel.setErrors(e.getErrors());
                }
            }
        }
        return result;
    }

    /** The prolog environment derived from the system store. */
    private final Factory<GrooveEnvironment> prologEnvironment
        = Factory.lazy(this::computePrologEnvironment);

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
    public void propertyChange(PropertyChangeEvent evt) {
        SystemStore.Edit edit = (SystemStore.Edit) evt.getNewValue();
        if (edit.getType() != EditType.LAYOUT) {
            syncResources(edit.getChange());
        }
    }

    /** Synchronises all resources, in order of dependency, and invalidates the grammar model. */
    private void syncResources(Set<ResourceKind> kinds) {
        new SyncSet(kinds).forEach(this::syncResource);
        invalidate();
    }

    /**
     * Synchronises the resources in the grammar model with the underlying store.
     * @param kind the kind of resources to be synchronised
     */
    private void syncResource(ResourceKind kind) {
        // register a change in this resource, regardless of what actually happens.
        // This might possibly be refined
        this.resourceChangeCounts.get(kind).increase();
        switch (kind) {
        case PROLOG:
            this.prologEnvironment.reset();
            break;
        case PROPERTIES:
            return;
        default:
            // proceed
        }
        // update the set of resource models
        Map<QualName,NamedResourceModel<?>> modelMap = this.resourceMap.get(kind);
        Set<QualName> names = getNames(kind);
        // restrict the resources to those whose names are in the store
        modelMap.keySet().retainAll(names);
        // collect the new active names
        SortedSet<QualName> newActiveNames = new TreeSet<>();
        if (kind != RULE && kind != ResourceKind.CONFIG) {
            newActiveNames.addAll(getProperties().getActiveNames(kind));
        }
        // now synchronise the models with the sources in the store
        for (var name : names) {
            var model = createModel(kind, name);
            modelMap.put(name, model);
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
     * Creates a graph-based resource model based on a given graph.
     * The graph may have to be transformed in order to form the source of the model.
     */
    public GraphBasedModel<?> createGraphModel(AspectGraph graph) {
        GraphBasedModel<?> result = null;
        switch (graph.getRole()) {
        case HOST:
            result = new HostModel(this, graph);
            break;
        case RULE:
            var currentTypeSortMap = getTypeModel().getTypeSortMap();
            if (!Objects.equals(currentTypeSortMap, graph.getTypeSortMap())) {
                graph = graph.clone();
                graph.setTypeSortMap(currentTypeSortMap);
                graph.setFixed();
            }
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
    private final Map<ResourceKind,SortedMap<QualName,NamedResourceModel<?>>> resourceMap
        = new EnumMap<>(ResourceKind.class);
    /**
     * Mapping from resource kinds to sets of names of active resources of that kind.
     * For {@link ResourceKind#RULE} this is determined by inspecting the active rules;
     * for all other resources, it is stored in the grammar properties.
     * @see #localActiveNamesMap
     */
    private final Map<ResourceKind,SortedSet<QualName>> storedActiveNamesMap
        = new EnumMap<>(ResourceKind.class);
    /**
     * Mapping from resource kinds to sets of names of active resources of that kind.
     * Where non-{@code null}, the values in this map override the {@link #storedActiveNamesMap}.
     */
    private final Map<ResourceKind,SortedSet<QualName>> localActiveNamesMap
        = new EnumMap<>(ResourceKind.class);
    /** Counter of the number of invalidations of the grammar. */
    private final ChangeCount changeCount;
    /** Local properties; if {@code null}, the stored properties are used. */
    private GrammarProperties localProperties;
    /** Flag to indicate if the start graph is external. */
    private boolean isExternalStartGraphModel = false;
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
        //        this.typeTracker = createChangeTracker(TYPE);
    }

    static private final boolean DEBUG = false;

    // ========================================================================
    // ENUM: MANIPULATION
    // ========================================================================

    /** Set of resource kinds to be synchronised.
     * The ordering and content of the set ensures that all dependencies are fulfilled.
     * @author Arend Rensink
     * @version $Revision$
     */
    static private class SyncSet extends TreeSet<ResourceKind> {
        /** Constructs a new synchronisation set with given content. */
        public SyncSet(Set<ResourceKind> set) {
            super(new MyComparator());
            addAll(set);
        }

        @Override
        public boolean add(ResourceKind e) {
            var result = super.add(e);
            if (result) {
                // also add all resource kinds that depend on this one
                addAll(backwardMap.get(e));
            }
            return result;
        }

        @Override
        public boolean addAll(Collection<? extends ResourceKind> c) {
            var result = super.addAll(c);
            if (result) {
                // also add all resource kinds that depend on the ones in c
                c.forEach(k -> addAll(backwardMap.get(k)));
            }
            return result;
        }

        /** Adds a dependency and constructs the transitive closure. */
        static private void addDependency(ResourceKind source, ResourceKind target) {
            var sourcePreds = backwardMap.get(source);
            if (source == target || sourcePreds.contains(target)) {
                throw Exceptions.illegalArg("Cyclic dependency between %s and %s", source, target);
            }
            var sourceSuccs = forwardMap.get(source);
            var targetPreds = backwardMap.get(target);
            var targetSuccs = forwardMap.get(target);
            sourceSuccs.add(target);
            sourceSuccs.addAll(targetSuccs);
            for (var pred : sourcePreds) {
                var predSuccs = forwardMap.get(pred);
                predSuccs.add(target);
                predSuccs.addAll(targetSuccs);
            }
            targetPreds.add(source);
            targetPreds.addAll(sourcePreds);
            for (var succ : targetSuccs) {
                var succPreds = backwardMap.get(succ);
                succPreds.add(source);
                succPreds.addAll(sourcePreds);
            }
        }

        /** Mapping from resource kinds to those kinds they depend on. */
        static private final Map<ResourceKind,Set<ResourceKind>> forwardMap
            = new EnumMap<>(ResourceKind.class);

        /** Mapping from resource kinds to those kinds that depend on them. */
        static private final Map<ResourceKind,Set<ResourceKind>> backwardMap
            = new EnumMap<>(ResourceKind.class);

        static {
            for (var kind : ResourceKind.values()) {
                forwardMap.put(kind, EnumSet.noneOf(ResourceKind.class));
                backwardMap.put(kind, EnumSet.noneOf(ResourceKind.class));
            }
            addDependency(RULE, TYPE);
        }

        static private class MyComparator implements Comparator<ResourceKind> {
            @Override
            public int compare(ResourceKind o1, ResourceKind o2) {
                // TODO Auto-generated method stub
                if (o1 == o2) {
                    return 0;
                }
                if (backwardMap.get(o1).contains(o2)) {
                    return -1;
                }
                if (forwardMap.get(o1).contains(o2)) {
                    return 1;
                }
                return o1.ordinal() - o2.ordinal();
            }
        }
    }

    /**
     * A {@link Manipulation} distinguishes between different kinds of set
     * update operations that can be applied to a set of selected resources.
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
