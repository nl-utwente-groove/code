/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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

import groove.control.CtrlPar.Var;
import groove.trans.Action;
import groove.trans.Rule;
import groove.util.Groove;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Encapsulates a call of a rule or function from a control automaton.
 * The call embodies the rule or function name and a sequence of arguments.
 * @author Arend Rensink
 * @version $Revision $
 */
public class CtrlCall {
    /** Constructor for the singleton success call. */
    private CtrlCall() {
        this.kind = Kind.OMEGA;
        this.name = OMEGA_NAME;
        this.rule = null;
        this.args = null;
    }

    /**
     * Constructs a call for a given function or recipe and list of arguments.
     * @param kind indices whether this concerns a function or recipe call
     * @param name name of the function or recipe to be called; non-{@code null}
     * @param args list of arguments for the call; may be {@code null}
     * for a transaction call
     */
    public CtrlCall(CtrlCall.Kind kind, String name, List<CtrlPar> args) {
        assert kind == CtrlCall.Kind.RECIPE || kind == CtrlCall.Kind.FUNCTION;
        this.kind = kind;
        this.name = name;
        this.rule = null;
        this.args = args;
    }

    /**
     * Constructs an instantiated call for a given rule and list of arguments.
     * @param rule the rule to be called; non-{@code null}
     * @param args list of arguments for the call; may be {@code null}
     */
    public CtrlCall(Rule rule, List<CtrlPar> args) {
        this.kind = CtrlCall.Kind.RULE;
        this.name = rule.getFullName();
        this.args = args;
        this.rule = rule;
        // the following assertion has been removed since in symbolic
        // exploration it is actually OK not to provide values for input parameters
        //        assert ruleInputSatisfied();
    }

    @SuppressWarnings("unused")
    private boolean ruleInputSatisfied() {
        for (int i = 0; i < this.rule.getSignature().size(); i++) {
            Var var = this.rule.getSignature().get(i);
            if (var.isInOnly()) {
                if (this.args == null || !this.args.get(i).isInOnly()) {
                    throw new IllegalArgumentException(String.format(
                        "Parameter %d of rule %s not instantiated in %s", i,
                        this.rule.getFullName(), this));
                }
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof CtrlCall)) {
            return false;
        }
        CtrlCall other = (CtrlCall) obj;
        if (getKind() != other.getKind()) {
            return false;
        }
        if (isOmega()) {
            return true;
        }
        if (!getName().equals(other.getName())) {
            return false;
        }
        if (getArgs() == null) {
            return other.getArgs() == null;
        } else {
            return getArgs().equals(other.getArgs());
        }
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = getKind().hashCode();
        result = prime * result + (isOmega() ? 1231 : 1237);
        if (getName() != null) {
            result = prime * result + getName().hashCode();
        }
        if (getArgs() != null) {
            result = prime * result + getArgs().hashCode();
        }
        return result;
    }

    @Override
    public String toString() {
        String result = getName();
        if (getArgs() != null) {
            result += Groove.toString(getArgs().toArray(), "(", ")", ",");
        }
        return result;
    }

    /**
     * Indicates if this is an omega call.
     * @see #OMEGA
     */
    public boolean isOmega() {
        return getKind() == Kind.OMEGA;
    }

    /**
     * Returns a new control call, based on the rule or function of
     * this call but with replaced arguments.
     * @param args the arguments of the new call
     */
    public CtrlCall copy(List<CtrlPar> args) {
        assert args == null || args.size() == getArgs().size();
        CtrlCall result;
        switch (getKind()) {
        case OMEGA:
            result = this;
            break;
        case RULE:
            result = new CtrlCall(getRule(), args);
            break;
        default:
            result = new CtrlCall(getKind(), getName(), args);
        }
        return result;
    }

