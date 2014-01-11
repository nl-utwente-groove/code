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
package groove.control;

import groove.control.CtrlPar.Const;
import groove.control.CtrlPar.Var;
import groove.grammar.Rule;
import groove.graph.AEdge;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Run-time control step, instantiating a control edge.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Step extends AEdge<Frame,CtrlEdge> {
    /**
     * Instantiates a given control edge, from a source to a target frame.
     * @param entered the control units that are entered by this step
     * @param exits the number of control units that are exited by this step
     */
    public Step(CtrlEdge edge, Frame source, Frame target,
            List<CtrlEdge> entered, int exits) {
        super(source, edge, target);
        assert target.getDepth() == source.getDepth() + entered.size() - exits;
        this.entered = entered;
        this.exits = exits;
    }

    private final List<CtrlEdge> entered;

    private final int exits;

    /** Returns the number of levels by which the frame stack depth changes. */
    public int getCallDepth() {
        return this.entered.size() - this.exits;
    }

    /** Returns the actions associated with this step. */
    public List<StepAction> getActions() {
        if (this.actions == null) {
            this.actions = computeActions();
        }
        return this.actions;
    }

    private List<StepAction> actions;

    private List<StepAction> computeActions() {
        List<StepAction> result = new ArrayList<StepAction>();
        // add pop actions for every successive call on the
        // stack of entered calls
        for (CtrlEdge call : this.entered) {
            result.add(StepAction.push(enter(call)));
        }
        result.add(StepAction.modify(rule(label())));
        // add pop actions for the calls that are finished
        for (int down = 0; down < this.exits; down++) {
            CtrlEdge call;
            int depth = this.entered.size() - down - 1;
            if (depth >= 0) {
                call = this.entered.get(depth);
            } else {
                call = this.source.getAncestor(-depth).getCall();
            }
            result.add(StepAction.pop(exit(call)));
        }
        return result;
    }

    /**
     * Computes the variable assignment for the initial location of a called template
     * from the variables of the caller location and the arguments of the call.
     * @param call the template call
     */
    private Map<CtrlVar,AssignSource> enter(CtrlEdge call) {
        assert call.getKind().hasBody();
        Map<CtrlVar,AssignSource> result =
            new LinkedHashMap<CtrlVar,AssignSource>();
        Map<CtrlVar,Integer> sourceVars = call.source().getVarIxMap();
        Map<CtrlVar,Integer> sig = ((CtrlUnit) call.getUnit()).getParIxMap();
        for (CtrlVar var : call.target().getVars()) {
            // all initial state variables are formal input parameters 
            Integer ix = sig.get(var);
            assert ix != null;
            // look up the corresponding argument in the call
            CtrlPar arg = call.getArgs().get(ix);
            AssignSource rhs;
            if (arg instanceof Const) {
                rhs = AssignSource.value((Const) arg);
            } else {
                rhs = AssignSource.var(sourceVars.get(((Var) arg).getVar()));
            }
            result.put(var, rhs);
        }
        return result;
    }

    /**
     * Computes the variable assignment for the location where control returns 
     * after a template call, from the variables in the final state of the 
     * template and the source state of the call.
     * @param call the template call
     */
    private Map<CtrlVar,AssignSource> exit(CtrlEdge call) {
        assert call.getKind().hasBody();
        Map<CtrlVar,AssignSource> result =
            new LinkedHashMap<CtrlVar,AssignSource>();
        List<CtrlPar.Var> sig = call.getUnit().getSignature();
        Map<CtrlVar,Integer> callerVars = call.source().getVarIxMap();
        Map<CtrlVar,Integer> finalVars =
            ((CtrlUnit) call.getUnit()).getTemplate().getSingleFinal().getVarIxMap();
        for (CtrlVar var : call.target().getVars()) {
            Integer ix = call.getOutVars().get(var);
            AssignSource rhs;
            if (ix == null) {
                // the value comes from the caller
                rhs = AssignSource.caller(callerVars.get(var));
            } else {
                // the value comes from an output parameter of the call
                ix = call.getOutVars().get(var);
                assert ix != null;
                // find the corresponding formal parameter
                CtrlVar par = sig.get(ix).getVar();
                // look it up in the final location variables
                rhs = AssignSource.var(finalVars.get(par));
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
    private Map<CtrlVar,AssignSource> rule(CtrlEdge call) {
        assert !call.getKind().hasBody();
        Map<CtrlVar,AssignSource> result =
            new LinkedHashMap<CtrlVar,AssignSource>();
        Map<CtrlVar,Integer> sourceVars = call.source().getVarIxMap();
        for (CtrlVar var : call.target().getVars()) {
            Integer ix = call.getOutVars().get(var);
            AssignSource rhs;
            if (ix == null) {
                // the value comes from the source
                ix = sourceVars.get(var);
                rhs = AssignSource.var(ix);
            } else {
                rhs = AssignSource.arg(ix);
            }
            result.put(var, rhs);
        }
        return result;
    }

    /** 
     * Returns an assignment of call parameters to source location
     * variables and constant values (for input parameters), respectively
     * anchor positions and fresh nodes (for output parameters).
     */
    public AssignSource[] getParAssign() {
        if (this.parAssign == null) {
            this.parAssign = computeParAssign();
        }
        return this.parAssign;
    }

    /** Binding of transition in-parameters to bound source variables. */
    private AssignSource[] parAssign;

    /** Computes the binding of call arguments to source location variables.
     */
    private AssignSource[] computeParAssign() {
        List<CtrlPar> args = label().getArgs();
        int size = args == null ? 0 : args.size();
        AssignSource[] result = new AssignSource[size];
        Map<CtrlVar,Integer> sourceVars = source().getLocation().getVarIxMap();
        for (int i = 0; i < size; i++) {
            CtrlPar arg = args.get(i);
            if (arg instanceof CtrlPar.Var) {
                CtrlPar.Var varArg = (CtrlPar.Var) arg;
                if (arg.isInOnly()) {
                    Integer ix = sourceVars.get(varArg.getVar());
                    assert ix != null;
                    result[i] = AssignSource.var(ix);
                } else if (arg.isOutOnly()) {
                    int ix = ((Rule) label().getUnit()).getParBinding(i);
                    result[i] = AssignSource.out(ix);
                } else {
                    assert arg.isDontCare();
                    result[i] = null;
                }
            }
        }
        return result;
    }
}
