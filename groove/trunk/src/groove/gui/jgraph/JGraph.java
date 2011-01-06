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
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
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
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.ToolTipManager;

import org.jgraph.event.GraphModelEvent;
import org.jgraph.event.GraphModelEvent.GraphModelChange;
import org.jgraph.event.GraphModelListener;
import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;
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
public class JGraph extends org.jgraph.JGraph implements GraphModelListener {
    /**
     * Constructs a JGraph on the basis of a given j-model.
     * @param model the JModel for which to create a JGraph
     * @param hasFilters indicates if this JGraph is to use label filtering.
     */
    public JGraph(GraphJModel<?,?> model, boolean hasFilters) {
        super((GraphJModel<?,?>) null);
        if (hasFilters) {
            this.filteredLabels = new ObservableSet<Label>();
            this.filteredLabels.addObserver(this.refreshListener);
        } else {
            this.filteredLabels = null;
        }
        // make sure the layout cache has been created
        getGraphLayoutCache();
        setMarqueeHandler(createMarqueeHandler());
        setSelectionModel(createSelectionModel());
        setModel(model);
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
     * Returns the (possibly <code>null</code>) set of filtered labels of this
     * {@link JGraph}.
     */
    public final ObservableSet<Label> getFilteredLabels() {
        return this.filteredLabels;
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

    /**
     * Propagates some types of changes from model to view. Reacts in particular
     * to {@link GraphJModel.RefreshEdit}-events: every refreshed cell with an empty
     * attribute set gets its view attributes refreshed by a call to
     * {@link GraphJCell#createAttributes(GraphJModel)}; moreover, hidden cells are
     * unselected.
     * @see GraphJModel.RefreshEdit#getRefreshedJCells()
     */
    public void graphChanged(GraphModelEvent evt) {
        if (evt.getSource() == getModel()
            && evt.getChange() instanceof GraphJModel<?,?>.RefreshEdit) {
            Collection<? extends GraphJCell> refreshedJCells =
                ((GraphJModel<?,?>.RefreshEdit) evt.getChange()).getRefreshedJCells();
            Collection<GraphJCell> visibleCells = new ArrayList<GraphJCell>();
            Collection<GraphJCell> invisibleCells = new ArrayList<GraphJCell>();
            List<GraphJCell> emphElems = new ArrayList<GraphJCell>();
            for (GraphJCell jCell : refreshedJCells) {
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
                        // visibleCells.add(jCell);
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
                    getSelectionModel().removeSelectionCell(jCell);
                }
                if (jCell.isEmphasised()) {
                    emphElems.add(jCell);
                }
            }
            getGraphLayoutCache().setVisible(visibleCells.toArray(),
                invisibleCells.toArray());
            if (!emphElems.isEmpty()) {
                Rectangle scope =
                    Groove.toRectangle(getCellBounds(emphElems.toArray()));
                if (scope != null) {
                    scrollRectToVisible(scope);
                }
            }
        }
        // if the backing JModel has an underlying Groove graph, then
        // store the changed layout information in that Groove graph
        GraphJModel<?,?> graphJModel = getModel();
        for (Object jCell : evt.getChange().getChanged()) {
            if (jCell instanceof GraphJCell) {
                GraphJCell graphJCell = (GraphJCell) jCell;
                graphJModel.synchroniseLayout(graphJCell);
            }
        }
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
                clearSelection();
                if (this.layouter != null) {
                    this.layouter.stop();
                }
                getModel().removeGraphModelListener(this);
            }
            jModel.setFilteredLabels(getFilteredLabels());
            super.setModel(jModel);
            getLabelTree().updateModel();
            jModel.addGraphModelListener(this);
            //            jModel.refresh();
            getSelectionModel().clearSelection();
            if (this.initialized) {
                if (this.layouter != null && !jModel.isLayedOut()) {
                    int layoutCount = jModel.freeze();
                    if (layoutCount > 0) {
                        Layouter layouter =
                            layoutCount == jModel.getRootCount()
                                    ? this.layouter : this.incrementalLayouter;
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
    }

    /** Specialises the return type to a {@link GraphJModel}. */
    @Override
    public GraphJModel<?,?> getModel() {
        return (GraphJModel<?,?>) this.graphModel;
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
        this.labelTree.updateModel();
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

    /**
     * Initialises and returns an action to add a point to the currently selected j-edge.
     */
    public JCellEditAction getAddPointAction(Point atPoint) {
        if (this.addPointAction == null) {
            this.addPointAction = new AddPointAction();
            addAccelerator(this.addPointAction);
        }
        this.addPointAction.setLocation(atPoint);
        return this.addPointAction;
    }

    /**
     * Initialises and returns an action to remove a point from the currently selected j-edge.
     */
    public JCellEditAction getRemovePointAction(Point atPoint) {
        if (this.removePointAction == null) {
            this.removePointAction = new RemovePointAction();
            addAccelerator(this.removePointAction);
        }
        this.removePointAction.setLocation(atPoint);
        return this.removePointAction;
    }

    /**
     * @return an action to reset the label position of the currently selected
     *         j-edge.
     */
    public JCellEditAction getResetLabelPositionAction() {
        if (this.resetLabelPositionAction == null) {
            this.resetLabelPositionAction = new ResetLabelPositionAction();
        }
        return this.resetLabelPositionAction;
    }

    /** Returns the action to export this JGraph in various formats. */
    public ExportAction getExportAction() {
        if (this.exportAction == null) {
            this.exportAction = new ExportAction();
        }
        return this.exportAction;
    }

    /**
     * @return an action to edit the currently selected j-cell label.
     */
    public JCellEditAction getEditLabelAction() {
        if (this.editLabelAction == null) {
            this.editLabelAction = new EditLabelAction();
            addAccelerator(this.editLabelAction);
        }
        return this.editLabelAction;
    }

    /**
     * @param lineStyle the lineStyle for which to get the set-action
     * @return an action to set the line style of the currently selected j-edge.
     */
    public JCellEditAction getSetLineStyleAction(int lineStyle) {
        JCellEditAction result =
            this.setLineStyleActionMap.get(Options.getLineStyleName(lineStyle));
        if (result == null) {
            this.setLineStyleActionMap.put(Options.getLineStyleName(lineStyle),
                result = new SetLineStyleAction(lineStyle));
            addAccelerator(result);
        }
        return result;
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
        this.exporter = exporter;
    }

    /**
     * Callback method to lazily creates and return the exporter used in the
     * ExportAction.
     */
    protected Exporter getExporter() {
        if (this.exporter == null) {
            this.exporter = new Exporter();
        }
        return this.exporter;
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
        addSubmenu(result, createEditMenu(atPoint, false));
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

    /**
     * Returns a menu containing all known editing actions.
     * @param atPoint point at which the popup menu will appear
     * @param always flag to indicate if disabled actions should be added
     */
    public JMenu createEditMenu(Point atPoint, boolean always) {
        JMenu result = new JMenu("Edit");
        List<JMenuItem> items = new ArrayList<JMenuItem>();
        items.add(new JMenuItem(getAddPointAction(atPoint)));
        items.add(new JMenuItem(getRemovePointAction(atPoint)));
        items.add(new JMenuItem(getResetLabelPositionAction()));
        items.add(createLineStyleMenu());
        boolean add = always;
        if (!add) {
            for (JMenuItem item : items) {
                add |= item.isEnabled();
            }
        }
        if (add) {
            for (JMenuItem item : items) {
                result.add(item);
            }
        }
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
     * Creates and returns a fresh line style menu for this j-graph.
     */
    protected JMenu createLineStyleMenu() {
        JMenu result = new SetLineStyleMenu();
        return result;
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

    /** The set of labels currently filtered from view. */
    private final ObservableSet<Label> filteredLabels;
    /** Set of all labels and subtypes in the graph. */
    private LabelStore labelStore;
    /** Mapping from names to sub-label stores. */
    private Map<String,Set<TypeLabel>> labelsMap;
    /** The fixed refresh listener of this {@link GraphJModel}. */
    private final RefreshListener refreshListener = new RefreshListener();
    /**
     * A standard layouter setting menu over this jgraph.
     */
    protected final SetLayoutMenu setLayoutMenu = createSetLayoutMenu();

    /**
     * The label list associated with this jgraph.
     */
    protected LabelTree labelTree;

    /**
     * The currently selected prototype layouter.
     */
    protected Layouter layouter;

    /** The permanent AddPointAction associated with this j-graph. */
    protected AddPointAction addPointAction;
    /**
     * The permanent RemovePointAction associated with this j-graph.
     */
    protected RemovePointAction removePointAction;
    /**
     * The permanent EditLabelAction associated with this j-graph.
     */
    protected EditLabelAction editLabelAction;
    /**
     * The permanent ExportAction associated with this j-graph.
     */
    protected ExportAction exportAction;
    /**
     * The permanent ResetLabelPositionAction associated with this j-graph.
     */
    protected ResetLabelPositionAction resetLabelPositionAction;
    /** Map from line style names to corresponding actions. */
    protected final Map<String,JCellEditAction> setLineStyleActionMap =
        new HashMap<String,JCellEditAction>();

    /**
     * The exporter used in the ExportAction. Lazily created in
     * {@link #getExportAction()}.
     */
    private Exporter exporter;
    /**
     * The background color of this component when it is enabled.
     */
    private Color enabledBackground;

    /**
     * Flag to indicate whether this jgraph is currently registered with the
     * {@link ToolTipManager}.
     */
    private boolean toolTipEnabled;

    /**
     * A variable to determined whether this MyJGraph instance has been
     * initialized. It is important that this is the last (non-static) variable
     * declared in the class.
     */
    private final boolean initialized = true;
    /** Layouter used if only part of the model should be layed out. */
    private final Layouter incrementalLayouter =
        new SpringLayouter().newInstance(this);

    /** Maximum duration for layouting a new model. */
    static private final long MAX_LAYOUT_DURATION = 1000;

    /**
     * Abstract class for j-cell edit actions.
     */
    private abstract class JCellEditAction extends AbstractAction implements
            GraphSelectionListener {
        /**
         * Constructs an edit action that is enabled for all j-cells.
         * @param name the name of the action
         */
        protected JCellEditAction(String name) {
            super(name);
            this.allCells = true;
            this.vertexOnly = true;
            this.jCells = new ArrayList<GraphJCell>();
            this.setEnabled(false);
            addGraphSelectionListener(this);
        }

        /**
         * Constructs an edit action that is enabled for only j-vertices or
         * j-edges.
         * @param name the name of the action
         * @param vertexOnly <tt>true</tt> if the action is for j-vertices only
         */
        protected JCellEditAction(String name, boolean vertexOnly) {
            super(name);
            this.allCells = false;
            this.vertexOnly = vertexOnly;
            this.jCells = new ArrayList<GraphJCell>();
            this.setEnabled(false);
            addGraphSelectionListener(this);
        }

        /**
         * Sets the j-cell to the first selected cell. Disables the action if
         * the type of the cell disagrees with the expected type.
         */
        public void valueChanged(GraphSelectionEvent e) {
            this.jCell = null;
            this.jCells.clear();
            for (Object cell : JGraph.this.getSelectionCells()) {
                GraphJCell jCell = (GraphJCell) cell;
                if (this.allCells
                    || this.vertexOnly == (jCell instanceof GraphJVertex)) {
                    this.jCell = jCell;
                    this.jCells.add(jCell);
                }
            }
            this.setEnabled(this.jCell != null);
        }

        /**
         * Sets the location attribute of this action.
         */
        public void setLocation(Point2D location) {
            this.location = location;
        }

        /**
         * Switch indication that the action is enabled for all types of
         * j-cells.
         */
        protected final boolean allCells;
        /** Switch indication that the action is enabled for all j-vertices. */
        protected final boolean vertexOnly;
        /** The first currently selected j-cell of the right type. */
        protected GraphJCell jCell;
        /** List list of currently selected j-cells of the right type. */
        protected final List<GraphJCell> jCells;
        /** The currently set point location. */
        protected Point2D location;
    }

    /**
     * Action to add a point to the currently selected j-edge.
     */
    private class AddPointAction extends JCellEditAction {
        /** Constructs an instance of the action. */
        AddPointAction() {
            super(Options.ADD_POINT_ACTION, false);
            putValue(ACCELERATOR_KEY, Options.ADD_POINT_KEY);
        }

        @Override
        public boolean isEnabled() {
            return this.jCells.size() == 1;
        }

        public void actionPerformed(ActionEvent evt) {
            addPoint((GraphJEdge) this.jCell, this.location);
        }
    }

    /**
     * Action to edit the label of the currently selected j-cell.
     */
    private class EditLabelAction extends JCellEditAction {
        /** Constructs an instance of the action. */
        EditLabelAction() {
            super(Options.EDIT_LABEL_ACTION);
            putValue(ACCELERATOR_KEY, Options.RENAME_KEY);
        }

        public void actionPerformed(ActionEvent evt) {
            startEditingAtCell(this.jCell);
        }
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
                getExporter().getFileChooser().setSelectedFile(
                    new File(fileName));
            }
            File selectedFile =
                ExtensionFilter.showSaveDialog(getExporter().getFileChooser(),
                    JGraph.this, null);
            // now save, if so required
            if (selectedFile != null) {
                try {
                    getExporter().export(JGraph.this, selectedFile);
                } catch (IOException exc) {
                    new ErrorDialog(JGraph.this, "Error while exporting to "
                        + selectedFile, exc).setVisible(true);
                }

            }
        }
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

    /**
     * Action to remove a point from the currently selected j-edge.
     */
    private class RemovePointAction extends JCellEditAction {
        /** Constructs an instance of the action. */
        RemovePointAction() {
            super(Options.REMOVE_POINT_ACTION, false);
            putValue(ACCELERATOR_KEY, Options.REMOVE_POINT_KEY);
        }

        @Override
        public boolean isEnabled() {
            return this.jCells.size() == 1;
        }

        public void actionPerformed(ActionEvent evt) {
            removePoint((GraphJEdge) this.jCell, this.location);
        }
    }

    /**
     * Action set the label of the currently selected j-cell to its default
     * position.
     */
    private class ResetLabelPositionAction extends JCellEditAction {
        /** Constructs an instance of the action. */
        ResetLabelPositionAction() {
            super(Options.RESET_LABEL_POSITION_ACTION, false);
        }

        public void actionPerformed(ActionEvent evt) {
            for (GraphJCell jCell : this.jCells) {
                resetLabelPosition((GraphJEdge) jCell);
            }
        }
    }

    /**
     * Action to set the line style of the currently selected j-edge.
     */
    private class SetLineStyleAction extends JCellEditAction {
        /** Constructs an instance of the action, for a given line style. */
        SetLineStyleAction(int lineStyle) {
            super(Options.getLineStyleName(lineStyle), false);
            putValue(ACCELERATOR_KEY, Options.getLineStyleKey(lineStyle));
            this.lineStyle = lineStyle;
        }

        public void actionPerformed(ActionEvent evt) {
            for (GraphJCell jCell : this.jCells) {
                GraphJEdge jEdge = (GraphJEdge) jCell;
                setLineStyle(jEdge, this.lineStyle);
                List<?> points =
                    GraphConstants.getPoints(jCell.getAttributes());
                if (points == null || points.size() == 2) {
                    addPoint(jEdge, this.location);
                }
            }
        }

        /** The line style set by this action instance. */
        protected final int lineStyle;
    }

    /**
     * Menu offering a choice of line style setting actions.
     */
    private class SetLineStyleMenu extends JMenu implements
            GraphSelectionListener {
        /** Constructs an instance of the action. */
        SetLineStyleMenu() {
            super(Options.SET_LINE_STYLE_MENU);
            valueChanged(null);
            addGraphSelectionListener(this);
            // initialize the line style menu
            add(getSetLineStyleAction(GraphConstants.STYLE_ORTHOGONAL));
            add(getSetLineStyleAction(GraphConstants.STYLE_SPLINE));
            add(getSetLineStyleAction(GraphConstants.STYLE_BEZIER));
            add(getSetLineStyleAction(JAttr.STYLE_MANHATTAN));
        }

        public void valueChanged(GraphSelectionEvent e) {
            this.setEnabled(getSelectionCell() instanceof GraphJEdge);
        }
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

        /**
         * Returns a listener that can update the graph when the model changes.
         */
        @Override
        protected GraphModelListener createGraphModelListener() {
            return new MyGraphModelHandler();
        }

        private class MyGraphModelHandler extends GraphModelHandler {
            MyGraphModelHandler() {
                // empty
            }

            @Override
            public void graphChanged(GraphModelEvent e) {
                if (!(e.getChange() instanceof groove.gui.jgraph.GraphJModel.RefreshEdit)) {
                    super.graphChanged(e);
                }
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
            super(null, new JCellViewFactory(JGraph.this), true);
            setSelectsLocalInsertedCells(false);
            setShowsExistingConnections(false);
            setShowsChangedConnections(false);
            setShowsInsertedConnections(false);
            setHidesExistingConnections(false);
            setHidesDanglingConnections(false);
        }

        /**
         * After calling the super method, sets all roots to visible. This is
         * necessary because the cache is partial.
         */
        @Override
        public void setModel(GraphModel model) {
            super.setModel(model);
            Object[] cells = DefaultGraphModel.getRoots(this.getModel());
            CellView[] cellViews = getMapping(cells, true);
            insertViews(cellViews);
            // Update PortView Cache and Notify Observers
            updatePorts();
            cellViewsChanged(getRoots());
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
         * Overrides the method so {@link GraphJModel.RefreshEdit}s are not passed
         * on.
         */
        @Override
        public void graphChanged(GraphModelChange change) {
            if (!(change instanceof GraphJModel<?,?>.RefreshEdit)) {
                super.graphChanged(change);
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
     * Observer that calls {@link GraphJModel#refresh()} whenever it receives an
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
            getModel().refresh(changedCellSet);
        }
    }
}