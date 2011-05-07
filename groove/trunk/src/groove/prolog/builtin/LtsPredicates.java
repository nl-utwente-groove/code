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

/** LTS-related Prolog predicates.
 * Documentation reading guide:
 * <li> +     The argument shall be instantiated.
 * <li> ?     The argument shall be instantiated or a variable.
 * <li> @     The argument shall remain unaltered.
 * <li> -     The argument shall be a variable that will be instantiated
 */
@SuppressWarnings("all")
public class LtsPredicates extends GroovePredicates {
    @ToolTip("Tests if the argument is a JavaObjectTerm with a GraphState")
    @Signature({"State", "@"})
    public void is_state_1() {
        s(":-build_in(is_state/1,'groove.prolog.builtin.lts.Predicate_is_state').");
    }

    @ToolTip("Tests if the argument is a JavaObjectTerm with a Transition")
    @Signature({"Trans", "@"})
    public void is_transition_1() {
        s(":-build_in(is_transition/1,'groove.prolog.builtin.lts.Predicate_is_transition').");
    }

    @ToolTip("Retrieves one state from the current GTS")
    @Signature({"State", "?"})
    @Param({"A state from the GTS"})
    public void state_1() {
        s(":-build_in(state/1,'groove.prolog.builtin.lts.Predicate_state').");
    }

    @ToolTip("Retrieves the currently selected state from the GTS")
    @Signature({"State", "?"})
    @Param({"The active state in the GTS"})
    public void active_state_1() {
        s(":-build_in(active_state/1,'groove.prolog.builtin.lts.Predicate_active_state').");
    }

    @ToolTip("Retrieves the graph for a state")
    @Signature({"State", "Graph", "?+"})
    @Param({"A state", "The graph belonging to the state"})
    //    % @groove.lts.GraphState#getGraph()
    public void state_graph_2() {
        s(":-build_in(state_graph/2,'groove.prolog.builtin.lts.Predicate_state_graph').");
    }

    @ToolTip("Cycles over the state graphs of the GTS")
    @Signature({"Graph", "?"})
    public void state_graph_1() {
        s("state_graph(G):-state(GS),state_graph(GS,G).");
    }

    @ToolTip("Tests if the graph state is closed (i.e. all transitions have been found)")
    @Signature({"State", "+"})
    @Param({"the graph state"})
    //    % @groove.lts.GraphState#isClosed()
    public void state_is_closed_1() {
        s(":-build_in(state_is_closed/1,'groove.prolog.builtin.lts.Predicate_state_is_closed').");
    }

    @ToolTip("Cycles over the closed states of the GTS")
    @Signature({"State", "?"})
    public void closed_state_1() {
        s("closed_state(GS):-state(GS),state_is_closed(GS).");
    }

    @ToolTip("Cycles over the outgoing transitions of a state")
    @Signature({"State", "Trans", "+?"})
    @Param({"the state", "the transition"})
    //    % @groove.lts.GraphState#getTransitionSet()
    public void state_transition_2() {
        s(":-build_in(state_transition/2,'groove.prolog.builtin.lts.Predicate_state_transition').");
    }

    @ToolTip("Retireves all current outgoing transitions of a state")
    @Signature({"State", "TransSet", "+?"})
    @Param({"the state", "the transition set"})
    //    % @groove.lts.GraphState#getTransitionSet()
    public void state_transition_set_2() {
        s(":-build_in(state_transition_set/2,'groove.prolog.builtin.lts.Predicate_state_transition_set').");
    }

    @ToolTip("Cycles over the successor states of a state")
    @Signature({"State", "NextState", "+?"})
    @Param({"the state", "the next state"})
    //    % @groove.lts.GraphState#getNextState()
    public void state_next_2() {
        s(" :-build_in(state_next/2,'groove.prolog.builtin.lts.Predicate_state_next').");
    }

