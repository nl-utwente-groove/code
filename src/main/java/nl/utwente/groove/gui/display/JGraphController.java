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

import java.util.LinkedList;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.ToolTipManager;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.grammar.GrammarProperties;
import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.gui.Options;
import nl.utwente.groove.gui.Simulator;
import nl.utwente.groove.gui.SimulatorModel;
import nl.utwente.groove.gui.action.ActionStore;
import nl.utwente.groove.gui.action.ExportAction;
import nl.utwente.groove.gui.action.LayoutAction;
import nl.utwente.groove.gui.jgraph.JGraph;
import nl.utwente.groove.gui.jgraph.JGraph.RefreshListener;
import nl.utwente.groove.gui.layout.Layouter;
import nl.utwente.groove.gui.tree.LabelTree;
import nl.utwente.groove.util.Exceptions;
import nl.utwente.groove.util.Pair;

/**
 * Display controller associated with a single {@link JGraph}.
 * Holds the library-independent controller state of a graph display —
 * simulator wiring, display-option machinery, layouter management,
 * the export/layout action caches, label-tree association and tooltip
 * registration — that was historically bundled into the {@link JGraph}
 * component class itself.
 * <p>
 * In this phase the component owns the controller and keeps delegating
 * stubs; the controller talks back to the component only through its
 * public API. See {@code claude/jgraph-controller-split.md}.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class JGraphController<G extends Graph> {
    /**
     * Constructs a controller for a given {@link JGraph}.
     * @param jGraph the graph component that this controller belongs to
     * @param simulator simulator to which the display belongs; may be {@code null}
     */
    public JGraphController(JGraph<G> jGraph, @Nullable Simulator simulator) {
        this.jGraph = jGraph;
        this.simulator = simulator;
        this.options = Options.instance();
    }

    /** Returns the graph component that this controller belongs to. */
    public JGraph<G> getJGraph() {
        return this.jGraph;
    }

    /** The graph component that this controller belongs to. */
    private final JGraph<G> jGraph;

    /** Returns the (possibly {@code null}) simulator associated with the display. */
    private @Nullable Simulator getSimulator() {
        return this.simulator;
    }

    /** Simulator tool to which the display belongs. */
    private final @Nullable Simulator simulator;

    /** Convenience method to retrieve the state of the simulator, if any. */
    public @Nullable SimulatorModel getSimulatorModel() {
        var simulator = getSimulator();
        return simulator == null
            ? null
            : simulator.getModel();
    }

    /** Convenience method to retrieve the action store of the simulator, if any. */
    public @Nullable ActionStore getActions() {
        var simulator = getSimulator();
        return simulator == null
            ? null
            : simulator.getActions();
    }

    /**
     * The properties of the grammar to which the displayed graph belongs.
     * May return {@code null} if the simulator is not set.
     */
    public @Nullable GrammarProperties getProperties() {
        var simulatorModel = getSimulatorModel();
        return simulatorModel == null
            ? null
            : simulatorModel.getGrammar().getProperties();
    }

    /** Returns the object holding the display options. */
    public final Options getOptions() {
        return this.options;
    }

    /** The options object of the display. */
    private final Options options;

    /**
     * Retrieves the value for a given option from the options object, or
     * <code>null</code> if the options are not set (i.e., <code>null</code>).
     * @param option the name of the option
     */
    public boolean getOptionValue(String option) {
        return getOptions().getItem(option).isEnabled() && getOptions().isSelected(option);
    }

    /**
     * Adds a refresh listener to the menu item of an option
     * with a given name.
     * @see JGraph#getRefreshListener
     */
    public void addOptionListener(String option) {
        JMenuItem optionItem = getOptions().getItem(option);
        if (optionItem == null) {
            throw Exceptions.illegalArg("Unknown option: %s", option);
        }
        RefreshListener listener = getJGraph().getRefreshListener(option);
        if (listener != null) {
            optionItem.addItemListener(listener);
            optionItem.addPropertyChangeListener(listener);
            this.optionListeners.add(Pair.newPair(optionItem, listener));
        }
    }

    /** The option listeners registered by this controller. */
    private final List<Pair<JMenuItem,RefreshListener>> optionListeners = new LinkedList<>();

    /**
     * Removes the listeners registered by this controller,
     * so as to avoid memory leaks.
     */
    public void removeListeners() {
        var actions = getActions();
        if (actions != null) {
            actions.removeRefreshable(getExportAction());
        }
        for (Pair<JMenuItem,RefreshListener> record : this.optionListeners) {
            record.one().removeItemListener(record.two());
            record.one().removePropertyChangeListener(record.two());
        }
        this.optionListeners.clear();
        this.exportAction = null;
    }

    /**
     * @return the current layouter for the display.
     * @see #setLayouter(Layouter)
     */
    public Layouter getLayouter() {
        var result = this.layouter;
        if (result == null) {
            result = getJGraph().getDefaultLayouter().newInstance(getJGraph());
            assert result != null; // newInstance never returns null
            this.layouter = result;
        }
        return result;
    }

    /**
     * Sets (but does not start) the layout action for the display. First stops
     * the current layout action, if it is running.
     * @param prototypeLayouter prototype for the new layout action; the actual
     *        layout action is obtained by calling <tt>newInstance(jGraph)</tt>
     * @see #getLayouter()
     */
    public void setLayouter(Layouter prototypeLayouter) {
        this.layouter = prototypeLayouter.newInstance(getJGraph());
    }

    /** The currently selected prototype layouter. */
    private @Nullable Layouter layouter;

    /**
     * Lays out the graph completely or incrementally.
     * The graph is layed out completely (according to the user-defined layouter)
     * if explicitly requested, or if all cells need to be layed out;
     * otherwise it is layed out incrementally.
     * @param complete if {@code true}, the used-defined layouter is used
     * if any, or the incremental layouter if none was defined
     * @return the layouter that has been used
     */
    public Layouter doLayout(boolean complete) {
        var model = getJGraph().getModel();
        assert model != null;
        Layouter result;
        if (complete) {
            model.setLayoutable(true);
            result = getLayouter();
        } else {
            result = getLayouter().getIncremental();
        }
        result.start();
        return result;
    }

    /** Returns the action to export the displayed graph in various formats. */
    public ExportAction getExportAction() {
        var result = this.exportAction;
        if (result == null) {
            this.exportAction = result = new ExportAction(getJGraph());
        }
        result.refresh();
        return result;
    }

    /** The permanent ExportAction associated with the display. */
    private @Nullable ExportAction exportAction;

    /** Returns the action to lay out the displayed graph. */
    public LayoutAction getLayoutAction() {
        var result = this.layoutAction;
        if (result == null) {
            this.layoutAction = result = new LayoutAction(getJGraph());
            getJGraph().addAccelerator(result);
        }
        return result;
    }

    /** The permanent layout action associated with the display. */
    private @Nullable LayoutAction layoutAction;

    /**
     * Associates a label tree with the display.
     * Note: this method is called from the label tree constructor.
     */
    public void setLabelTree(@Nullable LabelTree<G> labelTree) {
        this.labelTree = labelTree;
    }

    /**
     * Returns the label tree associated with the display.
     * @return the associated label tree, or {@code null} if there is none
     */
    public @Nullable LabelTree<G> getLabelTree() {
        return this.labelTree;
    }

    /** The label tree associated with the display. */
    private @Nullable LabelTree<G> labelTree;

    /**
     * Indicates whether the display is currently registered at the tool tip
     * manager.
     * @return <tt>true</tt> if the display is currently registered at the tool
     *         tip manager
     */
    public boolean getToolTipEnabled() {
        return this.toolTipEnabled;
    }

    /**
     * Registers or unregisters the graph component with the tool tip manager.
     * The current registration state can be queried using
     * <tt>getToolTipEnabled()</tt>
     * @param enabled <tt>true</tt> if the component is to be registered with the
     *        tool tip manager
     * @see #getToolTipEnabled()
     * @see ToolTipManager#registerComponent(javax.swing.JComponent)
     * @see ToolTipManager#unregisterComponent(javax.swing.JComponent)
     */
    public void setToolTipEnabled(boolean enabled) {
        if (enabled) {
            ToolTipManager.sharedInstance().registerComponent(getJGraph());
        } else {
            ToolTipManager.sharedInstance().unregisterComponent(getJGraph());
        }
        this.toolTipEnabled = enabled;
    }

    /**
     * Flag to indicate whether the graph component is currently registered
     * with the {@link ToolTipManager}.
     */
    private boolean toolTipEnabled;
}
