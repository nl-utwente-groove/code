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
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.BitSet;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.graph.Edge;
import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.graph.Node;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.Log;
import nl.utwente.groove.util.io.FileType;

/**
 * Listener class for the CADP {@link FileType#AUT} format.
 * Nodes are saved under their own numbers where those lie in the range
 * {@code 0..nodeCount-1}; only the remaining nodes get fresh numbers, through a map,
 * so that saving a large graph needs no per-node data (gh #854).
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
@AIGenerated("Claude Fable 5.1, 2026-09")
public class AutListener extends GraphExportListener {
    /** Constructs a listener for a single write. */
    public AutListener() {
        super(FileType.AUT);
    }

    /** Number of nodes of the graph being written; the valid node numbers are {@code 0..nodeCount-1}. */
    private int nodeCount;
    /** Marks the valid node numbers that are in use. */
    private BitSet nodeList = new BitSet();
    /** Nodes that do not have a valid number. */
    private final Set<Node> restNodes = new LinkedHashSet<>();
    /** Mapping from the nodes without a valid number to their fresh numbers. */
    private final Map<Node,Integer> restNodeNrMap = new HashMap<>();
    /** Number of lines written since the last flush. */
    private int lines;

    @Override
    protected void enterGraph(Graph graph) throws IOException {
        this.nodeCount = graph.nodeCount();
        this.nodeList = new BitSet(this.nodeCount);
        TIMING.log(Level.TRACE, "Building model for aut export");
        emit("des (%d, %d, %d)".formatted(0, graph.edgeCount(), this.nodeCount));
    }

    @Override
    protected void visitNode(Node node) {
        int nodeNr = node.getNumber();
        if (nodeNr >= 0 && nodeNr < this.nodeCount) {
            this.nodeList.set(nodeNr);
        } else {
            this.restNodes.add(node);
        }
    }

    @Override
    protected void enterEdges() {
        // all valid numbers are known now; assign the fresh ones
        int nextNodeNr = -1;
        for (Node restNode : this.restNodes) {
            do {
                nextNodeNr++;
            } while (this.nodeList.get(nextNodeNr));
            this.restNodeNrMap.put(restNode, nextNodeNr);
        }
        TIMING.log(Level.TRACE, "Starting aut export");
    }

    @Override
    protected void visitEdge(Edge edge) throws IOException {
        emit("(%d,%s,%d)"
            .formatted(nodeNr(edge.source()), AutIO.toLabelField(edge.label().text()),
                       nodeNr(edge.target())));
        this.lines = (this.lines + 1) % MAX_LINES;
        if (this.lines == 0) {
            TIMING
                .log(Level.TRACE, "Flushing after writing %s lines to aut".formatted(MAX_LINES));
            flush();
        }
    }

    @Override
    protected void exitGraph(Graph graph) {
        // empty
    }

    /** Returns the number under which a node is saved: its own number if that
     * is valid, otherwise the fresh number assigned to it.
     */
    private int nodeNr(Node node) {
        int result = node.getNumber();
        if (result < 0 || result >= this.nodeCount) {
            Integer restNr = this.restNodeNrMap.get(node);
            assert restNr != null;
            result = restNr;
        }
        return result;
    }

    static private final int MAX_LINES = 100000;

    /** Timing diagnostics, on the same channel as the exploration reporters
     * (enable with {@code -log trace:explore.timing}). */
    static private final Logger TIMING = Log.getLogger("explore.timing");
}
