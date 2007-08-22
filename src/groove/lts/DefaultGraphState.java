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
 * $Id: DefaultGraphState.java,v 1.12 2007-08-22 15:04:51 rensink Exp $
 */
package groove.lts;

import groove.control.Location;
import groove.graph.DeltaGraph;
import groove.graph.Element;
import groove.graph.Graph;
import groove.graph.Node;
import groove.graph.NodeEdgeMap;
import groove.trans.RuleEvent;
import groove.util.ArrayIterator;
import groove.util.TransformIterator;
import groove.util.TransformSet;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Combination of graph and node functionality, used to store the state of a graph transition
 * system.
 * 
 * @author Arend Rensink
 * @version $Revision: 1.12 $ $Date: 2007-08-22 15:04:51 $
 * @deprecated use {@link StartGraphState} or {@link DefaultGraphNextState} instead
 */
@Deprecated
public class DefaultGraphState extends DeltaGraph<DefaultStateCache> implements GraphState {
//	/**
//	 * Default constant null reference for open states.
//	 */
//	static private final SoftReference<DefaultStateCache> OPEN_NULL_REFERENCE = new SoftReference<DefaultStateCache>(null);
//	/**
//	 * Default constant null reference for closed states.
//	 */
//	static private final SoftReference<DefaultStateCache> CLOSED_NULL_REFERENCE = new SoftReference<DefaultStateCache>(null);
//    
//    /**
//     * Interface for objects that can be closed in some sense.
//     */
//    static protected interface Closeable {
//    	/** Tests if the object is currently closed. */
//        public boolean isClosed();
//        /**
//         * Sets the state of the object to closed.
//         */
//        public void setClosed();
//    }
//    
//    /**
//     * Cache reference class that adds the functionality of {@link Closeable}.
//     */
//    protected class StateCacheReference<R extends DefaultStateCache> extends CacheReference<R> implements Closeable {
//    	/** Creates a new reference, which is closed if the underlying graph is. */
//        public StateCacheReference(R referent) {
//            super(referent);
//            this.closed = DefaultGraphState.this.isClosed();
//        }
//
//        public boolean isClosed() {
//        	return closed;
//        }
//
//        public void setClosed() {
//        	closed = true;
//        }
//        
//        /**
//         * Flag indicating if the state having this reference is closed.
//         */
//        private boolean closed;
//    }

    /**
     * Constructs a new, empty graph state with a fresh number.
     * 
     * @ensure isEmpty()
     */
    @Deprecated
    public DefaultGraphState() {
        stateCount++;
    }

    /**
     * Constructs a graph state on the basis of a given graph. The graph is aliased.
     * @param graph the graph on the basis of which this state is constructed
     * @require <tt>graph != null && isFixed(graph)</tt>
     * @ensure <tt>equals(graph)</tt>
     */
    public DefaultGraphState(Graph graph) {
        super(graph);
        stateCount++;
    }
    
    public Graph getGraph() {
        return this;
    }
    
    /** This state type always returns <code>null</code> as control location. */
    public Location getControl() {
    	return null;
    }

    public Iterator<GraphTransition> getTransitionIter() {
    	if (!isCacheCleared() && getCache().isTransitionMapSet()) {
    		return getCache().getTransitionMap().values().iterator();
    	} else {
    		return new TransformIterator<GraphTransitionStub,GraphTransition>(getStoredStubIter()) {
				@Override
				protected GraphTransition toOuter(GraphTransitionStub from) {
					return from.toTransition(DefaultGraphState.this);
				}
    		};
    	}
    }

