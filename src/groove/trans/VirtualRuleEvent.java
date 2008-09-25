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
import groove.lts.DefaultAliasApplication;
import groove.lts.GraphTransitionStub;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class VirtualRuleEvent implements RuleEvent {
    public VirtualRuleEvent(RuleEvent event, GraphTransitionStub stub) {
        this.stub = stub;
        this.event = event;
    }

    public GraphTransitionStub getStub() {
        return stub;
    }
    
    public RuleEvent getWrappedEvent() {
        return event;
    }
    
    /**
     * @param o
     * @return
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(RuleEvent o) {
        return event.compareTo(o);
    }
    /**
     * @param other
     * @return
     * @see groove.trans.RuleEvent#conflicts(groove.trans.RuleEvent)
     */
    public boolean conflicts(RuleEvent other) {
        return event.conflicts(other);
    }
    /**
     * @return
     * @see groove.trans.RuleEvent#getAnchorImageString()
     */
    public String getAnchorImageString() {
        return event.getAnchorImageString();
    }
    /**
     * @param createdNodes
     * @return
     * @see groove.trans.RuleEvent#getComplexCreatedEdges(java.util.Iterator)
     */
    public Collection< ? extends Edge> getComplexCreatedEdges(Iterator<Node> createdNodes) {
        return event.getComplexCreatedEdges(createdNodes);
    }
    /**
     * @param hostNodes
     * @return
     * @see groove.trans.RuleEvent#getCreatedNodes(java.util.Set)
     */
    public List< ? extends Node> getCreatedNodes(Set< ? extends Node> hostNodes) {
        return event.getCreatedNodes(hostNodes);
    }
    /**
     * @return
     * @see groove.trans.RuleEvent#getErasedNodes()
     */
    public Set<Node> getErasedNodes() {
        return event.getErasedNodes();
    }
    /**
     * @return
     * @see groove.trans.RuleEvent#getLabel()
     */
    public Label getLabel() {
        return event.getLabel();
    }
    /**
     * @param source
     * @return
     * @see groove.trans.RuleEvent#getMatch(groove.graph.Graph)
     */
    public RuleMatch getMatch(Graph source) {
        return event.getMatch(source);
    }
    /**
     * @return
     * @see groove.trans.RuleEvent#getMergeMap()
     */
    public MergeMap getMergeMap() {
        return event.getMergeMap();
    }
    /**
     * @return
     * @see groove.trans.RuleEvent#getRule()
     */
    public Rule getRule() {
        return event.getRule();
    }
    /**
     * @return
     * @see groove.trans.RuleEvent#getSimpleCreatedEdges()
     */
    public Set<Edge> getSimpleCreatedEdges() {
        return event.getSimpleCreatedEdges();
    }
    /**
     * @return
     * @see groove.trans.RuleEvent#getSimpleErasedEdges()
     */
    public Set<Edge> getSimpleErasedEdges() {
        return event.getSimpleErasedEdges();
    }
    
    /**
     * @param source
     * @return
     * @see groove.trans.RuleEvent#hasMatch(groove.graph.Graph)
     */
    public boolean hasMatch(Graph source) {
        return event.hasMatch(source);
    }
    /**
     * @return
     * @see groove.trans.RuleEvent#identityHashCode()
     */
    public int identityHashCode() {
        return event.identityHashCode();
    }
    
    public int hashCode() {
        return event.hashCode();
    }
        
    @Override
    public boolean equals(Object obj) {
        return event.equals(obj);
    }

    public RuleApplication newApplication(Graph source) {
        throw new UnsupportedOperationException("Virtual events should give rise to DefaultAliasTranstions");
    }

    private final GraphTransitionStub stub;
    private final RuleEvent event;
}
