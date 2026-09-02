// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2023 University of Twente

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
package nl.utwente.groove.io.graph;

import static nl.utwente.groove.grammar.aspect.AspectKind.ABSTRACT;
import static nl.utwente.groove.grammar.aspect.AspectKind.SUBTYPE;
import static nl.utwente.groove.util.io.FileType.LAYOUT;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.graph.GraphInfo;
import nl.utwente.groove.grammar.ResourceProperties;
import nl.utwente.groove.graph.GraphRole;
import nl.utwente.groove.graph.layout.EdgeLayout;
import nl.utwente.groove.graph.layout.LayoutMap;
import nl.utwente.groove.graph.layout.NodeLayout;
import nl.utwente.groove.graph.plain.PlainGraph;
import nl.utwente.groove.gxl_1_0.AttrType;
import nl.utwente.groove.gxl_1_0.EdgeType;
import nl.utwente.groove.gxl_1_0.GraphElementType;
import nl.utwente.groove.gxl_1_0.GraphType;
import nl.utwente.groove.gxl_1_0.GxlType;
import nl.utwente.groove.gxl_1_0.NodeType;
import nl.utwente.groove.gxl_1_0.RelType;
import nl.utwente.groove.gxl_1_0.RelendType;
import nl.utwente.groove.util.Strings;
import nl.utwente.groove.util.Version;
import nl.utwente.groove.util.io.FileType;
import nl.utwente.groove.util.line.LineStyle;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Class to convert graphs to GXL format and back.
 * Loading is implemented using JAXB data binding; saving streams the
 * document through a {@link GxlWriter}, without building it in memory.
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
    public void saveGraph(Graph graph, File file) throws IOException {
        super.saveGraph(graph, file);
        // layout is now saved in the gxl file; delete the layout file
        deleteFile(toLayoutFile(file));
    }

    /**
     * Saves a graph to a file, streaming the GXL document through a {@link GxlWriter}.
     */
    @Override
    protected void doSaveGraph(Graph graph, File file) throws IOException {
        try (Writer out = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
            GxlWriter.write(graph, out);
        }
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
        } catch (IOException exc) {
            throw new IOException(
                String.format("Error while loading '%s':\n%s", file, exc.getMessage()), exc);
        }
        // set the graph name from the file name
        result.setName(FileType.getPureName(file));
        // add old-style priority, if necessitated by the file name
        PriorityFileName priorityName = new PriorityFileName(file);
        if (priorityName.hasPriority()) {
            ResourceProperties.setPriority(result, priorityName.getPriority());
        }
        // add old-style layout information, if there is a separate layout file
        File layoutFile = toLayoutFile(file);
        if (layoutFile.exists()) {
            try (InputStream in = new FileInputStream(layoutFile)) {
                LayoutIO.getInstance().loadLayout(result, in);
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
    public AttrGraph loadGraph(InputStream in) throws IOException {
        try {
            GraphType gxlGraph = unmarshal(in);
            AttrGraph graph;
            try {
                graph = gxlToGraph(gxlGraph);
            } catch (FormatException exc) {
                throw new IOException(String.format("Format error: %s", exc.getMessage()), exc);
            }
            String version = ResourceProperties.getVersion(graph);
            if (!Version.isKnownGxlVersion(version)) {
                graph
                    .addError("GXL file format version '%s' is higher than supported version '%s'",
                              version, Version.GXL_VERSION);
            }
            return graph;
        } finally {
            in.close();
        }
    }

    @Override
    public PlainGraph loadPlainGraph(InputStream in) throws IOException {
        return loadGraph(in).toPlainGraph();
    }

    /**
     * Converts an untyped GXL graph to a (groove) graph.
     * The method returns a map from GXL node ids to {@link nl.utwente.groove.graph.Node}s.
     * @param gxlGraph the source of the unmarshalling
     * @return pair consisting of the resulting graph and a non-<code>null</code> map
     */
    private AttrGraph gxlToGraph(GraphType gxlGraph) throws FormatException {
        // Initialize the new objects to be created.
        AttrGraph graph = new AttrGraph(gxlGraph.getId());
        Object latest = null;
        LayoutMap layoutMap = new LayoutMap();
        // Extract nodes out of the gxl elements.
        // First collect an ordered set of all node types
        Set<NodeType> nodes
            = new TreeSet<>((n1, n2) -> Strings.compareNatural(n1.getId(), n2.getId()));
        for (GraphElementType gxlElement : gxlGraph.getNodeOrEdgeOrRel()) {
            if (gxlElement instanceof NodeType nt) {
                nodes.add(nt);
            }
        }
        // now process the node types in order
        for (var nt : nodes) {
            // Extract the node id and create the node out of it.
            String nodeId = nt.getId();
            if (graph.hasNode(nodeId)) {
                throw new FormatException("The node " + nodeId + " is declared more than once.");
            }
            AttrNode node = graph.addNode(nodeId);
            latest = node;
            Map<String,String> attrs = loadAttributes(nt);
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

        // Extract node tuples out of the gxl elements.
        for (GraphElementType gxlElement : gxlGraph.getNodeOrEdgeOrRel()) {
            if (gxlElement instanceof RelType rt) {
                // We got a relation.
                List<String> nodeIds = new ArrayList<>();
                for (RelendType relEnd : rt.getRelend()) {
                    nodeIds.add(relEnd.getId());
                }
                latest = graph.addTuple(nodeIds);
            }
        }

        // Extract edges out of the gxl elements.
        for (GraphElementType gxlElement : gxlGraph.getNodeOrEdgeOrRel()) {
            if (gxlElement instanceof EdgeType gxlEdge) {
                // Find the source node of the edge.
                NodeType gxlSource = (NodeType) gxlEdge.getFrom();
                if (gxlSource == null) {
                    throw new FormatException(
                        "Unspecified source node of %s (last successfully loaded lement was %s)",
                        gxlEdge.getId(), latest);
                }
                String sourceId = gxlSource.getId();
                AttrNode sourceNode = graph.getNode(sourceId);
                if (sourceNode == null) {
                    throw new FormatException("Unable to find edge source node %s", sourceId);
                }
                // Find the target node of the edge.
                NodeType gxlTarget = (NodeType) gxlEdge.getTo();
                if (gxlTarget == null) {
                    throw new FormatException(
                        "Unspecified target node of %s (with source node %s; last successfully loaded lement was %s)",
                        gxlEdge, sourceId, latest);
                }
                String targetId = gxlTarget.getId();
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
                latest = edge;
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
        ResourceProperties properties = new ResourceProperties();
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
        ResourceProperties.setProperties(graph, properties);
        String roleName = gxlGraph.getRole();
        GraphRole role = roleName == null
            ? GraphRole.HOST
            : GraphRole.roles.get(roleName);
        if (role == null) {
            throw new FormatException("Unknown graph role %s", roleName);
        }
        graph.setRole(role);
        // the graph is simple unless the edgeids flag declares edge identities;
        // LTS graphs are non-simple regardless, for backward compatibility with
        // LTS files saved by older versions, which may contain parallel edges
        // without declaring edge identities
        graph.setSimple(!gxlGraph.isEdgeids() && graph.getRole() != GraphRole.LTS);
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

    private void loadNodeLayout(LayoutMap layoutMap, AttrNode node,
                                String layoutText) throws FormatException {
        // extract layout
        String[] parts = layoutText.split(" ");
        Rectangle bounds = LayoutIO.toBounds(parts, 0);
        if (bounds == null) {
            throw new FormatException("Bounds for " + parts[1] + " cannot be parsed");
        }
        layoutMap.putNode(node, new NodeLayout(bounds));
    }

    private void loadEdgeLayout(LayoutMap layoutMap, AttrEdge edge,
                                String layout) throws FormatException {
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
            NodeLayout sourceLayout = layoutMap.getLayout(edge.source());
            NodeLayout targetLayout = layoutMap.getLayout(edge.target());
            if (sourceLayout != null && targetLayout != null) {
                LayoutIO.correctPoints(points, sourceLayout, targetLayout);
            }
            Point2D labelPosition = LayoutIO
                .calculateLabelPosition(LayoutIO.toPoint(parts, 0), points, LayoutIO.VERSION2,
                                        edge.isLoop());
            EdgeLayout result
                = new EdgeLayout(points, labelPosition, LineStyle.getStyle(lineStyle));
            layoutMap.putEdge(edge, result);
        }
    }

    @SuppressWarnings("unchecked")
    private GraphType unmarshal(InputStream inputStream) throws IOException {
        try {
            JAXBElement<GxlType> doc
                = (JAXBElement<GxlType>) this.unmarshaller.unmarshal(inputStream);
            inputStream.close();
            var graphs = doc.getValue().getGraph();
            if (graphs.isEmpty()) {
                throw new IOException("GXL document contains no graph");
            }
            return graphs.get(0);
        } catch (JAXBException e) {
            throw new IOException(String.format("Error in %s: %s", inputStream, e.getMessage()));
        }
    }

    /** Reusable context for JAXB unmarshalling. */
    private final JAXBContext context;
    /** Reusable unmarshaller. */
    private final jakarta.xml.bind.Unmarshaller unmarshaller;

    {
        try {
            this.context = JAXBContext.newInstance(GxlType.class.getPackageName());
            this.unmarshaller = this.context.createUnmarshaller();
        } catch (JAXBException e) {
            throw new IllegalStateException();
        }
    }

    /** Returns the singleton instance of this class. */
    public static GxlIO instance() {
        return INSTANCE;
    }

    private static final GxlIO INSTANCE = new GxlIO();

    /** Attribute name for node and edge identities. */
    static final String LABEL_ATTR_NAME = "label";
    /** Attribute name for layout information. */
    static final String LAYOUT_ATTR_NAME = "layout";
    /** Subtype label. */
    static final String ABSTRACT_PREFIX = ABSTRACT.getAspect().toString();
    static final String SUBTYPE_PREFIX = SUBTYPE.getAspect().toString();
}