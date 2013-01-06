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
public interface Graph<N extends Node,E extends Edge> extends Fixable {
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
     * <tt>edgeSet().size()</tt>
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
    Set<? extends E> edgeSet(Label label);

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
    boolean containsNode(N node);

    /**
     * Tests whether this graph contains a given edge.
     * @param edge the edge of which the presence is tested.
     */
    boolean containsEdge(E edge);

    // -------------------- Commands -----------------

    /**
     * Changes the modifiability of this graph. After invoking this method,
     * <tt>isFixed()</tt> holds. If the graph is fixed, no <tt>add</tt>- or
     * <tt>remove</tt>-method may be invoked any more; moreover, all graph
     * listeners are removed.
     * @ensure <tt>isFixed()</tt>
     * @see #isFixed()
     */
    boolean setFixed();

    /**
     * Indicates if the {@link GraphInfo} object of this graph
     * has been initialised.
     * @return {@code true} if the information object has been initialised
     * @see #getInfo()
     */
    boolean hasInfo();

    /**
     * Returns an information object with additional information about this
     * graph. The information object is created (and stored) if it was not
     * initialised yet, resulting in a larger memory footprint for the graph.
     * To avoid creating the info object, test for its presence with {@link #hasInfo()}
     * @return the (non-{@code null}) information object
     * @see #hasInfo()
     */
    GraphInfo getInfo();

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
     * Convenience method; equivalent to {@code addNode(getFactory().createNode())}
     * @return the new node; non-{@code null}
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
     * label text, and returns the edge. The end nodes are assumed to be in the
     * graph already. 
     * If an edge with these properties already exists, the method
     * returns the existing edge.
     * This method is equivalent to {@code addEdge(getFactory().createEdge(source,label,target))}.
     * @param source the (non-{@code null}) source node of the new edge
     * @param label the (non-{@code null}) label text of the new edge
     * @param target the (non-{@code null}) target node of the new edge
     * @return a binary edge between <tt>source</tt> and <tt>target</tt>,
     *         labelled <tt>label</tt>
     * @see Graph#addEdge(Edge)
     */
    E addEdge(N source, String label, N target);

    /**
     * Adds a binary edge to the graph, between given nodes and with a given
     * label, and returns the edge. The end nodes are assumed to be in the
     * graph already. If an edge with these properties already exists, the method
     * returns the existing edge.
     * This method is equivalent to {@code addEdge(getFactory().createEdge(source,label,target))}.
     * @param source the (non-{@code null}) source node of the new edge
     * @param label the (non-{@code null}) label of the new edge
     * @param target the (non-{@code null}) target node of the new edge
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
     * @see #addEdge(Edge)
     * @see #isFixed()
     */
    boolean addNode(N node);

    /**
     * Convenience method to add an edge and its end nodes to this graph.
     * The effect is equivalent to calling {@code addNod(edge.source())},
     * {@code addNode(edge.target()} and {@code addEdge(edge)} in succession.
     * @param edge the (non-{@code null}) edge to be added, together
     * with its end nodes
     * @return <tt>true</tt> if the graph changed as a result of this call
     * @see #addNode(Node)
     */
    boolean addEdgeContext(E edge);

    /**
     * Adds a set of nodes to this graph. This is allowed only if the graph is
     * modifiable (and not fixed). If all the nodes are already in the graph
     * then the method has no effect. All GraphListeners are notified for every
     * node that is actually added.
     * @param nodeSet the collection of nodes to be added.
     * @return <tt>true</tt> if the graph changed as a result of this call
     * @see #addNode(Node)
     */
    boolean addNodeSet(Collection<? extends N> nodeSet);

    /**
     * Convenience method to add a set of edges and their end nodes to this graph.
     * The effect is equivalent to calling {@link #addEdgeContext(Edge)} for
     * every element of {@code edgeSet}.
     * @param edgeSet the (non-{@code null}) set of edges to be added, together
     * with their end nodes
     * @return <tt>true</tt> if the graph changed as a result of this call
     * @see #addEdgeContext(Edge)
     */
    boolean addEdgeSetContext(Collection<? extends E> edgeSet);

    /**
     * Removes a given edge from this graph, if it was in the graph to start
     * with. This method is allowed only if the graph is modifiable. The method
     * has no effect if the edge is not in this graph. All GraphListeners are
     * notified if the edge is indeed removed. <i>Note:</i> It is <i>not</i>
     * guaranteed that <tt>removeEdge(Edge)</tt> is called for the removal of
     * all edges, so overwriting it may not have the expected effect. Use
     * <tt>GraphListener</tt> to ensure notification of all changes to the
     * graph.
     * @param edge the (non-{@code null}) edge to be removed from the graph
     * @return <tt>true</tt> if the graph changed as a result of this call
     */
    boolean removeEdge(E edge);

    /**
     * Convenience method to remove a set of nodes together with
     * their incident edges. This method is equivalent to a call
     * of {{@link #removeNodeContext(Node)} for every node in {@code nodeSet}.
     * @param nodeSet the (non-{@code null}) collection of nodes to be removed from the graph
     * @return <tt>true</tt> if the graph changed as a result of this call
     * @see #removeNodeContext(Node)
     */
    boolean removeNodeSetContext(Collection<? extends N> nodeSet);

    /**
     * Convenience method to remove both a node and its incident edges.
     * Equivalent to a call of {@code removeEdgeSet(edgeSet(node))} followed
     * by a call of {@code removeNode(node)}.
     * @param node the (non-{@code null}) node to be removed from the graph,
     * together with its incident edges
     * @return <tt>true</tt> if the graph changed as a result of this call
     */
    boolean removeNodeContext(N node);

    /**
     * Removes a set of edges from this graph, if they were in the graph to
     * start with. This method is allowed only if the graph is modifiable. The
     * method has no effect if none of the edges are in this graph. All
     * GraphListeners are notified for all edges that are removed.
     * @param edgeSet the (non-{@code null}) collection of edges to be removed from the graph
     * @return <tt>true</tt> if the graph changed as a result of this call
     * @see #isFixed()
     * @see #removeEdge(Edge)
     * @see #removeNodeSetContext(Collection)
     */
    boolean removeEdgeSet(Collection<? extends E> edgeSet);

    /**
     * Adds an edge to the graph.
     * The end nodes are assumed to be in the graph already.
     * @param edge the (non-{@code null}) edge to be added to the graph
     * @return <tt>true</tt> if the graph changed as a result of this call
     */
    boolean addEdge(E edge);

    /**
     * Removes a node from the graph.
     * The node is assumed to have no incident edges.
     * @param node the (non-{@code null}) node to be removed from the graph
     * @return <tt>true</tt> if the graph changed as a result of this call
     * @see #removeNodeContext(Node)
     */
    boolean removeNode(N node);

    /**
     * Removes a set of nodes from the graph.
     * The nodes are assumed to have no incident edges.
     * The method is equivalent to calling {@link #removeNode(Node)}
     * for every element of {@code nodeSet}.
     * @param nodeSet the (non-{@code null}) set of nodes to be removed from the graph
     * @return <tt>true</tt> if the graph changed as a result of this call
     * @see #removeNodeSetContext(Collection)
     */
    boolean removeNodeSet(Collection<? extends N> nodeSet);

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
