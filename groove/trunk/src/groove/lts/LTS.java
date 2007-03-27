// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente
 
// Licensed under the Apache License, Version 2.0 (the "License"); 
// you may not use this file except in compliance with the License. 
// You may obtain a copy of the License at 
// http://www.apache.org/licenses/LICENSE-2.0 
 
// Unless required by applicable law or agreed to in writing, 
// software distributed under the License is distributed on an 
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
// either express or implied. See the License for the specific 
// language governing permissions and limitations under the License.
/* 
 * $Id: LTS.java,v 1.2 2007-03-27 14:18:38 rensink Exp $
 */
package groove.lts;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * Interface of a labelled transition system, as a graph where the nodes are
 * <tt>State</tt>s and the edges are <tt>Transition</tt>s.
 * The LTS is assumed to have a fixed associated rule production system.
 * Extends graph.Graph with a start (i.e., initial) initial state.
 * @version $Revision: 1.2 $ $Date: 2007-03-27 14:18:38 $
 */
public interface LTS extends groove.graph.GraphShape {
    /** The text of the self-edge label that indicates a start state. */
    public static final String START_LABEL_TEXT = "start";
    /** The text of the self-edge label that indicates an open state. */
    public static final String OPEN_LABEL_TEXT = "open";
    /** The text of the self-edge label that indicates a final state. */
    public static final String FINAL_LABEL_TEXT = "final";
    
    /**
     * Returns the start state of this LTS.
     * @return the start state of this LTS
     * @ensure result != null
     */
    public State startState();

    /**
     * Returns the set of states directly reachable from a given state.
     * Closes the state first, if this had not yet been done.
     * @param state the state from which the next ones are to be determined
     * @return the target states of the transitions from this state
     * @require <tt>containsNode(state)</tt>
     * @ensure <tt>isClosed(state)</tt> and <tt>result.contains(target)</tt> iff
     * <tt>outEdges.contains(trans)</tt> such that <tt>target.equals(trans.target())</tt>
     * @see #nextStateIter(State)
     */
    public Collection<? extends State> nextStates(State state);

    /**
     * Returns an iterator over the states directly reachable from a given state.
     * The next states may be generated "on the fly" during the construction of the 
     * resulting iterator's <tt>next()</tt> method.
     * Thus, if you are not interested in generating all next states then this method
     * may be more appropriate than <tt>nextStates(State)</tt>.
     * If the resulting iterator's <tt>next()</tt> is called until <tt>hasNext()</tt>
     * becomes <tt>false</tt>, then (afterwards) <tt>state.isClosed()</tt> and
     * the set of returned states equals <tt>nextStates(state)</tt>.
     * @param state the state from which the next ones are to be determined
     * @return an iterator over the target states of the transitions from this state
     * @require <tt>containsNode(state)</tt>
     * @ensure <tt>result.next() instanceof State</tt> and
     * <tt>outEdges.contains(trans)</tt> such that <tt>result.next().equals(trans.target())</tt>
     * @see #nextStates(State)
     */
    public Iterator<? extends State> nextStateIter(State state);
    
    /**
     * Sets the state exploration strategy.
     * @param strategy the new state exploration strategy
     * @deprecated decouple the strategy from the LTS
     */
    @Deprecated
    void setExploreStrategy(ExploreStrategy strategy);

    /**
     * Returns the current state exploratin strategy.
     * @return the current state exploration strategy
     * @deprecated decouple the strategy from the LTS
     */
    @Deprecated
    ExploreStrategy getExploreStrategy();
    
    /**
     * Indicates whether we have found a final state during explodation.
     * Convenience method for <tt>! getFinalStates().isEmpty()</tt>.
     */
    public boolean hasFinalStates();
    
    /**
     * Returns the set of final states explored so far.
     */
    Collection<? extends GraphState> getFinalStates();
    
    /**
     * Indicates whether a given state is final.
     * Equivalent to <tt>getFinalStates().contains(state)</tt>.
     */
    boolean isFinal(State state);
    
    /**
     * Indicates whether a given state is open, in the sense of not (completely) explored.
     * Equivalent to <tt>!state.isClosed()</tt>.
     */
    boolean isOpen(State state);

    // ------------------------- COMMANDS --------------------------

    /**
     * Calculates the outgoing transitions of a given state and adds them to the LTS.
     * Returns the set of newly discovered states.
     * If the state is not in the LTS, or is already generated, nothing happens.
     * If the state is truly closed, the LTS listeners are notified of this
     * (after the new nodes and edges have been added).
     * @param state the state where the extension is to take place
     * @return the list of newly discovered states
     * @see LTSListener#closeUpdate(LTS,State)
     * @ensure <tt>isGenerated(state)</tt> and
     * for all <tt>result.contans(newState)</tt>: <tt>! isGenerated(newState)</tt>
     * @deprecated use {@link StateGenerator} instead
     */
    @Deprecated
    Collection<? extends State> freshNextStates(State state);

    /**
     * Explores this LTS from a given state, using the LTS's state space exploration strategy.
     * The exploration is halted when a previously generated state is reached,
     * or if the thread is interrupted.
     * @param state the state where the extension is to take place
     * @see #freshNextStates(State)
     * @throws InterruptedException if the thread receives an interrupt
     * during the generation process.
     * @deprecated Set an exploration strategy instead
     */
    @Deprecated
    void explore(State state) throws InterruptedException;

    /**
     * Explores this LTS from its start state, using the LTS's state space exploration strategy.
     * The exploration is halted when a previously generated state is reached,
     * or if the thread is interrupted.
     * Convenience method for <tt>explore(startState())</tt>
     * @throws InterruptedException if the thread receives an interrupt
     * during the generation process.
     * @see #explore(State)
     * @deprecated Set an exploration strategy instead
     */
    @Deprecated
    void explore() throws InterruptedException;
    
    /**
     * The return type is specialised so that the method is known to return {@link State}s.
     */
    public Set<? extends State> nodeSet();
    
    /**
     * The return type is specialised so that the method is known to return {@link Transition}s.
     */
    public Set<? extends Transition> edgeSet();
}