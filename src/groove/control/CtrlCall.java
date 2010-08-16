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
import groove.util.Groove;

import java.util.List;

/**
 * Encapsulates a call of a rule from a control automaton.
 * The call embodies the rule and a sequence of arguments.
 * @author Arend Rensink
 * @version $Revision $
 */
public class CtrlCall {
    /** Constructor for the singleton success call. */
    private CtrlCall() {
        this.rule = null;
        this.arguments = null;
    }

    /**
     * Constructs a call from a given rule and list of arguments.
     * @param rule the rule to be called; non-{@code null}
     * @param arguments list of arguments for the call; non-{@code null}
     */
    public CtrlCall(Rule rule, List<CtrlArg> arguments) {
        this.arguments = arguments;
        this.rule = rule;
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof CtrlCall) {
            CtrlCall other = (CtrlCall) obj;
            if (isOmega()) {
                result = other.isOmega();
            } else {
                result =
                    getRule().equals(other.getRule())
                        && getArgs().equals(other.getArgs());
            }
        }
        return result;
    }

    @Override
    public int hashCode() {
        int result = 0;
        if (!isOmega()) {
            result = getRule().hashCode() ^ getArgs().hashCode();
        }
        return result;
    }

    @Override
    public String toString() {
        String result;
        if (isOmega()) {
            result = "OMEGA";
        } else {
            result =
                getRule().getName()
                    + Groove.toString(getArgs().toArray(), "(", ")", ",");
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
     * Returns the arguments of the call.
     * @return the list of arguments; or {@code null} if this is an omega call.
     * @see #OMEGA
     */
    public final List<CtrlArg> getArgs() {
        return this.arguments;
    }

    /** 
     * The list of arguments of the control call.
     */
    private final List<CtrlArg> arguments;

    /** 
     * Returns the rule being called.
     * @return the rule being called; or {@code null} if this is an omega call. 
     * @see #OMEGA
     */
    public final Rule getRule() {
        return this.rule;
    }

    /** The rule being called. */
    private final Rule rule;

    /**
     * A special call, indicating that the control program is successful.
     * Can be seen as a call to a rule that always matches and makes no changes.
     */
    public static final CtrlCall OMEGA = new CtrlCall();
}
