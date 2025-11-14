/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
 * University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * $Id$
 */
package nl.utwente.groove.explore;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.lts.GTSFragment;
import nl.utwente.groove.lts.GraphState;
import nl.utwente.groove.lts.GraphTransition;
import nl.utwente.groove.lts.StateProperty;

/**
 * A set of graph states that constitute the result of the execution of some
 * exploration.
 */
@NonNullByDefault
public class ExploreResult {
    /**
     * Creates a fresh, empty result for a given (non-{@code null}) GTS.
     */
    public ExploreResult(GTS gts) {
        this(null, gts);
    }

    /**
     * Creates a fresh, empty named result for a given (non-{@code null}) GTS.
     */
    public ExploreResult(@Nullable String name, GTS gts) {
        this.name = name;
        this.gts = gts;
    }

    /** Returns the graph transformation system to which this result applies. */
    public GTS getGTS() {
        return this.gts;
    }

    private final GTS gts;

    /** Returns the name of this result. */
    public @Nullable String getName() {
        return this.name;
    }

    private final @Nullable String name;

    /**
     * Adds a state to the result.
     */
    public void addState(GraphState t) {
        this.states.add(t);
        this.lastState = t;
    }

    /** Tests if this result contains a given graph state. */
    public boolean contains(GraphState state) {
        return getStates().contains(state);
    }

    /**
     * The set of states contained in the result.
     */
    public Set<GraphState> getStates() {
        return this.states;
    }

    /** The elements stored in this result. */
    private final Set<GraphState> states = new LinkedHashSet<>();

    /** Returns the most recently added state. */
    public @Nullable GraphState getLastState() {
        return this.lastState;
    }

    /** The most recently added state. */
    private @Nullable GraphState lastState;

    /** Returns the set of transitions stored in this result. */
    public Collection<GraphTransition> getTransitions() {
        return this.transitions;
    }

    /** Tests if this result contains a given graph transition. */
    public boolean contains(GraphTransition trans) {
        return getTransitions().contains(trans);
    }

    private final Set<GraphTransition> transitions = new LinkedHashSet<>();

    /**
     * Adds a transition to the result.
     */
    public void addTransition(GraphTransition t) {
        this.transitions.add(t);
    }

    /** Returns the number of states and transitions currently stored in this result. */
    public int size() {
        return getStates().size() + getTransitions().size();
    }

    /** Indicates if this result is currently empty, i.e., contains no states or transitions. */
    public boolean isEmpty() {
        return size() == 0;
    }

    /** Creates a GTS fragment containing the states and transitions of this result,
     * completed with paths from the start state.
     * @param internal if {@code true}, internal states and transitions are included
     * @see GTSFragment#complete
     */
    public GTSFragment toFragment(boolean internal) {
        var result = new GTSFragment(getGTS(), getStates(), getTransitions());
        result.complete(internal);
        // Declare states without outgoing transitions to be final
        var finalStates = new HashSet<>(getStates());
        result.edgeSet().forEach(t -> finalStates.remove(t.source()));
        result.setFinal(finalStates);
        return result;
    }

    /** Pushes the this result to the underlying GTS,
     * meaning that (if this is a named result) the GTS will get
     * a corresponding {@link StateProperty}.
     */
    public void push() {
        var name = getName();
        if (name != null) {
            getGTS().addStateProperty(name, state -> getStates().contains(state));
        }
    }

    /** Returns the number of states and transitions found during exploration. */
    public String getStatistics() {
        return "Exploration result: %s states, %s transitions"
            .formatted(this.states.size(), this.transitions.size());
    }

    @Override
    public String toString() {
        return "Result [states=" + this.states + ", " + "transitions=" + this.transitions
            + ", lastState =" + this.lastState + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.gts, this.lastState, this.states, this.transitions);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ExploreResult other = (ExploreResult) obj;
        return this.gts == other.gts && this.lastState == other.lastState
            && this.states.equals(other.states) && this.transitions.equals(other.transitions);
    }
}
