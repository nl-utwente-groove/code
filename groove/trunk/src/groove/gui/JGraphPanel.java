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

import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.Node;
import groove.gui.jgraph.GraphJEdge;
import groove.gui.jgraph.GraphJModel;
import groove.gui.jgraph.JCell;
import groove.gui.jgraph.JGraph;
import groove.gui.jgraph.JModel;
import groove.util.Pair;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.LinkedList;
import java.util.List;

import javax.accessibility.AccessibleState;
import javax.swing.Box;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;

/**
 * A panel that combines a {@link groove.gui.jgraph.JGraph}and (optionally) a
 * {@link groove.gui.LabelTree}.
 * 
 * @author Arend Rensink, updated by Carel van Leeuwen
 * @version $Revision$
 */
public class JGraphPanel<JG extends JGraph> extends JPanel {
    /**
     * Constructs a view upon a given jgraph, possibly with a status bar.
     * 
     * @param jGraph the jgraph on which this panel is a view
     * @param withStatusBar <tt>true</tt> if a status bar should be added to the
     *        panel
     * @param supportsSubtypes if <code>true</code>, the label tree of this
     *        panel will support subtypes
     * @param options Options object used to create menu item listeners. If
     *        <code>null</code>, no listeners are created.
     * @ensure <tt>getJGraph() == jGraph</tt>
     */
    public JGraphPanel(JG jGraph, boolean withStatusBar,
            boolean supportsSubtypes, Options options) {
        super(false);
        // right now we always want label panels; keep this option
        boolean withLabelPanel = true;
        this.jGraph = jGraph;
        this.labelTree = jGraph.initLabelTree(supportsSubtypes);
        this.options = options;
        this.statusBar = withStatusBar ? new JLabel(" ") : null;
        this.viewLabelListItem =
            withLabelPanel ? this.createViewLabelListItem() : null;
        setLayout(new BorderLayout());
        this.setPane(withLabelPanel ? createSplitPane() : this.createSoloPane());
        if (this.statusBar != null) {
            add(this.statusBar, BorderLayout.SOUTH);
        }
        addRefreshListener(Options.SHOW_BACKGROUND_OPTION);
    }

    /**
     * Returns a menu item that allows to switch the label list view on and off.
     */
    public JMenuItem getViewLabelListItem() {
        return this.viewLabelListItem;
    }

    /**
     * Returns the underlying {@link JGraph}.
     */
    public JG getJGraph() {
        return this.jGraph;
    }

    /** Returns the label tree displayed on this panel. */
    public LabelTree getLabelTree() {
        return this.labelTree;
    }

    /**
     * Returns the underlying {@link JModel}, or <code>null</code> if the jgraph
     * is currently disabled.
     */
    public JModel getJModel() {
        if (isEnabled()) {
            return this.jGraph.getModel();
        } else {
            return null;
        }
    }

    /**
     * Returns the status bar of this panel, if any.
     */
    public JLabel getStatusBar() {
        return this.statusBar;
    }

    /**
     * Delegates the method to the content pane and to super.
     */
    @Override
    public void setEnabled(boolean enabled) {
        this.jGraph.setEnabled(enabled);
        this.statusBar.setEnabled(enabled);
        super.setEnabled(enabled);
    }

