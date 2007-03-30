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
 * $Id: DefaultGraphOutTransition.java,v 1.2 2007-03-30 15:50:41 rensink Exp $
 */
package groove.lts;


import groove.graph.Element;
import groove.graph.NodeEdgeMap;
import groove.trans.Rule;
import groove.trans.RuleEvent;

/**
 * Abstract class to store the outgoing transitions locally at each state.
 * @author Arend Rensink
 * @version $Revision: 1.2 $
 */
public class DefaultGraphOutTransition implements GraphOutTransition {
    /**
     * Constructs a GraphTransition on the basis of a given production name
     * and direct derivation.
     */
    public DefaultGraphOutTransition(RuleEvent event, GraphState target) {
    	this.event = event;
        this.target = target;
    }

    public GraphState target() {
        return target;
    }

    public final RuleEvent getEvent() {
		return event;
	}

	public Rule getRule() {
		return getEvent().getRule();
	}

	public GraphTransition createTransition(GraphState source) {
        return new DefaultGraphTransition(getEvent(), source, target());
    }

    // ----------------------- OBJECT OVERRIDES -----------------------

    /**
     * Compares the events of this and the other transition.
     * Callback method from {@link #equals(Object)}.
     */
    protected boolean equalsEvent(GraphOutTransition other) {
        return getEvent() == other.getEvent();
    }

    /**
     * This implementation compares events for identity.
    */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else {
            return obj instanceof GraphOutTransition && equalsEvent((GraphOutTransition) obj);
        }
    }

    /**
     * This method is only there because we needed to make {@link groove.lts.GraphOutTransition}
     * a sub-interface of {@link Element}.
     * The method throws an {@link UnsupportedOperationException} always.
     */
    public int compareTo(Element obj) {
		throw new UnsupportedOperationException();
	}

    /**
     * This method is only there because we needed to make {@link groove.lts.GraphOutTransition}
     * a sub-interface of {@link Element}.
     * The method throws an {@link UnsupportedOperationException} always.
     */
	public Element imageFor(NodeEdgeMap elementMap) {
		throw new UnsupportedOperationException();
	}
	
    /**
	 * This implementation returns the identity of the event.
	 */
    @Override
    public int hashCode() {
        return System.identityHashCode(getEvent());
    }
	
    /**
     * The target state of this transition.
     * @invariant <tt>target != null</tt>
     */
    private final GraphState target;
    /**
     * The rule event wrapper in this out-transition
     */
    private final RuleEvent event;
}