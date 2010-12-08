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

import groove.abstraction.Shape;
import groove.abstraction.ShapeEdge;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.Port;

/**
 * EDUARDO: Comment this...
 * @author Eduardo Zambon
 */
public class ShapeJEdge extends DefaultEdge {

    private boolean mainSrc;
    private boolean mainTgt;

    /**
     * EDUARDO: Comment this...
     */
    public ShapeJEdge(Shape shape, ShapeEdge edge, Port source, Port target) {
        super(edge.label().text());
        assert source instanceof ShapeJPort && target instanceof ShapeJPort;
        this.mainSrc = false;
        this.mainTgt = false;
        this.setSource(source);
        this.setTarget(target);
        this.setAttributes();
    }

    /**
     * EDUARDO: Comment this...
     */
    public ShapeJEdge(Shape shape, ShapeEdge edge, ShapeJVertex source,
            ShapeJVertex target) {
        super(edge.label().text());
        this.mainSrc = false;
        this.mainTgt = false;
        this.setSource(source);
        this.setTarget(target);
        this.setAttributes();
    }

    private void setAttributes() {
        AttributeMap attrMap = this.getAttributes();
        GraphConstants.setLineEnd(attrMap, GraphConstants.ARROW_CLASSIC);
        GraphConstants.setEndFill(attrMap, true);
    }

    /**
     * EDUARDO: Comment this...
     */
    public void setMainSrc(boolean mainSrc) {
        this.mainSrc = mainSrc;
    }

    /**
     * EDUARDO: Comment this...
     */
    public void setMainTgt(boolean mainTgt) {
        this.mainTgt = mainTgt;
    }

    /**
     * EDUARDO: Comment this...
     */
    public boolean isMainSrc() {
        return this.mainSrc;
    }

    /**
     * EDUARDO: Comment this...
     */
    public boolean isMainTgt() {
        return this.mainTgt;
    }

}
