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

import groove.graph.AbstractBinaryEdge;
import groove.graph.AbstractLabel;
import groove.graph.AbstractUnaryEdge;
import groove.graph.BinaryEdge;
import groove.graph.DefaultLabel;
import groove.graph.DefaultNode;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.GraphFactory;
import groove.graph.GraphInfo;
import groove.graph.GraphProperties;
import groove.graph.GraphShape;
import groove.graph.Node;
import groove.graph.iso.DefaultIsoChecker;
import groove.graph.iso.IsoChecker;
import groove.util.Groove;
import groove.util.Pair;
import groove.util.Version;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;

/**
 * Class to convert graphs to GXL format and back. Currently the conversion only
 * supports binary edges. This class is implemented using data binding.
 * @author Arend Rensink
 * @version $Revision$
 */
public class DefaultGxl extends AbstractXml {
    /**
     * Constructs a Gxl transformer with a given graph factory for the graphs
     * constructed by unmarshalling.
     */
    public DefaultGxl(GraphFactory graphFactory) {
        super(graphFactory);
    }

    /**
     * Constructs a Gxl transformer with a default graph factory.
     * @see GraphFactory#getInstance()
     */
    public DefaultGxl() {
        this(GraphFactory.getInstance());
    }

    /**
     * This implementation works by converting the graph to an attributed graph
     * using {@link #attrToGxlGraph}, and marshalling the result using
     * {@link #marshalGxlGraph}.
     */
    public void marshalGraph(Graph graph, File file) throws IOException {
        File parentFile = file.getParentFile();
        // create the file, if necessary
        if (parentFile != null) {
            parentFile.mkdirs();
        }
        if (parentFile == null || parentFile.exists()) {
            file.createNewFile();
            Graph attrGraph = normToAttrGraph(graph);
            if (Groove.isRuleFile(file)) {
                GraphInfo.setRuleRole(attrGraph);
            } else if (Groove.isStateFile(file)) {
                GraphInfo.setGraphRole(attrGraph);
            }
            GraphInfo.setVersion(attrGraph, Version.GXL_VERSION);
            groove.gxl.Graph gxlGraph = attrToGxlGraph(attrGraph);
            // now marshal the attribute graph
            marshalGxlGraph(gxlGraph, file);
        } else {
            throw new IOException(String.format("Cannot create %s",
                file.getParentFile()));
        }
    }

    /**
     * This implementation works by unmarshalling to an attributed graph using
     * {@link #unmarshalGxlGraph} and {@link #gxlToAttrGraph}, and converting
     * the result to an ordinary graph using <tt>{@link #attrToNormGraph}</tt>.
     */
    @Override
    protected Pair<Graph,Map<String,Node>> unmarshalGraphMap(File file)
        throws IOException {
        Graph result;
        groove.gxl.Graph gxlGraph = unmarshalGxlGraph(file);
        Pair<Graph,Map<String,Node>> attrGraph = gxlToAttrGraph(gxlGraph);
        result = attrToNormGraph(attrGraph.first());
        Map<String,Node> conversion = attrGraph.second();
        GraphInfo.setFile(result, file);
        PriorityFileName priorityName = new PriorityFileName(file);
        GraphInfo.setName(result, priorityName.getActualName());
        if (priorityName.hasPriority()) {
            GraphInfo.getProperties(result, true).setPriority(
                priorityName.getPriority());
        }
        if (Groove.isRuleFile(file)) {
            GraphInfo.setRuleRole(result);
        } else if (Groove.isStateFile(file)) {
            GraphInfo.setGraphRole(result);
        }
        if (!Version.isKnownGxlVersion(GraphInfo.getVersion(result))) {
            GraphInfo.addErrors(
                result,
                Arrays.asList(new String[] {String.format(
                    "GXL file format version '%s' is higher than supported version '%s'",
                    GraphInfo.getVersion(result), Version.GXL_VERSION)}));
        }
        return new Pair<Graph,Map<String,Node>>(result, conversion);
    }

