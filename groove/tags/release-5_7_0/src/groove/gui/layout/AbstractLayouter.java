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
package groove.gui.layout;

import groove.gui.jgraph.JCell;
import groove.gui.jgraph.JEdge;
import groove.gui.jgraph.JGraph;
import groove.gui.jgraph.JModel;
import groove.gui.jgraph.JVertex;
import groove.gui.jgraph.JVertexView;
import groove.gui.look.VisualMap;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.CellView;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.VertexView;

/**
 * An abstract class for layout actions.
 * @author Arend Rensink
 * @version $Revision$
 */
abstract public class AbstractLayouter implements Layouter {
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
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * The name of this layout action
     */
    private final String name;

    /**
     * Prepares the actual layout process by calculating the information from
     * the current <tt>jmodel</tt>. This implementation calculates the
     * <tt>toLayoutableMap</tt>, and sets the line style to that preferred by
     * the layouter.
     * @param recordImmovables if {@code true}, the shift in position of the immovables
     * is recorded
     */
    protected void prepare(boolean recordImmovables) {
        this.jGraph.setLayouting(true);
        this.jGraph.setToolTipEnabled(false);
        // edge points are cleared when layout is stored back into view
        //        this.jGraph.clearAllEdgePoints();
        // copy the old layout map
        Map<JVertex<?>,LayoutNode> oldLayoutMap =
            new LinkedHashMap<>(this.layoutMap);
        // clear the transient information
        this.layoutMap.clear();
        this.immovableMap.clear();
        // iterate over the cell views
        CellView[] cellViews = this.jGraph.getGraphLayoutCache().getRoots();
        for (CellView cellView : cellViews) {
            if (!(cellView instanceof JVertexView)) {
                continue;
            }
            JVertexView vertexView = (JVertexView) cellView;
            JVertex<?> jVertex = vertexView.getCell();
            if (jVertex.isGrayedOut() || !jVertex.getVisuals().isVisible()) {
                continue;
            }
            LayoutNode layout = new LayoutNode(vertexView);
            if (!jVertex.isLayoutable()) {
                Point2D shift;
                if (recordImmovables) {
                    shift = new Point2D.Double();
                    LayoutNode oldLayout = oldLayoutMap.get(jVertex);
                    if (oldLayout != null) {
                        double x = layout.getX() - oldLayout.getX();
                        double y = layout.getY() - oldLayout.getY();
                        shift = new Point2D.Double(x, y);
                    }
                } else {
                    shift = null;
                }
                this.immovableMap.put(jVertex, shift);
            }
            this.layoutMap.put(jVertex, layout);
        }
    }

    /**
     * Finalises the layouting, by performing an edit on the model that records
     * the node bounds and edge points.
     */
    protected void finish() {
        final Map<JCell<?>,AttributeMap> change = new HashMap<>();
        for (LayoutNode layout : this.layoutMap.values()) {
            VisualMap visuals = new VisualMap();
            JVertex<?> jVertex = layout.getVertex();
            // store the bounds back into the model
            double x = layout.getCenterX();
            double y = layout.getCenterY();
            Point2D shift = this.immovableMap.get(jVertex);
            if (shift != null) {
                x += shift.getX();
                y += shift.getY();
            }
            visuals.setNodePos(new Point2D.Double(x, y));
            jVertex.setLayoutable(false);
            change.put(jVertex, visuals.getAttributes());
        }
        // clear edge points
        // not calling JGraph.clearAllEdgePoints to avoid generating a separate edit
        for (Object root : getJGraph().getRoots()) {
            if (!(root instanceof JEdge)) {
                continue;
            }
            JEdge<?> jEdge = (JEdge<?>) root;
            // only clear edge points for edges with relayouted source or target
            if (this.immovableMap.containsKey(jEdge.getSourceVertex())
                && this.immovableMap.containsKey(jEdge.getTargetVertex())) {
                continue;
            }
            VisualMap visuals = jEdge.getVisuals();
            List<Point2D> points = visuals.getPoints();
            // don't make the change directly in the cell,
            // as this messes up the undo history
            List<Point2D> newPoints = Arrays.asList(points.get(0), points.get(points.size() - 1));
            AttributeMap newAttributes = new AttributeMap();
            GraphConstants.setPoints(newAttributes, newPoints);
            change.put(jEdge, newAttributes);
        }
        // do the following in the event dispatch thread\
        final JModel<?> jModel = getJModel();
        Runnable edit = new Runnable() {
            @Override
            public void run() {
                if (change.size() != 0) {
                    jModel.edit(change, null, null, null);
                    // taking out the refresh as probably superfluous and
                    // certainly performance impacting
                    //                    AbstractLayouter.this.jgraph.refresh();
                }
                getJGraph().setLayouting(false);
            }
        };
        // do this now (if invoked from the event thread) or defer to event thread
        if (SwingUtilities.isEventDispatchThread()) {
            edit.run();
        } else {
            try {
                SwingUtilities.invokeAndWait(edit);
            } catch (InterruptedException exc) {
                // do nothing
            } catch (InvocationTargetException exc) {
                // do nothing
            }
        }
    }

    /** Returns the (fixed) jGraph for this layouter. */
    protected JGraph<?> getJGraph() {
        return this.jGraph;
    }

    /** Returns the jModel currently being layed out. */
    protected JModel<?> getJModel() {
        return getJGraph().getModel();
    }

    /**
     * The underlying jGraph for this layout action.
     */
    private final JGraph<?> jGraph;

    /**
     * Map from graph nodes to layoutables.
     */
    protected final Map<JVertex<?>,LayoutNode> layoutMap =
        new LinkedHashMap<>();

    /**
     * Map from vertices whose position should not be changed
     * to a point representing the shift between the position determined for them at the
     * last layout action, and their position at the start of the current layout action.
     */
    protected final Map<JVertex<?>,Point2D> immovableMap = new HashMap<>();

    @Override
    public Layouter getIncremental() {
        return this;
    }

    /**
     * Implements a layoutable that wraps a rectangle.
     */
    static final protected class LayoutNode {
        /** Constructs a new layoutable from a given vertex. */
        public LayoutNode(VertexView view) {
            this.r = (Rectangle2D) view.getBounds().clone();
            this.view = view;
        }

        /** Returns the bounds of this layout node. */
        public Rectangle2D getBounds() {
            return this.r;
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

        /** Returns the x-coordinate of the centre of this layoutable. */
        public double getCenterX() {
            return this.r.getCenterX();
        }

        /** Returns the y-coordinate of the centre of this layoutable. */
        public double getCenterY() {
            return this.r.getCenterY();
        }

        /** Sets a new position of this layoutable. */
        public void setLocation(double x, double y) {
            this.r.setRect(x, y, getWidth(), getHeight());

        }

        /** Returns the view for which this is the layout node. */
        public VertexView getView() {
            return this.view;
        }

        /** Returns the vertex for which this is the layout node. */
        public JVertex<?> getVertex() {
            return (JVertex<?>) this.view.getCell();
        }

        @Override
        public String toString() {
            return "Layout[x=" + getX() + ",y=" + getY() + ",width=" + getWidth() + ",height="
                + getHeight() + "]";
        }

        /** The internally stored bounds of this layoutable. */
        private final Rectangle2D r;
        /** Vertex for which this is the layout node. */
        private final VertexView view;
    }
}
