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

import groove.explore.config.ExploreKey;
import groove.explore.config.SuccessorKind;
import groove.lts.GTS;
import groove.lts.GraphState;

/**
 * General class for exploration, used for all cases except model checking.
 * @author Arend Rensink
 * @version $Revision $
 */
public class ExploreInstance {
    /** Creates an exploration instance from an exploration configuration and a start state. */
    public ExploreInstance(ExploreConfig config, GTS gts, GraphState startState) {
        this.config = config;
        this.startState = startState;
    }

    private final ExploreConfig config;
    private final GraphState startState;

    /** Flag indicating that {@link #go()} has been invoked. */
    private boolean gone;

    /**
     * Starts the exploration, and returns the outcome.
     * Should not be called more than once on this instance.
     * @return the exploration outcome
     */
    public ExploreOutcome go() {
        if (this.gone) {
            throw new IllegalStateException("Repeated invocation of go");
        }
        this.gone = true;
        ExploreOutcome outcome = createOutcome();
        ExplorePoint init = createPoint(this.startState);
        ExploreFrontier frontier = createFrontier();
        ExploreGoal goal = createGoal();
        frontier.add(init);
        ExplorePointSet succSet = createPointSet();
        outer: while (frontier.hasNext()) {
            ExplorePoint p = frontier.next();
            succSet.clear();
            while (p.hasNext() && !succSet.isFull()) {
                ExplorePoint succ = p.next();
                succSet.addPoint(succ);
                if (goal.test(succ.getState())) {
                    outcome.addResult(createProduct(succ));
                }
                if (Thread.interrupted()) {
                    outcome.setInterrupted();
                }
                if (outcome.isFinished()) {
                    break outer;
                }
            }
            if (isBacktrack() && p.hasNext()) {
                frontier.add(p);
            }
            frontier.addAll(succSet.getPoints());
        }
        return outcome;
    }

    /**
     * Indicates if the explore configuration implies backtracking to previously
     * (incompletely) explored states.
     * @return if the configuration implies backtracking
     */
    private boolean isBacktrack() {
        boolean result = false;
        if (this.config.getSuccessor() != SuccessorKind.ALL) {
            if (this.config.getFrontierSize() != 1) {
                result = true;
            }
        }
        return result;
    }

    /** Factory method to create an exploration goal. */
    protected ExploreGoal createGoal() {
        return this.config.getGoal()
            .getContent();
    }

    /** Factory method to create the initial explore point from a graph state. */
    protected ExplorePoint createPoint(GraphState state) {
        return new SimpleExplorePoint(state);
    }

    /** Factory method for an exploration frontier, based on the configuration. */
    protected ExploreFrontier createFrontier() {

    }

    /** Factory method to create an exploration product from an exploration point. */
    protected ExploreProduct createProduct(ExplorePoint point) {

    }

    /** Factory method for an exploration outcome, based on the configuration. */
    protected ExploreOutcome createOutcome() {
        int bound = (Integer) this.config.get(ExploreKey.RESULT_COUNT)
            .getContent();
        return new ExploreOutcome(bound);
    }

    /** Factory method for a successor set, based on the configuration. */
    protected ExplorePointSet createPointSet() {
        boolean singleton = false;
        if (this.config.getSuccessor() != SuccessorKind.ALL) {
            singleton = true;
        }
        return new ExplorePointSet(singleton);
    }
}
