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
 * $Id: Graph.java,v 1.11 2008-01-30 09:32:52 iovka Exp $
 */
package groove.graph;

import groove.util.Fixable;

import java.util.Collection;
import java.util.Set;

/**
 * Provides a model of a graph whose nodes and edges are unstructured, in the
 * sense that they are immutable and edges are completely determined by source
 * and target nodes and edge label.
 * @version $Revision$ $Date: 2008-01-30 09:32:52 $
 */
public interface Graph<N extends Node,E extends Edge<N>> extends Fixable {
    /**
     * Returns the set of nodes of this graph. The return value is an
     * unmodifiable view of the underlying node set, which is <i>not</i>
     * guaranteed to be up-to-date with, or even safe in the face of, concurrent
     * modifications to the graph.
     * @ensure <tt>result != null</tt>
     */
    Set<? extends N> nodeSet();

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
    Set<? extends E> edgeSet();

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
    Set<? extends E> edgeSet(Node node);

    /**
     * Returns the set of incoming edges of a given node of this graph.
     * @param node the node of which the incoming edges are required
     */
    Set<? extends E> inEdgeSet(Node node);

    /**
     * Returns the set of outgoing edges of a given node of this graph.
     * @param node the node of which the outgoing edges are required
     */
    Set<? extends E> outEdgeSet(Node node);

    /**
     * Returns the set of all edges in this graph with a given label.
     * Although the return
     * type is a <tt>Collection</tt> to allow efficient implementation, it is
     * guaranteed to contain distinct elements.
     * @param label the label of the required edges
     */
    Set<? extends E> labelEdgeSet(Label label);

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
    boolean containsEdge(Edge<?> edge);

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
    GraphInfo<N,E> getInfo();

    /**
     * Sets an information object with additional information about this graph,
     * by copying an existing information object.
     * @param info an information object; may be <code>null</code> to reset the
     *        graph info
     * @return a shallow copy of <code>info</code>, or <code>null</code> if
     *         <code>info</code> was <code>null</code>
     */
    GraphInfo<N,E> setInfo(GraphInfo<?,?> info);

    /**
     * Makes a copy of this Graph with cloned (not aliased) node and edge sets
     * but aliased nodes and edges.
     * @ensure <tt>resultnodeSet().equals(this.nodeSet()) && result.edgeSet().equals(this.edgeSet()</tt>
     */
    Graph<N,E> clone();

    /**
     * Factory method: returns a fresh, empty graph with a new name.
     * @param name the (non-{@code null}) name of the new graph.
     */
    Graph<N,E> newGraph(String name);

    /**
     * Generates a fresh node and adds it to this graph.
     * @return the new node
     * @see Graph#addNode(Node)
     */
    N addNode();

    /**
     * Adds a node with a given number to this graph.
     * The node is required to be fresh within the graph.
     * @return the new node
     * @see Graph#addNode(Node)
     */
    N addNode(int nr);

    /**
     * Adds a binary edge to the graph, between given nodes and with a given
     * label text, and returns the edge. Also adds the end nodes if these are not yet
     * in the graph. If an edge with these properties already exists, the method
     * returns the existing edge.
     * @param source the source node of the new edge
     * @param label the label text of the new edge
     * @param target the target node of the new edge
     * @return a binary edge between <tt>source</tt> and <tt>target</tt>,
     *         labelled <tt>label</tt>
     * @see Graph#addEdge(Edge)
     */
    E addEdge(N source, String label, N target);

    /**
     * Adds a binary edge to the graph, between given nodes and with a given
     * label, and returns the edge. Also adds the end nodes if these are not yet
     * in the graph. If an edge with these properties already exists, the method
     * returns the existing edge.
     * @param source the source node of the new edge
     * @param label the label of the new edge
     * @param target the target node of the new edge
     * @return a binary edge between <tt>source</tt> and <tt>target</tt>,
     *         labelled <tt>label</tt>
     * @see Graph#addEdge(Edge)
     */
    E addEdge(N source, Label label, N target);

    /**
     * Adds a node to this graph. This is allowed only if the graph is not
     * fixed. If the node is already in the graph then the method has no effect.
     * 
     * @param node the node to be added.
     * @return <tt>true</tt> if the node was indeed added (and not yet
     *         present)
     * @require <tt>!isFixed()</tt>
     * @ensure <tt>nodeSet() = old.nodeSet() \cup node</tt>
     * @see #addEdge(Edge)
     * @see #isFixed()
     */
    boolean addNode(N node);

