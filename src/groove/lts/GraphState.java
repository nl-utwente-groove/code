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
import groove.graph.Node;
import groove.trans.HostGraph;
import groove.trans.HostNode;
import groove.trans.RuleEvent;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
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
 * <li> <b>Cooked:</b> A graph state is cooked if it is closed and all reachable states up
 * until the first non-transient states are also closed. This means that all outgoing
 * transitions (including recipe transitions) are known.
 * <li> <b>Transient:</b> A graph state is transient if it is an intermediate state in 
 * the execution of a recipe.
 * <li> <b>Absent:</b> A graph state is absent if it is cooked and transient and does not 
 * have a path to a non-transient state, or violates a right 
 * application condition.
 * <li> <b>Error:</b> A graph state is erroneous if it fails to satisfy an invariant
 * </ul>
 * A derived concept is:
 * <ul>
 * <li> <b>Present:</b> A graph state is (definitely) present if it is cooked and not absent.
 * (Note that this is <i>not</i> strictly the inverse of being absent: an uncooked state
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
     * Retrieves an outgoing transition with a given event, if it exists. Yields
     * <code>null</code> otherwise.
     */
    public RuleTransitionStub getOutStub(RuleEvent prime);

    /**
     * Returns an iterator over the current set of outgoing transitions starting
     * in this state, as {@link RuleTransition}s.
     */
    public Iterator<RuleTransition> getTransitionIter();

    /**
     * Returns an unmodifiable set view on the currently generated outgoing
     * transitions starting in this state.
     */
    public Set<RuleTransition> getTransitionSet();

    /**
     * Returns an unmodifiable map from rules to the 
     * currently generated outgoing transitions.
     */
    public Map<RuleEvent,RuleTransition> getTransitionMap();

    /**
     * Returns (a copy of) the set of next states reachable from this state,
     * according to the currently generated outgoing transitions.
     */
    public Collection<? extends GraphState> getNextStateSet();

    /**
     * Adds an outgoing transition to this state, if it is not yet there.
     * @return <code>true</code> if the transition was added,
     *         <code>false</code> otherwise
     */
    public boolean addTransition(RuleTransition transition);

    /**
     * Tests if a certain transition is among the currently generated outgoing
     * transitions of this state.
     */
    public boolean containsTransition(RuleTransition transition);

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
     * Declares this state to be cooked.
     * @return if {@code false}, the state was already known to be cooked
     * @see #isCooked()
     */
    public boolean setCooked();

    /** 
     * Indicates if this state is cooked. 
     * This is the case if
     * all outgoing paths have been explored up until the first non-transient state.
     * @see #isTransient()
     */
    public boolean isCooked();

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
     * If a state is cooked, it is either present or absent.
     * @see #isCooked()
     * @see #isAbsent() 
     */
    public boolean isPresent();

    /** Changeable status flags of a graph state. */
    public enum Flag {
        /** 
         * Flag indicating that the state has been closed.
         * This is the case if and only if no more outgoing transitions will be added.
         */
        CLOSED,
        /**
         * Flag indicating that the graph state has been cooked. 
         * This is the case if and only if it is closed, and all outgoing transition
         * sequences eventually lead to non-transient or absent states.
         */
        COOKED,
        /**
         * Flag indicating that the graph state is absent.
         * This is the case if it is closed and transient and has
         * no reachable non-transient state, or if it violates a right
         * application condition.
         */
        ABSENT,
        /** Flag indicating that the state has an error. */
        ERROR;

        private Flag() {
            this.mask = 1 << ordinal();
        }

        /** Returns the mask corresponding to this flag. */
        public int mask() {
            return this.mask;
        }

        /** Sets this flag in a given integer value. */
        public int set(int status) {
            return status | this.mask;
        }

        /** Tests if this flag is set in a given integer value. */
        public boolean test(int status) {
            return (status & this.mask) != 0;
        }

        private final int mask;
    }
}
