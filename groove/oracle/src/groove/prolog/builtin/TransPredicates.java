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

import groove.annotation.Signature;
import groove.annotation.ToolTipBody;
import groove.annotation.ToolTipPars;

/** Transition-based GROOVE predicates. */
@SuppressWarnings("all")
public class TransPredicates extends GroovePredicates {
    @ToolTipBody("Success if the argument is a JavaObjectTerm with a RuleEvent")
    @Signature({"RuleEvent", "+"})
    public void is_ruleevent_1() {
        s(":-build_in(is_ruleevent/1,'groove.prolog.builtin.trans.Predicate_is_ruleevent').");
    }

    @ToolTipBody("Success if the argument is a JavaObjectTerm with a RuleMatch")
    @Signature({"RuleMatch", "+"})
    public void is_rulematch_1() {
        s(":-build_in(is_rulematch/1,'groove.prolog.builtin.trans.Predicate_is_rulematch').");
    }

    @ToolTipBody("Get the currently selected rule event.")
    @Signature({"RuleEvent", "?"})
    @ToolTipPars({"the rule event"})
    public void active_ruleevent_1() {
        s(":-build_in(active_ruleevent/1,'groove.prolog.builtin.trans.Predicate_active_ruleevent').");
    }

    @ToolTipBody("The label of a rule event")
    @Signature({"RuleEvent", "Label", "+?"})
    @ToolTipPars({"the rule event", "the label"})
    //    % @see groove.trans.RuleEvent#getLabel()
    public void ruleevent_label_2() {
        s(":-build_in(ruleevent_label/2,'groove.prolog.builtin.trans.Predicate_ruleevent_label').");
    }

    @ToolTipBody("The rule associated with this event")
    @Signature({"RuleEvent", "Rule", "+?"})
    @ToolTipPars({"the rule event", "the rule"})
    //    % @see groove.trans.RuleEvent#getRule()
    public void ruleevent_rule_2() {
        s(":-build_in(ruleevent_rule/2,'groove.prolog.builtin.trans.Predicate_ruleevent_rule').");
    }

    @ToolTipBody({
        "Translate a node/edge in the rule's graphs to a node/edge in the ruleevent's graph.",
        "Fails when the node/edge does not have a mapping"})
    @Signature({"RuleEvent", "NodeEdge", "NodeEdge", "++?"})
    @ToolTipPars({"the rule event", "node/edge as used in the rule's graph",
        "node/edge in the graph"})
    public void ruleevent_transpose_3() {
        s(":-build_in(ruleevent_transpose/3,'groove.prolog.builtin.trans.Predicate_ruleevent_transpose').");
    }

    //    @ToolTipBody("Erased edges in this event, with respect to a given host graph.")
    //    @Signature({"RuleEvent", "Graph", "Edge", "++?"})
    //    @ToolTipPars({"the rule event", "the host graph", "the edge"})
    //    public void ruleevent_erased_edge_3() {
    //        s(":-build_in(ruleevent_erased_edge/3,'groove.prolog.builtin.trans.Predicate_ruleevent_erased_edge').");
    //    }
    //
    //    @ToolTipBody("Erased nodes in this event, with respect to a given host graph.")
    //    @Signature({"RuleEvent", "Graph", "Node", "++?"})
    //    @ToolTipPars({"the rule event", "the host graph", "the node"})
    //    public void ruleevent_erased_node_3() {
    //        s(":-build_in(ruleevent_erased_node/3,'groove.prolog.builtin.trans.Predicate_ruleevent_erased_node').");
    //    }
    //
    //    @ToolTipBody("Created edges in this event, with respect to a given host graph.")
    //    @Signature({"RuleEvent", "Graph", "Edge", "++?"})
    //    @ToolTipPars({"the rule event", "the host graph", "the edge"})
    //    public void ruleevent_created_edge_3() {
    //        s(":-build_in(ruleevent_created_edge/3,'groove.prolog.builtin.trans.Predicate_ruleevent_created_edge').");
    //    }
    //
    //    @ToolTipBody("Created nodes in this event, with respect to a given host graph.")
    //    @Signature({"RuleEvent", "Graph", "Node", "++?"})
    //    @ToolTipPars({"the rule event", "the host graph", "the node"})
    //    public void ruleevent_created_node_3() {
    //        s(":-build_in(ruleevent_created_node/3,'groove.prolog.builtin.trans.Predicate_ruleevent_created_node').");
    //    }

    @ToolTipBody("The rule match")
    @Signature({"RuleEvent", "Graph", "RuleMatch", "++?"})
    @ToolTipPars({"the rule event", "the graph to match against", "the rule match"})
    //    % @see groove.trans.RuleEvent#getMatch()
    public void ruleevent_match_3() {
        s(":-build_in(ruleevent_match/3,'groove.prolog.builtin.trans.Predicate_ruleevent_match').");
    }

    @Signature({"RuleEvent", "RuleMatch", "+?"})
    public void ruleevent_match_2() {
        s("ruleevent_match(RE,RM):-state(GS),state_graph(GS,G),ruleevent_match(RE,G,RM).");
    }

    @ToolTipBody("Get all current rule matches")
    @Signature({"RuleMatch", "?"})
    public void rulematch_1() {
        s("rulematch(RM):-state(GS),state_graph(GS,G),state_ruleevent(GS,RE),ruleevent_match(RE,G,RM).");
    }

    @ToolTipBody("The edges in a rule match")
    @Signature({"RuleMatch", "Edge", "+?"})
    @ToolTipPars({"the rulematch", "the edge in the match"})
    public void rulematch_edge_2() {
        s(":-build_in(rulematch_edge/2,'groove.prolog.builtin.trans.Predicate_rulematch_edge').");
    }

    @ToolTipBody("The nodes in a rule match")
    @Signature({"RuleMatch", "Node", "+?"})
    @ToolTipPars({"the rulematch", "the node in the match"})
    public void rulematch_node_2() {
        s(":-build_in(rulematch_node/2,'groove.prolog.builtin.trans.Predicate_rulematch_node').");
    }

    @ToolTipBody("The rule which was used in this match")
    @Signature({"RuleMatch", "Rule", "+?"})
    @ToolTipPars({"the rulematch", "the rule"})
    public void rulematch_rule_2() {
        s(":-build_in(rulematch_rule/2,'groove.prolog.builtin.trans.Predicate_rulematch_rule').");
    }

}
