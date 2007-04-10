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
 * $Id: DeltaGraphCache.java,v 1.2 2007-03-30 15:50:23 rensink Exp $
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
 * @version $Revision: 1.2 $
 */
public class DeltaGraphCache extends GraphCache {
    /**
     * Constructs a cache for a given graph.  
     */
    protected DeltaGraphCache(DeltaGraph graph) {
        super(graph);
    }
    
    /**
     * Returns the cached node set. 
     * If no set is cached, it is reconstructed from the underlying graph.
     */
    public Set<Node> getNodeSet() {
        if (nodeSet == null) {
        	nodeSet = computeNodeSet();
        }
        return nodeSet;
    }
    
    /**
     * Returns the number of nodes in the graph.
     * This number is stored explicitly in the cache, 
     * so it can be used even if the node set itself is invalidated.
     */
    public int getNodeCount() {
        assert nodeCount < 0 || nodeCount == getNodeSet().size() : "Node count "+nodeCount+" should equal number of elements in node set "+getNodeSet();
        int result = nodeCount;
        if (result < 0) {
            result = getNodeSet().size();
            if (isFixed()) {
                nodeCount = result;
            }
        }
        assert result == getNodeSet().size() : "Node count "+result+" should equal number of elements in node set "+getNodeSet();
        return result;
    }
    
    /**
     * Returns the cached edge set. 
     * If no set is cached, it is reconstructed from the underlying graph.
     */
    public Set<Edge> getEdgeSet() {
        if (edgeSet == null) {
        	edgeSet = computeEdgeSet();
        }
        return edgeSet;
    }

    /**
     * Returns the number of edges in the graph.
     * This number is stored explicitly in the cache, 
     * so it can be used even if the edge set itself is invalidated.
     */
    public int getEdgeCount() {
        int result = edgeCount;
        if (result < 0) {
            result = getEdgeSet().size();
            if (isFixed()) {
                edgeCount = result;
            }
        }
        assert result == getEdgeSet().size() : "Edge count "+result+" should equal number of elements in edge set "+getEdgeSet();
        return result;
    }

    /**
     * Returns the delta store computed for the cache.
     * This is the difference between the underlying graph of this cache (as returned
     * by {@link #getGraph()} and the cache basis (as returned by {@link #getCacheBasis()}.
     */
    public DeltaStore getCacheDelta() {
    	if (! isCacheInit()) {
    		initCache();
    	}
    	return cacheDelta;
    }

    /** Returns the basis of the underlying graph. */
    public Graph getCacheBasis() {
    	if (! isCacheInit()) {
    		initCache();
    	}
    	return cacheBasis;
    }
    
    /**
	 * Indicates if the delta of this cache has been computed.
	 * Callback method from {@link #getCacheDelta()}.
	 */
	protected boolean isCacheInit() {
		return cacheDelta != null;
	}
//
//	/**
//	 * Returns the number of times the delta has been computed for this cache.
//	 */
//	protected int getDeltaComputeCount() {
//		return deltaComputeCount;
//	}

	/**
	 * Initializes the delta store the cache. This is done by taking
	 * the sets from the basis, and applying the delta if the graph is fixed.
	 * Callback method from {@link #getCacheDelta()}.
	 */
    protected void initCache() {
    	reporter.start(INIT_DELTA);
		frozen = getGraph().isFrozen();
    	if (frozen) {
    		initFrozenCache();
    	} else if (getGraph().isFixed()) {
    		initFixedCache();
    	} else {
    		initModifiableCache();
    	}
    	reporter.stop();
	}

    /**
     * Computes the cache delta in case the underlying graph is modifiable, i.e., not fixed.
     * @require <code>!isFixed()</code>
     */
	protected void initModifiableCache() {
		cacheBasis = getGraph().getBasis();
		cacheDelta = new DeltaStore();
	}

