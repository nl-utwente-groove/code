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
import java.util.List;

import nl.utwente.groove.gui.Options;
import nl.utwente.groove.gui.jgraph.AspectJCell;
import nl.utwente.groove.gui.jgraph.AspectJGraph;
import nl.utwente.groove.gui.look.VisualKey;
import nl.utwente.groove.gui.look.VisualMap;

/**
 * Action to add an intermediate point to a JEdge.
 * @author Arend Rensink
 * @version $Revision$
 */
public class AddPointAction extends JCellEditAction {
    /** Constructs an instance of the action. */
    public AddPointAction(AspectJGraph jGraph) {
        super(jGraph, Options.ADD_POINT_ACTION, false);
        putValue(ACCELERATOR_KEY, Options.ADD_POINT_KEY);
    }

    @Override
    public boolean isEnabled() {
        return this.jCells.size() == 1;
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        execute(this.jCell);
    }

    /** Executes the action. */
    public void execute(AspectJCell jCell) {
        VisualMap visuals = jCell.getVisuals();
        List<Point2D> points = addPointAt(visuals.getPoints(), this.location);
        edit(jCell, VisualKey.POINTS, points);
    }
}
