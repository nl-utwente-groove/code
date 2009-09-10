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

import groove.graph.Edge;
import groove.graph.GraphShape;
import groove.graph.Node;
import groove.gui.Options;
import groove.gui.jgraph.GraphJEdge;
import groove.gui.jgraph.GraphJModel;
import groove.gui.jgraph.GraphJVertex;
import groove.gui.jgraph.JAttr;
import groove.gui.jgraph.JCell;
import groove.gui.layout.JEdgeLayout;
import groove.gui.layout.JVertexLayout;
import groove.gui.layout.LayoutMap;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jgraph.graph.GraphConstants;


/**
 * Class to perform the conversion from Groove graphs to Tikz format. 
 * @author Eduardo Zambon
 * @version $Revision $
 */
public final class GraphToTikz {

    /**
     * Converts a graph to a Tikz representation.
     * @param model the graph to be converted.
     * @param layoutMap the layout information associated with the graph.
     * @return a string with the Tikz encoding of the graph.
     */
    public static String convertGraphToTikzStr(
            GraphJModel model,
            LayoutMap<Node,Edge> layoutMap) {

        GraphShape graph = model.getGraph();
        boolean showBackground = model.getOptions().
            getValue(Options.SHOW_BACKGROUND_OPTION) == 1 ? true : false;
        StringBuilder result = new StringBuilder();
     
        result.append(beginTikzFig());
        
        for (Node node : graph.nodeSet()) {
            JVertexLayout layout = layoutMap.getNode(node);
            result.append(convertNodeToTikzStr(model.getJVertex(node),
                layout, showBackground));
        }
        
        for (Edge edge : graph.edgeSet()) {
            JEdgeLayout layout = layoutMap.getEdge(edge);
            result.append(convertEdgeToTikzStr(model.getJCell(edge), layout));
        }
        
        result.append(endTikzFig());
        
        return result.toString();
    }
    
