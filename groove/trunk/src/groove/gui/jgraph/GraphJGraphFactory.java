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
package groove.gui.jgraph;

import groove.graph.Edge;
import groove.graph.Node;
import groove.gui.look.AdornmentValue;
import groove.gui.look.ColorValue;
import groove.gui.look.ConstantValue;
import groove.gui.look.EdgeSourceLabelValue;
import groove.gui.look.EdgeTargetLabelValue;
import groove.gui.look.ErrorValue;
import groove.gui.look.LabelValue;
import groove.gui.look.VisibleValue;
import groove.gui.look.VisualKey;
import groove.gui.look.VisualValue;

/**
 * Factory for {@link GraphJGraph}.
 * @author Arend Rensink
 * @version $Revision $
 */
public class GraphJGraphFactory implements JGraphFactory {
    /** Constructs a factory for a given JGraph. */
    public GraphJGraphFactory(GraphJGraph jGraph) {
        this.jGraph = jGraph;
    }

    @Override
    public GraphJGraph getJGraph() {
        return this.jGraph;
    }

    @Override
    public GraphJVertex newJVertex() {
        return new GraphJVertex();
    }

    @Override
    public GraphJModel<?,?> newModel() {
        return new GraphJModel<Node,Edge>(this.jGraph);
    }

    @Override
    public GraphJEdge newJEdge() {
        return new GraphJEdge();
    }

    @Override
    public VisualValue newVisualValue(VisualKey key) {
        switch (key) {
        case ADORNMENT:
            return new AdornmentValue();
        case COLOR:
            return new ColorValue();
        case EDGE_SOURCE_LABEL:
            return new EdgeSourceLabelValue();
        case EDGE_TARGET_LABEL:
            return new EdgeTargetLabelValue();
        case EMPHASIS:
            // for now, the emphasis key is not used
            return new ConstantValue(false);
        case ERROR:
            return new ErrorValue();
        case LABEL:
            return new LabelValue(this.jGraph);
        case NODE_SIZE:
            return null;
        case VISIBLE:
            return new VisibleValue(this.jGraph);
        default:
            assert false;
            return null;
        }
    }

    private final GraphJGraph jGraph;
}
