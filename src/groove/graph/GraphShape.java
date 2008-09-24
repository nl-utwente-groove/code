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
 * $Id: GraphShape.java,v 1.10 2008-01-30 09:32:53 iovka Exp $
 */
package groove.graph;

import java.util.Collection;
import java.util.Set;

/**
 * Model of a graph shape, consisting of nodes and labelled edges between them..
 * @author Arend Rensink
 * @version $Revision$ $Date: 2008-01-30 09:32:53 $
 */
public interface GraphShape extends java.io.Serializable {
    /**
     * Returns the set of nodes of this graph.
     * The return value is an unmodifiable view of the underlying node set,
     * which is <i>not</i> guaranteed to be up-to-date with, or even safe 
     * in the face of, concurrent modifications to the graph.
     * @ensure <tt>result != null</tt>
     */
    Set<? extends Node> nodeSet();

    /**
     * Returns the number of nodes in this graph.
     * Convenience method for <tt>nodeSet().size()</tt>
     * @return the number of nodes in this graph
     * @ensure <tt>result == nodeSet().size()</tt>
     */
    int nodeCount();

    /**
     * Returns the set of Edges of this Graph.
     * The return value is an unmodifiable view of the underlying edge set,
     * which is <i>not</i> guaranteed to remain up-to-date with, or even safe 
     * in the face of, concurrent modifications to the graph.
     * @ensure <tt>result != null</tt>
     */
    Set<? extends Edge> edgeSet();

    /**
     * Returns the number of edges of this graph.
     * Convenience method for <tt>nodeSet().size()</tt>
     * @return the number of edges in this graph
     * @ensure <tt>result == edgeSet().size()</tt>
     */
    int edgeCount();

     /**
      * Returns the set of all incident edges of a given node of this graph.
      * Although the return type is a <tt>Collection</tt> to allow 
      * efficient implementation, it is guaranteed 
      * to contain distinct elements.
      * @param node the node of which the incident edges are required
      * @require node != null
      * @ensure result == { edge \in E | \exists i: edge.end(i).equals(node) }
      * @see #edgeSet(Node, int)
      */
    Set<? extends Edge> edgeSet(Node node);

    /**
     * Returns the set of incident edges of a given node of this graph,
     * for a given end position in the edge.
     * Although the return type is a <tt>Collection</tt> to allow 
     * efficient implementation, it is guaranteed 
     * to contain distinct elements.
     * @param node the node of which the incident edges are required
     * @param i the position within the edges at which the node should occur
     * @require node != null
     * @ensure result == { edge \in E | edge.end(i).equals(node) }
     * @see #edgeSet(Node)
     * @see #outEdgeSet(Node)
     */
    Set<? extends Edge> edgeSet(Node node, int i);

    /**
     * Returns the set of outgoing edges of a given node of this graph.
     * This is a convenience method for <tt>edgeSet(node,Edge.SOURCE_INDEX)</tt>.
     * @param node the node of which the outgoing Edges are required
     * @require node != null
     * @ensure result == { edge \in E | edge.source().equals(node) }
     */
    Set<? extends Edge> outEdgeSet(Node node);

    /**
     * Returns the set of all edges in this graph with a given label and arity.
     * Convenience method for <tt>(Collection) labelEdgeMap(arity).get(label)</tt>
     * Although the return type is a <tt>Collection</tt> to allow 
     * efficient implementation, it is guaranteed 
     * to contain distinct elements.
     * @param label the label of the required edges
     * @param arity the number of endpoints of the required edges
     * @require <tt>label != null</tt> and <tt>1 <= arity <= AbstractEdge.getMaxEndCount()</tt>
     * @ensure <tt>result == labelEdgeMap(arity).get(Label).get(arity)</tt>
     */
    Set<? extends Edge> labelEdgeSet(int arity, Label label);
//
//    /**
//     * Returns a map from the labels in this graph of a given arity to non-empty sets
//     * (actually, <tt>Collection</tt>s guaranteed to contain distinct
//     * elements) of all edges with that label and arity in this graph.
//     * @param arity the number of endpoints of the required edges
//     * @return <tt>result: Label -> Collection^*</tt> such that
//     * <tt>result.get(label).get(arity).contains(edge)</tt> iff 
//     * <tt>contains(edge) && edge.label().equals(label) && edge.partsCount() == arity</tt> 
//     * @require <tt>1 <= arity <= AbstractEdge.getMaxEndCount()</tt>
//     */
//    Map<Label, ? extends Set<? extends Edge>> labelEdgeMap(int arity);
    
