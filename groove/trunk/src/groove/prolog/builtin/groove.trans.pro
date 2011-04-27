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

RuleEvent:
	A rule event is the instantiation of a rule for the current graph state.
	It maps the current graph to a rule.	 

RuleMatch:
	...

Rule:
	This is the object representation of a rule as defined in the
	production system. Rules never contain information about a current
	graph, it is static information that never changes in the production
	system.

*/

% Success if the argument is a JavaObjectTerm with a RuleEvent
% is_ruleevent(+RuleEvent)
:-build_in(is_ruleevent/1,'groove.prolog.builtin.trans.Predicate_is_ruleevent').

% Success if the argument is a JavaObjectTerm with a RuleMatch
% is_ruleevent(+RuleMatch)
:-build_in(is_rulematch/1,'groove.prolog.builtin.trans.Predicate_is_rulematch').

% Get the currently selected rule event.
% active_ruleevent(?RuleEvent)
% @param the rule event
:-build_in(active_ruleevent/1,'groove.prolog.builtin.trans.Predicate_active_ruleevent').

% The label of a rule event
% ruleevent_label(+RuleEvent,?Label)
% @param the rule event
% @param the label
% @see groove.trans.RuleEvent#getLabel()
:-build_in(ruleevent_label/2,'groove.prolog.builtin.trans.Predicate_ruleevent_label').

% The rule associated with this event
% ruleevent_rule(+RuleEvent,?Rule)
% @param the rule event
% @param the rule
% @see groove.trans.RuleEvent#getRule()
:-build_in(ruleevent_rule/2,'groove.prolog.builtin.trans.Predicate_ruleevent_rule').

% Translate a node/edge in the rule's graphs to a node/edge in the ruleevent's graph.
% Fails when the node/edge does not have a mapping
% ruleevent_transpose(+RuleEvent,+NodeEdge,?NodeEdge)
% @param the rule event
% @param node/edge as used in the rule's graph
% @param node/edge in the graph
:-build_in(ruleevent_transpose/3,'groove.prolog.builtin.trans.Predicate_ruleevent_transpose').

% Erased edges in this event
% ruleevent_erased_edge(+RuleEvent,?Edge)
% @param the rule event
% @param the edge
:-build_in(ruleevent_erased_edge/2,'groove.prolog.builtin.trans.Predicate_ruleevent_erased_edge').

% Erased nodes in this event
% ruleevent_erased_edge(+RuleEvent,?Node)
% @param the rule event
% @param the node
:-build_in(ruleevent_erased_node/2,'groove.prolog.builtin.trans.Predicate_ruleevent_erased_node').

% Created edges in this event. 
% ruleevent_created_edge(+RuleEvent,?Edge)
% @param the rule event
% @param the edge
:-build_in(ruleevent_created_edge/2,'groove.prolog.builtin.trans.Predicate_ruleevent_created_edge').

% Created nodes in this event. 
% ruleevent_created_node(+RuleEvent,?Node)
% @param the rule event
% @param the node
:-build_in(ruleevent_created_node/2,'groove.prolog.builtin.trans.Predicate_ruleevent_created_node').

% The rule match
% ruleevent_match(+RuleEvent,+Graph,?RuleMatch)
% @param the rule event
% @param the graph to match against
% @param the rule match
% @see groove.trans.RuleEvent#getMatch()
:-build_in(ruleevent_match/3,'groove.prolog.builtin.trans.Predicate_ruleevent_match').

% ruleevent_match(+RuleEvent,?RuleMatch)
ruleevent_match(RE,RM):-state(GS),state_graph(GS,G),ruleevent_match(RE,G,RM).

% Get all current rule matches
% rulematch(?RuleMatch)
rulematch(RM):-state(GS),state_graph(GS,G),state_ruleevent(GS,RE),ruleevent_match(RE,G,RM).

% The edges in a rule match
% rulematch_edge(+RuleMatch,?Edge)
% @param the rulematch
% @param the edge in the match
:-build_in(rulematch_edge/2,'groove.prolog.builtin.trans.Predicate_rulematch_edge').

% The nodes in a rule match
% rulematch_node(+RuleMatch,?Node)
% @param the rulematch
% @param the node in the match
:-build_in(rulematch_node/2,'groove.prolog.builtin.trans.Predicate_rulematch_node').

% The rule which was used in this match
% rulematch_rule(+RuleMatch,?Rule)
% @param the rulematch
% @param the rule
:-build_in(rulematch_rule/2,'groove.prolog.builtin.trans.Predicate_rulematch_rule').
