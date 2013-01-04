// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// http://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
// either express or implied. See the License for the specific
// language governing permissions and limitations under the License.
/*
 * $Id: RuleSystem.java,v 1.18 2008-01-30 12:37:40 fladder Exp $
 */
package groove.trans;

import groove.control.CtrlAut;
import groove.graph.TypeGraph;
import groove.prolog.GrooveEnvironment;
import groove.util.Fixable;
import groove.view.FormatException;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Default model of a graph grammar, consisting of a production rule system and
 * a default start graph.
 * Model of a graph grammar, as a simple map of rule names to production
 * rules and
 * a default start graph.
 * @author Arend Rensink
 * @version $Revision$ $Date: 2008-01-30 12:37:40 $
 * @see QualName
 * @see Rule
 */
public class Grammar {
    /**
     * Constructs an initially empty rule system.
     */
    public Grammar(String name) {
        this.name = name;
    }

    /**
     * Constructs a clone of a given rule system.
     * @param other the rule system to be cloned
     * @require <tt>ruleSystem != null</tt>
     * @ensure <tt>equals(ruleSystem)</tt>
     */
    public Grammar(Grammar other, DefaultHostGraph startGraph) {
        this(other.getName());
        this.startGraph = startGraph;
        getProperties().putAll(other.getProperties());
        this.nameRuleMap.putAll(other.nameRuleMap);
        // the target sets of the priority rule map must be copied, not aliased
        for (Map.Entry<Integer,Set<Action>> priorityRuleEntry : other.priorityActionMap.entrySet()) {
            Set<Action> newRuleSet = createActionSet();
            newRuleSet.addAll(priorityRuleEntry.getValue());
            this.priorityActionMap.put(priorityRuleEntry.getKey(), newRuleSet);
        }
    }

    /**
     * Returns the name of this rule system. May be <tt>null</tt> if the rule
     * system is anonymous.
     */
    public String getName() {
        return this.name;
    }

    /** Convenience method to return the rule with a given name, if any. */
    public Rule getRule(String name) {
        return this.nameRuleMap.get(name);
    }

    /** Convenience method to return the recipe with a given name, if any. */
    public Recipe getRecipe(String name) {
        return this.nameRecipeMap.get(name);
    }

    /** Indicates if there are any recipes in this grammar. */
    public boolean hasRecipes() {
        return !this.nameRecipeMap.isEmpty();
    }

    /** Convenience method to return the action with a given name, if any. */
    public Action getAction(String name) {
        Action result = getRule(name);
        if (result == null) {
            result = getRecipe(name);
        }
        return result;
    }

    /**
     * Returns an unmodifiable view upon the map from available priorities to
     * actions with that priority. The map is sorted from high to low priority.
     */
    public SortedMap<Integer,Set<Action>> getActionMap() {
        return Collections.unmodifiableSortedMap(this.priorityActionMap);
    }

    /**
     * Returns the underlying set of actions, i.e., rules and recipes. The
     * result is ordered by descending priority, and within each priority, by
     * alphabetical order of the names.
     */
    public Set<Action> getActions() {
        return this.actions;
    }

    /**
     * Returns the underlying set of rules.
     * This combines the top-level rules and the subrules used in recipes.
     */
    public Set<Rule> getAllRules() {
        return this.allRules;
    }

    /**
     * Returns <tt>true</tt> if the rule system has actions at more than one
     * priority.
     */
    public boolean hasMultiplePriorities() {
        return this.priorityActionMap.size() > 1;
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        res.append("Rule system:\n    ");
        for (Action action : getActions()) {
            res.append(action + "\n");
        }
        res.append("\nStart graph:\n    ");
        res.append(getStartGraph().toString());
        return res.toString();
    }

