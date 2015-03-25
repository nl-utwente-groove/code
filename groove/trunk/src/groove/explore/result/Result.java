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
package groove.explore.result;

import groove.lts.GraphState;

import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * A set of graph states that are the result of the execution of some
 * exploration. Whether the result is fully computed is defined by the
 * {@link #done()} method. Typical such objects are elements of a graph
 * transition system (states, paths, etc.).
 */
public class Result {
    /**
     * Creates a result with an empty set of elements, without a bound on the
     * size of the result.
     */
    public Result() {
        this(0);
    }

    /**
     * Creates a result with an empty set of elements, with a given bound on the
     * size of the result.
     * @param bound the bound on the size of the result. {@link #done()} will
     *        return <code>true</code> as soon as the size of
     *        {@link #getValue()} is at least <code>bound</code>. If
     *        <code>bound</code> is 0, no bound is checked.
     */
    public Result(int bound) {
        assert bound >= 0;
        this.elements = createResultSet();
        this.bound = bound;
    }

    /**
     * Adds an element to the result.
     */
    public void add(GraphState t) {
        this.elements.add(t);
    }

    /**
     * The set of elements contained in the result.
     */
    public Collection<GraphState> getValue() {
        return this.elements;
    }

    /**
     * The bound of the result.
     */
    public int getBound() {
        return this.bound;
    }

    /** Factory method for a new result of the same type as this one. */
    public Result newInstance() {
        return new Result(this.bound);
    }

    /**
     * Indicates whether the result is complete.
     * @return <code>true</code> if the result is complete, <code>false</code>
     *         otherwise. When the result is complete, no more elements should
     *         be added to it.
     */
    public boolean done() {
        return this.bound > 0 && this.elements.size() >= this.bound;
    }

    /** Callback factory method for the result set. */
    protected Collection<GraphState> createResultSet() {
        return new LinkedHashSet<GraphState>();
    }

    /** The elements stored in this result. */
    private final Collection<GraphState> elements;
    /** Bound on the size of the result; if <code>0</code>, no bound is used. */
    private final int bound;
}
