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
 * $Id: JGraph.java,v 1.5 2007-04-12 16:14:49 rensink Exp $
 */
package groove.gui.jgraph;

import groove.gui.LabelList;
import groove.gui.Options;
import groove.gui.SetLayoutMenu;
import groove.gui.ShowHideMenu;
import groove.gui.ZoomMenu;
import groove.gui.layout.JCellLayout;
import groove.gui.layout.Layouter;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;

import org.jgraph.event.GraphModelEvent;
import org.jgraph.event.GraphModelListener;
import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;
import org.jgraph.event.GraphModelEvent.GraphModelChange;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.BasicMarqueeHandler;
import org.jgraph.graph.CellView;
import org.jgraph.graph.DefaultGraphSelectionModel;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;
import org.jgraph.graph.GraphSelectionModel;
import org.jgraph.graph.PortView;
import org.jgraph.plaf.basic.BasicGraphUI;

/**
 * Enhanced j-graph, dedicated to j-models.
 * @author Arend Rensink
 * @version $Revision: 1.5 $ $Date: 2007-04-12 16:14:49 $
 */
public class JGraph extends org.jgraph.JGraph implements GraphModelListener {
    /**
     * Abstract class for j-cell edit actions.
     */
    protected abstract class JCellEditAction extends AbstractAction implements GraphSelectionListener {
        /**
         * Constructs an edit action that is enabled for all j-cells.
         * @param name the name of the action
         */
        protected JCellEditAction(String name) {
            super(name);
            this.allCells = true;
            this.vertexOnly = true;
            valueChanged(null);
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
            valueChanged(null);
            JGraph.this.addGraphSelectionListener(this);
        }
        
        /**
         * Sets the j-cell to the first selected cell.
         * Disables the action if the type of the cell disagrees with the expected type.
         */
        public void valueChanged(GraphSelectionEvent e) {
            jCell = (JCell) JGraph.this.getSelectionCell();
            if (jCell != null) {
                setEnabled(allCells || vertexOnly == (jCell instanceof JVertex));
            } else {
                setEnabled(false);
            }
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
        /** The currently selected j-cell. */
        protected JCell jCell;
        /** The currently set point location. */
        protected Point2D location;
    }
    
    /**
     * Action to add a point to the currently selected j-edge.
     */
    protected class AddPointAction extends JCellEditAction {
    	/** Constructs an instance of the action. */
        public AddPointAction() {
            super(Options.ADD_POINT_ACTION, false);
        }
        
        public void actionPerformed(ActionEvent evt) {
        	addPoint((JEdge) jCell, location);
        }
    }

    /**
     * Action to remove a point from the currently selected j-edge.
     */
    protected class RemovePointAction extends JCellEditAction {
    	/** Constructs an instance of the action. */
        public RemovePointAction() {
            super(Options.REMOVE_POINT_ACTION, false);
        }
        
        public void actionPerformed(ActionEvent evt) {
        	removePoint((JEdge) jCell, location);
        }
    }

    /**
     * Action to edit the label of the currently selected j-cell.
     */
    protected class EditLabelAction extends JCellEditAction {
    	/** Constructs an instance of the action. */
        public EditLabelAction() {
            super(Options.EDIT_LABEL_ACTION);
            putValue(ACCELERATOR_KEY, Options.EDIT_LABEL_KEY);
        }
        
        public void actionPerformed(ActionEvent evt) {
            startEditingAtCell(jCell);
        }
    }

    /**
     * Action set the label of the currently selected j-cell to its default position.
     */
    protected class ResetLabelPositionAction extends JCellEditAction {
    	/** Constructs an instance of the action. */
        public ResetLabelPositionAction() {
            super(Options.RESET_LABEL_POSITION_ACTION, false);
        }
        
        public void actionPerformed(ActionEvent evt) {
            resetLabelPosition((JEdge) jCell);
        }
    }
    
    /**
     * Action to set the line style of the currently selected j-edge.
     */
    protected class SetLineStyleAction extends JCellEditAction {
    	/** Constructs an instance of the action, for a given line style. */
        public SetLineStyleAction(int lineStyle) {
            super(Options.getLineStyleName(lineStyle));
            putValue(ACCELERATOR_KEY, Options.getLineStyleKey(lineStyle));
            this.lineStyle = lineStyle;
        }
        
