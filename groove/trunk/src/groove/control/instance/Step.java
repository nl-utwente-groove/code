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
import groove.control.Binding;
import groove.control.Binding.Source;
import groove.control.Call;
import groove.control.CallStack;
import groove.control.CtrlPar;
import groove.control.CtrlPar.Const;
import groove.control.CtrlPar.Var;
import groove.control.CtrlStep;
import groove.control.CtrlTransition;
import groove.control.CtrlVar;
import groove.control.Procedure;
import groove.control.template.Switch;
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
            result.add(enter(getSwitchStack().get(i)));
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
        result.add(modify(getSwitchStack().peek()));
        // add pop actions for the calls that are finished
        for (int i = getSwitchStack().size() - 2; i >= onFinish().getSwitchStack().size(); i--) {
            result.add(exit(getSwitchStack().get(i)));
        }
        return result;
    }

    /**
     * Returns bindings for a list of target variables of a
     * control step, using the source variables
     * combined with the output parameters of the call.
     */
    private Assignment modify(Switch swit) {
        List<Binding> result = new ArrayList<Binding>();
        List<CtrlVar> sourceVars = swit.getSourceVars();
        Map<CtrlVar,Integer> outVars = swit.getCall().getOutVars();
        for (CtrlVar var : swit.onFinish().getVars()) {
            Integer ix = outVars.get(var);
            Binding rhs;
            if (ix == null) {
                // the value comes from the source
                int pos = sourceVars.indexOf(var);
                assert pos >= 0;
                rhs = Binding.var(pos);
            } else {
                // the value is an output parameter of the rule
                Rule rule = getCallStack().getRule();
                rhs = rule.getParBinding(ix);
            }
            result.add(rhs);
        }
        return Assignment.call(result);
    }

    /**
     * Computes the variable assignment for the initial location of a called template
     * from the variables of the caller location and the arguments of the call.
     * @param swit the template call
     */
    private Assignment enter(Switch swit) {
        assert swit.getKind().isProcedure();
        List<Binding> result = new ArrayList<Binding>();
        List<CtrlVar> sourceVars = swit.getSourceVars();
        Procedure proc = (Procedure) swit.getUnit();
        Map<CtrlVar,Integer> sig = proc.getInPars();
        for (CtrlVar var : proc.getTemplate().getStart().getVars()) {
            // all initial state variables are formal input parameters 
            Integer ix = sig.get(var);
            assert ix != null;
            // look up the corresponding argument in the call
            CtrlPar arg = swit.getArgs().get(ix);
            Binding rhs;
            if (arg instanceof Const) {
                rhs = Binding.value((Const) arg);
            } else {
                rhs = Binding.var(sourceVars.indexOf(((Var) arg).getVar()));
            }
            result.add(rhs);
        }
        return Assignment.push(result);
    }

    /**
     * Computes the variable assignment for the location where control returns 
     * after a template call, from the variables in the final state of the 
     * template and the source state of the call.
     * @param swit the template call
     */
    private Assignment exit(Switch swit) {
        assert swit.getKind().isProcedure();
        List<Binding> result = new ArrayList<Binding>();
        List<CtrlPar.Var> sig = swit.getUnit().getSignature();
        List<CtrlVar> callerVars = swit.getSourceVars();
        Map<CtrlVar,Integer> outVars = swit.getCall().getOutVars();
        Map<CtrlVar,Integer> finalVars =
            ((Procedure) swit.getUnit()).getTemplate().getFinal().getVarIxMap();
        for (CtrlVar var : swit.onFinish().getVars()) {
            Integer ix = outVars.get(var);
            Binding rhs;
            if (ix == null) {
                // the value comes from the caller
                rhs = Binding.caller(callerVars.indexOf(var));
            } else {
                // the value comes from an output parameter of the call
                // find the corresponding formal parameter
                CtrlVar par = sig.get(ix).getVar();
                // look it up in the final location variables
                rhs = Binding.var(finalVars.get(par));
            }
            assert rhs != null;
            result.add(rhs);
        }
        return Assignment.pop(result);
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
