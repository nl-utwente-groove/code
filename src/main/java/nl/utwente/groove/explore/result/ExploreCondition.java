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
package nl.utwente.groove.explore.result;

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.lts.GraphState;
import nl.utwente.groove.util.Property;

/**
 * Defines a condition that may or not hold in a {@link GraphState}. The
 * condition may be negated. Such conditions may be used by strategies in order
 * to explore only states that satisfy the condition.
 * @author Iovka Boneva
 *
 * @param <C> Type of the object defining the criterion for the condition.
 */
@NonNullByDefault
public abstract class ExploreCondition<C> extends Property<GraphState> {
    /** Constructor to initialise the condition, to be used by subclasses.
     * The condition is not negated.
     */
    protected ExploreCondition(C criterion) {
        this(criterion, false);
    }

    /** Constructor to initialise the condition, to be used by subclasses.
     * The condition is optionally negated.
     */
    protected ExploreCondition(C criterion, boolean negated) {
        this.criterion = criterion;
        this.negated = negated;
    }

    /** Indicates whether the condition is negated or not. */
    public boolean isNegated() {
        return this.negated;
    }

    /**
     * Gets the condition.
     */
    public C criterion() {
        return this.criterion;
    }

    /** Indicates whether the condition is negated. */
    private final boolean negated;
    /** The condition. */
    private final C criterion;
}
