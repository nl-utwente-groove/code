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
package groove.control;

import java.util.List;

/**
 * Position in a control structure that is either deadlocked, final, or
 * offers an {@link Attempt}.
 * @author Arend Rensink
 * @version $Revision $
 */
public interface Position<P extends Position<P,A>,A extends Attempt.Stage<P,A>> {
    /** Indicates if this is a start position. */
    public abstract boolean isStart();

    /** Returns the position type. */
    public abstract Type getType();

    /**
     * Indicates if this is a deadlock position.
     * A deadlock position has no outgoing calls or verdicts.
     * Convenience method for {@code getType() == DEAD}.
     * @see Type#DEAD
     */
    public abstract boolean isDead();

    /**
     * Indicates if this is a final position.
     * A deadlock position has no outgoing calls or verdicts.
     * Convenience method for {@code getType() == FINAL}.
     * @see Type#FINAL
     */
    public abstract boolean isFinal();

    /**
     * Indicates if this is a trial position.
     * A trial position has at least one outgoing call, as well as verdicts.
     * Convenience method for {@code getType() == TRIAL}.
     * @see Type#TRIAL
     */
    public abstract boolean isTrial();

    /** Returns the transient depth of this position. */
    public abstract int getTransience();

    /**
     * Returns the attempt of this position.
     * Should only be invoked after the position is fixed, and is a trial position.
     */
    public abstract Attempt<P,A> getAttempt();

    /** Indicates if this location has a non-empty set of control variables. */
    public boolean hasVars();

    /**
     * Returns the list of control variables in this location,
     * ordered alphabetically according to their names.
     */
    public List<CtrlVar> getVars();

    /** Position type. */
    public static enum Type {
        /** Final position: terminating, no attempt or verdicts. */
        FINAL,
        /** Deadlock position: non-terminating, no attempt or verdicts. */
        DEAD,
        /** Trial position: has an attempt, and corresponding success and failure verdicts. */
        TRIAL, ;
    }
}