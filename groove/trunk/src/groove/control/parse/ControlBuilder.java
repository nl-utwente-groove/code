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
package groove.control.parse;

import groove.control.ControlAutomaton;
import groove.control.ControlState;
import groove.control.ControlTransition;

/**
 * Class that provides operations to compose control automata.
 * @author Arend Rensink
 * @version $Revision $
 */
public class ControlBuilder {
    private ControlBuilder() {
        // empty
    }

    /**
     * Constructs a control automaton consisting of a single rule invocation.
     */
    public ControlAutomaton getRuleInvocation(String rulename) {
        ControlAutomaton result = new ControlAutomaton();
        ControlState start = new ControlState(result);
        ControlState end = new ControlState(result);
        end.setSuccess();
        ControlTransition trans = new ControlTransition(start, end);
        result.addState(start);
        result.addState(end);
        result.addTransition(trans);
        result.setStart(start);
        result.setFixed();
        return result;
    }

    /** Returns the singleton instance of this class. */
    static public ControlBuilder getInstance() {
        return instance;
    }

    /** The singleton instance of this class. */
    static private final ControlBuilder instance = new ControlBuilder();
}
