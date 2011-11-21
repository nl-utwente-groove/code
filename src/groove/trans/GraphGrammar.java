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
import groove.util.CollectionOfCollections;
import groove.view.FormatException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
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
 * @see RuleName
 * @see Rule
 */
public class GraphGrammar {
    /**
     * Constructs an initially empty rule system.
     */
    public GraphGrammar(String name) {
        this.name = name;
    }

    /**
     * Constructs a clone of a given rule system.
     * @param other the rule system to be cloned
     * @require <tt>ruleSystem != null</tt>
     * @ensure <tt>equals(ruleSystem)</tt>
     */
    public GraphGrammar(GraphGrammar other, DefaultHostGraph startGraph) {
        this(other.getName());
        this.startGraph = startGraph;
        getProperties().putAll(other.getProperties());
        this.nameRuleMap.putAll(other.nameRuleMap);
        // the target sets of the priority rule map must be copied, not aliased
        for (Map.Entry<Integer,Set<Rule>> priorityRuleEntry : other.priorityRuleMap.entrySet()) {
            Set<Rule> newRuleSet = createRuleSet();
            newRuleSet.addAll(priorityRuleEntry.getValue());
            this.priorityRuleMap.put(priorityRuleEntry.getKey(), newRuleSet);
        }
    }

    /**
     * Returns the name of this rule system. May be <tt>null</tt> if the rule
     * system is anonymous.
     */
    public String getName() {
        return this.name;
    }

    /** Convenience method to return the rule with a name given as a string. */
    public Rule getRule(String name) {
        return this.nameRuleMap.get(name);
    }

    /**
     * Returns an unmodifiable view upon the names of all production rules in
     * this production system.
     * @ensure <tt>result.equals(getRuleMap.keySet())</tt>
     */
    public Set<String> getRuleNames() {
        return Collections.unmodifiableSet(this.nameRuleMap.keySet());
    }

    /**
     * Returns an unmodifiable view upon the map from available priorities to
     * rules with that priority. The map is sorted from high to low priority.
     */
    public SortedMap<Integer,Set<Rule>> getRuleMap() {
        return Collections.unmodifiableSortedMap(this.priorityRuleMap);
    }

    /**
     * Returns a Set<Rule> Iterator based on the global priorities
     * 
     * @return Iterator<Set<Rule>>
     */
    public Iterator<Set<Rule>> getRuleSetIter() {
        return this.priorityRuleMap.values().iterator();
    }

    /**
     * Returns an unmodifiable view upon the underlying collection of rules. The
     * result is ordered by descending priority, and within each priority, by
     * alphabetical order of the names. Don't invoke {@link Object#equals} on
     * the result!
     * @ensure <tt>result: Label -> Rule</tt>
     */
    public Collection<Rule> getRules() {
        Collection<Rule> result = this.ruleSet;
        if (result == null) {
            result =
                Arrays.asList(new CollectionOfCollections<Rule>(
                    this.priorityRuleMap.values()).toArray(new Rule[0]));
            if (isFixed()) {
                this.ruleSet = result;
            }
        }
        return result;
    }

    /**
     * Returns <tt>true</tt> if the rule system has rules at more than one
     * priority.
     */
    public boolean hasMultiplePriorities() {
        return this.priorityRuleMap.size() > 1;
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        res.append("Rule system:\n    ");
        for (Rule production : getRules()) {
            res.append(production + "\n");
        }
        res.append("\nStart graph:\n    ");
        res.append(getStartGraph().toString());
        return res.toString();
    }

    /**
     * Adds a production rule to this rule system. Removes the existing rule
     * with the same name, if any, and returns it. This is only allowed if the
     * grammar is not yet fixed, as indicated by {@link #isFixed()}.
     * @param rule the production rule to be added
     * @require <tt>rule != null</tt>
     * @throws IllegalStateException if the rule system is fixed
     * @see #isFixed()
     */
    public Rule add(Rule rule) {
        testFixed(false);
        String ruleName = rule.getName();
        int priority = rule.getPriority();
        Rule oldRuleForName = remove(ruleName);
        // add the rule to the priority map
        Set<Rule> priorityRuleSet = this.priorityRuleMap.get(priority);
        // if there is not yet any rule with this priority, create a set
        if (priorityRuleSet == null) {
            this.priorityRuleMap.put(priority, priorityRuleSet =
                createRuleSet());
        }
        priorityRuleSet.add(rule);
        // add the rule to the map
        this.nameRuleMap.put(ruleName, rule);
        return oldRuleForName;
    }

