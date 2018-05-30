/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
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
package groove.explore;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;

import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphTransition;

/**
 * A set of graph states that constitute the result of the execution of some
 * exploration.
 */
public class ExploreResult implements Iterable<GraphState> {
    /**
     * Creates a fresh, empty result for a given (non-{@code null}) GTS.
     */
    public ExploreResult(GTS gts) {
        this.gts = gts;
        this.elements = createResultSet();
    }

    /** Returns the graph transformation system to which this result applies. */
    public GTS getGTS() {
        return this.gts;
    }

    private final GTS gts;

    /**
     * Adds a state to the result.
     */
    public void addState(GraphState t) {
        this.elements.add(t);
        this.lastState = t;
    }

    /** Tests if this result contains a given graph state. */
    public boolean containsState(GraphState state) {
        return getStates().contains(state);
    }

    /**
     * The set of states contained in the result.
     */
    public Collection<GraphState> getStates() {
        return this.elements;
    }

    /** The elements stored in this result. */
    private final Collection<GraphState> elements;

    /** Returns the most recently added state. */
    public GraphState getLastState() {
        return this.lastState;
    }

    /** The most recently added state. */
    private GraphState lastState;

    @Override
    public Iterator<GraphState> iterator() {
        return getStates().iterator();
    }

    /** Returns the number of states currently stored in this result. */
    public int size() {
        return getStates().size();
    }

    /** Indicates if this result is currently empty, i.e., contains no states. */
    public boolean isEmpty() {
        return size() == 0;
    }

    /** Indicates if this result stores transitions (as well as states). */
    public boolean storesTransitions() {
        return false;
    }

    /** Returns the set of transitions stored in this result. */
    public Collection<GraphTransition> getTransitions() {
        return Collections.emptySet();
    }

    @Override
    public String toString() {
        return "Result [elements=" + this.elements + "]";
    }

    /** Callback factory method for the result set. */
    protected Collection<GraphState> createResultSet() {
        return new LinkedHashSet<>();
    }
}
