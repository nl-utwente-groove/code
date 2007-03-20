/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
 * $Id: JEdgeLayout.java,v 1.1.1.1 2007-03-20 10:05:31 kastenberg Exp $
 */
package groove.gui.layout;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.GraphConstants;


/**
 * Class containing the information to lay out an edge.
 * The information consists of a list of intermediate points,
 * and optional label position, and an optional line style.
 * The intermediate points are points that do not correspond
 * to the edge's source or target node.
 * The line style is one of <code>STYLE_ORTHOGONAL</code>,
 * <code>STYLE_BEZIER</code> or <code>STYLE_QUADRATIC</code>.
 */
public class JEdgeLayout implements JCellLayout {
    /**
     * Constructs an edge layout from a <tt>jgraph</tt>
     * attribute map.
     * @param attr the attribute map
     */
    static public JEdgeLayout newInstance(AttributeMap attr) {
        return new JEdgeLayout(GraphConstants.getPoints(attr), GraphConstants.getLabelPosition(attr), GraphConstants.getLineStyle(attr));
    }

    /**
     * Indicates whether a given label position is the default position.
     * @param labelPosition the label position to be tested
     * @return <code>true</code> if <code>labelPosition</code> is the default label position
     */
    static public boolean isDefaultLabelPosition(Point2D labelPosition) {
        return labelPosition == null || labelPosition.equals(defaultLabelPosition);
    }

    /**
     * Indicates whether a given line style is the default line style,
     * i.e., the orthogonal style.
     * @param lineStyle the line style to be tested
     * @return <code>true</code> if <code>lineStyle</code> is the default line style
     */
    static public boolean isDefaultLineStyle(int lineStyle) {
        return lineStyle == defaultLineStyle;
    }

    /**
     * Constructs an edge layout with a given list of intermediate points,
     * a given label position and a given linestyle.
     * @param points the list of intermediate points
     * @param labelPosition the label position
     * @param lineStyle the line style
     * @ensure <code>getPoints().equals(points)</code> and
     * <code>getLabelPosition().equals(labelPosition)</code> and
     * <code>getLineStyle() == lineStyle</code>
     */
    public JEdgeLayout(List<Point2D> points, Point2D labelPosition, int lineStyle) {
        if (points == null || points.isEmpty()) {
            // make a list of at least two points
            this.points = new LinkedList<Point2D>();
            this.points.add(new Point());
            this.points.add(new Point());
        } else {
            this.points = new LinkedList<Point2D>(points);
        }
        if (labelPosition == null) {
            this.labelPosition = defaultLabelPosition;
        } else {
            this.labelPosition = labelPosition;
        }
        this.lineStyle = lineStyle;
    }

    /**
     * Constructs an edge layout with a given list of intermediate points,
     * a given label position and unspecified line style.
     * @param points the list of intermediate points
     * @param labelPosition the label position
     * @ensure <code>getPoints().equals(points)</code> and
     * <code>getLabelPosition().equals(labelPosition)</code> and
     * <code>isDefaultLineStyle(getLineStyle())</code>
     */
    public JEdgeLayout(List<Point2D> points, Point labelPosition) {
        this(points, labelPosition, defaultLineStyle);
    }

    /**
     * Constructs an edge layout with a given list of intermediate points,
     * an unspecified label position and unspecified line style.
     * @param points the list of intermediate points
     * @ensure <code>getPoints().equals(points)</code> and
     * <code>isDefaultLabelPosition(getLabelPosition())</code> and
     * <code>isDefaultLineStyle(getLineStyle())</code>
     */
    public JEdgeLayout(List<Point2D> points) {
        this(points, defaultLabelPosition);
    }

    /**
     * Constructs an edge layout with no intermediate points,
     * an unspecified label position and unspecified line style.
     * @ensure <code>getPoints().size() == 0</code> and
     * <code>getLabelPosition() == null</code> and
     * <code>getLineStyle() == STYLE_UNKNOWN</code>
     */
    public JEdgeLayout() {
        this(new LinkedList<Point2D>());
    }

    /**
     * Returns an unmodifiable list of points of this edge.
     * The points include the source and target node.
     * Returns <code>null</code> if the edge simply runs from source to
     * target node.
     * @return the list of points of this edge
     */
    public List<Point2D> getPoints() {
        return Collections.unmodifiableList(points);
    }

    /**
     * Returns the label position of this edge.
     * Returns <code>null</code> if the label position is default
     * (halfway between source and target point).
     * @return the label position of this edge
     */
    public Point2D getLabelPosition() {
        return labelPosition;
    }

    /**
     * Returns the linestyle, or STYLE_UNKNOWN if no linestyle is specified.
     * Legal values are <code>STYLE_ORTHOGONAL</code>,
     * <code>STYLE_BEZIER</code> or
     * <code>STYLE_QUADRATIC</code>
     * @return the linestyle of this edge layout.
     */
    public int getLineStyle() {
        return lineStyle;
    }

    /**
     * Converts the layout information into an attribute map
     * as required by <tt>jgraph</tt>.
     * The attribute map contains points, label position and 
     * linestyle as specified by this edge layout.
     * @return an attribute map with layout information
     */
    public AttributeMap toJAttr() {
        AttributeMap result = new AttributeMap();
        GraphConstants.setPoints(result, points);
        GraphConstants.setLineStyle(result, lineStyle);
        GraphConstants.setLabelPosition(result, labelPosition == null ? defaultLabelPosition : labelPosition);
        return result;
    }
    
    /**
     * Edge information is default if there are no points, and the label position is default.
     */
    public boolean isDefault() {
        return isDefaultLabelPosition(getLabelPosition()) && isDefaultLineStyle(lineStyle) && getPoints().size() == 2;
    }

    /**
     * This layout equals another object if that is also a {@link JEdgeLayout},
     * with equal points, label position and line stype.
     */
    public boolean equals(Object obj) {
        if (obj instanceof JEdgeLayout) {
            JEdgeLayout other = (JEdgeLayout) obj;
            return getPoints().equals(other.getPoints()) && getLabelPosition().equals(other.getLabelPosition()) && getLineStyle() == other.getLineStyle();
        } else {
            return false;
        }
    }
    
    /**
     * The hash code is the sum of the hash codes of points, label position and line style.
     */
    public int hashCode() {
        return getPoints().hashCode() + getLabelPosition().hashCode() + getLineStyle();
    }

    /** The label position of this edge layout. */
    private final Point2D labelPosition;
    /** The list of intermediate points of this edge layout. */
    private final List<Point2D> points;
    /** The line style of this edge layout. */
    private final int lineStyle;
}