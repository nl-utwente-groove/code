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
package groove.control.template;

import groove.control.Binding;
import groove.control.Call;
import groove.control.Callable;
import groove.control.CtrlPar;
import groove.control.CtrlVar;
import groove.control.SoloAttempt;
import groove.grammar.Action;
import groove.grammar.Rule;
import groove.graph.ALabelEdge;
import groove.graph.Edge;
import groove.graph.EdgeRole;
import groove.util.Groove;

import java.util.List;
import java.util.Map;

/**
 * Transition between control locations, bearing either a call or a verdict.
 * A switch can either be <i>base</i>, meaning that it is used as an edge in a template,
 * or <i>derived</i>, meaning that it is used as the {@link SoloAttempt} of a control 
 * step between frames in an actual control automaton. Derived switches may have a 
 * caller switch.
 * Only base switches can be verdicts.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Switch extends ALabelEdge<Location> implements SoloAttempt<Stage> {
    /** Constructs a base verdict switch.
     * @param source source location of the switch
     * @param target target location of the switch
     * @param success flag indicating if this is a success or failure switch
     */
    public Switch(Location source, Location target, boolean success) {
        super(source, target);
        this.base = null;
        this.caller = null;
        this.onFinish = null;
        this.onSuccess = null;
        this.onFailure = null;
        this.kind = Kind.VERDICT;
        this.success = success;
        this.call = null;
    }

    /**
     * Constructs a base call switch.
     * @param source source location of the switch
     * @param target target location of the switch
     * @param call call to be used as label
     */
    public Switch(Location source, Location target, Call call) {
        super(source, target);
        this.base = null;
        this.caller = null;
        this.onFinish = null;
        this.onSuccess = null;
        this.onFailure = null;
        this.kind = call.getUnit().getKind();
        this.call = call;
        this.success = false;
    }

    /**
     * Constructs a derived call switch used as a solo-attempt.
     * @param base base switch from which this one is derived
     * @param caller derived switch from which this one is called; possibly {@code null}
     * @param onFinish next stage after the switch call has finished
     * @param onSuccess alternate stage if the switch call succeeds
     * @param onFailure alternate stage if the switch call fails
     */
    public Switch(Switch base, Switch caller, Stage onFinish, Stage onSuccess, Stage onFailure) {
        super(base.source(), onFinish.getLocation());
        assert base.isBase();
        this.base = base;
        assert caller == null || !caller.isBase();
        this.caller = caller;
        this.onFinish = onFinish;
        this.onSuccess = onSuccess;
        this.onFailure = onFailure;
        this.kind = base.getKind();
        this.call = base.getCall();
        this.success = false;
    }

    @Override
    public Stage onFinish() {
        assert !isBase() : "Base switch " + this + " should not be used as attempt";
        return this.onFinish;
    }

    private final Stage onFinish;

    @Override
    public Stage onSuccess() {
        assert !isBase() : "Base switch " + this + " should not be used as attempt";
        return this.onSuccess;
    }

    private final Stage onSuccess;

    @Override
    public Stage onFailure() {
        assert !isBase() : "Base switch " + this + " should not be used as attempt";
        return this.onFailure;
    }

    private final Stage onFailure;

    @Override
    public boolean sameVerdict() {
        return onFailure() == onSuccess();
    }

    /** Indicates that this is a base switch.
     * It the switch is not base, it is <i>derived</i> and used as a {@link SoloAttempt}.
     * @see #getBase()
     */
    public boolean isBase() {
        return getBase() == null;
    }

    /** If non-{@code null}, this switch is used as a {@link SoloAttempt}
     * derived from the returned base switch. */
    public Switch getBase() {
        return this.base;
    }

    private final Switch base;

    /** 
     * Indicates if this switch has a caller.
     * Only valid if this is a derived switch.
     * @see #isBase()
     */
    public boolean hasCaller() {
        return getCaller() != null;
    }

    /** Returns the switch from which this one was called, if any.
     * Only valid if this is a derived switch.
     * @see #isBase()
     */
    public Switch getCaller() {
        assert !isBase() : "Base switch " + this + " cannot have a caller";
        return this.caller;
    }

    private final Switch caller;

    /**
     * Convenience method testing if this is a verdict switch.
     * @see #getKind() 
     */
    public boolean isVerdict() {
        return getKind() == Kind.VERDICT;
    }

    /**
     * Returns the kind of switch.
     */
    public Kind getKind() {
        return this.kind;
    }

    private final Kind kind;

    /**
     * Convenience method to return the name of the unit called in
     * this switch.
     * Only valid if this is a call switch.
     */
    public String getName() {
        assert !isVerdict();
        return getUnit().getFullName();
    }

    /** 
     * Convenience method to return the arguments of the call of this switch.
     * Only valid if this is a call switch.
     * @return the list of arguments
     */
    public final List<? extends CtrlPar> getArgs() {
        assert !isVerdict() : "" + this + " is not a call switch";
        return getCall().getArgs();
    }

    /** 
     * Convenience method to return the called unit of this switch.
     * Only valid if this is a call switch.
     * @see #getKind()
     */
    public final Callable getUnit() {
        assert !isVerdict() : "" + this + " is not a call switch";
        return getCall().getUnit();
    }

    /*
     * Only valid if this is a call switch.
     * @see #getKind()
     */
    @Override
    public final Call getCall() {
        assert getKind().isCallable() : "" + this + " is not a call switch";
        return this.call;
    }

    /** 
     * The invoked unit of this call.
     * Is {@code null} if this is not a call switch.
     */
    private final Call call;

    /**
     * Indicates if this transition is a success switch.
     * Should only be invoked if this is a verdict switch.
     * @return {@code true} if this is a success switch, {@code false} if
     * this is a failure switch.
     */
    public boolean isSuccess() {
        assert isVerdict();
        return this.success;
    }

    private final boolean success;

    /** 
     * Returns a list of bindings for the variables in the target location.
     * For each variable, the binding is either a variable index of
     * the source location, or an output parameter index in the call.
     */
    public Binding[] getTargetBinding() {
        if (this.targetBinding == null) {
            this.targetBinding = isBase() ? computeTargetBinding() : getBase().getTargetBinding();
        }
        return this.targetBinding;
    }

    /** Binding of target variables to source variables and call parameters. */
    private Binding[] targetBinding;

    /**
     * Computes the binding of target variables to source
     * variables and call parameters.
     * @see #getTargetBinding()
     */
    private Binding[] computeTargetBinding() {
        Binding[] result;
        List<CtrlVar> targetVars = target().getVars();
        int targetVarCount = targetVars.size();
        if (targetVarCount == 0) {
            result = EMPTY_BINDING;
        } else {
            result = new Binding[targetVarCount];
            for (int i = 0; i < targetVarCount; i++) {
                result[i] = computeTargetBinding(targetVars.get(i));
            }
        }
        return result;
    }

    private Binding computeTargetBinding(CtrlVar var) {
        Binding result;
        Map<CtrlVar,Integer> outVars = getCall().getOutVars();
        if (outVars.containsKey(var)) {
            int index = outVars.get(var);
            result = Binding.out(index);
        } else {
            List<CtrlVar> sourceVars = label().source().getVars();
            int index = sourceVars.indexOf(var);
            result = Binding.var(index);
        }
        return result;
    }

    /** 
     * Returns an assignment of call parameters to source location
     * variables and constant values (for input parameters), respectively
     * anchor positions and creator nodes (for output parameters).
     * This is only valid for rule calls.
     */
    public Binding[] getCallBinding() {
        assert getKind() == Kind.RULE;
        if (this.callBinding == null) {
            this.callBinding = isBase() ? computeCallBinding() : getBase().getCallBinding();
        }
        return this.callBinding;
    }

    /** Binding of transition in-parameters to bound source variables. */
    private Binding[] callBinding;

    /** 
     * Computes the binding of call arguments to source location variables
     * and rule anchors and creators. 
     */
    private Binding[] computeCallBinding() {
        List<? extends CtrlPar> args = getArgs();
        int size = args == null ? 0 : args.size();
        Binding[] result = new Binding[size];
        Map<CtrlVar,Integer> sourceVars = source().getVarIxMap();
        for (int i = 0; i < size; i++) {
            CtrlPar arg = args.get(i);
            if (arg instanceof CtrlPar.Var) {
                CtrlPar.Var varArg = (CtrlPar.Var) arg;
                if (arg.isInOnly()) {
                    Integer ix = sourceVars.get(varArg.getVar());
                    assert ix != null;
                    result[i] = Binding.var(ix);
                } else if (arg.isOutOnly()) {
                    result[i] = ((Rule) getUnit()).getParBinding(i);
                } else {
                    assert arg.isDontCare();
                }
            }
        }
        return result;
    }

    @Override
    protected int computeLabelHash() {
        final int prime = 31;
        int result = 1;
        result = prime * result + getKind().hashCode();
        // don't call isSuccess here to escape kind test
        result = prime * result + (this.success ? 1231 : 1237);
        result = prime * result + ((this.call == null) ? 0 : this.call.hashCode());
        result = prime * result + ((this.base == null) ? 0 : this.base.hashCode());
        result = prime * result + ((this.onFinish == null) ? 0 : this.onFinish.hashCode());
        result = prime * result + ((this.onSuccess == null) ? 0 : this.onSuccess.hashCode());
        result = prime * result + ((this.onFailure == null) ? 0 : this.onFailure.hashCode());
        result = prime * result + ((this.caller == null) ? 0 : this.caller.hashCode());
        return result;
    }

    @Override
    protected boolean isTypeEqual(Object obj) {
        return obj instanceof Switch;
    }

    @Override
    protected boolean isLabelEqual(Edge obj) {
        if (this == obj) {
            return true;
        }
        Switch other = (Switch) obj;
        if (getKind() != other.getKind()) {
            return false;
        }
        if (isBase()) {
            if (!other.isBase()) {
                return false;
            }
        } else {
            if (!getBase().equals(other.getBase())) {
                return false;
            }
            if (getCaller() == null) {
                if (other.getCaller() != null) {
                    return false;
                }
            } else {
                if (!getCaller().equals(other.getCaller())) {
                    return false;
                }
            }
            if (!onFinish().equals(other.onFinish())) {
                return false;
            }
            if (!onSuccess().equals(other.onSuccess())) {
                return false;
            }
            if (!onFailure().equals(other.onFailure())) {
                return false;
            }
        }
        if (isVerdict()) {
            if (isSuccess() != other.isSuccess()) {
                return false;
            }
        } else {
            if (!getCall().equals(other.getCall())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public EdgeRole getRole() {
        return EdgeRole.BINARY;
    }

    @Override
    public String text() {
        String result;
        if (getKind() == Kind.VERDICT) {
            result = isSuccess() ? "succ" : "fail";
        } else {
            result = getName();
            if (getArgs() != null) {
                result += Groove.toString(getArgs().toArray(), "(", ")", ",");
            }
            return result;
        }
        return result;
    }

    /** Constant value for the empty binding. */
    private static final Binding[] EMPTY_BINDING = new Binding[0];

    /** Control switch kind. */
    public static enum Kind {
        /** Rule invocation transition. */
        RULE("rule"),
        /** Function call transition. */
        FUNCTION("function"),
        /** Recipe call transition. */
        RECIPE("recipe"),
        /** Verdict transition. */
        VERDICT("choice"),
        /** Legacy kind modelling final states. */
        OMEGA("omega"), ;

        private Kind(String name) {
            this.name = name;
        }

        /** 
         * Indicates if this kind of name denotes a procedure.
         */
        public boolean isProcedure() {
            return this == FUNCTION || this == RECIPE;
        }

        /** Indicates if this kind represents a {@link Callable} unit. */
        public boolean isCallable() {
            return this == FUNCTION || this == RECIPE || this == RULE;
        }

        /** Indicates if this kind represents an {@link Action}. */
        public boolean isAction() {
            return this == RECIPE || this == RULE;
        }

        /** 
         * Returns the description of this kind,
         * with the initial letter optionally capitalised.
         */
        public String getName(boolean upper) {
            StringBuilder result = new StringBuilder(this.name);
            if (upper) {
                result.replace(0, 1, "" + Character.toUpperCase(this.name.charAt(0)));
            }
            return result.toString();
        }

        private final String name;
    }
}
