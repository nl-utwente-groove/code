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
 * $Id: Xml.java,v 1.11 2008-01-30 09:33:43 iovka Exp $
 */
package groove.io.graph;

import groove.grammar.model.FormatException;
import groove.graph.Graph;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Interface for saving and loading graphs.
 * @author Arend Rensink
 * @version $Revision: 2968 $
 */
public interface GraphIO {
    /**
     * Saves a graph to file.
     * @param graph the graph to be saved
     * @param file the file to write to
     * @throws IOException if an error occurred during file output
     */
    public void saveGraph(Graph graph, File file) throws IOException;

    /**
     * Saves a graph to an output stream.
     */
    public void saveGraph(Graph graph, OutputStream out) throws IOException;

    /**
     * Loads an attributed graph from a file.
     * @throws IOException if an error occurred during file input
     */
    public AttrGraph loadGraph(File file) throws IOException;

    /**
     * Loads a graph from an input stream.
     */
    public AttrGraph loadGraph(InputStream in) throws FormatException,
        IOException;

    /** Deletes a file together with further information (such as layout info). */
    public void deleteGraph(File file);
}
