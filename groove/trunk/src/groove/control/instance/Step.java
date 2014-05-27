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

import groove.control.Attempt;
import groove.control.Binding.Source;
import groove.control.Call;
import groove.control.CallStack;
import groove.control.CtrlStep;
import groove.control.CtrlTransition;
import groove.control.CtrlVar;
import groove.control.template.Switch;
import groove.control.template.Switch.Kind;
import groove.control.template.SwitchStack;
import groove.grammar.Recipe;
import groove.grammar.Rule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Run-time control step, instantiating a control edge.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Step implements Attempt.Stage<Frame,Step>, CtrlStep {
    /**
     * Constructs a step from the given parameters.
     * @param source source frame for the step
     * @param newSwitches stack of new switches invoked from the source frame
     * @param onFinish target frame for the step
     */
    public Step(Frame source, SwitchStack newSwitches, Frame onFinish) {
        assert newSwitches.peek().getUnit().getKind() == Kind.RULE;
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
    public Frame target() {
        return onFinish();
    }

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
    public int getDepth() {
        return getSwitchStack().getDepth() - getSource().getDepth();
    }

    /** Returns the number of levels by which the call stack depth changes from source
     * to target frame. */
    public int getCallDepthChange() {
        return onFinish().getSwitchStack().size() - getSource().getSwitchStack().size();
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

    @Override
    public boolean isPartial() {
        return getSource().isTransient() || onFinish().isTransient();
    }

    @Override
    public boolean isInitial() {
        // if a recipe step starts in a non-recipe frame, it must be
        // the initial step of a recipe
        return inRecipe() && !getSource().inRecipe();
    }

    @Override
    public boolean inRecipe() {
        return getCallStack().inRecipe();
    }

    @Override
    public Recipe getRecipe() {
        return getCallStack().getRecipe();
    }

    /** Convenience method to return called rule of this step. */
    @Override
    public final Rule getRule() {
        return getRuleCall().getRule();
    }

    @Override
    public Map<CtrlVar,Integer> getOutVars() {
        return getRuleCall().getOutVars();
    }

    /**
     * Indicates if this step assigns a new value to
     * a given target variable position.
     */
    public boolean mayAssign(int targetVar) {
        if (getCallDepthChange() != 0) {
            return true;
        }
        // the target frame has the same depth as the source frame
        Assignment lastAction = getFrameChanges().get(getFrameChanges().size() - 1);
        return lastAction.getBinding(targetVar).getSource() != Source.VAR;
    }

    @Override
    public boolean isModifying() {
        return getSource().getPrime() != onFinish() || getRuleCall().hasOutVars();
    }

    /** Returns the push actions associated with this step. */
    public List<Assignment> getFramePushes() {
        if (this.pushes == null) {
            this.pushes = computeFramePushes();
        }
        return this.pushes;
    }

    private List<Assignment> pushes;

    private List<Assignment> computeFramePushes() {
        List<Assignment> result = new ArrayList<Assignment>();
        // add pop actions for every successive call on the
        // stack of entered calls
        for (int i = getSource().getSwitchStack().size(); i < getSwitchStack().size() - 1; i++) {
            result.add(Assignment.enter(getSwitchStack().get(i)));
        }
        return result;
    }

    @Override
    public List<Assignment> getFrameChanges() {
        if (this.changes == null) {
            this.changes = computeFrameChanges();
        }
        return this.changes;
    }

    private List<Assignment> changes;

    private List<Assignment> computeFrameChanges() {
        List<Assignment> result = computeFramePushes();
        result.add(Assignment.modify(getSwitchStack().peek()));
        // add pop actions for the calls that are finished
        for (int i = getSwitchStack().size() - 2; i >= onFinish().getSwitchStack().size(); i--) {
            result.add(Assignment.exit(getSwitchStack().get(i)));
        }
        return result;
    }

    @Override
    public int compareTo(CtrlStep o) {
        if (o instanceof CtrlTransition) {
            return -1;
        }
        Step other = (Step) o;
        int result = getSource().getNumber() - other.getSource().getNumber();
        if (result != 0) {
            return result;
        }
        result = getSwitchStack().compareTo(other.getSwitchStack());
        if (result != 0) {
            return result;
        }
        result = onFinish().getNumber() - other.onFinish().getNumber();
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
}
