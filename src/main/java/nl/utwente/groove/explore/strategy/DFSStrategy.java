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

import java.util.Deque;
import java.util.LinkedList;
import java.util.function.Predicate;

import nl.utwente.groove.lts.GraphState;

/**
 * Makes a depth first exploration by closing each visited states. Maintains a
 * stack for the order in which states are to be explored (thus is less memory
 * efficient). Is suitable for conditional strategies.
 *
 * This strategy is not considered as a backtracking strategy, as states are
 * fully explored and there is no need of maintaining caches.
 *
 * @author Iovka Boneva
 *
 */
public class DFSStrategy extends ClosingStrategy {
    /** Instantiates a conditional depth-first strategy, with a given continuation condition
     * and a moment at which to apply it.
     * @param moment moment at which to apply the condition
     * @param exploreCondition exploration continues for every state satisfying it
     * @param bound depth to which DFS continues; if 0, exploration is unbounded
     */
    public DFSStrategy(StopMode moment, Predicate<GraphState> exploreCondition, int bound) {
        super(moment, exploreCondition);
        this.bound = bound;
    }

    /** Instantiates an unconditional, unbounded depth-first strategy. */
    public DFSStrategy() {
        this(0);
    }

    /** Instantiates an unconditional, optionally bounded depth-first strategy.
     * @param bound depth to which DFS continues; if 0, exploration is unbounded
     */
    public DFSStrategy(int bound) {
        this.bound = bound;
    }

    private final int bound;

    @Override
    protected GraphState getFromPool() {
        var result = this.stack.poll();
        if (result != null) {
            int remaining;
            do {
                remaining = this.levelCount.pop();
                if (remaining > 0) {
                    this.levelCount.push(remaining - 1);
                    this.levelCount.push(0);
                }
            } while (remaining == 0);
        }
        return result;
    }

    @Override
    protected void putInPool(GraphState state) {
        if (this.bound == 0 || this.levelCount.size() - 1 < this.bound) {
            this.stack.push(state);
            this.levelCount.push(this.levelCount.pop() + 1);
        }
    }

    @Override
    protected void putBackInPool(GraphState state) {
        // the same as putInPool
        this.stack.push(state);
        this.levelCount.push(this.levelCount.pop() + 1);
    }

    @Override
    protected void clearPool() {
        this.stack.clear();
    }

    private final Deque<Integer> levelCount = new LinkedList<>();
    {
        this.levelCount.push(0);
    }
    private final Deque<GraphState> stack = new LinkedList<>();
}
