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
import groove.lts.GraphTransition;
import groove.lts.RuleTransition;
import groove.verify.ProductState;
import groove.verify.ProductTransition;

/**
 * This class provides some default implementations for a bounded model checking
 * strategy, such as setting the boundary and collecting the boundary graphs.
 * 
 * @author Harmen Kastenberg
 * @version $Revision$
 */
public class BoundedLTLStrategy extends LTLStrategy {
    /**
     * Sets the boundary specification used in the strategy.
     * @param boundary the boundary specification to use
     */
    public void setBoundary(Boundary boundary) {
        this.boundary = boundary.instantiate(getRecord());
    }

    /**
     * Returns the boundary specification used in the strategy.
     * @return the boundary specification
     */
    public Boundary getBoundary() {
        return this.boundary;
    }

    @Override
    protected boolean exploreState(ProductState prodState) {
        boolean result = false;
        if (prodState.isExplored()) {
            // if the state is already explored...
            for (ProductTransition prodTrans : prodState.outTransitions()) {
                result = findCounterExample(prodState, prodTrans.target());
                if (result) {
                    break;
                }
            }
        } else {
            // else we have to do it now...
            result = super.exploreState(prodState);
            if (!result) {
                prodState.setExplored();
            }
        }
        return result;
    }

    @Override
    protected ProductState computeNextState() {
        ProductState result = super.computeNextState();
        if (result == null && getStateSet().hasOpenStates()) {
            // from the initial state again
            result = getStartState();
            // next iteration
            getRecord().increase();
            // increase the boundary
            getBoundary().increase();
            // start with depth zero again
            getBoundary().setCurrentDepth(0);
        }
        return result;
    }

    @Override
    protected ProductState getFreshState() {
        ProductState result = null;
        for (ProductTransition outTransition : getNextState().outTransitions()) {
            ProductState target = outTransition.target();
            // we only continue with freshly created states
            if (isUnexplored(target) && target.getGraphState() instanceof RuleTransition) {
                // if the transition does not cross the boundary or its
                // target-state is already explored in previous
                // iterations
                // the transition must be traversed
                if (!getBoundary().crossingBoundary(outTransition, true)) {
                    result = target;
                    break;
                } else {
                    processBoundaryCrossingTransition(outTransition);
                }
            }
        }
        return result;
    }

    /**
     * Process boundary-crossing transitions properly.
     * @param transition the boundary-crossing transition
     */
    private ProductState processBoundaryCrossingTransition(ProductTransition transition) {
        // if the number of boundary-crossing transition on the current path
        if (getBoundary().currentDepth() < getRecord().getIteration() - 1) {
            return transition.target();
        } else {
            // set the iteration index of the graph properly
            transition.target().setIteration(getRecord().getIteration() + 1);
            // leave it unexplored
            return null;
        }
    }

    @Override
    protected ProductState rollbackState() {
        ProductState previous = getStateStack().peek();
        GraphTransition origin = previous.getOrigin();
        if (origin != null) {
            getBoundary().backtrackTransition(origin);
        }
        return super.rollbackState();
    }

    /**
     * If we backtracked from an accepting
     * state then the Buchi location must be coloured red, otherwise blue.
     */
    @Override
    protected void colourState(ProductState state) {
        if (state.getBuchiLocation().isAccepting()) {
            state.setColour(getRecord().red());
        } else {
            state.setColour(getRecord().blue());
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
            newState.colour() != getRecord().cyan() && newState.colour() != getRecord().blue()
                && newState.colour() != getRecord().red();
        return result;
    }

    @Override
    protected ProductState getNextSuccessor(ProductState state) {
        ProductState result = null;
        // pick a transition to an unexplored state
        RandomChooserInSequence<ProductTransition> chooser =
            new RandomChooserInSequence<>();
        for (ProductTransition p : state.outTransitions()) {
            ProductState buchiState = p.target();
            if (isUnexplored(buchiState)) {
                if (!getBoundary().crossingBoundary(p, false) || buchiState.isExplored()) {
                    chooser.show(p);
                } else {
                    buchiState.setIteration(getRecord().getIteration() + 1);
                }
            }
        }
        ProductTransition resultTransition = chooser.pickRandom();
        if (resultTransition != null) {
            // if this transition is a boundary-crossing transition,
            // the current depth of the boundary should be updated
            getBoundary().crossingBoundary(resultTransition, true);
            // and the state reached by that transition
            result = resultTransition.target();
        }
        return result;
    }

    /**
     * The boundary to be used.
     */
    private Boundary boundary;
}
