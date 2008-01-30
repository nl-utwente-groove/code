/*
 * $Id: EditorMarqueeHandler.java,v 1.7 2008-01-30 09:33:14 iovka Exp $
 *
 * Derived from: @(#)GPGraph.java	1.0 1/1/02
 *
 * Copyright (C) 2001 Gaudenz Alder
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package groove.gui.jgraph;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import org.jgraph.graph.BasicMarqueeHandler;
import org.jgraph.graph.VertexView;

/**
 * Abstract MarqueeHandler that can insert cells and edges. The class can be specialized with
 * different ways to detect node addition mode, edge addition mode and popup menu creation, and with
 * different implementations of those actions. Lobotomized from jgrappad.
 * 
 * @author Gaudenz Alder; adapted by Arend Rensink
 * @version $Revision: 1.7 $ $Date: 2008-01-30 09:33:14 $
 */
public class EditorMarqueeHandler extends BasicMarqueeHandler {
    static private final Color ADDING_EDGE_COLOR = Color.black;
    
    /**
     * Creates a marquee handler for a given <tt>JGraph</tt>.
     */
    public EditorMarqueeHandler(EditorJGraph jGraph) {
        this.jGraph = jGraph;
    }

    /**
     * Indicates if this handler should be preferred over other handlers. This question is deferred
     * to <tt>isMyMarqueeEvent()</tt> and <tt>super</tt>.
     */
    @Override
    public boolean isForceMarqueeEvent(MouseEvent evt) {
        return isMyMarqueeEvent(evt) || super.isForceMarqueeEvent(evt);
    }

    /**
     * If the mouse event is for this marquee handler, it can be one of
     * <ul>
     * <li>A node creation event: create the node
     * <li>The start of a edge creation; store the starting point
     * <li>A popup menu event; create the popup
     * </ul>
     * Pass on the event to <tt>super</tt> if it is not for us.
     * 
     * @param evt the event that happened
     */
    @Override
    public void mousePressed(MouseEvent evt) {
        assert evt.getSource() == getJGraph() : "Marquee handler can only deal with " + getJGraph()
                + ", not with " + evt.getSource();
        if (!evt.isConsumed() && isMyMarqueeEvent(evt)) {
            if (getJGraph().isNodeMode(evt)) {
                getJGraph().addVertex(evt.getPoint());
            } else if (getJGraph().isEdgeMode(evt)) {
                if (currentVertex != null) {
                    setAddingEdge(currentVertex);
                    setAddingEdgeEndPoint(evt.getPoint());
                    setEmphVertex(null);
                    redrawOverlay();
                }
            }
            evt.consume();
        } else {
        	super.mousePressed(evt);
        }
    }

    /**
     * If the mouse event is for this marquee handler, it means we are in the process of adding an
     * edge. We change the node emphasis accordingly. Pass on the event to <tt>super</tt> if it is
     * not for us.
     * 
     * @param evt the event that happened
     */
    @Override
    public void mouseDragged(MouseEvent evt) {
        assert evt.getSource() == getJGraph() : "Marquee handler can only deal with " + getJGraph()
                + ", not with " + evt.getSource();
        if (!evt.isConsumed() && isMyMarqueeEvent(evt) && isAddingEdge()) {
            currentVertex = vertexAt(evt.getPoint());
            setEmphVertex(currentVertex != startVertex ? currentVertex : null);
            setAddingEdgeEndPoint(currentVertex == null ? evt.getPoint() : getJGraph().toScreen(VertexView.getCenterPoint(currentVertex)));
            redrawOverlay();
            evt.consume();
        } else  {
            super.mouseDragged(evt);
        }
    }

    /**
     * If the mouse event is for this marquee handler, it must indicate the end of an edge. Add the
     * edge if start and end do not coincide. Passes on the event to <tt>super</tt> if it is not
     * for us.
     * 
     * @param evt the event that happened
     */
    @Override
    public void mouseReleased(MouseEvent evt) {
        assert evt.getSource() == getJGraph() : "Marquee handler can only deal with " + getJGraph()
                + ", not with " + evt.getSource();
        if (!evt.isConsumed() && isMyMarqueeEvent(evt)) {
            if (isAddingEdge() && currentVertex != startVertex) {
                Point2D endPoint = currentVertex == null ? addingEdgeEndPoint : getJGraph().toScreen(VertexView.getCenterPoint(currentVertex));
                getJGraph().addEdge(addingEdgeStartPoint, endPoint);
            }
            setAddingEdge(null);
            setEmphVertex(null);
            redrawOverlay();
            evt.consume();
        } 
        super.mouseReleased(evt);
    }