    /**
     * Converts an attributed graph to an untyped GXL graph. The attributes are
     * encoded in <tt>AttributeLabel</tt>s.
     * @param graph a graph with only <tt>AttributeLabel</tt>s
     * @return the resulting GXL graph
     */
    private groove.gxl.Graph attrToGxlGraph(Graph graph) {
        groove.gxl.Graph gxlGraph = new groove.gxl.Graph();
        gxlGraph.setEdgeids(false);
        String name = GraphInfo.getName(graph);
        gxlGraph.setId(name == null ? DEFAULT_GRAPH_NAME : name);
        String role = GraphInfo.getRole(graph);
        gxlGraph.setRole(role == null ? Groove.GRAPH_ROLE : role);
        // add the nodes
        Map<Node,groove.gxl.Node> nodeMap = new HashMap<Node,groove.gxl.Node>();
        for (Node node : graph.nodeSet()) {
            // create an xml element for this node
            groove.gxl.Node gxlNode = new groove.gxl.Node();
            nodeMap.put(node, gxlNode);
            // give the element an id
            gxlNode.setId(node.toString());
            // add the node to the graph
            groove.gxl.GraphTypeItem gxlGraphTypeItem =
                new groove.gxl.GraphTypeItem();
            gxlGraphTypeItem.setNode(gxlNode);
            gxlGraph.addGraphTypeItem(gxlGraphTypeItem);
        }
        // add the edges
        for (Edge edge : graph.edgeSet()) {
            // create an xml element for this edge
            groove.gxl.Edge gxlEdge = new groove.gxl.Edge();
            gxlEdge.setFrom(nodeMap.get(edge.source()));
            if (edge.endCount() == 2) {
                gxlEdge.setTo(nodeMap.get(edge.end(Edge.TARGET_INDEX)));
            } else if (edge.endCount() > 2) {
                throw new IllegalArgumentException("Hyperedge " + edge
                    + " not supported");
            }
            // add the attributes from the map
            assert edge.label() instanceof AttributeLabel : "Label "
                + edge.label() + " should have been an attribute label";
            for (Map.Entry<String,String> entry : ((AttributeLabel) edge.label()).getAttributes().entrySet()) {
                groove.gxl.Value gxlLabelText = new groove.gxl.Value();
                gxlLabelText.setString(entry.getValue().toString());
                groove.gxl.Attr gxlLabelAttr = new groove.gxl.Attr();
                gxlLabelAttr.setName(entry.getKey());
                gxlLabelAttr.setValue(gxlLabelText);
                gxlEdge.addAttr(gxlLabelAttr);
            }
            // add the edge to the graph
            groove.gxl.GraphTypeItem gxlGraphTypeItem =
                new groove.gxl.GraphTypeItem();
            gxlGraphTypeItem.setEdge(gxlEdge);
            gxlGraph.addGraphTypeItem(gxlGraphTypeItem);
        }
        // add the graph info
        GraphInfo info = GraphInfo.getInfo(graph, false);
        if (info != null) {
            if (info.hasName()) {
                gxlGraph.setId(info.getName());
            }
            if (info.hasRole()) {
                gxlGraph.setRole(info.getRole());
            }
            // for (Map.Entry<String,Object> infoEntry:
            // info.getData().entrySet()) {
            // if (!GraphInfo.KNOWN_KEYS.contains(infoEntry.getKey())) {
            // String attrName = GraphInfo.INFO_KEY_START + infoEntry.getKey();
            // String value = (String) infoEntry.getValue();
            // groove.gxl.Value gxlValue = new groove.gxl.Value();
            // gxlValue.setString(value);
            // groove.gxl.Attr gxlLabelAttr = new groove.gxl.Attr();
            // gxlLabelAttr.setName(attrName);
            // gxlLabelAttr.setValue(gxlValue);
            // gxlGraph.addAttr(gxlLabelAttr);
            // }
            // }
            // add the graph attributes, if any
            GraphProperties properties = info.getProperties(false);
            if (properties != null) {
                for (Map.Entry<Object,Object> entry : properties.entrySet()) {
                    String attrName = ((String) entry.getKey()).toLowerCase();
                    String value = (String) entry.getValue();
                    groove.gxl.Value gxlValue = new groove.gxl.Value();
                    gxlValue.setString(value);
                    groove.gxl.Attr gxlLabelAttr = new groove.gxl.Attr();
                    gxlLabelAttr.setName(attrName);
                    gxlLabelAttr.setValue(gxlValue);
                    gxlGraph.addAttr(gxlLabelAttr);
                }
            }
        }
        return gxlGraph;
    }

