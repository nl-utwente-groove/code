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
package groove.lts;

import groove.graph.AbstractEdge;
import groove.graph.AbstractGraph;
import groove.graph.DeltaGraph;
import groove.graph.DeltaTarget;
import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.Graph;
import groove.graph.GraphCache;
import groove.graph.Label;
import groove.graph.Morphism;
import groove.graph.Node;
import groove.graph.NodeEdgeMap;
import groove.trans.Rule;
import groove.trans.RuleApplication;
import groove.trans.RuleEvent;
import groove.util.CacheReference;
import groove.util.TransformIterator;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Class that combines state and incoming transition information.
 * The rule is stored in the state and the anchor images are added to the delta.
 * @author Arend
 * @version $Revision: 1.6 $
 */
public class DerivedGraphState extends DefaultGraphState implements GraphNextState {
    /**
     * Bound above which the suggestion to clear the cache is not taken.
     */
    public static final int CLEAR_UPPER_BOUND = 4;
//    /**
//     * Bound below which the suggestion to clear the cache is not taken.
//     * @see #scheduleClearCache()
//     */
//    public static final int CLEAR_LOWER_BOUND = 2;

    static {
        AbstractEdge.setMaxEndCount(END_COUNT);
    }
//
//    /**
//     * Interface for graph cache references that offers the functionality
//     * to retrieve the incarnation count of the cache.
//     */
//    static protected interface Counter {
//        /** Returns the incarnation count of the referent. */
//        public int getCount();
//    }
//    
//    /**
//     * Class used for shared <code>null</code> references that
//     * additionally record the incarnation count.
//     */
//    static protected class CountingNullReference extends SoftReference<DerivedStateCache> implements Counter, Closeable {
//    	/** Creates a new null reference, for a given incarnation count and closure information. */
//        public CountingNullReference(int count, boolean closed) {
//            super(null);
//            this.count = count;
//            this.closed = closed;
//        }
//        
//        public int getCount() {
//            return count;
//        }
//
//        public void setClosed() {
//        	throw new UnsupportedOperationException();
//        }
//        
//        public boolean isClosed() {
//        	return closed;
//        }
//        
//        /**
//         * The incarnation count of this cache.
//         */
//        private final int count;
//        /**
//         * Flag indicating if the state having this reference is closed.
//         */
//        private final boolean closed;
//    }
//    
//    /**
//     * Cache reference that additionally records the incarnation count.
//     */
//    protected class CountingCacheReference extends StateCacheReference<DerivedStateCache> implements Counter {
//        /**
//         * Constructs a reference, and sets the incarnation count by increasing
//         * the count of the current (presumably <code>null</code>) cache reference.
//         * @param referent
//         */
//        protected CountingCacheReference(DerivedStateCache referent) {
//            super(referent);
//            Reference<?> currentCacheReference = getCacheReference();
//            if (currentCacheReference instanceof Counter) {
//                count = ((Counter) currentCacheReference).getCount()+1;
//                if (CACHE_SCHEDULE_DEBUG) {
//                    System.err.println(DerivedGraphState.this+": Cache reconstruction #"+count+", depth "+getDepth());
//                }
//            } else {
//                count = 0;
//            }
//            incIncarnationSize(count);
//        }
//        
//        public int getCount() {
//            return count;
//        }
//
//        /** Cache incarnation count of this reference. */
//        private final int count;
//    }
//    /**
//     * Array of open {@link CountingNullReference}s for different incarnation counts.
//     */
//    static private CountingNullReference[] openNullReferences;
//    /**
//     * Array of closed {@link CountingNullReference}s for different incarnation counts.
//     */
//    static private CountingNullReference[] closedNullReferences;
//    /**
//     * Array of frequency counters for each incarnation count. 
//     */
//    static private int[] incarnationSize;
//    /**
//     * Global counter of the total number of cache reincarnations.
//     */
//    static private int reincarnationSize;
//    /**
//     * The length of the {@link #openNullReferences} array.
//     */
//    static private int countSize;
//    /**
//     * Initial size of the {@link #openNullReferences} array.
//     */
//    static private int INIT_COUNT_SIZE = 10;
//    
//    static {
//        fillNullReferences();
//    }
//
//	/**
//     * Returns a {@link SoftReference} to an {@link Integer} object that
//     * can be used (for instance) to store information about a cache when
//     * the cache itself is cleared.
//     * The {@link Integer} object is shared to save memory usage,
//     * and a hard reference to it is maintained internally to ensure that
//     * the reference is never cleared.  
//     * @param count the value to be stored in the referenced {@link Integer}
//     * @return a reference to an {@link Integer} with value <code>count</code>,
//     * guaranteed never to be cleared
//     */
//    static protected Reference<DerivedStateCache> getNullReference(int count, boolean closed) {
//        if (count+1 >= countSize) {
//            fillNullReferences();
//        }
//        if (closed) {
//            return closedNullReferences[count+1];
//        } else {
//            return openNullReferences[count+1];
//        }
//    }
//    
//    /**
//     * Fills out the arrays of count objects, references or frequency.
//     */
//    static protected void fillNullReferences() {
//        int oldSize = countSize;
//        countSize = (oldSize == 0) ? INIT_COUNT_SIZE : oldSize*2;
//        Reference[] oldOpenNullReferences = openNullReferences;
//        Reference[] oldClosedNullReferences = closedNullReferences;
//        int[] oldCountFrequency = incarnationSize;
//        openNullReferences = new CountingNullReference[countSize];
//        closedNullReferences = new CountingNullReference[countSize];
//        incarnationSize = new int[countSize];
//        if (oldSize > 0) {
//            System.arraycopy(oldOpenNullReferences, 0, openNullReferences, 0, oldSize);
//            System.arraycopy(oldClosedNullReferences, 0, closedNullReferences, 0, oldSize);
//            System.arraycopy(oldCountFrequency, 0, incarnationSize, 0, oldSize);
//        }
//        for (int i = oldSize; i < countSize; i++) {
//            openNullReferences[i] = new CountingNullReference(i-1, false);
//            closedNullReferences[i] = new CountingNullReference(i-1, true);
//        }
//    }

