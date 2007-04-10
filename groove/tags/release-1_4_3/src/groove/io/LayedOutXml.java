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
 * $Id: LayedOutXml.java,v 1.4 2007-04-01 12:50:23 rensink Exp $
 */
package groove.io;

import groove.graph.DefaultEdge;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.GraphFactory;
import groove.graph.GraphInfo;
import groove.graph.Node;
import groove.gui.layout.JEdgeLayout;
import groove.gui.layout.JVertexLayout;
import groove.gui.layout.LayoutMap;
import groove.util.FormatException;
import groove.util.ExprParser;
import groove.util.Groove;
import groove.util.Pair;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jgraph.graph.EdgeView;
import org.jgraph.graph.GraphConstants;

/**
 * 
 * @author Arend Rensink
 * @version $Revision: 1.4 $
 */
public class LayedOutXml implements Xml<Graph> {
    /** 
     * The layout prefix of a version number.
     * @see #layoutComment(File) for an explanation of the version numbers. 
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
    /** The current version number. */
    static public final int VERSION_NUMBER = 2;
    /** Line for the layoutfile with the version information. */
    static private final String VERSION_LINE = String.format("%s %d", VERSION_PREFIX, VERSION_NUMBER);
    /** Error message in case an error is detected in the layout file. */
    static private final String LAYOUT_FORMAT_ERROR = "Format error in layout file";
    /** Double quote character. */
    static private final char DOUBLE_QUOTE = '\"';
    /** Splitting expression for non-empty white space. */
    static private final String WHITESPACE = " ";
    
    /**
     * Constructs an xml (un)marshaller, based on {@link UntypedGxl},
     * also able to deal with layout information.
     * The graphs constructed by {@link #unmarshalGraph(File)} are as directed by the
     * graph factory, except that layout information is also taken into account.
     */
    public LayedOutXml(GraphFactory factory) {
        this(new UntypedGxl());
    }

    /**
     * Constructs an xml (un)marshaller, based on {@link UntypedGxl},
     * also able to deal with layout information.
     * The graphs constructed by {@link #unmarshalGraph(File)} are as directed by the
     * default graph factory, except that layout information is also taken into account.
     */
    public LayedOutXml() {
        this(new UntypedGxl());
    }

    /**
     * Wraps a given xml (un)marshaller so as to deal with layout information.
     * The graphs constructed by {@link #unmarshalGraph(File)} are as directed by the
     * given graph factory, except that layout information is also taken into account.
     */
    LayedOutXml(AbstractXml innerXml) {
        marshaller = innerXml;
//        graphXml.setGraphFactory(GraphFactory.newInstance(new DefaultGraph()));
    }

    /** First marshals the graph; then the layout map if there is one. */
    public void marshalGraph(Graph graph, File file) throws FormatException, IOException {
        if (GraphInfo.hasLayoutMap(graph)) {
            marshal(graph, GraphInfo.getLayoutMap(graph), file);
        } else {
            // first marshal the graph
            marshaller.marshalGraph(graph, file);
            // now delete any pre-existing layout information
            toLayoutFile(file).delete();
        }
    }

    /** Marshals the graph and stores the layout map. */
    public void marshal(Graph graph, LayoutMap<Node, Edge> layoutMap, File file) throws FormatException, IOException {
        // first marshal the graph
        marshaller.marshalGraph(graph, file);
        // if there is layout information, create a file for it
        PrintWriter layoutWriter = new PrintWriter(new FileWriter(toLayoutFile(file)));
        // some general wise words first
        for (String line: layoutComment(file)) {
            layoutWriter.println(line);            
        }
        layoutWriter.println(VERSION_LINE);
        // iterator over the layout map and write the layout information
        for (Map.Entry<Node, JVertexLayout> entry: layoutMap.nodeMap().entrySet()) {
            layoutWriter.println(toString(entry.getKey(), entry.getValue()));
        }
        for (Map.Entry<Edge, JEdgeLayout> entry: layoutMap.edgeMap().entrySet()) {
            layoutWriter.println(toString(entry.getKey(), entry.getValue()));
        }
        layoutWriter.close();
    }

