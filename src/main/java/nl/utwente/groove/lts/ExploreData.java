/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
 * $Id$
 */
package nl.utwente.groove.lts;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import nl.utwente.groove.control.Assignment;
import nl.utwente.groove.grammar.Callable.Kind;
import nl.utwente.groove.grammar.Recipe;
import nl.utwente.groove.grammar.host.HostNode;
import nl.utwente.groove.lts.GraphTransition.Claz;

/**
 * Information required for the proper exploration of transient states.
 * @author Arend Rensink
 * @version $Revision$
 */
class ExploreData {
    /**
     * Creates a record for a given state.
     */
    ExploreData(StateCache cache) {
        this.cache = cache;
        GraphState state = this.state = cache.getState();
        this.absence = state.getActualFrame().getTransience();
        var internal = this.primeInternal = state.getPrimeFrame().isInner();
        this.inRecipeInits = internal
            ? new ArrayList<>()
            : null;
        if (!state.isClosed()) {
            this.recipeTargets = new ArrayList<>();
            if (internal && !state.isInner()) {
                addRecipeTarget(new RecipeTarget(state));
            }
        }
    }

    @Override
    public String toString() {
        return "D-" + getState().getNumber();
    }

    /** Returns the state for which this record contains the exploration data. */
    GraphState getState() {
        return this.state;
    }

    /** The state for which this record contains the exploration data. */
    private final AbstractGraphState state;

    /** Returns the cache for which this record contains the exploration data. */
    StateCache getCache() {
        return this.cache;
    }

    /** The cache for which this record contains the exploration data. */
    private final StateCache cache;

    /**
     * Notifies the cache of the addition of an outgoing partial or internal transition.
     * @param partial new outgoing partial or internal rule transition from this state
     */
    void notifyOutPartial(RuleTransition partial) {
        if (DEBUG) {
            System.out
                .printf("Rule transition added: %s--%s-->%s%n", partial.source(), partial.label(),
                        partial.target());
        }
        assert partial.isPartialStep();
        assert partial.source() == getState();
        GraphState succ = partial.target();
        if (succ.getActualFrame().isRemoved()) {
            return;
        }
        ExploreData succData = succ.getCache().getExploreData();
        setAbsence(succData.absence);
        if (getState().isTransient()) {
            addReachable(partial);
            if (succ.isTransient()) {
                // add the child's reachable states to this cache
                for (var succReachable : succData.reachablePartials) {
                    addReachable(succReachable);
                }
                for (var succRecipeTarget : succData.getRecipeTargets()) {
                    addRecipeTarget(succRecipeTarget);
                }
                if (!succ.isFull()) {
                    succData.parentTransients.add(this);
                }
            }
        } else if (partial.getStep().isInitial()) {
            // immediately add recipe transitions to the
            // previously found recipe targets of the successor
            if (succ.isInner()) {
                succData.getRecipeTargets().forEach(t -> addRecipeTransition(partial, t));
            } else {
                addRecipeTransition(partial, new RecipeTarget(partial));
            }
            if (succ.isTransient() && !succ.isFull()) {
                succData.inRecipeInits.add(partial);
            }
        }
    }

    /**
     * Callback method invoked when the state has been closed.
     */
    void notifyClosure() {
        if (DEBUG) {
            System.out.printf("State closed: %s%n", getState());
        }
        if (getState().isTransient()) {
            // notify all parents of the closure
            fireChanged(getState(), Change.CLOSURE);
        }
        if (this.reachableTransients.isEmpty()) {
            setStateFull();
        }
    }

    /** Notifies the cache of a decrease in transient depth of the control frame. */
    final void notifyTransience(int transience) {
        if (DEBUG) {
            System.out.printf("Transient depth of %s set to %s%n", getState(), transience);
        }
        setAbsence(transience);
        Change change = isPrimeInternal() && transience == 0
            ? Change.STEADIED
            : Change.TRANSIENCE;
        fireChanged(getState(), change);
    }

    /**
     * Callback method invoked when a given reachable (child) state changed.
     * Notifies all incomplete parents and adds recipe transitions, as appropriate.
     */
    private void fireChanged(GraphState child, Change change) {
        // notify all incomplete parents of the change
        for (ExploreData parent : this.parentTransients) {
            assert parent != this;
            parent.notifyChildChanged(child, change);
        }
        if (isPrimeInternal() && change == Change.STEADIED) {
            if (DEBUG) {
                System.out.printf("Steady reachables of %s augmented by %s%n", getState(), child);
            }
            addRecipeTarget(new RecipeTarget(child));
        }
    }

    /** Callback method invoked when a (transitive) child closed or got a lower absence level.
     * @param child the changed child state
     * @param change the kind of change
     */
    private void notifyChildChanged(GraphState child, Change change) {
        if ((!child.isTransient() || child.isClosed()) && this.reachableTransients.remove(child)
            || this.reachableTransients.contains(child)) {
            setAbsence(child.getAbsence());
            fireChanged(child, change);
            if (this.reachableTransients.isEmpty() && getState().isClosed()) {
                setStateFull();
            }
        }
    }

