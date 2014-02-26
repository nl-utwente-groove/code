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
        this.presence =
            this.state.isError() ? Integer.MAX_VALUE : this.state.getActualFrame().getDepth();
    }

    private final AbstractGraphState state;

    /** 
     * Adds an outgoing partial transition to this cache.
     * @param partial new outgoing partial rule transition from this state
     */
    void addOutPartial(RuleTransition partial) {
        notifyPartial(partial, partial);
        GraphState child = partial.target();
        ExploreData childCache = child.getCache().getExploreData();
        this.presence = Math.min(this.presence, childCache.presence);
        if (child.isTransient()) {
            if (!child.isDone()) {
                // we've reached a transient raw state
                childCache.rawParents.add(Pair.newPair(this, partial));
            }
            // add the child partials to this cache
            for (RuleTransition childPartial : childCache.partials) {
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
        if (this.state.isTransient()) {
            if (this.partials.add(partial)) {
                this.presence = Math.min(this.presence, target.getPresence());
                // notify all parents of the new partial
                for (Pair<ExploreData,RuleTransition> parent : this.rawParents) {
                    parent.one().notifyPartial(partial, parent.two());
                }
            }
        } else if (!target.isTransient() && initial.isRecipeStep()) {
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
            fireChanged(this.state);
        }
        if (this.transientOpens.isEmpty()) {
            setStateDone();
        }
    }

    /** Callback method invoked when a child closed or became non-transient. */
    private void notifyChildChanged(GraphState child, RuleTransition initial) {
        if (this.transientOpens.remove(child)) {
            this.presence = Math.min(this.presence, child.getPresence());
            if (this.state.isTransient()) {
                // notify all parents of the change
                fireChanged(child);
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
        this.state.setDone(this.presence);
        this.rawParents.clear();
    }

    /** 
     * Callback method invoked when the state closed or became non-transient.
     * Notifies all raw parents.
     */
    private void fireChanged(GraphState state) {
        // notify all parents of the change
        for (Pair<ExploreData,RuleTransition> parent : this.rawParents) {
            parent.one().notifyChildChanged(state, parent.two());
        }
    }

    /** Decreases the presence level and fires a changed event. */
    final void setPresence(int presence) {
        if (presence < this.presence) {
            this.presence = presence;
            fireChanged(this.state);
        }
    }

    /** 
     * Returns the lowest known presence depth of the state.
     * This is {@link Integer#MAX_VALUE} if the state is erroneous,
     * otherwise it is the minimum transient depth of the reachable states.
     */
    final int getPresence() {
        return this.presence;
    }

    /** Flag indicating if the associated state is known to be present. */
    private int presence;

    /** 
     * Set of incoming transitions from raw parent states.
     */
    private final List<Pair<ExploreData,RuleTransition>> rawParents =
        new ArrayList<Pair<ExploreData,RuleTransition>>();
    /** Set of reachable transient open states. */
    private final Set<GraphState> transientOpens = new HashSet<GraphState>();
    /** Set of reachable partial rule transitions. */
    private final Set<RuleTransition> partials = new HashSet<RuleTransition>();
}
