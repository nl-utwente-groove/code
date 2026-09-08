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
package nl.utwente.groove.gui.menu;

import java.util.EnumMap;
import java.util.Map;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;

import org.eclipse.jdt.annotation.NonNull;

import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.gui.Options;
import nl.utwente.groove.gui.view.AspectGraphCanvas;
import nl.utwente.groove.gui.view.GraphCanvas;
import nl.utwente.groove.gui.view.GraphCanvasListener;
import nl.utwente.groove.gui.view.ViewEdge;
import nl.utwente.groove.util.line.LineStyle;

/**
 * Menu to set the line style of the selected edge; the style of the selected
 * edge is check-marked.
 * @author Arend Rensink
 * @version $Revision$
 */
public class SetLineStyleMenu extends JMenu implements GraphCanvasListener<@NonNull AspectGraph> {
    /**
     * Constructs a menu for a given canvas.
     */
    public SetLineStyleMenu(AspectGraphCanvas canvas) {
        super(Options.SET_LINE_STYLE_MENU);
        this.canvas = canvas;
        canvas.addCanvasListener(this);
        // initialise the line style menu
        for (LineStyle lineStyle : LineStyle.values()) {
            var item = new JCheckBoxMenuItem(canvas.getController().getSetLineStyleAction(lineStyle));
            this.items.put(lineStyle, item);
            add(item);
        }
        selectionChanged(canvas);
    }

    /*
     * The menu is enabled if an edge is selected, whatever else is; the line style
     * of the selected edges is check-marked if they all share it.
     */
    @Override
    public void selectionChanged(GraphCanvas<@NonNull AspectGraph> canvas) {
        boolean edgeSelected = false;
        LineStyle common = null;
        boolean mixed = false;
        for (var cell : this.canvas.getSelection()) {
            if (cell instanceof ViewEdge<?> edge) {
                edgeSelected = true;
                LineStyle style = edge.getVisuals().getLineStyle();
                if (common == null) {
                    common = style;
                } else if (common != style) {
                    mixed = true;
                }
            }
        }
        setEnabled(edgeSelected);
        for (var entry : this.items.entrySet()) {
            entry.getValue().setSelected(!mixed && entry.getKey() == common);
        }
    }

    private final AspectGraphCanvas canvas;
    /** The menu items, one per line style. */
    private final Map<LineStyle,JCheckBoxMenuItem> items = new EnumMap<>(LineStyle.class);
}
