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
import groove.explore.util.RuleEventApplier;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.trans.GraphGrammar;
import groove.view.FormatException;

/**
 * A strategy defines an order in which the states of a graph transition system
 * are to be explored. It can also determine which states are to be explored
 * either according to some condition (see {@link ConditionalStrategy}), or
 * because of the nature of the strategy (see for instance
 * {@link LinearStrategy}). Most often, a strategy starts its exploration at
 * some state, fixed by the {@link #prepare(GTS, GraphState)} method.
 * 
 * A strategy adds states and transitions to a graph transition system. However,
 * it should use a {@link RuleEventApplier} and not manipulate the graph
 * transition system directly.
 */
public interface Strategy {
    /**
     * Checks the strategy for compatibility with a given grammar. 
     * This is a callback method that is invoked after the strategy has been 
     * instantiated, but before it is applied. 
     * If the method returns normally, the grammar is compatible
     * @throws FormatException if a compatibility error is found
     */
    public void checkCompatible(GraphGrammar grammar) throws FormatException;

    /**
     * Sets the GTS to be explored. Also sets the exploration start state to the
     * GTS start state. Convenience method for
     * <code>prepare(gts, null)</code>.
     * @see #prepare(GTS, GraphState)
     */
    public void prepare(GTS gts);

    /**
     * Sets the GTS and start state to be explored. This is done in preparation
     * to a call of {@link #play()}.
     * It is assumed that the state (if not {@code null} is already in the GTS.
     * @param gts the GTS to be explored
     * @param state the start state for the exploration; if <code>null</code>,
     * the GTS start state is used
     */
    public void prepare(GTS gts, GraphState state);

    /** 
     * Plays out this strategy, until a halting condition kicks in, 
     * the thread is interrupted or exploration is done.
     * @param halter halting condition invoked after each state exploration;
     * ignored if {@code null} 
     */
    public void play(Halter halter);

    /** Plays out this strategy, until the thread is interrupted or exploration is done. */
    public void play();

    /** Signals if the last invocation of {@link #play} finished because the thread was interrupted. */
    public boolean isInterrupted();

    /** Returns the last state explored by the last invocation of {@link #play}. 
     */
    public GraphState getLastState();

    /**
     * Adds an acceptor to the strategy.
     */
    public void addGTSListener(Acceptor listener);

    /**
     * Removes an acceptor from the strategy.
     */
    public void removeGTSListener(Acceptor listener);

    /** Interface for a halting condition on exploration. */
    public interface Halter {
        /** Callback method to determine whether exploration should halt. */
        public boolean halt();
    }
}
