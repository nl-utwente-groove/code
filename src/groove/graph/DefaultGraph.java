// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente
 
// Licensed under the Apache License, Version 2.0 (the "License"); 
// you may not use this file except in compliance with the License. 
// You may obtain a copy of the License at 
// http://www.apache.org/licenses/LICENSE-2.0 
 
// Unless required by applicable law or agreed to in writing, 
// software distributed under the License is distributed on an 
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
// either express or implied. See the License for the specific 
// language governing permissions and limitations under the License.
/* 
 * $Id: DefaultGraph.java,v 1.3 2007-04-12 13:45:34 rensink Exp $
 */
package groove.graph;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of Graph based on a set of nodes and a 
 * mapping from nodes to sets of outgoing edges.
 * @author Arend Rensink
 * @version $Revision: 1.3 $ $Date: 2007-04-12 13:45:34 $
 */
public class DefaultGraph extends AbstractGraph {
    /**
     * Constructs a protytpe object of this class, to be used as a factory
     * for new (default) graphs.
     * @return a prototype <tt>DefaultGraph</tt> instance, only intended to
     * be used for its <tt>newGraph()</tt> method.
     */
    static public Graph getPrototype() {
        return new DefaultGraph();
    }
    
    /** 
     * Constructs a new, empty Graph.
     * @ensure result.isEmpty()
     */
    public DefaultGraph() {
    	// we need an explicit empty constructor
    }

    /** 
     * Constructs a clone of a given Graph.
     * @param graph the DefaultGraph to be cloned
     * @require graph != null
     * @ensure result.equals(graph)
     */
    protected DefaultGraph(DefaultGraph graph) {
        this();
        for (Map.Entry<Node,Set<Edge>> edgeEntry: graph.edgeMap.entrySet()) {
            edgeMap.put(edgeEntry.getKey(), new HashSet<Edge>(edgeEntry.getValue()));
        }
    }

    @Override
    public boolean containsElement(Element elem) {
        reporter.start(CONTAINS_ELEMENT);
        try {
            if (elem instanceof Node)
                return edgeMap.containsKey(elem);
            else {
                assert elem instanceof Edge;
                Set<Edge> edgeSet = edgeMap.get(((Edge) elem).source());
                return edgeSet != null && edgeSet.contains(elem);
            }
        } finally {
            reporter.stop();
        }
    }

    public Set<? extends Edge> edgeSet() {
        reporter.start(EDGE_SET);
        Set<Edge> result = new HashSet<Edge>();
        for (Map.Entry<Node,Set<Edge>> edgeEntry: edgeMap.entrySet()) {
            result.addAll(edgeEntry.getValue());
        }
        reporter.stop();
        return Collections.unmodifiableSet(result);
    }
    
	@Override
	public Collection<? extends Edge> outEdgeSet(Node node) {
		return Collections.unmodifiableSet(edgeMap.get(node));
	}

	public Set<? extends Node> nodeSet() {
        reporter.start(NODE_SET);
        Set<Node> result = unmodifiableNodeSet;
        reporter.stop();
        return result;
    }

    @Deprecated
    @Override
    public Iterator<? extends Edge> edgeIterator() {
        Iterator<? extends Edge> res = new Iterator<Edge>() {
            public boolean hasNext() {
                forwardEdgeSetIter();
                return forwardEdgeSetIter();
            }

            public Edge next() {
                forwardEdgeSetIter();
                return edgeIter.next();
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }

            private boolean forwardEdgeSetIter() {
                while (edgeIter == null || !edgeIter.hasNext())
                    if (edgeSetIter.hasNext())
                        edgeIter = edgeSetIter.next().iterator();
                    else
                        return false;
                return true;
            }

            private Iterator<Set<Edge>> edgeSetIter = edgeMap.values().iterator();
            private Iterator<Edge> edgeIter;
        };
        return res;
    }

    // ------------------------ OBJECT OVERRIDES -----------------------

    @Override
    public Graph clone() {
        reporter.start(CLONE);
        Graph result = new DefaultGraph(this);
        reporter.stop();
        return result;
    }

    public Graph newGraph() {
        return new DefaultGraph();
    }

    // ------------------------- COMMANDS ------------------------------

