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

import java.util.function.BooleanSupplier;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.lts.GraphState;
import nl.utwente.groove.lts.Status.Flag;
import nl.utwente.groove.match.MatchBoundException;

/**
 * A strategy defines an order in which the states of a graph transition system
 * are to be explored. It can also determine which states are to be explored
 * because of the nature of the strategy (see for instance
 * {@link LinearStrategy}).
 * To use, call {@link #setGTS} and optionally {@link #setState} and
 * {@link #setHalt}, then call {@link #play()}.
 */
@NonNullByDefault
public abstract class Strategy {
    /**
     * Sets the GTS to be explored, in preparation
     * to a call of {@link #play()}.
     * Also sets the state to be explored to {@code null},
     * meaning that exploration will start at the start state of the GTS.
     */
    final public void setGTS(GTS gts) {
        this.gts = gts;
        this.startState = null;
    }

    /**
     * Sets the state to be explored, in preparation to a call of {@link #play()}.
     * It is assumed that the state (if not {@code null}) is already in the GTS.
     * @param state the start state for the exploration; if {@code null},
     * the GTS start state will be used
     */
    final public void setState(@Nullable GraphState state) {
        this.startState = state;
    }

    /**
     * Sets the halt condition, consulted before every exploration step:
     * when it holds, exploration stops. The default is to never halt, so
     * that exploration continues until the strategy is exhausted.
     */
    final public void setHalt(BooleanSupplier halt) {
        this.halt = halt;
    }

    /**
     * Plays out this strategy, until the halt condition kicks in,
     * the thread is interrupted or the strategy is exhausted.
     * @throws MatchBoundException if the match bound was exceeded during
     * exploration; the strategy is finished before the exception propagates,
     * so that any enclosing strategy or exploration also halts
     */
    final public void play() throws MatchBoundException {
        var gts = this.gts;
        assert gts != null : "GTS not initialised";
        prepare(gts, this.startState);
        collectKnownStates(gts);
        this.interrupted = false;
        try {
            while (!this.halt.getAsBoolean() && hasNext() && !testInterrupted()) {
                this.lastState = doNext();
            }
        } catch (InterruptedException exc) {
            // exploration was interrupted by a cancelled oracle input
        } catch (MatchBoundException exc) {
            // the match bound was exceeded: count this as an interruption of
            // this strategy, and propagate after cleaning up (see gh #784)
            this.interrupted = true;
            throw exc;
        } finally {
            finish();
        }
    }

    /**
     * Callback method to initialise the iterator for exploring a given
     * GTS, starting from a given state.
     * @param gts the GTS to be explored; non-{@code null}
     * @param state the state at which exploration should
     * start; may be {@code null}, in which case the GTS' start state is to be used
     */
    protected void prepare(GTS gts, @Nullable GraphState state) {
        // no preparation by default
    }

    /**
     * Performs the next step in the exploration.
     * Should be called only if {@link #hasNext} holds.
     * @return the (last) state explored as a result of this call.
     * @throws InterruptedException if an oracle input was cancelled
     */
    abstract public GraphState doNext() throws InterruptedException;

    /** Indicates if there is a next step in the exploration. */
    abstract public boolean hasNext();

    /**
     * Callback method invoked after exploration has finished.
     * After this method, the only next operation allowed is
     * {@link #prepare}. This implementation does nothing.
     */
    public void finish() {
        // no clean-up by default
    }

    /**
     * Sets all states already in the state space to Flag.KNOWN.
     */
    private void collectKnownStates(GTS gts) {
        for (GraphState next : gts.nodeSet()) {
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
            result = this.interrupted = Thread.currentThread()
                .isInterrupted();
        }
        return result;
    }

    /** Returns the last state explored by the last invocation of {@link #play};
     * {@code null} if no state was explored yet.
     */
    final public @Nullable GraphState getLastState() {
        return this.lastState;
    }

    /** Flag indicating that the last invocation of {@link #play} was interrupted. */
    private boolean interrupted;
    /** The graph transition system explored by the strategy;
     * set by {@link #setGTS}. */
    private @Nullable GTS gts;
    /**
     * Start state for exploration, set by {@link #setState}.
     * If {@code null}, the GTS start state is selected at exploration time.
     */
    private @Nullable GraphState startState;
    /** The halt condition consulted before every exploration step;
     * set by {@link #setHalt}. */
    private BooleanSupplier halt = () -> false;
    /** The state returned by the last call of {@link #doNext()}. */
    private @Nullable GraphState lastState;
}
