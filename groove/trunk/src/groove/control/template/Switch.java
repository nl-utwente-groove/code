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

import groove.control.Attempt;
import groove.control.Binding;
import groove.control.Call;
import groove.control.CallStack;
import groove.control.Callable;
import groove.control.CtrlPar;
import groove.control.CtrlPar.Var;
import groove.control.CtrlVar;
import groove.grammar.Action;
import groove.util.Pair;

import java.util.LinkedList;
import java.util.List;

/**
 * Transition between control locations, bearing either a call or a verdict.
 * A switch can either be <i>base</i>, meaning that it is used as an edge in a template,
 * or <i>derived</i>, meaning that it is used as the {@link Attempt} of a control 
 * step between frames in an actual control automaton. Derived switches may have a 
 * caller switch.
 * Only base switches can be verdicts.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Switch implements Attempt.Stage<Location,Switch>, Comparable<Switch> {
    /**
     * Constructs a base call switch.
     * @param onFinish target location of the switch
     * @param call call to be used as label
     */
    public Switch(Location onFinish, Call call, int depth) {
        assert onFinish != null;
        this.onFinish = onFinish;
        this.nested = null;
        this.kind = call.getUnit().getKind();
        this.call = call;
        this.depth = depth;
    }

    /** Initialises the control variables of the source location of the switch. */
    void setSourceVars(List<CtrlVar> sourceVars) {
        this.sourceVars = sourceVars;
    }

    /** Returns the control variables in the source location of this switch. */
    public List<CtrlVar> getSourceVars() {
        if (this.sourceVars == null) {
            onFinish().getTemplate().initVars();
            assert this.sourceVars != null;
        }
        return this.sourceVars;
    }

    private List<CtrlVar> sourceVars;

    @Override
    public Location onFinish() {
        return this.onFinish;
    }

    private final Location onFinish;

    /**
     * Sets a nested switch called from this one.
     */
    public void setNested(Switch nested) {
        this.nested = nested;
    }

    /** 
     * Indicates if this switch has a nested (called) switch.
     */
    public boolean hasNested() {
        return getNested() != null;
    }

    /**
     * Returns the switch called from this one, if any.
     */
    public Switch getNested() {
        return this.nested;
    }

    private Switch nested;

    /** Returns the depth of the call stack of this switch (not including this switch).
     */
    public int getCallDepth() {
        return getStack().size();
    }

    /** Returns the call stack of this switch.
     */
    public SwitchStack getStack() {
        if (this.callStack == null) {
            this.callStack = new SwitchStack(this);
        }
        return this.callStack;
    }

    /** List of callers, from bottom to top. */
    private SwitchStack callStack;

    @Override
    public CallStack getCallStack() {
        return getStack().getCallStack();
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
        return getUnit().getFullName();
    }

    /** 
     * Convenience method to return the arguments of the call of this switch.
     * Only valid if this is a call switch.
     * @return the list of arguments
     */
    public final List<? extends CtrlPar> getArgs() {
        return getCall().getArgs();
    }

    /** 
     * Convenience method to return the called unit of this switch.
     * Only valid if this is a call switch.
     * @see #getKind()
     */
    public final Callable getUnit() {
        return getCall().getUnit();
    }

    /**
     * Returns the rule or procedure call wrapped in this switch.
     * The call is a procedure call if and only if the switch has a nested
     * switch.
     */
    public final Call getCall() {
        assert getKind().isCallable() : "" + this + " is not a call switch";
        return this.call;
    }

    /** 
     * The invoked unit of this call.
     * Is {@code null} if this is not a call switch.
     */
    private final Call call;

    @Override
    public Call getRuleCall() {
        return getCallStack().peek();
    }

    @Override
    public int getDepth() {
        return this.depth;
    }

    private final int depth;

    /** 
     * Returns pairs of input parameters of this call and corresponding
     * bindings to source location variables and constant values.
     * This is only valid for rule calls.
     */
    public List<Pair<Var,Binding>> getCallBinding() {
        assert getKind() == Kind.RULE;
        if (this.callBinding == null) {
            this.callBinding = computeCallBinding();
        }
        return this.callBinding;
    }

    /** Binding of in-parameter positions to source variables and constant arguments. */
    private List<Pair<Var,Binding>> callBinding;

    /** 
     * Computes the binding of call parameter positions to source location
     * variables and constant values.
     * @return a list of pairs of call parameter variables and bindings.
     * The binding is {@code null} for a non-input-parameter.
     */
    private List<Pair<Var,Binding>> computeCallBinding() {
        List<Pair<Var,Binding>> result = new LinkedList<Pair<Var,Binding>>();
        List<? extends CtrlPar> args = getArgs();
        List<Var> sig = getUnit().getSignature();
        int size = args == null ? 0 : args.size();
        List<CtrlVar> sourceVars = getSourceVars();
        for (int i = 0; i < size; i++) {
            CtrlPar arg = args.get(i);
            Binding bind;
            if (arg instanceof CtrlPar.Var) {
                CtrlPar.Var varArg = (CtrlPar.Var) arg;
                if (arg.isInOnly()) {
                    int ix = sourceVars.indexOf(varArg.getVar());
                    assert ix >= 0;
                    bind = Binding.var(ix);
                } else if (arg.isOutOnly()) {
                    bind = null;
                } else {
                    assert false;
                    bind = null;
                }
            } else if (arg instanceof CtrlPar.Const) {
                bind = Binding.value((CtrlPar.Const) arg);
            } else {
                assert arg instanceof CtrlPar.Wild;
                bind = null;
            }
            result.add(Pair.newPair(sig.get(i), bind));
        }
        return result;
    }

    @Override
    public int hashCode() {
        return hashCode(true);
    }

    /** Computes the hash code of this switch, optionally taking the nested switch into account. */
    public int hashCode(boolean full) {
        final int prime = 31;
        int result = 1;
        result = prime * result + getSourceVars().hashCode();
        result = prime * result + onFinish().hashCode();
        result = prime * result + getKind().hashCode();
        result = prime * result + getDepth();
        result = prime * result + ((this.call == null) ? 0 : this.call.hashCode());
        result = prime * result + ((!full || this.nested == null) ? 0 : this.nested.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Switch)) {
            return false;
        }
        return equals((Switch) obj, true);
    }

    /** 
     * Compares this switch with another, optionally ignoring the nested switch.
     * @param full if {@code true}, the nested switch is taken into account
     */
    public boolean equals(Switch other, boolean full) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (getKind() != other.getKind()) {
            return false;
        }
        if (getDepth() != other.getDepth()) {
            return false;
        }
        if (!getSourceVars().equals(other.getSourceVars())) {
            return false;
        }
        if (full) {
            if (getNested() == null) {
                if (other.getNested() != null) {
                    return false;
                }
            } else {
                if (!getNested().equals(other.getNested())) {
                    return false;
                }
            }
        }
        if (!onFinish().equals(other.onFinish())) {
            return false;
        }
        if (!getCall().equals(other.getCall())) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(Switch o) {
        int result = onFinish().getNumber() - o.onFinish().getNumber();
        if (result != 0) {
            return result;
        }
        result = getDepth() - o.getDepth();
        if (result != 0) {
            return result;
        }
        result = getKind().ordinal() - o.getKind().ordinal();
        if (result != 0) {
            return result;
        }
        result = getCall().compareTo(o.getCall());
        if (result != 0) {
            return result;
        }
        result = getSourceVars().size() - o.getSourceVars().size();
        if (result != 0) {
            return result;
        }
        for (int i = 0; i < getSourceVars().size(); i++) {
            result = getSourceVars().get(i).compareTo(o.getSourceVars().get(i));
            if (result != 0) {
                return result;
            }
        }
        if (hasNested()) {
            if (o.hasNested()) {
                result = getNested().compareTo(o.getNested());
            } else {
                return 1;
            }
        } else if (o.hasNested()) {
            return -1;
        }
        return result;
    }

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
