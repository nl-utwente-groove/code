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
package groove.gui.look;

import groove.graph.GraphRole;
import groove.graph.TypeGraph;
import groove.graph.TypeNode;
import groove.gui.jgraph.AspectJEdge;
import groove.gui.jgraph.AspectJVertex;
import groove.gui.jgraph.GraphJVertex;
import groove.view.aspect.AspectEdge;
import groove.view.aspect.AspectNode;

import java.awt.Color;

/**
 * Refresher for the controlled colour value of a JCell.
 * @author Arend Rensink
 * @version $Revision $
 */
public class ColorValue extends AspectValue<Color> {
    @Override
    protected Color getForJVertex(AspectJVertex jVertex) {
        Color result = null;
        AspectNode node = jVertex.getNode();
        if (node.getGraphRole() != GraphRole.RULE) {
            if (node.getColor() != null) {
                result = (Color) node.getColor().getContent();
            } else {
                TypeNode nodeType = getNodeType(jVertex);
                if (nodeType != null) {
                    result = nodeType.getColor();
                }
            }
        }
        return result;
    }

    @Override
    protected Color getForJEdge(AspectJEdge jEdge) {
        GraphJVertex source = jEdge.getSourceVertex();
        // just after jEdge creation, the source may not yet be there
        return source == null ? null : source.getVisuals().getColor();
    }

    /** 
     * Retrieves the node type corresponding to the node type label,
     * if the type graph is not implicit. 
     */
    private TypeNode getNodeType(AspectJVertex jVertex) {
        TypeNode result = null;
        TypeGraph typeGraph = jVertex.getJModel().getTypeGraph();
        for (AspectEdge edge : jVertex.getEdges()) {
            if (typeGraph.isNodeType(edge)) {
                result = typeGraph.getNode(edge.getTypeLabel());
                break;
            }
        }
        return result;
    }

}
