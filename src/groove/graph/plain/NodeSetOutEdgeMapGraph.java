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
 * $Id: DefaultGraph.java,v 1.8 2008-01-30 09:32:51 iovka Exp $
 */
package groove.graph.plain;

import groove.graph.AGraph;
import groove.graph.Edge;
import groove.graph.GraphRole;
import groove.graph.Node;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of Graph based on a set of nodes and a mapping from nodes to
 * sets of outgoing edges.
 * To change the implementation, override both {@link #outEdgeSet(Node)} and
 * {@link #nodeSet()}.
 * @author Arend Rensink
 * @version $Revision: 4813 $ $Date: 2008-01-30 09:32:51 $
 */
abstract public class NodeSetOutEdgeMapGraph<N extends Node,E extends Edge>
        extends AGraph<N,E> {
    /**
     * Constructs a new, empty Graph.
     * @ensure result.isEmpty()
     * @param name the (non-{@code null}) name of the graph.
     */
    public NodeSetOutEdgeMapGraph(String name, GraphRole role) {
        super(name);
        this.role = role;
    }

    /**
     * Constructs a clone of a given Graph.
     * @param graph the DefaultGraph to be cloned
     * @require graph != null
     * @ensure result.equals(graph)
     */
    protected NodeSetOutEdgeMapGraph(NodeSetOutEdgeMapGraph<N,E> graph) {
        super(graph.getName());
        for (Map.Entry<N,Set<E>> edgeEntry : graph.edgeMap.entrySet()) {
            this.edgeMap.put(edgeEntry.getKey(),
                new LinkedHashSet<E>(edgeEntry.getValue()));
        }
    }

    @Override
    public boolean containsNode(Node node) {
        return nodeSet().contains(node);
    }

    @Override
    public boolean containsEdge(Edge edge) {
        Set<E> edgeSet = getEdgeMap().get(edge.source());
        return edgeSet != null && edgeSet.contains(edge);
    }

    public Set<? extends E> edgeSet() {
        Set<E> result = new LinkedHashSet<E>();
        for (N node : nodeSet()) {
            result.addAll(outEdgeSet(node));
        }
        return Collections.unmodifiableSet(result);
    }

    @Override
    public Set<E> outEdgeSet(Node node) {
        return Collections.unmodifiableSet(getEdgeMap().get(node));
    }

    public Set<? extends N> nodeSet() {
        return getEdgeMap().keySet();
    }

    public boolean addNode(N node) {
        assert !isFixed() : "Trying to add " + node + " to unmodifiable graph";
        boolean added = !containsNode(node);
        if (added) {
            this.edgeMap.put(node, new LinkedHashSet<E>());
            fireAddNode(node);
        }
        return added;
    }

    public boolean addEdge(E edge) {
        assert isTypeCorrect(edge);
        assert !isFixed() : "Trying to add " + edge + " to unmodifiable graph";
        Set<E> sourceOutEdges = getEdgeMap().get(edge.source());
        boolean added = sourceOutEdges.add(edge);
        if (added) {
            fireAddEdge(edge);
        }
        return added;
    }

    public boolean removeEdge(E edge) {
        assert !isFixed() : "Trying to remove " + edge
            + " from unmodifiable graph";
        Set<E> outEdgeSet = getEdgeMap().get(edge.source());
        boolean removed = outEdgeSet != null && outEdgeSet.remove(edge);
        if (removed) {
            fireRemoveEdge(edge);
        }
        return removed;
    }

    /** Reimplementation to improve performance. */
    @Override
    public boolean removeNodeContext(N node) {
        assert !isFixed() : "Trying to remove " + node
            + " from unmodifiable graph";
        boolean result = false;
        Set<E> outEdges = getEdgeMap().remove(node);
        if (outEdges != null) {
            result = true;
            for (E outEdge : outEdges) {
                fireRemoveEdge(outEdge);
            }
            for (Set<E> edgeSet : getEdgeMap().values()) {
                Iterator<E> edgeIter = edgeSet.iterator();
                while (edgeIter.hasNext()) {
                    E edge = edgeIter.next();
                    if (edge.source().equals(node)
                        || edge.target().equals(node)) {
                        // remove and notify observers
                        edgeIter.remove();
                        fireRemoveEdge(edge);
                    }
                }
            }
            fireRemoveNode(node);
        }
        return result;
    }

    public boolean removeNode(N node) {
        assert !isFixed() : "Trying to remove " + node
            + " from unmodifiable graph";
        boolean result = false;
        Set<E> outEdges = getEdgeMap().remove(node);
        if (outEdges != null) {
            result = true;
            fireRemoveNode(node);
        }
        return result;
    }

    /**
     * Returns the role of this default graph.
     */
    public final GraphRole getRole() {
        return this.role;
    }

    /** The role of this default graph. */
    private GraphRole role;

    /** Returns the node-to-edge map. */
    protected Map<N,Set<E>> getEdgeMap() {
        return this.edgeMap;
    }

    /** Factory method for the node-to-edge map. */
    protected Map<N,Set<E>> createEdgeMap() {
        return new LinkedHashMap<N,Set<E>>();
    }

    /**
     * Map from the nodes of this graph to the corresponding sets of outgoing
     * edges.
     * @invariant <tt>edgeMap: DefaultNode -> 2^DefaultEdge</tt>
     */
    private final Map<N,Set<E>> edgeMap = new LinkedHashMap<N,Set<E>>();
}