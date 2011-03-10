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

import static groove.gui.jgraph.JAttr.EXTRA_BORDER_SPACE;
import static groove.gui.jgraph.JGraphMode.EDIT_MODE;
import static groove.gui.jgraph.JGraphMode.PAN_MODE;
import static groove.gui.jgraph.JGraphMode.SELECT_MODE;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.GraphRole;
import groove.graph.Label;
import groove.graph.Node;
import groove.gui.Icons;
import groove.gui.LabelTree;
import groove.gui.Options;
import groove.gui.SetLayoutMenu;
import groove.gui.ShowHideMenu;
import groove.gui.Simulator;
import groove.gui.ZoomMenu;
import groove.gui.dialog.ErrorDialog;
import groove.gui.dialog.SaveDialog;
import groove.gui.layout.Layouter;
import groove.gui.layout.SpringLayouter;
import groove.io.external.Exporter;
import groove.trans.SystemProperties;
import groove.util.Colors;
import groove.util.ObservableSet;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
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
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;

import org.jgraph.JGraph;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.BasicMarqueeHandler;
import org.jgraph.graph.CellView;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.DefaultPort;
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
     * Constructs a JGraph.
     * @param options display options object to be used
     * @param hasFilters indicates if this JGraph is to use label filtering.
     */
    public GraphJGraph(Options options, boolean hasFilters) {
        super((GraphJModel<?,?>) null);
        this.options = options == null ? new Options() : options;
        if (hasFilters) {
            this.filteredLabels = new ObservableSet<Label>();
            this.filteredLabels.addObserver(this.refreshListener);
        } else {
            this.filteredLabels = null;
        }
        // make sure the layout cache has been created
        getGraphLayoutCache().setSelectsAllInsertedCells(false);
        setMarqueeHandler(createMarqueeHandler());
        // Make Ports invisible by Default
        setPortsVisible(false);
        // Save edits to a cell whenever something else happens
        setInvokesStopCellEditing(true);
        addMouseListener(new MyMouseListener());
        setConnectable(false);
        setDisconnectable(false);
    }

    /**
     * Returns the set of labels that is currently filtered from view. If
     * <code>null</code>, no filtering is going on.
     */
    public final ObservableSet<Label> getFilteredLabels() {
        return this.filteredLabels;
    }

    /**
     * Indicates if a given label is currently being filtered from view. This is
     * the case if it is in the set of filtered labels.
     */
    public boolean isFiltering(Label label) {
        return this.filteredLabels != null
            && this.filteredLabels.contains(label);
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

    /** Indicates if nodes should determine their own background colour. */
    public boolean isShowBackground() {
        return getOptionValue(Options.SHOW_BACKGROUND_OPTION);
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
     * The properties of the grammar to which the displayed graph belongs.
     * May return {@code null} if the simulator is not set.
     */
    SystemProperties getProperties() {
        return getSimulator() == null ? null
                : getSimulator().getGrammarView().getProperties();
    }

    /** Returns the simulator associated with this {@link GraphJGraph}, if any. */
    public Simulator getSimulator() {
        return null;
    }

    /**
     * Overrides the method to call {@link GraphJCell#getText()} whenever
     * <code>object</code> is recognised as a {@link JVertexView},
     * {@link JEdgeView} or {@link GraphJCell}.
     */
    @Override
    public String convertValueToString(Object value) {
        String result;
        if (value instanceof JVertexView) {
            result = ((JVertexView) value).getCell().getText();
        } else if (value instanceof JEdgeView) {
            result = ((JEdgeView) value).getCell().getText();
        } else if (value instanceof GraphJCell) {
            result = ((GraphJCell) value).getText();
        } else {
            result = value.toString();
        }
        // set text to nonempty in case we have a node,
        // so the size gets set properly
        if (result == null || result.length() == 0
            && !(value instanceof JEdgeView)) {
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
        if (jCell != null && jCell.isVisible()) {
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
     * @return the bounds of the entire display.
     */
    public Rectangle2D getGraphBounds() {
        return getCellBounds(getRoots());
    }

    /** Refreshes the visibility and view of a given set of JCells. */
    public void refreshCells(final Collection<? extends GraphJCell> jCellSet) {
        if (!jCellSet.isEmpty()) {
            final Collection<GraphJCell> visibleCells =
                new ArrayList<GraphJCell>(jCellSet.size());
            final Collection<GraphJCell> invisibleCells =
                new ArrayList<GraphJCell>(jCellSet.size());
            final Collection<GraphJCell> grayedOutCells =
                new ArrayList<GraphJCell>(jCellSet.size());
            for (GraphJCell jCell : jCellSet) {
                CellView jView = getGraphLayoutCache().getMapping(jCell, false);
                if (jView != null) {
                    if (!jCell.isVisible()) {
                        invisibleCells.add(jCell);
                        getSelectionModel().removeSelectionCell(jCell);
                        if (jCell instanceof GraphJVertex) {
                            for (Object edge : ((GraphJVertex) jCell).getPort().getEdges()) {
                                if (!((GraphJEdge) edge).isVisible()) {
                                    invisibleCells.add((GraphJEdge) edge);
                                }
                            }
                        }
                    } else {
                        // the display modus might have changed,
                        // like for data edges; hence reaffirm the visibility
                        visibleCells.add(jCell);
                    }
                } else {
                    if (jCell.isVisible()) {
                        visibleCells.add(jCell);
                        if (jCell instanceof GraphJVertex) {
                            for (Object edge : ((GraphJVertex) jCell).getPort().getEdges()) {
                                if (((GraphJEdge) edge).isVisible()) {
                                    visibleCells.add((GraphJEdge) edge);
                                }
                            }
                        }
                    }
                }
                if (jCell.isGrayedOut()) {
                    grayedOutCells.add(jCell);
                }
            }
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    GraphJGraph.this.modelRefreshing = true;
                    // make sure refreshed cells are not selected
                    boolean selectsInsertedCells =
                        getGraphLayoutCache().isSelectsLocalInsertedCells();
                    getGraphLayoutCache().setSelectsLocalInsertedCells(false);
                    getGraphLayoutCache().setVisible(visibleCells.toArray(),
                        invisibleCells.toArray());
                    getGraphLayoutCache().setSelectsLocalInsertedCells(
                        selectsInsertedCells);
                    if (getSelectionCount() > 0) {
                        Rectangle2D scope =
                            (Rectangle2D) getCellBounds(getSelectionCells()).clone();
                        if (scope != null) {
                            scrollRectToVisible(toScreen(scope).getBounds());
                        }
                    }
                    GraphJGraph.this.modelRefreshing = false;
                }
            });
        }
    }

    /** Refreshes the visibility and view of all JCells in the model. */
    public void refreshAllCells() {
        refreshCells(getModel().getRoots());
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
                if (grayedOut) {
                    // also gray out incident edges
                    if (jCell instanceof GraphJVertex) {
                        Iterator<?> jEdgeIter =
                            ((GraphJVertex) jCell).getPort().edges();
                        while (jEdgeIter.hasNext()) {
                            GraphJEdge jEdge = (GraphJEdge) jEdgeIter.next();
                            if (jEdge.setGrayedOut(true)) {
                                changedJCells.add(jEdge);
                            }
                        }
                    }
                } else {
                    // also revive end nodes
                    if (jCell instanceof GraphJEdge) {
                        GraphJCell sourceJVertex =
                            ((GraphJEdge) jCell).getSourceVertex();
                        if (sourceJVertex.setGrayedOut(false)) {
                            changedJCells.add(sourceJVertex);
                        }
                        GraphJCell targetJVertex =
                            ((GraphJEdge) jCell).getTargetVertex();
                        if (targetJVertex.setGrayedOut(false)) {
                            changedJCells.add(targetJVertex);
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
     * @param edge <tt>true</tt> if we are not interested in vertice
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
            GraphJModel<?,?> jModel = (GraphJModel<?,?>) model;
            if (getModel() != null) {
                if (this.layouter != null) {
                    this.layouter.stop();
                }
                // if we don't clear the selection, the old selection
                // gives trouble when setting the model
                clearSelection();
            }
            super.setModel(jModel);
            if (jModel != null) {
                setName(jModel.getName());
            }
            getLabelTree().updateModel();
            if (model != null && this.layouter != null) {
                int layoutCount = freeze();
                if (layoutCount > 0) {
                    Layouter layouter =
                        layoutCount == jModel.getRootCount() ? this.layouter
                                : this.incrementalLayouter;
                    layouter.start(false);
                    final Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            GraphJGraph.this.layouter.stop();
                            // cancel the timer, because it may otherwise
                            // keep the entire program from terminating
                            timer.cancel();
                        }
                    }, MAX_LAYOUT_DURATION);
                }
            }
            setEnabled(model != null);
        }
    }

    /**
     * Sets all jcells to unmoveable, except those that are marked
     * as layoutable. This is done in preparation for layouting.
     * @return the number of moveable cells
     */
    public int freeze() {
        int result = 0;
        for (GraphJCell jCell : getModel().getRoots()) {
            boolean layoutable = jCell.setLayoutable(false);
            GraphConstants.setMoveable(jCell.getAttributes(), layoutable);
            if (layoutable) {
                result++;
            }
        }
        return result;
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
        return new GraphJModel<Node,Edge<Node>>(
            GraphJVertex.getPrototype(this), GraphJEdge.getPrototype(this));
    }

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
            getExportAction().setEnabled(enabled);
            for (JToggleButton button : getModeButtonMap().values()) {
                button.setEnabled(enabled);
            }
            getModeButton(getDefaultMode()).setSelected(true);
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
     * @return the current layout action for this jgraph.
     * @see #setLayouter(Layouter)
     */
    public Layouter getLayouter() {
        return this.layouter;
    }

    /**
     * Sets (but doe snot start) the layout action for this jgraph. First stops
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
     * Lays out this graph according to the currently set layouter (if any).
     * @see Layouter#start(boolean)
     */
    public void doGraphLayout() {
        if (this.layouter != null) {
            this.layouter.start(true);
        }
    }

    /** Adds a listener to {@link #setMode(JGraphMode)} calls. */
    public void addJGraphModeListener(PropertyChangeListener listener) {
        getChangeSupport().addPropertyChangeListener(JGRAPH_MODE_PROPERTY,
            listener);
    }

    /** Removes a listener to {@link #setMode(JGraphMode)} calls. */
    public void removeJGraphModeListener(PropertyChangeListener listener) {
        getChangeSupport().removePropertyChangeListener(JGRAPH_MODE_PROPERTY,
            listener);
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
            getChangeSupport().firePropertyChange(JGRAPH_MODE_PROPERTY,
                oldMode, mode);
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
     * Creates, sets and returns a new label tree instance for this jgraph.
     */
    public LabelTree initLabelTree() {
        this.labelTree = new LabelTree(this);
        return this.labelTree;
    }

    /**
     * Lazily creates and returns the label list associated with this jgraph.
     */
    public LabelTree getLabelTree() {
        if (this.labelTree == null) {
            initLabelTree();
        }
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
            Rectangle newRect =
                new Rectangle(newX, newY, viewBounds.width, viewBounds.height);
            super.scrollRectToVisible(newRect);
        }
    }

    /** Returns the action to export this JGraph in various formats. */
    public ExportAction getExportAction() {
        if (this.exportAction == null) {
            this.exportAction = new ExportAction();
        }
        return this.exportAction;
    }

    @Override
    public GraphLayoutCache getGraphLayoutCache() {
        GraphLayoutCache result = super.getGraphLayoutCache();
        if (!(result instanceof MyGraphLayoutCache)) {
            result = createGraphLayoutCache();
            setGraphLayoutCache(result);
            result.setModel(getModel());
        }
        return result;
    }

    /**
     * Factory method for the graph layout cache. This implementation returns a
     * {@link groove.gui.jgraph.GraphJGraph.MyGraphLayoutCache}.
     * @return the new graph layout cache
     */
    protected GraphLayoutCache createGraphLayoutCache() {
        return new MyGraphLayoutCache();
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

    /** Sets the exporter used in the ExportAction. */
    public void setExporter(Exporter exporter) {
        getExportAction().setExporter(exporter);
    }

    /** Callback method to return the export action name. */
    protected String getExportActionName() {
        return Options.EXPORT_ACTION_NAME;
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
        if (isPopupMenuEvent(evt)) {
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
        if (cells != null && cells.length > 0 && getSimulator() != null) {
            result.add(getSimulator().getRelabelAction());
            if (getSimulator().getSelectColorAction().isEnabled()) {
                result.add(getSimulator().getSelectColorAction());
            }
            itemAdded = true;
        }
        if (this.filteredLabels != null && cells != null && cells.length > 0) {
            result.add(new FilterAction(cells));
            itemAdded = true;
        }
        if (itemAdded) {
            result.addSeparator();
        }
        result.add(createShowHideMenu());
        result.add(createZoomMenu());
        return result;
    }

    /**
     * Returns a menu consisting of the menu items from the layouter 
     * setting menu of this jgraph.
     */
    public SetLayoutMenu getSetLayoutMenu() {
        return this.setLayoutMenu;
    }

    /**
     * Returns a layout menu for this jgraph.
     * The items added are the current layout action and a layouter setting
     * sub-menu.
     */
    public JMenu getLayoutMenu() {
        JMenu result = new JMenu("Layout");
        result.add(getSetLayoutMenu().getCurrentLayoutItem());
        result.add(getSetLayoutMenu());
        return result;
    }

    /**
     * Creates and returns a fresh zoom menu upon this jgraph.
     */
    protected ZoomMenu createZoomMenu() {
        return new ZoomMenu(this);
    }

    /**
     * Creates and returns a fresh show/hide menu upon this jgraph.
     */
    protected ShowHideMenu createShowHideMenu() {
        return new ShowHideMenu(this);
    }

    /** Creates and returns a fresh layout setting menu upon this j-graph. */
    public SetLayoutMenu createSetLayoutMenu() {
        return new SetLayoutMenu(this);
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
            InputMap im = getInputMap(JComponent.WHEN_FOCUSED);
            im.put(actionKey, actionName);
            im = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
            im.put(actionKey, actionName);
            im = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
            im.put(actionKey, actionName);
        }
    }

    /**
     * Lazily creates and returns the property change support object for this
     * editor.
     */
    private PropertyChangeSupport getChangeSupport() {
        if (this.propertyChangeSupport == null) {
            this.propertyChangeSupport = new PropertyChangeSupport(this);
        }
        return this.propertyChangeSupport;
    }

    /** 
     * Lazily creates and returns an action setting the mode of this 
     * JGraph. The actual setting is done by a call to {@link #setMode(JGraphMode)}.
     */
    public Action getModeAction(JGraphMode mode) {
        if (this.modeActionMap == null) {
            this.modeActionMap =
                new EnumMap<JGraphMode,Action>(JGraphMode.class);
            for (final JGraphMode any : EnumSet.allOf(JGraphMode.class)) {
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
            for (JGraphMode any : EnumSet.allOf(JGraphMode.class)) {
                JToggleButton button = new JToggleButton(getModeAction(any));
                button.setFocusable(false);
                button.setText(null);
                button.setToolTipText(any.getName());
                button.setEnabled(isEnabled());
                this.modeButtonMap.put(any, button);
                modeButtonGroup.add(button);
            }
            this.modeButtonMap.get(EDIT_MODE).setSelected(true);
        }
        return this.modeButtonMap;
    }

    private Map<JGraphMode,Action> modeActionMap;

    private Map<JGraphMode,JToggleButton> modeButtonMap;

    /** The options object with which this {@link GraphJGraph} was constructed. */
    private final Options options;
    /** The set of labels currently filtered from view. */
    private final ObservableSet<Label> filteredLabels;
    /** Object providing the core functionality for property changes. */
    private PropertyChangeSupport propertyChangeSupport;
    /** The manipulation mode of the JGraph. */
    private JGraphMode mode;
    /** The fixed refresh listener of this {@link GraphJModel}. */
    private final RefreshListener refreshListener = new RefreshListener();
    /** Flag indicating that a model refresh is being executed. */
    private boolean modelRefreshing;
    /**
     * A standard layouter setting menu over this jgraph.
     */
    private final SetLayoutMenu setLayoutMenu = createSetLayoutMenu();

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

    /** Creates a plain JGraph for a given GROOVE graph. */
    @SuppressWarnings({"rawtypes", "unchecked"})
    static public GraphJGraph createJGraph(Graph<?,?> graph) {
        GraphJGraph result = new GraphJGraph(null, false);
        GraphJModel<?,?> jModel = result.newModel();
        jModel.loadGraph((Graph) graph);
        result.setModel(jModel);
        return result;
    }

    /**
     * Constructs a JGraph for a given graph,
     * using given GraphJVertex and GraphJEdge prototypes
     * for displaying the nodes and edges.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    static public GraphJGraph createJGraph(Graph<?,?> graph,
            AttributeFactory factory) {
        GraphJGraph result = new AttrJGraph(factory);
        GraphJModel<?,?> jModel = result.newModel();
        jModel.loadGraph((Graph) graph);
        result.setModel(jModel);
        return result;
    }

    /** Maximum duration for layouting a new model. */
    static private final long MAX_LAYOUT_DURATION = 1000;

    /**
     * The standard jgraph attributes used for graying out nodes and edges.
     */
    static public final JAttr.AttributeMap GRAYED_OUT_ATTR;
    /**
     * The standard jgraph attributes used for representing nodes.
     */
    public static final JAttr.AttributeMap DEFAULT_NODE_ATTR;
    /**
     * The standard jgraph attributes used for representing edges.
     */
    public static final JAttr.AttributeMap DEFAULT_EDGE_ATTR;

    /** The factor by which the zoom is adapted. */
    public static final float ZOOM_FACTOR = 1.4f;

    /**
     * Property name of the JGraph mode. 
     * Values are of type {@link GraphRole}.
     */
    static public final String JGRAPH_MODE_PROPERTY = "JGraphMode";

    static {
        // graying out
        GRAYED_OUT_ATTR = new JAttr() {
            {
                this.foreColour = Colors.findColor("200 200 200 100");
                this.opaque = false;
            }
        }.getEdgeAttrs();
        // set default node and edge attributes
        JAttr defaultValues = new JAttr();
        DEFAULT_EDGE_ATTR = defaultValues.getEdgeAttrs();
        DEFAULT_NODE_ATTR = defaultValues.getNodeAttrs();
    }

    /**
     * Action to save the state, as a graph or in some export format.
     * @see Exporter#export(GraphJGraph, File)
     */
    private class ExportAction extends AbstractAction {
        /** Constructs an instance of the action. */
        ExportAction() {
            super(getExportActionName());
            putValue(ACCELERATOR_KEY, Options.EXPORT_KEY);
        }

        public void actionPerformed(ActionEvent e) {
            String fileName = getModel().getName();
            if (fileName != null) {
                this.exporter.getFileChooser().setSelectedFile(
                    new File(fileName));
            }
            File selectedFile =
                SaveDialog.show(this.exporter.getFileChooser(),
                    GraphJGraph.this, null);
            // now save, if so required
            if (selectedFile != null) {
                try {
                    this.exporter.export(GraphJGraph.this, selectedFile);
                } catch (IOException exc) {
                    new ErrorDialog(GraphJGraph.this,
                        "Error while exporting to " + selectedFile, exc).setVisible(true);
                }

            }
        }

        /** Sets the exporter to be used. */
        void setExporter(Exporter exporter) {
            this.exporter = exporter;
        }

        private Exporter exporter;
    }

    /** Action to turn filtering on for a set of selected cells. */
    private class FilterAction extends AbstractAction {
        FilterAction(Object[] cells) {
            super(Options.FILTER_ACTION_NAME);
            this.cells = cells;
        }

        public void actionPerformed(ActionEvent e) {
            Set<Label> labels = new HashSet<Label>();
            for (Object cell : this.cells) {
                labels.addAll(((GraphJCell) cell).getListLabels());
            }
            getFilteredLabels().addAll(labels);
        }

        /** The array of cells upon which this action works. */
        private final Object[] cells;
    }

    /** Own implementation of UI for performance reasons. */
    static class MyGraphUI extends org.jgraph.plaf.basic.BasicGraphUI {
        MyGraphUI() {
            // empty
        }

        private GraphJGraph getJGraph() {
            return (GraphJGraph) this.graph;
        }

        /**
         * Taken from <code>com.jgraph.example.fastgraph.FastGraphUI</code>.
         * Updates the <code>preferredSize</code> instance variable, which is
         * returned from <code>getPreferredSize()</code>. Ignores edges for
         * performance
         */
        @Override
        protected void updateCachedPreferredSize() {
            CellView[] views = this.graphLayoutCache.getRoots();
            Rectangle2D size = null;
            if (views != null && views.length > 0) {
                for (int i = 0; i < views.length; i++) {
                    if (views[i] != null && !(views[i] instanceof JEdgeView)) {
                        Rectangle2D r = views[i].getBounds();
                        if (r != null) {
                            if (size == null) {
                                size =
                                    new Rectangle2D.Double(r.getX(), r.getY(),
                                        r.getWidth(), r.getHeight());
                            } else {
                                Rectangle2D.union(size, r, size);
                            }
                        }
                    }
                }
            }
            if (size == null) {
                size = new Rectangle2D.Double();
            }
            Point2D psize =
                new Point2D.Double(size.getX() + size.getWidth(), size.getY()
                    + size.getHeight());
            Dimension d = getJGraph().getMinimumSize();
            Point2D min =
                (d != null)
                        ? getJGraph().toScreen(new Point(d.width, d.height))
                        : new Point(0, 0);
            Point2D scaled = getJGraph().toScreen(psize);
            this.preferredSize =
                new Dimension((int) Math.max(min.getX(), scaled.getX()),
                    (int) Math.max(min.getY(), scaled.getY()));
            Insets in = this.graph.getInsets();
            if (in != null) {
                this.preferredSize.setSize(this.preferredSize.getWidth()
                    + in.left + in.right, this.preferredSize.getHeight()
                    + in.top + in.bottom);
            }
            this.validCachedPreferredSize = true;
        }

        @Override
        protected Point2D getEditorLocation(Object cell,
                Dimension2D editorSize, Point2D pt) {
            double scale = getJGraph().getScale();
            // shift the location by the extra border space
            return super.getEditorLocation(cell, editorSize,
                new Point2D.Double(pt.getX() + scale * (EXTRA_BORDER_SPACE + 4)
                    - 4, pt.getY() + scale * (EXTRA_BORDER_SPACE + 3) - 3));
        }

        /** 
         * Makes sure that cancelled edits are nevertheless passed on to 
         * the JGraph.
         */
        @Override
        protected void completeEditing(boolean messageStop,
                boolean messageCancel, boolean messageGraph) {
            super.completeEditing(messageStop, messageCancel, true);
        }

        @Override
        protected void installListeners() {
            super.installListeners();
            this.graph.addMouseWheelListener((MouseWheelListener) this.mouseListener);
        }

        @Override
        protected MouseListener createMouseListener() {
            return new MyMouseHandler();
        }

        private final class MyMouseHandler extends MouseHandler {
            /** The coordinates of a point where panning started. */
            private int origX = -1, origY = -1;

            @Override
            public void mousePressed(MouseEvent e) {
                if (getJGraph().getMode() != PAN_MODE) {
                    super.mousePressed(e);
                } else if (isPanEnabled()
                    && e.getButton() == MouseEvent.BUTTON1) {
                    startPan(e);
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (getJGraph().getMode() != PAN_MODE) {
                    super.mouseDragged(e);
                } else if (isPanEnabled()) {
                    doPan(e);
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                if (getJGraph().getMode() != PAN_MODE) {
                    super.mouseMoved(e);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (getJGraph().getMode() != PAN_MODE) {
                    super.mouseReleased(e);
                } else if (isPanEnabled()
                    && e.getButton() == MouseEvent.BUTTON1) {
                    endPan();
                }
            }

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (getJGraph().getMode() == PAN_MODE) {
                    int change = -e.getWheelRotation();
                    getJGraph().changeScale(change);
                }
            }

            /**
             * Start panning.
             */
            private void startPan(MouseEvent e) {
                this.origX = e.getX();
                this.origY = e.getY();
                getJGraph().setCursor(Icons.CLOSED_HAND_CURSOR);
            }

            /**
             * Finish panning.
             */
            private void endPan() {
                this.origX = -1;
                this.origY = -1;
                getJGraph().setCursor(Icons.OPEN_HAND_CURSOR);
            }

            /**
             * Shift the viewport according to the panned distance.
             */
            private void doPan(MouseEvent e) {
                if (this.origX == -1) {
                    return; // never happens ??
                }
                Point p = getViewPort().getViewPosition();
                p.x -= (e.getX() - this.origX);
                p.y -= (e.getY() - this.origY);

                Dimension size = getJGraph().getSize();
                Dimension vsize = getViewPort().getExtentSize();

                if (p.x + vsize.width > size.width) {
                    p.x = size.width - vsize.width;
                }
                if (p.y + vsize.height > size.height) {
                    p.y = size.height - vsize.height;
                }
                if (p.x < 0) {
                    p.x = 0;
                }
                if (p.y < 0) {
                    p.y = 0;
                }
                getViewPort().setViewPosition(p);
            }

            private boolean isPanEnabled() {
                return getJGraph().getMode() == PAN_MODE
                    && getViewPort() != null;
            }

            /** The JGraph's ancestor viewport, if any. */
            private JViewport getViewPort() {
                return getJGraph().getViewPort();
            }
        }
    }

    /**
     * A layout cache that, for efficiency, does not pass on all change events,
     * and sets a {@link JCellViewFactory}. It should be possible to use the
     * partiality of the cache to hide elements, but this seems unnecessarily
     * complicated.
     */
    private class MyGraphLayoutCache extends GraphLayoutCache {
        /** Constructs an instance of the cache. */
        MyGraphLayoutCache() {
            super(null, new JCellViewFactory(GraphJGraph.this), true);
            setSelectsLocalInsertedCells(false);
            setShowsExistingConnections(false);
            setShowsChangedConnections(false);
            setShowsInsertedConnections(false);
            setHidesExistingConnections(false);
            setHidesDanglingConnections(false);
        }

        /**
         * Make sure all views are correctly inserted
         */
        @Override
        public void setModel(GraphModel model) {
            this.partial = false;
            super.setModel(model);
            this.partial = true;
        }

        @Override
        public boolean isVisible(Object cell) {
            if (cell instanceof GraphJCell) {
                return ((GraphJCell) cell).isVisible();
            } else if (cell instanceof DefaultPort) {
                return isVisible(((DefaultPort) cell).getParent());
            } else {
                return super.isVisible(cell);
            }
        }

        /**
         * Completely reloads all roots from the model in the order returned by
         * DefaultGraphModel.getAll. This uses the current visibleSet and
         * mapping to fetch the cell views for the cells.
         */
        @Override
        protected void reloadRoots() {
            // Reorder roots
            Object[] orderedCells = DefaultGraphModel.getAll(this.graphModel);
            List<CellView> newRoots = new ArrayList<CellView>();
            for (Object element : orderedCells) {
                CellView view = getMapping(element, true);
                if (view != null) {
                    // view.refresh(this, this, true);
                    if (view.getParentView() == null) {
                        newRoots.add(view);
                    }
                }
            }
            this.roots = newRoots;
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

    /**
     * Observer that calls {@link GraphJGraph#refreshCells(Collection)} whenever it receives an
     * update event.
     */
    private class RefreshListener implements Observer {
        /** Empty constructor wit the correct visibility. */
        RefreshListener() {
            // empty
        }

        /** The method is called when a filtered set is changed. */
        @SuppressWarnings({"unchecked"})
        public void update(Observable o, Object arg) {
            Set<Label> changedLabelSet = null;
            if (arg instanceof ObservableSet.AddUpdate) {
                changedLabelSet =
                    ((ObservableSet.AddUpdate<Label>) arg).getAddedSet();
            } else {
                changedLabelSet =
                    ((ObservableSet.RemoveUpdate<Label>) arg).getRemovedSet();
            }
            Set<GraphJCell> changedCellSet = new HashSet<GraphJCell>();
            for (Label label : changedLabelSet) {
                Set<GraphJCell> labelledCells = getLabelTree().getJCells(label);
                if (labelledCells != null) {
                    for (GraphJCell cell : labelledCells) {
                        changedCellSet.add(cell);
                        if (cell instanceof GraphJEdge) {
                            changedCellSet.add(((GraphJEdge) cell).getSourceVertex());
                            changedCellSet.add(((GraphJEdge) cell).getTargetVertex());
                        }
                    }
                }
            }
            refreshCells(changedCellSet);
        }
    }

    /**
     * Specialisation of JGraph that allows a simple modification of the
     * display attributes.
     */
    static private class AttrJGraph extends GraphJGraph {
        AttrJGraph(final AttributeFactory factory) {
            super(null, false);
            this.factory = factory;
            this.jVertexPrototype = new AttrJVertex(null);
            this.jEdgePrototype = new AttrJEdge();
        }

        @Override
        public GraphJModel<?,?> newModel() {
            return new GraphJModel<Node,Edge<Node>>(this.jVertexPrototype,
                this.jEdgePrototype);
        }

        private final AttributeFactory factory;
        private final GraphJVertex jVertexPrototype;
        private final GraphJEdge jEdgePrototype;

        /** JVertex specialisation that can be used as a prototype. */
        private class AttrJVertex extends GraphJVertex {
            /**
             * Creates a new instance.
             */
            public AttrJVertex(Node node) {
                super(AttrJGraph.this, node);
            }

            @Override
            public GraphJVertex newJVertex(Node node) {
                return new AttrJVertex(node);
            }

            @Override
            protected AttributeMap createAttributes() {
                AttributeMap result = super.createAttributes();
                AttributeMap modification =
                    AttrJGraph.this.factory.getAttributes(getNode());
                if (modification != null) {
                    result.applyMap(modification);
                }
                return result;
            }

        }

        /** JEdge specialisation that can be used as a prototype. */
        private class AttrJEdge extends GraphJEdge {
            /**
             * Constructor for a prototype.
             */
            private AttrJEdge() {
                super(AttrJGraph.this);
            }

            /**
             * Creates a new instance.
             */
            public AttrJEdge(Edge<?> edge) {
                super(AttrJGraph.this, edge);
            }

            @Override
            public GraphJEdge newJEdge(Edge<?> edge) {
                return new AttrJEdge(edge);
            }

            @Override
            protected AttributeMap createAttributes() {
                AttributeMap result = super.createAttributes();
                AttributeMap modification =
                    AttrJGraph.this.factory.getAttributes(getEdge());
                if (modification != null) {
                    result.applyMap(modification);
                }
                return result;
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
        AttributeMap getAttributes(Edge<?> edge);
    }
}
