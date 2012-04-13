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
 * $Id: GraphShapeCache.java,v 1.7 2008-01-30 09:32:52 iovka Exp $
 */
package groove.graph;

import groove.util.Groove;
import groove.util.TreeHashSet;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Stores graph information that can be reconstructed from the actual graph, for
 * faster access. Typically, the graph will have a graph cache as a
 * <tt>{@link java.lang.ref.Reference}</tt>.
 * @author Arend Rensink
 * @version $Revision$
 */
public class GraphShapeCache {
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
     * Constructs a dynamic graph cache for a given graph.
     * @param graph the graph for which the cache is to be created.
     * @see #GraphShapeCache(AbstractGraphShape,boolean)
     */
    public GraphShapeCache(AbstractGraphShape<?> graph) {
        this(graph, true);
    }

    /**
     * Constructs a graph cache for a given graph, which can be either dynamic
     * or static. A dynamic cache listens to graph changes, and keeps its
     * internally cached sets in sync. Since the cache does so by registering
     * itself as a graph listener, this means there will be a hard reference to
     * the cache, and any reference won't be cleared, until the cache is removed
     * from the graph listeners! (This happens automatically in
     * {@link AbstractGraph#setFixed()}). A static cache does not cache dynamic
     * information as long as the graph is not fixed.
     * @param graph the graph for which the cache is to be created.
     * @param dynamic switch to indicate if caching should bbe dynamic
     */
    public GraphShapeCache(AbstractGraphShape<?> graph, boolean dynamic) {
        this.graph = graph;
        this.dynamic = dynamic;
        if (Groove.GATHER_STATISTICS) {
            createCount++;
        }
    }

    /**
     * Keeps the cached sets in sync with changes in the graph.
     */
    protected void addUpdate(Node node) {
        addToNodeEdgeMap(this.nodeEdgeMap, node);
        addToNodeEdgeMap(this.nodeInEdgeMap, node);
        addToNodeEdgeMap(this.nodeOutEdgeMap, node);
    }

    /**
     * Keeps the cached sets in sync with changes in the graph.
     */
    protected void addUpdate(Edge edge) {
        addToLabelEdgeMap(this.labelEdgeMap, edge);
        addToNodeInEdgeMap(this.nodeInEdgeMap, edge);
        addToNodeOutEdgeMap(this.nodeOutEdgeMap, edge);
        addToNodeEdgeMap(this.nodeEdgeMap, edge);
    }

    /**
     * Keeps the cached sets in sync with changes in the graph.
     */
    protected void removeUpdate(Node node) {
        removeFromNodeEdgeMap(this.nodeEdgeMap, node);
        removeFromNodeEdgeMap(this.nodeInEdgeMap, node);
        removeFromNodeEdgeMap(this.nodeOutEdgeMap, node);
    }

    /**
     * Keeps the cached sets in sync with changes in the graph.
     */
    protected void removeUpdate(Edge elem) {
        removeFromLabelEdgeMap(this.labelEdgeMap, elem);
        removeFromNodeEdgeMap(this.nodeEdgeMap, elem);
        removeFromNodeInEdgeMap(this.nodeInEdgeMap, elem);
        removeFromNodeOutEdgeMap(this.nodeOutEdgeMap, elem);
    }

    /**
     * Returns the label-to-edge mapping
     * @see #computeLabelEdgeMap()
     */
    public Map<Label,? extends Set<? extends Edge>> getLabelEdgeMap() {
        Map<Label,? extends Set<? extends Edge>> result = this.labelEdgeMap;
        if (result == null) {
            Map<Label,Set<Edge>> newMaps = computeLabelEdgeMap();
            if (storeData()) {
                this.labelEdgeMap = newMaps;
            }
            result = newMaps;
        }
        return result;
    }

    /**
     * Returns a mapping from nodes to incident edges in the underlying graph.
     * If there is a cached mapping, that is returned, otherwise it is computed
     * fresh, and, if the cache is dynamic (see {@link #isDynamic()} or the
     * graph is fixed (see {@link Graph#isFixed()}) then the fresh mapping is
     * cached.
     */
    public Map<Node,? extends Set<? extends Edge>> getNodeInEdgeMap() {
        Map<Node,Set<Edge>> result = this.nodeInEdgeMap;
        if (result == null) {
            result = computeNodeInEdgeMap();
            if (storeData()) {
                this.nodeInEdgeMap = result;
            }
        }
        return result;
    }