    /**
     * If the underlying model is a {@link GraphJModel},
     * selects the element corresponding to a given graph element.
     * @return {@code true} if {@code elem} occurs in the {@link GraphJModel}.
     */
    public boolean selectJCell(Element elem) {
        JCell cell = null;
        if (getJModel() instanceof GraphJModel) {
            @SuppressWarnings("unchecked")
            GraphJModel<Node,Edge> graphJModel =
                (GraphJModel<Node,Edge>) getJModel();
            if (elem instanceof Node) {
                cell = graphJModel.getJCellForNode((Node) elem);
            } else if (elem instanceof Edge) {
                cell = graphJModel.getJCellForEdge((Edge) elem);
            }
            if (cell != null) {
                if (cell instanceof GraphJEdge
                    && ((GraphJEdge<?,?>) cell).isSourceLabel()) {
                    cell = ((GraphJEdge<?,?>) cell).getSourceVertex();
                }
                getJGraph().setSelectionCell(cell);
            }
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
     * Creates and returns a new pane, on which only the jgraph is shown (and no
     * label list).
     */
    protected JComponent createSoloPane() {
        // set up the real editor pane
        JScrollPane result = new JScrollPane(this.jGraph);
        result.setDoubleBuffered(false);
        result.setPreferredSize(new Dimension(500, 400));
        return result;
    }

    /**
     * Creates and returns a new split pane, on which both the jgraph and label
     * list are shown.
     */
    protected JComponent createSplitPane() {
        Box labelPaneTop = Box.createVerticalBox();
        JLabel labelPaneTitle =
            new JLabel(" " + Options.LABEL_PANE_TITLE + " ");
        labelPaneTitle.setAlignmentX(LEFT_ALIGNMENT);
        labelPaneTop.add(labelPaneTitle);
        JToolBar labelTreeToolbar = this.labelTree.createToolBar();
        if (labelTreeToolbar != null) {
            labelTreeToolbar.setAlignmentX(LEFT_ALIGNMENT);
            labelPaneTop.add(labelTreeToolbar);
        }
        JScrollPane scrollPane = new JScrollPane(this.labelTree) {
            @Override
            public Dimension getMinimumSize() {
                return new Dimension(MINIMUM_LABEL_PANE_WIDTH, 0);
            }
        };
        JPanel labelPane = new JPanel(new BorderLayout(), false);
        labelPane.add(labelPaneTop, BorderLayout.NORTH);
        labelPane.add(scrollPane, BorderLayout.CENTER);
        // set up the split editor pane
        JSplitPane result =
            new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createSoloPane(),
                labelPane);
        result.setOneTouchExpandable(true);
        result.setResizeWeight(1.0);
        return result;
    }

    /**
     * Creates a menu item that allows to add the label list to the panel.
     */
    protected JMenuItem createViewLabelListItem() {
        JCheckBoxMenuItem result = new JCheckBoxMenuItem("Label list", true);
        result.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent evt) {
                if (evt.getStateChange() == ItemEvent.SELECTED) {
                    setPane(createSplitPane());
                    // splitEditorPane.revalidate();
                } else {
                    setPane(createSoloPane());
                    // realEditorPane.revalidate();
                }
                revalidate();
            }
        });
        return result;
    }

    /**
     * Adds a given component as center component to the panel. Removes the
     * previous pane, if any, and revalidates the panel. The current pane is
     * stored in {@link #currentPane}.
     */
    protected void setPane(JComponent editorPane) {
        if (this.currentPane != null) {
            remove(this.currentPane);
        }
        add(editorPane, BorderLayout.CENTER);
        this.currentPane = editorPane;
        revalidate();
    }

    /** Returns the component inside the panel. */
    protected JComponent getPane() {
        return this.currentPane;
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
     * {@link JModel#refresh()} and {@link #refreshStatus()}.
     */
    protected void refresh() {
        JModel jModel = getJModel();
        if (jModel != null) {
            jModel.refresh();
        }
        getJGraph().setEnabled(jModel != null);
        getJGraph().clearSelection();
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

    /**
     * The {@link JGraph}on which this panel provides a view.
     */
    protected final JG jGraph;
    /** The label tree associated with this label pane. */
    private final LabelTree labelTree;
    /** Options for this panel. */
    private final Options options;
    /** Change listener that calls {@link #refresh()} when activated. */
    private RefreshListener refreshListener;
    /**
     * Panel for showing status messages
     */
    private final JLabel statusBar;

    /** The menu item to switch the label list on and off. */
    private final JMenuItem viewLabelListItem;

    private final List<Pair<JMenuItem,ItemListener>> listeners =
        new LinkedList<Pair<JMenuItem,ItemListener>>();
    /**
     * The editor pane most recently installed by {@link #setPane}.
     */
    private JComponent currentPane;

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
