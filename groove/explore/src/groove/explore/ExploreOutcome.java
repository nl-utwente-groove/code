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
package groove.explore;

import java.util.ArrayList;
import java.util.List;

/**
 * An exploration outcome collects exploration results.
 * There may be an upper bound to the number of expected results.
 * The outcome object also records if exploration was interrupted,
 * in which case the number of results may have stayed below the bound.
 * @author Arend Rensink
 * @version $Revision $
 */
public class ExploreOutcome {
    /** Constructs a new exploration outcome, with a given bound. */
    ExploreOutcome(int bound) {
        assert bound >= 0;
        this.results = new ArrayList<>();
        this.bound = bound;
    }

    /** The maximum number of exploration results, or {@code 0} if there is no maximum. */
    private final int bound;

    /** Indicates if there is an a priori bound to the number of results in this outcome. */
    public boolean hasBound() {
        return this.bound > 0;
    }

    /**
     * Returns the a priori bound to the number of expected results.
     * @return The number of expected results, or {@code 0} if there is no a priori bound
     */
    public int getBound() {
        return this.bound;
    }

    private final List<ExploreProduct> results;

    /** Adds an exploration result to this outcome. */
    void addResult(ExploreProduct result) {
        if (isFull()) {
            throw new IllegalStateException(
                String.format("Attempt to add result to full outcome (bound = %d)", getBound()));
        }
        this.results.add(result);
    }

    /** Returns the list of exploration results wrapped in this outcome. */
    public List<ExploreProduct> getResults() {
        return this.results;
    }

    /** Indicates if the bound of this outcome was reached. */
    public boolean isFull() {
        return hasBound() && getResults().size() == getBound();
    }

    /**
     * Indicates if the exploration is finished.
     * This is the case if the outcome is full,
     * or the exploration was interrupted.
     * @return {@code true} if the outcome is full or the exploration was interrupted.
     * @see #isFull()
     * @see #isInterrupted()
     */
    public boolean isFinished() {
        return isInterrupted() || isFull();
    }

    /** Flag indicating if the exploration was interrupted. */
    private boolean interrupted;

    /** Sets the interrupted flag. */
    void setInterrupted() {
        this.interrupted = true;
    }

    /** Indicates if the exploration was interrupted before being fully concluded. */
    public boolean isInterrupted() {
        return this.interrupted;
    }
}
