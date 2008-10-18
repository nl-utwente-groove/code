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
 * $Id: NestedDFSStrategy.java,v 1.5 2008-03-04 14:45:37 kastenberg Exp $
 */
package groove.explore.strategy;

import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.ProductTransition;
import groove.verify.BuchiGraphState;
import groove.verify.BuchiTransition;
import groove.verify.ModelChecking;

import java.util.Set;

/**
 * This depth-first strategy represents the blue search of a nested depth-first
 * search for finding counter-examples for an LTL formula. On backtracking it
 * closes the explored states. Closing a state potentially starts a red search,
 * depending on whether the closed state is accepting or not.
 * 
 * @author Harmen Kastenberg
 * @version $Revision$
 */
public class NestedDFSStrategy extends AbstractModelCheckingStrategy {
    /**
     * The next step makes atomic the full exploration of a state.
     */
    public boolean next() {
        if (getAtBuchiState() == null) {
            getProductGTS().removeListener(this.collector);
            return false;
        }

        // put current state on the stack
        searchStack().push(getAtBuchiState());
        this.atState = getAtBuchiState().getGraphState();
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

        for (BuchiTransition nextPropertyTransition : getAtBuchiLocation().outTransitions()) {
            if (isEnabled(nextPropertyTransition, applicableRules)) {
                boolean finalState = true;
                for (GraphTransition nextTransition : getGTS().outEdgeSet(
                    getAtBuchiState().getGraphState())) {
                    if (nextTransition.getEvent().getRule().isModifying()) {
                        finalState = false;
                        Set<? extends ProductTransition> productTransitions =
                            addProductTransition(nextTransition,
                                nextPropertyTransition.getTargetLocation());
                        assert (productTransitions.size() <= 1) : "There should be at most one target state instead of "
                            + productTransitions.size();
                        if (counterExample(getAtBuchiState(),
                            productTransitions.iterator().next().target())) {
                            // notify counter-example
                            for (BuchiGraphState state : searchStack()) {
                                getResult().add(state.getGraphState());
                            }
                            return true;
                        }
                    }
                }
                if (finalState) {
                    processFinalState(nextPropertyTransition);
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
        if (this.collector.pickRandomNewState() != null) {
            GraphState newState = this.collector.pickRandomNewState();
            assert (newState instanceof BuchiGraphState) : "Expected a Buchi graph-state instead of a "
                + newState.getClass();
            this.atBuchiState = (BuchiGraphState) newState;
            return;
        } else {
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
                }
            } while (parent != null && s == null); // ) &&
                                                    // !getProductGTS().isOpen(getAtBuchiState()));

            // identify the reason of exiting the loop
            if (parent == null) {
                // the start state is reached and does not have open successors
                this.atBuchiState = null;
                return;
            }
            if (s != null) { // the current state has an open successor (is
                                // not really backtracking, a sibling state is
                                // fully explored)
                this.atBuchiState = s;
            }
            // else, atState is open, so we continue exploring it
        }

    }

    /**
     * @param transition
     */
    protected void processFinalState(BuchiTransition transition) {
        if (transition == null) {
            // exclude the current state from further analysis
            // mark it red
            getAtBuchiState().setColour(ModelChecking.RED);
        } else {
            Set<? extends ProductTransition> productTransitions =
                addProductTransition(null, transition.getTargetLocation());
            assert (productTransitions.size() == 1) : "There should be at most one target state instead of "
                + productTransitions.size();
        }
    }
}
