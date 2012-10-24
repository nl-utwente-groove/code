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
import groove.match.MatcherFactory;
import groove.trans.GraphGrammar;
import groove.trans.SystemRecord;
import groove.view.FormatException;

/**
 * A partial (abstract) implementation of a strategy.
 * @author Arend Rensink
 * 
 */
public abstract class AbstractStrategy implements Strategy {
    @Override
    public void checkCompatible(GraphGrammar grammar) throws FormatException {
        // does nothing.
    }

    final public void prepare(GTS gts) {
        this.setGTS(gts, null);
    }

    final public void setGTS(GTS gts, GraphState state) {
        this.gts = gts;
        this.startState = state;
    }

    @Override
    final public void play() {
        play(null);
    }

    @Override
    final public void play(Halter halter) {
        prepare();
        collectKnownStates();
        this.interrupted = false;
        while ((halter == null || !halter.halt()) && hasState()
            && !testInterrupted()) {
            next();
        }
        finish();
    }

    /** Notification method invoked when the exploration is started. */
    protected void prepare() {
        assert this.gts != null : "GTS has not been set";
        this.atState =
            this.startState == null ? this.gts.startState() : this.startState;
        MatcherFactory.instance().setDefaultEngine();
    }

    /**
     * Sets all states already in the state space to Flag.KNOWN.
     */
    private void collectKnownStates() {
        for (GraphState next : getGTS().nodeSet()) {
            next.setFlag(Flag.KNOWN, isSuitableKnownState(next));
        }
    }

    /**
     * Callback method to determine if a given state may be added to the
     * set of known states.
     */
    protected boolean isSuitableKnownState(GraphState state) {
        return true;
    }

    /**
     * Notification method invoked when the exploration is stopped for any reason.
     * Reasons may be: the halter condition has kicked in, or the thread has been 
     * interrupted, or exploration is done. 
     */
    protected void finish() {
        // empty
    }

    @Override
    final public boolean isInterrupted() {
        return this.interrupted;
    }

    /** 
     * Tests if the thread has been interrupted, and stores the
     * result.
     */
    final protected boolean testInterrupted() {
        boolean result = this.interrupted;
        if (!result) {
            result = this.interrupted = Thread.currentThread().isInterrupted();
        }
        return result;
    }

    /**
     * Executes one step of the strategy.
     * @require The previous call of this method, if any, returned
     *          <code>true</code>. Otherwise, the behaviour is not
     *          guaranteed.
     */
    abstract protected void next();

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

    /** Indicates if there is a next state to be explored. */
    final protected boolean hasState() {
        return getState() != null;
    }

    /**
     * Returns the state that will be explored next. If <code>null</code>,
     * there is nothing left to explore. Is updated by {@link #getNextState()}.
     */
    protected GraphState getState() {
        return this.atState;
    }

    @Override
    public GraphState getLastState() {
        return this.lastState;
    }

    /**
     * Callback method to set the next state to be explored (which itself is 
     * determined by a call to {@link #getNextState()}), if any. This method
     * should be the only one who updates atState.
     * @see #getNextState()
     */
    final protected void updateState() {
        this.lastState = getState();
        this.atState = getNextState();
    }

    /**
     * Callback method to determine the next state to be explored. This is the place where
     * satisfaction of the condition is to be tested.
     * @return The next state to be explored, or {@code null} if exploration is done.
     */
    protected abstract GraphState getNextState();

    @Override
    public void addGTSListener(Acceptor listener) {
        getGTS().addLTSListener(listener);
    }

    @Override
    public void removeGTSListener(Acceptor listener) {
        getGTS().removeLTSListener(listener);
    }

    /** Convenience method to retrieve the GTS' system record. */
    final protected SystemRecord getRecord() {
        return getGTS().getRecord();
    }

    /** Flag indicating that the last invocation of {@link #play} was interrupted. */
    private boolean interrupted;
    /** The graph transition system explored by the strategy. */
    private GTS gts;
    /**
     * Start state for exploration, set in the constructor.
     * If {@code null}, the GTS start state is selected at exploration time.
     */
    private GraphState startState;
    /** The state that will be explored by the next call of {@link #next()}. */
    private GraphState atState;
    /** The state that will be explored by the next call of {@link #next()}. */
    private GraphState lastState;
}
