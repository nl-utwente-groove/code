/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.prolog.builtin.type;

import gnu.prolog.term.JavaObjectTerm;
import gnu.prolog.term.Term;
import gnu.prolog.vm.Interpreter;
import gnu.prolog.vm.PrologCollectionIterator;
import gnu.prolog.vm.PrologException;
import nl.utwente.groove.grammar.type.TypeGraph;
import nl.utwente.groove.grammar.type.TypeLabel;
import nl.utwente.groove.grammar.type.TypeNode;
import nl.utwente.groove.prolog.GrooveEnvironment;
import nl.utwente.groove.prolog.builtin.graph.GraphPrologCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Predicate direct_subtype(+TypeGraph,+Label,+Label)
 * @author Lesley Wevers
 */
public class Predicate_direct_subtype extends GraphPrologCode {
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
                TypeNode type1 = typeGraph.getNode(l1);
                List<TypeLabel> supertypes1 = new ArrayList<>();
                if (type1 != null) {
                    collectLabels(supertypes1,
                        typeGraph.getDirectSupertypeMap().get(type1));
                }
                PrologCollectionIterator it =
                    new PrologCollectionIterator(supertypes1, args[2],
                        interpreter.getUndoPosition());
                return it.nextSolution(interpreter);
            } catch (Exception e) {
                try {
                    TypeGraph typeGraph =
                        (TypeGraph) ((JavaObjectTerm) args[0]).value;
                    TypeLabel l2 = (TypeLabel) ((JavaObjectTerm) args[2]).value;
                    TypeNode type2 = typeGraph.getNode(l2);
                    List<TypeLabel> subtypes2 = new ArrayList<>();
                    if (type2 != null) {
                        collectLabels(subtypes2,
                            typeGraph.getDirectSubtypeMap().get(type2));
                    }
                    PrologCollectionIterator it =
                        new PrologCollectionIterator(subtypes2, args[1],
                            interpreter.getUndoPosition());
                    return it.nextSolution(interpreter);
                } catch (Exception ex) {
                    return FAIL;
                }
            }
        }
    }

    private void collectLabels(List<TypeLabel> result, Set<TypeNode> nodes) {
        for (TypeNode supertype : nodes) {
            result.add(supertype.label());
        }
    }
}