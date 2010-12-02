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
 * $Id: DeltaGraphCache.java,v 1.10 2008-01-30 09:32:50 iovka Exp $
 */
package groove.graph;

import groove.util.CollectionOfCollections;
import groove.util.DeltaSet;
import groove.util.StackedSet;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author Arend Rensink
 * @version $Revision$
 */
public class DeltaGraphCache extends GraphCache {
    /**
     * Constructs a cache for a given graph.
     */
    protected DeltaGraphCache(DeltaGraph graph) {
        super(graph, false);
    }

    /**
     * Returns the cached node set. If no set is cached, it is reconstructed
     * from the underlying graph.
     */
    public Set<Node> getNodeSet() {
        if (this.nodeSet == null) {
            this.nodeSet = computeNodeSet();
        }
        return this.nodeSet;
    }

    /**
     * Returns the number of nodes in the graph. This number is stored
     * explicitly in the cache, so it can be used even if the node set itself is
     * invalidated.
     */
    public int getNodeCount() {
        assert this.nodeCount < 0 || this.nodeCount == getNodeSet().size() : "Node count "
            + this.nodeCount
            + " should equal number of elements in node set "
            + getNodeSet();
        int result = this.nodeCount;
        if (result < 0) {
            result = getNodeSet().size();
            if (isFixed()) {
                this.nodeCount = result;
            }
        }
        assert result == getNodeSet().size() : "Node count " + result
            + " should equal number of elements in node set " + getNodeSet();
        return result;
    }

    /**
     * Returns the cached edge set. If no set is cached, it is reconstructed
     * from the underlying graph.
     */
    public Set<Edge> getEdgeSet() {
        if (this.edgeSet == null) {
            this.edgeSet = computeEdgeSet();
        }
        return this.edgeSet;
    }

    /**
     * Returns the number of edges in the graph. This number is stored
     * explicitly in the cache, so it can be used even if the edge set itself is
     * invalidated.
     */
    public int getEdgeCount() {
        int result = this.edgeCount;
        if (result < 0) {
            result = getEdgeSet().size();
            if (isFixed()) {
                this.edgeCount = result;
            }
        }
        assert result == getEdgeSet().size() : "Edge count " + result
            + " should equal number of elements in edge set " + getEdgeSet();
        return result;
    }

    /**
     * Returns the delta store computed for the cache. This is the difference
     * between the underlying graph of this cache (as returned by
     * {@link #getGraph()} and the cache basis (as returned by
     * {@link #getCacheBasis()}.
     */
    public DeltaStore getCacheDelta() {
        if (!isCacheInit()) {
            initCache();
        }
        return this.cacheDelta;
    }

    /** Returns the basis of the underlying graph. */
    public AbstractGraph<? extends GraphCache> getCacheBasis() {
        if (!isCacheInit()) {
            initCache();
        }
        return this.cacheBasis;
    }

    /**
     * Indicates if the delta of this cache has been computed. Callback method
     * from {@link #getCacheDelta()}.
     */
    protected boolean isCacheInit() {
        return this.cacheDelta != null;
    }

    /**
     * Initializes the delta store the cache. This is done by taking the sets
     * from the basis, and applying the delta if the graph is fixed. Callback
     * method from {@link #getCacheDelta()}.
     */
    protected void initCache() {
        this.frozen = getGraph().isFrozen();
        if (this.frozen) {
            initFrozenCache();
        } else if (getGraph().isFixed()) {
            initFixedCache();
        } else {
            initModifiableCache();
        }
    }

    /**
     * Computes the cache delta in case the underlying graph is modifiable,
     * i.e., not fixed.
     * @require <code>!isFixed()</code>
     */
    protected void initModifiableCache() {
        this.cacheBasis = getGraph().getBasis();
        this.cacheDelta = new DeltaStore();
    }

