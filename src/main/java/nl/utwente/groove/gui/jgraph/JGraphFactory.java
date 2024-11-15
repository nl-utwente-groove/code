/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.gui.jgraph;

import org.eclipse.jdt.annotation.NonNull;

import nl.utwente.groove.graph.Edge;
import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.graph.Node;

/**
 * Factory for {@link JGraph}.
 * @author Arend Rensink
 * @version $Revision$
 */
abstract public class JGraphFactory<G extends @NonNull Graph> {
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
        return new JModel<>(getJGraph()) {
            // empty
        };
    }

    private final JGraph<G> jGraph;
}
