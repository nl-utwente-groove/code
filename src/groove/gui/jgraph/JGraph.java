/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
 * $Id: JGraph.java,v 1.30 2008-02-05 13:27:59 rensink Exp $
 */
package groove.gui.jgraph;

import groove.gui.LabelList;
import groove.gui.Options;
import groove.gui.SetLayoutMenu;
import groove.gui.ShowHideMenu;
import groove.gui.ZoomMenu;
import groove.gui.jgraph.JModel.RefreshEdit;
import groove.gui.layout.JCellLayout;
import groove.gui.layout.Layouter;
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
import java.util.ArrayList;
import java.util.Arrays;
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
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.ToolTipManager;

import org.jgraph.event.GraphModelEvent;
import org.jgraph.event.GraphModelListener;
import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;
import org.jgraph.event.GraphModelEvent.GraphModelChange;
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
 * @version $Revision: 1.30 $ $Date: 2008-02-05 13:27:59 $
 */
public class JGraph extends org.jgraph.JGraph implements GraphModelListener {
	/**
     * Constructs a JGraph on the basis of a given j-model. 
     * @param model the JModel for which to create a JGraph
     * @param hasFilters indicates if this JGraph is to use label filtering.
     */
    public JGraph(JModel model, boolean hasFilters) {
        super((JModel) null);
        if (hasFilters) {
        	this.filteredLabels = new ObservableSet<String>();
        	this.filteredLabels.addObserver(refreshListener);
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
    }

    /**
	 * Returns the (possibly <code>null</code>) set of filtered labels of 
	 * this {@link JGraph}.
	 */
	public final ObservableSet<String> getFilteredLabels() {
		return this.filteredLabels;
	}

    /**
     * Overrides the method to call {@link JCell#getText()} whenever <code>object</code>
     * is recognised as a {@link JVertexView}, {@link JEdgeView} or {@link JCell}.
     */
    @Override
    public String convertValueToString(Object value) {
    	if (value instanceof JVertexView) {
    		return ((JVertexView) value).getCell().getText();
    	} else if (value instanceof JEdgeView) {
    		return ((JEdgeView) value).getCell().getText();
    	} else if (value instanceof JCell) {
    		return ((JCell) value).getText();
    	} else {
    		return value.toString();
    	}
    }

    /**
     * Returns a tool tip text for the front graph cell onder the mouse.
     */
    @Override
    public String getToolTipText(MouseEvent evt) {
        JCell jCell = (JCell) getFirstCellForLocation(evt.getX(), evt.getY());
        return getModel().getToolTipText(jCell);
    }

    /**
     * Tests whether a given object is a j-node according to the criteria of this j-graph.
     * This implementation tests whether the object is an instance of {@link JVertex}.
     * @param jCell the object to be tested
     * @return true if <tt>cell instanceof JVertex</tt>
     */
    public boolean isVertex(Object jCell) {
        return jCell instanceof JVertex;
    }

    /**
     * Tests whether a given object is a j-edge according to the criteria of this j-graph.
     * This implementation tests whether the object is an instance of {@link JEdge}.
     * @param jCell the object to be tested
     * @return true if <tt>cell instanceof JEdge</tt>
     */
    public boolean isEdge(Object jCell) {
        return (jCell instanceof JEdge);
    }

    /**
     * Convenience method te retrieve a j-edge view as a {@link JEdgeView}.
     * @param jEdge the JEdge for which to retrieve the JEdgeView
     * @return the JEdgeView corresponding to <code>jEdge</code>
     */
    public final JEdgeView getJEdgeView(JEdge jEdge) {
        return (JEdgeView) getGraphLayoutCache().getMapping(jEdge, false);
    }

    /**
     * Convenience method te retrieve a j-node view as a {@link JVertexView}.
     * @param jNode the JVertex for which to retrieve the JVertexView
     * @return the JVertexView corresponding to <code>jNode</code>
     */
    public final JVertexView getJNodeView(JVertex jNode) {
        return (JVertexView) getGraphLayoutCache().getMapping(jNode, false);
    }

    /**
     * Overrides the super method to make sure hidden cells ae never editable.
     * If the specified cell is hidden (according to the underlying model), returns false;
     * otherwise, passes on the query to super.
     * @see JModel#isGrayedOut(JCell)
     */
    @Override
    public boolean isCellEditable(Object cell) {
        return !(cell instanceof JCell && getModel().isGrayedOut((JCell) cell)) && super.isCellEditable(cell);
    }
    
    /**
     * Overwrites the method from JGraph for efficiency.
     */
    @Override
    public Object[] getDescendants(Object[] cells) {
        List<Object> res = new LinkedList<Object>();
        for (int i = 0; i < cells.length; i++) {
            res.add(cells[i]);
            if (isVertex(cells[i])) {
                res.add(((JVertex) cells[i]).getChildAt(0));
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
     * Propagates some types of changes from model to view. Reacts in particular to
     * {@link JModel.RefreshEdit}-events: every refreshed cell with an empty attribute set gets its
     * view attributes refreshed by a call to {@link JModel#createTransientJAttr(JCell)}; moreover, hidden cells
     * are deselected. by a call to {@link JModel#createTransientJAttr(JCell)}.
     * @see JModel.RefreshEdit#getRefreshedJCells()
     */
    public void graphChanged(GraphModelEvent evt) {
        if (evt.getSource() == getModel() && evt.getChange() instanceof JModel.RefreshEdit) {
            Collection<JCell> refreshedJCells = ((JModel.RefreshEdit) evt.getChange()).getRefreshedJCells();
            Collection<JCell> visibleCells = new ArrayList<JCell>();
            Collection<JCell> invisibleCells = new ArrayList<JCell>();
            Set<JCell> emphElems = new HashSet<JCell>();
            for (JCell jCell: refreshedJCells) {
            	AttributeMap transientAttributes = getModel().createTransientJAttr(jCell);
                CellView jView = getGraphLayoutCache().getMapping(jCell, false);
                if (jView != null) {
                	if (!jCell.isVisible()) {
                	    invisibleCells.add(jCell);
                        getSelectionModel().removeSelectionCell(jCell);
                	} else {
                	    visibleCells.add(jCell);
                    }
                    jView.changeAttributes(getGraphLayoutCache(),transientAttributes);
                } else {
                	if (jCell.isVisible()) {
                		visibleCells.add(jCell);
                	}
                }
                if (getModel().isGrayedOut(jCell)) {
                    getSelectionModel().removeSelectionCell(jCell);
                }
                if (getModel().isEmphasized(jCell)) {
                    emphElems.add(jCell);
                }
            }
        	getGraphLayoutCache().setVisible(visibleCells.toArray(), invisibleCells.toArray());
            if (!emphElems.isEmpty()) {
                Rectangle scope = Groove.toRectangle(getCellBounds(emphElems.toArray()));
                if (scope != null) {
                    scrollRectToVisible(scope);
                }
            }
        }
    }

    /** 
     * Helper method for {@link #getFirstCellForLocation(double, double)} and
     * {@link #getPortViewAt(double, double)}.
     * Returns the topmost visible cell at a given point.
     * A flag controls if we want only vertices.
     * @param x x-coordinate of the location we want to find a cell at
     * @param y y-coordinate of the location we want to find a cell at
     * @param vertex <tt>true</tt> if we are not interested in edges
     * @return the topmost visible cell at a given point
     */
    protected Object getFirstCellForLocation(double x, double y, boolean vertex) {
        x /= scale;
        y /= scale;
        Object result = null;
        JModel jModel = getModel();
        Rectangle xyArea = new Rectangle((int) (x-.5), (int) (y-.5), 1, 1);
        // iterate over the roots and query the visible ones
        CellView[] viewRoots = graphLayoutCache.getRoots();
        for (int i = viewRoots.length - 1; result == null && i >= 0; i--) {
            CellView jCellView = viewRoots[i];
            Object jCell = jCellView.getCell();
            boolean typeCorrect = vertex ? jCell instanceof JVertex : jCell instanceof JCell;
            if (typeCorrect && !jModel.isGrayedOut((JCell) jCell)) {
                // now see if this jCell is sufficiently close to the point
//                CellView jCellView = graphLayoutCache.getMapping(jCell, false);
                if (jCellView != null && jCellView.intersects(this, xyArea)) {
                    result = jCell;
                }
            }
        }
        return result;
    }

    /** 
     * Overrides the super method for greater efficiency.
     * Only returns visible cells.
     */
    @Override
    public Object getFirstCellForLocation(double x, double y) {
        return getFirstCellForLocation(x, y, false);
    }
    
    /**
     * This method rturns the port of the topmost vertex.
     */
    @Override
    public PortView getPortViewAt(double x, double y) {
        JVertex vertex = (JVertex) getFirstCellForLocation(x,y, true);
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
    	if (model instanceof JModel) {
			JModel jModel = (JModel) model;
			if (getModel() != null) {
				clearSelection();
				if (layouter != null) {
					layouter.stop();
				}
				getModel().removeGraphModelListener(this);
			}
			super.setModel(jModel);
			jModel.setFilteredLabels(getFilteredLabels());
			getLabelList().updateModel();
			jModel.addGraphModelListener(this);
//			jModel.refresh();
			if (initialized) {
				if (layouter != null && !jModel.isLayedOut()) {
					if (jModel.freeze()) {
//					    SwingUtilities.invokeLater(new Runnable() {
//					        public void run() {
		                        layouter.start(false);
		                        new Timer().schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        layouter.stop();
                                    }
		                        }, MAX_LAYOUT_DURATION);
					        }
//					    });
//					}
				}
				setEnabled(true);
			}
		}
    }
    
    /** Specialises the return type to a {@link JModel}. */
    @Override
	public JModel getModel() {
    	return (JModel) graphModel;
	}

	/**
     * In addition to delegating the method to the label list and to <tt>super</tt>, 
     * sets the background color to
     * <tt>null</tt> when disabled and back to the default when enabled.
     */
    @Override
    public void setEnabled(boolean enabled) {
        if (enabled != isEnabled()) {
            if (!enabled) {
                enabledBackground = getBackground();
                setBackground(null);
            } else if (enabledBackground != null) {
                setBackground(enabledBackground);
            }
        }
        getLabelList().setEnabled(enabled);
        super.setEnabled(enabled);
    }
//    
//    /** Lazily creates the graph layout cache. */
//    @Override
//	public GraphLayoutCache getGraphLayoutCache() {
//    	GraphLayoutCache result = super.getGraphLayoutCache();
//    	if (result == null) {
//    		result = createGraphLayoutCache();
//    		setGraphLayoutCache(result);
//    	}
//    	return result;
//	}

	/**
	 * Sets a graph UI that speeds up preferred size checking
	 * by caching previous values.
	 */
	@Override
	public void updateUI() {
		setUI(createGraphUI());
		invalidate();
	}

	/**
	 * Creates a graph UI that speeds up preferred size checking
	 * by caching previously computed values.
	 */
	protected BasicGraphUI createGraphUI() {
		return new MyGraphUI();
	}

	/**
     * Creates and returns an image of the jgraph, or <tt>null</tt> if the jgraph is empty.
     * @return an image object of the jgraph; <tt>null</tt> if this jgraph is empty.
     */
    public BufferedImage toImage() {
        Rectangle2D bounds = getGraphBounds();

        if (bounds != null) {
            toScreen(bounds);

            // Create a Buffered Image
            BufferedImage img = new BufferedImage((int) bounds.getWidth() + 10, (int) bounds
                    .getHeight() + 10, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = img.createGraphics();
            graphics.setColor(getBackground());
            graphics.fillRect(0, 0, img.getWidth(), img.getHeight());
            graphics.translate(-bounds.getX() + 5, -bounds.getY() + 5);

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
        return layouter;
    }

    /**
     * Sets (but doe snot start) the layout action for this jgraph. First stops the current layout action, if
     * it is running.
     * @param prototypeLayouter prototype for the new layout action; the actual layout action is
     *        obtained by calling <tt>newInstance(this)</tt>
     * @see #getLayouter()
     */
    public void setLayouter(Layouter prototypeLayouter) {
        if (layouter != null) {
            layouter.stop();
        }
        layouter = prototypeLayouter.newInstance(this);
    }
    
    /**
     * Lays out this graph according to the currently set layouter (if any). 
     * @see Layouter#start(boolean)
     */
    public void doGraphLayout() {
        if (layouter != null) {
            layouter.start(true);
        }
    }

    /**
     * Indicates whether this jgraph is currently registered at the tool tip manager.
     * @return <tt>true</tt> if this jgraph is currently registered at the tool tip manager
     */
    public boolean getToolTipEnabled() {
        return toolTipEnabled;
    }

    /**
     * Registers ur unregisters this jgraph with the tool tip manager. The current registration
     * state can be queried using <tt>getToolTipEnabled()</tt>
     * @param enabled <tt>true</tt> if this jgraph is to be registered with the tool tip manager
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
        toolTipEnabled = enabled;
    }

    /**
     * Adds all known j-edge editing actions to a given popup menu.
     * @param menu the menu to which to add some actions
     * @param always flag to indicate if disabled actions should be added
     */
    public void fillOutEditMenu(JPopupMenu menu, boolean always) {
    	List<JMenuItem> items = new ArrayList<JMenuItem>();
    	items.add(new JMenuItem(getAddPointAction()));
    	items.add(new JMenuItem(getRemovePointAction()));
    	items.add(new JMenuItem(getResetLabelPositionAction()));
    	items.add(createLineStyleMenu());
    	boolean add = always;
    	if (! add) {
    		for (JMenuItem item: items) {
    			add |= item.isEnabled();
    		}
    	}
    	if (add) {
			addSeparatorUnlessFirst(menu);
			for (JMenuItem item : items) {
				menu.add(item);
			}
		}
    }

    /**
	 * Adds all the display menu items of this jgraph to a given popup menu.
	 * 
	 * @param menu
	 *            the popup menu to receive the items
	 */
    public void fillOutDisplayMenu(JPopupMenu menu) {
        addSeparatorUnlessFirst(menu);
        Object[] cells = getSelectionCells();
        if (filteredLabels != null && cells.length > 0) {
        	menu.add(new FilterAction(cells));
        	menu.addSeparator();
        }
        menu.add(createShowHideMenu());
        menu.add(createZoomMenu());
    }

    /**
     * Adds all the menu items from the layouter setting menu of this jgraph to a given popup menu.
     * @param menu the popup menu to receive the items
     */
    public void fillOutSetLayoutMenu(JPopupMenu menu) {
        addSeparatorUnlessFirst(menu);
        for (int i = 0; i < setLayoutMenu.getComponentCount(); i++) {
            menu.add(setLayoutMenu.getComponent(i));
        }
    }

    /**
     * Adds the items of a layout menu for this jgraph to a given popup menu.
     * The items added are the current layout action and a layouter setting sub-menu.
     * @param menu the popup menu to receive the items
     */
    public void fillOutLayoutMenu(JPopupMenu menu) {
        addSeparatorUnlessFirst(menu);
        menu.add(setLayoutMenu.getCurrentLayoutItem());
        menu.add(setLayoutMenu);
    }

    /**
     * Lazily creates and returns the label list associated with this jgraph.
     */
    public LabelList getLabelList() {
    	if (labelList == null) {
    		labelList = new LabelList(this);
    		labelList.updateModel();
    	}
        return labelList;
    }

    /**
     * Adds an intermediate point to a given j-edge, controlled by
     * a given location.
     * If the location if <tt>null</tt>, the point is added directly after
     * the initial point of the edge, at a slightly randomized position.
     * Otherwise, the point is added at the given location, between the 
     * (existing) points closest to the location.
     * @param jEdge the j-edge to be modified
     * @param location the point to be added
     */
    public void addPoint(JEdge jEdge, Point2D location) {
        JEdgeView jEdgeView = getJEdgeView(jEdge);
        AttributeMap jEdgeAttr = new AttributeMap();
        List<?> points = jEdgeView.addPointAt(location);
        GraphConstants.setPoints(jEdgeAttr, points);
        Map<JCell,AttributeMap> change = new HashMap<JCell,AttributeMap>();
        change.put(jEdge, jEdgeAttr);
        getModel().edit(change, null, null, null);
    }

    /**
     * Removes an intermediate point from a given j-edge, controlled by
     * a given location.
     * The point removed is either the second point (if the location is <tt>null</tt>)
     * or the one closest to the location.
     * @param jEdge the j-edge to be modified
     * @param location the point to be removed
     */
    public void removePoint(JEdge jEdge, Point2D location) {
        JEdgeView jEdgeView = getJEdgeView(jEdge);
        AttributeMap jEdgeAttr = new AttributeMap();
        List<?> points = jEdgeView.removePointAt(location);
        GraphConstants.setPoints(jEdgeAttr, points);
        Map<JCell,AttributeMap> change = new HashMap<JCell,AttributeMap>();
        change.put(jEdge, jEdgeAttr);
        getModel().edit(change, null, null, null);
    }
    
    /**
     * Resets the label position of a given a given j-edge to the default position.
     * @param jEdge the j-edge to be modified
     */
    public void resetLabelPosition(JEdge jEdge) {
        AttributeMap newAttr = new AttributeMap();
        GraphConstants.setLabelPosition(newAttr, JCellLayout.defaultLabelPosition);
        Map<JCell,AttributeMap> change = new HashMap<JCell,AttributeMap>();
        change.put(jEdge, newAttr);
        getModel().edit(change, null, null, null);
    }
    
    /**
     * Sets the line style of a given a given j-edge to a given value.
     * @param jEdge the j-edge to be modified
     * @param lineStyle the new line style for <tt>jEdge</tt>
     */
    public void setLineStyle(JEdge jEdge, int lineStyle) {
        AttributeMap newAttr = new AttributeMap();
        GraphConstants.setLineStyle(newAttr, lineStyle);
        Map<JCell,AttributeMap> change = new HashMap<JCell,AttributeMap>();
        change.put(jEdge, newAttr);
        getModel().edit(change, null, null, null);
    }

    /**
     * @return an action to add a point to the currently selected j-edge.
     */
    public JCellEditAction getAddPointAction() {
        if (addPointAction == null) {
            addPointAction = new AddPointAction();
            addAccelerator(addPointAction);
        }
        return addPointAction;
    }

	/**
     * @return an action to remove a point from the currently selected j-edge.
     */
    public JCellEditAction getRemovePointAction() {
        if (removePointAction == null) {
            removePointAction = new RemovePointAction();
            addAccelerator(removePointAction);
        }
        return removePointAction;
    }

    /**
     * @return an action to reset the label position of the currently selected j-edge.
     */
    public JCellEditAction getResetLabelPositionAction() {
        if (resetLabelPositionAction == null) {
            resetLabelPositionAction = new ResetLabelPositionAction();
        }
        return resetLabelPositionAction;
    }

    /**
     * @return an action to edit the currently selected j-cell label.
     */
    public JCellEditAction getEditLabelAction() {
        if (editLabelAction == null) {
            editLabelAction = new EditLabelAction();
            addAccelerator(editLabelAction);
        }
        return editLabelAction;
    }

    /**
     * @param lineStyle the lineStyle for which to get the set-action
     * @return an action to set the line style of the currently selected j-edge.
     */
    public JCellEditAction getSetLineStyleAction(int lineStyle) {
        JCellEditAction result = setLineStyleActionMap.get(Options.getLineStyleName(lineStyle));
        if (result == null) {
            setLineStyleActionMap.put(Options.getLineStyleName(lineStyle), result = new SetLineStyleAction(lineStyle));
            addAccelerator(result);
        }
        return result;
    }

    /**
     * Factory method for the graph selection model.
     * This implementation returns a {@link JGraph.MyGraphSelectionModel}.
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
     * Factory method for the graph layout cache.
     * This implementation returns a {@link groove.gui.jgraph.JGraph.MyGraphLayoutCache}.
     * @return the new graph layout cache
     */
    protected GraphLayoutCache createGraphLayoutCache() {
        return new MyGraphLayoutCache();
    }

    /**
     * Factory method for the marquee handler.
     */
    protected BasicMarqueeHandler createMarqueeHandler() {
        return new BasicMarqueeHandler();
    }

    /** Shows a popup menu if the event is a popup trigger. */
    protected void maybeShowPopup(MouseEvent evt) {
    	if (isPopupMenuEvent(evt)) {
            Point atPoint = evt.getPoint();
            getPopupMenu(atPoint).show(this, atPoint.x, atPoint.y);
    	}
    }
    
    /**
     * Callback method to determine whether a given event is a menu popup event. This implementation
     * checks for the right hand mouse button. To be overridden by subclasses.
     * @param evt the event that could be a popup menu event
     * @return <tt>true</tt> if <tt>e</tt> is a popup menu event
     */
    protected boolean isPopupMenuEvent(MouseEvent evt) {
        return evt.isPopupTrigger() && !evt.isControlDown();
    }

    /**
     * Callback method to determine whether a given event is a menu popup event. This implementation
     * checks for the right hand mouse button. To be overridden by subclasses.
     * @param evt the event that could be a popup menu event
     * @return <tt>true</tt> if <tt>e</tt> is a popup menu event
     */
    protected boolean isAddPointEvent(MouseEvent evt) {
        return Options.isPointEditEvent(evt) && !isRemovePointEvent(evt);
    }

    /**
     * Callback method to determine whether a given event is a menu popup event. This implementation
     * checks for the right hand mouse button. To be overridden by subclasses.
     * @param evt the event that could be a popup menu event
     * @return <tt>true</tt> if <tt>e</tt> is a popup menu event
     */
    protected boolean isRemovePointEvent(MouseEvent evt) {
        if (Options.isPointEditEvent(evt)) {
            Object jCell = getSelectionCell();
            if (jCell instanceof JEdge) {
                // check if an intermediate point is in the neighbourhood of evt
                Rectangle r = new Rectangle(evt.getX()-tolerance, evt.getY()-tolerance, 2*tolerance, 2*tolerance);
                List<?> points = getJEdgeView((JEdge) jCell).getPoints();
                for (int i = 1; i < points.size()-1; i++) {
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
	 * Lazily creates and returns the popup menu for this j-graph, activated
	 * for a given point of the j-graph.
     * @param atPoint the point at which the menu is to be activated
	 */
	protected final JPopupMenu getPopupMenu(Point atPoint) {
		JPopupMenu popupMenu = new JPopupMenu();
		fillPopupMenu(popupMenu);
		activatePopupMenu(atPoint);
		return popupMenu;
	}

	/**
	 * Fills out the popup menu for this jgraph. Does not clear the menu first. This method is
	 * invoked at least once whenever a new jmodel is set. This implementation successively invokes
	 * {@link #fillOutLayoutMenu(JPopupMenu)}and {@link #fillOutDisplayMenu(JPopupMenu)}.
	 * @param result the menu to be initialised
	 * @see #activatePopupMenu(Point)
	 */
	protected void fillPopupMenu(JPopupMenu result) {
	    fillOutEditMenu(result, false);
	    fillOutDisplayMenu(result);
	    fillOutLayoutMenu(result);
	}

	/**
     * Adds a separator to a menu, unless the menu is empty.
     */
    protected void addSeparatorUnlessFirst(JPopupMenu menu) {
        if (menu.getComponentCount() > 0) {
            menu.addSeparator();
        }
    }

    /**
	 * Activates and returns a popup menu, given a certain point at which it is to appear. The menu
	 * may be freshly created, but will typically be fixed.
	 * @param point the selected point at the moment of menu popup
	 */
	protected void activatePopupMenu(Point point) {
	    getAddPointAction().setLocation(point);
	    getRemovePointAction().setLocation(point);
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
    
    /** Creates and returns a fresh layout setting meny upon this j-graph. */
    protected SetLayoutMenu createSetLayoutMenu() {
        return new SetLayoutMenu(this);
    }
    
    /**
     * Cretes and returns a fresh line style menu for this j-graph.
     */
    protected JMenu createLineStyleMenu() {
        JMenu result = new SetLineStyleMenu();
        // initialize the line style manu
        result.add(getSetLineStyleAction(GraphConstants.STYLE_ORTHOGONAL));
        result.add(getSetLineStyleAction(GraphConstants.STYLE_SPLINE));
        result.add(getSetLineStyleAction(GraphConstants.STYLE_BEZIER));
        result.add(getSetLineStyleAction(JAttr.STYLE_MANHATTAN));
        return result;
    }
    
    /**
     * Adds the accelerator key for a given action to
     * the action and input maps of this j-frame.
     * @param action the action to be added
     * @require <tt>frame.getContentPane()</tt> should be initialized
     */
    protected void addAccelerator(Action action) {
        ActionMap am = getActionMap();
        am.put(action.getValue(Action.NAME), action);
        InputMap im = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        im.put((KeyStroke) action.getValue(Action.ACCELERATOR_KEY), action.getValue(Action.NAME));
    }

    /** The set of labels currently filtered from view. */
    private final ObservableSet<String> filteredLabels;
    /** The fixed refresh listener of this {@link JModel}. */
    private final RefreshListener refreshListener = new RefreshListener();
    /**
     * A standard layouter setting menu over this jgraph.
     */
    protected final SetLayoutMenu setLayoutMenu = createSetLayoutMenu();

    /**
     * The label list associated with this jgraph.
     */
    protected LabelList labelList;

    /**
     * The currently selected prototype layouter.
     */
    protected Layouter layouter;
    
    /** The permanent <code>AddPointAction</code> associated with this j-graph. */
    protected AddPointAction addPointAction;
    /** The permanent <code>RemovePointAction</code> associated with this j-graph. */
    protected RemovePointAction removePointAction;
    /** The permanent <code>EditLabelAction</code> associated with this j-graph. */
    protected EditLabelAction editLabelAction;
    /** The permanent <code>ResetLabelPositionAction</code> associated with this j-graph. */
    protected ResetLabelPositionAction resetLabelPositionAction;
    /** Map from line style names to corresponding actions. */
    protected final Map<String,JCellEditAction> setLineStyleActionMap = new HashMap<String,JCellEditAction>();
    
    /**
     * The background color of this component when it is enabled.
     */
    private Color enabledBackground;

    /**
     * Flag to indicate whether this jgraph is currently registered with the {@link ToolTipManager}.
     */
    private boolean toolTipEnabled;

    /**
     * A variable to determined whether this MyJGraph instance has been initialized. It is important
     * that this is the last (non-static) variable declared in the class.
     */
    private boolean initialized = true;
    
    /** Maximum duration for layouting a new model. */
    static private final long MAX_LAYOUT_DURATION = 1000;
    
    /**
     * Abstract class for j-cell edit actions.
     */
    private abstract class JCellEditAction extends AbstractAction implements GraphSelectionListener {
        /**
         * Constructs an edit action that is enabled for all j-cells.
         * @param name the name of the action
         */
        protected JCellEditAction(String name) {
            super(name);
            this.allCells = true;
            this.vertexOnly = true;
            jCells = new ArrayList<JCell>();
            setEnabled(false);
            JGraph.this.addGraphSelectionListener(this);
        }
        
        /**
         * Constructs an edit action that is enabled for only j-vertices or j-edges.
         * @param name the name of the action
         * @param vertexOnly <tt>true</tt> if the action is for j-vertices only
         */
        protected JCellEditAction(String name, boolean vertexOnly) {
            super(name);
            this.allCells = false;
            this.vertexOnly = vertexOnly;
            this.jCells = new ArrayList<JCell>();
            setEnabled(false);
            JGraph.this.addGraphSelectionListener(this);
        }
        
        /**
         * Sets the j-cell to the first selected cell.
         * Disables the action if the type of the cell disagrees with the expected type.
         */
        public void valueChanged(GraphSelectionEvent e) {
        	jCell = null;
        	jCells.clear();
        	for (Object cell: JGraph.this.getSelectionCells()) {
        		JCell jCell = (JCell) cell;
        		if (allCells || vertexOnly == (jCell instanceof JVertex)) {
        			this.jCell = jCell;
        			this.jCells.add(jCell);
        		}
        	}
        	setEnabled(jCell != null);
        }
        
        /**
         * Sets the location attribute of this action.
         */
        public void setLocation(Point2D location) {
            this.location = location;
        }
        
        /** Switch indication that the action is enabled for all types of j-cells. */
        protected final boolean allCells;
        /** Switch indication that the action is enabled for all j-vertices. */
        protected final boolean vertexOnly;
        /** The first currently selected j-cell of the right tyope. */
        protected JCell jCell;
        /** List list of currently selected j-cells of the right type. */
        protected final List<JCell> jCells;
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
        
        public void actionPerformed(ActionEvent evt) {
        	addPoint((JEdge) jCell, location);
        }
    }

    /**
     * Action to edit the label of the currently selected j-cell.
     */
    private class EditLabelAction extends JCellEditAction {
    	/** Constructs an instance of the action. */
        EditLabelAction() {
            super(Options.EDIT_LABEL_ACTION);
            putValue(ACCELERATOR_KEY, Options.RELABEL_KEY);
        }
        
        public void actionPerformed(ActionEvent evt) {
            startEditingAtCell(jCell);
        }
    }

    /** Action to turn filtering on for a set of selected cells. */
    private class FilterAction extends AbstractAction {
    	FilterAction(Object[] cells) {
    		super(Options.FILTER_ACTION_NAME);
    		this.cells = cells;
    	}
    	
    	public void actionPerformed(ActionEvent e) {
			Set<String> labels = new HashSet<String>();
			for (Object cell: cells) {
				labels.addAll(((JCell) cell).getListLabels());
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
        
        public void actionPerformed(ActionEvent evt) {
        	removePoint((JEdge) jCell, location);
        }
    }

    /**
     * Action set the label of the currently selected j-cell to its default position.
     */
    private class ResetLabelPositionAction extends JCellEditAction {
    	/** Constructs an instance of the action. */
        ResetLabelPositionAction() {
            super(Options.RESET_LABEL_POSITION_ACTION, false);
        }
        
        public void actionPerformed(ActionEvent evt) {
        	for (JCell jCell: jCells) {
        		resetLabelPosition((JEdge) jCell);
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
        	for (JCell jCell : jCells) {
				setLineStyle((JEdge) jCell, lineStyle);
				List<?> points = GraphConstants.getPoints(jCell.getAttributes());
				if (points == null || points.size() == 2) {
					addPoint((JEdge) jCell, location);
				}
			}
        }
        
        /** The line style set by this action instance. */
        protected final int lineStyle;
    }

    /**
     * Menu offering a choice of line style setting actions.
     */
    private class SetLineStyleMenu extends JMenu implements GraphSelectionListener {
    	/** Constructs an instance of the action. */
        SetLineStyleMenu() {
            super(Options.SET_LINE_STYLE_MENU);
            valueChanged(null);
            JGraph.this.addGraphSelectionListener(this);
        }
        
        public void valueChanged(GraphSelectionEvent e) {
            setEnabled(getSelectionCell() instanceof JEdge);
        }
    }
    
    private class MyGraphUI extends org.jgraph.plaf.basic.BasicGraphUI {
    	MyGraphUI() {
    		// empty
    	}
    	
		@Override
		public Dimension2D getPreferredSize(org.jgraph.JGraph graph,
				CellView view) {
			Dimension2D result = null;
			if (view instanceof JVertexView) {
				JVertexView vertexView = (JVertexView) view;
				String text = convertDigits(vertexView.getCell().getText());
				result = sizeMap.get(text);
				if (result == null) {
					if (text.length() == 0) {
						result = JAttr.DEFAULT_NODE_SIZE;
					} else {
						result = super.getPreferredSize(graph, vertexView);
					}
					// // normalize for linewidth of the border
					// float linewidth =
					// GraphConstants.getLineWidth(vertexView.getAllAttributes());
					// result.setSize(result.getWidth()-linewidth,
					// result.getHeight()-linewidth);
					sizeMap.put(text, result);
				}
				// // adjust for linewidth of the border
				// float linewidth =
				// GraphConstants.getLineWidth(vertexView.getAllAttributes());
				// result = new Dimension((int)
				// Math.round(result.getWidth()+linewidth), (int)
				// Math.round(result.getHeight()+linewidth));
			} else {
				result = super.getPreferredSize(graph, view);
			}
			assert result != null;
			return result;
		}

		/**
		 * Converts all digits in a string in the range 2-9 to 0. The idea is
		 * that this will not affect the size of the string, but will unify many
		 * keys in the size map.
		 */
		private String convertDigits(String original) {
			char[] array = original.toCharArray();
			for (int i = 0; i < array.length; i++) {
				char c = array[i];
				if ('2' <= c && c <= '9') {
					array[i] = '0';
				}
			}
			return String.valueOf(array);
		}

		/**
		 * Taken from {@link com.jgraph.example.fastgraph.FastGraphUI}. Updates
		 * the <code>preferredSize</code> instance variable, which is returned
		 * from <code>getPreferredSize()</code>. Ignores edges for
		 * performance
		 */
		@Override
		protected void updateCachedPreferredSize() {
			CellView[] views = graphLayoutCache.getRoots();
			Rectangle2D size = null;
			if (views != null && views.length > 0) {
				for (int i = 0; i < views.length; i++) {
					if (views[i] != null && !(views[i] instanceof JEdgeView)) {
						Rectangle2D r = views[i].getBounds();
						if (r != null) {
							if (size == null)
								size = new Rectangle2D.Double(r.getX(),
										r.getY(), r.getWidth(), r.getHeight());
							else
								Rectangle2D.union(size, r, size);
						}
					}
				}
			}
			if (size == null)
				size = new Rectangle2D.Double();
			Point2D psize = new Point2D.Double(size.getX() + size.getWidth(),
					size.getY() + size.getHeight());
			Dimension d = graph.getMinimumSize();
			Point2D min = (d != null) ? graph.toScreen(new Point(d.width,
					d.height)) : new Point(0, 0);
			Point2D scaled = graph.toScreen(psize);
			preferredSize = new Dimension((int) Math.max(min.getX(),
					scaled.getX()), (int) Math.max(min.getY(), scaled.getY()));
			Insets in = graph.getInsets();
			if (in != null) {
				preferredSize.setSize(preferredSize.getWidth() + in.left
						+ in.right, preferredSize.getHeight() + in.top
						+ in.bottom);
			}
			validCachedPreferredSize = true;
		}

		/**
		 * Returns a listener that can update the graph when the model changes.
		 */
		@Override
		protected GraphModelListener createGraphModelListener() {
			return new MyGraphModelHandler();
		}

		private Map<String, Dimension2D> sizeMap = new HashMap<String, Dimension2D>();

	    private class MyGraphModelHandler extends GraphModelHandler {
	    	MyGraphModelHandler() {
	    		// empty
	    	}

			@Override
			public void graphChanged(GraphModelEvent e) {
				if (! (e.getChange() instanceof RefreshEdit)) {
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
         * After calling the super method, sets all roots to visible.
         * This is necessary because the cache is partial.
         */
        @Override
		public void setModel(GraphModel model) {
            super.setModel(model);
            Object[] cells = DefaultGraphModel.getRoots(getModel());
            CellView[] cellViews = getMapping(cells, true);
            insertViews(cellViews);
            // Update PortView Cache and Notify Observers
            updatePorts();
            cellViewsChanged(getRoots());
		}

		@Override
		public boolean isVisible(Object cell) {
			if (cell instanceof JCell) {
				return ((JCell) cell).isVisible();
			} else if (cell instanceof DefaultPort) {
				return isVisible(((DefaultPort) cell).getParent());
			} else {
				return super.isVisible(cell);
			}
		}

		/**
         * Overrides the method so {@link JModel.RefreshEdit}s are not
         * passed on.
         */
        @Override
        public void graphChanged(GraphModelChange change) {
            if (!(change instanceof JModel.RefreshEdit)) {
                super.graphChanged(change);
            }
        }

		/**
		 * Completely reloads all roots from the model in the order returned by
		 * DefaultGraphModel.getAll. This uses the current visibleSet and mapping to
		 * fetch the cell views for the cells.
		 */
		@Override
		protected void reloadRoots() {
			// Reorder roots
			Object[] orderedCells = DefaultGraphModel.getAll(graphModel);
			List<CellView> newRoots = new ArrayList<CellView>();
			for (int i = 0; i < orderedCells.length; i++) {
				CellView view = getMapping(orderedCells[i], true);
				if (view != null) {
//					view.refresh(this, this, true);
					if (view.getParentView() == null) {
						newRoots.add(view);
					}
				}
			}
			roots = newRoots;
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
                if (!getModel().isGrayedOut((JCell) cells[i])) {
                    visibleCells.add(cells[i]);
                }
            }
            super.addSelectionCells(visibleCells.toArray());
        }

        @Override
        public void setSelectionCells(Object[] cells) {
            List<Object> visibleCells = new LinkedList<Object>();
            for (Object cell: cells) {
                if (!getModel().isGrayedOut((JCell) cell)) {
                    visibleCells.add(cell);
                }
            }
            super.setSelectionCells(visibleCells.toArray());
        }
    }
    
	/** 
	 * Mouse listener that creates the popup menu and switches the view to 
	 * the rule panel on double-clicks.
	 */
	private class MyMouseListener extends MouseAdapter {
    	/** Empty constructor wit the correct visibility. */
		MyMouseListener() {
    		// empty
    	}
    	
        @Override
        public void mousePressed(MouseEvent evt) {
            if (isAddPointEvent(evt)) {
                JCell jCell = (JCell) getSelectionCell();
                if (jCell instanceof JEdge) {
                    addPoint((JEdge) jCell, evt.getPoint());
                }
            } else if (isRemovePointEvent(evt)) {
                JCell jCell = (JCell) getSelectionCell();
                if (jCell instanceof JEdge) {
                    removePoint((JEdge) jCell, evt.getPoint());
                }
            }
            maybeShowPopup(evt);
        }

        @Override
        public void mouseReleased(MouseEvent evt) {
            maybeShowPopup(evt);
        }
    }
    /** Observer that calls {@link JModel#refresh()} whenever it receives an update event. */
    private class RefreshListener implements Observer {
    	/** Empty constructor wit the correct visibility. */
    	RefreshListener() {
    		// empty
    	}
    	
    	/** The method is called when a filtered set is changed. */
		public void update(Observable o, Object arg) {
			Set<String> changedLabelSet = null;
			if (arg instanceof ObservableSet.AddUpdate) {
				changedLabelSet = ((ObservableSet.AddUpdate) arg).getAddedSet();
			} else {
				changedLabelSet = ((ObservableSet.RemoveUpdate) arg).getRemovedSet();
			}
			Set<JCell> changedCellSet = new HashSet<JCell>();
			for (String label : changedLabelSet) {
				Set<JCell> labelledCells = getLabelList().getJCells(label);
				if (labelledCells != null) {
					for (JCell cell : labelledCells) {
						changedCellSet.add(cell);
						if (cell instanceof JEdge) {
							changedCellSet.add(((JEdge) cell).getSourceVertex());
							changedCellSet.add(((JEdge) cell).getTargetVertex());
						}
					}
				}
			}
			getModel().refresh(changedCellSet);
		}
    }
}