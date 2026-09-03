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
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;

import org.eclipse.jdt.annotation.NonNull;
import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;

import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.gui.view.AspectViewCell;
import nl.utwente.groove.gui.jgraph.AspectJGraph;
import nl.utwente.groove.gui.view.ViewCell;
import nl.utwente.groove.gui.view.ViewVertex;
import nl.utwente.groove.gui.look.VisualKey;
import nl.utwente.groove.gui.look.VisualMap;

/**
 * Abstract class for j-cell edit actions, working on the selected cells of
 * a given {@link AspectJGraph}.
 * @author Arend Rensink
 * @version $Revision$
 */
public abstract class JCellEditAction extends AbstractAction implements GraphSelectionListener {
    /**
     * Constructs an edit action that is enabled for all j-cells.
     * @param jGraph the j-graph on which this action works
     * @param name the name of the action
     */
    protected JCellEditAction(AspectJGraph jGraph, String name) {
        super(name);
        this.jGraph = jGraph;
        this.allCells = true;
        this.vertexOnly = true;
        this.jCells = new ArrayList<>();
        refresh();
        jGraph.addGraphSelectionListener(this);
    }

    /**
     * Constructs an edit action that is enabled for only j-vertices or
     * j-edges.
     * @param jGraph the j-graph on which this action works
     * @param name the name of the action
     * @param vertexOnly <tt>true</tt> if the action is for j-vertices only
     */
    protected JCellEditAction(AspectJGraph jGraph, String name, boolean vertexOnly) {
        super(name);
        this.jGraph = jGraph;
        this.allCells = false;
        this.vertexOnly = vertexOnly;
        this.jCells = new ArrayList<>();
        refresh();
        jGraph.addGraphSelectionListener(this);
    }

    /**
     * Sets the j-cell to the first selected cell. Disables the action if
     * the type of the cell disagrees with the expected type.
     */
    @Override
    public void valueChanged(GraphSelectionEvent e) {
        refresh();
    }

    private void refresh() {
        this.jCell = null;
        this.jCells.clear();
        for (Object cell : this.jGraph.getSelectionCells()) {
            AspectViewCell jCell = (AspectViewCell) cell;
            if (this.allCells || this.vertexOnly == (jCell instanceof ViewVertex)) {
                this.jCell = jCell;
                this.jCells.add(jCell);
            }
        }
        this.setEnabled(this.jCell != null);
    }

    /**
     * Sets the location attribute of this action.
     */
    public void setLocation(Point2D location) {
        this.location = location;
    }

    /** Convenience method to invoke an edit of a single visual attribute. */
    protected void edit(ViewCell<@NonNull AspectGraph> jCell, VisualKey key, Object value) {
        VisualMap newVisuals = new VisualMap();
        newVisuals.put(key, value);
        edit(jCell, newVisuals);
    }

    /** Convenience method to invoke an edit of a set of visual attributes. */
    protected void edit(ViewCell<@NonNull AspectGraph> jCell, VisualMap newVisuals) {
        this.jGraph
            .getNonNullModel()
            .edit(Collections.singletonMap(jCell, newVisuals.getAttributes()), null, null, null);
    }

    /**
     * Adds a point at a given location to the underlying j-edge. The point is
     * added between those two existing (adjacent) edge points for which the sum
     * of the distances to the specified location is minimal. If the location is
     * <tt>null</tt>,{@link #createPointBetween} is invoked instead. Does not
     * update the view; this is to be done by the client.
     * @param location the location at which the new point should appear; if
     *        <tt>null</tt>, a point is added at random
     * @return a copy of the points of the underlying j-edge with a point added
     */
    protected List<Point2D> addPointAt(List<Point2D> points, Point2D location) {
        List<Point2D> result = new LinkedList<>(points);
        if (location == null) {
            result.add(1, createPointBetween(result.get(0), result.get(1)));
        } else {
            int closestIndex = getClosestIndex(result, location);
            assert closestIndex > 0;
            result.add(closestIndex, (Point) location.clone());
        }
        return result;
    }

    /**
     * Returns the positive index in a non-empty list of points of that
     * point which is closest to a given location.
     * @param location the location to which distances are measured.
     * @param points the list in which the index is sought
     * @return the index of the point (from position 1) closest to the location
     */
    protected int getClosestIndex(List<Point2D> points, Point2D location) {
        int result = 0;
        double closestDistance = Double.MAX_VALUE;
        for (int i = 1; i < points.size(); i++) {
            double distance
                = location.distance(points.get(i - 1)) + location.distance(points.get(i));
            if (distance < closestDistance) {
                result = i;
                closestDistance = distance;
            }
        }
        return result;
    }

    /**
     * Creates an returns a point halfway two given points, with a random effect
     * @param p1 the first boundary point
     * @param p2 the first boundary point
     * @return new point on the perpendicular of the line between <tt>p1</tt>
     *         and <tt>p2</tt>
     */
    private Point createPointBetween(Point2D p1, Point2D p2) {
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

    /** The j-graph on which this action works. */
    protected final AspectJGraph jGraph;
    /**
     * Switch indication that the action is enabled for all types of
     * j-cells.
     */
    protected final boolean allCells;
    /** Switch indication that the action is enabled for all j-vertices. */
    protected final boolean vertexOnly;
    /** The first currently selected j-cell of the right type. */
    protected AspectViewCell jCell;
    /** List list of currently selected j-cells of the right type. */
    protected final List<AspectViewCell> jCells;
    /** The currently set point location. */
    protected Point2D location;
}
