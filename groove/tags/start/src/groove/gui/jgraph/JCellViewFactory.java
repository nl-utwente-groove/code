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
 * $Id: JCellViewFactory.java,v 1.1.1.2 2007-03-20 10:42:46 kastenberg Exp $
 */
package groove.gui.jgraph;

import org.jgraph.graph.DefaultCellViewFactory;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.PortView;
import org.jgraph.graph.VertexView;

/**
 * Implementation of a cell view factory that returns {@link JVertexView} en {@link JEdgeView}
 * -objects. For this purpose, the graph on which the views are to be displayed is stored in the
 * factory.
 * @author Arend Rensink
 * @version $Revision: 1.1.1.2 $
 */
public class JCellViewFactory extends DefaultCellViewFactory {
    /**
     * Constructs a factory for creating views upon a particular {@link JGraph}.
     * @param jGraph the graph on which the views are to be displayed.
     */
    public JCellViewFactory(JGraph jGraph) {
        this.jGraph = jGraph;
    }

    /**
     * This implementation creates {@link JVertexView} if the cell to be viewed is a {@link JVertex}.
     * Otherwise, the method delegates to the super class.
     */
    protected VertexView createVertexView(Object cell) {
        if (cell instanceof JVertex) {
            return new JVertexView((JVertex) cell, jGraph);
        } else {
            return super.createVertexView(cell);
        }
    }

    /**
     * This implementation creates {@link JEdgeView} if the cell to be viewed is a {@link JEdge}.
     * Otherwise, the method delegates to the super class.
     */
    protected EdgeView createEdgeView(Object edge) {
        if (edge instanceof JEdge) {
            return new JEdgeView((JEdge) edge, jGraph);
        } else {
            return super.createEdgeView(edge);
        }
    }

//    /**
//     * This implementation returns a {@link JPortView}.
//     */
//    protected PortView createPortView(Object port) {
//        return new JPortView(port);
//    }
//    
    /**
     * The underlying graph on which all views are to be displayed.
     */
    private final JGraph jGraph;
}