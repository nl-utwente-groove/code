/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
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
package nl.utwente.groove.gui.jgraph;

import static nl.utwente.groove.gui.Options.SHOW_ASPECTS_OPTION;
import static nl.utwente.groove.gui.Options.SHOW_VALUE_NODES_OPTION;
import static nl.utwente.groove.gui.view.GraphViewMode.EDIT_MODE;
import static nl.utwente.groove.gui.view.GraphViewMode.PREVIEW_MODE;

import java.awt.event.ItemEvent;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.accessibility.AccessibleState;

import org.eclipse.jdt.annotation.NonNull;
import org.jgraph.event.GraphModelEvent;
import org.jgraph.event.GraphModelListener;
import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphModel;
import org.jgraph.graph.PortView;

import nl.utwente.groove.gui.view.GraphViewMode;
import nl.utwente.groove.grammar.aspect.AspectEdge;
import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.aspect.AspectNode;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.graph.Edge;
import nl.utwente.groove.graph.Element;
import nl.utwente.groove.graph.GraphRole;
import nl.utwente.groove.graph.Node;
import nl.utwente.groove.gui.Options;
import nl.utwente.groove.gui.Simulator;
import nl.utwente.groove.gui.view.AspectGraphCanvas;
import nl.utwente.groove.gui.view.AspectGraphViewController;
import nl.utwente.groove.grammar.model.GraphBasedModel;
import nl.utwente.groove.grammar.type.TypeGraph;
import nl.utwente.groove.gui.display.DisplayKind;
import nl.utwente.groove.gui.look.VisualKey;
import nl.utwente.groove.gui.view.AspectViewCell;
import nl.utwente.groove.gui.view.OptionRefreshListener;

/**
 * Extension of {@link JGraph} for {@link AspectGraph}s.
 */
