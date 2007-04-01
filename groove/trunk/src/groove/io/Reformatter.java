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
 * $Id: Reformatter.java,v 1.4 2007-04-01 12:50:23 rensink Exp $
 */
package groove.io;

import groove.graph.BinaryEdge;
import groove.graph.DefaultEdge;
import groove.graph.DefaultGraph;
import groove.graph.Edge;
import groove.graph.GenericNodeEdgeHashMap;
import groove.graph.GenericNodeEdgeMap;
import groove.graph.Graph;
import groove.graph.Node;
import groove.graph.aspect.AspectParser;
import groove.gui.layout.JEdgeLayout;
import groove.gui.layout.LayoutMap;
import groove.util.FormatException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.jgraph.graph.AttributeMap;

/**
 * This application class serves to convert old-style graph serialization
 * formats into new-style formats. It can cope with the following type of
 * files, where <code>ext</code> stands for <code>gst</code>, <code>gpr</code> 
 * or <code>gxl</code> files:
 * <ul>
 * <li> Object stream files (<code>.ext.gp</code>) as used by any of the versions 
 * of GROOVE prior to 0.2.0; these are transformed to pairs
 * of <code>.ext</code> and <code>.ext.gl</code> files. The first contains the
 * graph and the second the corresponding layout information, both in properly
 * validated, though untyped, GXL.
 * <li> Old-style GXL formatted graphs (<code>.ext</code>) as used by any of the
 * versions of GROOVE prior to 0.1.6; these are transformed to properly validated, though
 * untyped, GXL. 
 * </ul>
 * Where both types of files are found, the first overrules the second.
 * The tool works both on individual files and, recursively, in directories.
 * @author Arend Rensink
 * @version $Revision: 1.4 $
 */
public class Reformatter {
    /**
     * The name of the subdirectory in which the reformatted files will be placed.
     * A subdirectory with this name is created in each directory visited.
     * Overidden by the -d option.
     */
    public static final String REFORMAT_SUBDIR = ".new";
    /**
     * The command-line option to set the name of the subdirectory for 
     * reformatted files.
     */
    public static final String SUBDIR_OPTION = "-d";
    public static final String OLD_LAYOUT_EXTENSION = ".gp";
    public static final String WHITESPACE = "\\s*";
    public static final String OLD_LABEL_SEPARATOR = WHITESPACE + "\\|" + WHITESPACE;

    static public void main(String[] args) {
        reformatSubdir = REFORMAT_SUBDIR;
        int i = 0;
        while (i < args.length && args[i].startsWith("-")) {
            if (args[i].equals(SUBDIR_OPTION)) {
                if (i + 1 == args.length) {
                    usageError();
                }
                reformatSubdir = args[i + 1];
                i += 2;
            } else {
                usageError();
            }
        }

        List<File> files = new LinkedList<File>();

        for (; i < args.length; i++) {
            //            if (args[i].startsWith(OPTION_PREFIX))
            //                processOption(args[i].substring(OPTION_PREFIX.length()));
            //            else
            files.add(new File(args[i]));
        }
        GraphFileHandler fileValidator = new FileReformatter();
        verbosity = fileValidator.getVerbosity();
        if (fileValidator.getVerbosity() == GraphFileHandler.VERBOSE_MODE)
            System.out.println("Running reformatter in verbose mode");
        if (files.isEmpty()) {
            // no files were specified; take the working directory
            fileValidator.handle(new File("."));
        } else {
            fileValidator.handle(files);
        }
    }

    static private class FileReformatter extends GraphFileHandler {
        public FileReformatter() {
            super(VERBOSE_MODE);
        }

        @Override
        public void handleGraph(File file) {
            File subDir = new File(file.getParentFile(), reformatSubdir);
            subDir.mkdir();
            File toFile = new File(subDir, file.getName());

            if (verbosity > FileReformatter.QUIET_MODE) {
                if (isVerbose()) {
                    System.out.println();
                }
                System.out.println("Reformatting graph file " + file);
            }

            LayoutMap<Node,Edge> graphToLayoutMap = new LayoutMap<Node,Edge>();
            Graph graph = doImport(file, graphToLayoutMap);
            if (graph != null) {
                normalizeMultiLabels(graph, graphToLayoutMap);
                doExport(toFile, graph, graphToLayoutMap);
            }
        }

