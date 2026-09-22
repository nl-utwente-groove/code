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

import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JToggleButton;
import javax.swing.ToolTipManager;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.grammar.GrammarProperties;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.graph.GraphRole;
import nl.utwente.groove.graph.Label;
import nl.utwente.groove.gui.Options;
import nl.utwente.groove.gui.action.LayoutAction;
import nl.utwente.groove.gui.layout.Layouter;
import nl.utwente.groove.gui.layout.SpringLayouter;
import nl.utwente.groove.gui.menu.MyJMenu;
import nl.utwente.groove.gui.menu.SetLayoutMenu;
import nl.utwente.groove.gui.menu.ShowHideMenu;
import nl.utwente.groove.gui.menu.ZoomMenu;
import nl.utwente.groove.util.Exceptions;
import nl.utwente.groove.util.Pair;

/**
 * Display controller associated with a single {@link GraphCanvas}.
 * Holds the library-independent controller state of a graph display —
 * display-option machinery, layouter management, the layout action cache,
 * menu construction and tooltip registration — that was historically
 * bundled into the rendering component class itself. What the display
 * needs from the tool in which it is shown, the controller asks of its
 * {@link GraphViewContext}.
 * <p>
 * The controller owns its canvas, which it obtains from the {@link GraphBackend}
 * selected at start-up on first request; it talks to the canvas only through the
 * {@link GraphCanvas} interface. See {@code claude/view-facade.md} and
 * {@code claude/phase-2-model-and-ownership.md}.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public abstract class GraphViewController<G extends Graph> {
    /**
     * Constructs a controller.
     * @param context the context of the display; {@code null} if the display is
     * shown outside any tool
     */
    public GraphViewController(@Nullable GraphViewContext<G> context) {
        this.context = context;
        this.options = context == null
            ? Options.instance()
            : context.getOptions();
    }

    /**
     * Returns the canvas of this controller, creating it through the
     * selected {@link GraphBackend} on first request.
     */
    public GraphCanvas<G> getCanvas() {
        var result = this.canvas;
        if (result == null) {
            result = createCanvas(GraphBackend.instance());
            if (result != this.canvas) {
                throw Exceptions
                    .illegalState("Canvas %s did not attach itself to its controller", result);
            }
        }
        return result;
    }

    /**
     * Attaches a canvas to this controller.
     * Called by the canvas from its constructor, before it does anything that
     * may ask the controller for its canvas, so that no second canvas is created.
     * @throws IllegalStateException if a canvas was attached before
     */
    public void attachCanvas(GraphCanvas<G> canvas) {
        if (this.canvas != null) {
            throw Exceptions.illegalState("Controller already has a canvas");
        }
        this.canvas = canvas;
        var context = getContext();
        if (context != null) {
            context.canvasAttached(canvas);
        }
    }

    /** Callback factory method creating the canvas of this controller through a backend. */
    protected abstract GraphCanvas<G> createCanvas(GraphBackend backend);

    /** The canvas of this controller; {@code null} until created or attached. */
    private @Nullable GraphCanvas<G> canvas;

    /** Returns the context of the display, if it has one. */
    protected @Nullable GraphViewContext<G> getContext() {
        return this.context;
    }

    /** The context of the display; {@code null} if the display is shown outside any tool. */
    private final @Nullable GraphViewContext<G> context;

    /**
     * Indicates if the graph view offers the actions of a simulator.
     * A non-interactive view is one shown by a dialog or by the headless imager.
     */
    public boolean isInteractive() {
        var context = getContext();
        return context != null && context.isInteractive();
    }

    /**
     * The grammar to which the displayed graph belongs.
     * May return {@code null} if there is no grammar.
     */
    public @Nullable GrammarModel getGrammar() {
        var context = getContext();
        return context == null
            ? null
            : context.getGrammar();
    }

    /**
     * The properties of the grammar of the context, if there is a context.
     * Deliberately not derived from {@link #getGrammar()}: a grammar set by
     * hand, as in the imager, does not determine the rendering.
     */
    private @Nullable GrammarProperties getProperties() {
        var context = getContext();
        var grammar = context == null
            ? null
            : context.getGrammar();
        return grammar == null
            ? null
            : grammar.getProperties();
    }

    /** Returns the object holding the display options. */
    public final ViewOptions getOptions() {
        return this.options;
    }

    /** The options object of the display. */
    private final ViewOptions options;

    /**
     * Retrieves the value for a given option from the options object;
     * an option that is not enabled counts as unselected.
     * @param option the name of the option
     */
    public boolean getOptionValue(String option) {
        return getOptions().isEnabled(option) && getOptions().isSelected(option);
    }

    /**
     * Adds a refresh listener for an option with a given name.
     * @see GraphCanvas#getRefreshListener
     */
    public void addOptionListener(String option) {
        OptionRefreshListener listener = getCanvas().getRefreshListener(option);
        if (listener != null) {
            getOptions().addOptionListener(option, listener);
            this.optionListeners.add(Pair.newPair(option, listener));
        }
    }

    /** The option listeners registered by this controller. */
    private final List<Pair<String,OptionRefreshListener>> optionListeners = new LinkedList<>();

    /**
     * Removes the listeners registered by this controller,
     * so as to avoid memory leaks.
     */
    public void removeListeners() {
        var context = getContext();
        if (context != null) {
            context.canvasDetached(getCanvas());
        }
        for (Pair<String,OptionRefreshListener> record : this.optionListeners) {
            getOptions().removeOptionListener(record.one(), record.two());
        }
        this.optionListeners.clear();
    }

    /**
     * Indicates whether node identities should be shown on node labels.
     */
    public boolean isShowNodeIdentities() {
        return getOptionValue(ViewOptions.SHOW_INTERNAL_NODE_IDS_OPTION);
    }

    /**
     * Indicates whether anchors should be shown in the rule and lts views.
     */
    public boolean isShowAnchors() {
        return getOptionValue(ViewOptions.SHOW_ANCHORS_OPTION);
    }

    /**
     * Indicates whether self-edges should be shown as node labels.
     */
    public boolean isShowLoopsAsNodeLabels() {
        var properties = getProperties();
        return properties == null || properties.isShowLoopsAsLabels();
    }

    /**
     * Indicates whether arrow heads should be shown on labels, rather than
     * on edges.
     */
    public boolean isShowArrowsOnLabels() {
        return getOptionValue(ViewOptions.SHOW_ARROWS_ON_LABELS_OPTION);
    }

    /**
     * Indicates whether a single edge cell may stand for edges in two directions.
     */
    public boolean isShowBidirectionalEdges() {
        return getOptionValue(ViewOptions.SHOW_BIDIRECTIONAL_EDGES_OPTION);
    }

    /**
     * Returns the role of the graphs displayed in the graph view.
     * This implementation returns {@link GraphRole#NONE}; role controllers override it.
     */
    public GraphRole getGraphRole() {
        return GraphRole.NONE;
    }

    /** Convenience method to retrieve the displayed graph, if any. */
    public @Nullable G getGraph() {
        return getCanvas().getGraph();
    }

    /**
     * @return the current layouter for the display.
     * @see #setLayouter(Layouter)
     */
    public Layouter getLayouter() {
        var result = this.layouter;
        if (result == null) {
            result = getDefaultLayouter().newInstance(getCanvas());
            assert result != null; // newInstance never returns null
            this.layouter = result;
        }
        return result;
    }

    /**
     * Sets (but does not start) the layout action for the display. First stops
     * the current layout action, if it is running.
     * @param prototypeLayouter prototype for the new layout action; the actual
     *        layout action is obtained by calling <tt>newInstance(canvas)</tt>
     * @see #getLayouter()
     */
    public void setLayouter(Layouter prototypeLayouter) {
        this.layouter = prototypeLayouter.newInstance(getCanvas());
    }

    /** Returns the default (prototype) layouter of the graph view. */
    public Layouter getDefaultLayouter() {
        return SpringLayouter.PROTOTYPE;
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
        Layouter result;
        if (complete) {
            getCanvas().getNonNullViewModel().setLayoutable(true);
            result = getLayouter();
        } else {
            result = getLayouter().getIncremental();
        }
        result.start();
        return result;
    }

    /**
     * Returns the action to export the displayed graph in various formats,
     * or {@code null} if the context offers no export.
     */
    public @Nullable Action getExportAction() {
        var context = getContext();
        return context == null
            ? null
            : context.getExportAction(getCanvas());
    }

    /** Returns the action to lay out the displayed graph. */
    public Action getLayoutAction() {
        var result = this.layoutAction;
        if (result == null) {
            this.layoutAction = result = new LayoutAction(getCanvas());
            getCanvas().addAccelerator(result);
        }
        return result;
    }

    /** The permanent layout action associated with the display. */
    private @Nullable LayoutAction layoutAction;

    /** Enables or disables the label filter of the display, if there is one. */
    public void setLabelTreeEnabled(boolean enabled) {
        var context = getContext();
        if (context != null) {
            context.setFilteringEnabled(enabled);
        }
    }

    /**
     * Indicates if the graph view filters its cells by label at all.
     * If it does not, {@link #isFiltered(ViewCell)} and {@link #isFiltered(Label)}
     * are invariably {@code false}.
     */
    public boolean isFiltering() {
        var context = getContext();
        return context != null && context.isFiltering();
    }

    /** Indicates if a given cell is currently filtered out of the graph view. */
    public boolean isFiltered(ViewCell<G> cell) {
        var context = getContext();
        return context != null && context.isFiltered(cell);
    }

    /** Indicates if a given label is currently filtered out of the graph view. */
    public boolean isFiltered(Label label) {
        var context = getContext();
        return context != null && context.isFiltered(label);
    }

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
            ToolTipManager.sharedInstance().registerComponent(getCanvas().getComponent());
        } else {
            ToolTipManager.sharedInstance().unregisterComponent(getCanvas().getComponent());
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
     * @param atPoint the point at which the menu is to be activated, in graph
     * coordinates; {@code null} if the menu is not activated at a point
     */
    public JMenu createPopupMenu(@Nullable Point2D atPoint) {
        MyJMenu result = new MyJMenu("Popup");
        result.addSubmenu(createExportMenu());
        result.addSubmenu(createDisplayMenu());
        result.addSubmenu(getLayoutMenu());
        return result;
    }

    /**
     * Returns a menu consisting of the export action of the graph view,
     * preceded by the export items of the context.
     */
    public JMenu createExportMenu() {
        MyJMenu result = new MyJMenu("Export");
        var context = getContext();
        if (context != null) {
            result.addMenuItems(context.getExportItems());
        }
        var exportAction = getExportAction();
        if (exportAction != null) {
            result.add(exportAction);
        }
        return result;
    }

    /**
     * Returns a menu consisting of all the display menu items of the graph view.
     */
    public JMenu createDisplayMenu() {
        MyJMenu result = new MyJMenu("Display");
        var cells = getCanvas().getSelection();
        var context = getContext();
        if (!cells.isEmpty() && context != null) {
            result.addMenuItems(context.getSelectionItems(cells));
        }
        if (result.getItemCount() > 0) {
            result.addSeparator();
        }
        result.add(getModeAction(GraphViewMode.SELECT_MODE));
        result.add(getModeAction(GraphViewMode.PAN_MODE));
        result.add(createShowHideMenu());
        result.add(createZoomMenu());
        return result;
    }

    /**
     * Returns a menu consisting of the menu items from the layouter
     * setting menu of the graph view.
     */
    private SetLayoutMenu getSetLayoutMenu() {
        var result = this.setLayoutMenu;
        if (result == null) {
            this.setLayoutMenu = result = new SetLayoutMenu(this);
        }
        return result;
    }

    /**
     * Selects a layouter for the graph view, as the layouter setting menu does,
     * and returns the action that runs it.
     * @param prototypeLayouter prototype for the new layouter
     * @see #setLayouter(Layouter)
     */
    public Action selectLayouter(Layouter prototypeLayouter) {
        return getSetLayoutMenu().selectLayoutAction(prototypeLayouter);
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
        var context = getContext();
        var dialogAction = context == null
            ? null
            : context.getLayoutDialogAction();
        if (dialogAction != null) {
            result.add(dialogAction);
        }
        return result;
    }

    /**
     * Creates and returns a fresh zoom menu for the graph view.
     */
    public JMenu createZoomMenu() {
        return new ZoomMenu(getCanvas());
    }

    /**
     * Creates and returns a fresh show/hide menu for the graph view.
     */
    public JMenu createShowHideMenu() {
        return new ShowHideMenu<>(getCanvas(), this::getFilterLabels);
    }

    /**
     * Returns the labelled cells of the label filter of this graph view,
     * from which the label sub-menus of the show/hide menu are built.
     */
    private Collection<LabelledCells<G>> getFilterLabels() {
        var context = getContext();
        return context == null
            ? List.of()
            : context.getFilterLabels();
    }

    /**
     * Lazily creates and returns an action setting the mode of the graph view.
     * The actual setting is done by a call to {@link GraphCanvas#setMode}.
     */
    public Action getModeAction(GraphViewMode mode) {
        var modeActionMap = this.modeActionMap;
        if (modeActionMap == null) {
            this.modeActionMap = modeActionMap = new EnumMap<>(GraphViewMode.class);
            for (final GraphViewMode any : GraphViewMode.values()) {
                Action action = new AbstractAction(any.getName(), any.getIcon()) {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        getCanvas().setMode(any);
                    }
                };

                if (any.getAcceleratorKey() != null) {
                    action.putValue(Action.ACCELERATOR_KEY, any.getAcceleratorKey());
                    getCanvas().addAccelerator(action);
                }
                modeActionMap.put(any, action);
            }
        }
        var result = modeActionMap.get(mode);
        assert result != null; // the action map is filled for all modes
        return result;
    }

    private @Nullable Map<GraphViewMode,Action> modeActionMap;

    /**
     * Lazily creates and returns a button wrapping
     * {@link #getModeAction(GraphViewMode)}.
     */
    public JToggleButton getModeButton(GraphViewMode mode) {
        var result = getModeButtonMap().get(mode);
        assert result != null; // the button map is filled for all modes
        return result;
    }

    private Map<GraphViewMode,JToggleButton> getModeButtonMap() {
        var result = this.modeButtonMap;
        if (result == null) {
            this.modeButtonMap = result = new EnumMap<>(GraphViewMode.class);
            ButtonGroup modeButtonGroup = new ButtonGroup();
            for (GraphViewMode any : GraphViewMode.values()) {
                JToggleButton button = new JToggleButton(getModeAction(any));
                Options.setLAF(button);
                button.setToolTipText(any.getName());
                button.setEnabled(getCanvas().isEnabled());
                result.put(any, button);
                modeButtonGroup.add(button);
            }
            var editButton = result.get(GraphViewMode.EDIT_MODE);
            assert editButton != null; // the button map is filled for all modes
            editButton.setSelected(true);
        }
        return result;
    }

    private @Nullable Map<GraphViewMode,JToggleButton> modeButtonMap;

    /** Enables or disables all mode buttons of the graph view. */
    public void setModeButtonsEnabled(boolean enabled) {
        for (JToggleButton button : getModeButtonMap().values()) {
            button.setEnabled(enabled);
        }
    }
}
