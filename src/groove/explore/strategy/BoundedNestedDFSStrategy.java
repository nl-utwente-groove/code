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

import groove.explore.result.CycleAcceptor;
import groove.explore.util.RandomChooserInSequence;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.ProductTransition;
import groove.lts.StartGraphState;
import groove.util.LTLBenchmarker;
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
 * @version $Revision: 1.6 $
 */
public class BoundedNestedDFSStrategy extends DefaultBoundedModelCheckingStrategy<GraphState> {
	/**
	 * The next step makes atomic the full exploration of a state.
	 */
	public boolean next() {
		if (getAtBuchiState() == null) {
			// if we are finished
			if (finished()) {
//				System.out.println(stateVisits() + " state-visits.");
				getProductGTS().removeListener(this.collector);
				return false;
			} else {
				setNextStartState();
			}
		}

		// put current state on the stack
		searchStack().push(getAtBuchiState());
//		stateVisit();
		this.atState = this.getAtBuchiState().getGraphState();
		// colour state cyan as being on the search stack
		getAtBuchiState().setColour(ModelChecking.cyan());

		// fully explore the current state
		exploreState(this.atState);
		this.collector.reset();

		// now look in the GTS for the outgoing transitions of the
		// current state with the current Buchi location and add
		// the resulting combined transition to the product GTS

		// if the state is already explored...
		if (getAtBuchiState().isExplored()) {
			for (ProductTransition transition: getAtBuchiState().outTransitions()) {
				if (counterExample(getAtBuchiState(), transition.target())) {
					constructCounterExample();
					return true;
				}
			}
		}
		// else we have to do it now...
		else {
			Set<GraphTransition> outTransitions = getGTS().outEdgeSet(getAtState());
			Set<String> applicableRules = filterRuleNames(outTransitions);

//			if (applicableRules.contains("destroy")) {
//				System.out.println("destroy rule is applicable");
//			}
			for (BuchiTransition nextPropertyTransition: getAtBuchiLocation().outTransitions()) {
				if (isEnabled(nextPropertyTransition, applicableRules)) {
					boolean finalState = true;
					for (GraphTransition nextTransition: getGTS().outEdgeSet(getAtBuchiState().getGraphState())) {
						if (nextTransition.getEvent().getRule().isModifying()) {
							finalState = false;

							Set<? extends ProductTransition> productTransitions = addProductTransition(nextTransition, nextPropertyTransition.getTargetLocation());
							assert (productTransitions.size() == 1) : "There should be at most one target state instead of " + productTransitions.size();
							if (counterExample(getAtBuchiState(), productTransitions.iterator().next().target())) {
								// notify counter-example
								constructCounterExample();
								return true;
							}
						}
					}
					if (finalState) {
						if (counterExample(getAtBuchiState(), getAtBuchiState())) {
							constructCounterExample();
							return true;
						} else {
							processFinalState(nextPropertyTransition);
						}
					}
				}
				// if the transition of the property automaton is not enabled
				// the states reached in the system automaton do not have to
				// be explored further since all paths starting from here
				// will never yield a counter-example
			}
			getAtBuchiState().setExplored();
		}

		updateAtState();
		return true;
	}

	/**
	 * @param nextPropertyTransition
	 */
	protected void processFinalState(BuchiTransition nextPropertyTransition) {
		Set<? extends ProductTransition> productTransitions = addProductTransition(null, nextPropertyTransition.getTargetLocation()); 
		assert (productTransitions.size() == 1) : "There should be at most one target state instead of " + productTransitions.size();
	}

	protected void setNextStartState() {
		// increase the boundary
		getBoundary().increase();
		// next iteration
		ModelChecking.nextIteration();
		ModelChecking.toggle();
		// from the initial state again
		this.atBuchiState = startBuchiState();
		// clear the search-stack
		searchStack().clear();
	}

