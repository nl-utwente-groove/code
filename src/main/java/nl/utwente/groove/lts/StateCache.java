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

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import nl.utwente.groove.control.Assignment;
import nl.utwente.groove.control.NestedCall;
import nl.utwente.groove.grammar.Action;
import nl.utwente.groove.grammar.Action.Role;
import nl.utwente.groove.grammar.Callable.Kind;
import nl.utwente.groove.grammar.CheckPolicy;
import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.grammar.Recipe;
import nl.utwente.groove.grammar.host.DeltaHostGraph;
import nl.utwente.groove.grammar.host.HostEdge;
import nl.utwente.groove.grammar.host.HostElement;
import nl.utwente.groove.grammar.host.HostGraph;
import nl.utwente.groove.grammar.host.HostNode;
import nl.utwente.groove.lts.GraphTransition.Claz;
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
        this.graphFactory
            = DeltaHostGraph.getInstance(state.isSimple(), this.record.isCopyGraphs());
        if (DEBUG && state.isFull()) {
            System.out.printf("Recreating cache for full state %s%n", state);
        }
        initTransientExploration();
    }

    /** Adds a graph transition to the data structures stored in this cache. */
    boolean addTransition(GraphTransition trans) {
        assert trans.source() == getState();
        boolean result = getStubSet().add(trans.toStub());
        if (result && this.transitionMap != null) {
            this.transitionMap.add(trans);
        }
        if (trans instanceof RuleTransition) {
            getMatches().remove(trans.getKey());
            if (trans.isPartialStep()) {
                registerOutPartial((RuleTransition) trans);
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
                public boolean approves(Object obj) {
                    return obj instanceof GraphTransition && claz.admits((GraphTransition) obj);
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
        if (this.graph == null) {
            this.graph = computeGraph();
        }
        return this.graph;
    }

    /** Indicates if this cache currently stores a graph. */
    final boolean hasGraph() {
        return this.graph != null;
    }

    /** Cached graph for this state. */
    private DeltaHostGraph graph;

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

    /** The delta with respect to the state's parent. */
    private DeltaApplier delta;

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

    /** Cached map from events to target transitions. */
    private KeySet<GraphTransitionKey,GraphTransition> transitionMap;

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
     * The set of outgoing transitions computed for the underlying graph,
     * for every class of graph transitions.
     */
    private Set<GraphTransitionStub> stubSet;

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

    /** Returns the object keeping track of the explored matches of this state. */
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

    private void initTransientExploration() {
        if (this.transientInitialised) {
            return;
        }
        GraphState state = getState();
        boolean stateIsFull = state.isFull();
        assert stateIsFull
            || state.getActualFrame().getTransience() == state.getPrimeFrame().getTransience();
        var knownInner = this.knownInner = state.isInner();
        if (!knownInner || stateIsFull) {
            this.backInner = this.forwInner = EMPTY_CACHE_SET;
            this.backLaunch = EMPTY_TRANS_SET;
        } else {
            this.backInner = new HashSet<>();
            this.backInner.add(this);
            this.forwInner = new HashSet<>();
            this.forwInner.add(this);
            this.backLaunch = new HashSet<>();
        }
        if (!state.getPrimeFrame().isInner()) {
            this.forwTarget = EMPTY_TARGET_SET;
        } else if (stateIsFull) {
            this.forwTarget = computeForwOuter();
        } else {
            this.forwTarget = new HashSet<>();
            if (!knownInner) {
                this.forwTarget.add(new RecipeTarget(state));
            }
        }
        this.backTransient = new HashSet<>();
        int knownTransience = this.knownTransience = state.getActualFrame().getTransience();
        this.knownAbsence = stateIsFull
            ? state.getAbsence()
            : knownTransience;
        if (!stateIsFull) {
            this.forwTransient = new HashSet<>();
            this.forwTransientOpen = new HashSet<>();
            if (knownTransience > 0) {
                this.backTransient.add(this);
                this.forwTransient.add(this);
                if (!state.isClosed()) {
                    this.forwTransientOpen.add(this);
                }
            }
        } else {
            this.forwTransient = EMPTY_CACHE_SET;
            this.forwTransientOpen = EMPTY_CACHE_SET;
        }
        this.transientInitialised = true;
    }

    /** Flag indicating that {@link #initTransientExploration()} has been invoked. */
    private boolean transientInitialised = false;

    private Set<RecipeTarget> computeForwOuter() {
        assert getState().isFull() && getState().getPrimeFrame().isInner();
        Set<RecipeTarget> result = new LinkedHashSet<>();
        Set<GraphState> known = new HashSet<>();
        Queue<StateCache> queue = new LinkedList<>();
        queue.add(this);
        while (!queue.isEmpty()) {
            var source = queue.poll();
            var state = source.getState();
            assert state.getPrimeFrame().isInner();
            if (state.isInner()) {
                for (var trans : source.getState().getTransitions(Claz.NON_ABSENT)) {
                    assert trans.isInnerStep();
                    var target = trans.target();
                    if (!known.add(target)) {
                        if (target.getPrimeFrame().isInner()) {
                            queue.add(target.getCache());
                        } else {
                            result.add(new RecipeTarget((RuleTransition) trans));
                        }
                    }
                }
            } else {
                result.add(new RecipeTarget(state));
            }
        }
        return result;
    }

    /** The backward reachable inner states. */
    private Set<StateCache> backInner;

    /** The forward reachable inner states. */
    private Set<StateCache> forwInner;

    /** The backward reachable recipe launches. */
    private Set<RuleTransition> backLaunch;

    /** The forward reachable recipe targets. */
    private Set<RecipeTarget> forwTarget;

    /** The backward reachable transient states, up to and including the first steady state. */
    private Set<StateCache> backTransient;

    /** The forward reachable transient states. */
    private Set<StateCache> forwTransient;

    /** The forward reachable transient open states. */
    private Set<StateCache> forwTransientOpen;

    /**
     * Notifies the cache of the addition of an outgoing partial transition.
     * @param partial new outgoing partial rule transition from this state
     */
    void registerOutPartial(RuleTransition partial) {
        initTransientExploration();
        assert partial.isPartialStep();
        assert partial.source() == getState();
        GraphState target = partial.target();
        if (target.getActualFrame().isRemoved()) {
            return;
        }
        var targetCache = target.getCache();
        // add recipe transitions
        if (this.knownInner) {
            for (var back : this.backLaunch) {
                if (target.getPrimeFrame().isInner()) {
                    targetCache.forwTarget.forEach(t -> addRecipeTransition(back, t));
                } else {
                    addRecipeTransition(back, new RecipeTarget(partial));
                }
            }
        } else if (partial.getStep().isLaunch()) {
            if (target.getPrimeFrame().isInner()) {
                targetCache.forwTarget.forEach(t -> addRecipeTransition(partial, t));
            } else {
                // it's a single-step recipe transition
                addRecipeTransition(partial, new RecipeTarget(partial));
            }
        }
        // modify the reachable inner and outer sets
        if (target.getPrimeFrame().isInner()) {
            var backInner = new HashSet<>(this.backInner);
            if (!this.state.isInner()) {
                for (var forw : targetCache.forwInner) {
                    forw.backLaunch.add(partial);
                }
            } else if (!this.forwInner.contains(targetCache)) {
                // if targetCache is already known, then the assignments below
                // will have no effect
                for (var forw : targetCache.forwInner) {
                    forw.backInner.addAll(backInner);
                    forw.backLaunch.addAll(this.backLaunch);
                }
                for (var back : backInner) {
                    back.forwInner.addAll(targetCache.forwInner);
                    back.forwTarget.addAll(targetCache.forwTarget);
                }
            }
        } else if (partial.isInnerStep()) {
            var rTarget = new RecipeTarget(partial);
            this.backInner.forEach(back -> back.forwTarget.add(rTarget));
        }
        // modify the absence level
        var targetAbsence = target.getAbsence();
        this.backTransient.forEach(back -> back.setAbsence(targetAbsence));
        // modify the reachable transient sets
        if (target.isTransient()) {
            var targetForwTransient = new HashSet<>(targetCache.forwTransient);
            if (getState().isTransient()) {
                for (var back : this.backTransient) {
                    back.forwTransient.addAll(targetForwTransient);
                    back.forwTransientOpen.addAll(targetCache.forwTransientOpen);
                }
                for (var forw : targetForwTransient) {
                    forw.backTransient.addAll(this.backTransient);
                }
            } else {
                this.forwTransient.addAll(targetForwTransient);
                this.forwTransientOpen.addAll(targetCache.forwTransientOpen);
                for (var forw : targetForwTransient) {
                    forw.backTransient.add(this);
                }
            }
        }
    }

    /**
     * Callback method invoked when the state has been closed.
     * This may involve a simultaneous change in (known) transience.
     */
    void registerClosure() {
        initTransientExploration();
        int transience = getState().getActualFrame().getTransience();
        if (transience < this.knownTransience) {
            registerTransienceChange();
        }
        testSetFull();
        registerSteadyOrClosed();
    }

    /** Removes this cache from the {@link #forwTransientOpen} of all backward transients. */
    private void registerSteadyOrClosed() {
        this.backTransient.forEach(back -> back.removeFromForwTransientOpen(this));
        removeFromForwTransientOpen(this);
    }

    /** Remove a given state from the {@link #forwTransientOpen} and possibly sets this state to full. */
    private void removeFromForwTransientOpen(StateCache forw) {
        if (this.forwTransientOpen.remove(forw)) {
            testSetFull();
        }
    }

    /**
     * Sets this state to full, also modifying the inner and transient reachable sets.
     */
    private void testSetFull() {
        assert !getState().isFull();
        if (getState().isClosed() && this.forwTransientOpen.isEmpty()) {
            getState().setFull(this.knownAbsence);
            // reset the auxiliary sets, they are no longer needed
            this.backInner.forEach(d -> d.forwInner.remove(this));
            this.backInner = EMPTY_CACHE_SET;
            //            assert this.forwInner.isEmpty() : "Full state %s reports reachable inner states %s"
            //                .formatted(this, this.forwInner);
            this.forwInner = EMPTY_CACHE_SET;
            this.backLaunch = EMPTY_TRANS_SET;
            this.backTransient.forEach(d -> d.forwTransient.remove(this));
            this.backTransient = EMPTY_CACHE_SET;
            //            assert this.forwTransient
            //                .isEmpty() : "Full state %s reports reachable transient states %s"
            //                    .formatted(this, this.forwTransient);
            this.forwTransient = EMPTY_CACHE_SET;
            this.forwTransientOpen = EMPTY_CACHE_SET;
        }
    }

    /** Notifies the cache of a decrease in transient depth of the control frame. */
    final void registerTransienceChange() {
        initTransientExploration();
        var transience = getState().getActualFrame().getTransience();
        assert transience < this.knownTransience;
        this.knownTransience = transience;
        var state = getState();
        if (this.knownInner && !state.isInner()) {
            // the state changed from inner to outer
            this.knownInner = false;
            // add incoming recipe transitions
            var target = new RecipeTarget(state);
            this.backLaunch.forEach(launch -> addRecipeTransition(launch, target));
            // update reachable recipe targets
            this.backInner.forEach(back -> back.forwTarget.add(target));
        }
        this.backTransient.forEach(back -> back.setAbsence(transience));
        if (transience == 0) {
            registerSteadyOrClosed();
        }
    }

    /** Adds a new recipe transition to the GTS, with a given initial (partial) rule transition
     * and to a given recipe target.
     */
    private void addRecipeTransition(RuleTransition partial, RecipeTarget target) {
        var trans = new RecipeTransition(partial, target.outValues(), target.state());
        getState().getGTS().addTransition(trans);
    }

    /** Sets the known absence to a given level, if it is lower than the current level. */
    private void setAbsence(int newAbsence) {
        initTransientExploration();
        if (newAbsence < this.knownAbsence) {
            this.knownAbsence = newAbsence;
        }
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

    /** Shared unmodifiable empty set of states. */
    static private final Set<StateCache> EMPTY_CACHE_SET = Collections.emptySet();
    /** Shared unmodifiable empty set of transitions. */
    static private final Set<RuleTransition> EMPTY_TRANS_SET = Collections.emptySet();
    /** Shared unmodifiable empty set of recipe targets. */
    static private final Set<RecipeTarget> EMPTY_TARGET_SET = Collections.emptySet();

    /** Combination of target state and out-parameter values. */
    private record RecipeTarget(Recipe recipe, HostNode[] outValues, GraphState state) {
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
            var step = partial.getStep();
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
                .getSwitch()
                .stream()
                .filter(s -> s.getTemplate().filter(t -> t.hasOwner(Kind.RECIPE)).isPresent())
                .findFirst()
                .get();
            stack = step.getPopUntil(s -> s == recipeFinal).apply(stack);
            // now obtain the parameter values
            var result = recipeFinal.onFinish().assignFinal2Par().lookup(stack);
            // apply the transition's permutation, if it is not the identity
            if (!partial.getMorphism().isIdentity()) {
                var nodeMap = partial.getMorphism().nodeMap();
                result = Assignment.map(result, n -> nodeMap.get(n));
            }
            return result;
        }

        /** Computes the recipe out-parameter values from the prime frame of the target state. */
        static private HostNode[] getOutValuesFromTarget(GraphState target) {
            assert target.getPrimeFrame().isInner() && !target.isInner();
            // look for the last frame between the state's prime and actual frames
            // that was still internal; the corresponding stack contains the out-parameter values
            var frame = target.getActualFrame();
            while (!frame.isInner()) {
                var pred = frame.getPred();
                assert pred != null;
                frame = pred;
            }
            // get the stack at that frame
            Object[] stack = target.getFrameStack(frame);
            // get the out-parameter assignment
            return frame.getLocation().assignFinal2Par().lookup(stack);
        }
    }

    /** Debug flag. */
    private static final boolean DEBUG = false;
}