    /**
     * Removes a named production rule from this rule system, and returns it.
     * This is only allowed if the grammar is not yet fixed, as indicated by
     * {@link #isFixed()}.
     * @param ruleName the name of the production rule to be added
     * @throws IllegalStateException if the rule system is fixed
     * @see #isFixed()
     */
    private Rule remove(String ruleName) {
        testFixed(false);
        Rule result = this.nameRuleMap.remove(ruleName);
        // now remove the old rule with this name, if any
        if (result != null) {
            int priority = result.getPriority();
            Set<Rule> priorityRuleSet = this.priorityRuleMap.get(priority);
            priorityRuleSet.remove(result);
            // if this is the last rule with this priority, remove the entry
            // from the map
            if (priorityRuleSet.isEmpty()) {
                this.priorityRuleMap.remove(priority);
            }
        }
        return result;
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
     * Sets the rule system to fixed. After invoking this method,
     * {@link #add(Rule)} will throw an {@link IllegalStateException}.
     * @throws FormatException if the rules are inconsistent with the system
     *         properties or there is some other reason why they cannot be used
     *         in derivations.
     * @see #testConsistent()
     */
    public void setFixed() throws FormatException {
        testConsistent();
        for (Rule rule : getRules()) {
            rule.setFixed();
        }
        getStartGraph().setFixed();
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
        SystemProperties currentRuleProperties = getProperties();
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
    public SystemProperties getProperties() {
        if (this.properties == null) {
            this.properties = createProperties();
        }
        return this.properties;
    }

    /** Sets the type for this grammar.
     * @param type the combined type graph
     * @param typeMap the constituent type subgraphs; is {@code null} if 
     * the type graph is implicit
     */
    public final void setTypeGraph(TypeGraph type, Map<String,TypeGraph> typeMap) {
        this.typeGraph = type;
        this.typeMap = typeMap;
    }

    /** Returns the labels and subtypes of this rule system. */
    public final TypeGraph getTypeGraph() {
        return this.typeGraph;
    }

    /** Returns the set of constituent type subgraphs of this grammar. */
    public final Map<String,TypeGraph> getTypeMap() {
        return this.typeMap;
    }

    /**
     * Tests if the rule system is consistent. This is called in the course of
     * {@link #setFixed()}.
     */
    public void testConsistent() throws FormatException {
        List<String> errors = new ArrayList<String>();
        // collect the exceptions of the rules
        if (this.typeGraph == null) {
            errors.add(String.format("Labels and subtypes not initialised"));
        }
        // if any exception was encountered, throw it
        if (!errors.isEmpty()) {
            throw new FormatException(errors);
        }
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
    public void setStartGraph(HostGraph startGraph)
        throws IllegalStateException {
        testFixed(false);
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
        return (obj instanceof GraphGrammar)
            && getStartGraph().equals(((GraphGrammar) obj).getStartGraph())
            && super.equals(obj);
    }

    /** Combines the hash codes of the rule system and the start graph. */
    @Override
    public int hashCode() {
        return (getStartGraph().hashCode() << 8) ^ super.hashCode();
    }

    /**
     * Callback factory method to create an initially empty
     * {@link SystemProperties} object for this graph grammar.
     */
    private SystemProperties createProperties() {
        return new SystemProperties();
    }

    /**
     * Factory method to create a set to contain rules. This implementation
     * returns a {@link TreeSet}.
     */
    private Set<Rule> createRuleSet() {
        return new TreeSet<Rule>();
    }

    /**
     * A mapping from the rule names to the rules.
     */
    private final Map<String,Rule> nameRuleMap = new TreeMap<String,Rule>();
    /**
     * A mapping from priorities to sets of rules having that priority. The
     * ordering is from high to low priority.
     */
    private final SortedMap<Integer,Set<Rule>> priorityRuleMap =
        new TreeMap<Integer,Set<Rule>>(Rule.PRIORITY_COMPARATOR);
    /**
     * Set of rules, collected separately for purposes of speedup.
     * @see #getRules()
     */
    private Collection<Rule> ruleSet;
    /** Set of constituent type graphs. Is {@code null} iff {@link #typeGraph} is implicit. */
    private Map<String,TypeGraph> typeMap;
    /**
     * The properties bundle of this rule system.
     */
    private SystemProperties properties;
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
