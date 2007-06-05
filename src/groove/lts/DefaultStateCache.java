/**
 * GROOVE: GRaphs for Object Oriented VErification
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
 * $Id: DefaultStateCache.java,v 1.7 2007-06-05 08:55:09 kastenberg Exp $
 */
package groove.lts;

import groove.graph.DeltaGraphCache;
import groove.trans.RuleEvent;

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * Extends the cache with the outgoing transitions, as a set.
 * @author Arend Rensink
 * @version $Revision: 1.7 $
 * @deprecated replaced by {@link StateCache}
 */
@Deprecated
public class DefaultStateCache extends DeltaGraphCache {
    /**
     * Constructs a cache for a given state.
     */
    protected DefaultStateCache(DefaultGraphState state) {
        super(state);
    }
    
    /** 
     * Adds an outgoing transition to the structures stored in this cache.
     */
    boolean addTransition(GraphTransition transition) {
//    	boolean result = getStubSet().add(getGraph().createTransitionStub(event, target));
    	boolean result = getTransitionMap().put(transition.getEvent(), transition) == null;
    	return result;
    }

    /** 
     * Lazily creates and returns a mapping from the events to the target states 
     * of the currently stored outgoing transitions of this state.
     */
    Map<RuleEvent, GraphTransition> getTransitionMap() {
    	if (transitionMap == null) {
    		transitionMap = computeTransitionMap();
    	}
    	return transitionMap;
    }
    
    /** Indicates if the transition map is currently initialised. */
    boolean isTransitionMapSet() {
    	return transitionMap != null;
    }
    
    /** 
     * Computes a mapping from the events to the target states 
     * of the currently stored outgoing transitions of this state.
     */
    private Map<RuleEvent, GraphTransition> computeTransitionMap() {
    	Map<RuleEvent,GraphTransition> result = createTransitionMap();
    	Iterator<GraphTransitionStub> stubIter = getGraph().getStoredStubIter();
    	if (stubIter != null) {
			while (stubIter.hasNext()) {
				GraphTransitionStub stub = stubIter.next();
				result.put(stub.getEvent(getGraph()),
						stub.toTransition(getGraph()));
			}
		}
    	return result;
    }
    
    /** Callback factory method to create the transition map. */
    private Map<RuleEvent,GraphTransition> createTransitionMap() {
    	return new IdentityHashMap<RuleEvent,GraphTransition>();
    }
//
//    /**
//     * Returns the cached set out {@link GraphTransitionStub}s.
//     * The set is constructed lazily if the state is closed,
//     * using {@link #computeStubSet()}; if the state s not closed,
//     * an empty set is initialized.
//     */
//    Set<GraphTransitionStub> getStubSet() {
//        if (stubSet == null) {
//        	stubSet = computeStubSet();
//        }
//        return stubSet;
//    }
//    
    /**
     * Clears the cached set, so it does not occupy memory.
     * This is typically done at the moment the state is closed.
     */
    void clear() {
//    	stubSet = null;
    	transitionMap = null;
    }
//    
//    /**
//     * Reconstructs the set of {@link groove.lts.GraphTransitionStub}s from the corresponding
//     * {@link groove.util.ListEntry} in the underlying graph state.
//     * It is assumed that <code>getState().isClosed()</code>.
//     */
//    private Set<GraphTransitionStub> computeStubSet() {
//        Set<GraphTransitionStub> result = createStubSet();
//        if (getGraph().storesTransitionStubs()) {
//			Iterator<GraphTransitionStub> outTransitionIter = getGraph().getStoredStubIter();
//			while (outTransitionIter.hasNext()) {
//				GraphTransitionStub outTransition = outTransitionIter.next();
//				result.add(outTransition);
//			}
//		}
//        return result;
//    }
//    
//    /**
//     * Factory method for the outgoing transition set.
//     */
//    private Set<GraphTransitionStub> createStubSet() {
//    	return new TreeHashSet<GraphTransitionStub>() {
//			@Override
//			protected boolean areEqual(Object key, Object otherKey) {
//				return getEvent(key) == getEvent(otherKey);
//			}
//
//			@Override
//			protected int getCode(Object key) {
//				RuleEvent keyEvent = getEvent(key);
//				return keyEvent == null ? 0 : System.identityHashCode(keyEvent);
//			}
//			
//			private RuleEvent getEvent(Object key) {
//				if (key instanceof GraphTransitionStub) {
//					return ((GraphTransitionStub) key).getEvent(getGraph());
//				} else {
//					return null;
//				}
//			}
//    	};
//    }
    
    /** Specialises the returnt ype of the super methods. */
    @Override
    public DefaultGraphState getGraph() {
    	return (DefaultGraphState) super.getGraph();
    }
//    
//    /**
//     * The set of outgoing transitions computed for the underlying graph.
//     */
//    private Set<GraphTransitionStub> stubSet;
    /** Mapping from rule events to target states of transitions from this (source) state. */
    private Map<RuleEvent,GraphTransition> transitionMap;
}
