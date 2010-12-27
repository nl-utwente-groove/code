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
 * $Id: DefaultGxl.java,v 1.21 2007-12-03 08:55:18 rensink Exp $
 */
package groove.io;

import groove.graph.DefaultEdge;
import groove.graph.DefaultGraph;
import groove.graph.DefaultNode;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.GraphInfo;
import groove.graph.Node;
import groove.graph.iso.IsoChecker;
import groove.util.Groove;
import groove.util.Pair;
import groove.view.FormatException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

/**
 * Class to convert graphs to GXL format and back. Currently the conversion only
 * supports binary edges. This class is implemented using data binding.
 * @author Arend Rensink
 * @version $Revision$
 */
public class DefaultGxl implements Xml<DefaultGraph> {
    public DefaultGraph unmarshalGraph(URL url) throws IOException {
        try {
            URLConnection connection = url.openConnection();
            InputStream in = connection.getInputStream();
            DefaultGraph resultGraph = io.loadGraphWithMap(in).one();
            // set some more information in the graph, based on the URL
            GraphInfo.setFile(resultGraph, url.getFile());
            // derive the name of the graph from the URL
            String entryName;
            if (connection instanceof JarURLConnection) {
                entryName = ((JarURLConnection) connection).getEntryName();
            } else {
                entryName = url.getFile();
            }
            PriorityFileName priorityName =
                new PriorityFileName(new File(entryName));
            if (priorityName.hasPriority()) {
                GraphInfo.getProperties(resultGraph, true).setPriority(
                    priorityName.getPriority());
            }

            // set graph role if necessary
            if (GraphInfo.getRole(resultGraph) == null) {
                if (Groove.isRuleURL(url)) {
                    GraphInfo.setRuleRole(resultGraph);
                } else {
                    GraphInfo.setHostRole(resultGraph);
                }
            }
            // note: don't set the name,
            // there is no general scheme to derive it from the URL
            return resultGraph;
        } catch (FormatException exc) {
            throw new IOException(String.format(
                "Format error while loading '%s':\n%s", url, exc.getMessage()),
                exc);
        } catch (IOException exc) {
            throw new IOException(String.format(
                "Error while loading '%s':\n%s", url, exc.getMessage()), exc);
        }
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

    /**
     * Delete the given file
     */
    public void deleteFile(File file) {
        if (file.exists() && file.canWrite()) {
            file.delete();
        }
    }

    /**
     * This implementation works by delegating to a {@link GxlIO}.
     */
    public <N extends Node,E extends Edge> void marshalGraph(
            Graph<N,?,E> graph, File file) throws IOException {
        // create parent dirs if necessary
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        FileOutputStream out = new FileOutputStream(file);
        try {
            io.saveGraph(graph, out);
        } finally {
            out.close();
        }
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
    protected Pair<DefaultGraph,Map<String,DefaultNode>> unmarshalGraphMap(
            URL url) throws IOException {
        try {
            URLConnection connection = url.openConnection();
            InputStream in = connection.getInputStream();
            Pair<DefaultGraph,Map<String,DefaultNode>> result =
                io.loadGraphWithMap(in);
            DefaultGraph resultGraph = result.one();
            // set some more information in the graph, based on the URL
            GraphInfo.setFile(resultGraph, url.getFile());
            // derive the name of the graph from the URL
            String entryName;
            if (connection instanceof JarURLConnection) {
                entryName = ((JarURLConnection) connection).getEntryName();
            } else {
                entryName = url.getFile();
            }
            PriorityFileName priorityName =
                new PriorityFileName(new File(entryName));
            if (priorityName.hasPriority()) {
                GraphInfo.getProperties(resultGraph, true).setPriority(
                    priorityName.getPriority());
            }

            // set graph role if necessary
            if (GraphInfo.getRole(resultGraph) == null) {
                if (Groove.isRuleURL(url)) {
                    GraphInfo.setRuleRole(resultGraph);
                } else {
                    GraphInfo.setHostRole(resultGraph);
                }
            }
            // note: don't set the name,
            // there is no general scheme to derive it from the URL
            return result;
        } catch (FormatException exc) {
            throw new IOException(String.format(
                "Format error while loading '%s':\n%s", url, exc.getMessage()),
                exc);
        } catch (IOException exc) {
            throw new IOException(String.format(
                "Error while loading '%s':\n%s", url, exc.getMessage()), exc);
        }
    }

    /** Marshaller/unmarshaller. */
    static private final GxlIO io = JaxbGxlIO.getInstance();

    /**
     * Test method: tries loading and saving graphs, and comparing them for
     * isomorphism.
     */
    static public void main(String[] args) {
        System.out.println("Test of groove.io.UntypedGxl");
        System.out.println("===================");
        groove.io.DefaultGxl gxl = new groove.io.DefaultGxl();
        for (String element : args) {
            System.out.println("\nTesting: " + element);
            try {
                System.out.print("    Creating input file: ");
                java.net.URL url = new java.net.URL(element);
                System.out.println("OK");
                // Unmarshal graph
                System.out.print("    Unmarshalling graph: ");
                DefaultGraph graph = gxl.unmarshalGraph(url);
                System.out.println("OK");
                System.out.print("    Creating output file: ");
                // file = new java.io.File(element + ".tmp");
                System.out.println("OK");
                System.out.print("    Re-marshalling graph: ");
                gxl.marshalGraph(graph, new File(element));
                System.out.println("OK");
                // unmarshal again and test for isomorphism
                System.out.print("    Testing for isomorphism of original and re-marshalled graph: ");
                DefaultGraph newGraph = gxl.unmarshalGraph(url);

                if (isoChecker.areIsomorphic(newGraph, graph)) {
                    System.out.println("OK");
                } else {
                    System.out.println("ERROR");
                    System.out.println("Unmarshalled graph");
                    System.out.println("------------------");
                    System.out.println(newGraph);
                }
            } catch (Exception exc) {
                System.out.println(exc);
                exc.printStackTrace();
            }
        }
    }

    /**
     * The name of graphs whose name is not explicitly included in the graph
     * info.
     */
    static public final String DEFAULT_GRAPH_NAME = "graph";
    /** Attribute name for node and edge identities. */
    static public final String LABEL_ATTR_NAME = "label";
    /** Private isomorphism checker, for testing purposes. */
    static private final IsoChecker<DefaultNode,DefaultEdge> isoChecker =
        IsoChecker.getInstance(true);
}