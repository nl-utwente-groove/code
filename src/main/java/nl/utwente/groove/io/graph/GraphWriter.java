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
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.io.FileType;

/**
 * Streaming writer for graphs in a text format.
 * A writer emits lines to an output writer while traversing the graph,
 * so that the graph is saved without being copied or otherwise built up in
 * memory first. Subclasses define the traversal in {@link #doWrite};
 * {@link GraphExportListener} is the element-wise one, visiting nodes and then edges.
 * A writer holds per-write state and is not reentrant: create a new one for every write.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
@AIGenerated("Claude Fable 5.1, 2026-09")
abstract public class GraphWriter {
    /** Constructs a writer for a given file type. */
    protected GraphWriter(FileType fileType) {
        this.fileType = fileType;
    }

    /** Returns the file type for which this is a writer. */
    public FileType getFileType() {
        return this.fileType;
    }

    /** The file type for which this is a writer. */
    private final FileType fileType;

    /** Indicates if this writer can write a given graph.
     * The default implementation accepts every graph.
     */
    public boolean accepts(Graph graph) {
        return true;
    }

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
            doWrite(graph, nodes, edges);
        } finally {
            this.out = null;
        }
    }

    /** Callback from {@link #write} to traverse the graph and emit its description.
     * @param graph the graph being written
     * @param nodes the nodes to be included
     * @param edges the edges to be included
     */
    abstract protected void doWrite(Graph graph, Collection<? extends Node> nodes,
                                    Collection<? extends Edge> edges) throws IOException;

    /** Emits a line to the output. */
    protected void emit(String line) throws IOException {
        emit(0, line);
    }

    /** Emits a line to the output, indented to a given depth. */
    protected void emit(int depth, String line) throws IOException {
        var out = this.out;
        assert out != null : "Writer not opened";
        String indent = getIndentUnit();
        for (int i = 0; i < depth; i++) {
            out.write(indent);
        }
        out.write(line);
        out.write('\n');
    }

    /** Returns the indentation unit for {@link #emit(int, String)}.
     * The default is four spaces.
     */
    protected String getIndentUnit() {
        return INDENT;
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

    /** Default indentation unit for {@link #emit(int, String)}. */
    static private final String INDENT = "    ";
}
