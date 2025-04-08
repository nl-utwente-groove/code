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
package nl.utwente.groove.explore.strategy;

import java.util.LinkedList;
import java.util.function.Predicate;

import nl.utwente.groove.lts.GraphState;

/**
 * A breadth-first exploration that uses its own queue of open states.
 * Guarantees a breadth-first exploration, but consumes lots of memory.
 */
public class BFSStrategy extends ClosingStrategy {
    /** Instantiates a conditional breadth-first strategy, with a given continuation condition
     * and a moment at which to apply it.
     * @param stopMode moment at which to apply the condition
     * @param exploreCondition exploration continues for every state satisfying it
     */
    public BFSStrategy(StopMode stopMode, Predicate<GraphState> exploreCondition) {
        super(stopMode, exploreCondition);
        this.bound = 0;
    }

    /**
     * Instantiates an unconditional, unbounded breadth-first strategy.
     */
    public BFSStrategy() {
        this(0);
    }

    /**
     * Instantiates an unconditional, optionally bounded breadth-first strategy.
     * @param bound depth to which BFS continues; if 0, exploration is unbounded
     */
    public BFSStrategy(int bound) {
        this.bound = bound;
    }

    private final int bound;

    @Override
    protected GraphState getFromPool() {
        var result = this.stateQueue.poll();
        if (result == null) {
            // go to the next-depth queue
            this.stateQueue = this.nextDepthStateQueue;
            this.nextDepthStateQueue = new LinkedList<>();
            this.depth++;
            result = this.stateQueue.poll();
        }
        return result;
    }

    @Override
    protected void putInPool(GraphState state) {
        if (this.bound == 0 || this.depth < this.bound - 1) {
            this.nextDepthStateQueue.offer(state);
        }
    }

    @Override
    protected void putBackInPool(GraphState state) {
        // put in front, as this state was already scheduled for exploration
        this.stateQueue.addFirst(state);
    }

    @Override
    protected void clearPool() {
        this.stateQueue.clear();
        this.nextDepthStateQueue.clear();
    }

    /**
     * Queue of current-depth states to be explored.
     */
    private LinkedList<GraphState> stateQueue = new LinkedList<>();
    /**
     * Queue of next-depth states to be explored.
     */
    private LinkedList<GraphState> nextDepthStateQueue = new LinkedList<>();
    /** Exploration depth of the current state queue. */
    private int depth;
}
