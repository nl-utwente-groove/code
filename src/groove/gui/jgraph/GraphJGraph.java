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
 * $Id: JGraph.java,v 1.30 2008-02-05 13:27:59 rensink Exp $
 */
package groove.gui.jgraph;

import static groove.gui.jgraph.JGraphMode.EDIT_MODE;
import static groove.gui.jgraph.JGraphMode.PAN_MODE;
import static groove.gui.jgraph.JGraphMode.SELECT_MODE;
import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.Graph;
import groove.graph.GraphRole;
import groove.graph.Node;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.gui.SimulatorModel;
import groove.gui.action.ActionStore;
import groove.gui.action.ExportAction;
import groove.gui.action.LayoutAction;
import groove.gui.layout.ForestLayouter;
import groove.gui.layout.Layouter;
import groove.gui.layout.SpringLayouter;
import groove.gui.look.MultiLabel;
import groove.gui.look.VisualKey;
import groove.gui.look.VisualMap;
import groove.gui.look.VisualValue;
import groove.gui.menu.SetLayoutMenu;
import groove.gui.menu.ShowHideMenu;
import groove.gui.menu.ZoomMenu;
import groove.gui.tree.LabelTree;
import groove.trans.SystemProperties;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ButtonGroup;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JToggleButton;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.ToolTipManager;

import org.jgraph.JGraph;
import org.jgraph.event.GraphModelEvent;
import org.jgraph.event.GraphModelListener;
import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.BasicMarqueeHandler;
import org.jgraph.graph.CellView;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;
import org.jgraph.graph.PortView;
import org.jgraph.plaf.GraphUI;
import org.jgraph.plaf.basic.BasicGraphUI;

/**
 * Enhanced j-graph, dedicated to j-models.
 * @author Arend Rensink
 * @version $Revision$ $Date: 2008-02-05 13:27:59 $
 */
public class GraphJGraph extends org.jgraph.JGraph {
    /**
     * Constructs a JGraph for a given simulator.
     * @param simulator simulator to which the JGraph belongs.
     * @param filtering indicates if this JGraph is to use label filtering.
     */
    public GraphJGraph(Simulator simulator, boolean filtering) {
        super((GraphJModel<?,?>) null);
        this.simulator = simulator;
        this.options =
            simulator == null ? new Options() : simulator.getOptions();
        this.filtering = filtering;
        // make sure the layout cache has been created
        getGraphLayoutCache().setSelectsAllInsertedCells(false);
        setMarqueeHandler(createMarqueeHandler());
        // Make Ports invisible by Default
        setPortsVisible(false);
        // Save edits to a cell whenever something else happens
        setInvokesStopCellEditing(true);
        addMouseListener(new MyMouseListener());
        addKeyListener(getCancelEditListener());
        getSelectionModel().addGraphSelectionListener(
            new MyGraphSelectionListener());
        setEditable(false);
        setConnectable(false);
        setDisconnectable(false);
    }

    /** Returns the graph role of the graphs expected for this JGraph. */
    public GraphRole getGraphRole() {
        return GraphRole.NONE;
    }

    /** Indicates if the JGraph allows filtering of labels. */
    final public boolean isFiltering() {
        return this.filtering;
    }

    /**
     * Indicates if a given graph element is currently being filtered from view. This is
     * the case if it is in the set of filtered labels.
     */
    public boolean isFiltering(Element jCellKey) {
        return getLabelTree().isFiltered(jCellKey);
    }

    /** Returns the object holding the display options for this {@link GraphJGraph}. */
    public final Options getOptions() {
        return this.options;
    }

    /**
     * Retrieves the value for a given option from the options object, or
     * <code>null</code> if the options are not set (i.e., <code>null</code>).
     * @param option the name of the option
     */
    public boolean getOptionValue(String option) {
        return getOptions().getItem(option).isEnabled()
            && getOptions().isSelected(option);
    }

    /**
     * Indicates whether aspect prefixes should be shown for nodes and edges.
     */
    public boolean isShowNodeIdentities() {
        return getOptionValue(Options.SHOW_NODE_IDS_OPTION);
    }

    /**
     * Indicates whether unfiltered edges to filtered nodes should remain
     * visible.
     */
    public boolean isShowUnfilteredEdges() {
        return getOptionValue(Options.SHOW_UNFILTERED_EDGES_OPTION);
    }

    /**
     * Indicates whether anchors should be shown in the rule and lts views.
     */
    public boolean isShowAnchors() {
        return getOptionValue(Options.SHOW_ANCHORS_OPTION);
    }

    /**
     * Indicates whether self-edges should be shown as node labels.
     */
    public boolean isShowLoopsAsNodeLabels() {
        return getProperties() == null || getProperties().isShowLoopsAsLabels();
    }

    /**
     * Indicates whether arrow head should be shown on labels, rather than
     * on edges. 
     */
    public boolean isShowArrowsOnLabels() {
        return getOptionValue(Options.SHOW_ARROWS_ON_LABELS_OPTION);
    }

    /** 
     * The properties of the grammar to which the displayed graph belongs.
     * May return {@code null} if the simulator is not set.
     */
    private SystemProperties getProperties() {
        return getSimulatorModel() == null ? null
                : getSimulatorModel().getGrammar().getProperties();
    }

    /** Convenience method to retrieve the state of the simulator, if any. */
    final public SimulatorModel getSimulatorModel() {
        return this.simulator == null ? null : this.simulator.getModel();
    }

