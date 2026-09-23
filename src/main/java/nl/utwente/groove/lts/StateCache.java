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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.control.Assignment;
import nl.utwente.groove.control.NestedCall;
import nl.utwente.groove.grammar.Action;
import nl.utwente.groove.grammar.Action.Role;
import nl.utwente.groove.grammar.Callable.Kind;
import nl.utwente.groove.grammar.CheckPolicy;
import nl.utwente.groove.grammar.Recipe;
import nl.utwente.groove.grammar.host.DeltaApplier;
import nl.utwente.groove.grammar.host.DeltaHostGraph;
import nl.utwente.groove.grammar.host.HostEdge;
import nl.utwente.groove.grammar.host.HostElement;
import nl.utwente.groove.grammar.host.HostGraph;
import nl.utwente.groove.grammar.host.HostNode;
import nl.utwente.groove.grammar.host.ValueNode;
import nl.utwente.groove.lts.GraphTransition.Claz;
import nl.utwente.groove.transform.Record;
import nl.utwente.groove.transform.RuleApplication;
import nl.utwente.groove.util.Exceptions;
import nl.utwente.groove.util.QualName;
import nl.utwente.groove.util.Strings;
import nl.utwente.groove.util.TreeHashSet;
import nl.utwente.groove.util.cache.Cache;
import nl.utwente.groove.util.collect.KeySet;
import nl.utwente.groove.util.collect.SetView;