    /**
     * Adds a new action to this rule system.
     * This is only allowed if the
     * grammar is not yet fixed, as indicated by {@link #isFixed()};
     * moreover, the action name should be new.
     * @param action the production rule to be added
     * @require <tt>rule != null</tt>
     * @throws IllegalStateException if the rule system is fixed
     * @see #isFixed()
     */
    public void add(Action action) {
        testFixed(false);
        assert action instanceof Fixable ? ((Fixable) action).isFixed() : false;
        String actionName = action.getFullName();
        int priority = action.getPriority();
        // add the rule to the priority map
        Set<Action> priorityRuleSet = this.priorityActionMap.get(priority);
        // if there is not yet any rule with this priority, create a set
        if (priorityRuleSet == null) {
            this.priorityActionMap.put(priority, priorityRuleSet =
                createActionSet());
        }
        priorityRuleSet.add(action);
        this.actions.add(action);
        // add the rule to the map
        switch (action.getKind()) {
        case RULE:
            Rule rule = (Rule) action;
            this.nameRuleMap.put(actionName, rule);
            this.allRules.add(rule);
            break;
        case RECIPE:
            Recipe recipe = (Recipe) action;
            this.nameRecipeMap.put(actionName, recipe);
            for (Rule subRule : recipe.getRules()) {
                subRule.setPartial();
                this.allRules.add(subRule);
            }
        }
    }

    /**
     * Indicates if the rule system is fixed. Rules can only be added or edited
     * in a non-fixed rule system, whereas the rule system can only be used for
     * derivations when it is fixed.
     */
    public final boolean isFixed() {
        assert !this.fixed || this.typeGraph != null;
        return this.fixed;
    }

    /**
     * Sets the rule system to fixed.
     * @throws FormatException if the rules are inconsistent with the system
     *         properties or there is some other reason why they cannot be used
     *         in derivations.
     */
    public void setFixed() throws FormatException {
        this.fixed = true;
    }

    /**
     * Tests the the fixedness of the rule system.
     * @param value the expected fixedness
     * @throws IllegalStateException if {@link #isFixed()} does not equal
     *         <code>value</code>
     */
    public final void testFixed(boolean value) throws IllegalStateException {
        if (isFixed() != value) {
            if (value) {
                throw new IllegalStateException(
                    "Operation not allowed: Rule system is not fixed");
            } else {
                throw new IllegalStateException(
                    "Operation not allowed: Rule system is fixed");
            }
        }
    }

    /**
     * Sets the properties of this graph grammar by copying a given property
     * mapping. Clears the current properties first.
     * @param properties the new properties mapping
     */
    public void setProperties(java.util.Properties properties) {
        testFixed(false);
        GrammarProperties currentRuleProperties = getProperties();
        currentRuleProperties.clear();
        currentRuleProperties.putAll(properties);
    }

    /**
     * Convenience method to retrieve the value of a property key.
     * @see #getProperties()
     */
    public String getProperty(String key) {
        return getProperties().getProperty(key);
    }

    /**
     * Returns the properties object for this graph grammar. The properties
     * object is immutable.
     */
    public GrammarProperties getProperties() {
        if (this.properties == null) {
            this.properties = createProperties();
        }
        return this.properties;
    }

    /** Sets the type for this grammar.
     * @param type the combined type graph
     */
    public final void setTypeGraph(TypeGraph type) {
        testFixed(false);
        assert type.isFixed();
        this.typeGraph = type;
    }

    /** Returns the labels and subtypes of this rule system. */
    public final TypeGraph getTypeGraph() {
        return this.typeGraph;
    }

    /**
     * Returns the start graph of this graph grammar. If the graph is not set at
     * the time of invocation, an empty start graph is created through the graph
     * factory.
     * @return the start graph of this GraphGrammar
     * @ensure <tt>result != null</tt>
     */
    public HostGraph getStartGraph() {
        return this.startGraph;
    }

