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
import groove.graph.Graph;
import groove.graph.Node;
import groove.util.Converter;
import groove.util.Pair;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Map;

/**
 * Graph loader based on the CADP <code>.aut</code> format.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Aut extends AbstractXml {
    @Override
    protected Pair<Graph,Map<String,Node>> unmarshalGraphMap(URL url)
        throws IOException {
        Graph resultGraph = createGraph();
        InputStream in = url.openStream();
        Map<String,Node> resultMap = Converter.autToGraph(in, resultGraph);
        in.close();
        return new Pair<Graph,Map<String,Node>>(resultGraph, resultMap);
    }

    public void marshalGraph(Graph graph, File file) throws IOException {
        PrintWriter out = new PrintWriter(file);
        Converter.graphToAut(graph, out);
        out.close();
    }

    /** Callback factory method to create the underlying graph. */
    private Graph createGraph() {
        return new DefaultGraph();
    }
}
