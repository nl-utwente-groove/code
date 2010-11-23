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
        ShapeJEdge edge = (ShapeJEdge) this.getCell();
        ShapeJPort source = (ShapeJPort) edge.getSource();
        ShapeJPort target = (ShapeJPort) edge.getTarget();
        Object sp = source.getParent();
        Object tp = target.getParent();
        return sp.equals(tp);
    }

}
