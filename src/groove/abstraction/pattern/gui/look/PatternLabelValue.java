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
package groove.abstraction.pattern.gui.look;

import groove.abstraction.pattern.gui.jgraph.PatternJEdge;
import groove.abstraction.pattern.gui.jgraph.PatternJGraph;
import groove.abstraction.pattern.gui.jgraph.PatternJVertex;
import groove.abstraction.pattern.shape.AbstractPatternEdge;
import groove.abstraction.pattern.shape.PatternEdge;
import groove.abstraction.pattern.shape.PatternShape;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.gui.jgraph.JVertex;
import groove.gui.jgraph.JEdge;
import groove.gui.look.LabelValue;
import groove.gui.look.Line;
import groove.gui.look.MultiLabel;

/**
 * Label value refresher for pattern graphs.
 * @author Arend
 * @version $Revision $
 */
public class PatternLabelValue extends LabelValue {
    /** Constructs an instance for a given JGraph. */
    public PatternLabelValue(PatternJGraph jGraph) {
        super(jGraph);
    }

    @Override
    protected MultiLabel getJVertexLabel(JVertex<?> jVertex) {
        if (((PatternJVertex) jVertex).isOuter()) {
            return new MultiLabel();
        } else {
            return super.getJVertexLabel(jVertex);
        }
    }

    @Override
    protected MultiLabel getJEdgeLabel(JEdge<?> jEdge) {
        PatternJEdge myJEdge = (PatternJEdge) jEdge;
        if (myJEdge.isOuter()) {
            return getPatternJEdgeLabel(myJEdge);
        } else {
            return super.getJEdgeLabel(jEdge);
        }
    }

    private MultiLabel getPatternJEdgeLabel(PatternJEdge jEdge) {
        MultiLabel result = new MultiLabel();
        for (Edge edge : jEdge.getEdges()) {
            StringBuilder sb = new StringBuilder();
            sb.append(((AbstractPatternEdge<?>) edge).getPrintableLabel());
            Graph<?,?> graph = getJGraph().getModel().getGraph();
            if (graph instanceof PatternShape) {
                PatternShape pShape = (PatternShape) graph;
                sb.append("(" + pShape.getMult((PatternEdge) edge) + ")");
            }
            result.add(Line.atom(sb.toString()), jEdge.getDirect(edge));
        }
        return result;
    }
}
