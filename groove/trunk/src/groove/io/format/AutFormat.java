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

import groove.graph.DefaultGraph;
import groove.graph.DefaultNode;
import groove.graph.Edge;
import groove.graph.Node;
import groove.gui.jgraph.GraphJGraph;
import groove.gui.jgraph.GraphJModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Class that implements load/save of graphs in the CADP .aut format.
 * @author Eduardo Zambon
 */
public final class AutFormat extends AbsFileFormat<DefaultGraph> {

    private static final String DESCRIPTION = "CADP .aut files";
    private static final String EXTENSION = ".aut";
    private static final boolean ACCEPT_DIR = false;

    /** Label used to identify the start state, when reading in from .aut */
    private static final String ROOT_LABEL = "$ROOT$";

    private static final AutFormat INSTANCE = new AutFormat();

    /** Returns the singleton instance of this class. */
    public static final AutFormat getInstance() {
        return INSTANCE;
    }

    private AutFormat() {
        super(DESCRIPTION, EXTENSION, ACCEPT_DIR);
    }

    // Methods from FileFormat.

    @Override
    public void load(DefaultGraph graph, File file) throws IOException {
        FileInputStream fis = null;
        BufferedReader reader = null;
        try {
            fis = new FileInputStream(file);
            reader = new BufferedReader(new InputStreamReader(fis));
            this.load(graph, reader);
        } catch (FileNotFoundException e) {
            throw new IOException(String.format("File %s not found.", file));
        } finally {
            if (fis != null) {
                fis.close();
            }
            if (reader != null) {
                reader.close();
            }
        }
    }

    private void load(DefaultGraph graph, BufferedReader reader)
        throws IOException {
        Map<String,DefaultNode> result = new HashMap<String,DefaultNode>();
        int linenr = 0;
        try {
            String line = reader.readLine();
            linenr++;
            int rootStart = line.indexOf('(') + 1;
            int edgeCountStart = line.indexOf(',') + 1;
            int root =
                Integer.parseInt(line.substring(rootStart, edgeCountStart - 1).trim());
            DefaultNode rootNode = graph.addNode(root);
            result.put("" + root, rootNode);
            graph.addEdge(rootNode, ROOT_LABEL, rootNode);
            for (line = reader.readLine(); line != null; line =
                reader.readLine()) {
                linenr++;
                if (line.trim().length() > 0) {
                    int sourceStart = line.indexOf('(') + 1;
                    int labelStart = line.indexOf(',') + 1;
                    int targetStart = line.lastIndexOf(',') + 1;
                    int source =
                        Integer.parseInt(line.substring(sourceStart,
                            labelStart - 1).trim());
                    String label = line.substring(labelStart, targetStart - 1);
                    int target =
                        Integer.parseInt(line.substring(targetStart,
                            line.lastIndexOf(')')).trim());
                    DefaultNode sourceNode = graph.addNode(source);
                    DefaultNode targetNode = graph.addNode(target);
                    result.put("" + source, sourceNode);
                    result.put("" + target, targetNode);
                    graph.addEdge(sourceNode, label, targetNode);
                }
            }
        } catch (Exception e) {
            throw new IOException(String.format("Format error in line %d: %s",
                linenr, e.getMessage()));
        }
    }

    @Override
    public void save(GraphJGraph jGraph, File file) throws IOException {
        DefaultGraph graph =
            (DefaultGraph) ((GraphJModel<?,?>) jGraph.getModel()).getGraph();
        this.save(graph, file);
    }

    @Override
    public void save(DefaultGraph graph, File file) throws IOException {
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

    private void save(DefaultGraph graph, PrintWriter writer) {
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

    @Override
    public DefaultGraph createGraph(String graphName) {
        return new DefaultGraph(graphName);
    }

}
