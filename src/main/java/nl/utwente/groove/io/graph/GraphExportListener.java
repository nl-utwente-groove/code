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

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collection;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.graph.Edge;
import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.graph.Node;
import nl.utwente.groove.util.io.FileType;

/**
 * Streaming writer for graphs in a text format.
 * A listener receives the elements of a graph one by one, through
 * {@link #enterGraph}, {@link #visitNode}, {@link #enterEdges}, {@link #visitEdge}
 * and {@link #exitGraph}, and emits lines to an output writer as it goes,
 * so that a graph is saved without being copied or otherwise built up in memory first.
 * Nodes are always visited before edges.
 * A listener holds per-write state and is not reentrant: create a new one for every write.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
abstract public class GraphExportListener {
    /** Constructs a listener for a given file type. */
    protected GraphExportListener(FileType fileType) {
        this.fileType = fileType;
    }

    /** Returns the file type for which this is a listener. */
    public FileType getFileType() {
        return this.fileType;
    }

    /** The file type for which this is a listener. */
    private final FileType fileType;

    /** Writes a graph to a file, in UTF-8 with {@code \n} line ends. */
    public void write(Graph graph, File file) throws IOException {
        try (Writer out = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
            write(graph, out);
        }
    }

    /** Writes a graph to a writer, which is left open. */
    public void write(Graph graph, Writer out) throws IOException {
        write(graph, graph.nodeSet(), graph.edgeSet(), out);
    }

    /** Writes a graph to a writer, which is left open, restricted to
     * given collections of its nodes and edges.
     */
    public void write(Graph graph, Collection<? extends Node> nodes,
                      Collection<? extends Edge> edges, Writer out) throws IOException {
        this.out = out;
        try {
            enterGraph(graph);
            for (Node node : nodes) {
                visitNode(node);
            }
            enterEdges();
            for (Edge edge : edges) {
                visitEdge(edge);
            }
            exitGraph(graph);
        } finally {
            this.out = null;
        }
    }

    /** Emits a line to the output. */
    protected void emit(String line) throws IOException {
        emit(0, line);
    }

    /** Emits a line to the output, indented to a given depth. */
    protected void emit(int depth, String line) throws IOException {
        var out = this.out;
        assert out != null : "Writer not opened";
        for (int i = 0; i < depth; i++) {
            out.write(INDENT);
        }
        out.write(line);
        out.write('\n');
    }

    /** Flushes the output. */
    protected void flush() throws IOException {
        var out = this.out;
        assert out != null : "Writer not opened";
        out.flush();
    }

    /** The output writer; only set while a {@link #write} is in progress.
     * The writer is owned by the caller of {@link #write}, which also closes it.
     */
    private @Nullable Writer out;

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

    /** Indentation unit for {@link #emit(int, String)}. */
    static private final String INDENT = "    ";
}
