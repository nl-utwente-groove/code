/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id$
 */
package groove.io;

import groove.graph.DefaultGraph;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Node;
import groove.util.Converter;
import groove.util.Groove;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;

/**
 * Graph loader based on the CADP <code>.aut</code> format.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Aut implements Xml<DefaultGraph> {
    public DefaultGraph unmarshalGraph(URL url) throws IOException {
        String name = extractName(url.getPath());
        DefaultGraph result = createGraph(name);
        InputStream in = url.openStream();
        Converter.autToGraph(in, result);
        in.close();
        return result;
    }

    /** backwards compatibility method */
    public DefaultGraph unmarshalGraph(File file) throws IOException {
        return unmarshalGraph(Groove.toURL(file));
    }

    /**
     * Deletes the graph file, as well as all variants with the same name but
     * different priorities.
     */
    public final void deleteGraph(File file) {
        file.delete();
    }

    public <N extends Node,E extends Edge<N>> void marshalGraph(
            Graph<N,E> graph, File file) throws IOException {
        PrintWriter out = new PrintWriter(file);
        Converter.graphToAut(graph, out);
        out.close();
    }

    /**
     * Extracts a graph name from a location (given as a string) by regarding
     * the string as a file and returning the name part, without extension.
     * @param location string description of the location a graph was marshalled
     *        from
     * @return graph name extracted from <code>location</code>; non-null
     */
    private String extractName(String location) {
        return ExtensionFilter.getPureName(new File(location));
    }

    /** Callback factory method to create the underlying graph. */
    private DefaultGraph createGraph(String name) {
        return new DefaultGraph(name);
    }
}
