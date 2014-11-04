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
import gnu.prolog.vm.PrologException;
import groove.grammar.Rule;
import groove.prolog.builtin.graph.GraphPrologCode;
import groove.transform.RuleEvent;

/**
 * This class contains some utility methods to extract Java objects from prolog terms
 * 
 * @author Michiel Hendriks
 */
public abstract class TransPrologCode extends GraphPrologCode {
    /**
     * Atom term "rule_event"
     */
    public static final AtomTerm RULEEVENT_ATOM = AtomTerm.get("rule_event");

    /**
     * Atom term "rule_match"
     */
    public static final AtomTerm RULEMATCH_ATOM = AtomTerm.get("rule_match");

    /**
     * Atom term "rule"
     */
    public static final AtomTerm RULE_ATOM = AtomTerm.get("rule");

    /**
     * Get a rule event from a term representing a rule event
     * @param term      A term representing a rule event
     * @return          A rule event
     */
    public static final RuleEvent getRuleEvent(Term term)
        throws PrologException {
        if (term instanceof JavaObjectTerm) {
            JavaObjectTerm jot = (JavaObjectTerm) term;
            if (!(jot.value instanceof RuleEvent)) {
                PrologException.domainError(RULEEVENT_ATOM, term);
            }
            return (RuleEvent) jot.value;
        } else {
            PrologException.typeError(RULEEVENT_ATOM, term);
        }
        return null;

    }

    /**
     * Get a rule from a term representing a rule
     * @param term      A term representing a rule
     * @return          A rule
     */
    public static final Rule getRule(Term term) throws PrologException {
        if (term instanceof JavaObjectTerm) {
            JavaObjectTerm jot = (JavaObjectTerm) term;
            if (!(jot.value instanceof Rule)) {
                PrologException.domainError(RULE_ATOM, term);
            }
            return (Rule) jot.value;
        } else {
            PrologException.typeError(RULE_ATOM, term);
        }
        return null;

    }

}