    /** Convenience method to retrieve the state of the simulator, if any. */
    final public ActionStore getActions() {
        return this.simulator == null ? null : this.simulator.getActions();
    }

    /*
     * Overridden; we are being clever about constructing labels,
     * this method should be bypassed.
     * Overrides the method to call {@link GraphJCell#getVisuals} whenever
     * <code>object</code> is recognised as a {@link JVertexView},
     * {@link JEdgeView} or {@link GraphJCell}.
     */
    @Override
    public String convertValueToString(Object value) {
        String result = null;
        if (value instanceof String) {
            result = (String) value;
        } else if (value instanceof GraphJVertex) {
            MultiLabel label = ((GraphJVertex) value).getVisuals().getLabel();
            result = label.toString();
        } else if (value instanceof JEdgeView) {
            MultiLabel label =
                ((JEdgeView) value).getCell().getVisuals().getLabel();
            result = label.toString();
        }
        if (result == null || result.length() == 0) {
            result = " ";
        }
        return result;
    }

    /**
     * Returns a tool tip text for the front graph cell onder the mouse.
     */
    @Override
    public String getToolTipText(MouseEvent evt) {
        GraphJCell jCell = getFirstCellForLocation(evt.getX(), evt.getY());
        if (jCell != null && jCell.getVisuals().isVisible()) {
            return jCell.getToolTipText();
        } else {
            return null;
        }
    }

    /**
     * Overrides the super method to make sure hidden cells ae never editable.
     * If the specified cell is hidden (according to the underlying model),
     * returns false; otherwise, passes on the query to super.
     * @see GraphJCell#isGrayedOut()
     */
    @Override
    public boolean isCellEditable(Object cell) {
        return !(cell instanceof GraphJCell && ((GraphJCell) cell).isGrayedOut())
            && super.isCellEditable(cell);
    }

    /**
     * Overwrites the method from JGraph for efficiency.
     */
    @Override
    public Object[] getDescendants(Object[] cells) {
        List<Object> res = new LinkedList<Object>();
        for (Object element : cells) {
            res.add(element);
            if (element instanceof GraphJVertex) {
                res.add(((GraphJVertex) element).getChildAt(0));
            }
        }
        return res.toArray();
    }

    /**
     * Overwritten to freeze nodes to their center on
     * size changes.
     */
    @Override
    public void updateAutoSize(CellView view) {
        if (view != null && !isEditing()) {
            Rectangle2D bounds =
                (view.getAttributes() != null)
                        ? GraphConstants.getBounds(view.getAttributes()) : null;
            AttributeMap attrs = getModel().getAttributes(view.getCell());
            if (bounds == null) {
                bounds = GraphConstants.getBounds(attrs);
            }
            if (bounds != null) {
                boolean autosize =
                    GraphConstants.isAutoSize(view.getAllAttributes());
                boolean resize =
                    GraphConstants.isResize(view.getAllAttributes());
                if (autosize || resize) {
                    Dimension2D d = getUI().getPreferredSize(this, view);
                    // adjust the x,y corner so that the center stays in place
                    double shiftX = (bounds.getWidth() - d.getWidth()) / 2;
                    double shiftY = (bounds.getHeight() - d.getHeight()) / 2;
                    bounds.setFrame(bounds.getX() + shiftX, bounds.getY()
                        + shiftY, d.getWidth(), d.getHeight());
                    // Remove resize attribute
                    snap(bounds);
                    if (resize) {
                        if (view.getAttributes() != null) {
                            view.getAttributes().remove(GraphConstants.RESIZE);
                        }
                        attrs.remove(GraphConstants.RESIZE);
                    }
                    view.refresh(getGraphLayoutCache(), getGraphLayoutCache(),
                        false);
                }
            }
        }
    }

    /**
     * @return the bounds of the entire display.
     */
    public Rectangle2D getGraphBounds() {
        return getCellBounds(getRoots());
    }

    /** Refreshes the visibility and view of a given set of JCells. */
    public void refreshCells(final Collection<? extends GraphJCell> jCellSet) {
        if (!jCellSet.isEmpty()) {
            JGraphLayoutCache cache = getGraphLayoutCache();
            Collection<GraphJCell> visibleCells =
                new HashSet<GraphJCell>(jCellSet.size());
            Collection<GraphJCell> hiddenCells =
                new HashSet<GraphJCell>(jCellSet.size());
            for (GraphJCell jCell : jCellSet) {
                CellView jView = cache.getMapping(jCell, false);
                boolean wasVisible = jView != null;
                boolean isVisible = jCell.getVisuals().isVisible();
                Collection<GraphJCell> changeCells =
                    wasVisible ? hiddenCells : visibleCells;
                if (isVisible != wasVisible) {
                    changeCells.add(jCell);
                    // test context for visibility
                    for (GraphJCell c : jCell.getContext()) {
                        if (c.getVisuals().isVisible() != wasVisible) {
                            changeCells.add(c);
                        }
                    }
                }
                // add the cell in any case if it is visible,
                // to allow the view to refreshed itself
                if (isVisible) {
                    visibleCells.add(jCell);
                }
            }
            GraphJGraph.this.modelRefreshing = true;
            Object[] visibleArray = visibleCells.toArray();
            Object[] hiddenArray = hiddenCells.toArray();
            // unselect all hidden cells
            getSelectionModel().removeSelectionCells(hiddenArray);
            // make sure refreshed cells are not selected
            boolean selectsInsertedCells = cache.isSelectsLocalInsertedCells();
            cache.setSelectsLocalInsertedCells(false);
            cache.setVisible(visibleArray, hiddenArray);
            cache.setSelectsLocalInsertedCells(selectsInsertedCells);
            if (getSelectionCount() > 0) {
                Rectangle2D scope =
                    (Rectangle2D) getCellBounds(getSelectionCells()).clone();
                if (scope != null) {
                    scrollRectToVisible(toScreen(scope).getBounds());
                }
            }
            GraphJGraph.this.modelRefreshing = false;
        }
    }

