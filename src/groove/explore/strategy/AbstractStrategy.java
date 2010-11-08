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

import groove.control.Location;
import groove.explore.result.Acceptor;
import groove.explore.util.ExploreCache;
import groove.explore.util.MatchApplier;
import groove.explore.util.MatchSetCollector;
import groove.explore.util.RandomChooserInSequence;
import groove.explore.util.RuleEventApplier;
import groove.lts.GTS;
import groove.lts.GraphNextState;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.StartGraphState;
import groove.trans.RuleEvent;
import groove.trans.SystemRecord;

import java.util.Iterator;

/**
 * A partial (abstract) implementation of a strategy.
 * @author Arend Rensink
 * 
 */
public abstract class AbstractStrategy implements Strategy {
    final public void prepare(GTS gts) {
        this.prepare(gts, null);
    }

    public void prepare(GTS gts, GraphState state) {
        this.gts = gts;
        this.applier = null;
        this.atState =
            this.startState = state == null ? gts.startState() : state;
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
     * Returns a fresh match collector for this strategy, based on the current
     * state and related information.
     * @param cache the rule cache for the collector
     */
    protected MatchSetCollector createMatchCollector(ExploreCache cache) {
        return new MatchSetCollector(getAtState(), cache, getRecord());
    }

    /** Sets the match applier of this strategy. */
    public void setMatchApplier(RuleEventApplier applier) {
        this.applier = applier;
    }

    /** Returns the match applier of this strategy. */
    protected RuleEventApplier getMatchApplier() {
        if (this.applier == null) {
            this.applier = new MatchApplier(this.gts);
        }
        return this.applier;
    }

    /**
     * Applies a given rule event to the current state, and returns
     * the resulting transition.
     */
    protected GraphTransition applyEvent(RuleEvent event, ExploreCache cache) {
        Location targetLocation = cache.getTarget(event.getRule());
        return getMatchApplier().apply(getAtState(), event, targetLocation);
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

    /** Return the current value of the "close on exit" setting */
    public boolean closeExit() {
        return this.closeExit;
    }

    /**
     * Enable closeExit, to close states when a strategy changes its atState.
     * This can save memory when using linear strategies.
     */
    public void enableCloseExit() {
        this.closeExit = true;
    }

    /** Convenience method to retrieve the GTS' system record. */
    protected SystemRecord getRecord() {
        return getGTS().getRecord();
    }

    /**
     * Match applier for the underlying GTS.
     */
    protected RuleEventApplier applier;
    /** The graph transition system explored by the strategy. */
    private GTS gts;
    /** The state where the strategy starts exploring. */
    private GraphState startState;
    /** The state that will be explored by the next call of {@link #next()}. */
    protected GraphState atState;
    /**
     * Indicates whether the strategy should use aliasing or not. Default value
     * is true.
     */
    protected boolean aliasing = true;

    /** 
     * Option to close states after a transition has been added from them.
     * Can be used by linear strategies to save memory by closing states ASAP.
     */
    protected boolean closeExit = false;
}
