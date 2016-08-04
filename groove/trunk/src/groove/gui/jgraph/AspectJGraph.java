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
package groove.gui.jgraph;

import static groove.gui.Options.SHOW_ASPECTS_OPTION;
import static groove.gui.Options.SHOW_VALUE_NODES_OPTION;
import static groove.gui.jgraph.JGraphMode.EDIT_MODE;
import static groove.gui.jgraph.JGraphMode.PREVIEW_MODE;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.accessibility.AccessibleState;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;

import org.jgraph.event.GraphModelEvent;
import org.jgraph.event.GraphModelListener;
import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;
import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphModel;
import org.jgraph.graph.PortView;

import groove.grammar.aspect.AspectEdge;
import groove.grammar.aspect.AspectGraph;
import groove.grammar.aspect.AspectNode;
import groove.grammar.model.GrammarModel;
import groove.grammar.model.ResourceKind;
import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.GraphRole;
import groove.graph.Node;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.gui.display.DisplayKind;
import groove.gui.look.VisualKey;
import groove.gui.look.VisualMap;
import groove.gui.menu.MyJMenu;
import groove.gui.tree.RuleLevelTree;
import groove.util.line.LineStyle;

/**
 * Extension of {@link JGraph} for {@link AspectGraph}s.
 */
final public class AspectJGraph extends JGraph<AspectGraph> {
    /**
     * Creates a new instance, for a given graph role.
     * A flag determines whether the graph is editable.
     * @param kind display kind on which this JGraph will be showing
     * @param editing if {@code true}, the graph is editable
     */
    public AspectJGraph(Simulator simulator, DisplayKind kind, boolean editing) {
        super(simulator);
        this.editing = editing;
        this.forState = kind == DisplayKind.STATE;
        this.graphRole = this.forState ? GraphRole.HOST : kind.getGraphRole();
        setEditable(editing);
        getGraphLayoutCache().setSelectsLocalInsertedCells(editing);
        setCloneable(editing);
        setConnectable(editing);
        setDisconnectable(editing);
    }

    @Override
    protected void installListeners() {
        super.installListeners();
        addOptionListener(SHOW_ASPECTS_OPTION);
        addOptionListener(SHOW_VALUE_NODES_OPTION);
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
        AspectJModel result = (AspectJModel) super.newModel();
        GrammarModel grammar = getGrammar();
        if (grammar == null) {
            assert getSimulatorModel() != null : "Can't create AspectJGraphs without grammar model";
            grammar = getSimulatorModel().getGrammar();
        }
        result.setGrammar(grammar);
        return result;
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
        return hasActiveEditor() || getOptionValue(Options.SHOW_VALUE_NODES_OPTION);
    }

    /* Makes sure the JGraph is rebuilt rather than just refreshed, if necessary. */
    @Override
    protected RefreshListener getRefreshListener(String option) {
        if (option.equals(Options.SHOW_BIDIRECTIONAL_EDGES_OPTION)) {
            return new RebuildListener();
        } else {
            return super.getRefreshListener(option);
        }
    }

    /** Sets a level tree for this JGraph. */
    public void setLevelTree(RuleLevelTree levelTree) {
        assert levelTree == null || getGraphRole() == GraphRole.RULE && !hasActiveEditor();
        this.levelTree = levelTree;
    }

    /**
     * Lazily creates and returns the rule level tree associated
     * with this JGraph, if any.
     */
    public RuleLevelTree getLevelTree() {
        return this.levelTree;
    }

