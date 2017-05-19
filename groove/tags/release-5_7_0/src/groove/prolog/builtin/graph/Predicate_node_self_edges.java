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
package groove.prolog.builtin.graph;

import gnu.prolog.term.AtomTerm;
import gnu.prolog.term.CompoundTerm;
import gnu.prolog.term.JavaObjectTerm;
import gnu.prolog.term.Term;
import gnu.prolog.term.VariableTerm;
import gnu.prolog.vm.BacktrackInfo;
import gnu.prolog.vm.Interpreter;
import gnu.prolog.vm.PrologException;
import gnu.prolog.vm.TermConstants;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Node;
import groove.prolog.util.PrologUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Predicate node_self_edges(+Graph,+Node,?Edge)
 * @author Michiel Hendriks
 */
public class Predicate_node_self_edges extends GraphPrologCode {
    private static class SelfEdgesBacktrackInfo extends BacktrackInfo {
        int startUndoPosition;

        Graph graph;
        Iterator<? extends Node> nodes;
        List<String> labels; // can be null when argList isn't

        Term argNode;
        Term argList; // can be null when labels isn't

        SelfEdgesBacktrackInfo() {
            super(-1, -1);
        }
    }

    @Override
    public int execute(Interpreter interpreter, boolean backtrackMode,
            Term[] args) throws PrologException {
        if (backtrackMode) {
            SelfEdgesBacktrackInfo bi =
                (SelfEdgesBacktrackInfo) interpreter.popBacktrackInfo();
            interpreter.undo(bi.startUndoPosition);
            return nextSolution(interpreter, bi);
        } else {
            SelfEdgesBacktrackInfo bi = new SelfEdgesBacktrackInfo();
            bi.startUndoPosition = interpreter.getUndoPosition();
            bi.graph = getGraph(args[0]);
            bi.nodes = bi.graph.nodeSet().iterator();
            bi.argNode = args[1];
            if (args[2] instanceof VariableTerm) {
                bi.argList = args[2];
            } else if (args[2] instanceof CompoundTerm) {
                bi.labels = new ArrayList<>();
                Set<Term> termSet = new LinkedHashSet<>();
                PrologUtils.getTermSet(args[2], termSet);
                for (Term term : termSet) {
                    if (term instanceof AtomTerm) {
                        bi.labels.add(((AtomTerm) term).value);
                    } else {
                        PrologException.typeError(TermConstants.atomAtom, term);
                    }
                }
            } else {
                PrologException.typeError(TermConstants.compoundAtom, args[2]);
            }
            return nextSolution(interpreter, bi);
        }
    }

    /**
     * Returns the next solution
     */
    protected int nextSolution(Interpreter interpreter,
            SelfEdgesBacktrackInfo bi) throws PrologException {
        while (bi.nodes.hasNext()) {
            Node n = bi.nodes.next();
            List<String> edgeLabels = new ArrayList<>();
            for (Edge edge : bi.graph.outEdgeSet(n)) {
                if (edge.target() == n && edge.source() == n) {
                    edgeLabels.add(edge.label().text());
                }
            }
            if (bi.labels != null) {
                if (!isValidList(bi.labels, edgeLabels)) {
                    continue;
                }
            } else if (bi.argList != null) {
                Term lblList =
                    CompoundTerm.getList(PrologUtils.createJOTlist(edgeLabels));
                int rc = interpreter.unify(bi.argList, lblList);
                if (rc == FAIL) {
                    interpreter.undo(bi.startUndoPosition);
                    continue;
                }
            }
            int rc = interpreter.unify(bi.argNode, new JavaObjectTerm(n));
            if (rc == FAIL) {
                interpreter.undo(bi.startUndoPosition);
                continue;
            }
            interpreter.pushBacktrackInfo(bi);
            return SUCCESS;
        }
        return FAIL;
    }

    /**
     * Check if this is a valid list
     */
    protected boolean isValidList(List<String> testSet, List<String> curSet) {
        return curSet.containsAll(testSet);
    }
}