    /** This implementation also retrieves layout information. */
    public Graph unmarshalGraph(File file) throws FormatException, IOException {
    	// first get the non-layed out result
        Pair<Graph,Map<String,Node>> preliminary = marshaller.unmarshalGraphMap(file);
        Graph result = preliminary.first();
        Map<String,Node> nodeMap = preliminary.second();
        // read in layout information, if so required
        if (nodeMap != null) {
            LayoutMap<Node,Edge> layoutMap = new LayoutMap<Node,Edge>();
            File layoutFile = toLayoutFile(file);
            if (!layoutFile.exists()) {
                return result;
            }
            BufferedReader layoutReader = new BufferedReader(new FileReader(layoutFile));
            int version = 1;
            // read in from the layout file until done
            for (String nextLine = layoutReader.readLine(); nextLine != null; nextLine = layoutReader.readLine()) {
                String[] parts;
                try {
                    parts = ExprParser.splitExpr(nextLine, WHITESPACE);
                } catch (FormatException exc) {
                    throw new IOException(LAYOUT_FORMAT_ERROR + ": " + exc.getMessage());
                }
                if (parts.length > 0) {
                	String command = parts[0];
					if (command.equals(NODE_PREFIX)) {
						putVertexLayout(layoutMap, parts, nodeMap);
					} else if (command.equals(EDGE_PREFIX)) {
						Edge edge = putEdgeLayout(layoutMap, parts, nodeMap, version);
						if (!result.containsElement(edge)) {
							layoutReader.close();
							throw new IOException(LAYOUT_FORMAT_ERROR
									+ ": unknown edge " + edge);
						}
					} else if (command.equals(VERSION_PREFIX)) {
						try {
							version = Integer.parseInt(parts[1]);
						} catch (Exception exc) {
							throw new IOException(String.format("%s: Version number format error", LAYOUT_FORMAT_ERROR));
						}
					}
                }
            }
            layoutReader.close();
            GraphInfo.setLayoutMap(result, layoutMap);
        }
        return result;
    }

    /**
     * Inserts vertex layout information in a given layout map,
     * based on a string array description and node map.
     */
    protected void putVertexLayout(LayoutMap<Node, Edge> layoutMap, String[] parts, Map<String, Node> nodeMap) throws IOException {
        Node node = nodeMap.get(parts[1]);
        if (node == null) {
            throw new IOException(LAYOUT_FORMAT_ERROR + ": unknown node " + parts[1]);
        }
        Rectangle bounds = toBounds(parts, 2);
//        bounds.setSize(JAttr.DEFAULT_NODE_SIZE);
        if (bounds == null) {
            throw new IOException(LAYOUT_FORMAT_ERROR + ": bounds for " + parts[1] + " cannot be parsed");
        }
        layoutMap.putNode(node, new JVertexLayout(bounds));
    }

    /**
     * Inserts edge layout information in a given layout map,
     * based on a string array description and node map.
     * @param version for version 2, the layout position info has changed
     */
    protected Edge putEdgeLayout(LayoutMap<Node, Edge> layoutMap, String[] parts, Map<String, Node> nodeMap, int version) throws IOException {
        if (parts.length < 7) {
            throw new IOException(LAYOUT_FORMAT_ERROR + ": incomplete edge layout line");
        }
        Node source = nodeMap.get(parts[1]);
        if (source == null) {
            throw new IOException(LAYOUT_FORMAT_ERROR + ": unknown node " + parts[1]);
        }
        Node target = nodeMap.get(parts[2]);
        if (target == null) {
            throw new IOException(LAYOUT_FORMAT_ERROR + ": unknown node " + parts[2]);
        }
        String labelTextWithQuotes = parts[3];
        String labelText = ExprParser.toUnquoted(labelTextWithQuotes, DOUBLE_QUOTE);
        Edge edge = DefaultEdge.createEdge(source, labelText, target);
        try {
            List<Point2D> points;
            int lineStyle;
            if (parts.length == 5) {
            	points = null;
            	lineStyle = JEdgeLayout.defaultLineStyle;
            } else {
                points = toPoints(parts, 6);
                lineStyle = Integer.parseInt(parts[parts.length - 1]);
            }
            Point2D labelPosition = calculateLabelPosition(toPoint(parts, 4), points, version, source == target);
            layoutMap.putEdge(edge, new JEdgeLayout(points, labelPosition, lineStyle));
        } catch (NumberFormatException exc) {
            throw new IOException(LAYOUT_FORMAT_ERROR + ": number format error " + exc.getMessage());
        }
        return edge;
    }
    