    /**
     * Computes the cache delta in case the underlying graph is fixed but not
     * frozen.
     * @require <code>isFixed() && !isFrozen()</code>
     */
    protected void initFixedCache() {
        AbstractGraph<?> graphBasis = getGraph().getBasis();
        DeltaGraphCache basisCache = getDeltaCache(graphBasis);
        if (basisCache == null || basisCache.suggestSetFrozen()) {
            this.cacheBasis = graphBasis;
            this.cacheDelta = new DeltaStore();
        } else {
            this.cacheBasis = basisCache.getCacheBasis();
            this.cacheDelta = new DeltaStore(basisCache.getCacheDelta());
        }
        getGraph().applyDelta(this.cacheDelta);
    }

    /**
     * Computes the cache delta in case the underlying graph is frozen.
     * @require <code>isFrozen()</code>
     */
    protected void initFrozenCache() {
        this.cacheBasis = null;
        this.cacheDelta = new DeltaStore();
        getGraph().applyDelta(this.cacheDelta);
    }

    /**
     * Computes the node set of the underlying graph, by applying the cache
     * delta to the basis (if the basis exists) or to an empty set.
     * @see #getCacheBasis()
     * @see #getCacheDelta()
     */
    protected Set<Node> computeNodeSet() {
        Set<Node> result;
        Graph basis = getCacheBasis();
        DeltaStore delta = getCacheDelta();
        if (basis == null) {
            result = delta.getAddedNodeSet();
        } else {
            result = delta.newStackedNodeSet(getNodeSet(basis));
        }
        return result;
    }

    /**
     * Computes the edge set of the underlying graph, by applying the cache
     * delta to the basis (if the basis exists) or to an empty set.
     * @see #getCacheBasis()
     * @see #getCacheDelta()
     */
    protected Set<Edge> computeEdgeSet() {
        Set<Edge> result;
        DeltaStore delta = getCacheDelta();
        Graph basis = getCacheBasis();
        if (basis == null) {
            result = delta.getAddedEdgeSet();
        } else {
            result = delta.newStackedEdgeSet(getEdgeSet(basis));
        }
        return result;
    }

    /**
     * If the label-to-edge map of the basis is currently set, constructs the
     * result by cloning that one and performing the delta upon it. Otherwise,
     * delegates to super.
     */
    @Override
    protected Map<Label,Set<Edge>> computeLabelEdgeMap() {
        // the cache basis
        AbstractGraph<?> basis = getCacheBasis();
        // otherwise, we can use the cache delta
        DeltaApplier delta = getCacheDelta();
        @SuppressWarnings({"rawtypes", "unchecked"})
        final Map<Label,Set<Edge>> basisMap = (Map) basis.getLabelEdgeMap();
        final Map<Label,Set<Edge>> result = new HashMap<Label,Set<Edge>>(basisMap);
        DeltaTarget target = new DeltaTarget() {
            public boolean addEdge(Edge elem) {
                return addToLabelEdgeMap(result, elem, basisMap);
            }

            public boolean addNode(Node elem) {
                throw new UnsupportedOperationException(
                    "No node manipulation through this delta target");
            }

            public boolean removeEdge(Edge elem) {
                return removeFromLabelEdgeMap(result, elem, basisMap);
            }

            public boolean removeNode(Node elem) {
                throw new UnsupportedOperationException(
                    "No node manipulation through this delta target");
            }
        };
        delta.applyDelta(target, DeltaApplier.EDGES_ONLY);
        assert getEdgeSet().containsAll(
            new CollectionOfCollections<Edge>(result.values())) : "Edges not correct: "
            + getEdgeSet() + " does not contains all of " + result.values();
        return result;
    }

