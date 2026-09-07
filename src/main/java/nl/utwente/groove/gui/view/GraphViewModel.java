/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.gui.view;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.graph.Edge;
import nl.utwente.groove.graph.Element;
import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.graph.GraphInfo;
import nl.utwente.groove.graph.Node;
import nl.utwente.groove.graph.layout.EdgeLayout;
import nl.utwente.groove.graph.layout.LayoutMap;
import nl.utwente.groove.graph.layout.NodeLayout;
import nl.utwente.groove.gui.look.VisualKey;
import nl.utwente.groove.gui.look.VisualMap;
import nl.utwente.groove.gui.view.CellStore.Connection;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.gui.view.cell.AViewEdge;
import nl.utwente.groove.gui.view.cell.AViewVertex;
import nl.utwente.groove.util.collect.NestedIterator;

/**
 * Library-independent content model of a graph view:
 * the displayed graph, its layout map, the cells that display its elements
 * (kept in a backend {@link CellStore}) and the mapping from graph elements to cells.
 * Loading a graph creates the cells and commits them to the store; role subclasses
 * add the semantics of their kind of graph.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public abstract class GraphViewModel<G extends Graph> {
    /**
     * Constructs a view model for a given controller, with a given backend cell store.
     */
    public GraphViewModel(GraphViewController<G> controller, CellStore<G> store) {
        this.controller = controller;
        this.store = store;
    }

    /** Returns the controller of the graph view. */
    public GraphViewController<G> getController() {
        return this.controller;
    }

    private final GraphViewController<G> controller;

    /** Returns the canvas of the graph view. */
    public GraphCanvas<G> getCanvas() {
        return getController().getCanvas();
    }

    /** Returns the backend store of the cells of this model. */
    public CellStore<G> getStore() {
        return this.store;
    }

    private final CellStore<G> store;

    /**
     * Returns the underlying graph of this view model.
     */
    public @Nullable G getGraph() {
        return this.graph;
    }

    /** Returns the name of the underlying graph, if any. */
    public @Nullable String getName() {
        var graph = getGraph();
        return graph == null
            ? null
            : graph.getName();
    }

    /**
     * Changes the underlying graph to the one passed in as a parameter.
     * Note that this should only be done as part of an action that also
     * changes the cells of the view model, as well as the
     * mapping from graph elements to cells.
     */
    public void setGraph(G graph) {
        this.graph = graph;
        this.layoutMap = GraphInfo.getLayoutMap(graph);
    }

    /**
     * The underlying graph of this view model.
     */
    private @Nullable G graph;

    /**
     * Returns the (non-{@code null}) layout map of the graph.
     * This is retrieved from {@link GraphInfo#getLayoutMap(Graph)}.
     */
    public LayoutMap getLayoutMap() {
        var result = this.layoutMap;
        assert result != null; // set when the graph was loaded
        return result;
    }

    /**
     * The layout map for the underlying graph.
     * Set as soon as the graph is; {@code null} before that.
     */
    private @Nullable LayoutMap layoutMap;

    /** Stores the layout of a given cell back into the layout map of the graph. */
    public void synchroniseLayout(ViewCell<G> jCell) {
        LayoutMap layoutMap = getLayoutMap();
        assert layoutMap == GraphInfo.getLayoutMap(getGraph());
        if (jCell instanceof ViewEdge) {
            for (Edge edge : jCell.getEdges()) {
                layoutMap.putEdge(edge, jCell.getVisuals().toEdgeLayout());
            }
        } else if (jCell instanceof ViewVertex) {
            layoutMap.putNode(((ViewVertex<G>) jCell).getNode(), jCell.getVisuals().toNodeLayout());
        }
    }

    // ---------- editing ----------

    /**
     * Returns the edit history of this model, if it records its edits;
     * {@code null} for a model that is only viewed.
     */
    public @Nullable EditHistory<G> getEditHistory() {
        return this.editHistory;
    }

    /**
     * Starts recording edits: from now on, the editing operations of this
     * model ({@link #insert}, {@link #remove}, {@link #changeVisuals},
     * {@link #changeLabels}) are undoable through the history.
     */
    public EditHistory<G> enableEditHistory() {
        var result = this.editHistory;
        if (result == null) {
            this.editHistory = result = new EditHistory<>(this);
        }
        return result;
    }

    private @Nullable EditHistory<G> editHistory;

    /**
     * Inserts fresh cells, connected as given, as one edit.
     * The cells are created by {@link #newVertex} and {@link #newEdge}; every
     * inserted edge must have a connection.
     */
    @AIGenerated("Claude Fable 5.1, 2026-09")
    public void insert(List<? extends ViewVertex<G>> vertices, List<? extends ViewEdge<G>> edges,
                       List<Connection<G>> connections) {
        doEdit(new GraphEdit<G>().withInserted(vertices, edges, connections));
    }

    /**
     * Removes cells as one edit, together with the edges incident to the
     * removed vertices.
     */
    @AIGenerated("Claude Fable 5.1, 2026-09")
    public void remove(Collection<? extends ViewCell<G>> cells) {
        Set<ViewCell<G>> removed = new LinkedHashSet<>(cells);
        for (var cell : cells) {
            if (cell instanceof ViewVertex<G> vertex) {
                var edges = vertex.getContext();
                while (edges.hasNext()) {
                    removed.add(edges.next());
                }
            }
        }
        List<ViewVertex<G>> vertices = new ArrayList<>();
        List<ViewEdge<G>> edges = new ArrayList<>();
        List<Connection<G>> connections = new ArrayList<>();
        for (var cell : removed) {
            if (cell instanceof ViewVertex<G> vertex) {
                vertices.add(vertex);
            } else if (cell instanceof ViewEdge<G> edge) {
                var source = edge.getSourceVertex();
                var target = edge.getTargetVertex();
                assert source != null && target != null : "Removed edge " + edge + " is not connected";
                edges.add(edge);
                connections.add(new Connection<>(edge, source, target));
            }
        }
        doEdit(new GraphEdit<G>().withRemoved(vertices, edges, connections));
    }

    /**
     * Changes visuals of cells as one edit; the funnel behind
     * {@link GraphCanvas#edit}. Only controlled keys are recorded and changed.
     */
    @AIGenerated("Claude Fable 5.1, 2026-09")
    public void changeVisuals(Map<? extends ViewCell<G>,VisualMap> changes) {
        var edit = new GraphEdit<G>();
        for (var entry : changes.entrySet()) {
            ViewCell<G> cell = entry.getKey();
            VisualMap current = cell.getVisuals();
            VisualMap oldVisuals = new VisualMap();
            VisualMap newVisuals = new VisualMap();
            for (VisualKey key : entry.getValue().keySet()) {
                if (key.getNature() == VisualKey.Nature.CONTROLLED) {
                    oldVisuals.put(key, current.get(key));
                    newVisuals.put(key, entry.getValue().get(key));
                }
            }
            if (!newVisuals.keySet().isEmpty()) {
                edit.withVisuals(cell, oldVisuals, newVisuals);
            }
        }
        doEdit(edit);
    }

    /** Changes the editable labels of a cell as one edit. */
    @AIGenerated("Claude Fable 5.1, 2026-09")
    public void changeLabels(ViewCell<G> cell, EditableLabels labels) {
        var oldLabels = new EditableLabels(getLabels(cell));
        doEdit(new GraphEdit<G>().withLabels(cell, oldLabels, new EditableLabels(labels)));
    }

    /** Returns the editable labels of a cell; only role models with editable labels support this. */
    protected EditableLabels getLabels(ViewCell<G> cell) {
        throw new UnsupportedOperationException("Cells of " + getClass().getSimpleName()
            + " have no editable labels");
    }

    /** Sets the editable labels of a cell; only role models with editable labels support this. */
    protected void setLabels(ViewCell<G> cell, EditableLabels labels) {
        throw new UnsupportedOperationException("Cells of " + getClass().getSimpleName()
            + " have no editable labels");
    }

    /**
     * Applies an edit and records it in the history, if there is one and
     * the model is not loading. Empty edits are ignored.
     */
    protected void doEdit(GraphEdit<G> edit) {
        if (edit.isEmpty()) {
            return;
        }
        apply(edit, true);
        var history = this.editHistory;
        if (history != null && !isLoading()) {
            history.recorded(edit);
        }
    }

    /**
     * Applies an edit forward, or reverts it: the store inserts and removes
     * the cells and applies the visuals, the cells take their labels, and
     * {@link #afterEdit} lets the model react.
     * @param forward if {@code true}, the edit is applied; otherwise reverted
     */
    @AIGenerated("Claude Fable 5.1, 2026-09")
    public void apply(GraphEdit<G> edit, boolean forward) {
        var store = getStore();
        if (forward) {
            if (!edit.getRemovedCells().isEmpty()) {
                store.removeCells(edit.getRemovedCells());
            }
            if (!edit.getInsertedCells().isEmpty()) {
                store
                    .insertCells(edit.getInsertedVertices(), edit.getInsertedEdges(),
                                 edit.getInsertedConnections(), false);
            }
        } else {
            if (!edit.getInsertedCells().isEmpty()) {
                store.removeCells(edit.getInsertedCells());
            }
            if (!edit.getRemovedCells().isEmpty()) {
                store
                    .insertCells(edit.getRemovedVertices(), edit.getRemovedEdges(),
                                 edit.getRemovedConnections(), false);
            }
        }
        if (!edit.getVisualChanges().isEmpty()) {
            var visuals = edit.getVisuals(forward);
            store.applyVisuals(visuals);
            if (!isLoading()) {
                for (var cell : visuals.keySet()) {
                    synchroniseLayout(cell);
                }
            }
        }
        List<ViewCell<G>> relabelled = new ArrayList<>();
        for (var entry : edit.getLabelChanges().entrySet()) {
            setLabels(entry.getKey(), entry.getValue().get(forward));
            relabelled.add(entry.getKey());
        }
        afterEdit(edit);
        if (!relabelled.isEmpty()) {
            // after the graph was rebuilt from the labels, which the cells show
            getCanvas().refresh(relabelled, false);
        }
    }

    /**
     * Callback after an edit was applied or reverted; does nothing by default.
     * The aspect model rebuilds its graph from the cells after non-minor edits.
     */
    protected void afterEdit(GraphEdit<G> edit) {
        // empty
    }

    // ---------- element-to-cell maps ----------

    /** Returns the set of cells associated with a given collection
     * of graph elements.
     */
    public Set<@Nullable ViewCell<?>> getJCells(Collection<? extends Element> elements) {
        var result = new HashSet<@Nullable ViewCell<?>>();
        elements.stream().map(this::getJCell).forEach(result::add);
        return result;
    }

    /**
     * Returns the cell associated with a given graph element. The
     * result is a {@link ViewVertex} for which the graph element is the
     * underlying node or self-edge, or a {@link ViewEdge} for which the graph
     * element is an underlying edge.
     * @param elem the graph element for which the cell is requested
     * @return the cell associated with <tt>elem</tt>
     */
    public @Nullable ViewCell<G> getJCell(Element elem) {
        if (elem instanceof Node) {
            return getJCellForNode((Node) elem);
        } else {
            return getJCellForEdge((Edge) elem);
        }
    }

    /**
     * Returns the vertex or edge cell associated with a given
     * edge. The method returns a vertex if and only if <tt>edge</tt> is
     * a self-edge displayed as a node label.
     * @param edge the graph edge we're interested in
     * @return the cell displaying <tt>edge</tt>
     */
    public @Nullable ViewCell<G> getJCellForEdge(Edge edge) {
        return this.edgeJCellMap.get(edge);
    }

    /**
     * Returns the vertex cell associated with a given node.
     * @param node the graph node we're interested in
     * @return the cell displaying <tt>node</tt> (if the node is known)
     */
    public @Nullable ViewVertex<G> getJCellForNode(Node node) {
        return this.nodeJCellMap.get(node);
    }

    /**
     * Inserts a node-to-cell entry into the element-to-cell mapping.
     * @return the previous cell associated with the node, if any
     */
    public @Nullable ViewVertex<G> putNode(Node node, ViewVertex<G> jVertex) {
        return this.nodeJCellMap.put(node, jVertex);
    }

    /**
     * Inserts an edge-to-cell entry into the element-to-cell mapping.
     * @return the previous cell associated with the edge, if any
     */
    public @Nullable ViewCell<G> putEdge(Edge edge, ViewCell<G> jCell) {
        return this.edgeJCellMap.put(edge, jCell);
    }

    /**
     * Replaces the element-to-cell maps wholesale.
     * Used when the cells are the primary data from which the graph
     * is (re)constructed, as in the editor.
     */
    public void setJCellMaps(Map<? extends Node,? extends ViewVertex<G>> nodeJCellMap,
                             Map<? extends Edge,? extends ViewCell<G>> edgeJCellMap) {
        this.nodeJCellMap.clear();
        this.nodeJCellMap.putAll(nodeJCellMap);
        this.edgeJCellMap.clear();
        this.edgeJCellMap.putAll(edgeJCellMap);
    }

    /** Returns the set of graph nodes currently represented in this view model. */
    public Set<Node> getNodes() {
        return this.nodeJCellMap.keySet();
    }

    /**
     * Returns all cells of this view model, in the z-order of the backend store.
     * In an editor, this may include cells not (yet) mapped from a graph element.
     */
    public Collection<? extends ViewCell<G>> getCells() {
        return getStore().getCells();
    }

    /** Sets the layoutable status of all vertices. */
    public void setLayoutable(boolean layoutable) {
        for (var vertex : this.nodeJCellMap.values()) {
            vertex.setLayoutable(layoutable);
        }
    }

    /** Marks all refreshable visuals of all cells as stale. */
    public void refreshVisuals() {
        for (var cell : getCells()) {
            cell.setStale(VisualKey.refreshables());
        }
    }

    /** Returns a map from nodes to the foreground colours of their vertices. */
    public Map<Node,Color> getColorMap() {
        Map<Node,Color> result = new HashMap<>();
        for (var entry : this.nodeJCellMap.entrySet()) {
            Color foreground = entry.getValue().getVisuals().getForeground();
            if (foreground != null) {
                result.put(entry.getKey(), foreground);
            }
        }
        return result;
    }

    /**
     * Indicates if this model is in the process of being loaded from a graph.
     * Change events arriving while this holds reflect the loading, not user edits,
     * and should be ignored by listeners that track edits.
     */
    public boolean isLoading() {
        return this.loading;
    }

    /** Sets the loading status; see {@link #isLoading()}. */
    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    /** Flag indicating that the model is being loaded from a graph. */
    private boolean loading;

    /** Returns the number of graph nodes currently represented in this view model. */
    public int nodeCount() {
        return this.nodeJCellMap.size();
    }

    /** Returns the size of the graph, as a sum of the number of nodes and edges. */
    public int size() {
        return this.nodeJCellMap.size() + this.edgeJCellMap.size();
    }

    // ---------- loading ----------

    /**
     * Loads in a given graph, replacing the current cells by cells for its
     * nodes and edges.
     */
    public void loadGraph(G graph) {
        prepareLoad(graph);
        addElements(graph.nodeSet(), graph.edgeSet(), true);
    }

    /**
     * Prepares the view model for loading a new graph:
     * sets the graph and layout map, and clears the element-to-cell maps.
     */
    protected void prepareLoad(G graph) {
        this.graph = graph;
        this.layoutMap = GraphInfo.getLayoutMap(graph);
        if (this.layoutMap == null) {
            this.layoutMap = graph.getInfo().getLayoutMap();
        }
        this.nodeJCellMap.clear();
        this.edgeJCellMap.clear();
    }

    /**
     * Adds cells for new graph elements to this model, in one committed edit.
     * @param nodeSet the set of nodes to be added
     * @param edgeSet the set of edges to be added; if {@code null},
     * the incident edges of {@code nodeSet} are used (where a role supports this)
     * @param replace if {@code true}, all existing cells are removed
     * @return {@code true} if the model was changed
     */
    public boolean addElements(Collection<? extends Node> nodeSet,
                               @Nullable Collection<? extends Edge> edgeSet, boolean replace) {
        boolean result = replace;
        boolean wasLoading = isLoading();
        setLoading(true);
        prepareInsert();
        result |= addNodes(nodeSet);
        result |= addEdges(edgeSet);
        if (result) {
            doInsert(replace);
        }
        setLoading(wasLoading);
        return result;
    }

    /** Adds the given set of nodes to this model.
     * @return {@code true} if any nodes were added.*/
    protected boolean addNodes(Collection<? extends Node> nodeSet) {
        for (Node node : nodeSet) {
            addNode(node);
        }
        return !nodeSet.isEmpty();
    }

    /** Adds the given set of edges to this model.
     * @return {@code true} if any edges were added. */
    protected boolean addEdges(@Nullable Collection<? extends Edge> edgeSet) {
        assert edgeSet != null; // implied edges are only supported by role subclasses
        for (Edge edge : edgeSet) {
            addEdge(edge);
        }
        return !edgeSet.isEmpty();
    }

    /**
     * Creates a cell corresponding to a given node in the graph. Adds the
     * cell to the pending vertices, and updates the element-to-cell map.
     */
    protected ViewVertex<G> addNode(Node node) {
        ViewVertex<G> jVertex = computeVertex(node);
        this.addedVertices.add(jVertex);
        ViewVertex<G> oldNode = putNode(node, jVertex);
        assert oldNode == null;
        return jVertex;
    }

    /**
     * Creates a cell corresponding to a given graph edge. This may be a
     * vertex, if the edge can be graphically depicted by that vertex; or an
     * existing edge cell, if the edge can be represented by it. Otherwise, it will
     * be a new edge cell.
     */
    protected ViewCell<G> addEdge(Edge edge) {
        ViewCell<G> result = getJCellForEdge(edge);
        // check if edge was processed earlier
        ViewVertex<G> sourceJVertex = getJCellForNode(edge.source());
        assert sourceJVertex != null : "No vertex for source node of " + edge;
        if (result == null) {
            // try to add the edge as vertex label to its source vertex
            if (sourceJVertex.isCompatible(edge)) {
                sourceJVertex.addEdge(edge);
                // yes, the edge could be added here; we're done
                result = sourceJVertex;
            }
        }
        if (result == null) {
            // try to add the edge to an existing edge cell
            Iterator<? extends ViewEdge<G>> edgeIter = getJEdges(sourceJVertex);
            while (edgeIter.hasNext()) {
                ViewEdge<G> jEdge = edgeIter.next();
                if (jEdge.isCompatible(edge)) {
                    // yes, the edge could be added here; we're done
                    jEdge.addEdge(edge);
                    result = jEdge;
                    break;
                }
            }
        }
        if (result == null) {
            // none of the above: so create a new edge cell
            ViewEdge<G> jEdge;
            result = jEdge = computeEdge(edge);
            // put the edge at the end to make sure it goes to the back
            this.addedEdges.add(jEdge);
            ViewVertex<G> targetJVertex = getJCellForNode(edge.target());
            assert targetJVertex != null : "No vertex for target node of " + edge;
            this.connections.add(new Connection<>(jEdge, sourceJVertex, targetJVertex));
            addFreshJEdge(sourceJVertex, jEdge);
            addFreshJEdge(targetJVertex, jEdge);
        }
        putEdge(edge, result);
        return result;
    }

    /**
     * Retrieves the known incident edge cells of a given vertex,
     * either from the explicitly stored fresh edges (if the vertex is fresh)
     * or from the stored context of the vertex.
     */
    private Iterator<? extends ViewEdge<G>> getJEdges(ViewVertex<G> jVertex) {
        Iterator<? extends ViewEdge<G>> result;
        Set<ViewEdge<G>> outJEdges = this.freshJEdges.get(jVertex);
        if (outJEdges == null) {
            result = jVertex.getContext();
        } else {
            result = new NestedIterator<>(outJEdges.iterator(), jVertex.getContext());
        }
        return result;
    }

    /**
     * Adds a given edge cell to the fresh incident edges of a vertex.
     */
    private void addFreshJEdge(ViewVertex<G> jVertex, ViewEdge<G> jEdge) {
        Set<ViewEdge<G>> jEdges = this.freshJEdges.get(jVertex);
        if (jEdges == null) {
            this.freshJEdges.put(jVertex, jEdges = new HashSet<>());
        }
        jEdges.add(jEdge);
    }

    /**
     * Creates a new edge cell through the store, and adds available
     * layout information from the layout map stored in this model.
     * @param edge graph edge for which a corresponding cell is to be created
     */
    protected ViewEdge<G> computeEdge(Edge edge) {
        ViewEdge<G> result = newEdge(edge);
        EdgeLayout layout = getLayoutMap().getLayout(edge);
        if (layout != null) {
            result.putVisuals(VisualMap.newInstance(layout));
        }
        return result;
    }

    /**
     * Creates a new vertex cell through the store, and adds available
     * layout information from the layout map stored in this model; or adds a
     * random position otherwise.
     * @param node graph node for which a corresponding cell is to be created
     */
    final protected ViewVertex<G> computeVertex(Node node) {
        ViewVertex<G> result = newVertex(node);
        NodeLayout layout = getLayoutMap().getLayout(node);
        if (layout != null) {
            result.putVisuals(VisualMap.newInstance(layout));
        } else {
            Point2D nodePos = new Point2D.Double(this.nodeX, this.nodeY);
            result.putVisual(VisualKey.NODE_POS, nodePos);
            this.nodeX = randomCoordinate();
            this.nodeY = randomCoordinate();
            result.setLayoutable(true);
        }
        return result;
    }

    /** Creates a fresh, initialised vertex cell for a given node, not yet in the store. */
    public AViewVertex<G> newVertex(Node node) {
        var result = createVertexCell(node);
        result.setNode(node);
        result.initialise();
        return result;
    }

    /**
     * Creates a fresh, initialised edge cell, not yet in the store.
     * @param edge the initial edge of the cell; {@code null} if there is none yet
     */
    public AViewEdge<G> newEdge(@Nullable Edge edge) {
        var result = createEdgeCell();
        result.initialise();
        if (edge != null) {
            result.addEdge(edge);
        }
        return result;
    }

    /** Callback factory method for a vertex cell of the role of this model, for a given node. */
    protected abstract AViewVertex<G> createVertexCell(Node node);

    /** Callback factory method for an edge cell of the role of this model. */
    protected abstract AViewEdge<G> createEdgeCell();

    /**
     * Sets the transient variables (pending cells and connections) to fresh
     * (empty) initial values.
     */
    protected void prepareInsert() {
        this.addedEdges.clear();
        this.addedVertices.clear();
        this.freshJEdges.clear();
        this.connections.clear();
    }

    /**
     * Commits the insertion prepared by node and edge additions to the store.
     * @param replace if {@code true}, the old cells should be deleted
     */
    protected void doInsert(boolean replace) {
        getStore().insertCells(this.addedVertices, this.addedEdges, this.connections, replace);
    }

    /**
     * Returns a random number bounded by the size of the model. Used to
     * generate a random position for any added vertex without layout
     * information.
     */
    protected int randomCoordinate() {
        return 25 + randomGenerator.nextInt(size() * 5 + 1);
    }

    /**
     * Returns whether or not equally named bidirectional edges should be
     * merged (i.e. mapped to the same edge cell). Override in subclass to
     * modify this behaviour.
     */
    public boolean isMergeBidirectionalEdges() {
        return getController().isShowBidirectionalEdges();
    }

    /**
     * Returns whether all edges should be
     * merged (i.e. mapped to the same edge cell). Override in subclass to
     * modify this behaviour.
     */
    public boolean isMergeAllEdges() {
        return getController().isShowArrowsOnLabels();
    }

    /**
     * Map from graph nodes to the cells displaying them.
     */
    private final Map<Node,ViewVertex<G>> nodeJCellMap = new HashMap<>();
    /**
     * Map from graph edges to the cells displaying them.
     */
    private final Map<Edge,ViewCell<G>> edgeJCellMap = new HashMap<>();
    /**
     * Mapping from vertices to incident edge cells.
     * Used in the process of constructing the cells.
     */
    private final Map<ViewVertex<G>,Set<ViewEdge<G>>> freshJEdges = new HashMap<>();
    /** Pending edge cells of the current insertion. */
    private final List<ViewEdge<G>> addedEdges = new ArrayList<>();
    /** Pending vertex cells of the current insertion. */
    private final List<ViewVertex<G>> addedVertices = new ArrayList<>();
    /** Pending connections between newly created edge cells and their end vertices. */
    private final List<Connection<G>> connections = new ArrayList<>();
    /** Counter to provide the x-coordinate of fresh nodes with fresh values. */
    private int nodeX;
    /** Counter to provide the y-coordinate of fresh nodes with fresh values. */
    private int nodeY;

    /** Random generator for coordinates of new nodes. */
    private static final Random randomGenerator = new Random();
}
