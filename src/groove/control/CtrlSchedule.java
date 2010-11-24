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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/** Sequence of control transitions to be tried out from a control state. */
public class CtrlSchedule {
    /** Constructs a schedule from a set of transitions to be scheduled, plus a set of
     * rule calls that have been tried and failed.
     * @param transitions the transitions that have to be scheduled
     * @param failedCalls the calls that have been tried and failed
     */
    public CtrlSchedule(Set<CtrlTransition> transitions,
            Set<CtrlCall> failedCalls) {
        // look for the transition that appears in the fewest guards
        Map<CtrlCall,Integer> guardCount = new HashMap<CtrlCall,Integer>();
        for (CtrlTransition trans : transitions) {
            for (CtrlCall call : trans.label().getGuard()) {
                int count =
                    guardCount.containsKey(call) ? guardCount.get(call) + 1 : 1;
                guardCount.put(call, count);
            }
        }
        CtrlTransition myTrans = null;
        int myCount = Integer.MAX_VALUE;
        for (CtrlTransition trans : new TreeSet<CtrlTransition>(transitions)) {
            CtrlCall call = trans.label().getCall();
            int count = guardCount.containsKey(call) ? guardCount.get(call) : 0;
            if (failedCalls.containsAll(trans.label().getGuard())
                && count < myCount) {
                myTrans = trans;
                myCount = count;
                if (myCount == 0) {
                    break;
                }
            }
        }
        if (myTrans != null) {
            this.trans = myTrans;
            Set<CtrlTransition> remainder =
                new LinkedHashSet<CtrlTransition>(transitions);
            remainder.remove(myTrans);
            this.success = new CtrlSchedule(remainder, failedCalls);
            if (myCount == 0) {
                this.failure = this.success;
            } else {
                Set<CtrlCall> newFailedCalls =
                    new HashSet<CtrlCall>(failedCalls);
                newFailedCalls.add(myTrans.label().getCall());
                this.failure = new CtrlSchedule(remainder, newFailedCalls);
            }
        }
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
    private CtrlTransition trans;
    /** Next schedule node in case {@link #trans} succeeds. */
    private CtrlSchedule success;
    /** Next schedule node in case {@link #trans} fails. */
    private CtrlSchedule failure;
}
