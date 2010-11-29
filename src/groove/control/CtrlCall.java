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

import groove.trans.SPORule;
import groove.util.Groove;

import java.util.List;

/**
 * Encapsulates a call of a rule or function from a control automaton.
 * The call embodies the rule or function name and a sequence of arguments.
 * @author Arend Rensink
 * @version $Revision $
 */
public class CtrlCall {
    /** Constructor for the singleton success call. */
    private CtrlCall() {
        this.rule = null;
        this.function = null;
        this.args = null;
    }

    /**
     * Constructs a call for a given function and list of arguments.
     * @param function the name of the function to be called; non-{@code null}
     * @param args list of arguments for the call; non-{@code null}
     */
    public CtrlCall(String function, List<CtrlPar> args) {
        this.function = function;
        this.rule = null;
        this.args = args;
    }

    /**
     * Constructs an instantiated call for a given rule and list of arguments.
     * @param rule the rule to be called; non-{@code null}
     * @param args list of arguments for the call; non-{@code null}
     */
    public CtrlCall(SPORule rule, List<CtrlPar> args) {
        this.args = args;
        this.rule = rule;
        this.function = null;
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = obj == this;
        if (!result && obj instanceof CtrlCall) {
            CtrlCall other = (CtrlCall) obj;
            if (isOmega()) {
                result = other == this;
            } else if (isRule()) {
                result = getRule().equals(other.getRule());
            } else if (isFunction()) {
                result = getFunction().equals(other.getFunction());
            }
            if (getArgs() == null) {
                result &= other.getArgs() == null;
            } else {
                result &= getArgs().equals(other.getArgs());
            }
        }
        return result;
    }

    @Override
    public int hashCode() {
        int result = 0;
        if (isFunction()) {
            result = getFunction().hashCode();
        } else if (isOmega()) {
            result = super.hashCode();
        } else if (isRule()) {
            result = getRule().hashCode();
        }
        if (getArgs() != null) {
            result ^= getArgs().hashCode();
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
        return this == OMEGA;
    }

    /**
     * Indicates if this is a (non-omega) rule call.
     */
    public boolean isRule() {
        return this.rule != null;
    }

    /**
     * Indicates if this is a function call.
     */
    public boolean isFunction() {
        return this.function != null;
    }

    /** Returns the name of the called function or the invoked rule. */
    public String getName() {
        if (isFunction()) {
            return getFunction();
        } else if (isOmega()) {
            return OMEGA_NAME;
        } else {
            assert isRule();
            return getRule().getName().text();
        }
    }

    /**
     * Returns a new control call, based on the rule or function of
     * this call but with replaced arguments.
     * @param args the arguments of the new call
     */
    public CtrlCall copy(List<CtrlPar> args) {
        assert args == null || args.size() == getArgs().size();
        CtrlCall result;
        if (isFunction()) {
            result = new CtrlCall(getFunction(), args);
        } else if (isOmega()) {
            result = this;
        } else {
            assert isRule();
            result = new CtrlCall(getRule(), args);
        }
        return result;
    }

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
     * @return the rule being called; or {@code null} if this is an 
     * function call or an omega call.
     * @see #isOmega()
     */
    public final SPORule getRule() {
        return this.rule;
    }

    /** 
     * The rule being called. 
     * May be {@code null} if this is a function or omega call.
     */
    private final SPORule rule;

    /** 
     * Returns the name of the function being called.
     * @return the name of the function being called; or {@code null} if this is a rule call or
     * omega call.
     * @see #isOmega()
     */
    public final String getFunction() {
        return this.function;
    }

    /** The name of the function being called; non-{@code null}. */
    private final String function;

    /**
     * A special call, indicating that the control program is successful.
     * Can be seen as a call to a rule that always matches and makes no changes.
     */
    public static final CtrlCall OMEGA = new CtrlCall();

    /** Name of the omega rule (which models termination). */
    public static final String OMEGA_NAME = "\u03A9";
}