public class AspectJGraph extends JGraph<@NonNull AspectGraph> implements AspectGraphCanvas {
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
        this.graphRole = this.forState
            ? GraphRole.HOST
            : kind.getGraphRole();
        setEditable(editing);
        getGraphLayoutCache().setSelectsLocalInsertedCells(editing);
        setCloneable(editing);
        setConnectable(editing);
        setDisconnectable(editing);
    }

    @Override
    protected void installListeners() {
        super.installListeners();
        var actions = getActions();
        if (actions != null) {
            addCanvasListener(actions.getSelectColorAction());
        }
        addOptionListener(SHOW_ASPECTS_OPTION);
        addOptionListener(SHOW_VALUE_NODES_OPTION);
    }

    @Override
    public void removeListeners() {
        super.removeListeners();
        var actions = getActions();
        if (actions != null) {
            removeCanvasListener(actions.getSelectColorAction());
        }
    }

    @Override
    public void setModel(GraphModel model) {
        AspectJModel oldModel = getModel();
        if (oldModel != null) {
            oldModel.removeGraphModelListener(getRefreshGraphListener());
            oldModel.removeGraphChangeListener(this.graphChangeListener);
        }
        super.setModel(model);
        if (model instanceof AspectJModel newModel) {
            newModel.addGraphModelListener(getRefreshGraphListener());
            newModel.addGraphChangeListener(this.graphChangeListener);
        }
    }

    /** Forwards the graph rebuilds of the current model to the canvas listeners. */
    private final PropertyChangeListener graphChangeListener = evt -> notifyGraphChanged();

    @Override
    public AspectJModel getModel() {
        return (AspectJModel) super.getModel();
    }

    @Override
    public AspectJModel getNonNullModel() {
        return (AspectJModel) super.getNonNullModel();
    }

    /* Specialises the return type. */
    @Override
    public AspectGraphViewController getController() {
        return (AspectGraphViewController) super.getController();
    }

    @Override
    protected AspectGraphViewController createController(Simulator simulator) {
        return new AspectGraphViewController(this, simulator);
    }

    @Override
    public GraphBasedModel<?> getResourceModel() {
        return getNonNullModel().getResourceModel();
    }

    @Override
    public TypeGraph getTypeGraph() {
        return getNonNullModel().getTypeGraph();
    }

    @Override
    public AspectJModel newModel() {
        AspectJModel result = (AspectJModel) super.newModel();
        GrammarModel grammar = getController().getGrammar();
        if (grammar == null) {
            assert getSimulatorModel() != null : "Can't create AspectJGraphs without grammar model";
            grammar = getSimulatorModel().getGrammar();
        }
        result.setGrammar(grammar);
        return result;
    }

    /* Makes sure the JGraph is rebuilt rather than just refreshed, if necessary. */
    @Override
    public OptionRefreshListener getRefreshListener(String option) {
        if (option.equals(Options.SHOW_BIDIRECTIONAL_EDGES_OPTION)) {
            return new RebuildListener();
        } else {
            return super.getRefreshListener(option);
        }
    }

    /** Indicates that the JModel has an editor enabled. */
    @Override
    public boolean hasActiveEditor() {
        return this.editing && getMode() != PREVIEW_MODE;
    }

    /**
     * The (possibly {@code null}) editor with which this j-graph is associated.
     */
    private final boolean editing;

    /**
     * Indicates if the graph being displayed is a graph state.
     */
    @Override
    public boolean isForState() {
        return this.forState;
    }

    /** The kind of graphs being displayed. */
    private final boolean forState;

    /**
     * Returns the role of the graph being displayed.
     */
    @Override
    public GraphRole getGraphRole() {
        return this.graphRole;
    }

    /** The role for which this {@link JGraph} will display graphs. */
    private final GraphRole graphRole;

    @Override
    public void setEditable(boolean editable) {
        setCloneable(editable);
        setConnectable(editable);
        setDisconnectable(editable);
        super.setEditable(editable);
    }

    /**
     * Adds a j-vertex to the j-graph, and positions it at a given point. The
     * point is in screen coordinates
     * @param screenPoint the intended central point for the new j-vertex
     */
    void addVertex(Point2D screenPoint) {
        var model = getNonNullModel();
        stopEditing();
        Point2D atPoint = fromScreen(snap(screenPoint));
        // define the j-cell to be inserted
        AspectJVertex jVertex = (AspectJVertex) model.createJVertex(model.createAspectNode());
        jVertex.setNodeFixed();
        jVertex.putVisual(VisualKey.NODE_POS, atPoint);
        // add the cell to the jGraph
        Object[] insert = {jVertex};
        model.insert(insert, null, null, null, null);
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
        var model = getNonNullModel();
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
        AspectJEdge newEdge = (AspectJEdge) model.createJEdge(null);
        // add a single, empty label so the edge will be displayed
        newEdge.getEditableLabels().add("");
        // to make sure there is at least one graph edge wrapped by this ViewEdge,
        // we add a dummy edge label to the ViewEdge's user object
        Object[] insert = {newEdge};
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
        model.insert(insert, null, cs, null, null);
        setSelectionCell(newEdge);
        // immediately add a label
        if (this.startEditingNewEdge) {
            startEditingAtCell(newEdge);
        }
    }

    @Override
    public GraphViewMode getDefaultMode() {
        return this.editing
            ? EDIT_MODE
            : super.getDefaultMode();
    }

    /**
     * Selects the cells corresponding to a given collection of graph elements.
     */
    @Override
    public void selectElements(Collection<? extends Element> elems) {
        var model = getNonNullModel();
        var errorCells = new HashSet<AspectViewCell>();
        for (var elem : elems) {
            var errorCell = model.getJCell(elem);
            if (errorCell == null && elem instanceof Edge e) {
                errorCell = model.getJCell(e.source());
            } else if (errorCell instanceof AspectJEdge e && e.isSourceLabel()) {
                errorCell = e.getSourceVertex();
            }
            if (errorCell != null) {
                errorCells.add(errorCell);
            }
        }
        if (!errorCells.isEmpty()) {
            setSelectionCells(errorCells.toArray());
        }
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
    private class RebuildListener extends OptionRefreshListener {
        RebuildListener() {
            super(AspectJGraph.this);
        }

        @Override
        public void itemStateChanged(ItemEvent e) {
            if (isEnabled()) {
                rebuild();
            }
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals(AccessibleState.ENABLED.toDisplayString())
                && isEnabled()) {
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
            assert oldModel != null;
            AspectJModel newModel = oldModel.cloneWithNewGraph(oldModel.getGraph());
            setModel(newModel);
        }
    }

    @Override
    protected JGraphFactory<@NonNull AspectGraph> createFactory() {
        return new MyFactory();
    }

    private class MyFactory extends JGraphFactory<@NonNull AspectGraph> {
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
            return AspectJVertex.newInstance(getJGraph().getGraphRole());
        }

        @Override
        public AspectJEdge newJEdge(Edge edge) {
            assert edge == null || edge instanceof AspectEdge;
            return AspectJEdge.newInstance(getJGraph().getGraphRole());
        }

        @Override
        public AspectJModel newModel() {
            return new AspectJModel(getJGraph());
        }
    }
}