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
package nl.utwente.groove.graph;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.graph.iso.CertificateStrategy;
import nl.utwente.groove.util.DefaultDispenser;
import nl.utwente.groove.util.Dispenser;
import nl.utwente.groove.util.Groove;
import nl.utwente.groove.util.collect.TreeHashSet;

/**
 * Stores graph information that can be reconstructed from the actual graph, for
 * faster access. Typically, the graph will have a graph cache as a
 * <tt>{@link java.lang.ref.Reference}</tt>.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class GraphCache<N extends Node,E extends GEdge<N>> {
    /**
     * Constructs a dynamic graph cache for a given graph.
     * @param graph the graph for which the cache is to be created.
     * @see #GraphCache(AGraph,boolean)
     */
    public GraphCache(AGraph<N,E> graph) {
        this(graph, true);
    }

    /**
     * Constructs a graph cache for a given graph, which can be either dynamic
     * or static. A dynamic cache listens to graph changes, and keeps its
     * internally cached sets in sync. Since the cache does so by registering
     * itself as a graph listener, this means there will be a hard reference to
     * the cache, and any reference won't be cleared, until the cache is removed
     * from the graph listeners! (This happens automatically in
     * {@link AGraph#setFixed()}). A static cache does not cache dynamic
     * information as long as the graph is not fixed.
     * @param graph the graph for which the cache is to be created.
     * @param dynamic switch to indicate if caching should be dynamic
     */
    public GraphCache(AGraph<N,E> graph, boolean dynamic) {
        this.graph = graph;
        this.dynamic = dynamic;
        if (Groove.GATHER_STATISTICS) {
            createCount++;
        }
    }

    /**
     * Switch to determine if the node-edge-set should be dynamically cached.
     */
    static public final boolean NODE_EDGE_MAP_DYNAMIC = true;
    /**
     * The total number of graph caches created.
     */
    static private int createCount;

    /**
     * Returns the total number of graph caches created.
     */
    public static int getCreateCount() {
        return createCount;
    }

    /**
     * Keeps the cached sets in sync with changes in the graph.
     */
    protected void addUpdate(N node) {
        addToNodeEdgeMap(this.nodeEdgeMap, node);
        addToNodeEdgeMap(this.nodeInEdgeMap, node);
        addToNodeEdgeMap(this.nodeOutEdgeMap, node);
        getNodeCounter().notifyUsed(node.getNumber());
    }

    /**
     * Keeps the cached sets in sync with changes in the graph.
     */
    protected void addUpdate(E edge) {
        addToLabelEdgeMap(this.labelEdgeMap, edge);
        addToNodeInEdgeMap(this.nodeInEdgeMap, edge);
        addToNodeOutEdgeMap(this.nodeOutEdgeMap, edge);
        addToNodeEdgeMap(this.nodeEdgeMap, edge);
    }

    /**
     * Keeps the cached sets in sync with changes in the graph.
     */
    protected void removeUpdate(N node) {
        removeFromNodeEdgeMap(this.nodeEdgeMap, node);
        removeFromNodeEdgeMap(this.nodeInEdgeMap, node);
        removeFromNodeEdgeMap(this.nodeOutEdgeMap, node);
    }

    /**
     * Keeps the cached sets in sync with changes in the graph.
     */
    protected void removeUpdate(E elem) {
        removeFromLabelEdgeMap(this.labelEdgeMap, elem);
        removeFromNodeEdgeMap(this.nodeEdgeMap, elem);
        removeFromNodeInEdgeMap(this.nodeInEdgeMap, elem);
        removeFromNodeOutEdgeMap(this.nodeOutEdgeMap, elem);
    }

    /**
     * Returns the label-to-edge mapping
     * @see #computeLabelEdgeMap()
     */
    public Map<Label,? extends @Nullable Set<? extends E>> getLabelEdgeMap() {
        var result = this.labelEdgeMap;
        if (result == null) {
            var newMaps = computeLabelEdgeMap();
            if (storeData()) {
                this.labelEdgeMap = newMaps;
            }
            result = newMaps;
        }
        return result;
    }

    /**
     * An array of label-to-edge mappings, indexed by arity of the edges - 1.
     * Initially set to <tt>null</tt>.
     */
    private @Nullable Map<Label,@Nullable Set<E>> labelEdgeMap;

    /**
     * Returns a mapping from nodes to incident edges in the underlying graph.
     * If there is a cached mapping, that is returned, otherwise it is computed
     * fresh, and, if the cache is dynamic (see {@link #isDynamic()} or the
     * graph is fixed (see {@link GGraph#isFixed()}) then the fresh mapping is
     * cached.
     */
    public Map<N,? extends @Nullable Set<? extends E>> getNodeInEdgeMap() {
        var result = this.nodeInEdgeMap;
        if (result == null) {
            result = computeNodeInEdgeMap();
            if (storeData()) {
                this.nodeInEdgeMap = result;
            }
        }
        return result;
    }

    /**
     * A node-to-incoming-edge mapping.
     */
    private @Nullable Map<N,@Nullable Set<E>> nodeInEdgeMap;

    /**
     * Returns a mapping from nodes to incident edges in the underlying graph.
     * If there is a cached mapping, that is returned, otherwise it is computed
     * fresh, and, if the cache is dynamic (see {@link #isDynamic()} or the
     * graph is fixed (see {@link GGraph#isFixed()}) then the fresh mapping is
     * cached.
     */
    public Map<N,? extends @Nullable Set<? extends E>> getNodeOutEdgeMap() {
        var result = this.nodeOutEdgeMap;
        if (result == null) {
            result = computeNodeOutEdgeMap();
            if (storeData()) {
                this.nodeOutEdgeMap = result;
            }
        }
        return result;
    }

    /**
     * A node-to-outgoing-edge mapping.
     */
    private @Nullable Map<N,@Nullable Set<E>> nodeOutEdgeMap;

    /**
     * Returns a mapping from nodes to incident edges in the underlying graph.
     * If there is a cached mapping, that is returned, otherwise it is computed
     * fresh, and, if the cache is dynamic (see {@link #isDynamic()} or the
     * graph is fixed (see {@link GGraph#isFixed()}) then the fresh mapping is
     * cached.
     */
    public Map<N,? extends @Nullable Set<? extends E>> getNodeEdgeMap() {
        var result = this.nodeEdgeMap;
        if (result == null) {
            result = computeNodeEdgeMap();
            if (storeData()) {
                this.nodeEdgeMap = result;
            }
        }
        return result;
    }

    /**
     * A node-to-incident-edge mapping.
     */
    private @Nullable Map<N,@Nullable Set<E>> nodeEdgeMap;

    /**
     * Computes and returns a mapping from labels to
     * sets of edges.
     */
    private Map<Label,@Nullable Set<E>> computeLabelEdgeMap() {
        Map<Label,@Nullable Set<E>> result = new HashMap<>();
        for (E edge : this.graph.edgeSet()) {
            addToLabelEdgeMap(result, edge);
        }
        return result;
    }

    /**
     * Computes and returns a fresh mapping from nodes to incoming
     * edge sets.
     */
    private Map<N,@Nullable Set<E>> computeNodeInEdgeMap() {
        Map<N,@Nullable Set<E>> result;
        if (this.nodeEdgeMap == null) {
            result = new HashMap<>();
            for (N node : this.graph.nodeSet()) {
                result.put(node, createEdgeSet(null));
            }
            for (E edge : this.graph.edgeSet()) {
                var es = result.get(edge.target());
                assert es != null;
                es.add(edge);
            }
        } else {
            // reuse the precomputed node-edge-map
            result = new HashMap<>(this.nodeEdgeMap);
            for (var resultEntry : result.entrySet()) {
                N node = resultEntry.getKey();
                Set<E> inEdges = createEdgeSet(null);
                var es = resultEntry.getValue();
                assert es != null;
                es.stream().filter(e -> e.target().equals(node)).forEach(e -> inEdges.add(e));
                resultEntry.setValue(inEdges);
            }
        }
        return result;
    }

    /**
     * Computes and returns a fresh mapping from nodes to incoming
     * edge sets.
     */
    private Map<N,@Nullable Set<E>> computeNodeOutEdgeMap() {
        Map<N,@Nullable Set<E>> result;
        if (this.nodeEdgeMap == null) {
            result = new HashMap<>();
            for (N node : this.graph.nodeSet()) {
                result.put(node, createEdgeSet(null));
            }
            for (E edge : this.graph.edgeSet()) {
                var es = result.get(edge.source());
                assert es != null;
                es.add(edge);
            }
        } else {
            // reuse the precomputed node-edge-map
            result = new HashMap<>(this.nodeEdgeMap);
            for (var resultEntry : result.entrySet()) {
                N node = resultEntry.getKey();
                var inEdges = createEdgeSet(null);
                var es = resultEntry.getValue();
                assert es != null;
                es.stream().filter(e -> e.source().equals(node)).forEach(e -> inEdges.add(e));
                resultEntry.setValue(inEdges);
            }
        }
        return result;
    }

    /**
     * Computes and returns a fresh mapping from nodes to incident
     * edge sets.
     */
    private Map<N,@Nullable Set<E>> computeNodeEdgeMap() {
        Map<N,@Nullable Set<E>> result = new HashMap<>();
        for (E edge : this.graph.edgeSet()) {
            addToNodeEdgeMap(result, edge);
        }
        // only do the nodes if there are (apparently) loose nodes
        if (result.size() != this.graph.nodeCount()) {
            for (N node : this.graph.nodeSet()) {
                addToNodeEdgeMap(result, node);
            }
        }
        return result;
    }

    /**
     * Indicates if the cache is dynamic.
     */
    private boolean isDynamic() {
        return this.dynamic;
    }

    /**
     * Adds an edge to a given label-to-edgeset map.
     * @param currentMap the array to be updated
     * @param edge the edge to be added
     */
    private void addToLabelEdgeMap(@Nullable Map<Label,@Nullable Set<E>> currentMap, E edge) {
        if (currentMap != null) {
            Set<E> labelEdgeSet = currentMap.get(edge.label());
            if (labelEdgeSet == null) {
                labelEdgeSet = createSmallEdgeSet();
                currentMap.put(edge.label(), labelEdgeSet);
            }
            labelEdgeSet.add(edge);
        }
    }

    /**
     * Removes an edge from a given label-to-edgeset map.
     * @param currentMap the array to be updated
     * @param edge the edge to be removed
     */
    private void removeFromLabelEdgeMap(@Nullable Map<Label,@Nullable Set<E>> currentMap, E edge) {
        if (currentMap != null) {
            Set<E> labelEdgeSet = currentMap.get(edge.label());
            if (labelEdgeSet != null) {
                labelEdgeSet.remove(edge);
            }
        }
    }

    /**
     * Adds an incoming edge to a given node-to-edgeset mapping.
     * @param currentMap the mapping to be updated
     * @param edge the edge to be added
     */
    private void addToNodeInEdgeMap(@Nullable Map<N,@Nullable Set<E>> currentMap, E edge) {
        if (currentMap != null) {
            addToNodeEdgeMap(currentMap, edge.target(), edge);
        }
    }

    /**
     * Adds an outgoing edge to a given node-to-edgeset mapping.
     * @param currentMap the mapping to be updated
     * @param edge the edge to be added
     */
    private void addToNodeOutEdgeMap(@Nullable Map<N,@Nullable Set<E>> currentMap, E edge) {
        if (currentMap != null) {
            addToNodeEdgeMap(currentMap, edge.source(), edge);
        }
    }

    /**
     * Adds an incident edge to a given node-to-edgeset mapping.
     * @param currentMap the mapping to be updated
     * @param edge the edge to be added
     */
    private void addToNodeEdgeMap(@Nullable Map<N,@Nullable Set<E>> currentMap, E edge) {
        if (currentMap != null) {
            addToNodeEdgeMap(currentMap, edge.source(), edge);
            addToNodeEdgeMap(currentMap, edge.target(), edge);
        }
    }

    /**
     * Adds an edge to a given node-to-edgeset mapping.
     * @param currentMap the mapping to be updated
     * @param node the key for the node
     * @param edge the edge to be added
     */
    private void addToNodeEdgeMap(Map<N,@Nullable Set<E>> currentMap, N node, E edge) {
        Set<E> edgeSet = currentMap.get(node);
        if (edgeSet == null) {
            currentMap.put(node, edgeSet = createSmallEdgeSet());
        }
        edgeSet.add(edge);
    }

    /**
     * Adds a node enty to a given node-to-edgeset mapping, unless there already
     * is one. The corresponding edge set will be initially empty.
     * @param currentMap the mapping to be updated
     * @param node the node to be added
     */
    private void addToNodeEdgeMap(@Nullable Map<N,@Nullable Set<E>> currentMap, N node) {
        if (currentMap != null) {
            Set<E> currentValue = currentMap.put(node, createSmallEdgeSet());
            if (currentValue != null) {
                currentMap.put(node, currentValue);
            }
        }
    }

    /**
     * Removes an incoming edge from a given node-to-edgeset mapping.
     * @param currentMap the mapping to be updated
     * @param edge the edge to be removed
     */
    private void removeFromNodeInEdgeMap(@Nullable Map<N,@Nullable Set<E>> currentMap, E edge) {
        if (currentMap != null) {
            Set<E> edgeSet = currentMap.get(edge.target());
            if (edgeSet != null) {
                edgeSet.remove(edge);
            }
        }
    }

    /**
     * Removes an outgoing edge from a given node-to-edgeset mapping.
     * @param currentMap the mapping to be updated
     * @param edge the edge to be removed
     */
    private void removeFromNodeOutEdgeMap(@Nullable Map<N,@Nullable Set<E>> currentMap, E edge) {
        if (currentMap != null) {
            Set<E> edgeSet = currentMap.get(edge.source());
            if (edgeSet != null) {
                edgeSet.remove(edge);
            }
        }
    }

    /**
     * Removes an edge from a given node-to-edgeset mapping.
     * @param currentMap the mapping to be updated
     * @param edge the edge to be removed
     */
    private void removeFromNodeEdgeMap(@Nullable Map<N,@Nullable Set<E>> currentMap, E edge) {
        if (currentMap != null) {
            removeFromNodeInEdgeMap(currentMap, edge);
            removeFromNodeOutEdgeMap(currentMap, edge);
        }
    }

    /**
     * Removes a node entry from a given node-to-edgeset mapping.
     * @param currentMap the mapping to be updated
     * @param node the node to be removed
     */
    private void removeFromNodeEdgeMap(@Nullable Map<N,@Nullable Set<E>> currentMap, N node) {
        if (currentMap != null) {
            currentMap.remove(node);
        }
    }

    /**
     * Factory method for a set of edges, initialised on a given set. The
     * initial set may be <code>null</code>, indicating that the edge set is
     * to be initially empty.
     */
    private Set<E> createEdgeSet(@Nullable Collection<E> set) {
        Set<E> result = createSmallEdgeSet();
        if (set != null) {
            result.addAll(set);
        }
        return result;
    }

    /**
     * Factory method for small sets of edges, e.g., the edges with a given
     * source node or label. The set may be a collection; i.e., an edge should
     * only be added if it is certain that it is not already in the set.
     */
    private Set<E> createSmallEdgeSet() {
        return new TreeHashSet<>();
    }

    /** Indicates if the precomputed data should be permanently stored. */
    private boolean storeData() {
        return isDynamic() || this.graph.isFixed();
    }

    /**
     * The graph on which the cache works.
     */
    protected final AGraph<N,E> graph;
    /**
     * Switch to indicate that the cache is dynamic.
     */
    private final boolean dynamic;

    /** Counter to ensure distinctness of fresh node identities. */
    protected Dispenser getNodeCounter() {
        var result = this.nodeCounter;
        if (result == null) {
            this.nodeCounter = result = new DefaultDispenser();
            // make sure all existing node numbers are accounted for
            for (Node node : getGraph().nodeSet()) {
                result.notifyUsed(node.getNumber());
            }
        }
        return result;
    }

    /** Counter for node numbers. */
    private @Nullable Dispenser nodeCounter;

    /**
     * Tests if a certificate strategy of the right strength has been instantiated.
     * @param strong the desired strength of the certifier
     */
    protected boolean hasCertifier(boolean strong) {
        return this.certificateStrategy != null && this.certificateStrategy.getStrength() == strong;
    }

    /**
     * Returns a certificate strategy for the current state of this graph. If no
     * strategy is currently cached, it is created by calling
     * {@link CertificateStrategy#newInstance(Graph, boolean)} on
     * {@link AGraph#getCertificateFactory()}. If the underlying graph is
     * fixed (see {@link GGraph#isFixed()}, the strategy is cached.
     */
    protected CertificateStrategy getCertifier(boolean strong) {
        CertificateStrategy result;
        if (hasCertifier(strong)) {
            result = this.certificateStrategy;
            assert result != null;
        } else {
            result = AGraph.getCertificateFactory().newInstance(getGraph(), strong);
            if (this.graph.isFixed()) {
                this.certificateStrategy = result;
            }
        }
        return result;
    }

    /**
     * Returns the graph for which the cache is maintained.
     */
    public AGraph<N,E> getGraph() {
        return this.graph;
    }

    /**
     * The certificate strategy set for the graph. Initially set to
     * <tt>null</tt>.
     */
    private @Nullable CertificateStrategy certificateStrategy;
}