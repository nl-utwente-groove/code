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

import gnu.prolog.term.JavaObjectTerm;
import gnu.prolog.term.Term;
import gnu.prolog.vm.Interpreter;
import gnu.prolog.vm.PrologCollectionIterator;
import gnu.prolog.vm.PrologException;
import groove.graph.TypeGraph;
import groove.graph.TypeLabel;
import groove.prolog.GrooveEnvironment;
import groove.prolog.builtin.graph.GraphPrologCode;

/**
 * Predicate subtype(+TypeGraph,+Label,+Label)
 * @author Lesley Wevers
 */
public class Predicate_subtype extends GraphPrologCode {
    @Override
    public int execute(Interpreter interpreter, boolean backtrackMode,
            Term[] args) throws PrologException {
        if (backtrackMode) {
            PrologCollectionIterator it =
                (PrologCollectionIterator) interpreter.popBacktrackInfo();
            interpreter.undo(it.getUndoPosition());
            return it.nextSolution(interpreter);
        } else {
            if (!(interpreter.getEnvironment() instanceof GrooveEnvironment)) {
                GrooveEnvironment.invalidEnvironment();
            }

            try {
                TypeGraph typeGraph =
                    (TypeGraph) ((JavaObjectTerm) args[0]).value;
                TypeLabel l1 = (TypeLabel) ((JavaObjectTerm) args[1]).value;

                PrologCollectionIterator it =
                    new PrologCollectionIterator(
                        typeGraph.getLabelStore().getSupertypes(l1), args[2],
                        interpreter.getUndoPosition());
                return it.nextSolution(interpreter);
            } catch (Exception e) {
                try {
                    TypeGraph typeGraph =
                        (TypeGraph) ((JavaObjectTerm) args[0]).value;
                    TypeLabel l2 = (TypeLabel) ((JavaObjectTerm) args[2]).value;

                    PrologCollectionIterator it =
                        new PrologCollectionIterator(
                            typeGraph.getLabelStore().getSubtypes(l2), args[1],
                            interpreter.getUndoPosition());
                    return it.nextSolution(interpreter);
                } catch (Exception ex) {
                    return FAIL;
                }
            }
        }
    }
}