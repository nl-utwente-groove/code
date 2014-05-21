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
import java.util.HashSet;
import java.util.List;
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
        this.state = cache.getState();
        this.absence =
            this.state.isError() ? Flag.MAX_ABSENCE : this.state.getActualFrame().getDepth();
    }

    private final AbstractGraphState state;

    /** 
     * Adds an outgoing partial transition to this cache.
     * @param partial new outgoing partial rule transition from this state
     */
    void addOutPartial(RuleTransition partial) {
        RuleTransition initial = partial.getStep().isInitial() ? partial : null;
        notifyPartial(partial, initial);
        GraphState child = partial.target();
        ExploreData childCache = child.getCache().getExploreData();
        this.absence = Math.min(this.absence, childCache.absence);
        if (child.isTransient()) {
            if (!child.isDone()) {
                // we've reached a transient raw state
                childCache.rawParents.add(Pair.newPair(this, initial));
            }
            // add the child partials to this cache
            for (RuleTransition childPartial : childCache.partials) {
                notifyPartial(childPartial, initial);
            }
        }
    }

    /**
     * Notifies the cache of the existence of a reachable partial transition.
     * @param partial partial transition reachable from this state
     * @param initial initial transition, from this state, of a potential
     * recipe transition containing the new partial transition; may be {@code null}
     */
    private void notifyPartial(RuleTransition partial, RuleTransition initial) {
        // maybe add the transition target to the transient open states
        GraphState target = partial.target();
        if (target.isTransient() && !target.isClosed()) {
            this.transientOpens.add(target);
        }
        // add the partial if it was not already known
        if (this.state.isTransient()) {
            if (this.partials.add(partial)) {
                this.absence = Math.min(this.absence, target.getAbsence());
                // notify all parents of the new partial
                for (Pair<ExploreData,RuleTransition> parent : this.rawParents) {
                    RuleTransition parentInitial = parent.two();
                    // reuse the parent's initial transition if we are still in the same recipe
                    if (parentInitial != null && parentInitial.getRecipe() != partial.getRecipe()) {
                        parentInitial = null;
                    }
                    parent.one().notifyPartial(partial, parentInitial);
                }
            }
        }
        if (target.getActualFrame().getDepth() == this.state.getActualFrame().getDepth()
            && initial != null) {
            // add recipe transition if there was none
            this.state.getGTS().addTransition(new RecipeTransition(this.state, initial, target));
        }
    }

    /** 
     * Callback method invoked when the state has been closed.
     */
    void notifyClosed() {
        if (this.state.isTransient()) {
            // notify all parents of the closure
            fireChanged(this.state, Change.CLOSURE);
        }
        if (this.transientOpens.isEmpty()) {
            setStateDone();
        }
    }

    /** Callback method invoked when a child closed or got a lower absence level. */
    private void notifyChildChanged(GraphState child, RuleTransition initial, Change change) {
        int childAbsence = child.getAbsence();
        if (childAbsence == 0 && this.transientOpens.remove(child)
            || this.transientOpens.contains(child)) {
            this.absence = Math.min(this.absence, child.getAbsence());
            if (this.state.isTransient()) {
                // notify all parents of the change
                fireChanged(child, change);
            }
        }
        if (!this.state.isTransient() && !child.isTransient() && initial.isRecipeStep()) {
            this.state.getGTS().addTransition(new RecipeTransition(this.state, initial, child));
        }
        if (this.transientOpens.isEmpty() && this.state.isClosed()) {
            setStateDone();
        }
    }

    private void setStateDone() {
        this.state.setDone(this.absence);
        this.rawParents.clear();
    }

    /** 
     * Callback method invoked when a given (transient) changed. Notifies all raw parents.
     */
    private void fireChanged(GraphState state, Change change) {
        // notify all parents of the change
        for (Pair<ExploreData,RuleTransition> parent : this.rawParents) {
            parent.one().notifyChildChanged(state, parent.two(), change);
        }
    }

    /** Decreases the absence level and fires a changed event. */
    final void setAbsence(int absence) {
        if (absence < this.absence) {
            this.absence = absence;
            fireChanged(this.state, Change.ABSENCE);
        }
    }

    /** 
     * Returns the lowest known presence depth of the state.
     * This is {@link Integer#MAX_VALUE} if the state is erroneous,
     * otherwise it is the minimum transient depth of the reachable states.
     */
    final int getAbsence() {
        return this.absence;
    }

    /** Flag indicating if the associated state is known to be present. */
    private int absence;

    /** 
     * Set of incoming transitions from raw parent states.
     */
    private final List<Pair<ExploreData,RuleTransition>> rawParents =
        new ArrayList<Pair<ExploreData,RuleTransition>>();
    /** Set of reachable transient open states (transitively closed). */
    private final Set<GraphState> transientOpens = new HashSet<GraphState>();
    /** Set of reachable partial rule transitions. */
    private final Set<RuleTransition> partials = new HashSet<RuleTransition>();

    /** Type of propagated change. */
    enum Change {
        /** The absence level got lower because a child was found with lower transience. */
        ABSENCE,
        /** The transience level got lower because an atomic block was exited. */
        TRANSIENCE,
        /** The state closed. */
        CLOSURE;
    }
}
