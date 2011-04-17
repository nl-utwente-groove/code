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
import groove.prolog.GrooveEnvironment;
import groove.prolog.builtin.trans.TransPrologCode;
import groove.trans.Rule;
import groove.trans.RuleName;
import groove.view.RuleView;

/**
 * Predicate rule_name(+Name, ?Rule) and rule_name(?Name, +Rule)
 */
public class Predicate_rule extends TransPrologCode {
    @Override
    public int execute(Interpreter interpreter, boolean backtrackMode,
            Term[] args) throws PrologException {
        try {
            Rule rl = (Rule) ((JavaObjectTerm) args[1]).value;
            Term res = AtomTerm.get(rl.getName().toString());
            return interpreter.unify(args[0], res);
        } catch (Exception e) {
            try {
                RuleName ruleName = null;

                try {
                    ruleName = new RuleName(((AtomTerm) args[0]).value);
                } catch (Exception ee) {
                    return FAIL;
                }

                RuleView ruleView =
                    ((GrooveEnvironment) interpreter.getEnvironment()).getGrooveState().getGrammarView().getRuleView(
                        ruleName);

                if (ruleView == null) {
                    return FAIL;
                }

                Term nodeTerm = new JavaObjectTerm(ruleView.toModel());

                return interpreter.unify(args[1], nodeTerm);
            } catch (Exception ee) {
                return FAIL;
            }
        }
    }
}
