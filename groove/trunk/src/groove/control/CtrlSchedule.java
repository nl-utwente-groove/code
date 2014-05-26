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

import groove.control.instance.Assignment;
import groove.grammar.Recipe;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Sequence of control transitions to be tried out from a control state. */
public class CtrlSchedule implements CtrlFrame {
    /**
     * Constructs an untried schedule. 
     * @param state control state on which this schedule is based
     * @param trans transition to be tried at this point of the schedule; may be {@code null}
     * @param previous set of control transitions tried out before this schedule was reached
     * @param isTransient if {@code true}, the schedule is part of a transaction
     * @param success if {@code true}, this schedule represents a success state
     */
    public CtrlSchedule(CtrlState state, List<CtrlTransition> trans, Set<CtrlTransition> previous,
            boolean success, boolean isTransient) {
        this.state = state;
        this.transitions = trans;
        this.triedCalls = new HashSet<CtrlCall>();
        this.triedRules = new HashSet<String>();
        this.triedTransitions = new HashSet<CtrlTransition>();
        for (CtrlTransition t : previous) {
            CtrlCall call = t.getCall();
            this.triedCalls.add(call);
            this.triedRules.add(call.getRule().getFullName());
            this.triedTransitions.add(t);
        }
        this.success = success;
        assert !isTransient || state.isTransient();
        this.isTransient = isTransient;
    }

    @Override
    public boolean isStart() {
        return getPrime().isStart();
    }

    /** Indicates if this is the initial schedule of the control state. */
    public boolean isInitial() {
        return this == this.state.getSchedule();
    }

    /** Indicates if this node signals the end of the schedule. */
    @Override
    public boolean isDead() {
        return this.transitions == null && !isFinal();
    }

    /** Indicates if this node signals the end of the schedule. */
    @Override
    public boolean isTrial() {
        return this.transitions != null;
    }

    /** Indicates if this schedule represents a success state. */
    @Override
    public boolean isFinal() {
        return this.success;
    }

    @Override
    public int getDepth() {
        return isTransient() ? 1 : 0;
    }

    @Override
    public boolean isTransient() {
        return this.isTransient;
    }

    @Override
    public Recipe getRecipe() {
        return inRecipe() ? getPrime().getRecipe() : null;
    }

    @Override
    public boolean inRecipe() {
        return isTransient();
    }

    /** Returns the currently scheduled transition.
     * May be {@code null} if this is the end of the schedule.
      */
    public List<CtrlTransition> getTransitions() {
        return this.transitions;
    }

    /** Returns the control state to which this schedule belongs. */
    @Override
    public final CtrlState getPrime() {
        return this.state;
    }

    /**
     * Returns the set of control calls that have been tried at this point
     * of the schedule.
     * @return a set of tried control calls
     */
    public Set<CtrlCall> getTriedCalls() {
        return this.triedCalls;
    }

    /**
     * Returns the set of rules that have been tried at this point
     * of the schedule.
     * These are the rules occurring in {@link #getTriedCalls()}
     * @return a set of tried control calls, or {@code null} if {@link #isDead()} 
     * yields {@code false}.
     */
    public Set<String> getTriedRules() {
        return this.triedRules;
    }

    @Override
    public Set<? extends CalledAction> getPastAttempts() {
        return this.triedTransitions;
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
    public boolean hasVars() {
        return getPrime().hasVars();
    }

    @Override
    public List<CtrlVar> getVars() {
        return getPrime().getVars();
    }

    @Override
    public String toString() {
        return toString(0, "");
    }

    private String toString(int depth, String prefix) {
        StringBuilder result = new StringBuilder();
        StringBuilder spaces = new StringBuilder();
        for (int i = 0; i <= depth; i++) {
            spaces.append("    ");
        }
        result.append(spaces);
        result.append(prefix);
        if (this.transitions == null) {
            result.append("No transitions");
            if (isFinal()) {
                result.append("; success");
            }
            result.append("\n");
        } else {
            boolean first = true;
            for (CtrlTransition t : getTransitions()) {
                if (first) {
                    first = false;
                } else {
                    result.append('\n');
                    result.append(spaces);
                }
                result.append("Call ");
                result.append(t);
                if (t.getCallBinding().length > 0) {
                    result.append(", parameter binding: ");
                    result.append(Arrays.toString(t.getCallBinding()));
                }
                Assignment assign = t.getAssignment();
                if (assign.size() > 0) {
                    result.append(", target variable binding: ");
                    result.append(assign.toString());
                }
            }
            if (isFinal()) {
                result.append("; success");
            }
            if (isTransient()) {
                result.append("; transient");
            }
            result.append("\n");
            if (this.succNext == this.failNext) {
                if (!this.succNext.isDead()) {
                    result.append(this.succNext.toString(depth + 1, ""));
                }
            } else {
                if (!this.failNext.isDead()) {
                    result.append(this.failNext.toString(depth + 1, "Failed:  "));
                }
                if (!this.succNext.isDead()) {
                    result.append(this.succNext.toString(depth + 1, "Applied: "));
                }
            }
        }
        return result.toString();
    }

    /** The control state to which this schedule belongs. */
    private final CtrlState state;
    /** The transitions at this node of the schedule. */
    private final List<CtrlTransition> transitions;
    /** The set of recipes that have been tried when this point of the schedule is reached.
     */
    private final Set<CtrlTransition> triedTransitions;
    /** The set of calls that have been tried when this point of the schedule is reached.
     */
    private final Set<CtrlCall> triedCalls;
    /** The set of rules that have been tried when this point of the schedule is reached.
     */
    private final Set<String> triedRules;
    /** Next schedule node in case {@link #transitions} succeeds. */
    private CtrlSchedule succNext;
    /** Next schedule node in case {@link #transitions} fails. */
    private CtrlSchedule failNext;
    /** Flag indicating if this schedule represents a success state. */
    private final boolean success;
    /** 
     * Flag indicating if this schedule is part of a transaction.
     */
    private final boolean isTransient;
}
