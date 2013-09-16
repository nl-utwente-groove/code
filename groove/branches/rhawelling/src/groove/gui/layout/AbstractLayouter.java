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
 * $Id: AbstractLayouter.java,v 1.6 2008-01-30 09:33:01 iovka Exp $
 */
package groove.gui.layout;

import groove.gui.jgraph.JCell;
import groove.gui.jgraph.JGraph;
import groove.gui.jgraph.JModel;
import groove.gui.jgraph.JVertex;
import groove.gui.jgraph.JVertexView;
import groove.gui.look.VisualMap;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.SwingUtilities;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.CellView;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.VertexView;

/**
 * An abstract class for layout actions.
 * @author Arend Rensink
 * @version $Revision$
 */
abstract public class AbstractLayouter implements Layouter {
    /**
     * Implements a layoutable that wraps a rectangle.
     */
    static final protected class LayoutNode {
        /** Constructs a new layoutable from a given vertex. */
        public LayoutNode(VertexView view) {
            this.r = view.getBounds();
        }

        /** Returns the x-coordinate of this layoutable. */
        public double getX() {
            return this.r.getX();
        }

        /** Returns the y-coordinate of this layoutable. */
        public double getY() {
            return this.r.getY();
        }

        /** Returns the width of this layoutable. */
        public double getWidth() {
            return this.r.getWidth();
        }

        /** Returns the height of this layoutable. */
        public double getHeight() {
            return this.r.getHeight();
        }

        /** Sets a new position of this layoutable. */
        public void setLocation(double x, double y) {
            this.r.setRect(x, y, getWidth(), getHeight());

        }

        @Override
        public String toString() {
            return "VertexLayoutable[x=" + getX() + ",y=" + getY() + ",width="
                + getWidth() + ",height=" + getHeight() + "]";
        }

        /** The internally stored bounds of this layoutable. */
        private final Rectangle2D r;
    }

    /**
     * Constructor to create a dummy, prototype layout action. Proper layout
     * actions are created using <tt>newInstance(MyJGraph)</tt>
     * @see #newInstance(JGraph)
     */
    protected AbstractLayouter(String name) {
        this(name, null);
    }

    /**
     * Constructor to create a dummy, prototype layout action. Proper layout
     * actions are created using <tt>newInstance(MyJGraph)</tt>
     * @see #newInstance(JGraph)
     */
    protected AbstractLayouter(String name, JGraph<?> jgraph) {
        this.name = name;
        this.jGraph = jgraph;
    }

    /**
     * Returns the name stored for this action.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Prepares the actual layout process by calculating the information from
     * the current <tt>jmodel</tt>. This implementation calculates the
     * <tt>toLayoutableMap</tt>, and sets the line style to that preferred by
     * the layouter.
     */
    protected void prepare() {
        this.jGraph.notifyProgress("Layouting");
        this.jGraph.setLayouting(true);
        this.jGraph.clearAllEdgePoints();
        this.jmodel = this.jGraph.getModel();
        // clear the transient information
        this.layoutMap.clear();
        this.immovableSet.clear();
        // iterate over the cell views
        CellView[] cellViews = this.jGraph.getGraphLayoutCache().getRoots();
        for (CellView cellView : cellViews) {
            if (cellView instanceof JVertexView) {
                JVertex<?> jVertex = ((JVertexView) cellView).getCell();
                if (!jVertex.isGrayedOut()) {
                    LayoutNode layoutable =
                        new LayoutNode((VertexView) cellView);
                    this.layoutMap.put(jVertex, layoutable);
                    if (!jVertex.isLayoutable()) {
                        this.immovableSet.add(layoutable);
                    }
                }
            }
        }
        this.jGraph.setToolTipEnabled(false);
    }

    /**
     * Finalizes the layouting, by performing an edit on the model that records
     * the node bounds and edge points.
     */
    protected void finish() {
        final Map<JCell<?>,AttributeMap> change =
            new HashMap<JCell<?>,AttributeMap>();
        CellView[] cellViews = this.jGraph.getGraphLayoutCache().getRoots();
        for (CellView view : cellViews) {
            if (view instanceof VertexView || view instanceof EdgeView) {
                JCell<?> cell = (JCell<?>) view.getCell();
                VisualMap visuals = new VisualMap();
                if (view instanceof VertexView) {
                    // store the bounds back into the model
                    Rectangle2D bounds = ((VertexView) view).getCachedBounds();
                    visuals.setNodePos(new Point2D.Double(bounds.getCenterX(),
                        bounds.getCenterY()));
                }
                change.put(cell, visuals.getAttributes());
                cell.setLayoutable(false);
            }
        }
        // do the following in the event dispatch thread
        Runnable edit = new Runnable() {
            public void run() {
                if (change.size() != 0) {
                    AbstractLayouter.this.jmodel.edit(change, null, null, null);
                    // taking out the refresh as probably superfluous and 
                    // certainly performance impacting
                    //                    AbstractLayouter.this.jgraph.refresh();
                }
                AbstractLayouter.this.jGraph.notifyProgress("");
                AbstractLayouter.this.jGraph.setLayouting(false);
            }
        };
        // do this now (if invoked from the event thread) or defer to event thread
        if (SwingUtilities.isEventDispatchThread()) {
            edit.run();
        } else {
            SwingUtilities.invokeLater(edit);
        }
    }

    /**
     * The name of this layout action
     */
    private final String name;

    /**
     * The underlying jgraph for this layout action.
     */
    protected final JGraph<?> jGraph;

    /**
     * The model that has last been layed out.
     */
    protected JModel<?> jmodel;

    /**
     * A map from the vertex cells and pairs of edge cells/point indices to layoutables.
     * This is a transient
     * value, computed by <tt>prepare()</tt>. The layoutable for a cell
     * contains that cell's bounds; the layoutable for an edge point contains
     * the point.
     */
    protected final Map<JVertex<?>,LayoutNode> layoutMap =
        new LinkedHashMap<JVertex<?>,LayoutNode>();

    /**
     * The subset of layoutables that should be immovable, according to the
     * movability attribute of the corresponding jcell. Initialised in
     * <tt>prepare()</tt>
     */
    protected final Set<LayoutNode> immovableSet = new HashSet<LayoutNode>();
}
