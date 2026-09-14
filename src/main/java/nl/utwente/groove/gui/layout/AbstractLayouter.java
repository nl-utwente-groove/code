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
package nl.utwente.groove.gui.layout;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.gui.look.VisualMap;
import nl.utwente.groove.gui.view.GraphCanvas;
import nl.utwente.groove.gui.view.ViewCell;
import nl.utwente.groove.gui.view.ViewEdge;
import nl.utwente.groove.gui.view.ViewVertex;

/**
 * An abstract class for layout actions, working on the visuals of the cells
 * of a graph canvas and committing the result through the canvas' edit funnel.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
abstract public class AbstractLayouter implements Layouter {
    /**
     * Constructor to create a dummy, prototype layout action. Proper layout
     * actions are created using <tt>newInstance(GraphCanvas)</tt>
     * @see #newInstance(GraphCanvas)
     */
    protected AbstractLayouter(String name) {
        this(name, null);
    }

    /**
     * Constructor to create a layout action for a given canvas; or a prototype
     * if the canvas is {@code null}.
     * @see #newInstance(GraphCanvas)
     */
    protected AbstractLayouter(String name, @Nullable GraphCanvas<?> canvas) {
        this.name = name;
        this.canvas = canvas;
    }

    /**
     * Returns the name stored for this action.
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * The name of this layout action
     */
    private final String name;

    /**
     * Prepares the actual layout process by calculating the information from
     * the current canvas. This implementation calculates the
     * <tt>layoutMap</tt> of the visible, non-grayed-out vertices.
     * @param recordImmovables if {@code true}, the shift in position of the immovables
     * is recorded
     */
    protected void prepare(boolean recordImmovables) {
        var canvas = getCanvas();
        canvas.setLayouting(true);
        canvas.setToolTipEnabled(false);
        // edge points are cleared when layout is stored back into view
        // copy the old layout map
        Map<ViewVertex<?>,LayoutNode> oldLayoutMap = new LinkedHashMap<>(this.layoutMap);
        // clear the transient information
        this.layoutMap.clear();
        this.immovableMap.clear();
        // iterate over the vertices
        for (ViewCell<?> cell : canvas.getCells()) {
            if (!(cell instanceof ViewVertex<?> vertex)) {
                continue;
            }
            if (vertex.isGrayedOut() || !vertex.getVisuals().isVisible()) {
                continue;
            }
            LayoutNode layout = new LayoutNode(vertex, getBounds(vertex));
            if (!vertex.isLayoutable()) {
                Point2D shift;
                if (recordImmovables) {
                    shift = new Point2D.Double();
                    LayoutNode oldLayout = oldLayoutMap.get(vertex);
                    if (oldLayout != null) {
                        double x = layout.getX() - oldLayout.getX();
                        double y = layout.getY() - oldLayout.getY();
                        shift = new Point2D.Double(x, y);
                    }
                } else {
                    shift = null;
                }
                this.immovableMap.put(vertex, shift);
            }
            this.layoutMap.put(vertex, layout);
        }
    }

    /**
     * Returns the current bounds of a vertex: its position is the centre
     * stored in the visuals, its size the canvas' preferred size for it.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private Rectangle2D getBounds(ViewVertex<?> vertex) {
        Point2D pos = vertex.getVisuals().getNodePos();
        Dimension2D size = ((GraphCanvas) getCanvas()).getPreferredSize(vertex);
        return new Rectangle2D.Double(pos.getX() - size.getWidth() / 2,
            pos.getY() - size.getHeight() / 2, size.getWidth(), size.getHeight());
    }

    /**
     * Finalises the layouting, by performing an edit on the canvas that records
     * the node positions and clears the edge points.
     */
    protected void finish() {
        final Map<ViewCell<?>,VisualMap> change = new HashMap<>();
        for (LayoutNode layout : this.layoutMap.values()) {
            VisualMap visuals = new VisualMap();
            ViewVertex<?> vertex = layout.getVertex();
            // store the position back into the model
            double x = layout.getCenterX();
            double y = layout.getCenterY();
            Point2D shift = this.immovableMap.get(vertex);
            if (shift != null) {
                x += shift.getX();
                y += shift.getY();
            }
            visuals.setNodePos(new Point2D.Double(x, y));
            vertex.setLayoutable(false);
            change.put(vertex, visuals);
        }
        // clear edge points
        // not calling GraphCanvas.clearAllEdgePoints to avoid generating a separate edit
        for (ViewCell<?> cell : getCanvas().getCells()) {
            if (!(cell instanceof ViewEdge<?> edge)) {
                continue;
            }
            // only clear edge points for edges with relayouted source or target
            if (this.immovableMap.containsKey(edge.getSourceVertex())
                && this.immovableMap.containsKey(edge.getTargetVertex())) {
                continue;
            }
            List<Point2D> points = edge.getVisuals().getPoints();
            // don't make the change directly in the cell,
            // as this messes up the undo history
            VisualMap visuals = new VisualMap();
            visuals.setPoints(Arrays.asList(points.get(0), points.get(points.size() - 1)));
            change.put(edge, visuals);
        }
        // do the following in the event dispatch thread
        Runnable edit = () -> {
            if (!change.isEmpty()) {
                edit(change);
            }
            getCanvas().setLayouting(false);
        };
        // do this now (if invoked from the event thread) or defer to event thread
        if (SwingUtilities.isEventDispatchThread()) {
            edit.run();
        } else {
            try {
                SwingUtilities.invokeAndWait(edit);
            } catch (InterruptedException exc) {
                // do nothing
            } catch (InvocationTargetException exc) {
                // do nothing
            }
        }
    }

    /** Commits a change to the canvas; raw-typed to bridge the wildcard canvas type. */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private void edit(Map<ViewCell<?>,VisualMap> change) {
        ((GraphCanvas) getCanvas()).edit(change);
    }

    /** Returns the (fixed) canvas for this layouter; fails on a prototype. */
    protected GraphCanvas<?> getCanvas() {
        var result = this.canvas;
        assert result != null; // only canvas-bound instances are started
        return result;
    }

    /**
     * The underlying canvas for this layout action; {@code null} for a prototype.
     */
    private final @Nullable GraphCanvas<?> canvas;

    /**
     * Map from graph nodes to layoutables.
     */
    protected final Map<ViewVertex<?>,LayoutNode> layoutMap = new LinkedHashMap<>();

    /**
     * Map from vertices whose position should not be changed
     * to a point representing the shift between the position determined for them at the
     * last layout action, and their position at the start of the current layout action.
     */
    protected final Map<ViewVertex<?>,@Nullable Point2D> immovableMap = new HashMap<>();

    @Override
    public Layouter getIncremental() {
        return this;
    }

    /**
     * Implements a layoutable that wraps a rectangle.
     */
    static final protected class LayoutNode {
        /** Constructs a new layoutable from a given vertex and its current bounds. */
        public LayoutNode(ViewVertex<?> vertex, Rectangle2D bounds) {
            this.r = bounds;
            this.vertex = vertex;
        }

        /** Returns the bounds of this layout node. */
        public Rectangle2D getBounds() {
            return this.r;
        }

        /** Returns the x-coordinate of this layoutable. */
        public double getX() {
            return this.r.getX();
        }

        /** Returns the y-coordinate of this layoutable. */
        public double getY() {
            return this.r.getY();
        }

        /** Returns the width of this layoutable. */
        public double getWidth() {
            return this.r.getWidth();
        }

        /** Returns the height of this layoutable. */
        public double getHeight() {
            return this.r.getHeight();
        }

        /** Returns the x-coordinate of the centre of this layoutable. */
        public double getCenterX() {
            return this.r.getCenterX();
        }

        /** Returns the y-coordinate of the centre of this layoutable. */
        public double getCenterY() {
            return this.r.getCenterY();
        }

        /** Sets a new position of this layoutable. */
        public void setLocation(double x, double y) {
            this.r.setRect(x, y, getWidth(), getHeight());

        }

        /** Returns the vertex for which this is the layout node. */
        public ViewVertex<?> getVertex() {
            return this.vertex;
        }

        @Override
        public String toString() {
            return "Layout[x=" + getX() + ",y=" + getY() + ",width=" + getWidth() + ",height="
                + getHeight() + "]";
        }

        /** The internally stored bounds of this layoutable. */
        private final Rectangle2D r;
        /** Vertex for which this is the layout node. */
        private final ViewVertex<?> vertex;
    }
}
