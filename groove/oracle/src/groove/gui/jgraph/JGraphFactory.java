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
import groove.graph.Graph;
import groove.graph.Node;
import groove.gui.look.AdornmentValue;
import groove.gui.look.ColorValue;
import groove.gui.look.EdgeEndLabelValue;
import groove.gui.look.EdgeEndShapeValue;
import groove.gui.look.ErrorValue;
import groove.gui.look.LabelValue;
import groove.gui.look.VisibleValue;
import groove.gui.look.VisualKey;
import groove.gui.look.VisualValue;

/**
 * Factory for {@link JGraph}.
 * @author Arend Rensink
 * @version $Revision $
 */
abstract public class JGraphFactory<G extends Graph> {
    /** Constructs a factory for a given JGraph. */
    public JGraphFactory(JGraph<G> jGraph) {
        this.jGraph = jGraph;
    }

    /** JGraph instance for which this factory was created. */
    public JGraph<G> getJGraph() {
        return this.jGraph;
    }

    /**
     * Creates a fresh, uninitialised instance of a JVertex.
     * The JVertex is initialised with {@link JVertex#setNode(Node)}.
     * The result needs to be provided a JModel before it can be used.
     * @param node a (non-{@code null}) node,
     * used to determine the type of JVertex needed
     */
    abstract public JVertex<G> newJVertex(Node node);

    /**
     * Creates a fresh, initialised instance of a JEdge.
     * The result needs to provided a JModel before it can be used.
     * @param edge a (possibly {@code null}) edge,
     * used to determine the type of JEdge needed
     */
    abstract public JEdge<G> newJEdge(Edge edge);

    /** Constructs a new JModel suitable for the JGraph of this factory. */
    public JModel<G> newModel() {
        return new JModel<G>(getJGraph()) {
            // empty
        };
    }

    /** Creates a visual value refresher for a given key. */
    public VisualValue<?> newVisualValue(VisualKey key) {
        switch (key) {
        case ADORNMENT:
            return new AdornmentValue();
        case COLOR:
            return new ColorValue();
        case EDGE_SOURCE_LABEL:
            return new EdgeEndLabelValue(true);
        case EDGE_SOURCE_SHAPE:
            return new EdgeEndShapeValue(true);
        case EDGE_TARGET_LABEL:
            return new EdgeEndLabelValue(false);
        case EDGE_TARGET_SHAPE:
            return new EdgeEndShapeValue(false);
        case ERROR:
            return new ErrorValue();
        case LABEL:
            return new LabelValue();
        case NODE_SIZE:
            // this cannot be computed; instead it is refreshed
            // in the vertex view, when the UI is around
            return null;
        case TEXT_SIZE:
            // this cannot be computed; instead it is refreshed
            // in the vertex view, when the UI is around
            return null;
        case VISIBLE:
            return new VisibleValue();
        default:
            assert false;
            return null;
        }
    }

    private final JGraph<G> jGraph;
}
