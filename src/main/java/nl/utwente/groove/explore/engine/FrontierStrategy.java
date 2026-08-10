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

import java.util.function.Predicate;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.explore.strategy.ClosingStrategy;
import nl.utwente.groove.explore.strategy.StopMode;
import nl.utwente.groove.lts.GraphState;

/**
 * Parametric frontier-based exploration strategy: the exploration order is
 * determined by an injected {@link Pool} rather than by subclassing, so that
 * it composes with the conditional-exploration features (stop mode and
 * stop condition) inherited from {@link ClosingStrategy}.
 * This is the engine counterpart of the exploration configuration's
 * search-order features; it subsumes the retired legacy {@code BFSStrategy}
 * and {@code DFSStrategy} classes.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class FrontierStrategy extends ClosingStrategy {
    /**
     * Creates an unconditional frontier-based strategy with a given
     * exploration order.
     * @param pool determines the exploration order (and possibly a depth bound)
     */
    public FrontierStrategy(Pool pool) {
        this.pool = pool;
    }

    /**
     * Creates a conditional frontier-based strategy, with a given stop
     * condition and a moment at which to apply it.
     * @param stopMode moment at which to apply the condition
     * @param stopCondition exploration stops at every state satisfying it; the
     * stop mode determines whether such a state is itself still explored
     * @param pool determines the exploration order (and possibly a depth bound)
     */
    public FrontierStrategy(StopMode stopMode, Predicate<GraphState> stopCondition, Pool pool) {
        super(stopMode, stopCondition);
        this.pool = pool;
    }

    private final Pool pool;

    @Override
    protected @Nullable GraphState getFromPool() {
        return this.pool.take();
    }

    @Override
    protected void putInPool(GraphState state) {
        assert !state.isTransient();
        this.pool.add(state);
    }

    @Override
    protected void putBackInPool(GraphState state) {
        assert !state.isTransient();
        this.pool.readd(state);
    }

    @Override
    protected void clearPool() {
        this.pool.clear();
    }
}