    /**
     * Converts an untyped GXL graph to an attributed (groove) graph. Node
     * attributes are ignored. Edge attributes are encoded in
     * <tt>AttributeLabel</tt>s. The method returns a map from GXL node ids
     * to <tt>Node</tt>s.
     * 
     * @param gxlGraph the source of the unmarhalling
     * @return pair consisting of the resulting attribute graph (with only
     *         <tt>AttributeLabel</tt>s) and a non-<code>null</code> map
     */
    private Pair<Graph,Map<String,Node>> gxlToAttrGraph(
            groove.gxl.Graph gxlGraph) {
        Graph graph = getGraphFactory().newGraph();
        // Hashmap for the ID lookup (ID to Vertex)
        Map<String,Node> nodeIds = new HashMap<String,Node>();
        // iterator over the nodes
        Enumeration<?> nodeEnum = gxlGraph.enumerateGraphTypeItem();
        while (nodeEnum.hasMoreElements()) {
            groove.gxl.GraphTypeItem element =
                (groove.gxl.GraphTypeItem) nodeEnum.nextElement();
            groove.gxl.Node gxlNode = element.getNode();
            if (gxlNode != null) {
                // this is a node
                String nodeId = gxlNode.getId();
                // get graph node from map or create it
                Node node = nodeIds.get(nodeId);
                if (node == null) {
                    // attempt to construct a node from the GXL node ID
                    node = createNode(nodeId);
                    // Add Node to Graph
                    if (node == null || !graph.addNode(node)) {
                        // create a fresh node instead
                        node = graph.addNode();
                    }
                    // Add ID, groove.graph.Node pair to Map
                    nodeIds.put(nodeId, node);
                }
            }
        }
        // Get the edge tags
        Enumeration<?> edgeEnum = gxlGraph.enumerateGraphTypeItem();
        while (edgeEnum.hasMoreElements()) {
            groove.gxl.GraphTypeItem element =
                (groove.gxl.GraphTypeItem) edgeEnum.nextElement();
            groove.gxl.Edge gxlEdge = element.getEdge();
            if (gxlEdge != null) {
                // Create graph Edge
                // Fetch Source node
                String sourceId = ((groove.gxl.Node) gxlEdge.getFrom()).getId();
                Node sourceNode = nodeIds.get(sourceId);
                String targetId = ((groove.gxl.Node) gxlEdge.getTo()).getId();
                Node targetNode = nodeIds.get(targetId);
                // Fetch Label
                Map<String,String> attributes = new HashMap<String,String>();
                Enumeration<?> attrEnum = gxlEdge.enumerateAttr();
                while (attrEnum.hasMoreElements()) {
                    groove.gxl.Attr attr =
                        (groove.gxl.Attr) attrEnum.nextElement();
                    attributes.put(attr.getName().toLowerCase(),
                        attr.getValue().getString());
                }
                graph.addEdge(createEdge(sourceNode, attributes, targetNode));
            }
        }
        // add the graph attributes
        Enumeration<?> attrEnum = gxlGraph.enumerateAttr();
        GraphProperties properties = new GraphProperties();
        while (attrEnum.hasMoreElements()) {
            groove.gxl.Attr attr = (groove.gxl.Attr) attrEnum.nextElement();
            String attrName = attr.getName().toLowerCase();
            groove.gxl.Value attrValue = attr.getValue();
            Object dataValue;
            if (attrValue.hasBool()) {
                dataValue = attrValue.getBool();
            } else if (attrValue.hasInt()) {
                dataValue = attrValue.getInt();
            } else if (attrValue.hasFloat()) {
                dataValue = attrValue.getFloat();
            } else {
                dataValue = attrValue.getString();
            }
            properties.setProperty(attrName, dataValue.toString());
        }
        if (!properties.isEmpty()) {
            GraphInfo.setProperties(graph, properties);
        }
        GraphInfo.setName(graph, gxlGraph.getId());
        GraphInfo.setRole(graph, gxlGraph.getRole());
        return new Pair<Graph,Map<String,Node>>(graph, nodeIds);
    }

