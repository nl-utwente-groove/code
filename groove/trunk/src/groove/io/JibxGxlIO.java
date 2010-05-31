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
import groove.graph.DefaultNode;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.GraphFactory;
import groove.graph.GraphInfo;
import groove.graph.GraphProperties;
import groove.graph.Node;
import groove.util.Groove;
import groove.util.Pair;
import groove.util.Version;
import groove.view.FormatError;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;

import de.gupro.gxl.gxl1.AttrType;
import de.gupro.gxl.gxl1.EdgeType;
import de.gupro.gxl.gxl1.EdgemodeType;
import de.gupro.gxl.gxl1.GraphType;
import de.gupro.gxl.gxl1.GxlType;
import de.gupro.gxl.gxl1.NodeType;
import de.gupro.gxl.gxl1.Value;
import de.gupro.gxl.gxl1._String;

/**
 * Class to read and write graphs in GXL format, using JXB data binding.
 * @author Arend Rensink
 * @version $Revision: 1568 $
 */
public class JibxGxlIO {
    /**
     * Private constructor for the singleton instance.
     */
    private JibxGxlIO() {
        // empty
    }

    /**
     * Saves a graph to an output stream.
     */
    public void saveGraph(Graph graph, OutputStream out) throws IOException {
        GraphInfo.setVersion(graph, Version.GXL_VERSION);
        GraphType gxlGraph = graphToGxl(graph);
        // now marshal the attribute graph
        marshal(gxlGraph, out);
    }

    /**
     * Loads a graph plus mapping information from an input stream. The mapping
     * information consists of a map from node identities as they occur in the
     * input to node identities in the resulting graph.
     */
    public Pair<Graph,Map<String,Node>> loadGraphWithMap(InputStream in)
        throws IOException {
        GraphType gxlGraph = unmarshal(in);
        in.close();
        Pair<Graph,Map<String,Node>> result = gxlToGraph(gxlGraph);
        Graph graph = result.first();
        if (!Version.isKnownGxlVersion(GraphInfo.getVersion(graph))) {
            GraphInfo.addErrors(
                graph,
                Arrays.asList(new FormatError(
                    "GXL file format version '%s' is higher than supported version '%s'",
                    GraphInfo.getVersion(graph), Version.GXL_VERSION)));
        }
        return result;
    }

    /**
     * Loads a graph from an input stream. Convenience method for
     * <code>loadGraphWithMap(in).first()</code>.
     */
    public Graph loadGraph(InputStream in) throws IOException {
        return loadGraphWithMap(in).first();
    }

