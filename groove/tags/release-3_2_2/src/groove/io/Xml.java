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
package groove.io;

import groove.graph.GraphShape;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Interface for the conversion of graphs to and from XML documents. To be
 * implemented for particular XML formats.
 * 
 * @author Arend Rensink
 * @version $Revision$
 */
public interface Xml<G extends GraphShape> {
    /**
     * Writes a graph to an output stream, in XML format.
     * @param graph the graph to be marshalled
     * @param file the file to write to
     * @throws IOException if an error occurred during file output
     */
    public void marshalGraph(G graph, File file) throws IOException;

    /**
     * Converts an XML URL into a graph, and returns the graph.
     * @param url the URL to be read from
     * @return the unmarshalled graph
     * @throws IOException if an error occurred during input from the URL
     */
    public G unmarshalGraph(URL url) throws IOException;

    /**
     * Backwards compatibility method for unmarshalling from files.
     * @throws IOException if an error occurred during file input
     */
    public G unmarshalGraph(File file) throws IOException;

    /** Deletes a file together with further information (such as layout info). */
    public void deleteGraph(File file);
}
