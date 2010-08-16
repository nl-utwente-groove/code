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

import java.util.List;

/**
 * Encapsulates a call of a rule from a control automaton.
 * The call embodies the rule and a sequence of arguments.
 * @author Arend Rensink
 * @version $Revision $
 */
public class CtrlCall {
    /**
     * Constructs a call from a given rule and list of arguments.
     */
    public CtrlCall(Rule rule, List<CtrlPar> arguments) {
        this.arguments = arguments;
        this.rule = rule;
    }

    /** Returns the arguments of the call. */
    public final List<CtrlPar> getArgs() {
        return this.arguments;
    }

    /** The list of arguments of the control call. */
    private final List<CtrlPar> arguments;

    /** Returns the rule being called. */
    public final Rule getRule() {
        return this.rule;
    }

    /** The rule being called. */
    private final Rule rule;
}
