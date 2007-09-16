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
 * $Id: StateCache.java,v 1.6 2007-09-16 21:44:27 rensink Exp $
 */
package groove.lts;

import groove.graph.DeltaApplier;
import groove.graph.DeltaGraphFactory;
import groove.graph.DeltaTarget;
import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.FixedDeltaGraph;
import groove.graph.Graph;
import groove.graph.Node;
import groove.trans.RuleEvent;
import groove.util.TreeHashSet;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;


/**
 * Extends the cache with the outgoing transitions, as a set.
 * @author Arend Rensink
 * @version $Revision: 1.6 $
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
     * Lazily creates and returns the graph of the underlying state.
     * This is only supported if the state is a {@link GraphNextState}
     * @throws IllegalStateException if the underlying state is not a {@link GraphNextState}
     */
    Graph getGraph() {
    	if (graph == null) {
    		graph = computeGraph();
    	}
    	return graph;
    }
    
    /** 
     * Compute the graph from the information in the state.
     * The state is assumed to be a {@link DefaultGraphNextState}.
     */
    private Graph computeGraph() {
		Element[] frozenGraph = state.getFrozenGraph();
    	Graph result;
		if (frozenGraph != null) {
			result = graphFactory.newGraph(null, computeFrozenDelta(frozenGraph));
		} else if (!(state instanceof GraphNextState)) {
			throw new IllegalStateException("Underlying state does not have information to reconstruct the graph");
		} else {
			DefaultGraphNextState state = (DefaultGraphNextState) this.state;
			result = graphFactory.newGraph(state.source().getGraph(), state.getDelta());
			// If the state is closed, then we are reconstructing the graph
			// for the second time at least; see if we should freeze it
			// on the other hand, the stack of derived next states should not grow so large
			// that (re)constructing the graph gives a stack overflow!
			// so the closedness test should be skipped
			if (isFreezeGraph()) {
				state.setFrozenGraph(computeFrozenGraph(result));
			}
		}
		return result;
    }

    /**
     * Decides whether the underlying graph should be frozen.
     * The decision is taken on the basis of the <i>freeze count</i>, as
     * computed by {@link #getFreezeCount()}; the graph is frozen if the freeze
     * count exceeds {@link #FREEZE_BOUND}.
     * @return <code>true</code> if the graph should be frozen
     */
    private boolean isFreezeGraph() {
    	return freezeGraphs && getFreezeCount() > FREEZE_BOUND;
    }

    /** 
     * Computes a number expressing the urgency of freezing the underlying graph.
     * The current measure is based on the number of steps from the previous
     * frozen graph.
     * @return the freeze count of the underlying state
     */
    private int getFreezeCount() {
    	if (state instanceof DefaultGraphNextState) {
    		return getFreezeCount((DefaultGraphNextState) state);
    	} else {
    		return 0;
    	}
    }
    
    /**
     * Computes a number expressing the urgency of freezing the graph of a given
     * state.
     * The current measure is based on the number of steps from the previous
     * frozen graph, following the chain of parents from the given state.
     * @return the freeze count of a given state
     */
    private int getFreezeCount(DefaultGraphNextState state) {
    	// determine the freeze count of the state's parent state
    	int parentCount;
    	AbstractGraphState parent = state.source();
    	if (parent.getFrozenGraph() != null || !(parent instanceof DefaultGraphNextState)) {
    		parentCount = 0;
    	} else if (parent.isCacheCleared()) {
    		parentCount = getFreezeCount((DefaultGraphNextState) parent);
    	} else {
    		parentCount = parent.getCache().getFreezeCount();
    	}
    	return parentCount + 1;
    }
    
    /** 
     * Computes a frozen graph representation from a given graph.
     * The frozen graph representation consists of all nodes and edges of the
     * graph in a single array. 
     */
    Element[] computeFrozenGraph(Graph graph) {
    	Element[] result = new Element[graph.size()];
    	int index = 0;
    	for (Node node: graph.nodeSet()) {
    		result[index] = node;
    		index++;
    	}
    	for (Edge edge: graph.edgeSet()) {
    		result[index] = edge;
    		index++;
    	}
    	return result;
    }
    
    /**
     * Converts a frozen graph representation into a delta applier.
     * It is assumed that the frozen graph representation contains all nodes
     * and edges of the graph in a single array.
     * @param elements the frozen graph representation; non-<code>null</code>
     * @return a delta applier based on <code>elements</code>
     */
    private DeltaApplier computeFrozenDelta(final Element[] elements) {
		return new DeltaApplier() {
			public void applyDelta(DeltaTarget target, int mode) {
				for (Element elem : elements) {
					if (elem instanceof Node && mode != EDGES_ONLY) {
						target.addNode((Node) elem);
					} else if (elem instanceof Edge && mode != NODES_ONLY) {
						target.addEdge((Edge) elem);
					}
				}
			}

			public void applyDelta(DeltaTarget target) {
				applyDelta(target, ALL_ELEMENTS);
			}
		};
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
    
    /** Callback factory method to create the transition map object. */
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
    
    /**
     * The set of outgoing transitions computed for the underlying graph.
     */
    private Set<GraphTransitionStub> stubSet;
    /** The graph state of this cache. */
    private final AbstractGraphState state;
    /** Cached map from events to target transitions. */
    private Map<RuleEvent,GraphState> transitionMap;
    /** Cached graph for this state. */
    private Graph graph;
    
    /** 
     * Sets the freeze bound for state graphs;
     * a value of <code>-1</code> means graphs are never frozen.
     */
    static public void setFreezeGraphs(boolean freeze) {
    	StateCache.freezeGraphs = freeze;
    }
    
    /** Sets the factory used to create the state graphs. */
    static public void setGraphFactory(DeltaGraphFactory factory) {
    	graphFactory = factory;
    }
    
    /** The graph factory currently used for states. */
    static private DeltaGraphFactory graphFactory = FixedDeltaGraph.getInstance();
    /** Flag indicating if the graph should be frozen. */
    static private boolean freezeGraphs = true;
    /** 
     * The depth of the graph above which the underlying graph will be frozen.
     */
    static private final int FREEZE_BOUND = 10;
}