    /** 
     * Calculates the label position according to the version of the layout file. 
     * @param isLoop flag indicating that the onderlying edge is a loop
     */
    protected Point2D calculateLabelPosition(Point2D label, List<Point2D> points, int version, boolean isLoop) {
    	Point2D result;
    	if (version != 2) {
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
     * Calculates the relative position of a label from version 1 label position info.
     * The info is that both x and y of the label are given as permilles of the vector.
     * @param label the version 1 label position info 
     * @param points the list of points comprising the edge
     */
    private Point2D version1RelativePos(Point2D label, List<Point2D> points) {
    	// we're trying to reconstruct the label position from the JGraph 5.2 method,
    	// but at this point we don't have the view available which means we don't
    	// have precisely the same information 
		Rectangle2D tmp = version1PaintBounds(points);
		int unit = GraphConstants.PERMILLE;
		Point2D p0 = points.get(0);
		Point2D p1 = points.get(1);
		Point2D pe = points.get(points.size() - 1);
		// Position is direction-dependent
		double x0 = tmp.getX();
		int xdir = 1;
		// take right bound if end point is to the right, or equal and first slope directed left
		if (p0.getX() > pe.getX() || (p0.getX() == pe.getX() && p1.getX() > p0.getX())) {
			x0 += tmp.getWidth();
			xdir = -1;
		}
		double y0 = tmp.getY();
		int ydir = 1;
		// take lower bound if end point is below, or equal and first slope directed up
		if (p0.getY() > pe.getY() || (p0.getY() == pe.getY() && p1.getY() > p0.getY())) {
			y0 += tmp.getHeight();
			ydir = -1;
		}
		double x = x0 + xdir * (tmp.getWidth() * label.getX() / unit);
		double y = y0 + ydir * (tmp.getHeight() * label.getY() / unit);
		return new Point2D.Double(x - p0.getX(), y - p0.getY());
	}
    
    /**
     * Returns the bounds of a list of points.
     * The bounds is the minimal rectangle containing all points.
     */
    private Rectangle2D version1PaintBounds(List<Point2D> points) {
    	double minX = Double.MAX_VALUE;
    	double maxX = Double.MIN_VALUE;
    	double minY = Double.MAX_VALUE;
    	double maxY = Double.MIN_VALUE;
    	for (Point2D point: points) {
    		minX = Math.min(minX, point.getX());
    		maxX = Math.max(maxX, point.getX());
    		minY = Math.min(minY, point.getY());
    		maxY = Math.max(maxY, point.getY());
    	}
    	return new Rectangle2D.Double(minX, minY, maxX-minX, maxY-minY);
    }
    
    /**
	 * Creates an edge vector from a list of points. The edge vector is the
	 * vector from the first to the last point, if they are distinct; otherwise,
	 * it is the average of the edge points; if that yields <code>(0,0)</code>,
	 * the edge vector is given by {@link #DEFAULT_EDGE_VECTOR}.
	 * 
	 * @param points
	 *            the list of points; should not be empty
     * @param isLoop flag indicating that the underlying edge is a loop
	 * @see EdgeView#getLabelVector()
	 */
    private Point2D version2LabelVector(List<Point2D> points, boolean isLoop) {
    	Point2D result = null;
    	// first try a vector from the first to the last point
		Point2D begin = points.get(0);
		Point2D end = points.get(points.size()-1);
		if (! (isLoop || begin.equals(end))) {
			double dx = end.getX() - begin.getX();
			double dy = end.getY() - begin.getY();
			result = new Point2D.Double(dx, dy);
		} else if (points.size() > 0){
			// the first and last point coincide; try taking the max of all points
			double sumX = 0;
			double sumY = 0;
			for (Point2D point: points) {
				sumX += point.getX() - begin.getX();
				sumY += point.getY() - begin.getY();
			}
			// double the average (why? don't know; see EdgeView#getLabelVector())
			int n = points.size()/2;
			result = new Point2D.Double(sumX/n, sumY/n);
		}
		if (result == null || result.getX() == 0 && result.getY() == 0) {
			// nothing worked
			result = DEFAULT_EDGE_VECTOR;
		}
		return result;
    }
    
    /** 
     * The default edge vector, in case a list of points does not
     * give rise to a non-zero vector.
     * @see #version2LabelVector(List,boolean)
     */
    static private final Point2D DEFAULT_EDGE_VECTOR = new Point2D.Double(50,0);
//    
//    /** 
//     * Calculates the relative position of a label from version 1 label position info.
//     * The info is that both x and y of the label are given as permilles of the vector.
//     * @param label the version 1 label position info 
//     * @param edge the edge vector; should not be <code>(0,0)</code>
//     */
//    private Point2D version1RelativePos(Point2D label, Point2D edge) {
//    	double x = edge.getX() * label.getX() / GraphConstants.PERMILLE;
//    	double y = edge.getY() * label.getY() / GraphConstants.PERMILLE;
//    	return new Point2D.Double(x, y);
//    }
//    
    /** 
     * Calculates the version 2 label position values from the relative
     * position of the label.
     * @param pos the relative label position 
     * @param edge the edge vector; should not be <code>(0,0)</code>
     */
    private Point2D version2LabelPos(Point2D pos, Point2D edge) {
    	// the square of the length of the edge vector
    	double vector2 = edge.getX()*edge.getX() + edge.getY()*edge.getY();
    	// the ratio of the label vector to the edge vector
    	double ratio = (edge.getX()*pos.getX() + edge.getY()*pos.getY()) / vector2;
    	// the distance from the label position to the edge vector
    	double distance = (-pos.getX()*edge.getY() + pos.getY()*edge.getX()) / Math.sqrt(vector2);
    	return new Point2D.Double(ratio*GraphConstants.PERMILLE, distance);
    }
    
    /**
     * Returns a multi-line text, formatted as a comment,
     * describing the layout format.
     * @see #COMMENT_PREFIX
     */
    protected List<String> layoutComment(File file) {
        List<String> result = new LinkedList<String>();
        result.add("# Layout information for " + file);
        result.add("# Each line contains layout information about a node or edge");
        result.add("# Version info: v <version number>; 1 = pre-jgraph 5.9, 2 = jgraph 5.9");
        result.add("#     v 1: label position = (x-permillage, y-permillage)");
        result.add("#     v 2: label position = (vector permillage, perpendicular distance)");
        result.add("# Node Format: n <node id> <bounds>");
        result.add("# Edge Format: e <source id> <target id> <edge label> <relative label position> [<points list> <line style>]");
        return result;
    }
    
    /**
     * Converts a file containing a graph to the file containing the
     * graph's layout information, by adding <code>Groove.LAYOUT_EXTENSION</code>
     * ti the file name.
     */
    private File toLayoutFile(File graphFile) {
        return new File(graphFile.getParentFile(), graphFile.getName() + Groove.LAYOUT_EXTENSION);
    }

    /**
     * Converts a graph node plus layout information to a string.
     */
    private String toString(Node node, JVertexLayout layout) {
        StringBuffer result = new StringBuffer();
        result.append(NODE_PREFIX + " " + node + " ");
        Rectangle nodeBounds = Groove.toRectangle(layout.getBounds());
        //        nodeBounds.setSize(JAttr.DEFAULT_NODE_SIZE);
        result.append(toString(nodeBounds));
        return result.toString();
    }

    /**
     * Converts a graph edge plus layout information to a string.
     */
    private String toString(Edge edge, JEdgeLayout layout) {
        StringBuffer result = new StringBuffer();
        result.append(
            EDGE_PREFIX
                + " ");
        for (int i = 0; i < edge.endCount(); i++) {
            result.append(edge.end(i)+" ");
        }
        result.append(ExprParser.toQuoted(edge.label().text(), DOUBLE_QUOTE) + " ");
        result.append(toString(layout.getLabelPosition()));
        result.append(toString(layout.getPoints()));
        result.append("" + layout.getLineStyle());
        return result.toString();
    }

    /**
     * Converts a bounds rectangle to a string, as a space-separated list of
     * the x and y-coordinates and width and height, followed by a space.
     * @see #toBounds
     */
    private String toString(Rectangle r) {
        return "" + r.x + " " + r.y + " " + r.width + " " + r.height + " ";
    }

    /**
     * Converts a point to a string, as a space-separated list of
     * the x and y-coordinates, followed by a space.
     * @see #toBounds
     */
    private String toString(Point2D p) {
        return "" + (int) p.getX() + " " + (int) p.getY() + " ";
    }

    /**
     * Converts a list of points to a string, where the point coordinates
     * are listed in sequence
     * @see #toPoints
     */
    private String toString(List<Point2D> points) {
        StringBuffer pointsText = new StringBuffer();
        for (Point2D p: points) {
            pointsText.append(toString(p));
        }
        return pointsText.toString();
    }

    /**
     * Converts four elements of a string array to a rectangle. 
     */
    private Rectangle toBounds(String[] s, int i) {
        if (s.length - i < 4) {
            return null;
        } else {
            return new Rectangle(
                Integer.parseInt(s[i + 0]),
                Integer.parseInt(s[i + 1]),
                Integer.parseInt(s[i + 2]),
                Integer.parseInt(s[i + 3]));
        }
    }

    /**
     * Converts two elements of a string array to a point. 
     */
    private Point toPoint(String[] s, int i) {
        if (s.length - i < 2) {
            return null;
        } else {
            return new Point(Integer.parseInt(s[i + 0]), Integer.parseInt(s[i + 1]));
        }
    }

    /**
     * Converts pairs of elements of a string array to a list of points. 
     */
    private List<Point2D> toPoints(String[] s, int i) {
        List<Point2D> result = new LinkedList<Point2D>();
        for (int j = i; j < s.length - 1; j += 2) {
            result.add(toPoint(s, j));
        }
        return result;
    }

    /**
     * The inner (un)marshaller.
     */
    private final AbstractXml marshaller;
}