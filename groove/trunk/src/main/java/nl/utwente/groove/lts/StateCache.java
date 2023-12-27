// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2023 University of Twente

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
 * $Id$
 */
package nl.utwente.groove.lts;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import nl.utwente.groove.control.NestedCall;
import nl.utwente.groove.grammar.Action;
import nl.utwente.groove.grammar.Action.Role;
import nl.utwente.groove.grammar.CheckPolicy;
import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.grammar.host.DeltaHostGraph;
import nl.utwente.groove.grammar.host.HostEdge;
import nl.utwente.groove.grammar.host.HostElement;
import nl.utwente.groove.grammar.host.HostGraph;
import nl.utwente.groove.grammar.host.HostNode;
import nl.utwente.groove.transform.DeltaApplier;
import nl.utwente.groove.transform.Record;
import nl.utwente.groove.transform.RuleApplication;
import nl.utwente.groove.util.Exceptions;
import nl.utwente.groove.util.Groove;
import nl.utwente.groove.util.collect.KeySet;
import nl.utwente.groove.util.collect.SetView;
import nl.utwente.groove.util.collect.TreeHashSet;

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
    protected StateCache(AbstractGraphState state) {
        this.state = state;
        this.record = state.getRecord();
        this.freezeGraphs = this.record.isCollapse();
        this.graphFactory = DeltaHostGraph.getInstance(this.record.isCopyGraphs());
        if (DEBUG && state.isDone()) {
            System.out.printf("Recreating cache for done state %s%n", state);
        }
    }

    /** Adds a transition stub to the data structures stored in this cache. */
    boolean addTransition(GraphTransition trans) {
        assert trans.source() == getState();
        boolean result = getStubSet().add(trans.toStub());
        if (result && this.transitionMap != null) {
            this.transitionMap.add(trans);
        }
        if (trans instanceof RuleTransition) {
            getMatches().remove(trans.getKey());
            if (trans.isPartial() || trans.isInternalStep()) {
                getExploreData().notifyOutPartial((RuleTransition) trans);
            }
        }
        if (getMatches().isFinished()) {
            getState().setClosed(true);
        }
        return result;
    }

    Set<? extends GraphTransition> getTransitions(final GraphTransition.Claz claz) {
        if (claz == GraphTransition.Claz.ANY) {
            return getTransitionMap();
        } else {
            return new SetView<>(getTransitionMap()) {
                @Override
                public boolean approves(Object obj) {
                    return obj instanceof GraphTransition && claz.admits((GraphTransition) obj);
                }
            };
        }
    }

    /**
     * Callback method invoked when the state has been closed.
     */
    void notifyClosed() {
        getExploreData().notifyClosed();
    }

    final AbstractGraphState getState() {
        return this.state;
    }

    /**
     * Lazily creates and returns the graph of the underlying state. This is
     * only supported if the state is a {@link GraphNextState}
     * @throws IllegalStateException if the underlying state is not a
     *         {@link GraphNextState}
     */
    final DeltaHostGraph getGraph() {
        if (this.graph == null) {
            this.graph = computeGraph();
        }
        return this.graph;
    }

    /** Indicates if this cache currently stores a graph. */
    final boolean hasGraph() {
        return this.graph != null;
    }

    /**
     * Lazily creates and returns the delta with respect to the
     * parent state.
     */
    final DeltaApplier getDelta() {
        if (this.delta == null) {
            this.delta = createDelta();
        }
        return this.delta;
    }

    final ExploreData getExploreData() {
        if (this.exploreData == null) {
            this.exploreData = new ExploreData(this);
        }
        return this.exploreData;
    }

    private ExploreData exploreData;

    /**
     * Returns the lowest known absence depth of the state.
     * This is {@link Integer#MAX_VALUE} if the state is erroneous,
     * otherwise it is the minimum transient depth of the reachable states.
     */
    final int getAbsence() {
        return getExploreData().getAbsence();
    }

    /** Notifies the cache of a decrease in transient depth of the control frame. */
    final void notifyDepth(int depth) {
        getExploreData().notifyDepth(depth);
    }

    /**
     * Callback factory method for a rule application on the basis of this
     * state.
     */
    private DeltaApplier createDelta() {
        DeltaApplier result = null;
        if (this.state instanceof DefaultGraphNextState state) {
            return new RuleApplication(state.getEvent(), state.source().getGraph(),
                state.getAddedNodes());
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
            result = this.graphFactory
                .newGraph(getState().toString(), frozenGraph, this.record.getFactory());
        } else if (!(this.state instanceof GraphNextState)) {
            throw Exceptions
                .illegalState("Underlying state does not have information to reconstruct the graph");
        } else {
            int depth = 0; // depth of reconstruction
            DefaultGraphNextState state = (DefaultGraphNextState) this.state;
            // make sure states get reconstructed sequentially rather than
            // recursively
            AbstractGraphState backward = state.source();
            List<DefaultGraphNextState> stateChain = new LinkedList<>();
            while (backward instanceof GraphNextState && !backward.hasCache()
                && backward.getFrozenGraph() == null) {
                stateChain.add(0, (DefaultGraphNextState) backward);
                backward = ((DefaultGraphNextState) backward).source();
                depth++;
            }
            // now let all states along the chain reconstruct their graphs,
            // from ancestor to this one
            result = (DeltaHostGraph) backward.getGraph();
            for (DefaultGraphNextState forward : stateChain) {
                result = this.graphFactory.newGraph(state.toString(), result, forward.getDelta());
            }
            result = this.graphFactory.newGraph(state.toString(), result, getDelta());
            // If the state is closed, then we are reconstructing the graph
            // for the second time at least; see if we should freeze it
            if (getState().isClosed() && isFreezeGraph(depth)) {
                // if (isFreezeGraph()) {
                state.setFrozenGraph(computeFrozenGraph(result));
            }
        }
        if (getState().isDone() && getState().isError()) {
            if (getState().getGTS().getTypePolicy() != CheckPolicy.OFF) {
                // apparently we're reconstructing the graph after the state was already
                // done and found to be erroneous; so reconstruct the type errors
                result.addErrors(result.checkTypeConstraints());
            }
            // check the property and deadlock constraints
            GTS gts = getState().getGTS();
            // check for liveness
            boolean alive = false;
            // collect all property matches
            Set<Action> erroneous = new HashSet<>(gts.getGrammar().getActions(Role.INVARIANT));
            for (GraphTransition trans : getTransitions(GraphTransition.Claz.REAL)) {
                Action action = trans.getAction();
                switch (action.getRole()) {
                case FORBIDDEN:
                    erroneous.add(action);
                    break;
                case INVARIANT:
                    erroneous.remove(action);
                    break;
                case TRANSFORMER:
                    alive = true;
                    break;
                default:
                    // nothing to be done
                }
            }
            for (Action action : erroneous) {
                addConstraintError(result, action);
            }
            if (!alive && gts.isCheckDeadlock()) {
                addDeadlockError(result);
            }
        }
        return result;
    }

    /**
     * Adds a deadlock error message to a given graph.
     */
    void addDeadlockError(HostGraph graph) {
        Set<QualName> actions = new LinkedHashSet<>();
        for (NestedCall call : getState().getActualFrame().getPastAttempts()) {
            if (call.getAction().getRole() == Role.TRANSFORMER) {
                actions.add(call.getAction().getQualName());
            }
        }
        if (actions.isEmpty()) {
            graph.addError("Deadlock (no transformer scheduled)");
        } else {
            graph
                .addError("Deadlock: scheduled transformer%s %s failed to be applicable",
                          actions.size() == 1
                              ? ""
                              : "s",
                          Groove.toString(actions.toArray(), "'", "'", "', '", "' and '"));
        }
    }

    /** Adds an error message regarding the failure of t graph constraint to a given graph. */
    void addConstraintError(HostGraph graph, Action action) {
        switch (action.getRole()) {
        case FORBIDDEN:
            graph.addError("Graph satisfies forbidden property '%s'", action.getQualName());
            break;
        case INVARIANT:
            graph.addError("Graph fails to satisfy invariant property '%s'", action.getQualName());
            break;
        default:
            assert false;
        }
    }

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

    RuleTransition getRuleTransition(MatchResult match) {
        return (RuleTransition) getTransitionMap().get(match);
    }

    /**
     * Lazily creates and returns a mapping from the events to
     * outgoing transitions of this state.
     */
    KeySet<GraphTransitionKey,GraphTransition> getTransitionMap() {
        if (this.transitionMap == null) {
            this.transitionMap = computeTransitionMap();
        }
        return this.transitionMap;
    }

    /**
     * Computes a mapping from the events to the
     * outgoing transitions of this state.
     */
    private KeySet<GraphTransitionKey,GraphTransition> computeTransitionMap() {
        KeySet<GraphTransitionKey,GraphTransition> result = new KeySet<>() {
            @Override
            protected GraphTransitionKey getKey(Object value) {
                return ((GraphTransition) value).getKey();
            }
        };
        for (GraphTransitionStub stub : getStubSet()) {
            GraphTransition trans = stub.toTransition(this.state);
            result.add(trans);
        }
        return result;
    }

    /**
     * Returns the cached set of {@link RuleTransitionStub}s. The set is
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
     * Reconstructs the set of {@link nl.utwente.groove.lts.RuleTransitionStub}s from the
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
        return new TreeHashSet<>() {
            @Override
            protected boolean areEqual(GraphTransitionStub stub, GraphTransitionStub otherStub) {
                return getKey(stub).equals(getKey(otherStub));
            }

            @Override
            protected int getCode(GraphTransitionStub stub) {
                GraphTransitionKey keyEvent = getKey(stub);
                return keyEvent == null
                    ? 0
                    : keyEvent.hashCode();
            }

            private GraphTransitionKey getKey(GraphTransitionStub stub) {
                return stub.getKey(getState());
            }
        };
    }

    StateMatches getMatches() {
        if (this.stateMatches == null) {
            this.stateMatches = new StateMatches(this);
        }
        return this.stateMatches;
    }

    private StateMatches stateMatches;

    /** Factory method for a match collector. */
    protected MatchCollector createMatchCollector() {
        return new MatchCollector(getState());
    }

    @Override
    public String toString() {
        return "StateCache [state=" + this.state + "]";
    }

    /**
     * The set of outgoing transitions computed for the underlying graph,
     * for every class of graph transitions.
     */
    private Set<GraphTransitionStub> stubSet;
    /** The graph state of this cache. */
    private final AbstractGraphState state;
    /** The system record generating this state. */
    private final Record record;
    /** The delta with respect to the state's parent. */
    private DeltaApplier delta;
    /** Cached map from events to target transitions. */
    private KeySet<GraphTransitionKey,GraphTransition> transitionMap;
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
    /** Debug flag. */
    private static final boolean DEBUG = false;
}
