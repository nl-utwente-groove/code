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
 * $Id: AbstractLayouter.java,v 1.6 2008-01-30 09:33:01 iovka Exp $
 */
package groove.gui.layout;

import groove.gui.jgraph.JCell;
import groove.gui.jgraph.JEdgeView;
import groove.gui.jgraph.JGraph;
import groove.gui.jgraph.JModel;
import groove.gui.look.VisualMap;
import groove.util.Pair;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.SwingUtilities;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.CellView;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.VertexView;

/**
 * An abstract class for layout actions.
 * @author Arend Rensink
 * @version $Revision$
 */
abstract public class AbstractLayouter implements Layouter {
    /**
     * Interface of an item that is to be layed out. This is to unify node
     * bounds and edge points.
     */
    static protected interface Layoutable {
        /** Returns the x-coordinate of this layoutable. */
        public double getX();

        /** Returns the y-coordinate of this layoutable. */
        public double getY();

        /** Returns the height of this layoutable. */
        public double getHeight();

        /** Returns the width of this layoutable. */
        public double getWidth();

        /** Sets a new position of this layoutable. */
        public void setLocation(double x, double y);
    }

    /**
     * Implements a layoutable that wraps a point. Width and height are zero.
     */
    static final protected class PointLayoutable implements Layoutable {
        /** Constructs a new layoutable from a given point. */
        public PointLayoutable(Point2D p) {
            this.p = p;
        }

        public double getX() {
            return this.p.getX();
        }

        public double getY() {
            return this.p.getY();
        }

        public double getWidth() {
            return 0;
        }

        public double getHeight() {
            return 0;
        }

        public void setLocation(double x, double y) {
            this.p.setLocation(x, y);

        }

        @Override
        public String toString() {
            return "PointLayoutable[x=" + getX() + ",y=" + getY() + "]";
        }

        /** The internally stored point of this layoutable. */
        private final Point2D p;
    }

    /**
     * Implements a layoutable that wraps a rectangle.
     */
    static final protected class VertexLayoutable implements Layoutable {
        /** Constructs a new layoutable from a given vertex. */
        public VertexLayoutable(VertexView view) {
            this.r = view.getBounds();
        }

        public double getX() {
            return this.r.getX();
        }

        public double getY() {
            return this.r.getY();
        }

        public double getWidth() {
            return this.r.getWidth();
        }

        public double getHeight() {
            return this.r.getHeight();
        }

        public void setLocation(double x, double y) {
            this.r.setRect(x, y, getWidth(), getHeight());

        }

        @Override
        public String toString() {
            return "VertexLayoutable[x=" + getX() + ",y=" + getY() + ",width="
                + getWidth() + ",height=" + getHeight() + "]";
        }

        /** The internally stored bounds of this layoutable. */
        private final Rectangle2D r;
    }

    /**
     * Constructor to create a dummy, prototype layout action. Proper layout
     * actions are created using <tt>newInstance(MyJGraph)</tt>
     * @see #newInstance(JGraph)
     */
    protected AbstractLayouter(String name) {
        this(name, null);
    }

    /**
     * Constructor to create a dummy, prototype layout action. Proper layout
     * actions are created using <tt>newInstance(MyJGraph)</tt>
     * @see #newInstance(JGraph)
     */
    protected AbstractLayouter(String name, JGraph<?> jgraph) {
        this.name = name;
        this.jgraph = jgraph;
    }

