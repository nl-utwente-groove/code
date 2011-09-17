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

import groove.graph.TypeNode;
import groove.trans.DefaultHostNode;

/**
 * Class that implements the nodes of a shape.
 * This class is essentially a {@link DefaultHostNode} and it was created just 
 * to improve the code readability.
 * 
 * @author Eduardo Zambon
 */
public final class ShapeNode extends DefaultHostNode {
    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** Default constructor. */
    ShapeNode(int nr, TypeNode type) {
        super(nr, type);
    }

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    /** Factory constructor. */
    @Override
    public ShapeNode newNode(int nr, TypeNode type) {
        return new ShapeNode(nr, type);
    }
}
