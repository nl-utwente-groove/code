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
 * $Id$
 */
package groove.trans;

import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Label;
import groove.graph.MergeMap;
import groove.graph.Node;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Class wrapping an event together with additional information.
 * All methods are delegated to the event.
 * @author Arend Rensink
 * @version $Revision $
 */
public class VirtualRuleEvent<C> implements RuleEvent {
	/** Constructs an instance with a given event and content. */
    public VirtualRuleEvent(RuleEvent event, C content) {
        this.content = content;
        this.event = event;
    }

    /** Returns the content wrapped together with the event. */
    public C getContent() {
        return content;
    }
    
    /** Returns the wrapped event. */
    public RuleEvent getWrappedEvent() {
        return event;
    }
    
    /**
     * Delegated to the wrapped event.
     * @see #getWrappedEvent()
     */
    public int compareTo(RuleEvent o) {
        return event.compareTo(o);
    }

    /**
     * Delegated to the wrapped event.
     * @see #getWrappedEvent()
     */
    public boolean conflicts(RuleEvent other) {
        return event.conflicts(other);
    }

    /**
     * Delegated to the wrapped event.
     * @see #getWrappedEvent()
     */
    public String getAnchorImageString() {
        return event.getAnchorImageString();
    }

    /**
     * Delegated to the wrapped event.
     * @see #getWrappedEvent()
     */
    public Collection< ? extends Edge> getComplexCreatedEdges(Iterator<Node> createdNodes) {
        return event.getComplexCreatedEdges(createdNodes);
    }

    /**
     * Delegated to the wrapped event.
     * @see #getWrappedEvent()
     */
    public List< ? extends Node> getCreatedNodes(Set< ? extends Node> hostNodes) {
        return event.getCreatedNodes(hostNodes);
    }

    /**
     * Delegated to the wrapped event.
     * @see #getWrappedEvent()
     */
    public Set<Node> getErasedNodes() {
        return event.getErasedNodes();
    }

    /**
     * Delegated to the wrapped event.
     * @see #getWrappedEvent()
     */
    public Label getLabel() {
        return event.getLabel();
    }

    /**
     * Delegated to the wrapped event.
     * @see #getWrappedEvent()
     */
    public RuleMatch getMatch(Graph source) {
        return event.getMatch(source);
    }

    /**
     * Delegated to the wrapped event.
     * @see #getWrappedEvent()
     */
    public MergeMap getMergeMap() {
        return event.getMergeMap();
    }

    /**
     * Delegated to the wrapped event.
     * @see #getWrappedEvent()
     */
    public Rule getRule() {
        return event.getRule();
    }

    /**
     * Delegated to the wrapped event.
     * @see #getWrappedEvent()
     */
    public Set<Edge> getSimpleCreatedEdges() {
        return event.getSimpleCreatedEdges();
    }

    /**
     * Delegated to the wrapped event.
     * @see #getWrappedEvent()
     */
    public Set<Edge> getSimpleErasedEdges() {
        return event.getSimpleErasedEdges();
    }
    
    /**
     * Delegated to the wrapped event.
     * @see #getWrappedEvent()
     */
    public boolean hasMatch(Graph source) {
        return event.hasMatch(source);
    }
    
    /**
     * Delegated to the wrapped event.
     * @see #getWrappedEvent()
     */
    public int identityHashCode() {
        return event.identityHashCode();
    }
    
    @Override
    public int hashCode() {
        return event.hashCode();
    }
        
    @Override
    public boolean equals(Object obj) {
        return event.equals(obj);
    }

    @Override
	public String toString() {
		return "Virtual "+event.toString()+" with "+content;
	}

	public RuleApplication newApplication(Graph source) {
        throw new UnsupportedOperationException("Virtual events should give rise to DefaultAliasTranstions");
    }

    private final C content;
    private final RuleEvent event;
}
