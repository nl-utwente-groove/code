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
package groove.util;

import groove.control.ControlShape;
import groove.control.ControlTransition;
import groove.graph.Edge;
import groove.graph.GraphShape;
import groove.graph.Node;
import groove.gui.Options;
import groove.gui.jgraph.GraphJEdge;
import groove.gui.jgraph.GraphJModel;
import groove.gui.jgraph.GraphJVertex;
import groove.gui.jgraph.JAttr;
import groove.gui.jgraph.JCell;
import groove.gui.jgraph.ControlJModel.StateJVertex;
import groove.gui.jgraph.ControlJModel.TransitionJEdge;
import groove.gui.layout.JEdgeLayout;
import groove.gui.layout.JVertexLayout;
import groove.gui.layout.LayoutMap;
import groove.lts.LTS;
import groove.rel.RegExprLabel;
import groove.view.FormatException;
import groove.view.aspect.AspectEdge;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.GraphConstants;
import org.jgraph.util.Bezier;

/**
 * Class to perform the conversion from Groove graphs to Tikz format. 
 * @author Eduardo Zambon
 * @version $Revision$
 */
public final class GraphToTikz {

    /**
     * Converts a graph to a Tikz representation.
     * @param model the graph to be converted.
     * @param layoutMap the layout information associated with the graph.
     * @return a string with the Tikz encoding of the graph.
     */
    public static String convertGraphToTikzStr(GraphJModel model,
            LayoutMap<Node,Edge> layoutMap) {

        GraphShape graph = model.getGraph();
        boolean showBackground =
            model.getOptions().getValue(Options.SHOW_BACKGROUND_OPTION) == 1
                    ? true : false;
        StringBuilder result = new StringBuilder();

        result.append(beginTikzFig());

        for (Node node : graph.nodeSet()) {
            GraphJVertex vertex = model.getJVertex(node);
            model.synchroniseLayout(vertex);
            JVertexLayout layout = null;
            if (layoutMap != null) {
                layout = layoutMap.getNode(node);
            }
            boolean isEmphasized = model.isEmphasized(vertex);
            boolean isGrayedOut = model.isGrayedOut(vertex);
            result.append(convertNodeToTikzStr(vertex, layout, showBackground,
                isEmphasized, isGrayedOut));
        }

        for (Edge edge : graph.edgeSet()) {
            JEdgeLayout layout = null;
            if (layoutMap != null) {
                layout = layoutMap.getEdge(edge);
            }
            JCell jCell = model.getJCell(edge);
            boolean isEmphasized = model.isEmphasized(jCell);
            boolean isGrayedOut = model.isGrayedOut(jCell);
            result.append(convertEdgeToTikzStr(jCell, layout, layoutMap,
                isEmphasized, isGrayedOut));
        }

        result.append(endTikzFig());

        return result.toString();
    }

    /**
     * Converts a jGraph node to a Tikz string representation. 
     * @param node the node to be converted.
     * @param layout information regarding layout of the node. 
     * @param showBackground flag to indicate if the node should be filled.
     * @param isEmphasized flag that indicates if the node is emphasized.
     * @param isGrayedOut flag that indicates if the node is grayed out.
     * @return a StringBuilder filled with the Tikz string.
     */
    private static StringBuilder convertNodeToTikzStr(GraphJVertex node,
            JVertexLayout layout, boolean showBackground, boolean isEmphasized,
            boolean isGrayedOut) {

        StringBuilder result = new StringBuilder();

        if (node.isVisible()) {
            result.append(BEGIN_NODE);

            // Styles.
            result.append(convertStyles(node, showBackground, isEmphasized,
                isGrayedOut));

            // Node ID.
            appendNode(node, result);

            // Node Coordinates.
            if (layout != null) {
                result.append(encloseSpace(AT_KEYWORD));
                Rectangle2D bounds = layout.getBounds();
                double x = bounds.getCenterX();
                double y = bounds.getCenterY();
                appendPoint(x, y, true, result);
            }

            // Node Labels.
            List<StringBuilder> lines = node.getLines();
            if (lines.isEmpty()) {
                result.append(EMPTY_NODE_LAB);
            } else {
                result.append(BEGIN_NODE_LAB);
                for (StringBuilder line : node.getLines()) {
                    result.append(convertHtmlToTikz(line));
                }
                // Remove the last \\, if it exists
                if (result.lastIndexOf(CRLF) == result.length() - 2) {
                    result.deleteCharAt(result.length() - 1);
                    result.deleteCharAt(result.length() - 1);
                }
                result.append(END_NODE_LAB);
            }
        }

        return result;
    }

