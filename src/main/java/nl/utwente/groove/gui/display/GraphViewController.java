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

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JToggleButton;
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
import nl.utwente.groove.gui.jgraph.JGraphMode;
import nl.utwente.groove.gui.layout.Layouter;
import nl.utwente.groove.gui.menu.MyJMenu;
import nl.utwente.groove.gui.menu.SetLayoutMenu;
import nl.utwente.groove.gui.menu.ShowHideMenu;
import nl.utwente.groove.gui.menu.ZoomMenu;
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
public class GraphViewController<G extends Graph> {
    /**
     * Constructs a controller for a given {@link JGraph}.
     * @param graphView the graph-view component that this controller belongs to
     * @param simulator simulator to which the display belongs; may be {@code null}
     */
    public GraphViewController(JGraph<G> graphView, @Nullable Simulator simulator) {
        this.graphView = graphView;
        this.simulator = simulator;
        this.options = Options.instance();
    }

    /** Returns the graph-view component that this controller belongs to. */
    public JGraph<G> getGraphView() {
        return this.graphView;
    }

    /** The graph-view component that this controller belongs to. */
    private final JGraph<G> graphView;

    /** Returns the (possibly {@code null}) simulator associated with the display. */
    protected @Nullable Simulator getSimulator() {
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
        RefreshListener listener = getGraphView().getRefreshListener(option);
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
            result = getGraphView().getDefaultLayouter().newInstance(getGraphView());
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
        this.layouter = prototypeLayouter.newInstance(getGraphView());
    }

