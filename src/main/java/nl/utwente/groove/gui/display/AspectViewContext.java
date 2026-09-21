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
package nl.utwente.groove.gui.display;

import java.awt.geom.Point2D;

import javax.swing.JMenu;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.graph.GraphRole;
import nl.utwente.groove.gui.Simulator;
import nl.utwente.groove.gui.menu.MyJMenu;
import nl.utwente.groove.gui.tree.RuleLevelTree;
import nl.utwente.groove.gui.view.AspectViewCell;
import nl.utwente.groove.gui.view.GraphCanvas;
import nl.utwente.groove.gui.view.ViewCell;
import nl.utwente.groove.util.AIGenerated;

/**
 * The simulator as host of a graph view showing aspect graphs.
 * Adds the resource actions for the graph shown, the rule level tree by which
 * a rule view is filtered, and the colour-selection listener on the canvas.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Opus 5, 2026-09")
@NonNullByDefault
public class AspectViewContext extends SimulatorViewContext<AspectGraph> {
    /**
     * Constructs a context for a graph view on a given display.
     * @param simulator the simulator hosting the graph view
     * @param kind display kind on which the graphs are shown; determines the
     * graph role, and whether the graphs are graph states
     */
    public AspectViewContext(Simulator simulator, DisplayKind kind) {
        super(simulator);
        this.kind = kind;
    }

    /** The display kind on which the graphs are shown. */
    private final DisplayKind kind;

    /** Indicates if the graphs shown are graph states. */
    private boolean isForState() {
        return this.kind == DisplayKind.STATE;
    }

    /** Returns the role of the graphs shown. */
    private GraphRole getGraphRole() {
        return isForState()
            ? GraphRole.HOST
            : this.kind.getGraphRole();
    }

    /**
     * Sets a rule level tree for the graph view; a rule view is also
     * filtered by the levels selected in it.
     */
    public void setLevelTree(@Nullable RuleLevelTree levelTree) {
        assert levelTree == null || getGraphRole() == GraphRole.RULE;
        this.levelTree = levelTree;
    }

    /** Returns the rule level tree of the graph view, if there is one. */
    public @Nullable RuleLevelTree getLevelTree() {
        return this.levelTree;
    }

    /** The tree of rule levels, if any. */
    private @Nullable RuleLevelTree levelTree;

    @Override
    public boolean isLevelFiltered(ViewCell<AspectGraph> cell) {
        var levelTree = getLevelTree();
        return levelTree != null && cell instanceof AspectViewCell aspectCell
            && !levelTree.isVisible(aspectCell);
    }

    /* Registers the colour-selection action, which acts on the canvas selection. */
    @Override
    public void canvasAttached(GraphCanvas<AspectGraph> canvas) {
        canvas.addCanvasListener(getActions().getSelectColorAction());
    }

    @Override
    public void canvasDetached(GraphCanvas<AspectGraph> canvas) {
        canvas.removeCanvasListener(getActions().getSelectColorAction());
        super.canvasDetached(canvas);
    }

    @Override
    public JMenu getPopupItems(@Nullable Point2D atPoint) {
        MyJMenu result = new MyJMenu();
        var actions = getActions();
        if (getGraphRole() == GraphRole.HOST) {
            result.add(actions.getApplyMatchAction());
            result.addSeparator();
        }
        result
            .add(isForState()
                ? actions.getEditStateAction()
                : actions.getEditAction(ResourceKind.toResource(getGraphRole())));
        return result;
    }

    @Override
    public JMenu getExportItems() {
        JMenu result = new JMenu();
        var actions = getActions();
        if (isForState()) {
            result.add(actions.getSaveStateAction());
        } else {
            ResourceKind resource = ResourceKind.toResource(getGraphRole());
            result.add(actions.getSaveAction(resource));
            result.add(actions.getSaveAsAction(resource));
        }
        return result;
    }
}
