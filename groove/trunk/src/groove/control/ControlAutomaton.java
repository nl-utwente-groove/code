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
 * $Id: ControlAutomaton.java,v 1.10 2008-01-30 11:13:57 fladder Exp $
 */
package groove.control;

import groove.graph.AbstractGraphShape;
import groove.graph.Edge;
import groove.graph.GraphCache;
import groove.graph.Node;

import java.util.HashSet;
import java.util.Set;

/**
 * Representation of a Control automaton that can be visualised in a
 * JGraphPanel. 
 * 
 * @author Tom Staijen
 */
public class ControlAutomaton extends AbstractGraphShape<GraphCache> implements
        ControlShape {
    /** the top-level ControlShape * */

    private Set<ControlState> states = new HashSet<ControlState>();
    private Set<ControlTransition> transitions = new HashSet<ControlTransition>();
    private ControlState startState;

    public Set<? extends Edge> edgeSet() {
        return transitions();
    }

    public Set<? extends Node> nodeSet() {
        return states();
    }

    public void addState(ControlState state) {
        this.states.add(state);
    }

    public void addTransition(ControlTransition ct) {
        this.transitions.add(ct);

    }

    public ControlState getStart() {
        return this.startState;
    }

    public void removeState(ControlState state) {
        this.states.remove(state);
    }

    public void removeTransition(ControlTransition ct) {
        this.transitions.remove(ct);
    }

    public void setStart(ControlState start) {
        this.startState = start;
    }

    public Set<ControlState> states() {
        return this.states;
    }

    public Set<ControlTransition> transitions() {
        return this.transitions;
    }

    /**
     * Returns true if the given state is a success-state.
     * @param state
     * @return boolean
     */
    public boolean isSuccess(ControlState state) {
        return state.isSuccess();
    }
}
