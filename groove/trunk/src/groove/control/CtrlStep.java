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

import groove.control.instance.Assignment;
import groove.control.instance.Frame;
import groove.control.instance.Step;

import java.util.List;
import java.util.Map;

/**
 * Temporary interface to unify the old {code CtrlTransition} and {@link Step}.
 * @author Arend Rensink
 * @version $Revision $
 */
public interface CtrlStep extends Comparable<CtrlStep>, CalledAction {
    /**
     * Indicates if the step may cause modifications in the control state.
     * This is the case if the (prime) source and target of this step differ,
     * or the call has out-parameters.
     */
    boolean isModifying();

    /** Indicates if this step is the initial step of a recipe. */
    boolean isInitial();

    /** Indicates if this step is part of an atomic block. */
    boolean isPartial();

    /** Method returning supertype of {@code CtrlState} and {@link Frame}. */
    public CtrlFrame target();

    /**
     * Returns the list of frame value assignments involved in applying this step.
     * These consist of outstanding stack pops due to verdict transitions of the source frame,
     * insofar they have caused procedures to be exited, followed by pushes due to fresh
     * procedure calls, followed by the action of this step, followed by pops due to
     * procedures explicitly exited by this step.
     */
    public List<Assignment> getApplyAssignments();

    /** Returns the mapping of output variables to argument positions of the called unit. */
    public Map<CtrlVar,Integer> getOutVars();
}