        @Override
        public void handleRule(File file) {
            File subDir = new File(file.getParentFile(), reformatSubdir);
            subDir.mkdir();
            File toFile = new File(subDir, file.getName());

            if (verbosity > FileReformatter.QUIET_MODE) {
                if (isVerbose()) {
                    System.out.println();
                }
                System.out.println("Reformatting rule file " + file.getAbsolutePath());
            }
            
            LayoutMap<Node,Edge> graphToLayoutMap = new LayoutMap<Node,Edge>();
            Graph graph = doImport(file, graphToLayoutMap);
            if (graph != null) {
                normalizeMultiLabels(graph, graphToLayoutMap);
                normalizeAspectLabels(graph);
                doExport(toFile, graph, graphToLayoutMap);
            }
        }
        
        @Override
        public void handleDirectory(File dir) {
            if (!dir.getName().equals(reformatSubdir)) {
                super.handleDirectory(dir);
            }
        }

    }

    static public Graph doImport(File file, LayoutMap<Node,Edge> toLayoutMap) {
        Graph result;
        result = importFromStreamFile(file, toLayoutMap);
        if (result != null) {
            return result;
        }
//        result = importFromXml(sloppyGxl, "sloppy", file);
//        if (result != null) {
//            return result;
//        }
        result = importFromXml(untypedGxl, "untyped", file);
        if (result != null) {
            return result;
        }
        return null;
    }

    static public Graph importFromStreamFile(File file, LayoutMap<Node,Edge> toLayoutMap) {
        org.jgraph.graph.DefaultGraphModel jmodel = readStreamFile(file);
        if (jmodel != null) {
        	GenericNodeEdgeMap<Node,org.jgraph.graph.DefaultGraphCell,Edge,org.jgraph.graph.DefaultGraphCell> graphToModelMap = new GenericNodeEdgeHashMap<Node,org.jgraph.graph.DefaultGraphCell,Edge,org.jgraph.graph.DefaultGraphCell>();
            Graph graph = toGraph(jmodel, graphToModelMap);
            LayoutMap<org.jgraph.graph.DefaultGraphCell,org.jgraph.graph.DefaultGraphCell> modelToLayoutMap = toLayoutMap(jmodel);
            modelToLayoutMap.storeAfter(graphToModelMap, toLayoutMap);
            return graph;
        }
        return null;
    }

    static public Graph importFromXml(Xml xml, String name, File file) {
//        graph.removeNodeSet(new HashSet(graph.nodeSet()));
        if (verbosity == FileReformatter.VERBOSE_MODE) {
            System.out.print("Reading " + file + " in " + name + " GXL mode ... ");
        }
        try {
            Graph graph = xml.unmarshalGraph(file);
            if (isVerbose()) {
                System.out.println("Succeeded");
            }
            return graph;
        } catch (Exception exc) {
            if (isVerbose()) {
                System.out.println("Failed");
            }
            if (verbosity > FileReformatter.QUIET_MODE) {
                if (exc instanceof FormatException) {
                    System.out.println("Xml format error : " + exc.getMessage());
                } else if (exc instanceof FileNotFoundException) {
                    System.out.println("Error interpreting " + file + ": " + exc.getMessage());
                    System.out.println("Make sure jgraph 1.0.5 is on the class path");
                }
            }
            return null;
        }
    }

