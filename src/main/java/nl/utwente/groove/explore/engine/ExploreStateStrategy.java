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
package nl.utwente.groove.explore.engine;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.lts.GraphState;

/**
 * Explores all outgoing transitions of a given state.
 * The GTS and the state should be set before using it.
 * @author Iovka Boneva
 *
 */
@NonNullByDefault
public class ExploreStateStrategy extends ClosingStrategy {
    @Override
    protected @Nullable GraphState getFromPool() {
        GraphState result = this.state;
        this.state = null;
        return result;
    }

    @Override
    protected void putInPool(GraphState state) {
        if (state == getStartState()) {
            this.state = state;
        }
    }

    @Override
    protected void putBackInPool(GraphState state) {
        if (state == getStartState()) {
            this.state = state;
        }
    }

    @Override
    protected void clearPool() {
        this.state = null;
    }

    private @Nullable GraphState state;
}
