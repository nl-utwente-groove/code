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

import java.awt.Color;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.jgraph.event.GraphModelEvent.GraphModelChange;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.ParentMap;

import nl.utwente.groove.graph.Edge;
import nl.utwente.groove.graph.Element;
import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.graph.GraphInfo;
import nl.utwente.groove.graph.Node;
import nl.utwente.groove.graph.layout.LayoutMap;
import nl.utwente.groove.gui.view.CellStore;
import nl.utwente.groove.gui.view.GraphViewModel;
import nl.utwente.groove.gui.view.ViewCell;
import nl.utwente.groove.gui.view.ViewEdge;
import nl.utwente.groove.gui.view.ViewVertex;

/**
 * Implements JGraph's GraphModel interface on top of a GROOVE graph:
 * the backend adapter of a {@link GraphViewModel}, whose cells it stores as its roots
 * and whose structural changes it commits as JGraph edits.
 * The content operations delegate to the view model.
 * @author Arend Rensink
 * @version $Revision$
 */
abstract public class JModel<G extends @NonNull Graph> extends DefaultGraphModel
    implements CellStore<G> {
    /**
     * Creates a new model for a given JGraph, together with its view model.
     */
    protected JModel(JGraph<G> jGraph) {
        this.jGraph = jGraph;
        this.viewModel = createViewModel();
    }

    /** Callback factory method for the view model of this backend model. */
    protected GraphViewModel<G> createViewModel() {
        return new GraphViewModel<>(getJGraph().getController(), this);
    }

    /** Returns the JGraph in which this model belongs. */
    public JGraph<G> getJGraph() {
        return this.jGraph;
    }

    /** Specialises the type to a list of {@link ViewCell}s. */
    @Override
    @SuppressWarnings("unchecked")
    public List<? extends ViewCell<G>> getRoots() {
        return super.getRoots();
    }

    @Override
    public Collection<? extends ViewCell<G>> getCells() {
        return getRoots();
    }

    /** Refreshes all refreshable visual keys in all cells of this model. */
    public void refreshVisuals() {
        getViewModel().refreshVisuals();
    }

    /** Returns the size of the graph, as a sum of the number of nodes and edges. */
    public int size() {
        return getViewModel().size();
    }

    /**
     * Sends a set of cells to the back (in the z-order) without posting an edit.
     */
    void toBackSilent(Collection<? extends ViewCell<G>> jCells) {
        createLayerEdit(jCells.toArray(), GraphModelLayerEdit.BACK).execute();
    }

    @Override
    public AttributeMap getAttributes(Object node) {
        AttributeMap result;
        if (node instanceof ViewCell) {
            result = ((AJCell<?,?,?>) node).getAttributes();
        } else {
            result = super.getAttributes(node);
        }
        assert result != null : String.format("Cell %s has no attributes", node);
        return result;
    }

    /**
     * If the name is not explicitly set, obtains the name of the underlying
     * graph as set in the graph properties.
     */
    public String getName() {
        return getViewModel().getName();
    }

    /**
     * Returns the underlying Graph of this GraphModel.
     */
    public @Nullable G getGraph() {
        return getViewModel().getGraph();
    }

    /**
     * Returns the (non-{@code null}) layout map of the graph.
     * This is retrieved from {@link GraphInfo#getLayoutMap(Graph)}.
     */
    public LayoutMap getLayoutMap() {
        return getViewModel().getLayoutMap();
    }

    /**
     * Changes the underlying graph to the one passed in as a parameter.
     * Note that this should only be done as part of an action that also
     * changes the {@link ViewCell}s of the {@link JModel}, as well as the
     * mapping from graph elements to {@link ViewCell}s.
     */
    void setGraph(G graph) {
        getViewModel().setGraph(graph);
    }

    /**
     * Loads in a given graph, replacing the current cells.
     */
    public void loadGraph(G graph) {
        getViewModel().loadGraph(graph);
    }

    /**
     * Adds new graph elements from the current graph to this JModel.
     * @param nodeSet the set of nodes to be added; non-{@code null}
     * @param edgeSet the set of edges to be added; if{@code null},
     * the incident edges of {@code nodeSet} are used
     * @param replace if {@code true}, all existing jCells are removed
     * @return {@code true} if the jModel was changed
     */
    public boolean addElements(Collection<? extends Node> nodeSet,
                               Collection<? extends Edge> edgeSet, boolean replace) {
        return getViewModel().addElements(nodeSet, edgeSet, replace);
    }

    /** Returns the set of {@link ViewCell}s associated with a given collection
     * of graph elements.
     */
    public Set<ViewCell<?>> getJCells(Collection<? extends Element> elements) {
        return getViewModel().getJCells(elements);
    }

    /**
     * Returns the {@link ViewCell} associated with a given graph element. The
     * result is a {@link ViewVertex} for which the graph element is the
     * underlying node or self-edge, or a {@link ViewEdge} for which the graph
     * element is an underlying edge.
     * @param elem the graph element for which the jcell is requested
     * @return the jcell associated with <tt>elem</tt>
     */
    public ViewCell<G> getJCell(Element elem) {
        return getViewModel().getJCell(elem);
    }

    /**
     * Returns the <tt>JNode</tt> or <tt>ViewEdge</tt> associated with a given
     * edge. The method returns a <tt>JNode</tt> if and only if <tt>edge</tt> is
     * a self-edge and <tt>showNodeIdentities</tt> does not hold.
     * @param edge the graph edge we're interested in
     * @return the <tt>JNode</tt> or <tt>ViewEdge</tt> modelling <tt>edge</tt>
     */
    public ViewCell<G> getJCellForEdge(Edge edge) {
        return getViewModel().getJCellForEdge(edge);
    }

    /**
     * Returns the JNode associated with a given node.
     * @param node the graph node we're interested in
     * @return the JNode modelling node (if node is known)
     */
    public ViewVertex<G> getJCellForNode(Node node) {
        return getViewModel().getJCellForNode(node);
    }

    /** Returns the number of graph nodes currently represented in this {@link JModel}. */
    public int nodeCount() {
        return getViewModel().nodeCount();
    }

    /** Stores the layout from the JModel back into the graph. */
    public void synchroniseLayout(ViewCell<G> jCell) {
        getViewModel().synchroniseLayout(jCell);
    }

    /**
     * Sets the layoutability of all cells.
     * @param layoutable the new value for {@link ViewVertex#setLayoutable(boolean)}
     */
    public void setLayoutable(boolean layoutable) {
        getViewModel().setLayoutable(layoutable);
    }

    /** Retrieves a mapping from graph nodes to foreground colours
     * as stored in the corresponding {@link ViewVertex} attributes.
     */
    public Map<Node,Color> getColorMap() {
        return getViewModel().getColorMap();
    }

    @Override
    protected void fireGraphChanged(Object source, GraphModelChange edit) {
        if (!isLoading()) {
            // if we're loading, the layout is actually taken from the graph
            // so no synchronisation is necessary
            for (Object jCell : edit.getChanged()) {
                if (jCell instanceof ViewCell) {
                    @SuppressWarnings("unchecked")
                    ViewCell<G> graphJCell = (ViewCell<G>) jCell;
                    synchroniseLayout(graphJCell);
                }
            }
        }
        if (!vetoFireGraphChanged()) {
            super.fireGraphChanged(source, edit);
        }
    }

    /**
     * Callback method that may prevent {@link #fireGraphChanged(Object, GraphModelChange)}
     * from propagating its event. This can be done in preparation to layouting,
     * to avoid flickers.
     */
    protected boolean vetoFireGraphChanged() {
        return this.vetoFireGraphChanged;
    }

    /** Sets or retracts the veto for the {@link #fireGraphChanged(Object, GraphModelChange)}
     * event.
     */
    protected void setVetoFireGraphChanged(boolean veto) {
        this.vetoFireGraphChanged = veto;
    }

    /**
     * Returns whether or not equally named bidirectional edges should be
     * merged (i.e. mapped to the same GraphJEdge).
     */
    public boolean isMergeBidirectionalEdges() {
        return getViewModel().isMergeBidirectionalEdges();
    }

    /**
     * Returns whether all edges should be
     * merged (i.e. mapped to the same GraphJEdge).
     */
    public boolean isMergeAllEdges() {
        return getViewModel().isMergeAllEdges();
    }

    // ---------- the cell store ----------

    /* Creates a JGraph vertex cell through the factory, bound to this model. */
    @Override
    public ViewVertex<G> newVertex(Node node) {
        ViewVertex<G> result = getJGraph().getFactory().newJVertex(node);
        ((AJCell<?,?,?>) result).setJModel(this);
        result.setNode(node);
        result.initialise();
        return result;
    }

    /* Creates a JGraph edge cell through the factory, bound to this model. */
    @Override
    public ViewEdge<G> newEdge(@Nullable Edge edge) {
        ViewEdge<G> result = getJGraph().getFactory().newJEdge(edge);
        ((AJCell<?,?,?>) result).setJModel(this);
        result.initialise();
        if (edge != null) {
            result.addEdge(edge);
        }
        return result;
    }

    /* Commits the insertion as one JGraph edit; the edges go first so they end up at the back. */
    @Override
    public void insertCells(List<? extends ViewVertex<G>> vertices,
                            List<? extends ViewEdge<G>> edges, List<Connection<G>> connections,
                            boolean replace) {
        int vertexCount = vertices.size();
        int edgeCount = edges.size();
        Object[] addedCells = new ViewCell<?>[vertexCount + edgeCount];
        for (int i = 0; i < edgeCount; i++) {
            addedCells[i] = edges.get(i);
        }
        for (int i = 0; i < vertexCount; i++) {
            addedCells[edgeCount + i] = vertices.get(i);
        }
        Object[] removedCells = replace
            ? getRoots().toArray()
            : null;
        ConnectionSet connectionSet = new ConnectionSet();
        for (Connection<G> c : connections) {
            connectionSet
                .connect(c.edge(), ((AJVertex<?,?,?,?>) c.source()).getPort(),
                         ((AJVertex<?,?,?,?>) c.target()).getPort());
        }
        createEdit(addedCells, removedCells, null, connectionSet, getParentMap(), null)
            .execute();
    }

    /**
     * Returns the parent map for hierarchical graphs, default to null.
     * To be overriden in derived classes.
     */
    protected ParentMap getParentMap() {
        return null;
    }

    /** The JGraph to which this model belongs. */
    private final JGraph<G> jGraph;

    /** Returns the library-independent content model of the graph view. */
    public GraphViewModel<G> getViewModel() {
        return this.viewModel;
    }

    /** The library-independent content model of the graph view. */
    private final GraphViewModel<G> viewModel;

    /** Changes the loading status of the view model.
     * Callers restore the previous status afterwards, so that loading phases nest.
     */
    protected void setLoading(boolean loading) {
        getViewModel().setLoading(loading);
    }

    /** Indicates if the JModel is currently in the process of loading a graph. */
    public boolean isLoading() {
        return getViewModel().isLoading();
    }

    /** See {@link #setVetoFireGraphChanged(boolean)}. */
    private boolean vetoFireGraphChanged;
}
