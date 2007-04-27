/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
 * $Id: AbstractGraphTransitionStub.java,v 1.3 2007-04-27 12:50:12 iovka Exp $
 */
package groove.lts;

import groove.graph.Element;
import groove.graph.NodeEdgeMap;
import groove.trans.RuleEvent;

/**
 * Abstract graph transition stub that only stores an event and a target state.
 * There are two specialisations: one that is based on an identity morphism
 * ({@link SymmetryTransitionStub}) and one that is not ({@link SymmetryTransitionStub}).
 * The only abstract method is {@link #toTransition(GraphState)}.
 * @author Arend Rensink
 * @version $Revision: 1.3 $
 */
abstract class AbstractGraphTransitionStub implements GraphTransitionStub {
    /**
     * Constructs a stub on the basis of a given rule event and target state.
     */
    AbstractGraphTransitionStub(RuleEvent event, GraphState target) {
    	this.event = event;
        this.target = target;
    }

    public GraphState target() {
        return target;
    }

    /** The event wrapped by sthis stub. */
	public final RuleEvent getEvent() {
		return event;
	}
    public RuleEvent getEvent(GraphState source) {
		return getEvent();
	}

    /**
     * This implementation compares events for identity.
    */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else {
            return obj instanceof AbstractGraphTransitionStub && equalsStub((AbstractGraphTransitionStub) obj);
        }
    }

	public GraphTransition toTransition(GraphState source) {
        return new DefaultGraphTransition(getEvent(), source, target(), isSymmetry());
    }

    /**
     * Compares the events of this and the other transition.
     * Callback method from {@link #equals(Object)}.
     */
    protected boolean equalsStub(AbstractGraphTransitionStub other) {
        return target() == other.target() && getEvent() == other.getEvent();
    }

    /**
     * This method is only there because we needed to make {@link groove.lts.GraphTransitionStub}
     * a sub-interface of {@link Element}.
     * The method throws an {@link UnsupportedOperationException} always.
     */
    public int compareTo(Element obj) {
		throw new UnsupportedOperationException();
	}

    /**
     * This method is only there because we needed to make {@link groove.lts.GraphTransitionStub}
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
        return System.identityHashCode(getEvent()) + System.identityHashCode(target());
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