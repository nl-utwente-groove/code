% Groove Prolog Interface
% Copyright (C) 2009 Michiel Hendriks, University of Twente
% 
% This library is free software; you can redistribute it and/or
% modify it under the terms of the GNU Lesser General Public
% License as published by the Free Software Foundation; either
% version 2.1 of the License, or (at your option) any later version.
% 
% This library is distributed in the hope that it will be useful,
% but WITHOUT ANY WARRANTY; without even the implied warranty of
% MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
% Lesser General Public License for more details.
% 
% You should have received a copy of the GNU Lesser General Public
% License along with this library; if not, write to the Free Software
% Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA

/*

GTS (extends Graph):
	The GTS is the created graph when applying rules to the basic graph.
	The nodes in a GTS are graph states. The edges transitions from
	one state to the other.
	
Graphstate (extends Node):
	A graphstate is a node in the GTS graph. It contains a reference
	to the actual graph, and information about transitions to other
	states.
	
Transition (extends Edge):
	A transition is a special edge in the GTS. It contains information
	about the rule which was applied to the source state in order to
	get to the destination state.
	
Location:
	This is a reference to a Location in the control automata (if the 
	production system uses a control automata).
	
RuleEvent:
	see groove.trans.pro
	
RuleMatch:
	see groove.trans.pro

*/

% Success if the argument is a JavaObjectTerm with a GraphState
% is_graphstate(@State)
:-build_in(is_state/1,'groove.prolog.builtin.lts.Predicate_is_state').

% Success if the argument is a JavaObjectTerm with a Transition
% is_transition(@Trans)
:-build_in(is_transition/1,'groove.prolog.builtin.lts.Predicate_is_transition').

% Retrieves one state from the GTS
% state(?State)
% @param	A state fromt the GTS
:-build_in(state/1,'groove.prolog.builtin.lts.Predicate_state').

% Retrieves the currently selected state from the GTS
% active_state(?State)
% @param	The active state in the GTS
:-build_in(active_state/1,'groove.prolog.builtin.lts.Predicate_active_state').

% Retrieves the graph for a state
% state_graph(+State,?Graph)
% @param A state
% @param The graph belonging to the state
% @groove.lts.GraphState#getGraph()
:-build_in(state_graph/2,'groove.prolog.builtin.lts.Predicate_state_graph').

% state_graph(?Graph)
state_graph(G):-state(GS),state_graph(GS,G).

% Success if the graph state is closed (i.e. all transitions have been found)
% state_is_closed(+State)
% @param the graph state
% @groove.lts.GraphState#isClosed()
:-build_in(state_is_closed/1,'groove.prolog.builtin.lts.Predicate_state_is_closed').

% closed_state(?State)
closed_state(GS):-state(GS),state_is_closed(GS).

% A transition in a state
% graphstate_transition(+State,?Trans)
% @param the state
% @param the transition
% @groove.lts.GraphState#getTransitionSet()
:-build_in(state_transition/2,'groove.prolog.builtin.lts.Predicate_state_transition').

% All current transitions in a state
% graphstate_transition(+State,?TransSet)
% @param the state
% @param the transition set
% @groove.lts.GraphState#getTransitionSet()
:-build_in(state_transition_set/2,'groove.prolog.builtin.lts.Predicate_state_transition_set').

% A next state from this state
% graphstate_next(+State,?NextState)
% @param the state
% @param the next state
% @groove.lts.GraphState#getNextState()
:-build_in(state_next/2,'groove.prolog.builtin.lts.Predicate_state_next').

% All next states from this state
% graphstate_next_set(+State,?NextStateSet)
% @param the state
% @param the next state set
% @groove.lts.GraphState#getNextState()
:-build_in(state_next_set/2,'groove.prolog.builtin.lts.Predicate_state_next_set').

% The source of a transition
% transition_source(+Trans,?State)
% @param the transition
% @param the source state
% @groove.lts.GraphTransition#source()
:-build_in(transition_source/2,'groove.prolog.builtin.lts.Predicate_transition_source').

% The target of a transition
% transition_target(+Trans,?State)
% @param the transition
% @param the target state
% @groove.lts.GraphTransition#target()
:-build_in(transition_target/2,'groove.prolog.builtin.lts.Predicate_transition_target').

% The rule event that caused this transition
% transition_event(+Trans,?RuleEvent)
% @param the transition
% @param the rule event
% @see groove.lts.GraphTransition#getEvent()
:-build_in(transition_event/2,'groove.prolog.builtin.lts.Predicate_transition_event').

% The rule match that caused this transition
% transition_match(+Trans,?RuleMatch)
% @param the transition
% @param the rule match
% @see groove.lts.GraphTransition#getMatch()
:-build_in(transition_match/2,'groove.prolog.builtin.lts.Predicate_transition_match').

% Success if the object is a GTS
% is_gts(@GTS)
:-build_in(is_gts/1,'groove.prolog.builtin.lts.Predicate_is_gts').

% Get the current GTS. This can fail when not GTS is active.
% gts(-GTS)
:-build_in(gts/1,'groove.prolog.builtin.lts.Predicate_gts').

% The start graph state of a GTS
% gts_start_state(?State)
% @param the start GraphState
% @see groove.lts.LTS#startState()
:-build_in(start_state/1,'groove.prolog.builtin.lts.Predicate_start_state').

% The final states of a GTS
% gts_final_state(?State)
% @param the start GraphState
% @see groove.lts.LTS#getFinalStates()
% @see groove.lts.LTS#isFinal()
:-build_in(final_state/1,'groove.prolog.builtin.lts.Predicate_final_state').

% The final states of a GTS
% gts_final_state_set(?StateSet)
% @param the start GraphState
% @see groove.lts.LTS#getFinalStates()
:-build_in(final_state_set/1,'groove.prolog.builtin.lts.Predicate_final_state_set').

% Get a matching rule event for a given graph state
% state_ruleevent(+GraphState,?RuleEvent)
% @param the graphstate
% @param the ruleevent
:-build_in(state_ruleevent/2,'groove.prolog.builtin.lts.Predicate_gts_match').

% ruleevent(?RuleEvent)
ruleevent(RE):-state(GS),state_ruleevent(GS,RE).
