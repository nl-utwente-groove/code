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

import groove.trans.Rule;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/** Sequence of control transitions to be tried out from a control state. */
public class CtrlSchedule {
    /**
     * Constructs an untried schedule. 
     * @param state control state on which this schedule is based
     * @param trans transition to be tried at this point of the schedule; may be {@code null}
     * @param previous set of control transitions tried out before this schedule was reached
     * @param isTransient if {@code true}, the schedule is part of a transaction
     * @param success if {@code true}, this schedule represents a success state
     */
    public CtrlSchedule(CtrlState state, CtrlTransition trans,
            Set<CtrlTransition> previous, boolean success, boolean isTransient) {
        this.state = state;
        this.trans = trans;
        this.previousCalls = null;//new HashSet<CtrlCall>();
        this.previousRules = new HashSet<Rule>();
        for (CtrlTransition triedTrans : previous) {
            CtrlCall call = triedTrans.getCall();
            //this.previousCalls.add(call);
            this.previousRules.add(call.getRule());
        }
        this.success = success;
        assert !isTransient || state.isTransient();
        this.isTransient = isTransient;
        this.tried = false;
    }

    /**
     * Constructs a tried version of a given schedule.
     */
    private CtrlSchedule(CtrlSchedule origin) {
        this.state = origin.state;
        this.trans = origin.trans;
        CtrlCall call = this.trans.getCall();
        this.previousCalls = new HashSet<CtrlCall>(origin.previousCalls);
        this.previousCalls.add(call);
        this.previousRules = new HashSet<Rule>(origin.previousRules);
        this.previousRules.add(call.getRule());
        this.success = origin.success;
        this.isTransient = origin.isTransient;
        this.tried = true;
    }

    /** Indicates if this node signals the end of the schedule. */
    public boolean isFinished() {
        return this.trans == null;
    }

    /** Indicates if this schedule represents a success state. */
    public boolean isSuccess() {
        return this.success;
    }

    /**
     * Indicates if this schedule is part of a transaction.
     */
    public boolean isTransient() {
        return this.isTransient;
    }

    /**
     * Indicates if {@link #getTransition()} has already been tried out,
     * but the result has not yet been registered.
     */
    public boolean isTried() {
        return this.tried;
    }

    /** Returns a tried version of this schedule. */
    public CtrlSchedule getTriedSchedule() {
        assert !isTried();
        if (this.triedSchedule == null) {
            this.triedSchedule = new CtrlSchedule(this);
        }
        return this.triedSchedule;
    }

    /** Returns the currently scheduled transition.
     * May be {@code null} if this is the end of the schedule.
      */
    public CtrlTransition getTransition() {
        return this.trans;
    }

    /** Returns the control state to which this schedule belongs. */
    public final CtrlState getState() {
        return this.state;
    }

    /**
     * Returns the set of control calls that have been tried at this point
     * of the schedule.
     * @return a set of tried control calls
     */
    public Set<CtrlCall> getPreviousCalls() {
        return this.previousCalls;
    }

    /**
     * Returns the set of rules that have been tried at this point
     * of the schedule.
     * These are the rules occurring in {@link #getPreviousCalls()}
     * @return a set of tried control calls, or {@code null} if {@link #isFinished()} 
     * yields {@code false}.
     */
    public Set<Rule> getPreviousRules() {
        return this.previousRules;
    }

    /** Sets the success and failure schedules. */
    public void setNext(CtrlSchedule success, CtrlSchedule failure) {
        this.succNext = success;
        this.failNext = failure;
    }

    /** Returns the next node of the schedule, given success or failure of the transition of this node. */
    public CtrlSchedule next(boolean success) {
        return success ? this.succNext : this.failNext;
    }

    @Override
    public String toString() {
        return toString(0, "");
    }

    private String toString(int depth, String prefix) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i <= depth; i++) {
            result.append("    ");
        }
        result.append(prefix);
        if (this.trans == null) {
            result.append("No transitions");
            if (isSuccess()) {
                result.append("; success");
            }
            result.append("\n");
        } else {
            result.append("Call ");
            result.append(this.trans);
            if (this.trans.getParBinding().length > 0) {
                result.append(", parameter binding: ");
                result.append(Arrays.toString(this.trans.getParBinding()));
            }
            if (this.trans.getTargetVarBinding().length > 0) {
                result.append(", target variable binding: ");
                result.append(Arrays.toString(this.trans.getTargetVarBinding()));
            }
            if (isSuccess()) {
                result.append("; success");
            }
            if (isTransient()) {
                result.append("; transient");
            }
            result.append("\n");
            if (this.succNext == this.failNext) {
                if (!this.succNext.isFinished() || this.succNext.isSuccess()) {
                    result.append(this.succNext.toString(depth + 1, ""));
                }
            } else {
                if (!this.failNext.isFinished() || this.failNext.isSuccess()) {
                    result.append(this.failNext.toString(depth + 1, "Failed:  "));
                }
                if (!this.succNext.isFinished() || this.succNext.isSuccess()) {
                    result.append(this.succNext.toString(depth + 1, "Applied: "));
                }
            }
        }
        return result.toString();
    }

    /** The control state to which this schedule belongs. */
    private final CtrlState state;
    /** The transition at this node of the schedule. */
    private final CtrlTransition trans;
    /** The set of calls that have been tried when this point of the schedule is reached.
     */
    private final Set<CtrlCall> previousCalls;
    /** The set of rules that have been tried when this point of the schedule is reached.
     */
    private final Set<Rule> previousRules;
    /** 
     * Flag indicating that {@link #trans} was already tried out (but the result
     * has not yet been registered).
     */
    private final boolean tried;
    /** Next schedule node in case {@link #trans} succeeds. */
    private CtrlSchedule succNext;
    /** Next schedule node in case {@link #trans} fails. */
    private CtrlSchedule failNext;
    /** Flag indicating if this schedule represents a success state. */
    private final boolean success;
    /** 
     * Flag indicating if this schedule is part of a transaction.
     */
    private final boolean isTransient;
    /** Tried version of this schedule (if this schedule is untried). */
    private CtrlSchedule triedSchedule;
}
