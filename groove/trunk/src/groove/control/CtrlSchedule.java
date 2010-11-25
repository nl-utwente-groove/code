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
package groove.control;

import java.util.Arrays;
import java.util.Set;

/** Sequence of control transitions to be tried out from a control state. */
public class CtrlSchedule {
    /** Constructs an initially empty schedule. */
    public CtrlSchedule(CtrlTransition trans, Set<CtrlCall> triedCalls) {
        this.trans = trans;
        this.triedCalls = trans == null ? triedCalls : null;
    }

    /** Indicates if this node signals the end of the schedule. */
    public boolean isFinished() {
        return this.trans == null;
    }

    /** Returns the currently scheduled transition.
     * May be {@code null} if this is the end of the schedule.
      */
    public CtrlTransition getTransition() {
        return this.trans;
    }

    /** Returns the set of control calls that has been tried at this point
     * of the schedule, provided the schedule is finished
     * @return a set of tried control calls, or {@code null} if {@link #isFinished()} 
     * yields {@code false}.
     */
    public Set<CtrlCall> triedCalls() {
        return this.triedCalls;
    }

    /** Sets the success and failure schedules. */
    public void setNext(CtrlSchedule success, CtrlSchedule failure) {
        this.success = success;
        this.failure = failure;
    }

    /** Returns the next node of the schedule, given success or failure of the transition of this node. */
    public CtrlSchedule next(boolean success) {
        return success ? this.success : this.failure;
    }

    @Override
    public String toString() {
        return toString(0, "");
    }

    private String toString(int depth, String prefix) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i <= depth; i++) {
            result.append("  ");
        }
        result.append(prefix);
        if (this.trans == null) {
            result.append("No transitions\n");
        } else {
            result.append("Call ");
            result.append(this.trans);
            if (this.trans.getInVarBinding().length > 0) {
                result.append(", in-parameter binding: ");
                result.append(Arrays.toString(this.trans.getInVarBinding()));
            }
            if (this.trans.getTargetVarBinding().length > 0) {
                result.append(", target variable binding: ");
                result.append(Arrays.toString(this.trans.getTargetVarBinding()));
            }
            result.append("\n");
            if (this.success == this.failure) {
                if (!this.success.isFinished()) {
                    result.append(this.success.toString(depth + 1, ""));
                }
            } else {
                if (!this.success.isFinished()) {
                    result.append(this.success.toString(depth + 1, "Success: "));
                }
                if (!this.failure.isFinished()) {
                    result.append(this.failure.toString(depth + 1, "Failure: "));
                }
            }
        }
        return result.toString();
    }

    /** The transition at this node of the schedule. */
    private final CtrlTransition trans;
    /** The set of calls that have been tried when this point of the schedule is reached.
     * Only filled in if {@link #isFinished()} is satisfied.
     */
    private final Set<CtrlCall> triedCalls;
    /** Next schedule node in case {@link #trans} succeeds. */
    private CtrlSchedule success;
    /** Next schedule node in case {@link #trans} fails. */
    private CtrlSchedule failure;
}
