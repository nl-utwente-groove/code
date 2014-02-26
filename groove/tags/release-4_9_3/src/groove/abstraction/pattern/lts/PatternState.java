/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an 
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific 
 * language governing permissions and limitations under the License.
 *
 * $Id$
 */
package groove.abstraction.pattern.lts;

import groove.abstraction.pattern.shape.PatternGraph;
import groove.abstraction.pattern.shape.PatternShape;
import groove.control.CtrlFrame;
import groove.graph.Node;
import groove.lts.GraphState;

import java.util.Set;

/**
 * Common interface of states that may occur in a PGTS or PSTS.
 * 
 * See {@link GraphState}. 
 */
public interface PatternState extends Node {

    /** Returns the transition system of which this is a state. */
    public PGTS getPGTS();

    /** Returns the graph contained in this state. */
    public PatternGraph getGraph();

    /** Returns the (non-{@code null}) control state associated with this state. */
    public CtrlFrame getFrame();

    /** Returns the (non-{@code null}) control schedule associated with this state. */
    public CtrlFrame getCurrentFrame();

    /** 
     * Sets or changes the control frame.
     */
    public void setFrame(CtrlFrame frame);

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
     * Adds an outgoing transition to this state, if it is not yet there.
     * @return <code>true</code> if the transition was added,
     *         <code>false</code> otherwise
     */
    public boolean addTransition(PatternTransition transition);

    /**
     * Returns an unmodifiable set view on the currently generated outgoing
     * transitions starting in this state.
     */
    public Set<PatternTransition> getTransitionSet();

    // ------------------------------------------------------------------------
    // Abstraction methods (to be used only with a PSTS)
    // ------------------------------------------------------------------------

    // EZ says: there are a few assertions in place to ensure that these
    // methods are only called when appropriate but thread carefully nonetheless.
    // I know this is bad design, but it saves on having lots of subclasses.
    // Also, while it's nice to have a PGTS so we can check that pattern graph
    // transformations behave like normal simple graph ones, we are actually
    // interested in PSTSs only.

    /** Returns true if the graph stored in this state is a pattern shape. */
    public boolean hasPatternShape();

    /** Returns the pattern shape associated with this state. */
    public PatternShape getShape();

    /** Returns true if this state is subsumed by another. */
    public boolean isSubsumed();

    /**
     * Adds the given state to the list of states possibly subsumed by this
     * one.
     */
    public void addSubsumedState(PatternState subsumed);

    /**
     * Tries to set the subsumptor to the given state.
     * Returns true is this state didn't already have a subsumptor.
     */
    public boolean setSubsumptor(PatternState subsumptor);

    /**
     * Goes over the list of possible subsumed states and mark them as such,
     * trying to set this is state as their subsumptor. The list of possible
     * subsumed states of this state is destroyed during this method call.
     * Returns the number of states that were marked as subsumed.
     */
    public int markSubsumedStates();

}
