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

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.jgraph.event.GraphModelEvent.GraphModelChange;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.ParentMap;

import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.Graph;
import groove.graph.GraphInfo;
import groove.graph.Node;
import groove.gui.layout.JCellLayout;
import groove.gui.layout.JEdgeLayout;
import groove.gui.layout.JVertexLayout;
import groove.gui.layout.LayoutMap;
import groove.gui.look.VisualKey;
import groove.util.collect.NestedIterator;

/**
 * Implements JGraph's GraphModel interface on top of a GROOVE graph.
 * @author Arend Rensink
 * @version $Revision$
 */
abstract public class JModel<G extends Graph> extends DefaultGraphModel {
    /**
     * Creates a new GraphJModel instance on top of a given GraphJGraph, with given
     * node and edge attributes, and an indication whether self-edges should be
     * displayed as node labels. The node and edge attribute maps are cloned.
     */
    protected JModel(JGraph<G> jGraph) {
        this.jGraph = jGraph;
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

    /** Refreshes all refreshable visual keys in all cells of this model. */
    public void refreshVisuals() {
        for (JCell<G> jCell : getRoots()) {
            jCell.setStale(VisualKey.refreshables());
        }
    }

    /** Returns the size of the graph, as a sum of the number of nodes and edges. */
    public int size() {
        return this.nodeJCellMap.size() + this.edgeJCellMap.size();
    }

    /**
     * Sends a set of cells to the back (in the z-order) without posting an edit.
     */
    void toBackSilent(Collection<? extends JCell<G>> jCells) {
        createLayerEdit(jCells.toArray(), GraphModelLayerEdit.BACK).execute();
    }

    @Override
    public AttributeMap getAttributes(Object node) {
        AttributeMap result;
        if (node instanceof JCell) {
            result = ((JCell<?>) node).getAttributes();
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
        return getGraph() == null ? null : getGraph().getName();
    }

    /**
     * Returns the underlying Graph of this GraphModel.
     * @ensure result != null
     */
    public G getGraph() {
        return this.graph;
    }

    /**
     * Returns the (non-{@code null}) layout map of the graph.
     * This is retrieved from {@link GraphInfo#getLayoutMap(Graph)}.
     */
    public LayoutMap getLayoutMap() {
        return this.layoutMap;
    }

    /**
     * Changes the underlying graph to the one passed in as a parameter.
     * Note that this should only be done as part of an action that also
     * changes the {@link JCell}s of the {@link JModel}, as well as the
     * mapping from graph elements to {@link JCell}s.
     */
    void setGraph(G graph) {
        this.graph = graph;
        this.layoutMap = GraphInfo.getLayoutMap(graph);
    }

    /**
     * Loads in a given graph, adding any nodes and edges not yet in this
     * model. Also adds the model as a listener to the graph again. This may be
     * necessary if the model was removed as a graph listener, for instance for
     * the sake of efficiency.
     */
    public void loadGraph(G graph) {
        prepareLoad(graph);
        addElements(graph.nodeSet(), graph.edgeSet(), true);
    }

    /**
     * Adds new graph elements from the current graph to this JModel.
     * @param nodeSet the set of nodes to be added; non-{@code null}
     * @param edgeSet the set of edges to be added; if{@code null},
     * the incident edges of {@code nodeSet} are used
     * @param replace if {@code true}, all existing jCells are removed
     * @return {@code true} if the jModel was changed
     */
    protected boolean addElements(Collection<? extends Node> nodeSet,
        Collection<? extends Edge> edgeSet, boolean replace) {
        boolean result = replace;
        setLoading(true);
        prepareInsert();
        result |= addNodes(nodeSet);
        result |= addEdges(edgeSet);
        if (result) {
            doInsert(replace);
        }
        setLoading(false);
        return result;
    }

    /** Adds the given set of nodes to this JModel.
     * @return {@code true} if any nodes were added.*/
    protected boolean addNodes(Collection<? extends Node> nodeSet) {
        for (Node node : nodeSet) {
            addNode(node);
        }
        return !nodeSet.isEmpty();
    }

    /** Adds the given set of edges to this JModel.
     * @return {@code true} if any edges were added. */
    protected boolean addEdges(Collection<? extends Edge> edgeSet) {
        for (Edge edge : edgeSet) {
            addEdge(edge);
        }
        return !edgeSet.isEmpty();
    }

    /**
     * Prepare the object fields for loading a new graph.
     */
    protected void prepareLoad(G graph) {
        this.graph = graph;
        this.layoutMap = GraphInfo.getLayoutMap(graph);
        if (this.layoutMap == null) {
            this.layoutMap = graph.getInfo()
                .getLayoutMap();
        }
        this.nodeJCellMap.clear();
        this.edgeJCellMap.clear();
    }

    /**
     * Returns the {@link JCell} associated with a given graph element. The
     * result is a {@link JVertex} for which the graph element is the
     * underlying node or self-edge, or a {@link JEdge} for which the graph
     * element is an underlying edge.
     * @param elem the graph element for which the jcell is requested
     * @return the jcell associated with <tt>elem</tt>
     */
    public JCell<G> getJCell(Element elem) {
        if (elem instanceof Node) {
            return getJCellForNode((Node) elem);
        } else {
            return getJCellForEdge((Edge) elem);
        }
    }

    /**
     * Returns the <tt>JNode</tt> or <tt>JEdge</tt> associated with a given
     * edge. The method returns a <tt>JNode</tt> if and only if <tt>edge</tt> is
     * a self-edge and <tt>showNodeIdentities</tt> does not hold.
     * @param edge the graph edge we're interested in
     * @return the <tt>JNode</tt> or <tt>JEdge</tt> modelling <tt>edge</tt>
     * @ensure result instanceof JNode && result.labels().contains(edge.label())
     *         || result instanceof JEdge &&
     *         result.labels().contains(edge.label())
     */
    public JCell<G> getJCellForEdge(Edge edge) {
        return this.edgeJCellMap.get(edge);
    }

    /**
     * Returns the JNode associated with a given node.
     * @param node the graph node we're interested in
     * @return the JNode modelling node (if node is known)
     * @ensure result == null || result.getUserObject() == node
     */
    public JVertex<G> getJCellForNode(Node node) {
        return this.nodeJCellMap.get(node);
    }

    /** Returns the number of graph nodes currently represented in this {@link JModel}. */
    public int nodeCount() {
        return this.nodeJCellMap.size();
    }

    /** Stores the layout from the JModel back into the graph. */
    public void synchroniseLayout(JCell<G> jCell) {
        LayoutMap layoutMap = getLayoutMap();
        assert layoutMap == GraphInfo.getLayoutMap(getGraph());
        if (jCell instanceof JEdge) {
            for (Edge edge : ((JEdge<G>) jCell).getEdges()) {
                layoutMap.putEdge(edge, jCell.getVisuals());
            }
        } else if (jCell instanceof JVertex) {
            layoutMap.putNode(((JVertex<G>) jCell).getNode(), jCell.getVisuals());
        }
    }

    /**
     * Sets the layoutability of all cells.
     * @param layoutable the new value for {@link JVertex#setLayoutable(boolean)}
     */
    public void setLayoutable(boolean layoutable) {
        for (JCell<G> jCell : getRoots()) {
            if (jCell instanceof JVertex) {
                ((JVertex<?>) jCell).setLayoutable(layoutable);
            }
        }
    }

    /** Retrieves a mapping from graph nodes to foreground colours
     * as stored in the corresponding {@link JVertex} attributes.
     */
    public Map<Node,Color> getColorMap() {
        Map<Node,Color> result = new HashMap<>();
        for (JCell<G> jCell : getRoots()) {
            if (jCell instanceof JVertex) {
                Color foreground = jCell.getVisuals()
                    .getForeground();
                if (foreground != null) {
                    result.put(((JVertex<G>) jCell).getNode(), foreground);
                }
            }
        }
        return result;
    }

    @Override
    protected void fireGraphChanged(Object source, GraphModelChange edit) {
        if (!isLoading()) {
            // if we're loading, the layout is actually taken from the graph
            // so no synchronisation is necessary
            for (Object jCell : edit.getChanged()) {
                if (jCell instanceof JCell) {
                    @SuppressWarnings("unchecked") JCell<G> graphJCell = (JCell<G>) jCell;
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
     * merged (i.e. mapped to the same GraphJEdge). Override in subclass to
     * enable this behaviour.
     */
    public boolean isMergeBidirectionalEdges() {
        return getJGraph().isShowBidirectionalEdges();
    }

    /**
     * Returns whether all edges should be
     * merged (i.e. mapped to the same GraphJEdge). Override in subclass to
     * enable this behaviour.
     */
    public boolean isMergeAllEdges() {
        return getJGraph().isShowArrowsOnLabels();
    }

    /**
     * Creates a j-cell corresponding to a given node in the graph. Adds the
     * j-cell to {@link #addedJEdges}, and updates {@link #nodeJCellMap}.
     */
    protected JVertex<G> addNode(Node node) {
        JVertex<G> jVertex = computeJVertex(node);
        // we add nodes in front of the list to get them in front of the display
        this.addedJVertices.add(jVertex);
        JVertex<G> oldNode = this.nodeJCellMap.put(node, jVertex);
        assert oldNode == null;
        return jVertex;
    }

    /**
     * Creates a j-cell corresponding to a given graph edge. This may be a
     * j-vertex, if the edge can be graphically depicted by that vertex; or an
     * existing j-edge, if the edge can be represented by it. Otherwise, it will
     * be a new j-edge.
     */
    protected JCell<G> addEdge(Edge edge) {
        JCell<G> result = this.edgeJCellMap.get(edge);
        // check if edge was processed earlier
        JVertex<G> sourceJVertex = getJCellForNode(edge.source());
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
            // try to add the edge to an existing JEdge
            Iterator<? extends JEdge<G>> edgeIter = getJEdges(sourceJVertex);
            while (edgeIter.hasNext()) {
                JEdge<G> jEdge = edgeIter.next();
                if (jEdge.isCompatible(edge)) {
                    // yes, the edge could be added here; we're done
                    jEdge.addEdge(edge);
                    result = jEdge;
                    break;
                }
            }
        }
        if (result == null) {
            // none of the above: so create a new JEdge
            JEdge<G> jEdge;
            result = jEdge = computeJEdge(edge);
            // put the edge at the end to make sure it goes to the back
            this.addedJEdges.add(jEdge);
            JVertex<G> targetJVertex = getJCellForNode(edge.target());
            assert targetJVertex != null : "No vertex for target node of " + edge;
            this.connections.connect(result, sourceJVertex.getPort(), targetJVertex.getPort());
            addFreshJEdge(sourceJVertex, jEdge);
            addFreshJEdge(targetJVertex, jEdge);
        }
        this.edgeJCellMap.put(edge, result);
        return result;
    }

    /**
     * Retrieves the known incident JEdges of a given JVetex,
     * either from the explicitly stored JEdges (if the JVertex is fresh)
     * or from the stored context of the JVertex.
     */
    private Iterator<? extends JEdge<G>> getJEdges(JVertex<G> jVertex) {
        Iterator<? extends JEdge<G>> result;
        Set<JEdge<G>> outJEdges = this.freshJEdges.get(jVertex);
        if (outJEdges == null) {
            result = jVertex.getContext();
        } else {
            result = new NestedIterator<>(outJEdges.iterator(), jVertex.getContext());
        }
        return result;
    }

    /**
     * Adds a given JEdge to the fresh incident edges of a JVertex.
     */
    private void addFreshJEdge(JVertex<G> jVertex, JEdge<G> jEdge) {
        Set<JEdge<G>> jEdges = this.freshJEdges.get(jVertex);
        if (jEdges == null) {
            this.freshJEdges.put(jVertex, jEdges = new HashSet<>());
        }
        jEdges.add(jEdge);
    }

    /**
     * Creates a new j-edge using {@link #createJEdge(Edge)},  and adds available
     * layout information from the layout map stored in this model.
     * @param edge graph edge for which a corresponding j-edge is to be created
     */
    protected JEdge<G> computeJEdge(Edge edge) {
        JEdge<G> result = createJEdge(edge);
        JEdgeLayout layout = getLayoutMap().getLayout(edge);
        if (layout != null) {
            result.putVisuals(layout.toVisuals());
        }
        return result;
    }

    /**
     * Creates a new j-vertex using {@link #createJVertex(Node)}, and adds available
     * layout information from the layout map stored in this model; or adds a
     * random position otherwise.
     * @param node graph node for which a corresponding j-vertex is to be
     *        created
     */
    final protected JVertex<G> computeJVertex(Node node) {
        JVertex<G> result = createJVertex(node);
        JVertexLayout layout = getLayoutMap().getLayout(node);
        if (layout != null) {
            result.putVisuals(layout.toVisuals());
        } else {
            Point2D nodePos = new Point2D.Double(this.nodeX, this.nodeY);
            result.putVisual(VisualKey.NODE_POS, nodePos);
            this.nodeX = randomCoordinate();
            this.nodeY = randomCoordinate();
            result.setLayoutable(true);
        }
        return result;
    }

    /**
     * Factory method for JEdges.
     * @param edge graph edge for which a corresponding JEdge is to be created;
     * may be {@code null} if there is initially no edge
     * @return a fresh JEedge wrapping <tt>edge</tt>, initialised on this model
     */
    protected JEdge<G> createJEdge(Edge edge) {
        JEdge<G> result = getJGraph().getFactory()
            .newJEdge(edge);
        result.setJModel(this);
        result.initialise();
        if (edge != null) {
            result.addEdge(edge);
        }
        return result;
    }

    /**
     * Factory method for JVertices initialised on this JModel.
     */
    final protected JVertex<G> createJVertex(Node node) {
        JVertex<G> result = getJGraph().getFactory()
            .newJVertex(node);
        result.setJModel(this);
        result.setNode(node);
        result.initialise();
        return result;
    }

    /**
     * Sets the transient variables (cells, attributes and connections) to fresh
     * (empty) initial values.
     */
    protected void prepareInsert() {
        this.addedJEdges.clear();
        this.addedJVertices.clear();
        this.freshJEdges.clear();
        this.connections = new ConnectionSet();
    }

    /**
     * Executes the insertion prepared by node and edge additions.
     * Optionally sends the new elements to the back
     * @param replace if {@code true}, the old roots should be deleted
     */
    @SuppressWarnings("unchecked")
    protected void doInsert(boolean replace) {
        int vertexCount = this.addedJVertices.size();
        int edgeCount = this.addedJEdges.size();
        Object[] addedCells = new JCell<?>[vertexCount + edgeCount];
        for (int i = 0; i < edgeCount; i++) {
            addedCells[i] = this.addedJEdges.get(i);
        }
        for (int i = 0; i < vertexCount; i++) {
            addedCells[edgeCount + i] = this.addedJVertices.get(i);
        }
        Object[] removedCells = replace ? getRoots().toArray() : null;
        createEdit(addedCells, removedCells, null, this.connections, getParentMap(), null)
            .execute();
        List<JEdge<G>> edges = new ArrayList<>();
        for (Object jCell : addedCells) {
            if (jCell instanceof JEdge) {
                edges.add((JEdge<G>) jCell);
            }
        }
    }

    /**
     * Returns the parent map for hierarchical graphs, default to null.
     * To be overriden in derived classes.
     */
    protected ParentMap getParentMap() {
        return null;
    }

    /**
     * Returns a random number bounded by <tt>toJCellMap.size()</tt>. Used to
     * generate a random position for any added j-vertex without layout
     * information.
     */
    @SuppressWarnings("unchecked")
    protected int randomCoordinate() {
        Rectangle2D bounds = new Rectangle();
        for (Object cell : getJGraph().getSelectionCells()) {
            if (cell instanceof JVertex) {
                bounds.add(((JVertex<G>) cell).getVisuals()
                    .getNodePos());
            }
        }
        return 25 + randomGenerator
            .nextInt((this.nodeJCellMap.size() + this.edgeJCellMap.size()) * 5 + 1);
    }

    /** The JGraph to which this model belongs. */
    private final JGraph<G> jGraph;
    /**
     * The underlying Graph of this GraphModel.
     * @invariant graph != null
     */
    private G graph;
    /**
     * The layout map for the underlying graph. It maps {@link Element}s to
     * {@link JCellLayout}s. This is set to an empty map if the graph is not a
     * layed out graph.
     */
    private LayoutMap layoutMap;

    /** Changes the loading status of the JGraph. */
    private void setLoading(boolean loading) {
        this.loading = loading;
    }

    /** Indicates if the JModel is currently in the process of loading a graph. */
    protected boolean isLoading() {
        return this.loading;
    }

    /** Flag that indicates we're in the process of loading a graph. */
    private boolean loading;
    /**
     * Map from graph nodes to JGraph cells.
     */
    protected Map<Node,JVertex<G>> nodeJCellMap = new HashMap<>();
    /**
     * Map from graph edges to JGraph cells.
     */
    protected Map<Edge,JCell<G>> edgeJCellMap = new HashMap<>();

    /**
     * Mapping from jVertices to incident jEdges.
     * Used in the process of constructing a GraphJModel.
     */
    protected final Map<JVertex<G>,Set<JEdge<G>>> freshJEdges = new HashMap<>();
    /**
     * Set of added jEdges. Used in the process of constructing a
     * GraphJModel.
     */
    protected final List<JEdge<G>> addedJEdges = new ArrayList<>();
    /**
     * Set of added jVertices. Used in the process of constructing a
     * GraphJModel.
     */
    protected final List<JVertex<G>> addedJVertices = new ArrayList<>();
    /**
     * Set of GraphModel connections. Used in the process of constructing a
     * GraphJModel.
     */
    protected ConnectionSet connections;

    /** See {@link #setVetoFireGraphChanged(boolean)}. */
    private boolean vetoFireGraphChanged;
    /**
     * Counter to provide the x-coordinate of fresh nodes with fresh values
     */
    protected transient int nodeX;
    /**
     * Counter to provide the y-coordinate of fresh nodes with fresh values
     */
    protected transient int nodeY;

    /** Random generator for coordinates of new nodes. */
    private static final Random randomGenerator = new Random();
}