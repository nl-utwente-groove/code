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
import java.util.ArrayList;
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
import nl.utwente.groove.gui.view.cell.AViewCell;
import nl.utwente.groove.gui.view.cell.AViewEdge;
import nl.utwente.groove.gui.view.cell.AViewVertex;

/**
 * Implements JGraph's GraphModel interface on top of a GROOVE graph:
 * the backend adapter of a {@link GraphViewModel}, whose cells it shows through
 * {@link JCell} items kept as its roots, and whose structural changes it commits
 * as JGraph edits. The content operations delegate to the view model.
 * @author Arend Rensink
 * @version $Revision$
 */
abstract public class JModel<G extends @NonNull Graph> extends DefaultGraphModel
    implements CellStore<G> {
    /**
     * Creates a new model for a given JGraph, together with its view model,
     * which the JGraph creates for this model as its cell store.
     */
    protected JModel(JGraph<G> jGraph) {
        this.jGraph = jGraph;
        this.viewModel = jGraph.createViewModel(this);
    }

    /** Returns the JGraph in which this model belongs. */
    public JGraph<G> getJGraph() {
        return this.jGraph;
    }

    /** Specialises the type to a list of {@link JCell}s. */
    @Override
    @SuppressWarnings("unchecked")
    public List<? extends JCell<G>> getRoots() {
        return super.getRoots();
    }

    /* The cells shown by the roots, in z-order. */
    @Override
    public Collection<? extends ViewCell<G>> getCells() {
        List<AViewCell<G>> result = new ArrayList<>();
        for (var root : getRoots()) {
            result.add(root.getViewCell());
        }
        return result;
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
        createLayerEdit(JCell.items(jCells), GraphModelLayerEdit.BACK).execute();
    }

    @Override
    public AttributeMap getAttributes(Object node) {
        AttributeMap result;
        if (node instanceof JCell<?> jCell) {
            result = jCell.getAttributes();
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
     * changes the cells of the {@link JModel}, as well as the
     * mapping from graph elements to cells.
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
     * @param replace if {@code true}, all existing cells are removed
     * @return {@code true} if the jModel was changed
     */
    public boolean addElements(Collection<? extends Node> nodeSet,
                               Collection<? extends Edge> edgeSet, boolean replace) {
        return getViewModel().addElements(nodeSet, edgeSet, replace);
    }

    /** Returns the set of cells associated with a given collection
     * of graph elements.
     */
    public Set<ViewCell<?>> getJCells(Collection<? extends Element> elements) {
        return getViewModel().getJCells(elements);
    }

    /**
     * Returns the cell associated with a given graph element. The
     * result is a {@link ViewVertex} for which the graph element is the
     * underlying node or self-edge, or a {@link ViewEdge} for which the graph
     * element is an underlying edge.
     * @param elem the graph element for which the cell is requested
     * @return the cell associated with <tt>elem</tt>
     */
    public ViewCell<G> getJCell(Element elem) {
        return getViewModel().getJCell(elem);
    }

    /**
     * Returns the vertex or edge cell associated with a given
     * edge. The method returns a vertex cell if and only if <tt>edge</tt> is
     * a self-edge and <tt>showNodeIdentities</tt> does not hold.
     * @param edge the graph edge we're interested in
     * @return the cell modelling <tt>edge</tt>
     */
    public ViewCell<G> getJCellForEdge(Edge edge) {
        return getViewModel().getJCellForEdge(edge);
    }

    /**
     * Returns the vertex cell associated with a given node.
     * @param node the graph node we're interested in
     * @return the vertex cell modelling node (if node is known)
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

    /** Sets the layoutable status of all vertices. */
    public void setLayoutable(boolean layoutable) {
        getViewModel().setLayoutable(layoutable);
    }

    /** Returns a map from nodes to colours, as stored in the layout map. */
    public Map<Node,Color> getColorMap() {
        return getViewModel().getColorMap();
    }

    /**
     * Overrides the method to synchronise the layout of changed cells back into
     * the graph, and to allow the change notification to be vetoed.
     */
    @Override
    protected void fireGraphChanged(Object source, GraphModelChange edit) {
        if (!isLoading()) {
            // if we're loading, the layout is actually taken from the graph
            // so no synchronisation is necessary
            for (Object jCell : edit.getChanged()) {
                if (jCell instanceof JCell<?> item) {
                    @SuppressWarnings("unchecked")
                    ViewCell<G> viewCell = (ViewCell<G>) item.getViewCell();
                    synchroniseLayout(viewCell);
                }
            }
        }
        if (!vetoFireGraphChanged()) {
            super.fireGraphChanged(source, edit);
        }
    }

    /** Indicates if graph change notifications are currently vetoed. */
    protected boolean vetoFireGraphChanged() {
        return this.vetoFireGraphChanged;
    }

    /** Sets or resets the veto on graph change notifications. */
    protected void setVetoFireGraphChanged(boolean veto) {
        this.vetoFireGraphChanged = veto;
    }

    /**
     * Returns whether or not equally named bidirectional edges should be
     * merged (i.e. mapped to the same edge cell).
     */
    public boolean isMergeBidirectionalEdges() {
        return getViewModel().isMergeBidirectionalEdges();
    }

    /**
     * Returns whether all edges should be merged (i.e. mapped to the same edge cell).
     */
    public boolean isMergeAllEdges() {
        return getViewModel().isMergeAllEdges();
    }

    // ---------- the cell store ----------

    /**
     * Commits the insertion as one JGraph edit, creating the JGraph items of the
     * inserted cells; the edges go first so they end up at the back.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void insertCells(List<? extends ViewVertex<G>> vertices,
                            List<? extends ViewEdge<G>> edges, List<Connection<G>> connections,
                            boolean replace) {
        int vertexCount = vertices.size();
        int edgeCount = edges.size();
        Object[] addedCells = new JCell<?>[vertexCount + edgeCount];
        for (int i = 0; i < edgeCount; i++) {
            addedCells[i] = new JEdge<>((AViewEdge<G>) edges.get(i));
        }
        for (int i = 0; i < vertexCount; i++) {
            addedCells[edgeCount + i] = new JVertex<>((AViewVertex<G>) vertices.get(i));
        }
        Object[] removedCells = replace
            ? getRoots().toArray()
            : null;
        ConnectionSet connectionSet = new ConnectionSet();
        for (Connection<G> c : connections) {
            connectionSet
                .connect(JCell.of(c.edge()), vertexItem(c.source()).getPort(),
                         vertexItem(c.target()).getPort());
        }
        createEdit(addedCells, removedCells, null, connectionSet, getParentMap(), null)
            .execute();
    }

    /** Returns the JGraph item of a given vertex cell. */
    private JVertex<G> vertexItem(ViewVertex<G> vertex) {
        return (JVertex<G>) JCell.of(vertex);
    }

    /** Callback method to return the parent map for insertions; {@code null} by default. */
    protected ParentMap getParentMap() {
        return null;
    }

    private final JGraph<G> jGraph;

    /** Returns the view model this JGraph model is the adapter of. */
    public GraphViewModel<G> getViewModel() {
        return this.viewModel;
    }

    /** The view model this JGraph model is the adapter of. */
    private final GraphViewModel<G> viewModel;

    /** Sets the loading flag of the view model. */
    protected void setLoading(boolean loading) {
        getViewModel().setLoading(loading);
    }

    /** Indicates if the view model is being loaded. */
    public boolean isLoading() {
        return getViewModel().isLoading();
    }

    /** Flag indicating that graph change notifications are vetoed. */
    private boolean vetoFireGraphChanged;
}
