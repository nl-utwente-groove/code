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

import java.util.Deque;
import java.util.LinkedList;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.lts.GraphState;

/**
 * Depth-first pool: the most recently discovered state is explored first.
 * The ordering (including the bookkeeping of exploration depth for the
 * optional depth bound) replicates that of the legacy {@code DFSStrategy}.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class StackPool implements Pool {
    /**
     * Creates a depth-first pool with an optional depth bound.
     * @param bound depth up to which states are added; 0 means unbounded
     */
    public StackPool(int bound) {
        this.bound = bound;
    }

    private final int bound;

    @Override
    public @Nullable GraphState take() {
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
    public void add(GraphState state) {
        if (this.bound == 0 || this.levelCount.size() - 1 < this.bound) {
            this.stack.push(state);
            this.levelCount.push(this.levelCount.pop() + 1);
        }
    }

    @Override
    public void readd(GraphState state) {
        // as add, but never subject to the depth bound
        this.stack.push(state);
        this.levelCount.push(this.levelCount.pop() + 1);
    }

    @Override
    public void clear() {
        this.stack.clear();
        this.levelCount.clear();
        this.levelCount.push(0);
    }

    /** Per-level count of states still to be explored on that level. */
    private final Deque<Integer> levelCount = new LinkedList<>();
    {
        this.levelCount.push(0);
    }
    /** Stack of states to be explored. */
    private final Deque<GraphState> stack = new LinkedList<>();
}
