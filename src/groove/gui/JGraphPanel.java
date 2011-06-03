/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id: JGraphPanel.java,v 1.20 2008-01-30 09:33:36 iovka Exp $
 */
package groove.gui;

import static groove.gui.jgraph.JGraphMode.PAN_MODE;
import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.Graph;
import groove.graph.Node;
import groove.gui.action.ActionStore;
import groove.gui.jgraph.AspectJEdge;
import groove.gui.jgraph.GraphJCell;
import groove.gui.jgraph.GraphJGraph;
import groove.gui.jgraph.GraphJModel;
import groove.util.Pair;
import groove.view.aspect.AspectGraph;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.LinkedList;
import java.util.List;

import javax.accessibility.AccessibleState;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;

import org.jgraph.JGraph;
import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;

/**
 * A panel that combines a {@link groove.gui.jgraph.GraphJGraph}and (optionally) a
 * {@link groove.gui.LabelTree}.
 * 
 * @author Arend Rensink, updated by Carel van Leeuwen
 * @version $Revision$
 */
public class JGraphPanel<JG extends GraphJGraph> extends JPanel {
    /**
     * Constructs a view upon a given jgraph, possibly with a status bar.
     * 
     * @param jGraph the jgraph on which this panel is a view
     * @param withStatusBar <tt>true</tt> if a status bar should be added to the
     *        panel
     * @ensure <tt>getJGraph() == jGraph</tt>
     */
    public JGraphPanel(JG jGraph, boolean withStatusBar) {
        super(false);
        this.simulator = jGraph.getSimulator();
        setFocusable(false);
        setFocusCycleRoot(true);
        // right now we always want label panels; keep this option
        this.jGraph = jGraph;
        this.options = jGraph.getOptions();
        this.statusBar = withStatusBar ? new JLabel(" ") : null;
    }

    /** 
     * Initialises the GUI.
     * Should be called immediately after the constructor.
     */
    public void initialise() {
        // a JGraphPanel consists of an optional tool bar,
        // a main pane containing the graph, label tree and (possibly)
        // error panel, and an optional status bar.
        setLayout(new BorderLayout());
        add(createMainPane());
        JToolBar toolBar = createToolBar();
        if (toolBar != null) {
            processToolBar(toolBar);
            add(toolBar, BorderLayout.NORTH);
        }
        if (this.statusBar != null) {
            add(this.statusBar, BorderLayout.SOUTH);
        }
        installListeners();
        setEnabled(false);
    }