    /**
     * Converts an attributed graph to an untyped GXL graph. The attributes are
     * encoded in <tt>AttributeLabel</tt>s.
     * @param graph a graph with only <tt>AttributeLabel</tt>s
     * @return the resulting GXL graph
     */
    private GraphType graphToGxl(Graph graph) {
        GraphType gxlGraph = new GraphType();
        gxlGraph.setEdgeids(false);
        gxlGraph.setEdgemode(EdgemodeType.DIRECTED);
        String name = GraphInfo.getName(graph);
        gxlGraph.setId(name == null ? DEFAULT_GRAPH_NAME : name);
        String role = GraphInfo.getRole(graph);
        gxlGraph.setRole(role == null ? Groove.GRAPH_ROLE : role);
        List<GraphType.Choice> nodesEdges = new ArrayList<GraphType.Choice>();
        gxlGraph.setChoiceList(nodesEdges);
        // add the nodes
        Map<Node,NodeType> nodeMap = new HashMap<Node,NodeType>();
        for (Node node : graph.nodeSet()) {
            // create an xml element for this node
            NodeType gxlNode = new NodeType();
            // give the element an id
            gxlNode.setId(node.toString());
            nodeMap.put(node, gxlNode);
            // wrap the gxl node into a choice
            GraphType.Choice nodeWrapper = new GraphType.Choice();
            nodeWrapper.setNode(gxlNode);
            nodesEdges.add(nodeWrapper);
        }
        // add the edges
        for (Edge edge : graph.edgeSet()) {
            // create an xml element for this edge
            EdgeType gxlEdge = new EdgeType();
            gxlEdge.setFrom(nodeMap.get(edge.source()).toString());
            gxlEdge.setTo(nodeMap.get(edge.opposite()).toString());
            _String attrValueString = new _String();
            attrValueString.setString(edge.label().text());
            Value attrValue = new Value();
            attrValue.setString(attrValueString);
            AttrType labelAttr = new AttrType();
            labelAttr.setName(LABEL_ATTR_NAME);
            labelAttr.setValue(attrValue);
            gxlEdge.getAttrList().add(labelAttr);
            // wrap the gxl edge into a choice
            GraphType.Choice edgeWrapper = new GraphType.Choice();
            edgeWrapper.setEdge(gxlEdge);
            nodesEdges.add(edgeWrapper);
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
            // add the graph attributes, if any
            List<AttrType> graphAttrs = new ArrayList<AttrType>();
            gxlGraph.setAttrList(graphAttrs);
            GraphProperties properties = info.getProperties(false);
            if (properties != null) {
                for (Map.Entry<Object,Object> entry : properties.entrySet()) {
                    // EZ: Removed this conversion because it causes problems
                    // with rule properties keys.
                    // String attrName = ((String) entry.getKey()).toLowerCase();
                    _String attrValueString = new _String();
                    attrValueString.setString((String) entry.getValue());
                    Value attrValue = new Value();
                    attrValue.setString(attrValueString);
                    AttrType attr = new AttrType();
                    attr.setName((String) entry.getKey());
                    attr.setValue(attrValue);
                    graphAttrs.add(attr);
                }
            }
        }
        return gxlGraph;
    }

