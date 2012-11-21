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
package groove.abstraction.neigh.gui.jgraph;

import groove.gui.jgraph.GraphJVertex;
import groove.gui.look.Look;

/**
 * Class that connects to the JGraph library for displaying equivalence classes.
 * 
 * @author Eduardo Zambon
 */
public class EcJVertex extends GraphJVertex {

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    // Private constructor. Use the prototype.
    private EcJVertex() {
        setLayoutable(true);
    }

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    @Override
    public ShapeJGraph getJGraph() {
        return (ShapeJGraph) super.getJGraph();
    }

    @Override
    public String toString() {
        return String.format("EcJVertex %d with labels %s", getNumber(),
            getKeys());
    }

    // ------------------------------------------------------------------------
    // Static methods and fields
    // ------------------------------------------------------------------------

    @Override
    protected Look getStructuralLook() {
        return Look.EQUIV_CLASS;
    }

    /** Returns a new instance of this class. */
    public static EcJVertex newInstance() {
        return new EcJVertex();
    }
}
