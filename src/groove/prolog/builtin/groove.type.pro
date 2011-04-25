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

% composite_type_graph(?TypeGraph)
:-build_in(composite_type_graph/1,'groove.prolog.builtin.type.Predicate_composite_type_graph').

% type_graph_name(?Name)
:-build_in(type_graph_name/1,'groove.prolog.builtin.type.Predicate_type_graph_name').

% active_type_graph_name(?Name)
:-build_in(active_type_graph_name/1,'groove.prolog.builtin.type.Predicate_active_type_graph_name').

% type_graph(+Name, ?TypeGraph)
:-build_in(type_graph/2,'groove.prolog.builtin.type.Predicate_type_graph').

% subtype(+TypeGraph, +TypeLabel, ?TypeLabel)
% subtype(+TypeGraph, ?TypeLabel, +TypeLabel)
:-build_in(subtype/3,'groove.prolog.builtin.type.Predicate_subtype').

% direct_subtype(+TypeGraph, +TypeLabel, ?TypeLabel)
% direct_subtype(+TypeGraph, ?TypeLabel, +TypeLabel)
:-build_in(direct_subtype/3,'groove.prolog.builtin.type.Predicate_direct_subtype').

% Convert a string into a label, or a label into a string
% label(+Text, ?Label)
% label(?Text, +Label)
:-build_in(label/2,'groove.prolog.builtin.type.Predicate_label').

% Derived predicates

% type_graph(?TypeGraph)
type_graph(TG) :- type_graph_name(L), type_graph(L,TG).

% active_type_graph(?TypeGraph)
active_type_graph(TG) :- active_type_graph_name(TGN), type_graph(TGN,TG).

% subtype_label(+TypeGraph, +Label, ?Label)
% subtype_label(+TypeGraph, ?Label, +Label)
subtype_label(TG,A,B) :- label(A,AL), label(B,BL), subtype(TG,AL,BL).

% direct_subtype_label(+TypeGraph, +Label, ?Label)
% direct_subtype_label(+TypeGraph, ?Label, +Label)
direct_subtype_label(TG,A,B) :- label(A,AL), label(B,BL), direct_subtype(TG,AL,BL).