    /**
     * Adds a reachable partial transition to this cache.
     * @param partial rule transition reachable through a sequence of partial transitions.
     */
    private void addReachable(RuleTransition partial) {
        assert this.state.isTransient();
        var target = partial.target();
        // add the partial if it was not already known
        if (this.reachablePartials.add(partial)) {
            setAbsence(target.getAbsence());
            // notify all parents of the new partial
            for (ExploreData parent : this.parentTransients) {
                parent.addReachable(partial);
            }
            if (target.isTransient() && !target.isClosed()) {
                this.reachableTransients.add(target);
            }
        }
        if (getState().getPrimeFrame().isInner() && !target.isInner()) {
            addRecipeTarget(new RecipeTarget(partial));
        }
    }

    private void setStateFull() {
        getState().setFull();
        this.parentTransients.clear();
    }

    /** Adds recipe transitions from the known recipe sources to a given recipe target. */
    private void addRecipeTarget(RecipeTarget target) {
        assert isPrimeInternal() && !target.state().isInner();
        if (DEBUG) {
            System.out.printf("Recipe targets of %s augmented by %s%n", getState(), target);
        }
        getRecipeTargets().add(target);
        for (RuleTransition source : this.inRecipeInits) {
            addRecipeTransition(source, target);
        }
    }

    /** Returns the list of reachable recipe targets.
     * Only non-{@code null} for states that started their existence as transient.
     */
    private List<RecipeTarget> getRecipeTargets() {
        //assert getState().getPrimeFrame().isInternal();
        if (this.recipeTargets == null) {
            new RecipeTargetSearch(this).run();
        }
        return this.recipeTargets;
    }

    /** List of reachable outer states, which can serve as recipe targets
     * if this state appears as target of an in-recipe transition.
     */
    private List<RecipeTarget> recipeTargets;

    private RecipeTransition createRecipeTransition(RuleTransition partial, RecipeTarget target) {
        return new RecipeTransition(partial, target.outValues, target.state());
    }

    private void addRecipeTransition(RuleTransition partial, RecipeTarget target) {
        var trans = createRecipeTransition(partial, target);
        getState().getGTS().addTransition(trans);
        if (DEBUG) {
            System.out
                .printf("Recipe transition added: %s--%s-->%s%n", trans.source(), trans.label(),
                        target);
        }
    }

    /** Sets the absence value to a new value, if it is lower than the current value. */
    private void setAbsence(int newAbsence) {
        int oldAbsence = this.absence;
        if (newAbsence < oldAbsence) {
            this.absence = newAbsence;
            getState().setAbsence(newAbsence);
        }
    }

    /**
     * Returns the (known) absence level of the state.
     * This is {@link Status#MAX_ABSENCE} if the state is erroneous,
     * otherwise it is the minimum absence level of the reachable states.
     */
    final int getAbsence() {
        return this.absence;
    }

    /**
     * Known absence level of the state.
     * The absence level is the minimum transient depth of the reachable states.
     */
    private int absence;

    /** Indicates whether (the prime frame of) this state is internal to a recipe. */
    private boolean isPrimeInternal() {
        return this.primeInternal;
    }

    /** Flag indicating if (the prime frame of) this state is internal. */
    private final boolean primeInternal;

    /**
     * List of incoming rule transitions that are initial steps of a recipe.
     */
    private final List<RuleTransition> inRecipeInits;
    /**
     * Collection of transient direct predecessor states.
     */
    private final List<ExploreData> parentTransients = new ArrayList<>();
    /** Transitively closed set of reachable transient open states. */
    private final Set<GraphState> reachableTransients = new HashSet<>();
    /** Transitively closed set of reachable partial transitions. */
    private final Set<RuleTransition> reachablePartials = new HashSet<>();

    private final static boolean DEBUG = false;

    /**
     * Helper class to reconstruct the top-level reachable fields
     * of collected caches (of complete but internal states).
     */
    private static class RecipeTargetSearch {
        RecipeTargetSearch(ExploreData data) {
            assert data.getState().getPrimeFrame().isInner();
            if (DEBUG) {
                System.out.printf("Constructing top-level reachables of %s%n", data.getState());
            }
            addData(data);
        }

        /** Runs the reconstruction algorithm. */
        void run() {
            build();
            propagate();
            fill();
        }

        /**
         * Builds the backwards map and result map.
         */
        private void build() {
            while (!this.queue.isEmpty()) {
                ExploreData source = this.queue.poll();
                for (GraphTransition trans : source.getState().getTransitions(Claz.NON_ABSENT)) {
                    var target = trans.target();
                    assert !target.isInner() || trans.target().isFull();
                    if (target.getPrimeFrame().isInner()) {
                        ExploreData targetData = target.getCache().getExploreData();
                        addData(targetData);
                        this.backward.get(targetData).add(source);
                    } else {
                        this.resultMap.get(source).add(new RecipeTarget((RuleTransition) trans));
                    }
                }
            }
            if (DEBUG) {
                System.out.printf("Backward reachability map: %s%n", this.backward);
                System.out.printf("Intermediate result map: %s%n", this.resultMap);
            }
        }