	@Override
	protected void updateAtState() {
		Iterator<? extends GraphState> nextStateIter = getAtBuchiState().getNextStateIter();
		if (nextStateIter.hasNext()) {
			// select the first new state that does not cross the boundary
			BuchiGraphState newState = null;
			while (nextStateIter.hasNext()) {
				newState =  (BuchiGraphState) nextStateIter.next();
//				if (newState.getGraphState() instanceof StartGraphState) {
//					System.out.println("Returned at start-state");
//				}
				// we only continue with freshly created states
//				if (((GraphTransition)newState.getGraphState()).getEvent().getRule().getName().name().equals("del-process")) {
//					System.out.println("Deleting a process");
//				}
				if (unexplored(newState)) {
					if (newState.getGraphState() instanceof GraphTransition) {
						GraphTransition transition = (GraphTransition) newState.getGraphState();
						// if the transition does not cross the boundary or its
						// target-state is already explored, the transition must be traversed
						if (!getBoundary().crossingBoundary(transition)) { // || getProductGTS().containsElement(newState)) {
							this.atBuchiState = newState;
							return;
						} else {
							// leave it unexplored
							// set the iteration index of the graph properly
							newState.setIteration(ModelChecking.CURRENT_ITERATION + 1);
							// at this graph to the boundary-graphs
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
			colourState();

			// the parent is on top of the searchStack
			parent = peekSearchStack();
			if (parent != null) {
				this.atBuchiState = parent;
				s = (BuchiGraphState) getRandomOpenBuchiSuccessor(parent);
				// make sure that the next open successor is not yet explored
				if (s != null && !unexplored(s)) {
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

	protected void colourState() {
		getAtBuchiState().setColour(ModelChecking.blue());
	}

	/**
	 * Checks whether the given state is unexplored. This is determined based
	 * on the state-colour.
	 * @param newState the state to be checked
	 * @return <tt>true</tt> if the state-colour is neither of black, cyan, blue, or red, <tt>false</tt> otherwise
	 */
	public boolean unexplored(BuchiGraphState newState) {
		boolean result = newState.colour() != ModelChecking.cyan() &&
							newState.colour() != ModelChecking.blue() &&
							newState.colour() != ModelChecking.red();

//		if (result && newState.colour() != ModelChecking.NO_COLOUR) {
//			System.out.println("Reexploring a previous state");
//		}
		return result;
	}

	public void constructCounterExample() {
		for (BuchiGraphState state: searchStack()) {
			getResult().add(state.getGraphState());
		}
//		getResult().add(getAtState());
	}

	/* (non-Javadoc)
	 * @see groove.explore.strategy.DefaultBoundedModelCheckingStrategy#finished()
	 */
	@Override
	public boolean finished() {
		if (LTLBenchmarker.RESTART) {
			return true;
		} else {
			return !getProductGTS().hasOpenStates();
		}
	}

	/* (non-Javadoc)
	 * @see groove.explore.strategy.DefaultModelCheckingStrategy#getRandomOpenBuchiSuccessor(groove.verify.BuchiGraphState)
	 */
	protected GraphState getRandomOpenBuchiSuccessorOld(BuchiGraphState state) {
		Iterator<? extends GraphState> sucIter = state.getNextStateIter();
		RandomChooserInSequence<GraphState>  chooser = new RandomChooserInSequence<GraphState>();
		while (sucIter.hasNext()) {
			GraphState s = sucIter.next();
			assert (s instanceof BuchiGraphState) : "Expected a Buchi graph-state instead of a " + s.getClass();
			BuchiGraphState buchiState = (BuchiGraphState) s;
			if (unexplored(buchiState)) {
				if (buchiState.getGraphState() instanceof GraphTransition) {
					GraphTransition transition = (GraphTransition) buchiState.getGraphState();
					if (!getBoundary().crossingBoundary(transition)) {
						chooser.show(s);
					}
				}
//				chooser.show(s);
			}
		}
		return chooser.pickRandom();
	}

	/* (non-Javadoc)
	 * @see groove.explore.strategy.DefaultModelCheckingStrategy#getRandomOpenBuchiSuccessor(groove.verify.BuchiGraphState)
	 */
	@Override
	protected GraphState getRandomOpenBuchiSuccessor(BuchiGraphState state) {
		Iterator<ProductTransition> outTransitionIter = state.outTransitionIter();
		RandomChooserInSequence<GraphState>  chooser = new RandomChooserInSequence<GraphState>();
		while (outTransitionIter.hasNext()) {
			ProductTransition p = outTransitionIter.next();
			BuchiGraphState buchiState = (BuchiGraphState) p.target();
			if (unexplored(buchiState)) {
				if (!getBoundary().crossingBoundary(p.graphTransition())) {
					chooser.show(p.target());
				}
			}
		}
		return chooser.pickRandom();
	}
}
