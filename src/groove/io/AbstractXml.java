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
 * $Id: AbstractXml.java,v 1.13 2008-01-30 09:33:41 iovka Exp $
 */
package groove.io;

import groove.graph.Graph;
import groove.graph.GraphFactory;
import groove.graph.Node;
import groove.util.Groove;
import groove.util.Pair;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

/**
 * Convenience class that brings down <tt>Xml</tt>'s methods to just two
 * abstract methods: <tt>marshal(Graph)</tt> and
 * <tt>unmarshal(Document,Graph)</tt>.
 * 
 * @author Arend Rensink
 * @version $Revision$
 */
public abstract class AbstractXml implements Xml<Graph> {
    AbstractXml(GraphFactory graphFactory) {
        this.graphFactory = graphFactory;
    }

    public Graph unmarshalGraph(URL url) throws IOException {
        return unmarshalGraphMap(url).first();
    }

    /** backwards compatibility method */
    public Graph unmarshalGraph(File file) throws IOException {
        return unmarshalGraph(Groove.toURL(file));
    }

    /**
     * Deletes the graph file, as well as all variants with the same name but
     * different priorities.
     */
    public final void deleteGraph(File file) {
        deleteFile(file);
    }

    /** Deletes a given file, storing a graph, and possible auxiliary files. */
    protected void deleteFile(File file) {
        file.delete();
    }

    /**
     * Reads a graph from an XML formatted URL and returns it. Also constructs
     * a map from node identities in the XML file to graph nodes. This can be
     * used to connect with layout information.
     * @param url the URL to be read from
     * @return a pair consisting of the unmarshalled graph and a string-to-node
     *         map from node identities in the XML file to nodes in the
     *         unmarshalled graph
     * @throws IOException if an error occurred during file input
     */
    abstract protected Pair<Graph,Map<String,Node>> unmarshalGraphMap(URL url)
        throws IOException;

    /**
     * Changes the graph factory used for unmarshalling.
     */
    protected final void setGraphFactory(GraphFactory factory) {
        this.graphFactory = factory;
    }

    /**
     * Returns the graph factory used for unmarshalling.
     */
    protected final GraphFactory getGraphFactory() {
        return this.graphFactory;
    }

    /** The graph factory for this marshaller. */
    private GraphFactory graphFactory;
}
