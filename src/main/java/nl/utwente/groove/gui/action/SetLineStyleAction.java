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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import nl.utwente.groove.gui.Options;
import nl.utwente.groove.gui.view.AspectViewCell;
import nl.utwente.groove.gui.view.AspectGraphCanvas;
import nl.utwente.groove.gui.look.VisualKey;
import nl.utwente.groove.gui.look.VisualMap;
import nl.utwente.groove.util.line.LineStyle;

/**
 * Action to set the line style of the currently selected edge cell.
 * @author Arend Rensink
 * @version $Revision$
 */
public class SetLineStyleAction extends CellEditAction {
    /** Constructs an instance of the action, for a given line style. */
    public SetLineStyleAction(AspectGraphCanvas canvas, LineStyle lineStyle) {
        super(canvas, lineStyle.getName(), false);
        putValue(ACCELERATOR_KEY, Options.getLineStyleKey(lineStyle));
        this.lineStyle = lineStyle;
    }

    /*
     * The style is set on the selected edges that do not have it yet, as one edit;
     * if all have it already, nothing happens. A curved style is given a bend to show
     * on, if the edge has none, halfway the edge at a small distance from it.
     */
    @Override
    public void actionPerformed(ActionEvent evt) {
        Map<AspectViewCell,VisualMap> changes = new LinkedHashMap<>();
        for (AspectViewCell cell : this.cells) {
            VisualMap visuals = cell.getVisuals();
            if (visuals.getLineStyle() == this.lineStyle) {
                continue;
            }
            VisualMap newVisuals = new VisualMap();
            newVisuals.setLineStyle(this.lineStyle);
            List<Point2D> points = visuals.getPoints();
            if (this.lineStyle.isCurved() && points.size() == 2) {
                newVisuals.put(VisualKey.POINTS, addPointAt(cell, null));
            }
            changes.put(cell, newVisuals);
        }
        if (!changes.isEmpty()) {
            this.canvas.edit(changes);
        }
    }

    /** The line style set by this action instance. */
    protected final LineStyle lineStyle;
}
