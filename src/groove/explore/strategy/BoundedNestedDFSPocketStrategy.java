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
 * $Id: BoundedNestedDFSStrategy.java,v 1.6 2008/03/05 11:01:56 rensink Exp $
 */
package groove.explore.strategy;

import groove.lts.ProductTransition;
import groove.verify.BuchiGraphState;
import groove.verify.BuchiTransition;
import groove.verify.ModelChecking;

import java.util.Set;

/** 
 * This bounded version deviates from the default nested DFS in
 * the way it deals with so-called pocket states. This strategy
 * black-paints the pocket states such that they will not be
 * considered in any further iteration.
 * 
 * @author Harmen Kastenberg
 * @version $Revision: 1.6 $
 */
public class BoundedNestedDFSPocketStrategy extends BoundedNestedDFSStrategy {

//	protected void backtrack() {
//		BuchiGraphState parent = null;
//		BuchiGraphState s = null;
//		do {
//			// pop the current state from the search-stack
//			searchStack().pop();
//			// close the current state
//			setClosed(getAtBuchiState());
//			
//			colourState();
//
//			// the parent is on top of the searchStack
//			parent = peekSearchStack();
//			if (parent != null) {
//				this.atBuchiState = parent;
//				s = (BuchiGraphState) getRandomOpenBuchiSuccessor(parent);
//				// make sure that the next open successor is not yet explored
//				if (s != null) {
//					if (!unexplored(s)) {
//						s = null;
//					}
//				}
//			}
//		} while (parent != null && s == null); //) && !getProductGTS().isOpen(getAtBuchiState()));
//
//		// identify the reason of exiting the loop
//		if (parent == null) {
//			// the start state is reached and does not have open successors
//			this.atBuchiState = null;
//			return;
//		} else if (s != null) { // the current state has an open successor (is not really backtracking, a sibling state is fully explored)
//			this.atBuchiState = s; 
//			return;
//		} 
//		// else, atState is open, so we continue exploring it
//	}

	protected void processFinalState(BuchiTransition transition) {
		Set<? extends ProductTransition> productTransitions = addProductTransition(null, transition.getTargetLocation()); 
		assert (productTransitions.size() == 1) : "There should be at most one target state instead of " + productTransitions.size();
		// we should set the state to pocket but that is
		// the case by default
	}

	protected void colourState() {
		checkPocket(getAtBuchiState());
		// if this state is a pocket-state we actually do not
		// not to further colour it blue or red
		// nevertheless, for correctness reasons we still do it
		// (in case the pocket detection is faulty, the colouring
		// is at least correct)
		if (getAtBuchiState().isAccepting()) {
			getAtBuchiState().setColour(ModelChecking.red());
		} else {
			getAtBuchiState().setColour(ModelChecking.blue());
		}
	}

	/**
	 * Checks whether the given state is unexplored. This is determined based
	 * on the state-colour.
	 * @param newState the state to be checked
	 * @return <tt>true</tt> if the state is a non-pocket state or colour neither cyan, blue, nor red, <tt>false</tt> otherwise
	 */
	public boolean unexplored(BuchiGraphState newState) {
		boolean result = (!newState.isPocket() || newState.colour() == ModelChecking.NO_COLOUR) &&
							newState.colour() != ModelChecking.cyan() &&
							newState.colour() != ModelChecking.blue() &&
							newState.colour() != ModelChecking.red();
		return result;
	}

	/**
	 * Determines whether a given state can be marked black. This is the 
	 * case when either the state has no outgoing transitions, or when all
	 * its successor-states are marked black.
	 * @param state the state to be marked black potentially
	 */
	protected void checkPocket(BuchiGraphState state) {
		for (ProductTransition transition: state.outTransitions()) {
			if (transition.graphTransition() != null && !transition.target().isPocket())
				return;
		}
		state.setPocket();
		return;
	}

//	public static int pocketStates = 0;
}