    /**
     * Returns a mapping from nodes to incident edges in the underlying graph.
     * If there is a cached mapping, that is returned, otherwise it is computed
     * fresh, and, if the cache is dynamic (see {@link #isDynamic()} or the
     * graph is fixed (see {@link Graph#isFixed()}) then the fresh mapping is
     * cached.
     */
    public Map<Node,? extends Set<? extends Edge>> getNodeOutEdgeMap() {
        Map<Node,Set<Edge>> result = this.nodeOutEdgeMap;
        if (result == null) {
            result = computeNodeOutEdgeMap();
            if (storeData()) {
                this.nodeOutEdgeMap = result;
            }
        }
        return result;
    }

    /**
     * Returns a mapping from nodes to incident edges in the underlying graph.
     * If there is a cached mapping, that is returned, otherwise it is computed
     * fresh, and, if the cache is dynamic (see {@link #isDynamic()} or the
     * graph is fixed (see {@link Graph#isFixed()}) then the fresh mapping is
     * cached.
     */
    public Map<Node,? extends Set<? extends Edge>> getNodeEdgeMap() {
        Map<Node,Set<Edge>> result = this.nodeEdgeMap;
        if (result == null) {
            result = computeNodeEdgeMap();
            if (storeData()) {
                this.nodeEdgeMap = result;
            }
        }
        return result;
    }

    /**
     * Computes and returns a mapping from labels to
     * sets of edges.
     */
    private Map<Label,Set<Edge>> computeLabelEdgeMap() {
        Map<Label,Set<Edge>> result = new HashMap<Label,Set<Edge>>();
        for (Edge edge : this.graph.edgeSet()) {
            addToLabelEdgeMap(result, edge);
        }
        return result;
    }

    /**
     * Computes and returns a fresh mapping from nodes to incoming
     * edge sets.
     */
    private Map<Node,Set<Edge>> computeNodeInEdgeMap() {
        Map<Node,Set<Edge>> result;
        if (this.nodeEdgeMap == null) {
            result = new HashMap<Node,Set<Edge>>();
            for (Node node : this.graph.nodeSet()) {
                result.put(node, createEdgeSet(null));
            }
            for (Edge edge : this.graph.edgeSet()) {
                result.get(edge.target()).add(edge);
            }
        } else {
            // reuse the precomputed node-edge-map
            result = new HashMap<Node,Set<Edge>>(this.nodeEdgeMap);
            for (Map.Entry<Node,Set<Edge>> resultEntry : result.entrySet()) {
                Node node = resultEntry.getKey();
                Set<Edge> inEdges = createEdgeSet(null);
                for (Edge edge : resultEntry.getValue()) {
                    if (edge.target().equals(node)) {
                        inEdges.add(edge);
                    }
                }
                resultEntry.setValue(inEdges);
            }
        }
        return result;
    }

    /**
     * Computes and returns a fresh mapping from nodes to incoming
     * edge sets.
     */
    private Map<Node,Set<Edge>> computeNodeOutEdgeMap() {
        Map<Node,Set<Edge>> result;
        if (this.nodeEdgeMap == null) {
            result = new HashMap<Node,Set<Edge>>();
            for (Node node : this.graph.nodeSet()) {
                result.put(node, createEdgeSet(null));
            }
            for (Edge edge : this.graph.edgeSet()) {
                result.get(edge.source()).add(edge);
            }
        } else {
            // reuse the precomputed node-edge-map
            result = new HashMap<Node,Set<Edge>>(this.nodeEdgeMap);
            for (Map.Entry<Node,Set<Edge>> resultEntry : result.entrySet()) {
                Node node = resultEntry.getKey();
                Set<Edge> inEdges = createEdgeSet(null);
                for (Edge edge : resultEntry.getValue()) {
                    if (edge.source().equals(node)) {
                        inEdges.add(edge);
                    }
                }
                resultEntry.setValue(inEdges);
            }
        }
        return result;
    }

