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
import groove.match.SearchEngineFactory;
import groove.trans.AbstractCondition;
import groove.trans.Rule;
import groove.trans.SPORule;
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
        this.atState =
            this.startState = state == null ? gts.startState() : state;
        resetRulesMatchers();
        SearchEngineFactory.getInstance().setCurrentEngineType(
            SearchEngineFactory.getInstance().getDefaultEngineType());
    }

    public boolean next() {
        if (getAtState() == null) {
            return false;
        }
        for (MatchResult next : createMatchCollector().getMatchSet()) {
            getMatchApplier().apply(getAtState(), next);
        }
        setClosed(getAtState(), true);
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
     * Iterates through all the rules in the GTS's grammar and calls
     * their {#link {@link AbstractCondition#resetMatcher()} method. 
     */
    protected void resetRulesMatchers() {
        for (Rule r : getGTS().getGrammar().getRules()) {
            if (r instanceof AbstractCondition<?>) {
                ((SPORule) r).resetMatcher();
            }
        }
    }

    /**
     * The state where the strategy starts exploring.
     * @return The state where the strategy starts exploring.
     */
    protected final GraphState startState() {
        return this.startState;
    }

    /**
     * Returns the state that will be explored next. If <code>null</code>,
     * there is nothing left to explore. Is updated by {@link #updateAtState()}.
     */
    protected GraphState getAtState() {
        return this.atState;
    }

    /**
     * Sets atState to the next state to be explored, or <code>null</code> if
     * there are no more states to be explored. This is the place where
     * satisfaction of the condition is to be tested. This method should be the
     * only one who updates atState.
     * @return {@code true} if there are more states to be explored, {@code false}
     * otherwise.
     */
    protected abstract boolean updateAtState();

    /** 
     * Closes a given state. 
     * @param complete  indicates whether all outgoing transitions of the state have
     * been explored.
     */
    protected void setClosed(GraphState state, boolean complete) {
        getGTS().setClosed(state, complete);
    }

    /**
     * Returns a fresh match collector for this strategy, based on the current
     * state and related information.
     */
    protected MatchSetCollector createMatchCollector() {
        return new MatchSetCollector(getAtState(), getRecord(),
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

    /** Default implementation; does nothing. */
    public void addGTSListener(Acceptor listener) {
        getGTS().addGraphListener(listener);
    }

    /** Default implementation; does nothing. */
    public void removeGTSListener(Acceptor listener) {
        getGTS().removeGraphListener(listener);
    }

    /** Convenience method to retrieve the GTS' system record. */
    protected SystemRecord getRecord() {
        return getGTS().getRecord();
    }

    /**
     * Match applier for the underlying GTS.
     */
    private RuleEventApplier applier;
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
}