    /** Refreshes the visibility and view of all JCells in the model. */
    public void refreshAllCells() {
        if (getModel() != null) {
            refreshCells(getModel().getRoots());
        }
    }

    /**
     * Changes the grayed-out status of a given set of jgraph cells.
     * @param jCells the cells whose hiding status is to be changed
     * @param grayedOut the new grayed-out status of the cell
     * @see GraphJCell#isGrayedOut()
     */
    public void changeGrayedOut(Set<GraphJCell> jCells, boolean grayedOut) {
        Set<GraphJCell> changedJCells = new HashSet<GraphJCell>();
        for (GraphJCell jCell : jCells) {
            if (jCell.setGrayedOut(grayedOut)) {
                changedJCells.add(jCell);
                if (grayedOut && jCell instanceof GraphJVertex) {
                    // also gray out incident edges
                    for (GraphJCell c : jCell.getContext()) {
                        if (c.setGrayedOut(true)) {
                            changedJCells.add(c);
                        }
                    }
                } else if (!grayedOut && jCell instanceof GraphJEdge) {
                    // also revive end nodes
                    for (GraphJCell c : jCell.getContext()) {
                        if (c.setGrayedOut(false)) {
                            changedJCells.add(c);
                        }
                    }
                }
            }
        }
        getModel().toBackSilent(changedJCells);
        refreshCells(changedJCells);
    }

    /** 
     * Indicates if this {@link GraphJGraph} is in the course of processing
     * a {@link #refreshCells(Collection)}. This allows listeners to ignore the
     * resulting graph view update, if they wish.
     */
    public boolean isModelRefreshing() {
        return this.modelRefreshing;
    }

    /**
     * Helper method for {@link #getFirstCellForLocation(double, double)} and
     * {@link #getPortViewAt(double, double)}. Returns the topmost visible cell
     * at a given point. A flag controls if we want only vertices or only edges.
     * @param x x-coordinate of the location we want to find a cell at
     * @param y y-coordinate of the location we want to find a cell at
     * @param vertex <tt>true</tt> if we are not interested in edges
     * @param edge <tt>true</tt> if we are not interested in vertices
     * @return the topmost visible cell at a given point
     */
    protected GraphJCell getFirstCellForLocation(double x, double y,
            boolean vertex, boolean edge) {
        x /= this.scale;
        y /= this.scale;
        GraphJCell result = null;
        Rectangle xyArea = new Rectangle((int) (x - 2), (int) (y - 2), 4, 4);
        // iterate over the roots and query the visible ones
        CellView[] viewRoots = this.graphLayoutCache.getRoots();
        for (int i = viewRoots.length - 1; result == null && i >= 0; i--) {
            CellView jCellView = viewRoots[i];
            if (!(jCellView.getCell() instanceof GraphJCell)) {
                continue;
            }
            GraphJCell jCell = (GraphJCell) jCellView.getCell();
            boolean typeCorrect =
                vertex ? jCell instanceof GraphJVertex : edge
                        ? jCell instanceof GraphJEdge : true;
            if (typeCorrect && !jCell.isGrayedOut()) {
                // now see if this jCell is sufficiently close to the point
                if (jCellView.intersects(this, xyArea)) {
                    result = jCell;
                }
            }
        }
        return result;
    }

    /**
     * Overrides the super method for greater efficiency. Only returns visible
     * cells.
     */
    @Override
    public GraphJCell getFirstCellForLocation(double x, double y) {
        return getFirstCellForLocation(x, y, false, false);
    }

    /**
     * This method returns the port of the topmost vertex.
     */
    @Override
    public PortView getPortViewAt(double x, double y) {
        GraphJVertex vertex =
            (GraphJVertex) getFirstCellForLocation(x, y, true, false);
        if (vertex != null) {
            return (PortView) getGraphLayoutCache().getMapping(
                vertex.getPort(), false);
        } else {
            return null;
        }
    }

