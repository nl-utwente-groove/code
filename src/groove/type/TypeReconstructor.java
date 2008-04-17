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

import groove.graph.AbstractNodeEdgeMap;
import groove.graph.DefaultEdge;
import groove.graph.DefaultNode;
import groove.graph.Edge;
import groove.graph.GenericNodeEdgeHashMap;
import groove.graph.Graph;
import groove.graph.MergeMap;
import groove.graph.Node;
import groove.trans.GraphGrammar;
import groove.trans.Rule;
import groove.trans.RuleMatch;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Frank van Es
 * @version $Revision $
 */
public class TypeReconstructor {
	/**
	 * Reconstructs a type graph for a graph grammar
	 * @param grammar A graph grammar to construct a type graph for
	 * @return A type graph for grammar 
	 */
	public static Graph reconstruct(GraphGrammar grammar) {
		Graph startGraph = grammar.getStartGraph();
		Collection<Rule> rules = grammar.getRules();
		Map<Rule,AbstractNodeEdgeMap<Node,Node,Edge,Edge>> ruleMappings = 
			new HashMap<Rule,AbstractNodeEdgeMap<Node,Node,Edge,Edge>>();
		
		Graph typeGraph = startGraph.clone();
		
		for (Rule rule : rules) {
			AbstractNodeEdgeMap<Node,Node,Edge,Edge> map = 
				new GenericNodeEdgeHashMap<Node,Node,Edge,Edge>();
			
			Graph lhs = rule.lhs();
			Graph rhs = rule.rhs();
			
			Set<Node> nodes = new HashSet<Node>();
			Set<Edge> edges = new HashSet<Edge>();
			nodes.addAll(lhs.nodeSet());
			nodes.addAll(rhs.nodeSet());
			edges.addAll(lhs.edgeSet());
			edges.addAll(rhs.edgeSet());
			
			for (Node node : nodes) {
				Node newNode = DefaultNode.createNode();
				map.putNode(node, newNode);
				typeGraph.addNode(newNode);
			}
			for (Edge edge : edges) {
				Edge newEdge = DefaultEdge.createEdge(
						map.getNode(edge.end(Edge.SOURCE_INDEX)),
						edge.label(),
						map.getNode(edge.end(Edge.TARGET_INDEX))
				);
				map.putEdge(edge, newEdge);
				typeGraph.addEdge(newEdge);
			}
			ruleMappings.put(rule, map);
		}
		
		int nodeCount;
		do {
			nodeCount = typeGraph.nodeCount();
			MergeMap equivalentTypes = new MergeMap();
			for (Rule rule : rules) {
				AbstractNodeEdgeMap<Node,Node,Edge,Edge> map = ruleMappings.get(rule);
				
//				Map<Node,Node> ruleMap = rule.getMorphism().elementMap().nodeMap();
//				for (Map.Entry<Node,Node> nodes : ruleMap.entrySet()) {
//					equivalentTypes.putNode(
//							map.getNode(nodes.getKey()), 
//							map.getNode(nodes.getValue())
//					);
//				}
				
				Iterable<RuleMatch> matches = rule.getMatches(typeGraph, null);
				for (RuleMatch match : matches) {
					Map<Node,Node> nodeMap = match.getElementMap().nodeMap();
					for (Map.Entry<Node,Node> nodes : nodeMap.entrySet()) {
						equivalentTypes.putNode(
								map.getNode(nodes.getKey()), 
								nodes.getValue()
						);
					}
				}
			}
			for (Map.Entry<Node,Node> mapping : equivalentTypes.nodeMap().entrySet()) {
				typeGraph.mergeNodes(mapping.getKey(), mapping.getValue());
			}
		} while (typeGraph.nodeCount() != nodeCount);
		
		return typeGraph;
	}
}
