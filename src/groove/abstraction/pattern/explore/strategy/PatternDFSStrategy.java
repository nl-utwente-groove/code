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
package groove.abstraction.pattern.explore.strategy;

import groove.abstraction.pattern.lts.PatternState;
import groove.explore.strategy.DFSStrategy;

import java.util.Stack;

/**
 * See {@link DFSStrategy}.
 */
public final class PatternDFSStrategy extends ClosingPatternStrategy {

    private final Stack<PatternState> stack = new Stack<PatternState>();

    @Override
    protected void putInPool(PatternState element) {
        this.stack.push(element);
    }

    @Override
    protected PatternState getFromPool() {
        PatternState result;
        do {
            result = pop();
        } while (result != null && result.isSubsumed());
        return result;
    }

    @Override
    protected void clearPool() {
        this.stack.clear();
    }

    private PatternState pop() {
        if (this.stack.isEmpty()) {
            return null;
        } else {
            return this.stack.pop();
        }
    }

}
