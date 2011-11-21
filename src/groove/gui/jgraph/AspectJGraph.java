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

import static groove.gui.jgraph.JGraphMode.EDIT_MODE;
import static groove.gui.jgraph.JGraphMode.PREVIEW_MODE;
import static groove.trans.ResourceKind.RULE;
import static groove.view.aspect.AspectKind.ADDER;
import static groove.view.aspect.AspectKind.CREATOR;
import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.GraphRole;
import groove.graph.Node;
import groove.graph.TypeGraph;
import groove.gui.DisplayKind;
import groove.gui.Options;
import groove.gui.SetLayoutMenu;
import groove.gui.Simulator;
import groove.gui.layout.ForestLayouter;
import groove.gui.layout.JCellLayout;
import groove.gui.layout.SpringLayouter;
import groove.trans.ResourceKind;
import groove.util.Colors;
import groove.view.GrammarModel;
import groove.view.aspect.AspectGraph;
import groove.view.aspect.AspectKind;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;

import org.jgraph.event.GraphModelEvent;
import org.jgraph.event.GraphModelListener;
import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.AttributeMap.SerializableRectangle2D;
import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphModel;
import org.jgraph.graph.PortView;

/**
 * Extension of {@link GraphJGraph} for {@link AspectGraph}s.
 */
final public class AspectJGraph extends GraphJGraph {
    /**
     * Creates a new instance, for a given graph role.
     * A flag determines whether the graph is editable.
     * @param kind display kind on which this JGraph will be showing
     * @param editing if {@code true}, the graph is editable
     */
    public AspectJGraph(Simulator simulator, DisplayKind kind, boolean editing) {
        super(simulator, !editing && kind.getGraphRole() != GraphRole.RULE);
        this.editing = editing;
        this.forState = kind == DisplayKind.LTS;
        this.graphRole = this.forState ? GraphRole.HOST : kind.getGraphRole();
        setEditable(editing);
        getGraphLayoutCache().setSelectsLocalInsertedCells(editing);
        setCloneable(editing);
        setConnectable(editing);
        setDisconnectable(editing);
    }

    @Override
    public void setModel(GraphModel model) {
        GraphModel oldModel = getModel();
        if (oldModel != null) {
            oldModel.removeGraphModelListener(getRefreshGraphListener());
        }
        super.setModel(model);
        if (model != null) {
            model.addGraphModelListener(getRefreshGraphListener());
        }
    }

    @Override
    public AspectJModel getModel() {
        return (AspectJModel) super.getModel();
    }

    @Override
    public AspectJModel newModel() {
        return new AspectJModel(AspectJVertex.getPrototype(this),
            AspectJEdge.getPrototype(this), getGrammar());
    }

    /**
     * Notifies the graph that the grammar has changed.
     * This may affect the errors in the model.
     */
    public void updateGrammar(GrammarModel grammar) {
        this.typeGraph = grammar.getTypeGraph();
        this.typeGraphMap = grammar.getTypeModel().getTypeGraphMap();
        AspectJModel model = getModel();
        if (model != null) {
            model.syncGraph();
            getGraphLayoutCache().reload();
            refresh();
        }
    }

    /**
     * Returns a map from names to subsets of labels.
     * This can be used to filter labels.
     */
    public final Map<String,TypeGraph> getTypeGraphMap() {
        return this.typeGraphMap;
    }

    /**
     * Returns the set of labels and subtypes in the graph. May be
     * <code>null</code>.
     */
    public final TypeGraph getTypeGraph() {
        return this.typeGraph;
    }

    /**
     * Indicates whether aspect prefixes should be shown for nodes and edges.
     */
    public final boolean isShowAspects() {
        return getOptionValue(Options.SHOW_ASPECTS_OPTION);
    }

    /**
     * Indicates whether data nodes should be shown in the JGraph.
     * This is certainly the case if this model is editable.
     */
    public final boolean isShowValueNodes() {
        return hasActiveEditor()
            || getOptionValue(Options.SHOW_VALUE_NODES_OPTION);
    }

    @Override
    public void refreshCells(Collection<? extends GraphJCell> jCellSet) {
        // tell the model it has been modified,
        // so refreshing actually changes the cell outlook
        getModel().increaseModificationCount();
        super.refreshCells(jCellSet);
    }

