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

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

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
        for (CtrlTransition trans : transitions) {
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
            this.successChild = new CtrlSchedule(remainder, failedCalls);
            if (myCount == 0) {
                this.failureChild = this.successChild;
            } else {
                Set<CtrlCall> newFailedCalls =
                    new HashSet<CtrlCall>(failedCalls);
                newFailedCalls.add(myTrans.label().getCall());
                this.failureChild = new CtrlSchedule(remainder, newFailedCalls);
            }
        }
    }

    /** Returns the currently scheduled transition.
     * May be {@code null} if this is the end of the schedule.
      */
    public CtrlTransition getTransition() {
        return this.trans;
    }

    /** Returns the next node of the schedule, given success or failure of the transition of this node. */
    public CtrlSchedule next(boolean success) {
        return success ? this.successChild : this.failureChild;
    }

    @Override
    public String toString() {
        return toString(0, "");
    }

    private String toString(int depth, String prefix) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            result.append("  ");
        }
        result.append(prefix);
        if (this.trans == null) {
            result.append("Finished\n");
        } else {
            result.append(String.format("Call %s%n", this.trans));
            if (this.successChild == this.failureChild) {
                result.append(this.successChild.toString(depth, "Next: "));
            } else {
                result.append(this.successChild.toString(depth + 1, "Success: "));
                result.append(this.failureChild.toString(depth + 1, "Failure: "));
            }
        }
        return result.toString();
    }

    /** The transition at this node of the schedule. */
    private CtrlTransition trans;
    /** Next schedule node in case {@link #trans} succeeds. */
    private CtrlSchedule successChild;
    /** Next schedule node in case {@link #trans} fails. */
    private CtrlSchedule failureChild;
}
