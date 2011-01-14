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
import groove.graph.Label;
import groove.graph.LabelStore;
import groove.graph.TypeLabel;
import groove.gui.Exporter;
import groove.gui.LabelTree;
import groove.gui.Options;
import groove.gui.SetLayoutMenu;
import groove.gui.ShowHideMenu;
import groove.gui.Simulator;
import groove.gui.ZoomMenu;
import groove.gui.dialog.ErrorDialog;
import groove.gui.layout.JCellLayout;
import groove.gui.layout.Layouter;
import groove.gui.layout.SpringLayouter;
import groove.io.ExtensionFilter;
import groove.trans.SystemProperties;
import groove.util.Colors;
import groove.util.Groove;
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
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.ToolTipManager;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.BasicMarqueeHandler;
import org.jgraph.graph.CellView;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.DefaultGraphSelectionModel;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;
import org.jgraph.graph.GraphSelectionModel;
import org.jgraph.graph.PortView;
import org.jgraph.plaf.basic.BasicGraphUI;

/**
 * Enhanced j-graph, dedicated to j-models.
 * @author Arend Rensink
 * @version $Revision$ $Date: 2008-02-05 13:27:59 $
 */
abstract public class JGraph extends org.jgraph.JGraph {
    /**
     * Constructs a JGraph.
     * @param options display options object to be used
     * @param hasFilters indicates if this JGraph is to use label filtering.
     */
    public JGraph(Options options, boolean hasFilters) {
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
        setSelectionModel(createSelectionModel());
        // Make Ports invisible by Default
        setPortsVisible(false);
        // Save edits to a cell whenever something else happens
        setInvokesStopCellEditing(true);
        // Turn off double buffering for speed
        setDoubleBuffered(false);
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

    /**
     * Changes the label store of this {@link JGraph}.
     * @param store the global label stores
     * @param labelStoreMap map from names to subsets of labels; may be {@code null}
     */
    public final void setLabelStore(LabelStore store,
            Map<String,Set<TypeLabel>> labelStoreMap) {
        this.labelStore = store;
        this.labelsMap = labelStoreMap;
    }

    /**
     * Returns the set of labels and subtypes in the graph. May be
     * <code>null</code>.
     */
    public final LabelStore getLabelStore() {
        return this.labelStore;
    }

    /**
     * Returns a map from names to subsets of labels.
     * This can be used to filter labels.
     * May be {@code null} even if {@link #getLabelStore()} is not.
     */
    public final Map<String,Set<TypeLabel>> getLabelsMap() {
        return this.labelsMap;
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

    /** Returns the simulator associated with this {@link JGraph}, if any. */
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
     * Tests whether a given object is a j-node according to the criteria of
     * this j-graph. This implementation tests whether the object is an instance
     * of {@link GraphJVertex}.
     * @param jCell the object to be tested
     * @return true if <tt>cell instanceof GraphJVertex</tt>
     */
    public boolean isVertex(Object jCell) {
        return jCell instanceof GraphJVertex;
    }

    /**
     * Tests whether a given object is a j-edge according to the criteria of
     * this j-graph. This implementation tests whether the object is an instance
     * of {@link GraphJEdge}.
     * @param jCell the object to be tested
     * @return true if <tt>cell instanceof JEdge</tt>
     */
    public boolean isEdge(Object jCell) {
        return (jCell instanceof GraphJEdge);
    }

    /**
     * Convenience method to retrieve a j-edge view as a {@link JEdgeView}.
     * @param jEdge the JEdge for which to retrieve the JEdgeView
     * @return the JEdgeView corresponding to <code>jEdge</code>
     */
    public final JEdgeView getJEdgeView(GraphJEdge jEdge) {
        return (JEdgeView) getGraphLayoutCache().getMapping(jEdge, false);
    }

    /**
     * Convenience method to retrieve a j-node view as a {@link JVertexView}.
     * @param jNode the GraphJVertex for which to retrieve the JVertexView
     * @return the JVertexView corresponding to <code>jNode</code>
     */
    public final JVertexView getJNodeView(GraphJVertex jNode) {
        return (JVertexView) getGraphLayoutCache().getMapping(jNode, false);
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
            if (isVertex(element)) {
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
            this.modelRefreshing = true;
            Collection<GraphJCell> visibleCells =
                new ArrayList<GraphJCell>(jCellSet.size());
            Collection<GraphJCell> invisibleCells =
                new ArrayList<GraphJCell>(jCellSet.size());
            Collection<GraphJCell> grayedOutCells =
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
            // make sure refreshed cells are not selected
            boolean selectsInsertedCells =
                getGraphLayoutCache().isSelectsLocalInsertedCells();
            getGraphLayoutCache().setSelectsLocalInsertedCells(false);
            getGraphLayoutCache().setVisible(visibleCells.toArray(),
                invisibleCells.toArray());
            getGraphLayoutCache().setSelectsLocalInsertedCells(
                selectsInsertedCells);
            getSelectionModel().removeSelectionCells(grayedOutCells.toArray());
            if (getSelectionCount() > 0) {
                Rectangle scope =
                    Groove.toRectangle(getCellBounds(getSelectionCells()));
                if (scope != null) {
                    scrollRectToVisible(scope);
                }
            }
            this.modelRefreshing = false;
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
                    if (!isEdge(jCell)) {
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
                    if (isEdge(jCell)) {
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
     * at a given point. A flag controls if we want only vertices.
     * @param x x-coordinate of the location we want to find a cell at
     * @param y y-coordinate of the location we want to find a cell at
     * @param vertex <tt>true</tt> if we are not interested in edges
     * @return the topmost visible cell at a given point
     */
    protected GraphJCell getFirstCellForLocation(double x, double y,
            boolean vertex) {
        x /= this.scale;
        y /= this.scale;
        GraphJCell result = null;
        Rectangle xyArea = new Rectangle((int) (x - .5), (int) (y - .5), 1, 1);
        // iterate over the roots and query the visible ones
        CellView[] viewRoots = this.graphLayoutCache.getRoots();
        for (int i = viewRoots.length - 1; result == null && i >= 0; i--) {
            CellView jCellView = viewRoots[i];
            Object jCell = jCellView.getCell();
            boolean typeCorrect =
                vertex ? jCell instanceof GraphJVertex
                        : jCell instanceof GraphJCell;
            if (typeCorrect && !((GraphJCell) jCell).isGrayedOut()) {
                // now see if this jCell is sufficiently close to the point
                if (jCellView.intersects(this, xyArea)) {
                    result = (GraphJCell) jCell;
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
        return getFirstCellForLocation(x, y, false);
    }

    /**
     * This method returns the port of the topmost vertex.
     */
    @Override
    public PortView getPortViewAt(double x, double y) {
        GraphJVertex vertex =
            (GraphJVertex) getFirstCellForLocation(x, y, true);
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
        if (model instanceof GraphJModel<?,?>) {
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
            getLabelTree().updateModel();
            if (this.layouter != null) {
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
                            JGraph.this.layouter.stop();
                            // cancel the timer, because it may otherwise
                            // keep the entire program from terminating
                            timer.cancel();
                        }
                    }, MAX_LAYOUT_DURATION);
                }
            }
            setEnabled(true);
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
    public abstract GraphJModel<?,?> newModel();

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
        }
        getLabelTree().setEnabled(enabled);
        getExportAction().setEnabled(enabled);
        super.setEnabled(enabled);
    }

    /**
     * Sets a graph UI that speeds up preferred size checking by caching
     * previous values.
     */
    @Override
    public void updateUI() {
        setUI(createGraphUI());
        invalidate();
    }

    /**
     * Creates a graph UI that speeds up preferred size checking by caching
     * previously computed values.
     */
    protected BasicGraphUI createGraphUI() {
        return new MyGraphUI();
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
     * @param supportsSubtypes if <code>true</code>, the new label tree supports
     *        subtypes
     */
    public LabelTree initLabelTree(boolean supportsSubtypes) {
        this.labelTree = new LabelTree(this, supportsSubtypes);
        this.labelTree.setEnabled(isEnabled());
        return this.labelTree;
    }

    /**
     * Lazily creates and returns the label list associated with this jgraph.
     */
    public LabelTree getLabelTree() {
        if (this.labelTree == null) {
            initLabelTree(false);
        }
        return this.labelTree;
    }

    /**
     * Adds an intermediate point to a given j-edge, controlled by a given
     * location. If the location if <tt>null</tt>, the point is added directly
     * after the initial point of the edge, at a slightly randomized position.
     * Otherwise, the point is added at the given location, between the
     * (existing) points closest to the location.
     * @param jEdge the j-edge to be modified
     * @param location the point to be added
     */
    public void addPoint(GraphJEdge jEdge, Point2D location) {
        JEdgeView jEdgeView = getJEdgeView(jEdge);
        AttributeMap jEdgeAttr = new AttributeMap();
        List<?> points = jEdgeView.addPointAt(location);
        GraphConstants.setPoints(jEdgeAttr, points);
        Map<GraphJCell,AttributeMap> change =
            new HashMap<GraphJCell,AttributeMap>();
        change.put(jEdge, jEdgeAttr);
        getModel().edit(change, null, null, null);
    }

    /**
     * Removes an intermediate point from a given j-edge, controlled by a given
     * location. The point removed is either the second point (if the location
     * is <tt>null</tt>) or the one closest to the location.
     * @param jEdge the j-edge to be modified
     * @param location the point to be removed
     */
    public void removePoint(GraphJEdge jEdge, Point2D location) {
        JEdgeView jEdgeView = getJEdgeView(jEdge);
        AttributeMap jEdgeAttr = new AttributeMap();
        List<?> points = jEdgeView.removePointAt(location);
        GraphConstants.setPoints(jEdgeAttr, points);
        Map<GraphJCell,AttributeMap> change =
            new HashMap<GraphJCell,AttributeMap>();
        change.put(jEdge, jEdgeAttr);
        getModel().edit(change, null, null, null);
    }

    /**
     * Resets the label position of a given a given j-edge to the default
     * position.
     * @param jEdge the j-edge to be modified
     */
    public void resetLabelPosition(GraphJEdge jEdge) {
        AttributeMap newAttr = new AttributeMap();
        GraphConstants.setLabelPosition(newAttr,
            JCellLayout.defaultLabelPosition);
        Map<GraphJCell,AttributeMap> change =
            new HashMap<GraphJCell,AttributeMap>();
        change.put(jEdge, newAttr);
        getModel().edit(change, null, null, null);
    }

    /**
     * Sets the line style of a given a given j-edge to a given value.
     * @param jEdge the j-edge to be modified
     * @param lineStyle the new line style for <tt>jEdge</tt>
     */
    public void setLineStyle(GraphJEdge jEdge, int lineStyle) {
        AttributeMap newAttr = new AttributeMap();
        GraphConstants.setLineStyle(newAttr, lineStyle);
        Map<GraphJCell,AttributeMap> change =
            new HashMap<GraphJCell,AttributeMap>();
        change.put(jEdge, newAttr);
        getModel().edit(change, null, null, null);
    }

    /** Returns the action to export this JGraph in various formats. */
    public ExportAction getExportAction() {
        if (this.exportAction == null) {
            this.exportAction = new ExportAction();
        }
        return this.exportAction;
    }

    /**
     * Factory method for the graph selection model. This implementation returns
     * a {@link JGraph.MyGraphSelectionModel}.
     * @return the new graph selection model
     */
    protected GraphSelectionModel createSelectionModel() {
        return new MyGraphSelectionModel();
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
     * {@link groove.gui.jgraph.JGraph.MyGraphLayoutCache}.
     * @return the new graph layout cache
     */
    protected GraphLayoutCache createGraphLayoutCache() {
        return new MyGraphLayoutCache();
    }

    /**
     * Factory method for the marquee handler. This marquee handler ensures that
     * mouse right-clicks don't deselect unless they can select something else.
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
     * Callback method to determine whether a given event is a menu popup event.
     * This implementation checks for the right hand mouse button. To be
     * overridden by subclasses.
     * @param evt the event that could be a popup menu event
     * @return <tt>true</tt> if <tt>e</tt> is a popup menu event
     */
    protected boolean isAddPointEvent(MouseEvent evt) {
        return Options.isPointEditEvent(evt) && !isRemovePointEvent(evt);
    }

    /**
     * Callback method to determine whether a given event is a menu popup event.
     * This implementation checks for the right hand mouse button. To be
     * overridden by subclasses.
     * @param evt the event that could be a popup menu event
     * @return <tt>true</tt> if <tt>e</tt> is a popup menu event
     */
    protected boolean isRemovePointEvent(MouseEvent evt) {
        if (Options.isPointEditEvent(evt)) {
            Object jCell = getSelectionCell();
            if (jCell instanceof GraphJEdge) {
                // check if an intermediate point is in the neighbourhood of evt
                Rectangle r =
                    new Rectangle(evt.getX() - this.tolerance, evt.getY()
                        - this.tolerance, 2 * this.tolerance,
                        2 * this.tolerance);
                List<?> points = getJEdgeView((GraphJEdge) jCell).getPoints();
                for (int i = 1; i < points.size() - 1; i++) {
                    Point2D point = (Point2D) points.get(i);
                    if (r.intersects(point.getX(), point.getY(), 1, 1)) {
                        return true;
                    }
                }
            }
        }
        return false;
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
        if (submenu != null) {
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
     * of this j-frame.
     * @param action the action to be added
     * @require <tt>frame.getContentPane()</tt> should be initialized
     */
    protected void addAccelerator(Action action) {
        ActionMap am = getActionMap();
        am.put(action.getValue(Action.NAME), action);
        InputMap im =
            getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        im.put((KeyStroke) action.getValue(Action.ACCELERATOR_KEY),
            action.getValue(Action.NAME));
    }

    /** The options object with which this {@link JGraph} was constructed. */
    private final Options options;
    /** The set of labels currently filtered from view. */
    private final ObservableSet<Label> filteredLabels;
    /** Set of all labels and subtypes in the graph. */
    private LabelStore labelStore;
    /** Mapping from names to sub-label stores. */
    private Map<String,Set<TypeLabel>> labelsMap;
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
     * @see Exporter#export(JGraph, File)
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
                ExtensionFilter.showSaveDialog(this.exporter.getFileChooser(),
                    JGraph.this, null);
            // now save, if so required
            if (selectedFile != null) {
                try {
                    this.exporter.export(JGraph.this, selectedFile);
                } catch (IOException exc) {
                    new ErrorDialog(JGraph.this, "Error while exporting to "
                        + selectedFile, exc).setVisible(true);
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
    private static class MyGraphUI extends org.jgraph.plaf.basic.BasicGraphUI {
        MyGraphUI() {
            // empty
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
            Dimension d = this.graph.getMinimumSize();
            Point2D min =
                (d != null) ? this.graph.toScreen(new Point(d.width, d.height))
                        : new Point(0, 0);
            Point2D scaled = this.graph.toScreen(psize);
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
            double scale = this.graph.getScale();
            // shift the location by the extra border space
            return super.getEditorLocation(cell, editorSize,
                new Point2D.Double(pt.getX() + scale * (EXTRA_BORDER_SPACE + 4)
                    - 4, pt.getY() + scale * (EXTRA_BORDER_SPACE + 3) - 3));
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
            super(null, new JCellViewFactory(JGraph.this), true);
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
     * Selection model that makes sure hidden cells cannot be selected.
     */
    private class MyGraphSelectionModel extends DefaultGraphSelectionModel {
        /** Constructs an instance of the selection model. */
        MyGraphSelectionModel() {
            super(JGraph.this);
        }

        @Override
        public void addSelectionCells(Object[] cells) {
            List<Object> visibleCells = new LinkedList<Object>();
            for (int i = 0; i < cells.length; i++) {
                if (!((GraphJCell) cells[i]).isGrayedOut()) {
                    visibleCells.add(cells[i]);
                }
            }
            super.addSelectionCells(visibleCells.toArray());
        }

        @Override
        public void setSelectionCells(Object[] cells) {
            List<Object> visibleCells = new LinkedList<Object>();
            for (Object cell : cells) {
                if (!((GraphJCell) cell).isGrayedOut()) {
                    visibleCells.add(cell);
                }
            }
            super.setSelectionCells(visibleCells.toArray());
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
            Object jCell = getSelectionCell();
            if (isAddPointEvent(evt)) {
                if (jCell instanceof GraphJEdge) {
                    addPoint((GraphJEdge) jCell, evt.getPoint());
                }
            } else if (isRemovePointEvent(evt)) {
                if (jCell instanceof GraphJEdge) {
                    removePoint((GraphJEdge) jCell, evt.getPoint());
                }
            }
            maybeShowPopup(evt);
        }

        @Override
        public void mouseReleased(MouseEvent evt) {
            maybeShowPopup(evt);
        }
    }

    /**
     * Observer that calls {@link JGraph#refreshCells(Collection)} whenever it receives an
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
}