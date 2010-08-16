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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Represents a state in a control automaton. Typically a member of a
 * ControlShape. Can be viewed (as member of a GraphShape) in a viewer (Node
 * interface) Supplies methods for getting allowed outgoing transitions
 * (Location interface) (for exploration).
 * @author Tom Staijen
 * @version $Revision $
 */
public class CtrlState implements Node {
    /**
     * Creates a control state.
     */
    public CtrlState() {
        this.stateNumber = Counter.inc();
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

    /** Returns the outgoing control transitions of this control state. */
    public Set<CtrlTransition> getTransitions() {
        return this.outTransitions;
    }

    /** Set of outgoing transitions. */
    private final Set<CtrlTransition> outTransitions =
        new HashSet<CtrlTransition>();

    /**
     * Returns the set of bound variables in this state.
     */
    public Collection<String> getBoundVars() {
        return this.boundVars;
    }

    /**
     * Sets the bound variables of this state to the elements of a given collection.
     */
    public void setBoundVars(Collection<String> variables) {
        this.boundVars = new LinkedHashSet<String>(variables);
    }

    private Collection<String> boundVars = new ArrayList<String>();
}
