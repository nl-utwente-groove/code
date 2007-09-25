/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an 
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific 
 * language governing permissions and limitations under the License.
 *
 * $Id: DeltaGraph.java,v 1.10 2007-09-25 22:57:53 rensink Exp $
 */
package groove.graph;

import groove.util.Groove;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A delta graph is a graph that may store only the <i>changed elements</i> with respect to
 * another, <i>basis</i> graph. This brings a huge potential reduction in storage, at the penalty
 * of computation time, since the graph elements may have to be reconstructed from the basis modulo
 * the changes. This implementation caches the element set so as to avoid too frequent
 * reconstruction.
 * @author Arend Rensink
 * @version $Revision: 1.10 $
 */
public class DeltaGraph<C extends DeltaGraphCache> extends AbstractGraph<C> implements DeltaApplier {
    /**
     * An empty array constant, to share in order to save space.
     */
    protected static final Element[] EMPTY_ELEMENT_ARRAY = new Element[0];

    /**
     * Creates a new, empty delta graph.
     */
    public DeltaGraph() {
        this(EMPTY_GRAPH);
    }

    /**
     * Constructs a clone of a given graph. The cloned graph should be fixed.
     * @param graph the graph to be cloned
     * @require <tt>graph != null && graph.isFixed()</tt>
     * @ensure <tt>result.equals(graph)</tt>
     */
    public DeltaGraph(AbstractGraph graph) {
        assert graph.isFixed() : "Don't create delta graph on top of unfixed graph " + graph;
        this.basis = graph;
        deltaGraphCount++;
    }

    // ------------------------- COMMANDS ------------------------------

    public boolean addNode(Node node) {
        reporter.start(ADD_NODE);
        assert !isFixed() : "Trying to add " + node + " to unmodifiable graph";
        boolean added = getCachedNodeSet().add(node);
        if (added) {
            assert nodeCount() == new HashSet<Node>(nodeSet()).size() : String.format("Overlapping node number for %s in %s", node, nodeSet());
            fireAddNode(node);
        }
        reporter.stop();
        return added;
    }

    public boolean addEdge(Edge edge) {
        reporter.start(ADD_EDGE);
        assert !isFixed() : "Trying to add " + edge + " to unmodifiable graph";
        boolean isNew = !getCachedEdgeSet().contains(edge);
        if (isNew) {
            int arity = edge.endCount();
            for (int i = 0; i < arity; i++) {
                Node end = edge.end(i);
                if (getCachedNodeSet().add(end)) {
                    fireAddNode(end);
                }
            }
            getCachedEdgeSet().add(edge);
            fireAddEdge(edge);
        }
        reporter.stop();
        return isNew;
    }

    public boolean removeNode(Node node) {
        reporter.start(REMOVE_NODE);
        assert !isFixed() : "Trying to remove " + node + " from unmodifiable graph";
        boolean removed = getCachedNodeSet().contains(node);
        if (removed) {
            removeEdgeSet(new HashSet<Edge>(edgeSet(node)));
            getCachedNodeSet().remove(node);
            fireRemoveNode(node);
        }
        reporter.stop();
        return removed;
    }

    public boolean removeEdge(Edge edge) {
        reporter.start(REMOVE_EDGE);
        boolean removed = getCachedEdgeSet().remove(edge);
        if (removed) {
            fireRemoveEdge(edge);
        }
        reporter.stop();
        return removed;
    }

    // -------------------- PackageGraph methods ---------------------

    public boolean addEdgeWithoutCheck(Edge edge) {
        boolean result = getCachedEdgeSet().add(edge);
        if (result) {
            fireAddEdge(edge);
        }
        return result;
    }

    public boolean removeNodeWithoutCheck(Node node) {
        boolean result = getCachedNodeSet().remove(node);
        if (result) {
            fireRemoveNode(node);
        }
        return result;
    }
//    
//    @Override
//	public boolean isFixed() {
//		return isDeltaArraySet();
//	}