        public void actionPerformed(ActionEvent evt) {
            setLineStyle((JEdge) jCell, lineStyle);
        }
        
        /** The line style set by this action instance. */
        protected final int lineStyle;
    }
    
    /**
     * Menu offering a choice of line style setting actions.
     */
    protected class SetLineStyleMenu extends JMenu implements GraphSelectionListener {
    	/** Constructs an instance of the action. */
        public SetLineStyleMenu() {
            super(Options.SET_LINE_STYLE_MENU);
            valueChanged(null);
            JGraph.this.addGraphSelectionListener(this);
        }
        
        public void valueChanged(GraphSelectionEvent e) {
            setEnabled(getSelectionCell() instanceof JEdge);
        }
    }
    
    /**
     * A menu item, initialized to an action, that hides itself whenever it is disabled.
     */
    protected class DisappearingJMenuItem extends JMenuItem {
    	/**
    	 * Constructs a menu item for a given action.
    	 * @param action the Action for which to create a menu item
    	 */
        public DisappearingJMenuItem(Action action) {
            super(action);
        }
        
        @Override
        public void setEnabled(boolean enabled) {
            super.setEnabled(enabled);
            setVisible(enabled);
        }
    }
    
    /**
     * A layout cache that, for efficiency, does not pass on all change events,
     * and sets a {@link JCellViewFactory}.
     * It should be possible to use the partiality of the cache to 
     * hide elements, but this seems unnecessarily complicated.
     */
    private class MyGraphLayoutCache extends GraphLayoutCache {   
    	/** Constructs an instance of the cache. */
        public MyGraphLayoutCache() {
            super(JGraph.this.getModel(), new JCellViewFactory(JGraph.this));
        }
//        
//        /** 
//         * After calling the super method, sets all roots to visible.
//         * This is necessary because the cache is partial.
//         */
//        @Override
//		public void setModel(GraphModel model) {
//        	initializing = true;
//			super.setModel(model);
//			initializing = false;
//		}

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
//
//        /** Also returns <code>true</code> if a new model is being loaded. */
//		@Override
//		public boolean isPartial() {
//			return !initializing && super.isPartial();
//		}
//        
//		/** 
//		 * Flag indicating that a new model is being loaded,
//		 * so that for the moment the cache should not be partial.
//		 */
//        private boolean initializing;
    }
    
    /**
     * Marquee handler that activates and shows the popup menu and adds and 
     * removes edge points.
     * @see JGraph#isPopupMenuEvent(MouseEvent)
     * @see JGraph#activatePopupMenu(Point)
     * @see JGraph#addPoint(JEdge, Point2D)
     * @see JGraph#removePoint(JEdge, Point2D)
     */
    static protected class MyMarqueeHandler extends BasicMarqueeHandler {
    	/**
    	 * Constructs a marquee handler for a given j-graph.
    	 * @param jGraph the JGraph for which to create a marquee handler
    	 */
        MyMarqueeHandler(JGraph jGraph) {
            this.jGraph = jGraph;
        }
        
        @Override
        public boolean isForceMarqueeEvent(MouseEvent evt) {
            return jGraph.isPopupMenuEvent(evt) || super.isForceMarqueeEvent(evt);
        }

        /**
         * If the mouse event is a popup menu event, create the popup. 
         * If it is an add or remove event and the graph selection is appropriate,
         * add or remove j-edge points.
         * Pass on the event to <tt>super</tt> if it is not for us.
         * @param evt the event that happened
         */
        @Override
        public void mousePressed(MouseEvent evt) {
            if (!evt.isConsumed() && jGraph.isPopupMenuEvent(evt)) {
                Point atPoint = evt.getPoint();
                jGraph.getPopupMenu(atPoint).show(jGraph, atPoint.x, atPoint.y);
                evt.consume();
            } else if (jGraph.isAddPointEvent(evt)) {
                JCell jCell = (JCell) jGraph.getSelectionCell();
                if (jCell instanceof JEdge) {
                    jGraph.addPoint((JEdge) jCell, evt.getPoint());
                }
            } else if (jGraph.isRemovePointEvent(evt)) {
                JCell jCell = (JCell) jGraph.getSelectionCell();
                if (jCell instanceof JEdge) {
                    jGraph.removePoint((JEdge) jCell, evt.getPoint());
                }
            } else {
                super.mousePressed(evt);
            }
        }
        /** The j-graph upon which this marquee handler works. */
        protected final JGraph jGraph;
    }

