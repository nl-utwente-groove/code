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

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import groove.explore.util.ExploreCache;
import groove.explore.util.MatchesIterator;
import groove.lts.GraphState;

/**
 * A strategy that uses backtracking mechanism to determine a next state to be
 * explored.
 * 
 * Such a strategy knows the state from which it backtracks, and gives the
 * possibility of caching matches iterators for partially explored states.
 * @author Iovka Boneva
 * 
 */
public abstract class AbstractBacktrackingStrategy extends AbstractStrategy {

    /**
     * Size for caches of subclasses. IOVKA experiments with the existing
     * samples showed that a cache of size 128 is never full, and in lots of
     * cases it is even almost not used, as there is few backtracking.
     */
    protected static final int cacheSize = 128;

    @Override
    protected abstract void updateAtState();

    public abstract boolean next();

    /**
     * The state from which the strategy backtracks, or null if the current
     * value of atState is not got with backtracking.
     */
    protected GraphState getBackFrom() {
        return this.backFrom;
    }

    /** Gets an explore cache for atState, or creates it if it is not cached. */
    @Override
    protected ExploreCache getCache(boolean ruleInterrupted,
            boolean isRandomized) {
        if (this.explCacheCache == null) {
            return super.getCache(ruleInterrupted, isRandomized);
        }
        ExploreCache result = this.explCacheCache.get(getAtState());
        if (result == null) {
            result = super.getCache(ruleInterrupted, isRandomized);
            this.explCacheCache.put(getAtState(), result);
        }
        return result;
    }

    /** Tries to find the matches iterator in the cache first. */
    @Override
    protected MatchesIterator getMatchesIterator(ExploreCache cache) {
        if (this.matchIterCache == null) {
            return super.getMatchesIterator(cache);
        }
        MatchesIterator result = this.matchIterCache.get(getAtState());
        if (result == null) {
            result = super.getMatchesIterator(cache);
            this.matchIterCache.put(getAtState(), result);
        }
        return result;
    }

    /** Also removes the caches for that state. */
    @Override
    protected void setClosed(GraphState state) {
        super.setClosed(state);
        if (this.explCacheCache != null) {
            this.explCacheCache.remove(state);
        }
        if (this.matchIterCache != null) {
            this.matchIterCache.remove(state);
        }
    }

    /**
     * The state from which atState is backtracking. To be set by the
     * {@link #updateAtState()} method. Note that this is not necessarily the
     * last explored state, as backtracking can traverse several states.
     */
    protected GraphState backFrom;

    /**
     * Caches instances of {@link ExploreCache} for graph states. A strategy
     * which does not want to use this has to leave it null.
     */
    protected CacheMap<GraphState,ExploreCache> explCacheCache;

    /**
     * Caches instances of {@link MatchesIterator} for graph states. A strategy
     * which does not want to use this has to leave it null.
     */
    protected CacheMap<GraphState,MatchesIterator> matchIterCache;

    /**
     * Used to store a limited number of caches for open states. The capacity is
     * given at construct time. Automatically removes old caches when the
     * maximum capacity is reached. Removal is only allowed for the last added
     * element not yet removed.
     */
    // TODO to be done with an "ordered" hash map
    public static class CacheMap<T,C> extends HashMap<T,C> {

        @Override
        public C put(T key, C value) {
            checkInvariants();
            if (super.size() == this.capacity) {
                // free one slot
                super.remove(this.insertOrder.getFirst());
                this.insertOrder.removeFirst();
            }
            C result = super.put(key, value);
            if (result == null) {
                this.insertOrder.addLast(key);
            }
            checkInvariants();
            return result;
        }

        @Override
        public C remove(Object key) {
            checkInvariants();
            C result = super.remove(key);
            if (result != null) {
                this.insertOrder.remove(key);
            }
            checkInvariants();
            return result;

        }

        CacheMap(int capacity) {
            super(capacity);
            this.capacity = capacity;
            this.insertOrder = new LinkedList<T>();
            checkInvariants();
        }

        private final LinkedList<T> insertOrder;
        private final int capacity;

        // INVARIANTS
        private void checkInvariants() {
            if (!groove.abs.Util.ea()) {
                return;
            }
            assert super.keySet().size() == this.insertOrder.size()
                && super.keySet().equals(new HashSet<T>(this.insertOrder)) : "Key set: "
                + super.keySet() + " *** Insert order: " + this.insertOrder;
        }

    }
}