    /**
     * Adds an edge and its end nodes to this graph. This is allowed only if the
     * graph is not fixed. If the edge is already in the graph then the method
     * has no effect. 
     * @param edge the edge to be added.
     * @return <tt>true</tt> if the edge was indeed added (and not yet
     *         present)
     * @require <tt>!isFixed()</tt>
     * @ensure <tt>edgeSet() = old.edgeSet() \cup edge</tt> and
     *         <tt>nodeSet() = old.nodeSet() \cup edge.parts()</tt>
     * @see #addNode(Node)
     * @see #isFixed()
     */
    boolean addEdge(E edge);

    /**
     * Adds a set of nodes to this graph. This is allowed only if the graph is
     * modifiable (and not fixed). If all the nodes are already in the graph
     * then the method has no effect. All GraphListeners are notified for evey
     * node that is actually added.
     * @param nodeSet the collection of nodes to be added.
     * @return <tt>true</tt> if any node was indeed added
     * @require <tt>!isFixed()</tt>
     * @ensure <tt>nodeSet() = old.nodeSet() \cup nodeSet</tt>
     * @see #addNode(Node)
     * @see #addEdgeSet(Collection)
     * @see #isFixed()
     */
    boolean addNodeSet(Collection<? extends N> nodeSet);

    /**
     * Adds a set of edges and their end nodes to this graph. This is allowed
     * only if the graph is modifiable (and not fixed). If all the edges are
     * already in the graph then the method has no effect. All GraphListeners
     * are notified for every edge that is actually added.
     * @param edgeSet the collection of edges to be added.
     * @return <tt>true</tt> if any edge was indeed added
     * @require <tt>isFixed()</tt>
     * @ensure <tt>edgeSet() = old.edgeSet() \cup edgeSet</tt> and
     *         <tt>nodeSet() = old.nodeSet() \cup edgeSet.parts()</tt>
     * @see #addEdge(Edge)
     * @see #addNodeSet(Collection)
     * @see #isFixed()
     */
    boolean addEdgeSet(Collection<? extends E> edgeSet);

    /**
     * Removes a given node from this graph, if it was in the graph to start
     * with. All incident edges are also removed. This method is allowed only if
     * the graph is modifiable. The method has no effect if the node is not in
     * this graph. All GraphListeners are notified if the node is indeed
     * removed. <i>Note:</i> It is <i>not</i> guaranteed that
     * <tt>removeNode(Node)</tt> is called for the removal of all nodes, so
     * overwriting it may not have the expected effect. Use
     * <tt>GraphListener</tt> to ensure notification of all changes to the
     * graph.
     * @param node the node to be removed from the set. If
     *        <tt>other.hasPart(node)</tt> for some other element such that
     *        <tt>contains(other)</tt>, then <tt>other</tt> is also
     *        removed.
     * @return <tt>true</tt> if <tt>node</tt> was there in the first place
     * @require <tt>isFixed()</tt>
     * @ensure <tt>nodeSet() = old.nodeSet() \setminus node</tt> and
     *         <tt>edgeSet() = old.edgeSet() \seminus { edge | node\in edge.parts() }</tt>
     * @see #isFixed()
     * @see #removeEdge(Edge)
     */
    boolean removeNode(N node);

    /**
     * Removes a given edge from this graph, if it was in the graph to start
     * with. This method is allowed only if the graph is modifiable. The method
     * has no effect if the edge is not in this graph. All GraphListeners are
     * notified if the edge is indeed removed. <i>Note:</i> It is <i>not</i>
     * guaranteed that <tt>removeEdge(Edge)</tt> is called for the removal of
     * all edges, so overwriting it may not have the expected effect. Use
     * <tt>GraphListener</tt> to ensure notification of all changes to the
     * graph.
     * @param edge the edge to be removed from the graph.
     * @return <tt>true</tt> if <tt>edge</tt> was there in the first place
     * @require <tt>isFixed()</tt>
     * @ensure <tt>edgeSet() = edgeSet() \setminus edge</tt>
     * @see #isFixed()
     * @see #removeNode(Node)
     */
    boolean removeEdge(E edge);

