/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.io.graph;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.grammar.ResourceProperties;
import nl.utwente.groove.grammar.host.ValueNode;
import nl.utwente.groove.grammar.type.TypeEdge;
import nl.utwente.groove.grammar.type.TypeGraph;
import nl.utwente.groove.grammar.type.TypeLabel;
import nl.utwente.groove.grammar.type.TypeNode;
import nl.utwente.groove.graph.Edge;
import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.graph.GraphInfo;
import nl.utwente.groove.graph.Node;
import nl.utwente.groove.graph.layout.EdgeLayout;
import nl.utwente.groove.graph.layout.LayoutMap;
import nl.utwente.groove.graph.layout.NodeLayout;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.Version;

/**
 * Streaming writer for the GXL format used by {@link GxlIO}.
 * The document is written element by element while the graph is being
 * iterated, without first building an in-memory document, so that saving a
 * large graph (notably an LTS) needs no memory beyond the graph itself (gh #854).
 * The output is byte for byte what the JAXB marshaller used to produce:
 * the same element and attribute order, four-space indentation, and the
 * marshaller's minimal escaping.
 * Node types and flag labels as well as {@link ValueNode}s are converted
 * to prefixed form; if the graph is a {@link TypeGraph}, subtype edges are also added.
 */
@NonNullByDefault
@AIGenerated("Claude Fable 5.1, 2026-09")
class GxlWriter {
    /** Writes a graph as a GXL document to a given writer, which is left open. */
    static void write(Graph graph, Writer out) throws IOException {
        new GxlWriter(out, !graph.isSimple()).writeDocument(graph);
    }

    private GxlWriter(Writer out, boolean edgeIds) {
        this.out = out;
        this.edgeIds = edgeIds;
    }

    private final Writer out;
    /** Flag indicating that edges are given identities.
     * Non-simple graphs have edges with identities beyond source/label/target,
     * which is exactly what the GXL edgeids flag declares.
     */
    private final boolean edgeIds;
    /** Number of the next edge identity, if {@link #edgeIds} is set. */
    private int edgeNr;

