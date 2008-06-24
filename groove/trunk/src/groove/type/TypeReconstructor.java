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

import groove.graph.DefaultEdge;
import groove.graph.DefaultGraph;
import groove.graph.DefaultNode;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.MergeMap;
import groove.graph.Node;
import groove.graph.NodeEdgeHashMap;
import groove.graph.NodeEdgeMap;
import groove.trans.GraphGrammar;
import groove.trans.Rule;
import groove.trans.RuleMatch;
import groove.trans.SPORule;
import groove.view.FormatException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Frank van Es
 * @version $Revision $
 */
public class TypeReconstructor {
	
	private Map<Graph,NodeEdgeMap> typings = new HashMap<Graph,NodeEdgeMap>();
	private Graph typeGraph = new DefaultGraph();
	private MergeMap nodeTypes = new MergeMap();
	
	/**
	 * @param grammar
	 */
	protected TypeReconstructor(GraphGrammar grammar) {
		Graph startGraph = grammar.getStartGraph();
		Collection<Rule> rules = grammar.getRules();
		
		addTyping(startGraph);
		
		boolean addedRule;
		int nodeCount;
		do {
			nodeCount = typeGraph.nodeCount();
			addedRule = false;
			MergeMap equivalentTypes = new MergeMap();
			
			for (Rule rule : rules) {
				
				if (removeApplicationConditions(rule).hasMatch(typeGraph)) {
					
					if (getTyping(rule.rhs()) == null) {
						addedRule = true;
						addTyping(rule.lhs());
						addTyping(rule.rhs());
					}
					
					MergeMap newMerges = calculateMerges(rule);
					equivalentTypes.putAll(newMerges);
					// Putall didn't work properly for MergeMaps before. Is this fixed??
					// Or otherwise a different implementation between Java 5 & 6.
					// If getting odd results, replace the putAll by the following code:
					//for (Map.Entry<Node,Node> merge : newMerges.nodeMap().entrySet()) {
					//	equivalentTypes.putNode(merge.getKey(), merge.getValue());
					//}
				}
			}
			for (Map.Entry<Node,Node> mapping : equivalentTypes.nodeMap().entrySet()) {
				Node from = getNodeType(mapping.getKey());
				Node to = getNodeType(mapping.getValue());
				if (from != null && to != null) {
					typeGraph.mergeNodes(from, to);
					nodeTypes.putNode(from, to);
				}
			}
		} while (addedRule || typeGraph.nodeCount() != nodeCount);
	}
	
	/**
	 * @return
	 */
	public Graph getTypeGraph() {
		return typeGraph;
	}
	
	/**
	 * Reconstructs a type graph for a graph grammar
	 * @param grammar A graph grammar to construct a type graph for
	 * @return A type graph for grammar 
	 */
	public static Graph reconstruct(GraphGrammar grammar) {
		
		return new TypeReconstructor(grammar).getTypeGraph();
	}
	
	/**
	 * Calculates which nodes in the type graph need to be merged based on the
	 * given production rule.
	 * @param rule The rule to compute matches into the type graph from
	 * @return A MergeMap containing mappings between nodes that need to be merged 
	 */
	private MergeMap calculateMerges(Rule rule) {
		
		NodeEdgeMap lhsTyping = getTyping(rule.lhs());
		NodeEdgeMap rhsTyping = getTyping(rule.rhs());
		NodeEdgeMap newLhsTyping = getTyping(rule.lhs());
		
		MergeMap merges = new MergeMap();
		Map<Node,Node> nodeMap;
		
		nodeMap = rule.getMorphism().elementMap().nodeMap();
		for (Map.Entry<Node,Node> nodes : nodeMap.entrySet()) {
			merges.putNode(
					lhsTyping.getNode(nodes.getKey()), 
					rhsTyping.getNode(nodes.getValue())
			);
		}
		
		// Create a copy of the current rule in order to omit application
		// conditions and compute the matches from this rule into the 
		// current type graph		
		Iterable<RuleMatch> matches = removeApplicationConditions(rule).getMatches(typeGraph, null);
		for (RuleMatch match : matches) {
			nodeMap = match.getElementMap().nodeMap();
			for (Map.Entry<Node,Node> nodes : nodeMap.entrySet()) {
				merges.putNode(
						lhsTyping.getNode(nodes.getKey()), 
						nodes.getValue()
				);
			}
		}
		
		typings.put(rule.lhs(), newLhsTyping);
		
		return merges;
	}
	
	/**
	 * @param graph
	 */
	private void addTyping(Graph graph) {
		
		NodeEdgeMap typing = new NodeEdgeHashMap();
		
		for (Node node : graph.nodeSet()) {
			Node newNode = DefaultNode.createNode();
			typing.putNode(node, newNode);
			typeGraph.addNode(newNode);
		}
		for (Edge edge : graph.edgeSet()) {
			Edge newEdge = DefaultEdge.createEdge(
					typing.getNode(edge.end(Edge.SOURCE_INDEX)),
					edge.label(),
					typing.getNode(edge.end(Edge.TARGET_INDEX))
			);
			typing.putEdge(edge, newEdge);
			typeGraph.addEdge(newEdge);
		}
		
		typings.put(graph, typing);
	}
	
	private NodeEdgeMap getTyping(Graph graph) {
		return typings.get(graph);
	}
	
	/**
	 * @param rule The rule of which the application conditions need to be removed
	 * @return A copy of rule with all application conditions removed
	 */
	public static Rule removeApplicationConditions(Rule rule) {
		Rule result = null;
		try {
			result = new SPORule(
					rule.getMorphism(), 
					rule.getName(), 
					rule.getPriority(), 
					rule.getProperties()
			);
			result.setFixed();
		}
		catch (FormatException fe) {
			return rule;
		}
		return result;
	}
	
	private Node getNodeType(Node node) {
		if (typeGraph.containsElement(node)) {
			return node;
		} else if (typeGraph.containsElement(nodeTypes.getNode(node))) {
			return nodeTypes.getNode(node);
		} else {
			//System.err.println("Node not found! " + node.getClass().getCanonicalName());
			return null;
		}
	}
}
