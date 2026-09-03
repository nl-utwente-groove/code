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

import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;

import nl.utwente.groove.gui.Options;
import nl.utwente.groove.gui.view.AspectViewCell;
import nl.utwente.groove.gui.jgraph.AspectJGraph;
import nl.utwente.groove.gui.look.VisualKey;
import nl.utwente.groove.gui.look.VisualMap;

/**
 * Action to remove a point from the currently selected j-edge.
 * @author Arend Rensink
 * @version $Revision$
 */
public class RemovePointAction extends JCellEditAction {
    /** Constructs an instance of the action. */
    public RemovePointAction(AspectJGraph jGraph) {
        super(jGraph, Options.REMOVE_POINT_ACTION, false);
        putValue(ACCELERATOR_KEY, Options.REMOVE_POINT_KEY);
    }

    @Override
    public boolean isEnabled() {
        return this.jCells.size() == 1;
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        execute(this.jCell);
    }

    /**
     * Removes an intermediate point from a given j-edge, controlled by a given
     * location. The point removed is either the second point (if the location
     * is <tt>null</tt>) or the one closest to the location.
     * @param jEdge the j-edge to be modified
     */
    public void execute(AspectViewCell jEdge) {
        VisualMap visuals = jEdge.getVisuals();
        List<Point2D> points = visuals.getPoints();
        edit(jEdge, VisualKey.POINTS, removePointAt(points, this.location));
    }

    /**
     * Removes the intermediate point from a list of points that is closest
     * to a given location. Has no effect if the list had only two points to
     * start with, or if it is a loop. If
     * the location is <tt>null</tt>, the point at index 1 is removed
     * @param location the location at which the point to be removed is sought;
     *        if <tt>null</tt>, the first available point is removed
     * @return a copy of the points, possibly with a
     *         point removed
     */
    private List<Point2D> removePointAt(List<Point2D> points, Point2D location) {
        LinkedList<Point2D> result = new LinkedList<>(points);
        if (result.size() > 2
            && (!result.getFirst().equals(result.getLast()) || result.size() > 3)) {
            int ix = location == null
                ? 1
                : getClosestIndex(points, location);
            result.remove(ix);
        }
        return result;
    }
}
