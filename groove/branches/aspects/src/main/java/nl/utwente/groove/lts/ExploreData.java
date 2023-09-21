/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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

import nl.utwente.groove.lts.GraphTransition.Claz;

/**
 * Information required for the proper exploration of transient states.
 * @author Arend Rensink
 * @version $Revision $
 */
class ExploreData {
    /**
     * Creates a record for a given state.
     */
    ExploreData(StateCache cache) {
        this.cache = cache;
        GraphState state = this.state = cache.getState();
        this.absence = state.getActualFrame()
            .getTransience();
        this.inRecipe = state.isInternalState();
        if (!state.isClosed()) {
            this.recipeTargets = new ArrayList<>();
            if (!state.isInternalState()) {
                this.recipeTargets.add(state);
            }
        }
        this.recipeInits = state.isInternalState() ? new ArrayList<>() : null;
    }

    @Override
    public String toString() {
        return "D-" + getState().getNumber();
    }

    /** Returns the state for which this record contains the exploration data. */
    GraphState getState() {
        return this.state;
    }

    /** Returns the cache for which this record contains the exploration data. */
    StateCache getCache() {
        return this.cache;
    }

    /**
     * Notifies the cache of the addition of an outgoing partial or internal transition.
     * @param partial new outgoing partial or internal rule transition from this state
     */
    void notifyOutPartial(RuleTransition partial) {
        if (DEBUG) {
            System.out.printf("Rule transition added: %s--%s-->%s%n",
                partial.source(),
                partial.label(),
                partial.target());
        }
        assert partial.isPartial() || partial.isInternalStep();
        assert partial.source() == getState();
        GraphState succ = partial.target();
        if (succ.getActualFrame()
            .isRemoved()) {
            return;
        }
        ExploreData succData = succ.getCache()
            .getExploreData();
        // The reason for the following exit condition is lost in the mists of time
        /*if (childCache == this) {
            return;
        }*/
        this.absence = Math.min(this.absence, succData.absence);
        if (getState().isTransient()) {
            addReachable(succ);
            if (succ.isTransient()) {
                // add the child's reachable states to this cache
                for (GraphState succReachable : succData.reachables) {
                    addReachable(succReachable);
                }
                for (GraphState succRecipeTarget : succData.getRecipeTargets()) {
                    addRecipeTarget(succRecipeTarget);
                }
                if (!succ.isDone()) {
                    succData.rawPreds.add(this);
                }
            }
        } else if (partial.getStep()
            .isInitial()) {
            // immediately add recipe transitions to the
            // previously found surface descendants of the child
            for (GraphState target : succData.getRecipeTargets()) {
                addRecipeTransition(partial, target);
            }
            if (succ.isTransient() && !succ.isDone()) {
                succData.recipeInits.add(partial);
            }
        }
    }

    /**
     * Callback method invoked when the state has been closed.
     */
    void notifyClosed() {
        if (DEBUG) {
            System.out.printf("State closed: %s%n", getState());
        }
        if (getState().isTransient()) {
            // notify all parents of the closure
            fireChanged(getState(), Change.CLOSURE);
        }
        if (this.transientOpens.isEmpty()) {
            setStateDone();
        }
    }

    /** Notifies the cache of a decrease in transient depth of the control frame. */
    final void notifyDepth(int depth) {
        if (DEBUG) {
            System.out.printf("Transient depth of %s set to %s%n", getState(), depth);
        }
        if (depth < this.absence) {
            this.absence = depth;
        }
        Change change = Change.TRANSIENCE;
        if (this.inRecipe && !getState().isInternalState()) {
            this.inRecipe = false;
            change = Change.TOP_LEVEL;
        }
        fireChanged(getState(), change);
    }

    /**
     * Adds a reachable graph state to this cache.
     * @param target graph state reachable through a non-empty sequence of partial transitions.
     */
    private void addReachable(GraphState target) {
        assert this.state.isTransient();
        // add the partial if it was not already known
        if (this.reachables.add(target)) {
            this.absence = Math.min(this.absence, target.getAbsence());
            // notify all parents of the new partial
            for (ExploreData parent : this.rawPreds) {
                parent.addReachable(target);
            }
            if (target.isTransient() && !target.isClosed()) {
                this.transientOpens.add(target);
            }
        }
        if (this.inRecipe && !target.isInternalState()) {
            addRecipeTarget(target);
        }
    }

    /**
     * Callback method invoked when a given reachable state changed.
     * Notifies all raw parents and adds recipe transitions, as appropriate.
     */
    private void fireChanged(GraphState child, Change change) {
        // notify all raw parents of the change
        for (ExploreData parent : this.rawPreds) {
            assert parent != this;
            parent.notifyChildChanged(child, change);
        }
        if (change == Change.TOP_LEVEL) {
            if (DEBUG) {
                System.out
                    .printf("Top-level reachables of %s augmented by %s%n", getState(), child);
            }
            addRecipeTarget(child);
        }
    }

    /** Callback method invoked when a (transitive) child closed or got a lower absence level.
     * @param child the changed child state
     * @param change the kind of change
     */
    private void notifyChildChanged(GraphState child, Change change) {
        int childAbsence = child.getAbsence();
        if ((!child.isTransient() || child.isClosed()) && this.transientOpens.remove(child)
            || this.transientOpens.contains(child)) {
            if (childAbsence < this.absence) {
                this.absence = childAbsence;
            }
            fireChanged(child, change);
            if (this.transientOpens.isEmpty() && getState().isClosed()) {
                setStateDone();
            }
        }
    }

    private void setStateDone() {
        getState().setDone(this.absence);
        this.rawPreds.clear();
    }

