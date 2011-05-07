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

/** Transition-based GROOVE predicates. */
@SuppressWarnings("all")
public class TransPredicates extends GroovePredicates {
    @ToolTip("Success if the argument is a JavaObjectTerm with a RuleEvent")
    @Signature({"RuleEvent", "+"})
    public void is_ruleevent_1() {
        s(":-build_in(is_ruleevent/1,'groove.prolog.builtin.trans.Predicate_is_ruleevent').");
    }

    @ToolTip("Success if the argument is a JavaObjectTerm with a RuleMatch")
    @Signature({"RuleMatch", "+"})
    public void is_rulematch_1() {
        s(":-build_in(is_rulematch/1,'groove.prolog.builtin.trans.Predicate_is_rulematch').");
    }

    @ToolTip("Get the currently selected rule event.")
    @Signature({"RuleEvent", "?"})
    @Param({"the rule event"})
    public void active_ruleevent_1() {
        s(":-build_in(active_ruleevent/1,'groove.prolog.builtin.trans.Predicate_active_ruleevent').");
    }

    @ToolTip("The label of a rule event")
    @Signature({"RuleEvent", "Label", "+?"})
    @Param({"the rule event", "the label"})
    //    % @see groove.trans.RuleEvent#getLabel()
    public void ruleevent_label_2() {
        s(":-build_in(ruleevent_label/2,'groove.prolog.builtin.trans.Predicate_ruleevent_label').");
    }

    @ToolTip("The rule associated with this event")
    @Signature({"RuleEvent", "Rule", "+?"})
    @Param({"the rule event", "the rule"})
    //    % @see groove.trans.RuleEvent#getRule()
    public void ruleevent_rule_2() {
        s(":-build_in(ruleevent_rule/2,'groove.prolog.builtin.trans.Predicate_ruleevent_rule').");
    }

    @ToolTip({
        "Translate a node/edge in the rule's graphs to a node/edge in the ruleevent's graph.",
        "Fails when the node/edge does not have a mapping"})
    @Signature({"RuleEvent", "NodeEdge", "NodeEdge", "++?"})
    @Param({"the rule event", "node/edge as used in the rule's graph",
        "node/edge in the graph"})
    public void ruleevent_transpose_3() {
        s(":-build_in(ruleevent_transpose/3,'groove.prolog.builtin.trans.Predicate_ruleevent_transpose').");
    }

    @ToolTip("Erased edges in this event")
    @Signature({"RuleEvent", "Edge", "+?"})
    @Param({"the rule event", "the edge"})
    public void ruleevent_erased_edge_2() {
        s(":-build_in(ruleevent_erased_edge/2,'groove.prolog.builtin.trans.Predicate_ruleevent_erased_edge').");
    }

    @ToolTip("Erased nodes in this event")
    @Signature({"RuleEvent", "Node", "+?"})
    @Param({"the rule event", "the node"})
    public void ruleevent_erased_node_2() {
        s(":-build_in(ruleevent_erased_node/2,'groove.prolog.builtin.trans.Predicate_ruleevent_erased_node').");
    }

    @ToolTip("Created edges in this event.")
    @Signature({"RuleEvent", "Edge", "+?"})
    @Param({"the rule event", "the edge"})
    public void ruleevent_created_edge_2() {
        s(":-build_in(ruleevent_created_edge/2,'groove.prolog.builtin.trans.Predicate_ruleevent_created_edge').");
    }

    @ToolTip("Created nodes in this event.")
    @Signature({"RuleEvent", "Node", "+?"})
    @Param({"the rule event", "the node"})
    public void ruleevent_created_node_2() {
        s(":-build_in(ruleevent_created_node/2,'groove.prolog.builtin.trans.Predicate_ruleevent_created_node').");
    }

    @ToolTip("The rule match")
    @Signature({"RuleEvent", "Graph", "RuleMatch", "++?"})
    @Param({"the rule event", "the graph to match against", "the rule match"})
    //    % @see groove.trans.RuleEvent#getMatch()
    public void ruleevent_match_3() {
        s(":-build_in(ruleevent_match/3,'groove.prolog.builtin.trans.Predicate_ruleevent_match').");
    }

    @Signature({"RuleEvent", "RuleMatch", "+?"})
    public void ruleevent_match_2() {
        s("ruleevent_match(RE,RM):-state(GS),state_graph(GS,G),ruleevent_match(RE,G,RM).");
    }

    @ToolTip("Get all current rule matches")
    @Signature({"RuleMatch", "?"})
    public void rulematch_1() {
        s("rulematch(RM):-state(GS),state_graph(GS,G),state_ruleevent(GS,RE),ruleevent_match(RE,G,RM).");
    }

    @ToolTip("The edges in a rule match")
    @Signature({"RuleMatch", "Edge", "+?"})
    @Param({"the rulematch", "the edge in the match"})
    public void rulematch_edge_2() {
        s(":-build_in(rulematch_edge/2,'groove.prolog.builtin.trans.Predicate_rulematch_edge').");
    }

    @ToolTip("The nodes in a rule match")
    @Signature({"RuleMatch", "Node", "+?"})
    @Param({"the rulematch", "the node in the match"})
    public void rulematch_node_2() {
        s(":-build_in(rulematch_node/2,'groove.prolog.builtin.trans.Predicate_rulematch_node').");
    }

    @ToolTip("The rule which was used in this match")
    @Signature({"RuleMatch", "Rule", "+?"})
    @Param({"the rulematch", "the rule"})
    public void rulematch_rule_2() {
        s(":-build_in(rulematch_rule/2,'groove.prolog.builtin.trans.Predicate_rulematch_rule').");
    }

}
