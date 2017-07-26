/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2009 University of Twente
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
package groove.io.external.util;

import static groove.grammar.aspect.AspectKind.DEFAULT;
import static groove.grammar.aspect.AspectKind.PRODUCT;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.jgraph.graph.GraphConstants;
import org.jgraph.util.Bezier;

import groove.grammar.aspect.AspectKind;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.GraphInfo;
import groove.graph.Node;
import groove.gui.jgraph.AspectJVertex;
import groove.gui.jgraph.JCell;
import groove.gui.jgraph.JEdge;
import groove.gui.jgraph.JGraph;
import groove.gui.jgraph.JModel;
import groove.gui.jgraph.JVertex;
import groove.gui.layout.JEdgeLayout;
import groove.gui.layout.JVertexLayout;
import groove.gui.layout.LayoutMap;
import groove.gui.look.Look;
import groove.gui.look.MultiLabel;
import groove.io.external.util.TikzStylesExtractor.Style;

/**
 * Class to perform the conversion from Groove graphs to Tikz format.
 * @author Eduardo Zambon
 */
public final class GraphToTikz<G extends Graph> {

    // ------------------------------------------------------------------------
    // Object fields
    // ------------------------------------------------------------------------

    /** The jGraph to be output. */
    private final JGraph<G> jGraph;
    /** The underlying model for jGraph. */
    private final JModel<G> model;
    /** The underlying Groove graph connected to the jGraph. */
    private final Graph graph;
    /** The layout map of the graph. */
    private final LayoutMap layoutMap;
    /** The color map of the graph. */
    /** The builder that holds the Tikz string. */
    private final StringBuilder result;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /**
     * The constructor is private. To perform the conversion just call the
     * static method {@link #convert(JGraph)}.
     */
    private GraphToTikz(JGraph<G> jGraph) {
        this.jGraph = jGraph;
        this.model = this.jGraph.getModel();
        this.graph = this.model.getGraph();
        this.layoutMap = GraphInfo.getLayoutMap(this.graph);
        this.result = new StringBuilder();
    }

    // ------------------------------------------------------------------------
    // Static methods
    // ------------------------------------------------------------------------

    /** Writes a graph in LaTeX <code>Tikz</code> format to a print writer. */
    public static void export(JGraph<?> graph, PrintWriter writer) {
        writer.print(GraphToTikz.convert(graph));
    }

    /**
     * Converts a graph to a Tikz representation.
     * @param jGraph the graph to be converted.
     * @return a string with the Tikz encoding of the graph.
     */
    public static <G extends Graph> String convert(JGraph<G> jGraph) {
        return new GraphToTikz<>(jGraph).doConvert();
    }

    // BEGIN
    // Methods to enclose a string with extra characters.

    private static String enclose(String string, String start, String end) {
        return start + string + end;
    }

    private static String enclosePar(String string) {
        return enclose(string, "(", ")");
    }

    private static String encloseBrack(String string) {
        return enclose(string, "[", "]");
    }

    private static String encloseCurly(String string) {
        return enclose(string, "{", "}");
    }

    private static String encloseSpace(String string) {
        return enclose(string, " ", " ");
    }

    // Methods to enclose a string with extra characters.
    // END

    /**
     * Checks on which side of a rectangle a point lies. To avoid anchoring in
     * points very close to an angle we take a 0.9 scale on each side.
     * @param bounds the bounding box.
     * @param point the point to be checked.
     * @return 1 if the point lies east, 2 if it lies north, 3 if it lies west,
     *         4 if it lies south, and 0 if its outside a proper position.
     */
    private static int getSide(Rectangle2D bounds, Point2D point) {
        double x = point.getX();
        double y = point.getY();
        double ulx = bounds.getX();
        double uly = bounds.getY();
        double brx = bounds.getMaxX();
        double bry = bounds.getMaxY();
        double scale = 0.1;
        double dx = (bounds.getWidth() * scale) / 2;
        double dy = (bounds.getHeight() * scale) / 2;
        double minX = ulx + dx;
        double minY = uly + dy;
        double maxX = brx - dx;
        double maxY = bry - dy;

        int side = 0;

        if (x >= brx && y >= minY && y <= maxY) {
            side = 1;
        } else if (y <= uly && x >= minX && x <= maxX) {
            side = 2;
        } else if (x <= ulx && y >= minY && y <= maxY) {
            side = 3;
        } else if (y >= bry && x >= minX && x <= maxX) {
            side = 4;
        }

        return side;
    }

