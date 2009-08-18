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
import groove.trans.GraphGrammar;
import groove.trans.NameLabel;
import groove.trans.RuleNameLabel;
import groove.trans.SystemProperties;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

/**
 * Graph grammar with {@link RuleView} information for each rule.
 */
public class DefaultGrammarView implements
        GrammarView<AspectualGraphView,AspectualRuleView>, View<GraphGrammar> {
    /**
     * Constructs a (non-fixed) copy of an existing rule view grammar.
     */
    public DefaultGrammarView(
            GrammarView<AspectualGraphView,AspectualRuleView> oldGrammar) {
        this(oldGrammar.getName());
        getProperties().putAll(oldGrammar.getProperties());
        for (AspectualRuleView ruleView : oldGrammar.getRuleMap().values()) {
            addRule(ruleView);
        }
        setStartGraph(oldGrammar.getStartGraph());
    }

    /**
     * Constructs a named, empty grammar based on a given rule factory.
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
        invalidateGrammar();
    }

    public Map<RuleNameLabel,AspectualRuleView> getRuleMap() {
        return Collections.unmodifiableMap(this.ruleMap);
    }

    /**
     * Adds a rule based on a given rule view.
     * @see #getRule(RuleNameLabel)
     */
    public AspectualRuleView addRule(AspectualRuleView ruleView)
        throws IllegalStateException {
        AspectualRuleView result =
            this.ruleMap.put(ruleView.getNameLabel(), ruleView);
        invalidateGrammar();
        return result;
    }

    /**
     * Removes a rule view with a given name. Also removes the rule from the
     * graph grammar.
     * @return the view previously stored with name <code>name</code>, or
     *         <code>null</code>
     */
    public AspectualRuleView removeRule(NameLabel name) {
        AspectualRuleView result = this.ruleMap.remove(name);
        invalidateGrammar();
        return result;
    }

    /**
     * Returns the rule view stored for a given rule name. May be
     * <code>null</code> if the rule cannot be viewed in the available
     * {@link RuleView} format.
     */
    public AspectualRuleView getRule(RuleNameLabel name) {
        return this.ruleMap.get(name);
    }

    public AspectualGraphView getStartGraph() {
        return this.startGraph;
    }

    /** Sets the start graph to a given graph. */
    public void setStartGraph(AspectualGraphView startGraph) {
        this.startGraph = startGraph;
        invalidateGrammar();
    }

    /** Collects and returns the permanent errors of the rule views. */
    public List<String> getErrors() {
        if (this.errors == null) {
            initGrammar();
        }
        return this.errors;
    }

    /** setter method for the control view * */
    public void setControl(ControlView control) {
        this.controlView = control;
        invalidateGrammar();
    }

    /** getter method for control view * */
    public ControlView getControl() {
        if (this.errors == null) {
            initGrammar();
        }
        return this.controlView;
    }

    /**
     * Adds a graph to the list of graphs belonging to this grammar.
     * @param name the name of the graph to be added
     * @param file the file where the graph is actually stored
     */
    public void addGraph(String name, File file) {
        this.graphs.put(name, file);
    }

    /**
     * Removes a graph from the list of graphs belonging to this grammar.
     * @param name The name of the graph to be removed (non-null)
     * @return the file at which the graph with this name was stored;
     *         <code>null</code> if there was no graph with this name.
     */
    public File removeGraph(String name) {
        return this.graphs.remove(name);
    }

    /** Returns the graphs found during loading of the grammar */
    public Map<String,File> getGraphs() {
        return this.graphs;
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

        for (RuleView ruleView : getRuleMap().values()) {
            try {
                // only add the enabled rules
                if (ruleView.isEnabled()) {
                    result.add(ruleView.toRule());
                }
            } catch (FormatException exc) {
                for (String error : exc.getErrors()) {
                    errors.add(String.format("Format error in %s: %s",
                        ruleView.getNameLabel(), error));
                }
            }
        }

        if (this.controlView != null) {

            if (result.hasMultiplePriorities()) {
                errors.add("Unable to combine rule priorities and a control program, please disable either.");
            } else {
                try {
                    ControlAutomaton ca = this.controlView.toAutomaton(result);
                    result.setControl(ca);
                } catch (FormatException e) {
                    errors.addAll(e.getErrors());
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
    private final Map<RuleNameLabel,AspectualRuleView> ruleMap =
        new TreeMap<RuleNameLabel,AspectualRuleView>();
    /** Mapping from priorities to sets of rule names. */
    /** The name of this grammar view. */
    private String name;
    /** The control automaton * */
    private ControlView controlView;
    /** The start gramg of the grammar. */
    private AspectualGraphView startGraph;
    /** The rule system properties of this grammar view. */
    private SystemProperties properties;
    /** Possibly empty list of errors found in the conversion to a grammar. */
    private List<String> errors;
    /** The graph grammar derived from the rule views. */
    private GraphGrammar grammar;

    /** Contains the list of states found in the grammar location * */
    private final Map<String,File> graphs = new HashMap<String,File>();
}