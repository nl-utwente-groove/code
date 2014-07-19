/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
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
package groove.gui.jgraph;

import org.jgraph.graph.DefaultCellViewFactory;
import org.jgraph.graph.VertexView;

/**
 * Implementation of a cell view factory that returns {@link JVertexView} en
 * {@link JEdgeView} -objects. For this purpose, the graph on which the views
 * are to be displayed is stored in the factory.
 * @author Arend Rensink
 * @version $Revision$
 */
public class JCellViewFactory extends DefaultCellViewFactory {
    /**
     * Constructs a factory for creating views upon a particular {@link JGraph}.
     * @param jGraph the graph on which the views are to be displayed.
     */
    public JCellViewFactory(JGraph<?> jGraph) {
        this.jGraph = jGraph;
    }

    /**
     * This implementation creates {@link JVertexView} if the cell to be viewed
     * is a {@link JVertex}. Otherwise, the method delegates to the super
     * class.
     */
    @Override
    protected VertexView createVertexView(Object cell) {
        if (cell instanceof JVertex) {
            JVertexView result =
                new JVertexView((JVertex<?>) cell, this.jGraph);
            // the following is apparently necessary
            // to initialise the autosize correctly
            result.refresh(this.jGraph.getGraphLayoutCache(),
                this.jGraph.getGraphLayoutCache(), false);
            this.jGraph.updateAutoSize(result);
            return result;
        } else {
            return super.createVertexView(cell);
        }
    }

    /**
     * This implementation creates {@link JEdgeView} if the cell to be viewed is
     * a {@link JEdge}. Otherwise, the method delegates to the super class.
     */
    @Override
    protected JEdgeView createEdgeView(Object edge) {
        assert edge instanceof JEdge;
        return new JEdgeView((JEdge<?>) edge, this.jGraph);
    }

    /** Basic getter method. */
    public JGraph<?> getJGraph() {
        return this.jGraph;
    }

    /**
     * The underlying graph on which all views are to be displayed.
     */
    private final JGraph<?> jGraph;
}
