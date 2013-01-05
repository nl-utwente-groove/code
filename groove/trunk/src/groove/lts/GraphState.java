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
 * $Id: GraphState.java,v 1.11 2008-02-22 13:02:44 rensink Exp $
 */
package groove.lts;

import groove.control.CtrlSchedule;
import groove.control.CtrlState;
import groove.grammar.host.HostGraph;
import groove.grammar.host.HostNode;
import groove.graph.Node;

import java.util.List;
import java.util.Set;

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

    /** Returns the (non-{@code null}) control state associated with this state. */
    public CtrlState getCtrlState();

    /** Returns the (non-{@code null}) control schedule associated with this state. */
    public CtrlSchedule getSchedule();

    /** Sets a new control schedule for this state. */
    public void setSchedule(CtrlSchedule schedule);

    /**
     * Retrieves an outgoing transition with a given match, if it exists. Yields
     * <code>null</code> otherwise.
     */
    public RuleTransitionStub getOutStub(MatchResult match);

    /**
     * Returns the set of currently generated outgoing
     * complete transitions starting in this state.
     * Convenience method for {@code getTransitions(COMPLETE)}.
     * @see #getTransitions(GraphTransition.Class)
     */
    public Set<? extends GraphTransition> getTransitions();

    /**
     * Returns the set of currently generated outgoing
     * rule transitions starting in this state.
     * Convenience method for {@code getTransitions(RULE)}.
     * @see #getTransitions(GraphTransition.Class)
     */
    public Set<RuleTransition> getRuleTransitions();

    /**
     * Returns the set of currently generated outgoing
     * transitions of a certain class starting in this state.
     * @param claz class of graph transformations to be returned
     */
    public Set<? extends GraphTransition> getTransitions(
            GraphTransition.Class claz);

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
     */
    public RuleTransition applyMatch(MatchResult match);

    /**
     * Returns a list of values for the bound variables of
     * the control state.
     * @see #getCtrlState()
     * @see CtrlState#getBoundVars()
     */
    public HostNode[] getBoundNodes();

    /** 
     * Returns the current state cache, or a fresh one if the cache is cleared.
     */
    public StateCache getCache();

    /**
     * Closes this state. This announces that no more outgoing transitions will
     * be generated. The return value indicates if the state was already closed.
     * @ensure <tt>isClosed()</tt>
     * @param finished indicates that all transitions for this state have been added.
     * @return <code>true</code> if the state was closed as a result of this
     *         call; <code>false</code> if it was already closed
     * @see #isClosed()
     */
    public boolean setClosed(boolean finished);

    /**
     * Tests if this state is fully explored, i.e., all outgoing transitions
     * have been generated.
     */
    public boolean isClosed();

    /** 
     * Declares this state to be an error state.
     * The return value indicates if the error status was changed as
     * a result of this call.
     * @return if {@code false}, the state was already known to be an error state
     */
    public boolean setError();

    /** Indicates if this is an error state. */
    public boolean isError();

    /** 
     * Declares this state to be done, while also setting its presence.
     * @param present flag indicating if the state is present
     * @return if {@code false}, the state was already known to be done
     * @see Flag#DONE
     */
    public boolean setDone(boolean present);

    /** 
     * Indicates if this state is done. 
     * This is the case if
     * all outgoing paths have been explored up until the first non-transient state.
     * @see Flag#DONE
     */
    public boolean isDone();

    /** 
     * Indicates if this is a transient state.
     * This is the case if and only if the associated control schedule is transient.
     * @see #getSchedule()
     */
    public boolean isTransient();

    /** 
     * Declares this state to be absent from the state space.
     * An state is absent if after exploration it turns out that a correctness
     * condition is violated.
     * The return value indicates if the absentee status was changed as
     * a result of this call.
     * @return if {@code false}, the state was already known to be absent
     */
    public boolean setAbsent();

    /** Indicates if this state is not properly part of the state space. */
    public boolean isAbsent();

    /** 
     * Indicates if this state is properly part of the state space.
     * If a state is done, it is either present or absent.
     * @see #isDone()
     * @see #isAbsent() 
     */
    public boolean isPresent();

    /** Tests if a given status flag is set. */
    public boolean hasFlag(Flag flag);

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

    /** Changeable status flags of a graph state. */
    public enum Flag {
        /** 
         * Flag indicating that the state has been closed.
         * This is the case if and only if no more outgoing transitions will be added.
         */
        CLOSED(false),
        /**
         * Flag indicating that exploration of the graph state is done. 
         * This is the case if and only if it is closed, and all outgoing transition
         * sequences eventually lead to non-transient or absent states.
         */
        DONE(false),
        /**
         * Flag indicating that the graph state is absent.
         * This is the case if it is closed and transient and has
         * no reachable non-transient state, or if it violates a right
         * application condition.
         */
        ABSENT(false),
        /** Flag indicating that the state has an error. */
        ERROR(false),
        /** Helper flag used during state space exploration. */
        KNOWN(true);

        private Flag(boolean strategy) {
            this.mask = 1 << ordinal();
            this.strategy = strategy;
        }

        /** Returns the mask corresponding to this flag. */
        public int mask() {
            return this.mask;
        }

        /** Sets this flag in a given integer value. */
        public int set(int status) {
            return status | this.mask;
        }

        /** Resets this flag in a given integer value. */
        public int reset(int status) {
            return status & ~this.mask;
        }

        /** Tests if this flag is set in a given integer value. */
        public boolean test(int status) {
            return (status & this.mask) != 0;
        }

        /** Indicates if this flag is exploration strategy-related. */
        public boolean isStrategy() {
            return this.strategy;
        }

        private final int mask;
        /** Indicates if this flag is exploration-related. */
        private final boolean strategy;
    }
}
