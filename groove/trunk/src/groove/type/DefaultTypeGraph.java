/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an 
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific 
 * language governing permissions and limitations under the License.
 *
 * $Id$
 */
package groove.type;

import java.util.HashSet;
import java.util.Set;
import java.util.Map;

import groove.graph.AbstractNodeEdgeMap;
import groove.graph.DefaultGraph;
import groove.graph.Edge;
import groove.graph.GenericNodeEdgeHashMap;
import groove.graph.Graph;
import groove.graph.Node;
import groove.trans.Rule;

/**
 * @author Frank van Es
 * @version $Revision $
 */
public class DefaultTypeGraph extends DefaultGraph {
	
	public AbstractNodeEdgeMap<Node,Node,Edge,Edge> addTyping(Graph graph) {
		
		AbstractNodeEdgeMap<Node,Node,Edge,Edge> map = 
			new GenericNodeEdgeHashMap<Node,Node,Edge,Edge>();
		
		map.putAll(addNodeTypes(graph.nodeSet()));
		map.putAll(addEdgeTypes(graph.edgeSet()));
		
		return map;
	}
	
	public AbstractNodeEdgeMap<Node,Node,Edge,Edge> addTyping(Rule rule) {
		AbstractNodeEdgeMap<Node,Node,Edge,Edge> map = 
			new GenericNodeEdgeHashMap<Node,Node,Edge,Edge>();
		
		Set<Node> nodes = new HashSet<Node>();
		Set<Edge> edges = new HashSet<Edge>();
		
		nodes.addAll(rule.lhs().nodeSet());
		nodes.addAll(rule.rhs().nodeSet());
		
		edges.addAll(rule.lhs().edgeSet());
		edges.addAll(rule.rhs().edgeSet());
		
		map.putAll(addNodeTypes(nodes));
		map.putAll(addEdgeTypes(edges));
		
		return map;
	}
	
	public AbstractNodeEdgeMap<Node,Node,Edge,Edge> addNodeTypes(Set<? extends Node> nodes) {
		AbstractNodeEdgeMap<Node,Node,Edge,Edge> map = 
			new GenericNodeEdgeHashMap<Node,Node,Edge,Edge>();
		
		for (Node node : nodes) {
			Node newNode = super.addNode();
			map.putNode(node, newNode);
		}
		return map;
	}
	
	public AbstractNodeEdgeMap<Node,Node,Edge,Edge> addEdgeTypes(Set<? extends Edge> edges) {
		AbstractNodeEdgeMap<Node,Node,Edge,Edge> map = 
			new GenericNodeEdgeHashMap<Node,Node,Edge,Edge>();
		
		for (Edge edge : edges) {
			Edge newEdge = super.addEdge(
					edge.end(Edge.SOURCE_INDEX),
					edge.label(),
					edge.end(Edge.TARGET_INDEX)
			);
			map.putEdge(edge, newEdge);
		}
		return map;
	}
	
	public void mergeNodeTypes(AbstractNodeEdgeMap<Node,Node,Edge,Edge> map) {
		for (Map.Entry<Node,Node> nodes : map.nodeMap().entrySet()) {
			mergeNodes(nodes.getKey(), nodes.getValue());
		}
	}
	
	public void getTyping(Object o) {
		
	}
}
