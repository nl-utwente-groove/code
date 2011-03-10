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

import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Node;
import groove.gui.jgraph.GraphJGraph;
import groove.gui.jgraph.GraphJModel;
import groove.io.FileType;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/** 
 * Class that implements saving graphs in the FSM (Finite State Machine)
 * format.
 * Loading in this format is unsupported.
 * 
 * @author Arend Rensink 
 */

public class FsmFormat extends AbsExternalFileFormat<Graph<?,?>> {

    private static final FsmFormat INSTANCE = new FsmFormat();

    /** Returns the singleton instance of this class. */
    public static final FsmFormat getInstance() {
        return INSTANCE;
    }

    private FsmFormat() {
        super(FileType.FSM);
    }

    // Methods from FileFormat.

    @Override
    public void load(Graph<?,?> graph, File file) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void save(GraphJGraph jGraph, File file) throws IOException {
        Graph<?,?> graph = ((GraphJModel<?,?>) jGraph.getModel()).getGraph();
        this.save(graph, file);
    }

    @Override
    public void save(Graph<?,?> graph, File file) throws IOException {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(file);
            this.save(graph, writer);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    private void save(Graph<?,?> graph, PrintWriter writer) {
        // mapping from nodes of graphs to integers
        Map<Node,Integer> nodeMap = new HashMap<Node,Integer>();
        writer.println("NodeNumber(0)");
        writer.println("---");
        int nr = 1;
        for (Node node : graph.nodeSet()) {
            nodeMap.put(node, nr);
            writer.println(nr);
            nr++;
        }
        writer.println("---");
        for (Edge<?> edge : graph.edgeSet()) {
            writer.println(nodeMap.get(edge.source()) + " "
                + nodeMap.get(edge.target()) + " " + "\"" + edge.label() + "\"");
        }
    }

    // Methods from Xml

    @Override
    public Graph<?,?> createGraph(String graphName) {
        throw new UnsupportedOperationException();
    }

}
