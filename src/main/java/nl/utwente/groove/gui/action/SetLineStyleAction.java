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
import nl.utwente.groove.gui.view.AspectViewCell;
import nl.utwente.groove.gui.view.AspectGraphCanvas;
import nl.utwente.groove.gui.look.VisualKey;
import nl.utwente.groove.gui.look.VisualMap;
import nl.utwente.groove.util.line.LineStyle;

/**
 * Action to set the line style of the currently selected j-edge.
 * @author Arend Rensink
 * @version $Revision$
 */
public class SetLineStyleAction extends JCellEditAction {
    /** Constructs an instance of the action, for a given line style. */
    public SetLineStyleAction(AspectGraphCanvas canvas, LineStyle lineStyle) {
        super(canvas, lineStyle.getName(), false);
        putValue(ACCELERATOR_KEY, Options.getLineStyleKey(lineStyle));
        this.lineStyle = lineStyle;
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        VisualMap newVisuals = new VisualMap();
        for (AspectViewCell jCell : this.jCells) {
            VisualMap visuals = jCell.getVisuals();
            newVisuals.setLineStyle(this.lineStyle);
            List<Point2D> points = visuals.getPoints();
            if (points.size() == 2) {
                points = addPointAt(points, this.location);
                newVisuals.put(VisualKey.POINTS, points);
            }
            edit(jCell, newVisuals);
        }
    }

    /** The line style set by this action instance. */
    protected final LineStyle lineStyle;
}
