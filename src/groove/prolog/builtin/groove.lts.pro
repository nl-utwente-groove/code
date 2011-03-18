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
:-build_in(is_graphstate/1,'groove.prolog.builtin.lts.Predicate_is_graphstate').

% Success if the argument is a JavaObjectTerm with a Transition
% is_transition(@Trans)
:-build_in(is_transition/1,'groove.prolog.builtin.lts.Predicate_is_transition').

% Success if the argument is a JavaObjectTerm with a Location
% is_location(@Loc)
:-build_in(is_location/1,'groove.prolog.builtin.lts.Predicate_is_location').

% The current graph state. Might not always be available.
% graphstate(-State)
% @param the graph state
:-build_in(graphstate/1,'groove.prolog.builtin.lts.Predicate_graphstate').

% The graph of a graph state
% graphstate_graph(+State,?Graph)
% @param the graph state
% @param the graph 
% @groove.lts.GraphState#getGraph()
:-build_in(graphstate_graph/2,'groove.prolog.builtin.lts.Predicate_graphstate_graph').
graphstate_graph(G):-graphstate(GS),graphstate_graph(GS,G).

% Success if the graph state is closed (i.e. all transitions have been found)
% graphstate_is_closed(+State)
% @param the graph state
% @groove.lts.GraphState#isClosed()
:-build_in(graphstate_is_closed/1,'groove.prolog.builtin.lts.Predicate_graphstate_is_closed').
graphstate_is_closed:-graphstate(GS),graphstate_is_closed(GS).

% The location of a graph state
% graphstate_location(+State,?Loc)
% @param the graph state
% @param the location
% @groove.lts.GraphState#getLocation()
:-build_in(graphstate_location/2,'groove.prolog.builtin.lts.Predicate_graphstate_location').
graphstate_location(L):-graphstate(GS),graphstate_location(GS,L).

% A transition in a state
% graphstate_transition(+State,?Trans)
% @param the state
% @param the transition
% @groove.lts.GraphState#getTransitionSet()
:-build_in(graphstate_transition/2,'groove.prolog.builtin.lts.Predicate_graphstate_transition').
graphstate_transition(T):-graphstate(GS),graphstate_transition(GS,T).

% All current transitions in a state
% graphstate_transition(+State,?TransSet)
% @param the state
% @param the transition set
% @groove.lts.GraphState#getTransitionSet()
:-build_in(graphstate_transition_set/2,'groove.prolog.builtin.lts.Predicate_graphstate_transition_set').
graphstate_transition_set(T):-graphstate(GS),graphstate_transition_set(GS,T).

% A next state from this state
% graphstate_next(+State,?NextState)
% @param the state
% @param the next state
% @groove.lts.GraphState#getNextState()
:-build_in(graphstate_next/2,'groove.prolog.builtin.lts.Predicate_graphstate_next').
graphstate_next(T):-graphstate(GS),graphstate_next(GS,T).

% All next states from this state
% graphstate_next_set(+State,?NextStateSet)
% @param the state
% @param the next state set
% @groove.lts.GraphState#getNextState()
:-build_in(graphstate_next_set/2,'groove.prolog.builtin.lts.Predicate_graphstate_next_set').
graphstate_next_set(T):-graphstate(GS),graphstate_next_set(GS,T).

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
% gts_start_state(+GTS,?State)
% @param the gts
% @param the start GraphState
% @see groove.lts.LTS#startState()
:-build_in(gts_start_state/2,'groove.prolog.builtin.lts.Predicate_gts_start_state').
gts_start_state(GS):-gts(G),gts_start_state(G,GS).

% The final states of a GTS
% gts_final_state(+GTS,?State)
% @param the gts
% @param the start GraphState
% @see groove.lts.LTS#getFinalStates()
% @see groove.lts.LTS#isFinal()
:-build_in(gts_final_state/2,'groove.prolog.builtin.lts.Predicate_gts_final_state').
gts_final_state(GS):-gts(G),gts_final_state(G,GS).

% The final states of a GTS
% gts_final_state_set(+GTS,?StateSet)
% @param the gts
% @param the start GraphState
% @see groove.lts.LTS#getFinalStates()
:-build_in(gts_final_state_set/2,'groove.prolog.builtin.lts.Predicate_gts_final_state_set').
gts_final_state_set(GS):-gts(G),gts_final_state_set(G,GS).

% Get a matching rule event for a given graph state
% gts_match(+GTS,+GraphState,?RuleEvent)
% @param the gts
% @param the graphstate
% @param the ruleevent
:-build_in(gts_match/3,'groove.prolog.builtin.lts.Predicate_gts_match').
gts_match(GS,RE):-gts(G),gts_match(G,GS,RE).
gts_match(RE):-gts(G),graphstate(GS),gts_match(G,GS,RE).
