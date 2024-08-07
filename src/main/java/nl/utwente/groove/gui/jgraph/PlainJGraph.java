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
import nl.utwente.groove.gui.Simulator;

/**
 * Plain JGraph implementation, not specialised for any particular graph kind.
 * @author Arend Rensink
 * @version $Revision$
 */
public class PlainJGraph extends JGraph<@NonNull Graph> {
    /**
     * Private constructor.
     * @param simulator the simulator to which the {@link JGraph} belongs;
     * may be {@code null}.
     */
    private PlainJGraph(Simulator simulator) {
        super(simulator);
    }

    @Override
    protected JGraphFactory<@NonNull Graph> createFactory() {
        return new JGraphFactory<>(this) {
            @Override
            public PlainJEdge newJEdge(Edge edge) {
                return new PlainJEdge();
            }

            @Override
            public PlainJVertex newJVertex(Node node) {
                return new PlainJVertex();
            }
        };
    }

    /**
     * Creates a new instance, based on a given simulator.
     * @param simulator the simulator to which the {@link JGraph} belongs;
     * may be {@code null}.
     */
    public static PlainJGraph newInstance(Simulator simulator) {
        return new PlainJGraph(simulator);
    }

    private class PlainJEdge
        extends AJEdge<@NonNull Graph,PlainJGraph,JModel<@NonNull Graph>,PlainJVertex> {
        // empty
    }

    private class PlainJVertex
        extends AJVertex<@NonNull Graph,PlainJGraph,JModel<@NonNull Graph>,PlainJEdge> {
        // empty
    }
}