	/**
     * Sets the delta using {@link #computeFixedDeltaArray()}. In either case the super implementation
     * is called at the end.
     */
    @Override
    public void setFixed() {
        if (!isFixed()) {
            reporter.start(SET_FIXED);
            setDeltaArray(computeFixedDeltaArray());
            super.setFixed();
            getCache().notifySetFixed();
//            if (Groove.GATHER_STATISTICS) {
//                totalEdgeCount += this.edgeCount();
//                totalNodeCount += this.nodeCount();
//                fixedDeltaGraphCount++;
//                deltaElementCount += deltaArray.length;
//            }
            reporter.stop();
        }
    }

    // ------------- general methods (see AbstractGraph) ----------

    /**
     * If this graph is fixed, this implementation returns a {@link DeltaGraph} with this one as
     * basis; otherwise, it returns a {@link DefaultGraph}.
     */
    @Override
    public Graph clone() {
        reporter.start(CLONE);
        Graph result = isFixed() ? (Graph) new DeltaGraph(this) : new NodeSetEdgeSetGraph(this);
        reporter.stop();
        return result;
    }

    public Graph newGraph() {
        return new DeltaGraph();
    }

    public Set<? extends Edge> edgeSet() {
        return Collections.unmodifiableSet(getCachedEdgeSet());
    }

    public Set<? extends Node> nodeSet() {
        return Collections.unmodifiableSet(getCachedNodeSet());
    }
    
    /** Delegates to {@link DeltaGraphCache#getEdgeCount()}. */
    @Override
    final public int edgeCount() {
    	return getCache().getEdgeCount();
    }

    /** Delegates to {@link DeltaGraphCache#getNodeCount()}. */
    @Override
    final public int nodeCount() {
    	return getCache().getNodeCount();
    }

    /**
     * Clears the cache of this graph, using {@link #clearCache()}, and then recursively clears the
     * cache of the basis, if any. Used for accounting purposes.
     */
    public void clearAllCaches() {
        clearCache();
        if (basis instanceof DeltaGraph) {
            ((DeltaGraph) basis).clearAllCaches();
        } else {
            basis.clearCache();
        }
    }

    /**
     * Returns the basis of this delta graph. May be <tt>null</tt> if the graph has no basis.
     */
    public AbstractGraph getBasis() {
        return basis;
    }

    /**
     * Returns the cached node set of this graph.
     */
    protected Set<Node> getCachedNodeSet() {
        return getCache().getNodeSet();
    }

    /**
     * Returns the cached edge set of this graph.
     */
    protected Set<Edge> getCachedEdgeSet() {
        return getCache().getEdgeSet();
    }

    /**
     * Indicates if this graph is <i>frozen</i> rather than a proper delta graph. The decision to
     * freeze the graph is taken at the time it is fixed.
     * @see #setFrozen()
     */
    protected boolean isFrozen() {
        return basis == null;
    }

    /**
     * Fixes the graph by freezing it. This means that the basis is set to <tt>null</tt> and all graph elements
     * are considered to have been added. Furthermore, the basis is reversed, i.e., the newly frozen
     * graph is offered to the current basis as its new basis. The graph should be fixed before
     * invoking this method.
     * Callback method from {@link #setFixed()}.
     * @require <tt>!isFixed()</tt>
     * @see #invertBasis(DeltaGraph, Element[])
     */
    protected void setFrozen() {
    	assert isFixed() : "Graphs should only be frozen after they are fixed";
        setDeltaArray(computeFrozenDeltaArray());
        invertBasis();
        getCache().notifySetFrozen();
        if (Groove.GATHER_STATISTICS) {
        	frozenDeltaGraphCount++;
        }
    }
    
