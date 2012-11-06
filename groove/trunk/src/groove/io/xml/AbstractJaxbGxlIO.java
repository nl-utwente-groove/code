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
package groove.io.xml;

import static groove.view.aspect.AspectKind.ABSTRACT;
import static groove.view.aspect.AspectKind.SUBTYPE;
import groove.graph.DefaultGraph;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.GraphInfo;
import groove.graph.GraphProperties;
import groove.graph.GraphRole;
import groove.graph.Node;
import groove.graph.TypeEdge;
import groove.graph.TypeGraph;
import groove.graph.TypeLabel;
import groove.graph.TypeNode;
import groove.graph.algebra.ValueNode;
import groove.gui.jgraph.JAttr;
import groove.gui.layout.JEdgeLayout;
import groove.gui.layout.JVertexLayout;
import groove.gui.layout.LayoutMap;
import groove.io.LayoutIO;
import groove.util.Groove;
import groove.util.Pair;
import groove.util.Version;
import groove.view.FormatErrorSet;
import groove.view.FormatException;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
public abstract class AbstractJaxbGxlIO<N extends Node,E extends Edge>
        implements GxlIO<N,E> {

    /**
     * Saves a graph to an output stream.
     */
    public void saveGraph(Graph<?,?> graph, OutputStream out)
        throws IOException {
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
    public Pair<Graph<N,E>,Map<String,N>> loadGraphWithMap(InputStream in)
        throws IOException, FormatException {
        try {
            GraphType gxlGraph = unmarshal(in);
            Pair<Graph<N,E>,Map<String,N>> result = gxlToGraph(gxlGraph);
            Graph<N,E> graph = result.one();
            if (!Version.isKnownGxlVersion(GraphInfo.getVersion(graph))) {
                GraphInfo.addErrors(
                    graph,
                    new FormatErrorSet(
                        "GXL file format version '%s' is higher than supported version '%s'",
                        GraphInfo.getVersion(graph), Version.GXL_VERSION));
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
    public Graph<N,E> loadGraph(InputStream in) throws IOException,
        FormatException {
        return loadGraphWithMap(in).one();
    }

    /** Adds a layout attribute to a gxlNode. */
    private void layout(LayoutMap<?,?> map, Node node, NodeType gxl) {
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

    /** Adds a layout attribute to a gxlEdge. */
    private void layout(LayoutMap<?,?> map, Edge edge, EdgeType gxl) {
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

    /** Converts a {@link Point2D} to a text. */
    private String toString(Point2D point) {
        return (int) point.getX() + " " + (int) point.getY();
    }

    /** Converts a list of {@link Point2D} to a text. */
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
        Map<Node,NodeType> nodeMap = new HashMap<Node,NodeType>();
        Map<Edge,EdgeType> edgeMap = new HashMap<Edge,EdgeType>();

        // get the layout map
        LayoutMap<?,?> layoutMap = GraphInfo.getLayoutMap(graph);

        for (Node node : graph.nodeSet()) {
            // create an xml element for this node
            NodeType gxlNode = this.factory.createNodeType();
            // give the element an id
            gxlNode.setId(node.toString());
            // store the layout
            layout(layoutMap, node, gxlNode);

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
        for (Edge edge : graph.edgeSet()) {
            // create an xml element for this edge
            String prefixedLabel = TypeLabel.toPrefixedString(edge.label());
            if (edge instanceof TypeEdge && ((TypeEdge) edge).isAbstract()) {
                prefixedLabel = ABSTRACT_PREFIX + prefixedLabel;
            }
            EdgeType gxlEdge =
                createGxlEdge(nodeMap, edge.source(), prefixedLabel,
                    edge.target());
            edgeMap.put(edge, gxlEdge);
            nodesEdges.add(gxlEdge);
            // store the layout
            layout(layoutMap, edge, gxlEdge);
        }
        // add subtype edges if the graph is a type graph
        if (graph instanceof TypeGraph) {
            TypeGraph typeGraph = (TypeGraph) graph;
            Map<TypeNode,Set<TypeNode>> subtypeMap =
                typeGraph.getDirectSubtypeMap();
            for (Map.Entry<TypeNode,Set<TypeNode>> subtypeEntry : subtypeMap.entrySet()) {
                for (TypeNode subtype : subtypeEntry.getValue()) {
                    TypeNode supertype = subtypeEntry.getKey();
                    nodesEdges.add(createGxlEdge(nodeMap, subtype,
                        SUBTYPE_PREFIX, supertype));
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
            GraphProperties properties = info.getProperties();
            for (Map.Entry<Object,Object> entry : properties.entrySet()) {
                // EZ: Removed this conversion because it causes problems
                // with rule properties keys.
                // String attrName = ((String) entry.getKey()).toLowerCase();
                AttrType attr = this.factory.createAttrType();
                attr.setName((String) entry.getKey());
                attr.setString((String) entry.getValue());
                graphAttrs.add(attr);
            }
            // Add version info
            AttrType attr = this.factory.createAttrType();
            attr.setName(GraphProperties.Key.VERSION.getName());
            attr.setString(Version.GXL_VERSION);
            graphAttrs.add(attr);
        }
        // Maybe there are some additional structure that we want to store.
        this.storeAdditionalStructure(graph, gxlGraph, nodeMap, edgeMap);
        return gxlGraph;
    }

    /**
     * Stores additional structure information from the given graph into the
     * given gxlGraph. This is used, for example, with shapes (abstraction).
     */
    protected void storeAdditionalStructure(Graph<?,?> graph,
            GraphType gxlGraph, Map<Node,NodeType> nodeMap,
            Map<Edge,EdgeType> edgeMap) {
        // Empty by design. To be overriden by subclasses.
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
    protected String getAttrValue(String attrName, List<AttrType> attrs,
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
    private Pair<Graph<N,E>,Map<String,N>> gxlToGraph(GraphType gxlGraph)
        throws FormatException {

        // Initialize the new objects to be created.
        Graph<N,E> graph = createGraph(gxlGraph.getId());
        Map<String,N> nodeIds = new HashMap<String,N>();
        Map<EdgeType,E> edgeMap = new HashMap<EdgeType,E>();
        LayoutMap<N,E> layoutMap = new LayoutMap<N,E>();

        // Extract nodes out of the gxl elements.
        for (GraphElementType gxlElement : gxlGraph.getNodeOrEdgeOrRel()) {
            if (gxlElement instanceof NodeType) {
                // Extract the node id and create the node out of it.
                String nodeId = gxlElement.getId();
                if (nodeIds.containsKey(nodeId)) {
                    throw new FormatException("The node " + nodeId
                        + " is declared more than once.");
                }
                N node = createNode(nodeId);
                // Extract the layout from the gxlElement attributes.
                List<AttrType> attrs = ((NodeType) gxlElement).getAttr();

                // Save the layout.
                String layout =
                    getAttrValue(LAYOUT_ATTR_NAME, attrs, "node " + nodeId);
                if (layout != null) {
                    String[] parts = layout.split(" ");
                    Rectangle bounds = LayoutIO.toBounds(parts, 0);
                    if (bounds == null) {
                        throw new FormatException("Bounds for " + parts[1]
                            + " cannot be parsed");
                    }
                    layoutMap.putNode(node, new JVertexLayout(bounds));
                }
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
                N sourceNode = nodeIds.get(sourceId);
                if (sourceNode == null) {
                    throw new FormatException(
                        "Unable to find edge source node " + sourceId + ".");
                }
                // Find the target node of the edge.
                String targetId =
                    ((NodeType) ((EdgeType) gxlElement).getTo()).getId();
                N targetNode = nodeIds.get(targetId);
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

                // Create the edge object.
                E edge = createEdge(sourceNode, label, targetNode);
                edgeMap.put((EdgeType) gxlElement, edge);

                // Extract the layout.
                String layout = getAttrValue(LAYOUT_ATTR_NAME, attrs, context);
                if (layout != null) {
                    String[] parts = layout.split(" ");
                    List<Point2D> points;
                    int lineStyle;
                    if (parts.length > 2) {
                        points = LayoutIO.toPoints(parts, 2);
                        // if we have fewer than 2 points, something is wrong
                        if (points.size() <= 1) {
                            throw new FormatException(
                                "Edge layout needs at least 2 points");
                        }
                        lineStyle = Integer.parseInt(parts[parts.length - 1]);
                        if (!JAttr.isLineStyle(lineStyle)) {
                            lineStyle = JAttr.DEFAULT_LINE_STYLE;
                        }
                        if (layoutMap.getLayout(sourceNode) != null &&
                            layoutMap.getLayout(targetNode) != null) {
                            LayoutIO.correctPoints(points,
                                layoutMap.getLayout(sourceNode),
                                layoutMap.getLayout(targetNode));
                        }
                        Point2D labelPosition =
                            LayoutIO.calculateLabelPosition(
                                LayoutIO.toPoint(parts, 0), points,
                                LayoutIO.VERSION2, sourceNode == targetNode);
                        layoutMap.putEdge(edge, new JEdgeLayout(points,
                            labelPosition, lineStyle));
                    }
                }

                // Add the edge to the graph.
                graph.addEdge(edge);
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
        if (graph instanceof DefaultGraph) {
            ((DefaultGraph) graph).setRole(roleName == null ? GraphRole.HOST
                    : GraphRole.roles.get(roleName));
        }
        GraphInfo.setLayoutMap(graph, layoutMap);
        loadAdditionalStructure(graph, gxlGraph, nodeIds, edgeMap);
        return new Pair<Graph<N,E>,Map<String,N>>(graph, nodeIds);
    }

    /**
     * Loads additional structure information from the given gxlGraph into the
     * given graph. This is used, for example, with shapes (abstraction).
     */
    @SuppressWarnings("unused")
    protected void loadAdditionalStructure(Graph<N,E> graph,
            GraphType gxlGraph, Map<String,N> nodeMap, Map<EdgeType,E> edgeMap)
        throws FormatException {
        // Empty by design. To be overriden by subclasses.
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

    /** Creates a graph with the proper type. */
    protected abstract Graph<N,E> createGraph(String name);

    /**
     * Creates a GROOVE node from a GXL node ID, attempting to retain any node
     * number that appears as a suffix in the GXL node ID.
     * @return A GROOVE node with the number in <code>nodeId</code>, or
     *         <code>null</code> if <code>nodeId</code> does not end on a
     *         number.
     */
    protected abstract N createNode(String nodeId);

    /**
     * Callback factory method to create an attribute edge with given source
     * node, and a label based on a given attribute map. The edge will be unary
     * if <code>targetNode == null</code>, binary otherwise.
     */
    protected abstract E createEdge(N sourceNode, String label, N targetNode);

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
    protected final ObjectFactory factory = new ObjectFactory();

    /** Attribute name for node and edge identities. */
    static private final String LABEL_ATTR_NAME = "label";
    /** Attribute name for layout information. */
    static private final String LAYOUT_ATTR_NAME = "layout";
    /** Subtype label. */
    static private final String ABSTRACT_PREFIX =
        ABSTRACT.getAspect().toString();
    static private final String SUBTYPE_PREFIX = SUBTYPE.getAspect().toString();
}