    /**
     * Returns the name stored for this action.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Prepares the actual layout process by calculating the information from
     * the current <tt>jmodel</tt>. This implementation calculates the
     * <tt>toLayoutableMap</tt>, and sets the line style to that preferred by
     * the layouter.
     */
    protected void prepare() {
        this.jgraph.notifyProgress("Layouting");
        this.jmodel = this.jgraph.getModel();
        // clear the transient information
        this.toLayoutableMap.clear();
        this.immovableSet.clear();
        // iterate over the cell views
        CellView[] cellViews = this.jgraph.getGraphLayoutCache().getRoots();
        for (CellView cellView : cellViews) {
            if (!(cellView.getCell() instanceof JCell)) {
                continue;
            }
            JCell<?> jCell = (JCell<?>) cellView.getCell();
            if (jCell.isGrayedOut()) {
                continue;
            }
            if (cellView instanceof JEdgeView) {
                // all true points (i.e., that are not PortViews) are
                // subject to layouting
                List<Point2D> points =
                    ((JEdgeView) cellView).getCell().getVisuals().getPoints();
                for (int p = 1; p < points.size() - 1; p++) {
                    Object point = points.get(p);
                    if (point instanceof Point2D) {
                        Layoutable layoutable =
                            new PointLayoutable((Point2D) point);
                        this.toLayoutableMap.put(Pair.newPair(jCell, p),
                            layoutable);
                        if (!jCell.isLayoutable()) {
                            this.immovableSet.add(layoutable);
                        }
                    }
                }
            } else {
                assert cellView instanceof VertexView : String.format(
                    "%s instance of %s", cellView, cellView.getClass());
                Layoutable layoutable =
                    new VertexLayoutable((VertexView) cellView);
                this.toLayoutableMap.put(jCell, layoutable);
                if (!jCell.isLayoutable()) {
                    this.immovableSet.add(layoutable);
                }
            }
        }
        this.jgraph.setToolTipEnabled(false);
    }

    /**
     * Finalizes the layouting, by performing an edit on the model that records
     * the node bounds and edge points.
     */
    protected void finish() {
        final Map<JCell<?>,AttributeMap> change =
            new HashMap<JCell<?>,AttributeMap>();
        CellView[] cellViews = this.jgraph.getGraphLayoutCache().getRoots();
        for (CellView view : cellViews) {
            if (view instanceof VertexView || view instanceof EdgeView) {
                JCell<?> cell = (JCell<?>) view.getCell();
                VisualMap visuals = new VisualMap();
                if (view instanceof VertexView) {
                    // store the bounds back into the model
                    Rectangle2D bounds = ((VertexView) view).getCachedBounds();
                    visuals.setNodePos(new Point2D.Double(bounds.getCenterX(),
                        bounds.getCenterY()));
                } else {
                    // store the points back into the model
                    List<?> points =
                        ((JEdgeView) view).getCell().getVisuals().getPoints();
                    if (points != null) {
                        List<Point2D> newPoints =
                            new ArrayList<Point2D>(points.size());
                        for (Object p : points) {
                            if (p instanceof CellView) {
                                Rectangle2D bounds = ((CellView) p).getBounds();
                                Point2D point = new Point2D.Double();
                                point.setLocation(bounds.getCenterX(),
                                    bounds.getCenterY());
                                newPoints.add(point);
                            } else {
                                newPoints.add((Point2D) p);
                            }
                        }
                        visuals.setPoints(newPoints);
                    }
                }
                change.put(cell, visuals.getAttributes());
                cell.setLayoutable(false);
            }
        }
        // do the following in the event dispatch thread
        Runnable edit = new Runnable() {
            public void run() {
                if (change.size() != 0) {
                    AbstractLayouter.this.jmodel.edit(change, null, null, null);
                    // taking out the refresh as probably superfluous and 
                    // certainly performance impacting
                    //                    AbstractLayouter.this.jgraph.refresh();
                }
                AbstractLayouter.this.jgraph.notifyProgress("");
            }
        };
        // do this now (if invoked from the event thread) or defer to event thread
        if (SwingUtilities.isEventDispatchThread()) {
            edit.run();
        } else {
            SwingUtilities.invokeLater(edit);
        }
    }

    /**
     * The name of this layout action
     */
    private final String name;

    /**
     * The underlying jgraph for this layout action.
     */
    protected final JGraph<?> jgraph;

    /**
     * The model that has last been layed out.
     */
    protected JModel<?> jmodel;

    /**
     * A map from the vertex cells and pairs of edge cells/point indices to layoutables.
     * This is a transient
     * value, computed by <tt>prepare()</tt>. The layoutable for a cell
     * contains that cell's bounds; the layoutable for an edge point contains
     * the point.
     */
    protected final Map<Object,Layoutable> toLayoutableMap =
        new LinkedHashMap<Object,Layoutable>();

    /**
     * The subset of layoutables that should be immovable, according to the
     * movability attribute of the corresponding jcell. Initialised in
     * <tt>prepare()</tt>
     */
    protected final Set<Layoutable> immovableSet = new HashSet<Layoutable>();
}
