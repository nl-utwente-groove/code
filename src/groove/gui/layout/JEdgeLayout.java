/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id$
 */
package groove.gui.layout;

import groove.gui.look.VisualKey;
import groove.gui.look.VisualMap;
import groove.util.line.LineStyle;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.PortView;

/**
 * Class containing the information to lay out an edge. The information consists
 * of a list of intermediate points, and optional label position, and an
 * optional line style. The intermediate points are points that do not
 * correspond to the edge's source or target node. The line style is one of
 * <code>STYLE_ORTHOGONAL</code>, <code>STYLE_BEZIER</code> or
 * <code>STYLE_QUADRATIC</code>.
 */
public class JEdgeLayout implements JCellLayout {
    /**
     * Constructs an edge layout from a <tt>jgraph</tt> attribute map.
     * @param attr the attribute map
     */
    static public JEdgeLayout newInstance(AttributeMap attr) {
        List<Point2D> points = new ArrayList<>();
        List<?> attrPoints = GraphConstants.getPoints(attr);
        if (attrPoints == null) {
            points.add(new Point());
            points.add(new Point());
        } else {
            for (Object p : attrPoints) {
                Point2D point = null;
                if (p instanceof Point2D) {
                    point = (Point2D) p;
                } else if (p instanceof PortView) {
                    point = ((PortView) p).getLocation();
                }
                if (point != null) {
                    points.add(point);
                }
            }
        }
        return new JEdgeLayout(points, GraphConstants.getLabelPosition(attr),
            LineStyle.getStyle(GraphConstants.getLineStyle(attr)));
    }

    /**
     * Constructs an edge layout from a visual map.
     * @param visuals the visual map
     */
    static public JEdgeLayout newInstance(VisualMap visuals) {
        return new JEdgeLayout(visuals.getPoints(), visuals.getLabelPos(), visuals.getLineStyle());
    }

    /**
     * Indicates whether a given label position is the default position.
     * @param labelPosition the label position to be tested
     * @return <code>true</code> if <code>labelPosition</code> is the
     *         default label position
     */
    static public boolean isDefaultLabelPosition(Point2D labelPosition) {
        return labelPosition == null || labelPosition.equals(defaultLabelPosition);
    }

    /**
     * Constructs an edge layout with a given list of intermediate points, a
     * given label position and a given linestyle.
     * @param points the list of intermediate points; not <code>null</code>
     * @param labelPosition the label position
     * @param lineStyle the line style
     * @ensure <code>getPoints().equals(points)</code> and
     *         <code>getLabelPosition().equals(labelPosition)</code> and
     *         <code>getLineStyle() == lineStyle</code>
     */
    public JEdgeLayout(List<Point2D> points, Point2D labelPosition, LineStyle lineStyle) {
        this.points = new LinkedList<>(points);
        if (labelPosition == null) {
            this.labelPosition = defaultLabelPosition;
        } else {
            this.labelPosition = labelPosition;
        }
        this.lineStyle = lineStyle;
    }

    /**
     * Returns an unmodifiable list of points of this edge. The points include
     * the source and target node. Returns <code>null</code> if the edge
     * simply runs from source to target node.
     * @return the list of points of this edge
     */
    public List<Point2D> getPoints() {
        return Collections.unmodifiableList(this.points);
    }

    /**
     * Returns the label position of this edge. Returns <code>null</code> if
     * the label position is default (halfway between source and target point).
     * @return the label position of this edge
     */
    public Point2D getLabelPosition() {
        return this.labelPosition;
    }

    /**
     * Returns the linestyle, or STYLE_UNKNOWN if no linestyle is specified.
     * Legal values are <code>STYLE_ORTHOGONAL</code>,
     * <code>STYLE_BEZIER</code> or <code>STYLE_QUADRATIC</code>
     * @return the linestyle of this edge layout.
     */
    public LineStyle getLineStyle() {
        return this.lineStyle;
    }

    /**
     * Converts the layout information into an attribute map as required by
     * <tt>jgraph</tt>. The attribute map contains points, label position and
     * linestyle as specified by this edge layout.
     * @return an attribute map with layout information
     */
    @Override
    public AttributeMap toJAttr() {
        AttributeMap result = new AttributeMap();
        GraphConstants.setPoints(result, this.points);
        GraphConstants.setLineStyle(result, this.lineStyle.getCode());
        GraphConstants.setLabelPosition(result, this.labelPosition == null ? defaultLabelPosition
                : this.labelPosition);
        return result;
    }

    @Override
    public VisualMap toVisuals() {
        VisualMap result = new VisualMap();
        if (this.points != null) {
            result.put(VisualKey.POINTS, this.points);
        }
        result.put(VisualKey.LINE_STYLE, this.lineStyle);
        if (this.labelPosition != null) {
            result.put(VisualKey.LABEL_POS, this.labelPosition);
        }
        return result;
    }

    /**
     * Edge information is default if there are no points, and the label
     * position is default.
     */
    @Override
    public boolean isDefault() {
        return VisualKey.LABEL_POS.getDefaultValue().equals(getLabelPosition())
            && this.lineStyle.isDefault() && getPoints().size() == 2;
    }

    /**
     * This layout equals another object if that is also a {@link JEdgeLayout},
     * with equal points, label position and line stype.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof JEdgeLayout) {
            JEdgeLayout other = (JEdgeLayout) obj;
            return getPoints().equals(other.getPoints())
                && getLabelPosition().equals(other.getLabelPosition())
                && getLineStyle() == other.getLineStyle();
        } else {
            return false;
        }
    }

    /**
     * The hash code is the sum of the hash codes of points, label position and
     * line style.
     */
    @Override
    public int hashCode() {
        return getPoints().hashCode() + getLabelPosition().hashCode() + getLineStyle().hashCode();
    }

    @Override
    public String toString() {
        return "LabelPosition=" + getLabelPosition() + "; Points=" + getPoints() + "; LineStyle="
            + getLineStyle();
    }

    /** The label position of this edge layout. */
    private final Point2D labelPosition;
    /** The list of intermediate points of this edge layout. */
    private final List<Point2D> points;
    /** The line style of this edge layout. */
    private final LineStyle lineStyle;
}