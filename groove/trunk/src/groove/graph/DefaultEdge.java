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

/**
 * Default implementation of an (immutable) graph edge, as a triple consisting
 * of source and target nodes and an arbitrary label.
 * @author Arend Rensink
 * @version $Revision$ $Date: 2008-02-12 15:15:31 $
 */
public class DefaultEdge extends AbstractEdge<Node,Label,Node> {
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
        this.nr = nr;
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = this == obj;
        // test that the result is the same as number equality
        // or source-label-target equality
        assert result == (obj instanceof DefaultEdge && this.nr == ((DefaultEdge) obj).nr) : String.format(
            "Distinct %s and %s %s with the same number %d",
            getClass().getName(), obj.getClass().getName(), this, this.nr);
        assert result == (obj instanceof DefaultEdge && super.equals(obj)) : String.format(
            "Distinct %s and %s %s with the same content",
            getClass().getName(), obj.getClass().getName(), this);
        return result;
    }

    /** 
     * Returns the number of this edge.
     * The number is guaranteed to be unique for each canonical edge representative.
     */
    public int getNumber() {
        return this.nr;
    }

    /** The (unique) number of this edge. */
    private final int nr;

    /** Factory constructor. */
    protected DefaultEdge newEdge(Node source, Label label, Node target, int nr) {
        return new DefaultEdge(source, label, target, nr);
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
    static public Edge createEdge(Node source, String text, Node target) {
        return DefaultEdge.createEdge(source, DefaultLabel.createLabel(text),
            target);
    }

    /** Default method that uses the DefaultEdge constructor. */
    static public Edge createEdge(Node source, Label label, Node target) {
        // EDUARDO says: very ugly hack here.
        // Sorry about this, need to solve this fast... :P
        // Begin HACK 
        if (source instanceof ShapeNode && target instanceof ShapeNode) {
            return ShapeEdge.createEdge((ShapeNode) source, label,
                (ShapeNode) target);
        }
        // End HACK
        return store.createEdge(source, label, target);
    }

    /**
     * Returns the total number of default edges created.
     */
    static public int getEdgeCount() {
        return store.getEdgeCount();
    }

    /** Clears the store of canonical edges. */
    static public void clearEdgeMap() {
        store.clearEdgeMap();
    }

    /** Used only as a reference for the constructor */
    private static final DefaultEdge CONS =
        new DefaultEdge(null, null, null, 0);
    /** The static edge store. */
    private static final DefaultEdgeStore store = new DefaultEdgeStore(CONS);
}