    /** Indicates that the JModel has an editor enabled. */
    public boolean hasActiveEditor() {
        return this.editing && getMode() != PREVIEW_MODE;
    }

    /** Convenience method to retrieve the grammar view from the simulator. */
    private GrammarModel getGrammar() {
        return getSimulatorModel() == null ? null
                : getSimulatorModel().getGrammar();
    }

    /** 
     * Indicates if the graph being displayed is a graph state.
     */
    public boolean isForState() {
        return this.forState;
    }

    /** 
     * Returns the role of the graph being displayed.
     */
    public GraphRole getGraphRole() {
        return this.graphRole;
    }

    @Override
    public JMenu createPopupMenu(Point atPoint) {
        JMenu result = new JMenu("Popup");
        switch (getGraphRole()) {
        case HOST:
            result.add(getActions().getApplyTransitionAction());
            result.addSeparator();
            break;
        case RULE:
            JMenu setRuleMenu = createSetRuleMenu();
            setRuleMenu.setEnabled(getGrammar() != null);
            result.add(setRuleMenu);
            result.addSeparator();
            break;
        }
        Action editAction;
        if (isForState()) {
            editAction = getActions().getEditStateAction();
        } else {
            editAction =
                getActions().getEditAction(
                    ResourceKind.toResource(getGraphRole()));
        }
        result.add(editAction);
        addSubmenu(result, createEditMenu(atPoint));
        addSubmenu(result, super.createPopupMenu(atPoint));
        return result;
    }

    @Override
    public JMenu createExportMenu() {
        // add a save graph action as the first action
        JMenu result = new JMenu();
        if (getActions() != null) {
            Action saveAsAction;
            if (isForState()) {
                saveAsAction = getActions().getSaveStateAction();
            } else {
                saveAsAction =
                    getActions().getSaveAsAction(
                        ResourceKind.toResource(getGraphRole()));
            }
            result.add(saveAsAction);
        }
        addMenuItems(result, super.createExportMenu());
        return result;
    }

    @Override
    public SetLayoutMenu createSetLayoutMenu() {
        if (this.editing) {
            SetLayoutMenu result =
                new SetLayoutMenu(this, new SpringLayouter());
            result.addLayoutItem(new ForestLayouter());
            return result;
        } else {
            return super.createSetLayoutMenu();
        }
    }

