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
 * $Id: SetEvent.java,v 1.1 2007-10-01 16:02:14 rensink Exp $
 */
package groove.trans;

import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Label;
import groove.graph.MergeMap;
import groove.graph.Morphism;
import groove.graph.Node;
import groove.graph.NodeSet;
import groove.rel.VarNodeEdgeMap;
import groove.util.TreeHashSet;

import java.util.Set;

/**
 * Rule event consisting of a set of events.
 * @author Arend Rensink
 * @version $Revision $
 */
public class SetEvent implements RuleEvent {
    /** Creates a new event on the basis of a given event set. */
    public SetEvent(Rule rule, Set<RuleEvent> eventSet) {
        this.rule = rule;
        this.eventSet = eventSet;
    }

    public boolean conflicts(RuleEvent other) {
        for (RuleEvent event: eventSet) {
            if (event.conflicts(other)) {
                return true;
            }
        }
        return false;
    }

    public String getAnchorImageString() {
        // TODO Auto-generated method stub
        return null;
    }

    public VarNodeEdgeMap getAnchorMap() {
        // TODO Auto-generated method stub
        return null;
    }

    public Node[] getCoanchorImage(Graph source) {
        // TODO Auto-generated method stub
        return null;
    }

    public Set<Node> getErasedNodes() {
        Set<Node> result = createNodeSet();
        for (RuleEvent event: eventSet) {
            result.addAll(event.getErasedNodes());
        }
        return result;
    }

    public Label getLabel() {
        // TODO Auto-generated method stub
        return null;
    }

    public Morphism getMatching(Graph source) {
        // TODO Auto-generated method stub
        return null;
    }

    public MergeMap getMergeMap() {
        MergeMap result = new MergeMap();
        for (RuleEvent event: eventSet) {
            result.putAll(event.getMergeMap());
        }
        return result;
    }

    @Deprecated
    public RuleNameLabel getName() {
        return getRule().getName();
    }

    public Rule getRule() {
        // TODO Auto-generated method stub
        return rule;
    }

    public VarNodeEdgeMap getSimpleCoanchorMap() {
        // TODO Auto-generated method stub
        return null;
    }

    public Set<Edge> getSimpleCreatedEdges() {
        Set<Edge> result = createEdgeSet();
        for (RuleEvent event: eventSet) {
            result.addAll(event.getSimpleCreatedEdges());
        }
        return result;
    }

    public Set<Edge> getSimpleErasedEdges() {
        Set<Edge> result = createEdgeSet();
        for (RuleEvent event: eventSet) {
            result.addAll(event.getSimpleErasedEdges());
        }
        return result;
    }

    public boolean hasMatching(Graph source) {
        for (RuleEvent event: eventSet) {
            if (!event.hasMatching(source)) {
                return false;
            }
        }
        return true;
    }

    public int identityHashCode() {
        int result = identityHashCode;
        if (result == 0) {
            result = identityHashCode = System.identityHashCode(this);
            if (result == 0) {
                result = identityHashCode += 1;
            }
        }
        return result;
    }

    public RuleApplication newApplication(Graph source) {
        return new DefaultApplication(this, source);
    }

    public int compareTo(RuleEvent o) {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * Callback factory method to create a fresh, empty node set.
     */
    private Set<Node> createNodeSet() {
        return new NodeSet();
    }

    /**
     * Callback factory method to create a fresh, empty edge set.
     */
    private Set<Edge> createEdgeSet() {
        return new TreeHashSet<Edge>();
    }
    
    /** The (nested) rule for which this is a collective event. */
    private final Rule rule;
    /** The set of events constituting this event. */
    private final Set<RuleEvent> eventSet;
    /** The pre-computed identity hash code for this object. */
    private int identityHashCode;
}
