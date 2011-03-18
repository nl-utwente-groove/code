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
	Graph input/output
*/

% Save the graph to the disk. Throws an exception when loading fails.
% graph_save(+Graph,+Location)
% @param Graph the graph to save
% @param Location the location to save the graph to
% :-build_in(graph_save/2,'groove.prolog.builtin.graph.io.Predicate_graph_save').

% Load the graph from the disk. Throws an exception when loading fails.
% graph_load(+Location,?Graph)
% @param Location the location to save the graph to
% @param Graph Will contain the loaded graph
% :-build_in(graph_load/2,'groove.prolog.builtin.graph.io.Predicate_graph_load').
