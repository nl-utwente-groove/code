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
 * $Id: AbstractLayouter.java,v 1.6 2008-01-30 09:33:01 iovka Exp $
 */
package groove.gui.layout;

import groove.gui.jgraph.JCell;
import groove.gui.jgraph.JEdgeView;
import groove.gui.jgraph.JGraph;
import groove.gui.jgraph.JModel;
import groove.util.Reporter;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.SwingUtilities;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.CellView;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.VertexView;

/**
 * An abstract class for layout actions.
 * @author Arend Rensink
 * @version $Revision$
 */
abstract public class AbstractLayouter implements Layouter {
    /**
     * Interface of an item that is to be layed out. This is to unify node bounds and edge points.
     */
    static protected interface Layoutable {
        /** Returns the x-coordinate of this layoutable. */
        public double getX();

        /** Returns the y-coordinate of this layoutable. */
        public double getY();

        /** Returns the height of this layoutable. */
        public double getHeight();

        /** Returns the width of this layoutable. */
        public double getWidth();

        /** Sets a new position of this layoutable. */
        public void setLocation(double x, double y);
    }

    /**
     * Implements a layoutable that wraps a point. Width and height are zero.
     */
    static final protected class PointLayoutable implements Layoutable {
    	/** Constructs a new layoutable from a given point. */
        public PointLayoutable(Point2D p) {
            this.p = p;
        }

        public double getX() {
            return p.getX();
        }

        public double getY() {
            return p.getY();
        }

        public double getWidth() {
            return 0;
        }

        public double getHeight() {
            return 0;
        }

        public void setLocation(double x, double y) {
            p.setLocation(x, y);

        }

        @Override
        public String toString() {
            return "PointLayoutable[x=" + getX() + ",y=" + getY() + "]";
        }

        /** The internally stored point of this layoutable. */
        private Point2D p;
    }

    /**
     * Implements a layoutable that wraps a rectangle. Width and height are zero.
     */
    static final protected class VertexLayoutable implements Layoutable {
    	/** Constructs a new layoutable from a given vertex. */
        public VertexLayoutable(VertexView view) {
            this.r = view.getBounds();
        }

        public double getX() {
            return r.getX();
        }

        public double getY() {
            return r.getY();
        }

        public double getWidth() {
            return r.getWidth();
        }

        public double getHeight() {
            return r.getHeight();
        }

        public void setLocation(double x, double y) {
            r.setRect(x, y, r.getWidth(), r.getHeight());

        }

        @Override
        public String toString() {
            return "VertexLayoutable[x=" + getX() + ",y=" + getY() + ",width=" + getWidth()
                    + ",height=" + getHeight() + "]";
        }

        /** The internally stored bounds of this layoutable. */
        private final Rectangle2D r;
    }

    /**
     * Constructor to create a dummy, prototype layout action. Proper layout actions are created
     * using <tt>newInstance(MyJGraph)</tt>
     * @see #newInstance(JGraph)
     */
    protected AbstractLayouter(String name) {
        this(name, null);
    }

    /**
     * Constructor to create a dummy, prototype layout action. Proper layout actions are created
     * using <tt>newInstance(MyJGraph)</tt>
     * @see #newInstance(JGraph)
     */
    protected AbstractLayouter(String name, JGraph jgraph) {
        setName(name);
        this.jgraph = jgraph;
    }

    /**
     * Returns the name stored for this action.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the text to be displayed for this action.
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the name stored for this action. Used when the action is a factory. Note that this does
     * not affect the <i>display </i> name for this action.
     */
    protected void setName(String name) {
        this.name = name;
        this.text = name;
    }

    /**
     * Sets the text to be displayed for this action. Queried by an <tt>Action</tt> in which this
     * layout is embedded.
     */
    protected void setText(String text) {
        this.text = text;
    }

    /**
     * Restarts the entire layouting process on the basis of <tt>jview</tt>'s current model. This
     * implementation resets the movable attribute of all cells to true, so the layouting can touch
     * them.
     */
    protected void reset() {
        Object[] cells = jgraph.getRoots();
        for (int i = 0; i < cells.length; i++) {
            GraphConstants.setMoveable(((DefaultGraphCell) cells[i]).getAttributes(), true);
        }
    }

