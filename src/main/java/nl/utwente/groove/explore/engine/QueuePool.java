/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.explore.engine;

import java.util.LinkedList;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.lts.GraphState;

/**
 * Breadth-first pool: states are explored in the order in which they were
 * discovered, resulting in exploration by increasing depth. Guarantees a
 * breadth-first exploration, but consumes lots of memory.
 * The ordering replicates that of the legacy {@code BFSStrategy}.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class QueuePool implements Pool {
    /**
     * Creates a breadth-first pool with an optional depth bound.
     * @param bound depth up to which states are added; 0 means unbounded
     */
    public QueuePool(int bound) {
        this.bound = bound;
    }

    private final int bound;

    @Override
    public @Nullable GraphState take() {
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
    public void add(GraphState state) {
        if (this.bound == 0 || this.depth < this.bound - 1) {
            this.nextDepthStateQueue.offer(state);
        }
    }

    @Override
    public void readd(GraphState state) {
        // put in front, as this state was already scheduled for exploration
        this.stateQueue.addFirst(state);
    }

    @Override
    public void clear() {
        this.stateQueue.clear();
        this.nextDepthStateQueue.clear();
        this.depth = 0;
    }

    /** Queue of current-depth states to be explored. */
    private LinkedList<GraphState> stateQueue = new LinkedList<>();
    /** Queue of next-depth states to be explored. */
    private LinkedList<GraphState> nextDepthStateQueue = new LinkedList<>();
    /** Exploration depth of the current state queue. */
    private int depth;
}