    /**
     * Provides the string that is to be appended at a node name.
     * @param side the side of the node where a point lies.
     * @return the empty string if side is 0 and one of the four coordinates
     *         otherwise.
     */
    private static String getCoordString(int side) {
        String result;
        switch (side) {
        case 1:
            result = EAST;
            break;
        case 2:
            result = NORTH;
            break;
        case 3:
            result = WEST;
            break;
        case 4:
            result = SOUTH;
            break;
        default:
            result = "";
        }
        return result;
    }

    /**
     * Adapted from jGraph.
     * Converts an relative label position (x is distance along edge and y is
     * distance above/below edge vector) into an absolute coordination point.
     * @param geometry the relative label position.
     * @param points the list of points along the edge.
     * @return the absolute label position.
     */
    private static Point2D convertRelativeLabelPositionToAbsolute(Point2D geometry,
        List<Point2D> points) {

        Point2D pt = points.get(0);

        if (pt != null) {
            double length = 0;
            int pointCount = points.size();
            double[] segments = new double[pointCount];
            // Find the total length of the segments and also store the length
            // of each segment.
            for (int i = 1; i < pointCount; i++) {
                Point2D tmp = points.get(i);

                if (tmp != null) {
                    double dx = pt.getX() - tmp.getX();
                    double dy = pt.getY() - tmp.getY();

                    double segment = Math.sqrt(dx * dx + dy * dy);

                    segments[i - 1] = segment;
                    length += segment;
                    pt = tmp;
                }
            }

            // Change x to be a value between 0 and 1 indicating how far
            // along the edge the label is.
            double x = geometry.getX() / GraphConstants.PERMILLE;
            double y = geometry.getY();

            // dist is the distance along the edge the label is.
            double dist = x * length;
            length = 0;

            int index = 1;
            double segment = segments[0];

            // Find the length up to the start of the segment the label is
            // on (length) and retrieve the length of that segment (segment).
            while (dist > length + segment && index < pointCount - 1) {
                length += segment;
                segment = segments[index++];
            }

            // factor is the proportion along this segment the label lies at.
            double factor = (dist - length) / segment;

            Point2D p0 = points.get(index - 1);
            Point2D pe = points.get(index);

            if (p0 != null && pe != null) {
                // The x and y offsets of the label from the start point
                // of the segment.
                double dx = pe.getX() - p0.getX();
                double dy = pe.getY() - p0.getY();

                // The normal vectors.
                double nx = dy / segment;
                double ny = dx / segment;

                // The x position is the start x of the segment + the factor of
                // the x offset between the start and end of the segment + the
                // x component of the y (height) offset contributed along the
                // normal vector.
                x = p0.getX() + dx * factor - nx * y;

                // The x position is the start y of the segment + the factor of
                // the y offset between the start and end of the segment + the
                // y component of the y (height) offset contributed along the
                // normal vector.
                y = p0.getY() + dy * factor + ny * y;
                return new Point2D.Double(x, y);
            }
        }

        return null;
    }

    private static boolean isNodifiedEdge(JVertex<?> node) {
        return node instanceof AspectJVertex && ((AspectJVertex) node).isNodeEdge();
    }

    private static boolean hasParameter(JVertex<?> node) {
        return node instanceof AspectJVertex ? ((AspectJVertex) node).getNode()
            .hasParam() : false;
    }

    private static boolean hasNonEmptyLabel(JEdge<?> edge) {
        return !edge.getVisuals()
            .getLabel()
            .isEmptyLine();
    }

