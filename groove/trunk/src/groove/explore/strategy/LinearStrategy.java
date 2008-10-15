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

import groove.explore.util.ExploreCache;
import groove.graph.GraphAdapter;
import groove.graph.GraphShape;
import groove.graph.Node;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.trans.RuleEvent;

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
     * @param closeFast if <code>true</code>, close states immediately after
     *        a single outgoing transition has been computed.
     */
    public LinearStrategy(boolean closeFast) {
        if (closeFast) {
            enableCloseExit();
        }
    }

    public boolean next() {
        if (this.atState == null) {
            getGTS().removeGraphListener(this.collector);
            return false;
        }
        ExploreCache cache = getCache(true, false);
        RuleEvent event = getMatch(cache);
        if (event != null) {
            getMatchApplier().addTransition(getAtState(), event,
                cache.getTarget(event.getRule()));
            if (closeExit()) {
                setClosed(getAtState());
            }
        } else {
            setClosed(getAtState());
        }
        updateAtState();
        return true;
    }

    /** Callback method to return the single next match. */
    protected RuleEvent getMatch(ExploreCache cache) {
        return createMatchCollector(cache).getMatch();
    }

    @Override
    protected void updateAtState() {
        this.atState = this.collector.getNewState();
        this.collector.reset();
    }

    @Override
    public void prepare(GTS gts, GraphState state) {
        super.prepare(gts, state);
        gts.getRecord().setCopyGraphs(false);
        gts.getRecord().setReuseEvents(false);
        gts.addGraphListener(this.collector);
    }

    /** Collects states newly added to the GTS. */
    private final NewStateCollector collector = new NewStateCollector();

    /**
     * Registers the first new state added to the GTS it listens to. Such an
     * object should be added as listener only to a single GTS.
     */
    static private class NewStateCollector extends GraphAdapter {
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
        public void addUpdate(GraphShape shape, Node node) {
            if (this.newState == null) {
                this.newState = (GraphState) node;
            }
        }

        private GraphState newState;
    }

}