    /**
     * Computes the cache delta in case the underlying graph is fixed but not frozen.
     * @require <code>isFixed() && !isFrozen()</code>
     */
	protected void initFixedCache() {
		Graph graphBasis = getGraph().getBasis();
		DeltaGraphCache basisCache = getDeltaCache(graphBasis);
		if (basisCache == null || basisCache.suggestSetFrozen()) {
			cacheBasis = graphBasis;
			cacheDelta = new DeltaStore();
		} else {
			cacheBasis = basisCache.getCacheBasis();
			cacheDelta = new DeltaStore(basisCache.getCacheDelta());
		}
		getGraph().applyDelta(cacheDelta);
	}

    /**
     * Computes the cache delta in case the underlying graph is frozen.
     * @require <code>isFrozen()</code>
     */
	protected void initFrozenCache() {
		cacheBasis = null;
		cacheDelta = new DeltaStore();
		getGraph().applyDelta(cacheDelta);
	}

	protected Set<Node> computeNodeSet() {
		reporter.start(COMPUTE_NODE_SET);
		Set<Node> result;
		Graph basis = getCacheBasis();
		DeltaStore delta = getCacheDelta();
		if (basis == null) {
			result = delta.getAddedNodeSet();
		} else {
			result = delta.newStackedNodeSet(getNodeSet(basis));
		}
		reporter.stop();
		return result;
	}

    protected Set<Edge> computeEdgeSet() {
    	reporter.start(COMPUTE_EDGE_SET);
    	Set<Edge> result;
    	DeltaStore delta = getCacheDelta();
    	Graph basis = getCacheBasis();
    	if (basis == null) {
    		result = delta.getAddedEdgeSet();
    	} else {
    		result = delta.newStackedEdgeSet(getEdgeSet(basis));
    	}
    	reporter.stop();
    	return result;
    }

    /**
     * If the label-to-edge map of the basis is currently set,
     * constructs the result by cloning that one and performing the delta upon it.
     * Otherwise, delegates to super.
     */
    @Override
    protected Map<Label, Set<Edge>>[] computeLabelEdgeMaps() {
    	// the cache basis
    	Graph basis = getCacheBasis();
    	if (!(basis instanceof AbstractGraph)) {
    		// if the cache basis is not an abstract graph,
    		// we cannot use the delta to compute the label/edge maps array
    		// so we have to compute it the hard way
    		return super.computeLabelEdgeMaps();
    	} else {
    		// otherwise, we can use the cache delta
            reporter.start(COMPUTE_LABEL_EDGE_MAP);
    		DeltaApplier delta = getCacheDelta();
    		final Map<Label, Set<Edge>>[] basisMaps = ((AbstractGraph) basis).getLabelEdgeMaps();
			final Map<Label, Set<Edge>>[] result = new Map[basisMaps.length];
			for (int i = 1; i < result.length; i++) {
				if (basisMaps[i] != null) {
					result[i] = new HashMap<Label,Set<Edge>>(basisMaps[i]);
				}
			}
			DeltaTarget target = new DeltaTarget() {
				public boolean addEdge(Edge elem) {
					return addToLabelEdgeMaps(result, elem, basisMaps);
				}

				public boolean addNode(Node elem) {
					throw new UnsupportedOperationException("No node manipulation through this delta target");
				}

				public boolean removeEdge(Edge elem) {
					return removeFromLabelEdgeMaps(result, elem, basisMaps);
				}

				public boolean removeNode(Node elem) {
					throw new UnsupportedOperationException("No node manipulation through this delta target");
				}
			};
			delta.applyDelta(target, DeltaApplier.EDGES_ONLY);
			reporter.stop();
			assert getEdgeSet().containsAll(new CollectionOfCollections<Edge>(result[2].values())) : "Edges not correct: "
					+ getEdgeSet() + " does not contains all of " + result[2].values();
			return result;
		}
    }

