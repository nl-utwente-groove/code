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
 * $Id: LTS.java,v 1.6 2008-01-30 09:32:20 iovka Exp $
 */
package groove.lts;

import java.util.Collection;
import java.util.Set;

/**
 * Interface of a labelled transition system, as a graph where the nodes are
 * <tt>State</tt>s and the edges are <tt>Transition</tt>s.
 * The LTS is assumed to have a fixed associated rule production system.
 * Extends graph.Graph with a start (i.e., initial) initial state.
 * @version $Revision: 1.6 $ $Date: 2008-01-30 09:32:20 $
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
    
//    /**
//     * Sets the state exploration strategy.
//     * @param strategy the new state exploration strategy
//     * @deprecated decouple the strategy from the LTS
//     */
//    @Deprecated
//    void setExploreStrategy(ExploreStrategy strategy);
//
//    /**
//     * Returns the current state exploratin strategy.
//     * @return the current state exploration strategy
//     * @deprecated decouple the strategy from the LTS
//     */
//    @Deprecated
//    ExploreStrategy getExploreStrategy();
//    
    /**
     * Indicates whether we have found a final state during explodation.
     * Convenience method for <tt>! getFinalStates().isEmpty()</tt>.
     */
    public boolean hasFinalStates();
    
    /**
     * Returns the set of final states explored so far.
     */
    Collection<? extends State> getFinalStates();
    
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

//    /**
//     * Explores this LTS from a given state, using the LTS's state space exploration strategy.
//     * The exploration is halted when a previously generated state is reached,
//     * or if the thread is interrupted.
//     * @param state the state where the extension is to take place
//     * @throws InterruptedException if the thread receives an interrupt
//     * during the generation process.
//     * @deprecated Set an exploration strategy instead
//     */
//    @Deprecated
//    void explore(State state) throws InterruptedException;
//
//    /**
//     * Explores this LTS from its start state, using the LTS's state space exploration strategy.
//     * The exploration is halted when a previously generated state is reached,
//     * or if the thread is interrupted.
//     * Convenience method for <tt>explore(startState())</tt>
//     * @throws InterruptedException if the thread receives an interrupt
//     * during the generation process.
//     * @see #explore(State)
//     * @deprecated Set an exploration strategy instead
//     */
//    @Deprecated
//    void explore() throws InterruptedException;
//    
    /**
     * The return type is specialised so that the method is known to return {@link State}s.
     */
    public Set<? extends State> nodeSet();
    
    /**
     * The return type is specialised so that the method is known to return {@link Transition}s.
     */
    public Set<? extends Transition> edgeSet();
}