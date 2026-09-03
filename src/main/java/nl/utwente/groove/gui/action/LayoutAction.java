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

import javax.swing.AbstractAction;

import nl.utwente.groove.gui.Icons;
import nl.utwente.groove.gui.Options;
import nl.utwente.groove.gui.layout.Layouter;
import nl.utwente.groove.gui.view.GraphCanvas;
import nl.utwente.groove.gui.view.ViewVertex;

/**
 * Action to lay out a graph canvas, using the current layouter of its controller.
 * @author Arend Rensink
 * @version $Revision$
 */
public class LayoutAction extends AbstractAction {
    /** Constructs an action for a given canvas. */
    public LayoutAction(GraphCanvas<?> canvas) {
        super(canvas.getController().getLayouter().getName(), Icons.LAYOUT_ICON);
        putValue(ACCELERATOR_KEY, Options.LAYOUT_KEY);
        this.canvas = canvas;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (this.canvas.isEnabled()) {
            doLayout();
        }
    }

    /** Lays out the canvas: the selected cells only, if there are any. */
    public void doLayout() {
        var selection = this.canvas.getSelection();
        this.canvas.getNonNullViewModel().setLayoutable(selection.isEmpty());
        for (var jCell : selection) {
            if (jCell instanceof ViewVertex) {
                ((ViewVertex<?>) jCell).setLayoutable(true);
            }
        }
        getLayouter().start();
    }

    @Override
    public Object getValue(String key) {
        if (key.equals(NAME)) {
            return getLayouter().getName();
        } else {
            return super.getValue(key);
        }
    }

    private Layouter getLayouter() {
        return this.canvas.getController().getLayouter();
    }

    private final GraphCanvas<?> canvas;
}
