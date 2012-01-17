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

import groove.explore.result.Acceptor;
import groove.explore.util.MatchApplier;
import groove.explore.util.MatchSetCollector;
import groove.explore.util.RuleEventApplier;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.MatchResult;
import groove.match.MatcherFactory;
import groove.trans.SystemRecord;

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
        this.atState = state == null ? gts.startState() : state;
        MatcherFactory.instance().setDefaultEngine();
    }

    public boolean next() {
        if (getState() == null) {
            return false;
        }
        for (MatchResult next : createMatchCollector().getMatchSet()) {
            getMatchApplier().apply(getState(), next);
        }
        getState().setClosed(true);
        return updateAtState();
    }

    /**
     * The graph transition system explored by the strategy.
     * @return The graph transition system explored by the strategy.
     */
    protected GTS getGTS() {
        return this.gts;
    }

    /**
     * Returns the state that will be explored next. If <code>null</code>,
     * there is nothing left to explore. Is updated by {@link #getNextState()}.
     */
    public GraphState getState() {
        return this.atState;
    }

    @Override
    public GraphState getLastState() {
        return this.lastState;
    }

    /**
     * Sets atState to the next state to be explored, as
     * returned by {@link #getNextState()}, or <code>null</code> if
     * there are no more states to be explored. This is the place where
     * satisfaction of the condition is to be tested. This method should be the
     * only one who updates atState.
     * @return {@code true} if there are more states to be explored, {@code false}
     * otherwise.
     * @see #getNextState()
     */
    final protected boolean updateAtState() {
        this.lastState = getState();
        this.atState = getNextState();
        return this.atState != null;
    }

    /**
     * Sets atState to the next state to be explored, or <code>null</code> if
     * there are no more states to be explored. This is the place where
     * satisfaction of the condition is to be tested. This method should be the
     * only one who updates atState.
     * @return {@code true} if there are more states to be explored, {@code false}
     * otherwise.
     */
    protected abstract GraphState getNextState();

    /** 
     * Closes a given state. 
     * @param complete  indicates whether all outgoing transitions of the state have
     * been explored.
     */
    protected void setClosed(GraphState state, boolean complete) {
        state.setClosed(complete);
    }

    /**
     * Returns a fresh match collector for this strategy, based on the current
     * state and related information.
     */
    protected MatchSetCollector createMatchCollector() {
        return new MatchSetCollector(getState(), getRecord(),
            getGTS().checkDiamonds());
    }

    /** Sets the match applier of this strategy. */
    public void setMatchApplier(RuleEventApplier applier) {
        this.applier = applier;
    }

    /** Returns the match applier of this strategy. */
    protected final RuleEventApplier getMatchApplier() {
        if (this.applier == null) {
            this.applier = createMatchApplier();
        }
        return this.applier;
    }

    /** Callback factory method for the match applier. */
    protected RuleEventApplier createMatchApplier() {
        return new MatchApplier(this.gts);
    }

    public void addGTSListener(Acceptor listener) {
        getGTS().addLTSListener(listener);
    }

    public void removeGTSListener(Acceptor listener) {
        getGTS().removeLTSListener(listener);
    }

    /** Convenience method to retrieve the GTS' system record. */
    final protected SystemRecord getRecord() {
        return getGTS().getRecord();
    }

    /**
     * Match applier for the underlying GTS.
     */
    private RuleEventApplier applier;
    /** The graph transition system explored by the strategy. */
    private GTS gts;
    /** The state that will be explored by the next call of {@link #next()}. */
    private GraphState atState;
    /** The state that will be explored by the next call of {@link #next()}. */
    private GraphState lastState;
}
