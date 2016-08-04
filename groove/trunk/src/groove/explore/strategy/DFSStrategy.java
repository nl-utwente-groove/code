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
    @Override
    protected GraphState getFromPool() {
        if (this.stack.isEmpty()) {
            return null;
        } else {
            return this.stack.pop();
        }
    }

    @Override
    protected void putInPool(GraphState state) {
        this.stack.push(state);
    }

    @Override
    protected void clearPool() {
        this.stack.clear();
    }

    private final Stack<GraphState> stack = new Stack<>();
}
