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
package groove.abstraction.pattern.gui.jgraph;

import groove.gui.jgraph.GraphJGraph;
import groove.gui.jgraph.JCellViewFactory;
import groove.gui.jgraph.JEdgeView;

import org.jgraph.graph.VertexView;

/**
 * Cell view factory that dispatches to the proper view constructors
 * depending on the given object.
 * 
 * @author Eduardo Zambon
 */
public class PatternJCellViewFactory extends JCellViewFactory {

    /**
     * Constructs a factory for creating views upon a particular {@link GraphJGraph}.
     * @param jGraph the graph on which the views are to be displayed.
     */
    public PatternJCellViewFactory(PatternJGraph jGraph) {
        super(jGraph);
    }

    @Override
    public PatternJGraph getJGraph() {
        return (PatternJGraph) super.getJGraph();
    }

    @Override
    protected JEdgeView createEdgeView(Object cell) {
        if (cell instanceof PatternJEdge) {
            return new PatternJEdgeView((PatternJEdge) cell, this.getJGraph());
        } else {
            return super.createEdgeView(cell);
        }
    }

    @Override
    protected VertexView createVertexView(Object cell) {
        if (cell instanceof PatternJVertex) {
            /*return new PatternJVertexView((PatternJVertex) cell,
                this.getJGraph());*/
            return new PatternJVertexView((PatternJVertex) cell);
        } else {
            return super.createVertexView(cell);
        }
    }

}
