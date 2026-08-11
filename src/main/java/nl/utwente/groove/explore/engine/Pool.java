/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.explore.engine;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.explore.strategy.ClosingStrategy;
import nl.utwente.groove.lts.GraphState;

/**
 * Policy determining the order in which a {@link FrontierStrategy} explores
 * the open states of its frontier. This makes the pool of
 * {@link ClosingStrategy} a first-class, injectable value, so that
 * exploration orders compose with the other exploration features rather
 * than multiplying strategy classes.
 * <p>
 * A pool may also impose a depth bound, by silently refusing to
 * {@link #add} states beyond the bound.
 * <p>
 * A pool never receives transient states: those are part of a nested
 * sub-exploration, ending in an atomic (transactional) transition, which the
 * strategy runs to completion on an internal stack, bypassing the pool.
 * Pool implementations may therefore drop or reorder states freely without
 * ever cutting into a transaction.
 * <p>
 * A pool instance is stateful and must not be shared between explorations.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public interface Pool {
    /**
     * Retrieves and removes the next state to be explored.
     * @return the next state, or {@code null} if the pool is empty,
     * meaning that exploration is done
     */
    @Nullable
    GraphState take();

    /**
     * Adds a freshly discovered state to the pool.
     * The pool may refuse the state, if it lies beyond a depth bound.
     */
    void add(GraphState state);

    /**
     * Re-adds a state that was already scheduled for exploration but
     * could not be fully explored in one go (because not all its rule
     * matches were scheduled at once). In contrast to {@link #add},
     * this is never subject to a depth bound.
     */
    void readd(GraphState state);

    /** Empties the pool, in preparation of a (new) exploration. */
    void clear();
}
