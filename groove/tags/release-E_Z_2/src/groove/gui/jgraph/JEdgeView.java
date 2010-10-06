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
 * $Id: JEdgeView.java,v 1.10 2008-01-30 09:33:13 iovka Exp $
 */
package groove.gui.jgraph;

import groove.gui.Options;
import groove.gui.jgraph.JAttr.AttributeMap;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.jgraph.graph.CellHandle;
import org.jgraph.graph.CellMapper;
import org.jgraph.graph.CellView;
import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.EdgeRenderer;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.GraphCellEditor;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphContext;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.PortView;

/**
 * An edge view that uses the <tt>getText()</tt> of the underlying edge as a
 * label. Moreover, new views take care to bend to avoid overlap, and offer
 * functionality to add and remove points.
 * @author Arend Rensink
 * @version $Revision$
 */
public class JEdgeView extends EdgeView {

    /** The editor for all instances of <tt>JEdgeView</tt>. */
    static protected final MultiLinedEditor editor = new MultiLinedEditor();

    /**
     * Apart from constructing a new edge view, adds points to the edge if it is
     * a self-edge or if a straight parallel edge already exists. Queries the
     * underlying model for edge attributes. (using
     * <tt>@link JModel#createEdgeAttributes</tt>).
     */
    public JEdgeView(JEdge jEdge, JGraph jGraph) {
        super(jEdge);
        this.jModel = jGraph.getModel();
        // first we add points and change the linestyle of the edge
        // in the model attributes, if the new edge demands it
        // AttributeMap jEdgeAttr = jEdge.getAttributes();
        // jEdgeAttr.applyMap(jModel.createJEdgeAttr(jEdge));
    }

    @Override
    public String toString() {
        return this.cell.toString();
    }

    @Override
    public CellViewRenderer getRenderer() {
        return super.getRenderer();
    }

    /**
     * This implementation returns the (static) {@link MultiLinedEditor}.
     */
    @Override
    public GraphCellEditor getEditor() {
        return editor;
    }

    /**
     * Specialises the return type.
     */
    @Override
    public JEdge getCell() {
        return (JEdge) super.getCell();
    }

    /**
     * Does some routing of self-edges and overlapping edges.
     */
    @Override
    public void refresh(GraphLayoutCache cache, CellMapper mapper,
            boolean createDependentViews) {
        super.refresh(cache, mapper, createDependentViews);
        // target could be null, if we are dealing with a temporary cache that just
        // contains a mapping for the edge
        if (this.target != null) {
            if (this.source == this.target) {
                routeSelfEdge();
            } else if (getPointCount() <= 2) {
                routeParallelEdge(mapper);
            }
        }
    }

    /**
     * Adds a point between the first and second points of the underlying
     * j-edge. The point is offset from the straight edge between the current
     * first and second points. Does not update the view; this is to be done by
     * the client. Also adds the edge to the layoutables of the model.
     * @return a copy of the points of the underlying j-edge with a point added
     */
    public List<Object> addPointBetween() {
        this.jModel.addLayoutable(getCell());
        List<Object> points = new LinkedList<Object>(getViewPoints());
        points.add(1, createPointBetween(toPoint(points.get(0)),
            toPoint(points.get(1))));
        return points;
    }

    /**
     * Adds a point at a given location to the underlying j-edge. The point is
     * added between those two existing (adjacent) edge points for which the sum
     * of the distances to the specified location is minimal. If the location is
     * <tt>null</tt>,{@link #addPointBetween()}is invoked instead. Does not
     * update the view; this is to be done by the client.
     * @param location the location at which the new point should appear; if
     *        <tt>null</tt>, a point is added at random
     * @return a copy of the points of the underlying j-edge with a point added
     */
    public List<Object> addPointAt(Point2D location) {
        if (location == null) {
            return addPointBetween();
        } else {
            List<Object> points = new LinkedList<Object>(getViewPoints());
            double closestDistance = Double.MAX_VALUE;
            int closestIndex = 0;
            for (int i = 1; i < points.size(); i++) {
                double distance =
                    location.distance(toPoint(points.get(i - 1)))
                        + location.distance(toPoint(points.get(i)));
                if (distance < closestDistance) {
                    closestIndex = i;
                    closestDistance = distance;
                }
            }
            if (closestIndex > 0) {
                points.add(closestIndex, location.clone());
            }
            return points;
        }
    }

    /**
     * Removes the first intermediate point from the underlying j-edge. Has no
     * effect if the j-edge had only two points to start with, or if it is a
     * self-edge with no more than three points. Does not update the view; this
     * is to be done by the client.
     * @return a copy of the points of the underlying j-edge with the first
     *         point removed
     */
    public List<Object> removePoint() {
        List<Object> points = new LinkedList<Object>(getViewPoints());
        if (points.size() > 2
            && (getSource() != getTarget() || points.size() > 3)) {
            points.remove(1);
        }
        return points;
    }

