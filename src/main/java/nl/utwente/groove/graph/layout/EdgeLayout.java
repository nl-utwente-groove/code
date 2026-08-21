/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
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
package nl.utwente.groove.graph.layout;

import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.util.line.LineStyle;

/**
 * Class containing the information to lay out an edge. The information consists
 * of a list of intermediate points, and optional label position, and an
 * optional line style. The intermediate points are points that do not
 * correspond to the edge's source or target node. The line style is one of
 * <code>STYLE_ORTHOGONAL</code>, <code>STYLE_BEZIER</code> or
 * <code>STYLE_QUADRATIC</code>.
 */
@NonNullByDefault
public class EdgeLayout implements ElementLayout {
    /**
     * Indicates whether a given label position is the default position.
     * @param labelPosition the label position to be tested
     * @return <code>true</code> if <code>labelPosition</code> is the
     *         default label position
     */
    static public boolean isDefaultLabelPosition(@Nullable Point2D labelPosition) {
        return labelPosition == null || labelPosition.equals(defaultLabelPosition);
    }

    /**
     * Constructs an edge layout with a given list of intermediate points, a
     * given label position and a given linestyle.
     * @param points the list of intermediate points; not <code>null</code>
     * @param labelPosition the label position
     * @param lineStyle the line style
     */
    public EdgeLayout(List<Point2D> points, @Nullable Point2D labelPosition, LineStyle lineStyle) {
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
     * the source and target node.
     * @return the list of points of this edge
     */
    public List<Point2D> getPoints() {
        return Collections.unmodifiableList(this.points);
    }

    /**
     * Returns the label position of this edge.
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
     * Edge information is default if there are no points, and the label
     * position is default.
     */
    @Override
    public boolean isDefault() {
        return defaultLabelPosition.equals(getLabelPosition()) && this.lineStyle.isDefault()
            && getPoints().size() == 2;
    }

    /**
     * This layout equals another object if that is also a {@link EdgeLayout},
     * with equal points, label position and line stype.
     */
    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof EdgeLayout other) {
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
        return getPoints().hashCode() + getLabelPosition().hashCode() + getLineStyle().ordinal();
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