    @ToolTip("Retrieves all successor states of a state")
    @Signature({"State", "NextStateSet", "+?"})
    @Param({"the state", "the next state set"})
    //    % @groove.lts.GraphState#getNextState()
    public void state_next_set_2() {
        s(":-build_in(state_next_set/2,'groove.prolog.builtin.lts.Predicate_state_next_set').");
    }

    @ToolTip("Retrieves the source state of a transition")
    @Signature({"Trans", "State", "+?"})
    @Param({"the transition", "the source state"})
    //    % @groove.lts.GraphTransition#source()
    public void transition_source_2() {
        s(":-build_in(transition_source/2,'groove.prolog.builtin.lts.Predicate_transition_source').");
    }

    @ToolTip("Retrieves the target state of a transition")
    @Signature({"Trans", "State", "+?"})
    @Param({"the transition", "the target state"})
    //    % @groove.lts.GraphTransition#target()
    public void transition_target_2() {
        s(":-build_in(transition_target/2,'groove.prolog.builtin.lts.Predicate_transition_target').");
    }

    @ToolTip("Retrieves the rule event underlying a transition")
    @Signature({"Trans", "RuleEvent", "+?"})
    @Param({"the transition", "the rule event"})
    //    % @see groove.lts.GraphTransition#getEvent()
    public void transition_event_2() {
        s(":-build_in(transition_event/2,'groove.prolog.builtin.lts.Predicate_transition_event').");
    }

    @ToolTip("Retrieves the rule match underlying a transition")
    @Signature({"Trans", "RuleMatch", "+?"})
    @Param({"the transition", "the rule match"})
    //    % @see groove.lts.GraphTransition#getMatch()
    public void transition_match_2() {
        s(" :-build_in(transition_match/2,'groove.prolog.builtin.lts.Predicate_transition_match').");
    }

    @ToolTip("Tests if the object is a GTS")
    @Signature({"GTS", "@"})
    public void is_gts_1() {
        s(":-build_in(is_gts/1,'groove.prolog.builtin.lts.Predicate_is_gts').");
    }

    @ToolTip("Gets the current GTS. This can fail when not GTS is active.")
    @Signature({"GTS", "-"})
    public void gts_1() {
        s(":-build_in(gts/1,'groove.prolog.builtin.lts.Predicate_gts').");
    }

    @ToolTip("Gets the start graph state of a GTS")
    @Signature({"State", "?"})
    @Param({"the start GraphState"})
    //    % @see groove.lts.LTS#startState()
    public void start_state_1() {
        s(":-build_in(start_state/1,'groove.prolog.builtin.lts.Predicate_start_state').");
    }

    @ToolTip("Cycles over the final states of a GTS")
    @Signature({"State", "?"})
    @Param({"the start GraphState"})
    //    % @see groove.lts.LTS#getFinalStates()
    //    % @see groove.lts.LTS#isFinal()
    public void final_state_1() {
        s(" :-build_in(final_state/1,'groove.prolog.builtin.lts.Predicate_final_state').");
    }

    @ToolTip("Retrieves the set of final states of a GTS")
    @Signature({"StateSet", "?"})
    @Param({"the start GraphState"})
    //    % @see groove.lts.LTS#getFinalStates()
    public void final_state_set_1() {
        s(":-build_in(final_state_set/1,'groove.prolog.builtin.lts.Predicate_final_state_set').");
    }

    @ToolTip("Cycles over the rule events of the outgoing transitions of a graph state")
    @Signature({"GraphState", "RuleEvent", "+?"})
    @Param({"the graphstate", "the ruleevent"})
    public void state_ruleevent_2() {
        s(":-build_in(state_ruleevent/2,'groove.prolog.builtin.lts.Predicate_state_ruleevent').");
    }

    @ToolTip("Cycles over all rule events in the current GTS")
    @Signature({"RuleEvent", "?"})
    public void ruleevent_1() {
        s("ruleevent(RE):-state(GS),state_ruleevent(GS,RE).");
    }
}