    /**
     * Changes or sets the start graph of this graph grammar. This is only
     * allowed if the grammar is not yet fixed, as indicated by
     * {@link #isFixed()}.
     * @param startGraph the new start graph of this graph grammar
     * @throws IllegalStateException if the grammar is already fixed
     * @see #isFixed()
     */
    public void setStartGraph(HostGraph startGraph) {
        testFixed(false);
        assert startGraph.isFixed();
        this.startGraph = startGraph;
    }

    /**
     * Sets a control automaton for this grammar. This is only allowed if the
     * grammar is not yet fixed, as indicated by {@link #isFixed()}.
     * @throws IllegalStateException if the grammar is already fixed
     * @see #isFixed()
     */
    public void setCtrlAut(CtrlAut aut) {
        testFixed(false);
        assert aut.isFixed();
        this.ctrlAut = aut;
    }

    /**
     * Returns the control automaton of this grammar, or <code>null</code> if
     * there is none.
     */
    public CtrlAut getCtrlAut() {
        return this.ctrlAut;
    }

    /**
     * Sets a Prolog environment for this grammar. This is only allowed if the
     * grammar is not yet fixed, as indicated by {@link #isFixed()}.
     * @throws IllegalStateException if the grammar is already fixed
     * @see #isFixed()
     */
    public void setPrologEnvironment(GrooveEnvironment prologEnvironment) {
        testFixed(false);
        this.prologEnvironment = prologEnvironment;
    }

    /**
     * Returns the Prolog environment of this grammar, or <code>null</code> if
     * there is none.
     */
    public GrooveEnvironment getPrologEnvironment() {
        return this.prologEnvironment;
    }

    /** Tests for equality of the rule system and the start graph. */
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Grammar)
            && getStartGraph().equals(((Grammar) obj).getStartGraph())
            && super.equals(obj);
    }

    /** Combines the hash codes of the rule system and the start graph. */
    @Override
    public int hashCode() {
        return (getStartGraph().hashCode() << 8) ^ super.hashCode();
    }

    /**
     * Callback factory method to create an initially empty
     * {@link GrammarProperties} object for this graph grammar.
     */
    private GrammarProperties createProperties() {
        return new GrammarProperties();
    }

    /**
     * Factory method to create a set to contain rules. This implementation
     * returns a {@link TreeSet}.
     */
    private Set<Action> createActionSet() {
        return new TreeSet<Action>();
    }

    /**
     * A mapping from action names to the available rules.
     */
    private final Map<String,Rule> nameRuleMap = new TreeMap<String,Rule>();
    /**
     * A mapping from action names to the available transactions.
     */
    private final Map<String,Recipe> nameRecipeMap =
        new TreeMap<String,Recipe>();
    /**
     * A mapping from priorities to sets of rules having that priority. The
     * ordering is from high to low priority.
     */
    private final SortedMap<Integer,Set<Action>> priorityActionMap =
        new TreeMap<Integer,Set<Action>>(Action.PRIORITY_COMPARATOR);
    /**
     * Set of all actions, collected separately for purposes of speedup.
     * @see #getActions()
     */
    private final Set<Action> actions = new TreeSet<Action>(
        Action.ACTION_COMPARATOR);
    /**
     * Set of all rules, being the union of the top-level action rules
     * and the subrules used in recipes.
     */
    private final Set<Rule> allRules = new HashSet<Rule>();
    /**
     * The properties bundle of this rule system.
     */
    private GrammarProperties properties;
    /** The labels and subtypes occurring in this rule system. */
    private TypeGraph typeGraph;
    /**
     * Flag indicating that the rule system has been fixed and is ready for use.
     */
    private boolean fixed;

    /**
     * The name of this grammar; <tt>null</tt> if the grammar is anonymous.
     */
    private final String name;
    /**
     * The start graph of this graph grammar.
     */
    private HostGraph startGraph;
    /**
     * The control automaton of this grammar; <code>null</code> if there is
     * none.
     */
    private CtrlAut ctrlAut;
    /** The prolog environment derived from the system store. */
    private GrooveEnvironment prologEnvironment;
}
