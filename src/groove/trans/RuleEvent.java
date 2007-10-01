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
 * $Id: RuleEvent.java,v 1.14 2007-10-01 21:53:08 rensink Exp $
 */
package groove.trans;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Label;
import groove.graph.MergeMap;
import groove.graph.Morphism;
import groove.graph.Node;
import groove.rel.VarNodeEdgeMap;

/**
 * Interface to encode a rule instantiation that provides images to the rule anchors.
 * Together with the host graph, the event uniquely defines a transformation.
 * The event does not store information specific to the host graph. To apply it to 
 * a given host graph, it has to be further instantiated to a rule application.
 * @author Arend Rensink
 * @version $Revision: 1.14 $ $Date: 2007-10-01 21:53:08 $
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
     * Returns a label that globally describes this event.
     * This is typically the rule name.
     * The information provided by the name is less extensive than
     * that of the label (see {@link #getLabel()}).
     * @deprecated use <code>getRule().getName()</code> instead
     */
    @Deprecated
    public RuleNameLabel getName();
//
//	/**
//     * Returns the mapping from the anchors in the rule's LHS to the source graph.
//     */
//    public VarNodeEdgeMap getAnchorMap();
    
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
     * These are the images of the rule's creator edges of which the endpoints are not creator nodes.
     */
    public Set<Edge> getSimpleCreatedEdges();
    
    /**
     * Returns a mapping from the rule's RHS to the target graph,
     * minus the creator nodes.
     * The mapping is only guaranteed to provide images
     * for the endpoints and variables of the creator edges.
     * @deprecated use {@link #getCreatedNodes(Set)} instead
     */
    @Deprecated
    public VarNodeEdgeMap getSimpleCoanchorMap();

    /** 
     * Returns a coanchor image suitable for a given host graph.
     * This is delegated to the event because here we can indeed keep a map of such 
     * images, and so save memory. 
     */
    public List<Node> getCreatedNodes(Set<? extends Node> hostNodes);

    /** 
     * Returns a coanchor image suitable for a given host graph.
     * This is delegated to the event because here we can indeed keep a map of such 
     * images, and so save memory. 
     */
    public Set<Edge> getComplexCreatedEdges(Iterator<Node> createdNodes);
    
    /**
	 * Indicates if a matching of this event's rule exists, based on the anchor map in this event.
	 */
	public boolean hasMatching(Graph source);

    /**
	 * Returns a matching of this event's rule, based on the anchor map in this event, if a matching exists.
	 * Returns <code>null</code> otherwise.
	 */
	public Morphism getMatching(Graph source);

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
     * for that, use {@link #hasMatching(Graph)} first.
     */
    public RuleApplication newApplication(Graph source);

    /** Convenience method for {@link System#identityHashCode(Object)}, included here for efficiency. */
    public int identityHashCode();
}