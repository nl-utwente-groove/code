/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
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
 * $Id$
 */

package nl.utwente.groove.explore.verify;

import java.util.ArrayList;
import java.util.List;

import nl.utwente.groove.explore.result.ResultCollector;
import nl.utwente.groove.verify.ModelChecking.Record;
import nl.utwente.groove.verify.ProductListener;
import nl.utwente.groove.verify.ProductState;
import nl.utwente.groove.verify.ProductStateSet;
import nl.utwente.groove.verify.ProductTransition;

/**
 * Acceptor that is notified on closing a Buchi graph-state in a
 * {@link ProductStateSet}. If the Buchi graph-state is accepting, a a
 * cycle detection depth-first search is started. If a counter-example is found,
 * the graph-states currently on the search-stack constitute the path
 * representing the counter-example.
 *
 * @author Harmen Kastenberg
 * @version $Revision$
 */
public class CycleAcceptor extends ResultCollector implements ProductListener {
    /** Creates an acceptor that is done at the first counterexample. */
    public CycleAcceptor() {
        // the goal and result count play no role: the counterexample is
        // added by the strategy, and done() tests for it
    }

    @Override
    public boolean done() {
        return !getResult().isEmpty();
    }

    /** Sets the strategy to which this acceptor listens,
     * and which it invokes for the nested search.
     */
    public void setStrategy(LTLStrategy strategy) {
        this.strategy = strategy;
        this.record = strategy.getRecord();
    }

    @Override
    public void closeUpdate(ProductStateSet gts, ProductState state) {
        if (state.getBuchiLocation().isAccepting()) {
            var trail = new ArrayList<ProductTransition>();
            if (redDFS(state, trail)) {
                // the current search stack, extended by the trail from state,
                // constitutes the counter-example
                this.strategy.addCounterExample(state, trail);
            }
        }
    }

    /**
     * Depth-first search for an accepting cycle, over the previously explored
     * (blue) states. If a state on the current search stack (cyan) is reached,
     * such a cycle exists; the chain of transitions leading to it is collected
     * in {@code trail}.
     * @param state the state to search from
     * @param trail chain of product transitions from the original accepting
     * state to {@code state}; extended by this call
     * @return {@code true} if an accepting cycle was found
     */
    private boolean redDFS(ProductState state, List<ProductTransition> trail) {
        for (ProductTransition nextTransition : state.outTransitions()) {
            // although the outgoing transition in the gts might cross the
            // boundary
            // we do not have to check for this since the target states
            // themselves
            // will not yet have outgoing transitions and will therefore never
            // yield an accepting cycle
            // moreover, those states are not yet explored and will therefore
            // not
            // yet be coloured. The below code will thus not yield any
            // interesting
            // results for such states
            ProductState target = nextTransition.target();
            if (target.colour() == this.record.cyan()) {
                trail.add(nextTransition);
                return true;
            } else if (target.colour() == this.record.blue()) {
                target.setColour(this.record.red());
                trail.add(nextTransition);
                if (redDFS(target, trail)) {
                    return true;
                }
                trail.remove(trail.size() - 1);
            }
        }
        return false;
    }

    @Override
    public String getMessage() {
        String result;
        String property = this.strategy.getProperty();
        if (getResult().isEmpty()) {
            result = "No counterexample found for " + property;
        } else {
            var lasso = getResult().getLasso();
            result = property + " is violated by " + (lasso == null
                ? getResult().getStates().toString()
                : lasso.toString());
        }
        return result;
    }

    private LTLStrategy strategy;
    private Record record;
}
