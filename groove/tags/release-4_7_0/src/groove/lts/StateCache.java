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
import groove.control.CtrlTransition;
import groove.trans.DeltaApplier;
import groove.trans.DeltaHostGraph;
import groove.trans.HostEdge;
import groove.trans.HostElement;
import groove.trans.HostGraph;
import groove.trans.HostNode;
import groove.trans.RuleApplication;
import groove.trans.SystemRecord;
import groove.util.KeySet;
import groove.util.Pair;
import groove.util.SetView;
import groove.util.TreeHashSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
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
        if (trans instanceof RuleTransition) {
            this.matches.remove(trans.getKey());
            if (trans.isPartial()) {
                addOutPartial((RuleTransition) trans);
            }
        }
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
     * Adds an outgoing partial transition to this cache.
     * @param partial new outgoing partial rule transition from this state
     */
    private void addOutPartial(RuleTransition partial) {
        notifyPartial(partial, partial);
        GraphState child = partial.target();
        StateCache childCache = child.getCache();
        this.present |= childCache.present;
        if (child.isTransient()) {
            if (!child.isDone()) {
                // we've reached a transient raw state
                childCache.rawParents.add(Pair.newPair(this, partial));
            }
            // add the child partials to this cache
            for (RuleTransition childPartial : child.getCache().partials) {
                notifyPartial(childPartial, partial);
            }
        }
    }

    /**
     * Notifies the cache of the existence of a reachable partial transition.
     * @param partial partial transition reachable from this state
     * @param initial initial transition of a potential recipe transition ending
     * on the new partial transition
     */
    private void notifyPartial(RuleTransition partial, RuleTransition initial) {
        // maybe add the transition target to the transient open states
        GraphState target = partial.target();
        if (target.isTransient() && !target.isClosed()) {
            this.transientOpens.add(target);
        }
        // add the partial if it was not already known
        if (getState().isTransient()) {
            if (this.partials.add(partial)) {
                this.present |= !target.isTransient();
                // notify all parents of the new partial
                for (Pair<StateCache,RuleTransition> parent : this.rawParents) {
                    parent.one().notifyPartial(partial, parent.two());
                }
            }
        } else if (!target.isTransient()) {
            // add recipe transition if there was none
            getState().getGTS().addTransition(
                new RecipeTransition(getState(), initial, target));
        }
    }

    /** 
     * Callback method invoked when the state has been closed.
     */
    void notifyClosed() {
        if (getState().isTransient()) {
            // notify all parents of the closure
            fireChanged(getState());
        }
        if (this.transientOpens.isEmpty()) {
            setStateDone();
        }
    }

    /** Callback method invoked when a child closed or became non-transient. */
    private void notifyChildChanged(GraphState child, RuleTransition initial) {
        if (this.transientOpens.remove(child)) {
            this.present |= !child.isTransient();
            if (getState().isTransient()) {
                // notify all parents of the change
                fireChanged(child);
            }
        }
        if (!getState().isTransient() && !child.isTransient()) {
            getState().getGTS().addTransition(
                new RecipeTransition(getState(), initial, child));
        }
        if (this.transientOpens.isEmpty() && getState().isClosed()) {
            setStateDone();
        }
    }

    /** 
     * Callback method invoked when the state has become done.
     * All raw parents should already know this (they were notified when the state closed).
     */
    void notifyDone() {
        this.rawParents.clear();
    }

    /** 
     * Callback method invoked when the state closed or became non-transient.
     * Notifies all raw predecessors.
     */
    void fireChanged(GraphState state) {
        // notify all parents of the change
        for (Pair<StateCache,RuleTransition> parent : this.rawParents) {
            parent.one().notifyChildChanged(state, parent.two());
        }
    }

    private void setStateDone() {
        getState().setDone(this.present);
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

    /** Indicates if a path to a non-transient state has been found. */
    final boolean isPresent() {
        return this.present;
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
        KeySet<GraphTransitionKey,GraphTransition> result =
            new KeySet<GraphTransitionKey,GraphTransition>() {
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
            protected boolean areEqual(GraphTransitionStub stub,
                    GraphTransitionStub otherStub) {
                return getKey(stub).equals(getKey(otherStub));
            }

            @Override
            protected int getCode(GraphTransitionStub stub) {
                GraphTransitionKey keyEvent = getKey(stub);
                return keyEvent == null ? 0 : keyEvent.hashCode();
            }

            private GraphTransitionKey getKey(GraphTransitionStub stub) {
                return stub.getKey(getState());
            }
        };
    }

    /**
     * Returns all unexplored matches of the state, insofar they can be determined
     * without exploring any currently raw successor states. 
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
        boolean isTransient = schedule.isTransient();
        if (schedule.isTried()) {
            // the schedule has been tried and has yielded matches; 
            // now see if at least one match has resulted
            // in a transition to a present state, or all matches
            // have resulted in transitions to absent states
            boolean allAbsent = true;
            boolean somePresent = false;
            Iterator<MatchResult> matchIter = this.latestMatches.iterator();
            while (matchIter.hasNext()) {
                MatchResult m = matchIter.next();
                GraphTransition t = getTransitionMap().get(m);
                if (t == null) {
                    allAbsent = false;
                } else {
                    GraphState target = t.target();
                    if (target.isPresent()) {
                        somePresent = true;
                        break;
                    } else if (target.isAbsent()) {
                        matchIter.remove();
                    } else {
                        allAbsent = false;
                    }
                }
            }
            if (somePresent || allAbsent) {
                // yes, there is a present outgoing transition
                // or all outgoing transitions are absent
                schedule = schedule.next(somePresent);
                getState().setSchedule(schedule);
                this.latestMatches = EMPTY_MATCH_SET;
            }
        }
        if (schedule.isFinished()) {
            maybeSetClosed();
        } else if (!schedule.isTried()) {
            // flag collecting if any of the transitions in this schedule
            // have a transient target
            boolean transientTargets = false;
            List<MatchResult> latestMatches = new LinkedList<MatchResult>();
            for (CtrlTransition ct : schedule.getTransitions()) {
                latestMatches.addAll(getMatchCollector().computeMatches(ct));
                transientTargets |= ct.target().isTransient();
            }
            CtrlSchedule nextSchedule;
            if (latestMatches.isEmpty()) {
                // no transitions will be generated
                nextSchedule = schedule.next(false);
            } else if (schedule.next(true) == schedule.next(false)) {
                // it does not matter whether a transition is generated or not
                nextSchedule = schedule.next(false);
            } else if (schedule.isTransient() || !transientTargets) {
                // the control transition is atomic
                // so the existence of a match guarantees the existence of a transition
                nextSchedule = schedule.next(true);
            } else {
                nextSchedule = schedule.toTriedSchedule();
                this.latestMatches = latestMatches;
            }
            getState().setSchedule(nextSchedule);
            this.matches.addAll(latestMatches);
            result = true;
        }
        if (isTransient && !getState().isTransient()) {
            this.present = true;
            fireChanged(getState());
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

    @Override
    public String toString() {
        return "StateCache [state=" + this.state + "]";
    }

    /** Strategy object used to find the matches. */
    private MatchCollector matcher;
    /** The matches found so far for this state. */
    private MatchResultSet matches;
    /** The matches found during the latest successful call to {@link #trySchedule()}. */
    private List<MatchResult> latestMatches;
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
    private KeySet<GraphTransitionKey,GraphTransition> transitionMap;
    /** Cached graph for this state. */
    private DeltaHostGraph graph;
    /** 
     * Set of incoming transitions from raw parent states.
     */
    private final List<Pair<StateCache,RuleTransition>> rawParents =
        new ArrayList<Pair<StateCache,RuleTransition>>();
    /** Set of reachable transient open states. */
    private final Set<GraphState> transientOpens = new HashSet<GraphState>();
    /** Set of reachable partial rule transitions. */
    private final Set<RuleTransition> partials = new HashSet<RuleTransition>();
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
    static private final List<MatchResult> EMPTY_MATCH_SET =
        Collections.emptyList();
}
