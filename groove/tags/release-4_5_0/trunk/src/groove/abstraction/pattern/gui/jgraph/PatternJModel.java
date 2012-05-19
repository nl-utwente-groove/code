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

import groove.graph.Edge;
import groove.graph.Node;
import groove.gui.jgraph.GraphJCell;
import groove.gui.jgraph.GraphJModel;

/**
 * A JGraph model for pattern shapes.
 * 
 * @author Eduardo Zambon
 */
public class PatternJModel extends GraphJModel<Node,Edge> {

    /** Creates a new jModel with the given prototypes. */
    PatternJModel(PatternJVertex jVertexProt, PatternJEdge jEdgeProt) {
        super(jVertexProt, jEdgeProt);
    }

    @Override
    public void synchroniseLayout(GraphJCell jCell) {
        // Do nothing.
    }

}
