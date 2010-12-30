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
import groove.lts.GraphState;
import groove.lts.GTSAdapter;
import groove.lts.MatchResult;

/**
 * Explores a single path until reaching a final state or a loop. In case of
 * abstract simulation, this implementation will prefer going along a path then
 * stopping exploration when a loop is met.
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
        if (closeFast) {
            enableCloseExit();
        }
    }

    @Override
    public boolean next() {
        if (getAtState() == null) {
            return false;
        }
        MatchResult match = getMatch();
        if (match != null) {
            getMatchApplier().apply(getAtState(), match);
            if (closeExit()) {
                setClosed(getAtState(), false);
            }
        } else {
            setClosed(getAtState(), true);
        }
        return updateAtState();
    }

    /** Callback method to return the single next match. */
    protected MatchResult getMatch() {
        return createMatchCollector().getMatch();
    }

    @Override
    protected boolean updateAtState() {
        boolean result = (this.atState = this.collector.getNewState()) != null;
        this.collector.reset();
        if (!result) {
            getGTS().removeLTSListener(this.collector);
        }
        return result;
    }

    @Override
    public void prepare(GTS gts, GraphState state) {
        // We have to set the non-collapsing property before the first (start)
        // state is generated, otherwise it is too late.
        gts.getRecord().setCollapse(false);
        gts.getRecord().setCopyGraphs(false);
        gts.getRecord().setReuseEvents(false);
        super.prepare(gts, state);
        gts.addLTSListener(this.collector);
    }

    /** Return the current value of the "close on exit" setting */
    public boolean closeExit() {
        return this.closeExit;
    }

    /**
     * Enable closeExit, to close states immediately after a transition has been generated.
     * This can save memory when using linear strategies.
     */
    public void enableCloseExit() {
        this.closeExit = true;
    }

    /** Collects states newly added to the GTS. */
    private final NewStateCollector collector = new NewStateCollector();
    /** 
     * Option to close states immediately after a transition has been generated.
     * Used to save memory by closing states ASAP.
     */
    private boolean closeExit = false;

    /**
     * Registers the first new state added to the GTS it listens to. Such an
     * object should be added as listener only to a single GTS.
     */
    static private class NewStateCollector extends GTSAdapter {
        NewStateCollector() {
            reset();
        }

        /**
         * Returns the collected new state, or null if no new state was
         * registered.
         * @return the collected new state, or null if no new state was
         *         registered since last reset operation
         */
        GraphState getNewState() {
            return this.newState;
        }

        /** Forgets collected new state. */
        void reset() {
            this.newState = null;
        }

        @Override
        public void addUpdate(GTS shape, GraphState state) {
            if (this.newState == null) {
                this.newState = state;
            }
        }

        private GraphState newState;
    }

}
