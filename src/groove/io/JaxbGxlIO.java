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

import static groove.view.aspect.AspectKind.ABSTRACT;
import static groove.view.aspect.AspectKind.SUBTYPE;
import groove.graph.DefaultEdge;
import groove.graph.DefaultFactory;
import groove.graph.DefaultGraph;
import groove.graph.DefaultNode;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.GraphInfo;
import groove.graph.GraphProperties;
import groove.graph.GraphRole;
import groove.graph.Label;
import groove.graph.LabelStore;
import groove.graph.Node;
import groove.graph.TypeEdge;
import groove.graph.TypeGraph;
import groove.graph.TypeLabel;
import groove.graph.algebra.ValueNode;
import groove.util.Pair;
import groove.util.Version;
import groove.view.FormatError;
import groove.view.FormatException;

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
    public void saveGraph(Graph<?,?> graph, OutputStream out)
        throws IOException {
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
    public Pair<DefaultGraph,Map<String,DefaultNode>> loadGraphWithMap(
            InputStream in) throws IOException, FormatException {
        try {
            GraphType gxlGraph = unmarshal(in);
            Pair<DefaultGraph,Map<String,DefaultNode>> result =
                gxlToGraph(gxlGraph);
            DefaultGraph graph = result.one();
            if (!Version.isKnownGxlVersion(GraphInfo.getVersion(graph))) {
                GraphInfo.addErrors(
                    graph,
                    Arrays.asList(new FormatError(
                        "GXL file format version '%s' is higher than supported version '%s'",
                        GraphInfo.getVersion(graph), Version.GXL_VERSION)));
            }
            return result;
        } finally {
            in.close();
        }
    }

    /**
     * Loads a graph from an input stream. Convenience method for
     * <code>loadGraphWithMap(in).first()</code>.
     */
    public DefaultGraph loadGraph(InputStream in) throws IOException,
        FormatException {
        return loadGraphWithMap(in).one();
    }

    /** Adds a layout attribute to a gxlNode. */
    /* MdM -
    private void layout(LayoutMap<Node,Edge> map, Node node, NodeType gxl) {
        if (map == null) {
            return;
        }
        JVertexLayout layout = map.nodeMap().get(node);
        if (layout == null) {
            return;
        }
        Rectangle bounds = Groove.toRectangle(layout.getBounds());
        AttrType layoutAttr = this.factory.createAttrType();
        layoutAttr.setName(LAYOUT_ATTR_NAME);
        layoutAttr.setString(bounds.x + " " + bounds.y + " " + bounds.width
            + " " + bounds.height);
        gxl.getAttr().add(layoutAttr);
    }
    */

    /** Adds a layout attribute to a gxlEdge. */
    /* MdM -
    private void layout(LayoutMap<Node,Edge> map, Edge edge, EdgeType gxl) {
        if (map == null) {
            return;
        }
        JEdgeLayout layout = map.edgeMap().get(edge);
        if (layout == null) {
            return;
        }
        AttrType layoutAttr = this.factory.createAttrType();
        layoutAttr.setName(LAYOUT_ATTR_NAME);
        layoutAttr.setString(toString(layout.getLabelPosition()) + " "
            + toString(layout.getPoints()) + " " + layout.getLineStyle());
        gxl.getAttr().add(layoutAttr);
    }
    */

    /** Converts a {@link Point2D} to a text. */
    /* MdM -
    private String toString(Point2D point) {
        return (int) point.getX() + " " + (int) point.getY();
    }
    */

    /** Converts a list of {@link Point2D} to a text. */
    /* MdM -
    private String toString(List<Point2D> points) {
        boolean first = true;
        StringBuilder result = new StringBuilder();

        for (Point2D point : points) {
            if (!first) {
                result.append(" ");
            } else {
                first = false;
            }
            result.append(toString(point));
        }

        return result.toString();
    }
    */

    /**
     * Converts a graph to an untyped GXL graph.
     * Node types and flag labels as well as {@link ValueNode}s are converted 
     * to prefixed form.
     * If the graph is a {@link TypeGraph}, subtype edges are also added.
     */
    private GraphType graphToGxl(Graph<?,?> graph) {
        GraphType gxlGraph = this.factory.createGraphType();
        gxlGraph.setEdgeids(false);
        gxlGraph.setEdgemode(EdgemodeType.DIRECTED);
        gxlGraph.setId(graph.getName());
        gxlGraph.setRole(graph.getRole().toString());
        List<GraphElementType> nodesEdges = gxlGraph.getNodeOrEdgeOrRel();
        // add the nodes
        Map<Node,NodeType> nodeMap = new HashMap<Node,NodeType>();

        /* MdM -
        // get the layout map
        LayoutMap<Node,Edge> layoutMap = null;
        if (GraphInfo.hasLayoutMap(graph)) {
            layoutMap = GraphInfo.getLayoutMap(graph);
        }
        */

        for (Node node : graph.nodeSet()) {
            // create an xml element for this node
            NodeType gxlNode = this.factory.createNodeType();
            // give the element an id
            gxlNode.setId(node.toString());
            // store the layout
            // MdM - layout(layoutMap, node, gxlNode);

            nodeMap.put(node, gxlNode);
            nodesEdges.add(gxlNode);
            // add appropriate edges for value nodes
            if (node instanceof ValueNode) {
                EdgeType gxlEdge = this.factory.createEdgeType();
                gxlEdge.setFrom(nodeMap.get(node));
                gxlEdge.setTo(nodeMap.get(node));
                AttrType labelAttr = this.factory.createAttrType();
                labelAttr.setName(LABEL_ATTR_NAME);
                labelAttr.setString(((ValueNode) node).toString());
                gxlEdge.getAttr().add(labelAttr);
                nodesEdges.add(gxlEdge);
            }
        }
        // add the edges
        for (Edge<?> edge : graph.edgeSet()) {
            // create an xml element for this edge
            String prefixedLabel = TypeLabel.toPrefixedString(edge.label());
            if (edge instanceof TypeEdge && ((TypeEdge) edge).isAbstract()) {
                prefixedLabel = ABSTRACT_PREFIX + prefixedLabel;
            }
            EdgeType gxlEdge =
                createGxlEdge(nodeMap, edge.source(), prefixedLabel,
                    edge.target());
            nodesEdges.add(gxlEdge);
            // store the layout
            // MdM - layout(layoutMap, edge, gxlEdge);
        }
        // add subtype edges if the graph is a type graph
        if (graph instanceof TypeGraph) {
            LabelStore labelStore = ((TypeGraph) graph).getLabelStore();
            Map<TypeLabel,Set<TypeLabel>> subtypeMap =
                labelStore.getDirectSubtypeMap();
            for (Map.Entry<TypeLabel,Set<TypeLabel>> subtypeEntry : subtypeMap.entrySet()) {
                for (TypeLabel subtype : subtypeEntry.getValue()) {
                    for (Edge<?> subtypeEdge : graph.labelEdgeSet(subtype)) {
                        Label supertype = subtypeEntry.getKey();
                        for (Edge<?> supertypeEdge : graph.labelEdgeSet(supertype)) {
                            nodesEdges.add(createGxlEdge(nodeMap,
                                subtypeEdge.source(), SUBTYPE_PREFIX,
                                supertypeEdge.source()));
                        }
                    }
                }
            }
        }
        // add the graph info
        GraphInfo<?,?> info = GraphInfo.getInfo(graph, false);
        if (info != null) {
            gxlGraph.setId(graph.getName());
            gxlGraph.setRole(graph.getRole().toString());
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
     * Gets the value of a specific attribute from a list of attributes that
     * belong to a gxl element. Throws an exception if the value of the
     * attribute is not of type <code>string</code>, using the
     * <code>gxlElement</code> argument to produce a traceable message. Returns
     * <code>null</code> when the attribute does not appear in the list.
     */
    private String getAttrValue(String attrName, List<AttrType> attrs,
            String gxlElement) throws FormatException {
        for (AttrType attr : attrs) {
            if (attr.getName().equals(attrName)) {
                String value = attr.getString();
                if (value == null) {
                    throw new FormatException("The " + attrName
                        + " attribute of " + gxlElement
                        + " must have type <string>.");
                }
                return value;
            }
        }
        return null;
    }

    /**
     * Converts an untyped GXL graph to a (groove) graph.
     * The method returns a map from GXL node ids to {@link Node}s.
     * @param gxlGraph the source of the unmarshalling
     * @return pair consisting of the resulting graph and a non-<code>null</code> map
     */
    private Pair<DefaultGraph,Map<String,DefaultNode>> gxlToGraph(
            GraphType gxlGraph) throws FormatException {

        // Initialize the new objects to be created.
        DefaultGraph graph = createGraph(gxlGraph.getId());
        Map<String,DefaultNode> nodeIds = new HashMap<String,DefaultNode>();
        // MdM - LayoutMap<Node,Edge> layoutMap = new LayoutMap();

        // Extract nodes out of the gxl elements.
        for (GraphElementType gxlElement : gxlGraph.getNodeOrEdgeOrRel()) {
            if (gxlElement instanceof NodeType) {
                // Extract the node id and create the node out of it.
                String nodeId = gxlElement.getId();
                if (nodeIds.containsKey(nodeId)) {
                    throw new FormatException("The node " + nodeId
                        + " is declared more than once.");
                }
                DefaultNode node = createNode(nodeId);
                // Extract the layout from the gxlElement attributes.
                /* MdM -
                List<AttrType> attrs = ((NodeType) gxlElement).getAttr();
                MdM - String layout =
                MdM -     getAttrValue(LAYOUT_ATTR_NAME, attrs, "node " + nodeId);
                MdM - TODO - create layout (if not null)
                */
                // Add the node to the graph and the idMap.
                if (node == null || !graph.addNode(node)) {
                    node = graph.addNode(); // create fresh node
                }
                nodeIds.put(nodeId, node);
            }
        }

        // Extract nodes out of the gxl elements.
        for (GraphElementType gxlElement : gxlGraph.getNodeOrEdgeOrRel()) {
            if (gxlElement instanceof EdgeType) {
                // Find the source node of the edge.
                String sourceId =
                    ((NodeType) ((EdgeType) gxlElement).getFrom()).getId();
                DefaultNode sourceNode = nodeIds.get(sourceId);
                if (sourceNode == null) {
                    throw new FormatException(
                        "Unable to find edge source node " + sourceId + ".");
                }
                // Find the target node of the edge.
                String targetId =
                    ((NodeType) ((EdgeType) gxlElement).getTo()).getId();
                DefaultNode targetNode = nodeIds.get(targetId);
                if (targetNode == null) {
                    throw new FormatException(
                        "Unable to find edge target node " + sourceId + ".");
                }
                // Set context for error messages.
                String context = "edge " + sourceId + "->" + targetId;
                // Extract the label and the layout from the gxlElement attributes.
                List<AttrType> attrs = ((EdgeType) gxlElement).getAttr();
                String label = getAttrValue(LABEL_ATTR_NAME, attrs, context);
                if (label == null) {
                    throw new FormatException("The " + context
                        + " must have a " + LABEL_ATTR_NAME + " attribute.");
                }
                // MdM - String layout = getAttrValue(LAYOUT_ATTR_NAME, attrs, context);
                // MdM - TODO - create layout (if not null)
                // Add the edge to the graph.
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
        String roleName = gxlGraph.getRole();
        graph.setRole(roleName == null ? GraphRole.HOST
                : GraphRole.roles.get(roleName));
        // MdM - GraphInfo.setLayoutMap(graph, layoutMap);
        return new Pair<DefaultGraph,Map<String,DefaultNode>>(graph, nodeIds);
    }

    /**
     * Creates a GROOVE node from a GXL node ID, attempting to retain any node
     * number that appears as a suffix in the GXL node ID.
     * @return A GROOVE node with the number in <code>nodeId</code>, or
     *         <code>null</code> if <code>nodeId</code> does not end on a
     *         number.
     */
    private DefaultNode createNode(String nodeId) {
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
        return digitFound ? elementFactory.createNode(nodeNr) : null;
    }

    /**
     * Callback factory method to create an attribute edge with given source
     * node, and a label based on a given attribute map. The edge will be unary
     * of <code>targetNode == null</code>, binary otherwise.
     */
    private DefaultEdge createEdge(DefaultNode sourceNode, String label,
            DefaultNode targetNode) {
        return elementFactory.createEdge(sourceNode, label, targetNode);
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

    private DefaultGraph createGraph(String name) {
        return new DefaultGraph(name);
    }

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

    static private final DefaultFactory elementFactory =
        DefaultFactory.instance();
    /** Singleton instance of the class. */
    static private final JaxbGxlIO instance = new JaxbGxlIO();
    /** Attribute name for node and edge identities. */
    static private final String LABEL_ATTR_NAME = "label";
    /** Attribute name for layout information. */
    @SuppressWarnings("unused")
    static private final String LAYOUT_ATTR_NAME = "layout";
    /** Subtype label. */
    static private final String ABSTRACT_PREFIX =
        ABSTRACT.getAspect().toString();
    static private final String SUBTYPE_PREFIX = SUBTYPE.getAspect().toString();
}