    /**
	 * Constructs a state on the basis of a given source state, rule event and coanchor image.
     * @param source the source state of this derived state
     * @param event the rule event leading from the source state to this state
     * @param coanchorImage the fresh nodes created by the rule application
	 */
	public DerivedGraphState(GraphState source, RuleEvent event, Element[] coanchorImage) {
		super(source.getGraph());
		this.event = event;
		setCoanchorImage(coanchorImage);
	}
	
	/**
	 * Return the rule of the incoming transition with which this state
	 * was created.
	 */
	public Rule getRule() {
		return getEvent().getRule(); 
	}
	
	public RuleEvent getEvent() {
		return event;
	}
	
	/**
	 * This implementation reconstructs the matching using the
	 * rule, the anchor images, and the basis graph.
	 * @see #getRule()
	 * @see RuleEvent#getMatching(Graph)
	 */
	public Morphism matching() {
    	return getEvent().getMatching(getBasis());
	}
    
    /**
     * Constructs an underlying morphism for the transition from the stored footprint.
     */
    public Morphism morphism() {
        RuleApplication appl = getEvent().newApplication(source().getGraph());
        Graph derivedTarget = appl.getTarget();
        Graph realTarget = target().getGraph();
        if (derivedTarget.edgeSet().equals(realTarget.edgeSet())
                && derivedTarget.nodeSet().equals(realTarget.nodeSet())) {
            return appl.getMorphism();
        } else {
            Morphism iso = derivedTarget.getIsomorphismTo(realTarget);
            assert iso != null : "Can't reconstruct derivation from graph transition " + this
                    + ": \n" + AbstractGraph.toString(derivedTarget) + " and \n"
                    + AbstractGraph.toString(realTarget) + " \nnot isomorphic";
            return appl.getMorphism().then(iso);
        }
    }

	/**
	 * This implementation returns the rule name.
	 */
	public Label label() {
        return getEvent().getLabel();
//        if (DefaultGraphTransition.isRuleLabelled()) {
//            return getRule().getName();
//        } else {
//            return getEvent().getLabel();
//        }
	}

