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

import groove.abstraction.neigh.shape.Shape;
import groove.abstraction.neigh.shape.ShapeNode;
import groove.gui.jgraph.JVertex;

/**
 * Class that connects to the JGraph library for displaying ShapeNodes.
 * Nodes are displayed as usual, with the addition of the multiplicity on the
 * first line of the node.
 * 
 * @author Eduardo Zambon
 */
public class ShapeJVertex extends JVertex<Shape> implements ShapeJCell {
    // Private constructor. Use the prototype.
    private ShapeJVertex() {
        // Remove the default port that was added by the super constructor.
        this.remove(0);
    }

    @Override
    public ShapeJGraph getJGraph() {
        return (ShapeJGraph) super.getJGraph();
    }

    @Override
    public String toString() {
        return "ShapeJVertex: " + this.getNode().toString();
    }

    @Override
    public ShapeNode getNode() {
        return (ShapeNode) super.getNode();
    }

    /** Returns a prototype {@link ShapeJVertex}. */
    @SuppressWarnings("unchecked")
    public static ShapeJVertex newInstance() {
        return new ShapeJVertex();
    }
}
