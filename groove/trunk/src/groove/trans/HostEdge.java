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
package groove.trans;

import groove.graph.DefaultEdge;
import groove.graph.DefaultEdgeStore;
import groove.graph.Label;
import groove.graph.Node;
import groove.graph.TypeLabel;

/**
 * Class that implements the edges of a shape.
 * This class is essentially a DefaultEdge and it was created just to improve
 * the code readability.
 * 
 * @author Eduardo Zambon
 */
public class HostEdge extends DefaultEdge {
    /** Default constructor. */
    protected HostEdge(HostNode source, TypeLabel label, HostNode target, int nr) {
        super(source, label, target, nr);
    }

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    /** Factory constructor. */
    @Override
    protected HostEdge newEdge(Node source, Label label, Node target, int nr) {
        assert source instanceof HostNode : "Invalid source node";
        assert target instanceof HostNode : "Invalid target node";
        assert label instanceof TypeLabel : "Invalid target node";
        return new HostEdge((HostNode) source, (TypeLabel) label,
            (HostNode) target, nr);
    }

    /** Specialises the returned type. */
    @Override
    public HostNode source() {
        return (HostNode) super.source();
    }

    /** Specialises the returned type. */
    @Override
    public HostNode target() {
        return (HostNode) super.target();
    }

    /** Specialises the return type. */
    @Override
    public TypeLabel label() {
        return (TypeLabel) super.label();
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
    static public HostEdge createEdge(HostNode source, TypeLabel label,
            HostNode target) {
        return (HostEdge) store.createEdge(source, label, target);
    }

    // ------------------------------------------------------------------------
    // Static Fields
    // ------------------------------------------------------------------------

    /** Used only as a reference for the constructor. */
    private static final HostEdge PROTOTYPE = new HostEdge(null, null, null, 0);
    /** The static edge store. */
    private static final DefaultEdgeStore store = new DefaultEdgeStore(
        PROTOTYPE);
}
