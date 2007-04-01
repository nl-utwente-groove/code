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
 * $Id: RuleSystem.java,v 1.4 2007-04-01 12:49:54 rensink Exp $
 */
package groove.trans;

import groove.util.CollectionOfCollections;
import groove.util.FormatException;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Model of a production system, as a simple map of rule names to 
 * production rules.
 * Provides functionality to apply the system to a graph,
 * independently of the exact graph and rule implementation.
 * Any instance of this class is specialized towards a particular 
 * graph implementation.
 * @author Arend Rensink
 * @version $Revision: 1.4 $ $Date: 2007-04-01 12:49:54 $
 * @see NameLabel
 * @see SPORule
 */
public class RuleSystem {
    /**
     * Property name of the list of control labels of a graph grammar.
     * The control labels will be first in the matching order.
     */
    static public final String CONTROL_LABELS = "controlLabels";
    /**
     * Property name of the list of common labels of a graph grammar.
     * These will be used to determine the order in which the NACs are checked.
     */
    static public final String COMMON_LABELS = "commonLabels";
    /** 
     * Property that determines if the graph grammar uses attributes.
     * @see #ATTRIBUTES_YES
     */
    static public final String ATTRIBUTE_SUPPORT = "attributeSupport";
    /**
     * Value of {@link #ATTRIBUTES_YES} that means attributes are used.
     */
    static public final String ATTRIBUTES_YES = "1";

	/** Constructs an initially empty rule system. */
    public RuleSystem() {
        // explicit empty constructor
    }

    /**
     * Constructs a clone of a given rule system.
     * @param other the rule system to be cloned
     * @require <tt>ruleSystem != null</tt>
     * @ensure <tt>equals(ruleSystem)</tt>
     */
    public RuleSystem(RuleSystem other) {
        nameRuleMap.putAll(other.nameRuleMap);
        // the target sets of the priority rule map must be copied, not aliased
        for (Map.Entry<Integer,Set<Rule>> priorityRuleEntry: other.priorityRuleMap.entrySet()) {
            Set<Rule> newRuleSet = createRuleSet();
            newRuleSet.addAll(priorityRuleEntry.getValue());
            priorityRuleMap.put(priorityRuleEntry.getKey(), newRuleSet);
        }
    }
    /**
     * Returns the production rule known under a given name, if any.
     * @param name the name of the requested production rule
     * @return the Rule known as "name"; null if name is not known
     * @ensure <tt>resul.equals(getRuleMap.get(name))</tt>
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
     * Returns an unmodifiable view upon the set of rules with a given priority.
     * @ensure <tt>result.equals(getPriorityRuleMap().get(new Integer(priority))))</tt>
     */
    public Set<Rule> getRules(int priority) {
        return Collections.unmodifiableSet(priorityRuleMap.get(new Integer(priority)));
    }

    /**
     * Returns an unmodifiable view upon the underlying collection of rules.
     * The result is ordered by descending priority, and within each priority,
     * by alphabetical order of the names.
     * Don't invoke {@link Object#equals} on the result!
     * @ensure <tt>result: Label -> Rule</tt>
     */
    public Collection<Rule> getRules() {
    	if (ruleSet == null) {
    		ruleSet = Arrays.asList(new CollectionOfCollections<Rule>(priorityRuleMap.values()).toArray(new Rule[0]));
    	}
    	return ruleSet;
    }

