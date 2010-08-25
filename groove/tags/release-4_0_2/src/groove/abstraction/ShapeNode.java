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

import groove.graph.DefaultNode;

/**
 * Class that implements the nodes of a shape.
 * This class is essentially a DefaultNode and it was created just to improve
 * the code readability.
 * 
 * @author Eduardo Zambon
 */
public class ShapeNode extends DefaultNode {

    // ------------------------------------------------------------------------
    // Static Fields
    // ------------------------------------------------------------------------

    /** Used only as a reference for the constructor. */
    public static final ShapeNode CONS = new ShapeNode(NO_NODE_NUMBER);

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** Default constructor. */
    protected ShapeNode(int nr) {
        super(nr);
    }

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    /** Factory constructor. */
    @Override
    public DefaultNode newNode(int nr) {
        return new ShapeNode(nr);
    }

}