    /**
     * Returns the total number of elements (nodes plus edges) in this graph.
     * @ensure <tt>result == nodeCount() + edgeCount()</tt>
     */
    int size();

    /**
     * Tests whether this Graph is empty (i.e., contains no Nodes or Edges).
     * @return <tt>result == nodeSet().isEmpty()</tt>
     */
    boolean isEmpty();

    /**
     * Indicates whether the graph is modifiable,
     * i.e., if the <tt>add</tt> and <tt>remove</tt> methods can change the graph.
     * The graph is modifiable when it is created, and becomes fixed only after
     * an invocation of <tt>setFixed()</tt>.
     * @return <tt>true</tt> iff <tt>setFixed()</tt> has been invoked
     * @see #setFixed()
     */
    boolean isFixed();

    /** 
     * Tests whether this graph contains a given element.
     * @param elem the graph element of which the presence is tested.
     * @ensure <tt>result == (elem \in nodeSet() \cup edgeSet())</tt>
     */
    boolean containsElement(Element elem);
    
    /** 
     * Tests whether this graph contains an entire set of graph elements.
     * @param elements the set of which the presence is tested.
     * @require <tt>elements: 2^Element</tt>
     * @ensure <tt>result == (elements \subseteq nodeSet() \cup edgeSet())</tt>
     */
    boolean containsElementSet(Collection<? extends Element> elements);

    // -------------------- Commands -----------------

    /**
     * Changes the modifiability of this graph. After invoking this method,
     * <tt>isFixed()</tt> holds.
     * If the graph is fixed, no <tt>add</tt>- or <tt>remove</tt>-method may be
     * invoked any more; moreover, all graph listeners are removed.
     * @ensure <tt>isFixed()</tt>
     * @see #isFixed()
     */
    void setFixed();
//    
//    // ------------------------------ Object overrides ---------------------------
//
//    /** 
//     * Tests whether this Object is equal as a graph to another.
//     * @param obj the Object to be compared with this one
//     * @return true if <tt>other instanceof Graph && this.equals((Graph) other)</tt>
//     * @see #equals(GraphShape)
//     */
//    boolean equals(Object obj);
//
//    /** 
//     * Tests whether this Graph equals another.
//     * @param other the Object to be compared with this one
//     * @return true if <tt>other.nodeSet().equals(nodeSet())
//     *              && other.edgeSet().equals(edgeSet())</tt>
//     * @see #equals(Object)
//     */
//    boolean equals(GraphShape other);

    // ------------------------ graph listener methods ----------------------------
    /**
     * Adds a graph listener to this graph.
     * @param listener the GraphListener to be added
     * @require <tt>listener != null</tt>
     * @ensure <tt>listener</tt> will be notified of addition and removal of elements
     * @see #removeGraphListener(GraphShapeListener)
     */
    void addGraphListener(GraphShapeListener listener);

    /**
     * Removes a graph listener from this graph. 
     * @param listener the GraphListener to be removed
     * @require <tt>listener != null</tt>
     * @ensure <tt>listener</tt> will no longer be notified of addition and removal of elements
     * @see #addGraphListener(GraphShapeListener)
     */
    void removeGraphListener(GraphShapeListener listener);

    /** 
     * Returns an information object with additional information about this graph.
     * The object may be <code>null</code> if there is no additional information.
     */
    GraphInfo getInfo();
    
    /** 
     * Sets an information object with additional information about this graph,
     * by copying an existing information object.
     * @param info an information object; may be <code>null</code> to reset the graph info
     * @return a shallow copy of <code>info</code>, or <code>null</code> if <code>info</code> was <code>null</code>
     */
    GraphInfo setInfo(GraphInfo info);
//    
//    /**
//     * Stores specific data for the given key.
//     * @param key the key for storing the data
//     * @param data the data to be stored
//     */
//    void storeSpecificData(String key, Object data);
//
//    /**
//     * Returns the specific data for the given key.
//     * @param key the key for getting the data
//     * @return the specific data stored for the given key
//     */
//    Object getSpecificData(String key);
//
//    /**
//     * Checks whether the current instance has data stored for the given key.
//     * @param key the key for the data to be checked for existence
//     * @return <tt>true</tt> if for the given key data has been stored, <tt>false</tt> otherwise 
//     */
//    boolean hasSpecificData(String key);
}