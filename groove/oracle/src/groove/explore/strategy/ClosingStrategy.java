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
package groove.explore.strategy;

import java.util.List;
import java.util.Stack;

import groove.explore.result.Acceptor;
import groove.lts.GTS;
import groove.lts.GTSListener;
import groove.lts.GraphState;
import groove.lts.MatchResult;
import groove.lts.RuleTransition;
import groove.lts.Status.Flag;

/**
 * Strategy that closes every state it explores, and adds the newly generated
 * states to a pool, together with information regarding the outgoing
 * transitions of its parent. Subclasses must decide on the order of the pool;
 * e.g., breadth-first or depth-first.
 */
abstract public class ClosingStrategy extends GTSStrategy {
    @Override
    public GraphState doNext() throws InterruptedException {
        GraphState state = getNextState();
        List<MatchResult> matches = state.getMatches();
        if (state.getActualFrame()
            .isTrial()) {
            //assert !state.isTransient();
            // there are potential rule matches now blocked until
            // the previous ones have been explored
            putInPool(state);
        }
        // explore known outgoing transitions of known states
        if (state.setFlag(Flag.KNOWN, false)) {
            for (RuleTransition out : state.getRuleTransitions()) {
                GraphState target = out.target();
                if (target.hasFlag(Flag.KNOWN)) {
                    addExplorable(target);
                }
            }
        }
        for (MatchResult next : matches) {
            state.applyMatch(next);
        }
        setNextState();
        return state;
    }

    @Override
    protected void prepare(GTS gts, GraphState state, Acceptor acceptor) {
        super.prepare(gts, state, acceptor);
        // for the closing strategy, there is no problem in aliasing
        // the graph data structures. On the whole, this seems wise, to
        // avoid excessive garbage collection.
        // gts.getRecord().setCopyGraphs(true);
        gts.addLTSListener(this.exploreListener);
        clearPool();
    }

    @Override
    public void finish() {
        super.finish();
        getGTS().removeLTSListener(this.exploreListener);
    }

    @Override
    protected GraphState computeNextState() {
        if (this.transientStack.isEmpty()) {
            return getFromPool();
        } else {
            return this.transientStack.pop();
        }
    }

    /** Adds a given state to the set of explorable states. */
    private void addExplorable(GraphState state) {
        if (state.isTransient()) {
            ClosingStrategy.this.transientStack.push(state);
        } else {
            putInPool(state);
        }
    }

    /** Callback method to retrieve the next element from the pool.
     * @return the next element, or {@code null} when the exploration is done.
     */
    abstract protected GraphState getFromPool();

    /** Callback method to add a non-transient graph state to the pool. */
    abstract protected void putInPool(GraphState state);

    /** Clears the pool, in order to prepare the strategy for reuse. */
    abstract protected void clearPool();

    /** Listener to keep track of states added to the GTS. */
    private final ExploreListener exploreListener = new ExploreListener();

    /** Local stack of transient states; these should be explored first. */
    private final Stack<GraphState> transientStack = new Stack<>();

    /** A queue with states to be explored, used as a FIFO. */
    private class ExploreListener implements GTSListener {
        @Override
        public void addUpdate(GTS gts, GraphState state) {
            addExplorable(state);
        }
    }
}
