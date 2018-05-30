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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import groove.control.Attempt;
import groove.control.Call;
import groove.control.CallStack;
import groove.control.CtrlVar;
import groove.control.template.Switch;
import groove.control.template.SwitchStack;
import groove.grammar.Callable;
import groove.grammar.Recipe;
import groove.grammar.Rule;

/**
 * Run-time control step, instantiating a control edge.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Step implements Attempt.Stage<Frame,Step>, Comparable<Step> {
    /**
     * Constructs a step from the given parameters.
     * @param source source frame for the step
     * @param newSwitches stack of new switches invoked from the source frame
     * @param onFinish target frame for the step
     */
    public Step(Frame source, SwitchStack newSwitches, Frame onFinish) {
        assert newSwitches.peek()
            .getUnit()
            .getKind() == Callable.Kind.RULE;
        this.stack = new SwitchStack();
        this.stack.addAll(source.getSwitchStack());
        this.stack.addAll(newSwitches);
        this.onFinish = onFinish;
        this.source = source;
    }

    /** Returns the source frame of this step. */
    public Frame getSource() {
        return this.source;
    }

    private final Frame source;

    @Override
    public Frame onFinish() {
        return this.onFinish;
    }

    private final Frame onFinish;

    /** Convenience method to return the top switch of this step. */
    public Switch getRuleSwitch() {
        return getSwitchStack().peek();
    }

    @Override
    public Call getRuleCall() {
        return getSwitchStack().getRuleCall();
    }

    @Override
    public int getTransience() {
        return getSwitchStack().getTransience() - getSource().getTransience();
    }

    /** Returns the number of levels by which the call stack depth changes from source
     * to target frame. */
    public int getCallDepthChange() {
        return onFinish().getSwitchStack()
            .size()
            - getSource().getSwitchStack()
                .size();
    }

    /** Returns the stack of switches in this step. */
    public final SwitchStack getSwitchStack() {
        return this.stack;
    }

    private SwitchStack stack;

    @Override
    public CallStack getCallStack() {
        return getSwitchStack().getCallStack();
    }

    /** Indicates if this step is part of an atomic block. */
    public boolean isPartial() {
        return getSource().isTransient() || onFinish().isTransient();
    }

    /** Indicates if this step is the initial step of a recipe. */
    public boolean isInitial() {
        // if a recipe step starts in a non-recipe frame, it must be
        // the initial step of a recipe
        return isInternal() && !getSource().isInternal();
    }

    /** Indicates if this step is part of a recipe.
     * @return {@code true} if and only if {@link #getRecipe()} is non-{@code null}
     * @see #getRecipe()
     */
    public boolean isInternal() {
        return getCallStack().inRecipe();
    }

    /**
     * Returns the outermost recipe of which this step is a part, if any.
     * @see #isInternal()
     */
    public Recipe getRecipe() {
        return getCallStack().getRecipe();
    }

    /** Convenience method to return the called rule of this step. */
    public final Rule getRule() {
        return getRuleCall().getRule();
    }

    /** Returns the mapping of output variables to argument positions of the called unit. */
    public Map<CtrlVar,Integer> getOutVars() {
        return getRuleCall().getOutVars();
    }

    /**
     * Indicates if the step may cause modifications in the control state.
     * This is the case if the (prime) source and target of this step differ,
     * or the call has out-parameters.
     */
    public boolean isModifying() {
        return getSource().getPrime() != onFinish() || getRuleCall().hasOutVars();
    }

    /**
     * Returns the stack push assignments necessary to prepare for the actual action
     * of this step.
     */
    public List<Assignment> getEnterAssignments() {
        if (this.enters == null) {
            this.enters = computeEnterAssignments();
        }
        return this.enters;
    }

    private List<Assignment> enters;

    private List<Assignment> computeEnterAssignments() {
        List<Assignment> result = new ArrayList<>();
        // add push actions for every successive call on the
        // stack of entered calls
        for (int i = getSource().getSwitchStack()
            .size(); i < getSwitchStack().size() - 1; i++) {
            result.add(Assignment.enter(getSwitchStack().get(i)));
        }
        return result;
    }

    /**
     * Returns the list of frame value assignments involved in applying this step.
     * These consist pushes due to fresh
     * procedure calls, followed by the action of this step, followed by pops due to
     * procedures explicitly exited by this step.
     */
    public List<Assignment> getApplyAssignments() {
        if (this.applyAssignments == null) {
            this.applyAssignments = computeApplyAssignments();
        }
        return this.applyAssignments;
    }

    private List<Assignment> applyAssignments;

    private List<Assignment> computeApplyAssignments() {
        List<Assignment> result = computeEnterAssignments();
        SwitchStack stack = getSwitchStack();
        result.add(Assignment.modify(stack.peek()));
        // add pop actions for the calls that are finished
        for (int i = stack.size() - 2; i >= onFinish().getNestingDepth(); i--) {
            result.add(Assignment.exit(stack.get(i + 1)
                .onFinish(), stack.get(i)));
        }
        return result;
    }

    @Override
    public int compareTo(Step other) {
        int result = getSource().getNumber() - other.getSource()
            .getNumber();
        if (result != 0) {
            return result;
        }
        result = getSwitchStack().compareTo(other.getSwitchStack());
        if (result != 0) {
            return result;
        }
        result = onFinish().getNumber() - other.onFinish()
            .getNumber();
        return result;
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }

    @Override
    public boolean equals(Object other) {
        return this == other;
    }

    @Override
    public String toString() {
        return "Step " + this.source + "--" + this.stack + "-> " + this.onFinish;
    }
}
