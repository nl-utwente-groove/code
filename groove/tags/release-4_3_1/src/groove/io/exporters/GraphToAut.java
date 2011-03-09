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
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Writes a graph in CADP .aut format to a print writer.
 * @author Arend Rensink
 */
public class GraphToAut {

    /** Exports a graph in CADP .aut format to a print writer. */
    static public void export(Graph<?,?> graph, PrintWriter writer) {
        // collect the node numbers, to be able to number them consecutively
        int nodeCount = graph.nodeCount();
        // list marking which node numbers have been used
        BitSet nodeList = new BitSet(nodeCount);
        // mapping from nodes to node numbers
        Map<Node,Integer> nodeNrMap = new HashMap<Node,Integer>();
        // nodes that do not have a valid number (in the range 0..nodeCount-1)
        Set<Node> restNodes = new HashSet<Node>();
        // iterate over the existing nodes
        for (Node node : graph.nodeSet()) {
            int nodeNr = node.getNumber();
            if (nodeNr >= 0 && nodeNr < nodeCount) {
                nodeList.set(nodeNr);
                nodeNrMap.put(node, nodeNr);
            } else {
                restNodes.add(node);
            }
        }
        int nextNodeNr = -1;
        for (Node restNode : restNodes) {
            do {
                nextNodeNr++;
            } while (nodeList.get(nextNodeNr));
            nodeNrMap.put(restNode, nextNodeNr);
        }
        writer.printf("des (%d, %d, %d)%n", 0, graph.edgeCount(),
            graph.nodeCount());
        for (Edge<?> edge : graph.edgeSet()) {
            String format;
            if (edge.label().text().indexOf(',') >= 0) {
                format = "(%d,\"%s\",%d)%n";
            } else {
                format = "(%d,%s,%d)%n";
            }
            writer.printf(format, nodeNrMap.get(edge.source()), edge.label(),
                nodeNrMap.get(edge.target()));
        }
    }

}
