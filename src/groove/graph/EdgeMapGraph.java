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
 * $Id$
 */
package groove.graph;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of {@link Graph} based on a mapping from nodes to
 * sets of outgoing edges.
 * @author Arend Rensink
 * @version $Revision$ $Date: 2008-01-30 09:32:51 $
 */
abstract public class EdgeMapGraph<N extends Node,E extends GEdge<N>> extends AGraph<N,E> implements
    Cloneable {
    /**
     * Constructs a new, empty Graph with a given graph role.
     * @param name the (non-{@code null}) name of the graph.
     * @param role the (non-{@code null}) role of the graph.
     */
    protected EdgeMapGraph(String name, GraphRole role) {
        super(name);
        this.role = role;
    }

    /**
     * Constructs a clone of a given Graph.
     * @param graph the DefaultGraph to be cloned
     * @require graph != null
     * @ensure result.equals(graph)
     */
    protected EdgeMapGraph(EdgeMapGraph<N,E> graph) {
        this(graph.getName(), graph.getRole());
        for (Map.Entry<N,Set<E>> edgeEntry : graph.edgeMap.entrySet()) {
            this.edgeMap.put(edgeEntry.getKey(), new LinkedHashSet<>(edgeEntry.getValue()));
        }
    }

    @Override
    public boolean containsNode(Node node) {
        return this.edgeMap.containsKey(node);
    }

    @Override
    public boolean containsEdge(Edge edge) {
        Set<E> edgeSet = this.edgeMap.get(edge.source());
        return edgeSet != null && edgeSet.contains(edge);
    }

    @Override
    public Set<? extends E> edgeSet() {
        Set<E> result = new LinkedHashSet<>();
        for (Map.Entry<N,Set<E>> edgeEntry : this.edgeMap.entrySet()) {
            result.addAll(edgeEntry.getValue());
        }
        return Collections.unmodifiableSet(result);
    }

    @Override
    public Set<? extends E> outEdgeSet(Node node) {
        return Collections.unmodifiableSet(this.edgeMap.get(node));
    }

    @Override
    public Set<? extends N> nodeSet() {
        return Collections.unmodifiableSet(this.edgeMap.keySet());
    }

    // ------------------------- COMMANDS ------------------------------

    @Override
    public boolean addNode(N node) {
        assert !isFixed() : "Trying to add " + node + " to unmodifiable graph";
        boolean added = !containsNode(node);
        if (added) {
            this.edgeMap.put(node, new LinkedHashSet<E>());
            fireAddNode(node);
        }
        return added;
    }

    @Override
    public boolean addEdge(E edge) {
        assert isTypeCorrect(edge);
        assert !isFixed() : "Trying to add " + edge + " to unmodifiable graph";
        Set<E> sourceOutEdges = this.edgeMap.get(edge.source());
        boolean added = sourceOutEdges.add(edge);
        if (added) {
            fireAddEdge(edge);
        }
        return added;
    }

    @Override
    public boolean removeEdge(E edge) {
        assert !isFixed() : "Trying to remove " + edge + " from unmodifiable graph";
        Set<E> outEdgeSet = this.edgeMap.get(edge.source());
        boolean removed = outEdgeSet != null && outEdgeSet.remove(edge);
        if (removed) {
            fireRemoveEdge(edge);
        }
        return removed;
    }

    @Override
    public boolean removeNode(N node) {
        assert !isFixed() : "Trying to remove " + node + " from unmodifiable graph";
        boolean result = false;
        Set<E> outEdges = this.edgeMap.remove(node);
        if (outEdges != null) {
            result = true;
            fireRemoveNode(node);
        }
        return result;
    }

    /**
     * Returns the role of this default graph, as set in the constructor.
     */
    @Override
    public final GraphRole getRole() {
        return this.role;
    }

    /**
     * Map from the nodes of this graph to the corresponding sets of outgoing
     * edges.
     * @invariant <tt>edgeMap: DefaultNode -> 2^DefaultEdge</tt>
     */
    private final Map<N,Set<E>> edgeMap = new LinkedHashMap<>();

    /** The role of this default graph. */
    private final GraphRole role;
}