    /**
     * If the mouse event is for this marquee handler, it is a node mode or edge mode event. Because
     * we might be just entering this mode, we change the cursor. If it is edge mode and the mouse
     * entered or left a node, node emphasis is changed. Passes on the event to <tt>super</tt> if
     * it is not for us.
     * 
     * @param evt the event that happened
     */
    @Override
    public void mouseMoved(MouseEvent evt) {
        assert evt.getSource() == getJGraph() : "Marquee handler can only deal with " + getJGraph()
                + ", not with " + evt.getSource();
        // better make sure we're not still adding an edge
        setAddingEdge(null);
        if (!evt.isConsumed() && isMyMarqueeEvent(evt)) {
            getJGraph().setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
            evt.consume();
            if (getJGraph().isEdgeMode(evt)) {
                currentVertex = vertexAt(evt.getPoint());
                setEmphVertex(currentVertex);
                redrawOverlay();
            }
        } 
        else {
            setEmphVertex(null);
            redrawOverlay();
        }
        super.mouseMoved(evt);
    }

    /**
     * In addition to <tt>super</tt>, draw node emphasis and edge being added.
     * @see #drawEmphVertex(Graphics)
     * @see #drawAddingEdge(Graphics)
     */
    public void overlay(Graphics g) {
        super.overlay(getJGraph(), g, false);
        drawEmphVertex(g);
        drawAddingEdge(g);
    }
    
    /**
     * Calls {@link #overlay(Graphics)} twice, with the color of the graphics set to
     * the j-graph foreground and the XOR value to the background.
     * In between the method {@link #changeOverlayState()} is called to install the
     * values for the new overlay; at the end the {@link #overlayDone()} is called. 
     */
    public void redrawOverlay() {
        Graphics g = getJGraph().getGraphics();
        g.setColor(getJGraph().getForeground());
        g.setXORMode(getJGraph().getBackground());
        overlay(g);      
        changeOverlayState();
        overlay(g);
        overlayDone();
    }
    
    /**
     * Callback method to change the drawable state of the overlay to some
     * (previously determined) new state.
     * Callback method invoked in {@link #redrawOverlay()} aver undrawing 
     * (first call of {@link #overlay(Graphics)} but before (re)drawing
     * (second call of {@link #overlay(Graphics)}.
     */
    protected void changeOverlayState() {
        emphVertex = newEmphVertex;
        addingEdgeStartPoint = newAddingEdgeStartPoint;
        addingEdgeEndPoint = newAddingEdgeEndPoint;
    }
    
    /**
     * Sets the changed flags of the adding edge and emphasized vertex to <tt>false</tt>.
     * Callback method invoked at the end of {@link #redrawOverlay()}.
     */
    protected void overlayDone() {
        emphVertexChanged = false;
        addingEdgeChanged = false;
    }
    
    /**
     * Indicates whether the marquee handler is in the process of drawing a new edge.
     * @return <tt>true</tt> if a new edge is being drawn
     */
    protected boolean isAddingEdge() {
        return startVertex != null;
    }
    
    /**
     * Changes the <i>adding edge</i> state of the marquee hendler.
     * Sets or resets the start vertex and the start point of the edge.
     * @param startPort the new start port for the edge being added; if <tt>null</tt>, nu edge
     * will be drawn
     */
    protected void setAddingEdge(VertexView startPort) {
        this.startVertex = startPort;
        if (startPort == null) {
            setAddingEdgeStartPoint(null);
        } else {
            setAddingEdgeStartPoint(getJGraph().toScreen(VertexView.getCenterPoint(startPort)));
        }
    }

    /**
     * Sets a given color as foreground color for a given graphics.
     * The method returns the previously set colour as return value, to
     * allow restoring it at some later point.
     */
    protected Color setForeground(Graphics g, Color fg) {
            Color result = g.getColor();
            g.setColor(fg);
            return result;
    }
    
    /**
     * Tests if a given mouse event should be handeled by this handler. This is the case (currently)
     * if it is a node mode or edge mode event, or if the {@link #isAddingEdge()} proprty holds.
     * 
     * @param evt the event to be tested
     * @return <tt>jGraph().isNodeMode(evt) || jGraph().isEdgeMode(evt)</tt>
     */
    protected boolean isMyMarqueeEvent(MouseEvent evt) {
        return isAddingEdge() || getJGraph().isNodeMode(evt) || getJGraph().isEdgeMode(evt);
    }

