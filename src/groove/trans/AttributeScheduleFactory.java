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
 * $Id: AttributeScheduleFactory.java,v 1.3 2007-03-30 15:50:26 rensink Exp $
 */

package groove.trans;

import groove.algebra.Variable;
import groove.graph.BinaryEdge;
import groove.graph.DefaultEdge;
import groove.graph.DefaultGraph;
import groove.graph.DefaultLabel;
import groove.graph.DefaultNode;
import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.Graph;
import groove.graph.Node;
import groove.graph.algebra.AlgebraEdge;
import groove.graph.algebra.ProductEdge;
import groove.graph.algebra.ProductNode;
import groove.graph.algebra.ValueNode;
import groove.util.Bag;
import groove.util.HashBag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
/**
 * This class is a specialised version of the InDegreeOrderStrategy. It uses the
 * construct-method of its superclass and thereafter reorders the algebra-elements,
 * that may not be matched in the source graph.
 * 
 * @author Harmen Kastenberg
 * @version $Revision: 1.3 $ $Date: 2007-03-30 15:50:26 $
 */
public class AttributeScheduleFactory extends IndegreeScheduleFactory {
	/** A label with the empty string as text. */
	public static final DefaultLabel EMPTY_LABEL = DefaultLabel.createLabel("");
	
    /* (non-Javadoc)
	 * @see groove.trans.AbstractScheduleFactory#newSchedule(groove.graph.Graph, java.util.Set, java.util.Set)
	 */
	@Override
    public List<Element> newMatchingOrder(Graph subject, Set<Node> matchedNodes, Set<Edge> matchedEdges) {
    	// if the graph does not contain any attributes, the current
    	// result can be returned
    	List<Element> result = super.newMatchingOrder(subject, matchedNodes, matchedEdges);
    	Map<Node, Node> dependenceMap = new HashMap<Node, Node>();
    	Graph dependenceGraph = buildDependenceGraph(subject, dependenceMap);
    	// build up a list of nodes of the dependence-graph in a bottom-up fashion
    	List<Node> bottomUpNodeList = new ArrayList<Node>();
    	// calculate all indegrees which enable us later on to look for root elements
    	Bag<Node> indegrees = indegrees(dependenceGraph);

    	// starting from the root (there may be more roots)
    	// for every root, visit its successor-nodes
    	for (Node nextRoot: getRoots(dependenceGraph, indegrees)) {
    		visit(dependenceGraph, nextRoot, bottomUpNodeList);
    	}

    	// translate the created matching-schedule into the corresponding list of elements from
    	// the original graph
    	List<Node> operationNodeMatchingSchedule = translateToOriginal(dependenceMap, bottomUpNodeList);

    	List<Element> operatorEdges = new ArrayList<Element>();
    	Iterator<Element> resultIter = result.iterator();
    	while (resultIter.hasNext()) {
    		Element nextElement = resultIter.next();
    		if (nextElement instanceof Edge) {
    			Node source = ((Edge) nextElement).source();
    			if (source instanceof ProductNode) {
    				resultIter.remove();
    				if (nextElement instanceof ProductEdge && ! (source instanceof ValueNode)) {
    					operatorEdges.add(nextElement);
    				}
    			}
    		} else if (nextElement instanceof ValueNode) {
    			if (((ValueNode) nextElement).getConstant() instanceof Variable) {
    				resultIter.remove();
    			}
    		}
    	}

    	// now we have all the elements for the matching-schedule
    	result.addAll(operationNodeMatchingSchedule);
    	result.addAll(operatorEdges);

    	return result;
    }

