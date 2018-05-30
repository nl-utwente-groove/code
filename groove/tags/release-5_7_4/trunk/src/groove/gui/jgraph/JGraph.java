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
 * $Id$
 */
package groove.gui.jgraph;

import static groove.gui.Options.SHOW_ANCHORS_OPTION;
import static groove.gui.Options.SHOW_ARROWS_ON_LABELS_OPTION;
import static groove.gui.Options.SHOW_BIDIRECTIONAL_EDGES_OPTION;
import static groove.gui.Options.SHOW_NODE_IDS_OPTION;
import static groove.gui.Options.SHOW_UNFILTERED_EDGES_OPTION;
import static groove.gui.jgraph.JGraphMode.EDIT_MODE;
import static groove.gui.jgraph.JGraphMode.PAN_MODE;
import static groove.gui.jgraph.JGraphMode.SELECT_MODE;
import groove.grammar.GrammarProperties;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.GraphRole;
import groove.graph.Node;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.gui.SimulatorModel;
import groove.gui.action.ActionStore;
import groove.gui.action.ExportAction;
import groove.gui.action.LayoutAction;
import groove.gui.layout.Layouter;
import groove.gui.layout.SpringLayouter;
import groove.gui.look.MultiLabel;
import groove.gui.look.VisualKey;
import groove.gui.look.VisualMap;
import groove.gui.look.VisualValue;
import groove.gui.menu.MyJMenu;
import groove.gui.menu.SetLayoutMenu;
import groove.gui.menu.ShowHideMenu;
import groove.gui.menu.ZoomMenu;
import groove.gui.tree.LabelTree;
import groove.lts.GTS;
import groove.util.Pair;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.accessibility.AccessibleState;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ButtonGroup;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JToggleButton;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;

import org.jgraph.event.GraphModelEvent;
import org.jgraph.event.GraphModelListener;
import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.BasicMarqueeHandler;
import org.jgraph.graph.CellView;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.EdgeView;
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
abstract public class JGraph<G extends Graph> extends org.jgraph.JGraph {
    /**
     * Constructs a JGraph for a given simulator.
     * @param simulator simulator to which the JGraph belongs; may be {@code null}
     */
    protected JGraph(Simulator simulator) {
        super((JModel<G>) null);
        this.simulator = simulator;
        this.options = simulator == null ? new Options() : simulator.getOptions();
        // make sure the layout cache has been created
        getGraphLayoutCache().setSelectsAllInsertedCells(false);
        setMarqueeHandler(createMarqueeHandler());
        // Make Ports invisible by Default
        setPortsVisible(false);
        // Save edits to a cell whenever something else happens
        setInvokesStopCellEditing(true);
        setEditable(false);
        setConnectable(false);
        setDisconnectable(false);
        installListeners();
    }

    /**
     * Installs listeners to this JGraph,
     * and installs this JGraph as listener.
     */
    protected void installListeners() {
        addMouseListener(new MyMouseListener());
        addKeyListener(getCancelEditListener());
        getSelectionModel().addGraphSelectionListener(new MyGraphSelectionListener());
        addOptionListener(SHOW_NODE_IDS_OPTION);
        addOptionListener(SHOW_UNFILTERED_EDGES_OPTION);
        addOptionListener(SHOW_ANCHORS_OPTION);
        addOptionListener(SHOW_ARROWS_ON_LABELS_OPTION);
        addOptionListener(SHOW_BIDIRECTIONAL_EDGES_OPTION);
    }

    /**
     * Removes this {@link JGraph} as listener,
     * so as to avoid memory leaks.
     */
    public void removeListeners() {
        getActions().removeRefreshable(getExportAction());
        for (Pair<JMenuItem,RefreshListener> record : this.optionListeners) {
            record.one().removeItemListener(record.two());
            record.one().removePropertyChangeListener(record.two());
        }
        this.optionListeners.clear();
        this.exportAction = null;
    }

    /** Returns the graph role of the graphs expected for this JGraph. */
    public GraphRole getGraphRole() {
        return GraphRole.NONE;
    }

    /** Returns the object holding the display options for this {@link JGraph}. */
    public final Options getOptions() {
        return this.options;
    }

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
     * @see #getRefreshListener
     */
    public void addOptionListener(String option) {
        JMenuItem optionItem = getOptions().getItem(option);
        if (optionItem == null) {
            throw new IllegalArgumentException(String.format("Unknown option: %s", option));
        }
        RefreshListener listener = getRefreshListener(option);
        if (listener != null) {
            optionItem.addItemListener(listener);
            optionItem.addPropertyChangeListener(listener);
            this.optionListeners.add(Pair.newPair(optionItem, listener));
        }
    }

