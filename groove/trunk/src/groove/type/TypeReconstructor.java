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
import groove.trans.SPORule;
import groove.view.FormatException;

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
				
				// TODO: Possibility: updated putAll method in MergeMap class
				MergeMap newMerges = calculateMerges(typeGraph, rule, map);
				for (Map.Entry<Node,Node> merge : newMerges.nodeMap().entrySet()) {
					equivalentTypes.putNode(merge.getKey(), merge.getValue());
				}
			}
			for (Map.Entry<Node,Node> mapping : equivalentTypes.nodeMap().entrySet()) {
				typeGraph.mergeNodes(mapping.getKey(), mapping.getValue());
			}
		} while (typeGraph.nodeCount() != nodeCount);
		
		return typeGraph;
	}
	
	/**
	 * Calculates which nodes in the type graph need to be merged based on the
	 * given production rule and current typing morphisms into the type graph.
	 * @param typeGraph The type graph in which the rule should be matched
	 * @param rule The rule to compute matchies into the type graph from
	 * @param currentTypings Current typings, which state which elements of the
	 *        rule graphs are represented by which elements in the type graph
	 * @return A MergeMap containing mappings between nodes that need to be merged 
	 */
	public static MergeMap calculateMerges(Graph typeGraph, 
			Rule rule, AbstractNodeEdgeMap<Node,Node,Edge,Edge> currentTypings) {
		
		MergeMap merges = new MergeMap();
		
		// Create a copy of the current rule in order to omit application
		// conditions and compute the matches from this rule into the 
		// current type graph
		Rule ruleCopy = null;
		try {
			ruleCopy = new SPORule(
					rule.getMorphism(), 
					rule.getName(), 
					rule.getPriority(), 
					rule.getProperties()
			);
			ruleCopy.setFixed();
			
			Iterable<RuleMatch> matches = ruleCopy.getMatches(typeGraph, null);
			for (RuleMatch match : matches) {
				Map<Node,Node> nodeMap = match.getElementMap().nodeMap();
				for (Map.Entry<Node,Node> nodes : nodeMap.entrySet()) {
					merges.putNode(
							currentTypings.getNode(nodes.getKey()), 
							nodes.getValue()
					);
				}
			}
		}
		catch (FormatException fe) { }
		
		return merges;
	}
	
}