    /**
	 * Calculate the indegrees of the nodes of the given graph.
     * @param dependenceGraph the graph for which we calculate the indegrees for all its nodes
     * @return the multi-set of nodes of the given graph. The indegree of a node is equal to the number
     * of occurrences of that node in this multi-set
     */
    private Bag<Node> indegrees(Graph dependenceGraph) {
        Bag<Node> indegrees = new HashBag<Node>();
        for (Edge edge: dependenceGraph.edgeSet()) {
            for (int i = 1; i < edge.endCount(); i++) {
                Node source = edge.source();
                Node endpoint = edge.end(i);
                if (!source.equals(endpoint)) {
                    indegrees.add(edge.end(i));
                }
            }
        }
        return indegrees;
    }

	/**
	 * Creates a list containing the roots of the given graph. An element of that graph is a root if it is
	 * in the node-set of that graph and has indegree equal to 0.
     * @param dependenceGraph the graph of which we will return the list of root-nodes
     * @param indegrees the multi-set from which we are able to determine whether an alement is a root or not
     * @return the list of root elements
     */
    private List<Node> getRoots(Graph dependenceGraph, Bag<Node> indegrees) {
        List<Node> roots = new ArrayList<Node>();
        // look for the roots
        for (Node nextNode: dependenceGraph.nodeSet()) {
            if (indegrees.multiplicity(nextNode) == 0) {
                roots.add(nextNode);
            }
        }
        return roots;
    }

    /**
     * Returns the list of translated nodes.
     * @param dependenceMap the map containing the node translations
     * @param bottomUpNodeList the nodes to be translated
     * @return the list of translated nodes
     */
    private List<Node> translateToOriginal(Map<Node, Node> dependenceMap, List<Node> bottomUpNodeList) {
        List<Node> result = new ArrayList<Node>();
		for (Node nextNode : bottomUpNodeList) {
			for (Map.Entry<Node, Node> nextEntry : dependenceMap.entrySet()) {
				if (nextEntry.getValue().equals(nextNode))
					result.add(nextEntry.getKey());
			}
		}
		return result;
    }

    /**
     * Builds a graph in which the edges represent dependency relations.
	 * @param subject the original graph
	 * @param dependenceMap the map in which to put the translation
	 * @return the graph representing the dependency relations between algebra
	 * nodes in the original graph
	 */
    private Graph buildDependenceGraph(Graph subject, Map<Node, Node> dependenceMap) {
	    Graph result = new DefaultGraph();
	    for (Edge nextEdge: subject.edgeSet()) {
	    	Edge newEdge = null;
	    	Node source = dependenceMap.get(nextEdge.end(Edge.SOURCE_INDEX));
	    	if (source == null) {
	    		source = new DefaultNode();
	    		dependenceMap.put(nextEdge.end(Edge.SOURCE_INDEX), source);
	    	}

	    	Node target = dependenceMap.get(nextEdge.end(Edge.TARGET_INDEX));
	    	if (target == null) {
	    		target = new DefaultNode();
	    		dependenceMap.put(nextEdge.end(Edge.TARGET_INDEX), target);
	    	}

	    	if (nextEdge instanceof ProductEdge && ((ValueNode) nextEdge.opposite()).getConstant() instanceof Variable) {
                newEdge = DefaultEdge.createEdge(target, EMPTY_LABEL, source);
	    	}

	    	if (nextEdge instanceof AlgebraEdge) {
                newEdge = DefaultEdge.createEdge(source, EMPTY_LABEL, target);
            }
	    	if (newEdge != null)
	            result.addEdge(newEdge);
	    }
	    return result;
	}

	/**
	 * Visits the given graph starting with the given node and adds
	 * all reachable nodes in a bottom-up fashion to the given list.
	 * @param graph the graph to be visited
	 * @param node the node to start with
	 * @param currentOrder the list of nodes visited (bottom-up)
	 */
	private void visit(Graph graph, Node node, List<Node> currentOrder) {
		for (Edge nextOutEdge: graph.outEdgeSet(node)) {
            if (nextOutEdge instanceof BinaryEdge) {
                visit(graph, ((BinaryEdge) nextOutEdge).target(), currentOrder);
            }
            // CODE: when supporting unary and hyper-edges this part needs to be extended.
        }
		currentOrder.add(node);
    }
}
