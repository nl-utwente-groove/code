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
import groove.graph.DefaultLabel;
import groove.graph.DefaultNode;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.GraphFactory;
import groove.graph.GraphInfo;
import groove.graph.GraphProperties;
import groove.graph.Label;
import groove.graph.LabelStore;
import groove.graph.Node;
import groove.graph.TypeGraph;
import groove.graph.algebra.ValueNode;
import groove.util.Groove;
import groove.util.Pair;
import groove.util.Version;
import groove.view.FormatError;
import groove.view.aspect.TypeAspect;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import de.gupro.gxl.gxl_1_0.AttrType;
import de.gupro.gxl.gxl_1_0.EdgeType;
import de.gupro.gxl.gxl_1_0.EdgemodeType;
import de.gupro.gxl.gxl_1_0.GraphElementType;
import de.gupro.gxl.gxl_1_0.GraphType;
import de.gupro.gxl.gxl_1_0.GxlType;
import de.gupro.gxl.gxl_1_0.NodeType;
import de.gupro.gxl.gxl_1_0.ObjectFactory;

/**
 * Class to read and write graphs in GXL format, using JXB data binding.
 * @author Arend Rensink
 * @version $Revision: 1568 $
 */
public class JaxbGxlIO implements GxlIO {
    /**
     * Private constructor for the singleton instance.
     */
    private JaxbGxlIO() {
        // empty
    }

