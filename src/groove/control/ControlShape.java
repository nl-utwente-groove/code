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
 * $Id: ControlShape.java,v 1.3 2008-01-30 09:33:24 iovka Exp $
 */
package groove.control;

import java.util.Set;

/**
 * The ControlShape class is an ControlAutomaton element that can hold a
 * sub-automaton but also represents a ControlTransition. Typically used for
 * transactions or procedures. If the ControlShape is active in a
 * ControlAutomaton, all states and transitions of the ControlShape become nodes
 * and edges of the ControlAutomaton. The ControlShape itself will not be
 * visible as an edge.
 * @author Tom Staijen
 */
public interface ControlShape {

    /**
     * Adds a ControlState to this shape
     * @param state
     */
    public void addState(ControlState state);

    /**
     * Removes the state from this ControlShape
     * @param state
     */
    public void removeState(ControlState state);

    /**
     * Adds a ControlTransitions to this ControlShape
     * @param ct
     */
    public void addTransition(ControlTransition ct);

    /**
     * Removes a transition;
     * @param ct
     */
    public void removeTransition(ControlTransition ct);
    /**
     * Returns all ControlTransitions owned by this ControlShape.
     * @return Set<ControlTransition>
     */
    public Set<ControlTransition> transitions();

    /**
     * Returns all ControlStates owned by this ControlShape.
     * @return Set<ControlState>
     */
    public Set<ControlState> states();

    /**
     * Sets the start-state of this ControlShape. Maybe be <i>null</i>. Should
     * not be null if parent is null. In other words, the top-level shape should
     * have a start, otherwise the whole structure has no start.
     * 
     * @param start
     */
    public void setStart(ControlState start);
    
    /**
     * Returns the State state (if there is one). Typically, only the top-level
     * shape has a start state.
     * 
     * @return ControlState
     */
    public ControlState getStart();

}
