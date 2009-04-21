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
 * $Id: EditorJGraph.java,v 1.9 2008-01-30 09:33:13 iovka Exp $
 */
package groove.gui.jgraph;

import groove.gui.Editor;
import groove.gui.IEditorModes;
import groove.gui.SetLayoutMenu;
import groove.gui.layout.ForestLayouter;
import groove.gui.layout.SpringLayouter;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;

import javax.swing.Action;
import javax.swing.JPopupMenu;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.PortView;
import org.jgraph.graph.AttributeMap.SerializableRectangle2D;

/**
 * A j-graph dedicated towards the editor. In particular, provides a method to
 * add and remove points from edges.
 * @author Arend Rensink
 * @version $Revision$
 */
public class EditorJGraph extends JGraph {
    /**
     * Constructs an editor j-graph with an initially empty {@link EditorJModel}
     * .
     * @param editor the {@link IEditorModes} to which this j-graph is
     *        associated
     * @since june2005
     */
    public EditorJGraph(Editor editor) {
        super(new EditorJModel(editor.getOptions()), false);
        this.editor = editor;
        setMarqueeHandler(createMarqueeHandler());
        getGraphLayoutCache().setSelectsLocalInsertedCells(true);
        setCloneable(true);
    }

    @Override
    protected SetLayoutMenu createSetLayoutMenu() {
        SetLayoutMenu result = new SetLayoutMenu(this, new SpringLayouter());
        result.addLayoutItem(new ForestLayouter());
        return result;
    }

    /**
     * Adds all known general j-cell editing actions to a given popup menu.
     * (non-Javadoc)
     * @param menu the menu to which actions should be added
     * @param always if <code>false</code>, only enabled actions should be added
     * @see groove.gui.jgraph.JGraph#fillOutEditMenu(javax.swing.JPopupMenu,
     *      boolean)
     */
    @Override
    public void fillOutEditMenu(JPopupMenu menu, boolean always) {
        super.fillOutEditMenu(menu, always);
        Action editAction = getEditLabelAction();
        if (always || editAction.isEnabled()) {
            addSeparatorUnlessFirst(menu);
            menu.add(editAction);
        }
    }

    /** Specialises the return type to {@link EditorJModel}. */
    @Override
    public EditorJModel getModel() {
        return (EditorJModel) this.graphModel;
    }

    /**
     * @return the editor{@link IEditorModes} to which this j-graph is
     *         associated
     */
    protected IEditorModes getEditor() {
        return this.editor;
    }

    /**
     * This implementation returns a {@link EditorMarqueeHandler}. (non-Javadoc)
     * @see groove.gui.jgraph.JGraph#createMarqueeHandler()
     */
    @Override
    protected EditorMarqueeHandler createMarqueeHandler() {
        return new EditorMarqueeHandler(this);
    }

    /**
     * Adds a j-vertex to the j-graph, and positions it at a given point. The
     * point is in screen coorinates
     * @param screenPoint the intended central point for the new j-vertex
     */
    protected void addVertex(Point2D screenPoint) {
        stopEditing();
        Point2D atPoint = fromScreen(snap(screenPoint));
        // define the j-cell to be inserted
        DefaultGraphCell jVertex = getModel().computeJVertex();
        // set the bounds and store them in the cell
        Dimension size = JAttr.DEFAULT_NODE_SIZE;
        Point2D corner =
            new Double(atPoint.getX() - size.width / 2, atPoint.getY()
                - size.height / 2);
        GraphConstants.setBounds(jVertex.getAttributes(),
            new SerializableRectangle2D(corner.getX(), corner.getY(),
                size.getWidth(), size.getHeight()));
        // add the cell to the jGraph
        Object[] insert = new Object[] {jVertex};
        getModel().insert(insert, null, null, null, null);
        // immediately add a label, if so indicated by startEditingNewNode
        if (this.startEditingNewNode) {
            startEditingAtCell(jVertex);
        }
    }

    /**
     * Adds an edge beteen two given points. The edge actually goes from the
     * vertices underlying the points. The end point may not be at a vertex, in
     * which case a self-edge should be drawn. The points are given in screen
     * coordinates.
     * @param screenFrom The start point of the new edge
     * @param screenTo The end point of the new edge
     */
    protected void addEdge(Point2D screenFrom, Point2D screenTo) {
        stopEditing();
        // translate screen coordinates to real coordinates
        PortView fromPortView =
            getPortViewAt(screenFrom.getX(), screenFrom.getY());
        PortView toPortView = getPortViewAt(screenTo.getX(), screenTo.getY());
        Point2D from = fromScreen((Point2D) screenFrom.clone());
        Point2D to = fromScreen((Point2D) screenTo.clone());
        assert fromPortView != null : "addEdge should not be called with dangling source "
            + from;
        DefaultPort fromPort = (DefaultPort) fromPortView.getCell();
        // if toPortView is null, we're drawing a self-edge
        DefaultPort toPort =
            toPortView == null ? fromPort : (DefaultPort) toPortView.getCell();
        // define the edge to be inserted
        DefaultEdge newEdge = getModel().computeJEdge();
        Object[] insert = new Object[] {newEdge};
        // define connections between edge and nodes, if any
        ConnectionSet cs = new ConnectionSet();
        cs.connect(newEdge, fromPort, true);
        cs.connect(newEdge, toPort, false);
        // if we're drawing a self-edge, provide some intermediate points
        if (toPort == fromPort) {
            AttributeMap edgeAttr = newEdge.getAttributes();
            ArrayList<Point2D> endpointList = new ArrayList<Point2D>(4);
            endpointList.add(from);
            // this middle point is there to provide a vector for
            // the direction and size of the self-loop
            endpointList.add(to);
            endpointList.add(to);
            GraphConstants.setPoints(edgeAttr, endpointList);
        }
        // add the cell to the jGraph
        getModel().insert(insert, null, cs, null, null);
        // immediately add a label
        if (this.startEditingNewEdge) {
            startEditingAtCell(newEdge);
        }
    }

    /**
     * Callback method to determine whether an event concerns edge creation. To
     * be overridden by subclasses.
     * @param evt the event on the basis of which the judgement is made
     * @return <tt>true</tt> if edge creation mode is available and enabled
     */
    protected boolean isEdgeMode(MouseEvent evt) {
        /* added by Carel */
        if (getEditor() == null) {
            return false;
        }
        /* end */
        if (!getEditor().isEdgeMode()) {
            return false;
        }
        Object cell = getFirstCellForLocation(evt.getX(), evt.getY());
        return isVertex(cell);
    }

    /**
     * Callback method to determine whether an event concerns node creation. To
     * be overridden by subclasses.
     * @param evt evt the event on the basis of which the judgement is made
     * @return <tt>true</tt> if node creation mode is available and enabled
     */
    protected boolean isNodeMode(MouseEvent evt) {
        /* added by Carel */
        if (getEditor() == null) {
            return false;
        }
        /* end */

        return (getEditor().isNodeMode() && getFirstCellForLocation(evt.getX(),
            evt.getY()) == null);
    }

    /**
     * Flag to indicate creating a node will immediately start aditing the node
     * label
     */
    protected boolean startEditingNewNode = false;
    /**
     * Flag to indicate creating an edge will immediately start aditing the edge
     * label
     */
    protected boolean startEditingNewEdge = true;
    /**
     * The editor component in which this j-graph is placed.
     */
    private final IEditorModes editor;
}