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
 * $Id: AbstractGraphTransitionStub.java,v 1.11 2008-01-30 09:32:20 iovka Exp $
 */
package groove.lts;

import groove.graph.Element;
import groove.trans.HostNode;
import groove.trans.RuleEvent;

import java.util.Arrays;

/**
 * Abstract graph transition stub that only stores an event and a target state.
 * There are two specialisations: one that is based on an identity morphism ({@link SymmetryTransitionStub})
 * and one that is not ({@link SymmetryTransitionStub}). The only abstract
 * method is {@link #toTransition(GraphState)}.
 * @author Arend Rensink
 * @version $Revision$
 */
abstract class AbstractGraphTransitionStub implements GraphTransitionStub {
    /**
     * Constructs a stub on the basis of a given rule event, added nodes and
     * target state.
     */
    AbstractGraphTransitionStub(RuleEvent event, HostNode[] addedNodes,
            GraphState target) {
        this.event = event;
        this.addedNodes = addedNodes;
        this.target = target;
    }

    /** This implementation always returns the stored target state. */
    public GraphState getTarget(GraphState source) {
        return this.target;
    }

    /** The event wrapped by this stub. */
    public final RuleEvent getEvent() {
        return this.event;
    }

    public RuleEvent getEvent(GraphState source) {
        return getEvent();
    }

    public HostNode[] getAddedNodes(GraphState source) {
        return this.addedNodes;
    }

    /**
     * This implementation compares events for identity.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else {
            return obj instanceof AbstractGraphTransitionStub
                && equalsStub((AbstractGraphTransitionStub) obj);
        }
    }

    public GraphTransition toTransition(GraphState source) {
        return new DefaultGraphTransition(getEvent(source),
            getAddedNodes(source), source, getTarget(source), isSymmetry());
    }

    /**
     * Compares the events of this and the other transition. Callback method
     * from {@link #equals(Object)}.
     */
    protected boolean equalsStub(AbstractGraphTransitionStub other) {
        boolean result =
            this.target == other.target && getEvent() == other.getEvent()
                && isSymmetry() == other.isSymmetry();
        assert !result || Arrays.equals(this.addedNodes, other.addedNodes);
        return result;
    }

    /**
     * This method is only there because we needed to make
     * {@link groove.lts.GraphTransitionStub} a sub-interface of {@link Element}.
     * The method throws an {@link UnsupportedOperationException} always.
     */
    public int compareTo(Element obj) {
        throw new UnsupportedOperationException();
    }

    /**
     * This implementation returns the identity of the event.
     */
    @Override
    public int hashCode() {
        return getEvent().hashCode() + this.target.hashCode();
    }

    /**
     * The target state of this transition.
     * @invariant <tt>target != null</tt>
     */
    private final GraphState target;
    /**
     * The rule event of this transition stub.
     */
    private final RuleEvent event;
    /**
     * The added nodes of this transition stub.
     */
    private final HostNode[] addedNodes;
}