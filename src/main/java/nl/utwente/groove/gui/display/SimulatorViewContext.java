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

import java.util.Collection;
import java.util.List;

import javax.swing.Action;
import javax.swing.JMenu;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.graph.Label;
import nl.utwente.groove.gui.Simulator;
import nl.utwente.groove.gui.SimulatorModel;
import nl.utwente.groove.gui.action.ActionStore;
import nl.utwente.groove.gui.action.ExportAction;
import nl.utwente.groove.gui.tree.LabelTree;
import nl.utwente.groove.gui.view.GraphCanvas;
import nl.utwente.groove.gui.view.GraphViewContext;
import nl.utwente.groove.gui.view.LabelledCells;
import nl.utwente.groove.gui.view.ViewCell;
import nl.utwente.groove.gui.view.ViewOptions;
import nl.utwente.groove.util.AIGenerated;

/**
 * The simulator as host of a graph view: it provides the grammar and options
 * the view is shown under, the label tree it is filtered by, and the actions
 * of the simulator that the view offers in its menus.
 * <p>
 * The label tree is set after construction, since the trees are built on the
 * canvas, which the controller only creates once it has this context.
 * @param <G> the type of graphs shown in the graph view
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Opus 5, 2026-09")
@NonNullByDefault
public class SimulatorViewContext<G extends Graph> implements GraphViewContext<G> {
    /** Constructs a context for a graph view of a given simulator. */
    public SimulatorViewContext(Simulator simulator) {
        this.simulator = simulator;
    }

    /** Returns the simulator hosting the graph view. */
    protected final Simulator getSimulator() {
        return this.simulator;
    }

    /** The simulator hosting the graph view. */
    private final Simulator simulator;

    /** Returns the state of the simulator. */
    protected final SimulatorModel getSimulatorModel() {
        return getSimulator().getModel();
    }

    /** Returns the action store of the simulator. */
    protected final ActionStore getActions() {
        return getSimulator().getActions();
    }

    /**
     * Associates a label tree with the graph view; the view is filtered by it.
     * @param labelTree the label tree of the graph view, or {@code null} if
     * the view is not filtered
     */
    public void setLabelTree(@Nullable LabelTree<G> labelTree) {
        this.labelTree = labelTree;
    }

    /** Returns the label tree of the graph view, if there is one. */
    public @Nullable LabelTree<G> getLabelTree() {
        return this.labelTree;
    }

    /** The label tree by which the graph view is filtered, if any. */
    private @Nullable LabelTree<G> labelTree;

    @Override
    public @Nullable GrammarModel getGrammar() {
        return getSimulatorModel().getGrammar();
    }

    @Override
    public ViewOptions getOptions() {
        return getSimulator().getOptions();
    }

    @Override
    public boolean isInteractive() {
        return true;
    }

    @Override
    public boolean isFiltering() {
        return getLabelTree() != null;
    }

    @Override
    public boolean isFiltered(ViewCell<G> cell) {
        var labelTree = getLabelTree();
        return labelTree != null && !labelTree.isIncluded(cell);
    }

    @Override
    public boolean isFiltered(Label label) {
        var labelTree = getLabelTree();
        return labelTree != null && !labelTree.isIncluded(label);
    }

    @Override
    public void setFilteringEnabled(boolean enabled) {
        var labelTree = getLabelTree();
        if (labelTree != null) {
            labelTree.setEnabled(enabled);
        }
    }

    @Override
    public Collection<LabelledCells<G>> getFilterLabels() {
        var labelTree = getLabelTree();
        return labelTree == null
            ? List.of()
            : labelTree.getLabels();
    }

    /**
     * Returns the export action of the graph view, creating it on first request.
     * The action is refreshed, as the graph it exports may have changed.
     */
    @Override
    public ExportAction getExportAction(GraphCanvas<G> canvas) {
        var result = this.exportAction;
        if (result == null) {
            this.exportAction = result = new ExportAction(getSimulator(), canvas);
        }
        result.refresh();
        return result;
    }

    /** The permanent export action of the graph view. */
    private @Nullable ExportAction exportAction;

    @Override
    public Action getLayoutDialogAction() {
        return getActions().getLayoutDialogAction();
    }

    @Override
    public JMenu getSelectionItems(Collection<? extends ViewCell<G>> cells) {
        JMenu result = new JMenu();
        result.add(getActions().getFindReplaceAction());
        result.add(getActions().getSelectColorAction());
        var labelTree = getLabelTree();
        if (labelTree != null) {
            Action filterAction = labelTree.createFilterAction(cells.toArray());
            if (filterAction != null) {
                result.add(filterAction);
            }
        }
        return result;
    }

    /* Unregisters the export action, which is refreshed by the action store. */
    @Override
    public void canvasDetached(GraphCanvas<G> canvas) {
        var exportAction = this.exportAction;
        if (exportAction != null) {
            getActions().removeRefreshable(exportAction);
            this.exportAction = null;
        }
    }
}
