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
 * $Id: RuleEvent.java,v 1.21 2008-03-03 21:27:40 rensink Exp $
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
 * Interface to encode a rule instantiation that provides images to the rule anchors.
 * Together with the host graph, the event uniquely defines a transformation.
 * The event does not store information specific to the host graph. To apply it to 
 * a given host graph, it has to be further instantiated to a rule application.
 * @author Arend Rensink
 * @version $Revision$ $Date: 2008-03-03 21:27:40 $
 */
public interface RuleEvent extends Comparable<RuleEvent> {
    /**
     * Returns the rule for which this is an application.
     */
    public Rule getRule();

    /**
     * Returns a label that uniquely identifies this event.
     */
    public Label getLabel();

    /**
     * Returns a string representation of the anchor image.
     */
    public String getAnchorImageString();
    
    /** Returns the set of nodes erased by the event. */
    public Set<Node> getErasedNodes();
    
    /** 
     * Returns the set of edges explicitly erased by the event.
     * This does <code>not</code> include all incident edges of the erased or merged nodes. 
     */
    public Set<Edge> getSimpleErasedEdges();
    
    /**
     * Returns the merge map of the event.
     * The merge map contains entries for nodes that are deleted, and nodes that are
     * mapped to another node as a consequence of a merger in the rule.
     */
    public MergeMap getMergeMap();

    /**
     * Returns the set of explicitly created edges between existing nodes. 
     * These are the images of those creator edges of which the endpoints are not fresh.
     */
    public Set<Edge> getSimpleCreatedEdges();
    
    /** 
     * Returns a list of created nodes of this event, given a set of nodes
     * already existing in the graph (i.e., which may not be used).
     * @param hostNodes set of nodes not available as fresh nodes
     */
    public List<? extends Node> getCreatedNodes(Set<? extends Node> hostNodes);

    /** 
     * Returns the set of explicitly created edges of which at least one end is a fresh node.
     * @param createdNodes the images of the creator nodes, in the order of the rule's creator nodes.
     */
    public Collection<? extends Edge> getComplexCreatedEdges(Iterator<Node> createdNodes);
    
	/**
	 * Indicates if a matching of this event's rule exists, based on the anchor map in this event.
	 */
	public boolean hasMatch(Graph source);

    /**
	 * Returns a match of this event's rule, based on the anchor map in this event.
	 * Returns <code>null</code> if no match exists.
	 */
	public RuleMatch getMatch(Graph source);

    /** 
     * Tests if this event conflicts with another,
     * in the sense that if the events occur in either order it is not guaranteed that 
     * the result is the same.
     * This is the case if one event creates a simple edge (i.e., not 
     * between creator nodes) that the other erases.
     */
    public boolean conflicts(RuleEvent other);
    
    /**
     * Factory method to create a rule application on a given source graph.
     * The method does <i>not</i> check if the event is actually applicable to the host graph;
     * for that, use {@link #hasMatch(Graph)} first.
     */
    public RuleApplication newApplication(Graph source);

    /** Convenience method for {@link System#identityHashCode(Object)}, included here for efficiency. */
    public int identityHashCode();
}