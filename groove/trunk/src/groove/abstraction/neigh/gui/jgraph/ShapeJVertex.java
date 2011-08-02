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

import static groove.io.HTMLConverter.ITALIC_TAG;
import groove.abstraction.neigh.shape.Shape;
import groove.abstraction.neigh.shape.ShapeNode;
import groove.graph.Node;
import groove.gui.jgraph.GraphJVertex;
import groove.io.HTMLConverter;

import java.util.List;

/**
 * Class that connects to the JGraph library for displaying ShapeNodes.
 * Nodes are displayed as usual, with the addition of the multiplicity on the
 * first line of the node.
 * 
 * @author Eduardo Zambon
 */
public class ShapeJVertex extends GraphJVertex {

    // Private constructor. Use the prototype.
    private ShapeJVertex(ShapeJGraph jGraph, ShapeNode node) {
        super(jGraph, node);
        // Remove the default port that was added by the super constructor.
        this.remove(0);
    }

    @Override
    public ShapeJGraph getJGraph() {
        return (ShapeJGraph) super.getJGraph();
    }

    @Override
    public ShapeJVertex newJVertex(Node node) {
        return new ShapeJVertex(getJGraph(), (ShapeNode) node);
    }

    @Override
    public ShapeNode getNode() {
        return (ShapeNode) super.getNode();
    }

    @Override
    public List<StringBuilder> getLines() {
        List<StringBuilder> result = super.getLines();
        StringBuilder multStr = new StringBuilder();
        Shape shape = this.getJGraph().getShape();
        ShapeNode node = this.getNode();
        String mult = shape.getNodeMult(node).toString();
        multStr.append(HTMLConverter.createSpanTag("color: rgb(50,50,255)").on(
            ITALIC_TAG.on(mult)));
        result.add(0, multStr);
        return result;
    }

    /** Returns a prototype {@link ShapeJVertex} for a given {@link ShapeJGraph}. */
    public static ShapeJVertex getPrototype(ShapeJGraph jGraph) {
        return new ShapeJVertex(jGraph, null);
    }

}
