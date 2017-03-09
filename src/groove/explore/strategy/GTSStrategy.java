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

import groove.explore.result.Acceptor;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.match.MatcherFactory;

/**
 * A partial (abstract) implementation of a strategy.
 * @author Arend Rensink
 *
 */
public abstract class GTSStrategy extends Strategy {
    @Override
    protected void prepare(GTS gts, GraphState state, Acceptor acceptor) {
        super.prepare(gts, state, acceptor);
        this.gts = gts;
        this.nextState = this.startState = state == null ? gts.startState() : state;
        this.acceptor = acceptor;
        if (acceptor != null) {
            gts.addLTSListener(acceptor);
            acceptor.addUpdate(gts, this.startState);
        }
        MatcherFactory.instance(gts.isSimple())
            .setDefaultEngine();
    }

    @Override
    public boolean hasNext() {
        return getNextState() != null;
    }

    @Override
    public void finish() {
        if (this.acceptor != null) {
            getGTS().removeLTSListener(this.acceptor);
        }
    }

    /**
     * The graph transition system explored by the strategy.
     * @return The graph transition system explored by the strategy.
     */
    protected final GTS getGTS() {
        return this.gts;
    }

    /**
     * The start state set at construction time.
     * @return the start state for exploration; may be {@code null}.
     */
    protected final GraphState getStartState() {
        return this.startState;
    }

    /**
     * Returns the state that will be explored next. If <code>null</code>,
     * there is nothing left to explore.
     */
    protected GraphState getNextState() {
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
    protected abstract GraphState computeNextState();

    /** The graph transition system explored by the strategy. */
    private GTS gts;
    /**
     * Start state for exploration, set in the constructor.
     * If {@code null}, the GTS start state is selected at exploration time.
     */
    private GraphState startState;
    /** The acceptor to be used at the next exploration. */
    private Acceptor acceptor;
    /** The state that will be explored by the next call of {@link #doNext()}. */
    private GraphState nextState;
}