    static public void normalizeMultiLabels(Graph graph, LayoutMap<Node,Edge> toLayoutMap) {
        Set<Edge> newEdgeSet = new HashSet<Edge>();
        for (Edge edge: graph.edgeSet()) {
            JEdgeLayout layout = toLayoutMap.removeEdge(edge);
            String[] labels = edge.label().text().split(OLD_LABEL_SEPARATOR);
            if (isVerbose() && labels.length > 1) {
                System.out.println("Splitting label " + edge.label());
            }
            for (int i = 0; i < labels.length; i++) {
                if (!labels[i].matches(WHITESPACE)) {
                    BinaryEdge edgeImage = DefaultEdge.createEdge(edge.source(), labels[i], edge.opposite());
                    newEdgeSet.add(edgeImage);
                    if (layout != null) {
                        toLayoutMap.putEdge(edgeImage, layout);
                    }
                }
            }
        }
        graph.removeEdgeSet(new HashSet<Edge>(graph.edgeSet()));
        graph.addEdgeSet(newEdgeSet);
    }

    static public void normalizeAspectLabels(Graph graph) {
        Set<Edge> newEdgeSet = new HashSet<Edge>();
        Set<Edge> removedEdgeSet = new HashSet<Edge>();
        for (Edge edge : graph.edgeSet()) {
			try {
				String newLabelText = AspectParser.normalize(edge.label().text());
				BinaryEdge newEdge = DefaultEdge.createEdge(edge.source(),
						newLabelText,
						edge.opposite());
				removedEdgeSet.add(edge);
				newEdgeSet.add(newEdge);
				if (isVerbose()) {
					System.out.println("Normalizing merge labels: replacing "
							+ edge + " by " + newEdge);
				}
			} catch (FormatException exc) {
				throw new IllegalArgumentException(String.format("Graph contains label %s, which cannot be parsed", edge.label()));
			}
		}
        graph.removeEdgeSet(removedEdgeSet);
        graph.addEdgeSet(newEdgeSet);
    }

    static public void doExport(File toFile, Graph graph, LayoutMap<Node,Edge> toLayoutMap) {
        try {
            if (toLayoutMap.isEmpty()) {
                toLayoutMap = null;
            }
            layoutXml.marshal(graph, toLayoutMap, toFile);
            if (!isQuiet()) {
                System.out.println("Writing result to " + toFile);
            }
        } catch (Exception exc) {
            if (!isQuiet()) {
                System.out.println("Error in writing result to " + toFile);
                if (isVerbose()) {
                    exc.printStackTrace();
                }
            }
        }
    }

    static public void reformatGXL(File file) {
        if (verbosity == FileReformatter.VERBOSE_MODE) {
            System.out.println("Interpreting " + file.getAbsolutePath() + " as old-style GXL file");
        }
    }

    static public org.jgraph.graph.DefaultGraphModel readStreamFile(File file) {
        File streamFile = new File(file.getPath() + OLD_LAYOUT_EXTENSION);
        boolean validStreamFile = streamFile.exists();
        if (validStreamFile) {
            if (verbosity == FileReformatter.VERBOSE_MODE) {
                System.out.print("Trying to read stream file " + streamFile + " ... ");
            }
            org.jgraph.graph.DefaultGraphModel jmodel = new org.jgraph.graph.DefaultGraphModel();
            // we have a serialized jgraph; let's see if we can use it
            try {
                ObjectInputStream stream = new ObjectInputStream(new FileInputStream(streamFile));
                Vector v = (Vector) stream.readObject();
                Object[] cells = (Object[]) v.get(0);
                org.jgraph.graph.ConnectionSet cs = (org.jgraph.graph.ConnectionSet) v.get(1);
                Map attrib = (Map) v.get(2);
                jmodel.insert(cells, attrib, cs, null, null);
                stream.close();
                if (verbosity == FileReformatter.VERBOSE_MODE) {
                    System.out.println("Succeeded");
                }
                return jmodel;
            } catch (Exception exc) {
                if (verbosity == FileReformatter.VERBOSE_MODE) {
                    System.out.println("Failed");
                }
                if (isNormal()) {
                    if (exc instanceof IOException) {
                        System.out.println("Error reading " + streamFile + ": " + exc);
                    } else if (exc instanceof ClassNotFoundException) {
                        System.out.println("Error interpreting " + streamFile + ": " + exc);
                        System.out.println("Make sure jgraph 1.0.5 is on the class path");
                    } else if (exc instanceof ClassCastException) {
                        System.out.println("Format error in " + file + ": object is not a Vector");
                    }
                } else if (verbosity == FileReformatter.VERBOSE_MODE) {
//                    exc.printStackTrace();
                }
                return null;
            }
        } else {
            if (verbosity == FileReformatter.VERBOSE_MODE) {
                System.out.println("Could not find stream file for " + file);
            }
            return null;
        }
    }

