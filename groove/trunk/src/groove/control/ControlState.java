/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an 
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific 
 * language governing permissions and limitations under the License.
 *
 * $Id: ControlState.java,v 1.4 2007-09-16 21:44:31 rensink Exp $
 */
package groove.control;

import groove.graph.Element;
import groove.graph.Node;
import groove.graph.NodeEdgeMap;
import groove.lts.State;
import groove.trans.Rule;

import java.util.HashMap;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

public class ControlState implements State, Location {

	private int stateNumber;
	
	//private Collection<ControlTransition> lambdas = new HashSet<ControlTransition>();

	/** Store the Rules by priority **/
	protected final SortedMap<Integer,Set<Rule>> priorityRuleMap = new TreeMap<Integer, Set<Rule>>(Rule.PRIORITY_COMPARATOR);
	
	/** Store the ControlTransitions by priority (not sure if this is needed **/
	//protected final SortedMap<Integer,Set<ControlTransition>> priorityTransitionMap = new TreeMap<Integer,Set<ControlTransition>>(Rule.PRIORITY_COMPARATOR);

	/** store all transitions in a set with the associated rule as key **/
	private HashMap<Rule, Set<ControlTransition>> ruleTransitionMap = new HashMap<Rule, Set<ControlTransition>>();
	
	
	public ControlState(int stateNumber)
	{
		this.stateNumber = stateNumber;
	}
	
	public boolean isClosed() {
		return true;
	}

	public int compareTo(Element obj) {
		return getStateNumber() - ((ControlState) obj).getStateNumber();
	}

	@Deprecated
	public Element imageFor(NodeEdgeMap elementMap) {
		throw new UnsupportedOperationException();
	}

	public int getStateNumber()
	{
		return stateNumber;
	}
	
	@Deprecated
	public State newState() {
		throw new UnsupportedOperationException();
	}
	
	@Deprecated
	public Node newNode() {
		throw new UnsupportedOperationException();
	}
	
	public void add(ControlTransition transition) {
		int priority = transition.getPriority();
		
		Rule rule = transition.rule();
		
		// store transition by priority
		/*
		Set<ControlTransition> priorityTransitionSet = priorityTransitionMap.get(priority);
		if (priorityTransitionSet == null) {
			priorityTransitionMap.put(priority, priorityTransitionSet = createTransitionSet());
		}
		priorityTransitionSet.add(transition);
		*/

		// store rule by priority
		Set<Rule> priorityRuleSet = priorityRuleMap.get(priority);
		if( priorityRuleSet == null ) {
			priorityRuleMap.put(priority, priorityRuleSet = createRuleSet());
		}
		priorityRuleSet.add(rule);

		// store transition by rule
		Set<ControlTransition> ruleTransitionSet = ruleTransitionMap.get(rule);
		if( ruleTransitionSet == null ) {
			ruleTransitionMap.put(rule, ruleTransitionSet = createTransitionSet());
		}
		ruleTransitionSet.add(transition);
	}

	
    /**
     * Returns an unmodifiable view upon the underlying collection of transitions.
     * The result is ordered by descending priority, and within each priority,
     * by alphabetical order of the names.
     * Don't invoke {@link Object#equals} on the result!
     * @ensure <tt>result: Label -> Rule</tt>
     */
    /*
	public Collection<ControlTransition> getTransitions() {
    	// TODO: Needs testing
    	Collection<ControlTransition> result = null;
    	//if (result == null) {
    		result = Arrays.asList(new CollectionOfCollections<ControlTransition>(priorityTransitionMap.values()).toArray(new ControlTransition[0]));
    			//ruleSet = result;
    	//}
    	return result;
    }
    */
    
    public SortedMap<Integer, Set<Rule>> getRuleMap()
    {
    	return priorityRuleMap;
    }
    
	private Set<ControlTransition> createTransitionSet()
	{
		return new TreeSet<ControlTransition>();
	}
	
	private Set<Rule> createRuleSet()
	{
		return new TreeSet<Rule>();
	}
	
	@Override
	public String toString()
	{
		return "ControlState(" + stateNumber +")";
	}
	
	public Set<ControlTransition> getTransitions(Rule rule) {
		return ruleTransitionMap.get(rule);
	}
	
}
