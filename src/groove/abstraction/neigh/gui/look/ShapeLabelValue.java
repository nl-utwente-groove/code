/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
package groove.abstraction.neigh.gui.look;

import static groove.abstraction.neigh.EdgeMultDir.INCOMING;
import static groove.abstraction.neigh.EdgeMultDir.OUTGOING;
import groove.abstraction.neigh.gui.jgraph.EcJVertex;
import groove.abstraction.neigh.gui.jgraph.ShapeJEdge;
import groove.abstraction.neigh.gui.jgraph.ShapeJGraph;
import groove.abstraction.neigh.shape.Shape;
import groove.abstraction.neigh.shape.ShapeEdge;
import groove.graph.Edge;
import groove.gui.jgraph.JVertex;
import groove.gui.jgraph.JEdge;
import groove.gui.look.LabelValue;
import groove.gui.look.MultiLabel;
import groove.util.line.Line;

/**
 * Label value refresher for neighbourhood shapes.
 * @author Arend
 * @version $Revision $
 */
public class ShapeLabelValue extends LabelValue {
    /** Constructs an instance for a given JGraph. */
    public ShapeLabelValue(ShapeJGraph jGraph) {
        super(jGraph);
    }

    @Override
    protected MultiLabel getJVertexLabel(JVertex<?> jVertex) {
        if (jVertex instanceof EcJVertex) {
            return new MultiLabel();
        } else {
            return super.getJVertexLabel(jVertex);
        }
    }

    @Override
    protected MultiLabel getJEdgeLabel(JEdge<?> jEdge) {
        MultiLabel result = new MultiLabel();
        assert jEdge instanceof ShapeJEdge;
        Shape shape = ((ShapeJEdge) jEdge).getJGraph().getShape();
        for (Edge edge : jEdge.getEdges()) {
            ShapeEdge e = (ShapeEdge) edge;
            StringBuilder sb = new StringBuilder();
            sb.append(shape.getEdgeMult(e, OUTGOING) + ":" + e.label() + ":"
                + shape.getEdgeMult(e, INCOMING));
            result.add(Line.atom(sb.toString()), jEdge.getDirect(edge));
        }
        return result;
    }
}