    /** Tests if this control call modifies the input arguments of another. */
    public boolean modifies(CtrlCall other) {
        boolean result = false;
        if (!(getArgs() == null || getArgs().isEmpty()
            || other.getArgs() == null || other.getArgs().isEmpty())) {
            Map<CtrlVar,Integer> otherInVars = other.getInVars();
            for (CtrlVar outVar : getOutVars().keySet()) {
                if (otherInVars.containsKey(outVar)) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    /** Returns the mapping of output variables to argument positions of this call. */
    public Map<CtrlVar,Integer> getOutVars() {
        if (this.outVars == null) {
            initVars();
        }
        return this.outVars;
    }

    /** Returns the mapping of input variables to argument positions of this call. */
    public Map<CtrlVar,Integer> getInVars() {
        if (this.inVars == null) {
            initVars();
        }
        return this.inVars;
    }

    /** Initialises the input and output variables of this call. */
    private void initVars() {
        Map<CtrlVar,Integer> outVars = new HashMap<CtrlVar,Integer>();
        Map<CtrlVar,Integer> inVars = new HashMap<CtrlVar,Integer>();
        if (getArgs() != null && !getArgs().isEmpty()) {
            int size = getArgs().size();
            for (int i = 0; i < size; i++) {
                CtrlPar arg = getArgs().get(i);
                if (arg instanceof CtrlPar.Var) {
                    CtrlVar var = ((CtrlPar.Var) arg).getVar();
                    if (arg.isInOnly()) {
                        inVars.put(var, i);
                    } else {
                        assert arg.isOutOnly();
                        outVars.put(var, i);
                    }
                }
            }
        }
        this.outVars = outVars;
        this.inVars = inVars;
    }

    private Map<CtrlVar,Integer> inVars;
    private Map<CtrlVar,Integer> outVars;

    /** Returns the kind of object being called. */
    public CtrlCall.Kind getKind() {
        return this.kind;
    }

    /** The kind of object being called. */
    private final CtrlCall.Kind kind;

    /** 
     * Returns the arguments of the call.
     * @return the list of arguments; or {@code null} if this is an omega call
     * or a parameterless call.
     * @see #OMEGA
     */
    public final List<CtrlPar> getArgs() {
        return this.args;
    }

    /** 
     * The list of arguments of the control call.
     */
    private final List<CtrlPar> args;

    /** 
     * Returns the rule being called.
     * @return the rule being called; or {@code null} if this is a
     * function, recipe or omega call.
     * @see #getKind()
     */
    public final Rule getRule() {
        return this.rule;
    }

    /** 
     * The rule being called. 
     * May be {@code null} if this is a function or omega call.
     */
    private final Rule rule;

    /** 
     * Returns the name of the function being called.
     * @return the name of the function being called; or {@code null} if this is a rule call or
     * omega call.
     * @see #isOmega()
     */
    public final String getName() {
        return this.name;
    }

    /** The name of the function being called; non-{@code null}. */
    private final String name;

    /** 
     * Returns the call kind of a given grammar action.
     * This is either {@link Kind#RULE} or {@link Kind#RECIPE},
     * depending on the actual action object.
     */
    public static Kind getKind(Action action) {
        switch (action.getKind()) {
        case RULE:
            return Kind.RULE;
        case RECIPE:
            return Kind.RECIPE;
        default:
            assert false;
            return null;
        }
    }

    /** Name of the omega rule (which models termination). */
    public static final String OMEGA_NAME = "\u03A9";
    /**
     * A special call, indicating that the control program is successful.
     * Can be seen as a call to a rule that always matches and makes no changes.
     */
    public static final CtrlCall OMEGA = new CtrlCall();

    /** Kinds of calls encountered in a control program. */
    public static enum Kind {
        /** Graph transformation rules. */
        RULE("rule"),
        /** Transactions (declared by {@code rule} blocks). */
        RECIPE("recipe"),
        /** Functions (declared by {@code function} blocks). */
        FUNCTION("function"),
        /** Termination. */
        OMEGA("omega");

        private Kind(String name) {
            this.name = name;
        }

        /** 
         * Indicates if this kind of name has an associated body
         * (translated to a control automaton).
         */
        public boolean hasBody() {
            return this != RULE;
        }

        /** 
         * Returns the description of this name kind,
         * with the initial letter optionally capitalised.
         */
        public String getName(boolean upper) {
            StringBuilder result = new StringBuilder(this.name);
            if (upper) {
                result.replace(0, 1,
                    "" + Character.toUpperCase(this.name.charAt(0)));
            }
            return result.toString();
        }

        private final String name;
    }

}
