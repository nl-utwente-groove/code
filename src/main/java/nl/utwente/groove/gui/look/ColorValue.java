/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.gui.look;

import java.awt.Color;

import nl.utwente.groove.grammar.aspect.AspectKind;
import nl.utwente.groove.grammar.aspect.AspectNode;
import nl.utwente.groove.grammar.type.TypeNode;
import nl.utwente.groove.graph.GraphRole;
import nl.utwente.groove.gui.view.AspectViewEdge;
import nl.utwente.groove.gui.view.AspectViewVertex;

/**
 * Refresher for the controlled colour value of a ViewCell.
 * @author Arend Rensink
 * @version $Revision$
 */
public class ColorValue extends AspectValue<Color> {
    @Override
    protected Color getForVertex(AspectViewVertex vertex) {
        Color result = null;
        AspectNode node = vertex.getNode();
        if (node.getGraphRole() != GraphRole.RULE) {
            if (node.hasColor()) {
                result = node.getColor();
            } else {
                TypeNode nodeType = vertex.getNodeType();
                if (nodeType != null) {
                    result = nodeType.getColor();
                }
            }
        }
        return result;
    }

    @Override
    protected Color getForEdge(AspectViewEdge edge) {
        Color result = null;
        var aspectEdge = edge.getEdge();
        if (aspectEdge != null) {
            // determine the node that determines the colour
            AspectNode node = aspectEdge.has(AspectKind.SUBTYPE)
                ? aspectEdge.target()
                : aspectEdge.source();
            var vertex = (AspectViewVertex) edge.getViewModel().getCellForNode(node);
            if (vertex != null) {
                result = getForVertex(vertex);
            }
        }
        return result;
    }
}