        /** Adds an {@link ExploreData} item to the data structures}. */
        private void addData(ExploreData data) {
            var state = data.getState();
            assert state.getPrimeFrame().isInner();
            // to avoid circularity, only add if the data was not already added
            if (!this.backward.containsKey(data)) {
                if (DEBUG) {
                    System.out.printf("Adding data for %s%n", state);
                }
                this.backward.put(data, new HashSet<>());
                Set<RecipeTarget> resultEntry = new LinkedHashSet<>();
                var targets = data.recipeTargets;
                if (targets == null) {
                    // this cache was also lost, we're going to traverse further
                    if (state.isInner()) {
                        if (DEBUG) {
                            System.out.printf("Traversing further for %s%n", state);
                        }
                        this.queue.add(data);
                    } else {
                        if (DEBUG) {
                            System.out.printf("Recording top-level status for %s%n", state);
                        }
                        resultEntry.add(new RecipeTarget(state));
                    }
                } else {
                    // this state still has its cache, no need to explore
                    if (DEBUG) {
                        System.out.printf("Refreshing %s as targets for %s%n", targets, state);
                    }
                    resultEntry.addAll(targets);
                }
                this.resultMap.put(data, resultEntry);
            }
        }

        /**
         * Propagates the reachables backward.
         */
        private void propagate() {
            Set<ExploreData> changed = new LinkedHashSet<>(this.resultMap.keySet());
            while (!changed.isEmpty()) {
                Iterator<ExploreData> it = changed.iterator();
                ExploreData data = it.next();
                it.remove();
                var reachables = this.resultMap.get(data);
                for (ExploreData pred : this.backward.get(data)) {
                    if (this.resultMap.get(pred).addAll(reachables)) {
                        changed.add(pred);
                        if (DEBUG) {
                            System.out
                                .printf("Adding reachables %s for %s%n", reachables,
                                        pred.getState());
                        }
                    }
                }
            }
        }

        /**
         * Stores the computed results in the topLevelReachable fields.
         */
        private void fill() {
            for (var entry : this.resultMap.entrySet()) {
                var topLevelReachables = new ArrayList<>(entry.getValue());
                ExploreData keyData = entry.getKey();
                if (DEBUG) {
                    System.out
                        .printf("Top-level reachables of %s determined as %s%n", keyData.getState(),
                                topLevelReachables);
                }
                keyData.recipeTargets = topLevelReachables;
            }
        }

        /** backward map from states to direct predecessors. */
        private final Map<ExploreData,Set<ExploreData>> backward = new LinkedHashMap<>();
        /** map from states to top level reachables. */
        private final Map<ExploreData,Set<RecipeTarget>> resultMap = new LinkedHashMap<>();
        /** queue of outstanding states to be explored. */
        private final Queue<ExploreData> queue = new LinkedList<>();
    }

    /** Type of propagated change. */
    enum Change {
        /** The transient depth decreased (but not to 0) because an atomic block was exited. */
        TRANSIENCE,
        /** The state went from open to closed. */
        CLOSURE,
        /** The state went from transient to steady. */
        STEADIED,;
    }

    /** Combination of target state and out-parameter values. */
    private record RecipeTarget(Recipe recipe, HostNode[] outValues, GraphState state) {
        /** Creates a recipe target from a graph state whose prime call stack contains the
         * out-parameter values.
         */
        RecipeTarget(GraphState state) {
            this(state.getPrimeFrame().getRecipe().get(), getOutValues(state), state);
            assert state.getPrimeFrame().isInner() && !state.isInner();
        }

        /** Creates a recipe target from the last partial transition in the recipe.
         */
        RecipeTarget(RuleTransition partial) {
            this(partial.getStep().getRecipe().get(), getOutValues(partial), partial.target());
            assert partial.isInnerStep() && !state().isInner();
        }

        /** Computes the recipe out-parameter values from the prime frame of a state. */
        static private HostNode[] getOutValues(GraphState state) {
            // look for the last frame between the state's prime and actual frames
            // that was still internal; the corresponding stack contains the out-parameter values
            var frame = state.getActualFrame();
            while (!frame.isInner()) {
                var pred = frame.getPred();
                assert pred != null;
                frame = pred;
            }
            // get the stack at that frame
            Object[] stack = state.getFrameStack(frame);
            // get the out-parameter assignment
            return frame.getLocation().assignFinal2Par().lookup(stack);
        }

        /** Computes the recipe out-parameter values from the final internal transition. */
        static private HostNode[] getOutValues(RuleTransition partial) {
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
    }
}
