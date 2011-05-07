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
package groove.prolog.builtin;

import groove.prolog.annotation.Param;
import groove.prolog.annotation.Signature;
import groove.prolog.annotation.ToolTip;

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
        s(":-build_in(rule_name/1,'groove.prolog.builtin.rule.Predicate_rule_name').");
    }

    @Signature({"RuleName", "Rule", "+?", "?+"})
    public void rule_2() {
        s(":-build_in(rule/2,'groove.prolog.builtin.rule.Predicate_rule').");
    }

    @Signature({"RuleName", "+"})
    public void rule_confluent_1() {
        s(":-build_in(rule_confluent/1,'groove.prolog.builtin.rule.Predicate_rule_confluent').");
    }

    @ToolTip("Tests if the argument is a JavaObjectTerm with a Rule")
    @Signature({"RuleEvent", "+"})
    public void is_rule_1() {
        s(":-build_in(is_rule/1,'groove.prolog.builtin.rule.Predicate_is_rule').");
    }

    @ToolTip("Retrieves the priority of the rule")
    @Signature({"Rule", "Integer", "+?"})
    @Param({"the rule", "the priority"})
    //    % @see groove.trans.Rule#getPriority()
    public void rule_priority_2() {
        s(":-build_in(rule_priority/2, 'groove.prolog.builtin.rule.Predicate_rule_priority').");
    }

    @ToolTip({"Retrieves the left hand side of this Rule.",
        "Note: this does not use the same nodes as the current graph."})
    @Signature({"Rule", "Graph", "+?"})
    @Param({"the rule", "the graph"})
    //    % @see groove.trans.Rule#getLhs()
    public void rule_lhs_2() {
        s(":-build_in(rule_lhs/2, 'groove.prolog.builtin.rule.Predicate_rule_lhs').");
    }

    @ToolTip({"Retrieves the right hand side of this Rule.",
        "Note: this does not use the same nodes as the current graph."})
    @Signature({"Rule", "Graph", "+?"})
    @Param({"the rule", "the graph"})
    //    % @see groove.trans.Rule#getRhs()
    public void rule_rhs_2() {
        s(":-build_in(rule_rhs/2, 'groove.prolog.builtin.rule.Predicate_rule_rhs').");
    }

    @Signature({"RuleName", "+"})
    public void confluent_rule_name_1() {
        s("confluent_rule_name(RN) :- rule_name(RN), rule_confluent(RN).");
    }

    @Signature({"Rule", "?"})
    public void confluent_rule_1() {
        s("confluent_rule(R) :- confluent_rule_name(RN), rule(RN,R).");
    }
}
