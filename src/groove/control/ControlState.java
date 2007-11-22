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
 * $Id: ControlState.java,v 1.7 2007-11-22 15:39:10 fladder Exp $
 */
package groove.control;

import groove.control.parse.Counter;
import groove.graph.Element;
import groove.graph.GenericNodeEdgeMap;
import groove.graph.Node;
import groove.lts.State;
import groove.trans.Rule;

import java.util.HashMap;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

public class ControlState implements Location {


	private int stateNumber;
	private boolean success = false;
	
	/** Store the Rules by priority **/
	protected final SortedMap<Integer,Set<Rule>> priorityRuleMap = new TreeMap<Integer, Set<Rule>>(Rule.PRIORITY_COMPARATOR);
	
	/** store all transitions in a set with the associated rule as key **/
	private HashMap<Rule, Set<ControlTransition>> ruleTransitionMap = new HashMap<Rule, Set<ControlTransition>>();
	
	/**
	 * The contructor needs a unique integer id which is used to compare to controlstates.
	 * @param stateNumber
	 */
	
	private ControlShape parent;
	
	// a node has only a single 
	public ControlState(ControlShape parent) {
		this.parent = parent;
		this.stateNumber = Counter.inc();
	}
	
	public boolean isClosed() {
		return true;
	}

	public int compareTo(Element obj) {
		return getStateNumber() - ((ControlState) obj).getStateNumber();
	}
//
//	@Deprecated
//	public Element imageFor(GenericNodeEdgeMap elementMap) {
//		throw new UnsupportedOperationException();
//	}

	public int getStateNumber()
	{
		return stateNumber;
	}
//	
//	@Deprecated
//	public State newState() {
//		throw new UnsupportedOperationException();
//	}
//	
//	@Deprecated
//	public Node newNode() {
//		throw new UnsupportedOperationException();
//	}
	
	public void add(ControlTransition transition) {
//		int priority = transition.getPriority();
//		
//		Rule rule = transition.rule();

		// store rule by priority
//		Set<Rule> priorityRuleSet = priorityRuleMap.get(priority);
//		if( priorityRuleSet == null ) {
//			priorityRuleMap.put(priority, priorityRuleSet = createRuleSet());
//		}
//		
//		priorityRuleSet.add(rule);
//
//		// store transition by rule
//		Set<ControlTransition> ruleTransitionSet = ruleTransitionMap.get(rule);
//		if( ruleTransitionSet == null ) {
//			ruleTransitionMap.put(rule, ruleTransitionSet = createTransitionSet());
//		}
//		ruleTransitionSet.add(transition);
	}

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
		return "ControlState(" + stateNumber + (isSuccess()?"S":"") + ")";
	}
	
	public Set<ControlTransition> getTransitions(Rule rule) {
		return ruleTransitionMap.get(rule);
	}

	public boolean isSuccess() {
		// TODO Auto-generated method stub
		return this.success;
	}

	public void setSuccess() {
		this.success = true;
	}
	
	public ControlShape getParent() {
		return this.parent;
	}
	
}