	/**
	 * This implementation returns <code>this</code>.
	 */
	public GraphState target() {
		return this;
	}

	/**
	 * Returns <code>getBasis()</code> or <code>this</code>, depending on the index.
	 */
	public Node end(int i) {
		switch (i) {
		case SOURCE_INDEX : return source();
		case TARGET_INDEX : return target();
		default : throw new IllegalArgumentException("End index "+i+" not valid");
		}
	}

	/**
	 * @return {@link #END_COUNT}.
	 */
	public int endCount() {
		return END_COUNT;
	}

	public int endIndex(Node node) {
		if (source().equals(node)) {
			return SOURCE_INDEX;
		} else if (target().equals(node)) {
			return TARGET_INDEX;
		} else {
			throw new IllegalArgumentException("Node "+node+" is not an end state of this transition");
		}
	}

	public Node[] ends() {
		return new Node[] { source(), target() };
	}

	public boolean hasEnd(Node node) {
		return source().equals(node) || target().equals(node);
	}

	public Node opposite() {
		return target();
	}

	/**
	 * Returns the basis graph of the delta graph (which is guaranteed to be 
	 * a {@link GraphState}).
	 */
	public GraphState source() {
		return (GraphState) getBasis();
	}
	
	/**
	 * Has to be included to have a correct return type.
	 */
	@Override
	public DerivedGraphState imageFor(NodeEdgeMap elementMap) {
		throw new UnsupportedOperationException();
	}

    /**
     * This implementation retrieves the coanchor image from the delta array.
     */
    public Element[] getCoanchorImage() {
    	return getDeltaArray();
    }
    
    /** 
     * Sets the coanchor image for this state.
     * The parameter passed in may contain more than just the coanchor image
     * (for instance, it may have outgoing transitions attached); if this 
     * is the case, we copy just the necessary prefix, otherwise we alias the parameter.
     * @param image from index <code>0</code> up to {@link #getCoanchorSize()}, this
     * contains the coanchor image; any further elements are spurious (for us)
     */
    public void setCoanchorImage(Element[] image) {
    	int coanchorSize = getCoanchorSize();
    	if (image.length > coanchorSize) {
    		// just copy the prefix
    		Element[] copy = new Element[coanchorSize];
    		System.arraycopy(image, 0, copy, 0, coanchorSize);
    		setDeltaArray(copy);
    	} else {
    		setDeltaArray(image);
    	}
    }

	/**
	 * This implementation asks the rule for the footprint size.
	 */
    @Override
    protected int getDeltaSize() {
    	if (isFrozen()) {
    		return super.getDeltaSize();
    	} else {
    		return getCoanchorSize();
    	}
	}
    
    /** Convenience method to return the size of the rule's coanchor. */
    protected int getCoanchorSize() {
    	return getRule().coanchor().length;
    }

	/**
     * This implementation throws an {@link IllegalArgumentException} if
     * <code>source</code> is not equal to the source of the transition,
     * otherwise it returns <code>this</code>.
	 */
	public GraphTransition createTransition(GraphState source) {
		if (source != source()) {
			RuleEvent event = getSourceEvent();
			return new DefaultGraphTransition(event, source, this);
		} else {
			return this;
		}
	}
	
	/**
	 * Returns the event from the source of this transition,
	 * if that is itself a {@link groove.lts.GraphOutTransition}.
	 */
	protected RuleEvent getSourceEvent() {
		if (source() instanceof GraphOutTransition) {
			return ((GraphOutTransition) source()).getEvent();
		} else {
			return null;
		}
	}
	
    /**
     * This implementation compares the event identities.
     * Callback method from {@link #equals(Object)}.
     */
    protected boolean equalsEvent(GraphOutTransition other) {
        return getEvent() == other.getEvent();
    }

    /**
     * This implementation compares the source graph identities.
     * Callback method from {@link #equals(Object)}.
     */
    protected boolean equalsSource(GraphOutTransition other) {
        return !(other instanceof DerivedGraphState) || source() == ((DerivedGraphState) other).source();
    }
    
