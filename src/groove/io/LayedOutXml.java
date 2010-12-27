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
 * $Id: LayedOutXml.java,v 1.18 2008-03-25 15:13:55 rensink Exp $
 */
package groove.io;

import groove.graph.DefaultEdge;
import groove.graph.DefaultGraph;
import groove.graph.DefaultNode;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.GraphInfo;
import groove.graph.Node;
import groove.gui.layout.LayoutMap;
import groove.util.Groove;
import groove.util.Pair;
import groove.view.FormatException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * 
 * @author Arend Rensink
 * @version $Revision$
 */
public class LayedOutXml implements Xml<DefaultGraph> {
    /**
     * Constructs an xml (un)marshaller, based on {@link DefaultGxl}, also able
     * to deal with layout information. The graphs constructed by
     * {@link #unmarshalGraph(File)} are as directed by the default graph
     * factory, except that layout information is also taken into account.
     */
    public LayedOutXml() {
        this.marshaller = new DefaultGxl();
    }

    public DefaultGraph unmarshalGraph(URL url) throws IOException {
        // first get the non-layed out result
        Pair<DefaultGraph,Map<String,DefaultNode>> preliminary =
            this.marshaller.unmarshalGraphMap(url);
        DefaultGraph result = preliminary.one();
        Map<String,DefaultNode> nodeMap = preliminary.two();
        URL layoutURL = toLayoutURL(url);
        try {
            InputStream in = layoutURL.openStream();
            try {
                LayoutMap<DefaultNode,DefaultEdge> layout =
                    LayoutIO.getInstance().readLayout(nodeMap, in);
                GraphInfo.setLayoutMap(result, layout);
            } catch (FormatException exc) {
                GraphInfo.addErrors(result, exc.getErrors());
            }
        } catch (IOException e) {
            // we do nothing when there is no layout found at the url
        }
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
        deleteFile(file);
    }

    /** First marshals the graph; then the layout map if there is one. */
    public <N extends Node,E extends Edge<N>> void marshalGraph(
            Graph<N,E> graph, File file) throws IOException {
        // deleteVariants(file);
        if (GraphInfo.hasLayoutMap(graph)) {
            this.marshaller.marshalGraph(graph, file);
            FileOutputStream out = new FileOutputStream(toLayoutFile(file));
            try {
                LayoutIO.getInstance().writeLayout(
                    GraphInfo.getLayoutMap(graph), out);
            } finally {
                out.close();
            }
        } else {
            // first marshal the graph
            this.marshaller.marshalGraph(graph, file);
            // now delete any pre-existing layout information
            toLayoutFile(file).delete();
        }
    }

    /** Deletes the file itself as well as the layout file. */
    protected void deleteFile(File file) {
        this.marshaller.deleteFile(file);
        toLayoutFile(file).delete();
    }

    /**
     * Converts a file containing a graph to the file containing the graph's
     * layout information, by adding <code>Groove.LAYOUT_EXTENSION</code> ti the
     * file name.
     */
    private File toLayoutFile(File graphFile) {
        return new File(this.layoutFilter.addExtension(graphFile.toString()));
    }

    /**
     * Converts a file containing a graph to the file containing the graph's
     * layout information, by adding <code>Groove.LAYOUT_EXTENSION</code> to the
     * url path.
     */
    private URL toLayoutURL(URL graphURL) throws MalformedURLException {
        return new URL(
            this.layoutFilter.addExtension(graphURL.toExternalForm()));
    }

    /**
     * The inner (un)marshaller.
     */
    private final DefaultGxl marshaller;
    /** Extension filter for layout extension. */
    private final ExtensionFilter layoutFilter = new ExtensionFilter(
        Groove.LAYOUT_EXTENSION);
}