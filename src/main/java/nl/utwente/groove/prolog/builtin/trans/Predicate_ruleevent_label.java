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
package nl.utwente.groove.prolog.builtin.trans;

import gnu.prolog.term.AtomTerm;
import gnu.prolog.term.Term;
import gnu.prolog.vm.Interpreter;
import gnu.prolog.vm.PrologException;
import nl.utwente.groove.transform.RuleEvent;

/**
 * Predicate ruleevent_label(+RuleEvent,?Label)
 * @author Michiel Hendriks
 */
public class Predicate_ruleevent_label extends TransPrologCode {
    @Override
    public int execute(Interpreter interpreter, boolean backtrackMode,
                       Term[] args) throws PrologException {
        RuleEvent re = getRuleEvent(args[0]);
        Term lbl = AtomTerm.get(re.getRule().getQualName().toString());
        return interpreter.unify(args[1], lbl);
    }
}