    /**
     * Selection model that makes sure hidden cells cannot be selected.
     */
    private class MyGraphSelectionModel extends DefaultGraphSelectionModel {
    	/** Constructs an instance of the selection model. */
        public MyGraphSelectionModel() {
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
            for (int i = 0; i < cells.length; i++) {
                if (!getModel().isGrayedOut((JCell) cells[i])) {
                    visibleCells.add(cells[i]);
                }
            }
            super.setSelectionCells(visibleCells.toArray());
        }
    }

    /**
     * Constructs a JGraph on the basis of a given j-model. 
     * @param model the JModel for which to create a JGraph
     */
    public JGraph(JModel model) {
        super(model);
        setGraphLayoutCache(createGraphLayoutCache());
        setMarqueeHandler(createMarqueeHandler());
        setSelectionModel(createSelectionModel());
        setModel(model);
//        // for efficiency, set a graph layout cache that does not react
//        // to all change events
//        setGraphLayoutCache(createGraphLayoutCache());
        // Make Ports invisible by Default
        setPortsVisible(false);
        // Save edits to a cell whenever something else happens
        setInvokesStopCellEditing(true);
        // Turn off double buffering for speed
        // setDoubleBuffered(false);
    }
    
    /**
     * Constructs a JGraph with an initially empty model and initially disabled. The initial model
     * is a <tt>JModel</tt> showing node identities.
     */
    public JGraph() {
        this(null);
        setEnabled(false);
    }