    /** 
     * Extracts a layout map from a jgraph 1.0.5 model.
     * @param model the graph model to be converted
     * @return a map from <tt>model</tt> nodes and edges to <code>LayoutMap.Layout</code>s.
     */
    static public LayoutMap<org.jgraph.graph.DefaultGraphCell,org.jgraph.graph.DefaultGraphCell> toLayoutMap(org.jgraph.graph.DefaultGraphModel model) {
        LayoutMap<org.jgraph.graph.DefaultGraphCell,org.jgraph.graph.DefaultGraphCell> result = new LayoutMap<org.jgraph.graph.DefaultGraphCell,org.jgraph.graph.DefaultGraphCell>();
        int rootCount = model.getRootCount();
        for (int i = 0; i < rootCount; i++) {
        	org.jgraph.graph.DefaultGraphCell root = (org.jgraph.graph.DefaultGraphCell) model.getRootAt(i);
            AttributeMap attributes = new AttributeMap();
            attributes.applyMap(root.getAttributes());
            if (root instanceof org.jgraph.graph.DefaultEdge) {
            	result.putEdge(root, root.getAttributes());
            } else {
            	result.putNode(root, attributes);
            }
        }
        return result;
    }

    /** 
     * Converts a jgraph 1.0.5 model to a groove graph.
     * @param model the graph model to be converted
     * @param resultMap a map from <tt>result</tt> nodes and edges to <tt>model</tt> nodes and edges
     * @return a graph corresponding to the input graph model 
     */
    static public Graph toGraph(org.jgraph.graph.DefaultGraphModel model, GenericNodeEdgeMap<Node,org.jgraph.graph.DefaultGraphCell,Edge,org.jgraph.graph.DefaultGraphCell> resultMap) {
        Graph result = new DefaultGraph();
        resultMap.clear();
        Map<org.jgraph.graph.DefaultGraphCell,Node> nodeMap = new HashMap<org.jgraph.graph.DefaultGraphCell,Node>();
        int rootCount = model.getRootCount();

        // Create nodes
        for (int i = 0; i < rootCount; i++) {
            Object root = model.getRootAt(i);
            if (!(root instanceof org.jgraph.graph.DefaultEdge)) {
            	org.jgraph.graph.DefaultGraphCell cell = (org.jgraph.graph.DefaultGraphCell) root;
                Node node = result.addNode();
                nodeMap.put(cell, node);
                resultMap.putNode(node, cell);
                // add node labels as self-edges
                String[] labels = ((String) cell.getUserObject()).split(OLD_LABEL_SEPARATOR);
                for (int j = 0; j < labels.length; j++) {
                    String label = labels[j];
                    Edge edge = DefaultEdge.createEdge(node, label, node);
                    result.addEdge(edge);
                    resultMap.putEdge(edge, cell);
                }
            }
        }

        // Create Edges
        for (int i = 0; i < rootCount; i++) {
            Object root = model.getRootAt(i);
            if (root instanceof org.jgraph.graph.DefaultEdge) {
            	org.jgraph.graph.DefaultEdge jedge = (org.jgraph.graph.DefaultEdge) root;
                Node source = nodeMap.get(((org.jgraph.graph.DefaultPort) jedge.getSource()).getParent());
                Node target = nodeMap.get(((org.jgraph.graph.DefaultPort) jedge.getTarget()).getParent());
                if (target == null) {
                    target = source;
                }
                // parse edge text into label set
                String[] labels = ((String) jedge.getUserObject()).split(OLD_LABEL_SEPARATOR);
                for (int j = 0; j < labels.length; j++) {
                    String label = labels[j];
                    Edge edge = DefaultEdge.createEdge(source, label, target);
                    result.addEdge(edge);
                    resultMap.putEdge(edge, (org.jgraph.graph.DefaultEdge) root);
                }
            }
        }

        return result;
    }