    /**
     * This implementation compares the state on the basis of its qualities as
     * an outgoing transition of its basis.
     * That is, two objects are considered equal if they have the same basis,
     * rule and anchor images.
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof GraphOutTransition && equalsEvent((GraphOutTransition) obj) && equalsSource((GraphOutTransition) obj);
    }

    /**
     * This implementation combines the identities of source and event.
     */
    @Override
    public int hashCode() {
        return System.identityHashCode(source()) + System.identityHashCode(getEvent());
    }

    /**
     * Returns the incarnation count for the graph cache. The incarnation count is used as a parameter
     * in a policy to force early cache clearance, so as to save time on garbage collection.
     */
    public int getCacheIncarnationCount() {
        CacheReference cacheReference = getCacheReference();
        if (cacheReference == null) {
        	return -1;
        } else {
            return cacheReference.getIncarnation();
        }
    }
    
    /**
     * This implementation returns a {@link DerivedStateCache}.
     */
    @Override
	protected GraphCache createCache() {
	    return new DerivedStateCache(this);
    }
//    
//    /**
//     * This implementation returns a {@link CountingCacheReference}.
//     */
//    @Override
//    protected StateCacheReference<? extends DerivedStateCache> createCacheReference(GraphShapeCache referent) {
//        return new CountingCacheReference((DerivedStateCache) referent);
//    }
//    
//    /**
//     * This implementation returns a {@link CountingNullReference}.
//     * @see #getNullReference(int,boolean)
//     */
//    @Override
//    protected Reference<DerivedStateCache> createNullReference(boolean closed) {
//        return getNullReference(getCacheIncarnationCount(), closed);
//    }

    /**
     * This implementation transforms the outgoing transitions from
     * their raw format to the proper representation as a {@link groove.lts.GraphOutTransition}
     * from the current state.
     */
    @Override
    public Iterator<GraphOutTransition> getOutTransitionIter() {
		return new TransformIterator<GraphOutTransition,GraphOutTransition>(getRawOutTransitionIter()) {
		    @Override
			protected GraphOutTransition toOuter(GraphOutTransition inner) {
				if (inner instanceof DerivedGraphState) {
					return ((DerivedGraphState) inner).createOutTransitionTo(DerivedGraphState.this);
				} else {
					return inner;
				}
			}
		};
	}
    
    /**
     * Creates an outgoing transition starting in a given source state,
     * based on the transformation information of this state.
     */
    protected GraphOutTransition createOutTransitionTo(DerivedGraphState source) {
    	if (source != source()) {
    		return createOutTransitionToThis(getSourceEvent());
    	} else {
    		return this;
    	}
    }

    /**
	 * This implementation returns <code>this</code> if the derivation's event
	 * is identical to the event stored in this state.
	 * Otherwise it invokes <code>super</code>.
	 */
    @Override
	protected GraphOutTransition createOutTransitionToThis(GraphState source, RuleEvent event) {
	    if (source == source() && event == getEvent()) {
	        return this;
	    } else if (source != source() && event == getSourceEvent()) {
			return this;
		} else {
			return createOutTransitionToThis(event);
		}
	}

	/**
	 * This implementation takes into account that an outgoing transition may
	 * actually be an alias to some other transition that forms the outer end of
	 * a confluent diamond with this one.
	 */
    @Override
	protected RuleEvent getEvent(GraphOutTransition trans) {
		if (trans instanceof DerivedGraphState && ((DerivedGraphState)trans).source() != this) {
			return ((DerivedGraphState) trans).getSourceEvent();
		} else {
			return super.getEvent(trans);
		}
	}

	/**
     * This implementation does nothing: reversing the basis
     * is not an option for derived states.
     */
    @Override
    protected synchronized void invertBasis(DeltaGraph newBasis, Element[] reverseDelta) {
        // does nothing
    }

