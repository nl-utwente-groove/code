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
 * $Id: BoundedNestedDFSStrategy.java,v 1.5 2008-03-05 08:39:49 kastenberg Exp $
 */
package groove.explore.strategy;

import groove.explore.result.CycleAcceptor;
import groove.explore.util.RandomChooserInSequence;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.ProductTransition;
import groove.verify.BuchiGraphState;
import groove.verify.BuchiTransition;
import groove.verify.ModelChecking;

import java.util.Iterator;
import java.util.Set;

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
 * Checking whether the system has been fully checked is done by the
 * method {@link BoundedModelCheckingStrategy#finished()}.
 * 
 * @author Harmen Kastenberg
 * @version $Revision: 1.5 $
 */
public class BoundedNestedDFSStrategy extends DefaultBoundedModelCheckingStrategy<GraphState> {

	/**
	 * The next step makes atomic the full exploration of a state.
	 */
	@Override
	public boolean next() {
		if (getAtBuchiState() == null) {
			if (finished()) {
				getProductGTS().removeListener(this.collector);
				return false;
			}
		}

		// put current state on the stack
		searchStack().push(getAtBuchiState());
		this.atState = this.getAtBuchiState().getGraphState();
		// colour state cyan as being on the search stack
		getAtBuchiState().setColour(ModelChecking.cyan());

		// fully explore the current state
		exploreState(this.atState);
		this.collector.reset();

		// now look in the GTS for the outgoing transitions of the
		// current state with the current Buchi location and add
		// the resulting combined transition to the product GTS

		Set<GraphTransition> outTransitions = getGTS().outEdgeSet(getAtState());
    	Set<String> applicableRules = filterRuleNames(outTransitions);

        for (BuchiTransition nextPropertyTransition: getAtBuchiLocation().outTransitions()) {
        	if (isEnabled(nextPropertyTransition, applicableRules)) {
        		boolean finalState = true;
        		for (GraphTransition nextTransition: getGTS().outEdgeSet(getAtBuchiState().getGraphState())) {
        			if (nextTransition.getEvent().getRule().isModifying()) {
        				finalState = false;
        				
//            			Set<? extends ProductTransition> productTransitions = getProductGenerator().addTransition(getAtBuchiState(), nextTransition, nextPropertyTransition.getTargetLocation());
            			Set<? extends ProductTransition> productTransitions = addProductTransition(nextTransition, nextPropertyTransition.getTargetLocation());
            			assert (productTransitions.size() == 1) : "There should be at most one target state instead of " + productTransitions.size();
            			if (counterExample(getAtBuchiState(), productTransitions.iterator().next().target())) {
            				// notify counter-example
            				for (BuchiGraphState state: searchStack()) {
            					getResult().add(state.getGraphState());
            				}
            				return true;
            			}
        			}
        		}
        		if (finalState) {
        			Set<? extends ProductTransition> productTransitions = addProductTransition(null, nextPropertyTransition.getTargetLocation()); 
//        			Set<? extends ProductTransition> productTransitions = getProductGenerator().addTransition(getAtBuchiState(), null, nextPropertyTransition.getTargetLocation());
        			assert (productTransitions.size() == 1) : "There should be at most one target state instead of " + productTransitions.size();
        		}
        	}
        	// if the transition of the property automaton is not enabled
        	// the states reached in the system automaton do not have to
        	// be explored further since all paths starting from here
        	// will never yield a counter-example
        }

		updateAtState();
		return true;
	}

	@Override
	protected void updateAtState() {
		Iterator<? extends GraphState> nextStateIter = getAtBuchiState().getNextStateIter();
		if (nextStateIter.hasNext()) {
//		if (this.collector.pickRandomNewState() != null) {
			// select the first new state that does not cross the boundary
			BuchiGraphState newState = null;
//			Iterator<? extends GraphState> nextStateIter = getAtBuchiState().getNextStateIter();
			while (nextStateIter.hasNext()) {
				newState =  (BuchiGraphState) nextStateIter.next();
//				newState =  (BuchiGraphState) getRandomOpenBuchiSuccessor(getAtBuchiState());
				// we only continue with freshly created states
				if (
						newState.colour() != ModelChecking.cyan() &&
						newState.colour() != ModelChecking.blue() &&
						newState.colour() != ModelChecking.red()) {
					assert (newState instanceof BuchiGraphState) : "Expected a Buchi graph-state instead of a " + newState.getClass();
					if (newState.getGraphState() instanceof GraphTransition) {
						GraphTransition transition = (GraphTransition) newState.getGraphState();
						if (!getBoundary().crossingBoundary(transition)) {
							this.atBuchiState = newState;
							return;
						} else {
							// at this graph to the boundary-graphs
							addBoundaryGraph(newState);
							newState = null;
						}
					} else {
						// if the reached state is the start state look 
						// for another successor
						newState = null;
					}
				} else {
					// if we have seen this new state before
					// we pick an other one
					newState = null;
				}
			}
			// this point is reached when all successor states
			// are across the boundary
			// we should continue with backtracking from the current state
		}
		BuchiGraphState s = null;

		// backtracking
		BuchiGraphState parent = null;
		do {
			// pop the current state from the search-stack
			searchStack().pop();
			// close the current state
			setClosed(getAtBuchiState());
			getAtBuchiState().setColour(ModelChecking.blue());

			// the parent is on top of the searchStack
			parent = peekSearchStack();
			if (parent != null) {
				this.atBuchiState = parent;
				s = (BuchiGraphState) getRandomOpenBuchiSuccessor(parent);
				// make sure that the next open successor is not yet explored
				if (s != null && s.colour() != ModelChecking.white()) {
					s = null;
				}
			}
		} while (parent != null && s == null); //) && !getProductGTS().isOpen(getAtBuchiState()));

		// identify the reason of exiting the loop
		if (parent == null) {
			// the start state is reached and does not have open successors
			this.atBuchiState = null;
			return;
		}
		if (s != null) { // the current state has an open successor (is not really backtracking, a sibling state is fully explored)
			this.atBuchiState = s; 
		} 
		// else, atState is open, so we continue exploring it
	}

    @Override
	public boolean finished() {
		if (!boundaryGraphs().isEmpty()) {
			getBoundary().increase();
			boundaryGraphs().clear();
			ModelChecking.toggle();
			this.atBuchiState = startBuchiState();
			searchStack().clear();
			return false;
		}
		return true;
	}

	@Override
	protected GraphState getRandomOpenBuchiSuccessor(BuchiGraphState state) {
		Iterator<? extends GraphState> sucIter = state.getNextStateIter();
		RandomChooserInSequence<GraphState>  chooser = new RandomChooserInSequence<GraphState>();
		while (sucIter.hasNext()) {
			GraphState s = sucIter.next();
			assert (s instanceof BuchiGraphState) : "Expected a Buchi graph-state instead of a " + s.getClass();
			BuchiGraphState buchiState = (BuchiGraphState) s;
			if (
					buchiState.colour() != ModelChecking.cyan() &&
					buchiState.colour() != ModelChecking.blue() &&
					buchiState.colour() != ModelChecking.red()) {
				chooser.show(s);
			}
		}
		return chooser.pickRandom();
	}
}
