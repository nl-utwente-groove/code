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

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import org.jgraph.graph.EdgeView;
import org.jgraph.graph.GraphConstants;

import groove.graph.GraphInfo;
import groove.gui.layout.JEdgeLayout;
import groove.gui.layout.JVertexLayout;
import groove.gui.layout.LayoutMap;
import groove.io.FileType;
import groove.io.HTMLConverter;
import groove.util.line.LineStyle;
import groove.util.parse.FormatError;
import groove.util.parse.FormatErrorSet;
import groove.util.parse.FormatException;
import groove.util.parse.StringHandler;

/**
 * Class wrapping the functionality of reading and writing layout information in
 * the required format.
 * @author Arend Rensink
 * @version $Revision$
 */
public class LayoutIO {
    /**
     * Constructs a reader/writer for layout information.
     */
    private LayoutIO() {
        // empty
    }

    /**
     * Loads layout information into a given graph from a given input stream.
     * Any errors in the layout information are added to the graph errors.
     * @param graph graph to load the layout into
     * @param in input stream containing the layout information
     * @throws IOException if an error occurred in reading the layout file
     */
    public void loadLayout(AttrGraph graph, InputStream in) throws IOException {
        LayoutMap result = new LayoutMap();
        FormatErrorSet errors = new FormatErrorSet();
        try (BufferedReader layoutReader = new BufferedReader(new InputStreamReader(in))) {
            int version = 1;
            // read in from the layout file until done
            for (String nextLine = layoutReader.readLine(); nextLine != null; nextLine =
                layoutReader.readLine()) {
                String[] parts;
                try {
                    parts = StringHandler.splitExpr(nextLine, WHITESPACE);
                    if (parts.length > 0) {
                        String command = parts[0];
                        if (command.equals(NODE_PREFIX)) {
                            putVertexLayout(result, parts, graph);
                        } else if (command.equals(EDGE_PREFIX)) {
                            putEdgeLayout(result, parts, graph, version);
                        } else if (command.equals(VERSION_PREFIX)) {
                            try {
                                version = Integer.parseInt(parts[1]);
                            } catch (NumberFormatException exc) {
                                throw new FormatException("Format error in version number %s",
                                    parts[1]);
                            }
                        }
                    }
                } catch (FormatException exc) {
                    for (FormatError error : exc.getErrors()) {
                        errors.add(LAYOUT_FORMAT_ERROR + ": %s", error);
                    }
                }
            }
        }
        GraphInfo.addErrors(graph, errors);
        GraphInfo.setLayoutMap(graph, result);
    }

    /**
     * Inserts vertex layout information in a given layout map, based on a
     * string array description and node map.
     */
    private void putVertexLayout(LayoutMap layoutMap, String[] parts, AttrGraph graph)
        throws FormatException {
        AttrNode node = graph.getNode(parts[1]);
        if (node == null) {
            throw new FormatException("Unknown node " + parts[1]);
        }
        Rectangle bounds = toBounds(parts, 2);
        // bounds.setSize(JAttr.DEFAULT_NODE_SIZE);
        if (bounds == null) {
            throw new FormatException("Bounds for " + parts[1] + " cannot be parsed");
        }
        layoutMap.putNode(node, new JVertexLayout(bounds));
    }

    /**
     * Inserts edge layout information in a given layout map, based on a string
     * array description and node map.
     * @param version for version 2, the layout position info has changed
     */
    private AttrEdge putEdgeLayout(LayoutMap layoutMap, String[] parts, AttrGraph graph,
        int version) throws FormatException {
        if (parts.length < 7) {
            throw new FormatException("Incomplete edge layout line");
        }
        AttrNode source = graph.getNode(parts[1]);
        if (source == null) {
            throw new FormatException("Unknown node " + parts[1]);
        }
        AttrNode target = graph.getNode(parts[2]);
        if (target == null) {
            throw new FormatException("Unknown node " + parts[2]);
        }
        String labelTextWithQuotes = parts[3];
        String labelText = StringHandler.toUnquoted(labelTextWithQuotes, DOUBLE_QUOTE);
        AttrEdge edge = graph.getEdge(source, labelText, target);
        if (edge == null) {
            throw new FormatException("Unknown edge %s --%s-> %s", source, labelText, target);
        }
        try {
            List<Point2D> points;
            int lineStyle;
            points = toPoints(parts, 6);
            // if we have fewer than 2 points, something is wrong
            if (points.size() <= 1) {
                throw new FormatException("Edge layout needs at least 2 points");
            }
            lineStyle = Integer.parseInt(parts[parts.length - 1]);
            if (!LineStyle.isStyle(lineStyle)) {
                lineStyle = LineStyle.DEFAULT_VALUE.getCode();
            }
            correctPoints(points, layoutMap.getLayout(source), layoutMap.getLayout(target));
            Point2D labelPosition =
                calculateLabelPosition(toPoint(parts, 4), points, version, source == target);
            layoutMap.putEdge(edge,
                new JEdgeLayout(points, labelPosition, LineStyle.getStyle(lineStyle)));
        } catch (NumberFormatException exc) {
            throw new FormatException(
                "Number format error " + HTMLConverter.toUppercase(exc.getMessage(), false));
        }
        return edge;
    }

