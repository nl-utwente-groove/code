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

import groove.graph.Node;
import groove.graph.NodeFactory;

/**
 * A factory object for creating nodes for shapes.
 *  
 * @author Eduardo Zambon
 */
public final class ShapeNodeFactory implements NodeFactory {

    // ------------------------------------------------------------------------
    // Static Fields
    // ------------------------------------------------------------------------

    /** The factory reference. */
    public static final ShapeNodeFactory FACTORY = new ShapeNodeFactory();

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    private ShapeNodeFactory() {
        // Does nothing by design.
    }

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    /** Returns a new ShapeNode object. */
    @Override
    public Node newNode() {
        return ShapeNode.createNode();
    }

}
