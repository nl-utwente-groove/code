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
package groove.abstraction.neigh.shape;

import groove.abstraction.neigh.Multiplicity.EdgeMultDir;
import groove.graph.TypeLabel;
import groove.trans.HostEdge;

/**
 * Class that implements the edges of a shape.
 * This class is essentially a {@link HostEdge} and it was created just to improve
 * the code readability.
 * 
 * @author Eduardo Zambon
 */
public final class ShapeEdge extends HostEdge {

    /** Default constructor. */
    ShapeEdge(ShapeFactory factory, ShapeNode source, TypeLabel label,
            ShapeNode target, int nr) {
        super(factory, source, label, target, nr);
    }

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

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

    /**
     * If the given direction is OUTGOING, returns the source of this edge.
     * If the given direction is INCOMING, returns the target of this edge.
     */
    public ShapeNode incident(EdgeMultDir direction) {
        ShapeNode incident = null;
        switch (direction) {
        case OUTGOING:
            incident = this.source();
            break;
        case INCOMING:
            incident = this.target();
            break;
        default:
            assert false;
        }
        return incident;
    }

    /**
     * If the given direction is OUTGOING, returns the target of this edge.
     * If the given direction is INCOMING, returns the source of this edge.
     */
    public ShapeNode opposite(EdgeMultDir direction) {
        ShapeNode opposite = null;
        switch (direction) {
        case OUTGOING:
            opposite = this.target();
            break;
        case INCOMING:
            opposite = this.source();
            break;
        default:
            assert false;
        }
        return opposite;
    }

}