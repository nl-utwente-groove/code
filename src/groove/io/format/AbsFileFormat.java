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
package groove.io.format;

import groove.graph.Graph;
import groove.gui.jgraph.GraphJGraph;
import groove.io.ExtensionFilter;
import groove.io.FilterList;
import groove.io.xml.Xml;
import groove.util.Groove;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * @author Eduardo Zambon
 */
public abstract class AbsFileFormat<G extends Graph<?,?>> implements
        FileFormat<G>, Xml<G> {

    private final String description;
    private final String extension;
    private final boolean acceptDir;
    private final ExtensionFilter filter;

    AbsFileFormat(String description, String extension, boolean acceptDir) {
        this.description = description;
        this.extension = extension;
        this.acceptDir = acceptDir;
        this.filter = FilterList.getFilter(description, extension, acceptDir);
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public String getExtension() {
        return this.extension;
    }

    @Override
    public boolean isAcceptDir() {
        return this.acceptDir;
    }

    @Override
    public ExtensionFilter getFilter() {
        return this.filter;
    }

    // Methods from FileFormat.

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
    public void marshalGraph(G graph, File file) throws IOException {
        this.save(graph, file);
    }

    @Override
    public abstract G createGraph(String graphName);

}
