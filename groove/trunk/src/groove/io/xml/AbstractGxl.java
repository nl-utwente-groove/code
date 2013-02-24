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
 * $Id: DefaultGxl.java,v 1.21 2007-12-03 08:55:18 rensink Exp $
 */
package groove.io.xml;

import groove.grammar.model.FormatException;
import groove.graph.Edge;
import groove.graph.GGraph;
import groove.graph.Graph;
import groove.graph.GraphInfo;
import groove.graph.Node;
import groove.graph.plain.PlainGraph;
import groove.io.PriorityFileName;
import groove.util.Pair;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Class to convert graphs to GXL format and back. Currently the conversion only
 * supports binary edges. This class is implemented using data binding.
 * @author Arend Rensink
 * @version $Revision: 2973 $
 */
public abstract class AbstractGxl<N extends Node,E extends Edge,G extends GGraph<N,E>>
        implements Xml<G> {

    /** Returns the proper marshaller. */
    protected abstract GxlIO<N,E,G> getIO();

    /** backwards compatibility method */
    public G unmarshalGraph(File file) throws IOException {
        return unmarshalGraphMap(file).one();
    }

    /**
     * Deletes the graph file and all dependent files.
     */
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
     * This implementation works by delegating to a {@link GxlIO}.
     */
    @Override
    public void marshalGraph(Graph graph, File file) throws IOException {
        // create parent dirs if necessary
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        FileOutputStream out = new FileOutputStream(file);
        try {
            getIO().saveGraph(graph, out);
        } finally {
            out.close();
        }
    }

    /**
     * Reads a graph from a file and returns it. Also constructs
     * a map from node identities in the XML file to graph nodes. This can be
     * used to connect with layout information.
     * @param file the file to be read from
     * @return a pair consisting of the unmarshalled graph and a string-to-node
     *         map from node identities in the XML file to nodes in the
     *         unmarshalled graph
     * @throws IOException if an error occurred during file input
     */
    protected Pair<G,Map<String,N>> unmarshalGraphMap(File file)
        throws IOException {
        try {
            InputStream in = new FileInputStream(file);
            Pair<G,Map<String,N>> result = getIO().loadGraphWithMap(in);
            PlainGraph resultGraph = (PlainGraph) result.one();
            PriorityFileName priorityName = new PriorityFileName(file);
            if (priorityName.hasPriority()) {
                GraphInfo.setPriority(resultGraph, priorityName.getPriority());
            }
            // note: don't set the name,
            // there is no general scheme to derive it from the URL
            return result;
        } catch (FormatException exc) {
            throw new IOException(
                String.format("Format error while loading '%s':\n%s", file,
                    exc.getMessage()), exc);
        } catch (IOException exc) {
            throw new IOException(String.format(
                "Error while loading '%s':\n%s", file, exc.getMessage()), exc);
        }
    }

}