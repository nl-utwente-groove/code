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
import groove.control.template.Location;
import groove.control.template.Switch;
import groove.grammar.Recipe;
import groove.graph.AEdge;
import groove.graph.Edge;

import java.util.ArrayList;
import java.util.LinkedHashMap;
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
    public Step(Frame source, Switch edge, CallStack callStack, Frame onFinish, Frame onSuccess,
            Frame onFailure) {
        super(source, edge, onFinish);
        assert onFinish.testNormal();
        assert onSuccess.testNormal();
        assert onFailure.testNormal();
        this.callStack = new CallStack(callStack);
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

    /** Returns the call stack for this step. */
    public List<Switch> getCallStack() {
        return this.callStack;
    }

    private final CallStack callStack;

    /** Returns the number of levels by which the call stack depth changes. */
    public int getCallDepth() {
        return getCallStack().size() - source().getCallStack().size();
    }

    @Override
    public boolean isModifying() {
        return source().getPrime() != target() || getCall().hasOutVars();
    }

    /** Returns the actions associated with this step. */
    public List<StepAction> getPreActions() {
        if (this.preActions == null) {
            this.preActions = computePreActions();
        }
        return this.preActions;
    }

    private List<StepAction> preActions;

    /** Returns the actions associated with this step. */
    public List<StepAction> getFinishActions() {
        if (this.finishActions == null) {
            this.finishActions = computeFinishActions();
        }
        return this.finishActions;
    }

    private List<StepAction> finishActions;

    /** Returns the actions associated with this step. */
    public List<StepAction> getFailureActions() {
        if (this.failureActions == null) {
            this.failureActions = computeVerdictActions(onFailure());
        }
        return this.failureActions;
    }

    private List<StepAction> failureActions;

    /** Returns the actions associated with this step. */
    public List<StepAction> getSuccessActions() {
        if (this.successActions == null) {
            this.successActions = computeVerdictActions(onSuccess());
        }
        return this.successActions;
    }

    private List<StepAction> successActions;

    private List<StepAction> computePreActions() {
        List<StepAction> result = new ArrayList<StepAction>();
        // add pop actions for every successive call on the
        // stack of entered calls
        for (int i = source().getCallStack().size(); i < getCallStack().size(); i++) {
            Switch swit = getCallStack().get(i);
            result.add(StepAction.push(enter(swit)));
        }
        return result;
    }

    private List<StepAction> computeFinishActions() {
        List<StepAction> result = new ArrayList<StepAction>();
        result.add(StepAction.modify(rule(label())));
        result.addAll(computePostActions(onFinish()));
        return result;
    }

    private List<StepAction> computeVerdictActions(Frame target) {
        List<StepAction> result = new ArrayList<StepAction>();
        result.add(StepAction.modify(verdict(source().getLocation(), target.getLocation())));
        result.addAll(computePostActions(target));
        return result;
    }

    private List<StepAction> computePostActions(Frame target) {
        List<StepAction> result = new ArrayList<StepAction>();
        // add pop actions for the calls that are finished
        for (int i = getCallStack().size() - 1; i >= target.getCallStack().size(); i--) {
            Switch swit = getCallStack().get(i);
            result.add(StepAction.pop(exit(swit)));
        }
        return result;
    }

    /**
     * Computes the variable assignment for the initial location of a called template
     * from the variables of the caller location and the arguments of the call.
     * @param call the template call
     */
    private Map<CtrlVar,Binding> enter(Switch call) {
        assert call.getKind().isProcedure();
        Map<CtrlVar,Binding> result = new LinkedHashMap<CtrlVar,Binding>();
        Map<CtrlVar,Integer> sourceVars = call.source().getVarIxMap();
        Map<CtrlVar,Integer> sig = ((Procedure) call.getUnit()).getInPars();
        for (CtrlVar var : call.target().getVars()) {
            // all initial state variables are formal input parameters 
            Integer ix = sig.get(var);
            assert ix != null;
            // look up the corresponding argument in the call
            CtrlPar arg = call.getArgs().get(ix);
            Binding rhs;
            if (arg instanceof Const) {
                rhs = Binding.value((Const) arg);
            } else {
                rhs = Binding.var(sourceVars.get(((Var) arg).getVar()));
            }
            result.put(var, rhs);
        }
        return result;
    }

    /**
     * Computes the variable assignment for the location where control returns 
     * after a template call, from the variables in the final state of the 
     * template and the source state of the call.
     * @param swit the template call
     */
    private Map<CtrlVar,Binding> exit(Switch swit) {
        assert swit.getKind().isProcedure();
        Map<CtrlVar,Binding> result = new LinkedHashMap<CtrlVar,Binding>();
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
                ix = outVars.get(var);
                assert ix != null;
                // find the corresponding formal parameter
                CtrlVar par = sig.get(ix).getVar();
                // look it up in the final location variables
                rhs = Binding.var(finalVars.get(par));
            }
            assert rhs != null;
            result.put(var, rhs);
        }
        return result;
    }

    /** Computes the variable assignment for the target location of
     * a rule call, using the variables of the source location
     * combined with the output parameters of the call.
     */
    private Map<CtrlVar,Binding> rule(Switch swit) {
        assert !swit.getKind().isProcedure();
        Map<CtrlVar,Binding> result = new LinkedHashMap<CtrlVar,Binding>();
        Map<CtrlVar,Integer> sourceVars = swit.source().getVarIxMap();
        Map<CtrlVar,Integer> outVars = swit.getCall().getOutVars();
        for (CtrlVar var : swit.target().getVars()) {
            Integer ix = outVars.get(var);
            Binding rhs;
            if (ix == null) {
                // the value comes from the source
                ix = sourceVars.get(var);
                rhs = Binding.var(ix);
            } else {
                rhs = Binding.out(ix);
            }
            result.put(var, rhs);
        }
        return result;
    }

    /** Computes the variable assignment for the target location of
     * a verdict, using the variables of the source location.
     */
    private Map<CtrlVar,Binding> verdict(Location source, Location target) {
        Map<CtrlVar,Binding> result = new LinkedHashMap<CtrlVar,Binding>();
        Map<CtrlVar,Integer> sourceVars = source.getVarIxMap();
        for (CtrlVar var : target.getVars()) {
            // the value comes from the source
            int ix = sourceVars.get(var);
            Binding rhs = Binding.var(ix);
            result.put(var, rhs);
        }
        return result;
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
    public boolean isPartial() {
        return getRecipe() != null;
    }

    @Override
    public Recipe getRecipe() {
        if (!this.recipeInit) {
            for (Switch swit : getCallStack()) {
                Callable unit = swit.getCall().getUnit();
                if (unit instanceof Recipe) {
                    this.recipe = (Recipe) unit;
                    break;
                }
            }
            this.recipeInit = true;
        }
        return this.recipe;
    }

    private Recipe recipe;
    private boolean recipeInit;

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
        return new Step(source, swit, new CallStack(), target, base.onSuccess(), base.onFailure());
    }
}