    /**
     * This implementation returns <code>true</code> if the size of the delta
     * exceeds the size of the rule's footprint.
     */
    @Override
    protected boolean isFrozen() {
    	if (isFixed()) {
    		Element[] delta = getDeltaArray();
            return delta.length > getCoanchorSize() && isTrueDeltaElement(delta[getCoanchorSize()]);
    	} else {
    		return false;
    	}
    }

    @Override
    protected void invertBasis() {
    	// don't invert the basis
    }

    /**
	 * This implementation just returns the current delta array,
	 * which was initialized at construction time to contain the coanchor image.
	 */
    @Override
	protected Element[] computeFixedDeltaArray() {
		return getDeltaArray();
	}

	/**
	 * The frozen delta consists of the coanchor images followed by a blank element and then the 
	 * nodes and edges of the graph. 
	 */
    @Override
	protected Element[] computeFrozenDeltaArray() {
		assert !isFrozen();
	    int frozenDeltaSize = size();
	    int coanchorSize = getCoanchorSize();
	    Element[] deltaArray = getDeltaArray();
	    assert getDeltaSize() == coanchorSize : "Misformatted delta array "+Arrays.toString(deltaArray);
	    assert deltaArray.length >= coanchorSize : "Misformatted delta array "+Arrays.toString(deltaArray);
	    Element[] result = new Element[deltaArray.length + frozenDeltaSize];
	    // copy the coanchor image
	    System.arraycopy(deltaArray, 0, result, 0, coanchorSize);
	    // now copy the frozen delta
	    storeFrozenDeltaArray(result, coanchorSize);
	    // finally, copy the already derived outgoing transitions
	    int transitionCount = deltaArray.length - coanchorSize;
	    System.arraycopy(deltaArray, coanchorSize, result, coanchorSize+frozenDeltaSize, transitionCount);
	    return result;
	}

	/**
	 * Goes through the delta associated with the graph and calls the method of
	 * the given {@link DeltaTarget} with the appropriate elements of the delta.
	 * THat is, <code>action.added(elem)</code> is called for each element <code>elem</code>
	 * added in the delta, and <code>action.removed(elem)</code> for each removed element.
	 * @param target the action object to be called back
	 */
    @Override
	public void applyDelta(DeltaTarget target) {
		assert isFixed() : "Unfixed delta graph should not be asked to process delta";
	    if (isFrozen()) {
	        applyFrozenDelta(target);
	    } else {
		    applyRule(target);
	    }
	}
	
	/**
	 * Applies the frozen delta to a given target.
	 * It is required that {@link #isFrozen()} holds.
	 */
	protected void applyFrozenDelta(DeltaTarget target) {
        // go over the (previously computed) graph's delta
        Element[] delta = getDeltaArray();
        int deltaSize = getDeltaSize();
        // all elements are to be added 
        for (int i = getCoanchorSize(); i < deltaSize; i++) {
        	Element elem = delta[i];
        	if (elem instanceof Node) {
        		target.addNode((Node) delta[i]);
        	} else {
        		target.addEdge((Edge) elem);
        	}
        }
	}

	/**
	 * Applies the underlying rule of this derived state to a given target.
	 */
	protected void applyRule(DeltaTarget target) {
		// if the basis graph cache is cleared before rule application, 
		// clear it again afterwards
		AbstractGraph basis = getBasis() instanceof AbstractGraph ? (AbstractGraph) getBasis() : null;
		boolean basisCacheCleared = basis != null && basis.isCacheCleared();
		// do the actual rule application
		RuleApplication applier = getEvent().newApplication(getBasis());
		applier.setCoanchorImage(getCoanchorImage());
	    applier.applyDelta(target);
	    // clear the basis cache
	    if (basisCacheCleared) {
	    	CacheReference<?> reference = basis.getCacheReference();
	    	if (reference != null && reference.getIncarnation() < CLEAR_UPPER_BOUND) {
	    		basis.clearCache();
	    	}
	    }
	}
	
	/**
	 * The rule of the incoming transition with which this state was created.
	 */
	private final RuleEvent event;
//    /** Debugging flag for the cache scheduling mechanism. */
//    private final static boolean CACHE_SCHEDULE_DEBUG = false;
}