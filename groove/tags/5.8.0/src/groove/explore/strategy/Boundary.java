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
package groove.explore.strategy;

import groove.lts.GraphTransition;
import groove.verify.ModelChecking.Record;
import groove.verify.ProductTransition;

/**
 * Abstract implementation for boundaries.
 * 
 * @author Harmen Kastenberg
 * @version $Revision $
 */
public abstract class Boundary {
    /** 
     * Constructs a boundary with a given (possibly {@code null}) model checking run.
     * @param record record of the model checking run; if {@code null}, this is a
     * prototype boundary
     */
    protected Boundary(Record record) {
        this.record = record;
    }

    /** Instantiates this boundary for a given (non-{@code null}) model checking run. */
    abstract public Boundary instantiate(Record record);

    /**
     * Checks whether the given transition crosses the boundary. If so, it
     * return <tt>true</tt>, otherwise <tt>false</tt>.
     * @param transition the transition for which to check whether it crosses
     *        the boundary
     * @param traverse flag indicating whether this transition will be tried to
     *        traverse or if this call is simply a check whether this transition
     *        is allowed
     * @return <tt>true</tt> if the transition crosses the boundary,
     *         <tt>false</tt> otherwise
     */
    abstract public boolean crossingBoundary(ProductTransition transition,
            boolean traverse);

    /**
     * Increases the boundary.
     */
    abstract public void increase();

    /**
     * Returns the current depth of the exploration, in terms of
     * boundary-crossing transition on the current path.
     * @return the number of boundary-crossing transitions in the current path
     */
    public int currentDepth() {
        assert this.record != null;
        return this.currentDepth;
    }

    /**
     * Set the value of the current depth.
     * @param value the new value
     */
    public void setCurrentDepth(int value) {
        assert this.record != null;
        this.currentDepth = value;
    }

    /**
     * Decrease the <code>currentDepth</code>.
     */
    public void decreaseDepth() {
        assert this.record != null;
        this.currentDepth--;
        assert (this.currentDepth >= 0) : "The value of currentDepth should not be negative.";
    }

    /**
     * Increase the <code>currentDepth</code>.
     */
    public void increaseDepth() {
        this.currentDepth++;
        assert (this.currentDepth <= getRecord().getIteration()) : "the number of boundary-crossing transitions ("
            + this.currentDepth
            + ") in the current path exceeded the maximum ("
            + getRecord().getIteration() + ")";
    }

    /**
     * Backtrack the given transition.
     * @param transition the backtracked transition
     */
    public void backtrackTransition(GraphTransition transition) {
        // by default, do nothing
    }

    /** Returns the model checking record associated with the boundary. */
    final protected Record getRecord() {
        return this.record;
    }

    private Record record;
    /**
     * container for the number of boundary-crossing transitions in the current
     * path
     */
    private int currentDepth = 0;
}
