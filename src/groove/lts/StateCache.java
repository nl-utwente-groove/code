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
 * $Id: StateCache.java,v 1.1 2007-04-22 23:32:14 rensink Exp $
 */
package groove.lts;

import groove.graph.Graph;
import groove.trans.RuleEvent;
import groove.util.TreeHashSet;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;


/**
 * Extends the cache with the outgoing transitions, as a set.
 * @author Arend Rensink
 * @version $Revision: 1.1 $
 */
public class StateCache {
    /**
     * Constructs a cache for a given state.
     */
    protected StateCache(AbstractGraphState state) {
        this.state = state;
    }
   
    /** Adds a transition stub to the data structures stored in this cache. */
    boolean addTransitionStub(GraphTransitionStub stub) {
    	boolean result = getStubSet().add(stub);
    	if (transitionMap != null) {
    		transitionMap.put(stub.getEvent(state), stub.target());
    	}
    	return result;
    }
    
    /** 
     * Returns the graph of the underlying state.
     * This is only supported if the state is a {@link GraphNextState}
     * @throws IllegalStateException if the underlying state is not a {@link GraphNextState}
     */
    Graph getGraph() {
    	if (state instanceof DefaultGraphNextState) {
    		return ((DefaultGraphNextState) state).computeGraph();
    	} else {
    		throw new IllegalStateException();
    	}
    }
    
    /** 
     * Lazily creates and returns a mapping from the events to the target states 
     * of the currently stored outgoing transitions of this state.
     */
    Map<RuleEvent,GraphState> getTransitionMap() {
    	if (transitionMap == null) {
    		transitionMap = computeTransitionMap();
    	}
    	return transitionMap;
    }
    
    /** 
     * Computes a mapping from the events to the target states 
     * of the currently stored outgoing transitions of this state.
     */
    private Map<RuleEvent,GraphState> computeTransitionMap() {
    	Map<RuleEvent,GraphState> result = createTransitionMap();
    	for (GraphTransitionStub stub: state.getStoredTransitionStubs()) {
    		result.put(stub.getEvent(state), stub.target());
    	}
    	return result;
    }
    
    /** Callback factory method to create the transition map. */
    private Map<RuleEvent,GraphState> createTransitionMap() {
    	return new IdentityHashMap<RuleEvent,GraphState>();
    }

    /**
     * Returns the cached set out {@link GraphTransitionStub}s.
     * The set is constructed lazily if the state is closed,
     * using {@link #computeStubSet()}; if the state s not closed,
     * an empty set is initialized.
     */
    Set<GraphTransitionStub> getStubSet() {
        if (stubSet == null) {
        	stubSet = computeStubSet();
        }
        return stubSet;
    }
    
    /**
     * Clears the cached set, so it does not occupy memory.
     * This is typically done at the moment the state is closed.
     */
    void clearStubSet() {
    	stubSet = null;
    }
    
    /**
     * Reconstructs the set of {@link groove.lts.GraphTransitionStub}s from the corresponding
     * {@link groove.util.ListEntry} in the underlying graph state.
     * It is assumed that <code>getState().isClosed()</code>.
     */
    private Set<GraphTransitionStub> computeStubSet() {
        Set<GraphTransitionStub> result = createStubSet();
        result.addAll(state.getStoredTransitionStubs());
        return result;
    }
    
    /**
     * Factory method for the outgoing transition set.
     */
    private Set<GraphTransitionStub> createStubSet() {
    	return new TreeHashSet<GraphTransitionStub>() {
			@Override
			protected boolean areEqual(Object key, Object otherKey) {
				return getEvent(key) == getEvent(otherKey);
			}

			@Override
			protected int getCode(Object key) {
				RuleEvent keyEvent = getEvent(key);
				return keyEvent == null ? 0 : System.identityHashCode(keyEvent);
			}
			
			private RuleEvent getEvent(Object key) {
				if (key instanceof GraphTransitionStub) {
					return ((GraphTransitionStub) key).getEvent(state);
				} else {
					return null;
				}
			}
    	};
    }
//    
//    /** Specialises the returnt ype of the super methods. */
//    @Override
//    public DefaultGraphState getGraph() {
//    	return (DefaultGraphState) super.getGraph();
//    }
//    
    /**
     * The set of outgoing transitions computed for the underlying graph.
     */
    private Set<GraphTransitionStub> stubSet;
    /** The graph state of this cache. */
    private final AbstractGraphState state;
    private Map<RuleEvent,GraphState> transitionMap;
}
