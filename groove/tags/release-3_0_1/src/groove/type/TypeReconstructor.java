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
import groove.graph.DefaultGraph;
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
		
		Graph typeGraph = new DefaultTypeGraph();
		
		addTyping(typeGraph, startGraph);
		
		boolean addedRule;
		int nodeCount;
		do {
			nodeCount = typeGraph.nodeCount();
			addedRule = false;
			MergeMap equivalentTypes = new MergeMap();
			
			for (Rule rule : rules) {
				
				if (removeApplicationConditions(rule).hasMatch(typeGraph)) {
					
					if (ruleMappings.get(rule) == null) {
						addedRule = true;
						ruleMappings.put(rule, addTyping(typeGraph, rule));
						//ruleMappings.put(rule, addTyping(typeGraph, rule.lhs()));
					}
					
					AbstractNodeEdgeMap<Node,Node,Edge,Edge> map = ruleMappings.get(rule);
					
					// TODO: Possibility: updated putAll method in MergeMap class
					MergeMap newMerges = calculateMerges(typeGraph, rule, map);
					for (Map.Entry<Node,Node> merge : newMerges.nodeMap().entrySet()) {
						equivalentTypes.putNode(merge.getKey(), merge.getValue());
					}
				}
			}
			for (Map.Entry<Node,Node> mapping : equivalentTypes.nodeMap().entrySet()) {
				typeGraph.mergeNodes(mapping.getKey(), mapping.getValue());
			}
		} while (addedRule || typeGraph.nodeCount() != nodeCount);
		
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
		Iterable<RuleMatch> matches = removeApplicationConditions(rule).getMatches(typeGraph, null);
		for (RuleMatch match : matches) {
			Map<Node,Node> nodeMap = match.getElementMap().nodeMap();
			for (Map.Entry<Node,Node> nodes : nodeMap.entrySet()) {
				merges.putNode(
						currentTypings.getNode(nodes.getKey()), 
						nodes.getValue()
				);
			}
		}
		
		
		return merges;
	}
	
	/**
	 * @param typeGraph
	 * @param graph
	 * @return
	 */
	public static AbstractNodeEdgeMap<Node,Node,Edge,Edge> addTyping(Graph typeGraph, Graph graph) {
		
		AbstractNodeEdgeMap<Node,Node,Edge,Edge> result = 
			new GenericNodeEdgeHashMap<Node,Node,Edge,Edge>();
		
		for (Node node : graph.nodeSet()) {
			Node newNode = DefaultNode.createNode();
			result.putNode(node, newNode);
			typeGraph.addNode(newNode);
		}
		for (Edge edge : graph.edgeSet()) {
			Edge newEdge = DefaultEdge.createEdge(
					result.getNode(edge.end(Edge.SOURCE_INDEX)),
					edge.label(),
					result.getNode(edge.end(Edge.TARGET_INDEX))
			);
			result.putEdge(edge, newEdge);
			typeGraph.addEdge(newEdge);
		}
		
		return result;
	}
	
	public static AbstractNodeEdgeMap<Node,Node,Edge,Edge> addTyping(Graph typeGraph, Rule rule) {
		
		Graph resultGraph = new DefaultGraph();
		resultGraph.addNodeSet(rule.rhs().nodeSet());
		resultGraph.addEdgeSet(rule.rhs().edgeSet());
		resultGraph.addNodeSet(rule.lhs().nodeSet());
		resultGraph.addEdgeSet(rule.lhs().edgeSet());
		
		return addTyping(typeGraph, resultGraph);
	}
	
	
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
	
}