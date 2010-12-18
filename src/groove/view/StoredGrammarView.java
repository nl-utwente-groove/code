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

import groove.control.ControlView;
import groove.control.CtrlFactory;
import groove.graph.DefaultGraph;
import groove.graph.GraphInfo;
import groove.graph.LabelStore;
import groove.graph.TypeGraph;
import groove.io.SystemStore;
import groove.io.SystemStoreFactory;
import groove.trans.GraphGrammar;
import groove.trans.HostGraph;
import groove.trans.RuleName;
import groove.trans.SystemProperties;
import groove.util.Groove;
import groove.view.aspect.AspectGraph;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

/**
 * Grammar view based on a backing system store.
 */
public class StoredGrammarView implements GrammarView, Observer {
    /**
     * Constructs a grammar view from a rule system store. The start graph name
     * is the default one.
     * @see Groove#DEFAULT_START_GRAPH_NAME
     */
    public StoredGrammarView(SystemStore store) {
        this(store, null);
    }

    /**
     * Constructs a grammar view from a rule system store and a start graph
     * name.
     * @param startGraphName the name of the graph to be used as start state; if
     *        <code>null</code>, the default start graph name is used.
     */
    public StoredGrammarView(SystemStore store, String startGraphName) {
        this.store = store;
        loadControlMap();
        setStartGraph(startGraphName == null ? Groove.DEFAULT_START_GRAPH_NAME
                : startGraphName);
    }

    /** Returns the name of this grammar view. */
    public String getName() {
        return this.store.getName();
    }

    /** Returns the backing system store. */
    public SystemStore getStore() {
        return this.store;
    }

    /** Returns the system properties of this grammar view. */
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

    /** Returns a list of all available control program names. */
    public Set<String> getControlNames() {
        return Collections.unmodifiableSet(this.controlMap.keySet());
    }

    public Set<String> getGraphNames() {
        return Collections.unmodifiableSet(getStore().getGraphs().keySet());
    }

    public Set<RuleName> getRuleNames() {
        return Collections.unmodifiableSet(getStore().getRules().keySet());
    }

    /** Returns a list of all available type graph names. */
    public Set<String> getTypeNames() {
        return Collections.unmodifiableSet(getStore().getTypes().keySet());
    }

    public ControlView getControlView(String name) {
        return this.controlMap.get(name);
    }

    public GraphView getGraphView(String name) {
        GraphView result = null;
        AspectGraph stateGraph = getStore().getGraphs().get(name);
        if (stateGraph != null) {
            result = stateGraph.toGraphView(getProperties());
            TypeGraph type = null;
            try {
                type = getTypeViewList().toModel();
            } catch (FormatException e) {
                // don't set the type graph
            }
            result.setType(type);
        }
        return result;
    }

    public RuleView getRuleView(RuleName name) {
        RuleView result = null;
        AspectGraph ruleGraph = getStore().getRules().get(name);
        if (ruleGraph != null) {
            result = ruleGraph.toRuleView(getProperties());
            TypeGraph type = null;
            try {
                type = getTypeViewList().toModel();
            } catch (FormatException e) {
                // don't set the type graph
            }
            result.setType(type);
        }
        return result;
    }

    public TypeView getTypeView(String name) {
        AspectGraph typeGraph = getStore().getTypes().get(name);
        return typeGraph == null ? null : typeGraph.toTypeView(getProperties());
    }

    /**
     * Lazily creates a list of selected type views.
     * @return a list of type views that yield a composite type graph.
     */
    public TypeViewList getTypeViewList() {
        if (this.composedTypeView == null) {
            this.composedTypeView = new TypeViewList();
        }
        return this.composedTypeView;
    }

    /**
     * Returns a list of the active type graph names. This is taken from the
     * system properties. The empty list means that no type graph is set.
     * @see SystemProperties#getTypeNames()
     */
    public List<String> getActiveTypeNames() {
        return getProperties().getTypeNames();
    }

    /**
     * Returns the name of the control program to be used if control is 
     * enabled, or {@code null} otherwise. This is taken
     * from the system properties.
     */
    public String getControlName() {
        return getProperties().isUseControl()
                ? getProperties().getControlName() : null;
    }

