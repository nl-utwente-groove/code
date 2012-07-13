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

import static groove.io.HTMLConverter.SUB_TAG;
import static groove.io.HTMLConverter.SUPER_TAG;
import static groove.io.HTMLConverter.toHtml;
import static groove.view.aspect.AspectKind.DEFAULT;
import static groove.view.aspect.AspectKind.PRODUCT;
import groove.control.CtrlTransition;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.GraphInfo;
import groove.graph.GraphRole;
import groove.graph.Node;
import groove.gui.jgraph.AspectJEdge;
import groove.gui.jgraph.AspectJVertex;
import groove.gui.jgraph.CtrlJEdge;
import groove.gui.jgraph.CtrlJVertex;
import groove.gui.jgraph.GraphJCell;
import groove.gui.jgraph.GraphJEdge;
import groove.gui.jgraph.GraphJGraph;
import groove.gui.jgraph.GraphJModel;
import groove.gui.jgraph.GraphJVertex;
import groove.gui.jgraph.JAttr;
import groove.gui.jgraph.LTSJVertex;
import groove.gui.layout.JEdgeLayout;
import groove.gui.layout.JVertexLayout;
import groove.gui.layout.LayoutMap;
import groove.io.HTMLConverter;
import groove.io.Util;
import groove.trans.RuleLabel;
import groove.util.Duo;
import groove.view.aspect.AspectEdge;
import groove.view.aspect.AspectKind;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.GraphConstants;
import org.jgraph.util.Bezier;

/**
 * Class to perform the conversion from Groove graphs to Tikz format. 
 * @author Eduardo Zambon
 */
public final class GraphToTikz {

    // ------------------------------------------------------------------------
    // Object fields
    // ------------------------------------------------------------------------

    /** The jGraph to be output. */
    private final GraphJGraph jGraph;
    /** The underlying model for jGraph. */
    private final GraphJModel<Node,Edge> model;
    /** The underlying Groove graph connected to the jGraph. */
    private final Graph<Node,Edge> graph;
    /** The layout map of the graph. */
    private final LayoutMap<Node,Edge> layoutMap;
    /** The color map of the graph. */
    private final Map<GraphJVertex,Color> colorMap;
    /** The builder that holds the Tikz string. */
    private final StringBuilder result;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /**
     * The constructor is private. To perform the conversion just call the
     * static method {@link #convert(GraphJGraph)}.
     */
    @SuppressWarnings("unchecked")
    private GraphToTikz(GraphJGraph jGraph) {
        this.jGraph = jGraph;
        this.model = (GraphJModel<Node,Edge>) this.jGraph.getModel();
        this.graph = (Graph<Node,Edge>) this.model.getGraph();
        this.layoutMap = GraphInfo.getLayoutMap(this.graph);
        this.colorMap = this.createColorMap();
        this.result = new StringBuilder();
    }

    // ------------------------------------------------------------------------
    // Static methods
    // ------------------------------------------------------------------------

    /** Writes a graph in LaTeX <code>Tikz</code> format to a print writer. */
    static public <N extends Node,E extends Edge> void export(
            GraphJGraph graph, PrintWriter writer) {
        writer.print(GraphToTikz.convert(graph));
    }

    /**
     * Converts a graph to a Tikz representation.
     * @param jGraph the graph to be converted.
     * @return a string with the Tikz encoding of the graph.
     */
    public static String convert(GraphJGraph jGraph) {
        return new GraphToTikz(jGraph).doConvert();
    }

    /**
     * Square brackets are tricky because they are sometimes interpreted in
     * different ways by LaTeX and Tikz and sometimes they need to be escaped.
     * This method undoes the escaping on the given string. 
     */
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

    private static String encloseItalicStyle(String string) {
        return enclose(unescapeSquareBrack(string), ITALIC_STYLE, "}");
    }

    private static String encloseBoldStyle(String string) {
        return enclose(unescapeSquareBrack(string), BOLD_STYLE, "}");
    }

    // Methods to enclose a string with extra characters.
    // END

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
            case '+':
                result.append(PLUS);
                break;
            case '-':
                result.append(MINUS);
                break;
            case '\\':
                result.append(BACKSLASH);
                break;
            case Util.LC_PI:
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

    /**
     * Converts special HTML chars that show inside a node.
     * @param line the string to be converted.
     * @return the line with converted characters, if any.
     */
    private static StringBuilder convertInscriptedHtml(StringBuilder line) {
        replaceInline(line, toHtml(Util.LC_PI), PI);
        replaceInline(line, toHtml(GT), GT);
        replaceInline(line, toHtml(FORWARDSLASH), FORWARDSLASH);
        replaceInline(line, toHtml(LT), LT);
        return line;
    }

    /**
     * Replaces all occurrences of the HTML tag in the given line.
     * @param line the line to be operated on.
     * @param html the tag to be replaced.
     * @param tikz the string to replace the tag with.
     */
    private static void replaceInline(StringBuilder line, String html,
            String tikz) {
        int i = line.indexOf(html);
        while (i != -1) {
            line.replace(i, i + html.length(), tikz);
            i = line.indexOf(html);
        }
    }

