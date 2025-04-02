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
    }

    /** Instantiates an unconditional breadth-first strategy. */
    public BFSStrategy() {
        // empty
    }

    @Override
    protected GraphState getFromPool() {
        return this.stateQueue.poll();
    }

    @Override
    protected void putInPool(GraphState state) {
        this.stateQueue.offer(state);
    }

    @Override
    protected void putBackInPool(GraphState state) {
        // put in front, as this state was already scheduled for exploration
        this.stateQueue.addFirst(state);
    }

    @Override
    protected void clearPool() {
        this.stateQueue.clear();
    }

    /**
     * Queue of states to be explored. The set of outgoing transitions of the
     * parent state is included with each state.
     */
    private final LinkedList<GraphState> stateQueue = new LinkedList<>();
}
