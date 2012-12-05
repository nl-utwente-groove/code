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

import groove.graph.DefaultGraph;
import groove.graph.DefaultNode;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.GraphRole;
import groove.graph.Node;
import groove.io.FileType;
import groove.io.external.AbstractFormatExporter;
import groove.io.external.Exporter.Exportable;
import groove.io.external.Format;
import groove.io.external.FormatImporter;
import groove.io.external.PortException;
import groove.trans.ResourceKind;
import groove.view.GrammarModel;
import groove.view.aspect.AspectGraph;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class that implements load/save of graphs in the CADP .aut format.
 * @author Eduardo Zambon
 */
public final class AutPorter extends AbstractFormatExporter implements
        FormatImporter {

    private AutPorter() {
        Format autFormat = new Format(this, FileType.AUT);
        this.formats = Arrays.asList(autFormat);
    }

    @Override
    public Kind getFormatKind() {
        return Kind.GRAPH;
    }

    @Override
    public Collection<? extends Format> getSupportedFormats() {
        return this.formats;
    }

    @Override
    public Set<Resource> doImport(File file, Format format, GrammarModel grammar)
        throws PortException {
        Set<Resource> resources;
        try {
            FileInputStream stream = new FileInputStream(file);
            resources =
                doImport(format.stripExtension(file.getName()), stream, format,
                    grammar);
            stream.close();
        } catch (IOException e) {
            throw new PortException(e);
        }
        return resources;
    }

    @Override
    public Set<Resource> doImport(String name, InputStream stream,
            Format format, GrammarModel grammar) throws PortException {
        Map<String,DefaultNode> result = new HashMap<String,DefaultNode>();
        BufferedReader reader =
            new BufferedReader(new InputStreamReader(stream));
        int linenr = 0;
        try {
            DefaultGraph graph = new DefaultGraph(name);
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

            graph.setRole(GraphRole.HOST);
            AspectGraph agraph = AspectGraph.newInstance(graph);

            return Collections.singleton(new Resource(ResourceKind.HOST, name,
                agraph));
        } catch (Exception e) {
            throw new PortException(String.format(
                "Format error in line %d: %s", linenr, e.getMessage()));
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                throw new PortException(e);
            }
        }
    }

    @Override
    public void doExport(File file, Format format, Exportable exportable)
        throws PortException {
        Graph<?,?> graph = exportable.getGraph();
        try {
            PrintWriter writer = new PrintWriter(file);
            this.save(graph, writer);
            writer.close();
        } catch (FileNotFoundException e) {
            throw new PortException(e);
        }
    }

    private void save(Graph<?,?> graph, PrintWriter writer) {
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
        for (Edge edge : graph.edgeSet()) {
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

    private final List<Format> formats;

    /** Returns the singleton instance of this class. */
    public static final AutPorter getInstance() {
        return instance;
    }

    /** Label used to identify the start state, when reading in from .aut */
    private static final String ROOT_LABEL = "$ROOT$";

    private static final AutPorter instance = new AutPorter();

}
