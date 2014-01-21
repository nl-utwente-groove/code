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
package groove.control.instance;

import groove.control.AssignSource;
import groove.control.CtrlVar;

import java.util.Map;

import com.sun.org.apache.bcel.internal.generic.PUSH;

/**
 * Action to be taken as part of a {@link Step}.
 * @author Arend Rensink
 * @version $Revision $
 */
public class StepAction {
    /**
     * Creates an action with all necessary parameters.
     */
    private StepAction(Kind kind, Map<CtrlVar,AssignSource> assignment) {
        this.kind = Kind.PUSH;
        this.assignment = new AssignSource[assignment.size()];
        assignment.values().toArray(this.assignment);
    }

    /** Returns the kind of action. */
    public Kind getKind() {
        return this.kind;
    }

    private final Kind kind;

    /** Returns the assignment for this action. */
    public AssignSource[] getAssignment() {
        return this.assignment;
    }

    private final AssignSource[] assignment;

    /** Creates a new {@link PUSH} action with a given assignment. */
    public static StepAction push(Map<CtrlVar,AssignSource> assignment) {
        return new StepAction(Kind.PUSH, assignment);
    }

    /** Creates a new {@link Kind#POP} action with a given assignment. */
    public static StepAction pop(Map<CtrlVar,AssignSource> assignment) {
        return new StepAction(Kind.POP, assignment);
    }

    /** Creates a new {@link Kind#MODIFY} action, with a given assignment. */
    public static StepAction modify(Map<CtrlVar,AssignSource> assignment) {
        return new StepAction(Kind.MODIFY, assignment);
    }

    /** Kind of {@link StepAction}. */
    public static enum Kind {
        /** Create and initialise a frame instance. */
        PUSH,
        /** Pop a frame instance. */
        POP,
        /** Invoke a rule. */
        MODIFY, ;
    }
}