    /**
     * If the node-to-edge map of the basis is currently set, constructs the
     * result by cloning that one and performing the delta upon it. Otherwise,
     * delegates to super.
     */
    @Override
    protected Map<Node,Set<Edge>> computeNodeEdgeMap() {
        // the cache basis
        AbstractGraph<?> basis = getCacheBasis();
        if (basis == null) {
            // if the cache basis is not an abstract graph,
            // we cannot use the delta to compute the label/edge maps array
            // so we have to compute it the hard way
            return super.computeNodeEdgeMap();
        } else {
            @SuppressWarnings({"unchecked", "rawtypes"})
            Map<Node,Set<Edge>> basisMap = (Map) basis.nodeEdgeMap();
            Map<Node,Set<Edge>> result = new HashMap<Node,Set<Edge>>(basisMap);
            DeltaTarget target = createNodeEdgeMapTarget(basisMap, result);
            getCacheDelta().applyDelta(target);
            assert getEdgeSet().containsAll(
                new CollectionOfCollections<Edge>(result.values())) : "Map not correct: \nEdges "
                + getEdgeSet()
                + " not compatible with \nnode/edge map"
                + result;
            return result;
        }
    }

    /**
     * Creates a {@link DeltaTarget} that creates a node/edges map on the basis
     * of an existing map.
     */
    private DeltaTarget createNodeEdgeMapTarget(
            final Map<Node,? extends Set<? extends Edge>> basisMap,
            final Map<Node,Set<Edge>> result) {
        return new DeltaTarget() {
            // public void addElement(Element elem) {
            // if (elem instanceof Node) {
            // addToNodeEdgeMap(result, (Node) elem);
            // } else {
            // addToNodeEdgeMap(result, (Edge) elem, basisMap);
            // }
            // }
            //
            // public void removeElement(Element elem) {
            // if (elem instanceof Node) {
            // removeFromNodeEdgeMap(result, (Node) elem);
            // } else {
            // removeFromNodeEdgeMap(result, (Edge) elem, basisMap);
            // }
            // }
            //
            public boolean addEdge(Edge elem) {
                return addToNodeEdgeMap(result, elem, basisMap);
            }

            public boolean addNode(Node elem) {
                return addToNodeEdgeMap(result, elem);
            }

            public boolean removeEdge(Edge elem) {
                return removeFromNodeEdgeMap(result, elem, basisMap);
            }

            public boolean removeNode(Node elem) {
                return removeFromNodeEdgeMap(result, elem);
            }
        };
    }

    /**
     * Callback factory method to create a {@link DeltaSet} on top of the basis
     * set. It is required that the basis node set is not <code>null</code>.
     */
    protected <T> DeltaSet<T> createDeltaSet(Set<T> basis, Set<T> added,
            Set<T> removed) {
        return new DeltaSet<T>(basis, added, removed);
    }

    /**
     * Callback factory method to create a {@link StackedSet} on top of a basis
     * set, with predefined added and removed sets. It is required that none of
     * the sets is <code>null</code>.
     */
    protected <T> StackedSet<T> createStackedSet(Set<? extends T> basis,
            Set<T> added, Set<T> removed) {
        assert basis.containsAll(removed) : "Basis " + basis
            + " does not contain removed elements " + removed;
        return new StackedSet<T>(basis, added, removed);
    }

    /**
     * Adds an edge to a given label-to-edgeset map, cloning the
     * relevant entry if necessary.
     * @param labelEdgeMap the map to be updated
     * @param edge the edge to be added
     * @return <code>true</code> if the edge was indeed added, i.e., was not
     *         yet there in the first place
     */
    boolean addToLabelEdgeMap(Map<Label,Set<Edge>> labelEdgeMap, Edge edge,
            Map<Label,Set<Edge>> basisMap) {
        Set<Edge> labelEdgeSet = labelEdgeMap.get(edge.label());
        if (labelEdgeSet == null) {
            labelEdgeSet = createEdgeSet(null);
            labelEdgeMap.put(edge.label(), labelEdgeSet);
        } else if (labelEdgeSet == basisMap.get(edge.label())) {
            labelEdgeSet = createEdgeSet(labelEdgeSet);
            labelEdgeMap.put(edge.label(), labelEdgeSet);
        }
        return labelEdgeSet.add(edge);
    }

