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

import groove.control.Binding;
import groove.control.Binding.Source;
import groove.control.Call;
import groove.control.Callable;
import groove.control.CtrlPar;
import groove.control.CtrlPar.Const;
import groove.control.CtrlPar.Var;
import groove.control.CtrlStep;
import groove.control.CtrlTransition;
import groove.control.CtrlVar;
import groove.control.Procedure;
import groove.control.SoloAttempt;
import groove.control.template.Switch;
import groove.grammar.Recipe;
import groove.grammar.Rule;
import groove.graph.AEdge;
import groove.graph.Edge;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Run-time control step, instantiating a control edge.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Step extends AEdge<Frame,Switch> implements SoloAttempt<Frame>, CtrlStep {
    /**
     * Constructs a step from the given parameters.
     */
    public Step(Frame source, Switch swit, Frame onFinish, Frame onSuccess, Frame onFailure) {
        super(source, swit, onFinish);
        assert onFinish.testNormal();
        assert onSuccess.testNormal();
        assert onFailure.testNormal();
        this.onFailure = onFailure;
        this.onSuccess = onSuccess;
    }

    /** Convenience method to return the switch of this step. */
    public Switch getSwitch() {
        return label();
    }

    @Override
    public Call getCall() {
        return label().getCall();
    }

    @Override
    public Frame onFinish() {
        return target();
    }

    @Override
    public Frame onSuccess() {
        return this.onSuccess;
    }

    @Override
    public Frame onFailure() {
        return this.onFailure;
    }

    @Override
    public boolean sameVerdict() {
        return onFailure() == onSuccess();
    }

    private final Frame onSuccess;
    private final Frame onFailure;

    /** Returns the number of levels by which the call stack depth changes. */
    public int getCallDepth() {
        return getCallStack().size() - source().getCallStack().size();
    }

    /** Convenience method to return the call stack of the switch of this step. */
    public final CallStack getCallStack() {
        return getSwitch().getCallStack();
    }

    @Override
    public boolean isPartial() {
        return getSwitch().isPartial();
    }

    @Override
    public Recipe getRecipe() {
        return getSwitch().getRecipe();
    }

    /** Convenience method to return called unit of this step. */
    public final Callable getUnit() {
        return getCall().getUnit();
    }

    /** Convenience method to return called rule of this step. */
    @Override
    public final Rule getRule() {
        return getCall().getRule();
    }

    @Override
    public Map<CtrlVar,Integer> getOutVars() {
        return getCall().getOutVars();
    }

    /**
     * Indicates if this step assigns a new value to
     * a given target variable position.
     */
    public boolean mayAssign(int targetVar) {
        if (getCallDepth() != 0) {
            return true;
        }
        // the target frame has the same depth as the source frame
        Assignment lastAction = getFrameChanges().get(getFrameChanges().size() - 1);
        return lastAction.getBinding(targetVar).getSource() != Source.VAR;
    }

    @Override
    public boolean isModifying() {
        return source().getPrime() != target() || getCall().hasOutVars();
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
        for (int i = source().getCallStack().size(); i < getCallStack().size(); i++) {
            Switch swit = getCallStack().get(i);
            result.add(enter(swit));
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
        result.add(Assignment.modify(this));
        // add pop actions for the calls that are finished
        for (int i = getCallStack().size() - 1; i >= onFinish().getCallStack().size(); i--) {
            Switch swit = getCallStack().get(i);
            result.add(exit(swit));
        }
        return result;
    }

    /**
     * Computes the variable assignment for the initial location of a called template
     * from the variables of the caller location and the arguments of the call.
     * @param swit the template call
     */
    private Assignment enter(Switch swit) {
        assert swit.getKind().isProcedure();
        List<Binding> result = new ArrayList<Binding>();
        List<CtrlVar> sourceVars = swit.source().getVars();
        Map<CtrlVar,Integer> sig = ((Procedure) swit.getUnit()).getInPars();
        for (CtrlVar var : swit.target().getVars()) {
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
        Map<CtrlVar,Integer> callerVars = swit.source().getVarIxMap();
        Map<CtrlVar,Integer> outVars = swit.getCall().getOutVars();
        Map<CtrlVar,Integer> finalVars =
            ((Procedure) swit.getUnit()).getTemplate().getFinal().getVarIxMap();
        for (CtrlVar var : swit.target().getVars()) {
            Integer ix = outVars.get(var);
            Binding rhs;
            if (ix == null) {
                // the value comes from the caller
                rhs = Binding.caller(callerVars.get(var));
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
        return source().compareTo(other.source());
    }

    @Override
    protected int computeHashCode() {
        return System.identityHashCode(this);
    }

    @Override
    protected boolean isLabelEqual(Edge other) {
        return this == other;
    }

    /** Constructs an artificial step reflecting a verdict of a base step. */
    static Step newStep(Step base, boolean success) {
        Frame source = base.source();
        Frame target = success ? base.onSuccess() : base.onFailure();
        Switch swit = new Switch(source.getLocation(), target.getLocation(), success);
        return new Step(source, swit, target, base.onSuccess(), base.onFailure());
    }
}
