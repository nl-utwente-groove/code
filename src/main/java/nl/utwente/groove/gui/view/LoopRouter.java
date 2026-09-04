/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2026 University of Twente
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
 */
package nl.utwente.groove.gui.view;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.util.AIGenerated;

/**
 * Default routing of self-loops: a loop without bends of its own gets a single control
 * point at a fixed distance from the node, on the first side (in the order right, top,
 * left, bottom) that no other edge of the node leaves from or arrives at. The points are
 * stored as ordinary bends, so backends only draw them (see {@code claude/view-facade.md}).
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
@AIGenerated("Claude Fable 5.1, 2026-09")
public final class LoopRouter {
    private LoopRouter() {
        // no instances
    }

    /**
     * Computes the points of a loop with no bends of its own.
     * @param loop the loop cell; its source vertex must be set
     * @param bounds the current bounds of the node of the loop
     * @return the three points of the loop: the node centre, the control point,
     * and the node centre again
     */
    public static List<Point2D> route(ViewEdge<?> loop, Rectangle2D bounds) {
        Set<Side> occupied = EnumSet.noneOf(Side.class);
        var vertex = loop.getSourceVertex();
        Point2D centre = new Point2D.Double(bounds.getCenterX(), bounds.getCenterY());
        if (vertex != null) {
            var context = vertex.getContext();
            while (context.hasNext()) {
                var edge = context.next();
                if (edge == loop) {
                    continue;
                }
                if (edge.isLoop()) {
                    List<Point2D> points = edge.getVisuals().getPoints();
                    if (points.size() > 2) {
                        occupied.add(Side.of(bounds, points.get(1)));
                    }
                } else {
                    var other = edge.getSourceVertex() == vertex
                        ? edge.getTargetVertex()
                        : edge.getSourceVertex();
                    if (other != null) {
                        occupied.add(Side.of(bounds, other.getVisuals().getNodePos()));
                    }
                }
            }
        }
        Side side = Side.RIGHT;
        for (Side candidate : Side.values()) {
            if (!occupied.contains(candidate)) {
                side = candidate;
                break;
            }
        }
        List<Point2D> result = new ArrayList<>(3);
        result.add(centre);
        result.add(side.controlPoint(bounds));
        result.add((Point2D) centre.clone());
        return result;
    }

    /** Distance of the loop's control point from the node border. */
    public static final int LOOP_SIZE = 35;

    /** The sides of a node, in the order of preference for loops. */
    private enum Side {
        RIGHT, TOP, LEFT, BOTTOM;

        /** Returns the side of a node's bounds on which a given point lies. */
        static Side of(Rectangle2D bounds, Point2D point) {
            double dx = point.getX() - bounds.getCenterX();
            double dy = point.getY() - bounds.getCenterY();
            return Math.abs(dx) >= Math.abs(dy)
                ? (dx >= 0
                    ? RIGHT
                    : LEFT)
                : (dy >= 0
                    ? BOTTOM
                    : TOP);
        }

        /** Returns the control point of a loop on this side of a node's bounds. */
        Point2D controlPoint(Rectangle2D bounds) {
            return switch (this) {
            case RIGHT -> new Point2D.Double(bounds.getMaxX() + LOOP_SIZE, bounds.getCenterY());
            case TOP -> new Point2D.Double(bounds.getCenterX(), bounds.getMinY() - LOOP_SIZE);
            case LEFT -> new Point2D.Double(bounds.getMinX() - LOOP_SIZE, bounds.getCenterY());
            case BOTTOM -> new Point2D.Double(bounds.getCenterX(), bounds.getMaxY() + LOOP_SIZE);
            };
        }
    }
}