    private static AspectKind getAttributeKind(JVertex<?> node) {
        return node instanceof AspectJVertex ? ((AspectJVertex) node).getNode()
            .getAttrKind() : DEFAULT;
    }

    private static boolean isProductNode(JVertex<?> node) {
        return getAttributeKind(node) == PRODUCT;
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    private void append(String string) {
        this.result.append(string);
    }

    private void append(StringBuilder sb) {
        this.result.append(sb);
    }

    /**
     * Performs the entire conversion to Tikz and returns the resulting string.
     */
    private String doConvert() {
        appendTikzHeader();

        for (Node node : this.graph.nodeSet()) {
            JVertex<G> vertex = this.model.getJCellForNode(node);
            this.model.synchroniseLayout(vertex);
            JVertexLayout layout = null;
            if (this.layoutMap != null) {
                layout = this.layoutMap.getLayout(node);
            }
            appendTikzNode(vertex, layout);
        }

        append(ENTER);

        Set<JCell<G>> consumedEdges = new HashSet<>();
        for (Edge edge : this.graph.edgeSet()) {
            JEdgeLayout layout = null;
            if (this.layoutMap != null) {
                layout = this.layoutMap.getLayout(edge);
            }
            JCell<G> jCell = this.model.getJCellForEdge(edge);
            if (!consumedEdges.contains(jCell)) {
                appendTikzEdge(jCell, layout);
                consumedEdges.add(jCell);
            }
        }

        appendTikzFooter();

        return this.result.toString();
    }

    /**
     * Appends the header to the Tikz result string. The header includes
     * additional styles local to the figure.
     */
    private void appendTikzHeader() {
        append(DOC);
        append(String.format(BEGIN_TIKZ_FIG + ENTER, this.graph.getName()));
    }

    private void appendTikzFooter() {
        append(END_TIKZ_FIG + ENTER);
    }

    // -------------------------- Nodes ---------------------------------------

    /**
     * Converts a jGraph node to a Tikz string representation.
     * @param node the node to be converted.
     * @param layout information regarding layout of the node.
    */
    private void appendTikzNode(JVertex<G> node, JVertexLayout layout) {
        if (!node.getVisuals()
            .isVisible()) {
            return;
        }

        append(BEGIN_NODE);
        // Styles.
        appendNodeStyles(node);
        // Node ID.
        appendNode(node);

        // Node Coordinates.
        if (layout != null) {
            append(AT_KEYWORD + " ");
            appendPoint(getCenterPoint(layout.getBounds()));
        }

        // Node Labels.
        MultiLabel lines = node.getVisuals()
            .getLabel();
        if (lines.isEmpty() || isNodifiedEdge(node)) {
            append(EMPTY_NODE_LAB);
        } else {
            append(BEGIN_NODE_LAB);
            append(lines.toString(TeXLineFormat.instance()));
            append(END_NODE_LAB);
        }

        // Add small parameter node, if needed.
        if (hasParameter(node)) {
            appendParameterNode((AspectJVertex) node);
        }
    }

    private void appendParameterNode(AspectJVertex node) {
        String nodeId = node.getNode()
            .toString();
        String nr = node.getNode()
            .getParamNr() + "";
        // New node line.
        append(BEGIN_NODE + encloseBrack(PAR_NODE_STYLE));
        // Node name.
        append(encloseSpace(enclosePar(nodeId + PAR_NODE_SUFFIX)));
        // Node Coordinates.
        append(encloseSpace(AT_KEYWORD));
        append(enclosePar(nodeId + NORTH_WEST));
        // Parameter number.
        append(" " + encloseCurly(nr) + ";\n");
    }

    /**
     * Produces a string with the proper Tikz styles of a given node.
     * @param node the node to be converted.
     */
    private void appendNodeStyles(JVertex<G> node) {
        ArrayList<String> styles = new ArrayList<>();
        styles.add(""); // Placeholder for the main style.
        for (Look look : node.getLooks()) {
            if (TikzStylesExtractor.mainLooks.contains(look)) {
                styles.set(0, look.name()
                    .toLowerCase() + TikzStylesExtractor.NODE_SUFFIX);
            } else if (!TikzStylesExtractor.unusedLooks.contains(look)) {
                styles.add(look.name()
                    .toLowerCase());
            }
        }
        addAdditionalStyles(node, styles);
        append(styles.toString());
    }

    private void addAdditionalStyles(JVertex<G> node, List<String> styles) {
        // Check if there are some extra styles, for now we just look for
        // a color.
        Color color = node.getVisuals()
            .getColor();
        if (color != null) {
            Style.getForegroundColor(color, styles);
            Style.getBackgroundColor(node.getVisuals()
                .getBackground(), styles);
        }
    }

    private void addAdditionalStyles(JEdge<G> edge, List<String> styles) {
        // Check if there are some extra styles, for now we just look for
        // a color.
        Color color = edge.getVisuals()
            .getColor();
        if (color != null) {
            Style.getEdgeColor(color, styles);
        }
    }

    /** Appends the node name to the result string. */
    private void appendNode(JVertex<G> node) {
        append(encloseSpace(enclosePar(node.getNode()
            .toString())));
    }

    /**
     * Checks whether the given point is in a proper position with respect to
     * the given node and appends the node to the string builder, together
     * with a node anchor that keeps the edge horizontal or vertical.
     */
    private void appendNode(JVertex<G> node, Point2D point) {
        int side = getSide(node, point);
        if (side == 0 || isProductNode(node) || isNodifiedEdge(node)) {
            // The point is not aligned with the node, just use normal routing.
            appendNode(node);
        } else {
            String coord = getCoordString(side);
            String nodeName = node.getNode()
                .toString();
            append(enclosePar(nodeName + coord + getPointString(point, false)));
        }
    }

    /** Appends the point in position i of a list of points. */
    private void appendPoint(List<Point2D> points, int i) {
        appendPoint(points.get(i));
    }

    /** Appends the given point. */
    private void appendPoint(Point2D point) {
        double x = point.getX();
        double y = point.getY();
        appendPoint(x, y, true, this.result);
    }

    /** Computes and returns the centre point of a rectangle. */
    private Point2D getCenterPoint(Rectangle2D bounds) {
        return new Point2D.Double(bounds.getCenterX(), bounds.getCenterY());
    }

    /**
     * Appends the given points to the string builder. The coordinates are
     * scaled by a constant factor and the y-coordinate is inverted as the
     * jGraph and Tikz representation are different.
     * @param x the x coordinate of the point.
     * @param y the y coordinate of the point.
     * @param usePar flag to indicate whether the point coordinates should be
     *               enclosed in parentheses or not.
     */
    private void appendPoint(double x, double y, boolean usePar, StringBuilder s) {
        double scale = 100.0;
        double adjX = x / scale;
        double adjY = -1.0 * (y / scale);
        String format = "%5.3f, %5.3f";
        try (Formatter f = new Formatter()) {
            if (usePar) {
                format = enclosePar(format);
            }
            s.append(f.format(Locale.US, format, adjX, adjY)
                .toString());
        }
    }

    /**
     * Converts a point to a string.
     * @param point the point to be converted.
     * @param usePar flag to indicate whether the point coordinates should be
     *               enclosed in parentheses or not.
     * @return the string representation of the given point.
     */
    private String getPointString(Point2D point, boolean usePar) {
        double x = point.getX();
        double y = point.getY();
        StringBuilder s = new StringBuilder();
        appendPoint(x, y, usePar, s);
        return s.toString();
    }

    /**
     * Checks on which side of a node a point lies.
     * @param vertex the node to be checked.
     * @param point the point to be checked.
     * @return 1 if the point lies east, 2 if it lies north, 3 if it lies west,
     *         4 if it lies south, and 0 if its outside a proper position.
     */
    private int getSide(JVertex<G> vertex, Point2D point) {
        int side = 0;
        if (this.layoutMap != null) {
            JVertexLayout layout = this.layoutMap.getLayout(vertex.getNode());
            if (layout != null) {
                Rectangle2D bounds = layout.getBounds();
                side = getSide(bounds, point);
            }
        }
        return side;
    }

    // -------------------------- Edges ---------------------------------------

    /**
     * Helper method to perform safe JCell casting.
     * @param cell the edge to be converted.
     * @param layout information regarding layout of the node.
     */
    private void appendTikzEdge(JCell<G> cell, JEdgeLayout layout) {
        if (cell instanceof JEdge) {
            JEdge<G> edge = (JEdge<G>) cell;
            appendTikzEdge(edge, layout);
        }
    }

    /**
     * Converts a jGraph edge to a Tikz string representation.
     * @param edge the edge to be converted.
     * @param layout information regarding layout of the edge.
     */
    private void appendTikzEdge(JEdge<G> edge, JEdgeLayout layout) {
        if (!edge.getVisuals()
            .isVisible()) {
            return;
        }

        append(BEGIN_EDGE);
        appendEdgeStyles(edge);

        if (layout != null) {
            switch (layout.getLineStyle()) {
            case ORTHOGONAL:
                appendOrthogonalLayout(edge, layout);
                break;
            case BEZIER:
                appendBezierLayout(edge, layout);
                break;
            case SPLINE:
                appendSplineLayout(edge, layout);
                break;
            case MANHATTAN:
                appendManhattanLayout(edge, layout);
                break;
            default:
                throw new IllegalArgumentException("Unknown line style!");
            }
        } else {
            appendDefaultLayout(edge);
        }

        append(END_EDGE);
    }

    /**
     * Find the proper Tikz styles for a given edge.
     * @param edge the edge to be analysed.
     */
    private void appendEdgeStyles(JEdge<G> edge) {
        ArrayList<String> styles = new ArrayList<>();
        styles.add(""); // Placeholder for the main style.
        for (Look look : edge.getLooks()) {
            if (TikzStylesExtractor.mainLooks.contains(look)) {
                styles.set(0, look.name()
                    .toLowerCase() + TikzStylesExtractor.EDGE_SUFFIX);
            } else if (!TikzStylesExtractor.unusedLooks.contains(look)) {
                styles.add(look.name()
                    .toLowerCase());
            }
        }
        addAdditionalStyles(edge, styles);
        append(styles.toString());
    }

    /**
    * Creates an edge with a default layout. The edge is drawn as a straight
    * line from source to target node and the label is placed half-way.
    * @param edge the edge to be converted.
    */
    private void appendDefaultLayout(JEdge<G> edge) {
        JVertex<G> srcVertex = edge.getSourceVertex();
        JVertex<G> tgtVertex = edge.getTargetVertex();
        appendSourceNode(srcVertex, tgtVertex);
        append(encloseSpace(DOUBLE_DASH));
        appendEdgeLabelInPath(edge);
        appendTargetNode(srcVertex, tgtVertex);
    }

    /**
     * Creates an edge with orthogonal lines. Only the intermediate points of
     * the layout information are used, the first and last points are discarded
     * and replaced by Tikz node names and we let Tikz find the anchors.
     * @param edge the edge to be converted.
     * @param layout information regarding layout of the edge.
     * @param connection the string with the type of Tikz connection to be used.
     */
    private void appendOrthogonalLayout(JEdge<G> edge, JEdgeLayout layout, String connection) {

        JVertex<G> srcVertex = edge.getSourceVertex();
        JVertex<G> tgtVertex = edge.getTargetVertex();
        List<Point2D> points = layout.getPoints();

        if (points.size() == 2) {
            appendSourceNode(srcVertex, tgtVertex);
            append(encloseSpace(connection));
            appendTargetNode(srcVertex, tgtVertex);
        } else {
            int firstPoint = 1;
            int lastPoint = points.size() - 2;

            appendNode(srcVertex, points.get(firstPoint));
            append(encloseSpace(connection));
            // Intermediate points
            for (int i = firstPoint; i <= lastPoint; i++) {
                appendPoint(points, i);
                // When using the MANHATTAN style sometimes we cannot use the ANGLE
                // routing when going from the last point to the node because the
                // arrow will be in the wrong direction.
                // We test this condition here.
                if (i == lastPoint && connection.equals(ANGLE)
                    && isHorizontalOrVertical(points, i, tgtVertex)) {
                    // We are in this special case, use straight routing.
                    append(encloseSpace(DOUBLE_DASH));
                } else {
                    // A normal case, just use the provided connection string.
                    append(encloseSpace(connection));
                }
            }
            appendNode(tgtVertex, points.get(lastPoint));
        }
        append(END_PATH);
        appendEdgeLabel(edge, layout, points);
    }

    /**
     * Creates an edge with orthogonal lines. Only the intermediate points of
     * the layout information are used, the first and last points are discarded
     * and replaced by Tikz node names and we let Tikz find the anchors.
     * @param edge the edge to be converted.
     * @param layout information regarding layout of the edge.
     */
    private void appendOrthogonalLayout(JEdge<G> edge, JEdgeLayout layout) {
        appendOrthogonalLayout(edge, layout, DOUBLE_DASH);
    }

    /**
     * Creates an edge with bezier lines. Only the intermediate points of
     * the layout information are used, the first and last points are discarded
     * and replaced by Tikz node names and we let Tikz find the anchors.
     * Each point of the layout information is interspersed with control points
     * from the bezier lines.
     * @param edge the edge to be converted.
     * @param layout information regarding layout of the edge.
     */
    private void appendBezierLayout(JEdge<G> edge, JEdgeLayout layout) {
        JVertex<G> srcVertex = edge.getSourceVertex();
        assert srcVertex != null; // model has been fully initialised by now
        JVertex<G> tgtVertex = edge.getTargetVertex();
        assert tgtVertex != null; // model has been fully initialised by now
        List<Point2D> points = layout.getPoints();

        // Compute the bezier line.
        Bezier bezier = new Bezier(points.toArray(new Point2D[points.size()]));
        Point2D[] bPoints = bezier.getPoints();

        if (bPoints == null) {
            // The edge is with a bezier style but it does not have any bezier
            // points, just use standard layout.
            appendDefaultLayout(edge);
            return;
        }

        if (points.size() <= 4) {
            // If we have 4 or less points in the edge, we need to resort to
            // some black magic code when making the translation to Tikz.
            // This is needed to make the Tikz figure look similar to what
            // is shown in Groove. Otherwise, the bezier curve in Tikz is not
            // smooth enough.
            boolean isLoop = srcVertex.getNode()
                .equals(tgtVertex.getNode());
            appendNode(srcVertex);
            int i = 1; // Index for edge points.
            int j = 0; // Index for bezier points. Always j = i - 1;
            while (j < bPoints.length - 1) {
                append(BEGIN_CONTROLS);
                if (isLoop) {
                    // Drawing a loop edge is a special case, for the first and
                    // last control entry we need to use a point of the edge
                    // instead of a bezier point, otherwise Tikz draws the loop
                    // incorrectly.
                    if (i == points.size() - 1) {
                        // This is the LAST control entry.
                        appendPoint(points, i - 1);
                    } else {
                        // Not a special case, just use a bezier point.
                        appendPoint(bPoints[j]);
                    }
                    append(AND);
                    if (i == 1) {
                        // This is the FIRST control entry.
                        appendPoint(points, i);
                    } else {
                        // Not a special case, just use a bezier point.
                        appendPoint(bPoints[j + 1]);
                    }
                } else {
                    // The edge is not a loop, just use the bezier points.
                    appendPoint(bPoints[j]);
                    append(AND);
                    appendPoint(bPoints[j + 1]);
                }
                append(END_CONTROLS);
                // Use the edge intermediate point as the next coordinate.
                if (points.size() > 3 && i < points.size() - 1) {
                    appendPoint(points, i);
                }
                i++;
                j++;
            }
            appendNode(tgtVertex);
        } else {
            // General case, we have an edge with more than 4 points. We have
            // enough points to make the curve smooth, so just revert to
            // normal bezier calculation.

            // The first part of the curve is quadratic.
            appendNode(srcVertex);
            append(BEGIN_CONTROLS);
            appendPoint(bPoints[0]);
            append(END_CONTROLS);
            appendPoint(points, 1);

            // The middle part of the curve is cubic.
            for (int i = 2; i < points.size() - 1; i++) {
                append(BEGIN_CONTROLS);
                appendPoint(bPoints[2 * i - 3]);
                append(AND);
                appendPoint(bPoints[2 * i - 2]);
                append(END_CONTROLS);
                appendPoint(points, i);
            }

            // The last part of the curve is again quadratic.
            append(BEGIN_CONTROLS);
            appendPoint(bPoints[bPoints.length - 1]);
            append(END_CONTROLS);
            appendNode(tgtVertex);
        }

        append(END_PATH);
        appendEdgeLabel(edge, layout, points);
    }

    /**
     * This is not implemented yet. The Bezier style is used instead.
     * @param edge the edge to be converted.
     * @param layout information regarding layout of the edge.
     */
    private void appendSplineLayout(JEdge<G> edge, JEdgeLayout layout) {
        System.err.println(
            "Sorry, the SPLINE line style is not yet " + "supported, using BEZIER style...");
        appendBezierLayout(edge, layout);
    }

    /**
     * Creates an edge with Manhattan lines. Only the intermediate points of
     * the layout information are used, the first and last points are discarded
     * and replaced by Tikz node names and we let Tikz find the anchors.
     * @param edge the edge to be converted.
     * @param layout information regarding layout of the edge.
     */
    private void appendManhattanLayout(JEdge<G> edge, JEdgeLayout layout) {
        appendOrthogonalLayout(edge, layout, ANGLE);
    }

    /**
     * Checks whether the given target node is in a proper position with
     * respect to the given source node and appends the source node to the
     * string builder, together with a node anchor that keeps the edge
     * horizontal or vertical.
     */
    private void appendSourceNode(JVertex<G> srcNode, JVertex<G> tgtNode) {
        if (this.layoutMap != null) {
            JVertexLayout tgtLayout = this.layoutMap.getLayout(tgtNode.getNode());
            if (tgtLayout != null) {
                Rectangle2D tgtBounds = tgtLayout.getBounds();
                Point2D tgtCenter =
                    new Point2D.Double(tgtBounds.getCenterX(), tgtBounds.getCenterY());
                appendNode(srcNode, tgtCenter);
            }
        } else {
            appendNode(srcNode);
        }
    }

    /**
     * Checks whether the given source node is in a proper position with
     * respect to the given target node and appends the target node to the
     * string builder, together with a node anchor that keeps the edge
     * horizontal or vertical.
     */
    private void appendTargetNode(JVertex<G> srcNode, JVertex<G> tgtNode) {
        if (this.layoutMap != null) {
            JVertexLayout srcLayout = this.layoutMap.getLayout(srcNode.getNode());
            JVertexLayout tgtLayout = this.layoutMap.getLayout(tgtNode.getNode());
            if (srcLayout != null && tgtLayout != null) {
                Rectangle2D tgtBounds = tgtLayout.getBounds();
                Point2D tgtCenter =
                    new Point2D.Double(tgtBounds.getCenterX(), tgtBounds.getCenterY());
                int side = getSide(srcNode, tgtCenter);
                if (side == 0) {
                    Rectangle2D srcBounds = srcLayout.getBounds();
                    Point2D srcCenter =
                        new Point2D.Double(srcBounds.getCenterX(), srcBounds.getCenterY());
                    appendNode(tgtNode, srcCenter);
                } else {
                    appendNode(tgtNode);
                }
            }
        } else {
            appendNode(tgtNode);
        }
    }

    /** Appends the edge label along the path that is being drawn. */
    private void appendEdgeLabelInPath(JEdge<G> edge) {
        if (hasNonEmptyLabel(edge)) {
            append(NODE);
            appendEdgeLabel(edge);
        }
    }

    private void appendEdgeLabel(JEdge<G> edge) {
        if (hasNonEmptyLabel(edge)) {
            MultiLabel lines = edge.getVisuals()
                .getLabel();
            List<Point2D> points = edge.getVisuals()
                .getPoints();
            StringBuilder text;
            if (this.jGraph.isShowArrowsOnLabels()) {
                Point2D start = points.get(0);
                Point2D end = points.get(points.size() - 1);
                text = lines.toString(TeXLineFormat.instance(), start, end);
            } else {
                text = lines.toString(TeXLineFormat.instance());
            }
            append(BEGIN_EDGE_LAB);
            append(text);
            append(END_EDGE_LAB);
        }
    }

    /**
     * Creates an extra path to place the edge label which has special
     * placement requirements.
     */
    private void appendEdgeLabel(JEdge<G> edge, JEdgeLayout layout, List<Point2D> points) {
        if (hasNonEmptyLabel(edge)) {
            Point2D labelPos =
                convertRelativeLabelPositionToAbsolute(layout.getLabelPosition(), points);
            // Extra path for the label position.
            append(NODE);
            append(encloseSpace(AT_KEYWORD));
            appendPoint(labelPos);
            appendEdgeLabel(edge);
        }
    }

    /**
     * Checks if two points or a point and a node form an horizontal or
     * vertical edge.
     * @param points a list of points.
     * @param index the index of the point to be checked.
     * @param tgtVertex the target node.
     * @return true if the edge is horizontal or vertical and false otherwise.
     */
    private boolean isHorizontalOrVertical(List<Point2D> points, int index, JVertex<G> tgtVertex) {
        boolean result = false;
        if (this.layoutMap != null) {
            JVertexLayout layout = this.layoutMap.getLayout(tgtVertex.getNode());
            if (layout != null) {
                Rectangle2D tgtBounds = layout.getBounds();
                if (Math.abs(points.get(index)
                    .getY()
                    - points.get(index + 1)
                        .getY()) < 0.0001
                    || getSide(tgtBounds, points.get(index)) != 0) {
                    result = true;
                }
            }
        }
        return result;
    }

    // ------------------------------------------------------------------------
    // Tikz output strings
    // ------------------------------------------------------------------------

    private static final String ENTER = "\n";
    private static final String BEGIN_TIKZ_FIG =
        "\\begin{tikzpicture}[scale=\\tikzscale,name prefix=%s-]";
    private static final String END_TIKZ_FIG = "\\end{tikzpicture}";
    private static final String BEGIN_NODE = "\\node";
    private static final String AT_KEYWORD = "at";
    private static final String BEGIN_NODE_LAB = " {\\ml{";
    private static final String END_NODE_LAB = "}};" + ENTER;
    private static final String BEGIN_EDGE_LAB = " {\\ml{";
    private static final String END_EDGE_LAB = "}}";
    private static final String EMPTY_NODE_LAB = "{};" + ENTER;
    private static final String BEGIN_EDGE = "\\path";
    private static final String END_PATH = ENTER;
    private static final String END_EDGE = ";" + ENTER;
    private static final String NODE = "node[lab]";
    private static final String PAR_NODE_SUFFIX = "p";
    private static final String PAR_NODE_STYLE = "par_node";
    private static final String DOUBLE_DASH = "--";
    private static final String ANGLE = "-|";
    private static final String BEGIN_CONTROLS = ".. controls ";
    private static final String END_CONTROLS = " .. ";
    private static final String AND = " and ";
    private static final String NORTH = ".north -| ";
    private static final String SOUTH = ".south -| ";
    private static final String EAST = ".east |- ";
    private static final String WEST = ".west |- ";
    private static final String NORTH_WEST = ".north west";
    private static final String DOC = "% To use this figure in your LaTeX " + "document" + ENTER
        + "% import the package groove/resources/groove2tikz.sty" + ENTER + "%" + ENTER;
}