    /**
     * Removes an edge from a given label-to-edgeset map.
     * 
     * @param labelEdgeMap the map to be updated
     * @param edge the edge to be removed
     * @return <code>true</code> if the edge was actually there in the first
     *         place
     */
    boolean removeFromLabelEdgeMap(Map<Label,Set<Edge>> labelEdgeMap,
            Edge edge, Map<Label,Set<Edge>> basisMap) {
        Set<Edge> labelEdgeSet = labelEdgeMap.get(edge.label());
        if (labelEdgeSet == basisMap.get(edge.label())) {
            labelEdgeSet = createEdgeSet(labelEdgeSet);
            labelEdgeMap.put(edge.label(), labelEdgeSet);
        }
        if (labelEdgeSet != null) {
            return labelEdgeSet.remove(edge);
        } else {
            return false;
        }
    }

    /**
     * Adds an edge to a given node-to-edgeset mapping, cloning the relevant
     * entry if necessary.
     * 
     * @param newMap the mapping to be updated
     * @param edge the edge to be added
     * @return <code>true</code> if the edge was indeed added, i.e., was not
     *         yet ther in the first place
     */
    boolean addToNodeEdgeMap(Map<Node,Set<Edge>> newMap, Edge edge,
            Map<Node,? extends Set<? extends Edge>> basisMap) {
        assert basisMap != null;
        boolean result = putEdge(newMap, basisMap, edge.source(), edge);
        result |= putEdge(newMap, basisMap, edge.target(), edge);
        return result;
    }

    /** Adds an edge to the edge set associated with a given node.
     * Creates the edge set if necessary.
     */
    private boolean putEdge(Map<Node,Set<Edge>> newMap,
            Map<Node,? extends Set<? extends Edge>> basisMap, Node end,
            Edge edge) {
        Set<Edge> edgeSet = newMap.get(end);
        if (edgeSet == null) {
            newMap.put(end, edgeSet = createEdgeSet(null));
        } else if (edgeSet == basisMap.get(end)) {
            newMap.put(end, edgeSet = createEdgeSet(edgeSet));
        }
        return edgeSet.add(edge);
    }

    /**
     * Removes an edge from a given node-to-edgeset mapping, cloning the
     * relevant entry if necessary.
     * 
     * @param newMap the mapping to be updated
     * @param edge the edge to be removed
     * @return <code>true</code> if the edge was indeed removed
     */
    boolean removeFromNodeEdgeMap(Map<Node,Set<Edge>> newMap, Edge edge,
            Map<Node,? extends Set<? extends Edge>> basisMap) {
        boolean result =
            removeFromNodeEdgeMap(basisMap, newMap, edge, edge.source());
        result |= removeFromNodeEdgeMap(basisMap, newMap, edge, edge.target());
        return result;
    }

    /**
     * Removes an edge from the image of a given node, cloning
     * the entry if necessary.
     */
    private boolean removeFromNodeEdgeMap(
            Map<Node,? extends Set<? extends Edge>> basisMap,
            Map<Node,Set<Edge>> newMap, Edge edge, Node end) {
        boolean result = false;
        Set<Edge> edgeSet = newMap.get(end);
        if (edgeSet != null) {
            if (edgeSet == basisMap.get(end)) {
                newMap.put(end, edgeSet = createEdgeSet(edgeSet));
            }
            result = edgeSet.remove(edge);
        }
        return result;
    }

    /**
     * Convenience method for <code>(DeltaGraph) getGraph()</code>.
     */
    @Override
    public DeltaGraph getGraph() {
        return (DeltaGraph) this.graph;
    }

    /**
     * Indicates if the graph is fixed. This implementation defers the question
     * to the underlying graph.
     */
    protected boolean isFixed() {
        return getGraph().isFixed();
    }