    /**
     * Overwrites the super implementation to add the following functionality:
     * <ul>
     * <li>The selection is cleared
     * <li>the layout action is stopped for the old model
     * <li>the popup menu is re-initialised
     * <li>the layout action is started for the new model
     * </ul>
     */
    @Override
    public void setModel(GraphModel model) {
        if (model == null || model instanceof GraphJModel<?,?>) {
            GraphJModel<?,?> oldJModel = getModel();
            GraphJModel<?,?> newJModel = (GraphJModel<?,?>) model;
            if (oldJModel != null) {
                if (getLayouter() != null) {
                    getLayouter().stop();
                }
                // if we don't clear the selection, the old selection
                // gives trouble when setting the model
                clearSelection();
                oldJModel.removeGraphModelListener(getCancelEditListener());
            }
            if (newJModel != null) {
                newJModel.refreshVisuals();
            }
            super.setModel(newJModel);
            if (newJModel != null) {
                setName(newJModel.getName());
            }
            if (newJModel != null) {
                Layouter layouter = doLayout(false);
                if (layouter != null) {
                    final Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            getLayouter().stop();
                            // cancel the timer, because it may otherwise
                            // keep the entire program from terminating
                            timer.cancel();
                        }
                    }, MAX_LAYOUT_DURATION);
                }
            }
            if (newJModel != null) {
                newJModel.addGraphModelListener(getCancelEditListener());
            }
            setEnabled(newJModel != null);
            if (newJModel != null && getActions() != null) {
                // create the popup menu to create and activate the actions therein
                createPopupMenu(null);
            }
        }
    }

    /** Specialises the return type to a {@link GraphJModel}. */
    @Override
    public GraphJModel<?,?> getModel() {
        return (GraphJModel<?,?>) this.graphModel;
    }

    /** Callback factory method to create an appropriate JModel
     * instance for this JGraph.
     */
    public GraphJModel<?,?> newModel() {
        return getFactory().newModel();
    }

    /**
     * Returns the factory for JGraph-related objects.
     * The factory is initialised either through {@link #createFactory()}
     * or through {@link #setFactory(JGraphFactory)}
     * @return the factory to be used for this JGraph
     */
    final public JGraphFactory getFactory() {
        if (this.factory == null) {
            this.factory = createFactory();
        }
        return this.factory;
    }

    /** 
     * Sets the factory to be used.
     * Must be called before the first invocation of {@link #getFactory()}.
     */
    final public void setFactory(JGraphFactory factory) {
        assert this.factory == null;
        this.factory = factory;
    }

    /** Callback factory method for the JGraphFactory to be used. */
    protected JGraphFactory createFactory() {
        return new GraphJGraphFactory(this);
    }

    private JGraphFactory factory;

    /**
     * In addition to delegating the method to the label list and to
     * <tt>super</tt>, sets the background color to <tt>null</tt> when disabled
     * and back to the default when enabled.
     */
    @Override
    public void setEnabled(boolean enabled) {
        if (enabled != isEnabled()) {
            if (!enabled) {
                this.enabledBackground = getBackground();
                setBackground(null);
            } else if (this.enabledBackground != null) {
                setBackground(this.enabledBackground);
            }
            getLabelTree().setEnabled(enabled);
            for (JToggleButton button : getModeButtonMap().values()) {
                button.setEnabled(enabled);
            }
            getModeButton(getDefaultMode()).setSelected(true);
            // retrieve the layout action to get its key accelerator working
            getLayoutAction();
            super.setEnabled(enabled);
        }
    }

    /**
     * Sets a graph UI that speeds up preferred size checking by caching
     * previous values.
     */
    @Override
    public void updateUI() {
        GraphUI ui = createGraphUI();
        setUI(ui);
        invalidate();
    }

    /**
     * Creates a graph UI that speeds up preferred size checking by caching
     * previously computed values.
     */
    protected BasicGraphUI createGraphUI() {
        return new JGraphUI();
    }

    @Override
    public JGraphUI getUI() {
        return (JGraphUI) super.getUI();
    }

    /** 
     * Returns the nearest ancestor that is a {@link JViewport},
     * if there is any.
     */
    protected JViewport getViewPort() {
        JViewport result = null;
        for (JComponent parent = this; parent != null && result == null; parent =
            (JComponent) parent.getParent()) {
            if (parent instanceof JViewport) {
                result = (JViewport) parent;
            }
        }
        return result;
    }

    /**
     * Creates and returns an image of the jgraph, or <tt>null</tt> if the
     * jgraph is empty.
     * @return an image object of the jgraph; <tt>null</tt> if this jgraph is
     *         empty.
     */
    public BufferedImage toImage() {
        Rectangle2D bounds = getGraphBounds();

        if (bounds != null) {
            toScreen(bounds);
            // insert some extra space at the borders
            int extraSpace = 5;
            // Create a Buffered Image
            BufferedImage img =
                new BufferedImage((int) bounds.getWidth() + 2 * extraSpace,
                    (int) bounds.getHeight() + 2 * extraSpace,
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = img.createGraphics();
            graphics.setColor(getBackground());
            graphics.fillRect(0, 0, img.getWidth(), img.getHeight());
            graphics.translate(-bounds.getX() + extraSpace, -bounds.getY()
                + extraSpace);

            Object[] selection = getSelectionCells();
            boolean gridVisible = isGridVisible();
            setGridVisible(false);
            clearSelection();

            paint(graphics);

            setSelectionCells(selection);
            setGridVisible(gridVisible);

            return img;
        }
        return null;
    }

    /**
     * @return the current layouter for this JGraph.
     * @see #setLayouter(Layouter)
     */
    public Layouter getLayouter() {
        if (this.layouter == null) {
            this.layouter = createLayouter().newInstance(this);
        }
        return this.layouter;
    }

    /** Prototype factory method to create a layouter for this JGraph. */
    protected Layouter createLayouter() {
        return new ForestLayouter();
    }

    /**
     * Sets (but does not start) the layout action for this JGraph. First stops
     * the current layout action, if it is running.
     * @param prototypeLayouter prototype for the new layout action; the actual
     *        layout action is obtained by calling <tt>newInstance(this)</tt>
     * @see #getLayouter()
     */
    public void setLayouter(Layouter prototypeLayouter) {
        if (this.layouter != null) {
            this.layouter.stop();
        }
        this.layouter = prototypeLayouter.newInstance(this);
    }

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
        Layouter result = null;
        // Test if we do any layouting, or complete layouting
        // any means there is a layoutable
        boolean any = complete;
        // all means all cells are layoutable
        boolean all = true;
        for (GraphJCell jCell : getModel().getRoots()) {
            if (jCell.isLayoutable()) {
                any = true;
                if (!all) {
                    break;
                }
            } else {
                all = false;
                if (any) {
                    break;
                }
            }
        }
        if (any) {
            complete |= all;
            if (complete) {
                result = getLayouter();
            }
            if (result == null) {
                result = this.incrementalLayouter;
            }
            result.start(complete);
        }
        return result;
    }

    /** Adds a listener to {@link #setMode(JGraphMode)} calls. */
    public void addJGraphModeListener(PropertyChangeListener listener) {
        addPropertyChangeListener(JGRAPH_MODE_PROPERTY, listener);
    }

    /** Removes a listener to {@link #setMode(JGraphMode)} calls. */
    public void removeJGraphModeListener(PropertyChangeListener listener) {
        removePropertyChangeListener(JGRAPH_MODE_PROPERTY, listener);
    }

    /** Removes this {@link JGraph} from all its listeners,
     * so as to avoid memory leaks. 
     */
    public void removeFromListeners() {
        getActions().removeRefreshable(getExportAction());
        this.exportAction = null;
    }

    /**
     * Sets the JGraph mode to a new value.
     * Fires a property change event for {@link #JGRAPH_MODE_PROPERTY} if the
     * mode was changed.
     * @return {@code true} if the JGraph mode was changed as a result
     * of this call
     */
    public boolean setMode(JGraphMode mode) {
        JGraphMode oldMode = this.mode;
        boolean result = mode != oldMode;
        // set the value if it has changed
        if (result) {
            this.mode = mode;
            if (mode == EDIT_MODE) {
                clearSelection();
            }
            stopEditing();
            getModeButton(mode).setSelected(true);
            setCursor(mode.getCursor());
            // fire change only if there was a previous value
            firePropertyChange(JGRAPH_MODE_PROPERTY, oldMode, mode);
        }
        return result;
    }

    /** 
     * Returns the current JGraph mode.
     */
    public JGraphMode getMode() {
        if (this.mode == null) {
            this.mode = getDefaultMode();
        }
        return this.mode;
    }

    /** Callback method to create the default initial mode for this JGraph. */
    protected JGraphMode getDefaultMode() {
        return SELECT_MODE;
    }

    /**
     * Indicates whether this jgraph is currently registered at the tool tip
     * manager.
     * @return <tt>true</tt> if this jgraph is currently registered at the tool
     *         tip manager
     */
    public boolean getToolTipEnabled() {
        return this.toolTipEnabled;
    }

    /**
     * Registers ur unregisters this jgraph with the tool tip manager. The
     * current registration state can be queried using
     * <tt>getToolTipEnabled()</tt>
     * @param enabled <tt>true</tt> if this jgraph is to be registered with the
     *        tool tip manager
     * @see #getToolTipEnabled()
     * @see ToolTipManager#registerComponent(javax.swing.JComponent)
     * @see ToolTipManager#unregisterComponent(javax.swing.JComponent)
     */
    public void setToolTipEnabled(boolean enabled) {
        if (enabled) {
            ToolTipManager.sharedInstance().registerComponent(this);
        } else {
            ToolTipManager.sharedInstance().unregisterComponent(this);
        }
        this.toolTipEnabled = enabled;
    }

    /**
     * Lazily creates and returns the label list associated with this jgraph.
     */
    public LabelTree getLabelTree() {
        if (this.labelTree == null) {
            this.labelTree = createLabelTree();
            this.labelTree.getFilter().addObserver(new Observer() {
                /** The method is called when a filtered set is changed. */
                @SuppressWarnings({"unchecked"})
                public void update(Observable o, Object arg) {
                    assert arg instanceof Set;
                    refreshCells((Set<GraphJCell>) arg);
                }
            });
        }
        return this.labelTree;
    }

    /** Callback method to create the label tree. */
    protected LabelTree createLabelTree() {
        return new LabelTree(this, true, this.filtering);
    }

    /** 
     * Zooms and centres a given portion of the JGraph, as
     * defined by a certain rectangle. 
     */
    public void zoomTo(Rectangle2D bounds) {
        Rectangle2D viewBounds = getViewPortBounds();
        double widthScale = viewBounds.getWidth() / bounds.getWidth();
        double heightScale = viewBounds.getHeight() / bounds.getHeight();
        double scale = Math.min(widthScale, heightScale);
        double oldScale = getScale();
        setScale(oldScale * scale);
        int newX = (int) (bounds.getX() * scale);
        int newY = (int) (bounds.getY() * scale);
        int newWidth = (int) (scale * bounds.getWidth());
        int newHeight = (int) (scale * bounds.getHeight());
        Rectangle newBounds = new Rectangle(newX, newY, newWidth, newHeight);
        scrollRectToVisible(newBounds);
    }

    /** This implementation makes sure the rectangle gets centred on the viewport,
     * if it is not already contained in the viewport. */
    @Override
    public void scrollRectToVisible(Rectangle aRect) {
        Rectangle viewBounds = getViewPortBounds().getBounds();
        if (!viewBounds.contains(aRect)) {
            int newX = aRect.x - (viewBounds.width - aRect.width) / 2;
            int newY = aRect.y - (viewBounds.height - aRect.height) / 2;
            Rectangle newRect =
                new Rectangle(newX, newY, viewBounds.width, viewBounds.height);
            super.scrollRectToVisible(newRect);
        }
    }

    /** Returns the action to export this JGraph in various formats. */
    public ExportAction getExportAction() {
        if (this.exportAction == null) {
            this.exportAction = new ExportAction(this);
        }
        this.exportAction.refresh();
        return this.exportAction;
    }

    /** Returns the action to layout this JGraph. */
    public LayoutAction getLayoutAction() {
        if (this.layoutAction == null) {
            this.layoutAction = new LayoutAction(this);
            addAccelerator(this.layoutAction);
        }
        return this.layoutAction;
    }

    @Override
    public JGraphLayoutCache getGraphLayoutCache() {
        JGraphLayoutCache result;
        GraphLayoutCache superCache = super.getGraphLayoutCache();
        if (superCache instanceof JGraphLayoutCache) {
            result = (JGraphLayoutCache) superCache;
        } else {
            result = createGraphLayoutCache();
            if (getModel() != null) {
                result.setModel(getModel());
            }
            setGraphLayoutCache(result);
        }
        return result;
    }

    /**
     * Factory method for the graph layout cache. This implementation returns a
     * {@link groove.gui.jgraph.JGraphLayoutCache}.
     * @return the new graph layout cache
     */
    protected JGraphLayoutCache createGraphLayoutCache() {
        return new JGraphLayoutCache(createViewFactory());
    }

    /**
     * Factory method for the marquee handler. This marquee handler ensures that
     * mouse right-clicks don't deselect.
     */
    protected BasicMarqueeHandler createMarqueeHandler() {
        return new BasicMarqueeHandler() {
            @Override
            public void mousePressed(MouseEvent evt) {
                if (evt.getButton() != MouseEvent.BUTTON3) {
                    super.mousePressed(evt);
                }
            }
        };
    }

    /** Changes the scale of the {@link JGraph} by a given
     * increment or decrement.
     */
    public void changeScale(int change) {
        double scale = getScale();
        scale *= Math.pow(ZOOM_FACTOR, change);
        setScale(scale);
    }

    /** Shows a popup menu if the event is a popup trigger. */
    protected void maybeShowPopup(MouseEvent evt) {
        if (isPopupMenuEvent(evt) && getActions() != null) {
            getUI().cancelEdgeAdding();
            Point atPoint = evt.getPoint();
            createPopupMenu(atPoint).getPopupMenu().show(this, atPoint.x,
                atPoint.y);
        }
    }

    /**
     * Callback method to determine whether a given event is a menu popup event.
     * This implementation checks for the right hand mouse button. To be
     * overridden by subclasses.
     * @param evt the event that could be a popup menu event
     * @return <tt>true</tt> if <tt>e</tt> is a popup menu event
     */
    protected boolean isPopupMenuEvent(MouseEvent evt) {
        return evt.isPopupTrigger() && !evt.isControlDown();
    }

    /**
     * Lazily creates and returns the popup menu for this j-graph, activated for
     * a given point of the j-graph.
     * @param atPoint the point at which the menu is to be activated
     */
    public JMenu createPopupMenu(Point atPoint) {
        JMenu result = new JMenu("Popup");
        addSubmenu(result, createExportMenu());
        addSubmenu(result, createDisplayMenu());
        addSubmenu(result, getLayoutMenu());
        return result;
    }

    /** 
     * Adds to a given menu all the items of another menu.
     * Precedes the new menu with a separator if it is nonempty.
     * The submenu may be {@code null}, in which case nothing is added 
     * @param menu the menu to be extended
     * @param submenu the menu to be added to the popup menu
     */
    final public void addSubmenu(JMenu menu, JMenu submenu) {
        if (submenu != null && submenu.getItemCount() > 0) {
            // add a separator if this is not the first submenu
            if (menu.getItemCount() > 0) {
                menu.addSeparator();
            }
            addMenuItems(menu, submenu);
        }
    }

    /** 
     * Adds to a given menu all the items of another menu.
     * The submenu may be {@code null}, in which case nothing is added 
     * @param menu the menu to be extended
     * @param submenu the menu to be added to the popup menu
     */
    final public void addMenuItems(JMenu menu, JMenu submenu) {
        if (submenu != null && submenu.getItemCount() > 0) {
            // as we move items from the submenu to the main menu
            // the submenu gets modified
            while (submenu.getItemCount() > 0) {
                JMenuItem item = submenu.getItem(0);
                if (item == null) {
                    submenu.remove(0);
                    menu.addSeparator();
                } else {
                    menu.add(item);
                }
            }
        }
    }

    /** Returns a menu consisting of the export action of this JGraph. */
    public JMenu createExportMenu() {
        JMenu result = new JMenu("Export");
        result.add(getExportAction());
        return result;
    }

    /**
     * Returns a menu consisting of all the display menu items of this jgraph.
     */
    public JMenu createDisplayMenu() {
        JMenu result = new JMenu("Display");
        Object[] cells = getSelectionCells();
        boolean itemAdded = false;
        if (cells != null && cells.length > 0 && getActions() != null) {
            result.add(getActions().getFindReplaceAction());
            if (getActions().getSelectColorAction().isEnabled()) {
                result.add(getActions().getSelectColorAction());
            }
            itemAdded = true;
        }
        if (isFiltering() && cells != null && cells.length > 0) {
            result.add(getLabelTree().createFilterAction(cells));
            itemAdded = true;
        }
        if (itemAdded) {
            result.addSeparator();
        }
        result.add(getModeAction(SELECT_MODE));
        result.add(getModeAction(PAN_MODE));
        result.add(createShowHideMenu());
        result.add(createZoomMenu());
        return result;
    }

    /**
     * Returns a menu consisting of the menu items from the layouter 
     * setting menu of this JGraph.
     */
    public SetLayoutMenu getSetLayoutMenu() {
        if (this.setLayoutMenu == null) {
            this.setLayoutMenu = createSetLayoutMenu();
        }
        return this.setLayoutMenu;
    }

    /** Creates and returns a fresh layout setting menu upon this JGraph. */
    public SetLayoutMenu createSetLayoutMenu() {
        return new SetLayoutMenu(this);
    }

    /**
     * A standard layouter setting menu over this JGraph.
     */
    private SetLayoutMenu setLayoutMenu;

    /**
     * Returns a layout menu for this jgraph.
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
     * Creates and returns a fresh zoom menu upon this jgraph.
     */
    public ZoomMenu createZoomMenu() {
        return new ZoomMenu(this);
    }

    /**
     * Creates and returns a fresh show/hide menu upon this jgraph.
     */
    public ShowHideMenu createShowHideMenu() {
        return new ShowHideMenu(this);
    }

    private Action getShowLayoutDialogAction() {
        return this.getActions().getLayoutDialogAction();
    }

    /**
     * Adds the accelerator key for a given action to the action and input maps
     * of this JGraph.
     * @param action the action to be added
     */
    public void addAccelerator(Action action) {
        Object actionName = action.getValue(Action.NAME);
        KeyStroke actionKey =
            (KeyStroke) action.getValue(Action.ACCELERATOR_KEY);
        if (actionName != null && actionKey != null) {
            ActionMap am = getActionMap();
            am.put(actionName, action);
            InputMap im = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
            im.put(actionKey, actionName);
            im = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
            im.put(actionKey, actionName);
        }
    }

    /** 
     * Lazily creates and returns an action setting the mode of this 
     * JGraph. The actual setting is done by a call to {@link #setMode(JGraphMode)}.
     */
    public Action getModeAction(JGraphMode mode) {
        if (this.modeActionMap == null) {
            this.modeActionMap =
                new EnumMap<JGraphMode,Action>(JGraphMode.class);
            for (final JGraphMode any : JGraphMode.values()) {
                Action action =
                    new AbstractAction(any.getName(), any.getIcon()) {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            setMode(any);
                        }
                    };

                if (any.getAcceleratorKey() != null) {
                    action.putValue(Action.ACCELERATOR_KEY,
                        any.getAcceleratorKey());
                    addAccelerator(action);
                }
                this.modeActionMap.put(any, action);
            }
        }
        return this.modeActionMap.get(mode);
    }

    private Map<JGraphMode,Action> modeActionMap;

    /** 
     * Lazily creates and returns a button wrapping
     * {@link #getModeAction(JGraphMode)}.
     */
    public JToggleButton getModeButton(JGraphMode mode) {
        return getModeButtonMap().get(mode);
    }

    private Map<JGraphMode,JToggleButton> getModeButtonMap() {
        if (this.modeButtonMap == null) {
            this.modeButtonMap =
                new EnumMap<JGraphMode,JToggleButton>(JGraphMode.class);
            ButtonGroup modeButtonGroup = new ButtonGroup();
            for (JGraphMode any : JGraphMode.values()) {
                JToggleButton button = new JToggleButton(getModeAction(any));
                Options.setLAF(button);
                button.setToolTipText(any.getName());
                button.setEnabled(isEnabled());
                this.modeButtonMap.put(any, button);
                modeButtonGroup.add(button);
            }
            this.modeButtonMap.get(EDIT_MODE).setSelected(true);
        }
        return this.modeButtonMap;
    }

    private Map<JGraphMode,JToggleButton> modeButtonMap;

    @Override
    public void startEditingAtCell(Object cell) {
        firePropertyChange(CELL_EDIT_PROPERTY, null, cell);
        getUI().cancelEdgeAdding();
        super.startEditingAtCell(cell);
    }

    private CancelEditListener getCancelEditListener() {
        if (this.cancelListener == null) {
            this.cancelListener = new CancelEditListener();
        }
        return this.cancelListener;
    }

    /** Clear all intermediate points from all edges. */
    public void clearAllEdgePoints() {
        Map<GraphJCell,AttributeMap> change =
            new HashMap<GraphJCell,AttributeMap>();
        for (GraphJCell jCell : getModel().getRoots()) {
            if (jCell instanceof GraphJEdge) {
                VisualMap visuals = jCell.getVisuals();
                List<Point2D> points = visuals.getPoints();
                visuals.setPoints(Arrays.asList(points.get(0),
                    points.get(points.size() - 1)));
                change.put(jCell, visuals.getAttributes());
            }
        }
        getModel().edit(change, null, null, null);
    }

    /** Sets the layouting flag to the given value. */
    public void setLayouting(boolean layouting) {
        this.layouting = layouting;
    }

    /** Returns the layouting status of this jGraph. */
    public boolean isLayouting() {
        return this.layouting;
    }

    /** Flag indicating if the JGraph is being layouted. */
    private boolean layouting;

    /** Sets the visual refreshed to be used for a given visual key. */
    final protected void setVisualValue(VisualKey key, VisualValue<?> value) {
        this.visualValueMap.put(key, value);
    }

    /** Returns the visual refresher used for a given visual key. */
    final protected VisualValue<?> getVisualValue(VisualKey key) {
        VisualValue<?> result = this.visualValueMap.get(key);
        if (result == null) {
            this.visualValueMap.put(key,
                result = getFactory().newVisualValue(key));
        }
        return result;
    }

    private final Map<VisualKey,VisualValue<?>> visualValueMap =
        new EnumMap<VisualKey,VisualValue<?>>(VisualKey.class);

    /** Simulator tool to which this JGraph belongs. */
    private final Simulator simulator;
    /** The options object with which this {@link GraphJGraph} was constructed. */
    private final Options options;
    /** Flag indicating if the JGraph is filtering labels. */
    private final boolean filtering;

    /** The manipulation mode of the JGraph. */
    private JGraphMode mode;
    private CancelEditListener cancelListener;
    /** Flag indicating that a model refresh is being executed. */
    private boolean modelRefreshing;

    /**
     * The label list associated with this jgraph.
     */
    private LabelTree labelTree;

    /**
     * The currently selected prototype layouter.
     */
    private Layouter layouter;

    /**
     * The permanent ExportAction associated with this j-graph.
     */
    private ExportAction exportAction;
    /**
     * The permanent layout action associated with this jGraph.
     */
    private LayoutAction layoutAction;
    /**
     * The background color of this component when it is enabled.
     */
    private Color enabledBackground;

    /**
     * Flag to indicate whether this jgraph is currently registered with the
     * {@link ToolTipManager}.
     */
    private boolean toolTipEnabled;

    /** Layouter used if only part of the model should be layed out. */
    private final Layouter incrementalLayouter =
        new SpringLayouter().newInstance(this);

    /**
     * Constructs a JGraph for a given graph,
     * using a given {@link AttributeFactory}
     * for displaying the nodes and edges.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    static public GraphJGraph createJGraph(Graph<?,?> graph,
            AttributeFactory factory) {
        GraphJGraph result = new GraphJGraph(null, false);
        GraphJModel<?,?> jModel = result.newModel();
        jModel.loadGraph((Graph) graph);
        result.setModel(jModel);
        return result;
    }

    /** Maximum duration for layouting a new model. */
    static private final long MAX_LAYOUT_DURATION = 1000;

    /** The factor by which the zoom is adapted. */
    public static final float ZOOM_FACTOR = 1.4f;

    /**
     * Property name of the JGraph mode. 
     * Values are of type {@link GraphRole}.
     */
    static public final String JGRAPH_MODE_PROPERTY = "JGraphMode";
    /** Property name for the pseudo-property that signals a cell edit has started. */
    static public final String CELL_EDIT_PROPERTY = "editedCell";

    /** Listener class that cancels the edge adding mode on various occasions. */
    private final class CancelEditListener extends KeyAdapter implements
            GraphModelListener {
        @Override
        public void graphChanged(GraphModelEvent e) {
            getUI().cancelEdgeAdding();
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == Options.CANCEL_KEY.getKeyCode()) {
                getUI().cancelEdgeAdding();
            }
        }
    }

    /** Creates the view factory for this jGraph. */
    protected JCellViewFactory createViewFactory() {
        return new JCellViewFactory(this);
    }

    /**
     * Mouse listener that creates the popup menu and adds and deletes points on
     * appropriate events.
     */
    private class MyMouseListener extends MouseAdapter {
        /** Empty constructor wit the correct visibility. */
        MyMouseListener() {
            // empty
        }

        @Override
        public void mousePressed(MouseEvent evt) {
            maybeShowPopup(evt);
        }

        @Override
        public void mouseReleased(MouseEvent evt) {
            maybeShowPopup(evt);
        }
    }

    /** Listener that pushes selection values into the visual map. */
    private class MyGraphSelectionListener implements GraphSelectionListener {
        @Override
        public void valueChanged(GraphSelectionEvent e) {
            Object[] cells = e.getCells();
            for (int i = 0; i < cells.length; i++) {
                Object c = cells[i];
                if (c instanceof GraphJCell) {
                    GraphJCell jCell = (GraphJCell) c;
                    jCell.putVisual(VisualKey.EMPHASIS, e.isAddedCell(i));
                }
            }
        }
    }

    /** Interface for obtaining display attributes for graph elements. */
    static public interface AttributeFactory {
        /** 
         * Returns display attributes for a given graph node.
         * If {@code null}, the default attributes will be used. 
         */
        AttributeMap getAttributes(Node node);

        /** 
         * Returns display attributes for a given graph edge.
         * If {@code null}, the default attributes will be used. 
         */
        AttributeMap getAttributes(Edge edge);
    }
}
