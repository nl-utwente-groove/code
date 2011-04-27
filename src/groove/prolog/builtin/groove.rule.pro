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

% rule_name(?RuleName)
:-build_in(rule_name/1,'groove.prolog.builtin.rule.Predicate_rule_name').

% rule(+RuleName, ?Rule)
% rule(?RuleName, +Rule)
:-build_in(rule/2,'groove.prolog.builtin.rule.Predicate_rule').

% rule_confluent(+RuleName)
:-build_in(rule_confluent/1,'groove.prolog.builtin.rule.Predicate_rule_confluent').

% Success if the argument is a JavaObjectTerm with a Rule
% is_ruleevent(+RuleEvent)
:-build_in(is_rule/1,'groove.prolog.builtin.rule.Predicate_is_rule').

% The priority of the rule
% rule_priority(+Rule, ?Integer)
% @param the rule
% @param the priority
% @see groove.trans.Rule#getPriority()
:-build_in(rule_priority/2, 'groove.prolog.builtin.rule.Predicate_rule_priority').

% The left hand side of this Rule. The start graph. Note: this does not use the same
% nodes as the current graph.
% rule_lhs(+Rule, ?Graph)
% @param the rule
% @param the graph
% @see groove.trans.Rule#getLhs()
:-build_in(rule_lhs/2, 'groove.prolog.builtin.rule.Predicate_rule_lhs').

% The right hand side of this Rule. The target graph. Note: this does not use the same
% nodes as the current graph.
% rule_rhs(+Rule, ?Graph)
% @param the rule
% @param the graph
% @see groove.trans.Rule#getRhs()
:-build_in(rule_rhs/2, 'groove.prolog.builtin.rule.Predicate_rule_rhs').

% confluent_rule_name(?RuleName)
confluent_rule_name(RN) :- rule_name(RN), rule_confluent(RN).

% confluent_rule(?Rule)
confluent_rule(R) :- confluent_rule_name(RN), rule(RN,R).