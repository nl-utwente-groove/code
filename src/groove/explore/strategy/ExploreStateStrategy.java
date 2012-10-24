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

import java.util.Stack;

/**
 * Explores all outgoing transitions of a given state.
 * @author Iovka Boneva
 * 
 */
public class ExploreStateStrategy extends ClosingStrategy {
    /**
     * Creates a strategy with empty graph transition system and empty start
     * state. The GTS and the state should be set before using it.
     * 
     */
    public ExploreStateStrategy() {
        // empty
    }

    @Override
    protected boolean isSuitableKnownState(GraphState state) {
        return state == getStartState() || state.isTransient();
    }

    @Override
    protected GraphState getNextState() {
        if (this.stack.isEmpty()) {
            return null;
        } else {
            return this.stack.pop();
        }
    }

    @Override
    protected void putInPool(GraphState state) {
        if (state == getStartState() || state.isTransient()) {
            // insert on top of the stack
            this.stack.push(state);
        }
    }

    @Override
    protected void clearPool() {
        this.stack.clear();
    }

    private final Stack<GraphState> stack = new Stack<GraphState>();
}
