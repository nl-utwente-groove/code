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

import groove.control.instance.Frame;
import groove.grammar.Recipe;

import java.util.List;
import java.util.Set;

/**
 * Supertype of {@link CtrlState} and {@link Frame}, used for the
 * purpose of a smooth transition to the new control implementation.
 * @author Arend Rensink
 * @version $Revision $
 */
public interface CtrlFrame {
    /** Indicates that this is the initial frame of the automaton. */
    boolean isStart();

    /** Returns the transient depth of the frame. */
    int getTransience();

    /**
     * Indicates if this frame is inside an atomic block.
     * Convenience method for <code>getTransience() > 0</code>
     */
    boolean isTransient();

    /** Indicates if this frame is nested inside a procedure. */
    boolean isNested();

    /**
     * Indicates if this frame is inside a recipe.
     * This is the case if and only if the recipe has started
     * and not yet terminated.
     * A frame can only be inside a recipe if it is transient.
     * @see #getRecipe()
     * @see #isTransient()
     */
    boolean inRecipe();

    /**
     * Returns the outer recipe to which this frame belongs, if any.
     * @return the recipe to this this frame belongs, or {@code null}
     * if it is not inside a recipe
     * @see #inRecipe()
     */
    Recipe getRecipe();

    /** Indicates if this frame is deadlocked, meaning that it will
     * engender no more behaviour. */
    boolean isDead();

    /** Indicates if this frame represents success. */
    boolean isFinal();

    /** Indicates if this frame has an outgoing control step. */
    boolean isTrial();

    /**
     * Returns the set of called actions that have been tried at this point
     * of the frame.
     */
    public abstract Set<? extends CalledAction> getPastAttempts();

    /**
     * Returns the prime frame of this frame.
     * The prime frame is the initial frame from which this one was
     * reached after a sequence of verdicts.
     */
    public abstract CtrlFrame getPrime();

    /** Indicates if this frame has any control variables. */
    public boolean hasVars();

    /** Returns the list of control variables in this frame. */
    public List<CtrlVar> getVars();

    /** Flag determining if the new control implementation should be used. */
    public final static boolean NEW_CONTROL = true;
}