    /**
     * Creates a GROOVE node from a GXL node ID, attempting to retain any node
     * number that appears as a suffix in the GXL node ID.
     * @return A GROOVE node with the number in <code>nodeId</code>, or
     *         <code>null</code> if <code>nodeId</code> does not end on a
     *         number.
     */
    private Node createNode(String nodeId) {
        // attempt to construct node number from gxl node
        boolean digitFound = false;
        int nodeNr = 0;
        int unit = 1;
        for (int charIx = nodeId.length() - 1; charIx >= 0
            && Character.isDigit(nodeId.charAt(charIx)); charIx--) {
            nodeNr += unit * (nodeId.charAt(charIx) - '0');
            unit *= 10;
            digitFound = true;
        }
        return digitFound ? DefaultNode.createNode(nodeNr) : null;
    }

    /**
     * Converts an ordinary graph into an attribute graph, by turning edge
     * labels into maps with a <tt>LABEL_ATTR_NAME</tt>-key.
     * @param graph the original graph
     * @return the new, equivalent attribute graph.
     */
    private Graph normToAttrGraph(GraphShape graph) {
        Graph attrGraph = getGraphFactory().newGraph();
        // just copy the nodes
        attrGraph.addNodeSet(graph.nodeSet());
        // turn the edges into attribute maps and store those
        Map<String,String> labelAttr = new HashMap<String,String>(1);
        for (Edge edge : graph.edgeSet()) {
            labelAttr.put(LABEL_ATTR_NAME, edge.label().text());
            attrGraph.addEdge(createEdge(edge.ends(), labelAttr));
        }
        transferGraphInfo(graph, attrGraph);
        attrGraph.setFixed();
        return attrGraph;
    }

    /**
     * Converts an attribute graph into an ordinary graph, by turning maps with
     * a <tt>LABEL_ATTR_NAME</tt>-key into a {@link DefaultLabel}.
     * @param attrGraph the original attributed graph
     * @return the new, equivalent graph.
     */
    private Graph attrToNormGraph(Graph attrGraph) {
        Graph graph = getGraphFactory().newGraph();
        // Simply copy the nodes
        graph.addNodeSet(attrGraph.nodeSet());
        // Take the label value of the attribute labels
        for (Edge edge : attrGraph.edgeSet()) {
            Map<String,String> attributes =
                ((AttributeLabel) edge.label()).getAttributes();
            String labelText = attributes.get(LABEL_ATTR_NAME).trim();
            if (labelText.length() == 0) {
                GraphInfo.addErrors(graph,
                    Arrays.asList("Empty label in graph"));
            } else {
                graph.addEdge(edge.ends(), DefaultLabel.createLabel(labelText));
            }
        }
        transferGraphInfo(attrGraph, graph);
        return graph;
    }

    /**
     * Transfers the graph information items with allowed property names from
     * one graph to another.
     * @param source the source graph
     * @param target the target graph
     */
    private void transferGraphInfo(GraphShape source, Graph target) {
        GraphInfo info = GraphInfo.getInfo(source, false);
        if (info != null) {
            GraphInfo.getInfo(target, true).load(info);
        }
    }

    /**
     * Marshals a GXL graph to an untyped GXL writer.
     * @param gxlGraph the GXL graph
     * @param file the destination for the marshalling operation
     * @throws IOException is the marshalling runs into some IO or XML errors
     */
    private void marshalGxlGraph(groove.gxl.Graph gxlGraph, File file)
        throws IOException {
        groove.gxl.Gxl gxl = new groove.gxl.Gxl();
        gxl.addGraph(gxlGraph);
        try {
            gxl.validate();
            Writer writer = new FileWriter(file);
            Marshaller marshaller = new Marshaller(writer);
            marshaller.marshal(gxl);
            writer.close();
        } catch (MarshalException e) {
            throw new IOException(e.getMessage());
        } catch (ValidationException e) {
            throw new IOException(e.getMessage());
        }
    }

