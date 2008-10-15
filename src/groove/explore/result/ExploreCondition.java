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
package groove.explore.result;

import groove.lts.GraphState;
import groove.util.Property;

/**
 * Defines a condition that may or not hold in a {@link GraphState}. The
 * condition may be negated. Such conditions may be used by strategies in order
 * to explore only states that satisfy the condition.
 * @author Iovka Boneva
 * 
 * @param <C> Type of the object defining the condition.
 */
public abstract class ExploreCondition<C> extends Property<GraphState> {
    /**
     * The parameter determines whether the condition is to be checked
     * positively or negatively.
     */
    public void setNegated(boolean b) {
        this.negated = b;
    }

    /** Indicates whether the condition is negated or not. */
    public boolean isNegated() {
        return this.negated;
    }

    /**
     * Sets the condition.
     * @param condition should not be null
     */
    public void setCondition(C condition) {
        this.condition = condition;
    }

    /**
     * The type of the actual condition.
     */
    public Class<?> getConditionType() {
        return this.condition.getClass();
    }

    /** Indicates whether the condition is negated. */
    protected boolean negated;
    /** The condition. */
    protected C condition;
}
