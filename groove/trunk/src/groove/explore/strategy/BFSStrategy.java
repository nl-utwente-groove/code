/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
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
package groove.explore.strategy;

import groove.lts.GraphState;

import java.util.LinkedList;

/**
 * A breadth-first exploration that uses its own queue of open states.
 * Guarantees a breadth-first exploration, but consumes lots of memory.
 */
public class BFSStrategy extends ClosingStrategy {
    @Override
    protected GraphState getFromPool() {
        return this.stateQueue.poll();
    }

    @Override
    protected void putInPool(GraphState state) {
        this.stateQueue.offer(state);
    }

    @Override
    protected void clearPool() {
        this.stateQueue.clear();
    }

    /**
     * Queue of states to be explored. The set of outgoing transitions of the
     * parent state is included with each state.
     */
    private final LinkedList<GraphState> stateQueue =
        new LinkedList<>();
}
