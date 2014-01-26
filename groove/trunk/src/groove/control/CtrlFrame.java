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

    /** Indicates if this frame is inside an atomic block. */
    boolean isTransient();

    /** Indicates if this frame is deadlocked, meaning that it will
     * engender no more behaviour. */
    boolean isDead();

    /** Indicates if this frame represents success. */
    boolean isFinal();

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
}