    private static StringBuilder convertNodeToTikzStr(
            GraphJVertex node,
            JVertexLayout layout,
            boolean showBackground) {
        
        StringBuilder result = new StringBuilder();
        
        if (node.isVisible()) {
            result.append(BEGIN_NODE);
            
            // Styles.
            result.append(convertStyles(node, showBackground));
            
            // Node ID.
            result.append(encloseSpace(enclosePar(node.getNode().toString())));
            
            // Node Coordinates.
            result.append(encloseSpace(AT_KEYWORD));
            Rectangle2D bounds = layout.getBounds();
            double x = bounds.getCenterX();
            double y = bounds.getCenterY() * -1;
            appendPoint(x, y, result);
            
            // Node Labels.
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
        
        return result;
    }

    private static StringBuilder convertEdgeToTikzStr(
            JCell cell,
            JEdgeLayout layout) {
        
        if (cell instanceof GraphJEdge) {
            return convertEdgeToTikzStr((GraphJEdge) cell, layout);
        } else {
            return new StringBuilder();
        }
    }

//    for (int i = 0; i < points.size(); i++) {
//        appendPoint(points, i, result);
//        result.append(encloseSpace(ANGLED));
//        if (i+1 < points.size()) {
//            double x = points.get(i+1).getX();
//            double y = points.get(i).getY() * -1;
//            result.append(enclosePar(x + ", " + y));
//            result.append(encloseSpace(ANGLED));
//        }
//    }

    
    private static StringBuilder convertEdgeToTikzStr(
            GraphJEdge edge,
            JEdgeLayout layout) {
        
        StringBuilder result = new StringBuilder();
        
        if (edge.isVisible()) {
            ArrayList<String> styles = convertStyles(edge);
            String edgeStyle = styles.get(0); 
            String labStyle = styles.get(1);
            
            result.append(BEGIN_EDGE);
            
            // Edge Style.
            result.append(encloseBrack(edgeStyle));
            
            if (layout != null) { // We have layout points
                List<Point2D> points = layout.getPoints();
                
                if (layout.getLineStyle() == GraphConstants.STYLE_ORTHOGONAL) {

                    // In this style we ignore the node ports from jGraph and
                    // let Tikz find the best connection points.

                    // Source Node.
                    result.append(encloseSpace(enclosePar(
                        edge.getSourceVertex().getNode().toString())));
                    result.append(encloseSpace(DOUBLE_DASH));
                    
                    // Intermediate points
                    for (int i = 1; i < points.size() - 1; i++) {
                        appendPoint(points, i, result);
                        result.append(encloseSpace(DOUBLE_DASH));
                    }

                    // Target Node.
                    result.append(encloseSpace(enclosePar(
                        edge.getTargetVertex().getNode().toString())));
                    
                    result.append(END_PATH);
                    
                    // Extra path for the label position.
                    Point2D labelPos = convertRelativeLabelPositionToAbsolute
                        (layout.getLabelPosition(), points);
                    
                    result.append(BEGIN_NODE);
                    result.append(encloseBrack(labStyle));
                    result.append(encloseSpace(AT_KEYWORD));
                    appendPoint(labelPos, result);
                    result.append(encloseCurly(edge.getText()));
                    
                    result.append(END_EDGE);

                } else if (layout.getLineStyle() == GraphConstants.STYLE_BEZIER) {
                    // EDUARDO
                    System.out.println("Sorry, this line style is not supported yet... Use ORTHOGONAL lines.");
                } else if (layout.getLineStyle() == GraphConstants.STYLE_SPLINE) {
                    // EDUARDO
                    System.out.println("Sorry, this line style is not supported yet... Use ORTHOGONAL lines.");
                } else if (layout.getLineStyle() == JAttr.STYLE_MANHATTAN) {
                    // EDUARDO
                    System.out.println("Sorry, this line style is not supported yet... Use ORTHOGONAL lines.");
                }
            } else { // Default layout.
                // Source Node.
                result.append(encloseSpace(enclosePar(
                    edge.getSourceVertex().getNode().toString())));
                result.append(encloseSpace(DOUBLE_DASH));
                
                // Label.
                result.append(NODE);
                result.append(encloseBrack(labStyle));
                result.append(encloseCurly(edge.getText()));
                
                // Target Node.
                result.append(encloseSpace(enclosePar(
                    edge.getTargetVertex().getNode().toString())));
                
                result.append(END_EDGE);
            }
        }
        
        return result;
    }
    
    private static void appendPoint(List<Point2D> points, int i, StringBuilder s) {
        appendPoint(points.get(i),s);
    }

    private static void appendPoint(Point2D point, StringBuilder s) {
        double x = point.getX();
        double y = point.getY() * -1;
        appendPoint(x, y, s);
    }
    
    private static void appendPoint(double x, double y, StringBuilder s) {
        DecimalFormat df = new DecimalFormat("0.0");
        s.append(enclosePar(df.format(x) + ", " + df.format(y)));
    }
    
    /**
     * Adapted from jGraph.
     * Converts an relative label position (x is distance along edge and y is
     * distance above/below edge vector) into an absolute coordination point.
     * @param geometry the relative label position
     * @param points the list of points along the edge
     * @return the absolute label position
     */
    private static Point2D convertRelativeLabelPositionToAbsolute
        (Point2D geometry,
         List<Point2D> points) {
        Point2D pt = points.get(0);

        if (pt != null)
        {
            double length = 0;
            int pointCount = points.size();
            double[] segments = new double[pointCount];
            // Find the total length of the segments and also store the length
            // of each segment
            for (int i = 1; i < pointCount; i++)
            {
                Point2D tmp = points.get(i);

                if (tmp != null)
                {
                    double dx = pt.getX() - tmp.getX();
                    double dy = pt.getY() - tmp.getY();

                    double segment = Math.sqrt(dx * dx + dy * dy);

                    segments[i - 1] = segment;
                    length += segment;
                    pt = tmp;
                }
            }

            // Change x to be a value between 0 and 1 indicating how far
            // along the edge the label is
            double x = geometry.getX()/GraphConstants.PERMILLE;
            double y = geometry.getY();

            // dist is the distance along the edge the label is
            double dist = x * length;
            length = 0;

            int index = 1;
            double segment = segments[0];

            // Find the length up to the start of the segment the label is
            // on (length) and retrieve the length of that segment (segment)
            while (dist > length + segment && index < pointCount - 1)
            {
                length += segment;
                segment = segments[index++];
            }

            // factor is the proportion along this segment the label lies at
            double factor = (dist - length) / segment;

            Point2D p0 = points.get(index - 1);
            Point2D pe = points.get(index);

            if (p0 != null && pe != null)
            {
                // The x and y offsets of the label from the start point
                // of the segment
                double dx = pe.getX() - p0.getX();
                double dy = pe.getY() - p0.getY();

                // The normal vectors of
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
    
    private static StringBuilder convertHtmlToTikz(StringBuilder line) {
        StringBuilder result = new StringBuilder();
        
        if (line.indexOf(Converter.HTML_EXISTS) > -1) {
            result.append(EXISTS_STR);
        } else if (line.indexOf(Converter.HTML_FORALL) > -1) {
            if (line.indexOf("<" + Converter.SUPER_TAG_NAME + ">") > -1) {
                result.append(FORALLX_STR);
            } else {
                result.append(FORALL_STR);
            }
        } else if (line.indexOf("<" + Converter.ITALIC_TAG_NAME + ">") > -1) {
            line.delete(0,3);
            line.delete(line.length() - 4, line.length());
            result.append(enclose(line.toString(), ITALIC_STYLE, "}"));
        } else {
            result.append(line);
        }

        result.append(CRLF);
        
        return result;
    }
    
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

    private static String convertStyles(
            GraphJVertex node,
            boolean showBackground) {
        
        ArrayList<String> styles = new ArrayList<String>();
        Collection<String> allLabels = node.getPlainLabels();
        if (node.isValueNode()) {
            // Attribute node
            styles.add(BASIC_NODE_STYLE);
            styles.add(ATTRIBUTE_NODE_STYLE);
        } else if (node.isProductNode()) {
            styles.add(BASIC_NODE_STYLE);
            styles.add(PRODUCT_NODE_STYLE);
        } else if (allLabels.contains(EXISTS) || allLabels.contains(FORALL) ||
                   allLabels.contains(FORALLX)) {
            // Quantifier node
            styles.add(BASIC_NODE_STYLE);
            styles.add(QUANTIFIER_NODE_STYLE);
        }
        else if (allLabels.contains(DEL_COL)) {
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
            styles.add(BASIC_NODE_STYLE);
        }
        
        // Check background flag
        if (!showBackground) {
            styles.add(WHITE_FILL);
        }
        
        return styles.toString();
    }

    private static ArrayList<String> convertStyles(GraphJEdge edge) {
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
            Collection<String> src = edge.getSourceVertex().getPlainLabels();
            Collection<String> tgt = edge.getTargetVertex().getPlainLabels();
            if (src.contains(EXISTS) || src.contains(FORALL) || src.contains(FORALLX) ||
                tgt.contains(EXISTS) || tgt.contains(FORALL) || tgt.contains(FORALLX)) {
                styles.add(QUANTIFIER_EDGE_STYLE);
            } else {
                styles.add(BASIC_EDGE_STYLE);
            }
            styles.add(BASIC_LABEL_STYLE);
        }
        
        return styles;
    }
        
    /**
     * @return the line necessary to begin a Tikz figure.
     */
    public static String beginTikzFig() {
        return BEGIN_TIKZ_FIG + "\n";
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
    private static final String EXISTS = "exists:";
    private static final String FORALL = "forall:";
    private static final String FORALLX = "forallx:";
    private static final String NEW_COL = "new:";
    private static final String NEW = "new";
    private static final String NOT_COL = "not:";
    private static final String NOT = "not";
    
    // Tikz output
    private static final String CRLF = "\\\\";
    private static final String BEGIN_TIKZ_FIG = "\\begin{tikzpicture}[scale=0.02]";
    private static final String END_TIKZ_FIG = "\\end{tikzpicture}";
    private static final String BEGIN_NODE = "\\node";
    private static final String AT_KEYWORD = "at";
    private static final String BEGIN_NODE_LAB = " {\\ml{";
    private static final String END_NODE_LAB = "}};\n";
    private static final String EXISTS_STR = "$\\exists$";
    private static final String FORALL_STR = "$\\forall$";
    private static final String FORALLX_STR = "$\\forall^{>0}$";
    private static final String ITALIC_STYLE = "\\textit{";
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
    private static final String ATTRIBUTE_NODE_STYLE = "attr";
    private static final String PRODUCT_NODE_STYLE = "prod";
    private static final String QUANTIFIER_NODE_STYLE = "quantnode";
    private static final String QUANTIFIER_EDGE_STYLE = "quantedge";
    private static final String WHITE_FILL = "whitefill";
    private static final String DOUBLE_DASH = "--";
    private static final String ANGLED = "-|";
}
