/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
package groove.gui.look;

import groove.gui.jgraph.JEdge;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import org.jgraph.graph.Edge.Routing;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphLayoutCache;

/**
 * Edge routing class that only touches loops with fewer than two 
 * intermediate control points.
 * @author Arend Rensink
 * @version $Revision $
 */
final class LoopRouting implements Routing {
    public int getPreferredLineStyle(EdgeView edge) {
        return NO_PREFERENCE;
    }

    public List<?> route(GraphLayoutCache cache, EdgeView edge) {
        List<Point2D> result = null;
        if (isRoutable(edge)) {
            JEdge<?> cell = (JEdge<?>) edge.getCell();
            VisualMap visuals = cell.getVisuals();
            Point2D startPoint = edge.getPoint(0);
            Point2D endPoint = edge.getPoint(1);
            result = new ArrayList<Point2D>(4);
            result.add(startPoint);
            // test if end point is within node bounds
            Rectangle2D sourceBounds =
                edge.getSource().getParentView().getBounds();
            if (startPoint.equals(endPoint) || sourceBounds.contains(endPoint)) {
                // modify end point so it lies outside node bounds
                endPoint =
                    new Point2D.Double(endPoint.getX()
                        + sourceBounds.getWidth(), endPoint.getY());
            }
            // add first intermediate point
            Point2D newPoint =
                createPointPerpendicular(startPoint, endPoint, true);
            result.add(1, newPoint);
            if (visuals.getLineStyle() != LineStyle.MANHATTAN) {
                // in any but manhattan style, add second intermediate point
                newPoint =
                    createPointPerpendicular(startPoint, endPoint, false);
                result.add(1, newPoint);
                visuals.setLineStyle(LineStyle.BEZIER);
            }
            result.add(startPoint);
            visuals.setPoints(result);
            visuals.setLineStyle(LineStyle.BEZIER);
            GraphConstants.setPoints(edge.getAllAttributes(), result);
        }
        return result;
    }

    /** Determines if this edge should be routed. */
    private boolean isRoutable(EdgeView edge) {
        if (edge.getSource() == null) {
            return false;
        }
        if (!edge.isLoop()) {
            return false;
        }
        JEdge<?> jEdge = (JEdge<?>) edge.getCell();
        if (jEdge.getJGraph().isLayouting()) {
            return false;
        }
        VisualMap visuals = jEdge.getVisuals();
        boolean isManhattan = visuals.getLineStyle() == LineStyle.MANHATTAN;
        return edge.getPointCount() <= (isManhattan ? 2 : 3);
    }

    /**
     * Creates and returns a point perpendicular to the line between two points,
     * at a distance to the second point that is a fraction of the length of the
     * original line. A boolean flag controls the direction to which the
     * perpendicular point sticks out from the original line.
     * @param p1 the first boundary point
     * @param p2 the first boundary point
     * @param left flag to indicate whether the new point is to stick out on the
     *        left or right hand side of the line between <tt>p1</tt> and
     *        <tt>p2</tt>.
     * @return new point on the perpendicular of the line between <tt>p1</tt>
     *         and <tt>p2</tt>
     */
    private Point createPointPerpendicular(Point2D p1, Point2D p2, boolean left) {
        double distance = p1.distance(p2);
        int midX = (int) (p1.getX() + p2.getX()) / 2;
        int midY = (int) (p1.getY() + p2.getY()) / 2;
        // int offset = (int) (5 + distance / 2 + 20 * Math.random());
        int x, y;
        if (distance == 0) {
            x = midX + 20;
            y = midY + 20;
        } else {
            int offset = (int) (5 + distance / 4);
            if (left) {
                offset = -offset;
            }
            double xDelta = p1.getX() - p2.getX();
            double yDelta = p1.getY() - p2.getY();
            x = (int) (p2.getX() + offset * yDelta / distance);
            y = (int) (p2.getY() - offset * xDelta / distance);
        }
        return new Point(Math.max(x, 0), Math.max(y, 0));
    }

}