    /**
     * Removes the intermediate point from the underlying j-edge that is closest
     * to a given location. Has no effect if the j-edge had only two points to
     * start with, or if it is a self-edge with no more than three points. If
     * the location is <tt>null</tt>, {@link #removePoint()}is invoked
     * instead. Does not update the view; this is to be done by the client.
     * @param location the location at which the point to be removed is sought;
     *        if <tt>null</tt>, the first available point is removed
     * @return a copy of the points of the underlying j-edge, possibly with a
     *         point removed
     */
    public List<Object> removePointAt(Point2D location) {
        if (location == null) {
            return removePoint();
        } else {
            List<Object> points = new LinkedList<Object>(getViewPoints());
            if (points.size() > 2
                && (getSource() != getTarget() || points.size() > 3)) {
                double closestDistance = Double.MAX_VALUE;
                int closestIndex = 0;
                for (int i = 1; i < points.size() - 1; i++) {
                    double distance = location.distance(toPoint(points.get(i)));
                    if (distance < closestDistance) {
                        closestIndex = i;
                        closestDistance = distance;
                    }
                }
                if (closestIndex > 0) {
                    points.remove(closestIndex);
                }
            }
            return points;
        }
    }

    /**
     * Overrides the method to return a {@link MyEdgeHandle}.
     */
    @Override
    public CellHandle getHandle(GraphContext context) {
        return new MyEdgeHandle(this, context);
    }

    /**
     * Convenience method to return the points of the view as an instantiate
     * list.
     */
    @SuppressWarnings("unchecked")
    public final List<Object> getViewPoints() {
        return this.points;
    }

    /**
     * If we're doing this for the target point and the nearest point is the
     * source, take the corrected source point.
     */
    @Override
    protected Point2D getNearestPoint(boolean source) {
        if (getPointCount() == 2) {
            if (!source && this.source instanceof PortView) {
                JVertexView sourceCellView =
                    (JVertexView) ((PortView) this.source).getParentView();
                return sourceCellView.getPerimeterPoint(this, null,
                    getCenterPoint(this.target));
            }
        }
        return super.getNearestPoint(source);
    }

    /**
     * The vector is from the first to the second point.
     */
    @Override
    public Point2D getLabelVector() {
        Point2D p0 = getPoint(0);
        Point2D p1 = getPoint(1);
        if (JAttr.isManhattanStyle(getAllAttributes())
            && p1.getX() != p0.getX()) {
            p1 = new Point2D.Double(p1.getX(), p0.getY());
        }
        double dx = p1.getX() - p0.getX();
        double dy = p1.getY() - p0.getY();
        return new Point2D.Double(dx, dy);
    }

    /**
     * Adds points to the view and sets the line style so that the edge makes a
     * nice curve. The points are created perpendicular to the line between the
     * first and second point when the method is invoked, also taking the vertex
     * bound into account. All but the first and last points of the original
     * points are removed. Should only be called if
     * <code>getSource() == getTarget()</code>.
     */
    protected void routeSelfEdge() {
        int lineStyle = GraphConstants.getLineStyle(getAllAttributes());
        boolean isManhattan = lineStyle == JAttr.STYLE_MANHATTAN;
        if (isManhattan ? getPointCount() == 2 : getPointCount() <= 3) {
            List<Object> points = getViewPoints();
            Point2D startPoint = toPoint(points.get(0));
            Point2D endPoint = toPoint(points.get(1));
            while (points.size() > 2) {
                points.remove(1);
            }
            Rectangle2D vertexBounds = this.source.getParentView().getBounds();
            if (vertexBounds.contains(endPoint)) {
                endPoint.setLocation(endPoint.getX() + vertexBounds.getWidth()
                    * 2, endPoint.getY());
            }
            points.add(1, createPointPerpendicular(startPoint, endPoint, true));
            if (!isManhattan) {
                points.add(1, createPointPerpendicular(startPoint, endPoint,
                    false));
                setLineStyle(getPreferredLinestyle());
            }
            GraphConstants.setPoints(getCell().getAttributes(), points);
        }
    }

    /**
     * Tests if the edge has parallel edges and, if so, adds a point to this
     * edge so it can be routed around.
     */
    protected void routeParallelEdge(CellMapper mapper) {
        // look for parallel edges; if one exists, make this one bend
        int parallelEdgeCount = 0;
        Iterator<?> edgeIter = ((DefaultPort) this.source.getCell()).edges();
        while (parallelEdgeCount <= 1 && edgeIter.hasNext()) {
            EdgeView otherView =
                (EdgeView) mapper.getMapping(edgeIter.next(), false);
            if (otherView != null
                && otherView.getPointCount() <= 2
                && (otherView.getSource() == this.target || otherView.getTarget() == this.target)) {
                parallelEdgeCount++;
            }
        }
        if (parallelEdgeCount > 1) {
            List<Object> points = getViewPoints();
            assert points.size() > 1 : String.format(
                "JEdge %s has only points %s", getCell(), points);
            points.add(1, createPointBetween(toPoint(points.get(0)),
                toPoint(points.get(1))));
            GraphConstants.setPoints(getCell().getAttributes(), points);
            setLineStyle(getPreferredLinestyle());
        }
    }

