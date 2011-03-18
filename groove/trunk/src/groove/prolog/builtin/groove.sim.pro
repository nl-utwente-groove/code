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

% Documentation reading guide:
% + 	The argument shall be instantiated.
% ? 	The argument shall be instantiated or a variable.
% @ 	The argument shall remain unaltered.
% - 	The argument shall be a variable that will be instantiated

/*
	Simulator predicates. These predicates can be used to initiate a
	graph production simulations.
	
	@see groove.trans.RuleSystem
*/

% Succeeds if the provided term contains a graph production system (GPS).
% is_gps(@Term)
% @param Term the term that will be tested for a GPS
%:-build_in(is_gps/1,'groove.prolog.builtin.sim.Predicate_is_gps').

% gps_is_fixed(+GPS)
%:-build_in(gps_is_fixed/1,'groove.prolog.builtin.sim.Predicate_gps_is_fixed').

% Load a GPS from a given location. This works much like the graph_load/2 predicate.
% If loading fails an exception is thrown
% gps_load(+Location,?GPS)
% @param Location the location (file or URL) to load the GPS from
% @param GPS Will contain the loaded GPS
%:-build_in(gps_load/2,'groove.prolog.builtin.sim.Predicate_gps_load').

% Create a new production system, either a new empty or one based on an existing
% production system.
% gps_create(+AtomOrGPS,?GPS)
% @param AtomOrGPS An existing GPS, or the name of a new one
% @param GPS Will contain the create GPS
%:-build_in(gps_create/2,'groove.prolog.builtin.sim.Predicate_gps_create').

% Get or set the graph state. If the term is a variable it will retrieve the start graph, 
% otherwise it will set the start graph. To test the start graph use: 
%	gps_startgraph(GPS,_start),_start == SomeGraph
% gps_startgraph(+GPS,?GraphOrVar)
% @param GPS the GPS
% @param GraphOrVar	If a graph, use it as start graph, if a variable then set it to the start graph.
%:-build_in(gps_startgraph/2,'groove.prolog.builtin.sim.Predicate_gps_startgraph').

% gps_rule(+GPS,?Name,?Rule)
%:-build_in(gps_rule/3,'groove.prolog.builtin.sim.Predicate_gps_rule').

% gps_add_rule(+GPS,+Rule)
%:-build_in(gps_add_rule/2,'groove.prolog.builtin.sim.Predicate_gps_add_rule').

% gps_remove_rule(+GPS,+RuleOrAtom)
%:-build_in(gps_remove_rule/2,'groove.prolog.builtin.sim.Predicate_gps_remove_rule').