    public Set<GraphTransition> getTransitionSet() {
    	final Map<RuleEvent,GraphTransition> transitionMap = getCache().getTransitionMap();
        return new TransformSet<Map.Entry<RuleEvent,GraphTransition>,GraphTransition>(transitionMap.entrySet()) {
        	@Override
            protected GraphTransition toOuter(Map.Entry<RuleEvent,GraphTransition> obj) {
                return obj.getValue();
            }

			@Override
			public boolean contains(Object o) {
				if (o instanceof GraphTransition && ((GraphTransition) o).source() == DefaultGraphState.this) {
					return transitionMap.containsKey(((GraphTransition) o).getEvent()); 
				} else {
					return false;
				}
			}

			@Override
			public boolean remove(Object o) {
				if (o instanceof GraphTransition && ((GraphTransition) o).source() == DefaultGraphState.this) {
					return transitionMap.remove(((GraphTransition) o).getEvent()) != null; 
				} else {
					return false;
				}
			}
//            
//        	@Override
//			protected GraphTransitionStub toInner(Object key) {
//                if (key instanceof GraphTransition) {
//                	RuleEvent keyEvent = ((GraphTransition) key).getEvent();
//                	GraphState keyTarget = ((GraphTransition) key).target();
//                    return createTransitionStub(keyEvent, keyTarget);
//                } else {
//                    return null;
//                }
//			}
        };
    }

    public boolean containsTransition(GraphTransition transition) {
	    return transition.source().equals(this) && getNextState(transition.getEvent()) != null;
	}

	//    
	//    /**
	//     * Callback method to retrieve the event from an outgoing transition.
	//     */
	//    protected RuleEvent getEvent(GraphOutTransition trans) {
	//    	return trans.getEvent();
	//    }
	//    
	    // ----------------------- commands -----------------------------

	/**
	 * Add an outgoing transition to this state, if it is not yet there. Returns
	 * the {@link GraphTransition} that was added, or <code>null</code> if no
	 * new transition was added.
	 */
	public boolean addTransition(GraphTransition transition) {
		return getCache().addTransition(transition);
	}
//
//	/** Callback factory method for creating an outgoing graph transition. */
//	protected GraphTransition createTransition(RuleEvent event, GraphState target) {
//		if (target instanceof GraphTransition) {
//			GraphTransition candidate = (GraphTransition) target;
//			if (candidate.source() == this && candidate.getEvent() == event) {
//				return candidate;
//			}
//		}
//		return new DefaultGraphTransition(event, this, target, null);
//	}

	public Collection<GraphState> getNextStateSet() {
        return new TransformSet<Map.Entry<RuleEvent,GraphTransition>,GraphState>(getCache().getTransitionMap().entrySet()) {
        	@Override
            public GraphState toOuter(Map.Entry<RuleEvent,GraphTransition> entry) {
                return entry.getValue().target();
            }
        };
//        Set<GraphState> result = new HashSet<GraphState>();
//        Iterator<GraphTransitionStub> outTransitionIter = getTransitionStubIter();
//        while (outTransitionIter.hasNext()) {
//            GraphTransitionStub stub = outTransitionIter.next();
//            result.add(stub.target());
//        }
//        return result;
    }

    public Iterator<GraphState> getNextStateIter() {
    	if (isClosed()) {
			return new TransformIterator<GraphTransitionStub, GraphState>(
					getTransitionStubIter()) {
				@Override
				public GraphState toOuter(GraphTransitionStub stub) {
					return stub.target();
				}
			};
		} else {
            return new TransformIterator<GraphTransition,GraphState>(getTransitionIter()) {
            	@Override
                public GraphState toOuter(GraphTransition transition) {
                    return transition.target();
                }
            };
    	}
    }
    
    /** 
     * Returns an iterator over the transition stubs for this state.
     * If the state is closed, the cache is not (re)constructed for this purpose. 
     */
    public Iterator<GraphTransitionStub> getTransitionStubIter() {
    	if (isClosed()) {
    		return getStoredStubIter();
    	} else {
    		return new TransformIterator<GraphTransition,GraphTransitionStub>(getTransitionIter()) {
				@Override
				protected GraphTransitionStub toOuter(GraphTransition from) {
					return from.toStub();
				}
    		};
    	}
    }

    public GraphState getNextState(RuleEvent event) {
		reporter.start(GET_NEXT_STATE);
		assert event != null;
		GraphTransition transition = getCache().getTransitionMap().get(event);
		GraphState result = transition == null ? null : transition.target();
// if (isClosed()) {
//            Element[] deltaArray = getDeltaArray();
//            for (int i = getDeltaSize(); result == null && i < deltaArray.length; i++) {
//                GraphTransitionStub trans = (GraphTransitionStub) deltaArray[i];
//                if (trans.getEvent(this) == event) {
//                    result = trans.target();
//                }
//            }
//        } else {
//            Iterator<GraphTransitionStub> outTransIter = getTransitionStubIter();
//            while (result == null && outTransIter.hasNext()) {
//                GraphTransitionStub trans = outTransIter.next();
//                if (trans.getEvent(this) == event) {
//                    result = trans.target();
//                }
//            }
//        }
        reporter.stop();
        return result;
    }
//    
//    /**
//     * Callback method to retrieve the event from an outgoing transition.
//     */
//    protected RuleEvent getEvent(GraphOutTransition trans) {
//    	return trans.getEvent();
//    }
//    
    // ----------------------- commands -----------------------------
    
