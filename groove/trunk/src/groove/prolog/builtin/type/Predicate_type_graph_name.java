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
package groove.prolog.builtin.type;

import gnu.prolog.term.Term;
import gnu.prolog.vm.Interpreter;
import gnu.prolog.vm.PrologException;
import groove.prolog.GrooveEnvironment;
import groove.prolog.builtin.graph.GraphPrologCode;
import groove.prolog.util.PrologStringCollectionIterator;

import java.util.Set;

/**
 * Predicate type_graph_name(?Name)
 * @author Lesley Wevers
 */
public class Predicate_type_graph_name extends GraphPrologCode {
    @Override
    public int execute(Interpreter interpreter, boolean backtrackMode, Term[] args)
        throws PrologException {
        if (backtrackMode) {
            PrologStringCollectionIterator it =
                (PrologStringCollectionIterator) interpreter.popBacktrackInfo();
            interpreter.undo(it.getUndoPosition());
            return it.nextSolution(interpreter);
        } else {
            if (!(interpreter.getEnvironment() instanceof GrooveEnvironment)) {
                GrooveEnvironment.invalidEnvironment();
            }

            Set<String> typeNames =
                ((GrooveEnvironment) interpreter.getEnvironment()).getGrooveState().getGraphGrammar().getTypeGraph().getComponentMap().keySet();

            if (typeNames == null) {
                return FAIL;
            }

            PrologStringCollectionIterator it =
                new PrologStringCollectionIterator(typeNames, args[0],
                    interpreter.getUndoPosition());
            return it.nextSolution(interpreter);
        }
    }
}
