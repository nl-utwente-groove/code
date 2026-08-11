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

import nl.utwente.groove.explore.result.Acceptor;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.lts.GraphState;
import nl.utwente.groove.match.MatcherFactory;

/**
 * A partial (abstract) implementation of a strategy.
 * @author Arend Rensink
 *
 */
@NonNullByDefault
public abstract class GTSStrategy extends Strategy {
    @Override
    protected void prepare(GTS gts, @Nullable GraphState state, Acceptor acceptor) {
        super.prepare(gts, state, acceptor);
        this.gts = gts;
        var startState = state == null
            ? gts.startState()
            : state;
        this.nextState = this.startState = startState;
        this.acceptor = acceptor;
        gts.addLTSListener(acceptor);
        acceptor.addUpdate(gts, startState);
        MatcherFactory.instance(gts.hasSimpleGraphs())
            .setDefaultEngine();
    }

    @Override
    public boolean hasNext() {
        return getNextState() != null;
    }

    @Override
    public void finish() {
        var acceptor = this.acceptor;
        if (acceptor != null) {
            getGTS().removeLTSListener(acceptor);
        }
    }

    /**
     * The graph transition system explored by the strategy.
     * @return The graph transition system explored by the strategy;
     * non-{@code null} after a call to {@link #prepare}.
     */
    protected final GTS getGTS() {
        var result = this.gts;
        assert result != null : "Strategy not prepared";
        return result;
    }

    /**
     * The state at which the exploration starts.
     * @return the start state for exploration;
     * non-{@code null} after a call to {@link #prepare}.
     */
    protected final GraphState getStartState() {
        var result = this.startState;
        assert result != null : "Strategy not prepared";
        return result;
    }

    /**
     * Returns the state that will be explored next. If <code>null</code>,
     * there is nothing left to explore.
     */
    protected @Nullable GraphState getNextState() {
        return this.nextState;
    }

    /**
     * Sets the next state to be explored.
     * The next state is determined by a call to {@link #computeNextState()}.
     */
    protected final void setNextState() {
        this.nextState = computeNextState();
    }

    /**
     * Callback method to determine the next state to be explored. This is the place where
     * satisfaction of the condition is to be tested.
     * @return The next state to be explored, or {@code null} if exploration is done.
     */
    protected abstract @Nullable GraphState computeNextState();

    /** The graph transition system explored by the strategy;
     * set in {@link #prepare}. */
    private @Nullable GTS gts;
    /**
     * Start state for exploration, set in {@link #prepare}.
     */
    private @Nullable GraphState startState;
    /** The acceptor to be used at the next exploration;
     * set in {@link #prepare}. */
    private @Nullable Acceptor acceptor;
    /** The state that will be explored by the next call of {@link #doNext()}. */
    private @Nullable GraphState nextState;
}
