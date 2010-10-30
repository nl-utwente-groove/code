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
 * $Id: Edge.java,v 1.7 2008-01-30 09:32:52 iovka Exp $
 */
package groove.graph;

/**
 * Interface of a graph (hyper-)edge, with endpoints (i.e., nodes) and label.
 * There is at least one endpoint.
 * @author Arend Rensink
 * @version $Revision$
 */
public interface Edge extends Element {
    /**
     * Yields the endpoints of this edge, i.e., the graph nodes that the edge
     * depends on. The ordering of the ends is fixed for each type of graph
     * element; in particular, the source node always comes first (as determined
     * by <tt>SOURCE_INDEX</tt>.
     * @return an array of the endpoints of this edge
     * @ensure <tt>result</tt> does not contain <tt>null</tt> values
     * @deprecated no longer necessary since all edges are binary
     */
    @Deprecated
    public Node[] ends();

    /**
     * Returns the endpoint at a given index.
     * @param i the index of the requested endpoint
     * @return the endpoint at index <tt>i</tt>
     * @see #ends()
     * @require <tt>0 <= i && i <= endCount()</tt>
     * @ensure <tt>result != null && result == ends()[i]</tt>
     * @deprecated use {@link #source()} or {@link #target()} instead
     */
    @Deprecated
    public Node end(int i);

    /**
     * Yields the number of ends of this edge. The number of ends is always
     * positive.
     * @return The number of endpoints of this edge
     * @see #ends()
     * @ensure <tt>result >= 1 && result == ends().length</tt>
     * @deprecated no longer necessary since all edges are binary
     */
    @Deprecated
    public int endCount();

    /**
     * Returns the first end index of a given node in this composite, or
     * <tt>-1</tt> if the node is not an end of this composite. For instance,
     * the index of the source node is {@link #SOURCE_INDEX}.
     * @param node the potential endpoint
     * @return <tt>-1</tt> iff <tt>end</tt> is an endpoint of this edge
     * @require <tt>node != null</tt>
     * @ensure <tt>result == -1 || end(result).equals(node)</tt>
     * @see #end(int)
     * @deprecated test against source or target
     */
    @Deprecated
    public int endIndex(Node node);

    /**
     * Tests if this edge has a given endpoint. Convenience method for
     * <tt>endIndex(node) >= 0</tt>.
     * @param node the potential endpoint
     * @return <tt>true</tt> iff <tt>end</tt> is an endpoint of this edge
     * @require <tt>node != null</tt>
     * @ensure <tt>result </tt> holds if <tt>end(i).equals(end)</tt> for some
     *         <tt>i</tt>
     * @see #endIndex(Node)
     * @deprecated test against source or target
     */
    @Deprecated
    public boolean hasEnd(Node node);

    /**
     * Returns the source node of this edge. The source node is the first end of
     * the edge (i.e., with index <tt>SOURCE_INDEX</tt>).
     * @return the source node of this edge
     * @see #ends()
     * @see #SOURCE_INDEX
     * @ensure <tt>result == ends(SOURCE_INDEX)</tt>
     */
    public Node source();

    /**
     * Returns the end of this edge that is considered to be the "opposite" of
     * the source when the composite is used as a binary relation. Which end
     * this is depends on the edge: for a unary edge it will be the source, for
     * a binary edge typically the target.
     * @return the node "opposite" the source
     * @see #ends()
     * @deprecated use {@link #target()} instead
     */
    @Deprecated
    public Node opposite();

    /**
     * Returns the target node of this edge. The target node has index
     * {@link #TARGET_INDEX}.
     * @return the target node of this edge
     * @see #TARGET_INDEX
     * @see #end(int)
     * @ensure <tt>result != null && result == ends(TARGET_INDEX)</tt>
     */
    public Node target();

    /**
     * Returns the label of this edge. The label can never be <tt>null</tt>.
     * @return the label of this edge
     * @ensure <tt>result != null</tt>
     */
    public Label label();

    /**
     * The index (in the edge ends) of the edge source.
     * @see #source()
     */
    @Deprecated
    int SOURCE_INDEX = 0;
    /**
     * The endpoint index of the target node.
     */
    @Deprecated
    int TARGET_INDEX = 1;
    /** The number of ends of a binary edge.
     * @deprecated no longer necessary since all edges are binary 
     */
    @Deprecated
    static public final int END_COUNT = 2;
}