    /**
     * Returns a menu containing all known editing actions.
     * @param atPoint point at which the popup menu will appear
     */
    public JMenu createEditMenu(Point atPoint) {
        JMenu result = new JMenu("Edit");
        if (hasActiveEditor()) {
            result.add(getEditLabelAction());
            result.add(getAddPointAction(atPoint));
            result.add(getRemovePointAction(atPoint));
            result.add(getResetLabelPositionAction());
            result.add(createLineStyleMenu());
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
                    for (String ruleName : getGrammar().getNames(RULE)) {
                        add(createSetRuleAction(ruleName));
                    }
                }
            }
        };
        return setMenu;
    }

    /** Action to change the display to a given (named) rule. */
    private Action createSetRuleAction(final String ruleName) {
        return new AbstractAction(ruleName) {
            public void actionPerformed(ActionEvent evt) {
                getSimulatorModel().doSelect(RULE, ruleName);
            }
        };
    }

    @Override
    public void setEditable(boolean editable) {
        setCloneable(editable);
        setConnectable(editable);
        setDisconnectable(editable);
        super.setEditable(editable);
    }

    /**
     * Adds an intermediate point to a given j-edge, controlled by a given
     * location. If the location if <tt>null</tt>, the point is added directly
     * after the initial point of the edge, at a slightly randomized position.
     * Otherwise, the point is added at the given location, between the
     * (existing) points closest to the location.
     * @param jEdge the j-edge to be modified
     * @param location the point to be added
     */
    public void addPoint(GraphJEdge jEdge, Point2D location) {
        JEdgeView jEdgeView = getJEdgeView(jEdge);
        AttributeMap jEdgeAttr = new AttributeMap();
        List<?> points = jEdgeView.addPointAt(location);
        GraphConstants.setPoints(jEdgeAttr, points);
        Map<GraphJCell,AttributeMap> change =
            new HashMap<GraphJCell,AttributeMap>();
        change.put(jEdge, jEdgeAttr);
        getModel().edit(change, null, null, null);
    }

    /**
     * Removes an intermediate point from a given j-edge, controlled by a given
     * location. The point removed is either the second point (if the location
     * is <tt>null</tt>) or the one closest to the location.
     * @param jEdge the j-edge to be modified
     * @param location the point to be removed
     */
    public void removePoint(GraphJEdge jEdge, Point2D location) {
        JEdgeView jEdgeView = getJEdgeView(jEdge);
        AttributeMap jEdgeAttr = new AttributeMap();
        List<?> points = jEdgeView.removePointAt(location);
        GraphConstants.setPoints(jEdgeAttr, points);
        Map<GraphJCell,AttributeMap> change =
            new HashMap<GraphJCell,AttributeMap>();
        change.put(jEdge, jEdgeAttr);
        getModel().edit(change, null, null, null);
    }

    /**
     * Convenience method to retrieve a j-edge view as a {@link JEdgeView}.
     * @param jEdge the JEdge for which to retrieve the JEdgeView
     * @return the JEdgeView corresponding to <code>jEdge</code>
     */
    private JEdgeView getJEdgeView(GraphJEdge jEdge) {
        return (JEdgeView) getGraphLayoutCache().getMapping(jEdge, false);
    }

    /**
     * Resets the label position of a given a given j-edge to the default
     * position.
     * @param jEdge the j-edge to be modified
     */
    public void resetLabelPosition(GraphJEdge jEdge) {
        AttributeMap newAttr = new AttributeMap();
        GraphConstants.setLabelPosition(newAttr,
            JCellLayout.defaultLabelPosition);
        Map<GraphJCell,AttributeMap> change =
            new HashMap<GraphJCell,AttributeMap>();
        change.put(jEdge, newAttr);
        getModel().edit(change, null, null, null);
    }

    /**
     * Sets the line style of a given a given j-edge to a given value.
     * @param jEdge the j-edge to be modified
     * @param lineStyle the new line style for <tt>jEdge</tt>
     */
    public void setLineStyle(GraphJEdge jEdge, int lineStyle) {
        AttributeMap newAttr = new AttributeMap();
        GraphConstants.setLineStyle(newAttr, lineStyle);
        Map<GraphJCell,AttributeMap> change =
            new HashMap<GraphJCell,AttributeMap>();
        change.put(jEdge, newAttr);
        getModel().edit(change, null, null, null);
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
        if (this.startEditingNewNode) {
            setInserting(true);
        }
        getModel().insert(insert, null, null, null, null);
        setSelectionCell(jVertex);
        // immediately add a label, if so indicated by startEditingNewNode
        if (this.startEditingNewNode) {
            setInserting(false);
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
        if (this.startEditingNewEdge) {
            setInserting(true);
        }
        getModel().insert(insert, null, cs, null, null);
        setSelectionCell(newEdge);
        // immediately add a label
        if (this.startEditingNewEdge) {
            setInserting(false);
            startEditingAtCell(newEdge);
        }
    }

    @Override
    protected JGraphMode getDefaultMode() {
        return this.editing ? EDIT_MODE : super.getDefaultMode();
    }

    /** 
     * Indicates if the graph is in the process of inserting an element.
     * During this time (which will be concluded with a model change event)
     * some status updates can be skipped.
     */
    public boolean isInserting() {
        return this.inserting;
    }

    /** 
     * Sets the insertion mode.
     */
    private boolean setInserting(boolean inserting) {
        boolean result = this.inserting != inserting;
        if (result) {
            this.inserting = inserting;
        }
        return result;
    }

    /**
     * If the underlying model is a {@link GraphJModel},
     * selects the element corresponding to a given graph element.
     * @return {@code true} if {@code elem} occurs in the {@link GraphJModel}.
     */
    public boolean selectJCell(Element elem) {
        GraphJCell cell = null;
        if (elem instanceof Node) {
            cell = getModel().getJCellForNode((Node) elem);
        } else if (elem instanceof Edge) {
            cell = getModel().getJCellForEdge((Edge) elem);
        }
        if (cell != null) {
            if (cell instanceof AspectJEdge
                && ((AspectJEdge) cell).isSourceLabel()) {
                cell = ((AspectJEdge) cell).getSourceVertex();
            }
            setSelectionCell(cell);
        }
        return cell != null;
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
     * The (possibly {@code null}) editor with which this j-graph is associated.
     */
    private final boolean editing;

    /** The kind of graphs being displayed. */
    private final boolean forState;
    /** The role for which this {@link GraphJGraph} will display graphs. */
    private final GraphRole graphRole;
    /** Set of all labels and subtypes in the graph. */
    private TypeGraph typeGraph;
    /** Mapping from names to sub-label stores. */
    private Map<String,TypeGraph> typeGraphMap;
    /** Flag indicating that the graph is in the process of inserting an element. */
    private boolean inserting;
    /** Map from line style names to corresponding actions. */
    private final Map<String,JCellEditAction> setLineStyleActionMap =
        new HashMap<String,JCellEditAction>();
    /** Display attributes for a nodified edge. */
    static public final JAttr.AttributeMap EDGE_NODE_ATTR;
    static {
        JAttr v = new JAttr();
        v.linewidth = 0;
        v.lineColour = Color.WHITE;
        EDGE_NODE_ATTR = v.getNodeAttrs();
    }
    /** Collection of attributes for rule nodes. */
    static public final Map<AspectKind,JAttr.AttributeMap> ASPECT_NODE_ATTR =
        new EnumMap<AspectKind,JAttr.AttributeMap>(AspectKind.class);
    /** Collection of attributes for rule edges. */
    static public final Map<AspectKind,JAttr.AttributeMap> ASPECT_EDGE_ATTR =
        new EnumMap<AspectKind,JAttr.AttributeMap>(AspectKind.class);

    static {
        for (AspectKind aspect : EnumSet.allOf(AspectKind.class)) {
            /** Object to collect the attributes. */
            JAttr v = new JAttr();
            switch (aspect) {
            case REMARK:
                v.foreColour = Colors.findColor("255 140 0");
                v.backColour = Colors.findColor("255 255 180");
                break;
            case EMBARGO:
                v.foreColour = Color.red;
                v.backColour = null;
                v.linewidth = 5;
                v.dash = new float[] {2, 2};
                v.endFill = false;
                break;
            case CONNECT:
                v.foreColour = Color.red;
                v.backColour = null;
                v.linewidth = 4;
                v.dash = new float[] {2, 4};
                v.endFill = false;
                v.lineEnd = GraphConstants.ARROW_NONE;
                break;
            case ERASER:
                v.foreColour = Color.blue;
                v.backColour = Colors.findColor("200 240 255");
                v.dash = new float[] {4, 4};
                break;
            case CREATOR:
                v.foreColour = Color.green.darker();
                v.backColour = null;
                v.linewidth = 3;
                break;
            case ADDER:
                v.foreColour = Color.green.darker();
                v.backColour = null;
                v.linewidth = 6;
                v.dash = new float[] {2, 2};
                v.endFill = false;
                break;
            case FORALL:
            case FORALL_POS:
            case EXISTS:
            case EXISTS_OPT:
            case NESTED:
                v.dash = JAttr.NESTED_DASH;
                v.lineEnd = GraphConstants.ARROW_SIMPLE;
                v.endSize = GraphConstants.DEFAULTDECORATIONSIZE - 2;
                break;
            case SUBTYPE:
                v.lineEnd = GraphConstants.ARROW_TECHNICAL;
                v.endFill = false;
                v.endSize = GraphConstants.DEFAULTDECORATIONSIZE + 5;
                break;
            case ABSTRACT:
                v.dash = new float[] {6.0f, 2.0f};
                v.font = JAttr.ITALIC_FONT;
                break;
            }

            AspectJGraph.ASPECT_NODE_ATTR.put(aspect, v.getNodeAttrs());
            AspectJGraph.ASPECT_EDGE_ATTR.put(aspect, v.getEdgeAttrs());
        }
        // special formatting for ADDER
        AspectJGraph.ASPECT_NODE_ATTR.get(ADDER).put("line2map",
            AspectJGraph.ASPECT_NODE_ATTR.get(CREATOR));
        AspectJGraph.ASPECT_EDGE_ATTR.get(ADDER).put("line2map",
            AspectJGraph.ASPECT_EDGE_ATTR.get(CREATOR));
    }

    /**
     * Abstract class for j-cell edit actions.
     */
    private abstract class JCellEditAction extends AbstractAction implements
            GraphSelectionListener {
        /**
         * Constructs an edit action that is enabled for all j-cells.
         * @param name the name of the action
         */
        protected JCellEditAction(String name) {
            super(name);
            this.allCells = true;
            this.vertexOnly = true;
            this.jCells = new ArrayList<GraphJCell>();
            refresh();
            addGraphSelectionListener(this);
        }

        /**
         * Constructs an edit action that is enabled for only j-vertices or
         * j-edges.
         * @param name the name of the action
         * @param vertexOnly <tt>true</tt> if the action is for j-vertices only
         */
        protected JCellEditAction(String name, boolean vertexOnly) {
            super(name);
            this.allCells = false;
            this.vertexOnly = vertexOnly;
            this.jCells = new ArrayList<GraphJCell>();
            refresh();
            addGraphSelectionListener(this);
        }

        /**
         * Sets the j-cell to the first selected cell. Disables the action if
         * the type of the cell disagrees with the expected type.
         */
        public void valueChanged(GraphSelectionEvent e) {
            refresh();
        }

        private void refresh() {
            this.jCell = null;
            this.jCells.clear();
            for (Object cell : AspectJGraph.this.getSelectionCells()) {
                GraphJCell jCell = (GraphJCell) cell;
                if (this.allCells
                    || this.vertexOnly == (jCell instanceof GraphJVertex)) {
                    this.jCell = jCell;
                    this.jCells.add(jCell);
                }
            }
            this.setEnabled(this.jCell != null);
        }

        /**
         * Sets the location attribute of this action.
         */
        public void setLocation(Point2D location) {
            this.location = location;
        }

        /**
         * Switch indication that the action is enabled for all types of
         * j-cells.
         */
        protected final boolean allCells;
        /** Switch indication that the action is enabled for all j-vertices. */
        protected final boolean vertexOnly;
        /** The first currently selected j-cell of the right type. */
        protected GraphJCell jCell;
        /** List list of currently selected j-cells of the right type. */
        protected final List<GraphJCell> jCells;
        /** The currently set point location. */
        protected Point2D location;
    }

    /**
     * Initialises and returns an action to add a point to the currently selected j-edge.
     */
    public JCellEditAction getAddPointAction(Point atPoint) {
        if (this.addPointAction == null) {
            this.addPointAction = new AddPointAction();
            addAccelerator(this.addPointAction);
        }
        this.addPointAction.setLocation(atPoint);
        return this.addPointAction;
    }

    /** The permanent AddPointAction associated with this j-graph. */
    private AddPointAction addPointAction;

    /**
     * Action to add a point to the currently selected j-edge.
     */
    private class AddPointAction extends JCellEditAction {
        /** Constructs an instance of the action. */
        AddPointAction() {
            super(Options.ADD_POINT_ACTION, false);
            putValue(ACCELERATOR_KEY, Options.ADD_POINT_KEY);
        }

        @Override
        public boolean isEnabled() {
            return this.jCells.size() == 1;
        }

        public void actionPerformed(ActionEvent evt) {
            addPoint((GraphJEdge) this.jCell, this.location);
        }
    }

    /**
     * @return an action to edit the currently selected j-cell label.
     */
    public JCellEditAction getEditLabelAction() {
        if (this.editLabelAction == null) {
            this.editLabelAction = new EditLabelAction();
            addAccelerator(this.editLabelAction);
        }
        return this.editLabelAction;
    }

    /**
     * The permanent EditLabelAction associated with this j-graph.
     */
    private EditLabelAction editLabelAction;

    /**
     * Action to edit the label of the currently selected j-cell.
     */
    private class EditLabelAction extends JCellEditAction {
        /** Constructs an instance of the action. */
        EditLabelAction() {
            super(Options.EDIT_LABEL_ACTION);
            putValue(ACCELERATOR_KEY, Options.RENAME_KEY);
        }

        public void actionPerformed(ActionEvent evt) {
            startEditingAtCell(this.jCell);
        }
    }

    /**
     * Initialises and returns an action to remove a point from the currently selected j-edge.
     */
    public JCellEditAction getRemovePointAction(Point atPoint) {
        if (this.removePointAction == null) {
            this.removePointAction = new RemovePointAction();
            addAccelerator(this.removePointAction);
        }
        this.removePointAction.setLocation(atPoint);
        return this.removePointAction;
    }

    /**
     * The permanent RemovePointAction associated with this j-graph.
     */
    private RemovePointAction removePointAction;

    /**
     * Action to remove a point from the currently selected j-edge.
     */
    private class RemovePointAction extends JCellEditAction {
        /** Constructs an instance of the action. */
        RemovePointAction() {
            super(Options.REMOVE_POINT_ACTION, false);
            putValue(ACCELERATOR_KEY, Options.REMOVE_POINT_KEY);
        }

        @Override
        public boolean isEnabled() {
            return this.jCells.size() == 1;
        }

        public void actionPerformed(ActionEvent evt) {
            removePoint((GraphJEdge) this.jCell, this.location);
        }
    }

    /**
     * @return an action to reset the label position of the currently selected
     *         j-edge.
     */
    public JCellEditAction getResetLabelPositionAction() {
        if (this.resetLabelPositionAction == null) {
            this.resetLabelPositionAction = new ResetLabelPositionAction();
        }
        return this.resetLabelPositionAction;
    }

    /**
     * The permanent ResetLabelPositionAction associated with this j-graph.
     */
    private ResetLabelPositionAction resetLabelPositionAction;

    /**
     * Action set the label of the currently selected j-cell to its default
     * position.
     */
    private class ResetLabelPositionAction extends JCellEditAction {
        /** Constructs an instance of the action. */
        ResetLabelPositionAction() {
            super(Options.RESET_LABEL_POSITION_ACTION, false);
        }

        public void actionPerformed(ActionEvent evt) {
            for (GraphJCell jCell : this.jCells) {
                resetLabelPosition((GraphJEdge) jCell);
            }
        }
    }

    /**
     * @param lineStyle the lineStyle for which to get the set-action
     * @return an action to set the line style of the currently selected j-edge.
     */
    public JCellEditAction getSetLineStyleAction(int lineStyle) {
        JCellEditAction result =
            this.setLineStyleActionMap.get(Options.getLineStyleName(lineStyle));
        if (result == null) {
            this.setLineStyleActionMap.put(Options.getLineStyleName(lineStyle),
                result = new SetLineStyleAction(lineStyle));
            addAccelerator(result);
        }
        return result;
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
            for (GraphJCell jCell : this.jCells) {
                GraphJEdge jEdge = (GraphJEdge) jCell;
                setLineStyle(jEdge, this.lineStyle);
                List<?> points =
                    GraphConstants.getPoints(jCell.getAttributes());
                if (points == null || points.size() == 2) {
                    addPoint(jEdge, this.location);
                }
            }
        }

        /** The line style set by this action instance. */
        protected final int lineStyle;
    }

    /**
     * Creates and returns a fresh line style menu for this j-graph.
     */
    public JMenu createLineStyleMenu() {
        JMenu result = new SetLineStyleMenu();
        return result;
    }

    /**
     * Menu offering a choice of line style setting actions.
     */
    private class SetLineStyleMenu extends JMenu implements
            GraphSelectionListener {
        /** Constructs an instance of the action. */
        SetLineStyleMenu() {
            super(Options.SET_LINE_STYLE_MENU);
            valueChanged(null);
            addGraphSelectionListener(this);
            // initialize the line style menu
            add(getSetLineStyleAction(GraphConstants.STYLE_ORTHOGONAL));
            add(getSetLineStyleAction(GraphConstants.STYLE_SPLINE));
            add(getSetLineStyleAction(GraphConstants.STYLE_BEZIER));
            add(getSetLineStyleAction(JAttr.STYLE_MANHATTAN));
        }

        public void valueChanged(GraphSelectionEvent e) {
            this.setEnabled(getSelectionCell() instanceof GraphJEdge);
        }
    }

    private GraphModelListener getRefreshGraphListener() {
        if (this.refreshListener == null) {
            this.refreshListener = new RefreshGraphListener();
        }
        return this.refreshListener;
    }

    private GraphModelListener refreshListener;

    /** 
     * Repaints the graph on a model change.
     */
    private class RefreshGraphListener implements GraphModelListener {
        @Override
        public void graphChanged(GraphModelEvent e) {
            refresh();
        }
    }
}