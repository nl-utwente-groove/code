/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
package groove.abstraction.neigh.gui.look;

import groove.abstraction.neigh.gui.jgraph.ShapeJGraph;
import groove.abstraction.neigh.gui.jgraph.ShapeJVertex;
import groove.abstraction.neigh.shape.Shape;
import groove.abstraction.neigh.shape.ShapeNode;
import groove.graph.TypeNode;
import groove.gui.Options;
import groove.gui.jgraph.GraphJVertex;
import groove.gui.look.LabelValue;
import groove.gui.look.Line;
import groove.gui.look.Line.Style;
import groove.gui.look.MultiLabel;
import groove.util.Colors;

/**
 * Label value refresher for pattern graphs.
 * @author Arend
 * @version $Revision $
 */
public class ShapeLabelValue extends LabelValue {
    /** Constructs an instance for a given JGraph. */
    public ShapeLabelValue(ShapeJGraph jGraph) {
        super(jGraph);
    }

    @Override
    protected MultiLabel getJVertexLabel(GraphJVertex jVertex) {
        MultiLabel result = super.getJVertexLabel(jVertex);
        // Multiplicity.
        Shape shape = ((ShapeJGraph) getJGraph()).getShape();
        ShapeNode node = ((ShapeJVertex) jVertex).getNode();
        String mult = shape.getNodeMult(node).toString();
        Line multLine =
            Line.atom(mult).style(Style.ITALIC).color(
                Colors.findColor("50,50,255"));
        result.add(0, multLine);
        // Node type.
        TypeNode typeNode = node.getType();
        if (!typeNode.isTopType()) {
            Line typeLine =
                Line.atom(typeNode.label().text()).style(Style.BOLD);
            int pos;
            if (getOptionValue(Options.SHOW_NODE_IDS_OPTION)) {
                pos = 2;
            } else {
                pos = 1;
            }
            result.add(pos, typeLine);
        }
        return result;
    }
}
