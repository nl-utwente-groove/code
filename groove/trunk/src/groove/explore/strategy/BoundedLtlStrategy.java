/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id: DefaultBoundedModelCheckingStrategy.java,v 1.1 2008/03/04 14:44:25
 * kastenberg Exp $
 */
package groove.explore.strategy;

import groove.explore.util.RandomChooserInSequence;
import groove.graph.EdgeRole;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.RuleTransition;
import groove.verify.BuchiTransition;
import groove.verify.ModelChecking;
import groove.verify.ProductState;
import groove.verify.ProductTransition;

import java.util.Iterator;
import java.util.Set;

/**
 * This class provides some default implementations for a bounded model checking
 * strategy, such as setting the boundary and collecting the boundary graphs.
 * 
 * @author Harmen Kastenberg
 * @version $Revision$
 */
public class BoundedLtlStrategy extends LtlStrategy {
    /**
     * Sets the boundary specification used in the strategy.
     * @param boundary the boundary specification to use
     */
    public void setBoundary(Boundary boundary) {
        this.boundary = boundary;
    }

    /**
     * Returns the boundary specification used in the strategy.
     * @return the boundary specification
     */
    public Boundary getBoundary() {
        return this.boundary;
    }

    /**
     * The next step makes atomic the full exploration of a state.
     */
    @Override
    public boolean next() {
        if (getAtBuchiState() == null) {
            while (getAtBuchiState() == null
                && getProductGTS().hasOpenStates()
                && ModelChecking.CURRENT_ITERATION <= ModelChecking.MAX_ITERATIONS) {
                setNextStartState();
            }
            if (getAtBuchiState() == null) {
                getProductGTS().removeListener(this.collector);
                return false;
            }
        }

        // TODO: push the current state only on the stack when continuing with
        // one of its successors
        // this should therefore be done in the method updateAtState

        // put current state on the stack
        searchStack().push(getAtBuchiState());
        // push the last transition on the transition stack;
        if (getLastTransition() != null) {
            pushTransition(getLastTransition());
        }
        // colour state cyan as being on the search stack
        getAtBuchiState().setColour(ModelChecking.cyan());

        // fully explore the current state
        exploreState(getAtBuchiState().getGraphState());
        this.collector.reset();

        // now look in the GTS for the outgoing transitions of the
        // current state with the current Buchi location and add
        // the resulting combined transition to the product GTS

        // if the state is already explored...
        if (getAtBuchiState().isExplored()) {
            for (ProductTransition transition : getAtBuchiState().outTransitions()) {
                if (counterExample(getAtBuchiState(), transition.target())) {
                    constructCounterExample();
                    return true;
                }
            }
        }
        // else we have to do it now...
        else {
            Set<? extends GraphTransition> outTransitions =
                getGTS().outEdgeSet(getState());
            Set<String> applicableRules = filterRuleNames(outTransitions);

            for (BuchiTransition nextPropertyTransition : getAtBuchiLocation().outTransitions()) {
                if (nextPropertyTransition.isEnabled(applicableRules)) {
                    boolean finalState = true;
                    for (GraphTransition nextTransition : getGTS().outEdgeSet(
                        getAtBuchiState().getGraphState())) {
                        if (nextTransition.getRole() == EdgeRole.BINARY) {
                            finalState = false;

                            ProductTransition productTransition =
                                addProductTransition(nextTransition,
                                    nextPropertyTransition.target());
                            if (counterExample(getAtBuchiState(),
                                productTransition.target())) {
                                // notify counter-example
                                constructCounterExample();
                                return true;
                            }
                        }
                    }
                    if (finalState) {
                        // the product transition will leave the graph-state
                        // untouched
                        // since it is a final state
                        // the Buchi transition might nevertheless point to a
                        // different location
                        processFinalState(nextPropertyTransition);
                    }
                }
                // if the transition of the property automaton is not enabled
                // the states reached in the system automaton do not have to
                // be explored further since all paths starting from here
                // will never yield a counter-example
            }
            // this point will be reached for state that will never end
            // in an accepting cycle since its graph-component is a final
            // state and it has no outgoing transitions;
            // it must not be included in further analysis

            getAtBuchiState().setExplored();
        }

        return updateAtState();
    }