    @Override
    public ControlView getControlView() {
        return isUseControl() ? getControlView(getControlName()) : null;
    }

    public String getStartGraphName() {
        return this.startGraphName;
    }

    public GraphView getStartGraphView() {
        if (this.startGraph == null && this.startGraphName != null) {
            this.startGraph = getGraphView(this.startGraphName);
        }
        return this.startGraph;
    }

    @Override
    public void setStartGraph(AspectGraph startGraph) {
        assert startGraph != null;
        if (!GraphInfo.hasGraphRole(startGraph)) {
            throw new IllegalArgumentException(String.format(
                "Prospective start graph '%s' is not a graph", startGraph));
        }
        this.startGraph = startGraph.toGraphView(getProperties());
        this.startGraphName = null;
        invalidate();
    }

    @Override
    public void setStartGraph(String name) {
        assert name != null;
        this.startGraphName = name;
        invalidate();
    }

    @Override
    public void removeStartGraph() {
        this.startGraph = null;
    }

    /** Collects and returns the permanent errors of the rule views. */
    public List<FormatError> getErrors() {
        if (this.errors == null) {
            initGrammar();
        }
        return this.errors;
    }

    /** Returns the labels occurring in this grammar view. */
    public final LabelStore getLabelStore() {
        if (this.labelStore == null) {
            initGrammar();
        }
        return this.labelStore;
    }

    /** Delegates to {@link #toGrammar()}. */
    public GraphGrammar toModel() throws FormatException {
        return toGrammar();
    }

    /**
     * Converts the grammar view to a real grammar. With respect to control, we
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
        return getControlName() != null;
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
        if (this.controlMap.containsKey(Groove.DEFAULT_CONTROL_NAME)) {
            result = Groove.DEFAULT_CONTROL_NAME;
        } else if (this.controlMap.containsKey(getName())) {
            result = getName();
        }
        return result;
    }

    /** Initialises the {@link #grammar} and {@link #errors} fields. */
    private void initGrammar() {
        try {
            this.grammar = computeGrammar();
            this.errors = Collections.emptyList();
        } catch (FormatException exc) {
            this.errors = new ArrayList<FormatError>(exc.getErrors());
            Collections.sort(this.errors);
        }
    }

