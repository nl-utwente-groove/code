/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2010 University of Twente
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
package groove.io.external.format;

import groove.graph.Graph;
import groove.gui.jgraph.GraphJGraph;
import groove.io.ExtensionFilter;
import groove.io.FileType;
import groove.io.xml.Xml;
import groove.util.Groove;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Abstract class implementing the common functionality of the
 * ExternalFileFormat interface.
 * This class also acts as a XML marshaler.
 * 
 * @author Eduardo Zambon
 */
public abstract class AbstractExternalFileFormat<G extends Graph<?,?>>
        implements ExternalFileFormat<G>, Xml<G> {

    /** The filter associated with this format. */
    private final ExtensionFilter filter;

    /** Default constructor. */
    AbstractExternalFileFormat(FileType fileType) {
        this.filter = FileType.getFilter(fileType);
    }

    @Override
    public final ExtensionFilter getFilter() {
        return this.filter;
    }

    // Methods from ExternalFileFormat.

    @Override
    public final void load(G graph, String fileName) throws IOException {
        File file = new File(fileName);
        this.load(graph, file);
    }

    @Override
    abstract public void load(G graph, File file) throws IOException;

    @Override
    public final void save(G graph, String fileName) throws IOException {
        File file = new File(fileName);
        this.save(graph, file);
    }

    @Override
    abstract public void save(G graph, File file) throws IOException;

    @Override
    abstract public void save(GraphJGraph jGraph, File file) throws IOException;

    // Methods from Xml

    @Override
    public final G unmarshalGraph(URL url) throws IOException {
        String fileName = url.getPath();
        String graphName = ExtensionFilter.getPureName(fileName);
        G graph = createGraph(graphName);
        this.load(graph, fileName);
        return graph;
    }

    @Override
    public final G unmarshalGraph(File file) throws IOException {
        return this.unmarshalGraph(Groove.toURL(file));
    }

    @Override
    public final void deleteGraph(File file) {
        file.delete();
    }

    @Override
    public final void marshalGraph(G graph, File file) throws IOException {
        this.save(graph, file);
    }

    @Override
    public abstract G createGraph(String graphName);

}
