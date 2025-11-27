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
package nl.utwente.groove.lts;

import java.util.Collection;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.grammar.CheckPolicy;
import nl.utwente.groove.graph.Node;
import nl.utwente.groove.lts.Status.Flag;
import nl.utwente.groove.transform.Phase;

/**
 * Combination of graph and node functionality, used to store the state of a
 * graph transition system.
 * States store the outgoing rule transitions, but not the recipe transitions:
 * these can be calculated by the GTS.
 * Every graph state has a <i>status</i>, consisting of the following boolean
 * flags:
 * <ul>
 * <li> <b>Closed:</b> A graph state is closed if all rule applications have been explored.
 * <li> <b>Complete:</b> A graph state is complete if it is closed and all reachable states up
 * until the first non-transient states are also closed. This means that all outgoing
 * transitions (including recipe transitions) are known.
 * <li> <b>Transient:</b> A graph state is transient if it is an intermediate state in
 * the execution of an atomic block.
 * <li> <b>Absent:</b> A graph state is absent if it is complete and transient and does not
 * have a path to a non-transient state, or violates a right
 * application condition.
 * <li> <b>Error:</b> A graph state is erroneous if it fails to satisfy an invariant
 * </ul>
 * A derived concept is:
 * <ul>
 * <li> <b>Present:</b> A graph state is (definitely) present if it is complete and not absent.
 * (Note that this is <i>not</i> strictly the inverse of being absent: an incomplete state
 * is neither present nor absent.
 * </ul>
 * @author Arend Rensink
 * @version $Revision$ $Date: 2008-02-22 13:02:44 $
 */
@NonNullByDefault
public interface GraphState extends Node, Phase {
    /** Returns the Graph Transition System of which this is a state. */
    public GTS getGTS();

    /** Indicates if the underlying graph is simple. */
    default public boolean isSimple() {
        return !getGTS().hasSimpleGraphs();
    }

    /**
     * Retrieves an outgoing transition with a given match, if it exists. Yields
     * <code>null</code> otherwise.
     */
    public @Nullable RuleTransitionStub getOutStub(MatchResult match);

    /**
     * Returns the set of currently generated outgoing
     * complete transitions starting in this state.
     * Convenience method for {@code getTransitions(PUBLIC)}.
     * @see #getTransitions(GraphTransition.Claz)
     */
    public default Set<? extends GraphTransition> getTransitions() {
        return getTransitions(GraphTransition.Claz.PUBLIC);
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

    /** Returns the set of state properties satisfied by this state. */
    default public Collection<StateProperty> getSatisfiedProps() {
        return getGTS().getSatisfiedProps(this);
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
     * @return <code>true</code> if the state was closed as a result of this
     *         call; <code>false</code> if it was already closed
     * @see #isClosed()
     */
    public boolean setClosed();

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
     * Declares this state to be full, while also setting its absence level as part of the status.
     * @param absence the absence level of the state
     * @return if {@code false}, the state was already known to be full
     * @see Flag#FULL
     */
    public boolean setFull(int absence);

    /**
     * Indicates if this state is full.
     * This is the case if
     * all outgoing paths have been explored up until a non-transient
     * or deadlocked state.
     * @see Flag#FULL
     */
    public default boolean isFull() {
        return hasFlag(Flag.FULL);
    }

    /**
     * Indicates if this state is final.
     * This is the case if and only if the state is complete and the actual control frame is final.
     * @see Flag#FINAL
     */
    public default boolean isFinal() {
        return hasFlag(Flag.FINAL);
    }

    /** Indicates if this state is internal, i.e., inside a recipe.
     * This is the case if and only if the recipe has started
     * and not yet terminated.
     * A state can only be internal if it is transient.
     * @see #isTransient()
     * @see Flag#INNER
     */
    public default boolean isInner() {
        return hasFlag(Flag.INNER);
    }

    /**
     * Indicates if this state is a public part of the GTS.
     * This is the case if and only if the state is not inner or absent.
     * @see Status#isPublic(int)
     */
    public default boolean isPublic() {
        return Status.isPublic(getStatus());
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
     * space. This is the case if the state is complete and has a positive absence
     * level, or if it is absent because of the violation of some constraint
     * (in combination with a {@link CheckPolicy#REMOVE} policy).
     * @see #isFull()
     * @see #getAbsence()
     */
    public default boolean isAbsent() {
        return Status.isAbsent(getStatus());
    }

    /**
     * Indicates the absence level, which is defined as the lowest
     * transient depth of the known reachable states.
     * This is maximal ({@link Status#MAX_ABSENCE}) if the state is
     * erroneous, and 0 if the state is non-transient.
     * A state that is complete and has a positive absence level is absent.
     * @see #isFull()
     * @see #isAbsent()
     */
    public default int getAbsence() {
        return isFull()
            ? Status.getAbsence(getStatus())
            : getCache().getAbsence();
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
     * for others, (re)setting occurs internally.
     * @param flag the flag to be changed
     * @param value new value for the flag
     * @return if {@code true}, the value of the flag was changed as a result of this call
     * @see #hasFlag(Flag)
     * @see Flag#isStrategy()
     */
    public boolean setFlag(Flag flag, boolean value);
}
