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

import groove.grammar.host.DefaultHostEdge;
import groove.grammar.host.HostEdge;
import groove.grammar.type.TypeEdge;

/**
 * Class that implements the edges of a shape.
 * This class is essentially a {@link HostEdge} and it was created mainly to
 * improve the code readability.
 * 
 * @author Eduardo Zambon
 */
public final class ShapeEdge extends DefaultHostEdge {

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** Default constructor. Delegates to super. */
    ShapeEdge(ShapeFactory factory, ShapeNode source, TypeEdge type,
            ShapeNode target, int nr) {
        super(source, type, target, nr);
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
}