    /** Checks if the source and target point lie within the source and target nodes,
     * and corrects the points if this is not the case.
     * Fix for SF Bug #3562111.
     */
    public static void correctPoints(List<Point2D> points, JVertexLayout sourceLayout,
        JVertexLayout targetLayout) {
        correctPoint(points, 0, sourceLayout);
        correctPoint(points, points.size() - 1, targetLayout);
    }

    private static void correctPoint(List<Point2D> points, int i, JVertexLayout layout) {
        if (layout != null) {
            Rectangle2D bounds = layout.getBounds();
            if (!bounds.contains(points.get(i))) {
                points.set(i, new Point2D.Double(bounds.getCenterX(), bounds.getCenterY()));
            }
        }
    }

    /**
     * Calculates the label position according to the version of the layout
     * file.
     */
    public static Point2D calculateLabelPosition(Point2D label, List<Point2D> points, int version,
        boolean isLoop) {
        Point2D result;
        if (version == VERSION1) {
            // the y is now an offset rather than a percentile
            if (points != null && points.size() > 0) {
                Point2D relativePos = version1RelativePos(label, points);
                result = version2LabelPos(relativePos, version2LabelVector(points, isLoop));
            } else {
                result = new Point2D.Double(label.getX(), 0);
            }
        } else {
            result = label;
        }
        return result;
    }

    /**
     * Calculates the relative position of a label from version 1 label position
     * info. The info is that both x and y of the label are given as permilles
     * of the vector.
     * @param label the version 1 label position info
     * @param points the list of points comprising the edge
     */
    private static Point2D version1RelativePos(Point2D label, List<Point2D> points) {
        // we're trying to reconstruct the label position from the JGraph 5.2
        // method,
        // but at this point we don't have the view available which means we
        // don't
        // have precisely the same information
        Rectangle2D tmp = version1PaintBounds(points);
        int unit = GraphConstants.PERMILLE;
        Point2D p0 = points.get(0);
        Point2D p1 = points.get(1);
        Point2D pe = points.get(points.size() - 1);
        // Position is direction-dependent
        double x0 = tmp.getX();
        int xdir = 1;
        // take right bound if end point is to the right, or equal and first
        // slope directed left
        if (p0.getX() > pe.getX() || (p0.getX() == pe.getX() && p1.getX() > p0.getX())) {
            x0 += tmp.getWidth();
            xdir = -1;
        }
        double y0 = tmp.getY();
        int ydir = 1;
        // take lower bound if end point is below, or equal and first slope
        // directed up
        if (p0.getY() > pe.getY() || (p0.getY() == pe.getY() && p1.getY() > p0.getY())) {
            y0 += tmp.getHeight();
            ydir = -1;
        }
        double x = x0 + xdir * (tmp.getWidth() * label.getX() / unit);
        double y = y0 + ydir * (tmp.getHeight() * label.getY() / unit);
        return new Point2D.Double(x - p0.getX(), y - p0.getY());
    }

    /**
     * Returns the bounds of a list of points. The bounds is the minimal
     * rectangle containing all points.
     */
    private static Rectangle2D version1PaintBounds(List<Point2D> points) {
        double minX = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double minY = Double.MAX_VALUE;
        double maxY = Double.MIN_VALUE;
        for (Point2D point : points) {
            minX = Math.min(minX, point.getX());
            maxX = Math.max(maxX, point.getX());
            minY = Math.min(minY, point.getY());
            maxY = Math.max(maxY, point.getY());
        }
        return new Rectangle2D.Double(minX, minY, maxX - minX, maxY - minY);
    }

