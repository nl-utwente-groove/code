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
public class GraphShapeCache implements GraphShapeListener {
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
        graph.addGraphListener(this);
        if (Groove.GATHER_STATISTICS) {
            createCount++;
        }
    }

    /**
     * Keeps the cached sets in sync with changes in the graph.
     */
    public void addUpdate(GraphShape graph, Node node) {
        if (isNodeEdgeMapSet()) {
            addToNodeEdgeMap(this.nodeEdgeMap, node);
        }
    }

    /**
     * Keeps the cached sets in sync with changes in the graph.
     */
    public void addUpdate(GraphShape graph, Edge edge) {
        if (isLabelEdgeMapsSet()) {
            addToLabelEdgeMap(this.labelEdgeMap, edge);
        }
        if (isNodeEdgeMapSet()) {
            addToNodeEdgeMap(this.nodeEdgeMap, edge);
        }
    }

    /**
     * Keeps the cached sets in sync with changes in the graph.
     */
    public void removeUpdate(GraphShape graph, Node node) {
        if (isNodeEdgeMapSet()) {
            removeFromNodeEdgeMap(this.nodeEdgeMap, node);
        }
    }

    /**
     * Keeps the cached sets in sync with changes in the graph.
     */
    public void removeUpdate(GraphShape graph, Edge elem) {
        if (isLabelEdgeMapsSet()) {
            removeFromLabelEdgeMap(this.labelEdgeMap, elem);
        }
        if (isNodeEdgeMapSet()) {
            removeFromNodeEdgeMap(this.nodeEdgeMap, elem);
        }
    }

    /**
     * Signals if the label-to-edge map (to be returned by
     * {@link #getLabelEdgeMap()} is currently set, or if it yet has to be
     * computed for this cache.
     */
    boolean isLabelEdgeMapsSet() {
        return this.labelEdgeMap != null;
    }

    /**
     * Signals if the node-to-edge map (to be returned by
     * {@link #getNodeEdgeMap()} is currently set, or if it yet has to be
     * computed for this cache.
     */
    boolean isNodeEdgeMapSet() {
        return this.nodeEdgeMap != null;
    }

    /**
     * Returns the label-to-edge mapping
     * @see #computeLabelEdgeMap()
     */
    protected Map<Label,? extends Set<? extends Edge>> getLabelEdgeMap() {
        Map<Label,? extends Set<? extends Edge>> result = this.labelEdgeMap;
        if (result == null) {
            Map<Label,Set<Edge>> newMaps = computeLabelEdgeMap();
            if (this.dynamic || this.graph.isFixed()) {
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
    protected Map<Node,? extends Set<? extends Edge>> getNodeEdgeMap() {
        Map<Node,Set<Edge>> result;
        if (isNodeEdgeMapSet()) {
            result = this.nodeEdgeMap;
        } else {
            result = computeNodeEdgeMap();
            if ((NODE_EDGE_MAP_DYNAMIC && this.dynamic) || this.graph.isFixed()) {
                this.nodeEdgeMap = result;
            }
        }
        return result;
    }

    /**
     * Computes and returns a mapping from labels to
     * sets of edges.
     */
    protected Map<Label,Set<Edge>> computeLabelEdgeMap() {
        Map<Label,Set<Edge>> result = new HashMap<Label,Set<Edge>>();
        for (Edge edge : this.graph.edgeSet()) {
            addToLabelEdgeMap(result, edge);
        }
        return result;
    }

    /**
     * Computes and returns a fresh mapping from nodes to arrays of incident
     * edge sets. That is, the images of the map are of type <tt>Set[]</tt>
     * where the array index is the position in the edge that the node occupies.
     */
    protected Map<Node,Set<Edge>> computeNodeEdgeMap() {
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
    protected boolean isDynamic() {
        return this.dynamic;
    }

    /**
     * Adds an edge to a given label-to-edgeset map.
     * @param currentMap the array to be updated
     * @param edge the edge to be added
     */
    final void addToLabelEdgeMap(Map<Label,Set<Edge>> currentMap, Edge edge) {
        Set<Edge> labelEdgeSet = currentMap.get(edge.label());
        if (labelEdgeSet == null) {
            labelEdgeSet = createSmallEdgeSet();
            currentMap.put(edge.label(), labelEdgeSet);
        }
        labelEdgeSet.add(edge);
    }

    /**
     * Removes an edge from a given label-to-edgeset map.
     * @param currentMap the array to be updated
     * @param edge the edge to be removed
     */
    final void removeFromLabelEdgeMap(Map<Label,Set<Edge>> currentMap, Edge edge) {
        Set<Edge> labelEdgeSet = currentMap.get(edge.label());
        if (labelEdgeSet != null) {
            labelEdgeSet.remove(edge);
        }
    }

    /**
     * Adds an edge to a given node-to-edgeset mapping.
     * @param currentMap the mapping to be updated
     * @param edge the edge to be added
     * @return <code>true</code> if the edge was indeed added, i.e., was not
     *         yet there in the first place
     */
    final boolean addToNodeEdgeMap(Map<Node,Set<Edge>> currentMap, Edge edge) {
        boolean result = false;
        for (int i = 0; i < edge.endCount(); i++) {
            Node end = edge.end(i);
            Set<Edge> edgeSet = currentMap.get(end);
            if (edgeSet == null) {
                currentMap.put(end, edgeSet = createSmallEdgeSet());
            }
            result |= edgeSet.add(edge);
        }
        return result;
    }

    /**
     * Adds a node enty to a given node-to-edgeset mapping, unless there already
     * is one. The corresponding edge set will be initially empty.
     * @param currentMap the mapping to be updated
     * @param node the node to be added
     * @return <code>true</code> if the node was indeed added, i.e., was not
     *         yet there in the first place
     */
    final boolean addToNodeEdgeMap(Map<Node,Set<Edge>> currentMap, Node node) {
        Set<Edge> currentValue = currentMap.put(node, createSmallEdgeSet());
        if (currentValue != null) {
            currentMap.put(node, currentValue);
            return false;
        } else {
            return true;
        }
    }

    /**
     * Removes an edge from a given node-to-edgeset mapping.
     * @param currentMap the mapping to be updated
     * @param edge the edge to be removed
     */
    void removeFromNodeEdgeMap(Map<Node,Set<Edge>> currentMap, Edge edge) {
        for (int i = 0; i < edge.endCount(); i++) {
            Node end = edge.end(i);
            Set<Edge> edgeSet = currentMap.get(end);
            if (edgeSet != null) {
                edgeSet.remove(edge);
            }
        }
    }

    /**
     * Removes a node entry from a given node-to-edgeset mapping.
     * @param currentMap the mapping to be updated
     * @param node the node to be removed
     * @return <code>true</code> if the node was there in the first place
     */
    boolean removeFromNodeEdgeMap(Map<Node,Set<Edge>> currentMap, Node node) {
        return currentMap.remove(node) != null;
    }

    /**
     * Factory method for small sets of edges, e.g., the edges with a given
     * source node or label. The set may be a collection; i.e., an edge should
     * only be added if it is certain that it is not already in the set.
     */
    protected Set<Edge> createSmallEdgeSet() {
        return new TreeHashSet<Edge>();
    }

    /**
     * Factory method for a set of nodes, initialised on a given set. The
     * initial set may be <code>null</code>, indicating that the node set is
     * to be initially empty.
     */
    protected Set<Node> createNodeSet(Collection<Node> set) {
        if (set == null) {
            return new NodeSet();
        } else {
            return new NodeSet(set);
        }
    }

    /**
     * Factory method for a set of edges, initialised on a given set. The
     * initial set may be <code>null</code>, indicating that the edge set is
     * to be initially empty.
     */
    protected Set<Edge> createEdgeSet(Collection<Edge> set) {
        Set<Edge> result = new TreeHashSet<Edge>();
        if (set != null) {
            result.addAll(set);
        }
        return result;
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
     * A node-to-outgoing-edge mapping.
     */
    private Map<Node,Set<Edge>> nodeEdgeMap;
}