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
 * $Id: AspectJGraph.java,v 1.10 2008-01-30 09:33:14 iovka Exp $
 */
package groove.gui.jgraph;

import groove.graph.GraphRole;
import groove.gui.Editor;
import groove.gui.Exporter;
import groove.gui.Options;
import groove.gui.SetLayoutMenu;
import groove.gui.Simulator;
import groove.gui.layout.ForestLayouter;
import groove.gui.layout.SpringLayouter;
import groove.trans.RuleName;
import groove.view.aspect.AspectGraph;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.AttributeMap.SerializableRectangle2D;
import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.PortView;

/**
 * Extension of {@link JGraph} that provides the proper popup menu.
 */
final public class AspectJGraph extends JGraph {
    /**
     * Creates a j-graph for a given simulator, with an initially empty j-model.
     */
    public AspectJGraph(Simulator simulator, GraphRole role) {
        super(null, role != GraphRole.RULE);
        this.simulator = simulator;
        this.editor = null;
        assert role.inGrammar();
        this.graphRole = role;
        setModel(AspectJModel.EMPTY_JMODEL);
    }

    /**
     * Creates a j-graph for a given simulator, with an initially empty j-model.
     */
    public AspectJGraph(Editor editor) {
        super(null, false);
        this.simulator = null;
        this.editor = editor;
        this.graphRole = null;
        setMarqueeHandler(createMarqueeHandler());
        getGraphLayoutCache().setSelectsLocalInsertedCells(true);
        setCloneable(true);
        setConnectable(true);
        setDisconnectable(true);
        setModel(AspectJModel.newInstance(editor,
            AspectGraph.emptyGraph(editor.getRole())));
    }

    @Override
    public AspectJModel getModel() {
        return (AspectJModel) super.getModel();
    }

    @Override
    public Simulator getSimulator() {
        return this.simulator;
    }

    /** 
     * Returns the editor with which this JGraph is associated.
     * May be {@code null} is the simulator is set instead.
     */
    public Editor getEditor() {
        return this.editor;
    }

    /** 
     * Returns the role of either the simulator or the editor,
     * whichever is set.
     */
    public GraphRole getGraphRole() {
        if (this.simulator == null) {
            return this.editor.getRole();
        } else {
            return this.graphRole;
        }
    }

    @Override
    public JMenu createPopupMenu(Point atPoint) {
        JMenu result = new JMenu("Popup");
        if (this.simulator != null) {
            switch (getGraphRole()) {
            case HOST:
                result.add(this.simulator.getApplyTransitionAction());
                result.addSeparator();
                result.add(this.simulator.getEditGraphAction());
                break;
            case RULE:
                JMenu setRuleMenu = createSetRuleMenu();
                setRuleMenu.setEnabled(getSimulator().getGrammarView() != null);
                result.add(setRuleMenu);
                result.addSeparator();
                result.add(this.simulator.getEditRuleAction());
                break;
            case TYPE:
                result.add(this.simulator.getEditTypeAction());
            }
        }
        addSubmenu(result, super.createPopupMenu(atPoint));
        return result;
    }

    @Override
    public SetLayoutMenu createSetLayoutMenu() {
        if (this.editor != null) {
            SetLayoutMenu result =
                new SetLayoutMenu(this, new SpringLayouter());
            result.addLayoutItem(new ForestLayouter());
            return result;
        } else {
            return super.createSetLayoutMenu();
        }
    }

    /**
     * Adds the label editing action to the super menu.
     */
    @Override
    public JMenu createEditMenu(Point atPoint, boolean always) {
        JMenu result = super.createEditMenu(atPoint, always);
        if (this.editor != null) {
            Action editAction = getEditLabelAction();
            if (always || editAction.isEnabled()) {
                result.add(editAction);
            }
        }
        return result;
    }

    /**
     * Computes and returns a menu that allows setting the display to another
     * rule.
     */
    private JMenu createSetRuleMenu() {
        // add actions to set the rule display to each production rule
        JMenu setMenu = new JMenu("Set rule to") {
            @Override
            public void menuSelectionChanged(boolean selected) {
                super.menuSelectionChanged(selected);
                if (selected) {
                    removeAll();
                    for (RuleName ruleName : getSimulator().getGrammarView().getRuleNames()) {
                        add(createSetRuleAction(ruleName));
                    }
                }
            }
        };
        return setMenu;
    }