    /**
	 * If the node-to-edge map of the basis is currently set, constructs the
	 * result by cloning that one and performing the delta upon it. Otherwise,
	 * delegates to super.
	 */
    @Override
    protected Map<Node, Set<Edge>> computeNodeEdgeMap() {
    	// the cache basis
    	Graph basis = getCacheBasis();
    	if (basis == null) {
    		// if the cache basis is not an abstract graph,
    		// we cannot use the delta to compute the label/edge maps array
    		// so we have to compute it the hard way
        	return super.computeNodeEdgeMap();
    	} else {
			reporter.start(COMPUTE_NODE_EDGE_MAP);
			Map<Node, Set<Edge>> basisMap = (Map<Node,Set<Edge>>) basis.nodeEdgeMap();
			Map<Node, Set<Edge>> result = new HashMap<Node, Set<Edge>>(basisMap);
			DeltaTarget target = createNodeEdgeMapTarget(basisMap, result);
			getCacheDelta().applyDelta(target);
			reporter.stop();
			assert getEdgeSet().containsAll(new CollectionOfCollections<Edge>(result.values())) : "Map not correct: \nEdges "+getEdgeSet()+" not compatible with \nnode/edge map"+result;
			return result;
		}
	}

	/**
	 * Creates a {@link DeltaTarget} that creates a node/edges map on the basis of
	 * an existing map.
	 */
	private DeltaTarget createNodeEdgeMapTarget(final Map<Node, ? extends Set<? extends Edge>> basisMap, final Map<Node, Set<Edge>> result) {
		return new DeltaTarget() {
//			public void addElement(Element elem) {
//				if (elem instanceof Node) {
//					addToNodeEdgeMap(result, (Node) elem);
//				} else {
//					addToNodeEdgeMap(result, (Edge) elem, basisMap);
//				}
//			}
//
//			public void removeElement(Element elem) {
//				if (elem instanceof Node) {
//					removeFromNodeEdgeMap(result, (Node) elem);
//				} else {
//					removeFromNodeEdgeMap(result, (Edge) elem, basisMap);
//				}
//			}
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
	 * Callback factory method to create a {@link DeltaSet} on top of the
	 * basis set. It is required that the basis node set is not
	 * <code>null</code>.
	 */
    protected <T> DeltaSet<T> createDeltaSet(Set<T> basis, Set<T> added, Set<T> removed) {
        return new DeltaSet<T>(basis, added, removed);
    }

    /**
	 * Callback factory method to create a {@link StackedSet} on top of a
	 * basis set, with predefined added and removed sets. It is required that
	 * none of the sets is <code>null</code>.
	 */
    protected <T> StackedSet<T> createStackedSet(Set<? extends T> basis, Set<T> added, Set<T> removed) {
    	assert basis.containsAll(removed) : "Basis "+basis+" does not contain removed elements "+removed;
        return new StackedSet<T>(basis, added, removed);
    }

    /**
     * Adds an edge to a given label-to-edgeset mapping array,
     * cloning the relevant entry if necessary.
     * @param newMaps the array to be updated
     * @param edge the edge to be added
     * @return <code>true</code> if the edge was indeed added, i.e., was not
     * yet there in the first place
     */
    boolean addToLabelEdgeMaps(Map<Label, Set<Edge>>[] newMaps, Edge edge, Map<Label, Set<Edge>>[] basisMaps) {
		int arity = edge.endCount();
		Map<Label,Set<Edge>> labelEdgeMap = newMaps[arity];
		Set<Edge> labelEdgeSet = labelEdgeMap.get(edge.label());
		if (labelEdgeSet == null) {
			labelEdgeSet = createEdgeSet(null);
			labelEdgeMap.put(edge.label(), labelEdgeSet);
		} else if (labelEdgeSet == basisMaps[arity].get(edge.label())) {
			labelEdgeSet = createEdgeSet(labelEdgeSet);
			labelEdgeMap.put(edge.label(), labelEdgeSet);
		}
		return labelEdgeSet.add(edge);
	}

    /**
	 * Removes an edge from a given label-to-edgeset mapping array.
	 * 
	 * @param newMaps
	 *            the array to be updated
	 * @param edge
	 *            the edge to be removed
     * @return <code>true</code> if the edge was actually there in the first place
	 */
    boolean removeFromLabelEdgeMaps(Map<Label, Set<Edge>>[] newMaps, Edge edge, Map<Label, Set<Edge>>[] basisMaps) {
		Map<Label,Set<Edge>> labelEdgeMap = newMaps[edge.endCount()];
		Set<Edge> labelEdgeSet = labelEdgeMap.get(edge.label());
		if (labelEdgeSet == basisMaps[edge.endCount()].get(edge.label())) {
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
	 * @param newMap
	 *            the mapping to be updated
	 * @param edge
	 *            the edge to be added
     * @return <code>true</code> if the edge was indeed added, i.e.,
     * was not yet ther in the first place
	 */
    boolean addToNodeEdgeMap(Map<Node, Set<Edge>> newMap, Edge edge, Map<Node, ? extends Set<? extends Edge>> basisMap) {
    	boolean result = false;
    	assert basisMap != null;
		int arity = edge.endCount();
		for (int i = 0; i < arity; i++) {
			Node end = edge.end(i);
			Set<Edge> edgeSet = newMap.get(end);
			if (edgeSet == null) {
				newMap.put(end, edgeSet = createEdgeSet(null));
			} else if (edgeSet == basisMap.get(end)) {
				newMap.put(end, edgeSet = createEdgeSet(edgeSet));
			}
			result |= edgeSet.add(edge);
		}
		return result;
	}
    
    /**
	 * Removes an edge from a given node-to-edgeset mapping, cloning the
	 * relevant entry if necessary.
	 * 
	 * @param newMap
	 *            the mapping to be updated
	 * @param edge
	 *            the edge to be removed
     * @return <code>true</code> if the edge was indeed removed
	 */
    boolean removeFromNodeEdgeMap(Map<Node, Set<Edge>> newMap, Edge edge, Map<Node, ? extends Set<? extends Edge>> basisMap) {
    	boolean result = false;
    	assert basisMap != null;
		int arity = edge.endCount();
		for (int i = 0; i < arity; i++) {
			Node end = edge.end(i);
			Set<Edge> edgeSet = newMap.get(end);
			if (edgeSet != null) {
				if (edgeSet == basisMap.get(end)) {
					newMap.put(end, edgeSet = createEdgeSet(edgeSet));
				}
				result |= edgeSet.remove(edge);
			}
		}
		return result;
	}

    /**
	 * Convenience method for <code>(DeltaGraph) getGraph()</code>.
	 */
    @Override
    public DeltaGraph getGraph() {
        return (DeltaGraph) graph;
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
    	if (! isCacheInit()) {
    		initCache();
    	}
		return frozen;
    }

    /**
     * Signals that the underlying graph has been fixed.
     * This gives the cache the change to rearrange things.
     * Callback method from {@link DeltaGraph#setFixed()}.
     */
    protected void notifySetFixed() {
		if (nodeSet instanceof DeltaSet) {
			reporter.start(NODESET_LOWER);
			nodeSet = ((DeltaSet<Node>) nodeSet).lower();
			edgeSet = ((DeltaSet<Edge>) edgeSet).lower();
			reporter.stop();
		}
	}
    
    /**
     * Signals that the underlying graph has been frozen.
     * This means the cache has to be reset.
     * Callback method from {@link DeltaGraph#setFrozen()}.
     */
    protected void notifySetFrozen() {
    	// make sure the node and edge counts are locally stored
    	// and reset the node and edge sets, so they don't stack arbitrarily deep
    	if (nodeSet != null) {
    		getNodeCount();
    	}
    	if (edgeSet != null) {
        	getEdgeCount();
    	}
    	resetCache();
    }
    
    /**
     * Resets the cached delta.
     * Callback method from {@link #notifySetFrozen()}.
     */
    protected void resetCache() {
    	nodeSet = null;
    	edgeSet = null;
    	cacheDelta = null;
        cacheBasis = null;
        freezeDistance = INIT_DISTANCE;
    }
//
//	/**
//	 * The basis of the underlying graph, or <code>null</code> if the underlying
//	 * graph is frozen.
//	 */
//	protected Graph getGraphBasis() {
//		if (isFrozen()) {
//			return null;
//		} else {
//			return getGraph().getBasis();
//		}
//	}
//
//	/**
//	 * Returns the checkpoint of this cache.
//	 * The checkpoint is the graph with respect to which the added and removed sets
//	 * have been computed.
//	 * Looks up the checkpoint first, using {@link #initOrigin()}, if this has
//	 * not yet been done. 
//	 */
//	protected Graph getOrigin() {
//		if (! isOriginSet()) {
//			initOrigin();
//		}
//		return origin;
//	}

	/**
	 * Suggests that it might be worth considering checkpointing the underlying graph.
	 * The suggestion is followed if {@link #getFreezeDistance()} supports it;
	 * if so, {@link #notifySetFrozen()} is called to actually checkpoint the graph.
	 * The return value indicates if the graph was indeed ckheckpointed.
	 */
	protected boolean suggestSetFrozen() {
		if (isFrozen()) {
			return true;
		} else if (getFreezeDistance() < 0) {
			getGraph().setFrozen();
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Returns the checkpoint distance of this cache.
	 * The checpoint distance is a measure used to determine whether the underlying
	 * graph should be checkpointed. The lower the measure, the more urgent the
	 * need to checkpoint the graph; if it is <code>0</code>, checkpointing is in order.
	 * Computes the checkpoint distance first, using {@link #initOrigin()}, if this has
	 * not yet been done. 
	 */
	protected int getFreezeDistance() {
		if (freezeDistance == INIT_DISTANCE) {
			freezeDistance = computeFreezeDistance();
		}
		return freezeDistance;
	}
//	
//	/**
//	 * Tests if the origin has been initialized.
//	 * Callback mathod from {@link #isFrozen()} and {@link #getFreezeDistance()}.
//	 */
//	protected boolean isOriginSet() {
//		return freezeDistance != INIT_DISTANCE;
//	}

	/**
	 * Initializes the origin and origin distance.
	 * Callback mathod from {@link #isFrozen()} and {@link #getFreezeDistance()}.
	 */
	protected int computeFreezeDistance() {
		int result;
		DeltaGraph graph = getGraph();
		if (isFrozen()) {
			result = computeFreezeMeasure(graph);
		} else {
			DeltaGraphCache basisCache = getDeltaCache(graph.getBasis());
			if (basisCache == null) {
				result = 1;
			} else if (basisCache.isFrozen()) {
				result = basisCache.getFreezeDistance();
			} else {
				result = basisCache.getFreezeDistance() - computeFreezeDecrement(basisCache.getGraph());
//				if (result < 0 && basisCache.suggestSetFrozen()) {
//					// now the basis cache is after all frozen
//					result = basisCache.getFreezeDistance();
//				}
			}
		}
		return result;
	}

	/**
	 * Computes the freezing measure for a given (frozen) graph.
	 * This is a measure for when the next graph should be frozen.
	 * @see #getFreezeDistance()
	 * @see #computeFreezeDecrement(DeltaGraph)
	 */
	protected int computeFreezeMeasure(Graph graph) {
		int graphSize = graph instanceof DeltaGraph ? ((DeltaGraph) graph).getDeltaSize() : graph.size();
		return 2 * graphSize;
	}

	/**
	 * Computes the difference in freezing distance caused by a given delta graph.
	 * This is subtracted from the freezing distance of the basis.
	 * @see #getFreezeDistance()
	 */
	protected int computeFreezeDecrement(DeltaGraph graph) {
		return graph.getDeltaSize();
	}

	protected Set<? extends Edge> getEdgeSet(Graph graph) {
		DeltaGraphCache cache = getDeltaCache(graph);
		if (cache == null) {
			return graph == null ? null : graph.edgeSet();
		} else {
			return cache.getEdgeSet();
		}
	}

	protected Set<? extends Node> getNodeSet(Graph graph) {
		DeltaGraphCache cache = getDeltaCache(graph);
		if (cache == null) {
			return graph == null ? null : graph.nodeSet();
		} else {
			return cache.getNodeSet();
		}
	}
//
//	/**
//	 * Looks up the label-edge-map array of a given graph, creating it if required.
//	 * @param graph the graph of which we want the map array
//	 * @param create flag to indicate if the map array should be 
//	 * created if not already present
//	 * @return the label-edge map array from <code>graph</code>, or <code>null</code>
//	 * if the map was not there and not created
//	 */
//	protected Map[] getLabelEdgeMaps(Graph graph, boolean create) {
//		DeltaGraphCache cache = getDeltaCache(graph, create);
//		if (create && cache == null) {
//			if (graph instanceof AbstractGraph) {
//				return ((AbstractGraph) graph).getLabelEdgeMaps();
//			} else {
//				return createLabelEdgeMaps();
//			}
//		} else if (create || cache.isLabelEdgeMapsSet()) {
//			return cache.getLabelEdgeMaps();
//		} 
//		return null;
//	}
//
//
//	/**
//	 * Looks up the node/edge map of a given graph, creating it if required.
//	 * @param graph the graph of which we want the map
//	 * @param create flag to indicate if the map should be 
//	 * created if not already present
//	 * @return the node/edge map array from <code>graph</code>, or <code>null</code>
//	 * if the map was not there and not created
//	 */
//	protected Map getNodeEdgeMap(Graph graph, boolean create) {
//		DeltaGraphCache cache = getDeltaCache(graph, create);
//		if (create && cache == null) {
//			if (graph != null) {
//				return graph.nodeEdgeMap();
//			} else {
//				
//			}
//		} else if (create || cache.isNodeEdgeMapSet()) {
//			return cache.getNodeEdgeMap();
//		} 
//		return null;
//	}
//
//	/**
//	 * Retrives the delta cache of a given graph, if the graph is a 
//	 * {@link DeltaGraph}. A parameter controls if the cache is to be created
//	 * in case it is currently cleared. If the graph is not a {@link DeltaGraph}
//	 * or the cache is not created, the method returns <code>null</code>.
//	 * @param graph the graph of which we want to retrieve the cache
//	 * @param create if <code>true</code>, the cache should be created if it
//	 * is currently cleared
//	 */
//	protected DeltaGraphCache getDeltaCache(Graph graph, boolean create) {
//		if (graph instanceof DeltaGraph && (create || !((DeltaGraph) graph).isCacheCleared())) {
//			return ((DeltaGraph) graph).getDeltaCache();
//		} else {
//			return null;
//		}
//	}
	
	/**
	 * Retrives the delta cache of a given graph, if the graph is a 
	 * {@link DeltaGraph} and its cache is currently set. 
	 */
	protected DeltaGraphCache getDeltaCache(Graph graph) {
		if (graph instanceof DeltaGraph && !((DeltaGraph) graph).isCacheCleared()) {
			return ((DeltaGraph) graph).getDeltaCache();
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
     * The graph with respect to which the cache delta has been computed.
     * This is either some predecessor in the chain of graph bases or <code>null</code> (if the
     * graph is frozen).
     */
    private Graph cacheBasis;
//    /**
//     * Count of the number of times this cache delta has been computed.
//     */
//    private int deltaComputeCount;
//    /**
//     * Flag to indicate that the {@link #cacheDelta} is the difference with the basis rather than the origin.
//     */
//    protected boolean cacheDeltaIsGraphDelta; 
//    /**
//     * The cache of the basis graph, if any.
//     */
//    private Graph origin;
    /**
     * The distance between the checkpoint and the underlying graph of this cache.
     * If <code>distance == 0</code>, the underlying graph is itself a checkpoint.
     * The initial value is set to negative, to indicate that the distance has not been initializd.
     */
    private int freezeDistance = INIT_DISTANCE;
    /** Flag indicating that the underlying graph is frozen. */
    private boolean frozen;
    /**
     * Initial value for {@link #freezeDistance}, which indicates that it 
     * has not yet been computed.
     */
    static private final int INIT_DISTANCE = Integer.MAX_VALUE; 
	/**
	 * The treshhold for the depth: when it grows above this size the graph is frozen.
	 */
	public static final int FREEZE_DEPTH = 8;
    
    static final int INIT_DELTA = reporter.newMethod("computeCacheDelta()");
    static final int NODESET_LOWER = reporter.newMethod("setFixed - lower");
    static final int NODESET_NEW = reporter.newMethod("setFixed - new");
    static final int COMPUTE_NODE_SET = reporter.newMethod("computeNodeSet()");
    static final int COMPUTE_EDGE_SET = reporter.newMethod("computeEdgeSet()");
}