    /**
     * Creates an returns a point halfway two given points, with a random effect
     * @param p1 the first boundary point
     * @param p2 the first boundary point
     * @return new point on the perpendicular of the line between <tt>p1</tt>
     *         and <tt>p2</tt>
     */
    protected Point createPointBetween(Point2D p1, Point2D p2) {
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
            double xDelta = p1.getX() - p2.getX();
            double yDelta = p1.getY() - p2.getY();
            x = midX + (int) (offset * yDelta / distance);
            y = midY - (int) (offset * xDelta / distance);
        }
        return new Point(Math.max(x, 0), Math.max(y, 0));
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
    protected Point createPointPerpendicular(Point2D p1, Point2D p2,
            boolean left) {
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
     * Callback method to determine the line style for edges that have points
     * added automaticalled.
     * @return This method always returns {@link GraphConstants#STYLE_BEZIER}.
     */
    protected int getPreferredLinestyle() {
        return GraphConstants.STYLE_BEZIER;
    }

    /**
     * Sets the line style of the view and the model edge.
     */
    protected void setLineStyle(int linestyle) {
        GraphConstants.setLineStyle(getAllAttributes(), linestyle);
        GraphConstants.setLineStyle(getCell().getAttributes(), linestyle);
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

    /** The j-model underlying this edge view. */
    private final JModel jModel;
    //
    // /** Internally stored bounds. */
    //    
    // private Rectangle2D bounds;
    static {
        renderer = new MyEdgeRenderer();
    }

    /**
     * This class is overridden to get the same port emphasis.
     */
    static public class MyEdgeHandle extends EdgeHandle {
        /** Constructs an instance. */
        public MyEdgeHandle(EdgeView edge, GraphContext ctx) {
            super(edge, ctx);
        }

        @Override
        public void mousePressed(MouseEvent evt) {
            if (!Options.isPointEditEvent(evt)) {
                super.mousePressed(evt);
            }
        }

        @Override
        public void mouseReleased(MouseEvent evt) {
            if (!Options.isPointEditEvent(evt)) {
                super.mouseReleased(evt);
            }
        }

        @Override
        public boolean isAddPointEvent(MouseEvent event) {
            return Options.isPointEditEvent(event)
                && super.isAddPointEvent(event);
        }

        @Override
        public boolean isRemovePointEvent(MouseEvent event) {
            return Options.isPointEditEvent(event)
                && super.isRemovePointEvent(event);
        }

        /**
         * Sets the target port of the view to the source port if the target
         * port is currently <tt>null</tt>, and then invokes the super
         * method.
         */
        @Override
        protected ConnectionSet createConnectionSet(EdgeView view,
                boolean verbose) {
            if (view.getTarget() == null) {
                @SuppressWarnings("unchecked")
                List<Object> points = view.getPoints();
                points.add(points.get(points.size() - 1));
                view.setTarget(view.getSource());
            }
            return super.createConnectionSet(view, verbose);
        }

        /**
         * Delegates to {@link JVertexView#paintArmed(Graphics)} if the port's
         * parent view is a {@link JVertexView}; otherwise invokes the super
         * method.
         */
        @Override
        protected void paintPort(Graphics g, CellView p) {
            if (p.getParentView() instanceof JVertexView
                && this.graph instanceof EditorJGraph) {
                ((JVertexView) p.getParentView()).paintArmed(g);
            } else {
                super.paintPort(g, p);
            }
        }
    }

    /** Renderer subclass to enable our special line style. */
    static public class MyEdgeRenderer extends EdgeRenderer {
        @Override
        public Component getRendererComponent(org.jgraph.JGraph jGraph,
                CellView view, boolean sel, boolean focus, boolean preview) {

            assert view instanceof JEdgeView : String.format(
                "This renderer is only meant for %s", JVertexView.class);

            JEdgeView theView = (JEdgeView) view;
            AttributeMap secondMap =
                (AttributeMap) theView.getAllAttributes().get("line2map");
            if (secondMap != null) {
                this.twoLines = true;
                this.line2color = GraphConstants.getLineColor(secondMap);
                this.line2width = GraphConstants.getLineWidth(secondMap);
                this.line2dash = GraphConstants.getDashPattern(secondMap);
                this.line2map = secondMap;
            } else {
                this.twoLines = false;
            }
            this.error =
                ((JGraph) jGraph).getModel().hasError((JCell) view.getCell());
            if (this.error) {
                this.errorBounds =
                    getLabelBounds(jGraph, (EdgeView) view).getBounds();
            }
            return super.getRendererComponent(jGraph, view, sel, focus, preview);
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);

            if (this.twoLines) {
                // draw the second line
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(this.line2color);
                g2.setStroke(JAttr.createStroke(this.line2width, this.line2dash));
                g2.draw(this.view.lineShape);
                g2.fill(this.view.endShape);
                g2.draw(this.view.endShape);

                // write text again

                g2.setStroke(new BasicStroke(1));
                g.setFont(GraphConstants.getFont(this.line2map));

                JGraph graph = (JGraph) this.graph.get();
                if (graph.getEditingCell() != this.view.getCell()) {
                    Object label = graph.convertValueToString(this.view);
                    if (label != null) {
                        paintLabel(g, label.toString(),
                            getLabelPosition(this.view), true);
                    }
                }
            }
            if (this.error) {
                g.setColor(JAttr.ERROR_COLOR);
                g.fillRect(this.errorBounds.x, this.errorBounds.y,
                    this.errorBounds.width, this.errorBounds.height);
            }
        }

        /**
         * Overrides the method to take {@link JAttr#STYLE_MANHATTAN} into
         * account.
         */
        @Override
        protected Shape createShape() {
            if (JAttr.isManhattanStyle(this.view.getAllAttributes())) {
                return createManhattanShape();
            } else {
                return super.createShape();
            }
        }

        /** Creates a shape for the {@link JAttr#STYLE_MANHATTAN} line style. */
        protected Shape createManhattanShape() {
            int n = this.view.getPointCount();
            if (n > 1) {
                // Following block may modify static vars as side effect
                // (Flyweight Design)
                EdgeView tmp = this.view;
                Point2D[] p = null;
                p = new Point2D[n];
                for (int i = 0; i < n; i++) {
                    Point2D pt = tmp.getPoint(i);
                    if (pt == null) {
                        return null; // exit
                    }
                    p[i] = new Point2D.Double(pt.getX(), pt.getY());
                }

                // End of Side-Effect Block
                // Undo Possible MT-Side Effects
                if (this.view != tmp) {
                    this.view = tmp;
                    installAttributes(this.view);
                }
                // End of Undo
                if (this.view.sharedPath == null) {
                    this.view.sharedPath =
                        new GeneralPath(Path2D.WIND_NON_ZERO, n);
                } else {
                    this.view.sharedPath.reset();
                }
                this.view.beginShape =
                    this.view.lineShape = this.view.endShape = null;
                // first point
                Point2D p0 = p[0];
                // last point
                Point2D pe = p0;
                // second point
                Point2D p1 = null;
                // last point but one
                Point2D p2 = null;
                this.view.sharedPath.moveTo((float) p0.getX(),
                    (float) p0.getY());
                for (int i = 1; i < n; i++) {
                    // first move horizontally,
                    float x = (float) p[i].getX();
                    float y = (float) p[i - 1].getY();
                    this.view.sharedPath.lineTo(x, y);
                    p2 = pe;
                    pe = new Point2D.Float(x, y);
                    if (p1 == null) {
                        p1 = pe;
                    }
                    // then move vertically, if needed
                    if (p[i].getY() != y) {
                        y = (float) p[i].getY();
                        this.view.sharedPath.lineTo(x, y);
                        p2 = pe;
                        pe = new Point2D.Float(x, y);
                    }
                }
                if (this.beginDeco != GraphConstants.ARROW_NONE) {
                    this.view.beginShape =
                        createLineEnd(this.beginSize, this.beginDeco, p1, p0);
                }
                if (this.endDeco != GraphConstants.ARROW_NONE) {
                    this.view.endShape =
                        createLineEnd(this.endSize, this.endDeco, p2, pe);
                }
                if (this.view.endShape == null && this.view.beginShape == null) {
                    // With no end decorations the line shape is the same as the
                    // shared path and memory
                    this.view.lineShape = this.view.sharedPath;
                } else {
                    this.view.lineShape =
                        (GeneralPath) this.view.sharedPath.clone();
                    if (this.view.endShape != null) {
                        this.view.sharedPath.append(this.view.endShape, true);
                    }
                    if (this.view.beginShape != null) {
                        this.view.sharedPath.append(this.view.beginShape, true);
                    }
                }
                return this.view.sharedPath;
            }
            return null;
        }

        // properties for drawing a second line
        private boolean twoLines = false;
        private Color line2color;
        private float line2width;
        private float[] line2dash;
        private AttributeMap line2map;
        /** Flag indicating that the underlying edge has an error. */
        private boolean error;
        private Rectangle errorBounds;
    }
}
