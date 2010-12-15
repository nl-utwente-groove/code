/* * GROOVE: GRaphs for Object Oriented VErification *  * Copyright 2003--2007 University of Twente *  *  *  * Licensed under the Apache License, Version 2.0 (the "License"); *  * you may not use this file except in compliance with the License. *  * You may obtain a copy of the License at *  * http://www.apache.org/licenses/LICENSE-2.0 *  *  *  * Unless required by applicable law or agreed to in writing, *  * software distributed under the License is distributed on an *  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, *  * either express or implied. See the License for the specific *  * language governing permissions and limitations under the License. *  *  *  * $Id: ControlTransition.java,v 1.10 2008-01-30 11:13:57 fladder Exp $ */package groove.control;import groove.graph.AbstractEdge;import groove.trans.SPORule;import java.util.List;import java.util.Set;/** * Represents a transition in a control automaton. * Control transitions have pairs of guards and rule calls as labels. * A rule call is a rule with a sequence of input and output parameters. * A guard is a failure set, i.e., a set of rules that cannot be performed. * A transition is <i>virtual</i> if the rule names in the call and guard * are only given as strings, and <i>actual</i> if they are instantiated rules.  * @author Arend Rensink */public class CtrlTransition extends AbstractEdge<CtrlState,CtrlLabel,CtrlState> {    /**     * Creates a new control transition between two control states.     */    public CtrlTransition(CtrlState source, CtrlLabel label, CtrlState target) {        super(source, label, target);    }    /**      * Returns a list of indices corresponding to the bound variables in the target state.     * For each bound variable, the index either points to the bound variables of     * the source state, or to an (output) parameter position in the rule call.     * In the latter case, the index is offset by the number of bound source variables.     */    public int[] getTargetVarBinding() {        if (this.targetVarBinding == null) {            this.targetVarBinding = computeTargetVarBinding();        }        return this.targetVarBinding;    }    /** Computes the binding of bound target variables to bound source     * variables and transition parameters.     * @see #getTargetVarBinding()     */    private int[] computeTargetVarBinding() {        List<CtrlVar> targetVars = target().getBoundVars();        List<CtrlVar> sourceVars = source().getBoundVars();        int sourceVarCount = sourceVars.size();        int[] result = new int[targetVars.size()];        for (int i = 0; i < targetVars.size(); i++) {            CtrlVar targetVar = targetVars.get(i);            int index = sourceVars.indexOf(targetVar);            if (index < 0) {                assert getOutVars().contains(targetVar) : String.format(                    "Neither source vars %s nor out-parameters %s contain variable %s",                    sourceVars, getOutVars(), targetVar);                index = getCall().getOutVars().get(targetVar) + sourceVarCount;            }            result[i] = index;        }        return result;    }    /** Binding of bound target variables to bound source variables and transition parameters. */    private int[] targetVarBinding;    /**      * Returns a list of indices corresponding to the transition parameters.     * For each parameter position, if the parameter is an input variable,      * the index points to the index in the source bound variables; if it is     * an output variable, it either points to the anchor position or to the     * position in the created nodes.     */    public int[] getParBinding() {        if (this.parBinding == null) {            this.parBinding = computeParBinding();        }        return this.parBinding;    }    /** Computes the binding of transition parameters to bound source variables.     * @see #getTargetVarBinding()     */    private int[] computeParBinding() {        List<CtrlPar> args = getCall().getArgs();        int size = args == null ? 0 : args.size();        int[] result = new int[size];        List<CtrlVar> sourceVars = source().getBoundVars();        for (int i = 0; i < size; i++) {            CtrlPar arg = args.get(i);            if (arg instanceof CtrlPar.Var) {                CtrlPar.Var varArg = (CtrlPar.Var) arg;                if (arg.isInOnly()) {                    int index = sourceVars.indexOf(varArg.getVar());                    assert index >= 0;                    result[i] = index;                } else if (arg.isOutOnly()) {                    result[i] = getRule().getParBinding(i);                } else {                    assert arg.isDontCare();                    result[i] = -1;                }            }        }        return result;    }    /** Binding of transition in-parameters to bound source variables. */    private int[] parBinding;    /** Returns the set of variables used as input parameters in this transition. */    public Set<CtrlVar> getInVars() {        return getCall().getInVars().keySet();    }    /** Indicates that this transition has output parameters. */    public boolean hasOutVars() {        return !getCall().getOutVars().isEmpty();    }    /** Returns the set of variables used as output parameters in this transition. */    public Set<CtrlVar> getOutVars() {        return getCall().getOutVars().keySet();    }    /** Tests if this transition changes the control state or any of the bound variables. */    public boolean isModifying() {        return source() != target() || hasOutVars();    }    /** Convenience method to return the control call of this transition's label. */    final public CtrlCall getCall() {        return label().getCall();    }    /** Convenience method to return the called rule of this transition. */    public SPORule getRule() {        return getCall().getRule();    }}