    /**
     * Helper method to perform safe JCell casting.
     * @param cell the edge to be converted.
     * @param layout information regarding layout of the node.
     * @param layoutMap the layout information associated with the graph.
     * @param isEmphasized flag that indicates if the edge is emphasized.
     * @param isGrayedOut flag that indicates if the edge is grayed out.
     * @return a StringBuilder filled with the Tikz string if the JCell could
     *         cast into a valid sub-type or an empty StringBuilder otherwise.
     */
    private static StringBuilder convertEdgeToTikzStr(JCell cell,
            JEdgeLayout layout, LayoutMap<Node,Edge> layoutMap,
            boolean isEmphasized, boolean isGrayedOut) {

        if (cell instanceof GraphJEdge) {
            return convertEdgeToTikzStr((GraphJEdge) cell, layout, layoutMap,
                isEmphasized, isGrayedOut);
        } else {
            return new StringBuilder();
        }
    }

    /**
     * Converts a jGraph edge to a Tikz string representation. 
     * @param edge the edge to be converted.
     * @param layout information regarding layout of the edge.
     * @param layoutMap the layout information associated with the graph.
     * @param isEmphasized flag that indicates if the edge is emphasized.
     * @param isGrayedOut flag that indicates if the edge is grayed out.
     * @return a StringBuilder filled with the Tikz string.
     */
    private static StringBuilder convertEdgeToTikzStr(GraphJEdge edge,
            JEdgeLayout layout, LayoutMap<Node,Edge> layoutMap,
            boolean isEmphasized, boolean isGrayedOut) {

        StringBuilder result = new StringBuilder();

        if (edge.isVisible()) {
            ArrayList<String> styles =
                convertStyles(edge, isEmphasized, isGrayedOut);
            String edgeStyle = styles.get(0);
            String labStyle = styles.get(1);

            result.append(BEGIN_EDGE);
            result.append(encloseBrack(edgeStyle));

            if (layout != null) {
                switch (layout.getLineStyle()) {
                case GraphConstants.STYLE_ORTHOGONAL:
                    appendOrthogonalLayout(edge, layout, layoutMap, labStyle,
                        result);
                    break;
                case GraphConstants.STYLE_BEZIER:
                    appendBezierLayout(edge, layout, layoutMap, labStyle,
                        result);
                    break;
                case GraphConstants.STYLE_SPLINE:
                    appendSplineLayout(edge, layout, layoutMap, labStyle,
                        result);
                    break;
                case JAttr.STYLE_MANHATTAN:
                    appendManhattanLayout(edge, layout, layoutMap, labStyle,
                        result);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown line style!");
                }
            } else {
                appendDefaultLayout(edge, layoutMap, labStyle, result);
            }
        }

        return result;
    }

    /**
     * Creates an edge with a default layout. The edge is drawn as a straight
     * line from source to target node and the label is placed half-way.
     * @param edge the edge to be converted.
     * @param layoutMap the layout information associated with the graph.
     * @param labStyle a string describing the style to be used in the label.
     * @param s a StringBuilder where the Tikz string will be appended.
     */
    private static void appendDefaultLayout(GraphJEdge edge,
            LayoutMap<Node,Edge> layoutMap, String labStyle, StringBuilder s) {

        GraphJVertex srcVertex = edge.getSourceVertex();
        GraphJVertex tgtVertex = edge.getTargetVertex();

        appendSourceNode(srcVertex, tgtVertex, layoutMap, s);
        s.append(encloseSpace(DOUBLE_DASH));
        appendEdgeLabelInPath(edge, labStyle, s);
        appendTargetNode(srcVertex, tgtVertex, layoutMap, s);
        s.append(END_EDGE);
    }

    /**
     * Creates an edge with orthogonal lines. Only the intermediate points of
     * the layout information are used, the first and last points are discarded
     * and replaced by Tikz node names and we let Tikz find the anchors.
     * @param edge the edge to be converted.
     * @param layout information regarding layout of the edge.
     * @param layoutMap the layout information associated with the graph.
     * @param labStyle a string describing the style to be used in the label.
     * @param connection the string with the type of Tikz connection to be used.
     * @param s a StringBuilder where the Tikz string will be appended.
     */
    private static void appendOrthogonalLayout(GraphJEdge edge,
            JEdgeLayout layout, LayoutMap<Node,Edge> layoutMap,
            String labStyle, String connection, StringBuilder s) {

        GraphJVertex srcVertex = edge.getSourceVertex();
        GraphJVertex tgtVertex = edge.getTargetVertex();
        List<Point2D> points = layout.getPoints();
        int firstPoint = 1;
        int lastPoint = points.size() - 2;

        appendNode(srcVertex, points.get(firstPoint), layoutMap, s);
        s.append(encloseSpace(connection));
        // Intermediate points
        for (int i = firstPoint; i <= lastPoint; i++) {
            appendPoint(points, i, s);
            // When using the MANHATTAN style sometimes we cannot use the ANGLE
            // routing when going from the last point to the node because the
            // arrow will be in the wrong direction.
            // We test this condition here.
            if (i == lastPoint && connection.equals(ANGLE)
                && isHorizontalOrVertical(points, i, tgtVertex, layoutMap)) {
                // We are in this special case, use straight routing.
                s.append(encloseSpace(DOUBLE_DASH));
            } else {
                // A normal case, just use the provided connection string.
                s.append(encloseSpace(connection));
            }
        }
        appendNode(tgtVertex, points.get(lastPoint), layoutMap, s);
        s.append(END_PATH);
        appendEdgeLabel(edge, layout, labStyle, points, s);
        s.append(END_EDGE);
    }