    /**
     * Overrides <tt>JGraph</tt>'s method so a <tt>DefaultGraphCell</tt> is not automatically
     * bypassed in favour of its user object. This implementation simply invokes <tt>toString()</tt>
     * upon <tt>value</tt>.
     */
    @Override
    public String convertValueToString(Object value) {
        return value.toString();
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
            for (JCell jCell: refreshedJCells) {
            	AttributeMap transientAttributes = getModel().createTransientJAttr(jCell);
                CellView jView = getGraphLayoutCache().getMapping(jCell, false);
                if (jView != null) {
                    jView.changeAttributes(transientAttributes);
                }
                if (getModel().isGrayedOut(jCell)) {
                    getSelectionModel().removeSelectionCell(jCell);
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
     * <li>the popup menu is re-initialized
     * <li>the layout action is started for the new model
     * </ul>
     * @require <tt>model instanceof JModel</tt>
     */
    @Override
    public void setModel(GraphModel model) {
        JModel jModel = (JModel) model;
        if (initialized) {
            setEnabled(true);
            clearSelection();
            if (layouter != null) {
                layouter.stop();
            }
            getModel().removeGraphModelListener(this);
        }
        super.setModel(model);
        getLabelList().updateModel();
        model.addGraphModelListener(this);
        if (initialized) {
        	// invalidate the popup menu
            popupMenu = null;
            if (layouter != null && !jModel.isLayedOut()) {
                if (jModel.freeze()) {
                	layouter.start(false);
                }
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
     * Completely refreshes the view of the graph.
     */
    public void refreshView() {
    	getGraphLayoutCache().setModel(getModel());
    }

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
	 * by cachking previous values.
	 */
	protected BasicGraphUI createGraphUI() {
		return new org.jgraph.plaf.basic.BasicGraphUI() {
			@Override
			public Dimension2D getPreferredSize(org.jgraph.JGraph graph, CellView view) {
				Dimension2D result = null;
				if (view instanceof JVertexView) {
					JVertexView vertexView = (JVertexView) view;
					String text = convertDigits(vertexView.getHtmlText());
					result = sizeMap.get(text);
					if (result == null) {
						if (text.length() == 0) {
							result = JAttr.DEFAULT_NODE_SIZE;
						} else {
							result = super.getPreferredSize(graph, vertexView);
						}
						// normalize for linewidth of the border
						int linewidth = (int) GraphConstants.getLineWidth(vertexView.getAllAttributes());
						int lineDiff = linewidth - JAttr.DEFAULT_LINE_WIDTH;
						result = new Dimension((int) result.getWidth()-lineDiff, (int) result.getHeight()-lineDiff);
						sizeMap.put(text, result);
					}
					// adjust for linewidth of the border
					int linewidth = (int) GraphConstants.getLineWidth(vertexView.getAllAttributes());
					int lineDiff = linewidth - JAttr.DEFAULT_LINE_WIDTH;
					result = new Dimension((int) result.getWidth()+lineDiff, (int) result.getHeight()+lineDiff);
				} else {
					result = super.getPreferredSize(graph, view);
				}
				return result;
			}
			
			/** 
			 * Converts all digits in a string in the range 2-9 to 0.
			 * The idea is that this will not affect the size of the string,
			 * but will unify many keys in the size map.
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
			
			private Map<String,Dimension2D> sizeMap = new HashMap<String,Dimension2D>();
		};
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
     */
    public void fillOutEditMenu(JPopupMenu menu) {
        addSeparatorUnlessFirst(menu);
        menu.add(new DisappearingJMenuItem(getAddPointAction()));
        menu.add(new DisappearingJMenuItem(getRemovePointAction()));
        menu.add(new DisappearingJMenuItem(getResetLabelPositionAction()));
        menu.add(createLineStyleMenu());
    }

    /**
     * Adds all the display menu items of this jgraph to a given popup menu.
     * @param menu the popup menu to receive the items
     */
    public void fillOutDisplayMenu(JPopupMenu menu) {
        addSeparatorUnlessFirst(menu);
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
        JCellEditAction result = setLineStyleActionMap.get(Options.getLineStyleKey(lineStyle));
        if (result == null) {
            setLineStyleActionMap.put(Options.getLineStyleName(lineStyle), result = new SetLineStyleAction(lineStyle));
            addAccelerator(result);
        }
        return result;
    }

    /**
     * Factory method for the graph selection model.
     * This implementation returns a {@link MyGraphSelectionModel}.
     * @return the new graph selection model
     */
    protected GraphSelectionModel createSelectionModel() {
        return new MyGraphSelectionModel();
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
     * This implementation returns a {@link MyMarqueeHandler}.
     */
    protected BasicMarqueeHandler createMarqueeHandler() {
        return new MyMarqueeHandler(this);
    }

    /**
     * Callback method to determine whether a given event is a menu popup event. This implementation
     * checks for the right hand mouse button. To be overridden by subclasses.
     * @param evt the event that could be a popup menu event
     * @return <tt>true</tt> if <tt>e</tt> is a popup menu event
     */
    protected boolean isPopupMenuEvent(MouseEvent evt) {
        return SwingUtilities.isRightMouseButton(evt) && !evt.isControlDown();
    }

    /**
     * Callback method to determine whether a given event is a menu popup event. This implementation
     * checks for the right hand mouse button. To be overridden by subclasses.
     * @param evt the event that could be a popup menu event
     * @return <tt>true</tt> if <tt>e</tt> is a popup menu event
     */
    protected boolean isAddPointEvent(MouseEvent evt) {
        return evt.isAltDown() && ! isRemovePointEvent(evt);
    }

    /**
     * Callback method to determine whether a given event is a menu popup event. This implementation
     * checks for the right hand mouse button. To be overridden by subclasses.
     * @param evt the event that could be a popup menu event
     * @return <tt>true</tt> if <tt>e</tt> is a popup menu event
     */
    protected boolean isRemovePointEvent(MouseEvent evt) {
        if (evt.isAltDown()) {
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
		if (popupMenu == null) {
			popupMenu = new JPopupMenu();
			fillPopupMenu(popupMenu);
		}
		activatePopupMenu(atPoint);
		return this.popupMenu;
	}

	/**
	 * Fills out the popup menu for this jgraph. Does not clear the menu first. This method is
	 * invoked at least once whenever a new jmodel is set. This implementation successively invokes
	 * {@link #fillOutLayoutMenu(JPopupMenu)}and {@link #fillOutDisplayMenu(JPopupMenu)}.
	 * @param result the menu to be initialised
	 * @see #activatePopupMenu(Point)
	 */
	protected void fillPopupMenu(JPopupMenu result) {
	    fillOutEditMenu(result);
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

    /**
     * The popup menu for the jgraph.
     */
    protected JPopupMenu popupMenu;

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
}