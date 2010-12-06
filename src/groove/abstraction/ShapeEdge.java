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
package groove.abstraction;

import groove.graph.DefaultEdge;
import groove.graph.EdgeStore;
import groove.graph.Label;
import groove.graph.Node;

/**
 * Class that implements the edges of a shape.
 * This class is essentially a DefaultEdge and it was created just to improve
 * the code readability.
 * 
 * @author Eduardo Zambon
 */
public final class ShapeEdge extends DefaultEdge {
    /** Default constructor. */
    private ShapeEdge(Node source, Label label, Node target, int nr) {
        super(source, label, target, nr);
    }

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    /** Factory constructor. */
    @Override
    public ShapeEdge newEdge(Node source, Label label, Node target, int nr) {
        assert source instanceof ShapeNode : "Invalid source node";
        assert target instanceof ShapeNode : "Invalid target node";
        return new ShapeEdge(source, label, target, nr);
    }

    /** Specialises the returned type. */
    @Override
    public ShapeNode source() {
        return (ShapeNode) super.source();
    }

    /** Specialises the returned type. */
    @Override
    public ShapeNode target() {
        return (ShapeNode) super.target();
    }

    /** Returns true if the edge is a loop. */
    public boolean isLoop() {
        return this.source().equals(this.target());
    }

    /**
     * Creates a shape edge from a given source node, label and target node.
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
    static public ShapeEdge createEdge(ShapeNode source, Label label,
            ShapeNode target) {
        return store.createEdge(source, label, target);
    }

    // ------------------------------------------------------------------------
    // Static Fields
    // ------------------------------------------------------------------------

    /** Used only as a reference for the constructor. */
    private static final ShapeEdge PROTOTYPE = new ShapeEdge(null, null, null,
        0);
    /** The static edge store. */
    private static final EdgeStore<ShapeNode,ShapeEdge> store =
        new EdgeStore<ShapeNode,ShapeEdge>(new Factory<ShapeNode,ShapeEdge>() {
            @Override
            public ShapeEdge newEdge(ShapeNode source, Label label,
                    ShapeNode target, int nr) {
                return PROTOTYPE.newEdge(source, label, target, nr);
            }
        });
}
