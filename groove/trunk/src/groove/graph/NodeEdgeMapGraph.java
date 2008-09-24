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
 * $Id: NodeEdgeMapGraph.java,v 1.6 2008-01-30 09:32:50 iovka Exp $
 */
package groove.graph;

import groove.util.SetOfDisjointSets;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of {@link groove.graph.Graph} based on node-to-edge-set maps for
 * all node ends. This facilitates node removal, but is expensive in terms of space.
 * Arbitrary edge arities are supported.
 * @version $Revision$ $Date: 2008-01-30 09:32:50 $
 */
public class NodeEdgeMapGraph extends AbstractGraph<GraphCache> {
	/**
     * Constructs a protytpe object of this class, to be used as a factory
     * for new (default) graphs.
     * @return a prototype <tt>DefaultGraph</tt> instance, only intended to
     * be used for its <tt>newGraph()</tt> method.
     */
    static public Graph getPrototype() {
        return new NodeEdgeMapGraph();
    }
    
    /**
     * The maximum number of edge ends.
     * We need to record this because apparently {@link AbstractEdge#getMaxEndCount()}
     * is not working reliably. 
     */
    static private final int MAX_END_COUNT = 2;
    
    /** 
     * Constructs a new, empty Graph.
     * @ensure result.isEmpty()
     */
    public NodeEdgeMapGraph() {
    	// we need an explicit empty constructor
    }

    /** 
     * Constructs a clone of a given Graph.
     * @param graph the DefaultGraph to be cloned
     * @require graph != null
     * @ensure result.equals(graph)
     */
    protected NodeEdgeMapGraph(NodeEdgeMapGraph graph) {
        this();
        for (Map.Entry<Node,Set<Edge>> edgeEntry : graph.sourceEdgeMap.entrySet()) {
            sourceEdgeMap.put(edgeEntry.getKey(), new HashSet<Edge>(edgeEntry.getValue()));
        }
    }

    @Override
    public boolean containsElement(Element elem) {
        reporter.start(CONTAINS_ELEMENT);
        try {
            if (elem instanceof Node)
                return sourceEdgeMap.containsKey(elem);
            else {
                assert elem instanceof Edge;
                Set<Edge> edgeSet = sourceEdgeMap.get(((Edge) elem).source());
                return edgeSet != null && edgeSet.contains(elem);
            }
        } finally {
            reporter.stop();
        }
    }

    public Set<? extends Edge> edgeSet() {
        reporter.start(EDGE_SET);
        Set<Edge> result = new SetOfDisjointSets<Edge>(sourceEdgeMap.values());
        reporter.stop();
        return result;
    }
    
    @Override
    public int edgeCount() {
    	return edgeCount;
    }

    public Set<? extends Node> nodeSet() {
        reporter.start(NODE_SET);
        Set<Node> result = unmodifiableNodeSet;
        reporter.stop();
        return result;
    }

    // ------------------------ OBJECT OVERRIDES -----------------------

    @Override
    public NodeEdgeMapGraph clone() {
        reporter.start(CLONE);
        NodeEdgeMapGraph result = new NodeEdgeMapGraph(this);
        reporter.stop();
        return result;
    }

    public NodeEdgeMapGraph newGraph() {
        return new NodeEdgeMapGraph();
    }

    // ------------------------- COMMANDS ------------------------------

    public boolean addNode(Node node) {
        reporter.start(ADD_NODE);
        assert !isFixed() : "Trying to add " + node + " to unmodifiable graph";
        boolean added = !containsElement(node);
        if (added) {
            assert nodeCount() == new HashSet<Node>(nodeSet()).size() : String.format("Overlapping node number for %s in %s", node, nodeSet());
            sourceEdgeMap.put(node, new HashSet<Edge>());
            fireAddNode(node);
        }
        reporter.stop();
        return added;
    }


    public boolean addEdge(Edge edge) {
    	boolean result = false;
        reporter.start(ADD_EDGE);
        assert !isFixed() : "Trying to add " + edge + " to unmodifiable graph";
        for (int i = 0; i < edge.endCount(); i++) {
        	Node end = edge.end(i);
        	// first add the edge end if it does not yet exist
        	if (!nodeSet.contains(end)) {
                addNode(edge.source());
        	}
        	// now look up the map for this edge end
        	Set<Edge> outEdgeSet = nodeEdgeMaps[i].get(edge.source());
        	// create the map if it does not yet exist
        	if (outEdgeSet == null) {
        		nodeEdgeMaps[i].put(end, outEdgeSet = new HashSet<Edge>());
        	}
            result = outEdgeSet.add(edge);
        }
        if (result) {
        	edgeCount++;
            fireAddEdge(edge);
        }
        reporter.stop();
        return result;
    }

    public boolean addEdgeWithoutCheck(Edge edge) {
        reporter.start(ADD_EDGE);
        boolean result = addEdge(edge);
        reporter.stop();
        return result;
    }

    public boolean removeEdge(Edge edge) {
    	boolean result = false;
        reporter.start(REMOVE_EDGE);
        assert !isFixed() : "Trying to remove " + edge + " from unmodifiable graph";
        for (int i = 0; i < edge.endCount(); i++) {
        	Set<Edge> edgeSet = nodeEdgeMaps[i].get(edge.end(i));
        	result = edgeSet.remove(edge);
        }
        if (result) {
        	edgeCount--;
            fireRemoveEdge(edge);
        }
        reporter.stop();
        return result;
    }

    public boolean removeNode(Node node) {
        reporter.start(REMOVE_NODE);
        assert !isFixed() : "Trying to remove " + node + " from unmodifiable graph";
        boolean result = false;
        for (Map<Node,Set<Edge>> endEdgeMap: nodeEdgeMaps) {
        	Set<Edge> edgeSet = endEdgeMap.get(node);
        	if (edgeSet != null) {
        		for (Edge edge: edgeSet) {
        			removeEdge(edge);
        		}
        		result = true;
        	}
        }
        if (result) {
        	removeNodeWithoutCheck(node);
        }
        reporter.stop();
        return result;
    }

    public boolean removeNodeWithoutCheck(Node node) {
        reporter.start(REMOVE_NODE);
        assert !isFixed() : "Trying to remove " + node + " from unmodifiable graph";
        boolean result = false;
        for (Map<Node,Set<Edge>> endEdgeMap: nodeEdgeMaps) {
        	result |= endEdgeMap.remove(node) != null;
        }
        if (result) {
            fireRemoveNode(node);
        }
        reporter.stop();
        return result;
    }

    private final Map<Node,Set<Edge>>[] nodeEdgeMaps = new Map[MAX_END_COUNT];
    {
    	for (int i = 0; i < nodeEdgeMaps.length; i++) {
    		nodeEdgeMaps[i] = new HashMap<Node,Set<Edge>>();
    	}
    }
    /**
     * Map from the nodes of this graph to the corresponding
     * sets of outgoing edges.
     * @invariant <tt>edgeMap: Node -> 2^Edge</tt>
     */
    private final Map<Node,Set<Edge>> sourceEdgeMap = nodeEdgeMaps[Edge.SOURCE_INDEX];
    /**
     * The number of edges in the graph.
     */
    private int edgeCount;
    /**
     * Alias of the set of nodes in this Graph.
     */
    private final Set<Node> nodeSet = sourceEdgeMap.keySet();
    /**
     * An unmodifieable, shared view on the node set of this graph.
     */
    private final Set<Node> unmodifiableNodeSet = Collections.unmodifiableSet(nodeSet);
}
