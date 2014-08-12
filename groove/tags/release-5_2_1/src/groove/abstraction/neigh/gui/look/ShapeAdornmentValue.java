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

import groove.abstraction.neigh.gui.jgraph.ShapeJVertex;
import groove.abstraction.neigh.shape.Shape;
import groove.abstraction.neigh.shape.ShapeNode;
import groove.gui.jgraph.JCell;
import groove.gui.look.VisualValue;

/**
 * Returns the shape adornment 
 * @author Eduardo Zambon
 */
public class ShapeAdornmentValue implements VisualValue<String> {
    @Override
    public String get(JCell<?> cell) {
        String result = null;
        if (cell instanceof ShapeJVertex) {
            ShapeJVertex jVertex = (ShapeJVertex) cell;
            ShapeNode node = jVertex.getNode();
            result = node.getAdornment();
            Shape shape = jVertex.getJGraph().getShape();
            result += "(" + shape.getNodeMult(node) + ")";
        }
        return result;
    }
}
