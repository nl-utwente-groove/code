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

import groove.abstraction.pattern.gui.jgraph.PatternJVertex;
import groove.abstraction.pattern.shape.AbstractPatternNode;
import groove.abstraction.pattern.shape.PatternNode;
import groove.abstraction.pattern.shape.PatternShape;
import groove.graph.Graph;
import groove.gui.jgraph.GraphJCell;
import groove.gui.look.VisualValue;

/**
 * Returns the pattern adornment 
 * @author Arend Rensink
 * @version $Revision $
 */
public class PatternAdornmentValue implements VisualValue<String> {
    @Override
    public String get(GraphJCell cell) {
        String result = null;
        if (cell instanceof PatternJVertex) {
            PatternJVertex jVertex = (PatternJVertex) cell;
            AbstractPatternNode node = (AbstractPatternNode) jVertex.getNode();
            result = node.getAdornment();
            Graph<?,?> graph = cell.getJModel().getGraph();
            if (graph instanceof PatternShape) {
                PatternShape pShape = (PatternShape) graph;
                result += "(" + pShape.getMult((PatternNode) node) + ")";
            }
        }
        return result;
    }
}