    /**
     * Creates an edge with orthogonal lines. Only the intermediate points of
     * the layout information are used, the first and last points are discarded
     * and replaced by Tikz node names and we let Tikz find the anchors.
     * @param edge the edge to be converted.
     * @param layout information regarding layout of the edge.
     * @param layoutMap the layout information associated with the graph.
     * @param labStyle a string describing the style to be used in the label.
     * @param s a StringBuilder where the Tikz string will be appended.
     */
    private static void appendOrthogonalLayout(GraphJEdge edge,
            JEdgeLayout layout, LayoutMap<Node,Edge> layoutMap,
            String labStyle, StringBuilder s) {
        appendOrthogonalLayout(edge, layout, layoutMap, labStyle, DOUBLE_DASH,
            s);
    }

    /**
     * Creates an edge with bezier lines. Only the intermediate points of
     * the layout information are used, the first and last points are discarded
     * and replaced by Tikz node names and we let Tikz find the anchors.
     * Each point of the layout information is interspersed with control points
     * from the bezier lines.
     * @param edge the edge to be converted.
     * @param layout information regarding layout of the edge.
     * @param layoutMap the layout information associated with the graph.
     * @param labStyle a string describing the style to be used in the label.
     * @param s a StringBuilder where the Tikz string will be appended.
     */
    private static void appendBezierLayout(GraphJEdge edge, JEdgeLayout layout,
            LayoutMap<Node,Edge> layoutMap, String labStyle, StringBuilder s) {

        GraphJVertex srcVertex = edge.getSourceVertex();
        GraphJVertex tgtVertex = edge.getTargetVertex();
        List<Point2D> points = layout.getPoints();
        boolean isLoop = srcVertex.getNode().equals(tgtVertex.getNode());

        // Compute the bezier line.
        Bezier bezier = new Bezier(points.toArray(new Point2D[0]));
        Point2D[] bPoints = bezier.getPoints();

        if (bPoints == null) {
            // The edge is with a bezier style but it does not have any bezier
            // points, just use standard layout.
            appendDefaultLayout(edge, layoutMap, labStyle, s);
            return;
        }

        appendNode(srcVertex, s);

        int i = 1; // Index for edge points.
        for (int j = 0; j < bPoints.length - 1; j++) {
            s.append(BEGIN_CONTROLS);
            if (isLoop && i == points.size() - 1) {
                // This is the LAST control entry and we are drawing a loop
                // edge, we need to use a point of the edge instead of a bezier
                // point, otherwise the loop is drawn incorrectly.
                appendPoint(points, i - 1, s);
            } else {
                // No special case, just use a bezier point.
                appendPoint(bPoints[j], s);
            }
            s.append(AND);
            if (i == 1 && isLoop) {
                // This is the FIRST control entry and we are drawing a loop
                // edge, we need to use a point of the edge instead of a bezier
                // point, otherwise the loop is drawn incorrectly.
                appendPoint(points, i, s);
            } else {
                // No special case, just use a bezier point.
                appendPoint(bPoints[j + 1], s);
            }
            s.append(END_CONTROLS);
            if (i != points.size() - 1 && points.size() > 3) {
                // Intermediate point
                appendPoint(points, i, s);
            }
            i++;
        }

        appendNode(tgtVertex, s);
        s.append(END_PATH);
        appendEdgeLabel(edge, layout, labStyle, points, s);
        s.append(END_EDGE);
    }

    /**
     * This is not implemented yet. The Bezier style is used instead.
     * @param edge the edge to be converted.
     * @param layout information regarding layout of the edge.
     * @param layoutMap the layout information associated with the graph.
     * @param labStyle a string describing the style to be used in the label.
     * @param s a StringBuilder where the Tikz string will be appended.
     */
    private static void appendSplineLayout(GraphJEdge edge, JEdgeLayout layout,
            LayoutMap<Node,Edge> layoutMap, String labStyle, StringBuilder s) {

        System.err.println("Sorry, the SPLINE line style is not yet "
            + "supported, using BEZIER style...");
        appendBezierLayout(edge, layout, layoutMap, labStyle, s);
    }

    /**
     * Creates an edge with Manhattan lines. Only the intermediate points of
     * the layout information are used, the first and last points are discarded
     * and replaced by Tikz node names and we let Tikz find the anchors.
     * @param edge the edge to be converted.
     * @param layout information regarding layout of the edge.
     * @param layoutMap the layout information associated with the graph.
     * @param labStyle a string describing the style to be used in the label.
     * @param s a StringBuilder where the Tikz string will be appended.
     */
    private static void appendManhattanLayout(GraphJEdge edge,
            JEdgeLayout layout, LayoutMap<Node,Edge> layoutMap,
            String labStyle, StringBuilder s) {
        appendOrthogonalLayout(edge, layout, layoutMap, labStyle, ANGLE, s);
    }

