/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.gui.action;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.gui.view.EdgeGeometry;
import nl.utwente.groove.gui.look.VisualKey;
import nl.utwente.groove.gui.look.VisualMap;
import nl.utwente.groove.gui.view.AspectGraphCanvas;
import nl.utwente.groove.gui.view.AspectViewCell;
import nl.utwente.groove.gui.view.GraphCanvas;
import nl.utwente.groove.gui.view.GraphCanvasListener;
import nl.utwente.groove.gui.view.ViewCell;
import nl.utwente.groove.gui.view.ViewEdge;
import nl.utwente.groove.gui.view.ViewVertex;

/**
 * Abstract class for cell edit actions, working on the selected cells of
 * a given {@link AspectGraphCanvas}.
 * @author Arend Rensink
 * @version $Revision$
 */
public abstract class CellEditAction extends AbstractAction
    implements GraphCanvasListener<@NonNull AspectGraph> {
    /**
     * Constructs an edit action that is enabled for all cells.
     * @param canvas the canvas on which this action works
     * @param name the name of the action
     */
    protected CellEditAction(AspectGraphCanvas canvas, String name) {
        super(name);
        this.canvas = canvas;
        this.allCells = true;
        this.vertexOnly = true;
        this.cells = new ArrayList<>();
        refresh();
        canvas.addCanvasListener(this);
    }

    /**
     * Constructs an edit action that is enabled for only vertex cells or
     * edge cells.
     * @param canvas the canvas on which this action works
     * @param name the name of the action
     * @param vertexOnly <tt>true</tt> if the action is for vertex cells only
     */
    protected CellEditAction(AspectGraphCanvas canvas, String name, boolean vertexOnly) {
        super(name);
        this.canvas = canvas;
        this.allCells = false;
        this.vertexOnly = vertexOnly;
        this.cells = new ArrayList<>();
        refresh();
        canvas.addCanvasListener(this);
    }

    /**
     * Sets the cell to the first selected cell. Disables the action if
     * the type of the cell disagrees with the expected type.
     */
    @Override
    public void selectionChanged(GraphCanvas<@NonNull AspectGraph> canvas) {
        refresh();
    }

    private void refresh() {
        this.cell = null;
        this.cells.clear();
        for (var selected : this.canvas.getSelection()) {
            AspectViewCell cell = (AspectViewCell) selected;
            if (this.allCells || this.vertexOnly == (cell instanceof ViewVertex)) {
                this.cell = cell;
                this.cells.add(cell);
            }
        }
        this.setEnabled(this.cell != null);
    }

    /**
     * Sets the location at which the next invocation of the action acts, in graph
     * coordinates; {@code null} to let it act at the pointer location.
     * @see #takeLocation()
     */
    public void setLocation(@Nullable Point2D location) {
        this.location = location;
    }

    /**
     * Returns the location at which the action is to act, in graph coordinates: the
     * location set for this invocation (see {@link #setLocation}), if any, or else the
     * current location of the mouse pointer over the canvas, if any. The location set
     * is consumed: a next invocation acts at the pointer location again.
     */
    protected @Nullable Point2D takeLocation() {
        Point2D result = this.location;
        this.location = null;
        if (result == null) {
            result = this.canvas.getPointerLocation();
        }
        return result;
    }

    /**
     * Creates a menu item for this action that passes a given location to the action
     * when the item is activated: the location at which the menu was invoked.
     * @param at the location, in graph coordinates; {@code null} for none
     */
    public JMenuItem createMenuItem(@Nullable Point2D at) {
        return new JMenuItem(this) {
            @Override
            protected void fireActionPerformed(ActionEvent event) {
                CellEditAction.this.setLocation(at);
                super.fireActionPerformed(event);
            }
        };
    }

    /** Convenience method to invoke an edit of a single visual attribute. */
    protected void edit(ViewCell<@NonNull AspectGraph> cell, VisualKey key, Object value) {
        VisualMap newVisuals = new VisualMap();
        newVisuals.put(key, value);
        edit(cell, newVisuals);
    }

    /** Convenience method to invoke an edit of a set of visual attributes. */
    protected void edit(ViewCell<@NonNull AspectGraph> cell, VisualMap newVisuals) {
        this.canvas.edit(Collections.singletonMap(cell, newVisuals));
    }

    /**
     * Adds a point at a given location to the points of an edge. The point is
     * added between the two existing (adjacent) edge points whose segment is
     * closest to the location. If the location is
     * <tt>null</tt>,{@link #createPointBetween} is invoked instead. Does not
     * update the view; this is to be done by the client.
     * @param cell the edge to which the point is added
     * @param location the location at which the new point should appear; if
     *        <tt>null</tt>, a point is added beside the first segment
     * @return a copy of the points of the edge (see {@link #shownPoints}) with a
     * point added
     */
    protected List<Point2D> addPointAt(AspectViewCell cell, @Nullable Point2D location) {
        List<Point2D> result = shownPoints(cell);
        if (location == null) {
            result.add(1, createPointBetween(result.get(0), result.get(1)));
        } else {
            int closestIndex = EdgeGeometry.closestSegment(location, result);
            result
                .add(closestIndex, new Point((int) Math.round(location.getX()),
                    (int) Math.round(location.getY())));
        }
        return result;
    }

    /**
     * Returns the points of an edge as it is shown: the stored points, with the
     * end points replaced by the current centres of the end vertices, which the
     * stored end points do not follow when a vertex is moved.
     */
    protected static List<Point2D> shownPoints(AspectViewCell cell) {
        List<Point2D> result = new LinkedList<>(cell.getVisuals().getPoints());
        if (cell instanceof ViewEdge<?> edge && result.size() >= 2) {
            var source = edge.getSourceVertex();
            if (source != null) {
                result.set(0, source.getVisuals().getNodePos());
            }
            var target = edge.getTargetVertex();
            if (target != null) {
                result.set(result.size() - 1, target.getVisuals().getNodePos());
            }
        }
        return result;
    }

    /**
     * Creates an returns a point halfway two given points, at a small distance
     * from the line between them so that a bend shows.
     * @param p1 the first boundary point
     * @param p2 the first boundary point
     * @return new point on the perpendicular of the line between <tt>p1</tt>
     *         and <tt>p2</tt>
     */
    private Point createPointBetween(Point2D p1, Point2D p2) {
        double distance = p1.distance(p2);
        int midX = (int) (p1.getX() + p2.getX()) / 2;
        int midY = (int) (p1.getY() + p2.getY()) / 2;
        int x, y;
        if (distance == 0) {
            x = midX + BEND_OFFSET;
            y = midY + BEND_OFFSET;
        } else {
            int offset = BEND_OFFSET;
            double xDelta = p1.getX() - p2.getX();
            double yDelta = p1.getY() - p2.getY();
            x = midX + (int) (offset * yDelta / distance);
            y = midY - (int) (offset * xDelta / distance);
        }
        return new Point(Math.max(x, 0), Math.max(y, 0));
    }

    /** Distance from the edge at which a point is added beside it. */
    private static final int BEND_OFFSET = 20;

    /** The canvas on which this action works. */
    protected final AspectGraphCanvas canvas;
    /**
     * Switch indication that the action is enabled for all types of
     * cells.
     */
    protected final boolean allCells;
    /** Switch indication that the action is enabled for all vertex cells. */
    protected final boolean vertexOnly;
    /** The first currently selected cell of the right type. */
    protected AspectViewCell cell;
    /** List list of currently selected cells of the right type. */
    protected final List<AspectViewCell> cells;
    /** The location set for the next invocation, if any; see {@link #takeLocation()}. */
    private @Nullable Point2D location;
}
