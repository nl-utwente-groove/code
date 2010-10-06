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
 * $Id: StateCache.java,v 1.23 2008-02-05 13:43:28 rensink Exp $
 */
package groove.lts;

import groove.graph.DeltaApplier;
import groove.graph.DeltaGraphFactory;
import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.FrozenDeltaApplier;
import groove.graph.Graph;
import groove.graph.NewDeltaGraph;
import groove.graph.Node;
import groove.trans.DefaultApplication;
import groove.trans.RuleEvent;
import groove.trans.SystemRecord;
import groove.util.TreeHashSet;

import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Caches information of a state. Cached are the graph, the set of outgoing
 * transitions, and the delta with respect to the previous state.
 * @author Arend Rensink
 * @version $Revision$
 */
class StateCache {
    /**
     * Constructs a cache for a given state.
     */
    StateCache(AbstractGraphState state) {
        this.state = state;
        this.record = state.getRecord();
        this.freezeGraphs = this.record.isCollapse();
        this.graphFactory =
            NewDeltaGraph.getInstance(this.record.isCopyGraphs());
    }

    /** Adds a transition stub to the data structures stored in this cache. */
    boolean addTransitionStub(GraphTransitionStub stub) {
        boolean result = getStubSet().add(stub);
        if (result && this.transitionMap != null) {
            GraphState oldState =
                this.transitionMap.put(stub.getEvent(this.state),
                    stub.getTarget(this.state));
            assert oldState == null;
        }
        return result;
    }

    AbstractGraphState getState() {
        return this.state;
    }

    /** Sets the cached graph. */
    void setGraph(Graph graph) {
        this.graph = graph;
    }

    /**
     * Lazily creates and returns the graph of the underlying state. This is
     * only supported if the state is a {@link GraphNextState}
     * @throws IllegalStateException if the underlying state is not a
     *         {@link GraphNextState}
     */
    Graph getGraph() {
        if (this.graph == null) {
            this.graph = computeGraph();
        }
        return this.graph;
    }

    /** Indicates if this cache currently stores a graph. */
    boolean hasGraph() {
        return this.graph != null;
    }

    /** 
     * Lazily creates and returns the delta with respect to the
     * parent state.
     */
    DeltaApplier getDelta() {
        if (this.delta == null) {
            this.delta = createDelta();
        }
        return this.delta;
    }

    //
    //    /** 
    //     * Returns the delta of a given state.
    //     */
    //    private DeltaApplier computeDelta(AbstractGraphState state) {
    //        DeltaApplier result;
    //        Element[] frozenGraph = state.getFrozenGraph();
    //        if (frozenGraph != null) {
    //            result = new FrozenDeltaApplier(frozenGraph);
    //        } else {
    //            result = createDelta((DefaultGraphNextState) state);
    //        }
    //        return result;
    //    }

    /**
     * Callback factory method for a rule application on the basis of this
     * state.
     */
    private DeltaApplier createDelta() {
        DeltaApplier result = null;
        if (this.state instanceof DefaultGraphNextState) {
            DefaultGraphNextState state = (DefaultGraphNextState) this.state;
            return new DefaultApplication(state.getEvent(),
                state.source().getGraph(), state.getAddedNodes());
        }
        return result;
    }

    /**
     * Compute the graph from the information in the state. The state is assumed
     * to be a {@link DefaultGraphNextState}.
     */
    @SuppressWarnings("unchecked")
    private Graph computeGraph() {
        Element[] frozenGraph = this.state.getFrozenGraph();
        Graph result;
        if (frozenGraph != null) {
            result =
                this.graphFactory.newGraph(null, new FrozenDeltaApplier(
                    frozenGraph));
        } else if (!(this.state instanceof GraphNextState)) {
            throw new IllegalStateException(
                "Underlying state does not have information to reconstruct the graph");
        } else {
            int depth = 0; // depth of reconstruction
            DefaultGraphNextState state = (DefaultGraphNextState) this.state;
            // make sure states get reconstructed sequentially rather than
            // recursively
            AbstractGraphState backward = state.source();
            List<DefaultGraphNextState> stateChain =
                new LinkedList<DefaultGraphNextState>();
            while (backward instanceof GraphNextState
                && backward.isCacheCleared()
                && backward.getFrozenGraph() == null) {
                stateChain.add(0, (DefaultGraphNextState) backward);
                backward = ((DefaultGraphNextState) backward).source();
                depth++;
            }
            // now let all states along the chain reconstruct their graphs,
            // from ancestor to this one
            result = backward.getGraph();
            for (DefaultGraphNextState forward : stateChain) {
                result = this.graphFactory.newGraph(result, forward.getDelta());
            }
            result = this.graphFactory.newGraph(result, getDelta());
            // If the state is closed, then we are reconstructing the graph
            // for the second time at least; see if we should freeze it
            if (getState().isClosed() && isFreezeGraph(depth)) {
                // if (isFreezeGraph()) {
                state.setFrozenGraph(computeFrozenGraph(result));
            }
        }
        return result;
    }

    //
    //    /**
    //     * Decides whether the underlying graph should be frozen. The decision is
    //     * taken on the basis of the <i>freeze count</i>, as computed by
    //     * {@link #getFreezeCount()}; the graph is frozen if the freeze count
    //     * exceeds {@link #FREEZE_BOUND}.
    //     * @return <code>true</code> if the graph should be frozen
    //     */
    //    private boolean isFreezeGraph() {
    //        return isFreezeGraph(getFreezeCount());
    //    }