    /**
     * Converts an untyped GXL graph to an attributed (groove) graph. Node
     * attributes are ignored. Edge attributes are encoded in
     * <tt>AttributeLabel</tt>s. The method returns a map from GXL node ids to
     * <tt>Node</tt>s.
     * 
     * @param gxlGraph the source of the unmarshalling
     * @return pair consisting of the resulting attribute graph (with only
     *         <tt>AttributeLabel</tt>s) and a non-<code>null</code> map
     */
    private Pair<Graph,Map<String,Node>> gxlToGraph(GraphType gxlGraph) {
        Graph graph = this.graphFactory.newGraph();
        // Hash map for the ID lookup (ID to Vertex)
        Map<String,Node> nodeIds = new HashMap<String,Node>();
        // iterator over the nodes
        List<GraphType.Choice> gxlElementList = gxlGraph.getChoiceList();
        if (gxlElementList != null) {
            for (GraphType.Choice gxlElement : gxlElementList) {
                if (gxlElement.ifNode()) {
                    // this is a node
                    String nodeId = gxlElement.getNode().getId();
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
            for (GraphType.Choice gxlElement : gxlElementList) {
                if (gxlElement.ifEdge()) {
                    // Create graph Edge
                    EdgeType gxlEdge = gxlElement.getEdge();
                    // Fetch Source node
                    String sourceId = gxlEdge.getFrom();
                    Node sourceNode = nodeIds.get(sourceId);
                    String targetId = gxlEdge.getTo();
                    Node targetNode = nodeIds.get(targetId);
                    // Fetch Label
                    List<AttrType> attrs = gxlEdge.getAttrList();
                    assert attrs.size() == 1 : String.format(
                        "More than one edge attribute in %s", attrs);
                    AttrType edgeAttr = attrs.get(0);
                    assert edgeAttr.getName().equals(LABEL_ATTR_NAME) : String.format(
                        "Unknown edge attribute %s", edgeAttr.getName());
                    String label = edgeAttr.getValue().getString().getString();
                    assert label != null : String.format(
                        "Label attribute %s should have String type", edgeAttr);
                    graph.addEdge(createEdge(sourceNode, label, targetNode));
                }
            }
        }
        // add the graph attributes
        GraphProperties properties = new GraphProperties();
        List<AttrType> graphAttrList = gxlGraph.getAttrList();
        if (graphAttrList != null) {
            for (AttrType graphAttr : graphAttrList) {
                // EZ: Removed this conversion because it causes problems
                // with rule properties keys.
                // String attrName = attr.getName().toLowerCase();
                String attrName = graphAttr.getName();
                Value attrValue = graphAttr.getValue();
                Object dataValue;
                if (attrValue.ifBool()) {
                    dataValue = attrValue.getBool().isBool();
                } else if (attrValue.ifInt()) {
                    dataValue = attrValue.getInt().getInt();
                } else if (attrValue.ifFloat()) {
                    dataValue = attrValue.getFloat().getFloat();
                } else {
                    dataValue = attrValue.getString().getString();
                }
                properties.setProperty(attrName, dataValue.toString());
            }
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
        // by looking at trailing number shape of node id
        boolean digitFound = false;
        int nodeNr = 0;
        int unit = 1;
        int charIx;
        for (charIx = nodeId.length() - 1; charIx >= 0
            && Character.isDigit(nodeId.charAt(charIx)); charIx--) {
            nodeNr += unit * (nodeId.charAt(charIx) - '0');
            unit *= 10;
            digitFound = true;
        }
        if (charIx >= 0 && nodeId.charAt(charIx) == '-') {
            nodeNr = -nodeNr;
        }
        return digitFound ? DefaultNode.createNode(nodeNr) : null;
    }

    /**
     * Callback factory method to create an attribute edge with given source
     * node, and a label based on a given attribute map. The edge will be unary
     * of <code>targetNode == null</code>, binary otherwise.
     */
    private Edge createEdge(Node sourceNode, String label, Node targetNode) {
        //return DefaultEdge.createEdge(sourceNode, label, targetNode);
        return DefaultEdge.createEdgeWithLabelPrefix(sourceNode, label,
            targetNode);
    }

    private void marshal(GraphType gxlGraph, OutputStream out)
        throws IOException {
        try {
            List<GraphType> graphList = new ArrayList<GraphType>();
            graphList.add(gxlGraph);
            GxlType document = new GxlType();
            document.setGraphList(graphList);
            this.mctx.marshalDocument(document, "UTF-8", null, out);
        } catch (JiBXException e) {
            throw new IOException(String.format(
                "Error while marshalling %s: %s", gxlGraph.getId(),
                e.getMessage()));
        }
    }

    private GraphType unmarshal(InputStream inputStream) throws IOException {
        try {
            GxlType gxl =
                (GxlType) this.uctx.unmarshalDocument(inputStream, null);
            return gxl.getGraphList().get(0);
        } catch (JiBXException e) {
            throw new IOException(String.format("Error in %s: %s", inputStream,
                e.getMessage()));
        }
    }

    /** The graph factory for this marshaller. */
    private final GraphFactory graphFactory = GraphFactory.getInstance();

    private IBindingFactory bfact;
    private IUnmarshallingContext uctx;
    //    Object obj = uctx.unmarshalDocument
    //        (new FileInputStream("filename.xml"), null);
    private IMarshallingContext mctx;
    //    mctx.marshalDocument(obj, "UTF-8", null,
    //        new FileOutputStream("filename.xml"));
    {
        try {
            this.bfact = BindingDirectory.getFactory(GxlType.class);
            this.uctx = this.bfact.createUnmarshallingContext();
            this.mctx = this.bfact.createMarshallingContext();
        } catch (JiBXException e) {
            e.printStackTrace();
        }
    }

    /** Returns the singleton instance of this class. */
    static public JibxGxlIO getInstance() {
        return instance;
    }

    /** Singleton instance of the class. */
    static private final JibxGxlIO instance = new JibxGxlIO();
    /**
     * The name of graphs whose name is not explicitly included in the graph
     * info.
     */
    static private final String DEFAULT_GRAPH_NAME = "graph";
    /** Attribute name for node and edge identities. */
    static private final String LABEL_ATTR_NAME = "label";
}