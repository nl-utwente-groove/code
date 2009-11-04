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
import groove.graph.GraphInfo;
import groove.io.AspectGxl;
import groove.io.LayedOutXml;
import groove.trans.GraphGrammar;
import groove.trans.RuleName;
import groove.trans.SystemProperties;
import groove.view.aspect.AspectGraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

/**
 * Graph grammar with {@link RuleView} information for each rule.
 * @deprecated Use {@link GrammarView} or {@link StoredGrammarView} instead
 */
@Deprecated
public class DefaultGrammarView implements GrammarView {
    // /**
    // * Constructs a (non-fixed) copy of an existing rule view grammar.
    // */
    // public DefaultGrammarView(
    // GrammarView<AspectualGraphView,AspectualRuleView> oldGrammar) {
    // this(oldGrammar.getName());
    // getProperties().putAll(oldGrammar.getProperties());
    // for (AspectualRuleView ruleView : oldGrammar.getRuleMap().values()) {
    // addRule(ruleView);
    // }
    // setStartGraph(oldGrammar.getStartGraph());
    // }
    /**
     * Constructs a named, empty grammar.
     */
    public DefaultGrammarView(String name) {
        this.name = name;
    }

    /** Returns the name of this grammar view. */
    public String getName() {
        return this.name;
    }

    /** Changes the name of this grammar view. */
    public void setName(String name) {
        this.name = name;
    }

    /** Returns the system properties of this grammar view. */
    public SystemProperties getProperties() {
        if (this.properties == null) {
            this.properties = new SystemProperties();
            this.properties.setFixed();
        }
        return this.properties;
    }

    /**
     * Sets and fixes the properties of this view, by copying a given properties
     * object.
     */
    public final void setProperties(Properties properties) {
        this.properties = new SystemProperties();
        this.properties.putAll(properties);
        for (AspectualRuleView rule : this.ruleMap.values()) {
            rule.setProperties(this.properties);
        }
        this.properties.setFixed();
        invalidateGrammar();
    }

    public Set<String> getGraphNames() {
        return Collections.unmodifiableSet(this.graphMap.keySet());
    }

    public Set<RuleName> getRuleNames() {
        return Collections.unmodifiableSet(this.ruleMap.keySet());
    }

    /**
     * Adds a rule based on a given rule view.
     * @see #getRuleView(RuleName)
     */
    public AspectualRuleView addRule(AspectualRuleView ruleView)
        throws IllegalStateException {
        AspectualRuleView result =
            this.ruleMap.put(ruleView.getRuleName(), ruleView);
        invalidateGrammar();
        return result;
    }

    /**
     * Removes a rule view with a given name. Also removes the rule from the
     * graph grammar.
     * @return the view previously stored with name <code>name</code>, or
     *         <code>null</code>
     */
    public AspectualRuleView removeRule(RuleName name) {
        AspectualRuleView result = this.ruleMap.remove(name);
        invalidateGrammar();
        return result;
    }

    public AspectualGraphView getGraphView(String name) {
        return this.graphMap.get(name);
    }

    public AspectualRuleView getRuleView(RuleName name) {
        return this.ruleMap.get(name);
    }

    public AspectualGraphView getStartGraphView() {
        return this.startGraph;
    }

    public String getStartGraphName() {
        return this.startGraphName;
    }

    @Override
    public boolean setStartGraph(String name) {
        assert name != null;
        AspectualGraphView graphView = this.graphMap.get(name);
        if (graphView == null) {
            return false;
        } else {
            this.startGraph = graphView;
            this.startGraphName = name;
            invalidateGrammar();
            return true;
        }
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
        invalidateGrammar();
    }

    public void removeStartGraph() {
        this.startGraph = null;
        invalidateGrammar();
    }

    /** Collects and returns the permanent errors of the rule views. */
    public List<String> getErrors() {
        if (this.errors == null) {
            initGrammar();
        }
        return this.errors;
    }

    @Override
    @Deprecated
    public Set<String> getControlNames() {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    public ControlView getControlView(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    public void setControl(String name) {
        throw new UnsupportedOperationException();
    }

    /** setter method for the control view * */
    public void setControl(ControlView control) {
        this.controlView = control;
        invalidateGrammar();
    }

    /** getter method for control view * */
    public ControlView getControlView() {
        if (this.errors == null) {
            initGrammar();
        }
        return this.controlView;
    }

    /**
     * Adds a graph to the list of graphs belonging to this grammar.
     * @param name the name of the graph to be added
     * @param graph the aspect graph representation of the graph
     */
    public void addGraph(String name, AspectGraph graph) {
        this.graphMap.put(name, graph.toGraphView(getProperties()));
    }

    /**
     * Removes a graph from the list of graphs belonging to this grammar.
     * @param name The name of the graph to be removed (non-null)
     * @return the file at which the graph with this name was stored;
     *         <code>null</code> if there was no graph with this name.
     */
    public AspectualGraphView removeGraph(String name) {
        return this.graphMap.remove(name);
    }

    /** Returns the graphs found during loading of the grammar */
    public Map<String,AspectualGraphView> getGraphs() {
        return this.graphMap;
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

        for (RuleView ruleView : this.ruleMap.values()) {
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

        if (getProperties().isUseControl()) {
            if (this.controlView != null) {
                if (result.hasMultiplePriorities()) {
                    errors.add("Unable to combine rule priorities and a control program, please disable either.");
                } else {
                    try {
                        ControlAutomaton ca =
                            this.controlView.toAutomaton(result);
                        result.setControl(ca);
                    } catch (FormatException e) {
                        errors.addAll(e.getErrors());
                    }
                }
            } else if (getProperties().getControlName() != null) {
                errors.add(String.format(
                    "Control program '%s' cannot be found",
                    getProperties().getControlName()));
            }
        }

        result.setProperties(getProperties());
        if (getStartGraphView() == null) {
            if (getStartGraphName() == null) {
                errors.add(String.format("No start graph set"));
            } else {
                errors.add(String.format("Start graph '%s' cannot be loaded",
                    getStartGraphName()));
            }
        } else {
            try {
                result.setStartGraph(getStartGraphView().toModel());
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
     * Resets the internally stored graph grammar to <code>null</code>, so the
     * next call to {@link #toGrammar()} will have to recompute it. This is done
     * in reaction to a change in the rules, start graph or properties.
     */
    private void invalidateGrammar() {
        this.grammar = null;
        this.errors = null;
        if (this.controlView != null) {
            this.controlView.invalidateAutomaton();
        }
    }

    /** Mapping from rule names to views on the corresponding rules. */
    private final Map<RuleName,AspectualRuleView> ruleMap =
        new TreeMap<RuleName,AspectualRuleView>();
    /** Mapping from priorities to sets of rule names. */
    /** The name of this grammar view. */
    private String name;
    /** The control automaton */
    private ControlView controlView;
    /** The start graph of the grammar. */
    private AspectualGraphView startGraph;
    /**
     * Name of the start graph, if the start graph is one of the graphs stored
     * in the rule system.
     */
    private String startGraphName;
    /** The rule system properties of this grammar view. */
    private SystemProperties properties;
    /** Possibly empty list of errors found in the conversion to a grammar. */
    private List<String> errors;
    /** The graph grammar derived from the rule views. */
    private GraphGrammar grammar;

    /** Contains the list of states found in the grammar location * */
    private final Map<String,AspectualGraphView> graphMap =
        new HashMap<String,AspectualGraphView>();

    @SuppressWarnings("unused")
    private final AspectGxl graphMarshaller = new AspectGxl(new LayedOutXml());
}