    /**
     * Indicates if the underlying graph is a checkpoint for this cache.
     */
    protected boolean isFrozen() {
        if (!isCacheInit()) {
            initCache();
        }
        return this.frozen;
    }

    /**
     * Signals that the underlying graph has been fixed. This gives the cache
     * the change to rearrange things. Callback method from
     * {@link DeltaGraph#setFixed()}.
     */
    protected void notifySetFixed() {
        if (this.nodeSet instanceof DeltaSet<?>) {
            this.nodeSet = ((DeltaSet<Node>) this.nodeSet).lower();
            this.edgeSet = ((DeltaSet<Edge>) this.edgeSet).lower();
        }
    }

    /**
     * Signals that the underlying graph has been frozen. This means the cache
     * has to be reset. Callback method from {@link DeltaGraph#setFrozen()}.
     */
    protected void notifySetFrozen() {
        // make sure the node and edge counts are locally stored
        // and reset the node and edge sets, so they don't stack arbitrarily
        // deep
        if (this.nodeSet != null) {
            getNodeCount();
        }
        if (this.edgeSet != null) {
            getEdgeCount();
        }
        resetCache();
    }

    /**
     * Resets the cached delta. Callback method from {@link #notifySetFrozen()}.
     */
    protected void resetCache() {
        this.nodeSet = null;
        this.edgeSet = null;
        this.cacheDelta = null;
        this.cacheBasis = null;
        this.freezeCondition = null;
    }

