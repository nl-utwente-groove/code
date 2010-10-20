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
import groove.explore.util.MatchSetCollector;
import groove.graph.GraphShape;
import groove.graph.Node;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.LTSAdapter;
import groove.trans.RuleEvent;
import groove.trans.VirtualEvent;
import groove.util.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Strategy that closes every state it explores, and adds the newly generated
 * states to a pool, together with information regarding the outgoing
 * transitions of its parent. Subclasses must decide on the order of the pool;
 * e.g., breadth-first or depth-first.
 */
abstract public class ClosingStrategy extends AbstractStrategy {
    public boolean next() {
        if (getAtState() == null) {
            getGTS().removeGraphListener(this.exploreListener);
            return false;
        }
        ExploreCache cache = getCache(false, false);
        Collection<RuleEvent> matchSet =
            createMatchCollector(cache).getMatchSet();
        Iterator<RuleEvent> matchIter = matchSet.iterator();
        Collection<VirtualEvent.GraphState> outTransitions =
            new ArrayList<VirtualEvent.GraphState>(matchSet.size());
        while (matchIter.hasNext()) {
            RuleEvent nextEvent = matchIter.next();
            GraphTransition trans =
                getMatchApplier().apply(getAtState(), nextEvent,
                    cache.getTarget(nextEvent.getRule()));
            outTransitions.add(new VirtualEvent.GraphState(trans));
        }
        addToPool(outTransitions);
        setClosed(getAtState());
        updateAtState();
        return true;
    }

    @Override
    public void prepare(GTS gts, GraphState startState) {
        super.prepare(gts, startState);
        // for the closing strategy, there is no problem in aliasing
        // the graph data structures. On the whole, this seems wise, to
        // avoid excessive garbage collection.
        // TODO switched off again due to observed problems
        gts.getRecord().setCopyGraphs(true);
        getGTS().addGraphListener(this.exploreListener);
        this.newStates.clear();
        this.virtualEvents = null;
        clearPool();
    }

    @Override
    protected void updateAtState() {
        PoolElement next = getFromPool();
        this.atState = next == null ? null : next.first();
        this.virtualEvents = next == null ? null : next.second();
    }

    /**
     * Adds the newly generated states to the pool of states to be explored.
     * @param newVirtualEvents the virtual events generated for the current
     *        state
     */
    private void addToPool(Collection<VirtualEvent.GraphState> newVirtualEvents) {
        for (GraphState newState : this.newStates) {
            putInPool(new PoolElement(newState, newVirtualEvents));
        }
        this.newStates.clear();
    }

    /** Callback method to add a pool element to the pool. */
    abstract protected void putInPool(PoolElement element);

    /** Returns the next element from the pool of explorable states. */
    abstract protected PoolElement getFromPool();

    /** Clears the pool, in order to prepare the strategy for reuse. */
    abstract protected void clearPool();

    /**
     * This collector takes the virtual events for the current state into
     * account.
     */
    @Override
    protected MatchSetCollector createMatchCollector(ExploreCache cache) {
        return new MatchSetCollector(getAtState(), cache, getRecord(),
            this.virtualEvents);
    }

    /** Internal store of newly generated states. */
    final Collection<GraphState> newStates = new ArrayList<GraphState>();
    /** Parent transitions of the currently explored state. */
    private Collection<VirtualEvent.GraphState> virtualEvents;
    /** Listener to keep track of states added to the GTS. */
    private final ExploreListener exploreListener = new ExploreListener();

    /** A queue with states to be explored, used as a FIFO. */
    private class ExploreListener extends LTSAdapter {
        @Override
        public void addUpdate(GraphShape graph, Node node) {
            ClosingStrategy.this.newStates.add((GraphState) node);
        }
    }

    /** Element type of the pool of explorable elements. */
    protected static class PoolElement extends
            Pair<GraphState,Collection<VirtualEvent.GraphState>> {
        /** Constructs a pool element from a given state and virtual event set. */
        public PoolElement(GraphState state,
                Collection<groove.trans.VirtualEvent.GraphState> virtualEvents) {
            super(state, virtualEvents);
        }
    }
}
