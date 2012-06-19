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

import org.jgraph.graph.DefaultCellViewFactory;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.PortView;
import org.jgraph.graph.VertexView;

/**
 * EDUARDO: Comment this...
 * @author Eduardo Zambon
 */
public class ShapeJCellViewFactory extends DefaultCellViewFactory {

    @Override
    protected PortView createPortView(Object cell) {
        assert cell instanceof ShapeJPort;
        return new ShapeJPortView(cell);
    }

    @Override
    protected EdgeView createEdgeView(Object cell) {
        assert cell instanceof ShapeJEdge;
        return new ShapeJEdgeView(cell);
    }

    @Override
    protected VertexView createVertexView(Object cell) {
        if (cell instanceof ShapeJVertex) {
            return new ShapeJVertexView(cell);
        } else {
            return super.createVertexView(cell);
        }
    }

}