    /* Helper methods */

    /**
     * Appends the node name to the given string builder.
     */
    private static void appendNode(GraphJVertex node, StringBuilder s) {
        s.append(encloseSpace(enclosePar(node.getNode().toString())));
    }

    /**
     * Checks whether the given point is in a proper position with respect to
     * the given node and appends the node to the string builder, together
     * with a node anchor that keeps the edge horizontal or vertical.
     */
    private static void appendNode(GraphJVertex node, Point2D point,
            LayoutMap<Node,Edge> layoutMap, StringBuilder s) {

        int side = getSide(node, point, layoutMap);
        if (side == 0) {
            // The point is not aligned with the node, just use normal routing.
            appendNode(node, s);
        } else {
            String coord = getCoordString(side);
            String nodeName = node.getNode().toString();
            s.append(enclosePar(nodeName + coord + appendPoint(point, false)));
        }
    }

    /**
     * Checks whether the given target node is in a proper position with
     * respect to the given source node and appends the source node to the
     * string builder, together with a node anchor that keeps the edge
     * horizontal or vertical.
     */
    private static void appendSourceNode(GraphJVertex srcNode,
            GraphJVertex tgtNode, LayoutMap<Node,Edge> layoutMap,
            StringBuilder s) {

        if (layoutMap != null) {
            JVertexLayout tgtLayout = layoutMap.getNode(tgtNode.getNode());
            if (tgtLayout != null) {
                Rectangle2D tgtBounds = tgtLayout.getBounds();
                Point2D tgtCenter =
                    new Point2D.Double(tgtBounds.getCenterX(),
                        tgtBounds.getCenterY());
                appendNode(srcNode, tgtCenter, layoutMap, s);
            }
        } else {
            appendNode(srcNode, s);
        }
    }

    /**
     * Checks whether the given source node is in a proper position with
     * respect to the given target node and appends the target node to the
     * string builder, together with a node anchor that keeps the edge
     * horizontal or vertical.
     */
    private static void appendTargetNode(GraphJVertex srcNode,
            GraphJVertex tgtNode, LayoutMap<Node,Edge> layoutMap,
            StringBuilder s) {

        if (layoutMap != null) {
            JVertexLayout srcLayout = layoutMap.getNode(srcNode.getNode());
            JVertexLayout tgtLayout = layoutMap.getNode(tgtNode.getNode());
            if (srcLayout != null && tgtLayout != null) {
                Rectangle2D tgtBounds = tgtLayout.getBounds();
                Point2D tgtCenter =
                    new Point2D.Double(tgtBounds.getCenterX(),
                        tgtBounds.getCenterY());
                int side = getSide(srcNode, tgtCenter, layoutMap);
                if (side == 0) {
                    Rectangle2D srcBounds = srcLayout.getBounds();
                    Point2D srcCenter =
                        new Point2D.Double(srcBounds.getCenterX(),
                            srcBounds.getCenterY());
                    appendNode(tgtNode, srcCenter, layoutMap, s);
                } else {
                    appendNode(tgtNode, s);
                }
            }
        } else {
            appendNode(tgtNode, s);
        }
    }

    private static void appendEdgeLabel(GraphJEdge edge, StringBuilder s) {
        Edge e = edge.getEdge();
        if (e instanceof AspectEdge) {
            try {
                if (((AspectEdge) e).getModelLabel() instanceof RegExprLabel) {
                    // We have a regular expression on the label, make it italic.
                    s.append(encloseCurly(encloseItalicStyle(escapeSpecialChars(edge.getText()))));
                } else {
                    // This is a normal AspectEdge.
                    s.append(encloseCurly(escapeSpecialChars(edge.getText())));
                }
            } catch (FormatException except) {
                // Don't export a graph with errors...
            }
        } else {
            s.append(encloseCurly(escapeSpecialChars(edge.getText())));
        }
    }

    /**
     * Appends the edge label along the path that is being drawn.
     */
    private static void appendEdgeLabelInPath(GraphJEdge edge, String labStyle,
            StringBuilder s) {

        s.append(NODE);
        s.append(encloseBrack(labStyle));
        appendEdgeLabel(edge, s);
    }

    /**
     * Creates an extra path to place the edge label which has especial
     * placement requirements.
     */
    private static void appendEdgeLabel(GraphJEdge edge, JEdgeLayout layout,
            String labStyle, List<Point2D> points, StringBuilder s) {

        Point2D labelPos =
            convertRelativeLabelPositionToAbsolute(layout.getLabelPosition(),
                points);
        // Extra path for the label position.
        s.append(BEGIN_NODE);
        s.append(encloseBrack(labStyle));
        s.append(encloseSpace(AT_KEYWORD));
        appendPoint(labelPos, s);
        appendEdgeLabel(edge, s);
    }

    /**
     * Appends the point in position i of a list of points to a string builder.
     */
    private static void appendPoint(List<Point2D> points, int i, StringBuilder s) {

        appendPoint(points.get(i), s);
    }

