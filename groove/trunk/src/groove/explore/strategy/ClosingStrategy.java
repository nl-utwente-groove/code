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

import groove.lts.GTS;
import groove.lts.GTSAdapter;
import groove.lts.GraphState;
import groove.lts.GraphState.Flag;
import groove.lts.MatchResult;
import groove.lts.RuleTransition;

import java.util.List;

/**
 * Strategy that closes every state it explores, and adds the newly generated
 * states to a pool, together with information regarding the outgoing
 * transitions of its parent. Subclasses must decide on the order of the pool;
 * e.g., breadth-first or depth-first.
 */
abstract public class ClosingStrategy extends AbstractStrategy {
    @Override
    protected void next() {
        GraphState state = getState();
        List<MatchResult> matches = state.getMatches();
        if (!state.getSchedule().isFinished()) {
            // there are potential rule matches now blocked until 
            // the previous ones have been explored
            putInPool(state);
        }
        // explore known outgoing transitions of known states
        if (state.setFlag(Flag.KNOWN, false)) {
            for (RuleTransition out : state.getRuleTransitions()) {
                GraphState target = out.target();
                if (target.hasFlag(Flag.KNOWN)) {
                    putInPool(target);
                }
            }
        }
        for (MatchResult next : matches) {
            state.applyMatch(next);
        }
        updateState();
    }

    @Override
    protected void prepare() {
        super.prepare();
        // for the closing strategy, there is no problem in aliasing
        // the graph data structures. On the whole, this seems wise, to
        // avoid excessive garbage collection.
        // gts.getRecord().setCopyGraphs(true);
        getGTS().addLTSListener(this.exploreListener);
        clearPool();
    }

    @Override
    protected void finish() {
        super.finish();
        getGTS().removeLTSListener(this.exploreListener);
    }

    /** Callback method to add a pool element to the pool. */
    abstract protected void putInPool(GraphState state);

    /** Clears the pool, in order to prepare the strategy for reuse. */
    abstract protected void clearPool();

    /** Listener to keep track of states added to the GTS. */
    private final ExploreListener exploreListener = new ExploreListener();

    /** A queue with states to be explored, used as a FIFO. */
    private class ExploreListener extends GTSAdapter {
        @Override
        public void addUpdate(GTS gts, GraphState state) {
            putInPool(state);
        }
    }
}
