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
 * $Id$
 */
package groove.trans;

import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Label;
import groove.graph.MergeMap;
import groove.graph.Node;
import groove.lts.GraphTransition;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * Class wrapping an event together with additional information. All methods are
 * delegated to the event.
 * @author Arend Rensink
 * @version $Revision $
 */
public class VirtualEvent<C> implements RuleEvent {
    /** Constructs an instance with a given event and content. */
    public VirtualEvent(RuleEvent event, C content) {
        assert !(event instanceof VirtualEvent<?>);
        this.innerTarget = content;
        this.innerEvent = event;
    }

    /** Returns the content wrapped together with the event. */
    public C getInnerTarget() {
        return this.innerTarget;
    }

    /** Returns the wrapped event. */
    public RuleEvent getInnerEvent() {
        return this.innerEvent;
    }

    /**
     * Delegated to the wrapped event.
     * @see #getInnerEvent()
     */
    public int compareTo(RuleEvent o) {
        return this.innerEvent.compareTo(o);
    }

    /**
     * Delegated to the wrapped event.
     * @see #getInnerEvent()
     */
    public boolean conflicts(RuleEvent other) {
        return this.innerEvent.conflicts(other);
    }

    /**
     * Delegated to the wrapped event.
     * @see #getInnerEvent()
     */
    public String getAnchorImageString() {
        return this.innerEvent.getAnchorImageString();
    }

    /**
     * Delegated to the wrapped event.
     * @see #getInnerEvent()
     */
    public Collection<? extends Edge> getComplexCreatedEdges(
            Iterator<Node> createdNodes) {
        return this.innerEvent.getComplexCreatedEdges(createdNodes);
    }

    /**
     * Delegated to the wrapped event.
     * @see #getInnerEvent()
     */
    public Set<? extends Node> getCreatedNodes(Set<? extends Node> hostNodes) {
        return this.innerEvent.getCreatedNodes(hostNodes);
    }

    /**
     * Delegated to the wrapped event.
     * @see #getInnerEvent()
     */
    public Set<Node> getErasedNodes() {
        return this.innerEvent.getErasedNodes();
    }

    /**
     * Delegated to the wrapped event.
     * @see #getInnerEvent()
     */
    public Label getLabel() {
        return this.innerEvent.getLabel();
    }

    /**
     * Delegated to the wrapped event.
     * @see #getInnerEvent()
     */
    public RuleMatch getMatch(Graph source) {
        return this.innerEvent.getMatch(source);
    }

    /**
     * Delegated to the wrapped event.
     * @see #getInnerEvent()
     */
    public MergeMap getMergeMap() {
        return this.innerEvent.getMergeMap();
    }

    /**
     * Delegated to the wrapped event.
     * @see #getInnerEvent()
     */
    public Rule getRule() {
        return this.innerEvent.getRule();
    }

    /**
     * Delegated to the wrapped event.
     * @see #getInnerEvent()
     */
    public Set<Edge> getSimpleCreatedEdges() {
        return this.innerEvent.getSimpleCreatedEdges();
    }

    /**
     * Delegated to the wrapped event.
     * @see #getInnerEvent()
     */
    public Set<Edge> getSimpleErasedEdges() {
        return this.innerEvent.getSimpleErasedEdges();
    }

    /**
     * Delegated to the wrapped event.
     * @see #getInnerEvent()
     */
    public boolean hasMatch(Graph source) {
        return this.innerEvent.hasMatch(source);
    }

    /**
     * Delegated to the wrapped event.
     * @see #getInnerEvent()
     */
    public int identityHashCode() {
        return this.innerEvent.identityHashCode();
    }

    @Override
    public int hashCode() {
        return this.innerEvent.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this.innerEvent.equals(obj);
    }

    @Override
    public String toString() {
        return "Virtual " + this.innerEvent.toString() + " with "
            + this.innerTarget;
    }

    public RuleApplication newApplication(Graph source) {
        throw new UnsupportedOperationException(
            "Virtual events should give rise to DefaultAliasTranstions");
    }

    public Node[] getAddedNodes(groove.lts.GraphState source) {
        return this.innerEvent.getAddedNodes(source);
    }

    public RuleEvent getEvent(groove.lts.GraphState source) {
        return this.innerEvent.getEvent(source);
    }

    public groove.lts.GraphState getTarget(groove.lts.GraphState source) {
        return this.innerEvent.getTarget(source);
    }

    public boolean isSymmetry() {
        return this.innerEvent.isSymmetry();
    }

    public GraphTransition toTransition(groove.lts.GraphState source) {
        return this.innerEvent.toTransition(source);
    }

    /** Object wrapped together with the event. */
    private final C innerTarget;
    /** Wrapped event. */
    private final RuleEvent innerEvent;

    /**
     * Instantiation of generic class for type parameter
     * {@link groove.lts.GraphState}.
     */
    public static class GraphState extends VirtualEvent<groove.lts.GraphState> {
        /** Constructs a virtual event from a given graph transition. */
        public GraphState(GraphTransition inner) {
            this(inner.getEvent(), inner.target(), inner.getAddedNodes(),
                inner.isSymmetry());
        }

        /** Invokes super constructor. */
        public GraphState(RuleEvent event, groove.lts.GraphState innerTarget,
                Node[] innerAddedNodes, boolean symmetry) {
            super(event, innerTarget);
            this.innerAddedNodes = innerAddedNodes;
            this.symmetry = symmetry;
            // assert event.getRule().equals(innerTarget.getEvent().getRule()) :
            // String.format("Virtual rule %s should equal content rule %s",
            // event.getRule().getName(),
            // innerTarget.getEvent().getRule().getName());
        }

        /**
         * Returns the target of a given rule event, by trying to walk around
         * three sides of a confluent diamond instead of computing the target
         * directly.
         * @param event the rule event
         * @return the target state; <code>null</code> if no confluent diamond
         *         was found
         */
        public groove.lts.GraphState getConfluentTarget(RuleEvent event) {
            return isConfluent(event) ? getInnerTarget().getNextState(event)
                    : null;
        }

        /**
         * Returns the inner added nodes of this event.
         */
        public final Node[] getInnerAddedNodes() {
            return this.innerAddedNodes;
        }

        /**
         * Tests if the effect of this virtual event is independent of that of
         * another event. If this is the case, then the target states does not
         * depend on the order in which the two events are applied.
         * @return <code>true</code> if the virtual event does not involve a
         *         non-trivial symmetry, and does not conflict with the other
         *         event.
         */
        public boolean isConfluent(RuleEvent event) {
            return !(this.symmetry || conflicts(event));
        }

        private final Node[] innerAddedNodes;
        private final boolean symmetry;
    }
}