    /** Returns the default layouter of the graph view. */
    public Layouter getDefaultLayouter() {
        return getGraphView().getDefaultLayouter();
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
        var model = getGraphView().getModel();
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
            this.exportAction = result = new ExportAction(getGraphView());
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
            this.layoutAction = result = new LayoutAction(getGraphView());
            getGraphView().addAccelerator(result);
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
            ToolTipManager.sharedInstance().registerComponent(getGraphView());
        } else {
            ToolTipManager.sharedInstance().unregisterComponent(getGraphView());
        }
        this.toolTipEnabled = enabled;
    }

    /**
     * Flag to indicate whether the graph component is currently registered
     * with the {@link ToolTipManager}.
     */
    private boolean toolTipEnabled;

    /**
     * Lazily creates and returns the popup menu for the graph view, activated
     * for a given point.
     * @param atPoint the point at which the menu is to be activated
     */
    public JMenu createPopupMenu(@Nullable Point atPoint) {
        MyJMenu result = new MyJMenu("Popup");
        result.addSubmenu(createExportMenu());
        result.addSubmenu(createDisplayMenu());
        result.addSubmenu(getLayoutMenu());
        return result;
    }

    /** Returns a menu consisting of the export action of the graph view. */
    public JMenu createExportMenu() {
        JMenu result = new JMenu("Export");
        result.add(getExportAction());
        return result;
    }

    /**
     * Returns a menu consisting of all the display menu items of the graph view.
     */
    public JMenu createDisplayMenu() {
        JMenu result = new JMenu("Display");
        Object[] cells = getGraphView().getSelectionCells();
        boolean itemAdded = false;
        var actions = getActions();
        if (cells != null && cells.length > 0 && actions != null) {
            result.add(actions.getFindReplaceAction());
            result.add(actions.getSelectColorAction());
            itemAdded = true;
        }
        var labelTree = getLabelTree();
        if (labelTree != null && cells != null && cells.length > 0) {
            Action filterAction = labelTree.createFilterAction(cells);
            if (filterAction != null) {
                result.add(filterAction);
                itemAdded = true;
            }
        }
        if (itemAdded) {
            result.addSeparator();
        }
        result.add(getModeAction(JGraphMode.SELECT_MODE));
        result.add(getModeAction(JGraphMode.PAN_MODE));
        result.add(createShowHideMenu());
        result.add(createZoomMenu());
        return result;
    }

    /**
     * Returns a menu consisting of the menu items from the layouter
     * setting menu of the graph view.
     */
    public SetLayoutMenu getSetLayoutMenu() {
        var result = this.setLayoutMenu;
        if (result == null) {
            this.setLayoutMenu = result = createSetLayoutMenu();
        }
        return result;
    }

    /** Creates and returns a fresh layout setting menu for the graph view. */
    public SetLayoutMenu createSetLayoutMenu() {
        return new SetLayoutMenu(this);
    }

    /**
     * A standard layouter setting menu for the graph view.
     */
    private @Nullable SetLayoutMenu setLayoutMenu;

    /**
     * Returns a layout menu for the graph view.
     * The items added are the current layout action and a layouter setting
     * sub-menu.
     */
    public JMenu getLayoutMenu() {
        JMenu result = new JMenu("Layout");
        result.add(getSetLayoutMenu().getCurrentLayoutItem());
        result.add(getSetLayoutMenu());
        result.add(getShowLayoutDialogAction());
        return result;
    }

    /**
     * Creates and returns a fresh zoom menu for the graph view.
     */
    public ZoomMenu createZoomMenu() {
        return new ZoomMenu(getGraphView());
    }

    /**
     * Creates and returns a fresh show/hide menu for the graph view.
     */
    public ShowHideMenu<G> createShowHideMenu() {
        return new ShowHideMenu<>(getGraphView());
    }

    private Action getShowLayoutDialogAction() {
        var actions = getActions();
        assert actions != null; // the layout menu is only built with a simulator present
        return actions.getLayoutDialogAction();
    }

    /**
     * Lazily creates and returns an action setting the mode of the graph view.
     * The actual setting is done by a call to {@link JGraph#setMode}.
     */
    public Action getModeAction(JGraphMode mode) {
        var modeActionMap = this.modeActionMap;
        if (modeActionMap == null) {
            this.modeActionMap = modeActionMap = new EnumMap<>(JGraphMode.class);
            for (final JGraphMode any : JGraphMode.values()) {
                Action action = new AbstractAction(any.getName(), any.getIcon()) {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        getGraphView().setMode(any);
                    }
                };

                if (any.getAcceleratorKey() != null) {
                    action.putValue(Action.ACCELERATOR_KEY, any.getAcceleratorKey());
                    getGraphView().addAccelerator(action);
                }
                modeActionMap.put(any, action);
            }
        }
        var result = modeActionMap.get(mode);
        assert result != null; // the action map is filled for all modes
        return result;
    }

    private @Nullable Map<JGraphMode,Action> modeActionMap;

    /**
     * Lazily creates and returns a button wrapping
     * {@link #getModeAction(JGraphMode)}.
     */
    public JToggleButton getModeButton(JGraphMode mode) {
        var result = getModeButtonMap().get(mode);
        assert result != null; // the button map is filled for all modes
        return result;
    }

    private Map<JGraphMode,JToggleButton> getModeButtonMap() {
        var result = this.modeButtonMap;
        if (result == null) {
            this.modeButtonMap = result = new EnumMap<>(JGraphMode.class);
            ButtonGroup modeButtonGroup = new ButtonGroup();
            for (JGraphMode any : JGraphMode.values()) {
                JToggleButton button = new JToggleButton(getModeAction(any));
                Options.setLAF(button);
                button.setToolTipText(any.getName());
                button.setEnabled(getGraphView().isEnabled());
                result.put(any, button);
                modeButtonGroup.add(button);
            }
            var editButton = result.get(JGraphMode.EDIT_MODE);
            assert editButton != null; // the button map is filled for all modes
            editButton.setSelected(true);
        }
        return result;
    }

    private @Nullable Map<JGraphMode,JToggleButton> modeButtonMap;

    /** Enables or disables all mode buttons of the graph view. */
    public void setModeButtonsEnabled(boolean enabled) {
        for (JToggleButton button : getModeButtonMap().values()) {
            button.setEnabled(enabled);
        }
    }
}
