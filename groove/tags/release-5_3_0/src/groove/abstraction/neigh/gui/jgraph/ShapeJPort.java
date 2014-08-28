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

import groove.abstraction.neigh.shape.EdgeSignature;

import java.awt.geom.Point2D;

import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;

/**
 * A visible JGraph port that is associated with an edge signature (bundle).
 * This departs from the usual mode of handling ports in Groove. Normally
 * a node has only one port that is not shown. For shapes, a shape node has one
 * port for each edge signature. This allows us to draw edges from a bundle
 * together.
 *
 * @author Eduardo Zambon
 */
public class ShapeJPort extends DefaultPort {

    private final boolean alwaysMovable;

    /**
     * Constructor.
     * @param vertex the vertex to add this port to.
     * @param es the edge signature associated with this port.
     * @param alwaysMovable flag to indicate if the port should always be
     *        moved or only when a main edge is involved.
     */
    public ShapeJPort(ShapeJVertex vertex, EdgeSignature es,
            boolean alwaysMovable) {
        super();
        this.alwaysMovable = alwaysMovable;
        GraphConstants.setOffset(this.getAttributes(), new Point2D.Double(0, 0));
        vertex.add(this);
        this.setUserObject(es);
    }

    /** Basic inspection method. */
    public boolean isAlwaysMovable() {
        return this.alwaysMovable;
    }

    @Override
    public EdgeSignature getUserObject() {
        return (EdgeSignature) super.getUserObject();
    }

    @Override
    public String toString() {
        return "ShapeJPort: " + this.getUserObject().toString()
            + (this.isAlwaysMovable() ? " AM" : "");
    }
}
