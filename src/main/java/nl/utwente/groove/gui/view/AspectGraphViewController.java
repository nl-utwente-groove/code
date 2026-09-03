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
package nl.utwente.groove.gui.view;

import java.awt.Point;
import java.util.EnumMap;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JMenu;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.graph.GraphRole;
import nl.utwente.groove.gui.Options;
import nl.utwente.groove.gui.Simulator;
import nl.utwente.groove.gui.action.AddPointAction;
import nl.utwente.groove.gui.action.EditLabelAction;
import nl.utwente.groove.gui.action.JCellEditAction;
import nl.utwente.groove.gui.action.RemovePointAction;
import nl.utwente.groove.gui.action.ResetLabelPositionAction;
import nl.utwente.groove.gui.action.SetLineStyleAction;
import nl.utwente.groove.gui.jgraph.AspectJGraph;
import nl.utwente.groove.gui.menu.MyJMenu;
import nl.utwente.groove.gui.menu.SetLineStyleMenu;
import nl.utwente.groove.gui.tree.RuleLevelTree;
import nl.utwente.groove.util.line.LineStyle;

/**
 * Display controller for graph views showing {@link AspectGraph}s.
 * Adds the editing-related menus and cell-edit actions, and the
 * manually-set grammar used when there is no simulator.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class AspectGraphViewController extends GraphViewController<AspectGraph> {
    /**
     * Constructs a controller for a given graph-view component.
     * @param graphView the graph-view component that this controller belongs to
     * @param simulator simulator to which the display belongs; may be {@code null}
     */
    public AspectGraphViewController(AspectJGraph graphView, @Nullable Simulator simulator) {
        super(graphView, simulator);
    }

    /* Specialises the return type. */
    @Override
    public AspectJGraph getGraphView() {
        return (AspectJGraph) super.getGraphView();
    }

    @Override
    public JMenu createPopupMenu(@Nullable Point atPoint) {
        MyJMenu result = new MyJMenu("Popup");
        var actions = getActions();
        assert actions != null; // the popup menu is only built with a simulator present
        switch (getGraphView().getGraphRole()) {
        case HOST:
            result.add(actions.getApplyMatchAction());
            result.addSeparator();
            break;
        default:
            // do nothing
        }
        Action editAction;
        if (getGraphView().isForState()) {
            editAction = actions.getEditStateAction();
        } else {
            editAction
                = actions.getEditAction(ResourceKind.toResource(getGraphView().getGraphRole()));
        }
        result.add(editAction);
        result.addSubmenu(createEditMenu(atPoint));
        result.addSubmenu(super.createPopupMenu(atPoint));
        return result;
    }

    @Override
    public JMenu createExportMenu() {
        // add a save graph action as the first action
        MyJMenu result = new MyJMenu();
        var actions = getActions();
        if (actions != null) {
            if (getGraphView().isForState()) {
                result.add(actions.getSaveStateAction());
            } else {
                ResourceKind resource = ResourceKind.toResource(getGraphView().getGraphRole());
                result.add(actions.getSaveAction(resource));
                result.add(actions.getSaveAsAction(resource));
            }
        }
        result.addMenuItems(super.createExportMenu());
        return result;
    }

    /**
     * Returns a menu containing all known editing actions.
     * @param atPoint point at which the popup menu will appear
     */
    public JMenu createEditMenu(@Nullable Point atPoint) {
        JMenu result = new JMenu("Edit");
        if (getGraphView().hasActiveEditor()) {
            result.add(getEditLabelAction());
            result.add(getAddPointAction(atPoint));
            result.add(getRemovePointAction(atPoint));
            result.add(getResetLabelPositionAction());
            result.add(createLineStyleMenu());
        }
        return result;
    }

    /**
     * Initialises and returns an action to add a point to the currently selected edge.
     */
    public AddPointAction getAddPointAction(@Nullable Point atPoint) {
        var result = this.addPointAction;
        if (result == null) {
            this.addPointAction = result = new AddPointAction(getGraphView());
            getGraphView().addAccelerator(result);
        }
        result.setLocation(atPoint);
        return result;
    }

    /** The permanent AddPointAction associated with the graph view. */
    private @Nullable AddPointAction addPointAction;

    /**
     * @return an action to edit the currently selected cell label.
     */
    public JCellEditAction getEditLabelAction() {
        var result = this.editLabelAction;
        if (result == null) {
            this.editLabelAction = result = new EditLabelAction(getGraphView());
            getGraphView().addAccelerator(result);
        }
        return result;
    }

    /** The permanent EditLabelAction associated with the graph view. */
    private @Nullable EditLabelAction editLabelAction;

    /**
     * Initialises and returns an action to remove a point from the currently selected edge.
     */
    public RemovePointAction getRemovePointAction(@Nullable Point atPoint) {
        var result = this.removePointAction;
        if (result == null) {
            this.removePointAction = result = new RemovePointAction(getGraphView());
            getGraphView().addAccelerator(result);
        }
        result.setLocation(atPoint);
        return result;
    }

    /** The permanent RemovePointAction associated with the graph view. */
    private @Nullable RemovePointAction removePointAction;

    /**
     * @return an action to reset the label position of the currently selected
     *         edge.
     */
    public JCellEditAction getResetLabelPositionAction() {
        var result = this.resetLabelPositionAction;
        if (result == null) {
            this.resetLabelPositionAction = result = new ResetLabelPositionAction(getGraphView());
        }
        return result;
    }

    /** The permanent ResetLabelPositionAction associated with the graph view. */
    private @Nullable ResetLabelPositionAction resetLabelPositionAction;

    /**
     * @param lineStyle the lineStyle for which to get the set-action
     * @return an action to set the line style of the currently selected edge.
     */
    public JCellEditAction getSetLineStyleAction(LineStyle lineStyle) {
        var result = this.setLineStyleActionMap.get(lineStyle);
        if (result == null) {
            result = new SetLineStyleAction(getGraphView(), lineStyle);
            this.setLineStyleActionMap.put(lineStyle, result);
            getGraphView().addAccelerator(result);
        }
        return result;
    }

    /** Map from line styles to corresponding actions. */
    private final Map<LineStyle,@Nullable JCellEditAction> setLineStyleActionMap
        = new EnumMap<>(LineStyle.class);

    /**
     * Creates and returns a fresh line style menu for the graph view.
     */
    public JMenu createLineStyleMenu() {
        return new SetLineStyleMenu(getGraphView());
    }

    /**
     * Indicates whether aspect prefixes should be shown for nodes and edges.
     */
    public final boolean isShowAspects() {
        return getOptionValue(Options.SHOW_ASPECTS_OPTION);
    }

    /**
     * Indicates whether user node identities should be shown for nodes and edges.
     */
    public final boolean isShowUserIds() {
        return getOptionValue(Options.SHOW_USER_NODE_IDS_OPTION);
    }

    /**
     * Indicates whether data nodes should be shown in the graph view.
     * This is certainly the case if the view is being edited.
     */
    public final boolean isShowValueNodes() {
        return getGraphView().hasActiveEditor() || getOptionValue(Options.SHOW_VALUE_NODES_OPTION);
    }

    /** Sets a level tree for this graph view. */
    public void setLevelTree(@Nullable RuleLevelTree levelTree) {
        assert levelTree == null
            || getGraphView().getGraphRole() == GraphRole.RULE
                && !getGraphView().hasActiveEditor();
        this.levelTree = levelTree;
    }

    /**
     * Returns the rule level tree associated with this graph view, if any.
     */
    public @Nullable RuleLevelTree getLevelTree() {
        return this.levelTree;
    }

    /** The tree of rule levels, if any. */
    private @Nullable RuleLevelTree levelTree;

    /** Returns the grammar that has manually been set for this graph view. */
    public @Nullable GrammarModel getGrammar() {
        return this.grammar;
    }

    /** Manually sets a new grammar in this graph view.
     * This should only be done if there is no underlying simulator.
     * @param grammar the grammar to be used.
     */
    public void setGrammar(GrammarModel grammar) {
        assert getSimulatorModel() == null;
        this.grammar = grammar;
    }

    /** The manually-set grammar; used when there is no simulator. */
    private @Nullable GrammarModel grammar;
}
