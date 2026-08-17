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
package nl.utwente.groove.explore.result;

import nl.utwente.groove.lts.ExploreResult;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.lts.GTSListener;
import nl.utwente.groove.lts.GraphState;
import nl.utwente.groove.lts.GraphTransition;

/**
 * Run-time realisation of the goal, outcome and count features of an
 * exploration: listens to the GTS being explored, collects the states
 * realising its {@link Goal} into the result, and signals (through
 * {@link #done()}) when the result count has been reached. Instances are
 * stateful and are used for a single exploration run; they are created
 * afresh by {@link nl.utwente.groove.explore.ExploreType#realise}.
 */
public class ResultCollector implements GTSListener {
    /** Creates a collector with the empty goal and no result count. */
    public ResultCollector() {
        this(Goal.none(), 0);
    }

    /** Creates a collector with a given goal and result count. */
    public ResultCollector(Goal goal, int count) {
        assert count >= 0;
        this.goal = goal;
        this.count = count;
    }

    private final Goal goal;

    /** Indicates if this collector has a (non-zero) result count. */
    public boolean hasResultCount() {
        return getResultCount() > 0;
    }

    /** Returns the result count of this collector.
     * The count is the number of states in the result after which the collector
     * signals that the exploration should be halted (using {@link #done()}).
     * A count of 0 means that exploration is never halted.
     * @see #done()
     */
    public int getResultCount() {
        return this.count;
    }

    private final int count;

    @Override
    public void addUpdate(GTS gts, GraphState state) {
        if (this.goal.testAdded(state)) {
            getResult().addState(state);
        }
    }

    @Override
    public void addUpdate(GTS gts, GraphTransition transition) {
        if (this.goal.testAdded(transition)) {
            getResult().addState(transition.source());
        }
    }

    @Override
    public void statusUpdate(GTS gts, GraphState state, int change) {
        if (this.goal.testStatus(state, change)) {
            getResult().addState(state);
        }
    }

    /** Prepares the collector for a new exploration.
     * In particular, sets a fresh {@link ExploreResult}.
     * @param gts the GTS of the new exploration
     */
    public void prepare(GTS gts) {
        this.result = createResult(gts);
    }

    /** Factory method to create a result object.
     * @param gts the GTS being explored
     */
    protected ExploreResult createResult(GTS gts) {
        return new ExploreResult(gts);
    }

    /** Tests if the exploration is done,
     * according to the demands of this collector.
     */
    public boolean done() {
        return hasResultCount() && getResult().size() >= getResultCount();
    }

    /**
     * Returns the result.
     * @return The result
     */
    public ExploreResult getResult() {
        return this.result;
    }

    private ExploreResult result;

    /** Returns a message describing the collected result. */
    public String getMessage() {
        return this.result.getStatistics();
    }
}