    public boolean addNode(Node node) {
        reporter.start(ADD_NODE);
        assert !isFixed() : "Trying to add " + node + " to unmodifiable graph";
        boolean added = !containsElement(node);
        assert added == ! new HashSet<Node>(nodeSet()).contains(node) : String.format("Overlapping node number for %s in %s", node, nodeSet());
        if (added) {
            edgeMap.put(node, new HashSet<Edge>());
            fireAddNode(node);
        }
        reporter.stop();
        return added;
    }


    public boolean addEdge(Edge edge) {
        reporter.start(ADD_EDGE);
//        assert edge instanceof BinaryEdge : "This graph implementation only supports binary edges";
        assert !isFixed() : "Trying to add " + edge + " to unmodifiable graph";
        Set<Edge> sourceOutEdges = edgeMap.get(edge.source());
        if (sourceOutEdges == null) {
            addNode(edge.source());
            sourceOutEdges = edgeMap.get(edge.source());
        }
        for (int i = 1; i < edge.endCount(); i++) {
            Node end = edge.end(i);
            if (!edgeMap.containsKey(end)) {
                addNode(end);
            }
        }
        boolean added = sourceOutEdges.add(edge);
        if (added)
            fireAddEdge(edge);
        reporter.stop();
        return added;
    }

    public boolean addEdgeWithoutCheck(Edge edge) {
        reporter.start(ADD_EDGE);
        assert !isFixed() : "Trying to add " + edge + " to unmodifiable graph";
        Set<Edge> sourceOutEdges = edgeMap.get(edge.source());
        boolean added = sourceOutEdges.add(edge);
        if (added)
            fireAddEdge(edge);
        reporter.stop();
        return added;
    }

    public boolean removeEdge(Edge edge) {
        reporter.start(REMOVE_EDGE);
        assert !isFixed() : "Trying to remove " + edge + " from unmodifiable graph";
        Set<Edge> outEdgeSet = edgeMap.get(edge.source());
        boolean removed = outEdgeSet != null && outEdgeSet.remove(edge);
        if (removed) {
            fireRemoveEdge(edge);
        }
        reporter.stop();
        return removed;
    }

    public boolean removeNode(Node node) {
        reporter.start(REMOVE_NODE);
        assert !isFixed() : "Trying to remove " + node + " from unmodifiable graph";
        boolean result = false;
        Set<Edge> outEdges = edgeMap.remove(node);
        if (outEdges != null) {
            result = true;
            for (Edge outEdge: outEdges) {
                fireRemoveEdge(outEdge);
            }
            for (Set<Edge> edgeSet: edgeMap.values()) {
            	Iterator<Edge> edgeIter = edgeSet.iterator();
                while (edgeIter.hasNext()) {
                    Edge edge = edgeIter.next();
                    boolean nodeFound = false;
                    for (int i = 1; !nodeFound && i < edge.endCount(); i++) {
                        nodeFound = edge.end(i).equals(node);
                    }
                    if (nodeFound) {
                        // remove and notify observers
                        edgeIter.remove();
                        fireRemoveEdge(edge);
                    }
                }
            }
            fireRemoveNode(node);
        }
        reporter.stop();
        return result;
    }

    public boolean removeNodeWithoutCheck(Node node) {
        reporter.start(REMOVE_NODE);
        assert !isFixed() : "Trying to remove " + node + " from unmodifiable graph";
        boolean result = false;
        Set<Edge> outEdges = edgeMap.remove(node);
        if (outEdges != null) {
            result = true;
            fireRemoveNode(node);
        }
        reporter.stop();
        return result;
    }

    /**
     * Map from the nodes of this graph to the corresponding
     * sets of outgoing edges.
     * @invariant <tt>edgeMap: Node -> 2^Edge</tt>
     */
    private final Map<Node,Set<Edge>> edgeMap = new HashMap<Node,Set<Edge>>();
    /**
     * Alias of the set of nodes in this Graph.
     */
    private final Set<Node> nodeSet = edgeMap.keySet();
    /**
     * An unmodifieable, shared view on the node set of this graph.
     */
    private final Set<Node> unmodifiableNodeSet = Collections.unmodifiableSet(nodeSet);
}