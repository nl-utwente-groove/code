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

import groove.trans.DeltaApplier;
import groove.trans.DeltaHostGraph;
import groove.trans.HostEdge;
import groove.trans.HostElement;
import groove.trans.HostGraph;
import groove.trans.HostNode;
import groove.trans.RuleApplication;
import groove.trans.RuleEvent;
import groove.trans.SystemRecord;
import groove.util.TreeHashSet;

import java.util.HashMap;
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
public class StateCache {
    /**
     * Constructs a cache for a given state.
     */
    StateCache(AbstractGraphState state) {
        this.state = state;
        this.record = state.getRecord();
        this.freezeGraphs = this.record.isCollapse();
        this.graphFactory =
            DeltaHostGraph.getInstance(this.record.isCopyGraphs());
    }

    /** Adds a transition stub to the data structures stored in this cache. */
    boolean addTransitionStub(GraphTransitionStub stub) {
        boolean result = getStubSet().add(stub);
        if (result && this.transitionMap != null) {
            GraphTransition trans = stub.toTransition(this.state);
            this.transitionMap.put(trans.getEvent(), trans);
        }
        return result;
    }

    AbstractGraphState getState() {
        return this.state;
    }

    /** Sets the cached graph. */
    void setGraph(DeltaHostGraph graph) {
        this.graph = graph;
    }

    /**
     * Lazily creates and returns the graph of the underlying state. This is
     * only supported if the state is a {@link GraphNextState}
     * @throws IllegalStateException if the underlying state is not a
     *         {@link GraphNextState}
     */
    DeltaHostGraph getGraph() {
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

    /**
     * Callback factory method for a rule application on the basis of this
     * state.
     */
    private DeltaApplier createDelta() {
        DeltaApplier result = null;
        if (this.state instanceof DefaultGraphNextState) {
            DefaultGraphNextState state = (DefaultGraphNextState) this.state;
            return new RuleApplication(state.getEvent(),
                state.source().getGraph(), state.getAddedNodes());
        }
        return result;
    }

    /**
     * Compute the graph from the information in the state.
     */
    private DeltaHostGraph computeGraph() {
        HostElement[] frozenGraph = this.state.getFrozenGraph();
        DeltaHostGraph result;
        if (frozenGraph != null) {
            result =
                this.graphFactory.newGraph(this.state.toString(), frozenGraph,
                    this.record.getFactory());
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
            result = (DeltaHostGraph) backward.getGraph();
            for (DefaultGraphNextState forward : stateChain) {
                result =
                    this.graphFactory.newGraph(state.toString(), result,
                        forward.getDelta());
            }
            result =
                this.graphFactory.newGraph(state.toString(), result, getDelta());
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

    /**
     * Computes a frozen graph representation from a given graph. The frozen
     * graph representation consists of all nodes and edges of the graph in a
     * single array.
     */
    HostElement[] computeFrozenGraph(HostGraph graph) {
        HostElement[] result = new HostElement[graph.size()];
        int index = 0;
        for (HostNode node : graph.nodeSet()) {
            result[index] = node;
            index++;
        }
        for (HostEdge edge : graph.edgeSet()) {
            result[index] = edge;
            index++;
        }
        return result;
    }

    /**
     * Lazily creates and returns a mapping from the events to 
     * outgoing transitions of this state.
     */
    Map<RuleEvent,GraphTransition> getTransitionMap() {
        if (this.transitionMap == null) {
            this.transitionMap = computeTransitionMap();
        }
        return this.transitionMap;
    }

    /**
     * Computes a mapping from the events to the 
     * outgoing transitions of this state.
     */
    private Map<RuleEvent,GraphTransition> computeTransitionMap() {
        Map<RuleEvent,GraphTransition> result =
            new HashMap<RuleEvent,GraphTransition>();
        for (GraphTransitionStub stub : getStubSet()) {
            GraphTransition trans = stub.toTransition(this.state);
            result.put(trans.getEvent(), trans);
        }
        return result;
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
    private Map<RuleEvent,GraphTransition> transitionMap;
    /** Cached graph for this state. */
    private DeltaHostGraph graph;
    /**
     * Flag indicating if (a fraction of the) state graphs should be frozen.
     * This is set to <code>true</code> if states in the GTS are collapsed.
     */
    private final boolean freezeGraphs;
    /** Factory used to create the state graphs. */
    private final DeltaHostGraph graphFactory;
    /**
     * The depth of the graph above which the underlying graph will be frozen.
     */
    static private final int FREEZE_BOUND = 10;
}
