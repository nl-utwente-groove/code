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

import groove.gui.jgraph.JVertexView;

/**
 * View renderer for ShapeJVertex.
 * @author Eduardo Zambon
 */
public class ShapeJVertexView extends JVertexView {

    /**
     * Creates a vertex view for a given node, to be displayed on a given graph.
     * @param jNode the node underlying the view
     * @param jGraph the graph on which the node is to be displayed
     */
    public ShapeJVertexView(ShapeJVertex jNode, ShapeJGraph jGraph) {
        super(jNode, jGraph);
    }

    /** Basic getter method. */
    @Override
    public ShapeJVertex getCell() {
        return (ShapeJVertex) super.getCell();
    }

}