    /**
     * Saves a graph to an output stream.
     */
    public void saveGraph(Graph graph, OutputStream out) throws IOException {
        GraphInfo.setVersion(graph, Version.GXL_VERSION);
        GraphType gxlGraph = graphToGxl(graph);
        // now marshal the attribute graph
        try {
            marshal(gxlGraph, out);
        } catch (JAXBException e) {
            throw new IOException(e);
        }
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
     * Converts a graph to an untyped GXL graph.
     * Node types and flag labels as well as {@link ValueNode}s are converted 
     * to prefixed form.
     * If the graph is a {@link TypeGraph}, subtype edges are also added.
     */
    private GraphType graphToGxl(Graph graph) {
        GraphType gxlGraph = this.factory.createGraphType();
        gxlGraph.setEdgeids(false);
        gxlGraph.setEdgemode(EdgemodeType.DIRECTED);
        String name = GraphInfo.getName(graph);
        gxlGraph.setId(name == null ? DEFAULT_GRAPH_NAME : name);
        String role = GraphInfo.getRole(graph);
        gxlGraph.setRole(role == null ? Groove.GRAPH_ROLE : role);
        List<GraphElementType> nodesEdges = gxlGraph.getNodeOrEdgeOrRel();
        // add the nodes
        Map<Node,NodeType> nodeMap = new HashMap<Node,NodeType>();
        for (Node node : graph.nodeSet()) {
            // create an xml element for this node
            NodeType gxlNode = this.factory.createNodeType();
            // give the element an id
            gxlNode.setId(node.toString());
            nodeMap.put(node, gxlNode);
            nodesEdges.add(gxlNode);
            // add appropriate edges for value nodes
            if (node instanceof ValueNode) {
                EdgeType gxlEdge = this.factory.createEdgeType();
                gxlEdge.setFrom(nodeMap.get(node));
                gxlEdge.setTo(nodeMap.get(node));
                AttrType labelAttr = this.factory.createAttrType();
                labelAttr.setName(LABEL_ATTR_NAME);
                labelAttr.setString(((ValueNode) node).getLabelText());
                gxlEdge.getAttr().add(labelAttr);
                nodesEdges.add(gxlEdge);
            }
        }
        // add the edges
        for (Edge edge : graph.edgeSet()) {
            // create an xml element for this edge
            String prefixedLabel = DefaultLabel.toPrefixedString(edge.label());
            if (graph instanceof TypeGraph
                && ((TypeGraph) graph).isAbstract(edge)) {
                prefixedLabel = ABSTRACT_PREFIX + prefixedLabel;
            }
            EdgeType gxlEdge =
                createGxlEdge(nodeMap, edge.source(), prefixedLabel,
                    edge.target());
            nodesEdges.add(gxlEdge);
        }
        // add subtype edges if the graph is a type graph
        if (graph instanceof TypeGraph) {
            LabelStore labelStore = ((TypeGraph) graph).getLabelStore();
            Map<Label,Set<Label>> subtypeMap = labelStore.getDirectSubtypeMap();
            for (Map.Entry<Label,Set<Label>> subtypeEntry : subtypeMap.entrySet()) {
                for (Label subtype : subtypeEntry.getValue()) {
                    for (Edge subtypeEdge : graph.labelEdgeSet(2, subtype)) {
                        Label supertype = subtypeEntry.getKey();
                        for (Edge supertypeEdge : graph.labelEdgeSet(2,
                            supertype)) {
                            nodesEdges.add(createGxlEdge(nodeMap,
                                subtypeEdge.source(), SUBTYPE_PREFIX,
                                supertypeEdge.source()));
                        }
                    }
                }
            }
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
            List<AttrType> graphAttrs = gxlGraph.getAttr();
            GraphProperties properties = info.getProperties(false);
            if (properties != null) {
                for (Map.Entry<Object,Object> entry : properties.entrySet()) {
                    // EZ: Removed this conversion because it causes problems
                    // with rule properties keys.
                    // String attrName = ((String) entry.getKey()).toLowerCase();
                    AttrType attr = this.factory.createAttrType();
                    attr.setName((String) entry.getKey());
                    attr.setString((String) entry.getValue());
                    graphAttrs.add(attr);
                }
            }
        }
        return gxlGraph;
    }

    private EdgeType createGxlEdge(Map<Node,NodeType> nodeMap, Node source,
            String labelText, Node target) {
        EdgeType result = this.factory.createEdgeType();
        result.setFrom(nodeMap.get(source));
        result.setTo(nodeMap.get(target));
        AttrType labelAttr = this.factory.createAttrType();
        labelAttr.setName(LABEL_ATTR_NAME);
        labelAttr.setString(labelText);
        result.getAttr().add(labelAttr);
        return result;
    }

    /**
     * Converts an untyped GXL graph to a (groove) graph.
     * The method returns a map from GXL node ids to {@link Node}s.
     * @param gxlGraph the source of the unmarshalling
     * @return pair consisting of the resulting graph and a non-<code>null</code> map
     */
    private Pair<Graph,Map<String,Node>> gxlToGraph(GraphType gxlGraph) {
        Graph graph = this.graphFactory.newGraph();
        // Hash map for the ID lookup (ID to Vertex)
        Map<String,Node> nodeIds = new HashMap<String,Node>();
        // iterator over the nodes
        for (GraphElementType gxlElement : gxlGraph.getNodeOrEdgeOrRel()) {
            if (gxlElement instanceof NodeType) {
                // this is a node
                String nodeId = gxlElement.getId();
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
        for (GraphElementType gxlElement : gxlGraph.getNodeOrEdgeOrRel()) {
            if (gxlElement instanceof EdgeType) {
                // Create graph Edge
                // Fetch Source node
                String sourceId =
                    ((NodeType) ((EdgeType) gxlElement).getFrom()).getId();
                Node sourceNode = nodeIds.get(sourceId);
                String targetId =
                    ((NodeType) ((EdgeType) gxlElement).getTo()).getId();
                Node targetNode = nodeIds.get(targetId);
                // Fetch Label
                List<AttrType> attrs = ((EdgeType) gxlElement).getAttr();
                assert attrs.size() == 1 : String.format(
                    "More than one edge attribute in %s", attrs);
                AttrType edgeAttr = attrs.get(0);
                assert edgeAttr.getName().equals(LABEL_ATTR_NAME) : String.format(
                    "Unknown edge attribute %s", edgeAttr.getName());
                String label = edgeAttr.getString();
                assert label != null : String.format(
                    "Label attribute %s should have String type", edgeAttr);
                graph.addEdge(createEdge(sourceNode, label, targetNode));
            }
        }
        // add the graph attributes
        GraphProperties properties = new GraphProperties();
        for (AttrType graphAttr : gxlGraph.getAttr()) {
            // EZ: Removed this conversion because it causes problems
            // with rule properties keys.
            // String attrName = attr.getName().toLowerCase();
            String attrName = graphAttr.getName();
            Object dataValue;
            if (graphAttr.isBool() != null) {
                dataValue = graphAttr.isBool();
            } else if (graphAttr.getInt() != null) {
                dataValue = graphAttr.getInt();
            } else if (graphAttr.getFloat() != null) {
                dataValue = graphAttr.getFloat();
            } else {
                dataValue = graphAttr.getString();
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
        return DefaultEdge.createEdge(sourceNode, label, targetNode);
    }

    private void marshal(GraphType gxlGraph, OutputStream out)
        throws JAXBException, IOException {
        GxlType document = new GxlType();
        document.getGraph().add(gxlGraph);
        this.marshaller.marshal(this.factory.createGxl(document), out);
        out.close();
    }

    @SuppressWarnings("unchecked")
    private GraphType unmarshal(InputStream inputStream) throws IOException {
        try {
            JAXBElement<GxlType> doc =
                (JAXBElement<GxlType>) this.unmarshaller.unmarshal(inputStream);
            inputStream.close();
            return doc.getValue().getGraph().get(0);
        } catch (JAXBException e) {
            throw new IOException(String.format("Error in %s: %s", inputStream,
                e.getMessage()));
        }
    }

    /** The graph factory for this marshaller. */
    private final GraphFactory graphFactory = GraphFactory.getInstance();

    /** Reusable context for JAXB (un)marshalling. */
    private JAXBContext context;
    /** Reusable marshaller. */
    private javax.xml.bind.Marshaller marshaller;
    /** Reusable unmarshaller. */
    private javax.xml.bind.Unmarshaller unmarshaller;
    {
        try {
            this.context =
                JAXBContext.newInstance(GxlType.class.getPackage().getName());
            this.unmarshaller = this.context.createUnmarshaller();
            this.marshaller = this.context.createMarshaller();
            this.marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
                Boolean.TRUE);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }
    /** Object factory used for marshalling. */
    private final ObjectFactory factory = new ObjectFactory();

    /** Returns the singleton instance of this class. */
    static public JaxbGxlIO getInstance() {
        return instance;
    }

    /** Singleton instance of the class. */
    static private final JaxbGxlIO instance = new JaxbGxlIO();
    /**
     * The name of graphs whose name is not explicitly included in the graph
     * info.
     */
    static private final String DEFAULT_GRAPH_NAME = "graph";
    /** Attribute name for node and edge identities. */
    static private final String LABEL_ATTR_NAME = "label";
    /** Subtype label. */
    static private final String ABSTRACT_PREFIX = TypeAspect.ABS.getPrefix();
    static private final String SUBTYPE_PREFIX = TypeAspect.SUB.getPrefix();
}