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

import groove.trans.Rule;
import groove.trans.RuleSystem;
import groove.util.Groove;
import groove.view.FormatException;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates a call of a rule from a control automaton.
 * The call embodies the rule and a sequence of arguments.
 * A call is <i>virtual</i> if the rule is only specified by
 * name, and <i>actual</i> if the rule is instantiated.
 * @author Arend Rensink
 * @version $Revision $
 */
public class CtrlCall {
    /** Constructor for the singleton success call. */
    private CtrlCall() {
        this.rule = null;
        this.ruleName = null;
        this.args = null;
    }

    /**
     * Constructs a virtual call for a given rule and list of arguments.
     * @param ruleName the name of the rule to be called; non-{@code null}
     * @param args list of arguments for the call; may be {@code null} in
     * case of a parameterless call
     */
    public CtrlCall(String ruleName, List<CtrlPar> args) {
        this.ruleName = ruleName;
        this.rule = null;
        this.args = args;
    }

    /**
     * Constructs an instantiated call for a given rule and list of arguments.
     * @param rule the rule to be called; non-{@code null}
     * @param args list of arguments for the call; may be {@code null} in
     * case of a parameterless call
     */
    public CtrlCall(Rule rule, List<CtrlPar> args) {
        this.args = args;
        this.rule = rule;
        this.ruleName = rule.getName().text();
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = obj == this;
        if (!result && obj instanceof CtrlCall) {
            CtrlCall other = (CtrlCall) obj;
            if (isOmega()) {
                result = other.isOmega();
            } else {
                result =
                    isVirtual() == other.isVirtual()
                        && getRuleName().equals(other.getRuleName());
                if (result) {
                    if (getArgs() == null) {
                        result = other.getArgs() == null;
                    } else {
                        result = getArgs().equals(other.getArgs());
                    }
                }
            }
        }
        return result;
    }

    @Override
    public int hashCode() {
        int result = 0;
        if (!isOmega()) {
            result = getRuleName().hashCode();
            if (getArgs() != null) {
                result ^= getArgs().hashCode();
            }
            if (isVirtual()) {
                result = -result;
            }
        }
        return result;
    }

    @Override
    public String toString() {
        String result;
        if (isOmega()) {
            result = "OMEGA";
        } else {
            result = getRuleName();
            if (getArgs() != null) {
                result += Groove.toString(getArgs().toArray(), "(", ")", ",");
            }
        }
        return result;
    }

    /**
     * Indicates if this is a virtual call.
     * A call is virtual if the actual rule is only given by name, 
     * and not instantiated.
     */
    public boolean isVirtual() {
        return getRule() == null;
    }

    /**
     * Indicates if this is an omega call.
     * @see #OMEGA
     */
    public boolean isOmega() {
        return this == OMEGA;
    }

    /** 
     * Returns the arguments of the call.
     * @return the list of arguments; or {@code null} if this is an omega call.
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
     * virtual call or an omega call. 
     * @see #isVirtual()
     * @see #isOmega()
     */
    public final Rule getRule() {
        return this.rule;
    }

    /** 
     * The rule being called. 
     * May be {@code null} if this is a virtual call.
     */
    private final Rule rule;

    /** 
     * Returns the name of the rule being called.
     * @return the name of the rule being called; or {@code null} if this is an 
     * omega call.
     * @see #isOmega()
     */
    public final String getRuleName() {
        return this.ruleName;
    }

    /** The name of the rule being called; non-{@code null}. */
    private final String ruleName;

    /** 
     * Returns an actual call of the rule named in this (virtual) call,
     * based on a given rule system.
     * @throws FormatException if this call's rule name does not occur in the rule system,
     * or the arguments of this call are not compatible with the rule parameters.  
     */
    public CtrlCall instantiate(RuleSystem grammar) throws FormatException {
        Rule rule = grammar.getRule(getRuleName());
        if (rule == null) {
            throw new FormatException(
                "Called rule '%s' does not occur in grammar", getRuleName());
        }
        List<CtrlPar> newArgs;
        if (getArgs() == null) {
            newArgs = null;
        } else {
            newArgs = new ArrayList<CtrlPar>(getArgs().size());
            for (CtrlPar arg : getArgs()) {
                newArgs.add(arg.instantiate(grammar));
            }
        }
        // TODO the test for argument compatibility is to be added later
        return new CtrlCall(rule, newArgs);
    }

    /**
     * A special call, indicating that the control program is successful.
     * Can be seen as a call to a rule that always matches and makes no changes.
     */
    public static final CtrlCall OMEGA = new CtrlCall();
}
