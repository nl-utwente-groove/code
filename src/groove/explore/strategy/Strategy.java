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
import groove.lts.GraphState.Flag;

/**
 * A strategy defines an order in which the states of a graph transition system
 * are to be explored. It can also determine which states are to be explored
 * because of the nature of the strategy (see for instance
 * {@link LinearStrategy}). Most often, a strategy starts its exploration at
 * some state, fixed by the {@link #setGTS(GTS, GraphState)} method.
 */
public abstract class Strategy {
    /**
     * Constructs a new strategy,
     * under the assumption that the instantiated class
     * is itself an instance of {@link ExploreIterator}.
     */
    protected Strategy() {
        this.iterator = (ExploreIterator) this;
    }

    /**
     * Constructs a new strategy,
     * for a given exploration iterator.
     * @param iterator the exploration iterator to be used.
     */
    protected Strategy(ExploreIterator iterator) {
        this.iterator = iterator;
    }

    /**
     * Sets the GTS to be explored. Also sets the exploration start state to the
     * GTS start state. Convenience method for {@link #setGTS(GTS, GraphState)}.
     * @see #setGTS(GTS, GraphState)
     */
    final public void setGTS(GTS gts) {
        this.setGTS(gts, null);
    }

    /**
     * Sets the GTS and start state to be explored. This is done in preparation
     * to a call of {@link #play()}.
     * It is assumed that the state (if not {@code null} is already in the GTS.
     * @param gts the GTS to be explored
     * @param state the start state for the exploration; if <code>null</code>,
     * the GTS start state is used
     */
    final public void setGTS(GTS gts, GraphState state) {
        this.gts = gts;
        this.startState = state;
    }

    /**
     * Adds an acceptor to the strategy.
     */
    final public void setAcceptor(Acceptor listener) {
        this.acceptor = listener;
    }

    /** Plays out this strategy, until the thread is interrupted or exploration is done. */
    final public void play() {
        play(null);
    }

    /** 
     * Plays out this strategy, until a halting condition kicks in, 
     * the thread is interrupted or exploration is done.
     * @param halter halting condition invoked after each state exploration;
     * ignored if {@code null} 
     */
    final public void play(Halter halter) {
        this.iterator.prepare(this.gts, this.startState, this.acceptor);
        collectKnownStates();
        this.interrupted = false;
        while ((halter == null || !halter.halt()) && this.iterator.hasNext()
            && !testInterrupted()) {
            this.lastState = this.iterator.doNext();
        }
        this.iterator.finish();
    }

    /**
     * Sets all states already in the state space to Flag.KNOWN.
     */
    private void collectKnownStates() {
        for (GraphState next : this.gts.nodeSet()) {
            next.setFlag(Flag.KNOWN, true);
        }
    }

    /** Signals if the last invocation of {@link #play} finished because the thread was interrupted. */
    final public boolean isInterrupted() {
        return this.interrupted;
    }

    /** 
     * Tests if the thread has been interrupted, and stores the
     * result.
     */
    private boolean testInterrupted() {
        boolean result = this.interrupted;
        if (!result) {
            result = this.interrupted = Thread.currentThread().isInterrupted();
        }
        return result;
    }

    /** Returns the last state explored by the last invocation of {@link #play}. 
     */
    final public GraphState getLastState() {
        return this.lastState;
    }

    private final ExploreIterator iterator;
    /** Flag indicating that the last invocation of {@link #play} was interrupted. */
    private boolean interrupted;
    /** The graph transition system explored by the strategy. */
    private GTS gts;
    /**
     * Start state for exploration, set in the constructor.
     * If {@code null}, the GTS start state is selected at exploration time.
     */
    private GraphState startState;
    /** The acceptor to be used at the next exploration. */
    private Acceptor acceptor;
    /** The state returned by the last call of {@link ExploreIterator#doNext()}. */
    private GraphState lastState;

    /** Interface for a halting condition on exploration. */
    public interface Halter {
        /** Callback method to determine whether exploration should halt. */
        public boolean halt();
    }
}
