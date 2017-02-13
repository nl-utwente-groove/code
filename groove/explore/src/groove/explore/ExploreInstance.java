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

import java.util.function.Function;

import groove.explore.config.BoundKind;
import groove.explore.config.BoundSetting;
import groove.explore.config.CostKind;
import groove.explore.config.ExploreKey;
import groove.explore.config.HeuristicKind;
import groove.explore.config.SuccessorKind;
import groove.explore.config.TraverseKind;
import groove.grammar.host.HostGraph;
import groove.lts.ActionLabel;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.util.Exceptions;

/**
 * General class for exploration, used for all cases except model checking.
 * @author Arend Rensink
 * @version $Revision $
 */
public class ExploreInstance {
    /** Creates an exploration instance from an exploration configuration and a start state. */
    public ExploreInstance(ExploreConfig config, GTS gts, GraphState startState) {
        this.config = config;
        this.computePriority = config.getCost()
            .getKind() != CostKind.NONE
            && config.getHeuristic()
                .getKind() != HeuristicKind.NONE;
        this.costFunction = config.getCost()
            .getContent();
        this.heuristicFunction = config.getHeuristic()
            .getContent();
        this.startState = startState;
    }

    /** The explore configuration according to which this instance is configured. */
    private final ExploreConfig config;

    /** The start state for the exploration. */
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

    boolean isComputePriority() {
        return this.computePriority;
    }

    private final boolean computePriority;

    int computeCost(ActionLabel label) {
        return this.costFunction.apply(label);
    }

    private final Function<ActionLabel,Integer> costFunction;

    int computeHeuristic(GraphState state) {
        return this.heuristicFunction.apply(state.getGraph());
    }

    private final Function<HostGraph,Integer> heuristicFunction;

    /** Factory method to create an exploration goal. */
    protected ExploreGoal createGoal() {
        return this.config.getGoal()
            .getContent();
    }

    /** Factory method to create the initial explore point from a graph state. */
    protected ExplorePoint createPoint(GraphState state) {
        return new SimpleExplorePoint(this, state);
    }

    /** Factory method for an exploration frontier, based on the configuration. */
    protected ExploreFrontier createFrontier() {
        ExploreFrontier result;
        if (this.config.getFrontierSize() == 1) {
            result = new SingularFrontier();
        } else {
            TraverseKind traverse = this.config.getTraversal();
            int maxSize = this.config.getFrontierSize();
            if (isComputePriority()) {
                result =
                    new PriorityFrontier(() -> BasicFrontier.createFrontier(traverse), maxSize);
            } else {
                result = BasicFrontier.createFrontier(traverse, maxSize);
            }
            BoundSetting bound = this.config.getBound();
            if (bound.getKind() != BoundKind.NONE) {
                result = new BoundedFrontier(result, bound.getContent(), bound.getBound(),
                    bound.getIncrement());
            }
        }
        return result;
    }

    /** Factory method to create an exploration product from an exploration point. */
    protected ExploreProduct createProduct(ExplorePoint point) {
        GraphState state = point.getState();
        switch (this.config.getResultType()) {
        case PATH:
            return new TraceExploreProduct(state);
        case STATE:
            return new StateExploreProduct(state);
        default:
            throw Exceptions.UNREACHABLE;
        }
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
