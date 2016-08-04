/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
package groove.io.graph;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.GraphRole;
import groove.graph.Node;
import groove.graph.plain.PlainGraph;
import groove.graph.plain.PlainNode;
import groove.io.FileType;

/**
 * Plain graph reader/writer for the CADP {@code .aut} format.
 * @see FileType#AUT
 * @author Arend Rensink
 * @version $Revision $
 */
public class AutIO extends GraphIO<PlainGraph> {
    @Override
    public boolean canSave() {
        return true;
    }

    @Override
    protected void doSaveGraph(Graph graph, File file) throws IOException {
        try (PrintWriter writer = new PrintWriter(file)) {
            // collect the node numbers, to be able to number them consecutively
            int nodeCount = graph.nodeCount();
            // list marking which node numbers have been used
            BitSet nodeList = new BitSet(nodeCount);
            // mapping from nodes to node numbers
            Map<Node,Integer> nodeNrMap = new HashMap<>();
            // nodes that do not have a valid number (in the range 0..nodeCount-1)
            Set<Node> restNodes = new HashSet<>();
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
            writer.printf("des (%d, %d, %d)%n", 0, graph.edgeCount(), graph.nodeCount());
            for (Edge edge : graph.edgeSet()) {
                String format;
                if (edge.label()
                    .text()
                    .indexOf(',') >= 0) {
                    format = "(%d,\"%s\",%d)%n";
                } else {
                    format = "(%d,%s,%d)%n";
                }
                writer.printf(format,
                    nodeNrMap.get(edge.source()),
                    edge.label(),
                    nodeNrMap.get(edge.target()));
            }
        }
    }

    @Override
    public boolean canLoad() {
        return true;
    }

    @Override
    public PlainGraph loadGraph(InputStream in) throws IOException {
        PlainGraph result = createGraph();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            String line = reader.readLine();
            int rootStart = line.indexOf('(') + 1;
            int edgeCountStart = line.indexOf(',') + 1;
            Map<Integer,PlainNode> nodeMap = new HashMap<>();
            int root = Integer.parseInt(line.substring(rootStart, edgeCountStart - 1)
                .trim());
            PlainNode rootNode = result.addNode(root);
            nodeMap.put(root, rootNode);
            if (this.rootLabel != null) {
                result.addEdge(rootNode, this.rootLabel, rootNode);
            }
            for (line = reader.readLine(); line != null; line = reader.readLine()) {
                if (line.trim()
                    .length() > 0) {
                    int sourceStart = line.indexOf('(') + 1;
                    int labelStart = line.indexOf(',') + 1;
                    int targetStart = line.lastIndexOf(',') + 1;
                    int source = Integer.parseInt(line.substring(sourceStart, labelStart - 1)
                        .trim());
                    String label = line.substring(labelStart, targetStart - 1);
                    int target = Integer.parseInt(line.substring(targetStart, line.lastIndexOf(')'))
                        .trim());
                    PlainNode sourceNode = nodeMap.get(source);
                    if (sourceNode == null) {
                        sourceNode = result.addNode(source);
                        nodeMap.put(source, sourceNode);
                    }
                    PlainNode targetNode = nodeMap.get(target);
                    if (targetNode == null) {
                        targetNode = result.addNode(target);
                        nodeMap.put(target, targetNode);
                    }
                    result.addEdge(sourceNode, label, targetNode);
                }
            }
        }
        return result;
    }

    /**
     * Sets the label used to distinguish the root node.
     * By default, there is no root label.
     * @param rootLabel label for the root node; if {@code null}, no label will be added.
     */
    public void setRootLabel(String rootLabel) {
        this.rootLabel = rootLabel;
    }

    private String rootLabel;

    /**
     * Callback factory method for a plain graph with the right name and role.
     * @see #setGraphName(String)
     * @see #setGraphRole(GraphRole)
     */
    private PlainGraph createGraph() {
        PlainGraph result = new PlainGraph(getGraphName(), getGraphRole());
        return result;
    }
}
