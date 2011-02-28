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
package groove.io.exporters;

import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Node;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/** 
 * Writes a graph in FSM format to a print writer.
 * @author Arend Rensink
 */
public class GraphToFsm {

    /** Exports a graph in FSM format to a print writer. */
    static public void export(Graph<?,?> graph, PrintWriter writer) {
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

}
