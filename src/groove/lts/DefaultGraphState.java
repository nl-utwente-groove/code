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
 * $Id: DefaultGraphState.java,v 1.1.1.2 2007-03-20 10:42:51 kastenberg Exp $
 */
package groove.lts;

import groove.graph.DeltaGraph;
import groove.graph.Element;
import groove.graph.GraphShapeCache;
import groove.graph.NodeEdgeMap;
import groove.graph.Graph;
import groove.graph.GraphCache;
import groove.graph.Node;
import groove.trans.RuleApplication;
import groove.trans.RuleEvent;
import groove.util.ArrayIterator;
import groove.util.TransformCollection;
import groove.util.TransformIterator;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Combination of graph and node functionality, used to store the state of a graph transition
 * system.
 * 
 * @author Arend Rensink
 * @version $Revision: 1.1.1.2 $ $Date: 2007-03-20 10:42:51 $
 */
public class DefaultGraphState extends DeltaGraph implements GraphState {
	/**
	 * Default constant null reference for open states.
	 */
	static private final SoftReference<DefaultStateCache> OPEN_NULL_REFERENCE = new SoftReference<DefaultStateCache>(null);
	/**
	 * Default constant null reference for closed states.
	 */
	static private final SoftReference<DefaultStateCache> CLOSED_NULL_REFERENCE = new SoftReference<DefaultStateCache>(null);
//    /**
//     * Constructs a protytpe object of this class, to be used as a factory for new (graph) states
//     * and (graph state) nodes.
//     * 
//     * @return a prototype <tt>Node</tt> instance, only intended to be used for its
//     *         <tt>newNode()</tt> or <tt>newState(Graph)</tt> methods.
//     */
//    static public State getPrototype() {
//        return new DefaultGraphState();
//    }
    
    /**
     * Interface for objects that can be closed in some sense.
     */
    static protected interface Closeable {
    	/** Tests if the object is currently closed. */
        public boolean isClosed();
        /**
         * Sets the state of the object to closed.
         */
        public void setClosed();
    }
    
    /**
     * Cache reference class that adds the functionality of {@link Closeable}.
     */
    protected class StateCacheReference<R extends DefaultStateCache> extends CacheReference<R> implements Closeable {
    	/** Creates a new reference, which is closed if the underlying graph is. */
        public StateCacheReference(R referent) {
            super(referent);
            this.closed = DefaultGraphState.this.isClosed();
        }

        public boolean isClosed() {
        	return closed;
        }

        public void setClosed() {
        	closed = true;
        }
        
        /**
         * Flag indicating if the state having this reference is closed.
         */
        private boolean closed;
    }

    /**
     * Constructs a new, empty graph state with a fresh number.
     * 
     * @ensure isEmpty()
     */
    @Deprecated
    public DefaultGraphState() {
        nr = stateCount++;
    }

    /**
     * Constructs a graph state on the basis of a given graph. The graph is aliased.
     * @param graph the graph on the basis of which this state is constructed
     * @require <tt>graph != null && isFixed(graph)</tt>
     * @ensure <tt>equals(graph)</tt>
     */
    public DefaultGraphState(Graph graph) {
        super(graph);
        nr = stateCount++;
    }
    
    public Graph getGraph() {
        return this;
    }

    /* (non-Javadoc)
     * @see groove.lts.GraphState#getOutTransitionIter()
     */
    public Iterator<GraphTransition> getTransitionIter() {
        // the iterator is created as a transformation of the iterator on the
        // stored OutGraphTransitions.
        return new TransformIterator<GraphOutTransition,GraphTransition>(getOutTransitionIter()) {
            public GraphTransition toOuter(GraphOutTransition obj) {
                return obj.createTransition(DefaultGraphState.this);
            }
        };
    }

    /* (non-Javadoc)
     * @see groove.lts.GraphState#getOutTransitionSet()
     */
    public Collection<GraphTransition> getTransitionSet() {
        return new TransformCollection<GraphOutTransition,GraphTransition>(getOutTransitionSet()) {
            public GraphTransition toOuter(GraphOutTransition obj) {
                return obj.createTransition(DefaultGraphState.this);
            }
            
            public boolean contains(Object obj) {
                if (obj instanceof GraphTransition) {
                    return containsTransition((GraphTransition) obj);
                } else {
                    return false;
                }
            }
        };
    }

