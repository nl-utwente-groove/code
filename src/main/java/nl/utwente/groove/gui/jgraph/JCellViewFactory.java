/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
 * University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * $Id$
 */
package nl.utwente.groove.gui.jgraph;

import org.jgraph.graph.DefaultCellViewFactory;
import org.jgraph.graph.VertexView;

/**
 * Cell view factory creating GROOVE's own views for the JGraph cells.
 * @author Arend Rensink
 * @version $Revision$
 */
public class JCellViewFactory extends DefaultCellViewFactory {
    /** Constructs a factory for a given JGraph. */
    public JCellViewFactory(JGraph<?> jGraph) {
        this.jGraph = jGraph;
    }

    @Override
    protected VertexView createVertexView(Object cell) {
        if (cell instanceof JVertex<?> vertex) {
            JVertexView result = new JVertexView(vertex, this.jGraph);
            // the following is apparently necessary
            // to initialise the autosize correctly
            result.refresh(this.jGraph.getGraphLayoutCache(), this.jGraph.getGraphLayoutCache(),
                           false);
            this.jGraph.updateAutoSize(result);
            return result;
        } else {
            return super.createVertexView(cell);
        }
    }

    @Override
    protected JEdgeView createEdgeView(Object edge) {
        assert edge instanceof JEdge;
        return new JEdgeView((JEdge<?>) edge, this.jGraph);
    }

    /** Returns the JGraph this factory belongs to. */
    public JGraph<?> getJGraph() {
        return this.jGraph;
    }

    private final JGraph<?> jGraph;
}
