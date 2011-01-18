/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2010 University of Twente
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
 * $Id$
 */
package groove.gui.jgraph;

import static groove.gui.jgraph.JAttr.EXTRA_BORDER_SPACE;
import static groove.gui.jgraph.JGraphMode.EDGE_MODE;
import static groove.gui.jgraph.JGraphMode.NODE_MODE;
import static groove.gui.jgraph.JGraphMode.PAN_MODE;
import groove.gui.RubberBand;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import javax.swing.JViewport;

import org.jgraph.graph.CellView;
import org.jgraph.plaf.basic.BasicGraphUI;

/** Adapted UI for JGraphs. */
public class JGraphUI extends BasicGraphUI {
    private GraphJGraph getJGraph() {
        return (GraphJGraph) this.graph;
    }

    private JGraphMode getJGraphMode() {
        return getJGraph().getMode();
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
            (d != null) ? getJGraph().toScreen(new Point(d.width, d.height))
                    : new Point(0, 0);
        Point2D scaled = getJGraph().toScreen(psize);
        this.preferredSize =
            new Dimension((int) Math.max(min.getX(), scaled.getX()),
                (int) Math.max(min.getY(), scaled.getY()));
        Insets in = this.graph.getInsets();
        if (in != null) {
            this.preferredSize.setSize(this.preferredSize.getWidth() + in.left
                + in.right, this.preferredSize.getHeight() + in.top + in.bottom);
        }
        this.validCachedPreferredSize = true;
    }

    @Override
    protected Point2D getEditorLocation(Object cell, Dimension2D editorSize,
            Point2D pt) {
        double scale = getJGraph().getScale();
        // shift the location by the extra border space
        return super.getEditorLocation(cell, editorSize,
            new Point2D.Double(
                pt.getX() + scale * (EXTRA_BORDER_SPACE + 4) - 4, pt.getY()
                    + scale * (EXTRA_BORDER_SPACE + 3) - 3));
    }

    /** 
     * Makes sure that cancelled edits are nevertheless passed on to 
     * the JGraph.
     */
    @Override
    protected void completeEditing(boolean messageStop, boolean messageCancel,
            boolean messageGraph) {
        super.completeEditing(messageStop, messageCancel, true);
    }

    @Override
    protected void installListeners() {
        super.installListeners();
        this.graph.addMouseWheelListener((MouseWheelListener) this.mouseListener);
    }

    @Override
    protected void paintOverlay(Graphics g) {
        ((MouseHandler) this.mouseListener).paintOverlay(g);
    }

    @Override
    protected MouseListener createMouseListener() {
        return new MouseHandler();
    }