    /** 
     * Resets the basis of this delta graph, and suggests to the
     * old basis that it take this graph as its basis instead.
     */
    protected void invertBasis() {
	    Graph oldBasis = basis;
	    Element[] oldDelta = computeFixedDeltaArray();
	    this.basis = null;
        // reset the cache, since it may have stored the wrong delta
        getCache().resetCache();
	    // here we ask the old basis if it would not rather take us as
	    // a new basis, since we have a small reconstruction depth
	    // Given that caches are reconstructed only rarely, it's not
	    // clear if this is really a good idea, timewise
	    if (oldBasis instanceof DeltaGraph) {
	        ((DeltaGraph) oldBasis).invertBasis(this, oldDelta);
	    }
	}

	/**
	 * Changes the basis of this delta graph to another. Also offers this graph as a new basis to
	 * the previous basis, if this improves the delta for the previous basis.
	 * TODO: Currently disabled
	 * @param newBasis the new basis
	 * @param invertedDelta the change of <tt>newDelta</tt> w.r.t. this graph, as a array of
	 *        removed and added elements
	 */
	protected synchronized void invertBasis(DeltaGraph newBasis, Element[] invertedDelta) {
	    if (false) {//newBasis.getDepth() + 1 < getDepth()) {
	        if (Groove.GATHER_STATISTICS) {
	            deltaElementCount -= this.deltaArray.length;
	            deltaElementCount += invertedDelta.length;
	        }
	        int reverseDeltaSize = invertedDelta.length;
	        // copy the old values to temporary variables
	        Graph oldBasis = getBasis();
	        Element[] oldDelta = getDeltaArray();
	        int oldDeltaSize = getDeltaSize();
	        // compute the number of elements in the delta array that are actually not delta elements
	        int restSize = oldDelta.length - oldDeltaSize;
	        // compute the new delta by reversing the added and removed sub-arrays in the parameters
	        basis = newBasis;
	        // search first removable element
	        int reverseAddedLength;
	        for (reverseAddedLength = 0; reverseAddedLength < reverseDeltaSize
	                && invertedDelta[reverseAddedLength] != null; reverseAddedLength++) {
	            // nothing to be done; we're just searching
	        }
	        if (reverseDeltaSize == 0) {
	        	if (restSize == 0) {
	        		deltaArray = EMPTY_ELEMENT_ARRAY;
	        	} else {
	        		deltaArray = new Element[restSize];
	        	}
	        } else if (reverseAddedLength == 0) {
	            // there are no added elements in reverse, so no removed elements in this
	            deltaArray = new Element[reverseDeltaSize - 1 + restSize];
	            System.arraycopy(invertedDelta, 1, deltaArray, 0, reverseDeltaSize - 1);
	        } else if (reverseAddedLength == reverseDeltaSize) {
	            // there are no removed elements in reverse, so no added elements in this
	            deltaArray = new Element[reverseDeltaSize + 1 + restSize];
	            System.arraycopy(invertedDelta, 0, deltaArray, 1, reverseDeltaSize);
	        } else {
	            deltaArray = new Element[reverseDeltaSize + restSize];
	            int addedLength = reverseDeltaSize - reverseAddedLength - 1;
	            System.arraycopy(invertedDelta, 0, deltaArray, addedLength + 1, reverseAddedLength);
	            System.arraycopy(invertedDelta, reverseAddedLength + 1, deltaArray, 0, addedLength);
	        }
	        // copy the non-delta elements from the old to the new delta
	        System.arraycopy(oldDelta, oldDeltaSize, deltaArray, deltaArray.length - restSize, restSize);
	        clearCache();
	        // now change the basis of the old basis to this graph
	        if (oldBasis instanceof DeltaGraph) {
	        	if (restSize > 0) {
	        		// extract the real delta elements from the old delta
	        		Element[] realOldDelta = new Element[oldDeltaSize];
	        		System.arraycopy(oldDelta, 0, realOldDelta, 0, oldDeltaSize);
	        		oldDelta = realOldDelta;
	        	}
	            ((DeltaGraph) oldBasis).invertBasis(this, oldDelta);
	        }
	    }
	}