    /**
     * Creates an edge vector from a list of points. The edge vector is the
     * vector from the first to the last point, if they are distinct; otherwise,
     * it is the average of the edge points; if that yields <code>(0,0)</code>,
     * the edge vector is given by {@link #DEFAULT_EDGE_VECTOR}.
     *
     * @param points the list of points; should not be empty
     * @param isLoop flag indicating that the underlying edge is a loop
     * @see EdgeView#getLabelVector()
     */
    private static Point2D version2LabelVector(List<Point2D> points, boolean isLoop) {
        Point2D result = null;
        // first try a vector from the first to the last point
        Point2D begin = points.get(0);
        Point2D end = points.get(points.size() - 1);
        if (!(isLoop || begin.equals(end))) {
            double dx = end.getX() - begin.getX();
            double dy = end.getY() - begin.getY();
            result = new Point2D.Double(dx, dy);
        } else if (points.size() > 0) {
            // the first and last point coincide; try taking the max of all
            // points
            double sumX = 0;
            double sumY = 0;
            for (Point2D point : points) {
                sumX += point.getX() - begin.getX();
                sumY += point.getY() - begin.getY();
            }
            // double the average (why? don't know; see
            // EdgeView#getLabelVector())
            int n = points.size() / 2;
            result = new Point2D.Double(sumX / n, sumY / n);
        }
        if (result == null || result.getX() == 0 && result.getY() == 0) {
            // nothing worked
            result = DEFAULT_EDGE_VECTOR;
        }
        return result;
    }

    /**
     * Calculates the version 2 label position values from the relative position
     * of the label.
     * @param pos the relative label position
     * @param edge the edge vector; should not be <code>(0,0)</code>
     */
    private static Point2D version2LabelPos(Point2D pos, Point2D edge) {
        // the square of the length of the edge vector
        double vector2 = edge.getX() * edge.getX() + edge.getY() * edge.getY();
        // the ratio of the label vector to the edge vector
        double ratio = (edge.getX() * pos.getX() + edge.getY() * pos.getY()) / vector2;
        // the distance from the label position to the edge vector
        double distance =
            (-pos.getX() * edge.getY() + pos.getY() * edge.getX()) / Math.sqrt(vector2);
        return new Point2D.Double(ratio * GraphConstants.PERMILLE, distance);
    }

    /**
     * Converts four elements of a string array to a rectangle.
     */
    public static Rectangle toBounds(String[] s, int i) {
        if (s.length - i < 4) {
            return null;
        } else {
            return new Rectangle(Integer.parseInt(s[i + 0]), Integer.parseInt(s[i + 1]),
                Integer.parseInt(s[i + 2]), Integer.parseInt(s[i + 3]));
        }
    }

    /**
     * Converts two elements of a string array to a point.
     */
    public static Point toPoint(String[] s, int i) {
        if (s.length - i < 2) {
            return null;
        } else {
            return new Point(Integer.parseInt(s[i + 0]), Integer.parseInt(s[i + 1]));
        }
    }

    /**
     * Converts pairs of elements of a string array to a list of points.
     * @param s array containing string representations of coordinates
     * @param i index from which the string array will be converted to points
     */
    public static List<Point2D> toPoints(String[] s, int i) {
        List<Point2D> result = new LinkedList<>();
        for (int j = i; j < s.length - 1; j += 2) {
            result.add(toPoint(s, j));
        }
        return result;
    }

    /** Returns the singular instance of this class. */
    public static LayoutIO getInstance() {
        return instance;
    }

    /** Singular instance of the class. */
    private static LayoutIO instance = new LayoutIO();
    /**
     * The layout prefix of a version number.
     */
    static public final String VERSION_PREFIX = "v";
    /** The layout prefix of a node layout line. */
    static public final String NODE_PREFIX = "n";
    /** The layout prefix of a node layout line. */
    static public final String EDGE_PREFIX = "e";
    /** The layout prefix of an info layout line. */
    static public final String INFO_PREFIX = "i";
    /** The layout prefix of a layout comment. */
    static public final String COMMENT_PREFIX = "#";
    /**
     * Symbolic name for first layout version.
     */
    static public final int VERSION1 = 1;
    /**
     * Symbolic name for second layout version. The difference with the first
     * version is that the label position is calculated differently.
     */
    static public final int VERSION2 = 2;

    /** The current version number. */
    static public final int CURRENT_VERSION_NUMBER = VERSION2;
    /** Error message in case an error is detected in the layout file. */
    static private final String LAYOUT_FORMAT_ERROR =
        String.format("Error in %s file", FileType.LAYOUT.getExtension());
    /** Double quote character. */
    static private final char DOUBLE_QUOTE = '\"';
    /** Splitting expression for non-empty white space. */
    static private final String WHITESPACE = " ";
    /**
     * The default edge vector, in case a list of points does not give rise to a
     * non-zero vector.
     * @see #version2LabelVector(List,boolean)
     */
    static private final Point2D DEFAULT_EDGE_VECTOR = new Point2D.Double(50, 0);
}