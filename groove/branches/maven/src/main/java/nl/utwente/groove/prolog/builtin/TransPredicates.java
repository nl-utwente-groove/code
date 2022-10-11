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
import nl.utwente.groove.prolog.builtin.trans.Predicate_active_ruleevent;
import nl.utwente.groove.prolog.builtin.trans.Predicate_is_ruleevent;
import nl.utwente.groove.prolog.builtin.trans.Predicate_is_rulematch;
import nl.utwente.groove.prolog.builtin.trans.Predicate_ruleevent_label;
import nl.utwente.groove.prolog.builtin.trans.Predicate_ruleevent_match;
import nl.utwente.groove.prolog.builtin.trans.Predicate_ruleevent_rule;
import nl.utwente.groove.prolog.builtin.trans.Predicate_ruleevent_transpose;
import nl.utwente.groove.prolog.builtin.trans.Predicate_rulematch_edge;
import nl.utwente.groove.prolog.builtin.trans.Predicate_rulematch_node;
import nl.utwente.groove.prolog.builtin.trans.Predicate_rulematch_rule;

/** Transition-based GROOVE predicates. */
@SuppressWarnings("all")
public class TransPredicates extends GroovePredicates {
    @ToolTipBody("Success if the argument is a JavaObjectTerm with a RuleEvent")
    @Signature({"RuleEvent", "+"})
    public void is_ruleevent_1() {
        s(Predicate_is_ruleevent.class, 1);
    }

    @ToolTipBody("Success if the argument is a JavaObjectTerm with a RuleMatch")
    @Signature({"RuleMatch", "+"})
    public void is_rulematch_1() {
        s(Predicate_is_rulematch.class, 1);
    }

    @ToolTipBody("Get the currently selected rule event.")
    @Signature({"RuleEvent", "?"})
    @ToolTipPars({"the rule event"})
    public void active_ruleevent_1() {
        s(Predicate_active_ruleevent.class, 1);
    }

    @ToolTipBody("The label of a rule event")
    @Signature({"RuleEvent", "Label", "+?"})
    @ToolTipPars({"the rule event", "the label"})
    //    % @see nl.utwente.groove.trans.RuleEvent#getLabel()
    public void ruleevent_label_2() {
        s(Predicate_ruleevent_label.class, 2);
    }

    @ToolTipBody("The rule associated with this event")
    @Signature({"RuleEvent", "Rule", "+?"})
    @ToolTipPars({"the rule event", "the rule"})
    //    % @see nl.utwente.groove.trans.RuleEvent#getRule()
    public void ruleevent_rule_2() {
        s(Predicate_ruleevent_rule.class, 2);
    }

    @ToolTipBody({
        "Translate a node/edge in the rule's graphs to a node/edge in the ruleevent's graph.",
        "Fails when the node/edge does not have a mapping"})
    @Signature({"RuleEvent", "NodeEdge", "NodeEdge", "++?"})
    @ToolTipPars({"the rule event", "node/edge as used in the rule's graph",
        "node/edge in the graph"})
    public void ruleevent_transpose_3() {
        s(Predicate_ruleevent_transpose.class, 3);
    }

    @ToolTipBody("The rule match")
    @Signature({"RuleEvent", "Graph", "RuleMatch", "++?"})
    @ToolTipPars({"the rule event", "the graph to match against", "the rule match"})
    //    % @see nl.utwente.groove.trans.RuleEvent#getMatch()
    public void ruleevent_match_3() {
        s(Predicate_ruleevent_match.class, 3);
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
        s(Predicate_rulematch_edge.class, 2);
    }

    @ToolTipBody("The nodes in a rule match")
    @Signature({"RuleMatch", "Node", "+?"})
    @ToolTipPars({"the rulematch", "the node in the match"})
    public void rulematch_node_2() {
        s(Predicate_rulematch_node.class, 2);
    }

    @ToolTipBody("The rule which was used in this match")
    @Signature({"RuleMatch", "Rule", "+?"})
    @ToolTipPars({"the rulematch", "the rule"})
    public void rulematch_rule_2() {
        s(Predicate_rulematch_rule.class, 2);
    }
}
