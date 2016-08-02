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
package groove.prolog.builtin.rule;

import gnu.prolog.term.AtomTerm;
import gnu.prolog.term.JavaObjectTerm;
import gnu.prolog.term.Term;
import gnu.prolog.vm.Interpreter;
import gnu.prolog.vm.PrologException;
import groove.annotation.Signature;
import groove.annotation.ToolTipBody;
import groove.grammar.Action;
import groove.grammar.QualName;
import groove.grammar.Rule;
import groove.prolog.GrooveEnvironment;
import groove.prolog.builtin.trans.TransPrologCode;

/**
 * Predicate rule_name(+Name, ?Rule)
 * Predicate rule_name(?Name, +Rule)
 * @author Lesley Wevers
 */
@Signature({"RuleName", "Rule", "+?", "?+"})
@ToolTipBody("Establishes the one-to-one relation between rule names and rules in the current grammar")
public class Predicate_rule extends TransPrologCode {
    @Override
    public int execute(Interpreter interpreter, boolean backtrackMode, Term[] args)
        throws PrologException {
        try {
            Action rl = (Action) ((JavaObjectTerm) args[1]).value;
            Term res = AtomTerm.get(rl.getQualName()
                .toString());
            return interpreter.unify(args[0], res);
        } catch (Exception e) {
            try {
                QualName ruleName = null;

                try {
                    ruleName = QualName.parse(((AtomTerm) args[0]).value);
                } catch (Exception ee) {
                    return FAIL;
                }

                Rule rule = ((GrooveEnvironment) interpreter.getEnvironment()).getGrooveState()
                    .getGraphGrammar()
                    .getRule(ruleName);

                if (rule == null) {
                    return FAIL;
                }

                Term nodeTerm = new JavaObjectTerm(rule);

                return interpreter.unify(args[1], nodeTerm);
            } catch (Exception ee) {
                return FAIL;
            }
        }
    }
}
