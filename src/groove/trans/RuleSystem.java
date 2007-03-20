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
 * $Id: RuleSystem.java,v 1.1.1.1 2007-03-20 10:05:20 kastenberg Exp $
 */
package groove.trans;

import groove.util.CollectionOfCollections;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
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
 * @version $Revision: 1.1.1.1 $ $Date: 2007-03-20 10:05:20 $
 * @see NameLabel
 * @see SPORule
 */
public class RuleSystem {
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
     */
    public Rule add(Rule rule) {
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
     * Adds the rules of a given rule system.
     * @param ruleSystem the added rule system; may not be <tt>null</tt>.
     */
    public void addRuleSystem(RuleSystem ruleSystem) {
    	for (NameLabel name: ruleSystem.getRuleNames()) {
            add(ruleSystem.getRule(name));
        }
    }
//    
//    /**
//     * Indicates if one rule enables another, according to the currently calculated
//     * dependencies.
//     * @param enabler the (potential) enabler rule)
//     * @param dependent the (potentially) enabled rule
//     * @return <code>true</code> if <code>enabler</code> indeed enables <code>dependent</code>
//     */
//    protected boolean enables(Rule enabler, Rule dependent) {
//        if (dependencies == null) {
//            initDependencies();
//        }
//        return dependencies.getEnablers(dependent).contains(enabler);
//    }
//    
//    /**
//     * Indicates if one rule disables another, according to the currently calculated
//     * dependencies.
//     * @param disabler the (potential) disabler rule)
//     * @param dependent the (potentially) disabled rule
//     * @return <code>true</code> if <code>disabler</code> indeed disables <code>dependent</code>
//     */
//    protected boolean disables(Rule disabler, Rule dependent) {
//        if (dependencies == null) {
//            initDependencies();
//        }
//        return dependencies.getDisablers(dependent).contains(disabler);
//    }
//    
//    /**
//     * Initializes the rule dependencies.
//     */
//    protected void initDependencies() {
//        dependencies = new RuleDependencies(this.getRules());
//    }
    
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
}
