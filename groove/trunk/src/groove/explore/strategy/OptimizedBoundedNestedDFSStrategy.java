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
 * $Id: OptimizedBoundedNestedDFSStrategy.java,v 1.2 2008/02/22 13:02:45 rensink Exp $
 */
package groove.explore.strategy;

import groove.explore.result.CycleAcceptor;
import groove.lts.GraphTransition;
import groove.verify.BuchiGraphState;
import groove.verify.ModelChecking;

import java.util.Iterator;

/** 
 * This depth-first strategy represents the blue search of a nested
 * depth-first search for finding counter-examples for an LTL
 * formula. On backtracking it closes the explored states. Closing
 * a state potentially starts a red search, depending on whether the
 * closed state is accepting or not. This is taken care of by the
 * accompanying {@link CycleAcceptor}.
 * 
 * This bounded version deviates from the usual Nested DFS in the
 * way of setting the next state to be explored. If a potential
 * next state crosses the boundary, an other next state is selected.
 * 
 * @author Harmen Kastenberg
 * @version $Revision: 1.2 $
 */
public class OptimizedBoundedNestedDFSStrategy extends BoundedNestedDFSStrategy {

	/* (non-Javadoc)
	 * @see groove.explore.strategy.DefaultBoundedModelCheckingStrategy#setNextStartState()
	 */
	protected void setNextStartState() {
		while (getProductGTS().hasOpenStates() && getAtBuchiState() == null) {
			// increase the boundary
			getBoundary().increase();
			ModelChecking.nextIteration();
			// iterator over the open states
			// TODO: maybe there is a more efficient way of 
			// iterating over the open states than to start
			// at the beginning every time one has been
			// processed
			Iterator<BuchiGraphState> openStateIter = getProductGTS().getOpenStateIter();
			while (openStateIter.hasNext() && getAtBuchiState() == null) {
				BuchiGraphState nextOpenState = openStateIter.next();
				// states that are part of later iterations
				// are not considered here
				if (nextOpenState.iteration() <= ModelChecking.CURRENT_ITERATION) {
					// furthermore, the transition by which the next open 
					// state is reached should also not cross the current boundary
					assert (nextOpenState.getGraphState() instanceof GraphTransition) : "Was expecting a graph-transition instead of a " + nextOpenState.getGraphState().getClass();
					if (!getBoundary().crossingBoundary((GraphTransition) nextOpenState.getGraphState())) {
						this.atBuchiState = nextOpenState;
						ModelChecking.nextColourScheme();
					}
				}
			}
		}
	}
}
