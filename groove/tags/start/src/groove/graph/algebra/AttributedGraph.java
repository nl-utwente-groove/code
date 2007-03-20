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
 * $Id: AttributedGraph.java,v 1.1.1.2 2007-03-20 10:42:43 kastenberg Exp $
 */

package groove.graph.algebra;

import groove.algebra.Constant;
import groove.graph.DefaultGraph;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.GraphFactory;
import groove.graph.GraphFormatException;
import groove.graph.GraphInfo;
import groove.graph.Node;
import groove.graph.NodeEdgeHashMap;
import groove.graph.NodeEdgeMap;

/**
 * This class constructs a real attributed graph from an encoded attributed
 * graph. It uses a graph factory for creating actual graphs which will then
 * contain nodes representing algebraic data values without the graph itself
 * knowing this.
 * 
 * @author Harmen Kastenberg
 * @version $Revision: 1.1.1.2 $ $Date: 2007-03-20 10:42:43 $
 */
public class AttributedGraph extends DefaultGraph {

	/**
	 * The graph factory for creating new graphs.
	 */
	private GraphFactory factory;

	/**
	 * Creates an attributed graph with the given graph factory.
	 * @param factory the factory for creating actual graphs.
	 */
	public AttributedGraph(GraphFactory factory) {
		this.factory = factory;
	}

	/* (non-Javadoc)
	 * @see groove.graph.AbstractGraph#newGraph(groove.graph.Graph)
	 */
	public Graph newGraph(Graph graph) throws GraphFormatException {
		Graph result = factory.newGraph();

		NodeEdgeMap graphToAttributedGraphMap = new NodeEdgeHashMap();

		AlgebraGraph algebraGraph = AlgebraGraph.getInstance();

		// first add all the nodes and watch for nodes representing algebraic data values
		Node newNode = null;
		for (Node nextNode: graph.nodeSet()) {
			Constant constant = AlgebraConstants.getNodeValue(graph, nextNode);
			if (constant != null) {
				// if this algebra node is not connected to the graph, we'd
				// better not include it
				if (isConnectedToGraph(graph, nextNode)) {
					newNode = algebraGraph.getValueNode(constant);
				} else {
					// do not set the newNode-variable
				}
			}
			else {
				newNode = nextNode;
			}

			if (newNode != null) {
				graphToAttributedGraphMap.putNode(nextNode, newNode);
				result.addNode(newNode);
			}
		}

		// now add the edges
		for (Edge nextEdge: graph.edgeSet()) {
			// if the source of this edge is not in the graph, discard this edge
			// since it was used for determining the algebraic data value the
			// node represents
			Node source = nextEdge.end(Edge.SOURCE_INDEX);
			Node target = nextEdge.end(Edge.TARGET_INDEX);
			if (result.nodeSet().contains(source)) {
				Edge newEdge;
				if (!(result.nodeSet().contains(target))) {
					// the edge is an attribute edge and we have to replace the
					// target-node with the correct value node
					Node valueNode = graphToAttributedGraphMap.getNode(target);
					newEdge = new AttributeEdge(source, nextEdge.label(), valueNode);
				}
				else {
					// both source and target nodes are already in the graph
					// so we can also add the edge now
					newEdge = nextEdge;
				}
				graphToAttributedGraphMap.putEdge(nextEdge, nextEdge);
				result.addEdge(newEdge);
			}
			// if the source node is not even in the graph, we do not add
			// any edge to the resulting graph
		}
        GraphInfo graphInfo = graph.getInfo();
        GraphInfo resultInfo = result.setInfo(graphInfo);
        if (graphInfo != null && graphInfo.hasLayoutMap()) {
                resultInfo.setLayoutMap(graphInfo.getLayoutMap().afterInverse(graphToAttributedGraphMap));
        }
		return result;
	}

	/**
	 * Checks whether the given node, representing an algebraic data value,
	 * is connected to the graph. It returns <tt>true</tt> it this is the case,
	 * <tt>false</tt> otherwise.
	 * @param graph the graph in which this node occurs
	 * @param node the node te be checked for connectedness
	 * @return <tt>true</tt> if the node is connected to the rest of the graph,
	 * <tt>false</tt> otherwise
	 */
	private boolean isConnectedToGraph(Graph graph, Node node) {
		boolean isConnected = false;
		for (Edge nextEdge : graph.edgeSet(node)) {
			// checking whether the source and target nodes are
			// different is sufficient
			if (!nextEdge.end(Edge.SOURCE_INDEX).equals(nextEdge.end(Edge.TARGET_INDEX)))
				isConnected = true;
		}
		return isConnected;
	}
}
