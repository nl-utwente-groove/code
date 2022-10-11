/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2010 University of Twente
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
package nl.utwente.groove.prolog.builtin;

import nl.utwente.groove.annotation.Signature;
import nl.utwente.groove.annotation.ToolTipBody;
import nl.utwente.groove.annotation.ToolTipPars;
import nl.utwente.groove.prolog.builtin.rule.Predicate_is_rule;
import nl.utwente.groove.prolog.builtin.rule.Predicate_rule;
import nl.utwente.groove.prolog.builtin.rule.Predicate_rule_lhs;
import nl.utwente.groove.prolog.builtin.rule.Predicate_rule_name;
import nl.utwente.groove.prolog.builtin.rule.Predicate_rule_priority;
import nl.utwente.groove.prolog.builtin.rule.Predicate_rule_rhs;

/** Rule-based GROOVE Prolog predicates.
 * Documentation reading guide:
 * <li> +     The argument shall be instantiated.
 * <li> ?     The argument shall be instantiated or a variable.
 * <li> @     The argument shall remain unaltered.
 * <li> -     The argument shall be a variable that will be instantiated
 */
@SuppressWarnings("all")
public class RulePredicates extends GroovePredicates {
    @Signature({"RuleName", "?"})
    public void rule_name_1() {
        s(Predicate_rule_name.class, 1);
    }

    @Signature({"RuleName", "Rule", "+?", "?+"})
    public void rule_2() {
        s(Predicate_rule.class, 2);
    }

    @ToolTipBody("Tests if the argument is a JavaObjectTerm with a Rule")
    @Signature({"RuleEvent", "+"})
    public void is_rule_1() {
        s(Predicate_is_rule.class, 1);
    }

    @ToolTipBody("Retrieves the priority of the rule")
    @Signature({"Rule", "Integer", "+?"})
    @ToolTipPars({"the rule", "the priority"})
    //    % @see nl.utwente.groove.trans.Rule#getPriority()
    public void rule_priority_2() {
        s(Predicate_rule_priority.class, 2);
    }

    @ToolTipBody({"Retrieves the left hand side of this Rule.",
        "Note: this does not use the same nodes as the current graph."})
    @Signature({"Rule", "Graph", "+?"})
    @ToolTipPars({"the rule", "the graph"})
    //    % @see nl.utwente.groove.trans.Rule#getLhs()
    public void rule_lhs_2() {
        s(Predicate_rule_lhs.class, 2);
    }

    @ToolTipBody({"Retrieves the right hand side of this Rule.",
        "Note: this does not use the same nodes as the current graph."})
    @Signature({"Rule", "Graph", "+?"})
    @ToolTipPars({"the rule", "the graph"})
    //    % @see nl.utwente.groove.trans.Rule#getRhs()
    public void rule_rhs_2() {
        s(Predicate_rule_rhs.class, 2);
    }

}