    //    /**
    //     * Splits the labels of a graph according to the old-style label separator.
    //     * @return a map from the <code>to</code> to the <code>from</code> elements.
    //     */
    //    public static Map splitLabels(Graph from, Graph to) {
    //        Map result = new HashMap();
    //        Iterator nodeIter = from.nodeIterator();
    //        while (nodeIter.hasNext()) {
    //            Node node = (Node) nodeIter.next();
    //            to.addNode(node);
    //            result.put(node, node);
    //        }
    //        Iterator edgeIter = to.edgeIterator();
    //        while (edgeIter.hasNext()) {
    //            BinaryEdge edge = (BinaryEdge) nodeIter.next();
    //            String[] labels = edge.label().text().split(OLD_LABEL_SEPARATOR);
    //            if (labels.length > 1) {
    //                if (verbosity == FileReformatter.VERBOSE_MODE) {
    //                    System.out.println("Splitting label " + edge.label());
    //                }
    //                for (int i = 0; i < labels.length; i++) {
    //                    BinaryEdge edgeImage = DefaultEdge.createEdge(edge.source(), labels[i], edge.target());
    //                    to.addEdge(edgeImage);
    //                    result.put(edgeImage, edge);
    //                }
    //            } else {
    //                to.addEdge(edge);
    //                result.put(edge, edge);
    //            }
    //        }
    //        return result;
    //    }
    //    /**
    //     * Splits the labels of a graph according to the old-style label separator.
    //     * @return a map from the <code>to</code> to the <code>from</code> elements.
    //     */
    //    public static Map convertMergeLabels(Graph from, Graph to) {
    //        Map result = new HashMap();
    //        Iterator nodeIter = to.nodeIterator();
    //        while (nodeIter.hasNext()) {
    //            Node node = (Node) nodeIter.next();
    //            to.addNode(node);
    //            result.put(node, node);
    //        }
    //        Iterator edgeIter = to.edgeIterator();
    //        while (edgeIter.hasNext()) {
    //            BinaryEdge edge = (BinaryEdge) nodeIter.next();
    //            String[] labels = edge.label().text().split(OLD_LABEL_SEPARATOR);
    //            if (labels.length > 1) {
    //                if (verbosity == FileReformatter.VERBOSE_MODE) {
    //                    System.out.println("Splitting label " + edge.label());
    //                }
    //                for (int i = 0; i < labels.length; i++) {
    //                    BinaryEdge edgeImage = DefaultEdge.createEdge(edge.source(), labels[i], edge.target());
    //                    to.addEdge(edgeImage);
    //                    result.put(edgeImage, edge);
    //                }
    //            } else {
    //                to.addEdge(edge);
    //                result.put(edge, edge);
    //            }
    //        }
    //        return result;
    //    }

    static private void usageError() {
        System.out.println("Usage: reformat [-d <reformat-subdir>] [<files>]");
        System.exit(0);
    }

    static private boolean isVerbose() {
        return verbosity == FileReformatter.VERBOSE_MODE;
    }

    static private boolean isNormal() {
        return verbosity == FileReformatter.NORMAL_MODE;
    }

    static private boolean isQuiet() {
        return verbosity == FileReformatter.QUIET_MODE;
    }
    /**
     * The name of the subdirectory in which to place the reformatted files.
     */
    static private String reformatSubdir;
    /**
     * The verbosity level of this reformatting action.
     */
    static private int verbosity;
//    static private Xml sloppyGxl = new SloppyGxl();
    static private UntypedGxl untypedGxl = new UntypedGxl();
    static private LayedOutXml layoutXml = new LayedOutXml();
}
