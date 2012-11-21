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

import org.jgraph.graph.CellView;

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

    /** Basic getter method. */
    @Override
    public ShapeJEdge getCell() {
        return (ShapeJEdge) super.getCell();
    }

    @Override
    public String toString() {
        return "ShapeJEdgeView for " + this.getCell().toString();
    }

    @Override
    protected boolean isSelfEdge() {
        //return this.getCell().isLoop();
        return false;
    }

    /** Basic getter method. */
    public ShapeJPort getSourcePort() {
        return (ShapeJPort) this.getCell().getSource();
    }

    /** Basic getter method. */
    public ShapeJPort getTargetPort() {
        return (ShapeJPort) this.getCell().getTarget();
    }

    /**
     * Returns true if the vertex associated with the given vertex view
     * corresponds to the source of this edge view.
     */
    public boolean isSrcVertex(CellView vertexView) {
        ShapeJVertex srcVertex = (ShapeJVertex) vertexView.getCell();
        ShapeJPort srcPort = this.getSourcePort();
        return srcVertex.equals(srcPort.getParent());
    }

    /**
     * Returns true if the vertex associated with the given vertex view
     * corresponds to the target of this edge view.
     */
    public boolean isTgtVertex(CellView vertexView) {
        ShapeJVertex tgtVertex = (ShapeJVertex) vertexView.getCell();
        ShapeJPort tgtPort = this.getTargetPort();
        return tgtVertex.equals(tgtPort.getParent());
    }

}