    /** Adds recipe transitions from the surface parents to a given (surface) target. */
    private void addRecipeTarget(GraphState target) {
        assert !target.isInternalState();
        if (DEBUG) {
            System.out.printf("Recipe targets of %s augmented by %s%n", getState(), target);
        }
        getRecipeTargets().add(target);
        for (RuleTransition init : this.recipeInits) {
            addRecipeTransition(init, target);
        }
    }

    private void addRecipeTransition(RuleTransition partial, GraphState target) {
        RecipeTransition trans = new RecipeTransition(partial, target);
        getState().getGTS()
            .addTransition(trans);
        if (DEBUG) {
            System.out.printf("Recipe transition added: %s--%s-->%s%n",
                trans.source(),
                trans.label(),
                target);
        }
    }

    /**
     * Returns the absence level of the state.
     * This is {@link Status#MAX_ABSENCE} if the state is erroneous,
     * otherwise it is the minimum transient depth of the reachable states.
     */
    final int getAbsence() {
        return this.absence;
    }

    /**
     * Known absence level of the state.
     * The absence level is the minimum transient depth of the known reachable states.
     */
    private int absence;

    /** Flag indicating if this state is known to be a recipe state. */
    private boolean inRecipe;

    /** Returns the list of reachable surface states.
     * Only non-{@code null} for states that started their existence as transient.
     */
    private List<GraphState> getRecipeTargets() {
        if (this.recipeTargets == null) {
            new TopLevelRecord(this).run();
        }
        return this.recipeTargets;
    }

    /** List of reachable top-level states, which can serve as recipe targets
     * if this state appears as target of an in-recipe transition.
     */
    private List<GraphState> recipeTargets;

    /** The cache for which this record contains the exploration data. */
    private final StateCache cache;
    /** The state for which this record contains the exploration data. */
    private final AbstractGraphState state;
    /**
     * List of rule transitions to this state that are initial steps of a recipe.
     */
    private final List<RuleTransition> recipeInits;
    /**
     * Collection of direct parent transient states.
     */
    private final List<ExploreData> rawPreds = new ArrayList<>();
    /** Set of reachable transient open states (transitively closed). */
    private final Set<GraphState> transientOpens = new HashSet<>();
    /** Set of states reachable through a non-empty sequence of partial transitions. */
    private final Set<GraphState> reachables = new HashSet<>();

    private final static boolean DEBUG = false;

    /**
     * Helper class to reconstruct the top-level reachable fields
     * of collected caches (of done states).
     */
    private static class TopLevelRecord {
        TopLevelRecord(ExploreData data) {
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
                for (GraphTransition trans : source.getState()
                    .getTransitions(Claz.PRESENT)) {
                    assert !trans.target()
                        .isInternalState() || trans.target()
                            .isDone();
                    ExploreData target = trans.target()
                        .getCache()
                        .getExploreData();
                    addData(target);
                    this.backward.get(target)
                        .add(source);
                }
            }
            if (DEBUG) {
                System.out.printf("Backward reachability matrix: %s%n", this.backward);
                System.out.printf("Intermediate result map: %s%n", this.resultMap);
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
                Set<GraphState> reachables = this.resultMap.get(data);
                for (ExploreData pred : this.backward.get(data)) {
                    if (this.resultMap.get(pred)
                        .addAll(reachables)) {
                        changed.add(pred);
                        if (DEBUG) {
                            System.out.printf("Adding reachables %s for %s%n",
                                reachables,
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
            for (java.util.Map.Entry<ExploreData,Set<GraphState>> entry : this.resultMap
                .entrySet()) {
                List<GraphState> topLevelReachables = new ArrayList<>(entry.getValue());
                ExploreData keyData = entry.getKey();
                if (DEBUG) {
                    System.out.printf("Top-level reachables of %s determined as %s%n",
                        keyData.getState(),
                        topLevelReachables);
                }
                keyData.recipeTargets = topLevelReachables;
            }
        }

        /** Adds an {@link ExploreData} item to the data structures}. */
        private void addData(ExploreData data) {
            // to avoid circularity, only add if the data was not already added
            if (!this.backward.containsKey(data)) {
                if (DEBUG) {
                    System.out.printf("Adding data for %s%n", data.getState());
                }
                Set<ExploreData> targetBackward = new HashSet<>();
                this.backward.put(data, targetBackward);
                Set<GraphState> resultEntry = new LinkedHashSet<>();
                this.resultMap.put(data, resultEntry);
                List<GraphState> targets = data.recipeTargets;
                if (targets == null) {
                    // we're going to traverse further
                    if (data.getState()
                        .isInternalState()) {
                        if (DEBUG) {
                            System.out.printf("Traversing further for %s%n", data.getState());
                        }
                        this.queue.add(data);
                    } else {
                        if (DEBUG) {
                            System.out.printf("Recording top-level status for %s%n",
                                data.getState());
                        }
                        resultEntry.add(data.getState());
                    }
                } else {
                    // this state still has its cache, no need to explore
                    if (DEBUG) {
                        System.out
                            .printf("Refreshing %s as targets for %s%n", targets, data.getState());
                    }
                    resultEntry.addAll(targets);
                }
            }
        }

        /** backward map from states to direct predecessors. */
        private final Map<ExploreData,Set<ExploreData>> backward = new LinkedHashMap<>();
        /** map from states to top level reachables. */
        private final Map<ExploreData,Set<GraphState>> resultMap = new LinkedHashMap<>();
        /** queue of outstanding states to be explored. */
        private final Queue<ExploreData> queue = new LinkedList<>();
    }

    /** Type of propagated change. */
    enum Change {
        /** The transient depth decreased because an atomic block was exited. */
        TRANSIENCE,
        /** The state closed. */
        CLOSURE,
        /** The state became top-level. */
        TOP_LEVEL,;
    }
}
