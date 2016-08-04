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
 * $Id$
 */
package groove.io.graph;

import static groove.grammar.aspect.AspectKind.ABSTRACT;
import static groove.grammar.aspect.AspectKind.SUBTYPE;
import static groove.io.FileType.LAYOUT;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
import de.gupro.gxl.gxl_1_0.RelType;
import de.gupro.gxl.gxl_1_0.RelendType;
import de.gupro.gxl.gxl_1_0.TypedElementType;
import groove.grammar.host.ValueNode;
import groove.grammar.type.TypeEdge;
import groove.grammar.type.TypeGraph;
import groove.grammar.type.TypeLabel;
import groove.grammar.type.TypeNode;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.GraphInfo;
import groove.graph.GraphProperties;
import groove.graph.GraphRole;
import groove.graph.Node;
import groove.gui.layout.JEdgeLayout;
import groove.gui.layout.JVertexLayout;
import groove.gui.layout.LayoutMap;
import groove.io.FileType;
import groove.util.Groove;
import groove.util.Version;
import groove.util.line.LineStyle;
import groove.util.parse.FormatErrorSet;
import groove.util.parse.FormatException;

/**
 * Class to convert graphs to GXL format and back.
 * This class is implemented using JAXB data binding.
 * @author Arend Rensink
 * @version $Revision$
 */
public class GxlIO extends GraphIO<AttrGraph> {
    private GxlIO() {
        // Private to avoid object creation. Use getInstance() method.
    }

    @Override
    public void deleteGraph(File file) {
        deleteFile(file);
        // delete the layout file as well, if any
        deleteFile(toLayoutFile(file));
    }

    /**
     * Converts a file containing a graph to the file containing the graph's
     * layout information, by adding <code>Groove.LAYOUT_EXTENSION</code> to the
     * file name.
     */
    private File toLayoutFile(File graphFile) {
        return new File(LAYOUT.addExtension(graphFile.toString()));
    }

    @Override
    public boolean canSave() {
        return true;
    }

    @Override
    public void saveGraph(Graph graph, File file) throws IOException {
        super.saveGraph(graph, file);
        // layout is now saved in the gxl file; delete the layout file
        deleteFile(toLayoutFile(file));
    }

