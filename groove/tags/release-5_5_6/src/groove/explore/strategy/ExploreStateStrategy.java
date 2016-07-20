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

import groove.explore.result.NoStateAcceptor;
import groove.lts.GraphState;

/**
 * Explores all outgoing transitions of a given state.
 * @author Iovka Boneva
 *
 */
public class ExploreStateStrategy extends ClosingStrategy {
    /**
     * Creates a strategy with empty graph transition system and empty start
     * state. The GTS and the state should be set before using it.
     * The acceptor is initialised to {@link NoStateAcceptor}.
     */
    public ExploreStateStrategy() {
        setAcceptor(NoStateAcceptor.INSTANCE.newAcceptor(0));
    }

    @Override
    protected GraphState getFromPool() {
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
    protected void clearPool() {
        this.state = null;
    }

    private GraphState state;
}