    /**
     * Removes a set of nodes from this graph, if they were in the graph to
     * start with. All incident edges are also removed. This method is allowed
     * only if the graph is modifiable. The method has no effect if none of the
     * nodes are in this graph. All GraphListeners are notified if the node is
     * indeed removed.
     * @param nodeSet the collection of nodes to be removed from the set. If
     *        <tt>nodeSet.removeAll(other.parts())</tt> for some other element
     *        such that <tt>contains(other)</tt>, then <tt>other</tt> is
     *        also removed.
     * @return <tt>true</tt> if <tt>old.nodeSet() \cap nodeSet</tt> was
     *         nonempty
     * @require <tt>isFixed()</tt>
     * @ensure <tt>this.nodeSet() = old.nodeSet() \setminus nodeSet</tt> and
     *         <tt>edgeSet() = old.edgeSet() \setminus { other | other.parts() \cap nodeSet }</tt>
     * @see #isFixed()
     * @see #removeNode(Node)
     * @see #removeEdgeSet(Collection)
     */
    boolean removeNodeSet(Collection<? extends N> nodeSet);

    /**
     * Removes a set of edges from this graph, if they were in the graph to
     * start with. This method is allowed only if the graph is modifiable. The
     * method has no effect if none of the edges are in this graph. All
     * GraphListeners are notified if the edge is indeed removed.
     * @param edgeSet the collection of edges to be removed from the graph.
     * @return <tt>true</tt> if <tt>old.edgeSet() \cap edgeSet</tt> was
     *         nonempty
     * @require <tt>isFixed()</tt>
     * @ensure <tt>this.edgeSet() = old.edgeSet() \setminus edgeSet</tt>
     * @see #isFixed()
     * @see #removeEdge(Edge)
     * @see #removeNodeSet(Collection)
     */
    boolean removeEdgeSet(Collection<? extends E> edgeSet);

    /**
     * Merges two nodes in this graph, by adding all edges to and from the first
     * node to the second, and subsequently removing the first.
     * @param from node to be deleted
     * @param to node to receive copies of the edges to and from the other
     * @return <tt>true</tt> if <code>first</code> is distinct from
     *         <code>second</code>, so a merge actually took place
     * @require <tt>containsElement(from) && containsElement(to)</tt>
     * @ensure <tt>! containsElement(from)</tt> and
     *         <tt>containsElement(to,l,n)</tt> if
     *         <tt>old.containsElement(from,l,n)</tt> and
     *         <tt>containsElement(n,l,to)</tt> if
     *         <tt>old.containsElement(n,l,from)</tt> and
     */
    boolean mergeNodes(N from, N to);

    /**
     * More efficient addition of edges; for package use only. Avoids both the
     * class cast and especially the recursive addition of edge ends. Make sure
     * you satisfy the precondition, or an inconsistent graph may ensue!
     * @require <tt>edge instanceof Edge && containsAll(edge.ends())</tt>
     * @see #addEdge(Edge)
     */
    boolean addEdgeWithoutCheck(E edge);

    /**
     * More efficient addition of sets of edges; for package use only. Avoids
     * the recursive addition of parts. Make sure you satisfy the precondition,
     * or an inconsistent graph may ensue!
     * @require <tt>edge: 2^Edge && containsAll(edge.ends())</tt>
     * @see #addEdgeSet(Collection)
     */
    boolean addEdgeSetWithoutCheck(Collection<? extends E> edgeSet);

    /**
     * More efficient removal of nodes; for package use only. Avoids both the
     * class cast and especially the recursive removal of incident edges. Make
     * sure you satisfy the precondition, or an inconsistent graph may ensue!
     * @require <tt>node instanceof Node</tt> and the graph contains no
     *          incident edges.
     * @see #removeNode(Node)
     */
    boolean removeNodeWithoutCheck(N node);

    /**
     * More efficient removal of sets of nodes; for package use only. Avoids the
     * recursive removal of incident edges. Make sure you satisfy the
     * precondition, or an inconsistent graph may ensue!
     * @require <tt>nodeSet: 2^Node</tt> and the graph contains no incident
     *          edges.
     * @see #removeNodeSet(Collection)
     */
    boolean removeNodeSetWithoutCheck(Collection<? extends N> nodeSet);

    /** Returns the element factory used for elements of this graph. */
    ElementFactory<N,E> getFactory();

    /** 
     * Sets a new (non-{@code null}) name of this graph.
     * Only allowed if the graph is not fixed.
     */
    void setName(String name);

    /** Returns the (non-{@code null}) name of this graph. */
    String getName();

    /** Returns the (non-{@code null}) role of this graph. */
    GraphRole getRole();

    /** Default name for graphs. */
    public final String NO_NAME = "nameless graph";
}