    /**
     * Sets the state from which to start the next iteration.
     */
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
        transitionStack().clear();
        this.lastTransition = null;
        // start with depth zero again
        getBoundary().setCurrentDepth(0);
    }

    @Override
    protected GraphState getNextState() {
        Iterator<ProductTransition> outTransitionIter =
            getAtBuchiState().outTransitions().iterator();
        if (outTransitionIter.hasNext()) {
            // select the first new state that does not cross the boundary
            ProductState newState = null;
            while (outTransitionIter.hasNext()) {
                ProductTransition outTransition = outTransitionIter.next();
                newState = outTransition.target();

                // we only continue with freshly created states
                if (unexplored(newState)) {
                    if (newState.getGraphState() instanceof RuleTransition) {
                        // if the transition does not cross the boundary or its
                        // target-state is already explored in previous
                        // iterations
                        // the transition must be traversed
                        if (!getBoundary().crossingBoundary(outTransition, true)) {
                            setAtBuchiState(newState);
                            setLastTransition(outTransition);
                            return newState.getGraphState();
                        } else {
                            processBoundaryCrossingTransition(outTransition);
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
        } else {
            // if this state has no outgoing transition
            // it is a final state and will therefore never lead to an
            // accepting cycle
        }

        // backtracking
        backtrack();
        return getAtBuchiState() == null ? null
                : getAtBuchiState().getGraphState();
    }

    /**
     * Backtrack to the next state to be explored.
     */
    protected void backtrack() {
        ProductState parent = null;
        ProductState s = null;
        do {
            // pop the current state from the search-stack
            searchStack().pop();
            // close the current state
            setClosed(getAtBuchiState());
            colourState();

            ProductTransition previousTransition = null;
            if (transitionStack().isEmpty()) {
                // the start state is reached and does not have open successors
                setAtBuchiState(null);
                return;
            } else {
                previousTransition = transitionStack().pop();
            }

            // backtrack the last transition
            getBoundary().backtrackTransition(previousTransition);

            // the parent is on top of the searchStack
            parent = peekSearchStack();
            if (parent != null) {
                setAtBuchiState(parent);
                ProductTransition openTransition =
                    getRandomOpenBuchiTransition(parent);
                // make sure that the next open successor is not yet explored
                if (openTransition != null) {
                    assert (unexplored(openTransition.target())) : "We only continue from unexplored states";
                    if (unexplored(openTransition.target())) {
                        // if this transition is a boundary-crossing transition,
                        // the current depth of the boundary should be updated
                        getBoundary().crossingBoundary(openTransition, true);
                        // set the next transition to take
                        this.lastTransition = openTransition;
                        // and the state reached by that transition
                        s = openTransition.target();
                    }
                }
            }
        } while (parent != null && s == null);

        // identify the reason of exiting the loop
        if (parent == null) {
            // the start state is reached and does not have open successors
            setAtBuchiState(null);
            return;
        } else if (s != null) { // the current state has an open successor (is
            // not really backtracking, a sibling state is
            // fully explored)
            setAtBuchiState(s);
            return;
        }
        // else, atState is open, so we continue exploring it
    }

    /**
     * Process boundary-crossing transitions properly.
     * @param transition the boundary-crossing transition
     */
    public ProductState processBoundaryCrossingTransition(
            ProductTransition transition) {
        // if the number of boundary-crossing transition on the current path
        if (getBoundary().currentDepth() < ModelChecking.CURRENT_ITERATION - 1) {
            return transition.target();
        } else {
            // set the iteration index of the graph properly
            transition.target().setIteration(
                ModelChecking.CURRENT_ITERATION + 1);
            // leave it unexplored
            return null;
        }
    }

    /**
     * Colour the current state properly. If we backtracked from an accepting
     * state then it must be coloured red, otherwise blue.
     */
    protected void colourState() {
        if (getAtBuchiState().getBuchiLocation().isAccepting()) {
            getAtBuchiState().setColour(ModelChecking.red());
        } else {
            getAtBuchiState().setColour(ModelChecking.blue());
        }
    }

    /**
     * Checks whether the given state is unexplored. This is determined based on
     * the state-colour.
     * @param newState the state to be checked
     * @return <tt>true</tt> if the state-colour is neither of black, cyan,
     *         blue, or red, <tt>false</tt> otherwise
     */
    public boolean unexplored(ProductState newState) {
        boolean result =
            newState.colour() != ModelChecking.cyan()
                && newState.colour() != ModelChecking.blue()
                && newState.colour() != ModelChecking.red();
        return result;
    }

    /**
     * Construct the counter-example as currently on the search-stack.
     */
    public void constructCounterExample() {
        for (ProductState state : searchStack()) {
            getResult().add(state.getGraphState());
        }
    }

    /**
     * Checks whether all states have been fully explored.
     * @return <tt>true</tt> if there are states left, <tt>false</tt>
     *         otherwise
     */
    public boolean finished() {
        if (ModelChecking.CURRENT_ITERATION > ModelChecking.MAX_ITERATIONS) {
            return true;
        } else {
            return !getProductGTS().hasOpenStates();
        }
    }

    /**
     * Returns a random buchi transition from a given state.
     */
    protected ProductTransition getRandomOpenBuchiTransition(ProductState state) {
        RandomChooserInSequence<ProductTransition> chooser =
            new RandomChooserInSequence<ProductTransition>();
        for (ProductTransition p : state.outTransitions()) {
            ProductState buchiState = p.target();
            if (unexplored(buchiState)) {
                if (!getBoundary().crossingBoundary(p, false)
                    || buchiState.isExplored()) {
                    chooser.show(p);
                } else {
                    buchiState.setIteration(ModelChecking.CURRENT_ITERATION + 1);
                }
            }
        }
        ProductTransition result = chooser.pickRandom();

        return result;
    }

    /**
     * The boundary to be used.
     */
    private Boundary boundary;
}