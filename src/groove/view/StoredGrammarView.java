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
import groove.io.DefaultArchiveSystemStore;
import groove.io.DefaultFileSystemStore;
import groove.io.SystemStore;
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

/**
 * Grammar view based on a backing system store.
 */
public class StoredGrammarView implements GrammarView, Observer {
    /**
     * Constructs a grammar view from a rule system store. If the store is
     * modifiable and the view should be kept in sync, it should be added as
     * observer to the store.
     */
    public StoredGrammarView(SystemStore store) {
        this.store = store;
        loadRuleMap();
        loadGraphMap();
        loadControlMap();
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

    public Map<RuleName,AspectualRuleView> getRuleMap() {
        return Collections.unmodifiableMap(this.ruleMap);
    }

    public Map<String,AspectualGraphView> getGraphMap() {
        return Collections.unmodifiableMap(this.graphMap);
    }

    public Map<String,ControlView> getControlMap() {
        return Collections.unmodifiableMap(this.controlMap);
    }

    /**
     * Adds a rule based on a given rule view.
     * @see #getRule(RuleName)
     */
    public AspectualRuleView addRule(AspectualRuleView ruleView)
        throws IllegalStateException {
        throw new UnsupportedOperationException();
    }

    //
    // /**
    // * Removes a rule view with a given name. Also removes the rule from the
    // * graph grammar.
    // * @return the view previously stored with name <code>name</code>, or
    // * <code>null</code>
    // */
    // public AspectualRuleView removeRule(RuleName name) {
    // AspectualRuleView result = this.ruleMap.remove(name);
    // invalidateGrammar();
    // return result;
    // }

    public AspectualRuleView getRule(RuleName name) {
        return this.ruleMap.get(name);
    }

    public AspectualGraphView getStartGraph() {
        return this.startGraph;
    }

    /** Sets the start graph to a given graph. */
    public void setStartGraph(AspectualGraphView startGraph) {
        this.startGraph = startGraph;
        invalidate();
    }

    /** Collects and returns the permanent errors of the rule views. */
    public List<String> getErrors() {
        if (this.errors == null) {
            initGrammar();
        }
        return this.errors;
    }

    /** Delegates to {@link #toGrammar()}. */
    public GraphGrammar toModel() throws FormatException {
        return toGrammar();
    }

    /** Converts the grammar view to a real grammar. */
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
     * Records the name of the control program to be used. Currently, this is
     * always {@link Groove#DEFAULT_CONTROL_NAME}.
     */
    private String getControlName() {
        return Groove.DEFAULT_CONTROL_NAME;
    }

    /** Initialises the {@link #grammar} and {@link #errors} fields. */
    private void initGrammar() {
        try {
            this.grammar = computeGrammar();
            this.errors = Collections.emptyList();
        } catch (FormatException exc) {
            this.errors = new ArrayList<String>(exc.getErrors());
            Collections.sort(this.errors);
        }
    }

    /**
     * Computes a graph grammar from this view.
     * @throws FormatException if there are syntax errors in the view
     */
    private GraphGrammar computeGrammar() throws FormatException {
        GraphGrammar result = new GraphGrammar(getName());
        List<String> errors = new ArrayList<String>();

        for (RuleView ruleView : getRuleMap().values()) {
            try {
                // only add the enabled rules
                if (ruleView.isEnabled()) {
                    result.add(ruleView.toRule());
                }
            } catch (FormatException exc) {
                for (String error : exc.getErrors()) {
                    errors.add(String.format("Format error in '%s': %s",
                        ruleView.getName(), error));
                }
            }
        }

        boolean hasControl = getProperties().isControlEnabled();
        ControlView controlView =
            hasControl ? getControlMap().get(getControlName()) : null;
        if (controlView != null) {
            if (result.hasMultiplePriorities()) {
                errors.add("Rule priorities and control programs are incompatible, please disable either.");
            } else {
                try {
                    ControlAutomaton ca = controlView.toAutomaton(result);
                    result.setControl(ca);
                } catch (FormatException exc) {
                    for (String error : exc.getErrors()) {
                        errors.add(String.format(
                            "Format error in control program: %s", error));
                    }
                }
            }
        }

        result.setProperties(getProperties());
        if (getStartGraph() == null) {
            errors.add(String.format("No start graph set for grammar '%s'",
                getName()));
        } else {
            try {
                result.setStartGraph(getStartGraph().toModel());
            } catch (FormatException exc) {
                for (String error : exc.getErrors()) {
                    errors.add(String.format("Format error in start graph: %s",
                        error));
                }
            }
            try {
                result.setFixed();
            } catch (FormatException exc) {
                errors.addAll(exc.getErrors());
            }
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
    private void invalidate() {
        this.grammar = null;
        this.errors = null;
    }

    /**
     * Reloads the rule map from the backing {@link SystemStore}.
     */
    private void loadRuleMap() {
        this.ruleMap.clear();
        for (Map.Entry<RuleName,AspectGraph> storedRuleEntry : this.store.getRules().entrySet()) {
            this.ruleMap.put(storedRuleEntry.getKey(),
                storedRuleEntry.getValue().toRuleView(getProperties()));
        }
    }

    /**
     * Reloads the graph map from the backing {@link SystemStore}.
     */
    private void loadGraphMap() {
        this.graphMap.clear();
        for (Map.Entry<String,AspectGraph> storedGraphEntry : this.store.getGraphs().entrySet()) {
            this.graphMap.put(storedGraphEntry.getKey(),
                storedGraphEntry.getValue().toGraphView(getProperties()));
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
    public void update(Observable arg0, Object arg1) {
        assert arg0 == this.store;
        String change = (String) arg1;
        if (change.equals(SystemStore.GRAPH_CHANGE)) {
            loadGraphMap();
        } else if (change.equals(SystemStore.RULE_CHANGE)) {
            loadRuleMap();
        } else if (change.equals(SystemStore.CONTROL_CHANGE)) {
            loadControlMap();
        }
        invalidate();
    }

    /** Mapping from rule names to views on the corresponding rules. */
    private final Map<RuleName,AspectualRuleView> ruleMap =
        new HashMap<RuleName,AspectualRuleView>();
    /** Mapping from graph names to views on the corresponding graphs. */
    private final Map<String,AspectualGraphView> graphMap =
        new HashMap<String,AspectualGraphView>();
    /** Mapping from control names to views on the corresponding automata. */
    private final Map<String,ControlView> controlMap =
        new HashMap<String,ControlView>();

    /** The store backing this view. */
    private final SystemStore store;
    /** The start graph of the grammar. */
    private AspectualGraphView startGraph;
    /** Possibly empty list of errors found in the conversion to a grammar. */
    private List<String> errors;
    /** The graph grammar derived from the rule views. */
    private GraphGrammar grammar;

    /**
     * Creates an instance based on a store located at a given URL.
     * @param url the URL to load the grammar from
     * @throws IllegalArgumentException if no store can be created from the
     *         given URL
     * @throws IOException if a store can be created but not loaded
     */
    static public StoredGrammarView newInstance(URL url)
        throws IllegalArgumentException, IOException {
        SystemStore store = null;
        try {
            store = new DefaultArchiveSystemStore(url, true);
        } catch (IllegalArgumentException exc) {
            store = new DefaultFileSystemStore(url, true);
        }
        store.reload();
        return store.toGrammarView();
    }

    /**
     * Creates an instance based on a given file.
     * @param file the file to load the grammar from
     * @throws IllegalArgumentException if no store can be created from the
     *         given file
     * @throws IOException if a store can be created but not loaded
     */
    static public StoredGrammarView newInstance(File file)
        throws IllegalArgumentException, IOException {
        SystemStore store = null;
        try {
            store = new DefaultArchiveSystemStore(file, true);
        } catch (IllegalArgumentException exc) {
            store = new DefaultFileSystemStore(file, true);
        }
        store.reload();
        return store.toGrammarView();
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
            return newInstance(new File(location));
        } catch (IOException exc) {
            return newInstance(new File(location));
        }
    }
}