    private final List<Pair<JMenuItem,RefreshListener>> optionListeners =
        new LinkedList<>();

    /**
     * Returns the refresh listener for a given option.
     * @return the refresh listener, or {@code null} if this JGraph doesn't
     * not need refreshing for a given option.
     */
    protected RefreshListener getRefreshListener(String option) {
        if (this.refreshListener == null) {
            this.refreshListener = new RefreshListener();
        }
        return this.refreshListener;
    }

    /** Change listener that refreshes the JGraph cells when activated. */
    private RefreshListener refreshListener;

    /**
     * Indicates whether node identities should be shown on node labels.
     */
    public boolean isShowNodeIdentities() {
        return getOptionValue(SHOW_NODE_IDS_OPTION);
    }

    /**
     * Indicates whether unfiltered edges to filtered nodes should remain
     * visible.
     */
    public boolean isShowUnfilteredEdges() {
        return getOptionValue(SHOW_UNFILTERED_EDGES_OPTION);
    }

    /**
     * Indicates whether anchors should be shown in the rule and lts views.
     */
    public boolean isShowAnchors() {
        return getOptionValue(SHOW_ANCHORS_OPTION);
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
        return getOptionValue(SHOW_ARROWS_ON_LABELS_OPTION);
    }

    /**
     * Indicates whether a single JEdge may stand for edges in two directions.
     */
    public boolean isShowBidirectionalEdges() {
        return getOptionValue(Options.SHOW_BIDIRECTIONAL_EDGES_OPTION);
    }

    /** Returns the (possibly {@code null}) simulator associated with this JGraph. */
    private Simulator getSimulator() {
        return this.simulator;
    }

    /** Convenience method to retrieve the state of the simulator, if any. */
    final public SimulatorModel getSimulatorModel() {
        return getSimulator() == null ? null : getSimulator().getModel();
    }

    /** Convenience method to retrieve the state of the simulator, if any. */
    final public ActionStore getActions() {
        return getSimulator() == null ? null : getSimulator().getActions();
    }