	/**
     * Returns the length of the delta chain for this graph. The delta chain is the chain of graphs
     * composed from each other by deltas upon a basis graph.
     */
    protected int getDepth() {
        if (isFrozen()) {
            return 0;
        } else {
            int basisDepth = basis instanceof DeltaGraph ? ((DeltaGraph) basis).getDepth() : 0;
            return basisDepth + 1;
        }
    }
//
//    /**
//	 * Convenience method for <tt>(DeltaGraphCache) getCache()</tt>.
//	 */
//	final protected DeltaGraphCache getDeltaCache() {
//	    return (DeltaGraphCache) getCache();
//	}

	/**
     * This implementation returns a {@link DeltaGraphCache}. Note that the cache will attempt to
     * initialise itself using the basis' node and edge sets, if this graph is not fixed.
     */
	@Override
    protected C createCache() {
        return (C) new DeltaGraphCache(this);
    }

    /**
	 * Returns the delta array, or <tt>null</tt> as long as the delta is not set.
	 */
	protected Element[] getDeltaArray() {
	    return deltaArray;
	}

    /**
     * Indicates if the delta array is currently set.
     */
    protected boolean isDeltaArraySet() {
        return deltaArray != null;
    }
    
	/**
	 * Returns the number of {@link Element} entries in {@link #getDeltaArray()}.
	 * Normally this would equals <code>delta.length</code>, but if the delta array is
	 * used for other things as well, then it might be less.
	 */
	protected int getDeltaSize() {
		return deltaArray.length;
	}

	/**
     * Sets the delta array to a given value.
     */
    protected void setDeltaArray(Element[] deltaArray) {
        this.deltaArray = deltaArray;
    }
    
    /**
	 * Computes the array of added and removed element sets on the basis of the current 
	 * graph cache.
	 */
	protected Element[] computeFixedDeltaArray() {
		return computeDeltaArray(getCache().getCacheDelta());
	}

	/**
	 * Computes the array of added and removed element sets on the basis of a given delta store.
	 */
	protected Element[] computeDeltaArray(DeltaStore deltaStore) {
	    Element[] result;
	    int deltaSize = deltaStore.addedSize();
	    int removedSize = deltaStore.removedSize();
	    if (removedSize > 0) {
	        deltaSize += removedSize + 1;
	    }
	    if (deltaSize == 0) {
	        result = EMPTY_ELEMENT_ARRAY;
	    } else {
	        result = new Element[deltaSize];
	        int deltaIndex = 0;
	        for (Node addedElem: deltaStore.getAddedNodeSet()) {
	            result[deltaIndex] = addedElem;
	            deltaIndex++;
	        }
	        for (Edge addedElem: deltaStore.getAddedEdgeSet()) {
	            result[deltaIndex] = addedElem;
	            deltaIndex++;
	        }
	        // now skip one to demarcate the boundary between added and removed
	        deltaIndex++;
	        for (Node removedElem: deltaStore.getRemovedNodeSet()) {
	            result[deltaIndex] = removedElem;
	            deltaIndex++;
	        }
	        for (Edge removedElem: deltaStore.getRemovedEdgeSet()) {
	            result[deltaIndex] = removedElem;
	            deltaIndex++;
	        }
	    }
	    return result;
	}

	/**
	 * Delivers an array containing all elements in this graph,
	 * so that the graph can be reconstructed without the basis.
	 */
	protected Element[] computeFrozenDeltaArray() {
		Element[] result = new Element[size()];
	    storeFrozenDeltaArray(result, 0);
	    return result;
	}

	/**
	 * Stores the frozen delta, consisting of all the nodes and edges of the graph, 
	 * into a given element array, from a given position.
	 * The array has to be initialized to the correct length before invocation.
	 * @param result the array into which the frozen delta is to be stored
	 * @param prefixSize the index of the first element to store the frozen delta
	 */
	protected void storeFrozenDeltaArray(Element[] result, int prefixSize) {
	    int deltaIndex = prefixSize;
	    for (Node node: nodeSet()) {
	        result[deltaIndex] = node;
	        deltaIndex++;
	    }
	    for (Edge edge: edgeSet()) {
	        result[deltaIndex] = edge;
	        deltaIndex++;
	    }
	}

