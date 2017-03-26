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

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import groove.lts.GTS;
import groove.lts.GTSListener;
import groove.lts.GraphNextState;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.MatchResult;
import groove.lts.Status.Flag;

/**
 * State in an exploration.
 * Combines a {@link GraphState} with functionality to explore its successors
 * in the order determined by the explore configuration, and to construct an
 * appropriate {@link ExploreProduct}.
 * @author Arend Rensink
 * @version $Revision $
 */
class ExplorePoint {
    /** Constructs in initial exploration point from a graph state. */
    ExplorePoint(ExploreInstance exploration, GraphState state) {
        this.exploration = exploration;
        this.state = state;
        this.cost = 0;
        if (exploration.isComputeHeuristic()) {
            this.priority = exploration.computeHeuristic(state);
        } else {
            this.priority = 0;
        }
        setGTSListener();
    }

    /** Constructs an exploration point for a given state, with a given parent. */
    private ExplorePoint(ExplorePoint parent, GraphState state) {
        this.exploration = parent.getExploration();
        this.state = state;
        // compute the cost of this explore point
        int cost = parent.getCost();
        if (this.exploration.isComputeCost()) {
            GraphState target = state;
            // follow incoming transitions back to parent
            do {
                GraphTransition trans = ((GraphNextState) target).getInTransition();
                cost += this.exploration.computeCost(trans.label());
                target = trans.source();
            } while (target != parent.getState());
        }
        this.cost = cost;
        // compute the priority of this explore point
        int priority = cost;
        if (this.exploration.isComputeHeuristic()) {
            priority += this.exploration.computeHeuristic(this.state);
        }
        this.priority = priority;
        setGTSListener();
    }

    /**
     * Returns the exploration instance that has generated this point.
     */
    public ExploreInstance getExploration() {
        return this.exploration;
    }

    /** Exploration instance to which this exploration point belongs. */
    private final ExploreInstance exploration;

    private void setGTSListener() {
        this.state.getGTS()
            .addLTSListener(new GTSListener() {
                @Override
                public void addUpdate(GTS gts, GraphState state) {
                    testAndAdd(state);
                }

                @Override
                public void statusUpdate(GTS gts, GraphState state, int change) {
                    if (Flag.TRANSIENT.test(change)) {
                        // the transience just changed, so maybe this state is now eligible
                        testAndAdd(state);
                    }
                }

                /** Adds a state to the set of discovered successor states
                 * if it is a real and non-transient state
                 */
                private void testAndAdd(GraphState state) {
                    if (state.isRealState() && !state.isTransient()) {
                        ExplorePoint.this.nextStates.add(state);
                    }
                }
            });
    }

    private Queue<GraphState> nextStates = new LinkedList<>();

    /**
     * The next successor to be returned by {@link #next()}.
     * The value is computed by a call to {@link #hasNext()}.
     */
    private ExplorePoint next;

    /** Flag indicating that the search for a next successor has not yet failed. */
    private boolean hasNext;

    /** Indicates if this explore point has unexplored successors. */
    public boolean hasNext() {
        if (this.next == null && this.hasNext) {
            GraphState nextState = computeNext();
            if (nextState == null) {
                this.hasNext = false;
            } else {
                this.next = createPoint(nextState);
            }
        }
        return this.next == null;
    }

    /** Computes and returns the next unexplored successor of {@link #state},
     * if any.
     * @return the next unexplored successor of {@link #state},
     * or {@code null} if there is none
     */
    private GraphState computeNext() {
        GraphState result = this.nextStates.poll();
        while (result == null && !this.state.isClosed()) {
            exploreNextMatch();
            result = this.nextStates.poll();
        }
        return result;
    }

    /**
     * Explores the next match of {@link #state}, up to the next real, non-transient states.
     * After finishing, the discovered successor states (if any) will
     * be collected in {@link #nextStates}
     */
    private void exploreNextMatch() {
        Stack<GraphState> stack = new Stack<>();
        stack.push(this.state);
        while (!stack.isEmpty()) {
            GraphState top = stack.pop();
            MatchResult nextMatch = top.getMatch();
            if (nextMatch == null) {
                // top is exhausted; continue with next stack element
                continue;
            }
            GraphState succ = top.applyMatch(nextMatch)
                .target();
            // top may now have become non-transient or closed
            if (top.isTransient() && !top.isClosed()) {
                // otherwise, put top back in stack for backtracking
                stack.push(top);
            }
            // put successor on stack if transient
            if (succ.isTransient()) {
                stack.push(succ);
            }
        }
    }

    /** Factory method for an exploration point. */
    private ExplorePoint createPoint(GraphState state) {
        return new ExplorePoint(this, state);
    }

    /** Returns the next unexplored successor of this explore point. */
    public ExplorePoint next() {
        if (hasNext()) {
            ExplorePoint result = this.next;
            this.next = null;
            return result;
        } else {
            throw new IllegalStateException(String.format("State %s is closed", this.state));
        }
    }

    /** Returns the graph state wrapped in this explore point. */
    public GraphState getState() {
        return this.state;
    }

    /** Graph state wrapped in this exploration point. */
    private final GraphState state;

    /** Returns the cost of this explore point. */
    public int getCost() {
        return this.cost;
    }

    /** The cost of this exploration point.
     */
    private final int cost;

    /** Returns the sum of the cost and heuristic of this explore point. */
    public int getPriority() {
        return this.priority;
    }

    /** The priority of this exploration point, used if
     * there is a cost or heuristic function involved.
     */
    private final int priority;
}