    /**
     * The properties of the grammar to which the displayed graph belongs.
     * May return {@code null} if the simulator is not set.
     */
    private GrammarProperties getProperties() {
        return getSimulatorModel() == null ? null
            : getSimulatorModel().getGrammar().getProperties();
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
        } else if (value instanceof JVertex) {
            MultiLabel label = ((JVertex<?>) value).getVisuals().getLabel();
            result = label.toString();
        } else if (value instanceof JEdgeView) {
            MultiLabel label = ((JEdgeView) value).getCell().getVisuals().getLabel();
            result = label.toString();
        }
        if (result == null || result.length() == 0) {
            result = " ";
        }
        return result;
    }

    /**
     * Returns a tool tip text for the front graph cell under the mouse.
     */
    @Override
    public String getToolTipText(MouseEvent evt) {
        JCell<?> jCell = getFirstCellForLocation(evt.getX(), evt.getY());
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
     * @see JCell#isGrayedOut()
     */
    @Override
    public boolean isCellEditable(Object cell) {
        return !(cell instanceof JCell && ((JCell<?>) cell).isGrayedOut())
            && super.isCellEditable(cell);
    }

    /**
     * Overwrites the method from JGraph for efficiency.
     */
    @Override
    public Object[] getDescendants(Object[] cells) {
        List<Object> res = new LinkedList<>();
        for (Object element : cells) {
            res.add(element);
            if (element instanceof DefaultGraphCell
                && ((DefaultGraphCell) element).getChildCount() > 0) {
                res.add(((DefaultGraphCell) element).getChildAt(0));
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
                (view.getAttributes() != null) ? GraphConstants.getBounds(view.getAttributes())
                    : null;
            AttributeMap attrs = getModel().getAttributes(view.getCell());
            if (bounds == null) {
                bounds = GraphConstants.getBounds(attrs);
            }
            if (bounds != null) {
                boolean autosize = GraphConstants.isAutoSize(view.getAllAttributes());
                boolean resize = GraphConstants.isResize(view.getAllAttributes());
                if (autosize || resize) {
                    Dimension2D d = getPreferredSize(view);
                    int inset = 2 * GraphConstants.getInset(view.getAllAttributes());
                    // adjust the x,y corner so that the center stays in place
                    double shiftX = (bounds.getWidth() - d.getWidth() - inset) / 2;
                    double shiftY = (bounds.getHeight() - d.getHeight() - inset) / 2;
                    bounds.setFrame(bounds.getX() + shiftX, bounds.getY() + shiftY, d.getWidth(),
                        d.getHeight());
                    // Remove resize attribute
                    snap(bounds);
                    if (resize) {
                        if (view.getAttributes() != null) {
                            view.getAttributes().remove(GraphConstants.RESIZE);
                        }
                        attrs.remove(GraphConstants.RESIZE);
                    }
                    view.refresh(getGraphLayoutCache(), getGraphLayoutCache(), false);
                }
            }
        }
    }

    private Dimension2D getPreferredSize(CellView view) {
        Dimension2D result;
        JVertex<?> vertex = view instanceof JVertexView ? ((JVertexView) view).getCell() : null;
        if (vertex == null) {
            result = getUI().getPreferredSize(this, view);
        } else {
            if (vertex.isStale(VisualKey.TEXT_SIZE)) {
                result = getUI().getPreferredSize(this, view);
                vertex.putVisual(VisualKey.TEXT_SIZE, result);
            } else {
                result = vertex.getVisuals().getTextSize();
            }
        }
        return result;
    }

    /**
     * @return the bounds of the entire display.
     */
    public Rectangle2D getGraphBounds() {
        return getCellBounds(getRoots());
    }

    /** Refreshes the visibility and view of a given set of JCells. */
    public void refreshCells(Collection<? extends JCell<G>> jCellSet) {
        if (!jCellSet.isEmpty()) {
            JGraphLayoutCache cache = getGraphLayoutCache();
            Collection<JCell<G>> visibleCells = new HashSet<>(jCellSet.size());
            Collection<JCell<G>> hiddenCells = new HashSet<>(jCellSet.size());
            for (JCell<G> jCell : jCellSet) {
                CellView jView = cache.getMapping(jCell, false);
                boolean wasVisible = jView != null;
                boolean isVisible = jCell.getVisuals().isVisible();
                Collection<JCell<G>> changeCells = wasVisible ? hiddenCells : visibleCells;
                if (isVisible != wasVisible) {
                    changeCells.add(jCell);
                    // test context for visibility
                    Iterator<? extends JCell<G>> iter = jCell.getContext();
                    while (iter.hasNext()) {
                        JCell<G> c = iter.next();
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
            JGraph.this.modelRefreshing = true;
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
                Rectangle2D scope = (Rectangle2D) getCellBounds(getSelectionCells()).clone();
                if (scope != null) {
                    scrollRectToVisible(toScreen(scope).getBounds());
                }
            }
            JGraph.this.modelRefreshing = false;
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
     * @see JCell#isGrayedOut()
     */
    public void changeGrayedOut(Set<JCell<G>> jCells, boolean grayedOut) {
        Set<JCell<G>> changedJCells = new HashSet<>();
        for (JCell<G> jCell : jCells) {
            if (jCell.setGrayedOut(grayedOut)) {
                changedJCells.add(jCell);
                if (grayedOut && jCell instanceof JVertex) {
                    // also gray out incident edges
                    Iterator<? extends JCell<G>> iter = jCell.getContext();
                    while (iter.hasNext()) {
                        JCell<G> c = iter.next();
                        if (c.setGrayedOut(true)) {
                            changedJCells.add(c);
                        }
                    }
                } else if (!grayedOut && jCell instanceof JEdge) {
                    // also revive end nodes
                    Iterator<? extends JCell<G>> iter = jCell.getContext();
                    while (iter.hasNext()) {
                        JCell<G> c = iter.next();
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
     * Indicates if this {@link JGraph} is in the course of processing
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
    protected JCell<G> getFirstCellForLocation(double x, double y, boolean vertex, boolean edge) {
        x /= this.scale;
        y /= this.scale;
        JCell<G> result = null;
        Rectangle xyArea = new Rectangle((int) (x - 2), (int) (y - 2), 4, 4);
        // iterate over the roots and query the visible ones
        CellView[] viewRoots = this.graphLayoutCache.getRoots();
        for (int i = viewRoots.length - 1; result == null && i >= 0; i--) {
            CellView jCellView = viewRoots[i];
            if (!(jCellView.getCell() instanceof JCell)) {
                continue;
            }
            @SuppressWarnings("unchecked")
            JCell<G> jCell = (JCell<G>) jCellView.getCell();
            boolean typeCorrect =
                vertex ? jCell instanceof JVertex : edge ? jCell instanceof JEdge : true;
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
    public JCell<G> getFirstCellForLocation(double x, double y) {
        return getFirstCellForLocation(x, y, false, false);
    }

    /**
     * This method returns the port of the topmost vertex.
     */
    @Override
    public PortView getPortViewAt(double x, double y) {
        JVertex<?> vertex = (JVertex<?>) getFirstCellForLocation(x, y, true, false);
        if (vertex != null) {
            return (PortView) getGraphLayoutCache().getMapping(vertex.getPort(), false);
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
        // Added a check that the new model differs from the current one
        // This should be OK, but if not, please comment here!
        if ((model == null || model instanceof JModel) && model != getModel()) {
            JModel<G> oldJModel = getModel();
            @SuppressWarnings("unchecked")
            JModel<G> newJModel = (JModel<G>) model;
            if (oldJModel != null) {
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
            //            if (newJModel != null) {
            //                doLayout(false);
            //            }
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

    /** Specialises the return type to a {@link JModel}. */
    @SuppressWarnings("unchecked")
    @Override
    public JModel<G> getModel() {
        return (JModel<G>) this.graphModel;
    }

    /** Callback factory method to create an appropriate JModel
     * instance for this JGraph.
     */
    public JModel<G> newModel() {
        return getFactory().newModel();
    }

    /**
     * Returns the factory for JGraph-related objects.
     * The factory is initialised either through {@link #createFactory()}
     * or through {@link #setFactory}
     * @return the factory to be used for this JGraph
     */
    final public JGraphFactory<G> getFactory() {
        if (this.factory == null) {
            this.factory = createFactory();
        }
        return this.factory;
    }

    /**
     * Sets the factory to be used.
     * Must be called before the first invocation of {@link #getFactory()}.
     */
    final public void setFactory(JGraphFactory<G> factory) {
        assert this.factory == null;
        this.factory = factory;
    }

    /** Callback factory method for the JGraphFactory to be used. */
    abstract protected JGraphFactory<G> createFactory();

    private JGraphFactory<G> factory;

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
            if (getLabelTree() != null) {
                getLabelTree().setEnabled(enabled);
            }
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
        return new JGraphUI<GTS>();
    }

    @SuppressWarnings("unchecked")
    @Override
    public JGraphUI<GTS> getUI() {
        return (JGraphUI<GTS>) super.getUI();
    }

    /**
     * Returns the nearest ancestor that is a {@link JViewport},
     * if there is any.
     */
    protected JViewport getViewPort() {
        JViewport result = null;
        for (Component parent = this; parent != null; parent = parent.getParent()) {
            if (parent instanceof JViewport) {
                result = (JViewport) parent;
                break;
            }
        }
        return result;
    }

    /**
     * Returns the nearest ancestor that is a {@link JFrame},
     * if there is any.
     */
    protected JFrame getFrame() {
        JFrame result = null;
        for (Component parent = this; parent != null; parent = parent.getParent()) {
            if (parent instanceof JFrame) {
                result = (JFrame) parent;
                break;
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
                    (int) bounds.getHeight() + 2 * extraSpace, BufferedImage.TYPE_INT_RGB);
            final Graphics2D graphics = img.createGraphics();
            graphics.setColor(getBackground());
            graphics.fillRect(0, 0, img.getWidth(), img.getHeight());
            graphics.translate(-bounds.getX() + extraSpace, -bounds.getY() + extraSpace);

            Object[] selection = getSelectionCells();
            boolean gridVisible = isGridVisible();
            setGridVisible(false);
            clearSelection();

            // if we don't do the painting the event thread,
            // strange and nondeterministic layout errors occur
            Runnable paint = new Runnable() {
                @Override
                public void run() {
                    paint(graphics);
                }
            };
            if (SwingUtilities.isEventDispatchThread()) {
                paint.run();
            } else {
                try {
                    SwingUtilities.invokeAndWait(paint);
                } catch (InterruptedException e) {
                    // do nothing
                } catch (InvocationTargetException e) {
                    // do nothing
                }
            }

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
            this.layouter = getDefaultLayouter().newInstance(this);
        }
        return this.layouter;
    }

    /** Prototype factory method to create a layouter for this JGraph. */
    public Layouter getDefaultLayouter() {
        return SpringLayouter.PROTOTYPE;
    }

    /**
     * Sets (but does not start) the layout action for this JGraph. First stops
     * the current layout action, if it is running.
     * @param prototypeLayouter prototype for the new layout action; the actual
     *        layout action is obtained by calling <tt>newInstance(this)</tt>
     * @see #getLayouter()
     */
    public void setLayouter(Layouter prototypeLayouter) {
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
        if (complete) {
            getModel().setLayoutable(true);
            result = getLayouter();
        } else {
            result = getLayouter().getIncremental();
        }
        result.start();
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
     * Registers or unregisters this jgraph with the tool tip manager. The
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
     * Associates a label tree with this JGraph.
     * Note: this method is called from the label tree constructor.
     */
    public void setLabelTree(LabelTree<G> labelTree) {
        this.labelTree = labelTree;
    }

    /**
     * Returns the label tree associated with this JGraph.
     * @return the associated label tree, or {@code null} if there is none
     */
    public LabelTree<G> getLabelTree() {
        return this.labelTree;
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
            Rectangle newRect = new Rectangle(newX, newY, viewBounds.width, viewBounds.height);
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

    /** Creates the view factory for this jGraph. */
    protected JCellViewFactory createViewFactory() {
        return new JCellViewFactory(this);
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
            createPopupMenu(atPoint).getPopupMenu().show(this, atPoint.x, atPoint.y);
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
        MyJMenu result = new MyJMenu("Popup");
        result.addSubmenu(createExportMenu());
        result.addSubmenu(createDisplayMenu());
        result.addSubmenu(getLayoutMenu());
        return result;
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
        LabelTree<G> labelTree = getLabelTree();
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
    public ShowHideMenu<G> createShowHideMenu() {
        return new ShowHideMenu<>(this);
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
        KeyStroke actionKey = (KeyStroke) action.getValue(Action.ACCELERATOR_KEY);
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
            this.modeActionMap = new EnumMap<>(JGraphMode.class);
            for (final JGraphMode any : JGraphMode.values()) {
                Action action = new AbstractAction(any.getName(), any.getIcon()) {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        setMode(any);
                    }
                };

                if (any.getAcceleratorKey() != null) {
                    action.putValue(Action.ACCELERATOR_KEY, any.getAcceleratorKey());
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
            this.modeButtonMap = new EnumMap<>(JGraphMode.class);
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
        Map<JCell<G>,AttributeMap> change = new HashMap<>();
        for (JCell<G> jCell : getModel().getRoots()) {
            if (jCell instanceof JEdge) {
                VisualMap visuals = jCell.getVisuals();
                List<Point2D> points = visuals.getPoints();
                // don't make the change directly in the cell,
                // as this messes up the undo history
                List<Point2D> newPoints =
                    Arrays.asList(points.get(0), points.get(points.size() - 1));
                AttributeMap newAttributes = new AttributeMap();
                GraphConstants.setPoints(newAttributes, newPoints);
                change.put(jCell, newAttributes);
            }
        }
        getModel().edit(change, null, null, null);
    }

    /** Sets the layouting flag to the given value. */
    public void setLayouting(boolean layouting) {
        if (layouting != this.layouting) {
            this.layouting = layouting;
            if (layouting) {
                // start the layouting
                getModel().beginUpdate();
            } else {
                // reroute the loops
                GraphLayoutCache cache = getGraphLayoutCache();
                for (CellView view : cache.getRoots()) {
                    if (view instanceof EdgeView && ((EdgeView) view).isLoop()) {
                        view.update(cache);
                    }
                }
                // end the layouting
                getModel().endUpdate();
            }
        }
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
    final public VisualValue<?> getVisualValue(VisualKey key) {
        VisualValue<?> result = this.visualValueMap.get(key);
        if (result == null) {
            this.visualValueMap.put(key, result = getFactory().newVisualValue(key));
        }
        return result;
    }

    private final Map<VisualKey,VisualValue<?>> visualValueMap =
        new EnumMap<>(VisualKey.class);

    /** Simulator tool to which this JGraph belongs. */
    private final Simulator simulator;
    /** The options object with which this {@link JGraph} was constructed. */
    private final Options options;
    /** The manipulation mode of the JGraph. */
    private JGraphMode mode;
    private CancelEditListener cancelListener;
    /** Flag indicating that a model refresh is being executed. */
    private boolean modelRefreshing;

    /**
     * The label list associated with this jgraph.
     */
    private LabelTree<G> labelTree;

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
    private final class CancelEditListener extends KeyAdapter implements GraphModelListener {
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
                if (c instanceof JCell) {
                    @SuppressWarnings("unchecked")
                    JCell<G> jCell = (JCell<G>) c;
                    jCell.putVisual(VisualKey.EMPHASIS, e.isAddedCell(i));
                }
            }
        }
    }

    /**
     * Listener that causes all cells of this JGraph to be refreshed
     * on activation.
     */
    protected class RefreshListener implements ItemListener, PropertyChangeListener {
        @Override
        public void itemStateChanged(ItemEvent e) {
            if (isEnabled()) {
                doRefresh();
            }
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals(AccessibleState.ENABLED.toDisplayString())
                && isEnabled()) {
                doRefresh();
            }
        }

        /** Callback option to refresh as this listener demands. */
        protected void doRefresh() {
            getModel().refreshVisuals();
            refreshAllCells();
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