	/**
	 * Goes through the delta associated with the graph and calls the method of
	 * the given {@link DeltaTarget} with the appropriate elements of
	 * the delta. THat is, <code>action.added(elem)</code> is called for each
	 * element <code>elem</code> added in the delta, and
	 * <code>action.removed(elem)</code> for each removed element.
	 * @param target
	 *            the action object to be called back
	 * @param mode
	 *            indication if callback should only be done only for edges ({@link DeltaApplier#EDGES_ONLY}),
	 *            only for nodes ({@link DeltaApplier#NODES_ONLY}) or for all elements ({@link DeltaApplier#ALL_ELEMENTS})
	 */
	public void applyDelta(DeltaTarget target, int mode) {
		applyDelta(new FilteredDeltaTarget(target, mode));
	}
	
	/**
	 * Convenience method for <code>applyDelta(target, All_ELEMENTS)</code>.
	 */
	public void applyDelta(DeltaTarget target) {
		assert isFixed() : "Unfixed delta graph should not be asked to process delta";
		// go over the (previously computed) graph's delta
		Element[] delta = getDeltaArray();
		int deltaSize = getDeltaSize();
		int deltaIndex = 0;
		// all elements before null are to be added
		while (deltaIndex < deltaSize && delta[deltaIndex] != null) {
			Element elem = delta[deltaIndex];
			if (elem instanceof Node) {
				target.addNode((Node) delta[deltaIndex]);
			} else {
				target.addEdge((Edge) delta[deltaIndex]);
			}
			deltaIndex++;
		}
		// skip the null (if there was any)
		deltaIndex++;
		// all elements after null are to be removed
		while (deltaIndex < deltaSize) {
			Element elem = delta[deltaIndex];
			if (elem instanceof Node) {
				target.removeNode((Node) delta[deltaIndex]);
			} else {
				target.removeEdge((Edge) delta[deltaIndex]);
			}
			deltaIndex++;
		}
	}

	/**
	 * After fixing the graph, contains the changes w.r.t. <tt>basis</tt>, in
	 * the form of a series of elements that have been added followed by a
	 * series of elements that have been removed. The index first element that
	 * has been removed is stored in <tt>removedIndex</tt>
	 */
    private Element[] deltaArray = null;
    /**
	 * The basis graph, with respect to which the delta is calculated.
	 */
    private AbstractGraph basis;

    private static int SET_FIXED = reporter.newMethod("setFixed()");

    /** The number of delta caches reconstructed. */
    private static int cacheReconstructCount;

    /** The total number of delta elements created. */
    protected static int deltaElementCount;

    /** The total number of delta graphs. */
    private static int deltaGraphCount;

    /** The total number of delta graphs fixed. */
    private static int fixedDeltaGraphCount;

    /** The total number of delta graphs frozen. */
    protected static int frozenDeltaGraphCount;

    /** Returns the total number of frozen delta graphs. */
    static public int getDeltaGraphCount() {
        return deltaGraphCount;
    }

    /** Returns the total number of frozen delta graphs. */
    static public int getFixedDeltaGraphCount() {
        return fixedDeltaGraphCount;
    }

    /** Returns the total number of fixed delta graphs. */
    static public int getFrozenDeltaGraphCount() {
        return frozenDeltaGraphCount;
    }

    /** Returns the fraction of frozen to fixed delta graphs. */
    static public double getFrozenFraction() {
        return frozenDeltaGraphCount / (double) fixedDeltaGraphCount;
    }

    /** Returns the total number of delta caches reconstructed. */
    static public int getCacheReconstructCount() {
        return cacheReconstructCount;
    }
}