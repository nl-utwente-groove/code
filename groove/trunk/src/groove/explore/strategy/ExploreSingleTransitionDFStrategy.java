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
import groove.explore.util.MatchesIterator;
import groove.explore.util.RandomNewStateChooser;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.trans.RuleEvent;

/**
 * This depth-first search algorithm systematically generates a unique outgoing
 * transition and continues with it. The difference with {@link LinearStrategy}
 * is that the strategy does not stop until a full exploration. The difference
 * with {@link DFSStrategy} is that this strategy applies at most one matching
 * yielding a new state at each step.
 * 
 * This strategy is quite space efficient in the sense that it does not add
 * states to the GTS when they are not on the explored path. This is at the cost
 * of performing several matches (time unefficient).
 * @author Iovka Boneva
 * 
 */
public class ExploreSingleTransitionDFStrategy extends
        AbstractBacktrackingStrategy {

    @Override
    /**
     * The next step makes atomic the full exploration of a state.
     */
    public boolean next() {
        if (getAtState() == null) {
            getGTS().removeGraphListener(this.collector);
            return false;
        }
        ExploreCache cache = getCache(true, false);
        MatchesIterator matchIter = getMatchesIterator(cache);

        // Add transitions until a transition yielding a new state is added
        this.collector.reset();
        while (matchIter.hasNext() && this.collector.isEmpty()) {
            RuleEvent event = matchIter.next();
            if (!isExistingOutTransition(event, getAtState())) {
                // add the transition corresponding to match, this may not
                // result in a new state
                applyEvent(event, cache);
            }
        }
        if (!matchIter.hasNext()) {
            setClosed(getAtState());
        }
        updateAtState();
        return true;
    }

    @Override
    protected void updateAtState() {
        this.backFrom = null; // set to non null if backtracking
        if (!this.collector.isEmpty()) {
            this.atState = this.collector.pickRandomNewState();
            return;
        }
        // backtracking
        GraphState s = null;
        do {
            this.backFrom = this.atState;
            this.atState = parentOf(this.atState);
        } while (this.atState != null
            && (s = getFirstOpenSuccessor(this.atState)) == null
            && !getGTS().isOpen(this.atState));

        // identify the reason of exiting the loop
        if (this.atState == null) {
            return;
        } // the start state is reached and does not have open successors

        if (s != null) { // the current atState has an open successor (not
            // really a backtracking, a sibling is fully
            // explored)
            this.backFrom = null;
            this.atState = s;
        } // else, atState is open, so we continue exploring it

    }

    /**
     * Determines whether a match corresponds to an outgoing transition of a
     * state, in the GTS constructed by this strategy.
     */
    private boolean isExistingOutTransition(RuleEvent event, GraphState state) {
        for (GraphTransition trans : getGTS().outEdgeSet(this.atState)) {
            if (event.equals(trans.getEvent())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void prepare(GTS gts, GraphState state) {
        super.prepare(gts, state);
        getGTS().addGraphListener(this.collector);
    }

    /**
     * Creates a strategy with a given cache size.
     */
    public ExploreSingleTransitionDFStrategy() {
        this.explCacheCache = new CacheMap<GraphState,ExploreCache>(cacheSize);
        this.matchIterCache =
            new CacheMap<GraphState,MatchesIterator>(cacheSize);
    }

    /**
     * Used to register a state added to the GTS. Randomness plays a role in the
     * case of abstract transformation, when one matching may require adding
     * several new states.
     */
    private final RandomNewStateChooser collector = new RandomNewStateChooser();

}
