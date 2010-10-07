/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
package groove.io;

import groove.graph.Graph;
import groove.graph.Node;
import groove.util.Pair;
import groove.view.FormatException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/** Interface for marshalling and unmarshalling graphs. */
public interface GxlIO {
    /**
     * Saves a graph to an output stream.
     */
    public abstract void saveGraph(Graph graph, OutputStream out)
        throws IOException;

    /**
     * Loads a graph plus mapping information from an input stream. The mapping
     * information consists of a map from node identities as they occur in the
     * input to node identities in the resulting graph.
     */
    public abstract Pair<Graph,Map<String,Node>> loadGraphWithMap(InputStream in)
        throws FormatException, IOException;

    /**
     * Loads a graph from an input stream. Convenience method for
     * <code>loadGraphWithMap(in).first()</code>.
     */
    public abstract Graph loadGraph(InputStream in) throws FormatException,
        IOException;

}