    /**
     * Prepares the actual layout precess by calculating the information from the curent
     * <tt>jmodel</tt>. This implementation calculates the <tt>toLayoutableMap</tt>, and sets
     * the line style to that preferred by the layouter.
     */
    protected void prepare() {
        reporter.start(PREPARE);
        jmodel = jgraph.getModel();
        // clear the transient information
        toLayoutableMap.clear();
        immovableSet.clear();
        // iterate over the cell views
        CellView[] cellViews = jgraph.getGraphLayoutCache().getRoots();
        for (CellView cellView : cellViews) {
            if (cellView.getCell() instanceof JCell && !jmodel.isGrayedOut((JCell) cellView.getCell())) {
                JCell jCell = (JCell) cellView.getCell();
				boolean immovable = !GraphConstants.isMoveable(jCell.getAttributes());
				if (cellView instanceof JEdgeView) {
					// all true points (i.e., that are not PortViews) are
					// subject to layouting
					List<Object> points = ((JEdgeView) cellView).getViewPoints();
					// failed attempt to store edges beck so they will be layed
					// out live
					// GraphConstants.setPoints(cell.getAttributes(),points);
					for (int p = 1; p < points.size(); p++) {
						Object point = points.get(p);
						if (point instanceof Point2D) {
							Layoutable layoutable = new PointLayoutable(
									(Point2D) point);
							toLayoutableMap.put(point, layoutable);
							if (immovable) {
								immovableSet.add(layoutable);
							}
						}
					}
				} else {
					assert cellView instanceof VertexView : String.format("%s instance of %s", cellView, cellView.getClass());
					// insert the bounds of the cell as layoutable
					// failed attempt to store edges beck so they will be layed
					// out live
					// GraphConstants.setBounds(cell.getAttributes(),
					// cellBounds);
					Layoutable layoutable = new VertexLayoutable(
							(VertexView) cellView);
					toLayoutableMap.put(jCell, layoutable);
					if (immovable) {
						immovableSet.add(layoutable);
					}
				}
			}
        }
        jgraph.setToolTipEnabled(false);
        reporter.stop();
    }

    /**
	 * Finalizes the layouting, by performing an edit on the model that records
	 * the node bounds and edge points.
	 */
    protected void finish() {
        reporter.start(FINISH);
        final Map<JCell,AttributeMap> change = new HashMap<JCell,AttributeMap>();
        CellView[] cellViews = jgraph.getGraphLayoutCache().getRoots();
        for (CellView view: cellViews) {
            if (view instanceof VertexView || view instanceof EdgeView) {
                JCell cell = (JCell) view.getCell();
                GraphConstants.setMoveable(cell.getAttributes(), jmodel.isMoveable(cell));
                AttributeMap modelAttr = new AttributeMap();
                if (view instanceof VertexView) {
                    // store the bounds back into the model
                    GraphConstants.setBounds(modelAttr, ((VertexView) view).getCachedBounds());
                } else {
                    // store the points back into the model
                    GraphConstants.setPoints(modelAttr, ((EdgeView) view).getPoints());
                }
                change.put(cell, modelAttr);
            }
        }
        // do the following in the event dispatch thread
        SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (change.size() != 0) {
					jmodel.edit(change, null, null, null);
				}
//				jgraph.setToolTipEnabled(jgraphWasToolTipEnabled);
//				jgraph.repaint();
				jmodel.setLayedOut(true);
			}
		});
		reporter.stop();
    }

    /**
	 * The name of this layout action
	 */
    protected String name;

    /**
     * The text to be currently displayed by this layout action.
     */
    protected String text;

    /**
     * The underlying jgraph for this layout action.
     */
    protected final JGraph jgraph;

    /**
     * The model that has last been layed out.
     */
    protected JModel jmodel;

    /**
     * A map from the cells and edge points to layoutables. This is a transient value, computed by
     * <tt>prepare()</tt>. The layoutable for a cell contains that cell's bounds; the layoutable
     * for an edge point contains the point.
     */
    protected final Map<Object,Layoutable> toLayoutableMap = new HashMap<Object,Layoutable>();

    /**
     * The subest of layoutables that should be immovable, according to the movability attribute of
     * the corresponding jcell. Initisialized in <tt>prepare()</tt>
     */
    protected final Set<Layoutable> immovableSet = new HashSet<Layoutable>();
//
//    /**
//     * Indicates whether the jgraph was tool tip enabled before layouting started; then it has to be
//     * re-registered at finishing time.
//     */
//    private boolean jgraphWasToolTipEnabled;
    /** Profiling reporter. */
    static protected final Reporter reporter = Reporter.register(NoLayouter.class);
    /** Handle for profiling the start phase of layouting. */
    static protected final int START = reporter.newMethod("start()");
    /** Handle for profiling the preparation phase of layouting. */
    static protected final int PREPARE = reporter.newMethod("prepare()");
    /** Handle for profiling the finishing phase of layouting. */
    static protected final int FINISH = reporter.newMethod("finish()");
}
