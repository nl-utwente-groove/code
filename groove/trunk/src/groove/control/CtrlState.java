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
 * $Id: ControlState.java,v 1.10 2008-01-30 12:37:39 fladder Exp $
 */
package groove.control;

import groove.control.parse.Counter;
import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.Node;
import groove.view.FormatException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * A state in a control automaton.
 * The state stores a set of bound variables, and the
 * set of its outgoing (control) transitions.
 * @author Arend Rensink
 * @version $Revision $
 */
public class CtrlState implements Node {
    /**
     * Creates a control state with a fresh number.
     */
    public CtrlState() {
        this(Counter.inc());
    }

    /**
     * Creates a control state with a given number.
     */
    public CtrlState(int nr) {
        this.stateNumber = nr;
    }

    public int compareTo(Element obj) {
        if (obj instanceof Node) {
            return getNumber() - ((Node) obj).getNumber();
        } else {
            return getNumber() - ((Edge) obj).source().getNumber();
        }
    }

    public int getNumber() {
        return this.stateNumber;
    }

    /** Internal number to identify the state. */
    private final int stateNumber;

    @Override
    public String toString() {
        return "q" + this.stateNumber;
    }

    /**
     * Add an outgoing transition to this control state.
     */
    public boolean addTransition(CtrlTransition transition) {
        return this.outTransitions.add(transition);
    }

    /**
     * Removes an outgoing transition from this control state.
     */
    public boolean removeTransition(CtrlTransition transition) {
        return this.outTransitions.remove(transition);
    }

    /** Returns the outgoing control transitions of this control state. */
    public Set<CtrlTransition> getTransitions() {
        return this.outTransitions;
    }

    /** 
     * Returns the set of rule names of outgoing transitions, or
     * {@code null} if there is an outgoing omega transition. 
     */
    public Set<CtrlCall> getInit() {
        Set<CtrlCall> result = new HashSet<CtrlCall>();
        for (CtrlTransition trans : getTransitions()) {
            CtrlCall call = trans.label().getCall();
            if (call.isOmega()) {
                result = null;
                break;
            } else {
                result.add(call);
            }
        }
        return result;
    }

    /** Set of outgoing transitions. */
    private final Set<CtrlTransition> outTransitions =
        new HashSet<CtrlTransition>();

    /**
     * Returns the set of bound variables in this state.
     */
    public Collection<CtrlVar> getBoundVars() {
        return this.boundVars;
    }

    /**
     * Sets the bound variables of this state to the elements of a given collection.
     */
    public void setBoundVars(Collection<CtrlVar> variables) {
        this.boundVars = new LinkedHashSet<CtrlVar>(variables);
    }

    /** The collection of bound variables of this control state. */
    private Collection<CtrlVar> boundVars = new ArrayList<CtrlVar>();

    /**
     * Returns an instantiation of this (virtual) control state using a given 
     * rule system.
     * The instantiated state has as yet no outgoing transitions.
     * The instantiation is recorded in a state map passed as a parameter.
     * @param stateMap mapping from virtual to instantiated states
     * @return the image of this state in {@code stateMap}, or a fresh 
     * instantiated control state if there is no image
     * @throws FormatException if the rule or one of the failures do not 
     * exist in the given rule system
     */
    public CtrlState instantiate(Map<CtrlState,CtrlState> stateMap)
        throws FormatException {
        CtrlState result = stateMap.get(this);
        if (result == null) {
            result = new CtrlState(getNumber());
            stateMap.put(this, result);
            result.setBoundVars(getBoundVars());
        }
        return result;
    }
}
