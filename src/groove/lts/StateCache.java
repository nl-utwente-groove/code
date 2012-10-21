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

import groove.control.CtrlSchedule;
import groove.trans.Event;
import groove.trans.DeltaApplier;
import groove.trans.DeltaHostGraph;
import groove.trans.HostEdge;
import groove.trans.HostElement;
import groove.trans.HostGraph;
import groove.trans.HostNode;
import groove.trans.RuleApplication;
import groove.trans.RuleEvent;
import groove.trans.SystemRecord;
import groove.util.KeySet;
import groove.util.SetView;
import groove.util.TreeHashSet;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
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
    protected StateCache(AbstractGraphState state) {
        this.state = state;
        this.present = !state.isTransient();
        this.record = state.getRecord();
        this.freezeGraphs = this.record.isCollapse();
        this.graphFactory =
            DeltaHostGraph.getInstance(this.record.isCopyGraphs());
    }

    /** Adds a transition stub to the data structures stored in this cache. */
    boolean addTransition(GraphTransition trans) {
        assert trans.source() == getState();
        boolean result = getStubSet().add(trans.toStub());
        if (result && this.transitionMap != null) {
            this.transitionMap.add(trans);
        }
        this.matches.remove(trans);
        addChild(trans.target(), new GTS.NormalisedStateSet());
        maybeSetClosed();
        return result;
    }

    Set<? extends GraphTransition> getTransitions(
            final GraphTransition.Class claz) {
        if (claz == GraphTransition.Class.ANY) {
            return getTransitionMap();
        } else {
            return new SetView<GraphTransition>(getTransitionMap()) {
                @Override
                public boolean approves(Object obj) {
                    return obj instanceof GraphTransition
                        && claz.admits((GraphTransition) obj);
                }
            };
        }
    }

    /** 
     * Possibly adds this cache to the raw parents of a given
     * child state. If the prospective child is itself one of this cache's
     * raw ancestors, the grandchildren are added recursively.
     * @param child the child state to whose parents this cache should be added
     * @param cyclic intersection of this cache's uncooked ancestors and {@code child}'s
     * successors that have already been investigated 
     */
    private void addChild(GraphState child, Set<GraphState> cyclic) {
        // only add the child if it is raw
        if (child.isTransient() && !child.isCooked()) {
            if (child.equals(getState())) {
                // do nothing
            } else if (this.rawAncestors.contains(child)) {
                if (cyclic.add(child)) {
                    // recursively investigate all grandchildren
                    for (RuleTransition childTrans : child.getRuleTransitions()) {
                        addChild(childTrans.target(), cyclic);
                    }
                }
            } else {
                // add this cache as uncooked ancestor to the given child
                StateCache childCache = child.getCache();
                childCache.rawParents.add(this);
                childCache.rawAncestors.add(getState());
                childCache.rawAncestors.addAll(this.rawAncestors);
                this.rawChildCount++;
            }
        } else {
            this.present = true;
        }
    }

    /** 
     * Callback method invoked when the state has been closed.
     */
    void notifyClosed() {
        if (this.rawChildCount == 0) {
            getState().setCooked();
            if (!this.present) {
                getState().setAbsent();
            }
        }
    }

    /** 
     * Callback method invoked when the state has become cooked.
     * Notifies all raw predecessors that the associated state has become cooked.
     */
    void notifyCooked() {
        for (StateCache parent : this.rawParents) {
            parent.notifyChildCooked(this.present);
        }
        this.rawParents.clear();
        this.rawAncestors.clear();
    }

    /** 
     * Callback method signalling that one of the uncooked successors has
     * become cooked.
     * @param present flag indicating that the cooked successor is now present,
     * implying that this state is present as well.
     */
    private void notifyChildCooked(boolean present) {
        this.rawChildCount--;
        this.present |= present;
        if (this.rawChildCount == 0) {
            getState().setCooked();
            if (!this.present) {
                getState().setAbsent();
            }
        }
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
                this.graphFactory.newGraph(getState().toString(), frozenGraph,
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

    RuleTransition getRuleTransition(RuleEvent event) {
        return (RuleTransition) getTransitionMap().get(event);
    }

    /**
     * Lazily creates and returns a mapping from the events to 
     * outgoing transitions of this state.
     */
    KeySet<Event,GraphTransition> getTransitionMap() {
        if (this.transitionMap == null) {
            this.transitionMap = computeTransitionMap();
        }
        return this.transitionMap;
    }

    /**
     * Computes a mapping from the events to the 
     * outgoing transitions of this state.
     */
    private KeySet<Event,GraphTransition> computeTransitionMap() {
        KeySet<Event,GraphTransition> result =
            new KeySet<Event,GraphTransition>() {
                @Override
                protected Event getKey(Object value) {
                    Event result = null;
                    if (value instanceof GraphTransition) {
                        result = ((GraphTransition) value).getEvent();
                    }
                    return result;
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
     * Reconstructs the set of {@link groove.lts.RuleTransitionStub}s from the
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
                return getEvent(key).equals(getEvent(otherKey));
            }

            @Override
            protected int getCode(GraphTransitionStub key) {
                Event keyEvent = getEvent(key);
                return keyEvent == null ? 0 : keyEvent.hashCode();
            }

            private Event getEvent(GraphTransitionStub stub) {
                if (stub instanceof RecipeEvent) {
                    return (RecipeEvent) stub;
                } else {
                    return ((RuleTransitionStub) stub).getEvent(getState());
                }
            }
        };
    }

    /**
     * Returns all unexplored matches of the state, insofar they can be determined
     * without cooking any currently uncooked successor states. 
     * @return set of unexplored matches
     */
    MatchResultSet getMatches() {
        if (this.matches == null) {
            this.matches = new MatchResultSet();
        }
        // try all schedules as long as this is possible
        while (trySchedule()) {
            // do nothing
        }
        return this.matches;
    }

    /** Returns the first unexplored match of the state. */
    MatchResult getMatch() {
        MatchResult result = null;
        // compute matches insofar necessary and feasible
        if (this.matches == null) {
            this.matches = new MatchResultSet();
        }
        while (this.matches.isEmpty() && trySchedule()) {
            // do nothing
        }
        // return the first match if there is one
        if (!this.matches.isEmpty()) {
            result = this.matches.iterator().next();
        }
        return result;
    }

    private boolean trySchedule() {
        boolean result = false;
        CtrlSchedule schedule = getState().getSchedule();
        if (schedule.isTried()) {
            // the schedule has been tried and has yielded matches; 
            // now see if at least one match has resulted
            // in a transition to a present state, or all matches
            // have resulted in transitions to absent states
            boolean allAbsent = true;
            boolean somePresent = false;
            for (MatchResult m : this.latestMatches) {
                GraphTransition t = getTransitionMap().get(m.getEvent());
                if (t == null) {
                    allAbsent = false;
                } else {
                    GraphState target = t.target();
                    if (target.isPresent()) {
                        somePresent = true;
                        break;
                    }
                    allAbsent &= target.isAbsent();
                }
            }
            if (somePresent || allAbsent) {
                // yes, there is a present outgoing transition
                // or all outgoing transitions are absent
                schedule = schedule.next(somePresent);
                getState().setSchedule(schedule);
            }
        }
        if (schedule.isFinished()) {
            this.latestMatches = EMPTY_MATCH_SET;
            maybeSetClosed();
        } else if (!schedule.isTried()) {
            this.latestMatches =
                getMatchCollector().computeMatches(schedule.getTransition());
            CtrlSchedule nextSchedule;
            if (this.latestMatches.isEmpty()) {
                // no transitions will be generated
                nextSchedule = schedule.next(false);
            } else if (schedule.next(true) == schedule.next(false)) {
                // it does not matter whether a transition is generated or not
                nextSchedule = schedule.next(false);
            } else if (schedule.isTransient()
                || !schedule.getTransition().target().isTransient()) {
                // the control transition is atomic
                // so the existence of a match guarantees the existence of a transition
                nextSchedule = schedule.next(true);
            } else {
                nextSchedule = schedule.getTriedSchedule();
            }
            getState().setSchedule(nextSchedule);
            this.matches.addAll(this.latestMatches);
            result = true;
        }
        return result;
    }

    private MatchCollector getMatchCollector() {
        if (this.matcher == null) {
            this.matcher = createMatchCollector();
        }
        return this.matcher;
    }

    /** Factory method for the match collector. */
    protected MatchCollector createMatchCollector() {
        return new MatchCollector(getState());
    }

    /** 
     * If there are no more matches, and the schedule is finished,
     * sets the state to closed.
     */
    private void maybeSetClosed() {
        if (this.matches.isEmpty() && getState().getSchedule().isFinished()) {
            getState().setClosed(true);
        }
    }

    /** Strategy object used to find the matches. */
    private MatchCollector matcher;
    /** The matches found so far for this state. */
    private MatchResultSet matches;
    /** The matches found during the latest successful call to {@link #trySchedule()}. */
    private MatchResultSet latestMatches;
    /**
     * The set of outgoing transitions computed for the underlying graph,
     * for every class of graph transitions.
     */
    private Set<GraphTransitionStub> stubSet;
    /** The graph state of this cache. */
    private final AbstractGraphState state;
    /** The system record generating this state. */
    private final SystemRecord record;
    /** The delta with respect to the state's parent. */
    private DeltaApplier delta;
    /** Cached map from events to target transitions. */
    private KeySet<Event,GraphTransition> transitionMap;
    /** Cached graph for this state. */
    private DeltaHostGraph graph;
    /** 
     * Set of direct uncooked predecessor states, maintained as long this state 
     * is transient and uncooked. These states are notified as soon as this state is
     * discovered to be non-transient or cooked.
     * @see #notifyCooked()
     */
    private final List<StateCache> rawParents = new ArrayList<StateCache>();
    /** Transitively closed set of uncooked ancestors. This is maintained
     * to ensure that cycles of transient states are correctly cooked.
     */
    private final Set<GraphState> rawAncestors = new GTS.NormalisedStateSet();
    /** Number of transient, uncooked successors. */
    private int rawChildCount;
    /** Flag indicating if the associated state is known to be present. */
    private boolean present;
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
    /** Unique empty match set. */
    static private final MatchResultSet EMPTY_MATCH_SET = new MatchResultSet();
}
