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
package groove.lts;

import java.util.List;
import java.util.Set;

import groove.control.instance.Frame;
import groove.grammar.CheckPolicy;
import groove.grammar.host.HostGraph;
import groove.graph.Node;
import groove.lts.Status.Flag;

/**
 * Combination of graph and node functionality, used to store the state of a
 * graph transition system.
 * States store the outgoing rule transitions, but not the recipe transitions:
 * these can be calculated by the GTS.
 * Every graph state has a <i>status</i>, consisting of the following boolean
 * flags:
 * <ul>
 * <li> <b>Closed:</b> A graph state is closed if all rule applications have been explored.
 * <li> <b>Cooked:</b> A graph state is done if it is closed and all reachable states up
 * until the first non-transient states are also closed. This means that all outgoing
 * transitions (including recipe transitions) are known.
 * <li> <b>Transient:</b> A graph state is transient if it is an intermediate state in
 * the execution of a recipe.
 * <li> <b>Absent:</b> A graph state is absent if it is done and transient and does not
 * have a path to a non-transient state, or violates a right
 * application condition.
 * <li> <b>Error:</b> A graph state is erroneous if it fails to satisfy an invariant
 * </ul>
 * A derived concept is:
 * <ul>
 * <li> <b>Present:</b> A graph state is (definitely) present if it is done and not absent.
 * (Note that this is <i>not</i> strictly the inverse of being absent: a raw state
 * is neither present nor absent.
 * </ul>
 * @author Arend Rensink
 * @version $Revision$ $Date: 2008-02-22 13:02:44 $
 */
public interface GraphState extends Node {
    /** Returns the Graph Transition System of which this is a state. */
    public GTS getGTS();

    /** Returns the graph contained in this state. */
    public HostGraph getGraph();

    /**
     * Sets a new actual frame for this state.
     * This also initialises the prime frame, if that has not been done yet.
     * If the prime frame has been initialised, it should equal the prime of
     * the new actual frame.
     */
    public void setFrame(Frame frame);

    /**
     * Returns the prime control frame associated with this state.
     * The prime frame is the frame with which the state is initialised;
     * it is fixed for the lifetime of the state.
     * This is in contrast with the actual frame, which may change as
     * the state is explored.
     */
    public Frame getPrimeFrame();

    /**
     * Returns the actual control frame associated with this state.
     * The actual control frame evolves as the state is explored, whereas the
     * prime frame is fixed at creation time.
     * The prime frame is always the prime of the actual frame.
     * @see Frame#getPrime()
     */
    public Frame getActualFrame();

    /**
     * Returns a stack of values for the bound variables of
     * the prime control frame.
     * @see #getPrimeFrame()
     * @see Frame#getVars()
     */
    public Object[] getPrimeValues();

    /**
     * Returns a stack of values for the bound variables of
     * the actual control frame.
     * @see #getPrimeFrame()
     * @see Frame#getVars()
     */
    public Object[] getActualValues();

    /**
     * Retrieves an outgoing transition with a given match, if it exists. Yields
     * <code>null</code> otherwise.
     */
    public RuleTransitionStub getOutStub(MatchResult match);

    /**
     * Returns the set of currently generated outgoing
     * complete transitions starting in this state.
     * Convenience method for {@code getTransitions(COMPLETE)}.
     * @see #getTransitions(GraphTransition.Claz)
     */
    public default Set<? extends GraphTransition> getTransitions() {
        return getTransitions(GraphTransition.Claz.REAL);
    }

    /**
     * Returns the set of currently generated outgoing
     * rule transitions starting in this state.
     * Convenience method for {@code getTransitions(RULE)}.
     * @see #getTransitions(GraphTransition.Claz)
     */
    @SuppressWarnings("unchecked")
    public default Set<RuleTransition> getRuleTransitions() {
        return (Set<RuleTransition>) getTransitions(GraphTransition.Claz.RULE);
    }

    /**
     * Returns the set of currently generated outgoing
     * transitions of a certain class starting in this state.
     * @param claz class of graph transformations to be returned
     */
    public Set<? extends GraphTransition> getTransitions(GraphTransition.Claz claz);

    /**
     * Adds an outgoing transition to this state, if it is not yet there.
     * @return <code>true</code> if the transition was added,
     *         <code>false</code> otherwise
     */
    public boolean addTransition(GraphTransition transition);

    /**
     * Returns the first unexplored match found for this state, insofar one can
     * currently be computed.
     */
    public MatchResult getMatch();

    /**
     * Returns the set of all unexplored matches for this state, insofar they can
     * currently be computed.
     */
    public List<MatchResult> getMatches();

