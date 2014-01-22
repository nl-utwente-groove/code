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

import groove.control.AssignSource;
import groove.control.Call;
import groove.control.Callable;
import groove.control.CtrlPar;
import groove.control.CtrlVar;
import groove.control.SoloAttempt;
import groove.grammar.Action;
import groove.graph.ALabelEdge;
import groove.graph.Edge;
import groove.graph.EdgeRole;
import groove.util.Groove;

import java.util.List;
import java.util.Map;

/**
 * Control template edge.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Switch extends ALabelEdge<Location> implements SoloAttempt<Stage> {
    /** Constructs a verdict switch.
     * @param source source location of the switch
     * @param target target location of the switch
     * @param success flag indicating if this is a success or failure switch
     */
    public Switch(Location source, Location target, boolean success) {
        super(source, target);
        this.base = null;
        this.onFinish = null;
        this.onSuccess = null;
        this.onFailure = null;
        this.kind = Kind.VERDICT;
        this.success = success;
        this.call = null;
    }

    /**
     * Constructs a call switch.
     * @param source source location of the switch
     * @param target target location of the switch
     * @param call call to be used as label
     */
    public Switch(Location source, Location target, Call call) {
        super(source, target);
        this.base = null;
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
     * @param onFinish next stage after the switch call has finished
     * @param onSuccess alternate stage if the switch call succeeds
     * @param onFailure alternate stage if the switch call fails
     */
    public Switch(Switch base, Stage onFinish, Stage onSuccess, Stage onFailure) {
        super(base.source(), onFinish.getLocation());
        this.base = base;
        this.onFinish = onFinish;
        this.onSuccess = onSuccess;
        this.onFailure = onFailure;
        this.kind = base.getKind();
        this.call = base.getCall();
        this.success = false;
    }

    public Stage onFinish() {
        return this.onFinish;
    }

    private final Stage onFinish;

    public Stage onSuccess() {
        return this.onSuccess;
    }

    private final Stage onSuccess;

    public Stage onFailure() {
        return this.onFailure;
    }

    private final Stage onFailure;

    /** Indicates that this is a derived switch, used as a {@link SoloAttempt}. */
    public boolean hasBase() {
        return getBase() != null;
    }

    /** If non-{@code null}, this switch is used as a {@link SoloAttempt}
     * derived from the returned base switch. */
    public Switch getBase() {
        return this.base;
    }

    private final Switch base;

    /**
     * Convenience method testing if this is a verdict switch.
     * @see #getKind() 
     */
    public boolean isVerdict() {
        return getKind() == Kind.VERDICT;
    }

    /**
     * Returns the kind of this label.
     */
    public Kind getKind() {
        return this.kind;
    }

    private final Kind kind;

    /**
     * Returns the name of the callable unit invoked in
     * this switch.
     * Should only be called if this is a call switch.
     */
    public String getName() {
        assert !isVerdict();
        return getUnit().getFullName();
    }

    /** 
     * Returns the arguments of the call of this switch.
     * Should only be invoked if this is a call switch.
     * @return the list of arguments
     */
    public final List<? extends CtrlPar> getArgs() {
        assert !isVerdict();
        return getCall().getArgs();
    }

    /** 
     * Returns the invoked unit of this call.
     * Should only be invoked if this is a call switch.
     * @see #getKind()
     */
    public final Callable getUnit() {
        return getCall().getUnit();
    }

    /** 
     * Returns the call of this switch.
     * Should only be invoked if this is a call switch.
     * @see #getKind()
     */
    public final Call getCall() {
        assert getKind().isCallable();
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
     * Returns a list of assignment sources for the variables in the target location.
     * For each variable, the source is either a variable of
     * the source location, or an output argument in the call.
     */
    public AssignSource[] getAssignment() {
        if (this.assignment == null) {
            if (hasBase()) {
                this.assignment = getBase().getAssignment();
            } else {
                this.assignment = computeAssignment();
            }
        }
        return this.assignment;
    }

    /**
     * Computes the binding of target variables to source
     * variables and call parameters.
     * @see #getAssignment()
     */
    private AssignSource[] computeAssignment() {
        AssignSource[] result;
        List<CtrlVar> targetVars = target().getVars();
        int targetVarCount = targetVars.size();
        if (targetVarCount == 0) {
            result = EMPTY_BINDING;
        } else {
            result = new AssignSource[targetVarCount];
            for (int i = 0; i < targetVarCount; i++) {
                result[i] = computeAssignSource(targetVars.get(i));
            }
        }
        return result;
    }

    private AssignSource computeAssignSource(CtrlVar var) {
        AssignSource result;
        Map<CtrlVar,Integer> outVars = getCall().getOutVars();
        if (outVars.containsKey(var)) {
            int index = outVars.get(var);
            result = AssignSource.arg(index);
        } else {
            List<CtrlVar> sourceVars = label().source().getVars();
            int index = sourceVars.indexOf(var);
            result = AssignSource.var(index);
        }
        return result;
    }

    /** Binding of target variables to source variables and call parameters. */
    private AssignSource[] assignment;

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
        if (hasBase()) {
            if (!other.hasBase()) {
                return false;
            }
            if (!getBase().equals(other.getBase())) {
                return false;
            }
        }
        if (isVerdict()) {
            if (isSuccess() != other.isSuccess()) {
                return false;
            }
        } else {
            if (!getArgs().equals(other.getArgs())) {
                return false;
            }
            if (!getName().equals(other.getName())) {
                return false;
            }
            if (onFinish() == null) {
                if (other.onFinish() != null) {
                    return false;
                }
            } else if (!onFinish().equals(other.onFinish())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public EdgeRole getRole() {
        return EdgeRole.BINARY;
    }

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
    private static final AssignSource[] EMPTY_BINDING = new AssignSource[0];

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
