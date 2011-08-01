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

import groove.gui.jgraph.JEdgeView;

/**
 * View renderer for ShapeJEdges.
 * 
 * @author Eduardo Zambon
 */
public class ShapeJEdgeView extends JEdgeView {

    /**
     * Creates an edge view for a given edge, to be displayed on a given graph.
     * @param jEdge the edge underlying the view
     * @param jGraph the graph on which the edge is to be displayed
     */
    public ShapeJEdgeView(ShapeJEdge jEdge, ShapeJGraph jGraph) {
        super(jEdge, jGraph);
    }

}