    /**
     * Removes all occurrences of the given tag on the given line.
     * @param line the line to be processed.
     * @param tag the tag to be removed.
     * @return the original line that was passed to the method.
     */
    private static String removeAllTags(StringBuilder line,
            HTMLConverter.HTMLTag tag) {
        String origLine = line.toString();
        StringBuilder newLine = new StringBuilder(line);
        tag.off(newLine);
        while (!newLine.toString().equals(origLine)) {
            origLine = newLine.toString();
            tag.off(newLine);
        }
        return origLine;
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

    // BEGIN
    // Methods to handle special colors.

    private static String getColorName(GraphJVertex vertex) {
        return "n" + vertex.getNumber() + COLOR_SUFFIX;
    }

    private static String getColorStyle(GraphJVertex vertex) {
        return getColorName(vertex) + COLOR_STYLE_SUFFIX;
    }

    private static String getRGBString(Color color) {
        return encloseCurly(color.getRed() + "," + color.getGreen() + ","
            + color.getBlue());
    }

    private static String getColorDefStr(GraphJVertex vertex, Color color) {
        return DEF_COLOR + encloseCurly(getColorName(vertex)) + RGB
            + getRGBString(color) + ENTER;
    }

    private static String getColorStyleDefStr(GraphJVertex vertex) {
        String c = getColorName(vertex);
        return getColorStyle(vertex) + STYLE_DEF
            + encloseCurly(DRAW + c + TEXT + c + FILL + c + FILL_SUFFIX) + ","
            + ENTER;
    }

    // Methods to handle special colors.
    // END

    private static boolean isNodifiedEdge(GraphJVertex node) {
        return node instanceof AspectJVertex && ((AspectJVertex) node).isEdge();
    }

    private static boolean hasNonEmptyLabel(GraphJEdge edge) {
        return !"".equals(edge.getText());
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    /**
     * Performs the entire conversion to Tikz and returns the resulting string.
     */
    private String doConvert() {
        this.appendTikzHeader();

        for (Node node : this.graph.nodeSet()) {
            GraphJVertex vertex = this.model.getJCellForNode(node);
            this.model.synchroniseLayout(vertex);
            JVertexLayout layout = null;
            if (this.layoutMap != null) {
                layout = this.layoutMap.getLayout(node);
            }
            this.appendTikzNode(vertex, layout,
                this.jGraph.getSelectionModel().isCellSelected(vertex));
        }

        Set<GraphJCell> consumedEdges = new HashSet<GraphJCell>();
        for (Edge edge : this.graph.edgeSet()) {
            JEdgeLayout layout = null;
            if (this.layoutMap != null) {
                layout = this.layoutMap.getLayout(edge);
            }
            GraphJCell jCell = this.model.getJCellForEdge(edge);
            if (!consumedEdges.contains(jCell)) {
                this.appendTikzEdge(jCell, layout,
                    this.jGraph.getSelectionModel().isCellSelected(jCell));
                consumedEdges.add(jCell);
            }
        }

        this.appendTikzFooter();

        return this.result.toString();
    }

    /** Returns true is the graph to be exported can have personalized colors. */
    private boolean hasExtraColors() {
        GraphRole role = this.graph.getRole();
        return (role == GraphRole.HOST || role == GraphRole.TYPE);
    }

    /** Creates a map for the personalized colors. */
    private Map<GraphJVertex,Color> createColorMap() {
        Map<GraphJVertex,Color> result = new HashMap<GraphJVertex,Color>();
        if (this.hasExtraColors()) {
            for (Node node : this.graph.nodeSet()) {
                AspectJVertex vertex =
                    (AspectJVertex) this.model.getJCellForNode(node);
                Color color = vertex.getColor();
                if (color != null) {
                    result.put(vertex, color);
                }
            }
        }
        return result;
    }

    /**
     * Checks if the given vertex is a key in the color map.
     * @return the name of the color style for the vertex if it is a key;
     *         null, otherwise.
     */
    private String getVertexColorStyle(GraphJVertex vertex) {
        if (this.colorMap.get(vertex) != null) {
            return getColorStyle(vertex);
        } else {
            return null;
        }
    }

    /**
     * Appends the header to the Tikz result string. The header includes
     * additional styles local to the figure.
     */
    private void appendTikzHeader() {
        this.result.append(DOC);

        // Special color definitions.
        this.result.append(COLORS);
        for (Entry<GraphJVertex,Color> entry : this.colorMap.entrySet()) {
            this.result.append(getColorDefStr(entry.getKey(), entry.getValue()));
        }

        this.result.append(BEGIN_TIKZ_FIG_OPEN);

        // Special color styles.
        this.result.append(COLOR_STYLES);
        for (GraphJVertex vertex : this.colorMap.keySet()) {
            this.result.append(getColorStyleDefStr(vertex));
        }

        this.result.append(BEGIN_TIKZ_FIG_CLOSE);
    }

    private void appendTikzFooter() {
        this.result.append(END_TIKZ_FIG + ENTER);
    }

    // -------------------------- Nodes ---------------------------------------

    /**
     * Converts a jGraph node to a Tikz string representation. 
     * @param node the node to be converted.
     * @param layout information regarding layout of the node. 
     * @param selected flag to indicate that the node should be drawn as
     *                 selected.
     */
    private void appendTikzNode(GraphJVertex node, JVertexLayout layout,
            boolean selected) {
        if (node.isVisible()) {
            this.result.append(BEGIN_NODE);

            // Styles.
            this.appendNodeStyles(node, selected);

            // Node ID.
            this.appendNode(node);

            // Node Coordinates.
            if (layout != null) {
                this.result.append(encloseSpace(AT_KEYWORD));
                Rectangle2D bounds = layout.getBounds();
                double x = bounds.getCenterX();
                double y = bounds.getCenterY();
                this.appendPoint(x, y, true);
            }

            // Node Labels.
            List<StringBuilder> lines = node.getLines();
            if (lines.isEmpty() || isNodifiedEdge(node)) {
                this.result.append(EMPTY_NODE_LAB);
            } else {
                this.result.append(BEGIN_NODE_LAB);
                for (StringBuilder line : lines) {
                    this.appendNodeInscription(line);
                }
                // Remove the last \\, if it exists
                if (this.result.lastIndexOf(CRLF) == this.result.length() - 2) {
                    this.result.deleteCharAt(this.result.length() - 1);
                    this.result.deleteCharAt(this.result.length() - 1);
                }
                this.result.append(END_NODE_LAB);
            }

            // Add small parameter node, if needed.
            boolean hasParameter =
                node instanceof AspectJVertex
                        ? ((AspectJVertex) node).getNode().hasParam() : false;
            if (hasParameter) {
                this.appendParameterNode((AspectJVertex) node);
            }
        }
    }

    private void appendParameterNode(AspectJVertex node) {
        String nodeId = node.getNode().toString();
        String nr = node.getNode().getParamNr() + "";
        // New node line.
        this.result.append(BEGIN_NODE + encloseBrack(PAR_NODE_STYLE));
        // Node name.
        this.result.append(encloseSpace(enclosePar(nodeId + PAR_NODE_SUFFIX)));
        // Node Coordinates.
        this.result.append(encloseSpace(AT_KEYWORD));
        this.result.append(enclosePar(nodeId + NORTH_WEST));
        // Parameter number.
        this.result.append(" " + encloseCurly(nr) + ";\n");
    }

    /**
     * Produces a string with the proper Tikz styles of a given node.
     * @param node the node to be converted.
     * @param selected flag to indicate that the node should be drawn as selected
     */
    private void appendNodeStyles(GraphJVertex node, boolean selected) {
        ArrayList<String> styles = new ArrayList<String>();
        boolean isControlNode = false;
        boolean isTypeGraphNode =
            node instanceof AspectJVertex
                    ? ((AspectJVertex) node).getNode().getGraphRole() == GraphRole.TYPE
                    : false;

        if (node instanceof CtrlJVertex) {
            // Node from control automaton.
            this.getControlNodeStyles((CtrlJVertex) node, styles);
            isControlNode = true;
        }

        AspectKind nodeKind =
            node instanceof AspectJVertex
                    ? ((AspectJVertex) node).getNode().getKind() : DEFAULT;
        switch (nodeKind) {
        case ERASER: // Eraser node
            styles.add(ERASER_NODE_STYLE);
            break;
        case CREATOR: // Creator node
            styles.add(CREATOR_NODE_STYLE);
            break;
        case EMBARGO: // Embargo node
            styles.add(EMBARGO_NODE_STYLE);
            break;
        case REMARK: // Remark node
            styles.add(REMARK_NODE_STYLE);
            break;
        case ABSTRACT: // Abstract type node
            styles.add(ABS_NODE_STYLE);
            break;
        case EXISTS:
        case EXISTS_OPT:
        case FORALL:
        case FORALL_POS:
            styles.add(QUANTIFIER_NODE_STYLE);
            break;
        default:
            if (isNodifiedEdge(node)) {
                styles.add(NODIFIED_EDGE_STYLE);
            } else {
                if (node.isGrayedOut()) {
                    styles.add(THIN_NODE_STYLE);
                } else if (!isControlNode) {
                    styles.add(BASIC_NODE_STYLE);
                }
            }
        }

        if (node instanceof LTSJVertex) {
            // Node from LTS.
            this.getLTSNodeStyles((LTSJVertex) node, styles);
        }

        AspectKind attrKind =
            node instanceof AspectJVertex
                    ? ((AspectJVertex) node).getNode().getAttrKind() : DEFAULT;
        if (attrKind.hasSignature()) {
            styles.add(ATTRIBUTE_NODE_STYLE);
        } else if (attrKind == PRODUCT) {
            styles.add(PRODUCT_NODE_STYLE);
        }

        if (selected) {
            if (nodeKind == AspectKind.CREATOR
                || nodeKind == AspectKind.EMBARGO) {
                styles.add(ULTRA_BOLD_LINE);
            } else {
                styles.add(BOLD_LINE);
            }
        }

        if (isTypeGraphNode) {
            styles.add(TYPE_NODE_STYLE);
        }

        String colorStyle = this.getVertexColorStyle(node);
        if (colorStyle != null) {
            styles.add(colorStyle);
        }

        this.result.append(styles.toString());
    }

    /**
     * Produces a string with the proper Tikz styles of a given control node.
     * @param node the control node to be converted.
     */
    private void getControlNodeStyles(CtrlJVertex node, ArrayList<String> styles) {
        if (node.isStart()) {
            styles.add(CONTROL_START_NODE_STYLE);
        } else if (node.isFinal()) {
            styles.add(CONTROL_SUCCESS_NODE_STYLE);
        } else {
            styles.add(CONTROL_NODE_STYLE);
        }
    }

    /**
     * Produces a string with the proper Tikz styles of a given LTS node.
     * @param node the LTS node to be converted.
     */
    private void getLTSNodeStyles(LTSJVertex node, ArrayList<String> styles) {
        if (node.isResult()) {
            styles.add(RESULT_NODE_STYLE);
        } else if (node.isStart()) {
            styles.add(START_NODE_STYLE);
        } else if (node.isFinal()) {
            styles.add(FINAL_NODE_STYLE);
        } else if (!node.isClosed()) {
            styles.add(OPEN_NODE_STYLE);
        }
    }

    /** Appends the node name to the result string. */
    private void appendNode(GraphJVertex node) {
        this.result.append(encloseSpace(enclosePar(node.getNode().toString())));
    }

    /**
     * Checks whether the given point is in a proper position with respect to
     * the given node and appends the node to the string builder, together
     * with a node anchor that keeps the edge horizontal or vertical.
     */
    private void appendNode(GraphJVertex node, Point2D point) {
        int side = this.getSide(node, point);
        boolean isProductNode =
            node instanceof AspectJVertex
                    ? ((AspectJVertex) node).getNode().getAttrKind() == PRODUCT
                    : false;
        if (side == 0 || isProductNode) {
            // The point is not aligned with the node, just use normal routing.
            this.appendNode(node);
        } else {
            String coord = getCoordString(side);
            String nodeName = node.getNode().toString();
            this.result.append(enclosePar(nodeName + coord
                + this.getPointString(point, false)));
        }
    }

    /** Appends the point in position i of a list of points. */
    private void appendPoint(List<Point2D> points, int i) {
        this.appendPoint(points.get(i));
    }

    /** Appends the given point. */
    private void appendPoint(Point2D point) {
        double x = point.getX();
        double y = point.getY();
        this.appendPoint(x, y, true, this.result);
    }

    private void appendPoint(double x, double y, boolean usePar) {
        this.appendPoint(x, y, usePar, this.result);
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
        Formatter f = new Formatter();
        if (usePar) {
            format = enclosePar(format);
        }
        s.append(f.format(Locale.US, format, adjX, adjY).toString());
        f.close();
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
     * Scans the HTML string of a node and converts the tags to Tikz.
     * @param htmlLine the HTML string to be converted.
     */
    private void appendNodeInscription(StringBuilder htmlLine) {
        int color = HTMLConverter.removeColorTags(htmlLine);
        StringBuilder line =
            convertInscriptedHtml(escapeSpecialChars(htmlLine));
        int font = HTMLConverter.removeFontTags(line);
        String aux = "";
        int i = line.indexOf(toHtml(Util.EXISTS));
        if (i >= 0) {
            this.result.append(line.substring(0, i));
            appendQuantifierNodeInscription(true, line);
        } else if ((i = line.indexOf(toHtml(Util.FORALL))) >= 0) {
            this.result.append(line.substring(0, i));
            appendQuantifierNodeInscription(false, line);
        } else {
            aux = line.toString();
        }

        switch (font) {
        case 0:
            // nothing to do.
            break;
        case 1:
            aux = encloseBoldStyle(aux);
            break;
        case 2:
            aux = encloseItalicStyle(aux);
            break;
        case 3:
            aux = encloseBoldStyle(encloseItalicStyle(aux));
            break;
        }

        switch (color) {
        case 0:
            this.result.append(aux);
            break;
        case 1:
            this.result.append(enclose(aux, BEGIN_COLOR_BLUE, "}"));
            break;
        case 2:
            this.result.append(enclose(aux, BEGIN_COLOR_GREEN, "}"));
            break;
        case 3:
            this.result.append(enclose(aux, BEGIN_COLOR_RED, "}"));
            break;
        case 4:
            this.result.append(enclose(aux, BEGIN_COLOR_ORANGE, "}"));
            break;
        }

        this.result.append(CRLF);
    }

    /**
     * Scans the HTML string of a quantified node label and converts the tags to Tikz.
     * @param exists if {@code true}, the quantifier is existential
     * @param line the HTML string to be converted.
     */
    private void appendQuantifierNodeInscription(boolean exists,
            StringBuilder line) {
        // open math environment
        this.result.append('$');
        // find out if this is a ...x quantifier
        boolean special = line.indexOf(SUPER_TAG.tagBegin) >= 0;
        if (exists) {
            this.result.append(special ? EXISTSX_STR : EXISTS_STR);
        } else {
            this.result.append(special ? FORALLX_STR : FORALL_STR);
        }
        // append subscript, if there is one
        int subIx = line.indexOf(SUB_TAG.tagBegin);
        if (subIx >= 0) {
            line = line.replace(0, subIx, "");
            String sub = removeAllTags(line, SUB_TAG);
            this.result.append("_\\mathsf{" + sub + "}");
        }
        // close math environment
        this.result.append('$');
    }

    /**
     * Checks on which side of a node a point lies.
     * @param vertex the node to be checked.
     * @param point the point to be checked.
     * @return 1 if the point lies east, 2 if it lies north, 3 if it lies west,
     *         4 if it lies south, and 0 if its outside a proper position.
     */
    private int getSide(GraphJVertex vertex, Point2D point) {
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
     * @param selected flag to indicate that the edge should be drawn as
     *                 selected.
     */
    private void appendTikzEdge(GraphJCell cell, JEdgeLayout layout,
            boolean selected) {
        if (cell instanceof GraphJEdge) {
            GraphJEdge graphCell = (GraphJEdge) cell;
            this.appendTikzEdge(graphCell, layout, selected);
        }
    }

    /**
     * Converts a jGraph edge to a Tikz string representation. 
     * @param edge the edge to be converted.
     * @param layout information regarding layout of the edge.
     * @param selected flag to indicate that the edge should be drawn as selected
     */
    private void appendTikzEdge(GraphJEdge edge, JEdgeLayout layout,
            boolean selected) {
        if (edge.isVisible()) {
            Duo<String> styles = this.getEdgeStyles(edge, selected);
            String edgeStyle = styles.one();
            String labStyle = styles.two();

            this.result.append(BEGIN_EDGE);
            this.result.append(encloseBrack(edgeStyle));

            if (layout != null) {
                switch (layout.getLineStyle()) {
                case GraphConstants.STYLE_ORTHOGONAL:
                    this.appendOrthogonalLayout(edge, layout, labStyle);
                    break;
                case GraphConstants.STYLE_BEZIER:
                    this.appendBezierLayout(edge, layout, labStyle);
                    break;
                case GraphConstants.STYLE_SPLINE:
                    this.appendSplineLayout(edge, layout, labStyle);
                    break;
                case JAttr.STYLE_MANHATTAN:
                    this.appendManhattanLayout(edge, layout, labStyle);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown line style!");
                }
            } else {
                this.appendDefaultLayout(edge, labStyle);
            }
        }
    }

    /**
     * Find the proper Tikz styles for a given edge.
     * @param edge the edge to be analysed.
     * @param selected flag to indicate that the edge should be drawn as selected
     */
    private Duo<String> getEdgeStyles(GraphJEdge edge, boolean selected) {
        Duo<String> styles = new Duo<String>("", "");

        if (edge instanceof CtrlJEdge) {
            this.getControlEdgeStyles((CtrlJEdge) edge, styles);
        }

        AspectKind edgeKind =
            edge instanceof AspectJEdge
                    ? ((AspectJEdge) edge).getEdge().getKind() : DEFAULT;
        switch (edgeKind) {
        case ERASER:
            styles.setOne(ERASER_EDGE_STYLE);
            styles.setTwo(ERASER_LABEL_STYLE);
            break;
        case CREATOR:
            styles.setOne(CREATOR_EDGE_STYLE);
            styles.setTwo(CREATOR_LABEL_STYLE);
            break;
        case EMBARGO:
        case CONNECT:
            styles.setOne(EMBARGO_EDGE_STYLE);
            styles.setTwo(EMBARGO_LABEL_STYLE);
            break;
        case REMARK:
            styles.setOne(REMARK_EDGE_STYLE);
            styles.setTwo(REMARK_LABEL_STYLE);
            break;
        case SUBTYPE:
            styles.setOne(INHERITANCE_EDGE_STYLE);
            styles.setTwo(INHERITANCE_LABEL_STYLE);
            break;
        case ABSTRACT:
            styles.setOne(ABS_EDGE_STYLE);
            styles.setTwo(ABS_LABEL_STYLE);
            break;
        case NESTED:
            styles.setOne(QUANTIFIER_EDGE_STYLE);
            styles.setTwo(BASIC_LABEL_STYLE);
            break;
        default:
            styles.setOne(BASIC_EDGE_STYLE);
            styles.setTwo(BASIC_LABEL_STYLE);
        }

        if (edge.isGrayedOut()) {
            styles.setOne(THIN_EDGE_STYLE);
            styles.setTwo(THIN_LABEL_STYLE);
        }

        if (selected) {
            if (edgeKind == AspectKind.CREATOR
                || edgeKind == AspectKind.EMBARGO) {
                styles.setOne(styles.one() + ", " + ULTRA_BOLD_LINE);
            } else {
                styles.setOne(styles.one() + ", " + BOLD_LINE);
            }
        }

        // Check if we should draw the end arrow of the edge.
        AttributeMap attrMap = edge.getAttributes();
        if (GraphConstants.getLineEnd(attrMap) == GraphConstants.ARROW_NONE) {
            styles.setOne(styles.one() + ", " + UNDIRECTED_EDGE_STYLE);
        } else if (edge.isBidirectional()) {
            styles.setOne(styles.one() + ", " + BIDIRECTIONAL_EDGE_STYLE);
        }

        // Check if the edge has a special color.
        String colorStyle = this.getVertexColorStyle(edge.getSourceVertex());
        if (colorStyle != null) {
            styles.setOne(styles.one() + ", " + colorStyle);
        }

        return styles;
    }

    /**
     * Find the proper Tikz styles for a given control edge.
     * @param edge the control edge to be analysed.
     */
    private void getControlEdgeStyles(CtrlJEdge edge, Duo<String> styles) {
        CtrlTransition t = edge.getEdge();
        if (!t.label().getGuard().isEmpty()) {
            styles.setOne(CONTROL_FAILURE_EDGE_STYLE);
        } else {
            styles.setOne(CONTROL_EDGE_STYLE);
        }
        styles.setTwo(CONTROL_LABEL_STYLE);
    }

    /**
     * Creates an edge with a default layout. The edge is drawn as a straight
     * line from source to target node and the label is placed half-way.
     * @param edge the edge to be converted.
     * @param labStyle a string describing the style to be used in the label.
     */
    private void appendDefaultLayout(GraphJEdge edge, String labStyle) {
        GraphJVertex srcVertex = edge.getSourceVertex();
        GraphJVertex tgtVertex = edge.getTargetVertex();
        this.appendSourceNode(srcVertex, tgtVertex);
        this.result.append(encloseSpace(DOUBLE_DASH));
        this.appendEdgeLabelInPath(edge, labStyle);
        this.appendTargetNode(srcVertex, tgtVertex);
        this.result.append(END_EDGE);
    }

    /**
     * Creates an edge with orthogonal lines. Only the intermediate points of
     * the layout information are used, the first and last points are discarded
     * and replaced by Tikz node names and we let Tikz find the anchors.
     * @param edge the edge to be converted.
     * @param layout information regarding layout of the edge.
     * @param labStyle a string describing the style to be used in the label.
     * @param connection the string with the type of Tikz connection to be used.
     */
    private void appendOrthogonalLayout(GraphJEdge edge, JEdgeLayout layout,
            String labStyle, String connection) {

        GraphJVertex srcVertex = edge.getSourceVertex();
        GraphJVertex tgtVertex = edge.getTargetVertex();
        List<Point2D> points = layout.getPoints();

        if (points.size() == 2) {
            this.appendSourceNode(srcVertex, tgtVertex);
            this.result.append(encloseSpace(connection));
            this.appendTargetNode(srcVertex, tgtVertex);
            this.result.append(END_PATH);
            this.appendEdgeLabel(edge, layout, labStyle, points);
            this.result.append(END_EDGE);
            return;
        }

        int firstPoint = 1;
        int lastPoint = points.size() - 2;

        this.appendNode(srcVertex, points.get(firstPoint));
        this.result.append(encloseSpace(connection));
        // Intermediate points
        for (int i = firstPoint; i <= lastPoint; i++) {
            this.appendPoint(points, i);
            // When using the MANHATTAN style sometimes we cannot use the ANGLE
            // routing when going from the last point to the node because the
            // arrow will be in the wrong direction.
            // We test this condition here.
            if (i == lastPoint && connection.equals(ANGLE)
                && this.isHorizontalOrVertical(points, i, tgtVertex)) {
                // We are in this special case, use straight routing.
                this.result.append(encloseSpace(DOUBLE_DASH));
            } else {
                // A normal case, just use the provided connection string.
                this.result.append(encloseSpace(connection));
            }
        }
        this.appendNode(tgtVertex, points.get(lastPoint));
        this.result.append(END_PATH);
        this.appendEdgeLabel(edge, layout, labStyle, points);
        this.result.append(END_EDGE);
    }

    /**
     * Creates an edge with orthogonal lines. Only the intermediate points of
     * the layout information are used, the first and last points are discarded
     * and replaced by Tikz node names and we let Tikz find the anchors.
     * @param edge the edge to be converted.
     * @param layout information regarding layout of the edge.
     * @param labStyle a string describing the style to be used in the label.
     */
    private void appendOrthogonalLayout(GraphJEdge edge, JEdgeLayout layout,
            String labStyle) {
        this.appendOrthogonalLayout(edge, layout, labStyle, DOUBLE_DASH);
    }

    /**
     * Creates an edge with bezier lines. Only the intermediate points of
     * the layout information are used, the first and last points are discarded
     * and replaced by Tikz node names and we let Tikz find the anchors.
     * Each point of the layout information is interspersed with control points
     * from the bezier lines.
     * @param edge the edge to be converted.
     * @param layout information regarding layout of the edge.
     * @param labStyle a string describing the style to be used in the label.
     */
    private void appendBezierLayout(GraphJEdge edge, JEdgeLayout layout,
            String labStyle) {
        GraphJVertex srcVertex = edge.getSourceVertex();
        GraphJVertex tgtVertex = edge.getTargetVertex();
        List<Point2D> points = layout.getPoints();

        // Compute the bezier line.
        Bezier bezier = new Bezier(points.toArray(new Point2D[points.size()]));
        Point2D[] bPoints = bezier.getPoints();

        if (bPoints == null) {
            // The edge is with a bezier style but it does not have any bezier
            // points, just use standard layout.
            this.appendDefaultLayout(edge, labStyle);
            return;
        }

        if (points.size() <= 4) {
            // If we have 4 or less points in the edge, we need to resort to
            // some black magic code when making the translation to Tikz.
            // This is needed to make the Tikz figure look similar to what
            // is shown in Groove. Otherwise, the bezier curve in Tikz is not
            // smooth enough.
            boolean isLoop = srcVertex.getNode().equals(tgtVertex.getNode());
            this.appendNode(srcVertex);
            int i = 1; // Index for edge points.
            int j = 0; // Index for bezier points. Always j = i - 1;
            while (j < bPoints.length - 1) {
                this.result.append(BEGIN_CONTROLS);
                if (isLoop) {
                    // Drawing a loop edge is a special case, for the first and
                    // last control entry we need to use a point of the edge
                    // instead of a bezier point, otherwise Tikz draws the loop
                    // incorrectly.
                    if (i == points.size() - 1) {
                        // This is the LAST control entry.
                        this.appendPoint(points, i - 1);
                    } else {
                        // Not a special case, just use a bezier point.
                        this.appendPoint(bPoints[j]);
                    }
                    this.result.append(AND);
                    if (i == 1) {
                        // This is the FIRST control entry.
                        this.appendPoint(points, i);
                    } else {
                        // Not a special case, just use a bezier point.
                        this.appendPoint(bPoints[j + 1]);
                    }
                } else {
                    // The edge is not a loop, just use the bezier points.
                    this.appendPoint(bPoints[j]);
                    this.result.append(AND);
                    this.appendPoint(bPoints[j + 1]);
                }
                this.result.append(END_CONTROLS);
                // Use the edge intermediate point as the next coordinate.
                if (points.size() > 3 && i < points.size() - 1) {
                    this.appendPoint(points, i);
                }
                i++;
                j++;
            }
            this.appendNode(tgtVertex);
        } else {
            // General case, we have an edge with more than 4 points. We have
            // enough points to make the curve smooth, so just revert to
            // normal bezier calculation.

            // The first part of the curve is quadratic.
            this.appendNode(srcVertex);
            this.result.append(BEGIN_CONTROLS);
            this.appendPoint(bPoints[0]);
            this.result.append(END_CONTROLS);
            this.appendPoint(points, 1);

            // The middle part of the curve is cubic.
            for (int i = 2; i < points.size() - 1; i++) {
                this.result.append(BEGIN_CONTROLS);
                this.appendPoint(bPoints[2 * i - 3]);
                this.result.append(AND);
                this.appendPoint(bPoints[2 * i - 2]);
                this.result.append(END_CONTROLS);
                this.appendPoint(points, i);
            }

            // The last part of the curve is again quadratic.
            this.result.append(BEGIN_CONTROLS);
            this.appendPoint(bPoints[bPoints.length - 1]);
            this.result.append(END_CONTROLS);
            this.appendNode(tgtVertex);
        }

        this.result.append(END_PATH);
        this.appendEdgeLabel(edge, layout, labStyle, points);
        this.result.append(END_EDGE);
    }

    /**
     * This is not implemented yet. The Bezier style is used instead.
     * @param edge the edge to be converted.
     * @param layout information regarding layout of the edge.
     * @param labStyle a string describing the style to be used in the label.
     */
    private void appendSplineLayout(GraphJEdge edge, JEdgeLayout layout,
            String labStyle) {
        System.err.println("Sorry, the SPLINE line style is not yet "
            + "supported, using BEZIER style...");
        this.appendBezierLayout(edge, layout, labStyle);
    }

    /**
     * Creates an edge with Manhattan lines. Only the intermediate points of
     * the layout information are used, the first and last points are discarded
     * and replaced by Tikz node names and we let Tikz find the anchors.
     * @param edge the edge to be converted.
     * @param layout information regarding layout of the edge.
     * @param labStyle a string describing the style to be used in the label.
     */
    private void appendManhattanLayout(GraphJEdge edge, JEdgeLayout layout,
            String labStyle) {
        this.appendOrthogonalLayout(edge, layout, labStyle, ANGLE);
    }

    /**
     * Checks whether the given target node is in a proper position with
     * respect to the given source node and appends the source node to the
     * string builder, together with a node anchor that keeps the edge
     * horizontal or vertical.
     */
    private void appendSourceNode(GraphJVertex srcNode, GraphJVertex tgtNode) {
        if (this.layoutMap != null) {
            JVertexLayout tgtLayout =
                this.layoutMap.getLayout(tgtNode.getNode());
            if (tgtLayout != null) {
                Rectangle2D tgtBounds = tgtLayout.getBounds();
                Point2D tgtCenter =
                    new Point2D.Double(tgtBounds.getCenterX(),
                        tgtBounds.getCenterY());
                this.appendNode(srcNode, tgtCenter);
            }
        } else {
            this.appendNode(srcNode);
        }
    }

    /**
     * Checks whether the given source node is in a proper position with
     * respect to the given target node and appends the target node to the
     * string builder, together with a node anchor that keeps the edge
     * horizontal or vertical.
     */
    private void appendTargetNode(GraphJVertex srcNode, GraphJVertex tgtNode) {
        if (this.layoutMap != null) {
            JVertexLayout srcLayout =
                this.layoutMap.getLayout(srcNode.getNode());
            JVertexLayout tgtLayout =
                this.layoutMap.getLayout(tgtNode.getNode());
            if (srcLayout != null && tgtLayout != null) {
                Rectangle2D tgtBounds = tgtLayout.getBounds();
                Point2D tgtCenter =
                    new Point2D.Double(tgtBounds.getCenterX(),
                        tgtBounds.getCenterY());
                int side = this.getSide(srcNode, tgtCenter);
                if (side == 0) {
                    Rectangle2D srcBounds = srcLayout.getBounds();
                    Point2D srcCenter =
                        new Point2D.Double(srcBounds.getCenterX(),
                            srcBounds.getCenterY());
                    this.appendNode(tgtNode, srcCenter);
                } else {
                    this.appendNode(tgtNode);
                }
            }
        } else {
            this.appendNode(tgtNode);
        }
    }

    private void appendEdgeLabel(GraphJEdge edge) {
        Edge e = edge.getEdge();
        if (e instanceof AspectEdge) {
            RuleLabel ruleLabel = ((AspectEdge) e).getRuleLabel();
            if (ruleLabel != null && !ruleLabel.isAtom()
                && !ruleLabel.isSharp()) {
                // We have a regular expression on the label, make it italic.
                this.result.append(encloseCurly(encloseItalicStyle(escapeSpecialChars(edge.getText()))));
            } else {
                // This is a normal AspectEdge.
                this.result.append(encloseCurly(escapeSpecialChars(edge.getText())));
            }
        } else {
            this.result.append(encloseCurly(escapeSpecialChars(edge.getText())));
        }
    }

    /**
     * Creates an extra path to place the edge label which has special
     * placement requirements.
     */
    private void appendEdgeLabel(GraphJEdge edge, JEdgeLayout layout,
            String labStyle, List<Point2D> points) {
        if (!labStyle.equals(INHERITANCE_LABEL_STYLE) && hasNonEmptyLabel(edge)) {
            Point2D labelPos =
                convertRelativeLabelPositionToAbsolute(
                    layout.getLabelPosition(), points);
            // Extra path for the label position.
            this.result.append(BEGIN_NODE);
            this.result.append(encloseBrack(labStyle));
            this.result.append(encloseSpace(AT_KEYWORD));
            this.appendPoint(labelPos);
            this.appendEdgeLabel(edge);
        }
    }

    /** Appends the edge label along the path that is being drawn. */
    private void appendEdgeLabelInPath(GraphJEdge edge, String labStyle) {
        if (!labStyle.equals(INHERITANCE_LABEL_STYLE) && hasNonEmptyLabel(edge)) {
            this.result.append(NODE);
            this.result.append(encloseBrack(labStyle));
            this.appendEdgeLabel(edge);
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
    private boolean isHorizontalOrVertical(List<Point2D> points, int index,
            GraphJVertex tgtVertex) {
        boolean result = false;
        if (this.layoutMap != null) {
            JVertexLayout layout =
                this.layoutMap.getLayout(tgtVertex.getNode());
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

    // ------------------------------------------------------------------------
    // Tikz output strings
    // ------------------------------------------------------------------------

    private static final String ENTER = "\n";
    private static final String CRLF = "\\\\";
    private static final String BEGIN_TIKZ_FIG_OPEN = "\\begin{tikzpicture}["
        + ENTER;
    private static final String BEGIN_TIKZ_FIG_CLOSE = "scale=\\tikzscale]"
        + ENTER;
    private static final String END_TIKZ_FIG = "\\userdefinedmacro" + ENTER
        + "\\end{tikzpicture}" + ENTER
        + "\\renewcommand{\\userdefinedmacro}{\\relax}";
    private static final String BEGIN_NODE = "\\node";
    private static final String AT_KEYWORD = "at";
    private static final String BEGIN_NODE_LAB = " {\\ml{";
    private static final String END_NODE_LAB = "}};" + ENTER;
    private static final String EMPTY_NODE_LAB = "{};" + ENTER;
    private static final String EXISTS_STR = "\\exists";
    private static final String EXISTSX_STR = "\\exists^{?}";
    private static final String FORALL_STR = "\\forall";
    private static final String FORALLX_STR = "\\forall^{>0}";
    private static final String ITALIC_STYLE = "\\textit{";
    private static final String BOLD_STYLE = "\\textbf{";
    private static final String BEGIN_EDGE = "\\path";
    private static final String END_PATH = ";" + ENTER;
    private static final String END_EDGE = END_PATH;
    private static final String NODE = "node";
    private static final String BEGIN_COLOR_BLUE = "{\\color{\\blue}";
    private static final String BEGIN_COLOR_GREEN = "{\\color{\\green}";
    private static final String BEGIN_COLOR_RED = "{\\color{\\red}";
    private static final String BEGIN_COLOR_ORANGE = "{\\color{\\orange}";
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
    private static final String REMARK_NODE_STYLE = "remnode";
    private static final String REMARK_EDGE_STYLE = "remedge";
    private static final String REMARK_LABEL_STYLE = "remlab";
    private static final String THIN_NODE_STYLE = "thinnode";
    private static final String THIN_EDGE_STYLE = "thinedge";
    private static final String THIN_LABEL_STYLE = "thinlab";
    private static final String NODIFIED_EDGE_STYLE = "nodified";
    private static final String TYPE_NODE_STYLE = "type";
    private static final String BIDIRECTIONAL_EDGE_STYLE = "bidir";
    private static final String ABS_NODE_STYLE = "absnode";
    private static final String ABS_EDGE_STYLE = "absedge";
    private static final String ABS_LABEL_STYLE = "abslab";
    private static final String ATTRIBUTE_NODE_STYLE = "attr";
    private static final String PRODUCT_NODE_STYLE = "prod";
    private static final String QUANTIFIER_NODE_STYLE = "quantnode";
    private static final String QUANTIFIER_EDGE_STYLE = "quantedge";
    private static final String PAR_NODE_STYLE = "parnode";
    private static final String CONTROL_NODE_STYLE = "cnode";
    private static final String CONTROL_START_NODE_STYLE = "cstart";
    private static final String CONTROL_SUCCESS_NODE_STYLE = "csuccess";
    private static final String CONTROL_EDGE_STYLE = "cedge";
    private static final String CONTROL_FAILURE_EDGE_STYLE = "cfailure";
    private static final String UNDIRECTED_EDGE_STYLE = "-";
    private static final String INHERITANCE_EDGE_STYLE = "subedge";
    private static final String INHERITANCE_LABEL_STYLE = "none";
    private static final String CONTROL_LABEL_STYLE = "clab";
    private static final String RESULT_NODE_STYLE = "result";
    private static final String FINAL_NODE_STYLE = "final";
    private static final String START_NODE_STYLE = "start";
    private static final String OPEN_NODE_STYLE = "open";
    private static final String BOLD_LINE = "bold";
    private static final String ULTRA_BOLD_LINE = "ultrabold";
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
    private static final String PLUS = "$+$";
    private static final String MINUS = "$-$";
    private static final String TILDE = "\\~{}";
    private static final String VERT_BAR = "$|$";
    private static final String BACKSLASH = "$\\backslash$";
    private static final String PI = "$\\pi$";
    private static final String GT = ">";
    private static final String LT = "<";
    private static final String FORWARDSLASH = "/";
    private static final String NORTH = ".north -| ";
    private static final String SOUTH = ".south -| ";
    private static final String EAST = ".east |- ";
    private static final String WEST = ".west |- ";
    private static final String NORTH_WEST = ".north west";
    private static final String PAR_NODE_SUFFIX = "p";
    private static final String COLOR_SUFFIX = "c";
    private static final String COLOR_STYLE_SUFFIX = "s";
    private static final String FILL_SUFFIX = "!10";
    private static final String STYLE_DEF = "/.style=";
    private static final String DRAW = "draw=";
    private static final String FILL = ",fill=";
    private static final String TEXT = ",text=";
    private static final String DEF_COLOR = "\\definecolor";
    private static final String RGB = "{RGB}";
    private static final String DOC = "% To use this figure in your LaTeX "
        + "document" + ENTER
        + "% import the package groove/resources/groove2tikz.sty" + ENTER + "%"
        + ENTER;
    private static final String COLORS = "% Special colors" + ENTER;
    private static final String COLOR_STYLES = "% Special color styles" + ENTER;
}
