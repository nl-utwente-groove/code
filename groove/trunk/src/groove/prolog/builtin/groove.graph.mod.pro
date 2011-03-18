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
	Graph modification predicates
*/

% Succeeds if the given graph can be changed (i.e. nodes/edges can be added or removed)
% is_graph_writeable(@Graph)
% @param Graph a term to test for a writeable graph
% :-build_in(is_graph_writeable/1,'groove.prolog.builtin.graph.mod.Predicate_is_graph_writeable').

% Creates a modifyable clone of the graph
% graph_clone(+Graph,?NewGraph)
% @param Graph the graph to clone
% @param NewGraph the newly created graph
% :-build_in(graph_clone/2,'groove.prolog.builtin.graph.mod.Predicate_graph_clone').
% graph_clone(NewGraph):-graph(Graph),graph_clone(Graph,NewGraph).

% Replaces the current graph with the provided graph. After calling this predicate the 
% graph/1 predicate returns this graph. Use this predicate carefully because previously
% retrieved nodes and edges will become invalid.
% set_graph(+Graph)
% @param Graph make this graph the current graph
% :-build_in(set_graph/1,'groove.prolog.builtin.graph.mod.Predicate_set_graph').

% Add an new edge with the given label and source and target nodes to the graph
% graph_add_edge(+Graph,+SourceNode,+TargetNode,+Label,?Edge)
% @param Graph the graph to update
% @param SourceNode the source of the new edge
% @param TargetNode the target of the new edge
% @param Label the label for the new Edge
% @param Edge the created edge
% :-build_in(graph_add_edge/5,'groove.prolog.builtin.graph.mod.Predicate_graph_add_edge').
% graph_add_edge(SourceNode,TargetNode,Label,Edge):-graph(Graph),graph_add_edge(Graph,SourceNode,TargetNode,Label,Edge).

% Add a new node to the graph. 
% graph_add_node(+Graph,?Node)
% @param Graph the Graph to update
% @param Node the created node
% :-build_in(graph_add_node/2,'groove.prolog.builtin.graph.mod.Predicate_graph_add_node').
% graph_add_node(Node):-graph(Graph),graph_add_node(Graph,Node).

% Add a new node to the graph with a collection of self edges
% graph_add_node(+Graph,+Labels,?Node)
% @param Graph the Graph to update
% @param Labels one or more labels of edges to add
% @param Node the created node
% :-build_in(graph_add_node/3,'groove.prolog.builtin.graph.mod.Predicate_graph_add_node').
% graph_add_node(Labels,Node):-graph(Graph),graph_add_node(Graph,Labels,Node). % note: implied

% Remove an edge from the graph
% graph_remove_edge(+Graph,+Edge,+Options)
% @param Graph the graph to update
% @param Edge the edge(s) to remove
% @param Options if it contains 'nodes(remove)' it will also remove nodes that no longer have any edges
% :-build_in(graph_add_edge/3,'groove.prolog.builtin.graph.mod.Predicate_graph_remove_edge').
% graph_remove_edge(Graph,Edge):-graph_remove_edge(Graph,Edge,[]).
% graph_remove_edge(Edge):-graph(Graph),graph_remove_edge(Graph,Edge,[]).

% Remove a node from the graph. 
% graph_remove_node(+Graph,+Node,+Options)
% @param Graph the Graph to update
% @param Node the node(s) to remove
% @param Options not used
% :-build_in(graph_remove_node/3,'groove.prolog.builtin.graph.mod.Predicate_graph_remove_node').
% graph_remove_node(Graph,Node):-graph_remove_node(Graph,Node,[]).
% graph_remove_node(Node):-graph(Graph),graph_remove_node(Graph,Node,[]).