    /** Action to change the display to a given (named) rule. */
    private Action createSetRuleAction(final RuleName ruleName) {
        return new AbstractAction(ruleName.toString()) {
            public void actionPerformed(ActionEvent evt) {
                getSimulator().setRule(ruleName);
            }
        };
    }

    @Override
    protected Exporter getExporter() {
        if (this.simulator == null) {
            return this.editor.getExporter();
        } else {
            return this.simulator.getExporter();
        }
    }

    @Override
    protected String getExportActionName() {
        switch (getGraphRole()) {
        case HOST:
            return Options.EXPORT_STATE_ACTION_NAME;
        case RULE:
            return Options.EXPORT_RULE_ACTION_NAME;
        case TYPE:
            return Options.EXPORT_TYPE_ACTION_NAME;
        }
        throw new IllegalStateException();
    }

    @Override
    public void setEditable(boolean editable) {
        setCloneable(editable);
        setConnectable(editable);
        setDisconnectable(editable);
        super.setEditable(editable);
    }

    /**
     * This implementation returns a {@link EditorMarqueeHandler}.
     * @see groove.gui.jgraph.JGraph#createMarqueeHandler()
     */
    @Override
    protected EditorMarqueeHandler createMarqueeHandler() {
        return new EditorMarqueeHandler(this);
    }

    /**
     * Adds a j-vertex to the j-graph, and positions it at a given point. The
     * point is in screen coordinates
     * @param screenPoint the intended central point for the new j-vertex
     */
    void addVertex(Point2D screenPoint) {
        stopEditing();
        Point2D atPoint = fromScreen(snap(screenPoint));
        // define the j-cell to be inserted
        AspectJVertex jVertex = getModel().computeJVertex();
        jVertex.setNodeFixed();
        // set the bounds and store them in the cell
        Dimension size = JAttr.DEFAULT_NODE_SIZE;
        Point2D corner =
            new Double(atPoint.getX() - (double) size.width / 2
                - JAttr.EXTRA_BORDER_SPACE, atPoint.getY()
                - (double) size.height / 2 - JAttr.EXTRA_BORDER_SPACE);
        GraphConstants.setBounds(
            jVertex.getAttributes(),
            new SerializableRectangle2D(corner.getX(), corner.getY(),
                size.getWidth(), size.getHeight()));
        // add the cell to the jGraph
        Object[] insert = new Object[] {jVertex};
        getModel().insert(insert, null, null, null, null);
        setSelectionCell(jVertex);
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
    void addEdge(Point2D screenFrom, Point2D screenTo) {
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
        AspectJEdge newEdge = getModel().computeJEdge();
        // to make sure there is at least one graph edge wrapped by this JEdge,
        // we add a dummy edge label to the JEdge's user object
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
        setSelectionCell(newEdge);
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
    boolean isEdgeMode(MouseEvent evt) {
        boolean result = false;
        if (this.editor != null && this.editor.isEdgeMode()) {
            result = isVertex(getFirstCellForLocation(evt.getX(), evt.getY()));
        }
        return result;
    }

    /**
     * Callback method to determine whether an event concerns node creation. To
     * be overridden by subclasses.
     * @param evt evt the event on the basis of which the judgement is made
     * @return <tt>true</tt> if node creation mode is available and enabled
     */
    boolean isNodeMode(MouseEvent evt) {
        boolean result = false;
        if (evt.getButton() == MouseEvent.BUTTON1 && this.editor != null
            && this.editor.isNodeMode()) {
            result = getFirstCellForLocation(evt.getX(), evt.getY()) == null;
        }
        return result;
    }

    /**
     * Flag to indicate creating a node will immediately start editing the node
     * label
     */
    private final boolean startEditingNewNode = true;
    /**
     * Flag to indicate creating an edge will immediately start editing the edge
     * label
     */
    private final boolean startEditingNewEdge = true;
    /**
     * The simulator with which this j-graph is associated.
     * Either this or {@link #editor} is set.
     */
    private final Simulator simulator;
    /**
     * The editor with which this j-graph is associated.
     * Either this or {@link #simulator} is set.
     */
    private final Editor editor;

    /** The role for which this {@link JGraph} will display graphs. */
    private final GraphRole graphRole;
}