    /** Callback method that adds the required listeners to this panel. */
    protected void installListeners() {
        addRefreshListener(Options.SHOW_BACKGROUND_OPTION);
        getJGraph().addGraphSelectionListener(new GraphSelectionListener() {
            @Override
            public void valueChanged(GraphSelectionEvent e) {
                getLabelTree().clearSelection();
            }
        });
        getJGraph().addJGraphModeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                getScrollPane().setWheelScrollingEnabled(
                    evt.getNewValue() != PAN_MODE);
            }
        });
    }

    /** Post-processes an already constructed toolbar.
     */
    private void processToolBar(JToolBar toolBar) {
        for (int i = 0; i < toolBar.getComponentCount(); i++) {
            Component element = toolBar.getComponent(i);
            if (element instanceof JButton) {
                JButton button = (JButton) element;
                Action action = button.getAction();
                if (action != null) {
                    getJGraph().addAccelerator(action);
                }
            }
        }
        // ensure the JGraph gets focus as soon as the graph panel
        // is clicked anywhere
        // for reasons not clear to me, mouse listeners do not work on
        // the level of the JGraphPanel
        toolBar.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                getJGraph().requestFocus();
            }
        });
    }

    /**
     * Creates and fills a tool bar for this JGraphPanel.
     * If the method returns {@code null}, no tool bar is used.
     */
    protected JToolBar createToolBar() {
        return null;
    }

    /** Component on which the graph and the label tree are displayed. */
    protected JComponent createMainPane() {
        // set up the split editor pane
        JSplitPane result =
            new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createGraphPane(),
                createLabelPane());
        result.setOneTouchExpandable(true);
        result.setResizeWeight(1.0);
        result.setBorder(null);
        return result;
    }

    /** Creates the right hand side label pane. */
    protected JComponent createLabelPane() {
        JPanel result = new JPanel(new BorderLayout(), false);
        Box labelPaneTop = Box.createVerticalBox();
        JLabel labelPaneTitle =
            new JLabel(" " + Options.LABEL_PANE_TITLE + " ");
        labelPaneTitle.setAlignmentX(LEFT_ALIGNMENT);
        labelPaneTop.add(labelPaneTitle);
        JToolBar labelTreeToolbar = getLabelTree().createToolBar();
        if (labelTreeToolbar != null) {
            labelTreeToolbar.setAlignmentX(LEFT_ALIGNMENT);
            labelPaneTop.add(labelTreeToolbar);
        }
        result.add(labelPaneTop, BorderLayout.NORTH);
        result.add(createLabelScrollPane(getLabelTree()), BorderLayout.CENTER);
        return result;
    }

    /** Creates a scroll pane of the right dimensions around a given component. */
    protected JScrollPane createLabelScrollPane(Component component) {
        return new JScrollPane(component) {
            @Override
            public Dimension getMinimumSize() {
                return new Dimension(MINIMUM_LABEL_PANE_WIDTH, 0);
            }
        };
    }

    /**
     * Creates and returns a new pane, on which only the jgraph is shown (and no
     * label list).
     */
    protected JComponent createGraphPane() {
        // set up the real editor pane
        JScrollPane result = getScrollPane();
        result.setDoubleBuffered(false);
        result.setPreferredSize(new Dimension(500, 400));
        return result;
    }

    /**
     * Lazily creates and returns the scroll pane within which the {@link JGraph}
     * is displayed.
     */
    protected JScrollPane getScrollPane() {
        if (this.scrollPane == null) {
            this.scrollPane = new JScrollPane(getJGraph());
            this.scrollPane.getVerticalScrollBar().setUnitIncrement(10);
        }
        return this.scrollPane;
    }

    /**
     * Returns the underlying {@link GraphJGraph}.
     */
    public JG getJGraph() {
        return this.jGraph;
    }

    /** Returns the label tree displayed on this panel. */
    public LabelTree getLabelTree() {
        return getJGraph().getLabelTree();
    }

    /**
     * Changes the graph model displayed in this panel.
     * Also enables the panel if the new model is not {@code null},
     * and disables it otherwise.  
     */
    public void setJModel(GraphJModel<?,?> jModel) {
        getJGraph().setModel(jModel);
        boolean enabled = jModel != null;
        setEnabled(enabled);
        getTabLabel().setTitle(enabled ? jModel.getName() : null);
        refreshStatus();
    }

    /**
     * Returns the underlying {@link GraphJModel}, or <code>null</code> if the jgraph
     * is currently disabled.
     */
    public GraphJModel<?,?> getJModel() {
        if (isEnabled()) {
            return this.jGraph.getModel();
        } else {
            return null;
        }
    }

    /**
     * Returns the graph that is currently selected (either one of the
     * grammar view's host graphs, or a state).
     */
    public AspectGraph getGraph() {
        return getJModel() == null ? null
                : (AspectGraph) getJModel().getGraph();
    }

    /**
     * Returns the status bar of this panel, if any.
     */
    private JLabel getStatusBar() {
        return this.statusBar;
    }

    /** 
     * Returns the component to be used to fill the tab in a 
     * {@link JTabbedPane}, when this panel is displayed.
     */
    public final TabLabel getTabLabel() {
        if (this.tabLabel == null) {
            this.tabLabel = createTabLabel();
        }
        return this.tabLabel;
    }

    /**
     * Callback method to create a tab component for this panel,
     * in case it is used in a {@link JTabbedPane}.
     */
    protected TabLabel createTabLabel() {
        TabLabel result = new TabLabel(this, null, "");
        return result;
    }

    /**
     * Delegates the method to the content pane and to super.
     * Also sets the background appropriately.
     * @see #getEnabledBackground()
     */
    @Override
    public void setEnabled(boolean enabled) {
        this.jGraph.setEnabled(enabled);
        getLabelTree().setEnabled(enabled);
        if (getStatusBar() != null) {
            getStatusBar().setEnabled(enabled);
        }
        super.setEnabled(enabled);
        Color background = enabled ? getEnabledBackground() : null;
        getJGraph().setBackground(background);
        getLabelTree().setBackground(background);
    }

    /** Callback method to return the background colour in case the panel is enabled.
     * This is {@link Color#WHITE} by default, but may be changed by a call to
     * #setEnabledBackgound.
     * The background colour for an enabled panel; non-{@code null}
     */
    protected Color getEnabledBackground() {
        return this.enabledBackground;
    }

    /** Sets the background colour for an enabled panel. */
    protected void setEnabledBackground(Color enabledBackground) {
        this.enabledBackground = enabledBackground;
    }

    /**
     * If the underlying model is a {@link GraphJModel},
     * selects the element corresponding to a given graph element.
     * @return {@code true} if {@code elem} occurs in the {@link GraphJModel}.
     */
    public boolean selectJCell(Element elem) {
        GraphJCell cell = null;
        if (elem instanceof Node) {
            cell = getJModel().getJCellForNode((Node) elem);
        } else if (elem instanceof Edge) {
            cell = getJModel().getJCellForEdge((Edge<?>) elem);
        }
        if (cell != null) {
            if (cell instanceof AspectJEdge
                && ((AspectJEdge) cell).isSourceLabel()) {
                cell = ((AspectJEdge) cell).getSourceVertex();
            }
            //            getJModel().setEmphasised(Collections.singleton(cell));
            getJGraph().setSelectionCell(cell);
        }
        return cell != null;
    }

    /**
     * Readies the panel for garbage collection, in particular unregistering all
     * listeners.
     */
    public void dispose() {
        removeOptionListeners();
    }

    /**
     * Adds a refresh listener to the menu item associated with for an option
     * with a given name.
     * @see #getRefreshListener()
     */
    protected void addRefreshListener(String option) {
        addOptionListener(option, getRefreshListener());
    }

    /**
     * Returns the refresh listener for this panel. Lazily creates the listener.
     */
    protected final RefreshListener getRefreshListener() {
        if (this.refreshListener == null) {
            this.refreshListener = new RefreshListener();
        }
        return this.refreshListener;
    }

    /**
     * Adds a listener to the menu item associated with for an option with a
     * given name. Throws an exception if no such option was in the options
     * object passed in at construction time.
     */
    private void addOptionListener(String option, RefreshListener listener) {
        JMenuItem optionItem = getOptionsItem(option);
        if (optionItem == null) {
            throw new IllegalArgumentException(String.format(
                "Unknown option: %s", option));
        }
        optionItem.addItemListener(listener);
        optionItem.addPropertyChangeListener(listener);
        this.listeners.add(new Pair<JMenuItem,ItemListener>(optionItem,
            listener));
    }

    /**
     * Removes all listeners added by
     * {@link #addOptionListener(String, RefreshListener)}.
     */
    private void removeOptionListeners() {
        for (Pair<JMenuItem,ItemListener> record : this.listeners) {
            record.one().removeItemListener(record.two());
        }
        this.listeners.clear();
    }

    /**
     * Refreshes everything on the panel, for instance in reaction to a change
     * in one of the visualisation options. This implementation calls
     * {@link GraphJGraph#refreshAllCells()} and {@link #refreshStatus()}.
     */
    protected void refresh() {
        getJGraph().refreshAllCells();
        getJGraph().setEnabled(getJModel() != null);
        getLabelTree().updateModel();
        refreshStatus();
    }

    /**
     * If the panel has a status bar, refreshes it with the text obtained from
     * {@link #getStatusText()}.
     */
    protected void refreshStatus() {
        if (getStatusBar() != null) {
            getStatusBar().setText(getStatusText());
        }
    }

    /**
     * Callback method from {@link #refreshStatus()} to obtain the current
     * status text, which is to be printed on the status bar (if any). This
     * implementation returns the empty string.
     */
    protected String getStatusText() {
        return "";
    }

    /**
     * Returns the options object passed in at construction time.
     */
    protected final Options getOptions() {
        return this.options;
    }

    /**
     * Retrieves the options item for a given option name, creating it first if
     * necessary.
     */
    protected JMenuItem getOptionsItem(String option) {
        Options options = getOptions();
        return options == null ? null : options.getItem(option);
    }

    /** Returns the simulator of which this panel is part. */
    public final Simulator getSimulator() {
        return this.simulator;
    }

    /** Convenience method to retrieve the simulator model. */
    protected final SimulatorModel getSimulatorModel() {
        return getSimulator().getModel();
    }

    /** Convenience method to retrieve the simulator model. */
    protected final ActionStore getActions() {
        return getSimulator().getActions();
    }

    private final Simulator simulator;
    /**
     * The {@link GraphJGraph}on which this panel provides a view.
     */
    private final JG jGraph;
    /** The background colour in case the panel is enabled. */
    private Color enabledBackground = Color.WHITE;
    /** Options for this panel. */
    private final Options options;
    /** The component that constitutes the tab when this panel is used in a {@link JTabbedPane}. */
    private TabLabel tabLabel;
    /** Change listener that calls {@link #refresh()} when activated. */
    private RefreshListener refreshListener;
    /**
     * Panel for showing status messages
     */
    private final JLabel statusBar;

    private final List<Pair<JMenuItem,ItemListener>> listeners =
        new LinkedList<Pair<JMenuItem,ItemListener>>();
    /**
     * The scroll pane in which the JGraph is displayed.
     */
    private JScrollPane scrollPane;

    /** Shows a dialog displaying an arbitrary JGraph. */
    public static void displayGraph(GraphJGraph jGraph) {
        jGraph.doGraphLayout();
        JGraphPanel<?> panel = new JGraphPanel<GraphJGraph>(jGraph, false);
        panel.initialise();
        panel.setEnabled(true);
        JDialog dialog =
            new JDialog((JDialog) null, "Graph " + jGraph.getName(), false);
        dialog.add(panel);
        dialog.pack();
        dialog.setVisible(true);
    }

    /** Shows a dialog displaying an arbitrary GROOVE graph. */
    public static void displayGraph(Graph<?,?> graph) {
        displayGraph(GraphJGraph.createJGraph(graph));
    }

    /** Shows a dialog displaying an arbitrary GROOVE graph, with
     * some control over the display attributes of the graph elements. */
    public static void displayGraph(Graph<?,?> graph,
            GraphJGraph.AttributeFactory factory) {
        displayGraph(GraphJGraph.createJGraph(graph, factory));
    }

    /**
     * The minimum width of the label pane. If the label list is empty, the
     * preferred width is set to the minimum width.
     */
    public final static int MINIMUM_LABEL_PANE_WIDTH = 100;

    private class RefreshListener implements ItemListener,
            PropertyChangeListener {
        public void itemStateChanged(ItemEvent e) {
            if (isEnabled()) {
                refresh();
            }
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals(
                AccessibleState.ENABLED.toDisplayString())) {
                if (isEnabled()) {
                    refresh();
                }
            }
        }
    }
}
