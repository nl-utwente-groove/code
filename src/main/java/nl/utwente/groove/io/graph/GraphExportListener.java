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
package nl.utwente.groove.io.graph;

import java.io.IOException;
import java.util.Collection;

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.graph.Edge;
import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.graph.Node;
import nl.utwente.groove.util.io.FileType;

/**
 * Element-wise {@link GraphWriter}: a listener receives the elements of a graph
 * one by one, through {@link #enterGraph}, {@link #visitNode}, {@link #enterEdges},
 * {@link #visitEdge} and {@link #exitGraph}, and emits lines as it goes.
 * Nodes are always visited before edges.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
abstract public class GraphExportListener extends GraphWriter {
    /** Constructs a listener for a given file type. */
    protected GraphExportListener(FileType fileType) {
        super(fileType);
    }

    @Override
    protected void doWrite(Graph graph, Collection<? extends Node> nodes,
                           Collection<? extends Edge> edges) throws IOException {
        enterGraph(graph);
        for (Node node : nodes) {
            visitNode(node);
        }
        enterEdges();
        for (Edge edge : edges) {
            visitEdge(edge);
        }
        exitGraph(graph);
    }

    /** Starts processing a graph. */
    abstract protected void enterGraph(Graph graph) throws IOException;

    /** Emits the description of a graph node. */
    abstract protected void visitNode(Node node) throws IOException;

    /** Callback after the last node and before the first edge.
     * The default implementation does nothing.
     * @throws IOException if an overriding implementation fails to emit output
     */
    protected void enterEdges() throws IOException {
        // empty
    }

    /** Emits the description of a graph edge. */
    abstract protected void visitEdge(Edge edge) throws IOException;

    /** Finishes processing a graph. */
    abstract protected void exitGraph(Graph graph) throws IOException;
}
