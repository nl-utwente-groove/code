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
import static groove.gui.jgraph.JGraphMode.EDIT_MODE;
import static groove.gui.jgraph.JGraphMode.PAN_MODE;
import static groove.gui.jgraph.JGraphUI.DragMode.EDGE;
import static groove.gui.jgraph.JGraphUI.DragMode.MOVE;
import static groove.gui.jgraph.JGraphUI.DragMode.PAN;
import static groove.gui.jgraph.JGraphUI.DragMode.SELECT;
import static java.awt.event.MouseEvent.BUTTON1;
import static java.awt.event.MouseEvent.BUTTON3;
import groove.graph.Graph;
import groove.gui.Icons;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.swing.JComponent;
import javax.swing.JViewport;
import javax.swing.event.MouseInputAdapter;

import org.jgraph.graph.CellView;
import org.jgraph.plaf.basic.BasicGraphUI;

/** Adapted UI for JGraphs. */
public class JGraphUI<G extends Graph> extends BasicGraphUI {
    @SuppressWarnings("unchecked")
    private JGraph<G> getJGraph() {
        return (JGraph<G>) this.graph;
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

    //
    //    @Override
    //    public Dimension2D getPreferredSize(JGraph graph, CellView view) {
    //        if (view.getCell() instanceof AspectJVertex
    //            && ((AspectJVertex) view.getCell()).isNodeEdge()) {
    //            int diameter = (int) (2 * (NODE_EDGE_RADIUS + EXTRA_BORDER_SPACE));
    //            return new Dimension(diameter, diameter);
    //        } else {
    //            return super.getPreferredSize(graph, view);
    //        }
    //    }

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

    @Override
    protected void installListeners() {
        super.installListeners();
        this.graph.addMouseWheelListener((MouseWheelListener) this.mouseListener);
    }

    @Override
    public void drawGraph(Graphics g, Rectangle2D clipBounds) {
        super.drawGraph(g, clipBounds);
        if (!this.graph.isDoubleBuffered()) {
            getMouseListener().draw(g);
        }
    }

    @Override
    protected void paintOverlay(Graphics g) {
        // this method is used if the JGraph is double buffered
        getMouseListener().draw(g);
    }

    @SuppressWarnings("unchecked")
    private MouseHandler getMouseListener() {
        return ((MouseHandler) this.mouseListener);
    }

    @Override
    protected MouseListener createMouseListener() {
        return new MouseHandler();
    }

    /** Cancel the edge adding mode that the mouse handler might be engaged in. */
    public void cancelEdgeAdding() {
        getMouseListener().cancelEdgeAdding(null);
    }

    private static final boolean ADD_EDGE_BY_CLICK = true;

    /**
     * This is a complete reimplementation.
     */
    public class MouseHandler extends MouseAdapter implements Serializable {
        MouseHandler() {
            this.selectHandler = new RubberBand(getJGraph());
            if (getJGraph() instanceof AspectJGraph) {
                this.edgeHandler = new EdgePreview((AspectJGraph) getJGraph());
            } else {
                this.edgeHandler = null;
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (!isMyEvent(e)) {
                return;
            }
            boolean addEdge = false;
            if (getJGraphMode() == EDIT_MODE && e.getButton() == BUTTON1) {
                // this is an editing-related event
                AspectJGraph jGraph = (AspectJGraph) getJGraph();
                if (isEdgeAdding() && e.getClickCount() == 1) {
                    // finish edge adding
                    finishEdgeAdding(e);
                } else if (e.isAltDown()) {
                    // add or remove an edge point
                    JCell<G> jEdge = getJEdgeAt(e.getPoint());
                    Object selectedCell = getJGraph().getSelectionCell();
                    if (selectedCell instanceof JEdge) {
                        AspectJEdge selectedEdge = (AspectJEdge) selectedCell;
                        if (selectedCell == jEdge) {
                            jGraph.getRemovePointAction(e.getPoint()).execute(
                                selectedEdge);
                        } else {
                            jGraph.getAddPointAction(e.getPoint()).execute(
                                selectedEdge);
                        }
                    }
                } else if (getJCellAt(e.getPoint()) != null) {
                    // select and possibly start adding edge, or edit vertex
                    JCell<G> jCell = getJCellAt(e.getPoint());
                    switch (e.getClickCount()) {
                    case 1:
                        selectCellsForEvent(Collections.singleton(jCell), e);
                        addEdge =
                            jCell instanceof JVertex && !e.isControlDown()
                                && !e.isShiftDown();
                        break;
                    case 2:
                        jGraph.startEditingAtCell(jCell);
                    }
                } else {
                    switch (e.getClickCount()) {
                    case 1:
                        jGraph.clearSelection();
                        break;
                    case 2:
                        if (jGraph.isEditable()) {
                            jGraph.addVertex(e.getPoint());
                        }
                    }
                }
            } else if (e.getButton() != BUTTON3) {
                // this is not an editing-related event
                JCell<G> jCell = getJCellAt(e.getPoint());
                if (jCell == null) {
                    getJGraph().clearSelection();
                } else {
                    selectCellsForEvent(Collections.singleton(jCell), e);
                }
            }
            if (isEdgeAdding()) {
                cancelEdgeAdding(e);
            } else if (ADD_EDGE_BY_CLICK && addEdge && getJGraph().isEditable()) {
                startEdgeAdding(e);
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (!isMyEvent(e)) {
                return;
            }
            getJGraph().requestFocus();
            stopEditing(getJGraph());
            // determine the drag mode (although dragging does not yet start)
            DragMode newDragMode;
            JCell<G> jVertex = getJVertexAt(e.getPoint());
            JCell<G> jEdge = getJEdgeAt(e.getPoint());
            if (getJGraphMode() == PAN_MODE && e.getButton() == BUTTON1) {
                newDragMode = PAN;
            } else if (jVertex != null || jEdge != null) {
                // either start adding an edge, or move 
                if (getJGraphMode() == EDIT_MODE && e.getButton() == BUTTON1
                    && !ADD_EDGE_BY_CLICK) {
                    if (jEdge != null) {
                        newDragMode = MOVE;
                    } else if (getJGraph().isCellSelected(jVertex)) {
                        newDragMode = EDGE;
                    } else if (e.isAltDown()) {
                        newDragMode = EDGE;
                    } else {
                        newDragMode = MOVE;
                    }
                } else {
                    newDragMode = MOVE;
                }
            } else {
                newDragMode = SELECT;
            }
            this.dragMode = newDragMode;
            this.dragStart = e;
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (!isMyEvent(e)) {
                return;
            }
            autoscroll(getJGraph(), e.getPoint());
            switch (this.dragMode) {
            case PAN:
                if (this.dragStart != null) {
                    this.dragOrigX = this.dragStart.getX();
                    this.dragOrigY = this.dragStart.getY();
                    this.selectHandler.mousePressed(this.dragStart);
                }
                if (isPanEnabled()) {
                    doPan(e);
                }
                return;
            case MOVE:
                if (this.dragStart != null) {
                    // there is a focused cell, or we wouldn't be in move mode
                    // select it if currently not selected
                    // we give preference to selected cells, since otherwise
                    // we will never be able to drag edge points
                    JCell<G> cell = getJEdgeAt(this.dragStart.getPoint());
                    if (cell == null
                        || !getJGraph().getSelectionModel().isCellSelected(cell)) {
                        cell = getJCellAt(this.dragStart.getPoint());
                        getJGraph().setCursor(Icons.HAND_CLOSED_CURSOR);
                    }
                    if (!getJGraph().isCellSelected(cell)) {
                        getJGraph().setSelectionCell(cell);
                    }
                    getHandle().mousePressed(this.dragStart);
                }
                if (getHandle() != null) {
                    getHandle().mouseDragged(e);
                }
                break;
            case EDGE:
                if (this.dragStart != null) {
                    this.edgeHandler.mousePressed(this.dragStart);
                }
                this.edgeHandler.mouseDragged(e);
                break;
            case SELECT:
                if (this.dragStart != null) {
                    this.selectHandler.mousePressed(this.dragStart);
                }
                this.selectHandler.mouseDragged(e);
            }
            this.dragStart = null;
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (!isMyEvent(e)) {
                return;
            }
            // if the drag start has been consumed, end the drag session
            if (this.dragStart == null && this.dragMode != null) {
                switch (this.dragMode) {
                case EDGE:
                    finishEdgeAdding(e);
                    break;
                case MOVE:
                    if (getHandle() != null) {
                        getHandle().mouseReleased(e);
                    }
                    break;
                case SELECT:
                    completeSelect(e);
                    this.selectHandler.mouseReleased(e);
                    break;
                case PAN:
                    this.dragOrigX = -1;
                    this.dragOrigY = -1;
                }
            }
            getJGraph().setCursor(getJGraph().getMode().getCursor());
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            if (!isMyEvent(e)) {
                return;
            }
            if (isEdgeAdding()) {
                if (e.isControlDown() || e.isShiftDown() || e.isAltDown()) {
                    cancelEdgeAdding(e);
                } else {
                    continueEdgeAdding(e);
                }
            } else if (getHandle() != null) {
                getHandle().mouseMoved(e);
                if (!e.isConsumed()) {
                    getJGraph().setCursor(getJGraph().getMode().getCursor());
                }
            }
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            if (!isMyEvent(e)) {
                return;
            }
            if (getJGraph().getMode() == PAN_MODE) {
                int change = -e.getWheelRotation();
                getJGraph().changeScale(change);
            } else {
                getJGraph().getParent().dispatchEvent(e);
            }
        }

        void draw(Graphics g) {
            if (this.dragMode == SELECT) {
                this.selectHandler.draw(g);
            } else if (isEdgeAdding()) {
                this.edgeHandler.draw(g);
            }
        }

        /** Indicates is a given mouse event should be processed. */
        private boolean isMyEvent(MouseEvent evt) {
            return evt != null && !evt.isConsumed() && getJGraph().isEnabled()
                && !getJGraph().isPopupMenuEvent(evt);
        }

        /**
         * Completes the select drag action.
         */
        @SuppressWarnings("unchecked")
        private void completeSelect(MouseEvent evt) {
            Rectangle bounds = this.selectHandler.getBounds();
            if (getJGraphMode() == PAN_MODE) {
                getJGraph().zoomTo(bounds);
            } else {
                // adapt the bound to the scale
                bounds = getJGraph().fromScreen(bounds).getBounds();
                // collect the cells that are entirely in the bounds
                ArrayList<JCell<G>> list = new ArrayList<>();
                CellView[] views = getJGraph().getGraphLayoutCache().getRoots();
                for (int i = 0; i < views.length; i++) {
                    if (bounds.contains(views[i].getBounds())) {
                        list.add((JCell<G>) views[i].getCell());
                    }
                }
                selectCellsForEvent(list, evt);
            }
        }

        /** Changes the selection on the basis of a given collection of 
         * cells and a mouse event.
         * The Ctrl- and Shift-keys of the mouse event determine how
         * the selection changes.
         */
        private void selectCellsForEvent(Collection<JCell<G>> cells,
                MouseEvent evt) {
            if (cells.isEmpty()) {
                getJGraph().clearSelection();
            } else if (isToggleSelectionEvent(evt)) {
                for (JCell<G> jCell : cells) {
                    toggleSelectionCellForEvent(jCell, evt);
                }
            } else if (isAddToSelectionEvent(evt)) {
                getJGraph().addSelectionCells(cells.toArray());
            } else {
                getJGraph().setSelectionCells(cells.toArray());
            }
        }

        /**
         * Returns the current cell at a given x- and y-coordinate, or
         * <tt>null</tt> if there is no cell there.
         */
        private JCell<G> getJCellAt(Point2D p) {
            return getJGraph().getFirstCellForLocation(p.getX(), p.getY());
        }

        /**
         * Returns the current vertex at a given x- and y-coordinate, or
         * <tt>null</tt> if there is no vertex there.
         */
        private JCell<G> getJVertexAt(Point2D p) {
            return getJGraph().getFirstCellForLocation(p.getX(), p.getY(),
                true, false);
        }

        /**
         * Returns the current edge at a given x- and y-coordinate, or
         * <tt>null</tt> if there is no edge there.
         */
        private JCell<G> getJEdgeAt(Point2D p) {
            return getJGraph().getFirstCellForLocation(p.getX(), p.getY(),
                false, true);
        }

        /**
         * Shift the viewport according to the panned distance.
         */
        private void doPan(MouseEvent e) {
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

        private boolean isEdgeAdding() {
            return this.edgeAdding;
        }

        private void startEdgeAdding(MouseEvent e) {
            // possibly start adding edge
            this.edgeAdding = true;
            this.edgeAddStart = e;
            getJGraph().setCursor(
                Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        }

        private void continueEdgeAdding(MouseEvent e) {
            if (this.edgeAddStart != null) {
                this.edgeHandler.mousePressed(e);
                this.edgeAddStart = null;
            }
            this.edgeHandler.mouseDragged(e);
        }

        private void cancelEdgeAdding(MouseEvent e) {
            if (this.edgeAdding) {
                this.edgeAdding = false;
                if (this.edgeAddStart == null) {
                    this.edgeHandler.mouseReleased(e);
                }
            }
            getJGraph().setCursor(getJGraph().getMode().getCursor());
        }

        /** Completes the edge drag action. */
        private void finishEdgeAdding(MouseEvent e) {
            if (this.edgeHandler.edgeStart() != null
                && this.edgeHandler.edgeStart() != this.edgeHandler.edgeEnd()) {
                Rectangle2D start =
                    this.edgeHandler.getScreenBounds(this.edgeHandler.edgeStart());
                Point point = e.getPoint();
                ((AspectJGraph) getJGraph()).addEdge(
                    new Point((int) start.getCenterX(),
                        (int) start.getCenterY()), point);
            }
            this.edgeHandler.mouseReleased(e);
        }

        /** The JGraph's ancestor viewport, if any. */
        private JViewport getViewPort() {
            return getJGraph().getViewPort();
        }

        /** Rubber band renderer on the JGraph. */
        private final RubberBand selectHandler;
        /** Edge preview renderer on the JGraph. */
        private final EdgePreview edgeHandler;
        /** The current drag mode of the cursor. */
        private DragMode dragMode = MOVE;
        /** Mouse pressed event that (with hindsight) started the dragging. */
        private MouseEvent dragStart;
        /** Flag indicating that we are adding an edge. */
        private boolean edgeAdding;
        /** Mouse pressed event that (with hindsight) started adding an edge */
        private MouseEvent edgeAddStart;
        /** X-coordinate of a point where dragging started. */
        private int dragOrigX = -1;
        /** Y-coordinate of a point where dragging started. */
        private int dragOrigY = -1;

    } // End of BasicGraphUI.MouseHandler

    /**
     * A rubber band renderer that reacts on mouse events.
     * Inspired by swingfx
     * @author rwickesser
     * @since 1.0
     * $Revision$
     */
    static public class RubberBand extends MouseInputAdapter {
        /**
         * Creates a new <code>RubberBand</code> and sets the canvas
         * @param canvas    the <code>RubberBandCanvas</code> on which the rubber band
         *                  will be drawn
         */
        public RubberBand(JComponent canvas) {
            this.canvas = canvas;
            this.bounds = new Rectangle();
        }

        /** Returns the bounds of the rubber band. */
        public Rectangle getBounds() {
            return this.bounds.getBounds();
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();
            int w = 0;
            int h = 0;

            // adjust x and width
            if (this.pressX < x) {
                int tmp = x;
                x = this.pressX;
                w = tmp - x;
            } else {
                w = this.pressX - x;
            }

            // adjust y and height
            if (this.pressY < y) {
                int tmp = y;
                y = this.pressY;
                h = tmp - y;
            } else {
                h = this.pressY - y;
            }

            // update rubber band size and location
            update(x, y, w, h);
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (isAllowed(e)) {
                this.pressX = e.getX();
                this.pressY = e.getY();
                this.bounds.setBounds(this.pressX, this.pressY, 0, 0);
                this.active = true;
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (this.active) {
                // only report if the rectangle size is within an error margin
                if (this.bounds.width > 3 && this.bounds.height > 3) {
                    stopRubberBand(e);
                }
                update(-1, -1, 0, 0);
                this.canvas.removeMouseMotionListener(this);
                this.active = false;
            }
        }

        /** 
         * Updates the bounds of the rubber band,
         * and optionally repaints the dirty area.
         */
        private void update(int x, int y, int width, int height) {
            Rectangle dirty = (Rectangle) this.bounds.clone();
            this.bounds.setBounds(x, y, width, height);
            dirty = dirty.union(this.bounds);
            // make sure the dirty area includes the contour of the rubber band
            dirty.x -= 1;
            dirty.y -= 1;
            dirty.height += 2;
            dirty.width += 2;
            this.canvas.repaint(dirty);
        }

        /** Renders the rubber band on the given graphics object. */
        public void draw(Graphics g) {
            if (this.bounds.width >= 0) {
                Color oldColor = g.getColor();
                g.setColor(JAttr.RUBBER_FOREGROUND);
                g.drawRect(this.bounds.x, this.bounds.y, this.bounds.width,
                    this.bounds.height);
                g.setColor(JAttr.RUBBER_BACKGROUND);
                g.fillRect(this.bounds.x, this.bounds.y, this.bounds.width,
                    this.bounds.height);
                g.setColor(oldColor);
            }
        }

        /** Callback method to determine whether the rubber band may be started. */
        protected boolean isAllowed(MouseEvent event) {
            return true;
        }

        /** Callback method invoked after the rubber band is released. */
        protected void stopRubberBand(MouseEvent event) {
            // does nothing
        }

        private boolean active;
        /** the canvas where the rubber band will be drawn onto */
        private final JComponent canvas;

        /** maintains the size and location of the rubber band */
        private final Rectangle bounds;
        /** stores the x coordinate of the mouse pressed event */
        private int pressX;
        /** stores the y coordinate of the mouse pressed event */
        private int pressY;

    }

    /**
     * A rubber band renderer that reacts on mouse events.
     * Inspired by swingfx
     * @author rwickesser
     * @since 1.0
     * $Revision$
     */
    static public class EdgePreview extends MouseInputAdapter {
        /**
         * Creates a new {@link EdgePreview} and sets the canvas
         */
        public EdgePreview(AspectJGraph canvas) {
            this.canvas = canvas;
        }

        @Override
        public void mousePressed(MouseEvent e) {
            this.dragOrigVertex = vertexAt(e.getPoint());
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            update(e);
            if (this.dragCurrVertex != null) {
                this.canvas.setSelectionCell(this.dragCurrVertex.getCell());
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            update(e);
            this.dragOrigVertex = null;
        }

        /**
         * Calculates the dirty area and causes the canvas to be repainted.
         */
        private void update(MouseEvent e) {
            if (this.dragOrigVertex != null) {
                Rectangle dirty =
                    getScreenBounds(this.dragOrigVertex).getBounds();
                if (this.dragCurrPoint != null) {
                    dirty.add(this.dragCurrPoint);
                }
                if (this.dragCurrVertex != null) {
                    dirty.add(this.dragCurrVertex.getBounds());
                }
                if (e != null) {
                    Point point = e.getPoint();
                    this.dragCurrPoint = point;
                    this.dragCurrVertex = vertexAt(point);
                    dirty.add(point);
                }
                if (this.dragCurrVertex != null) {
                    dirty.add(getScreenBounds(this.dragCurrVertex));
                }
                dirty.x -= 1;
                dirty.y -= 1;
                dirty.width += 2;
                dirty.height += 2;
                this.canvas.repaint(dirty);
            }
        }

        /** Renders the rubber band on the given graphics object. */
        public void draw(Graphics g) {
            if (this.dragOrigVertex != null) {
                Rectangle2D startBounds = getScreenBounds(this.dragOrigVertex);
                int startX = (int) startBounds.getCenterX();
                int startY = (int) startBounds.getCenterY();
                int endX, endY;
                if (this.dragCurrVertex != null
                    && this.dragCurrVertex != this.dragOrigVertex) {
                    Rectangle2D endBounds =
                        getScreenBounds(this.dragCurrVertex);
                    endX = (int) endBounds.getCenterX();
                    endY = (int) endBounds.getCenterY();
                } else {
                    endX = this.dragCurrPoint.x;
                    endY = this.dragCurrPoint.y;
                }
                g.setColor(Color.black);
                g.drawLine(startX, startY, endX, endY);
            }
        }

        /** Returns the cloned and upscaled bounds of a vertex view. */
        Rectangle2D getScreenBounds(JVertexView vertex) {
            return this.canvas.toScreen((Rectangle2D) vertex.getBounds().clone());
        }

        /** Returns the vertex view at which the edge dragging started. */
        JVertexView edgeStart() {
            return this.dragOrigVertex;
        }

        /** Returns the (possibly {@code null}) vertex view at which the
         * edge dragging stopped.
         */
        JVertexView edgeEnd() {
            return this.dragCurrVertex;
        }

        /**
         * Returns the current vertex view at a given x- and y-coordinate, or
         * <tt>null</tt> if there is no vertex there.
         */
        private JVertexView vertexAt(Point2D p) {
            JCell<?> jCell =
                this.canvas.getFirstCellForLocation(p.getX(), p.getY(), true,
                    false);
            return (JVertexView) this.canvas.getGraphLayoutCache().getMapping(
                jCell, false);
        }

        /** the canvas where the rubber band will be drawn onto */
        private final AspectJGraph canvas;
        /** Vertex at the point where dragging started. */
        private JVertexView dragOrigVertex = null;
        /** Vertex at {@link #dragCurrPoint}. */
        private JVertexView dragCurrVertex = null;
        /** Point to which an edge has been dragged. */
        private Point dragCurrPoint;
    }

    /** Drag modes of the MouseHandler. */
    static enum DragMode {
        /** Moving a selected set of nodes and edges. */
        MOVE,
        /** Panning the entire graph. */
        PAN,
        /** Adding an edge. */
        EDGE,
        /** Drawing a selection marquee. */
        SELECT;
    }
}