    /**
     * Suggests that it might be worth considering checkpointing the underlying
     * graph. The suggestion is followed if {@link #getFreezeCondition()}
     * supports it; if so, {@link #notifySetFrozen()} is called to actually
     * checkpoint the graph. The return value indicates if the graph was indeed
     * ckheckpointed.
     */
    protected boolean suggestSetFrozen() {
        if (isFrozen()) {
            return true;
        } else if (getFreezeCondition().isSatisfied(this)) {
            getGraph().setFrozen();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns the condition that decides whether the underlying graph should be
     * frozen.
     */
    final protected FreezeCondition getFreezeCondition() {
        if (this.freezeCondition == null) {
            this.freezeCondition = createFreezeCondition();
        }
        return this.freezeCondition;
    }

    /**
     * Returns the condition that decides whether the underlying graph should be
     * frozen.
     */
    protected FreezeCondition createFreezeCondition() {
        return new FreezeCondition();
    }

    /**
     * Retrieves the node set of a given graph. This is either done through the
     * graph's cache, if that exists, or by querying the graph itself.
     * @see #getDeltaCache(Graph)
     */
    protected Set<? extends Edge> getEdgeSet(Graph graph) {
        DeltaGraphCache cache = getDeltaCache(graph);
        if (cache == null) {
            return graph == null ? null : graph.edgeSet();
        } else {
            return cache.getEdgeSet();
        }
    }

    /**
     * Retrieves the edge set of a given graph. This is either done through the
     * graph's cache, if that exists, or by querying the graph itself.
     * @see #getDeltaCache(Graph)
     */
    protected Set<? extends Node> getNodeSet(Graph graph) {
        DeltaGraphCache cache = getDeltaCache(graph);
        if (cache == null) {
            return graph == null ? null : graph.nodeSet();
        } else {
            return cache.getNodeSet();
        }
    }

    /**
     * Retrieves the delta cache of a given graph, if the graph is a
     * {@link DeltaGraph} and its cache is currently set.
     */
    protected DeltaGraphCache getDeltaCache(Graph graph) {
        if (graph instanceof DeltaGraph
            && !((DeltaGraph) graph).isCacheCleared()) {
            return ((DeltaGraph) graph).getCache();
        } else {
            return null;
        }
    }

    /**
     * The cached node set of the underlying graph.
     */
    private Set<Node> nodeSet;
    /**
     * The size of {@link #nodeSet}.
     */
    private int nodeCount = -1;
    /**
     * The cached edge set of the underlying graph.
     */
    private Set<Edge> edgeSet;
    /**
     * The size of {@link #edgeSet}.
     */
    private int edgeCount = -1;
    /**
     * The delta from the cache basis to the underlying graph.
     */
    private DeltaStore cacheDelta;
    /**
     * The graph with respect to which the cache delta has been computed. This
     * is either some predecessor in the chain of graph bases or
     * <code>null</code> (if the graph is frozen).
     */
    private AbstractGraph<? extends GraphCache> cacheBasis;

    /**
     * The distance between the checkpoint and the underlying graph of this
     * cache. If <code>distance == 0</code>, the underlying graph is itself a
     * checkpoint. The initial value is set to negative, to indicate that the
     * distance has not been initialised.
     */
    private FreezeCondition freezeCondition;
    /** Flag indicating that the underlying graph is frozen. */
    private boolean frozen;

    /** Condition testing if a given graph is eligible for freezing. */
    protected class FreezeCondition {
        /** Indicates if the freeze condition is satisfied for a given cache. */
        public boolean isSatisfied(DeltaGraphCache subject) {
            return getFreezeDistance() < 0;
        }

        /**
         * Returns the checkpoint distance of this cache. The checkpoint
         * distance is a measure used to determine whether the underlying graph
         * should be checkpointed. The lower the measure, the more urgent the
         * need to checkpoint the graph; if it is <code>0</code>,
         * checkpointing is in order.
         */
        int getFreezeDistance() {
            if (this.freezeDistance == INIT_DISTANCE) {
                this.freezeDistance = computeFreezeDistance();
            }
            return this.freezeDistance;
        }

        /** Resets the condition to its initial state. */
        void reset() {
            this.freezeDistance = INIT_DISTANCE;
        }

        /**
         * Initialises the origin and origin distance. Callback method from
         * {@link #isFrozen()} and {@link #getFreezeDistance()}.
         */
        private int computeFreezeDistance() {
            int result;
            DeltaGraph graph = getGraph();
            if (isFrozen()) {
                result = computeFreezeMeasure(graph);
            } else {
                DeltaGraphCache basisCache = getDeltaCache(graph.getBasis());
                if (basisCache == null) {
                    result = 1;
                } else if (basisCache.isFrozen()) {
                    result =
                        basisCache.getFreezeCondition().getFreezeDistance();
                } else {
                    result =
                        basisCache.getFreezeCondition().getFreezeDistance()
                            - computeFreezeDecrement(basisCache.getGraph());
                }
            }
            return result;
        }

        /**
         * Computes the freezing measure for a given (frozen) graph. This is a
         * measure for when the next graph should be frozen.
         * @see #getFreezeDistance()
         * @see #computeFreezeDecrement(DeltaGraph)
         */
        private int computeFreezeMeasure(Graph graph) {
            int graphSize =
                graph instanceof DeltaGraph
                        ? ((DeltaGraph) graph).getDeltaSize() : graph.size();
            return 2 * graphSize;
        }

        /**
         * Computes the difference in freezing distance caused by a given delta
         * graph. This is subtracted from the freezing distance of the basis.
         * @see #getFreezeDistance()
         */
        protected int computeFreezeDecrement(DeltaGraph graph) {
            return graph.getDeltaSize();
        }

        /**
         * The distance between the checkpoint and the underlying graph of this
         * cache. If <code>distance == 0</code>, the underlying graph is
         * itself a checkpoint. The initial value is set to negative, to
         * indicate that the distance has not been initializd.
         */
        private int freezeDistance = INIT_DISTANCE;

        /**
         * Initial value for {@link #freezeDistance}, which indicates that it
         * has not yet been computed.
         */
        static private final int INIT_DISTANCE = Integer.MAX_VALUE;
    }
}