    /**
     * Converts a point to a string.
     * @param point the point to be converted.
     * @param usePar flag to indicate whether the point coordinates should be
     *               enclosed in parentheses or not.
     * @return the string representation of the given point.
     */
    private static String appendPoint(Point2D point, boolean usePar) {
        double x = point.getX();
        double y = point.getY();
        StringBuilder s = new StringBuilder();
        appendPoint(x, y, usePar, s);
        return s.toString();
    }

    /**
     * Appends the given point to the string builder.
     */
    private static void appendPoint(Point2D point, StringBuilder s) {
        double x = point.getX();
        double y = point.getY();
        appendPoint(x, y, true, s);
    }

    /**
     * Appends the given points to the string builder. The coordinates are
     * scaled by a constant factor and the y-coordinate is inverted as the
     * jGraph and Tikz representation are different.
     * @param x the x coordinate of the point.
     * @param y the y coordinate of the point.
     * @param usePar flag to indicate whether the point coordinates should be
     *               enclosed in parentheses or not.
     * @param s a StringBuilder where the Tikz string will be appended.
     */
    private static void appendPoint(double x, double y, boolean usePar,
            StringBuilder s) {

        double scale = 100.0;
        double adjX = x / scale;
        double adjY = -1.0 * (y / scale);
        String format = "%5.3f, %5.3f";
        Formatter f = new Formatter();

        if (usePar) {
            format = enclosePar(format);
        }
        s.append(f.format(Locale.US, format, adjX, adjY).toString());
    }

    /**
     * Adapted from jGraph.
     * Converts an relative label position (x is distance along edge and y is
     * distance above/below edge vector) into an absolute coordination point.
     * @param geometry the relative label position.
     * @param points the list of points along the edge.
     * @return the absolute label position.
     */
    private static Point2D convertRelativeLabelPositionToAbsolute(
            Point2D geometry, List<Point2D> points) {

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

    /**
     * Checks if two points or a point and a node form an horizontal or
     * vertical edge.
     * @param points a list of points.
     * @param index the index of the point to be checked.
     * @param tgtVertex the target node.
     * @param layoutMap the layout information associated with the graph.
     * @return true if the edge is horizontal or vertical and false otherwise.
     */
    private static boolean isHorizontalOrVertical(List<Point2D> points,
            int index, GraphJVertex tgtVertex, LayoutMap<Node,Edge> layoutMap) {

        boolean result = false;

        if (layoutMap != null) {
            JVertexLayout layout = layoutMap.getNode(tgtVertex.getNode());
            if (layout != null) {
                Rectangle2D tgtBounds = layout.getBounds();
                if (points.get(index).getY() == points.get(index + 1).getY()
                    || getSide(tgtBounds, points.get(index)) != 0) {
                    result = true;
                }
            }
        }

        return result;
    }

    /**
     * Checks on which side of a node a point lies.
     * @param vertex the node to be checked.
     * @param point the point to be checked.
     * @param layoutMap the layout information associated with the graph.
     * @return 1 if the point lies east, 2 if it lies north, 3 if it lies west,
     *         4 if it lies south, and 0 if its outside a proper position.
     */
    private static int getSide(GraphJVertex vertex, Point2D point,
            LayoutMap<Node,Edge> layoutMap) {

        int side = 0;
        if (layoutMap != null) {
            JVertexLayout layout = layoutMap.getNode(vertex.getNode());
            if (layout != null) {
                Rectangle2D bounds = layout.getBounds();
                side = getSide(bounds, point);
            }
        }
        return side;
    }

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
     * Scans the HTML string of a node and convert the tags to Tikz.
     * @param htmlLine the HTML string to be converted.
     * @return a produced Tikz string as a StringBuilder.
     */
    private static StringBuilder convertHtmlToTikz(StringBuilder htmlLine) {
        StringBuilder result = new StringBuilder();

        StringBuilder line = escapeSpecialChars(htmlLine);
        int i = line.indexOf(Converter.HTML_EXISTS);
        if (i > -1) {
            result.append(line.substring(0, i));
            result.append(EXISTS_STR);
        } else if (line.indexOf(Converter.HTML_FORALL) > -1) {
            if (line.indexOf(Converter.SUPER_TAG.tagBegin) > -1) {
                result.append(FORALLX_STR);
            } else {
                result.append(FORALL_STR);
            }
        } else if (line.indexOf(Converter.ITALIC_TAG.tagBegin) > -1) {
            result.append(encloseItalicStyle(Converter.ITALIC_TAG.off(line)));
        } else if (line.indexOf(Converter.STRONG_TAG.tagBegin) > -1) {
            result.append(enclose(Converter.STRONG_TAG.off(line), BOLD_STYLE,
                "}"));
        } else {
            result.append(line);
        }

        result.append(CRLF);

        return result;
    }

    /**
     * Escapes the special LaTeX characters in the line and copies the rest.
     * @param line the string to be escaped.
     * @return the line with escaped characters, if any.
     */
    private static StringBuilder escapeSpecialChars(StringBuilder line) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            switch (c) {
            case '&': // We have to check if the & is part of a special
                // HTML char.
                if (line.charAt(i + 1) == '#') {
                    // Yes, it is. Keep it.
                    result.append("&#");
                    i++;
                } else { // It's not.
                    result.append(AMP);
                }
                break;
            case '$':
                result.append(DOLLAR);
                break;
            case '#':
                result.append(NUMBER);
                break;
            case '|':
                result.append(VERT_BAR);
                break;
            case '%':
                result.append(PERCENT);
                break;
            case '_':
                result.append(UNDERSCORE);
                break;
            case '{':
                result.append(LEFT_CURLY);
                break;
            case '}':
                result.append(RIGHT_CURLY);
                break;
            case '[':
                result.append(LEFT_SQUARE);
                break;
            case ']':
                result.append(RIGHT_SQUARE);
                break;
            case '^':
                result.append(CIRCUNFLEX);
                break;
            case '~':
                result.append(TILDE);
                break;
            case '\\':
                result.append(BACKSLASH);
                break;
            case Groove.LC_PI:
                result.append(PI);
                break;
            default:
                result.append(c);
            }
        }

