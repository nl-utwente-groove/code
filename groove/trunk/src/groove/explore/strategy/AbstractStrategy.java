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

import groove.abs.lts.AbstrGraphState;
import groove.explore.result.Acceptor;
import groove.explore.util.AliasMatchesIterator;
import groove.explore.util.ExploreCache;
import groove.explore.util.LocationCache;
import groove.explore.util.MatchApplier;
import groove.explore.util.MatchSetCollector;
import groove.explore.util.MatchesIterator;
import groove.explore.util.RandomChooserInSequence;
import groove.lts.GTS;
import groove.lts.GraphNextState;
import groove.lts.GraphState;
import groove.lts.StartGraphState;
import groove.lts.StateGenerator;
import groove.trans.SystemRecord;

import java.util.Iterator;

/**
 * A partial (abstract) implementation of a strategy.
 * @author
 * 
 */
public abstract class AbstractStrategy implements Strategy {
    final public void prepare(GTS gts) {
        this.prepare(gts, null);
    }

    public void prepare(GTS gts, GraphState state) {
        this.gts = gts;
        this.generator = gts.getRecord().getStateGenerator(gts);
        this.atState = this.startState = state == null ? gts.startState() : state;
        this.applier = new MatchApplier(gts);
    }

    /**
     * The graph transition system explored by the strategy.
     * @return The graph transition system explored by the strategy.
     */
    protected GTS getGTS() {
        return this.gts;
    }

    /**
     * The state where the strategy starts exploring.
     * @return The state where the strategy starts exploring.
     */
    protected final GraphState startState() {
        return this.startState;
    }

    /**
     * The state generator used as interface with the GTS. Is initialised at the
     * same time as the GTS.
     */
    protected final StateGenerator getGenerator() {
        return this.generator;
    }

    /**
     * Sets atState to the next state to be explored, or <code>null</code> if
     * there are no more states to be explored. This is the place where
     * satisfaction of the condition is to be tested. This method should be the
     * only one who updates atState.
     */
    protected abstract void updateAtState();

    /**
     * Returns the state that will be explored next. If <code>null</code>,
     * there is nothing left to explore. Is updated by {@link #updateAtState()}.
     */
    protected final GraphState getAtState() {
        return this.atState;
    }

    /** Closes a given state. */
    protected void setClosed(GraphState state) {
        getGTS().setClosed(state);
    }

    /**
     * The parent of a given state, or null if this is the start state. May be
     * used by the backtracking strategies and for alias matching
     * @param state state for which the parent will be returned
     */
    protected final GraphState parentOf(GraphState state) {
        if (state instanceof StartGraphState) {
            return null;
        } else {
            return ((GraphNextState) state).source();
        }
    }

    /**
     * Returns a random open successor of a state, if any. Returns null
     * otherwise. Is considered as successor only a state that is a successor in
     * the spanning tree.
     */
    protected final GraphState getRandomOpenSuccessor(GraphState state) {
        Iterator<? extends GraphState> sucIter = state.getNextStateIter();
        RandomChooserInSequence<GraphState> chooser =
            new RandomChooserInSequence<GraphState>();
        while (sucIter.hasNext()) {
            GraphState s = sucIter.next();
            if (getGTS().getOpenStates().contains(s)
                && s instanceof GraphNextState
                && ((GraphNextState) s).source().equals(state)) {
                chooser.show(s);
            }
        }
        return chooser.pickRandom();
    }

    /**
     * Returns the first open successor of a state, if any. Returns null
     * otherwise.
     */
    protected final GraphState getFirstOpenSuccessor(GraphState state) {
        Iterator<? extends GraphState> sucIter = state.getNextStateIter();
        while (sucIter.hasNext()) {
            GraphState s = sucIter.next();
            if (getGTS().getOpenStates().contains(s)) {
                return s;
            }
        }
        return null;
    }