    /**
     * Returns <tt>true</tt> if the rule system has rules at more than one priority.
     */
    public boolean hasMultiplePriorities() {
        return priorityRuleMap.keySet().size() > 1;
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
     * Adds a production rule to this production system, taking the rule priority into account.
     * Removes the existing rule with the same name, if any, and returns it.
     * @param rule the production rule to be added
     * @require <tt>rule != null</tt>
     * @throws FormatException if the new rule does not comply with the grammar properties
     */
    public Rule add(Rule rule) throws FormatException {
    	if (! isConsistent(rule)) {
    		throw new FormatException(getInconsistency(rule));
    	}
        NameLabel ruleName = rule.getName();
        Integer priority = new Integer(rule.getPriority());
        Rule oldRuleForName = nameRuleMap.put(ruleName, rule);
        // now remove the old rule with this name, if any
        if (oldRuleForName != null) {
            Integer oldPriorityForName = new Integer(oldRuleForName.getPriority());
            Set<Rule> oldPriorityRuleSet = priorityRuleMap.get(oldPriorityForName);
            oldPriorityRuleSet.remove(oldRuleForName);
            // if this is the last rule with this priority, remove the entry from the map
            if (oldPriorityRuleSet.isEmpty()) {
                priorityRuleMap.remove(oldPriorityForName);
            }
        }
        // add the rule to the priority map
        Set<Rule> priorityRuleSet = priorityRuleMap.get(priority);
        // if there is not yet any rule with this priority, create a set
        if (priorityRuleSet == null) {
            priorityRuleMap.put(priority, priorityRuleSet = createRuleSet());
        }
        priorityRuleSet.add(rule);
        // add the rule to the map
        nameRuleMap.put(ruleName, rule);
        ruleSet = null;
        return oldRuleForName;
    }

    /**
     * Sets the properties of this graph grammar by copying a given property mapping.
     * Clears the current properties first.
     * @param properties the new properties mapping
     */
    public void setProperties(Properties properties) {
    	testRuleSystemEmpty();
        Properties currentProperties = getProperties();
        currentProperties.clear();
        currentProperties.putAll(properties);
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
    public Properties getProperties() {
        if (properties == null) {
            properties = createProperties();
        }
        return properties;
    }

    /** 
     * Indicates if the rule system is attributed, according to the
     * properties. 
     * @see #ATTRIBUTE_SUPPORT
     * @see #ATTRIBUTES_YES
     */
    public boolean isAttributed() {
    	String attributed = getProperty(ATTRIBUTE_SUPPORT);
    	return attributed != null && attributed.equals(ATTRIBUTES_YES);
    }

    /** 
     * Returns a list of control labels, according to the {@link #CONTROL_LABELS}
     * property of the rule system.
     * @see #CONTROL_LABELS
     */
    public List<String> getControlLabels() {
    	String controlLabels = getProperty(CONTROL_LABELS);
    	if (controlLabels == null) {
    		return Collections.emptyList();
    	} else {
    		return Arrays.asList(controlLabels.split("\\s"));
    	}
    }

    /** 
     * Returns a list of common labels, according to the {@link #COMMON_LABELS}
     * property of the rule system.
     * @see #COMMON_LABELS
     */
    public List<String> getCommonLabels() {
    	String commonLabels = getProperty(COMMON_LABELS);
    	if (commonLabels == null) {
    		return Collections.emptyList();
    	} else {
    		return Arrays.asList(commonLabels.split("\\s"));
    	}
    }
    
    /** 
     * Tests if a given rule is consistent with the properties of this rule system. 
     * If it is not consistent, then it cannot be added to the rule system.
     * The reason for the inconsistency can be retrieved using #getInconsistency(Rule)
     */
    public boolean isConsistent(Rule rule) {
    	return getInconsistency(rule) == null;
    }
    
    /**
     * Gives a description of the reason why a given rule is inconsistent with the
     * properties of the rule system, or <code>null</code> if it is not inconsistent.
     */
    public String getInconsistency(Rule rule) {
    	if (isAttributed()) {
    		if (((SPORule) rule).getIsolatedNodes().length > 0) {
    			return String.format("Isolated nodes in rule not allowed in attributed rule systems", rule.getName());
    		}
    	} else {
    		if (rule.isAttributed()) {
    			return String.format("Attributed rule %s not allowed in non-attributed rule system", rule.getName());
    		}
    	}
    	return null;
    }
    
    /**
     * Callback factory method to create an initially empty {@link Properties} object 
     * for this graph grammar.
     */
    protected Properties createProperties() {
        return new GrammarProperties();
    }
    
    /**
     * Callback factory method to create a rule name from a {@link String}.
     */
    protected NameLabel createRuleName(String name) {
        return new StructuredRuleName(name);
    }
    /** 
     * Factory method to create a set to contain rules.
     * This implementation returns a {@link TreeSet}.
     */
    protected Set<Rule> createRuleSet() {
        return new TreeSet<Rule>();
    }
    
    /**
     * Returns a comparator for priorities, encoded as {@link Integer} objects.
     * This implementation orders priorities from high to low.
     */
    protected Comparator<Integer> createPriorityComparator() {
        return new Comparator<Integer>() {
            public int compare(Integer o1, Integer o2) {
                return o2.intValue() - o1.intValue();
            }
            
        };
    }
    
    /** 
     * Tests if the rule system currently contains any rules, and
     * throws an exception if it does.
     * @throws IllegalStateException if {@link #getRules()} returns a non-empty set.
     */
    private void testRuleSystemEmpty() throws IllegalStateException {
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
    protected final Map<Integer,Set<Rule>> priorityRuleMap = new TreeMap<Integer,Set<Rule>>(createPriorityComparator());
    /**
     * Set of rules, collected separately for purposes of speedup.
     * @see #getRules()
     */
    private Collection<Rule> ruleSet;
    /**
     * The properties bundle of this grammar.
     */
    private Properties properties;    
    
    /** Properties specialisation that forbids setting properties after construction. */
    private class GrammarProperties extends Properties {
    	/** Constructs a properties object with some initial properties. */
    	GrammarProperties(Properties properties) {
    		putAll(properties);
    	}
    	
    	/** Constructs an empty properties object. */
    	GrammarProperties() {
    		// empty
    	}
    	
		@Override
		public synchronized Object setProperty(String key, String value) {
			testRuleSystemEmpty();
			return super.setProperty(key, value);
		}    	
    }
}