    /**
     * Computes a graph grammar from this view.
     * @throws FormatException if there are syntax errors in the view
     */
    private GraphGrammar computeGrammar() throws FormatException {
        GraphGrammar result = new GraphGrammar(getName());
        List<FormatError> errors = new ArrayList<FormatError>();
        // check type correctness
        for (String typeName : getActiveTypeNames()) {
            TypeView typeView = getTypeView(typeName);
            if (typeView == null) {
                errors.add(new FormatError("Type graph '%s' cannot be found",
                    typeName));
            } else {
                try {
                    typeView.toModel();
                } catch (FormatException exc) {
                    for (FormatError error : exc.getErrors()) {
                        errors.add(new FormatError(
                            "Error in type graph '%s': %s", typeView.getName(),
                            error, typeView.getView()));
                    }
                }
            }
        }
        // We have constructed all views of the type graphs.
        // Make the composition now.
        try {
            result.setType(this.getTypeViewList().toModel());
        } catch (FormatException exc) {
            errors.addAll(exc.getErrors());
        }
        // set a label store (get it from the type graph if that exists)
        if (result.getType() == null) {
            this.labelStore = new LabelStore();
            for (RuleName ruleName : getRuleNames()) {
                RuleView ruleView = getRuleView(ruleName);
                this.labelStore.addLabels(ruleView.getLabels());
            }
            // add types from all known graphs to label store
            for (String graphName : getGraphNames()) {
                GraphView graphView = getGraphView(graphName);
                this.labelStore.addLabels(graphView.getLabels());
            }
            if (getStartGraphView() != null) {
                GraphView graphView = getStartGraphView();
                this.labelStore.addLabels(graphView.getLabels());
            }
            this.labelStore.addDirectSubtypes(getProperties().getSubtypes());
            this.labelStore.setFixed();
        } else {
            this.labelStore = result.getType().getLabelStore();
        }
        result.setLabelStore(this.labelStore);
        // set rules
        for (RuleName ruleName : getRuleNames()) {
            RuleView ruleView = getRuleView(ruleName);
            try {
                // only add the enabled rules
                if (ruleView.isEnabled()) {
                    result.add(ruleView.toRule());
                }
            } catch (FormatException exc) {
                for (FormatError error : exc.getErrors()) {
                    errors.add(new FormatError("Error in rule '%s': %s",
                        ruleView.getName(), error, ruleView.getView()));
                }
            }
        }
        // set control
        if (isUseControl()) {
            ControlView controlView = getControlView(getControlName());
            if (controlView == null) {
                errors.add(new FormatError(
                    "Control program '%s' cannot be found", getControlName()));
            } else if (result.hasMultiplePriorities()) {
                errors.add(new FormatError(
                    "Rule priorities and control programs are incompatible, please disable either."));
            } else {
                try {
                    result.setCtrlAut(controlView.toCtrlAut(result));
                } catch (FormatException exc) {
                    for (FormatError error : exc.getErrors()) {
                        errors.add(new FormatError(
                            "Error in control program '%s': %s",
                            getControlName(), error, controlView));
                    }
                }
            }
        } else if (ALWAYS_CONTROL) {
            result.setCtrlAut(CtrlFactory.getInstance().buildDefault(result));
        }
        // set properties
        result.setProperties(getProperties());
        // set start graph
        if (getStartGraphView() == null) {
            if (getStartGraphName() == null) {
                errors.add(new FormatError("No start graph set"));
            } else {
                errors.add(new FormatError("Start graph '%s' cannot be loaded",
                    getStartGraphName()));
            }
        } else {
            List<FormatError> startGraphErrors;
            try {
                HostGraph startGraph = getStartGraphView().toModel();
                result.setStartGraph(startGraph);
                startGraphErrors = GraphInfo.getErrors(startGraph);
            } catch (FormatException exc) {
                startGraphErrors = exc.getErrors();
            }
            for (FormatError error : startGraphErrors) {
                errors.add(new FormatError("Error in start graph: %s", error,
                    getStartGraphView().getView()));
            }
        }
        try {
            result.setFixed();
        } catch (FormatException exc) {
            errors.addAll(exc.getErrors());
        }
        if (errors.isEmpty()) {
            return result;
        } else {
            throw new FormatException(errors);
        }
    }

    /**
     * Resets the {@link #grammar} and {@link #errors} objects, making sure that
     * they are regenerated at a next call of {@link #toModel()}.
     */
    void invalidate() {
        this.grammar = null;
        this.errors = null;
        this.labelStore = null;
        this.controlPropertyAdjusted = false;
        this.composedTypeView = null;
        if (this.startGraphName != null) {
            this.startGraph = null;
        }
    }

    /**
     * Reloads the control map from the backing {@link SystemStore}.
     */
    private void loadControlMap() {
        this.controlMap.clear();
        for (Map.Entry<String,String> storedRuleEntry : this.store.getControls().entrySet()) {
            this.controlMap.put(storedRuleEntry.getKey(), new ControlView(
                storedRuleEntry.getValue(), storedRuleEntry.getKey()));
        }
    }

    @Override
    public void update(Observable source, Object edit) {
        int change = ((SystemStore.Edit) edit).getChange();
        if ((change & SystemStore.CONTROL_CHANGE) > 0) {
            loadControlMap();
        }
        invalidate();
    }

    /** Mapping from control names to views on the corresponding automata. */
    final Map<String,ControlView> controlMap =
        new HashMap<String,ControlView>();

    /** The store backing this view. */
    private final SystemStore store;
    /** 
     * Flag indicating that the system properties
     * have been adjusted to reflect the presence of a control program
     * with a default name.
     * @see #getProperties()
     */
    private boolean controlPropertyAdjusted;
    /** The start graph of the grammar. */
    private GraphView startGraph;
    /**
     * Name of the current start graph, if it is one of the graphs in this rule
     * system; <code>null</code> otherwise.
     */
    private String startGraphName;
    /** Possibly empty list of errors found in the conversion to a grammar. */
    private List<FormatError> errors;
    /** The graph grammar derived from the rule views. */
    private GraphGrammar grammar;
    /** The labels occurring in this view. */
    private LabelStore labelStore;
    /** The type view composed from the individual elements. */
    private TypeViewList composedTypeView;