    /**
     * This is a complete reimplementation.
     */
    public class MouseHandler extends MouseAdapter implements
            MouseMotionListener, MouseWheelListener, Serializable {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (getJGraphMode() != EDGE_MODE && getJGraphMode() != PAN_MODE) {
                GraphJCell jCell = getJCellAt(e.getPoint());
                if (jCell != null) {
                    selectCellForEvent(jCell, e);
                } else if (!isToggleSelectionEvent(e)
                    && !isAddToSelectionEvent(e)) {
                    getJGraph().clearSelection();
                    if (getJGraphMode() == NODE_MODE) {
                        ((AspectJGraph) getJGraph()).addVertex(e.getPoint());
                    }
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            this.dragOrigX = e.getX();
            this.dragOrigY = e.getY();
            this.dragOrigVertex = vertexAt(e.getPoint());
            if (getJGraphMode() == PAN_MODE) {
                this.dragMode = DragMode.PAN;
            } else if (getJGraph().isCellSelected(getJCellAt(e.getPoint()))) {
                this.dragMode = DragMode.DRAG;
                JGraphUI.this.handle.mousePressed(e);
            } else if (getJGraphMode() == EDGE_MODE && this.focusVertex != null) {
                this.dragMode = DragMode.EDGE;
                JGraphUI.this.marquee.mousePressed(e);
            } else {
                this.dragMode = DragMode.RUBBER;
                this.band.mousePressed(e);
            }
            if (this.dragMode != DragMode.DRAG) {
                getJGraph().setCursor(getJGraph().getMode().getDragCursor());
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            autoscroll(getJGraph(), e.getPoint());
            switch (this.dragMode) {
            case PAN:
                if (isPanEnabled()) {
                    doPan(e);
                }
                return;
            case DRAG:
                JGraphUI.this.handle.mouseDragged(e);
                break;
            case EDGE:
                JGraphUI.this.marquee.mouseDragged(e);
                break;
            case RUBBER:
                this.band.mouseDragged(e);
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            switch (getJGraphMode()) {
            case EDGE_MODE:
                focusVertexAt(e.getPoint());
            }
            JGraphUI.this.marquee.mouseMoved(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            switch (this.dragMode) {
            case EDGE:
                JGraphUI.this.marquee.mouseReleased(e);
                break;
            case DRAG:
                JGraphUI.this.handle.mouseReleased(e);
                break;
            case RUBBER:
                this.band.mouseReleased(e);
            }
            this.dragOrigX = -1;
            this.dragOrigY = -1;
            this.dragOrigVertex = null;
            getJGraph().setCursor(getJGraph().getMode().getCursor());
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            if (getJGraph().getMode() == PAN_MODE) {
                int change = -e.getWheelRotation();
                getJGraph().changeScale(change);
            }
        }

        void paintOverlay(Graphics g) {
            this.band.draw(g);
        }

        private void focusVertexAt(Point p) {
            JVertexView newFocusVertex = vertexAt(p);
            if (this.focusVertex != newFocusVertex) {
                Graphics g = getJGraph().getGraphics();
                if (this.focusVertex != null) {
                    this.focusVertex.paintArmed(g);
                }
                if (newFocusVertex != null) {
                    newFocusVertex.paintArmed(g);
                }
                this.focusVertex = newFocusVertex;
            }
        }

        /**
         * Returns the current vertex view at a given x- and y-coordinate, or
         * <tt>null</tt> if there is no vertex there.
         */
        private GraphJCell getJCellAt(Point2D p) {
            return getJGraph().getFirstCellForLocation(p.getX(), p.getY());
        }

        /**
         * Returns the current vertex view at a given x- and y-coordinate, or
         * <tt>null</tt> if there is no vertex there.
         */
        private JVertexView vertexAt(Point2D p) {
            GraphJCell jCell =
                getJGraph().getFirstCellForLocation(p.getX(), p.getY(), true);
            return (JVertexView) getJGraph().getGraphLayoutCache().getMapping(
                jCell, false);
        }

        /**
         * Shift the viewport according to the panned distance.
         */
        private void doPan(MouseEvent e) {
            if (this.dragOrigX == -1) {
                return; // never happens ??
            }
            Point p = getViewPort().getViewPosition();
            p.x -= (e.getX() - this.dragOrigX);
            p.y -= (e.getY() - this.dragOrigY);

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
            return getJGraph().getMode() == PAN_MODE && getViewPort() != null;
        }

        /** The JGraph's ancestor viewport, if any. */
        private JViewport getViewPort() {
            return getJGraph().getViewPort();
        }

        /** Rubber band renderer on the JGraph. */
        private final RubberBand band = new RubberBand(getJGraph());
        /**
         * The currently focused vertex view, i.e., the vertex view under the
         * mouse pointer.
         */
        private JVertexView focusVertex;

        private DragMode dragMode;
        /** X-coordinate of a point where dragging started. */
        private int dragOrigX = -1;
        /** Y-coordinate of a point where dragging started. */
        private int dragOrigY = -1;
        /** Armed vertex at the point where dragging started. */
        private JVertexView dragOrigVertex = null;

    } // End of BasicGraphUI.MouseHandler

    enum DragMode {
        DRAG, PAN, EDGE, RUBBER;
    }
}
