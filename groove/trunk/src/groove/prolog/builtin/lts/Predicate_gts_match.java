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
package groove.prolog.builtin.lts;

import gnu.prolog.term.JavaObjectTerm;
import gnu.prolog.term.Term;
import gnu.prolog.vm.BacktrackInfo;
import gnu.prolog.vm.Interpreter;
import gnu.prolog.vm.PrologException;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.trans.RuleEvent;
import groove.util.TransformIterator;

import java.util.Iterator;

/**
 * 
 * 
 * @author Michiel Hendriks
 */
public class Predicate_gts_match extends LtsPrologCode {
    private static class GtsMatchBacktrackInfo extends BacktrackInfo {
        Iterator<RuleEvent> it;
        Term dest;
        int startUndoPosition;

        GtsMatchBacktrackInfo() {
            super(-1, -1);
        }
    }

    private static int nextSolution(Interpreter interpreter,
            GtsMatchBacktrackInfo bi) throws PrologException {
        while (bi.it.hasNext()) {
            Term res = new JavaObjectTerm(bi.it.next());
            int rc = interpreter.unify(bi.dest, res);
            if (rc == FAIL) {
                interpreter.undo(bi.startUndoPosition);
                continue;
            }
            interpreter.pushBacktrackInfo(bi);
            return SUCCESS;
        }
        return FAIL;
    }

    /*
     * (non-Javadoc)
     * @see gnu.prolog.vm.PrologCode#execute(gnu.prolog.vm.Interpreter, boolean,
     * gnu.prolog.term.Term[])
     */
    public int execute(Interpreter interpreter, boolean backtrackMode,
            Term[] args) throws PrologException {
        if (backtrackMode) {
            GtsMatchBacktrackInfo bi =
                (GtsMatchBacktrackInfo) interpreter.popBacktrackInfo();
            interpreter.undo(bi.startUndoPosition);
            return nextSolution(interpreter, bi);
        } else {
            GraphState graphState = getGraphState(args[1]);
            graphState.getTransitionIter();
            Iterator<RuleEvent> it =
                new TransformIterator<GraphTransition,RuleEvent>(
                    graphState.getTransitionIter()) {
                    @Override
                    protected RuleEvent toOuter(GraphTransition from) {
                        return from.getEvent();
                    }
                };
            GtsMatchBacktrackInfo bi = new GtsMatchBacktrackInfo();
            bi.it = it;
            bi.dest = args[2];
            bi.startUndoPosition = interpreter.getUndoPosition();
            return nextSolution(interpreter, bi);
        }
    }
}
