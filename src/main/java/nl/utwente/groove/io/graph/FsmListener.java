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
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.graph.Edge;
import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.graph.Node;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.io.FileType;

/**
 * Listener class for the {@link FileType#FSM} format:
 * a header, a section numbering the nodes from 1 in visiting order,
 * and a section of edges with quoted labels.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
@AIGenerated("Claude Fable 5.1, 2026-09")
public class FsmListener extends GraphExportListener {
    /** Constructs a listener for a single write. */
    public FsmListener() {
        super(FileType.FSM);
    }

    /** Mapping from the nodes to their numbers in the file. */
    private final Map<Node,Integer> nodeMap = new HashMap<>();
    /** Number for the next node. */
    private int nr;

    @Override
    protected void enterGraph(Graph graph) throws IOException {
        emit("NodeNumber(0)");
        emit("---");
        this.nr = 1;
    }

    @Override
    protected void visitNode(Node node) throws IOException {
        this.nodeMap.put(node, this.nr);
        emit(Integer.toString(this.nr));
        this.nr++;
    }

    @Override
    protected void enterEdges() throws IOException {
        emit("---");
    }

    @Override
    protected void visitEdge(Edge edge) throws IOException {
        emit(this.nodeMap.get(edge.source()) + " " + this.nodeMap.get(edge.target()) + " \""
            + edge.label() + "\"");
    }

    @Override
    protected void exitGraph(Graph graph) {
        // empty
    }
}
