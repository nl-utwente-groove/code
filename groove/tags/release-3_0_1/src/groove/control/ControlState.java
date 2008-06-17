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
 * $Id: ControlState.java,v 1.10 2008-01-30 12:37:39 fladder Exp $
 */
package groove.control;

import groove.control.parse.Counter;
import groove.graph.Element;
import groove.graph.Node;
import groove.trans.Rule;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/** * Represents a state in a control automaton. * Typically a member of a ControlShape. * Can be viewed (as member of a GraphShape) in a viewer (Node interface) * Supplies methods for getting allowed outgoing transitions (Location interface) (for exploration). * @author Tom Staijen * @version $Revision $ */public class ControlState implements Node {
	/** Contains the targets of outgoing lambda-transitions of this state **/	private HashSet<ControlState> lambdaTargets;
	/** hold the 'success' property of the state. **/ 	private boolean success = false;
	private HashMap<Rule, HashSet<ControlState>> ruleTargetMap = new HashMap<Rule, HashSet<ControlState>>();
	private Set<ElseControlTransition> elseTransitions = new HashSet<ElseControlTransition>();
	/**	 * Create a ControlState. A ControlState needs to know the	 * ControlShape it is in to be able to properly delete it.	 * @param parent	 */	public ControlState(ControlShape parent) {		this.parent = parent;		this.lambdaTargets = new HashSet<ControlState>();		this.stateNumber = Counter.inc();	}
	
	public int compareTo(Element obj) {		return this.hashCode() - ((ControlState) obj).hashCode();	}
	/**	 * Add an outgoing transition to this control state.	 * @param transition	 */	public void add(ControlTransition transition) {
		if( transition instanceof LambdaControlTransition ) {
			this.lambdaTargets.add(transition.target());
			return;
		}
		else if( transition instanceof ElseControlTransition ) {
			this.elseTransitions.add((ElseControlTransition)transition);
			return;
		}
		else if( transition instanceof RuleControlTransition ) {
			// TODO: store the transitions somehow..  not sure how's best.

			Rule rule = ((RuleControlTransition) transition).getRule();
			
			//store targets by rule
			HashSet<ControlState> targetSet = ruleTargetMap.get(rule);
			if( targetSet == null ) {
				ruleTargetMap.put(rule, targetSet = new HashSet<ControlState>());
			}
			targetSet.add(transition.target());
		}
		else {
			// should never be reached 
		}
	}
	@Override	public String toString()	{		return (isSuccess()?"S":"q") + stateNumber;	}	/**	 * Returns whether this controlstate is a succes-state	 * @return boolean	 */	public boolean isSuccess() {		return this.success;	}	/**	 *  Set this state to be a success state	 */	public void setSuccess() {		this.success = true;	}		/**	 * Returns the Shape this state is owned by.	 * @return ControlShape	 */	public ControlShape getParent() {		return this.parent;	}
	/**	 * Returns a StateSet with this state and all targets reacheable through LambdaRuleTransitions	 * @return stateset	 */	public HashSet<ControlState> lambdaTargets() {		return this.lambdaTargets;	}
	/**	 * Returns the ControlStates that can be reached when non of the other rules can be applied	 * and no lambda-transitions exist.	 * 	 * @return set	 */	public Set<ElseControlTransition> elseTransitions() {		return this.elseTransitions;	}
	/**	 * Returns the a set of target ControlState's for the given Rule.	 * @param rule	 * @return set	 */	public Set<ControlState> targets(Rule rule) {		return ruleTargetMap.get(rule);	}
	/**	 * Returns a set of rules are allowed to be applied.	 * @return set of rules	 */	public Set<Rule> rules() {		return ruleTargetMap.keySet();	}	private ControlShape parent;	private int stateNumber;
}
