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
package groove.lts;

import groove.lts.GraphState.Flag;
import groove.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

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
        GraphState state = this.state = cache.getState();
        this.absence =
            this.state.isError() ? Flag.MAX_ABSENCE : this.state.getActualFrame().getDepth();
        if (!state.isClosed()) {
            this.surfaceDescendants = new ArrayList<GraphState>();
            if (!state.isTransient()) {
                this.surfaceDescendants.add(state);
            }
        }
    }

    private final AbstractGraphState state;

    /** 
     * Notifies the cache of the addition of an outgoing partial transition.
     * @param partial new outgoing partial rule transition from this state
     */
    void notifyOutPartial(RuleTransition partial) {
        if (DEBUG) {
            System.out.printf("Rule transition added: %s--%s-->%s%n", partial.source(),
                partial.label(), partial.target());
        }
        assert partial.isPartial();
        assert partial.source() == this.state;
        GraphState source = this.state;
        GraphState child = partial.target();
        ExploreData childCache = child.getCache().getExploreData();
        this.absence = Math.min(this.absence, childCache.absence);
        if (source.isTransient()) {
            addReachablePartial(partial);
            if (child.isTransient()) {
                // add the child partials to this cache
                for (RuleTransition childPartial : childCache.partials) {
                    addReachablePartial(childPartial);
                }
                if (!child.isDone()) {
                    childCache.rawParents.add(this);
                }
            }
        } else {
            assert partial.getStep().isInitial();
            // immediately add recipe transitions to the 
            // previously found surface descendants of the child
            for (GraphState target : childCache.getSurfaceDescendants()) {
                addRecipeTransition(this.state, partial, target);
            }
            if (child.isTransient() && !child.isDone()) {
                childCache.surfaceParents.add(Pair.newPair(this, partial));
            }
        }
    }

    /** 
     * Callback method invoked when the state has been closed.
     */
    void notifyClosed() {
        if (DEBUG) {
            System.out.printf("State closed: %s%n", this.state);
        }
        if (this.state.isTransient()) {
            // notify all parents of the closure
            fireChanged(this.state, Change.CLOSURE);
        }
        if (this.transientOpens.isEmpty()) {
            setStateDone();
        }
    }

    /** Notifies the cache of a decrease in transient depth of the control frame. */
    final void notifyDepth(int depth) {
        if (DEBUG) {
            System.out.printf("Transient depth of %s set to %s%n", this.state, depth);
        }
        if (depth < this.absence) {
            this.absence = depth;
        }
        fireChanged(this.state, Change.TRANSIENCE);
    }

    /**
     * Adds a reachable partial transition to this cache.
     * @param partial partial transition reachable from this state
     */
    private void addReachablePartial(RuleTransition partial) {
        assert this.state.isTransient();
        // maybe add the transition target to the transient open states
        GraphState target = partial.target();
        // add the partial if it was not already known
        if (this.partials.add(partial)) {
            this.absence = Math.min(this.absence, target.getAbsence());
            // notify all parents of the new partial
            for (ExploreData parent : this.rawParents) {
                parent.addReachablePartial(partial);
            }
        }
        if (target.isTransient()) {
            if (!target.isClosed()) {
                this.transientOpens.add(target);
            }
        } else {
            getSurfaceDescendants().add(target);
            if (DEBUG) {
                System.out.printf("Surface descendents of %s augmented by %s%n", this.state, target);
            }
            addRecipeTransitions(target);
        }
    }

    /** 
     * Callback method invoked when a given reachable state changed. 
     * Notifies all raw parents and adds recipe transitions, as appropriate.
     */
    private void fireChanged(GraphState child, Change change) {
        // notify all raw parents of the change
        for (ExploreData parent : this.rawParents) {
            parent.notifyChildChanged(child, change);
        }
        if (change == Change.TRANSIENCE && !child.isTransient()) {
            if (DEBUG) {
                System.out.printf("Surface descendents of %s augmented by %s%n", this.state, child);
            }
            getSurfaceDescendants().add(child);
            addRecipeTransitions(child);
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
            if (this.transientOpens.isEmpty() && this.state.isClosed()) {
                setStateDone();
            }
        }
    }

    private void setStateDone() {
        this.state.setDone(this.absence);
        this.rawParents.clear();
    }

    /** Adds recipe transitions from the surface parents to a given (surface) target. */
    private void addRecipeTransitions(GraphState target) {
        assert !target.isTransient();
        for (Pair<ExploreData,RuleTransition> parent : this.surfaceParents) {
            addRecipeTransition(parent.one().state, parent.two(), target);
        }
    }

    private void addRecipeTransition(GraphState source, RuleTransition partial, GraphState target) {
        RecipeTransition trans = new RecipeTransition(source, partial, target);
        this.state.getGTS().addTransition(trans);
        if (DEBUG) {
            System.out.printf("Recipe transition added: %s--%s-->%s%n", source, trans.label(),
                target);
        }
    }

    /** 
     * Returns the absence level of the state.
     * This is {@link Flag#MAX_ABSENCE} if the state is erroneous,
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

    /** Returns the list of reachable surface states.
     * Only non-{@code null} for states that started their existence as transient.
     */
    private List<GraphState> getSurfaceDescendants() {
        if (this.surfaceDescendants == null) {
            initSurfaceDescendants(this);
        }
        return this.surfaceDescendants;
    }

    /** List of reachable surface states.
     * Only non-{@code null} for states that started their existence as transient.
     */
    private List<GraphState> surfaceDescendants = new ArrayList<GraphState>();

    /** 
     * Collection of direct parent surface states, with transitions from parent to this.
     * Only non-{@code null} for states that started their existence as transient.
     */
    private final List<Pair<ExploreData,RuleTransition>> surfaceParents =
        new ArrayList<Pair<ExploreData,RuleTransition>>();
    /** 
     * Collection of direct parent transient states.
     */
    private final List<ExploreData> rawParents = new ArrayList<ExploreData>();
    /** Set of reachable transient open states (transitively closed). */
    private final Set<GraphState> transientOpens = new HashSet<GraphState>();
    /** Set of reachable partial rule transitions (transitively closed. */
    private final Set<RuleTransition> partials = new HashSet<RuleTransition>();

    /**
     * Goes over the part of the already explored transition system reachable from start,
     * and sets the {@link #surfaceDescendants} fields of all non-transient states in that part. 
     * This needs to be done when state caches are rebuilt.
     */
    private static void initSurfaceDescendants(ExploreData start) {
        assert start.state.isClosed();
        // build the transitive closure of the existing transitions between states 
        // reachable from start, stopping at the first surface state
        // transitive and reflexive forward map from states to successors
        Map<ExploreData,Set<ExploreData>> forward = new HashMap<ExploreData,Set<ExploreData>>();
        // transitive and reflexive backward map from states to predecessors
        Map<ExploreData,Set<ExploreData>> backward = new HashMap<ExploreData,Set<ExploreData>>();
        // queue of outstanding states to be explored
        Queue<ExploreData> queue = new LinkedList<ExploreData>();
        addData(start, forward, backward, queue);
        while (!queue.isEmpty()) {
            ExploreData source = queue.poll();
            for (GraphTransition trans : source.state.getTransitions()) {
                if (trans instanceof RuleTransition
                    && !((RuleTransition) trans).getStep().isInitial()) {
                    ExploreData target = trans.target().getCache().getExploreData();
                    if (!forward.containsKey(target)) {
                        addData(target, forward, backward, queue);
                    }
                    if (!backward.get(target).contains(source)) {
                        Set<ExploreData> targetForward = forward.get(target);
                        Set<ExploreData> sourceBackward = backward.get(source);
                        for (ExploreData d : targetForward) {
                            backward.get(d).addAll(sourceBackward);
                        }
                        for (ExploreData d : sourceBackward) {
                            forward.get(d).addAll(targetForward);
                        }
                    }
                }
            }
        }
        // store the result in the surfaceDescendents fields
        for (Map.Entry<ExploreData,Set<ExploreData>> entry : forward.entrySet()) {
            List<GraphState> surfaceDescendents = new ArrayList<GraphState>();
            for (ExploreData target : entry.getValue()) {
                GraphState state = target.state;
                if (!state.isTransient()) {
                    surfaceDescendents.add(state);
                }
            }
            entry.getKey().surfaceDescendants = surfaceDescendents;
            if (DEBUG) {
                System.out.printf("Surface descendents of %s determined at %s", entry.getKey(),
                    surfaceDescendents);
            }
        }
    }

    /** Adds an {@link ExploreData} item to the data structures used in {@link #initSurfaceDescendants(ExploreData)}. */
    private static void addData(ExploreData data, Map<ExploreData,Set<ExploreData>> forward,
            Map<ExploreData,Set<ExploreData>> backward, Queue<ExploreData> queue) {
        queue.add(data);
        Set<ExploreData> targetForward = new HashSet<ExploreData>();
        forward.put(data, targetForward);
        targetForward.add(data);
        Set<ExploreData> targetBackward = new HashSet<ExploreData>();
        backward.put(data, targetBackward);
        targetBackward.add(data);
    }

    private final static boolean DEBUG = false;

    /** Type of propagated change. */
    enum Change {
        /** The transient depth decreased because an atomic block was exited. */
        TRANSIENCE,
        /** The state closed. */
        CLOSURE;
    }
}
