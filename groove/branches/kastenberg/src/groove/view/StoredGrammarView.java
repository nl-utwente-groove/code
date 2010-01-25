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

import groove.control.ControlAutomaton;
import groove.control.ControlView;
import groove.graph.Graph;
import groove.graph.GraphInfo;
import groove.graph.LabelStore;
import groove.graph.TypeGraph;
import groove.io.SystemStore;
import groove.io.SystemStoreFactory;
import groove.trans.GraphGrammar;
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
        return this.store.getProperties();
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
            if (getTypeView() != null) {
                try {
                    type = getTypeView().toModel();
                } catch (FormatException e) {
                    // don't set the type graph
                }
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
            if (getTypeView() != null) {
                try {
                    type = getTypeView().toModel();
                } catch (FormatException e) {
                    // don't set the type graph
                }
            }
            result.setType(type);
        }
        return result;
    }

    public TypeView getTypeView(String name) {
        AspectGraph typeGraph = getStore().getTypes().get(name);
        return typeGraph == null ? null : typeGraph.toTypeView(getProperties());
    }

    public TypeView getTypeView() {
        return getTypeName() == null ? null : getTypeView(getTypeName());
    }

    /**
     * Returns the name of the type graph to be used. This is taken from the
     * system properties. The empty string means that no type graph is set.
     * @see SystemProperties#getTypeName()
     */
    public String getTypeName() {
        return getProperties().getTypeName();
    }

    /**
     * Returns the name of the control program to be used. This is taken from
     * the system properties; if no explicit control name is given, it is set to
     * {@link Groove#DEFAULT_CONTROL_NAME}.
     * @see SystemProperties#getControlName()
     */
    public String getControlName() {
        return getControlName(true);
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
            throw new IllegalArgumentException(
                String.format("Prospective start graph '%s' is not a graph"));
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
     * Indicates if control is explicitly enabled. This means not only the
     * {@link SystemProperties#isUseControl()} yields <code>true</code>, but
     * also {@link #getControlName()} yields a non-<code>null</code> value.
     * @return <code>true</code> if control is explicitly enabled, in the above
     *         sense.
     */
    public boolean isUseControl() {
        return getProperties().isUseControl() && getControlName() != null;
    }

    /**
     * Returns the name of the control program to be used. This is taken from
     * the system properties. If no explicit control name is given,
     * {@link Groove#DEFAULT_CONTROL_NAME} or {@link #getName()} is taken as
     * default name, if any control program by that name exists; otherwise
     * <code>null</code> is returned.
     * @param useDefault flag to determine if either of the defaults (
     *        {@link Groove#DEFAULT_CONTROL_NAME} or {@link #getName()}) should
     *        be returned in case no explicit control name has been set in the
     *        system properties.
     * @see SystemProperties#getControlName()
     */
    private String getControlName(boolean useDefault) {
        String result = this.controlName;
        if (result == null || result.length() == 0) {
            result = getProperties().getControlName();
        }
        if ((result == null || result.length() == 0) && useDefault) {
            // first default name: Groove.DEFAULT_CONTROL_NAME
            // second default name: grammar name
            if (this.controlMap.containsKey(Groove.DEFAULT_CONTROL_NAME)) {
                result = Groove.DEFAULT_CONTROL_NAME;
            } else if (this.controlMap.containsKey(getName())) {
                result = getName();
            }
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
        if (!getTypeName().isEmpty()) {
            TypeView typeView = getTypeView();
            if (typeView == null) {
                errors.add(new FormatError("Type graph '%s' cannot be found",
                    getTypeName()));
            } else {
                try {
                    typeView.toModel();
                } catch (FormatException exc) {
                    errors.addAll(exc.getErrors());
                }
            }
        }
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
                    ControlAutomaton ca = controlView.toAutomaton(result);
                    result.setControl(ca);
                } catch (FormatException exc) {
                    for (FormatError error : exc.getErrors()) {
                        errors.add(new FormatError(
                            "Error in control program '%s': %s",
                            getControlName(), error, controlView));
                    }
                }
            }
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
                Graph startGraph = getStartGraphView().toModel();
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
        this.labelStore = new LabelStore();
        for (RuleName ruleName : getRuleNames()) {
            this.labelStore.addLabels(getRuleView(ruleName).getLabels());
        }
        // add types from all known graphs to label store
        for (String graphName : getGraphNames()) {
            this.labelStore.addLabels(getGraphView(graphName).getLabels());
        }
        if (getStartGraphView() != null) {
            this.labelStore.addLabels(getStartGraphView().getLabels());
        }
        // add subtyping relation from properties to label store
        if (getTypeView() == null) {
            this.labelStore.addDirectSubtypes(getProperties().getSubtypes());
        } else {
            this.labelStore.add(getTypeView().toModel().getLabelStore());
            this.labelStore.setFixed();
        }
        result.setLabelStore(this.labelStore);
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
    /** The start graph of the grammar. */
    private GraphView startGraph;
    /**
     * Name of the current control program, if it is explicitly set;
     * <code>null</code> otherwise.
     */
    private String controlName;
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
            result.setStartGraph(startGraphName);
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
}
