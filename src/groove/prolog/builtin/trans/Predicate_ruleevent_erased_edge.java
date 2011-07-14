/*
 * Groove Prolog Interface
 * Copyright (C) 2009 Michiel Hendriks, University of Twente
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package groove.prolog.builtin.trans;

import gnu.prolog.term.Term;
import gnu.prolog.vm.Interpreter;
import gnu.prolog.vm.PrologCollectionIterator;
import gnu.prolog.vm.PrologException;
import groove.trans.HostEdge;
import groove.trans.HostGraph;
import groove.trans.RuleApplicationRecord;
import groove.trans.RuleEvent;

/**
 * Predicate erased_edge(+RuleEvent,+Graph,?Edge)
 * @author Michiel Hendriks
 */
public class Predicate_ruleevent_erased_edge extends TransPrologCode {
    @Override
    public int execute(Interpreter interpreter, boolean backtrackMode,
            Term[] args) throws PrologException {
        if (backtrackMode) {
            PrologCollectionIterator it =
                (PrologCollectionIterator) interpreter.popBacktrackInfo();
            interpreter.undo(it.getUndoPosition());
            return it.nextSolution(interpreter);
        } else {
            RuleEvent event = getRuleEvent(args[0]);
            HostGraph graph = (HostGraph) getGraph(args[1]);
            RuleApplicationRecord record = event.recordApplication(graph);
            Iterable<HostEdge> erasedEdges = record.getErasedEdges();
            if (erasedEdges == null) {
                return FAIL;
            } else {
                PrologCollectionIterator it =
                    new PrologCollectionIterator(erasedEdges, args[2],
                        interpreter.getUndoPosition());
                return it.nextSolution(interpreter);
            }
        }
    }

}
