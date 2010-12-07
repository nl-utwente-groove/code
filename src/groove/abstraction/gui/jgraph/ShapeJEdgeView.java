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
package groove.abstraction.gui.jgraph;

import org.jgraph.graph.EdgeView;

/**
 * EDUARDO: Comment this...
 * @author Eduardo Zambon
 */
public class ShapeJEdgeView extends EdgeView {

    /**
     * EDUARDO: Comment this...
     */
    public ShapeJEdgeView(Object cell) {
        super(cell);
    }

    @Override
    public boolean isLoop() {
        ShapeJPort source = this.getSourcePort();
        ShapeJPort target = this.getTargetPort();
        Object sp = source.getParent();
        Object tp = target.getParent();
        return sp.equals(tp);
    }

    /**
     * EDUARDO: Comment this...
     */
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
     * EDUARDO: Comment this...
     */
    public boolean isSrcVertex(ShapeJVertexView vertexView) {
        ShapeJVertex srcVertex = vertexView.getShapeJVertex();
        ShapeJPort srcPort = this.getSourcePort();
        return srcVertex.equals(srcPort.getParent());
    }

    /**
     * EDUARDO: Comment this...
     */
    public boolean isTgtVertex(ShapeJVertexView vertexView) {
        ShapeJVertex tgtVertex = vertexView.getShapeJVertex();
        ShapeJPort tgtPort = this.getTargetPort();
        return tgtVertex.equals(tgtPort.getParent());
    }

}
