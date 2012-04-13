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
import groove.graph.Label;
import groove.graph.Node;

/**
 * Class that implements the edges of a shape.
 * This class is essentially a DefaultEdge and it was created just to improve
 * the code readability.
 * 
 * @author Eduardo Zambon
 */
public class ShapeEdge extends DefaultEdge {

    // ------------------------------------------------------------------------
    // Static Fields
    // ------------------------------------------------------------------------

    /** Used only as a reference for the constructor. */
    public static final ShapeEdge CONS = new ShapeEdge();

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** Default constructor. */
    protected ShapeEdge(Node source, Label label, Node target, int nr) {
        super(source, label, target, nr);
    }

    /** 
     * This is just a factory constructor so we can have a reference for an
     * object of this class.
     */
    private ShapeEdge() {
        super();
    }

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    /** Factory constructor. */
    @Override
    public DefaultEdge newEdge(Node source, Label label, Node target, int nr) {
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
    public ShapeNode opposite() {
        return (ShapeNode) super.opposite();
    }

}