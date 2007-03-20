/* $Id: JEdgeRouting.java,v 1.1.1.1 2007-03-20 10:05:32 kastenberg Exp $ */
package groove.gui.jgraph;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.PortView;

public class JEdgeRouting extends DefaultEdge.LoopRouting {
    /**
     * Adds points to the view and sets the line style so that the edge makes a nice curve.
     * The points are created perpendicular to the line between the first and
     * second point when the method is invoked, also taking the vertex bound
     * into account. All but the first and last points of the original points are removed.
     */
	@Override
	protected List routeLoop(EdgeView edge) {
		List<Object> points = edge.getPoints();
		if (points.size() <= 3) {
			Point2D startPoint = toPoint(points.get(0));
			Point2D endPoint = toPoint(points.get(1));
			if (points.size() > 2) {
				points.remove(1);
			}
			Rectangle2D vertexBounds = edge.getSource().getParentView().getBounds();
			if (vertexBounds.contains(endPoint)) {
				endPoint.setLocation(endPoint.getX()
						+ vertexBounds.getWidth() * 2, endPoint.getY());
			}
			points.add(1, createPointPerpendicular(startPoint, endPoint, true));
			points.add(1, createPointPerpendicular(startPoint, endPoint, false));
			return points;
		} else {
			// edge is already routed
			return null;
		}
	}
	
    /**
     * Creates and returns a point perpendicular to the line between two points,
     * at a distance to the second point that is a fraction of the length of
     * the original line.
     * A boolean flag controls the direction to which the perpendicular point
     * sticks out from the original line.
     * @param p1 the first boundary point
     * @param p2 the first boundary point
     * @param left flag to indicate whether the new point is to stick out on the
     * left or right hand side of the line between <tt>p1</tt> and <tt>p2</tt>.
     * @return new point on the perpendicular of the line between <tt>p1</tt> and <tt>p2</tt>
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
    

    /**
     * Returns the point associated with a point or port view.
     */
    private Point2D toPoint(Object obj) {
        if (obj instanceof Point2D) {
            return (Point2D) obj;
        } else if (obj instanceof PortView) {
            return ((PortView) obj).getLocation(null);
        } else {
            return null;
        }
    }
}