        return result;
    }

    /**
     * Escapes the special LaTeX characters in the line and copies the rest.
     * @param line the string to be escaped.
     * @return the line with escaped characters, if any.
     */
    private static String escapeSpecialChars(String line) {
        return escapeSpecialChars(new StringBuilder(line)).toString();
    }

    private static String unescapeSquareBrack(String string) {
        StringBuilder result = new StringBuilder(string);

        int i = result.indexOf(LEFT_SQUARE);
        while (i > -1) {
            result.replace(i, i + LEFT_SQUARE.length(), "[");
            i = result.indexOf(LEFT_SQUARE);
        }
        i = result.indexOf(RIGHT_SQUARE);
        while (i > -1) {
            result.replace(i, i + RIGHT_SQUARE.length(), "]");
            i = result.indexOf(RIGHT_SQUARE);
        }

        return result.toString();
    }

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

    private static String encloseItalicStyle(String string) {
        return enclose(unescapeSquareBrack(string), ITALIC_STYLE, "}");
    }

    /**
     * Produces a string with the proper Tikz styles of a given node.
     * @param node the node to be converted.
     * @param showBackground flag to indicate if the node should be filled.
     * @param isEmphasized flag that indicates if the node is emphasized.
     * @param isGrayedOut flag that indicates if the node is grayed out.
     * @return a string with all the Tikz styles to be used.
     */
    private static String convertStyles(GraphJVertex node,
            boolean showBackground, boolean isEmphasized, boolean isGrayedOut) {

        if (node instanceof StateJVertex) {
            return convertStyles((StateJVertex) node, showBackground,
                isEmphasized, isGrayedOut);
        }

        ArrayList<String> styles = new ArrayList<String>();
        Collection<String> allLabels = node.getPlainLabels();

        if (allLabels.contains(DEL_COL)) {
            // Eraser node
            styles.add(ERASER_NODE_STYLE);
        } else if (allLabels.contains(NEW_COL)) {
            // Creator node
            styles.add(CREATOR_NODE_STYLE);
        } else if (allLabels.contains(NOT_COL)) {
            // Embargo node
            styles.add(EMBARGO_NODE_STYLE);
        } else {
            // Reader node
            if (isGrayedOut) {
                styles.add(THIN_NODE_STYLE);
            } else {
                styles.add(BASIC_NODE_STYLE);
            }
        }

        if (node.isValueNode()) {
            styles.add(ATTRIBUTE_NODE_STYLE);
        } else if (node.isProductNode()) {
            styles.add(PRODUCT_NODE_STYLE);
        } else {
            for (String label : allLabels) {
                if (label.contains(FORALL) || label.contains(EXISTS + ":")
                    || label.contains(FORALLX) || label.contains(EXISTS + "=")) {
                    styles.add(QUANTIFIER_NODE_STYLE);
                    break;
                }
            }

        }

        // LTS nodes
        if (allLabels.contains(LTS.FINAL_LABEL_TEXT)) {
            styles.add(FINAL_NODE_STYLE);
        } else if (allLabels.contains(LTS.OPEN_LABEL_TEXT)) {
            styles.add(OPEN_NODE_STYLE);
        } else if (allLabels.contains(LTS.START_LABEL_TEXT)) {
            styles.add(START_NODE_STYLE);
        }

        if (isEmphasized) {
            styles.add(BOLD_LINE);
        }

        if (!showBackground) {
            styles.add(WHITE_FILL);
        }

        return styles.toString();
    }

    /**
     * Produces a string with the proper Tikz styles of a given control node.
     * @param node the control node to be converted.
     * @param showBackground flag to indicate if the node should be filled.
     * @param isEmphasized flag that indicates if the node is emphasized.
     * @param isGrayedOut flag that indicates if the node is grayed out.
     * @return a string with all the Tikz styles to be used.
     */
    private static String convertStyles(StateJVertex node,
            boolean showBackground, boolean isEmphasized, boolean isGrayedOut) {

        ArrayList<String> styles = new ArrayList<String>();

        if (node.isStart()) {
            styles.add(CONTROL_START_NODE_STYLE);
        } else if (node.getNode().isSuccess()) {
            styles.add(CONTROL_SUCCESS_NODE_STYLE);
        } else {
            styles.add(CONTROL_NODE_STYLE);
        }

        if (isEmphasized) {
            styles.add(BOLD_LINE);
        }

        if (isGrayedOut) {
            styles.add(THIN_NODE_STYLE);
        }

        if (!showBackground) {
            styles.add(WHITE_FILL);
        }

        return styles.toString();
    }

    /**
     * Find the proper Tikz styles for a given edge.
     * @param edge the edge to be analysed.
     * @param isEmphasized flag that indicates if the edge is emphasized.
     * @param isGrayedOut flag that indicates if the edge is grayed out.
     * @return an array of size two. The first string is the edge style and the
     *         second one is the label style.
     */
    private static ArrayList<String> convertStyles(GraphJEdge edge,
            boolean isEmphasized, boolean isGrayedOut) {

        if (edge instanceof TransitionJEdge) {
            return convertStyles((TransitionJEdge) edge, isEmphasized,
                isGrayedOut);
        }

        ArrayList<String> styles = new ArrayList<String>();

        if (edge.getRole().equals(DEL)) {
            styles.add(ERASER_EDGE_STYLE);
            styles.add(ERASER_LABEL_STYLE);
        } else if (edge.getRole().equals(NEW)) {
            styles.add(CREATOR_EDGE_STYLE);
            styles.add(CREATOR_LABEL_STYLE);
        } else if (edge.getRole().equals(NOT)) {
            styles.add(EMBARGO_EDGE_STYLE);
            styles.add(EMBARGO_LABEL_STYLE);
        } else { // role == "use"
            styles.add(BASIC_EDGE_STYLE);
            styles.add(BASIC_LABEL_STYLE);
        }

        // Quantification edges.
        Collection<String> col = new ArrayList<String>();
        col.addAll(edge.getSourceVertex().getPlainLabels());
        col.addAll(edge.getTargetVertex().getPlainLabels());
        for (String label : col) {
            if (label.contains(EXISTS + ":") || label.contains(EXISTS + "=")
                || label.contains(FORALL) || label.contains(FORALLX)) {
                styles.set(0, QUANTIFIER_EDGE_STYLE);
                styles.set(1, BASIC_LABEL_STYLE);
            }
        }

        if (isGrayedOut) {
            styles.set(0, THIN_EDGE_STYLE);
            styles.set(1, THIN_LABEL_STYLE);
        }

        if (isEmphasized) {
            styles.set(0, styles.get(0) + ", " + BOLD_LINE);
        }

        // Check if we should draw the end arrow of the edge.
        AttributeMap attrMap = edge.getAttributes();
        if (GraphConstants.getLineEnd(attrMap) == GraphConstants.ARROW_NONE) {
            styles.set(0, styles.get(0) + ", " + UNDIRECTED_EDGE_STYLE);
        }

        return styles;
    }

    /**
     * Find the proper Tikz styles for a given control edge.
     * @param edge the control edge to be analysed.
     * @param isEmphasized flag that indicates if the edge is emphasized.
     * @param isGrayedOut flag that indicates if the edge is grayed out.
     * @return an array of size two. The first string is the edge style and the
     *         second one is the label style.
     */
    private static ArrayList<String> convertStyles(TransitionJEdge edge,
            boolean isEmphasized, boolean isGrayedOut) {

        ArrayList<String> styles = new ArrayList<String>();

        ControlTransition t = edge.getTransition();
        if (t instanceof ControlShape) {
            styles.add(CONTROL_SHAPE_EDGE_STYLE);
        } else if (t.isLambda()) {
            styles.add(CONTROL_LAMBDA_EDGE_STYLE);
        } else if (t.hasFailures()) {
            styles.add(CONTROL_FAILURE_EDGE_STYLE);
        } else {
            styles.add(CONTROL_EDGE_STYLE);
        }
        styles.add(CONTROL_LABEL_STYLE);

        if (isGrayedOut) {
            styles.set(0, THIN_EDGE_STYLE);
            styles.set(1, THIN_LABEL_STYLE);
        }

        if (isEmphasized) {
            styles.set(0, styles.get(0) + ", " + BOLD_LINE);
        }

        return styles;
    }

    /**
     * @return the line necessary to begin a Tikz figure.
     */
    public static String beginTikzFig() {
        return DOC + BEGIN_TIKZ_FIG + "\n";
    }

    /**
     * @return the line necessary to end a Tikz figure.
     */
    public static String endTikzFig() {
        return END_TIKZ_FIG + "\n";
    }

    // Labels
    private static final String DEL_COL = "del:";
    private static final String DEL = "del";
    private static final String EXISTS = "exists";
    private static final String FORALL = "forall:";
    private static final String FORALLX = "forallx:";
    private static final String NEW_COL = "new:";
    private static final String NEW = "new";
    private static final String NOT_COL = "not:";
    private static final String NOT = "not";

    // Tikz output
    private static final String CRLF = "\\\\";
    private static final String BEGIN_TIKZ_FIG =
        "\\begin{tikzpicture}[scale=\\tikzscale]";
    private static final String END_TIKZ_FIG =
        "\\userdefinedmacro\n" + "\\end{tikzpicture}\n"
            + "\\renewcommand{\\userdefinedmacro}{\\relax}";
    private static final String BEGIN_NODE = "\\node";
    private static final String AT_KEYWORD = "at";
    private static final String BEGIN_NODE_LAB = " {\\ml{";
    private static final String END_NODE_LAB = "}};\n";
    private static final String EMPTY_NODE_LAB = "{};\n";
    private static final String EXISTS_STR = "$\\exists$";
    private static final String FORALL_STR = "$\\forall$";
    private static final String FORALLX_STR = "$\\forall^{>0}$";
    private static final String ITALIC_STYLE = "\\textit{";
    private static final String BOLD_STYLE = "\\textbf{";
    private static final String BEGIN_EDGE = "\\path";
    private static final String END_PATH = ";\n";
    private static final String END_EDGE = END_PATH;
    private static final String NODE = "node";
    private static final String BASIC_NODE_STYLE = "node";
    private static final String BASIC_EDGE_STYLE = "edge";
    private static final String BASIC_LABEL_STYLE = "lab";
    private static final String ERASER_NODE_STYLE = "delnode";
    private static final String ERASER_EDGE_STYLE = "deledge";
    private static final String ERASER_LABEL_STYLE = "dellab";
    private static final String CREATOR_NODE_STYLE = "newnode";
    private static final String CREATOR_EDGE_STYLE = "newedge";
    private static final String CREATOR_LABEL_STYLE = "newlab";
    private static final String EMBARGO_NODE_STYLE = "nacnode";
    private static final String EMBARGO_EDGE_STYLE = "nacedge";
    private static final String EMBARGO_LABEL_STYLE = "naclab";
    private static final String THIN_NODE_STYLE = "thinnode";
    private static final String THIN_EDGE_STYLE = "thinedge";
    private static final String THIN_LABEL_STYLE = "thinlab";
    private static final String ATTRIBUTE_NODE_STYLE = "attr";
    private static final String PRODUCT_NODE_STYLE = "prod";
    private static final String QUANTIFIER_NODE_STYLE = "quantnode";
    private static final String QUANTIFIER_EDGE_STYLE = "quantedge";
    private static final String CONTROL_NODE_STYLE = "cnode";
    private static final String CONTROL_START_NODE_STYLE = "cstart";
    private static final String CONTROL_SUCCESS_NODE_STYLE = "csuccess";
    private static final String CONTROL_EDGE_STYLE = "cedge";
    private static final String CONTROL_SHAPE_EDGE_STYLE = "cshape";
    private static final String CONTROL_LAMBDA_EDGE_STYLE = "clambda";
    private static final String CONTROL_FAILURE_EDGE_STYLE = "cfailure";
    private static final String UNDIRECTED_EDGE_STYLE = "-";
    private static final String CONTROL_LABEL_STYLE = "clab";
    private static final String FINAL_NODE_STYLE = "final";
    private static final String START_NODE_STYLE = "start";
    private static final String OPEN_NODE_STYLE = "open";
    private static final String WHITE_FILL = "whitefill";
    private static final String BOLD_LINE = "bold";
    private static final String DOUBLE_DASH = "--";
    private static final String ANGLE = "-|";
    private static final String BEGIN_CONTROLS = ".. controls ";
    private static final String END_CONTROLS = " .. ";
    private static final String AND = " and ";
    private static final String AMP = "\\&";
    private static final String DOLLAR = "\\$";
    private static final String NUMBER = "\\#";
    private static final String PERCENT = "\\%";
    private static final String UNDERSCORE = "\\_";
    private static final String LEFT_CURLY = "\\{";
    private static final String RIGHT_CURLY = "\\}";
    private static final String LEFT_SQUARE = "$[$";
    private static final String RIGHT_SQUARE = "$]$";
    private static final String CIRCUNFLEX = "\\^{}";
    private static final String TILDE = "\\~{}";
    private static final String VERT_BAR = "$|$";
    private static final String BACKSLASH = "$\\backslash$";
    private static final String PI = "$\\pi$";
    private static final String NORTH = ".north -| ";
    private static final String SOUTH = ".south -| ";
    private static final String EAST = ".east |- ";
    private static final String WEST = ".west |- ";
    private static final String DOC =
        "% To use this figure in your LaTeX "
            + "document\n% import the package groove/resources/groove2tikz.sty\n";
}
