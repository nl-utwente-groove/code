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
package groove.graph;

import groove.graph.CanonicalEdge.Factory;
import groove.util.TreeHashSet;

/**
 * Store and factory for canonical representatives of edge types.
 * @author Arend Rensink
 * @version $Revision $
 */
public class EdgeStore<N extends Node,E extends Edge> {
    /**
     * Creates a store, given a prototype edge object to 
     * create new edges off.
     */
    public EdgeStore(Factory<N,E> factory) {
        this.factory = factory;
    }

    /**
     * Creates an default edge from a given source node, label text and target
     * node. To save space, a set of standard instances is kept internally, and
     * consulted to return the same object whenever an edge is requested with
     * the same end nodes and label text.
     * @param source the source node of the new edge; should not be
     *        <code>null</code>
     * @param text the text of the new edge; should not be <code>null</code>
     * @param target the target node of the new edge; should not be
     *        <code>null</code>
     * @return an edge based on <code>source</code>, <code>text</code> and
     *         <code>target</code>; the label is a {@link DefaultLabel}
     * @see #createEdge(Node, Label, Node)
     */
    public E createEdge(N source, String text, N target) {
        return createEdge(source, DefaultLabel.createLabel(text), target);
    }

    /**
     * Creates an default edge from a given source node, label and target node.
     * To save space, a set of standard instances is kept internally, and
     * consulted to return the same object whenever an edge is requested with
     * the same end nodes and label text.
     * @param source the source node of the new edge; should not be
     *        <code>null</code>
     * @param label for the new edge; should not be <code>null</code>
     * @param target the target node of the new edge; should not be
     *        <code>null</code>
     * @return an edge based on <code>source</code>, <code>label</code> and
     *         <code>target</code>
     * @see #createEdge(Node, String, Node)
     */
    public E createEdge(N source, Label label, N target) {
        assert source != null : "Source node of default edge should not be null";
        assert target != null : "Target node of default edge should not be null";
        assert label != null : "Label of default edge should not be null";
        E edge = this.factory.newEdge(source, label, target, getEdgeCount());
        E result = this.edgeSet.put(edge);
        if (result == null) {
            result = edge;
        }
        return result;
    }

    /**
     * Returns the total number of default edges created.
     */
    public int getEdgeCount() {
        return this.edgeSet.size();
    }

    /** Clears the store of canonical edges. */
    public void clearEdgeMap() {
        this.edgeSet.clear();
    }

    private final Factory<N,E> factory;

    /**
     * A identity map, mapping previously created instances of
     * {@link DefaultEdge} to themselves. Used to ensure that edge objects are
     * reused.
     */
    private final TreeHashSet<E> edgeSet = new TreeHashSet<E>() {
        /**
         * As {@link DefaultEdge}s test equality by object identity,
         * we need to weaken the set's equality test.
         */
        @Override
        final protected boolean areEqual(E o1, E o2) {
            return o1.source().equals(o2.source())
                && o1.target().equals(o2.target())
                && o1.label().equals(o2.label());
        }

        @Override
        final protected boolean allEqual() {
            return false;
        }
    };
}