    /**
     * Decides whether the underlying graph should be frozen. The decision is
     * taken on the basis of the <i>freeze count</i>, passed in as a 
     * parameter; the graph is frozen if the freeze count
     * exceeds {@link #FREEZE_BOUND}.
     * @return <code>true</code> if the graph should be frozen
     */
    private boolean isFreezeGraph(int freezeCount) {
        return this.freezeGraphs && freezeCount > FREEZE_BOUND;
    }

    //
    //    /**
    //     * Computes a number expressing the urgency of freezing the underlying
    //     * graph. The current measure is based on the number of steps from the
    //     * previous frozen graph.
    //     * @return the freeze count of the underlying state
    //     */
    //    private int getFreezeCount() {
    //        if (this.state instanceof DefaultGraphNextState) {
    //            return getFreezeCount((DefaultGraphNextState) this.state);
    //        } else {
    //            return 0;
    //        }
    //    }
    //
    //    /**
    //     * Computes a number expressing the urgency of freezing the graph of a given
    //     * state. The current measure is based on the number of steps from the
    //     * previous frozen graph, following the chain of parents from the given
    //     * state.
    //     * @return the freeze count of a given state
    //     */
    //    private int getFreezeCount(DefaultGraphNextState state) {
    //        // determine the freeze count of the state's parent state
    //        int parentCount;
    //        AbstractGraphState parent = state.source();
    //        parentCount = 1;
    //        while (parent instanceof DefaultGraphNextState && parent.getFrozenGraph() != null) {
    //            if (parent.isCacheCleared()) {
    //                parent = ((DefaultGraphNextState) parent).source();
    //                parentCount++;
    //            } else {
    //                parentCount += parent.getCache().getFreezeCount();
    //            }
    //        }
    //        return parentCount;
    //    }

    /**
     * Computes a frozen graph representation from a given graph. The frozen
     * graph representation consists of all nodes and edges of the graph in a
     * single array.
     */
    Element[] computeFrozenGraph(Graph graph) {
        Element[] result = new Element[graph.size()];
        int index = 0;
        for (Node node : graph.nodeSet()) {
            result[index] = node;
            index++;
        }
        for (Edge edge : graph.edgeSet()) {
            result[index] = edge;
            index++;
        }
        return result;
    }

    /**
     * Lazily creates and returns a mapping from the events to the target states
     * of the currently stored outgoing transitions of this state.
     */
    Map<RuleEvent,GraphState> getTransitionMap() {
        if (this.transitionMap == null) {
            this.transitionMap = computeTransitionMap();
        }
        return this.transitionMap;
    }

    /**
     * Computes a mapping from the events to the target states of the currently
     * stored outgoing transitions of this state.
     */
    private Map<RuleEvent,GraphState> computeTransitionMap() {
        Map<RuleEvent,GraphState> result = createTransitionMap();
        for (GraphTransitionStub stub : getStubSet()) {
            result.put(stub.getEvent(this.state), stub.getTarget(this.state));
        }
        return result;
    }

    /** Callback factory method to create the transition map object. */
    private Map<RuleEvent,GraphState> createTransitionMap() {
        return new IdentityHashMap<RuleEvent,GraphState>();
    }

    /**
     * Returns the cached set of {@link GraphTransitionStub}s. The set is
     * constructed lazily if the state is closed, using
     * {@link #computeStubSet()}; if the state is not closed, an empty set is
     * initialised.
     */
    Set<GraphTransitionStub> getStubSet() {
        if (this.stubSet == null) {
            this.stubSet = computeStubSet();
        }
        return this.stubSet;
    }

    /**
     * Clears the cached set, so it does not occupy memory. This is typically
     * done at the moment the state is closed.
     */
    void clearStubSet() {
        this.stubSet = null;
    }

    /**
     * Reconstructs the set of {@link groove.lts.GraphTransitionStub}s from the
     * corresponding array in the underlying graph state. It is assumed that
     * <code>getState().isClosed()</code>.
     */
    private Set<GraphTransitionStub> computeStubSet() {
        Set<GraphTransitionStub> result = createStubSet();
        result.addAll(this.state.getStoredTransitionStubs());
        return result;
    }

    /**
     * Factory method for the outgoing transition set.
     */
    private Set<GraphTransitionStub> createStubSet() {
        return new TreeHashSet<GraphTransitionStub>() {
            @Override
            protected boolean areEqual(GraphTransitionStub key,
                    GraphTransitionStub otherKey) {
                return key.getEvent(getState()).equals(
                    otherKey.getEvent(getState()));
                // return key.getEvent(getState()) ==
                // otherKey.getEvent(getState());
            }

            @Override
            protected int getCode(GraphTransitionStub key) {
                RuleEvent keyEvent = key.getEvent(getState());
                // return keyEvent == null ? 0 : keyEvent.identityHashCode();
                return keyEvent == null ? 0 : keyEvent.hashCode();
            }
        };
    }

    /**
     * The set of outgoing transitions computed for the underlying graph.
     */
    private Set<GraphTransitionStub> stubSet;
    /** The graph state of this cache. */
    private final AbstractGraphState state;
    /** The system record generating this state. */
    private final SystemRecord record;
    /** The delta with respect to the state's parent. */
    private DeltaApplier delta;
    /** Cached map from events to target transitions. */
    private Map<RuleEvent,GraphState> transitionMap;
    /** Cached graph for this state. */
    private Graph graph;
    /**
     * Flag indicating if (a fraction of the) state graphs should be frozen.
     * This is set to <code>true</code> if states in the GTS are collapsed.
     */
    private final boolean freezeGraphs;
    /** Factory used to create the state graphs. */
    @SuppressWarnings("unchecked")
    private final DeltaGraphFactory graphFactory;
    /**
     * The depth of the graph above which the underlying graph will be frozen.
     */
    static private final int FREEZE_BOUND = 10;
}
