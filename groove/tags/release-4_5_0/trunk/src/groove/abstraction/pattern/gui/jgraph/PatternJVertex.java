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

import groove.abstraction.pattern.shape.AbstractPatternNode;
import groove.graph.Node;
import groove.gui.jgraph.GraphJModel;
import groove.gui.jgraph.GraphJVertex;

/**
 * Class that connects to the JGraph library for displaying pattern nodes.
 * 
 * @author Eduardo Zambon
 */
public class PatternJVertex extends GraphJVertex {

    // Private constructor. Use the prototype.
    private PatternJVertex(PatternJGraph jGraph, PatternJModel jModel,
            AbstractPatternNode pNode) {
        super(jGraph, jModel, pNode);
    }

    @Override
    public PatternJGraph getJGraph() {
        return (PatternJGraph) super.getJGraph();
    }

    @Override
    public PatternJVertex newJVertex(GraphJModel<?,?> jModel, Node node) {
        assert node instanceof AbstractPatternNode;
        return new PatternJVertex(getJGraph(), (PatternJModel) jModel,
            (AbstractPatternNode) node);
    }

    /** Returns a prototype {@link PatternJVertex} for a given {@link PatternJGraph}. */
    public static PatternJVertex getPrototype(PatternJGraph jGraph) {
        return new PatternJVertex(jGraph, null, null);
    }

}