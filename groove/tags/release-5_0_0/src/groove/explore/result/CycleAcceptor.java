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
 * $Id: CycleAcceptor.java,v 1.4 2008-03-04 14:48:17 kastenberg Exp $
 */

package groove.explore.result;

import groove.explore.strategy.LTLStrategy;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphState.Flag;
import groove.verify.ModelChecking.Outcome;
import groove.verify.ModelChecking.Record;
import groove.verify.ProductListener;
import groove.verify.ProductState;
import groove.verify.ProductStateSet;
import groove.verify.ProductTransition;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Acceptor that is notified on closing a Buchi graph-state in a
 * {@link groove.verify.ProductStateSet}. If the Buchi graph-state is accepting, a a
 * cycle detection depth-first search is started. If a counter-example is found,
 * the graph-states currently on the search-stack constitute the path
 * representing the counter-example.
 * 
 * @author Harmen Kastenberg
 * @version $Revision: 5191 $
 */
public class CycleAcceptor extends Acceptor implements ProductListener {
    /** Creates a new acceptor with a 1-bounded {@link Result}. */
    public CycleAcceptor() {
        super(new CycleResult());
    }

    /** Sets the strategy to which this acceptor listens,
     * and which it invokes for the nested search.
     */
    public void setStrategy(LTLStrategy strategy) {
        this.strategy = strategy;
        this.record = strategy.getRecord();
    }

    @Override
    public void setResult(Result result) {
        // resist attempts to change the result: do nothing
    }

    @Override
    public void statusUpdate(GTS gts, GraphState state, Flag flag) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addUpdate(ProductStateSet gts, ProductState state) {
        // does nothing
    }

    @Override
    public void closeUpdate(ProductStateSet gts, ProductState state) {
        if (state.getBuchiLocation().isAccepting()) {
            Outcome event = redDFS(state);
            if (event != Outcome.OK) {
                // put the counter-example in the result
                for (ProductState stackState : this.strategy.getStateStack()) {
                    getResult().add(stackState.getGraphState());
                }
                getResult().add(state.getGraphState());
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
        Collection<GraphState> states = getResult().getValue();
        String property = this.strategy.getProperty();
        if (states.size() == 0) {
            result = "No counterexample found for " + property;
        } else {
            result = property + " is violated; counterexample: " + states;
        }
        return result;
    }

    private LTLStrategy strategy;
    private Record record;

    /** 
     * Type of the result object for the {@link CycleAcceptor}.
     * The result is a list rather than a set, allowing for the multiple
     * occurrence of the same graph state in a counter-example.
     */
    static private class CycleResult extends Result {
        public CycleResult() {
            super(1);
        }

        @Override
        protected Collection<GraphState> createResultSet() {
            return new ArrayList<GraphState>();
        }

        @Override
        public Result newInstance() {
            return new CycleResult();
        }
    }
}
