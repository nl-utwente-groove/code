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

import static groove.trans.RuleEvent.Reuse.NONE;
import groove.lts.GTS;
import groove.lts.GTSAdapter;
import groove.lts.GraphState;
import groove.lts.GraphState.Flag;
import groove.lts.MatchResult;

import java.util.Stack;

/**
 * Explores a single path until reaching a final state or a loop.
 * @author Iovka Boneva
 * 
 */
public class LinearStrategy extends AbstractStrategy {
    /**
     * Constructs a default instance of the strategy, in which states are only
     * closed if they have been fully explored
     */
    public LinearStrategy() {
        this(false);
    }

    /**
     * Constructs an instance of the strategy with control over the closing of
     * states.
     * @param closeFast if <code>true</code>, close states immediately after a
     *        single outgoing transition has been computed.
     */
    public LinearStrategy(boolean closeFast) {
        this.closeFast = closeFast;
    }

    @Override
    public void next() {
        GraphState state = getState();
        MatchResult match = getMatch();
        // put the state back in the pool for backtracking of recipes
        if (!state.isClosed()) {
            putBackInPool();
        }
        if (match != null) {
            state.applyMatch(match);
        }
        updateState();
    }

    /** Callback method to return the single next match. */
    protected MatchResult getMatch() {
        return getState().getMatch();
    }

    @Override
    protected GraphState getNextState() {
        if (this.pool.isEmpty()) {
            return null;
        } else {
            return this.pool.pop();
        }
    }

    @Override
    protected void prepare() {
        // We have to set the non-collapsing property before the first (start)
        // state is generated, otherwise it is too late.
        getGTS().getRecord().setCollapse(false);
        getGTS().getRecord().setCopyGraphs(false);
        getGTS().getRecord().setReuseEvents(NONE);
        super.prepare();
        getGTS().addLTSListener(this.exploreListener);
    }

    @Override
    protected void finish() {
        getGTS().removeLTSListener(this.exploreListener);
    }

    @Override
    protected boolean isSuitableKnownState(GraphState state) {
        return state != getStartState();
    }

    /**
     * Pushes the currently explored state back onto the stack,
     * for backtracking recipes. 
     */
    private void putBackInPool() {
        this.pool.push(getState());
    }

    private void putFreshInPool(GraphState state) {
        // empty the pool if the new state is not transient
        // as then no more backtracking is going to be needed
        if (!state.isTransient()) {
            if (isCloseFast()) {
                for (GraphState s : this.pool) {
                    s.setClosed(false);
                }
            }
            this.pool.clear();
        }
        // only add non-transient states if they are unknown
        if (state.isTransient() || !state.hasFlag(Flag.KNOWN)) {
            this.pool.push(state);
        }
    }

    /** Return the current value of the "close on exit" setting */
    private boolean isCloseFast() {
        return this.closeFast;
    }

    /** 
     * Option to close states immediately after a transition has been generated.
     * Used to save memory by closing states ASAP.
     */
    private final boolean closeFast;

    private final Stack<GraphState> pool = new Stack<GraphState>();

    /** Listener to keep track of states added to the GTS. */
    private final ExploreListener exploreListener = new ExploreListener();

    /** A queue with states to be explored, used as a FIFO. */
    private class ExploreListener extends GTSAdapter {
        @Override
        public void addUpdate(GTS gts, GraphState state) {
            putFreshInPool(state);
        }
    }
}