    /* (non-Javadoc)
     * @see groove.lts.GraphState#getNextStateSet()
     */
    public Collection<GraphState> getNextStateSet() {
        Collection<GraphState> result = new HashSet<GraphState>();
        Iterator<GraphOutTransition> outTransitionIter = getRawOutTransitionIter();
        while (outTransitionIter.hasNext()) {
            GraphOutTransition semiTransition = outTransitionIter.next();
            result.add(semiTransition.target());
        }
        return result;
    }

    /* (non-Javadoc)
     * @see groove.lts.GraphState#getNextStateIter()
     */
    public Iterator<GraphState> getNextStateIter() {
        return new TransformIterator<GraphOutTransition,GraphState>(getRawOutTransitionIter()) {
            public GraphState toOuter(GraphOutTransition obj) {
                return obj.target();
            }
        };
    }

    /* (non-Javadoc)
     * @see groove.lts.GraphState#containsOutTransition(groove.lts.GraphTransition)
     */
    public boolean containsTransition(GraphTransition transition) {
        return transition.source().equals(this) && getOutTransitionSet().contains(
                new DefaultGraphOutTransition(transition.getEvent(), transition.target()));
    }

    public GraphState getNextState(RuleEvent event) {
        reporter.start(GET_PRIMED);
        assert event != null;
        GraphState result = null;
        if (isClosed()) {
            Element[] deltaArray = getDeltaArray();
            for (int i = getDeltaSize(); result == null && i < deltaArray.length; i++) {
                GraphOutTransition trans = (GraphOutTransition) deltaArray[i];
                if (getEvent(trans) == event) {
                    result = trans.target();
                }
            }
        } else {
            Iterator<GraphOutTransition> outTransIter = getOutTransitionIter();
            while (result == null && outTransIter.hasNext()) {
                GraphOutTransition trans = outTransIter.next();
                if (getEvent(trans) == event) {
                    result = trans.target();
                }
            }
        }
        reporter.stop();
        return result;
    }
    
    /**
     * Callback method to retrieve the event from an outgoing transition.
     */
    protected RuleEvent getEvent(GraphOutTransition trans) {
    	return trans.getEvent();
    }
    
    // ----------------------- commands -----------------------------
    
    /**
     * Add an outgoing transition to this state, if it is not yet there.
     * Returns the {@link GraphTransition} that was added, or <code>null</code>
     * if no new transition was added.
     */
    public GraphOutTransition addOutTransition(RuleApplication appl, GraphState target) {
        GraphOutTransition outTransition = createOutTransition(appl, target);
//        if (semiTransitions == Collections.EMPTY_SET) {
//            semiTransitions = new HashSet();
//        }
        if (getOutTransitionSet().add(outTransition)) {
            return outTransition;
        } else {
            return null;
        }
    }

    public boolean isClosed() {
        // we have put the knowledge about the state of the graph into the
    	// reference object.
    	Reference<?> cacheReference = getCacheReference();
    	if (cacheReference instanceof Closeable) {
    		return ((Closeable)cacheReference).isClosed();
    	} else {
    		return cacheReference == CLOSED_NULL_REFERENCE;
    	}
    }

    // -------------------- groove.graph.Element methods -----------

    // ----------------------- commands -----------------------------
    