    /** Indicates that the JModel has an editor enabled. */
    public boolean hasActiveEditor() {
        return this.editing && getMode() != PREVIEW_MODE;
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
    @Override
    public GraphRole getGraphRole() {
        return this.graphRole;
    }

    @Override
    public JMenu createPopupMenu(Point atPoint) {
        MyJMenu result = new MyJMenu("Popup");
        switch (getGraphRole()) {
        case HOST:
            result.add(getActions().getApplyMatchAction());
            result.addSeparator();
            break;
        default:
            // do nothing
        }
        Action editAction;
        if (isForState()) {
            editAction = getActions().getEditStateAction();
        } else {
            editAction = getActions().getEditAction(ResourceKind.toResource(getGraphRole()));
        }
        result.add(editAction);
        result.addSubmenu(createEditMenu(atPoint));
        result.addSubmenu(super.createPopupMenu(atPoint));
        return result;
    }

    @Override
    public JMenu createExportMenu() {
        // add a save graph action as the first action
        MyJMenu result = new MyJMenu();
        if (getActions() != null) {
            if (isForState()) {
                result.add(getActions().getSaveStateAction());
            } else {
                ResourceKind resource = ResourceKind.toResource(getGraphRole());
                result.add(getActions().getSaveAction(resource));
                result.add(getActions().getSaveAsAction(resource));
            }
        }
        result.addMenuItems(super.createExportMenu());
        return result;
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

    @Override
    public void setEditable(boolean editable) {
        setCloneable(editable);
        setConnectable(editable);
        setDisconnectable(editable);
        super.setEditable(editable);
    }

    /** Convenience method to invoke an edit of a single visual attribute. */
    void edit(JCell<AspectGraph> jCell, VisualKey key, Object value) {
        VisualMap newVisuals = new VisualMap();
        newVisuals.put(key, value);
        edit(jCell, newVisuals);
    }

    /** Convenience method to invoke an edit of a set of visual attributes. */
    void edit(JCell<AspectGraph> jCell, VisualMap newVisuals) {
        getModel().edit(Collections.singletonMap(jCell, newVisuals.getAttributes()),
            null,
            null,
            null);
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
        AspectJVertex jVertex =
            (AspectJVertex) getModel().createJVertex(getModel().createAspectNode());
        jVertex.setNodeFixed();
        jVertex.putVisual(VisualKey.NODE_POS, atPoint);
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
        PortView fromPortView = getPortViewAt(screenFrom.getX(), screenFrom.getY());
        assert fromPortView != null; // should be guaranteed by caller
        Point2D from = fromPortView.getLocation();
        PortView toPortView = getPortViewAt(screenTo.getX(), screenTo.getY());
        Point2D to;
        // if toPortView is null, we're drawing a self-edge
        if (toPortView == null) {
            toPortView = fromPortView;
            to = screenTo;
        } else {
            to = toPortView.getLocation();
        }
        assert fromPortView != null : "addEdge should not be called with dangling source " + from;
        DefaultPort fromPort = (DefaultPort) fromPortView.getCell();
        DefaultPort toPort = (DefaultPort) toPortView.getCell();
        // define the edge to be inserted
        AspectJEdge newEdge = (AspectJEdge) getModel().createJEdge(null);
        // add a single, empty label so the edge will be displayed
        newEdge.getUserObject()
            .add("");
        // to make sure there is at least one graph edge wrapped by this JEdge,
        // we add a dummy edge label to the JEdge's user object
        Object[] insert = new Object[] {newEdge};
        // define connections between edge and nodes, if any
        ConnectionSet cs = new ConnectionSet();
        cs.connect(newEdge, fromPort, true);
        cs.connect(newEdge, toPort, false);
        // if we're drawing a self-edge, provide some intermediate points
        List<Point2D> points;
        if (toPort == fromPort) {
            points = Arrays.asList(from, to, to);
        } else {
            points = Arrays.asList(from, to);
        }
        newEdge.putVisual(VisualKey.POINTS, points);
        // add the cell to the jGraph
        getModel().insert(insert, null, cs, null, null);
        setSelectionCell(newEdge);
        // immediately add a label
        if (this.startEditingNewEdge) {
            startEditingAtCell(newEdge);
        }
    }

    @Override
    protected JGraphMode getDefaultMode() {
        return this.editing ? EDIT_MODE : super.getDefaultMode();
    }

    /**
     * If the underlying model is a {@link JModel},
     * selects the element corresponding to a given graph element.
     * @return {@code true} if {@code elem} occurs in the {@link JModel}.
     */
    public boolean selectJCell(Element elem) {
        JCell<?> cell = null;
        if (elem instanceof Node) {
            cell = getModel().getJCellForNode((Node) elem);
        } else if (elem instanceof Edge) {
            cell = getModel().getJCellForEdge((Edge) elem);
        }
        if (cell != null) {
            if (cell instanceof AspectJEdge && ((AspectJEdge) cell).isSourceLabel()) {
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
    /** The role for which this {@link JGraph} will display graphs. */
    private final GraphRole graphRole;
    /** The JTree of rule levels, if any. */
    private RuleLevelTree levelTree;
    /** Map from line style names to corresponding actions. */
    private final Map<LineStyle,JCellEditAction> setLineStyleActionMap =
        new EnumMap<>(LineStyle.class);

    /** Returns the grammar that has manually been set for this JGraph. */
    public GrammarModel getGrammar() {
        return this.grammar;
    }

    /** Manually sets a new grammar in this JGraph.
     * This should only be done if there is no underlying simulator.
     * @param grammar the grammar to be used.
     */
    public void setGrammar(GrammarModel grammar) {
        assert getSimulatorModel() == null;
        this.grammar = grammar;
    }

    private GrammarModel grammar;

    /**
     * Abstract class for j-cell edit actions.
     */
    private abstract class JCellEditAction extends AbstractAction
        implements GraphSelectionListener {
        /**
         * Constructs an edit action that is enabled for all j-cells.
         * @param name the name of the action
         */
        protected JCellEditAction(String name) {
            super(name);
            this.allCells = true;
            this.vertexOnly = true;
            this.jCells = new ArrayList<>();
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
            this.jCells = new ArrayList<>();
            refresh();
            addGraphSelectionListener(this);
        }

        /**
         * Sets the j-cell to the first selected cell. Disables the action if
         * the type of the cell disagrees with the expected type.
         */
        @Override
        public void valueChanged(GraphSelectionEvent e) {
            refresh();
        }

        private void refresh() {
            this.jCell = null;
            this.jCells.clear();
            for (Object cell : AspectJGraph.this.getSelectionCells()) {
                AspectJCell jCell = (AspectJCell) cell;
                if (this.allCells || this.vertexOnly == (jCell instanceof JVertex)) {
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
         * Adds a point at a given location to the underlying j-edge. The point is
         * added between those two existing (adjacent) edge points for which the sum
         * of the distances to the specified location is minimal. If the location is
         * <tt>null</tt>,{@link #createPointBetween} is invoked instead. Does not
         * update the view; this is to be done by the client.
         * @param location the location at which the new point should appear; if
         *        <tt>null</tt>, a point is added at random
         * @return a copy of the points of the underlying j-edge with a point added
         */
        protected List<Point2D> addPointAt(List<Point2D> points, Point2D location) {
            List<Point2D> result = new LinkedList<>(points);
            if (location == null) {
                result.add(1, createPointBetween(result.get(0), result.get(1)));
            } else {
                int closestIndex = getClosestIndex(result, location);
                assert closestIndex > 0;
                result.add(closestIndex, (Point) location.clone());
            }
            return result;
        }

        /**
         * Returns the positive index in a non-empty list of points of that
         * point which is closest to a given location.
         * @param location the location to which distances are measured.
         * @param points the list in which the index is sought
         * @return the index of the point (from position 1) closest to the location
         */
        protected int getClosestIndex(List<Point2D> points, Point2D location) {
            int result = 0;
            double closestDistance = Double.MAX_VALUE;
            for (int i = 1; i < points.size(); i++) {
                double distance =
                    location.distance(points.get(i - 1)) + location.distance(points.get(i));
                if (distance < closestDistance) {
                    result = i;
                    closestDistance = distance;
                }
            }
            return result;
        }

        /**
         * Creates an returns a point halfway two given points, with a random effect
         * @param p1 the first boundary point
         * @param p2 the first boundary point
         * @return new point on the perpendicular of the line between <tt>p1</tt>
         *         and <tt>p2</tt>
         */
        private Point createPointBetween(Point2D p1, Point2D p2) {
            double distance = p1.distance(p2);
            int midX = (int) (p1.getX() + p2.getX()) / 2;
            int midY = (int) (p1.getY() + p2.getY()) / 2;
            // int offset = (int) (5 + distance / 2 + 20 * Math.random());
            int x, y;
            if (distance == 0) {
                x = midX + 20;
                y = midY + 20;
            } else {
                int offset = (int) (5 + distance / 4);
                double xDelta = p1.getX() - p2.getX();
                double yDelta = p1.getY() - p2.getY();
                x = midX + (int) (offset * yDelta / distance);
                y = midY - (int) (offset * xDelta / distance);
            }
            return new Point(Math.max(x, 0), Math.max(y, 0));
        }

        /**
         * Switch indication that the action is enabled for all types of
         * j-cells.
         */
        protected final boolean allCells;
        /** Switch indication that the action is enabled for all j-vertices. */
        protected final boolean vertexOnly;
        /** The first currently selected j-cell of the right type. */
        protected AspectJCell jCell;
        /** List list of currently selected j-cells of the right type. */
        protected final List<AspectJCell> jCells;
        /** The currently set point location. */
        protected Point2D location;
    }

    /**
     * Initialises and returns an action to add a point to the currently selected j-edge.
     */
    public AddPointAction getAddPointAction(Point atPoint) {
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
     * Action to add an intermediate point to a JEdge.
     */
    public class AddPointAction extends JCellEditAction {
        /** Constructs an instance of the action. */
        AddPointAction() {
            super(Options.ADD_POINT_ACTION, false);
            putValue(ACCELERATOR_KEY, Options.ADD_POINT_KEY);
        }

        @Override
        public boolean isEnabled() {
            return this.jCells.size() == 1;
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            execute(this.jCell);
        }

        /** Executes the action. */
        public void execute(AspectJCell jCell) {
            VisualMap visuals = jCell.getVisuals();
            List<Point2D> points = addPointAt(visuals.getPoints(), this.location);
            edit(jCell, VisualKey.POINTS, points);
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

        @Override
        public void actionPerformed(ActionEvent evt) {
            startEditingAtCell(this.jCell);
        }
    }

    /**
     * Initialises and returns an action to remove a point from the currently selected j-edge.
     */
    public RemovePointAction getRemovePointAction(Point atPoint) {
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
    public class RemovePointAction extends JCellEditAction {
        /** Constructs an instance of the action. */
        RemovePointAction() {
            super(Options.REMOVE_POINT_ACTION, false);
            putValue(ACCELERATOR_KEY, Options.REMOVE_POINT_KEY);
        }

        @Override
        public boolean isEnabled() {
            return this.jCells.size() == 1;
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            execute(this.jCell);
        }

        /**
         * Removes an intermediate point from a given j-edge, controlled by a given
         * location. The point removed is either the second point (if the location
         * is <tt>null</tt>) or the one closest to the location.
         * @param jEdge the j-edge to be modified
         */
        public void execute(AspectJCell jEdge) {
            VisualMap visuals = jEdge.getVisuals();
            List<Point2D> points = visuals.getPoints();
            edit(jEdge, VisualKey.POINTS, removePointAt(points, this.location));
        }

        /**
         * Removes the intermediate point from a list of points that is closest
         * to a given location. Has no effect if the list had only two points to
         * start with, or if it is a loop. If
         * the location is <tt>null</tt>, the point at index 1 is removed
         * @param location the location at which the point to be removed is sought;
         *        if <tt>null</tt>, the first available point is removed
         * @return a copy of the points, possibly with a
         *         point removed
         */
        private List<Point2D> removePointAt(List<Point2D> points, Point2D location) {
            LinkedList<Point2D> result = new LinkedList<>(points);
            if (result.size() > 2 && (!result.getFirst()
                .equals(result.getLast()) || result.size() > 3)) {
                int ix = location == null ? 1 : getClosestIndex(points, location);
                result.remove(ix);
            }
            return result;
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

        /** Resets the label positions of the selected cells. */
        @Override
        public void actionPerformed(ActionEvent evt) {
            for (AspectJCell jCell : this.jCells) {
                execute(jCell);
            }
        }

        /**
         * Resets the label position of a given a given j-edge to the default
         * position.
         * @param jEdge the j-edge to be modified
         */
        public void execute(AspectJCell jEdge) {
            edit(jEdge, VisualKey.LABEL_POS, VisualKey.LABEL_POS.getDefaultValue());
        }
    }

    /**
     * @param lineStyle the lineStyle for which to get the set-action
     * @return an action to set the line style of the currently selected j-edge.
     */
    public JCellEditAction getSetLineStyleAction(LineStyle lineStyle) {
        JCellEditAction result = this.setLineStyleActionMap.get(lineStyle);
        if (result == null) {
            this.setLineStyleActionMap.put(lineStyle, result = new SetLineStyleAction(lineStyle));
            addAccelerator(result);
        }
        return result;
    }

    /**
     * Action to set the line style of the currently selected j-edge.
     */
    private class SetLineStyleAction extends JCellEditAction {
        /** Constructs an instance of the action, for a given line style. */
        SetLineStyleAction(LineStyle lineStyle) {
            super(lineStyle.getName(), false);
            putValue(ACCELERATOR_KEY, lineStyle.getKey());
            this.lineStyle = lineStyle;
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            VisualMap newVisuals = new VisualMap();
            for (AspectJCell jCell : this.jCells) {
                VisualMap visuals = jCell.getVisuals();
                newVisuals.setLineStyle(this.lineStyle);
                List<Point2D> points = visuals.getPoints();
                if (points.size() == 2) {
                    points = addPointAt(points, this.location);
                    newVisuals.put(VisualKey.POINTS, points);
                }
                edit(jCell, newVisuals);
            }
        }

        /** The line style set by this action instance. */
        protected final LineStyle lineStyle;
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
    private class SetLineStyleMenu extends JMenu implements GraphSelectionListener {
        /** Constructs an instance of the action. */
        SetLineStyleMenu() {
            super(Options.SET_LINE_STYLE_MENU);
            valueChanged(null);
            addGraphSelectionListener(this);
            // initialise the line style menu
            for (LineStyle lineStyle : LineStyle.values()) {
                add(getSetLineStyleAction(lineStyle));
            }
        }

        @Override
        public void valueChanged(GraphSelectionEvent e) {
            this.setEnabled(getSelectionCell() instanceof JEdge);
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

    /**
     * Special listener for the show bidirectional edges option, for which a
     * refresh is not enough, but a rebuild is required.
     */
    private class RebuildListener extends RefreshListener {
        @Override
        public void itemStateChanged(ItemEvent e) {
            if (isEnabled()) {
                rebuild();
            }
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName()
                .equals(AccessibleState.ENABLED.toDisplayString()) && isEnabled()) {
                rebuild();
            }
        }

        /**
         * Rebuilds the underlying {@link AspectJGraph} from its underlying graph,
         * and then refreshes. This is necessary when the 'showBidirectionalEdges'
         * option is changed.
         */
        private void rebuild() {
            AspectJModel oldModel = getModel();
            AspectJModel newModel = oldModel.cloneWithNewGraph(oldModel.getGraph());
            setModel(newModel);
        }
    }

    @Override
    protected JGraphFactory<AspectGraph> createFactory() {
        return new MyFactory();
    }

    private class MyFactory extends JGraphFactory<AspectGraph> {
        public MyFactory() {
            super(AspectJGraph.this);
        }

        @Override
        public AspectJGraph getJGraph() {
            return (AspectJGraph) super.getJGraph();
        }

        @Override
        public AspectJVertex newJVertex(Node node) {
            assert node instanceof AspectNode;
            return AspectJVertex.newInstance();
        }

        @Override
        public AspectJEdge newJEdge(Edge edge) {
            assert edge == null || edge instanceof AspectEdge;
            return AspectJEdge.newInstance();
        }

        @Override
        public AspectJModel newModel() {
            return new AspectJModel(getJGraph());
        }
    }
}