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

import groove.abstraction.neigh.shape.Shape;
import groove.gui.jgraph.AJEdge;
import groove.gui.jgraph.JModel;

/**
 * Class that connects to the JGraph library for displaying ShapeEdges.
 * Objects of this class can be flagged as the main edge for the source and/or
 * target edge bundles. A main edge is the one that is used when calculating
 * the relative position of ports on node boundaries. If a main edge is dragged,
 * the ports move accordingly.
 * 
 * @author Eduardo Zambon
 */
public class ShapeJEdge extends
        AJEdge<Shape,ShapeJGraph,ShapeJModel,ShapeJVertex> implements
        ShapeJCell {

    private boolean mainSrc;
    private boolean mainTgt;

    private ShapeJEdge() {
        // empty
    }

    @Override
    protected void initialise() {
        super.initialise();
        this.mainSrc = false;
        this.mainTgt = false;
    }

    /** Toggles the mainSrc flag. */
    public void setMainSrc(boolean mainSrc) {
        this.mainSrc = mainSrc;
    }

    /** Toggles the mainTgt flag. */
    public void setMainTgt(boolean mainTgt) {
        this.mainTgt = mainTgt;
    }

    /**
     * Checks if this edge is flagged as a main edge for a bundle of source
     * edges.
     */
    public boolean isMainSrc() {
        return this.mainSrc;
    }

    /**
     * Checks if this edge is flagged as a main edge for a bundle of target
     * edges.
     */
    public boolean isMainTgt() {
        return this.mainTgt;
    }

    /** 
     * Returns fresh, uninitialised instance.
     * Call {@link #setJModel(JModel)} to initialise.
     */
    public static ShapeJEdge newInstance() {
        return new ShapeJEdge();
    }

}
