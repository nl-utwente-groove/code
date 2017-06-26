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

import gnu.prolog.term.AtomTerm;
import gnu.prolog.term.JavaObjectTerm;
import gnu.prolog.term.Term;
import gnu.prolog.vm.Interpreter;
import gnu.prolog.vm.PrologException;
import groove.grammar.rule.RuleEdge;
import groove.grammar.rule.RuleNode;
import groove.graph.Edge;
import groove.graph.Node;
import groove.transform.BasicEvent;
import groove.transform.RuleEvent;

/**
 * Predicate ruleevent_transpose(+RuleEvent,+Node,?Node)
 * Predicate ruleevent_transpose(+RuleEvent,+Edge,?Edge)
 * @author Michiel Hendriks
 */
public class Predicate_ruleevent_transpose extends TransPrologCode {
    @Override
    public int execute(Interpreter interpreter, boolean backtrackMode,
            Term[] args) throws PrologException {
        RuleEvent re = getRuleEvent(args[0]);
        if (!(re instanceof BasicEvent)) {
            PrologException.domainError(RULEEVENT_ATOM, args[0]);
        }
        BasicEvent spo = (BasicEvent) re;
        if (!(args[1] instanceof JavaObjectTerm)) {
            PrologException.instantiationError();
        }
        Object arg1 = ((JavaObjectTerm) args[1]).value;
        Object arg1Transposed = null;
        if (arg1 instanceof Node) {
            arg1Transposed = spo.getAnchorMap().getNode((RuleNode) arg1);
        } else if (arg1 instanceof Edge) {
            arg1Transposed = spo.getAnchorMap().getEdge((RuleEdge) arg1);
        } else {
            PrologException.domainError(AtomTerm.get("node_edge"), args[1]);
        }
        if (arg1Transposed == null) {
            return FAIL;
        }
        Term res = new JavaObjectTerm(arg1Transposed);
        return interpreter.unify(args[2], res);
    }
}