    /**
     * Gives a cache for atState.
     * @param ruleInterrupted If <code>false</code>, the cache considers that
     *        if there is a match for some rule <code>r</code>, then all
     *        matches have been found. Therefore, the rule <code>r</code> is
     *        not returned by the iterator. If <code>true</code>, the cache
     *        will explore again all rules that are not explicitly stated as
     *        fully explored.
     * @param isRandomized If <code>true</code>, the cache iterates over the
     *        rules in a random order.
     */
    protected ExploreCache getCache(boolean ruleInterrupted,
            boolean isRandomized) {
        return getGTS().getRecord().createCache(getAtState(), ruleInterrupted,
            isRandomized);
    }

    /**
     * Gives an appropriate matches iterator for atState. This may be a
     * {@link MatchesIterator} or an {@link AliasMatchesIterator}.
     * @param cache
     */
    protected MatchesIterator getMatchesIterator(ExploreCache cache) {
        // Two cases where an alias iterator may be returned :
        // the parent is closed, or one of the successors is closed

        /* Un ugly hack to forbid aliasing for cases when it does not work */
        boolean aliasingNotAllowed =
            cache instanceof LocationCache
                || getAtState() instanceof AbstrGraphState;

        if (!aliasingNotAllowed) {

            // First case : the parent is closed
            GraphState parent = parentOf(getAtState());
            if (this.aliasing && parent != null && parent.isClosed()) {
                GraphNextState s = (GraphNextState) getAtState();
                return new AliasMatchesIterator(s, cache, getRecord());
            }

            // Second case : one of the successors is closed.
            // This is only considered for backtracking strategies, in
            // which the state from which we backtrack is closed
            // and recently used matches iterators may be cached
            if (false && this instanceof AbstractBacktrackingStrategy) {
                AbstractBacktrackingStrategy str =
                    (AbstractBacktrackingStrategy) this;
                // TODO integrate alias matches iterator constructed from a
                // sibling or child
            }
        }

        // in all other cases, return a "normal" matches iterator
        return new MatchesIterator(getAtState(), cache, getRecord());
    }

    /**
     * Returns a fresh match collector for this strategy, based on the current
     * state and related information.
     * @param cache the rule cache for the collector
     */
    protected MatchSetCollector createMatchCollector(ExploreCache cache) {
        return new MatchSetCollector(getAtState(), cache, getRecord(), null);
    }

    /** Returns the match applier of this strategy. */
    protected MatchApplier getMatchApplier() {
        return this.applier;
    }

    /**
     * Method for exploring a single state locally. The state will be closed
     * afterwards.
     * @param state the state to be fully explored locally
     */
    public void exploreState(GraphState state) {
        Strategy explore = new ExploreStateStrategy();
        if (getGTS().isOpen(state)) {
            explore.prepare(getGTS(), state);
            explore.next();
        }
    }

    /** Default implementation; does nothing. */
    public void addGTSListener(Acceptor listener) {
        getGTS().addGraphListener(listener);
    }

    /** Default implementation; does nothing. */
    public void removeGTSListener(Acceptor listener) {
        getGTS().removeGraphListener(listener);
    }

    /**
     * Enable closeExit, to close states when a strategy changes its atState.
     * This can save memory when using linear strategies.
     */
    public void enableCloseExit() {
        this.closeExit = true;
    }

    /** Return the current value of the "close on exit" setting */
    public boolean closeExit() {
        return this.closeExit;
    }

    /** Convenience method to retrieve the GTS' system record. */
    protected SystemRecord getRecord() {
        return getGTS().getRecord();
    }

    /**
     * Match applier for the underlying GTS.
     */
    private MatchApplier applier;
    /** The graph transition system explored by the strategy. */
    private GTS gts;
    /** The state where the strategy starts exploring. */
    private GraphState startState;
    /** The state that will be explored by the next call of {@link #next()}. */
    protected GraphState atState;
    /** The state generator used as interface with the GTS. */
    private StateGenerator generator;
    /**
     * Indicates whether the strategy should use aliasing or not. Default value
     * is true.
     */
    // TODO this is set to false until the aliased matcher is debugged
    protected boolean aliasing = true;

    /** Option to close states after a transition has been added from them * */
    /**
     * Can optionally be used by linear strategies to save memory by closing
     * states asap *
     */
    protected boolean closeExit = false;

}
