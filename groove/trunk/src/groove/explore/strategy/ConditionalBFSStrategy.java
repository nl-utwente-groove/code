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

import groove.explore.result.OldExploreCondition;

/**
 * Breadth first exploration, by exploring only non explored states.
 * @author Iovka Boneva
 * 
 */
public class ConditionalBFSStrategy extends BFSStrategy implements
        ConditionalStrategy {
    @Override
    protected PoolElement getFromPool() {
        PoolElement result = super.getFromPool();
        while (result != null && !getExplCond().isSatisfied(result.first())) {
            result = super.getFromPool();
        }
        return result;
    }

    public void setExploreCondition(OldExploreCondition<?> condition) {
        this.explCond = condition;
    }

    private OldExploreCondition<?> getExplCond() {
        return this.explCond;
    }

    private OldExploreCondition<?> explCond;

}
