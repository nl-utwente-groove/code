/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2026
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
package nl.utwente.groove.gui.yfiles;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import com.yworks.yfiles.geometry.GeneralPath;
import com.yworks.yfiles.geometry.IPoint;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.styles.IArrow;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.input.IHitTestable;
import com.yworks.yfiles.graph.styles.IEdgeStyle;
import com.yworks.yfiles.graph.styles.PolylineEdgeStyleRenderer;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.VisualGroup;

import nl.utwente.groove.gui.look.Values;
import nl.utwente.groove.gui.look.VisualMap;
import nl.utwente.groove.gui.view.InterpolatingBezier;
import nl.utwente.groove.gui.view.LoopRouter;
import nl.utwente.groove.gui.view.EdgeGeometry;
import nl.utwente.groove.gui.view.ParallelEdges;
import nl.utwente.groove.gui.view.cell.AViewEdge;
import nl.utwente.groove.gui.view.cell.AViewVertex;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.line.LineStyle;

/**
 * Renderer of the shared edge style, reading its parameters from the edge cell in the
 * edge's tag: the pen, the arrows, and the path according to the cell's line style
 * (polyline, Manhattan, Bezier through the points, or a smoothed polyline for splines)
 * with the loop shape of the JGraph backend. On top of the polyline machinery of yFiles
 * (cropping at the node outlines, arrows, hit testing, label placement along the path)
 * it adds the decorations that the JGraph backend paints: the inner line, the
 * translucent overlay of an edge with diagnostics, and the emphasis of a selected edge.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
final class CellEdgeStyleRenderer extends PolylineEdgeStyleRenderer {
    /** Returns the edge cell shown by a given edge, if the edge has one. */
    static @Nullable AViewEdge<?> cellOf(IEdge edge) {
        return edge.getTag() instanceof AViewEdge<?> cell
            ? cell
            : null;
    }

    /** Returns the cell of the currently configured edge, if any. */
    private @Nullable AViewEdge<?> cell() {
        return cellOf(getEdge());
    }

    /**
     * Returns the visuals of the currently configured edge: those of its cell, or the
     * defaults for an edge without a cell, such as the preview of an edge being created.
     */
    private VisualMap visuals() {
        var cell = cell();
        return cell == null
            ? DEFAULT_VISUALS
            : cell.getVisuals();
    }

    /** The visuals of an edge without a cell; never modified. */
    private static final VisualMap DEFAULT_VISUALS = new VisualMap();

    @Override
    protected Pen getPen() {
        return CellStyles.pen(visuals());
    }

    @Override
    protected IArrow getSourceArrow() {
        VisualMap visuals = visuals();
        return CellStyles
            .arrow(visuals.getEdgeSourceShape(), visuals.getForeground(), visuals.getLineWidth());
    }

    @Override
    protected IArrow getTargetArrow() {
        VisualMap visuals = visuals();
        return CellStyles
            .arrow(visuals.getEdgeTargetShape(), visuals.getForeground(), visuals.getLineWidth());
    }

    /* Splines are approximated by smoothing the corners of the polyline. */
    @Override
    protected double getSmoothingLength() {
        return visuals().getLineStyle() == LineStyle.SPLINE
            ? SPLINE_SMOOTHING
            : 0;
    }

    /** Smoothing length used for the corners of spline edges. */
    private static final double SPLINE_SMOOTHING = 40;

    @Override
    protected GeneralPath createPath() {
        List<Point2D> points = new ArrayList<>();
        for (IPoint point : getEdge().getPathPoints()) {
            points.add(new Point2D.Double(point.getX(), point.getY()));
        }
        var cell = cell();
        LineStyle style = visuals().getLineStyle();
        boolean loop;
        if (cell == null) {
            // the preview of an edge being created: a line to the pointer
            loop = false;
        } else {
            loop = cell.isLoop();
            if (!loop && points.size() == 2 && isLoop(getEdge())
                && YFilesCellStore.cellOf(getEdge().getSourceNode()) instanceof AViewVertex<?> vertex) {
                // an edge whose end is being dragged onto its other end's node:
                // previewed as the loop it will become
                loop = true;
                points = LoopRouter
                    .route(vertex, cell,
                           getEdge().getSourceNode().getLayout().toRectD().toRectangle2D());
            } else if (!loop && points.size() == 2) {
                points = fanOut(cell, points);
            }
        }
        if (loop && points.size() == 3) {
            points = loopPoints(points);
        } else {
            points = trimInsideEnds(points);
        }
        boolean manhattan = style == LineStyle.MANHATTAN && points.size() > 2;
        if (style == LineStyle.BEZIER && points.size() > 2) {
            // the curve is computed to the outline points, as JGraph does, and cut
            // short for the arrows at its straight tails
            return bezier(clipEnds(points, false, 0, 0), arrowCrop(getSourceArrow()),
                          arrowCrop(getTargetArrow()));
        }
        points = clipEnds(points, manhattan, arrowCrop(getSourceArrow()),
                          arrowCrop(getTargetArrow()));
        return manhattan
            ? manhattan(points)
            : polyline(points);
    }

    /**
     * Returns the length by which a path is cut short at an end for its arrow: yFiles
     * draws an arrow from the end of the path onwards, over the arrow's length.
     */
    private static double arrowCrop(IArrow arrow) {
        return arrow.getLength() + arrow.getCropLength();
    }

    /*
     * The hit test follows the drawn path, not the polyline through the points: a
     * Manhattan edge is drawn along other lines, and a spline along rounded corners.
     */
    @Override
    @NonNullByDefault({})
    public IHitTestable getHitTestable(IEdge edge, IEdgeStyle style) {
        IHitTestable result = super.getHitTestable(edge, style);
        if (result != null) {
            configure();
        }
        return result;
    }

    @Override
    @NonNullByDefault({})
    public boolean isHit(IInputModeContext context, PointD location) {
        GeneralPath path = getPath();
        if (path == null) {
            return false;
        }
        double smoothing = getSmoothingLength();
        if (smoothing > 0) {
            path = path
                .createSmoothedPath(smoothing, getSmoothingPolicy(),
                                    isUsingStraightEndsForSmoothingEnabled());
        }
        double radius = context.getHitTestRadius() + CellStyles.strokeWidth(visuals()) / 2;
        return path.pathContains(location, radius);
    }

    /*
     * The path is not cropped by yFiles: createPath already ends it at the node
     * outlines, at the points where the JGraph backend ends its edges. yFiles' own
     * cropping replaces a final curve segment by a straight line to the intersection,
     * which turned the last segment of a Bezier edge straight.
     */
    @Override
    @NonNullByDefault({})
    protected GeneralPath cropPath(GeneralPath path) {
        return path;
    }

    /** Tests if an edge item connects a node to itself. */
    private static boolean isLoop(IEdge edge) {
        return edge.getSourcePort().getOwner() == edge.getTargetPort().getOwner();
    }

    /**
     * Shifts the ends of an unrouted edge with parallels away from the line between the
     * vertex centres, as the JGraph backend does (see {@link ParallelEdges}); the shift
     * is capped by the radius of each end node in the direction of the shift, so that
     * the shifted end stays inside its node and the path is cropped at the outline.
     */
    private List<Point2D> fanOut(AViewEdge<?> cell, List<Point2D> points) {
        int rank = ParallelEdges.rank(cell);
        if (rank == 0) {
            return points;
        }
        Point2D start = points.get(0);
        Point2D end = points.get(1);
        double dx = end.getX() - start.getX();
        double dy = end.getY() - start.getY();
        IEdge edge = getEdge();
        Point2D startShift = ParallelEdges
            .shift(rank, dx, dy, radius(edge.getSourceNode(), dy, -dx));
        Point2D endShift = ParallelEdges.shift(rank, dx, dy, radius(edge.getTargetNode(), dy, -dx));
        List<Point2D> result = new ArrayList<>(2);
        result
            .add(new Point2D.Double(start.getX() + startShift.getX(),
                start.getY() + startShift.getY()));
        result.add(new Point2D.Double(end.getX() + endShift.getX(), end.getY() + endShift.getY()));
        return result;
    }

    /** Returns the radius of a node in a given direction, less a pixel to stay inside. */
    private static double radius(INode node, double dx, double dy) {
        Rectangle2D bounds = node.getLayout().toRectD().toRectangle2D();
        double result = YFilesCellStore.cellOf(node) instanceof AViewVertex<?> vertex
            ? vertex.getVisuals().getNodeShape().getRadius(bounds, dx, dy)
            : Math.min(bounds.getWidth(), bounds.getHeight()) / 2;
        return Math.max(0, result - 1);
    }

    /**
     * Replaces the single control point of a loop by two points on either side of
     * the line from the node centre to the control point, as the JGraph backend does.
     */
    private static List<Point2D> loopPoints(List<Point2D> points) {
        Point2D centre = points.get(0);
        Point2D control = points.get(1);
        List<Point2D> result = new ArrayList<>(4);
        result.add(centre);
        result.add(perpendicular(centre, control, true));
        result.add(perpendicular(centre, control, false));
        result.add(points.get(2));
        return result;
    }

    /**
     * Returns a point sticking out perpendicularly from the line between two points,
     * at the second point, at a distance that grows with the length of the line.
     */
    private static Point2D perpendicular(Point2D p1, Point2D p2, boolean left) {
        double distance = p1.distance(p2);
        if (distance == 0) {
            return new Point2D.Double(p2.getX() + 20, p2.getY() + 20);
        }
        double offset = 5 + distance / 4;
        if (left) {
            offset = -offset;
        }
        double xDelta = p1.getX() - p2.getX();
        double yDelta = p1.getY() - p2.getY();
        return new Point2D.Double(p2.getX() + offset * yDelta / distance,
            p2.getY() - offset * xDelta / distance);
    }

    /** Creates a polyline through the given points. */
    private static GeneralPath polyline(List<Point2D> points) {
        GeneralPath result = new GeneralPath(points.size());
        boolean first = true;
        for (Point2D point : points) {
            if (first) {
                result.moveTo(point.getX(), point.getY());
                first = false;
            } else {
                result.lineTo(point.getX(), point.getY());
            }
        }
        return result;
    }

    /** Creates a path of horizontal and vertical segments through the given points. */
    private static GeneralPath manhattan(List<Point2D> points) {
        GeneralPath result = new GeneralPath(2 * points.size());
        Point2D previous = points.get(0);
        result.moveTo(previous.getX(), previous.getY());
        for (int i = 1; i < points.size(); i++) {
            Point2D point = points.get(i);
            // first move horizontally, then vertically if needed
            result.lineTo(point.getX(), previous.getY());
            if (point.getY() != previous.getY()) {
                result.lineTo(point.getX(), point.getY());
            }
            previous = point;
        }
        return result;
    }

    /**
     * Replaces the end points of a path (the node centres) by the points where the path
     * leaves the end nodes, as the JGraph backend ends its edges (see
     * {@link #endPoint}), cut short by given lengths for the arrows. Ends that
     * coincide are left alone. A straight edge between aligned nodes runs vertically
     * or horizontally between them, see {@link EdgeGeometry#alignedCentres}.
     * @param manhattan if {@code true}, the path is drawn in the Manhattan style
     * @param sourceCrop length to cut off at the source end, for its arrow
     * @param targetCrop length to cut off at the target end, for its arrow
     */
    private List<Point2D> clipEnds(List<Point2D> points, boolean manhattan,
                                   double sourceCrop, double targetCrop) {
        int n = points.size();
        if (n < 2 || points.get(0).distance(points.get(n - 1)) < MIN_DISTANCE && n == 2) {
            return points;
        }
        IEdge edge = getEdge();
        INode sourceNode = edge.getSourceNode();
        INode targetNode = edge.getTargetNode();
        // the inner points from which the ends are computed, null for the smart
        // choice from the centre, and the outer points towards which they go
        Point2D innerStart = null;
        Point2D outerStart = points.get(1);
        Point2D innerEnd = null;
        Point2D outerEnd = points.get(n - 2);
        if (n == 2) {
            var aligned = EdgeGeometry.alignedCentres(bounds(sourceNode), bounds(targetNode));
            if (aligned != null) {
                // the ends keep the shift of a fanned-out edge
                innerStart = shifted(aligned.source(), sourceNode, points.get(0));
                innerEnd = shifted(aligned.target(), targetNode, points.get(1));
                outerStart = innerEnd;
                outerEnd = innerStart;
            }
        }
        List<Point2D> result = new ArrayList<>(points);
        Point2D start = endPoint(sourceNode, innerStart, outerStart, manhattan, true);
        if (start != null) {
            Point2D towards = manhattan
                ? cornerAfter(start, outerStart)
                : outerStart;
            result.set(0, shorten(start, towards, sourceCrop));
        }
        Point2D end = endPoint(targetNode, innerEnd, outerEnd, manhattan, false);
        if (end != null) {
            Point2D towards = manhattan
                ? cornerBefore(outerEnd, end)
                : outerEnd;
            result.set(n - 1, shorten(end, towards, targetCrop));
        }
        return result;
    }

    /** Returns the bounds of the shape of a node. */
    private static Rectangle2D bounds(INode node) {
        return CellNodeStyle.bounds(node, CellNodeStyle.lineWidth(node));
    }

    /**
     * Moves an aligned centre by the offset of an edge's end point from the centre of
     * its node: the shift of a fanned-out edge, see {@link #fanOut}.
     */
    private static Point2D shifted(Point2D aligned, INode node, Point2D end) {
        PointD centre = node.getLayout().getCenter();
        return new Point2D.Double(aligned.getX() + end.getX() - centre.getX(),
            aligned.getY() + end.getY() - centre.getY());
    }

    /** Returns the corner at which a Manhattan path leaves its first point towards the next. */
    private static Point2D cornerAfter(Point2D first, Point2D next) {
        // the path moves horizontally first
        return next.getX() == first.getX()
            ? next
            : new Point2D.Double(next.getX(), first.getY());
    }

    /** Returns the corner from which a Manhattan path arrives at its last point. */
    private static Point2D cornerBefore(Point2D previous, Point2D last) {
        // the path arrives vertically, unless the last horizontal move ends it
        return last.getY() == previous.getY()
            ? previous
            : new Point2D.Double(last.getX(), previous.getY());
    }

    /** Moves a point towards another over a given length, at most up to that other point. */
    private static Point2D shorten(Point2D point, Point2D towards, double length) {
        double distance = point.distance(towards);
        if (length <= 0 || distance == 0) {
            return point;
        }
        double ratio = Math.min(1, length / distance);
        return between(point, towards, ratio);
    }

    /**
     * Returns the point where an edge leaves a node towards a given point, as the JGraph
     * backend chooses it: the line is drawn from an inner point of the node to the
     * outer point. If no inner point is given, it is the centre, shifted to the
     * coordinate of the outer point where that lies within the node's extent (less a
     * margin), so that an edge to a point nearly above the node leaves it straight up;
     * for a Manhattan edge whose outer point lies diagonally, the outer point is first
     * pulled into horizontal reach of the source node or vertical reach of the target
     * node, so that the edge leaves the source sideways and enters the target from
     * above or below. {@code null} if the node's geometry gives no finite intersection.
     * @param node the end node
     * @param inner the inner point of the line; if {@code null}, computed from the centre
     * @param outer the next point of the path
     * @param manhattan if {@code true}, the path is drawn in the Manhattan style
     * @param source if {@code true}, the node is the source of the edge
     */
    private static @Nullable Point2D endPoint(INode node, @Nullable Point2D inner,
                                              Point2D outer, boolean manhattan,
                                              boolean source) {
        Rectangle2D bounds = bounds(node);
        double left = bounds.getMinX();
        double right = bounds.getMaxX();
        double top = bounds.getMinY();
        double bottom = bounds.getMaxY();
        double cx = bounds.getCenterX();
        double cy = bounds.getCenterY();
        double qx = outer.getX();
        double qy = outer.getY();
        if (manhattan && (qx < left || qx > right) && (qy < top || qy > bottom)) {
            if (source) {
                // move the outer point into horizontal reach
                double dy = qy - cy;
                double room = bounds.getHeight() * (1 - 2 / DROP_FRACTION) * 0.5;
                qy = cy + room * Math.signum(dy) * Math.min(Math.abs(dy) / MAX_RATIO_DISTANCE, 1);
            } else {
                // move the outer point into vertical reach
                double dx = qx - cx;
                double room = bounds.getWidth() * (1 - 2 / DROP_FRACTION) * 0.5;
                qx = cx + room * Math.signum(dx) * Math.min(Math.abs(dx) / MAX_RATIO_DISTANCE, 1);
            }
        }
        double px;
        double py;
        if (inner == null) {
            // the inner point follows the outer one where that is within reach
            double xDrop = bounds.getWidth() / DROP_FRACTION;
            double yDrop = bounds.getHeight() / DROP_FRACTION;
            px = qx > left + xDrop && qx < right - xDrop
                ? qx
                : cx;
            py = qy > top + yDrop && qy < bottom - yDrop
                ? qy
                : cy;
        } else {
            px = inner.getX();
            py = inner.getY();
        }
        var style = node.getStyle();
        var geometry = style.getRenderer().getShapeGeometry(node, style);
        PointD result = geometry.getIntersection(new PointD(px, py), new PointD(qx, qy));
        return result == null || !Double.isFinite(result.getX())
            || !Double.isFinite(result.getY())
                ? null
                : new Point2D.Double(result.getX(), result.getY());
    }

    /** Fraction of a node's extent within which an edge end does not follow the outer point. */
    private static final double DROP_FRACTION = EdgeGeometry.DROP_FRACTION;
    /** Distance over which the pull of a Manhattan edge's outer point grows to its full room. */
    private static final double MAX_RATIO_DISTANCE = 250;

    /**
     * Creates a smooth Bezier curve through the given points, see {@link InterpolatingBezier}.
     * Coinciding neighbours are dropped first: the interpolation divides by the distances
     * between neighbours, and a layout may put a bend onto a node centre or another bend.
     */
    private static GeneralPath bezier(List<Point2D> points, double sourceCrop, double targetCrop) {
        points = distinct(points);
        int n = points.size();
        if (n < 3) {
            return polyline(points);
        }
        List<Point2D> control = InterpolatingBezier.controlPoints(points);
        GeneralPath result = new GeneralPath(n + 2);
        // the curve starts and ends with a short straight piece along its tangent:
        // yFiles derives the arrows from straight segments (as does JGraph, from the
        // control points), and a curve up to the end point would leave it without
        // arrows; the arrows themselves take the room after the straight pieces
        Point2D start = points.get(0);
        Point2D c0 = control.get(0);
        Point2D p1 = points.get(1);
        double t0 = tailRatio(start, c0, TAIL_LENGTH + sourceCrop);
        Point2D headEnd = quadPoint(start, c0, p1, t0);
        Point2D headControl = between(c0, p1, t0);
        Point2D headStart = shorten(start, headEnd, sourceCrop);
        result.moveTo(headStart.getX(), headStart.getY());
        result.lineTo(headEnd.getX(), headEnd.getY());
        result.quadTo(headControl.getX(), headControl.getY(), p1.getX(), p1.getY());
        for (int i = 2; i <= n - 2; i++) {
            Point2D c1 = control.get(2 * i - 3);
            Point2D c2 = control.get(2 * i - 2);
            Point2D p = points.get(i);
            result.cubicTo(c1.getX(), c1.getY(), c2.getX(), c2.getY(), p.getX(), p.getY());
        }
        Point2D pLast = points.get(n - 2);
        Point2D cLast = control.get(2 * (n - 2) - 1);
        Point2D end = points.get(n - 1);
        double t1 = 1 - tailRatio(end, cLast, TAIL_LENGTH + targetCrop);
        Point2D tailStart = quadPoint(pLast, cLast, end, t1);
        Point2D tailControl = between(pLast, cLast, t1);
        Point2D tailEnd = shorten(end, tailStart, targetCrop);
        result.quadTo(tailControl.getX(), tailControl.getY(), tailStart.getX(), tailStart.getY());
        result.lineTo(tailEnd.getX(), tailEnd.getY());
        return result;
    }

    /**
     * Drops the bends that lie inside (or on the border of) the end nodes: yFiles crops
     * the path at the node outlines, and a bend on the outline leaves it a segment of no
     * length to crop, from which it computes absurd coordinates. Layered layouts put
     * bends exactly on the node borders.
     */
    private List<Point2D> trimInsideEnds(List<Point2D> points) {
        if (points.size() < 3) {
            return points;
        }
        IEdge edge = getEdge();
        RectD source = edge.getSourceNode().getLayout().toRectD().getEnlarged(BORDER_TOLERANCE);
        RectD target = edge.getTargetNode().getLayout().toRectD().getEnlarged(BORDER_TOLERANCE);
        List<Point2D> result = new ArrayList<>(points);
        while (result.size() > 2 && source.contains(toPointD(result.get(1)))) {
            result.remove(1);
        }
        while (result.size() > 2 && target.contains(toPointD(result.get(result.size() - 2)))) {
            result.remove(result.size() - 2);
        }
        return result;
    }

    /** Returns a given AWT point as a yFiles point. */
    private static PointD toPointD(Point2D point) {
        return new PointD(point.getX(), point.getY());
    }

    /** Distance from a node border within which a bend counts as on the border. */
    private static final double BORDER_TOLERANCE = 0.5;

    /** Returns the points without coinciding neighbours (closer than {@link #MIN_DISTANCE}). */
    private static List<Point2D> distinct(List<Point2D> points) {
        List<Point2D> result = new ArrayList<>(points.size());
        for (Point2D point : points) {
            if (result.isEmpty() || result.get(result.size() - 1).distance(point) >= MIN_DISTANCE) {
                result.add(point);
            }
        }
        return result;
    }

    /** Distance below which neighbouring path points are considered to coincide. */
    private static final double MIN_DISTANCE = 1e-3;

    /** Length of the straight pieces at the ends of a curve. */
    private static final double TAIL_LENGTH = 2;

    /**
     * Returns the fraction of a quadratic curve segment, at the end whose control point
     * is given, that is replaced by a straight piece of a given length.
     */
    private static double tailRatio(Point2D end, Point2D control, double length) {
        double distance = end.distance(control);
        // the speed of a quadratic curve at its end is twice the distance to the control point
        return distance == 0
            ? 0
            : Math.min(0.5, length / (2 * distance));
    }

    /** Returns the point at a given ratio on a quadratic curve. */
    private static Point2D quadPoint(Point2D p0, Point2D c, Point2D p1, double t) {
        double u = 1 - t;
        return new Point2D.Double(u * u * p0.getX() + 2 * u * t * c.getX() + t * t * p1.getX(),
            u * u * p0.getY() + 2 * u * t * c.getY() + t * t * p1.getY());
    }

    /** Returns the point at a given ratio on the line between two points. */
    private static Point2D between(Point2D p0, Point2D p1, double t) {
        return new Point2D.Double(p0.getX() + t * (p1.getX() - p0.getX()),
            p0.getY() + t * (p1.getY() - p0.getY()));
    }

    @Override
    @NonNullByDefault({})
    public IVisual createVisual(IRenderContext context) {
        IVisual base = super.createVisual(context);
        EdgeDecoration decoration = new EdgeDecoration();
        if (!decoration.update(this)) {
            return base;
        }
        VisualGroup result = new VisualGroup();
        result.add(base);
        result.add(decoration);
        return result;
    }

    @Override
    @NonNullByDefault({})
    public IVisual updateVisual(IRenderContext context, IVisual oldVisual) {
        if (oldVisual instanceof VisualGroup group && group.getChildren().size() == 2
            && group.getChildren().get(1) instanceof EdgeDecoration decoration) {
            IVisual oldBase = group.getChildren().get(0);
            IVisual base = super.updateVisual(context, oldBase);
            if (base != oldBase) {
                group.getChildren().set(0, base);
            }
            decoration.update(this);
            return group;
        }
        if (oldVisual instanceof VisualGroup) {
            return createVisual(context);
        }
        IVisual base = super.updateVisual(context, oldVisual);
        EdgeDecoration decoration = new EdgeDecoration();
        if (!decoration.update(this)) {
            return base;
        }
        VisualGroup result = new VisualGroup();
        result.add(base);
        result.add(decoration);
        return result;
    }

    /**
     * The decorations painted over the edge path: the shaft of a dashed edge (see
     * {@link CellStyles#pen}), the inner line, the diagnostics overlay (along the path
     * and over the main label) and the emphasis of a selected edge. Refreshed from the
     * renderer's configured edge on every update.
     */
    private static final class EdgeDecoration implements IVisual {
        /**
         * Refreshes the decoration from the configured edge of a renderer.
         * @return {@code true} if there is anything to paint
         */
        boolean update(CellEdgeStyleRenderer renderer) {
            var cell = renderer.cell();
            if (cell == null) {
                this.innerLine = null;
                this.overlay = null;
                this.emphasised = false;
                return false;
            }
            VisualMap visuals = cell.getVisuals();
            float[] dash = visuals.getDash();
            this.dash = CellStyles.isDashed(dash)
                ? dash
                : null;
            this.foreground = visuals.getForeground();
            this.strokeWidth = CellStyles.strokeWidth(visuals);
            this.innerLine = visuals.getInnerLine();
            this.overlay = Values.getSeverityOverlay(visuals.getErrorSeverity());
            this.emphasised = visuals.isEmphasised();
            this.lineWidth = visuals.getLineWidth();
            boolean result = this.dash != null || this.innerLine != null || this.overlay != null
                || this.emphasised;
            if (result) {
                GeneralPath path = renderer.getPath();
                if (path == null) {
                    this.path = null;
                } else {
                    // the decorations follow the smoothed path of a spline edge
                    double smoothing = renderer.getSmoothingLength();
                    if (smoothing > 0) {
                        path = path
                            .createSmoothedPath(smoothing, renderer.getSmoothingPolicy(),
                                                renderer.isUsingStraightEndsForSmoothingEnabled());
                    }
                    Path2D shape = new Path2D.Double();
                    path.updatePath(shape, null);
                    this.path = shape;
                }
                var labels = renderer.getEdge().getLabels();
                this.labelBounds = labels.size() == 0
                    ? null
                    : labels.getItem(0).getLayout().getBounds().toRectangle2D();
            }
            return result;
        }

        @Override
        @NonNullByDefault({})
        public void paint(IRenderContext context, Graphics2D g) {
            Path2D path = this.path;
            if (path == null) {
                return;
            }
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2
                    .setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                      RenderingHints.VALUE_ANTIALIAS_ON);
                float[] dash = this.dash;
                if (dash != null) {
                    // the shaft of a dashed edge, which the pen leaves transparent
                    g2.setColor(this.foreground);
                    g2.setStroke(CellStyles.stroke(this.strokeWidth, dash));
                    g2.draw(path);
                }
                Color innerLine = this.innerLine;
                if (innerLine != null) {
                    g2.setColor(innerLine);
                    g2.setStroke(CellStyles.stroke(CellStyles.INNER_LINE_WIDTH, null));
                    g2.draw(path);
                }
                Color overlay = this.overlay;
                if (overlay != null) {
                    g2.setColor(overlay);
                    g2
                        .setStroke(CellStyles
                            .stroke(this.lineWidth + CellStyles.EXTRA_BORDER_SPACE, null));
                    g2.draw(path);
                    Rectangle2D labelBounds = this.labelBounds;
                    if (labelBounds != null) {
                        g2
                            .fill(new Rectangle2D.Double(labelBounds.getX() - 1,
                                labelBounds.getY() - 1, labelBounds.getWidth() + 2,
                                labelBounds.getHeight() + 2));
                    }
                }
                if (this.emphasised) {
                    g2.setColor(CellStyles.SELECTION_COLOR);
                    g2.setStroke(CellStyles.SELECTION_STROKE);
                    g2.draw(path);
                }
            } finally {
                g2.dispose();
            }
        }

        /** The smoothed path the decorations follow; {@code null} if there is nothing to paint. */
        private @Nullable Path2D path;
        /** The bounds of the main label, covered by the overlay; {@code null} if there is none. */
        private @Nullable Rectangle2D labelBounds;
        /** The dash pattern of a dashed edge; {@code null} if the edge is solid. */
        private float @Nullable [] dash;
        /** The colour of the shaft of a dashed edge. */
        private Color foreground = Color.BLACK;
        /** The stroke width of the shaft of a dashed edge. */
        private float strokeWidth = 1f;
        /** The colour of the inner line; {@code null} if the edge has none. */
        private @Nullable Color innerLine;
        /** The colour of the diagnostics overlay; {@code null} if the edge has no error. */
        private @Nullable Color overlay;
        /** If {@code true}, the edge is emphasised as part of the selection. */
        private boolean emphasised;
        /** The line width of the edge, which the overlay extends beyond. */
        private float lineWidth = 1f;
    }
}
