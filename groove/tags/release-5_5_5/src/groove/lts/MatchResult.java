/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
package groove.lts;

import groove.control.instance.Step;
import groove.grammar.Rule;
import groove.transform.RuleEvent;

/**
 * Class encoding the result of matching the rule in a control transition.
 * This essentially consists of a rule event and the control transition.
 */
public class MatchResult implements GraphTransitionKey {
    /** Constructs a result from a given rule transition. */
    public MatchResult(RuleTransition ruleTrans) {
        this.ruleTrans = ruleTrans;
        this.event = ruleTrans.getEvent();
        this.step = ruleTrans.getStep();
    }

    /** Constructs a result from a given event and control step. */
    public MatchResult(RuleEvent event, Step step) {
        this.ruleTrans = null;
        this.event = event;
        this.step = step;
    }

    /**
     * Indicates if this match result corresponds to an
     * already explored rule transition from a given source state.
     * @param state the source state for which the test is carried out
     * @see #hasTransition()
     */
    public boolean hasTransitionFrom(GraphState state) {
        return hasTransition() && getTransition().source() == state;
    }

    /**
     * Indicates if this match result is based on an already explored rule transition.
     * Note that, in case the match is reused from a parent state,
     * the source of this transition (if any) may differ from
     * the state to which the match is to be applied.
     * @see #hasTransitionFrom(GraphState)
     */
    public boolean hasTransition() {
        return this.ruleTrans != null;
    }

    /** Returns the rule transition wrapped in this match result, if any. */
    public RuleTransition getTransition() {
        return this.ruleTrans;
    }

    private final RuleTransition ruleTrans;

    /** Returns the event wrapped by this transition key. */
    @Override
    public RuleEvent getEvent() {
        return this.event;
    }

    private final RuleEvent event;

    /** Returns the control transition wrapped by this transition key. */
    public Step getStep() {
        return this.step;
    }

    private final Step step;

    /** Returns the underlying rule of this match. */
    @Override
    public Rule getAction() {
        return this.event.getRule();
    }

    @Override
    public int hashCode() {
        int hashcode = this.hashcode;
        if (hashcode == 0) {
            hashcode = computeHashCode();
            if (hashcode == 0) {
                hashcode++;
            }
            this.hashcode = hashcode;
        }
        return hashcode;
    }

    private int computeHashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + getStep().hashCode();
        result = prime * result + getEvent().hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof MatchResult)) {
            return false;
        }
        MatchResult other = (MatchResult) obj;
        if (!getStep().equals(other.getStep())) {
            return false;
        }
        if (!getEvent().equals(other.getEvent())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return getAction().getLastName();
    }

    /** The precomputed hashcode; 0 if it has not yet been not initialised. */
    private int hashcode;
}
