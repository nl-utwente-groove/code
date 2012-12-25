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
import groove.lts.GraphState;
import groove.lts.RuleTransition;
import groove.verify.ModelChecking;
import groove.verify.ProductState;
import groove.verify.ProductTransition;

import java.util.Iterator;

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

    @Override
    protected boolean exploreCurrentLocation(ProductState prodState) {
        boolean result = false;
        if (prodState.isExplored()) {
            // if the state is already explored...
            for (ProductTransition prodTrans : getAtProductState().outTransitions()) {
                result = findCounterExample(prodTrans.target());
                if (result) {
                    break;
                }
            }
        } else {
            // else we have to do it now...
            result = super.exploreCurrentLocation(prodState);
            if (!result) {
                prodState.setExplored();
            }
        }
        return result;
    }

    @Override
    protected ProductState getNextProductState() {
        ProductState result = getAtProductState();
        if (result == null && getProductGTS().hasOpenStates()
            && ModelChecking.getIteration() <= ModelChecking.MAX_ITERATIONS) {
            // from the initial state again
            result = getStartProductState();
            setAtProductState(result);
            // next iteration
            ModelChecking.nextIteration();
            ModelChecking.toggle();
            // clear the stacks
            searchStack().clear();
            transitionStack().clear();
            this.lastTransition = null;
            // increase the boundary
            getBoundary().increase();
            // start with depth zero again
            getBoundary().setCurrentDepth(0);
        }
        return result;
    }

    @Override
    protected void pushState(ProductState state) {
        super.pushState(state);
        if (getLastTransition() != null) {
            // push the last transition on the transition stack;
            pushTransition(getLastTransition());
        }
    }

    @Override
    protected GraphState computeNextState() {
        Iterator<ProductTransition> outTransitionIter =
            getAtProductState().outTransitions().iterator();
        if (outTransitionIter.hasNext()) {
            // select the first new state that does not cross the boundary
            ProductState newState = null;
            while (outTransitionIter.hasNext()) {
                ProductTransition outTransition = outTransitionIter.next();
                newState = outTransition.target();

                // we only continue with freshly created states
                if (isUnexplored(newState)) {
                    if (newState.getGraphState() instanceof RuleTransition) {
                        // if the transition does not cross the boundary or its
                        // target-state is already explored in previous
                        // iterations
                        // the transition must be traversed
                        if (!getBoundary().crossingBoundary(outTransition, true)) {
                            setAtProductState(newState);
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
        return getAtProductState() == null ? null
                : getAtProductState().getGraphState();
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
            setClosed(getAtProductState());
            colourState();

            ProductTransition previousTransition = null;
            if (transitionStack().isEmpty()) {
                // the start state is reached and does not have open successors
                setAtProductState(null);
                return;
            } else {
                previousTransition = transitionStack().pop();
            }

            // backtrack the last transition
            getBoundary().backtrackTransition(previousTransition);

            // the parent is on top of the searchStack
            parent = peekSearchStack();
            if (parent != null) {
                setAtProductState(parent);
                ProductTransition openTransition =
                    getRandomOpenBuchiTransition(parent);
                // make sure that the next open successor is not yet explored
                if (openTransition != null) {
                    assert (isUnexplored(openTransition.target())) : "We only continue from unexplored states";
                    if (isUnexplored(openTransition.target())) {
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
            setAtProductState(null);
            return;
        } else if (s != null) { // the current state has an open successor (is
            // not really backtracking, a sibling state is
            // fully explored)
            setAtProductState(s);
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
        if (getBoundary().currentDepth() < ModelChecking.getIteration() - 1) {
            return transition.target();
        } else {
            // set the iteration index of the graph properly
            transition.target().setIteration(ModelChecking.getIteration() + 1);
            // leave it unexplored
            return null;
        }
    }

    /**
     * Colour the current state properly. If we backtracked from an accepting
     * state then it must be coloured red, otherwise blue.
     */
    protected void colourState() {
        if (getAtProductState().getBuchiLocation().isAccepting()) {
            getAtProductState().setColour(ModelChecking.red());
        } else {
            getAtProductState().setColour(ModelChecking.blue());
        }
    }

    /**
     * Checks whether the given state is unexplored. This is determined based on
     * the state-colour.
     * @param newState the state to be checked
     * @return <tt>true</tt> if the state-colour is neither of black, cyan,
     *         blue, or red, <tt>false</tt> otherwise
     */
    protected boolean isUnexplored(ProductState newState) {
        boolean result =
            newState.colour() != ModelChecking.cyan()
                && newState.colour() != ModelChecking.blue()
                && newState.colour() != ModelChecking.red();
        return result;
    }

    /**
     * Checks whether all states have been fully explored.
     * @return <tt>true</tt> if there are states left, <tt>false</tt>
     *         otherwise
     */
    public boolean finished() {
        if (ModelChecking.getIteration() > ModelChecking.MAX_ITERATIONS) {
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
            if (isUnexplored(buchiState)) {
                if (!getBoundary().crossingBoundary(p, false)
                    || buchiState.isExplored()) {
                    chooser.show(p);
                } else {
                    buchiState.setIteration(ModelChecking.getIteration() + 1);
                }
            }
        }
        ProductTransition result = chooser.pickRandom();

        return result;
    }

    /**
     * Returns the value of <code>lastTransition</code>.
     * @return the value of <code>lastTransition</code>
     */
    public ProductTransition getLastTransition() {
        return this.lastTransition;
    }

    /**
     * Sets the last transition taken.
     * @param transition the value for <code>lastTransition</code>
     */
    public void setLastTransition(ProductTransition transition) {
        this.lastTransition = transition;
    }

    /** The transition by which the current Buchi graph-state is reached. */
    private ProductTransition lastTransition;
    /**
     * The boundary to be used.
     */
    private Boundary boundary;
}