    /**
     * Applies a rule match to this state.
     * If the match is an outgoing rule transition of this state, nothing happens.
     * @param match the match to be applied
     * @return the added transition (or the match itself if that is an outgoing
     * transition); non-{@code null}
     * @throws InterruptedException if an oracle input was cancelled
     */
    public RuleTransition applyMatch(MatchResult match) throws InterruptedException;

    /**
     * Returns the current state cache, or a fresh one if the cache is cleared.
     */
    public StateCache getCache();

    /**
     * Closes this state. This announces that no more outgoing transitions will
     * be generated. The return value indicates if the state was already closed.
     * @ensure <tt>isClosed()</tt>
     * @param finished indicates that all transitions for this state have been added.
     * This might fail to be the case in an incomplete exploration strategy; e.g.,
     * linear exploration.
     * @return <code>true</code> if the state was closed as a result of this
     *         call; <code>false</code> if it was already closed
     * @see #isClosed()
     */
    public boolean setClosed(boolean finished);

    /**
     * Tests if this state is fully explored, i.e., all outgoing transitions
     * have been generated.
     */
    public default boolean isClosed() {
        return hasFlag(Flag.CLOSED);
    }

    /**
     * Declares this state to be an error state.
     * The return value indicates if the error status was changed as
     * a result of this call.
     * @return if {@code false}, the state was already known to be an error state
     */
    public boolean setError();

    /** Indicates if this is an error state.
     * This corresponds to having the {@link Flag#ERROR} flag.
     */
    public default boolean isError() {
        return hasFlag(Flag.ERROR);
    }

    /**
     * Declares this state to be done, while also setting its absence.
     * @param absence level of the state; if positive, the state is absent
     * @return if {@code false}, the state was already known to be done
     * @see Flag#DONE
     */
    public boolean setDone(int absence);

    /**
     * Indicates if this state is done.
     * This is the case if
     * all outgoing paths have been explored up until a non-transient
     * or deadlocked state.
     * @see Flag#DONE
     */
    public default boolean isDone() {
        return hasFlag(Flag.DONE);
    }

    /**
     * Indicates if this state is final.
     * This is the case if and only if the state is done and the actual control frame is final.
     * @see Flag#FINAL
     */
    public default boolean isFinal() {
        return hasFlag(Flag.FINAL);
    }

    /** Indicates if this state is inside a recipe.
     * This is the case if and only if the recipe has started
     * and not yet terminated.
     * A state can only be inside a recipe if it is transient.
     * @see #isTransient()
     * @see Flag#INTERNAL
     */
    public default boolean isInternalState() {
        return hasFlag(Flag.INTERNAL);
    }

    /**
     * Indicates if this state is a real part of the GTS.
     * This is the case if and only if the state is not internal or absent.
     * @see Status#isReal(int)
     */
    public default boolean isRealState() {
        return Status.isReal(getStatus());
    }

    /**
     * Indicates if this is a transient state, i.e., it is inside an atomic block.
     * This is the case if and only if the associated control frame is transient.
     * @see #getActualFrame()
     */
    public default boolean isTransient() {
        return hasFlag(Flag.TRANSIENT);
    }

    /**
     * Indicates if this state is known to be not properly part of the state
     * space. This is the case if the state is done and has a positive absence
     * level, or if it is absent because of the violation of some constraint
     * (in combination with a {@link CheckPolicy#REMOVE} policy).
     * @see #isDone()
     * @see #getAbsence()
     */
    public default boolean isAbsent() {
        return hasFlag(Flag.ABSENT);
    }

    /**
     * Indicates the absence level, which is defined as the lowest
     * transient depth of the known reachable states.
     * This is maximal ({@link Status#MAX_ABSENCE}) if the state is
     * erroneous, and 0 if the state is non-transient.
     * A state that is done and has a positive absence level is absent.
     * @see #isDone()
     * @see #isAbsent()
     */
    public int getAbsence();

    /**
     * Indicates if this state is properly part of the state space.
     * Convenience method for <code>getAbsence() == 0 && !isAbsent()</code>.
     * If a state is done, it is either present or absent.
     * @see #isDone()
     * @see #isAbsent()
     * @see #getAbsence()
     */
    public default boolean isPresent() {
        return getAbsence() == 0 && !isAbsent();
    }

    /** Returns the integer representation of the status of this state. */
    public int getStatus();

    /** Tests if a given status flag is set. */
    public default boolean hasFlag(Flag flag) {
        return flag.test(getStatus());
    }

    /**
     * Changes the value of a given status flag.
     * This is only allowed for exploration strategy-related flags;
     * for others, (re)setting is done internally.
     * @param flag the flag to be changed
     * @param value new value for the flag
     * @return if {@code true}, the value of the flag was changed as a result of this call
     * @see #hasFlag(Flag)
     * @see Flag#isStrategy()
     */
    public boolean setFlag(Flag flag, boolean value);
}
