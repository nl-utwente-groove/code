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

:-build_in(composite_type_graph/1,'groove.prolog.builtin.type.Predicate_composite_type_graph').
:-build_in(type_graph_name/1,'groove.prolog.builtin.type.Predicate_type_graph_name').
:-build_in(active_type_graph_name/1,'groove.prolog.builtin.type.Predicate_active_type_graph_name').
:-build_in(type_graph/2,'groove.prolog.builtin.type.Predicate_type_graph').
:-build_in(type_label/2,'groove.prolog.builtin.type.Predicate_type_label').
:-build_in(subtype/3,'groove.prolog.builtin.type.Predicate_subtype').
:-build_in(direct_subtype/3,'groove.prolog.builtin.type.Predicate_direct_subtype').

% Derived predicates
type_graph(TG) :- type_graph_name(L), type_graph(L,TG).
subtype_label(TG,A,B) :- type_label(A,AL), type_label(B,BL), subtype(TG,AL,BL).
direct_subtype_label(TG,A,B) :- type_label(A,AL), type_label(B,BL), direct_subtype(TG,AL,BL).
active_type_graph(TG) :- active_type_graph_name(TGN), type_graph(TGN,TG).