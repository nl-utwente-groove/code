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

import groove.gui.Simulator;
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

	protected void processFinalState(BuchiTransition nextPropertyTransition) {
		Set<? extends ProductTransition> productTransitions = addProductTransition(null, nextPropertyTransition.getTargetLocation()); 
		assert (productTransitions.size() == 1) : "There should be at most one target state instead of " + productTransitions.size();
		getAtBuchiState().setColour(ModelChecking.black());
	}

	/* (non-Javadoc)
	 * @see groove.explore.strategy.DefaultBoundedModelCheckingStrategy#setNextStartState()
	 */
	protected void setNextStartState() {
		getBoundary().increase();
		ModelChecking.nextIteration();
		ModelChecking.toggle();
		this.atBuchiState = startBuchiState();
		searchStack().clear();
	}

	protected void colourState() {
		if (black(getAtBuchiState())) {
			getAtBuchiState().setColour(ModelChecking.black());
			blackStates++;
		} else {
			getAtBuchiState().setColour(ModelChecking.blue());
		}
	}

	/**
	 * Checks whether the given state is unexplored. This is determined based
	 * on the state-colour.
	 * @param newState the state to be checked
	 * @return <tt>true</tt> if the state-colour is neither of black, cyan, blue, or red, <tt>false</tt> otherwise
	 */
	public boolean unexplored(BuchiGraphState newState) {
		if (newState.colour() == ModelChecking.black()) {
			return false;
		}
		boolean result = newState.colour() != ModelChecking.black() &&
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
	 * @return <tt>true</tt> if the conditions for marking the state
	 * black are satisfied, <tt>false</tt> otherwise
	 */
	protected boolean black(BuchiGraphState state) {
		for (ProductTransition transition: state.outTransitions()) {
			if (transition.target().colour() != ModelChecking.black())
				return false;
		}
		return true;
	}

	public static int blackStates = 0;
}