    /**
     * Sets the starting point of the <i>adding edge</i> element on the overlay.
     * if <tt>null</tt>, no adding edge is drawn.
     * The change will be realized on the next invocation of {@link #redrawOverlay()}.
     */
    private void setAddingEdgeStartPoint(Point2D newStartPoint) {
        addingEdgeChanged |= (newStartPoint != addingEdgeStartPoint);
        this.newAddingEdgeStartPoint = newStartPoint;
    }

    /**
     * Sets the end point of the <i>adding edge</i> element on the overlay.
     * The change will be realized on the next invocation of {@link #redrawOverlay()}.
     */
    private void setAddingEdgeEndPoint(Point2D newEndPoint) {
        addingEdgeChanged |= (newEndPoint != addingEdgeEndPoint);
        this.newAddingEdgeEndPoint = newEndPoint;
    }

    /**
     * Sets the <i>emphasised vertex</i> element on the overlay.
     * If <tt>null</tt>, no vertex is emphasised.
     * The change will be realized on the next invocation of {@link #redrawOverlay()}.
     */
    private void setEmphVertex(VertexView newEmphVertex) {
        emphVertexChanged = (newEmphVertex != emphVertex);
        this.newEmphVertex = newEmphVertex;
    }

    /**
     * Draws a line for the edge being added, if any.
     */
    private void drawAddingEdge(Graphics g) {
        // draw a line for an edge being added
        Color origColor = setForeground(g, ADDING_EDGE_COLOR);
        if (addingEdgeChanged && addingEdgeStartPoint != null && addingEdgeEndPoint != null) {
            g.drawLine((int) addingEdgeStartPoint.getX(),
                (int) addingEdgeStartPoint.getY(),
                (int) addingEdgeEndPoint.getX(),
                (int) addingEdgeEndPoint.getY());
        }
        setForeground(g, origColor);
    }

    /**
     * Draws an emphasized vertex, if it is set
     * @see #setEmphVertex(VertexView)
     */
    private void drawEmphVertex(Graphics g) {
        if (emphVertexChanged && emphVertex instanceof JVertexView) {
            ((JVertexView) emphVertex).paintArmed(g);
        }
    }

    /**
     * Returns the current vertex view at a given x- and y-coordinate,
     * or <tt>null</tt> if there is no vertex there.
     */
    private VertexView vertexAt(Point2D p) {
        JCell jCell = (JCell) getJGraph().getFirstCellForLocation(p.getX(), p.getY());
        if (jCell instanceof JVertex) {
            return (VertexView) getJGraph().getGraphLayoutCache().getMapping(jCell, false);
        } else {
            return null;
        }
    }

    /**
     * Returns the j-graph upon which this marquee handler works.
     */
    final EditorJGraph getJGraph() {
        return this.jGraph;
    }

    /** The j-graph upon which this marquee handler works. */
    private final EditorJGraph jGraph;
    /** 
     * While adding an edge, the vertex at which edge drawing started.
     * A value of <tt>null</tt> indicates no edge is being added.
     */
    private VertexView startVertex;

    /** The vertex at <tt>currentPoint</tt> */
    private VertexView currentVertex;
    /**
     * Flag indicating that some aspect of the <i>adding edge</i> component on the overlay has
     * changed, so it should be redrawn.
     */
    private boolean addingEdgeChanged;
    /**
     * The current start and point of the edge being drawn.
     * Used in repainting the overlay.
     * @see #drawAddingEdge(Graphics)
     */
    private Point2D addingEdgeStartPoint;
    /**
     * The new end point of the edge being drawn.
     * Used in repainting the overlay.
     * @see #drawAddingEdge(Graphics)
     */
    private Point2D newAddingEdgeStartPoint;
    /**
     * The current end point of the edge being drawn.
     * Used in repainting the overlay.
     * @see #drawAddingEdge(Graphics)
     */
    private Point2D addingEdgeEndPoint;
    /**
     * The new end point of the edge being drawn.
     * Used in repainting the overlay.
     * @see #drawAddingEdge(Graphics)
     */
    private Point2D newAddingEdgeEndPoint;
    /**
     * Indicates that the <i>emphasized vertex</i> component on the overlay has
     * changed, so it should be redrawn.
     */
    private boolean emphVertexChanged;
    /**
     * The current emphasized vertex.
     * Used in repainting the overlay.
     * @see #drawEmphVertex(Graphics)
     */
    private VertexView emphVertex;
    /**
     * The new emphasized vertex.
     * Used in repainting the overlay.
     * @see #drawEmphVertex(Graphics)
     */
    private VertexView newEmphVertex;
}