    /**
     * Saves a graph to an output stream.
     */
    @Override
    protected void doSaveGraph(Graph graph, File file) throws IOException {
        GraphType gxlGraph = graphToGxl(graph);
        // now marshal the attribute graph
        try {
            GxlType document = new GxlType();
            document.getGraph()
                .add(gxlGraph);
            this.marshaller.marshal(this.factory.createGxl(document), file);
        } catch (JAXBException e) {
            throw new IOException(e);
        }
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
        gxlGraph.setId(graph.getName());
        gxlGraph.setRole(graph.getRole()
            .toString());
        List<GraphElementType> nodesEdges = gxlGraph.getNodeOrEdgeOrRel();
        Map<Node,NodeType> nodeMap = new HashMap<>();
        Map<Edge,EdgeType> edgeMap = new HashMap<>();

        // get the layout map
        LayoutMap layoutMap = GraphInfo.getLayoutMap(graph);

        for (Node node : graph.nodeSet()) {
            // create an xml element for this node
            NodeType gxlNode = this.factory.createNodeType();
            // give the element an id based on the node number
            gxlNode.setId("n" + node.getNumber());
            if (layoutMap != null) {
                // store the layout
                storeNodeLayout(layoutMap, node, gxlNode);
            }
            nodeMap.put(node, gxlNode);
            nodesEdges.add(gxlNode);
            // add appropriate edges for value nodes
            if (node instanceof ValueNode) {
                EdgeType gxlEdge =
                    createGxlEdge(nodeMap, node, ((ValueNode) node).toString(), node);
                nodesEdges.add(gxlEdge);
            }
            // add attributes of XML nodes
            if (node instanceof AttrNode) {
                saveAttributes(gxlNode, ((AttrNode) node).getAttributes());
            }
        }
        // add the edges
        for (Edge edge : graph.edgeSet()) {
            // create an xml element for this edge
            String prefixedLabel = edge.label()
                .text();
            if (edge.label() instanceof TypeLabel) {
                prefixedLabel = edge.getRole()
                    .getPrefix() + prefixedLabel;
            }
            if (edge instanceof TypeEdge && ((TypeEdge) edge).isAbstract()) {
                prefixedLabel = ABSTRACT_PREFIX + prefixedLabel;
            }
            EdgeType gxlEdge = createGxlEdge(nodeMap, edge.source(), prefixedLabel, edge.target());
            edgeMap.put(edge, gxlEdge);
            nodesEdges.add(gxlEdge);
            if (layoutMap != null) {
                // store the layout
                storeEdgeLayout(layoutMap, edge, gxlEdge);
            }
            // add attributes of XML nodes
            if (edge instanceof AttrEdge) {
                saveAttributes(gxlEdge, ((AttrEdge) edge).getAttributes());
            }
        }
        // add node tuples if appropriate
        if (graph instanceof AttrGraph) {
            int count = 0;
            for (AttrTuple tuple : ((AttrGraph) graph).getTuples()) {
                RelType gxlRel = this.factory.createRelType();
                // Create an arbitrary id for the tuple.
                gxlRel.setId("ec" + count);
                count++;
                // For each equivalence class, create a relation end.
                for (AttrNode node : tuple.getNodes()) {
                    RelendType relEnd = this.factory.createRelendType();
                    relEnd.setId(node.toString());
                    gxlRel.getRelend()
                        .add(relEnd);
                }
                nodesEdges.add(gxlRel);
            }
        }
        // add subtype edges if the graph is a type graph
        if (graph instanceof TypeGraph) {
            TypeGraph typeGraph = (TypeGraph) graph;
            Map<TypeNode,Set<TypeNode>> subtypeMap = typeGraph.getDirectSubtypeMap();
            for (Map.Entry<TypeNode,Set<TypeNode>> subtypeEntry : subtypeMap.entrySet()) {
                for (TypeNode subtype : subtypeEntry.getValue()) {
                    TypeNode supertype = subtypeEntry.getKey();
                    nodesEdges.add(createGxlEdge(nodeMap, subtype, SUBTYPE_PREFIX, supertype));
                }
            }
        }
        // add the graph info
        if (graph.hasInfo()) {
            gxlGraph.setId(graph.getName());
            gxlGraph.setRole(graph.getRole()
                .toString());
            // add the graph attributes, if any
            GraphProperties properties = GraphInfo.getProperties(graph);
            for (Map.Entry<Object,Object> entry : properties.entrySet()) {
                // EZ: Removed this conversion because it causes problems
                // with rule properties keys.
                // String attrName = ((String) entry.getKey()).toLowerCase();
                storeAttribute(gxlGraph, (String) entry.getKey(), (String) entry.getValue());
            }
            // Add version info
            if (!properties.containsKey(GraphProperties.Key.VERSION.getName())) {
                storeAttribute(gxlGraph,
                    GraphProperties.Key.VERSION.getName(),
                    Version.GXL_VERSION);
            }
        }
        return gxlGraph;
    }

    /**
     * Adds a layout attribute to a gxlNode.
     * @param map the map providing the layout info; non-{@code null}
     */
    private void storeNodeLayout(LayoutMap map, Node node, NodeType gxl) {
        JVertexLayout layout = map.nodeMap()
            .get(node);
        if (layout != null) {
            Rectangle bounds = Groove.toRectangle(layout.getBounds());
            String value = bounds.x + " " + bounds.y + " " + bounds.width + " " + bounds.height;
            storeAttribute(gxl, LAYOUT_ATTR_NAME, value);
        }
    }