    /**
     * Unmarshals an untyped GXL reader to a GXL graph.
     * @param file the source of the unmarhalling
     * @return the resulting GXL graph
     */
    private groove.gxl.Graph unmarshalGxlGraph(File file) throws IOException,
        FileNotFoundException {
        // get a gxl object from the reader
        groove.gxl.Gxl gxl;
        try {
            gxl = new groove.gxl.Gxl();
            Unmarshaller unmarshaller = new Unmarshaller(gxl);
            unmarshaller.setLogWriter(new PrintWriter(System.err));
            Reader reader = new FileReader(file);
            unmarshaller.unmarshal(reader);
        } catch (MarshalException e) {
            throw new IOException(String.format("Error in %s: %s", file,
                e.getMessage()));
        } catch (ValidationException e) {
            throw new IOException(String.format("Error in %s: %s", file,
                e.getMessage()));
        }

        // now convert the gxl to an attribute graph
        if (gxl.getGraphCount() != 1) {
            throw new IOException(String.format(
                "Only one graph allowed in document %s", file));
        }
        // Get the first and only graph element
        return gxl.getGraph(0);
    }

    /**
     * Callback factory method to create an attribute edge with given ends and
     * attribute map.
     */
    private Edge createEdge(Node[] ends, Map<String,String> attributes) {
        if (ends.length == BinaryEdge.END_COUNT) {
            return new AttributeEdge2(ends, new AttributeLabel(attributes));
        } else {
            return new AttributeEdge1(ends, new AttributeLabel(attributes));
        }
    }

    /**
     * Callback factory method to create an attribute edge with given source
     * node, and a label based on a given attribute map. The edge will be unary
     * of <code>targetNode == null</code>, binary otherwise.
     */
    private Edge createEdge(Node sourceNode, Map<String,String> attributes,
            Node targetNode) {
        if (targetNode == null) {
            return new AttributeEdge1(sourceNode,
                new AttributeLabel(attributes));
        } else {
            return new AttributeEdge2(sourceNode,
                new AttributeLabel(attributes), targetNode);
        }
    }

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
                java.io.File file = new java.io.File(element);
                System.out.println("OK");
                // Unmarshal graph
                System.out.print("    Unmarshalling graph: ");
                Graph graph = gxl.unmarshalGraph(file);
                System.out.println("OK");
                System.out.print("    Creating output file: ");
                file = new java.io.File(element + ".tmp");
                System.out.println("OK");
                System.out.print("    Re-marshalling graph: ");
                gxl.marshalGraph(graph, file);
                System.out.println("OK");
                // unmarshal again and test for isomorphism
                System.out.print("    Testing for isomorphism of original and re-marshalled graph: ");
                Graph newGraph = gxl.unmarshalGraph(file);

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
    static private final IsoChecker isoChecker =
        DefaultIsoChecker.getInstance();

    /**
     * The following implementation of <tt>Label</tt> allows us to use graphs
     * to store attribute maps.
     */
    static private class AttributeLabel extends AbstractLabel {
        /** Constructs an instance, based on a given attribute map. */
        public AttributeLabel(Map<String,String> attributes) {
            this.attributes = new HashMap<String,String>(attributes);
        }

        public String text() {
            return this.attributes.toString();
        }

        @Override
        public String toString() {
            return this.attributes.toString();
        }

        /** Returns the attribute map of this label instance. */
        public Map<String,String> getAttributes() {
            return this.attributes;
        }

        private final Map<String,String> attributes;
    }

    /** Edge carrying an attribute map on its label, with an unknown end count. */
    static private class AttributeEdge2 extends
            AbstractBinaryEdge<Node,AttributeLabel,Node> {
        /** Constructs in instance for given ends and label. */
        public AttributeEdge2(Node[] ends, AttributeLabel label) {
            this(ends[SOURCE_INDEX], label, ends[TARGET_INDEX]);
        }

        AttributeEdge2(Node source, AttributeLabel label, Node target) {
            super(source, label, target);
        }
    }

    /** Edge carrying an attribute map on its label, with an unknown end count. */
    static private class AttributeEdge1 extends
            AbstractUnaryEdge<Node,AttributeLabel> {
        /** Constructs in instance for given ends and label. */
        public AttributeEdge1(Node[] ends, AttributeLabel label) {
            this(ends[SOURCE_INDEX], label);
        }

        AttributeEdge1(Node source, AttributeLabel label) {
            super(source, label);
        }
    }
}