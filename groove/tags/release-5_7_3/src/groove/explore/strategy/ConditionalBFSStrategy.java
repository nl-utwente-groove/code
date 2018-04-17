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

import groove.explore.result.ExploreCondition;
import groove.lts.GraphState;

/**
 * Breadth first exploration, by exploring only non explored states.
 * @author Iovka Boneva
 * 
 */
public class ConditionalBFSStrategy extends BFSStrategy {
    @Override
    protected GraphState computeNextState() {
        GraphState result = super.computeNextState();
        while (result != null && !getExplCond().isSatisfied(result)) {
            result = super.computeNextState();
        }
        return result;
    }

    /**
     * The exploration condition that, when not satisfied by a state, forbids
     * exploring this state.
     */
    public void setExploreCondition(ExploreCondition<?> condition) {
        this.explCond = condition;
    }

    private ExploreCondition<?> getExplCond() {
        return this.explCond;
    }

    private ExploreCondition<?> explCond;

}
