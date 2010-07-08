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
 * $Id: DefaultEdge.java,v 1.15 2008-02-12 15:15:31 fladder Exp $
 */
package groove.graph;

import groove.abstraction.ShapeEdge;
import groove.abstraction.ShapeNode;
import groove.util.TreeHashSet;

/**
 * Default implementation of an (immutable) graph edge, as a triple consisting
 * of source and target nodes and an arbitrary label.
 * @author Arend Rensink
 * @version $Revision$ $Date: 2008-02-12 15:15:31 $
 */
public class DefaultEdge extends AbstractBinaryEdge<Node,Label,Node> {

    /**
     * Constructs a new edge on the basis of a given source, label and target.
     * @param source source node of the new edge
     * @param label label of the new edge
     * @param target target node of the new edge
     * @require <tt>source != null && target != null</tt>
     * @ensure <tt>source()==source</tt>, <tt>label()==label</tt>,
     *         <tt>target()==target </tt>
     */
    protected DefaultEdge(Node source, Label label, Node target, int nr) {
        super(source, label, target);
        //        assert label.isBinary() || source.equals(target) : String.format(
        //            "Unary edge label %s for binary edge between %s and %s",
        //            DefaultLabel.toPrefixedString(label), source, target);
        this.nr = nr;
    }

    /** This is just a factory constructor so we can have a reference for an
     *  object of this class.
     */
    protected DefaultEdge() {
        super(null, null, null);
        this.nr = -1;
    }

    /** Factory constructor. */
    public DefaultEdge newEdge(Node source, Label label, Node target, int nr) {
        return new DefaultEdge(source, label, target, nr);
    }

    /**
     * For efficiency, this implementation tests for object equality. It is,
     * however, considered an error if two distinct {@link DefaultEdge} objects
     * have the same source and target nodes and the same label.
     */
    @Override
    public boolean equals(Object obj) {
        boolean result = this == obj;
        assert result || !(obj instanceof DefaultEdge) || !super.equals(obj) : String.format(
            "Distinct edges with same appearance (%s)", toString());
        return result;
    }

    /**
     * For efficiency, this implementation uses the identity hash code of the
     * object.
     */
    @Override
    protected int computeHashCode() {
        return System.identityHashCode(this);
    }

    /**
     * Returns a fixed sequence number for the edge. The numbers are chosen
     * consecutively and uniquely identify the edge.
     */
    public int getNumber() {
        return this.nr;
    }

    /**
     * The fixed edge number. This is guaranteed to be unique for all
     * {@link DefaultEdge}s.
     */
    final int nr;

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
    static public DefaultEdge createEdge(Node source, String text, Node target) {
        return DefaultEdge.createEdge(source, DefaultLabel.createLabel(text),
            target);
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
     * @param cons is an object that defines the constructor. Subclasses of
     *             <code>DefaultEdge</code> may override the method
     *             {@link #newEdge(Node, Label, Node, int)} so that the factory
     *             creates nodes with a more specialized type.
     *             See, e.g., <code>ShapeEdge</code>.
     * @return an edge based on <code>source</code>, <code>label</code> and
     *         <code>target</code>
     * @see #createEdge(Node, String, Node)
     */
    static public DefaultEdge createEdge(Node source, Label label, Node target,
            DefaultEdge cons) {
        assert source != null : "Source node of default edge should not be null";
        assert target != null : "Target node of default edge should not be null";
        assert label != null : "Label of default edge should not be null";
        DefaultEdge edge = cons.newEdge(source, label, target, edgeSet.size());
        DefaultEdge result = DefaultEdge.edgeSet.put(edge);
        if (result == null) {
            result = edge;
        }
        return result;
    }

    /** Default method that uses the DefaultEdge constructor. */
    static public DefaultEdge createEdge(Node source, Label label, Node target) {
        // EDUARDO says: very ugly hack here.
        // Sorry about this, need to solve this fast... :P
        // Begin HACK 
        if (source instanceof ShapeNode && target instanceof ShapeNode) {
            return createEdge(source, label, target, ShapeEdge.CONS);
        }
        // End HACK
        return createEdge(source, label, target, CONS);
    }

    /**
     * Returns the total number of default edges created.
     */
    static public int getEdgeCount() {
        return edgeSet.size();
    }

    /** Clears the store of canonical edges. */
    static public void clearEdgeMap() {
        edgeSet.clear();
    }

    /**
     * A identity map, mapping previously created instances of
     * {@link DefaultEdge} to themselves. Used to ensure that edge objects are
     * reused.
     */
    static private final TreeHashSet<DefaultEdge> edgeSet =
        new TreeHashSet<DefaultEdge>() {
            @Override
            final protected boolean areEqual(DefaultEdge o1, DefaultEdge o2) {
                return o1.source().equals(o2.source())
                    && o1.target().equals(o2.target())
                    && o1.label().equals(o2.label());
            }

            @Override
            final protected int getCode(DefaultEdge key) {
                return key.edgeHashCode();
            }

            @Override
            final protected boolean allEqual() {
                return false;
            }
        };

    /** Used only as a reference for the constructor */
    public static final DefaultEdge CONS = new DefaultEdge();
}
