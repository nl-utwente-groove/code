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

ValueNode (extends Node):
	A value node is a special type of node which contains a value (usually 
	a string or integer). A value node is used as the value of an 
	attribute of an other node (like the value of a 'name' or 'id' assigned
	to a node).

*/

% Succeeds if the given term is a value node
% is_valuenode(@Node)
:-build_in(is_valuenode/1,'groove.prolog.builtin.algebra.Predicate_is_valuenode').

% Converts the value node's value to a prolog term. A string value is converted to an 
% AtomicTerm, and integer and double value are converted to a IntegerTerm and FloatTerm
% respectively. All other values are converted to a JavaObjectTerm 
% convert_valuenode(+Node,?Term)
% @param the value node
% @param the term
:-build_in(convert_valuenode/2,'groove.prolog.builtin.algebra.Predicate_convert_valuenode').

% Only convert when it is a value node. Will never fail.
% try_convert_valuenode(+Node,?Term)
try_convert_valuenode(Node,Term):-(is_valuenode(Node) -> convert_valuenode(Node,Term)).

% Get all nodes with a given attribute
% node_with_attribute(+Graph,?Node,+AttrName,?AttrValue)
% @param the graph
% @param the node with the given attribute
% @param the attribute name
% @param the value of the attribute
node_with_attribute(Graph,Node,AttrName,AttrValue):-
	label_edge(Graph,AttrName,Edge), % get all edges with a given label
	edge_opposite(Edge,ValNode), % get the destination of the edge
	is_valuenode(ValNode), % make sure it's a value node
	convert_valuenode(ValNode,AttrValue), % convert it to a term
	edge_source(Edge,Node). % get the node that has this attribute

% When X is a Graph, then Y is the Node and Z is the AttrName
% Otherwise X is the Node, Y is the AttrName, and Z is the AttrValue
node_with_attribute(X,Y,Z):-
	is_graph(X)->node_with_attribute(X,Y,Z,_);
	graph(Graph),node_with_attribute(Graph,X,Y,Z).

node_with_attribute(Node,AttrName):-
	graph(Graph),node_with_attribute(Graph,Node,AttrName,_).
