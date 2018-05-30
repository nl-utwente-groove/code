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
package groove.graph;

import java.util.Set;

/**
 * Provides a model of a graph whose nodes and edges are unstructured, in the
 * sense that they are immutable and edges are completely determined by source
 * and target nodes and edge label.
 * @version $Revision$ $Date: 2008-01-30 09:32:52 $
 */
public interface Graph {
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
    default int nodeCount() {
        return nodeSet().size();
    }

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
     * <tt>edgeSet().size()</tt>
     * @return the number of edges in this graph
     * @ensure <tt>result == edgeSet().size()</tt>
     */
    default int edgeCount() {
        return edgeSet().size();
    }

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
    Set<? extends Edge> edgeSet(Label label);

    /**
     * Returns the total number of elements (nodes plus edges) in this graph.
     * @ensure <tt>result == nodeCount() + edgeCount()</tt>
     */
    default int size() {
        return nodeCount() + edgeCount();
    }

    /**
     * Tests whether this Graph is empty (i.e., contains no Nodes or Edges).
     * @return <tt>result == nodeSet().isEmpty()</tt>
     */
    default boolean isEmpty() {
        return nodeCount() == 0;
    }

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
    Graph clone();

    /**
     * Factory method: returns a fresh, empty graph with a new name.
     * @param name the (non-{@code null}) name of the new graph.
     */
    Graph newGraph(String name);

    /** Returns the element factory used for elements of this graph. */
    ElementFactory<? extends Node,? extends Edge> getFactory();

    /** Returns the (non-{@code null}) name of this graph. */
    String getName();

    /** Returns the (non-{@code null}) role of this graph. */
    GraphRole getRole();

    /** Default name for graphs. */
    public final String NO_NAME = "nameless graph";
}
