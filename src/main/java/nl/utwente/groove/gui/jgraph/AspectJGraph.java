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
import org.eclipse.jdt.annotation.Nullable;
import org.jgraph.event.GraphModelEvent;
import org.jgraph.event.GraphModelListener;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphModel;
import org.jgraph.graph.PortView;

import nl.utwente.groove.gui.view.GraphViewMode;
import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.graph.Edge;
import nl.utwente.groove.graph.Element;
import nl.utwente.groove.gui.Options;
import nl.utwente.groove.gui.view.AspectGraphCanvas;
import nl.utwente.groove.gui.view.AspectGraphViewController;
import nl.utwente.groove.gui.view.AspectGraphViewModel;
import nl.utwente.groove.gui.view.CellStore.Connection;
import nl.utwente.groove.gui.view.AspectViewEdge;
import nl.utwente.groove.gui.view.CellStore;
import nl.utwente.groove.gui.view.cell.AspectEdgeCell;
import nl.utwente.groove.gui.view.cell.AspectVertexCell;
import nl.utwente.groove.grammar.model.GraphBasedModel;
import nl.utwente.groove.grammar.type.TypeGraph;
import nl.utwente.groove.gui.look.VisualKey;
import nl.utwente.groove.gui.view.AspectViewCell;
import nl.utwente.groove.gui.view.OptionRefreshListener;

/**
 * Extension of {@link JGraph} for {@link AspectGraph}s.
 */
public class AspectJGraph extends JGraph<@NonNull AspectGraph> implements AspectGraphCanvas {
    /**
     * Creates a new instance as the canvas of a given controller,
     * which determines the graph role and whether the graph is editable.
     */
    public AspectJGraph(AspectGraphViewController controller) {
        super(controller);
        boolean editing = controller.isEditing();
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
    public @Nullable AspectGraphViewModel getViewModel() {
        var model = getModel();
        return model == null
            ? null
            : model.getViewModel();
    }

    @Override
    public AspectGraphViewModel newViewModel() {
        return newModel().getViewModel();
    }

    @Override
    public AspectGraphViewModel showGraph(AspectGraph graph) {
        return (AspectGraphViewModel) super.showGraph(graph);
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
    AspectGraphViewModel createViewModel(CellStore<@NonNull AspectGraph> store) {
        return new AspectGraphViewModel(getController(), store);
    }

    @Override
    public AspectJModel newModel() {
        AspectJModel result = new AspectJModel(this);
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
        return getController().isEditing() && getMode() != PREVIEW_MODE;
    }

    @Override
    public void setEditable(boolean editable) {
        setCloneable(editable);
        setConnectable(editable);
        // reconnecting an edge by dragging its end is not an edit of the view model
        setDisconnectable(false);
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
        var viewModel = model.getViewModel();
        AspectVertexCell vertex = viewModel.newVertex(viewModel.createAspectNode());
        vertex.setNodeFixed();
        vertex.putVisual(VisualKey.NODE_POS, atPoint);
        // add the cell through the view model, which records the edit
        viewModel.insert(List.of(vertex), List.of(), List.of());
        var jVertex = JCell.of(vertex);
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
        var source = ((JVertex<?>) fromPort.getParent()).getViewCell();
        var target = ((JVertex<?>) toPort.getParent()).getViewCell();
        // define the edge to be inserted
        AspectEdgeCell edge = model.getViewModel().newEdge(null);
        // add a single, empty label so the edge will be displayed
        edge.getEditableLabels().add("");
        // if we're drawing a self-edge, provide some intermediate points
        List<Point2D> points;
        if (toPort == fromPort) {
            points = Arrays.asList(from, to, to);
        } else {
            points = Arrays.asList(from, to);
        }
        edge.putVisual(VisualKey.POINTS, points);
        // add the cell through the view model, which records the edit
        model
            .getViewModel()
            .insert(List.of(), List.of(edge),
                    List
                        .of(new Connection<>(edge, (AspectVertexCell) source,
                            (AspectVertexCell) target)));
        var newEdge = JCell.of(edge);
        setSelectionCell(newEdge);
        // immediately add a label
        if (this.startEditingNewEdge) {
            startEditingAtCell(newEdge);
        }
    }

    @Override
    public GraphViewMode getDefaultMode() {
        return getController().isEditing()
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
            } else if (errorCell instanceof AspectViewEdge e && e.isSourceLabel()) {
                errorCell = e.getSourceVertex();
            }
            if (errorCell != null) {
                errorCells.add(errorCell);
            }
        }
        if (!errorCells.isEmpty()) {
            setSelectionCells(JCell.items(errorCells));
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

}