    /**
     * Adds a layout attribute to a gxlEdge.
     * @param map the map providing the layout info; non-{@code null}
     */
    private void storeEdgeLayout(LayoutMap map, Edge edge, EdgeType gxl) {
        JEdgeLayout layout = map.edgeMap()
            .get(edge);
        if (layout != null) {
            String value = toString(layout.getLabelPosition()) + " " + toString(layout.getPoints())
                + " " + layout.getLineStyle()
                    .getCode();
            storeAttribute(gxl, LAYOUT_ATTR_NAME, value);
        }
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
     * Adds attributes from an attribute map to a given graph element.
     */
    private void saveAttributes(TypedElementType gxlElem, Map<String,String> attrs) {
        for (Map.Entry<String,String> e : attrs.entrySet()) {
            storeAttribute(gxlElem, e.getKey(), e.getValue());
        }
    }

    /**
     * Adds a single key-value pair to the attributes of a given graph element.
     */
    private void storeAttribute(TypedElementType gxlElem, String key, String value) {
        AttrType nodeMultAttr = this.factory.createAttrType();
        nodeMultAttr.setName(key);
        nodeMultAttr.setString(value);
        gxlElem.getAttr()
            .add(nodeMultAttr);
    }

    private EdgeType createGxlEdge(Map<Node,NodeType> nodeMap, Node source, String labelText,
        Node target) {
        EdgeType result = this.factory.createEdgeType();
        result.setFrom(nodeMap.get(source));
        result.setTo(nodeMap.get(target));
        storeAttribute(result, LABEL_ATTR_NAME, labelText);
        return result;
    }

    @Override
    public boolean canLoad() {
        return true;
    }

    @Override
    public AttrGraph loadGraph(File file) throws IOException {
        // first get the non-layed out result
        AttrGraph result;
        try (InputStream in = new FileInputStream(file)) {
            result = loadGraph(in);
        } catch (FormatException exc) {
            throw new IOException(
                String.format("Format error while loading '%s':\n%s", file, exc.getMessage()), exc);
        } catch (IOException exc) {
            throw new IOException(
                String.format("Error while loading '%s':\n%s", file, exc.getMessage()), exc);
        }
        // set the graph name from the file name
        result.setName(FileType.getPureName(file));
        // add old-style priority, if necessitated by the file name
        PriorityFileName priorityName = new PriorityFileName(file);
        if (priorityName.hasPriority()) {
            GraphInfo.setPriority(result, priorityName.getPriority());
        }
        // add old-style layout information, if there is a separate layout file
        File layoutFile = toLayoutFile(file);
        if (layoutFile.exists()) {
            try (InputStream in = new FileInputStream(layoutFile)) {
                LayoutIO.getInstance()
                    .loadLayout(result, in);
            } catch (IOException e) {
                // we do nothing when there is no layout found
            }
        }
        return result;
    }

    /**
     * Loads a graph from an input stream. Convenience method for
     * <code>loadGraphWithMap(in).first()</code>.
     */
    @Override
    public AttrGraph loadGraph(InputStream in) throws IOException, FormatException {
        try {
            GraphType gxlGraph = unmarshal(in);
            AttrGraph graph = gxlToGraph(gxlGraph);
            String version = GraphInfo.getVersion(graph);
            if (!Version.isKnownGxlVersion(version)) {
                GraphInfo.addErrors(graph,
                    new FormatErrorSet(
                        "GXL file format version '%s' is higher than supported version '%s'",
                        version, Version.GXL_VERSION));
            }
            return graph;
        } finally {
            in.close();
        }
    }

    /**
     * Converts an untyped GXL graph to a (groove) graph.
     * The method returns a map from GXL node ids to {@link Node}s.
     * @param gxlGraph the source of the unmarshalling
     * @return pair consisting of the resulting graph and a non-<code>null</code> map
     */
    private AttrGraph gxlToGraph(GraphType gxlGraph) throws FormatException {
        // Initialize the new objects to be created.
        AttrGraph graph = new AttrGraph(gxlGraph.getId());
        LayoutMap layoutMap = new LayoutMap();

        // Extract nodes out of the gxl elements.
        for (GraphElementType gxlElement : gxlGraph.getNodeOrEdgeOrRel()) {
            if (gxlElement instanceof NodeType) {
                // Extract the node id and create the node out of it.
                String nodeId = gxlElement.getId();
                if (graph.hasNode(nodeId)) {
                    throw new FormatException(
                        "The node " + nodeId + " is declared more than once.");
                }
                AttrNode node = graph.addNode(nodeId);
                Map<String,String> attrs = loadAttributes(gxlElement);
                // check for the presence of layout information
                String layoutText = attrs.remove(LAYOUT_ATTR_NAME);
                if (layoutText != null) {
                    loadNodeLayout(layoutMap, node, layoutText);
                }
                // put the rest of the attributes into the node
                for (Map.Entry<String,String> e : attrs.entrySet()) {
                    node.setAttribute(e.getKey(), e.getValue());
                }
            }
        }

        // Extract node tuples out of the gxl elements.
        for (GraphElementType gxlElement : gxlGraph.getNodeOrEdgeOrRel()) {
            if (gxlElement instanceof RelType) {
                // We got a relation.
                List<String> nodeIds = new ArrayList<>();
                for (RelendType relEnd : ((RelType) gxlElement).getRelend()) {
                    nodeIds.add(relEnd.getId());
                }
                graph.addTuple(nodeIds);
            }
        }

        // Extract edges out of the gxl elements.
        for (GraphElementType gxlElement : gxlGraph.getNodeOrEdgeOrRel()) {
            if (gxlElement instanceof EdgeType) {
                EdgeType gxlEdge = (EdgeType) gxlElement;
                // Find the source node of the edge.
                NodeType gxlSource = (NodeType) gxlEdge.getFrom();
                if (gxlSource == null) {
                    throw new FormatException("Unable to find source node of %s", gxlEdge);
                }
                String sourceId = gxlSource.getId();
                AttrNode sourceNode = graph.getNode(sourceId);
                if (sourceNode == null) {
                    throw new FormatException("Unable to find edge source node %s", sourceId);
                }
                // Find the target node of the edge.
                NodeType gxlTarget = (NodeType) gxlEdge.getTo();
                if (gxlTarget == null) {
                    throw new FormatException("Unable to find target node of %s", gxlEdge);
                }
                String targetId = ((NodeType) gxlEdge.getTo()).getId();
                AttrNode targetNode = graph.getNode(targetId);
                if (targetNode == null) {
                    throw new FormatException("Unable to find edge target node %s", targetId);
                }

                // Extract the gxlElement attributes.
                Map<String,String> attrs = loadAttributes(gxlElement);
                // check for the presence of a label
                String labelText = attrs.remove(LABEL_ATTR_NAME);
                if (labelText == null) {
                    throw new FormatException("Edge %s -> %s must have a %s attribute ", sourceId,
                        targetId, LABEL_ATTR_NAME);
                }
                // Create the edge object.
                AttrEdge edge = graph.addEdge(sourceNode, labelText, targetNode);
                // check for the presence of layout information
                String layoutText = attrs.remove(LAYOUT_ATTR_NAME);
                if (layoutText != null) {
                    loadEdgeLayout(layoutMap, edge, layoutText);
                }
                // put the rest of the attributes into the edge
                for (Map.Entry<String,String> e : attrs.entrySet()) {
                    edge.setAttribute(e.getKey(), e.getValue());
                }
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
        GraphInfo.setProperties(graph, properties);
        String roleName = gxlGraph.getRole();
        graph.setRole(roleName == null ? GraphRole.HOST : GraphRole.roles.get(roleName));
        GraphInfo.setLayoutMap(graph, layoutMap);
        return graph;
    }

    /**
     * Returns the string attributes of a given GXL element as a string-to-string map
     */
    private Map<String,String> loadAttributes(GraphElementType gxlElement) {
        Map<String,String> result = new LinkedHashMap<>();
        for (AttrType attr : gxlElement.getAttr()) {
            String value = attr.getString();
            if (value != null) {
                String key = attr.getName();
                result.put(key, value);
            }
        }
        return result;
    }

    private void loadNodeLayout(LayoutMap layoutMap, AttrNode node, String layoutText)
        throws FormatException {
        // extract layout
        String[] parts = layoutText.split(" ");
        Rectangle bounds = LayoutIO.toBounds(parts, 0);
        if (bounds == null) {
            throw new FormatException("Bounds for " + parts[1] + " cannot be parsed");
        }
        layoutMap.putNode(node, new JVertexLayout(bounds));
    }

    private void loadEdgeLayout(LayoutMap layoutMap, AttrEdge edge, String layout)
        throws FormatException {
        String[] parts = layout.split(" ");
        if (parts.length > 2) {
            List<Point2D> points = LayoutIO.toPoints(parts, 2);
            // if we have fewer than 2 points, something is wrong
            if (points.size() <= 1) {
                throw new FormatException("Edge layout needs at least 2 points");
            }
            int lineStyle = Integer.parseInt(parts[parts.length - 1]);
            if (!LineStyle.isStyle(lineStyle)) {
                lineStyle = LineStyle.DEFAULT_VALUE.getCode();
            }
            JVertexLayout sourceLayout = layoutMap.getLayout(edge.source());
            JVertexLayout targetLayout = layoutMap.getLayout(edge.target());
            if (sourceLayout != null && targetLayout != null) {
                LayoutIO.correctPoints(points, sourceLayout, targetLayout);
            }
            Point2D labelPosition = LayoutIO.calculateLabelPosition(LayoutIO.toPoint(parts, 0),
                points,
                LayoutIO.VERSION2,
                edge.isLoop());
            JEdgeLayout result =
                new JEdgeLayout(points, labelPosition, LineStyle.getStyle(lineStyle));
            layoutMap.putEdge(edge, result);
        }
    }

    @SuppressWarnings("unchecked")
    private GraphType unmarshal(InputStream inputStream) throws IOException {
        try {
            JAXBElement<GxlType> doc =
                (JAXBElement<GxlType>) this.unmarshaller.unmarshal(inputStream);
            inputStream.close();
            return doc.getValue()
                .getGraph()
                .get(0);
        } catch (JAXBException e) {
            throw new IOException(String.format("Error in %s: %s", inputStream, e.getMessage()));
        }
    }

    /** Reusable context for JAXB (un)marshalling. */
    private final JAXBContext context;
    /** Reusable marshaller. */
    private final javax.xml.bind.Marshaller marshaller;
    /** Reusable unmarshaller. */
    private final javax.xml.bind.Unmarshaller unmarshaller;

    {
        try {
            this.context = JAXBContext.newInstance(GxlType.class.getPackage()
                .getName());
            this.unmarshaller = this.context.createUnmarshaller();
            this.marshaller = this.context.createMarshaller();
            this.marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        } catch (JAXBException e) {
            throw new IllegalStateException();
        }
    }

    /** Object factory used for marshalling. */
    private final ObjectFactory factory = new ObjectFactory();

    /** Returns the singleton instance of this class. */
    public static GxlIO instance() {
        return INSTANCE;
    }

    private static final GxlIO INSTANCE = new GxlIO();

    /** Attribute name for node and edge identities. */
    static private final String LABEL_ATTR_NAME = "label";
    /** Attribute name for layout information. */
    static private final String LAYOUT_ATTR_NAME = "layout";
    /** Subtype label. */
    static private final String ABSTRACT_PREFIX = ABSTRACT.getAspect()
        .toString();
    static private final String SUBTYPE_PREFIX = SUBTYPE.getAspect()
        .toString();
}