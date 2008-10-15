/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id: AdjacencyMapGraph.java,v 1.6 2008-01-30 09:32:50 iovka Exp $
 */
package groove.graph;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Graph implementation based on a mapping from nodes to incident edges. This
 * gives good performance of <tt>removeNode(Node)</tt> but, on the other hand,
 * <tt>edgeSet()</tt> and <tt>edgeIterator()</tt> pay a heavy penalty. Also
 * memory consumption may not be so good.
 * @author Arend Rensink
 * @version $Revision$ $Date: 2008-01-30 09:32:50 $
 */
public class AdjacencyMapGraph extends AbstractGraph<GraphCache> {
    /**
     * Constructs a prototype object of this class, to be used as a factory for
     * new (default) graphs.
     * @return a prototype <tt>AdjacencyMapGraph</tt> instance, only intended
     *         to be used for its <tt>newGraph()</tt> method.
     */
    static Graph getPrototype() {
        return new AdjacencyMapGraph();
    }

    /**
     * Constructs a new, empty graph.
     * @ensure <tt>result.isEmpty()</tt>
     */
    public AdjacencyMapGraph() { // need explicit empty constructor
    }

    /**
     * Constructs a clone of a given Graph.
     * @param graph the raph to be cloned
     * @require <tt>graph != null</tt>
     * @ensure <tt>result.equals(graph)</tt>
     */
    protected AdjacencyMapGraph(AdjacencyMapGraph graph) {
        this();
        for (Map.Entry<Node,Set<Edge>> edgeEntry : graph.edgeMap.entrySet()) {
            this.edgeMap.put(edgeEntry.getKey(), new HashSet<Edge>(
                edgeEntry.getValue()));
        }
    }

    @Override
    public boolean containsElement(Element elem) {
        reporter.start(CONTAINS_ELEMENT);
        try {
            if (elem instanceof Node) {
                return this.edgeMap.containsKey(elem);
            } else {
                assert elem instanceof Edge;
                Set<Edge> edgeSet = this.edgeMap.get(((Edge) elem).source());
                return edgeSet != null && edgeSet.contains(elem);
            }
        } finally {
            reporter.stop();
        }
    }

    public Set<? extends Node> nodeSet() {
        reporter.start(NODE_SET);
        Set<Node> result = this.unmodifiableNodeSet;
        reporter.stop();
        return result;
    }

    // ------------------------ OBJECT OVERRIDES -----------------------

    public Set<? extends Edge> edgeSet() {
        reporter.start(EDGE_SET);
        Set<Edge> result = new HashSet<Edge>();
        for (Map.Entry<Node,Set<Edge>> edgeEntry : this.edgeMap.entrySet()) {
            result.addAll(edgeEntry.getValue());
        }
        reporter.stop();
        return Collections.unmodifiableSet(result);
    }

    @Override
    public Graph clone() {
        reporter.start(CLONE);
        Graph result = new AdjacencyMapGraph(this);
        reporter.stop();
        return result;
    }

    public Graph newGraph() {
        return new AdjacencyMapGraph();
    }

    // ------------------------- COMMANDS ------------------------------

    public boolean addNode(Node node) {
        reporter.start(ADD_NODE);
        assert !isFixed() : "Trying to add " + node + " to unmodifiable graph";
        boolean added = !containsElement(node);
        if (added) {
            assert nodeCount() == new HashSet<Node>(nodeSet()).size() : String.format(
                "Overlapping node number for %s in %s", node, nodeSet());
            this.edgeMap.put(node, new HashSet<Edge>());
            fireAddNode(node);
        }
        reporter.stop();
        return added;
    }

    public boolean addEdge(Edge edge) {
        reporter.start(ADD_EDGE);
        boolean added = false;
        assert !isFixed() : "Trying to add " + edge + " to unmodifiable graph";
        Node[] parts = edge.ends();
        for (Node element : parts) {
            added |= addNodeGetEdges(element).add(edge);
        }
        if (added) {
            fireAddEdge(edge);
        }
        reporter.stop();
        return added;
    }

    public boolean addEdgeWithoutCheck(Edge edge) {
        return addEdge(edge);
    }

    public boolean removeEdge(Edge edge) {
        reporter.start(REMOVE_EDGE);
        assert !isFixed() : "Trying to remove " + edge
            + " from unmodifiable graph";
        boolean removed = false;
        Node[] parts = edge.ends();
        for (Node element : parts) {
            Set<Edge> edgeSet = this.edgeMap.get(element);
            removed |= edgeSet != null && edgeSet.remove(edge);
        }
        if (removed) {
            fireRemoveEdge(edge);
        }
        reporter.stop();
        return removed;
    }

    public boolean removeNode(Node node) {
        reporter.start(REMOVE_NODE);
        assert !isFixed() : "Trying to remove " + node
            + " from unmodifiable graph";
        boolean removed = false;
        Set<Edge> incidentEdges = this.edgeMap.remove(node);
        if (incidentEdges != null) {
            removed = true;
            for (Edge incidentEdge : incidentEdges) {
                removeEdge(incidentEdge);
            }
            fireRemoveNode(node);
        }
        reporter.stop();
        return removed;
    }

    public boolean removeNodeWithoutCheck(Node node) {
        return removeNode(node);
    }

    /**
     * Adds a node to this graph, if it is not already there, and returns the
     * associated set of incident edges. Notifies all graph listeners if the
     * node had to be added first.
     */
    protected Set<Edge> addNodeGetEdges(Node node) {
        Set<Edge> result = this.edgeMap.get(node);
        if (result == null) {
            this.edgeMap.put(node, result = new HashSet<Edge>());
            fireAddNode(node);
        }
        return result;
    }

    /**
     * Map from the nodes of this graph to the corresponding sets of outgoing
     * edges.
     * @invariant <tt>edgeMap: Node -> 2^Edge</tt>
     */
    private final Map<Node,Set<Edge>> edgeMap = new HashMap<Node,Set<Edge>>();
    /**
     * Alias of the set of nodes in this Graph.
     */
    private final Set<Node> nodeSet = this.edgeMap.keySet();
    /**
     * An unmodifieable, shared view on the node set of this graph.
     */
    private final Set<Node> unmodifiableNodeSet =
        Collections.unmodifiableSet(this.nodeSet);
}
