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

import groove.gui.Simulator;
import groove.gui.jgraph.GraphJGraph;

/**
 * JGraph class for displaying pattern shapes. 
 * 
 * @author Eduardo Zambon
 */
public final class PatternJGraph extends GraphJGraph {

    /** Constructs an instance of the j-graph for a given simulator. */
    public PatternJGraph(Simulator simulator) {
        super(simulator, false);
    }

    /** Specialises the return type to a {@link PatternJModel}. */
    @Override
    public PatternJModel getModel() {
        return (PatternJModel) this.graphModel;
    }

    @Override
    public PatternJModel newModel() {
        return new PatternJModel(PatternJVertex.getPrototype(this),
            PatternJEdge.getPrototype(this));
    }

    @Override
    public boolean isShowLoopsAsNodeLabels() {
        return false;
    }

}
