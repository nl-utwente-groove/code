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

import org.eclipse.jdt.annotation.NonNullByDefault;

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
@NonNullByDefault
public abstract class ExploreCondition<C> extends Property<GraphState> {
    /** Constructor to initialise the condition, to be used by subclasses.
     * The condition is not negated.
     */
    protected ExploreCondition(C condition) {
        this(condition, false);
    }

    /** Constructor to initialise the condition, to be used by subclasses.
     * The condition is optionally negated.
     */
    protected ExploreCondition(C condition, boolean negated) {
        this.condition = condition;
        this.negated = negated;
    }

    /** Indicates whether the condition is negated or not. */
    public boolean isNegated() {
        return this.negated;
    }

    /**
     * Gets the condition.
     */
    public C getCondition() {
        return this.condition;
    }

    /**
     * The type of the actual condition.
     */
    public Class<?> getConditionType() {
        return this.condition.getClass();
    }

    /** Indicates whether the condition is negated. */
    protected final boolean negated;
    /** The condition. */
    protected final C condition;
}