/**
 * Caches information of a state. Cached are the graph, the set of outgoing
 * transitions, and the delta with respect to the previous state.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class StateCache implements Cache {
    /**
     * Constructs a cache for a given state.
     * After creation, the cache should be initialised by a call to #init
     */
    protected StateCache(AbstractGraphState state) {
        this.state = state;
        this.record = state.getRecord();
        this.freezeGraphs = this.record.isCollapse();
        this.graphFactory
            = DeltaHostGraph.getInstance(state.hasSimpleGraph(), this.record.isCopyGraphs());
        if (DEBUG && state.isFull()) {
            System.out.printf("Recreating cache for full state %s%n", state);
        }
    }

    /** Adds a graph transition to the data structures stored in this cache. */
    boolean addTransition(GraphTransition trans) {
        assert this.initialised;
        assert trans.source() == getState();
        boolean result = getStubSet().add(trans.toStub());
        if (result) {
            if (this.transitionMap != null) {
                this.transitionMap.add(trans);
            }
            if (getState().isClosed() && getState().getGTS().isStoring()) {
                // the state copied its stubs at closure; a recipe transition
                // arrives later, once its run is explored, and must be stored
                // as well or it dies with the (by then collectable) cache
                getState().addStoredTransitionStub(trans.toStub());
            }
        }
        if (trans instanceof RuleTransition ruleTrans) {
            getMatches().remove(trans.getKey());
            if (result && trans.isPartialStep()) {
                registerOutPartial(ruleTrans);
            }
        }
        if (getMatches().isFinished()) {
            getState().setClosed();
        }
        return result;
    }

    Set<? extends GraphTransition> getTransitions(final GraphTransition.Claz claz) {
        if (claz == GraphTransition.Claz.ANY) {
            return getTransitionMap();
        } else {
            return new SetView<>(getTransitionMap()) {
                @Override
                public boolean approves(@Nullable Object obj) {
                    return obj instanceof GraphTransition trans && claz.admits(trans);
                }
            };
        }
    }

    final AbstractGraphState getState() {
        return this.state;
    }

    /** The graph state of this cache. */
    private final AbstractGraphState state;

    /**
     * Lazily creates and returns the graph of the underlying state. This is
     * only supported if the state is a {@link GraphNextState}
     * @throws IllegalStateException if the underlying state is not a
     *         {@link GraphNextState}
     */
    final DeltaHostGraph getGraph() {
        var result = this.graph;
        if (result == null) {
            result = this.graph = computeGraph();
        }
        return result;
    }

    /** Indicates if this cache currently stores a graph. */
    final boolean hasGraph() {
        return this.graph != null;
    }

    /** Cached graph for this state. */
    private @Nullable DeltaHostGraph graph;

    /**
     * Lazily creates and returns the delta with respect to the
     * parent state.
     */
    final DeltaApplier getDelta() {
        var result = this.delta;
        if (result == null) {
            result = this.delta = createDelta();
        }
        assert result != null : "State %s has no parent, hence no delta".formatted(this.state);
        return result;
    }

    /** The delta with respect to the state's parent. */
    private @Nullable DeltaApplier delta;

    /**
     * Callback factory method for a rule application on the basis of this
     * state; returns {@code null} if the state has no parent.
     */
    private @Nullable DeltaApplier createDelta() {
        DeltaApplier result = null;
        if (this.state instanceof DefaultGraphNextState state) {
            HostGraph source = state.source().getGraph();
            // the added node and edge identities were computed at state
            // creation and are replayed by every derivation
            result = new RuleApplication(state.getEvent(), source, state.getAddedNodes(),
                state.getAddedEdges());
        }
        return result;
    }

    /**
     * Compute the graph from the information in the state.
     * If the state has neither a frozen graph nor a live cache, the graph is
     * reconstructed by replaying deltas forward from the nearest ancestor that
     * still has one. The reconstruction path thus depends on GC timing (on
     * which caches happen to have survived); in combination with the structure
     * sharing of {@link DeltaHostGraph} (see the note on element set iteration
     * order there), the element sets of the resulting graph, though equal in
     * content to any previous materialisation, may iterate in a different
     * order.
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
        if (getState().isFull() && getState().isError()) {
            if (getState().getGTS().getTypePolicy() != CheckPolicy.OFF) {
                // apparently we're reconstructing the graph after the state was already
                // filled and found to be erroneous; so reconstruct the type errors
                result.addErrors(result.checkTypeConstraints());
            }
            // check the property and deadlock constraints
            GTS gts = getState().getGTS();
            // check for liveness
            boolean alive = false;
            // collect all property matches
            Set<Action> erroneous = new HashSet<>(gts.getGrammar().getActions(Role.INVARIANT));
            for (GraphTransition trans : getTransitions(GraphTransition.Claz.PUBLIC)) {
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
                          Strings.toString(actions.toArray(), "'", "'", "', '", "' and '"));
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
        var result = this.transitionMap;
        if (result == null) {
            result = this.transitionMap = computeTransitionMap();
        }
        return result;
    }

    /** Cached map from events to target transitions. */
    private @Nullable KeySet<GraphTransitionKey,GraphTransition> transitionMap;

    /**
     * Computes a mapping from the events to the
     * outgoing transitions of this state.
     */
    private KeySet<GraphTransitionKey,GraphTransition> computeTransitionMap() {
        assert this.initialised;
        KeySet<GraphTransitionKey,GraphTransition> result = new KeySet<>() {
            @Override
            protected GraphTransitionKey getKey(@Nullable Object value) {
                assert value != null; // the set holds transitions only
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
        var result = this.stubSet;
        if (result == null) {
            result = this.stubSet = computeStubSet();
        }
        return result;
    }

    /**
     * Clears the cached outgoing-transition structures (the stub set and
     * the transition map derived from it), so that they are recomputed
     * from the stored transition stubs on next use. Used after an unstored
     * exploration (see {@link GTS#retainTraces}), when the stored stubs of
     * a retained state have been reduced to the spanning stubs while the
     * live cache still holds all discovered transitions.
     */
    void clearTransitionCache() {
        this.stubSet = null;
        this.transitionMap = null;
    }

    /**
     * The set of outgoing transitions computed for the underlying graph,
     * for every class of graph transitions.
     */
    private @Nullable Set<GraphTransitionStub> stubSet;

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
                return getKey(stub).hashCode();
            }

            private GraphTransitionKey getKey(GraphTransitionStub stub) {
                return stub.getKey(getState());
            }
        };
    }

    /** Returns the object keeping track of the explored matches of this state. */
    StateMatches getMatches() {
        var result = this.stateMatches;
        if (result == null) {
            result = this.stateMatches = new StateMatches(this);
        }
        return result;
    }

    private @Nullable StateMatches stateMatches;

    /** Factory method for a match collector. */
    protected MatchCollector createMatchCollector() {
        return new MatchCollector(getState());
    }

    @Override
    public String toString() {
        return "StateCache [state=" + this.state + "]";
    }

    /** The system record generating this state. */
    private final Record record;
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

    @Override
    public void init() {
        if (this.initialised) {
            return;
        }
        this.initialised = true;
        GraphState state = getState();
        boolean stateIsFull = state.isFull();
        assert stateIsFull
            || state.getActualFrame().getTransience() == state.getPrimeFrame().getTransience();
        this.knownInner = state.isInner();
        if (!state.getPrimeFrame().isInner()) {
            this.forwTarget = EMPTY_TARGET_SET;
        } else if (!stateIsFull) {
            this.forwTarget = new LinkedHashSet<>();
            if (!this.knownInner) {
                this.forwTarget.add(new RecipeTarget(state));
            }
        }
        // for a full state with an inner prime frame, the targets are
        // recomputed on demand (see #getForwTarget)
        this.knownTransience = state.getActualFrame().getTransience();
        this.knownAbsence = stateIsFull
            ? state.getAbsence()
            : this.knownTransience;
    }

    /** Flag indicating that {@link #init()} has been invoked. */
    private boolean initialised = false;

    /**
     * Recomputes the reachable recipe targets of a full state whose prime frame
     * is inner, by a forward search over the states created inside the recipe;
     * used when the cache of such a state is recreated after having been collected.
     */
    private Set<RecipeTarget> computeForwOuter() {
        assert getState().isFull() && getState().getPrimeFrame().isInner();
        Set<RecipeTarget> result = new LinkedHashSet<>();
        Set<GraphState> known = new LinkedHashSet<>();
        Queue<StateCache> queue = new LinkedList<>();
        queue.add(this);
        while (!queue.isEmpty()) {
            var source = queue.poll();
            assert source != null; // queue is non-empty
            var state = source.getState();
            assert state.getPrimeFrame().isInner();
            if (!state.isInner()) {
                // the state left the recipe through a verdict
                result.add(new RecipeTarget(state));
            }
            // follow the steps of this recipe run, also from a state that left
            // the recipe through a verdict after having generated them; the
            // launches of other recipes from such a state do not belong to it
            for (var trans : state.getTransitions(Claz.NON_ABSENT)) {
                if (!(trans instanceof RuleTransition ruleTrans) || !ruleTrans.isInnerStep()
                    || ruleTrans.getStep().isLaunch()) {
                    continue;
                }
                var target = ruleTrans.target();
                if (known.add(target)) {
                    if (target.getPrimeFrame().isInner()) {
                        queue.add(target.getCache());
                    } else {
                        result.add(new RecipeTarget(ruleTrans));
                    }
                }
            }
        }
        return result;
    }

    /*
     * Bookkeeping of the transient region behind this state (gh #924).
     *
     * Full-ness and absence are derived from the direct successors only, and
     * changes are propagated backwards over the direct predecessors: a state
     * is full when it is closed and every transient successor is full or has
     * become steady; its absence is the minimum of its own transience and the
     * absence of its successors, which only ever decreases. Recipe targets
     * are propagated backwards over the direct inner predecessors, to the
     * launches. Cycles inside a transient region (loops in a recipe or atomic
     * block) defeat the local full-ness rule, since no member of the cycle
     * ever sees all its successors full; a closed state whose successors are
     * all closed therefore searches forward and, if it finds no open state,
     * declares the entire visited set full.
     *
     * The predecessor lists live in the caches of the non-full states, which
     * are strongly referenced until they are full, so a garbage collection
     * cannot lose them; an explicit clearing of the cache of a closed but not
     * yet full transient state does lose them, as it did before.
     */

    /**
     * Sources of the partial transitions into this state that were registered
     * while this state was transient and not full; they wait for this state to
     * close and to become full or steady, and take over decreases of its absence.
     */
    private List<StateCache> preds = Collections.emptyList();

    /** Number of registered transient successors that are not yet full or steady. */
    private int pendingCount;

    /** Number of registered transient successors that are not yet closed, full or steady. */
    private int openCount;

    /** Inner states with an inner step into this (inner-prime, non-full) state. */
    private List<StateCache> innerPreds = Collections.emptyList();

    /** Recipe launches into this (inner-prime, non-full) state. */
    private List<RuleTransition> launches = Collections.emptyList();

    /**
     * Returns the recipe targets known to be reachable from this state; complete
     * once the state is full. For a full state whose cache has been recreated,
     * the targets are recomputed on first demand rather than at initialisation,
     * so that the recreation of one cache does not recursively recompute the
     * targets of all reachable inner states.
     */
    private Set<RecipeTarget> getForwTarget() {
        var result = this.forwTarget;
        if (result == null) {
            result = this.forwTarget = computeForwOuter();
        }
        return result;
    }

    /** The recipe targets known to be reachable from this state; see {@link #getForwTarget()}.
     * Only {@code null} for a full state with an inner prime frame, until first demanded. */
    private @Nullable Set<RecipeTarget> forwTarget;

    /**
     * Notifies the cache of the addition of an outgoing partial transition.
     * @param partial new outgoing partial rule transition from this state
     */
    void registerOutPartial(RuleTransition partial) {
        init();
        assert partial.isPartialStep();
        assert partial.source() == getState();
        GraphState target = partial.target();
        if (target.getActualFrame().isRemoved()) {
            return;
        }
        boolean targetFull = target.isFull();
        // recipe transitions and targets
        if (partial.getStep().isLaunch()) {
            if (target.getPrimeFrame().isInner()) {
                var targetCache = target.getCache();
                targetCache.getForwTarget().forEach(t -> addRecipeTransition(partial, t));
                if (!targetFull) {
                    targetCache.launches = add(targetCache.launches, partial);
                }
            } else {
                // it's a single-step recipe transition
                addRecipeTransition(partial, new RecipeTarget(partial));
            }
        } else if (partial.isInnerStep()) {
            // a non-launch inner step starts from a state created inside the
            // recipe, which may meanwhile have left it through a verdict;
            // the targets reached through the step count for the recipe
            // transitions all the same
            assert getState().getPrimeFrame().isInner();
            if (target.getPrimeFrame().isInner()) {
                var targetCache = target.getCache();
                if (!targetFull) {
                    targetCache.innerPreds = add(targetCache.innerPreds, this);
                }
                targetCache.getForwTarget().forEach(this::addTarget);
            } else {
                // the step finishes the recipe
                addTarget(new RecipeTarget(partial));
            }
        }
        // absence
        lowerAbsence(target.getAbsence());
        // full-ness
        if (target.isTransient() && !targetFull) {
            var targetCache = target.getCache();
            targetCache.preds = add(targetCache.preds, this);
            this.pendingCount++;
            if (!target.isClosed()) {
                this.openCount++;
            }
        }
    }

    /** Adds an element to a list, replacing the shared empty list by a fresh one. */
    static private <T> List<T> add(List<T> list, T elem) {
        List<T> result = list.isEmpty()
            ? new ArrayList<>(2)
            : list;
        result.add(elem);
        return result;
    }

    /**
     * Callback method invoked when the state has been closed.
     * This may involve a simultaneous change in (known) transience.
     */
    void registerClosure() {
        init();
        Deque<StateCache> agenda = new ArrayDeque<>();
        // the predecessors are notified of the closure before a possible
        // steadiness, which drops them
        for (var pred : this.preds) {
            pred.openCount--;
        }
        agenda.add(this);
        agenda.addAll(this.preds);
        int transience = getState().getActualFrame().getTransience();
        if (transience < this.knownTransience) {
            registerTransienceChange(agenda);
        }
        runAgenda(agenda);
    }

    /** Notifies the cache of a decrease in transient depth of the control frame. */
    final void registerTransienceChange() {
        init();
        Deque<StateCache> agenda = new ArrayDeque<>();
        registerTransienceChange(agenda);
        runAgenda(agenda);
    }

    /**
     * Registers a decrease in transient depth, adding the caches whose
     * full-ness may have changed as a consequence to a given agenda.
     */
    private void registerTransienceChange(Deque<StateCache> agenda) {
        var state = getState();
        var transience = state.getActualFrame().getTransience();
        assert transience < this.knownTransience;
        this.knownTransience = transience;
        if (this.knownInner && !state.isInner()) {
            // the state changed from inner to outer, so it is a recipe target itself
            this.knownInner = false;
            addTarget(new RecipeTarget(state));
        }
        lowerAbsence(transience);
        if (transience == 0) {
            // the predecessors no longer wait for this state
            notifyDone(agenda);
        }
    }

    /**
     * Lowers the known absence to a given level, if that is lower than the
     * current level, and propagates the decrease to the predecessors.
     */
    private void lowerAbsence(int absence) {
        if (absence < this.knownAbsence) {
            this.knownAbsence = absence;
            Deque<StateCache> agenda = new ArrayDeque<>(this.preds);
            while (!agenda.isEmpty()) {
                var next = agenda.poll();
                assert next != null; // agenda is non-empty
                if (absence < next.knownAbsence) {
                    next.knownAbsence = absence;
                    agenda.addAll(next.preds);
                }
            }
        }
    }

    /**
     * Adds a recipe target to the known reachable targets of this state, and
     * propagates it to the launches and inner predecessors.
     */
    private void addTarget(RecipeTarget target) {
        if (!getForwTarget().add(target)) {
            return;
        }
        Deque<StateCache> agenda = new ArrayDeque<>();
        agenda.add(this);
        while (!agenda.isEmpty()) {
            var next = agenda.poll();
            assert next != null; // agenda is non-empty
            for (var launch : next.launches) {
                addRecipeTransition(launch, target);
            }
            for (var pred : next.innerPreds) {
                if (!pred.getState().isFull() && pred.getForwTarget().add(target)) {
                    agenda.add(pred);
                }
            }
        }
    }

    /** Tests the full-ness of the caches on a given agenda, until it is empty. */
    private void runAgenda(Deque<StateCache> agenda) {
        while (!agenda.isEmpty()) {
            var next = agenda.poll();
            assert next != null; // agenda is non-empty
            next.testFull(agenda);
        }
    }

    /**
     * Sets this state to full if it is closed and its transient successors
     * are done; if they are all closed but not all done, searches forward
     * for a cycle of closed states.
     * @param agenda receives the predecessors whose full-ness is to be retested
     */
    private void testFull(Deque<StateCache> agenda) {
        var state = getState();
        if (state.isFull() || !state.isClosed()) {
            return;
        }
        if (this.pendingCount == 0) {
            setFull(agenda);
        } else if (this.openCount == 0) {
            searchFull(agenda);
        }
    }

    /**
     * Searches forward from this (closed) state over the transient states
     * that are not yet full; if none of them is open, they are all full.
     */
    private void searchFull(Deque<StateCache> agenda) {
        assert getState().isClosed() && this.openCount == 0 && this.pendingCount > 0;
        Set<StateCache> visited = new LinkedHashSet<>();
        Deque<StateCache> stack = new ArrayDeque<>();
        visited.add(this);
        stack.push(this);
        while (!stack.isEmpty()) {
            var next = stack.pop();
            if (next.openCount > 0) {
                return;
            }
            for (var trans : next.getTransitionMap()) {
                var target = trans.target();
                if (!target.isTransient() || target.isFull()
                    || target.getActualFrame().isRemoved()) {
                    continue;
                }
                if (!target.isClosed()) {
                    return;
                }
                var targetCache = target.getCache();
                if (visited.add(targetCache)) {
                    stack.push(targetCache);
                }
            }
        }
        for (var cache : visited) {
            cache.setFull(agenda);
        }
    }

    /** Sets this state to full, with the known absence, and notifies the predecessors. */
    private void setFull(Deque<StateCache> agenda) {
        assert getState().isClosed();
        getState().setFull(this.knownAbsence);
        notifyDone(agenda);
        // no new targets can be found from a full state
        this.innerPreds = Collections.emptyList();
        this.launches = Collections.emptyList();
    }

    /**
     * Notifies the predecessors that this state is done, i.e., full or steady,
     * and drops them; if the state is not yet closed, its closure is thereby
     * pre-empted.
     */
    private void notifyDone(Deque<StateCache> agenda) {
        boolean closed = getState().isClosed();
        for (var pred : this.preds) {
            if (!pred.getState().isFull()) {
                pred.pendingCount--;
                if (!closed) {
                    pred.openCount--;
                }
                agenda.add(pred);
            }
        }
        this.preds = Collections.emptyList();
    }

    /** Adds a new recipe transition to the GTS, with a given initial (partial) rule transition
     * and to a given recipe target.
     */
    private void addRecipeTransition(RuleTransition partial, RecipeTarget target) {
        var trans = new RecipeTransition(partial, target.outValues(), target.state());
        getState().getGTS().addTransition(trans);
    }

    /**
     * Returns the (known) absence level of the state.
     * This is {@link Status#MAX_ABSENCE} if the state is erroneous,
     * otherwise it is the minimum absence level of the reachable states.
     */
    final int getAbsence() {
        return this.knownAbsence;
    }

    /** Flag indicating if this state is known to be inner. */
    private boolean knownInner;

    /** Known transience level. */
    private int knownTransience;

    /** Known absence level. */
    private int knownAbsence;

    /** Shared unmodifiable empty set of recipe targets. */
    static private final Set<RecipeTarget> EMPTY_TARGET_SET = Collections.emptySet();

    /** Combination of target state and out-parameter values.
     * Equality is by content, including the out-parameter values, so that a
     * target reached along different paths is propagated only once.
     */
    private record RecipeTarget(Recipe recipe, HostNode[] outValues, GraphState state) {
        @Override
        public boolean equals(@Nullable Object obj) {
            return this == obj || obj instanceof RecipeTarget other && this.recipe.equals(other.recipe)
                && this.state.equals(other.state) && Arrays.equals(this.outValues, other.outValues);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.recipe, this.state, Arrays.hashCode(this.outValues));
        }

        /** Creates a recipe target from a graph state whose prime call stack contains the
         * out-parameter values.
         */
        RecipeTarget(GraphState target) {
            this(target.getPrimeFrame().getRecipe().get(), getOutValuesFromTarget(target), target);
        }

        /** Creates a recipe target from the last partial transition in the recipe.
         */
        RecipeTarget(RuleTransition partial) {
            this(partial.getStep().getRecipe().get(), getOutValuesFromFinalTrans(partial),
                 partial.target());
        }

        /** Computes the recipe out-parameter values by reconstructing the final transition. */
        static private HostNode[] getOutValuesFromFinalTrans(RuleTransition partial) {
            assert partial.isInnerStep() && !partial.target().getPrimeFrame().isInner();
            HostNode[] result;
            var step = partial.getStep();
            if (step.getRecipe().get().getSignature().isEmpty()) {
                result = EMPTY_OUT_VALUES;
            } else {
                var valuator = partial.getGTS().getRecord().getValuator();
                // apply the transition's push change; for this we need the rule arguments
                var anchorImages = partial.getEvent().getAnchorImages();
                valuator.setAnchorInfo(i -> (HostNode) anchorImages[i]);
                var addedNodes = partial.getAddedNodes();
                valuator.setCreatorInfo(i -> addedNodes[i]);
                Object[] stack = partial.source().getFrameStack(step.getSource());
                stack = step.getPush().apply(stack, valuator);
                // pop until the (final) switch within the outer recipe body
                var recipeFinal = step
                    .getFullStack()
                    .stream()
                    .filter(s -> s.getTemplate().filter(t -> t.hasOwner(Kind.RECIPE)).isPresent())
                    .findFirst()
                    .get();
                stack = step.getPopUntil(s -> s == recipeFinal).apply(stack);
                // now obtain the parameter values;
                // in-parameter slots are null (Source.NONE bindings)
                result = recipeFinal.onFinish().assignFinal2Par().lookup(stack);
                // map the values into the target graph through the transition
                // morphism: an out-parameter node deleted by the final step has
                // no image and becomes null (undefined), a merged node is mapped
                // to its merge target, and a possible isomorphism to the actual
                // target state is applied (see claude/recipe-outpar-deletion.md).
                // Two kinds of values have no image in the morphism (whose
                // domain is the source graph) yet must not become null: value
                // nodes computed by the final step (anchor images that are not
                // source graph nodes; canonical, hence stable under the target
                // isomorphism) and nodes created by the final step (filled in
                // from the added nodes above, so a target-graph identity
                // already; under a symmetry collapse this yields the derived
                // identity, as for the creator arguments of rule transition
                // labels). Both pass through unchanged.
                var nodeMap = partial.getMorphism().nodeMap();
                result = Assignment.map(result, n -> {
                    var image = nodeMap.get(n);
                    if (image == null && n instanceof ValueNode) {
                        image = n;
                    }
                    if (image == null) {
                        for (var added : addedNodes) {
                            if (added == n) {
                                image = n;
                                break;
                            }
                        }
                    }
                    return image;
                });
            }
            return result;
        }

        /** Computes the recipe out-parameter values from the prime frame of the target state. */
        static private HostNode[] getOutValuesFromTarget(GraphState target) {
            HostNode[] result;
            var primeFrame = target.getPrimeFrame();
            assert primeFrame.isInner() && !target.isInner();
            if (primeFrame.getRecipe().get().getSignature().isEmpty()) {
                result = EMPTY_OUT_VALUES;
            } else {
                HostNode @Nullable [] found = null;
                // look for the last frame between the state's prime and actual frames
                // that was still internal
                var stack = target.getPrimeStack();
                var context = primeFrame.getContextStack();
                var loc = primeFrame.getLocation();
                for (var call : context.outIterable()) {
                    if (call.getCall().getUnit().getKind() == Kind.RECIPE) {
                        found = loc.assignFinal2Par().lookup(stack);
                        break;
                    } else {
                        stack = call.assignFinal2Target(loc).toPop().apply(stack);
                        loc = call.onFinish();
                    }
                }
                // the prime frame is inner, so the context stack holds a recipe call
                assert found != null;
                result = found;
            }
            return result;
        }

        static private final HostNode[] EMPTY_OUT_VALUES = {};
    }

    /** Debug flag. */
    private static final boolean DEBUG = false;
}
