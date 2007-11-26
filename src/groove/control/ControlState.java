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
 * $Id: ControlState.java,v 1.8 2007-11-26 08:58:11 fladder Exp $
 */
package groove.control;

import groove.control.parse.Counter;
import groove.graph.Element;
import groove.graph.Node;
import groove.trans.Rule;

import java.util.HashMap;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * 
 * Represents a state in a control automaton.
 * Typically a member of a ControlShape.
 * 
 * Can be viewed (as member of a GraphShape) in a viewer (Node interface)
 * 
 * Supplies methods for getting allowed outgoing transitions (Location interface) (for exploration).
 * 
 * @author Tom Staijen
 * @version $Revision $
 */
public class ControlState implements Node {

	/** 
	 * Holds this state as a stateset. The stateset also includes states reacheable with lambda-transitions,
	 * which are added when added as a transitions, but not stored as a transitions.
	 */
	private StateSet stateset;
	
	/** A unique number for viewing/debugging purposes **/
	private int stateNumber;
	
	/** hold the 'success' property of the state. **/ 
	private boolean success = false;
	
	/** store allowed outgoing Rules by priority **/
	// TODO: is this set ever used?
	protected final SortedMap<Integer,Set<Rule>> priorityRuleMap = new TreeMap<Integer, Set<Rule>>(Rule.PRIORITY_COMPARATOR);
	
	private HashMap<Rule, StateSet> ruleTargetMap = new HashMap<Rule, StateSet>();
	
	private ControlShape parent;
	
	/**
	 * Create a ControlState. A ControlState needs to know the
	 * ControlShape it is in to be able to properly delete it.
	 * @param parent
	 */
	public ControlState(ControlShape parent) {
		this.parent = parent;
		this.stateNumber = Counter.inc();
		
		this.stateset = new StateSet();
		this.stateset.add(this);
	}
	
	public int compareTo(Element obj) {
		return getStateNumber() - ((ControlState) obj).getStateNumber();
	}
	
	/**
	 * Returns the unique number of this state.
	 * @return int
	 */
	public int getStateNumber()
	{
		return stateNumber;
	}
	
	/**
	 * TODO: fix this method.
	 * @param transition
	 */
	public void add(ControlTransition transition) {
//		if( transition instanceof LambdaControlTransition ) {
//			this.stateset.add(transition.target());
//			return;
//		}
//		else 
		if( transition instanceof RuleControlTransition ) {
			// TODO: store the transitions somehow..  not sure how's best.

			Rule rule = ((RuleControlTransition) transition).getRule();

			System.out.println("Rule " + rule.getName().text() + " allowed from: " + this);

			
			int priority = rule.getPriority();
	
			// store rule by priority
			Set<Rule> priorityRuleSet = priorityRuleMap.get(priority);
			if( priorityRuleSet == null ) {
				priorityRuleMap.put(priority, priorityRuleSet = new TreeSet<Rule>());
			}
			priorityRuleSet.add(rule);
	
			//store targets by rule
			StateSet targetSet = ruleTargetMap.get(rule);
			if( targetSet == null ) {
				ruleTargetMap.put(rule, targetSet = new StateSet());
			}
			targetSet.add(transition.target());
		}
	}

	/**
	 * Returns the sets of rules sorted by priority
	 * @return SortedMap<Integer>, Set<Rule>>
	 */
    public SortedMap<Integer, Set<Rule>> getRuleMap()
    {
    	return priorityRuleMap;
    }

	@Override
	public String toString()
	{
		return "Q " + stateNumber + (isSuccess()?" ++":"");
	}
	
//	public Set<ControlTransition> getTransitions(Rule rule) {
//		return ruleTransitionMap.get(rule);
//	}

	public boolean isSuccess() {
		// TODO Auto-generated method stub
		return this.success;
	}

	/**
	 *  Set this state to be a success state
	 */
	public void setSuccess() {
		this.success = true;
	}
	
	/**
	 * Returns the Shape this state is owned by.
	 * @return ControlShape
	 */
	public ControlShape getParent() {
		return this.parent;
	}
	
	/**
	 * Returns a StateSet with this state and all targets reacheable through LambdaRuleTransitions
	 * @return stateset
	 */
	public StateSet asStateSet() {
		return this.stateset;
	}
	
	public StateSet getRuleTargets(Rule rule) {
		return ruleTargetMap.get(rule);
	}
	
}
