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

import org.jgraph.graph.EdgeView;

/**
 * View renderer for ShapeJEdges.
 * 
 * @author Eduardo Zambon
 */
public class ShapeJEdgeView extends EdgeView {

    /** Basic constructor, delegates to super class. */
    public ShapeJEdgeView(Object cell) {
        super(cell);
    }

    @Override
    public boolean isLoop() {
        if (this.getShapeJEdge().getSource() instanceof ShapeJPort) {
            ShapeJPort source = this.getSourcePort();
            ShapeJPort target = this.getTargetPort();
            Object sp = source.getParent();
            Object tp = target.getParent();
            return sp.equals(tp);
        } else {
            return false;
        }
    }

    /** Basic getter method. */
    public ShapeJEdge getShapeJEdge() {
        return (ShapeJEdge) this.getCell();
    }

    private ShapeJPort getSourcePort() {
        return (ShapeJPort) this.getShapeJEdge().getSource();
    }

    private ShapeJPort getTargetPort() {
        return (ShapeJPort) this.getShapeJEdge().getTarget();
    }

    /**
     * Returns true if the vertex associated with the given vertex view
     * corresponds to the source of this edge view.
     */
    public boolean isSrcVertex(ShapeJVertexView vertexView) {
        ShapeJVertex srcVertex = vertexView.getCell();
        ShapeJPort srcPort = this.getSourcePort();
        return srcVertex.equals(srcPort.getParent());
    }

    /**
     * Returns true if the vertex associated with the given vertex view
     * corresponds to the target of this edge view.
     */
    public boolean isTgtVertex(ShapeJVertexView vertexView) {
        ShapeJVertex tgtVertex = vertexView.getCell();
        ShapeJPort tgtPort = this.getTargetPort();
        return tgtVertex.equals(tgtPort.getParent());
    }

}
