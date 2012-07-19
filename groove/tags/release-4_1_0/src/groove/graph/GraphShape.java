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
 * $Id: GraphShape.java,v 1.10 2008-01-30 09:32:53 iovka Exp $
 */
package groove.graph;

import groove.util.Fixable;

import java.util.Collection;
import java.util.Set;

/**
 * Model of a graph shape, consisting of nodes and labelled edges between them..
 * @author Arend Rensink
 * @version $Revision$ $Date: 2008-01-30 09:32:53 $
 */
public interface GraphShape extends java.io.Serializable, Fixable {
    /**
     * Returns the set of nodes of this graph. The return value is an
     * unmodifiable view of the underlying node set, which is <i>not</i>
     * guaranteed to be up-to-date with, or even safe in the face of, concurrent
     * modifications to the graph.
     * @ensure <tt>result != null</tt>
     */
    Set<? extends Node> nodeSet();

    /**
     * Returns the number of nodes in this graph. Convenience method for
     * <tt>nodeSet().size()</tt>
     * @return the number of nodes in this graph
     * @ensure <tt>result == nodeSet().size()</tt>
     */
    int nodeCount();

    /**
     * Returns the set of Edges of this Graph. The return value is an
     * unmodifiable view of the underlying edge set, which is <i>not</i>
     * guaranteed to remain up-to-date with, or even safe in the face of,
     * concurrent modifications to the graph.
     * @ensure <tt>result != null</tt>
     */
    Set<? extends Edge> edgeSet();

    /**
     * Returns the number of edges of this graph. Convenience method for
     * <tt>nodeSet().size()</tt>
     * @return the number of edges in this graph
     * @ensure <tt>result == edgeSet().size()</tt>
     */
    int edgeCount();

    /**
     * Returns the set of all incident edges of a given node of this graph.
     * Although the return type is a <tt>Collection</tt> to allow efficient
     * implementation, it is guaranteed to contain distinct elements.
     * @param node the node of which the incident edges are required
     * @require node != null
     * @ensure result == { edge \in E | \exists i: edge.end(i).equals(node) }
     */
    Set<? extends Edge> edgeSet(Node node);

    /**
     * Returns the set of incoming edges of a given node of this graph.
     * @param node the node of which the incoming edges are required
     */
    Set<? extends Edge> inEdgeSet(Node node);

    /**
     * Returns the set of outgoing edges of a given node of this graph.
     * @param node the node of which the outgoing edges are required
     */
    Set<? extends Edge> outEdgeSet(Node node);

    /**
     * Returns the set of all edges in this graph with a given label.
     * Although the return
     * type is a <tt>Collection</tt> to allow efficient implementation, it is
     * guaranteed to contain distinct elements.
     * @param label the label of the required edges
     */
    Set<? extends Edge> labelEdgeSet(Label label);

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
     * Indicates whether the graph is modifiable, i.e., if the <tt>add</tt> and
     * <tt>remove</tt> methods can change the graph. The graph is modifiable
     * when it is created, and becomes fixed only after an invocation of
     * <tt>setFixed()</tt>.
     * @return <tt>true</tt> iff <tt>setFixed()</tt> has been invoked
     * @see #setFixed()
     */
    boolean isFixed();

    /**
     * Tests whether this graph contains a given node.
     * @param node the node of which the presence is tested.
     */
    boolean containsNode(Node node);

    /**
     * Tests whether this graph contains a given edge.
     * @param edge the edge of which the presence is tested.
     */
    boolean containsEdge(Edge edge);

    /**
     * Tests whether this graph contains a given element.
     * @param elem the graph element of which the presence is tested.
     * @ensure <tt>result == (elem \in nodeSet() \cup edgeSet())</tt>
     * @deprecated use {@link #containsNode(Node)} or {@link #containsEdge(Edge)}
     */
    @Deprecated
    boolean containsElement(Element elem);

    /**
     * Tests whether this graph contains an entire set of graph elements.
     * @param elements the set of which the presence is tested.
     * @require <tt>elements: 2^Element</tt>
     * @ensure <tt>result == (elements \subseteq nodeSet() \cup edgeSet())</tt>
     */
    @Deprecated
    boolean containsElementSet(Collection<? extends Element> elements);

    // -------------------- Commands -----------------

    /**
     * Changes the modifiability of this graph. After invoking this method,
     * <tt>isFixed()</tt> holds. If the graph is fixed, no <tt>add</tt>- or
     * <tt>remove</tt>-method may be invoked any more; moreover, all graph
     * listeners are removed.
     * @ensure <tt>isFixed()</tt>
     * @see #isFixed()
     */
    void setFixed();

    /**
     * Returns an information object with additional information about this
     * graph. The object may be <code>null</code> if there is no additional
     * information.
     */
    GraphInfo getInfo();

    /**
     * Sets an information object with additional information about this graph,
     * by copying an existing information object.
     * @param info an information object; may be <code>null</code> to reset the
     *        graph info
     * @return a shallow copy of <code>info</code>, or <code>null</code> if
     *         <code>info</code> was <code>null</code>
     */
    GraphInfo setInfo(GraphInfo info);
}