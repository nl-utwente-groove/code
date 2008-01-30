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

import groove.util.CollectionOfCollections;import groove.view.FormatException;import java.util.ArrayList;import java.util.Arrays;import java.util.Collection;import java.util.Collections;import java.util.HashSet;import java.util.Iterator;import java.util.List;import java.util.Map;import java.util.Set;import java.util.SortedMap;import java.util.TreeMap;import java.util.TreeSet;

/**
 * Model of a production system, as a simple map of rule names to 
 * production rules.
 * Provides functionality to apply the system to a graph,
 * independently of the exact graph and rule implementation.
 * Any instance of this class is specialized towards a particular 
 * graph implementation.
 * @author Arend Rensink
 * @version $Revision: 1.18 $ $Date: 2008-01-30 12:37:40 $
 * @see NameLabel
 * @see SPORule
 */
public class RuleSystem {
    /** 
	 * Constructs an initially empty, anonymous rule system, with
	 * default rule factory. 
	 */
    public RuleSystem() {
        this((String) null);
    }

    /** 
	 * Constructs an initially empty rule system.
	 */
    public RuleSystem(String name) {
        this.name = name;
    }

    /**
     * Constructs a clone of a given rule system.
     * @param other the rule system to be cloned
     * @require <tt>ruleSystem != null</tt>
     * @ensure <tt>equals(ruleSystem)</tt>
     */
    public RuleSystem(RuleSystem other) {
    	this(other.getName());
    	getProperties().putAll(other.getProperties());
        nameRuleMap.putAll(other.nameRuleMap);
        // the target sets of the priority rule map must be copied, not aliased
        for (Map.Entry<Integer,Set<Rule>> priorityRuleEntry: other.priorityRuleMap.entrySet()) {
            Set<Rule> newRuleSet = createRuleSet();
            newRuleSet.addAll(priorityRuleEntry.getValue());
            priorityRuleMap.put(priorityRuleEntry.getKey(), newRuleSet);
        }
    }

    /**
     * Returns the name of this rule system.
     * May be <tt>null</tt> if the rule system is anonymous.
     */
    public String getName() {
        return name;
    }
    
    /**
     * Returns the production rule known under a given name, if any.
     * @param name the name of the requested production rule
     * @return the Rule known as "name"; null if name is not known
     * @ensure <tt>result.equals(getRuleMap.get(name))</tt>
      */
    public Rule getRule(NameLabel name) {
        return nameRuleMap.get(name);
    }
    
    /** Convenience method to return the rule with a name given as a string. */
    public Rule getRule(String name) {
        return getRule(createRuleName(name));
    }

    /**
     * Returns an unmodifiable view upon the names of all production rules in this production system.
     * @ensure <tt>result.equals(getRuleMap.keySet())</tt>
     */
    public Set<NameLabel> getRuleNames() {
        return Collections.unmodifiableSet(nameRuleMap.keySet());
    }

    /**
     * 
     */
    public Set<Rule> getChildRules(String parent) {
    	Set<Rule> result = new HashSet<Rule>();
    	
    	//TODO: optimize
    	
    	for( Rule rule : this.getRules() ) {
    		RuleNameLabel label = rule.getName().parent();
    		while( label != null ) {
    			if( label.name().compareTo(parent) == 0 )
    				result.add(rule);
    			label = label.parent();
    		}
    	}
    	
    	return result;
    }
    