    /**
     * Computes and returns a fresh mapping from nodes to incident
     * edge sets.
     */
    private Map<Node,Set<Edge>> computeNodeEdgeMap() {
        Map<Node,Set<Edge>> result = new HashMap<Node,Set<Edge>>();
        for (Edge edge : this.graph.edgeSet()) {
            addToNodeEdgeMap(result, edge);
        }
        // only do the nodes if there are (apparently) loose nodes
        if (result.size() != this.graph.nodeCount()) {
            for (Node node : this.graph.nodeSet()) {
                addToNodeEdgeMap(result, node);
            }
        }
        return result;
    }

    /**
     * Returns the graph for which the cache is maintained.
     */
    public AbstractGraphShape<?> getGraph() {
        return this.graph;
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
    private void addToLabelEdgeMap(Map<Label,Set<Edge>> currentMap, Edge edge) {
        if (currentMap != null) {
            Set<Edge> labelEdgeSet = currentMap.get(edge.label());
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
    private void removeFromLabelEdgeMap(Map<Label,Set<Edge>> currentMap,
            Edge edge) {
        if (currentMap != null) {
            Set<Edge> labelEdgeSet = currentMap.get(edge.label());
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
    private void addToNodeInEdgeMap(Map<Node,Set<Edge>> currentMap, Edge edge) {
        if (currentMap != null) {
            addToNodeEdgeMap(currentMap, edge.target(), edge);
        }
    }

    /**
     * Adds an outgoing edge to a given node-to-edgeset mapping.
     * @param currentMap the mapping to be updated
     * @param edge the edge to be added
     */
    private void addToNodeOutEdgeMap(Map<Node,Set<Edge>> currentMap, Edge edge) {
        if (currentMap != null) {
            addToNodeEdgeMap(currentMap, edge.source(), edge);
        }
    }

    /**
     * Adds an incident edge to a given node-to-edgeset mapping.
     * @param currentMap the mapping to be updated
     * @param edge the edge to be added
     */
    private void addToNodeEdgeMap(Map<Node,Set<Edge>> currentMap, Edge edge) {
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
    private void addToNodeEdgeMap(Map<Node,Set<Edge>> currentMap, Node node,
            Edge edge) {
        Set<Edge> edgeSet = currentMap.get(node);
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
    private void addToNodeEdgeMap(Map<Node,Set<Edge>> currentMap, Node node) {
        if (currentMap != null) {
            Set<Edge> currentValue = currentMap.put(node, createSmallEdgeSet());
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
    private void removeFromNodeInEdgeMap(Map<Node,Set<Edge>> currentMap,
            Edge edge) {
        if (currentMap != null) {
            Set<Edge> edgeSet = currentMap.get(edge.target());
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
    private void removeFromNodeOutEdgeMap(Map<Node,Set<Edge>> currentMap,
            Edge edge) {
        if (currentMap != null) {
            Set<Edge> edgeSet = currentMap.get(edge.source());
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
    private void removeFromNodeEdgeMap(Map<Node,Set<Edge>> currentMap, Edge edge) {
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
    private void removeFromNodeEdgeMap(Map<Node,Set<Edge>> currentMap, Node node) {
        if (currentMap != null) {
            currentMap.remove(node);
        }
    }

    /**
     * Factory method for a set of edges, initialised on a given set. The
     * initial set may be <code>null</code>, indicating that the edge set is
     * to be initially empty.
     */
    private Set<Edge> createEdgeSet(Collection<Edge> set) {
        Set<Edge> result = createSmallEdgeSet();
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
    private Set<Edge> createSmallEdgeSet() {
        return new TreeHashSet<Edge>();
    }

    /** Indicates if the precomuted data should be permanently stored. */
    private boolean storeData() {
        return isDynamic() || this.graph.isFixed();
    }

    /**
     * The graph on which the cache works.
     */
    protected final AbstractGraphShape<?> graph;
    /**
     * Switch to indicate that the cache is dynamic.
     */
    private final boolean dynamic;
    /**
     * An array of label-to-edge mappings, indexed by arity of the edges - 1.
     * Initially set to <tt>null</tt>.
     */
    private Map<Label,Set<Edge>> labelEdgeMap;
    /**
     * A node-to-incoming-edge mapping.
     */
    private Map<Node,Set<Edge>> nodeInEdgeMap;
    /**
     * A node-to-outgoing-edge mapping.
     */
    private Map<Node,Set<Edge>> nodeOutEdgeMap;
    /**
     * A node-to-incident-edge mapping.
     */
    private Map<Node,Set<Edge>> nodeEdgeMap;
}