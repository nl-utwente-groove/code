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
 * $Id: ExploreResult.java 6072 2021-07-14 18:23:50Z rensink $
 */
package nl.utwente.groove.graph;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * A subset of the nodes and edges in a given graph.
 */
@NonNullByDefault
public abstract class AOverlay<G extends GGraph<? extends N,? extends GEdge<N>>,N extends Node,E extends GEdge<? extends N>> {
    /**
     * Creates a fresh, empty result for a given graph.
     */
    public AOverlay(G gts) {
        this.graph = gts;
        this.nodes = createResultSet();
        this.lastNode = Optional.empty();
        this.edges = createResultSet();
    }

    /** Returns the graph of which this is an overlay. */
    public G graph() {
        return this.graph;
    }

    private final G graph;

    /**
     * Adds a node to the overlay.
     */
    public void addNode(N node) {
        this.nodes.add(node);
        this.lastNode = Optional.of(node);
    }

    /** Tests if this overlay contains a given node. */
    public boolean containsNode(N node) {
        return nodes().contains(node);
    }

    /**
     * The set of nodes contained in the overlay.
     */
    public Collection<N> nodes() {
        return this.nodes;
    }

    /** The nodes stored in this overlay. */
    private final Collection<N> nodes;

    /** Returns the most recently added node. */
    public Optional<N> lastNode() {
        return this.lastNode;
    }

    /** The most recently added state. */
    private Optional<N> lastNode;

    /** Returns the number of nodes currently stored in this overlay. */
    public int nodeCount() {
        return nodes().size();
    }

    /** Indicates if this overlay is currently empty. */
    public boolean isEmpty() {
        return nodeCount() == 0;
    }

    /** Adds an edge to the overlay, together with its source and target nodes.
     * @return {@code true} if the edge was not already in the overlay.
      */
    public boolean addEdge(E edge) {
        boolean result = edges().add(edge);
        if (result) {
            addNode(edge.source());
            addNode(edge.target());
        }
        return result;
    }

    /** Returns the set of transitions stored in this result. */
    public Collection<E> edges() {
        return this.edges;
    }

    private final Collection<E> edges;

    /** Tests if this overlay contains a given node. */
    public boolean containsEdge(E edge) {
        return edges().contains(edge);
    }

    /** Returns the number of edges currently stored in this overlay. */
    public int edgeCount() {
        return edges().size();
    }

    /** Callback factory method for the result set. */
    protected <T> Collection<T> createResultSet() {
        return new LinkedHashSet<>();
    }

    @Override
    public String toString() {
        return "Overlay: nodes = " + nodes() + ", edges = " + edges();
    }
}
