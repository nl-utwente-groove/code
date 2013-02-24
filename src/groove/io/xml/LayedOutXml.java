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
package groove.io.xml;

import static groove.io.FileType.LAYOUT_FILTER;
import groove.grammar.model.FormatException;
import groove.graph.Graph;
import groove.graph.GraphInfo;
import groove.graph.plain.PlainGraph;
import groove.graph.plain.PlainNode;
import groove.gui.layout.LayoutMap;
import groove.io.LayoutIO;
import groove.util.Groove;
import groove.util.Pair;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * 
 * @author Arend Rensink
 * @version $Revision: 3148 $
 */
public class LayedOutXml implements Xml<PlainGraph> {
    /**
     * Constructs an xml (un)marshaller, based on {@link PlainGxl}, also able
     * to deal with layout information. The graphs constructed by
     * {@link #unmarshalGraph(File)} are as directed by the default graph
     * factory, except that layout information is also taken into account.
     * The constructor is private to avoid object creation. Use the method
     * {@link #getInstance()} instead.
     */
    private LayedOutXml() {
        this.marshaller = PlainGxl.getInstance();
    }

    /** Returns the singleton instance of this class. */
    public static LayedOutXml getInstance() {
        return INSTANCE;
    }

    public PlainGraph unmarshalGraph(URL url) throws IOException {
        // first get the non-layed out result
        Pair<PlainGraph,Map<String,PlainNode>> preliminary =
            this.marshaller.unmarshalGraphMap(url);
        PlainGraph result = preliminary.one();
        Map<String,PlainNode> nodeMap = preliminary.two();
        URL layoutURL = toLayoutURL(url);
        try {
            InputStream in = layoutURL.openStream();
            try {
                LayoutMap layout =
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
    public PlainGraph unmarshalGraph(File file) throws IOException {
        return unmarshalGraph(Groove.toURL(file));
    }

    /**
     * Deletes the graph file, as well as all variants with the same name but
     * different priorities.
     */
    @Override
    public final void deleteGraph(File file) {
        deleteFile(file);
    }

    @Override
    public void marshalGraph(Graph graph, File file) throws IOException {
        this.marshaller.marshalGraph(graph, file);
        toLayoutFile(file).delete();
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
        return new File(LAYOUT_FILTER.addExtension(graphFile.toString()));
    }

    /**
     * Converts a file containing a graph to the file containing the graph's
     * layout information, by adding <code>Groove.LAYOUT_EXTENSION</code> to the
     * url path.
     */
    private URL toLayoutURL(URL graphURL) throws MalformedURLException {
        return new URL(LAYOUT_FILTER.addExtension(graphURL.toExternalForm()));
    }

    /**
     * The inner (un)marshaller.
     */
    private final PlainGxl marshaller;

    private static final LayedOutXml INSTANCE = new LayedOutXml();

}