    /**
     * Returns an unmodifiable view upon the map from available priorities to 
     * rules with that priority. The map is sorted from high to low priority.
     */
    public SortedMap<Integer, Set<Rule>> getRuleMap() {
        return Collections.unmodifiableSortedMap(priorityRuleMap);
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
     * Returns an unmodifiable view upon the underlying collection of rules.
     * The result is ordered by descending priority, and within each priority,
     * by alphabetical order of the names.
     * Don't invoke {@link Object#equals} on the result!
     * @ensure <tt>result: Label -> Rule</tt>
     */
    public Collection<Rule> getRules() {
    	Collection<Rule> result = ruleSet;
    	if (result == null) {
    		result = Arrays.asList(new CollectionOfCollections<Rule>(priorityRuleMap.values()).toArray(new Rule[0]));
    		if (isFixed()) {
    			ruleSet = result;
    		}
    	}
    	return result;
    }

    /**
     * Returns <tt>true</tt> if the rule system has rules at more than one priority.
     */
    public boolean hasMultiplePriorities() {
        return priorityRuleMap.size() > 1;
    }

    @Override
    public String toString() {
        String res = "";
        for (Rule production: getRules()) {
            res += production + "\n";
        }
        return res;
    }

    /**
     * Adds a production rule to this rule system.
     * Removes the existing rule with the same name, if any, and returns it.
     * This is only allowed if the grammar is not yet fixed, as indicated by {@link #isFixed()}.
     * @param rule the production rule to be added
     * @require <tt>rule != null</tt>
     * @throws IllegalStateException if the rule system is fixed
     * @see #isFixed()
     */
    public Rule add(Rule rule) {
    	testFixed(false);
        NameLabel ruleName = rule.getName();
        int priority = rule.getPriority();
        Rule oldRuleForName = remove(ruleName);
        // add the rule to the priority map
        Set<Rule> priorityRuleSet = priorityRuleMap.get(priority);
        // if there is not yet any rule with this priority, create a set
        if (priorityRuleSet == null) {
            priorityRuleMap.put(priority, priorityRuleSet = createRuleSet());
        }
        priorityRuleSet.add(rule);
        // add the rule to the map
        nameRuleMap.put(ruleName, rule);
        return oldRuleForName;
    }

    /**
     * Removes a named production rule from this rule system, and returns it.
     * This is only allowed if the grammar is not yet fixed, as indicated by {@link #isFixed()}.
     * @param ruleName the name of the production rule to be added
     * @throws IllegalStateException if the rule system is fixed
     * @see #isFixed()
     */
    public Rule remove(NameLabel ruleName) {
    	testFixed(false);
        Rule result = nameRuleMap.remove(ruleName);
        // now remove the old rule with this name, if any
        if (result != null) {
            int priority = result.getPriority();
            Set<Rule> priorityRuleSet = priorityRuleMap.get(priority);
            priorityRuleSet.remove(result);
            // if this is the last rule with this priority, remove the entry from the map
            if (priorityRuleSet.isEmpty()) {
                priorityRuleMap.remove(priority);
            }
        }
        return result;
    }

    /**
	 * Indicates if the rule system is fixed.
	 * Rules can only be added or edited in a non-fixed rule system, 
	 * whereas the rule system can only be used for derivations when it is fixed. 
	 */
	public final boolean isFixed() {
		return fixed;
	}

	/**
	 * Sets the rule system to fixed.
	 * After invoking this method, {@link #add(Rule)} will throw an {@link IllegalStateException}.
	 * @throws FormatException if the rules are inconsistent with the system properties
	 * or there is some other reason why they cannot be used in derivations.
	 * @see #testConsistent()
	 */
	public void setFixed() throws FormatException {
		testConsistent();
		this.fixed = true;
		for (Rule rule: getRules()) {
			rule.setFixed();
		}
	}
	
	/**
	 * Tests the the fixedness of the rule system.
	 * @param value the expected fixedness
	 * @throws IllegalStateException if {@link #isFixed()} does not equal <code>value</code>
	 */
	public final void testFixed(boolean value) throws IllegalStateException {
		if (isFixed() != value) {
			if (value) {
				throw new IllegalStateException("Operation not allowed: Rule system is not fixed");
			} else {
				throw new IllegalStateException("Operation not allowed: Rule system is fixed");
			}
		}
	}

	/**
     * Sets the properties of this graph grammar by copying a given property mapping.
     * Clears the current properties first.
     * @param properties the new properties mapping
     */
    public void setProperties(java.util.Properties properties) {
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
     * Returns the properties object for this graph grammar.
     * The properties object is immutable.
     */
    public SystemProperties getProperties() {
        if (properties == null) {
            properties = createProperties();
        }
        return properties;
    }
    
    /** 
     * Tests if a given rule is consistent with the properties of this rule system. 
     * If it is not consistent, then it cannot be added to the rule system.
     * The reason for the inconsistency can be retrieved using #getInconsistency(Rule)
     */
    public void testConsistent() throws FormatException {
    	List<String> errors = new ArrayList<String>();
    	// collect the exceptions of the rules
    	for (Rule rule: getRules()) {
    		try {
    			rule.testConsistent();
    		} catch (FormatException exc) {
    			for (String error: exc.getErrors()) {
    				errors.add(String.format("System property error in %s: %s", rule.getName(), error));
    			}
    		}
    	}
    	// if any exception was encountered, throw it
    	if (! errors.isEmpty()) {
    		throw new FormatException(errors);
    	}
    }
    
	/**
     * Callback factory method to create an initially empty {@link SystemProperties} object 
     * for this graph grammar.
     */
    protected SystemProperties createProperties() {
        return new SystemProperties();
    }
    
    /**
     * Callback factory method to create a rule name from a {@link String}.
     */
    protected NameLabel createRuleName(String name) {
        return new RuleNameLabel(name);
    }
    /** 
     * Factory method to create a set to contain rules.
     * This implementation returns a {@link TreeSet}.
     */
    protected Set<Rule> createRuleSet() {
        return new TreeSet<Rule>();
    }
    
    /** 
     * Tests if the rule system currently contains any rules, and
     * throws an exception if it does.
     * @throws IllegalStateException if {@link #getRules()} returns a non-empty set.
     */
    void testRuleSystemEmpty() throws IllegalStateException {
    	if (! getRules().isEmpty()) {
    		throw new IllegalStateException(String.format("Rule system not empty: %s", getRuleNames()));
    	}
    }
    
    /**
     * A mapping from the rule names to the rules.
     */
    protected final Map<NameLabel,Rule> nameRuleMap = new TreeMap<NameLabel,Rule>();
    /**
     * A mapping from priorities to sets of rules having that priority.
     * The ordering is from high to low priority. 
     */
    protected final SortedMap<Integer,Set<Rule>> priorityRuleMap = new TreeMap<Integer,Set<Rule>>(Rule.PRIORITY_COMPARATOR);
    /**
     * Set of rules, collected separately for purposes of speedup.
     * @see #getRules()
     */
    private Collection<Rule> ruleSet;
    /**
     * The properties bundle of this rule system.
     */
    private SystemProperties properties; 
    /** Flag indicating that the rule system has been fixed and is ready for use. */
    private boolean fixed;

    /**
     * The name of this grammar;
     * <tt>null</tt> if the grammar is anonymous.
     */
    private final String name;
}