    /* (non-Javadoc)
     * @see groove.lts.GraphState#setClosed()
     */
    public boolean setClosed() {
        if (!isClosed()) {
            storeOutTransitionSet();
            ((Closeable) getCacheReference()).setClosed();
            // this is a point where clearing the cache might be worth considering,
            // on the assumption that the state is not going to be revisited soon
//          ((DefaultStateCache) getCache()).clearOutTransitionSet();
            if (!isFrozen()) {
            	clearCache();
            }
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * This implementation clears the cache upon disposal.
     */
    public void dispose() {
        clearCache();
    }

    public Element imageFor(NodeEdgeMap elementMap) {
        throw new UnsupportedOperationException(
                "Mappings between transition systems are currently not supported");
    }

    // ----------------------- OBJECT OVERRIDES -------------------

    /**
     * Returns a clone of this state.
     * 
     * @return a clone of this state
     * @ensure <tt>result != null</tt>
     */
    @Deprecated
    public State cloneState() {
        return clone();
    }

    /**
     * Returns a clone of this state.
     * 
     * @return a clone of this state
     * @ensure <tt>result != null</tt>
     */
    public DefaultGraphState clone() {
        return new DefaultGraphState(this);
    }

    /**
     * This implementation compares state numbers.
     * The current state is either compared with the other, if that is a {@link DefaultGraphState},
     * or with its source state if it is a {@link DefaultGraphTransition}.
     * Otherwise, the method throws an {@link UnsupportedOperationException}.
     */
    public int compareTo(Element obj) {
        if (obj instanceof DefaultGraphState) {
            return nr - ((DefaultGraphState) obj).nr;
        } else if (obj instanceof DefaultGraphTransition) {
            return getStateNumber() - ((DefaultGraphTransition) obj).source().getStateNumber();
        } else {
            throw new UnsupportedOperationException(String.format("Classes %s and %s cannot be compared", getClass(), obj.getClass()));
        }
    }

    @Deprecated
    public Node newNode() {
        return newState();
    }

    @Deprecated
    public State newState() {
        return new DefaultGraphState();
    }

    //
    // public GraphState newGraphState(Graph graph) {
    // return new GraphState(graph);
    // }

    // ------------------------------ Object overrides -----------------------------------

    /** Returns the number of this <tt>GraphState</tt>. */
    public int hashCode() {
        return nr;
    }

    // ----------------------- commands -----------------------------

    /**
     * Two <tt>GraphState</tt> s are equal if they have the same number.
     */
    public boolean equals(Object obj) {
        return (obj instanceof DefaultGraphState) && nr == ((DefaultGraphState) obj).nr;
    }

    /**
     * Returns a name for this state, rather than a full description. To get the full description,
     * use <tt>DefaultGraph.toString(Graph)</tt>.
     * 
     * @see groove.graph.AbstractGraph#toString(groove.graph.GraphShape)
     */
    public String toString() {
        return "s" + nr;
    }

	/**
     * This implementation returns a {@link DefaultStateCache}.
     */
    protected GraphCache createCache() {
        return new DefaultStateCache(this);
    }

    /**
     * Before clearing the cache, stores the transitions from the
     * cache into the delta array, if the state is not slready closed.
	 */
	@Override
	public void clearCache() {
		if (!isClosed()) {
			storeOutTransitionSet();
		}
		super.clearCache();
	}

	/**
     * This implementation returns a {@link StateCacheReference}.
     */
    protected StateCacheReference<? extends DefaultStateCache> createCacheReference(GraphShapeCache referent) {
    	return new StateCacheReference<DefaultStateCache>((DefaultStateCache) referent);
	}

	/**
     * This implementation calls {@link #createNullReference(boolean)} with
     * the current (open or closed) state of the graph as a parameter, as
     * indicated by {@link #isClosed()}.
     */
    final protected Reference<? extends DefaultStateCache> createNullReference() {
    	return createNullReference(isClosed());
    }

    /**
     * This implementation also takes into account any transitions
     * at the end of the delta array, in case the state is already frozen.
     */
    @Override
	protected Element[] computeFrozenDeltaArray() {
    	// make sure there is enough room for the transitions
    	int deltaSize = getDeltaSize();
    	int transitionCount = getDeltaArray().length - deltaSize;
    	int graphSize = size();
		Element[] result = new Element[graphSize + transitionCount];
		storeFrozenDeltaArray(result, 0);
		// store the transitions
		System.arraycopy(getDeltaArray(), deltaSize, result, graphSize, transitionCount);
		return result;
	}

	/**
	 * This implementation returns the {@link #OPEN_NULL_REFERENCE} or
	 * {@link #CLOSED_NULL_REFERENCE}, depending on the parameter.
	 */
    protected Reference<? extends DefaultStateCache> createNullReference(boolean closed) {
    	if (closed) {
    		return CLOSED_NULL_REFERENCE;
    	} else {
    		return OPEN_NULL_REFERENCE;
    	}
    }
    
    /**
     * Callback factory method for creating an outgoing transition (from this state) for the given
     * derivation and target state.
     * This implementation invokes {@link #createOutTransitionTo(RuleApplication)} if the target is 
     * a {@link DefaultGraphState}, otherwise it creates a {@link DefaultGraphOutTransition}.
     */
    protected GraphOutTransition createOutTransition(RuleApplication appl, GraphState target) {
        if (target instanceof DefaultGraphState) {
            return ((DefaultGraphState) target).createOutTransitionTo(appl);
        } else {
            return new DefaultGraphOutTransition(appl.getEvent(), target);
        }
    }
    
    /**
     * Callback factory method for creating a semi-transition to this state,
     * on the basis of a given derivation.
     */
    protected GraphOutTransition createOutTransitionTo(RuleApplication appl) {
        return createOutTransitionTo(appl.getEvent());
    }
    
    /**
     * Callback factory method to create a {@link groove.lts.GraphOutTransition} with
     * this state as a target, based on a given event.
     */
    protected GraphOutTransition createOutTransitionTo(RuleEvent event) {
    	return new DefaultGraphOutTransition(event, this);
    }

    /**
     * Returns the number of this state.
     * The number is guaranteed to be unique for each state in a given transition system.
     */
    protected int getStateNumber() {
        return nr;
    }

    /**
     * Returns a list view upon the current outgoing transitions.
     */
    public Iterator<GraphOutTransition> getOutTransitionIter() {
    	return getRawOutTransitionIter();
    }

    /**
	 * Returns an iterator over the outgoing transitions as stored,
	 * i.e., without encodings taken into account.
	 */
	final protected Iterator<GraphOutTransition> getRawOutTransitionIter() {
        if (isClosed()) {
            return getStoredOutTransitionIter();
        } else {
            return getOutTransitionSet().iterator();
        }
	}

    /**
	 * Returns an iterator over the outgoing transitions as stored in the delta array.
	 */
	final protected Iterator<GraphOutTransition> getStoredOutTransitionIter() {
		return new ArrayIterator<GraphOutTransition>(getDeltaArray(), getDeltaSize());
	}

	/**
     * Returns the position in the graph's delta from where to start looking for
     * outgoing transitions rather than true delta elements.
     */
    protected int getDeltaSize() {
    	Element[] delta = getDeltaArray();
    	int result = delta.length;
    	while (result > 0 && !isTrueDeltaElement(delta[result-1])) {
    		result--;
    	}
    	return result;
    }
    
    /**
     * Returns a list view upon the current outgoing transitions.
     */
    private Collection<GraphOutTransition> getOutTransitionSet() {
    	return ((DefaultStateCache) getCache()).getOutTransitionSet();
    }

    /**
     * Stores the outgoing transitions in a memory efficient way.
     */
    private void storeOutTransitionSet() {
    	Collection<GraphOutTransition> outTransitionSet = getOutTransitionSet();
    	Element[] oldDelta = getDeltaArray();
    	int oldDeltaSize = getDeltaSize();
    	Element[] newDelta = new Element[oldDeltaSize + outTransitionSet.size()];
    	System.arraycopy(oldDelta, 0, newDelta, 0, oldDeltaSize);
    	int index = oldDeltaSize;
    	for (Element semiTransition: outTransitionSet) {
			newDelta[index] = semiTransition;
			index++;
		}
    	setDeltaArray(newDelta);
    	((DefaultStateCache) getCache()).clearOutTransitionSet();
    }
    
    /** Indicates if this graph currently stores any transitions. */
    boolean storesOutTransition() {
    	Element[] delta = getDeltaArray();
    	return delta.length > 0 && ! isTrueDeltaElement(delta[delta.length-1]);
    }
    
    /**
     * Tests if a given element (presumably from the delta) is a true delta element;
     * if not, then it is used for something else, such as storing an outgoing transition.
     * @param elem the element to be tested
     * @return <code>true</code> if <code>elem</code> is part of the true delta.
     */
    protected boolean isTrueDeltaElement(Element elem) {
    	return !(elem instanceof GraphOutTransition);
    }
    
    /**
     * The number of this Node.
     * 
     * @invariant nr < nrNodes
     */
    private final int nr;

    /**
     * The number of DefaultStates constructed.
     * 
     * @invariant nrNodes >= 0
     */
    private static int stateCount;

    /** Profiles the {@link #getNextState(RuleEvent)} method. */
    protected static int GET_PRIMED = reporter.newMethod("getPrimedOutTransition"); 
}