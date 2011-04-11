/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2010 University of Twente
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
package groove.prolog.builtin.label;

import gnu.prolog.term.AtomTerm;
import gnu.prolog.term.JavaObjectTerm;
import gnu.prolog.term.Term;
import gnu.prolog.vm.Interpreter;
import gnu.prolog.vm.PrologException;
import groove.graph.DefaultLabel;
import groove.graph.Label;
import groove.prolog.builtin.graph.GraphPrologCode;

/**
 * Predicate label(+Text,?Label), label(?Text,+Label)
 * @author Lesley Wevers
 */
public class Predicate_label extends GraphPrologCode {
    @Override
    public int execute(Interpreter interpreter, boolean backtrackMode,
            Term[] args) throws PrologException {

        // label(+Text,?Label)
        if (args[0] instanceof AtomTerm) {
            try {
                Label label =
                    DefaultLabel.createLabel(((AtomTerm) args[0]).value);

                return interpreter.unify(new JavaObjectTerm(label), args[1]);
            } catch (Exception e) {
                return FAIL;
            }
        }

        // label(?Text,+Label)
        if (args[1] instanceof JavaObjectTerm) {
            try {
                Label label = (Label) ((JavaObjectTerm) args[1]).value;

                return interpreter.unify(AtomTerm.get(label.text()), args[0]);
            } catch (Exception e) {
                return FAIL;
            }
        }

        return FAIL;
    }
}