    private void writeDocument(Graph graph) throws IOException {
        line(0, "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
        line(0, "<gxl xmlns=\"http://www.gupro.de/GXL/gxl-1.0.dtd\">");
        line(1, "<graph role=\"" + attr(graph.getRole().toString()) + "\" edgeids=\""
            + this.edgeIds + "\" edgemode=\"directed\" id=\"" + attr(graph.getName()) + "\">");
        // add the graph info
        if (graph.hasInfo()) {
            // add the graph attributes, if any
            ResourceProperties properties = ResourceProperties.getProperties(graph);
            for (var e : properties.entryStream().toList()) {
                writeAttr(2, e.getKey(), e.getValue());
            }
            // Add version info
            if (!properties.containsKey(ResourceProperties.Key.VERSION)) {
                writeAttr(2, ResourceProperties.Key.VERSION.getName(), Version.GXL_VERSION);
            }
        }
        // get the layout map
        @Nullable
        LayoutMap layoutMap = GraphInfo.getLayoutMap(graph);
        for (Node node : graph.nodeSet()) {
            List<Attr> attrs = new ArrayList<>();
            // store the layout
            NodeLayout layout = layoutMap == null
                ? null
                : layoutMap.getLayout(node);
            if (layout != null) {
                attrs.add(new Attr(GxlIO.LAYOUT_ATTR_NAME, toString(layout)));
            }
            // add attributes of XML nodes
            if (node instanceof AttrNode an) {
                addAttrs(attrs, an.getAttributes());
            }
            // give the element an id based on the node number
            writeElement(2, "node", "id=\"n" + node.getNumber() + "\"", attrs);
            // add appropriate edges for value nodes
            if (node instanceof ValueNode vn) {
                writeEdge(node, vn.toString(), node, List.of());
            }
        }
        // add the edges
        for (Edge edge : graph.edgeSet()) {
            String prefixedLabel = edge.label().text();
            if (edge.label() instanceof TypeLabel) {
                prefixedLabel = edge.getRole().getPrefix() + prefixedLabel;
            }
            if (edge instanceof TypeEdge te && te.isAbstract()) {
                prefixedLabel = GxlIO.ABSTRACT_PREFIX + prefixedLabel;
            }
            List<Attr> attrs = new ArrayList<>();
            // store the layout
            EdgeLayout layout = layoutMap == null
                ? null
                : layoutMap.getLayout(edge);
            if (layout != null) {
                attrs.add(new Attr(GxlIO.LAYOUT_ATTR_NAME, toString(layout)));
            }
            // add attributes of XML edges
            if (edge instanceof AttrEdge ae) {
                addAttrs(attrs, ae.getAttributes());
            }
            writeEdge(edge.source(), prefixedLabel, edge.target(), attrs);
        }
        // add node tuples if appropriate
        if (graph instanceof AttrGraph ag) {
            int count = 0;
            for (AttrTuple tuple : ag.getTuples()) {
                // Create an arbitrary id for the tuple.
                String head = "id=\"ec" + count + "\"";
                count++;
                var nodes = tuple.getNodes();
                if (nodes.isEmpty()) {
                    line(2, "<rel " + head + "/>");
                } else {
                    line(2, "<rel " + head + ">");
                    // For each equivalence class, create a relation end.
                    for (AttrNode node : nodes) {
                        line(3, "<relend id=\"" + attr(node.toString()) + "\"/>");
                    }
                    line(2, "</rel>");
                }
            }
        }
        // add subtype edges if the graph is a type graph
        if (graph instanceof TypeGraph typeGraph) {
            Map<TypeNode,Set<TypeNode>> subtypeMap = typeGraph.getDirectSubtypeMap();
            for (Map.Entry<TypeNode,Set<TypeNode>> subtypeEntry : subtypeMap.entrySet()) {
                for (TypeNode subtype : subtypeEntry.getValue()) {
                    TypeNode supertype = subtypeEntry.getKey();
                    writeEdge(subtype, GxlIO.SUBTYPE_PREFIX, supertype, List.of());
                }
            }
        }
        line(1, "</graph>");
        line(0, "</gxl>");
    }

    /** Writes an edge element with a given label attribute, followed by further attributes.
     * If edges have identities, the edge element gets an id, unique with respect
     * to the "n"-prefixed node ids.
     */
    private void writeEdge(Node source, String labelText, Node target,
                           List<Attr> attrs) throws IOException {
        String head = "from=\"n" + source.getNumber() + "\" to=\"n" + target.getNumber() + "\"";
        if (this.edgeIds) {
            head += " id=\"e" + this.edgeNr + "\"";
            this.edgeNr++;
        }
        List<Attr> allAttrs = new ArrayList<>();
        allAttrs.add(new Attr(GxlIO.LABEL_ATTR_NAME, labelText));
        allAttrs.addAll(attrs);
        writeElement(2, "edge", head, allAttrs);
    }

    /** Writes an element with given name, XML attributes and GXL attributes,
     * as an empty element if there are no GXL attributes.
     */
    private void writeElement(int depth, String name, String head,
                              List<Attr> attrs) throws IOException {
        if (attrs.isEmpty()) {
            line(depth, "<" + name + " " + head + "/>");
        } else {
            line(depth, "<" + name + " " + head + ">");
            for (Attr attr : attrs) {
                writeAttr(depth + 1, attr.key(), attr.value());
            }
            line(depth, "</" + name + ">");
        }
    }

    /** Writes a single key-value pair as a GXL string attribute. */
    private void writeAttr(int depth, String key, String value) throws IOException {
        line(depth, "<attr name=\"" + attr(key) + "\">");
        line(depth + 1, "<string>" + text(value) + "</string>");
        line(depth, "</attr>");
    }

    /** Writes an indented line. */
    private void line(int depth, String content) throws IOException {
        for (int i = 0; i < depth; i++) {
            this.out.write(INDENT);
        }
        this.out.write(content);
        this.out.write('\n');
    }

    /** Adds the entries of an attribute map to a list of attributes. */
    private static void addAttrs(List<Attr> attrs, Map<String,String> attrMap) {
        for (Map.Entry<String,String> e : attrMap.entrySet()) {
            attrs.add(new Attr(e.getKey(), e.getValue()));
        }
    }

    /** Converts a node layout to its attribute text. */
    private static String toString(NodeLayout layout) {
        Rectangle bounds = toRectangle(layout.getBounds());
        return bounds.x + " " + bounds.y + " " + bounds.width + " " + bounds.height;
    }

    /** Converts a {@link Rectangle2D} to a {@link Rectangle}. */
    private static Rectangle toRectangle(Rectangle2D r) {
        return new Rectangle((int) r.getX(), (int) r.getY(), (int) r.getWidth(),
            (int) r.getHeight());
    }

    /** Converts an edge layout to its attribute text. */
    private static String toString(EdgeLayout layout) {
        return toString(layout.getLabelPosition()) + " " + toString(layout.getPoints()) + " "
            + layout.getLineStyle().getCode();
    }

    /** Converts a {@link Point2D} to a text. */
    private static String toString(Point2D point) {
        return (int) point.getX() + " " + (int) point.getY();
    }

    /** Converts a list of {@link Point2D} to a text. */
    private static String toString(List<Point2D> points) {
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

    /** Escapes element text the way the JAXB marshaller does. */
    private static String text(String value) {
        return escape(value, false);
    }

    /** Escapes an XML attribute value the way the JAXB marshaller does. */
    private static String attr(String value) {
        return escape(value, true);
    }

    /** Minimal escaping: {@code &}, {@code <}, {@code >} and carriage returns
     * always, double quotes and line feeds in attribute values only.
     */
    private static String escape(String value, boolean isAttr) {
        StringBuilder result = null;
        int start = 0;
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            String replacement = switch (c) {
            case '&' -> "&amp;";
            case '<' -> "&lt;";
            case '>' -> "&gt;";
            case '\r' -> "&#13;";
            case '"' -> isAttr
                ? "&quot;"
                : null;
            case '\n' -> isAttr
                ? "&#10;"
                : null;
            default -> null;
            };
            if (replacement != null) {
                if (result == null) {
                    result = new StringBuilder();
                }
                result.append(value, start, i).append(replacement);
                start = i + 1;
            }
        }
        if (result == null) {
            return value;
        }
        result.append(value, start, value.length());
        return result.toString();
    }

    /** GXL string attribute: a key-value pair. */
    private record Attr(String key, String value) {
        // no additional functionality
    }

    /** Indentation unit. */
    private static final String INDENT = "    ";
}
