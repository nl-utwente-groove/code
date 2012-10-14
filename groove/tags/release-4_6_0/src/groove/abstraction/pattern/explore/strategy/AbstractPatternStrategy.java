/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an 
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific 
 * language governing permissions and limitations under the License.
 *
 * $Id$
 */
package groove.abstraction.pattern.explore.strategy;

import groove.abstraction.pattern.explore.util.PatternGraphMatchSetCollector;
import groove.abstraction.pattern.explore.util.PatternRuleEventApplier;
import groove.abstraction.pattern.lts.PGTS;
import groove.abstraction.pattern.lts.PatternState;
import groove.abstraction.pattern.match.Match;
import groove.explore.strategy.AbstractStrategy;

/**
 * Basic implementation of {@link PatternStrategy} interface with common
 * functionality shared by all sub-classes.
 * 
 * See {@link AbstractStrategy}. 
 */
public abstract class AbstractPatternStrategy implements PatternStrategy {

    // ------------------------------------------------------------------------
    // Object fields
    // ------------------------------------------------------------------------

    /** Match applier for the underlying GTS. */
    private PatternRuleEventApplier applier;
    /** The pattern graph transition system explored by the strategy. */
    private PGTS pgts;
    /** The state that will be explored by the next call of {@link #next()}. */
    private PatternState atState;

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    @Override
    public final void prepare(PGTS pgts) {
        this.prepare(pgts, null);
    }

    @Override
    public void prepare(PGTS pgts, PatternState state) {
        this.pgts = pgts;
        this.atState = state == null ? pgts.startState() : state;
    }

    @Override
    public boolean next() {
        if (getState() == null) {
            return false;
        }
        for (Match next : createMatchCollector().getMatchSet()) {
            getMatchApplier().apply(getState(), next);
        }
        getState().setClosed(true);
        return updateAtState();
    }

    /**
     * Returns the state that will be explored next. If <code>null</code>,
     * there is nothing left to explore. Is updated by {@link #getNextState()}.
     */
    @Override
    public PatternState getState() {
        return this.atState;
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    /**
     * Sets atState to the next state to be explored, or <code>null</code> if
     * there are no more states to be explored. This is the place where
     * satisfaction of the condition is to be tested. This method should be the
     * only one who updates atState.
     * @return {@code true} if there are more states to be explored, {@code false}
     * otherwise.
     */
    protected abstract PatternState getNextState();

    /** Returns the graph transition system explored by the strategy. */
    protected PGTS getPGTS() {
        return this.pgts;
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
    private boolean updateAtState() {
        this.atState = getNextState();
        return this.atState != null;
    }

    /**
     * Returns a fresh match collector for this strategy, based on the current
     * state and related information.
     */
    private PatternGraphMatchSetCollector createMatchCollector() {
        return getPGTS().createMatchCollector(getState());
    }

    /** Returns the match applier of this strategy. (Lazy creation). */
    private PatternRuleEventApplier getMatchApplier() {
        if (this.applier == null) {
            this.applier = getPGTS().createMatchApplier();
        }
        return this.applier;
    }

}