    static private boolean ALWAYS_CONTROL = true;

    /**
     * Creates an instance based on a store located at a given URL.
     * @param url the URL to load the grammar from
     * @throws IllegalArgumentException if no store can be created from the
     *         given URL
     * @throws IOException if a store can be created but not loaded
     */
    static public StoredGrammarView newInstance(URL url)
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
    static public StoredGrammarView newInstance(URL url, String startGraphName)
        throws IllegalArgumentException, IOException {
        SystemStore store = SystemStoreFactory.newStore(url);
        store.reload();
        StoredGrammarView result = store.toGrammarView();
        if (startGraphName != null) {
            if (result.getGraphNames().contains(startGraphName)) {
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
     * @throws IllegalArgumentException if no store can be created from the
     *         given file
     * @throws IOException if a store can be created but not loaded
     */
    static public StoredGrammarView newInstance(File file, boolean create)
        throws IllegalArgumentException, IOException {
        return newInstance(file, null, create);
    }

    /**
     * Creates an instance based on a given file and start graph name.
     * @param file the file to load the grammar from
     * @param startGraphName the start graph name; if <code>null</code>, the
     *        default start graph name is used
     * @param create if <code>true</code> and <code>file</code> does not yet
     *        exist, attempt to create it.
     * @throws IllegalArgumentException if no store can be created from the
     *         given file
     * @throws IOException if a store can be created but not loaded
     */
    static public StoredGrammarView newInstance(File file,
            String startGraphName, boolean create) throws IOException {
        SystemStore store = SystemStoreFactory.newStore(file, create);
        store.reload();
        StoredGrammarView result = store.toGrammarView();
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
    static public StoredGrammarView newInstance(String location)
        throws IllegalArgumentException, IOException {
        try {
            return newInstance(new URL(location));
        } catch (IllegalArgumentException exc) {
            return newInstance(new File(location), false);
        } catch (IOException exc) {
            return newInstance(new File(location), false);
        }
    }

    /** Class to store the views that are used to compose the type graph. */
    public class TypeViewList {

        private Map<String,TypeView> typeViewMap;
        private TypeGraph model;
        private List<FormatError> errors;

        private TypeViewList() {
            this.typeViewMap = new HashMap<String,TypeView>();
            this.model = null;
            this.errors = new ArrayList<FormatError>();
            for (String typeName : getActiveTypeNames()) {
                TypeView typeView = getTypeView(typeName);
                if (typeView != null) {
                    this.typeViewMap.put(typeName, typeView);
                    this.errors.addAll(typeView.getErrors());
                }
            }
        }

        /**
         * @return the composite type graph from the view list.
         * @throws FormatException if any of the views has errors.
         * @throws IllegalArgumentException if the composition of types gives
         *         rise to typing cycles.
         */
        public TypeGraph toModel() throws FormatException,
            IllegalArgumentException {
            this.initialise();
            if (this.model == null) {
                throw new FormatException(this.getErrors());
            } else {
                if (this.typeViewMap.isEmpty()) {
                    return null;
                } else {
                    return this.model;
                }
            }
        }

        /**
         * @return the errors in the underlying type views.
         */
        public List<FormatError> getErrors() {
            this.initialise();
            return this.errors;
        }

        /**
         * @return an unmodifiable map from type names to views.
         */
        public Map<String,TypeView> getTypeViewMap() {
            return Collections.unmodifiableMap(this.typeViewMap);
        }

        /** Constructs the model and associated data structures from the view. */
        private void initialise() {
            // first test if there is something to be done
            if (this.errors.isEmpty() && this.model == null) {
                // There are no errors in each of the views, try to compose the
                // type graph.
                this.model = new TypeGraph();
                for (TypeView view : this.typeViewMap.values()) {
                    try {
                        this.model.add(view.toModel());
                    } catch (FormatException e) {
                        this.errors.addAll(e.getErrors());
                    } catch (IllegalArgumentException e) {
                        this.errors.add(new FormatError(e.getMessage()));
                    }
                }
                if (this.errors.isEmpty()) {
                    this.model.setFixed();
                } else {
                    this.model = null;
                }
            }
        }
    }
}