    @Override
    protected void registerFixed() {
    	// does nothing; the registration is already done
    	// because the delta array is fixed. We don't want to release the cache
    	// until the state is closed
    }
    
    public boolean isClosed() {
    	return ! getCacheReference().isStrong();
//        // we have put the knowledge about the state of the graph into the
//    	// reference object.
//    	Reference<?> cacheReference = getCacheReference();
//    	if (cacheReference instanceof Closeable) {
//    		return ((Closeable)cacheReference).isClosed();
//    	} else {
//    		return cacheReference == CLOSED_NULL_REFERENCE;
//    	}
    }

    // -------------------- groove.graph.Element methods -----------

    // ----------------------- commands -----------------------------
    
    /* (non-Javadoc)
     * @see groove.lts.GraphState#setClosed()
     */
    public boolean setClosed() {
        if (!isClosed()) {
            storeTransitionStubSet();
            getCacheReference().setSoft();
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
//    
//    /**
//     * This implementation clears the cache upon disposal.
//     */
//    public void dispose() {
//        clearCache();
//    }

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
     * This implementation returns a {@link DefaultGraphState}
     * @see #DefaultGraphState(Graph)
     */
    @Override
    public DefaultGraphState clone() {
//    	throw new UnsupportedOperationException(String.format("Cloning of %s not supported", this.getClass()));
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
            return getStateNumber() - ((DefaultGraphState) obj).getStateNumber();
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

    /**
     * Returns a name for this state, rather than a full description. To get the full description,
     * use <tt>DefaultGraph.toString(Graph)</tt>.
     * 
     * @see groove.graph.AbstractGraph#toString(groove.graph.GraphShape)
     */
	@Override
    public String toString() {
		if (hasStateNumber()) {
			return "s" + getStateNumber();
		} else {
			return "s??";
		}
    }

	/**
     * This implementation returns a {@link DefaultStateCache}.
     */
	@Override
    protected DefaultStateCache createCache() {
        return new DefaultStateCache(this);
    }

    /**
     * Before clearing the cache, stores the transitions from the
     * cache into the delta array, if the state is not slready closed.
	 */
	@Override
	public void clearCache() {
		if (!isClosed()) {
			storeTransitionStubSet();
		}
		super.clearCache();
	}
//
//	/**
//     * This implementation returns a {@link StateCacheReference}.
//     */
//	@Override
//    protected StateCacheReference<? extends DefaultStateCache> createCacheReference(GraphShapeCache referent) {
//    	return new StateCacheReference<DefaultStateCache>((DefaultStateCache) referent);
//	}
//
//	/**
//     * This implementation calls {@link #createNullReference(boolean)} with
//     * the current (open or closed) state of the graph as a parameter, as
//     * indicated by {@link #isClosed()}.
//     */
//	@Override
//    final protected Reference<? extends DefaultStateCache> createNullReference() {
//    	return createNullReference(isClosed());
//    }

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
    
//    /**
//     * Callback factory method for creating an outgoing transition (from this state) for the given
//     * derivation and target state.
//     * This implementation invokes {@link #createInTransitionStub(GraphState, RuleEvent)} if the target is 
//     * a {@link DefaultGraphState}, otherwise it creates a {@link IdGraphTransitionStub}.
//     */
//    protected GraphTransitionStub createTransitionStub(RuleEvent event, GraphState target) {
//        if (target instanceof DefaultGraphState) {
//            return ((DefaultGraphState) target).createInTransitionStub(this, event);
//        } else {
//            return new IdGraphTransitionStub(event, target);
//        }
//    }
//    
//    /**
//     * Callback factory method for creating a transition stub to this state,
//     * from a given graph and with a given rule event.
//     */
//    protected GraphTransitionStub createInTransitionStub(GraphState source, RuleEvent event) {
//    	return new IdGraphTransitionStub(event, this);
//    }
//    
    /** Indicates whether the state has already been assigned a number. */
    protected boolean hasStateNumber() {
    	return this.nr >= 0;
    }
    
    /**
     * Returns the number of this state.
     * The number is meant to be unique for each state in a given transition system.
     * @throws IllegalStateException if {@link #hasStateNumber()} returns <code>false</code>
     * at the time of calling
     */
    protected int getStateNumber() {
    	if (!hasStateNumber()) {
        	throw new IllegalStateException("State number not set"); 
        }
        return nr;
    }

    /**
     * Sets the state number.
     * This method should be called only once, with a non-negative number.
     * @throws IllegalStateException if {@link #hasStateNumber()} returns <code>true</code>
     * @throws IllegalArgumentException if <code>nr</code> is illegal (i.e., negative)
     */
    protected void setStateNumber(int nr) {
        if (hasStateNumber()) {
        	throw new IllegalStateException(String.format("State number already set to %s", this.nr)); 
        }
        if (nr < 0) {
        	throw new IllegalArgumentException(String.format("Illegal state number %s", nr));
        }
    	this.nr = nr;
    }
//
//    /**
//     * Returns a list view upon the current outgoing transitions.
//     */
//    public Iterator<GraphTransitionStub> getOutTransitionIter() {
//    	return getTransitionStubIter();
//    }
//
//    /**
//	 * Returns an iterator over the outgoing transitions as stored,
//	 * i.e., without encodings taken into account.
//	 */
//	final protected Iterator<GraphTransitionStub> getTransitionStubIter() {
//        if (isClosed()) {
//            return getStoredStubIter();
//        } else {
//            return getTransitionStubSet().iterator();
//        }
//	}

    /**
	 * Returns an iterator over the outgoing transitions as stored in the delta array.
	 */
	final protected Iterator<GraphTransitionStub> getStoredStubIter() {
		if (getDeltaArray() == null) {
			return null;
		} else {
			return new ArrayIterator<GraphTransitionStub>(getDeltaArray(), getDeltaSize());
		}
	}

	/**
     * Returns the position in the graph's delta from where to start looking for
     * outgoing transitions rather than true delta elements.
     */
	@Override
    protected int getDeltaSize() {
    	Element[] delta = getDeltaArray();
    	int result = delta.length;
    	while (result > 0 && !isTrueDeltaElement(delta[result-1])) {
    		result--;
    	}
    	return result;
    }
//    
//    /**
//     * Returns a list view upon the current outgoing transitions.
//     */
//    private Set<GraphTransitionStub> getTransitionStubSet() {
//    	return getCache().getStubSet();
//    }

    /**
     * Stores the outgoing transitions in a memory efficient way.
     */
    private void storeTransitionStubSet() {
    	Map<RuleEvent, GraphTransition> transitionMap = getCache().getTransitionMap();
//    	Collection<GraphTransitionStub> outTransitionSet = getTransitionStubSet();
    	Element[] oldDelta = getDeltaArray();
    	int oldDeltaSize = getDeltaSize();
    	Element[] newDelta = new Element[oldDeltaSize + transitionMap.size()];
    	System.arraycopy(oldDelta, 0, newDelta, 0, oldDeltaSize);
    	int index = oldDeltaSize;
    	for (GraphTransition transition: transitionMap.values()) {
			newDelta[index] = transition.toStub();
			index++;
		}
    	setDeltaArray(newDelta);
    	getCache().clear();
    }
    
    /** Indicates if this graph currently stores any transitions. */
    boolean storesTransitionStubs() {
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
    	return !(elem instanceof GraphTransitionStub);
    }
    
    /**
     * The number of this Node.
     * 
     * @invariant nr < nrNodes
     */
    private int nr = -1;

    /**
     * The number of DefaultStates constructed.
     * 
     * @invariant nrNodes >= 0
     */
    private static int stateCount;

    /** Profiles the {@link #getNextState(RuleEvent)} method. */
    protected static int GET_NEXT_STATE = reporter.newMethod("getNextState"); 
}