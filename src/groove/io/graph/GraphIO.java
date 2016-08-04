// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// http://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
// either express or implied. See the License for the specific
// language governing permissions and limitations under the License.
/*
 * $Id$
 */
package groove.io.graph;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import groove.graph.Graph;
import groove.graph.GraphRole;
import groove.io.FileType;
import groove.util.parse.FormatException;

/**
 * Class for saving and loading graphs.
 * @author Arend Rensink
 * @version $Revision$
 */
public abstract class GraphIO<G extends Graph> {
    /** Indicates if this IO implementation can save graphs.
     * If {@code false}, {@link #saveGraph(Graph, File)} and {@link #doSaveGraph(Graph, File)}
     * will throw {@link UnsupportedOperationException}s.
     */
    abstract public boolean canSave();

    /**
     * Saves a graph to file.
     * @param graph the graph to be saved
     * @param file the file to write to
     * @throws IOException if an error occurred during file output
     */
    public void saveGraph(Graph graph, File file) throws IOException {
        // create parent dirs if necessary
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        doSaveGraph(graph, file);
    }

    /**
     * Callback method to save a graph to a file, after all
     * necessary directories have been created.
     */
    protected abstract void doSaveGraph(Graph graph, File file) throws IOException;

    /**
     * Indicates if this IO implementation can load graphs.
     * If {@code false}, {@link #loadGraph(File)} and {@link #loadGraph(InputStream)}
     * will throw {@link UnsupportedOperationException}s.
     */
    public abstract boolean canLoad();

    /**
     * Loads an attributed graph from a file.
     * @throws IOException if an error occurred during file input
     */
    public G loadGraph(File file) throws FormatException, IOException {
        setGraphName(FileType.getPureName(file));
        try (FileInputStream in = new FileInputStream(file)) {
            return loadGraph(in);
        }
    }

    /**
     * Loads a graph from an input stream.
     */
    abstract public G loadGraph(InputStream in) throws FormatException, IOException;

    /** Deletes a file together with further information (such as layout info). */
    public void deleteGraph(File file) {
        deleteFile(file);
    }

    /**
     * Deletes the given file.
     */
    protected void deleteFile(File file) {
        if (file.exists() && file.canWrite()) {
            file.delete();
        }
    }

    /**
     * Sets the name for the next graph to be loaded.
     * This is only used for formats which do not store the graph name.
     * The default graph name is "graph".
     * @param graphName the name for the next graph; non-{@code null}
     */
    public void setGraphName(String graphName) {
        assert graphName != null;
        this.graphName = graphName;
    }

    /** Returns the currently set graph name.
     * @see #setGraphName(String)
     */
    public String getGraphName() {
        return this.graphName;
    }

    private String graphName = "graph";

    /**
     * Sets a graph role for the next graph to be loaded.
     * This is only used for formats which do not store the graph role.
     * The default graph role is {@link GraphRole#UNKNOWN}.
     * @param graphRole the role of the next graph to be loaded;non-{@code null}
     */
    public void setGraphRole(GraphRole graphRole) {
        assert graphRole != null;
        this.graphRole = graphRole;
    }

    /** Returns the currently set graph role.
     * @see #setGraphRole(GraphRole)
     */
    public GraphRole getGraphRole() {
        return this.graphRole;
    }

    private GraphRole graphRole = GraphRole.UNKNOWN;

}
