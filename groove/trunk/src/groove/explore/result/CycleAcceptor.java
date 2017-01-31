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
 * $Id$
 */

package groove.explore.result;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import groove.explore.ExploreResult;
import groove.explore.strategy.LTLStrategy;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.verify.ModelChecking.Outcome;
import groove.verify.ModelChecking.Record;
import groove.verify.ProductListener;
import groove.verify.ProductState;
import groove.verify.ProductStateSet;
import groove.verify.ProductTransition;

/**
 * Acceptor that is notified on closing a Buchi graph-state in a
 * {@link groove.verify.ProductStateSet}. If the Buchi graph-state is accepting, a a
 * cycle detection depth-first search is started. If a counter-example is found,
 * the graph-states currently on the search-stack constitute the path
 * representing the counter-example.
 *
 * @author Harmen Kastenberg
 * @version $Revision$
 */
public class CycleAcceptor extends Acceptor implements ProductListener {
    /** Creates a new acceptor with a 1-bounded {@link ExploreResult}. */
    private CycleAcceptor(boolean prototype) {
        super(prototype);
    }

    @Override
    public CycleAcceptor newAcceptor(int bound) {
        // the bound is disregarded
        return new CycleAcceptor(false);
    }

    @Override
    protected ExploreResult createResult(GTS gts) {
        return new CycleResult(gts);
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
        if (state.getBuchiLocation()
            .isAccepting()) {
            Outcome event = redDFS(state);
            if (event != Outcome.OK) {
                // put the counter-example in the result
                for (ProductState stackState : this.strategy.getStateStack()) {
                    getResult().addState(stackState.getGraphState());
                }
                getResult().addState(state.getGraphState());
            }
        }
    }

    private Outcome redDFS(ProductState state) {
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
                return Outcome.ERROR;
            } else if (target.colour() == this.record.blue()) {
                target.setColour(this.record.red());
                Outcome event = redDFS(target);
                if (event != Outcome.OK) {
                    return event;
                }
            }
        }
        return Outcome.OK;
    }

    @Override
    public String getMessage() {
        String result;
        String property = this.strategy.getProperty();
        if (getResult().isEmpty()) {
            result = "No counterexample found for " + property;
        } else {
            result = property + " is violated; counterexample: " + getResult().getStates();
        }
        return result;
    }

    private LTLStrategy strategy;
    private Record record;

    /** Prototype acceptor. */
    public static final CycleAcceptor PROTOTYPE = new CycleAcceptor(true);

    /**
     * Type of the result object for the {@link CycleAcceptor}.
     * The result is a list rather than a set, allowing for the multiple
     * occurrence of the same graph state in a counter-example.
     */
    static private class CycleResult extends ExploreResult {
        public CycleResult(GTS gts) {
            super(gts);
            this.transitions = new ArrayList<>();
        }

        @Override
        public void addState(GraphState state) {
            super.addState(state);
            if (this.lastState != null) {
                // look for the outgoing transition to state
                for (GraphTransition trans : this.lastState.getTransitions()) {
                    if (trans.target() == state) {
                        addTransition(trans);
                        break;
                    }
                }
            }
            this.lastState = state;
        }

        /** The previously added state. */
        private GraphState lastState;

        @Override
        protected Collection<GraphState> createResultSet() {
            return new ArrayList<>();
        }

        @Override
        public boolean storesTransitions() {
            return true;
        }

        /** Adds a transition to those stored in this result object. */
        private void addTransition(GraphTransition trans) {
            this.transitions.add(trans);
        }

        @Override
        public Collection<GraphTransition> getTransitions() {
            return this.transitions;
        }

        private List<GraphTransition> transitions;
    }
}
