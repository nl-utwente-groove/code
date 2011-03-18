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
import groove.trans.HostNode;
import groove.trans.RuleEvent;
import groove.trans.SPOEvent;

import java.util.HashSet;
import java.util.Set;

/**
 * 
 * 
 * @author Michiel Hendriks
 */
public class Predicate_ruleevent_created_edge extends TransPrologCode {
    /*
     * (non-Javadoc)
     * @see gnu.prolog.vm.PrologCode#execute(gnu.prolog.vm.Interpreter, boolean,
     * gnu.prolog.term.Term[])
     */
    public int execute(Interpreter interpreter, boolean backtrackMode,
            Term[] args) throws PrologException {
        if (backtrackMode) {
            PrologCollectionIterator it =
                (PrologCollectionIterator) interpreter.popBacktrackInfo();
            interpreter.undo(it.getUndoPosition());
            return it.nextSolution(interpreter);
        } else {
            RuleEvent re = getRuleEvent(args[0]);
            PrologCollectionIterator it;
            if (re instanceof SPOEvent) {
                SPOEvent se = (SPOEvent) re;
                Set<HostEdge> edges = new HashSet<HostEdge>();
                Set<HostNode> createdNodes = new HashSet<HostNode>();
                createdNodes.addAll(se.getCreatedNodes(new HashSet<HostNode>(
                    se.getAnchorMap().nodeMap().values())));
                // combine the created edges from both new and old nodes
                edges.addAll(se.getComplexCreatedEdges(createdNodes.iterator()));
                edges.addAll(se.getSimpleCreatedEdges());
                it =
                    new PrologCollectionIterator(edges, args[1],
                        interpreter.getUndoPosition());
            } else {
                it =
                    new PrologCollectionIterator(re.getSimpleCreatedEdges(),
                        args[1], interpreter.getUndoPosition());
            }
            return it.nextSolution(interpreter);
        }
    }
}
