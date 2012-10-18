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
 * $Id: GraphJModel.java,v 1.21 2008-02-29 11:02:19 fladder Exp $
 */

package groove.gui.jgraph;

import static groove.graph.EdgeRole.BINARY;
import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.Graph;
import groove.graph.GraphInfo;
import groove.graph.Node;
import groove.gui.layout.JCellLayout;
import groove.gui.layout.JEdgeLayout;
import groove.gui.layout.JVertexLayout;
import groove.gui.layout.LayoutMap;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.jgraph.event.GraphModelEvent.GraphModelChange;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.GraphConstants;

/**
 * Implements jgraph's GraphModel interface on top of a groove graph. The
 * resulting GraphModel should only be edited through the Graph interface.
 * @author Arend Rensink
 * @version $Revision$
 */
public class GraphJModel<N extends Node,E extends Edge> extends
        DefaultGraphModel {
    /**
     * Creates a new GraphJModel instance on top of a given GraphJGraph, with given
     * node and edge attributes, and an indication whether self-edges should be
     * displayed as node labels. The node and edge attribute maps are cloned.
     * @param jVertexProt prototype object for JVertices of this model
     * @param jEdgeProt prototype object for JEdges of this model
     */
    protected GraphJModel(GraphJGraph jGraph, GraphJVertex jVertexProt,
            GraphJEdge jEdgeProt) {
        this.jGraph = jGraph;
        this.jVertexProt = jVertexProt;
        this.jEdgeProt = jEdgeProt;
    }

    /** Returns the JGraph in which this model belongs. */
    public GraphJGraph getJGraph() {
        return this.jGraph;
    }

    /** Specialises the type to a list of {@link GraphJCell}s. */
    @Override
    @SuppressWarnings("unchecked")
    public List<? extends GraphJCell> getRoots() {
        return super.getRoots();
    }

    /** Returns the size of the graph, as a sum of the number of nodes and edges. */
    public int size() {
        return this.nodeJCellMap.size() + this.edgeJCellMap.size();
    }

    /**
     * Sends a set of cells to the back (in the z-order) without posting an edit.
     */
    void toBackSilent(Set<GraphJCell> jCells) {
        createLayerEdit(jCells.toArray(), GraphModelLayerEdit.BACK).execute();
    }

    @Override
    public AttributeMap getAttributes(Object node) {
        AttributeMap result;
        if (node instanceof GraphJCell) {
            ((GraphJCell) node).refreshAttributes();
            result = ((GraphJCell) node).getAttributes();
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
    public Graph<?,?> getGraph() {
        return this.graph;
    }

    /** 
     * Returns the (non-{@code null}) layout map of the graph.
     * This is retrieved from {@link GraphInfo#getLayoutMap(Graph)}. 
     */
    public LayoutMap<N,E> getLayoutMap() {
        return this.layoutMap;
    }

    /** 
     * Changes the underlying graph to the one passed in as a parameter.
     * Note that this should only be done as part of an action that also
     * changes the {@link GraphJCell}s of the {@link GraphJModel}, as well as the
     * mapping from graph elements to {@link GraphJCell}s. 
     */
    void setGraph(Graph<N,E> graph) {
        this.graph = graph;
    }

    /** 
     * Changes the underlying graph to the one passed in as a parameter.
     * Note that this should only be done as part of an action that also
     * changes the {@link GraphJCell}s of the {@link GraphJModel}, as well as the
     * mapping from graph elements to {@link GraphJCell}s. 
     */
    void setGraph(Graph<N,E> graph, Map<N,? extends GraphJVertex> nodeJCellMap,
            Map<E,? extends GraphJCell> edgeJCellMap) {
        setGraph(graph);
        this.nodeJCellMap.clear();
        this.nodeJCellMap.putAll(nodeJCellMap);
        this.edgeJCellMap.clear();
        this.edgeJCellMap.putAll(edgeJCellMap);
    }

    /**
     * Loads in a given graph, adding any nodes and edges not yet in this
     * model. Also adds the model as a listener to the graph again. This may be
     * necessary if the model was removed as a graph listener, for instance for
     * the sake of efficiency.
     */
    public void loadGraph(Graph<N,E> graph) {
        prepareLoad(graph);
        prepareInsert();
        boolean merge = mergeBidirectionalEdges();
        for (N node : graph.nodeSet()) {
            addNode(node);
        }
        for (E edge : graph.edgeSet()) {
            addEdge(edge, merge);
        }
        doInsert(true, false);
    }

    /**
     * Prepare the object fields for loading a new graph.
     */
    protected void prepareLoad(Graph<N,E> graph) {
        this.graph = graph;
        this.loading = true;
        this.layoutMap = GraphInfo.getInfo(graph, true).getLayoutMap();
        this.nodeJCellMap.clear();
        this.edgeJCellMap.clear();
    }

    /**
     * Returns the {@link GraphJCell}associated with a given graph element. The
     * result is a {@link GraphJVertex}for which the graph element is the
     * underlying node or self-edge, or a {@link GraphJEdge}for which the graph
     * element is an underlying edge.
     * @param elem the graph element for which the jcell is requested
     * @return the jcell associated with <tt>elem</tt>
     */
    public GraphJCell getJCell(Element elem) {
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
    public GraphJCell getJCellForEdge(Edge edge) {
        return this.edgeJCellMap.get(edge);
    }

    /**
     * Returns the JNode associated with a given node.
     * @param node the graph node we're interested in
     * @return the JNode modelling node (if node is known)
     * @ensure result == null || result.getUserObject() == node
     */
    public GraphJVertex getJCellForNode(Node node) {
        return this.nodeJCellMap.get(node);
    }

    /** Stores the layout from the JModel back into the graph. */
    @SuppressWarnings("unchecked")
    public void synchroniseLayout(GraphJCell jCell) {
        LayoutMap<N,E> currentLayout = GraphInfo.getLayoutMap(getGraph());
        if (jCell instanceof GraphJEdge) {
            for (Edge edge : ((GraphJEdge) jCell).getEdges()) {
                currentLayout.putEdge((E) edge, jCell.getAttributes());
            }
        } else if (jCell instanceof GraphJVertex) {
            currentLayout.putNode((N) ((GraphJVertex) jCell).getNode(),
                jCell.getAttributes());
        }
    }

    /** Retrieves a mapping from graph nodes to foreground colours
     * as stored in the corresponding {@link GraphJVertex} attributes.
     */
    @SuppressWarnings("unchecked")
    public Map<N,Color> getColorMap() {
        Map<N,Color> result = new HashMap<N,Color>();
        for (GraphJCell jCell : getRoots()) {
            if (jCell instanceof GraphJVertex) {
                Color foreground =
                    GraphConstants.getForeground(jCell.getAttributes());
                if (foreground != null) {
                    result.put((N) ((GraphJVertex) jCell).getNode(), foreground);
                }
            }
        }
        return result;
    }

    @Override
    protected void fireGraphChanged(Object source, GraphModelChange edit) {
        if (!this.loading) {
            // if we're loading, the layout is actually taken from the graph
            // so no synchronisation is necessary
            for (Object jCell : edit.getChanged()) {
                if (jCell instanceof GraphJCell) {
                    GraphJCell graphJCell = (GraphJCell) jCell;
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
     * enable this behavior.
     * @see AspectJModel
     */
    public boolean mergeBidirectionalEdges() {
        return false;
    }

    /**
     * Creates a j-cell corresponding to a given node in the graph. Adds the
     * j-cell to {@link #addedJCells}, and updates {@link #nodeJCellMap}.
     */
    protected GraphJVertex addNode(N node) {
        GraphJVertex jVertex = computeJVertex(node);
        // we add nodes in front of the list to get them in front of the display
        this.addedJCells.add(0, jVertex);
        this.nodeJCellMap.put(node, jVertex);
        return jVertex;
    }

    /**
     * Creates a j-cell corresponding to a given graph edge. This may be a
     * j-vertex, if the edge can be graphically depicted by that vertex; or an
     * existing j-edge, if the edge can be represented by it. Otherwise, it will
     * be a new j-edge.
     * @param mergeBidirectional flag to indicate whether bidirectional edges
     *                           should be merged into one GraphJEdge
     */
    @SuppressWarnings("unchecked")
    protected GraphJCell addEdge(E edge, boolean mergeBidirectional) {
        // check if edge was processed earlier
        if (this.edgeJCellMap.containsKey(edge)) {
            return this.edgeJCellMap.get(edge);
        }
        // try to add the edge as vertex label to its source vertex
        if (edge.source() == edge.target()
            && (edge.getRole() != BINARY || getLayoutMap().getLayout(edge) == null)) {
            GraphJVertex jVertex = getJCellForNode(edge.source());
            if (jVertex.addJVertexLabel(edge)) {
                // yes, the edge could be added here; we're done
                this.edgeJCellMap.put(edge, jVertex);
                return jVertex;
            }
        }
        N source = (N) edge.source();
        N target = (N) edge.target();
        // check for bidirectional edges
        E opposite = null;
        if (mergeBidirectional && !source.equals(target)) {
            for (E candidate : this.graph.outEdgeSet(target)) {
                if (candidate.target().equals(source)
                    && candidate.label().equals(edge.label())) {
                    opposite = candidate;
                }
            }
        }
        // maybe a JEdge between this source and target is already in the
        // JGraph
        Set<GraphJEdge> outJEdges = this.addedOutJEdges.get(source);
        if (opposite == null) {
            if (outJEdges == null) {
                this.addedOutJEdges.put(source, outJEdges =
                    new HashSet<GraphJEdge>());
            }
            for (GraphJEdge jEdge : outJEdges) {
                if (jEdge.getTargetNode() == target
                    && isLayoutCompatible(jEdge, edge) && jEdge.addEdge(edge)) {
                    // yes, the edge could be added here; we're done
                    this.edgeJCellMap.put(edge, jEdge);
                    return jEdge;
                }
            }
        }
        // none of the above: so create a new JEdge
        GraphJEdge jEdge = computeJEdge(edge, opposite != null);
        // put the edge at the end to make sure it goes to the back
        this.addedJCells.add(jEdge);
        // store mapping of edge to jedge(s)
        this.edgeJCellMap.put(edge, jEdge);
        if (opposite == null) {
            outJEdges.add(jEdge);
        } else {
            this.edgeJCellMap.put(opposite, jEdge);
        }
        // verification
        GraphJVertex sourceNode = getJCellForNode(source);
        assert sourceNode != null : "No vertex for source node of " + edge;
        GraphJVertex targetNode = getJCellForNode(target);
        assert targetNode != null : "No vertex for target node of " + edge;
        this.connections.connect(jEdge, sourceNode.getPort(),
            targetNode.getPort());
        return jEdge;
    }

    /**
     * Tests if a given edge may be added to an existing jedge, as far as the
     * available layout information is concerned. The two are compatible if the
     * layout information for the edge equals that for the (edges contained in
     * the) jedge, or both are <tt>null</tt>.
     * @param jEdge the jedge to which the edge is about to be added
     * @param edge the edge that is investigated for compatibility
     */
    protected boolean isLayoutCompatible(GraphJEdge jEdge, E edge) {
        JCellLayout edgeLayout = this.layoutMap.getLayout(edge);
        @SuppressWarnings("unchecked")
        JCellLayout jEdgeLayout = this.layoutMap.getLayout((E) jEdge.getEdge());
        if (edgeLayout == null) {
            return jEdgeLayout == null;
        } else {
            return jEdgeLayout != null && edgeLayout.equals(jEdgeLayout);
        }
    }

    /**
     * Creates a new j-edge using {@link #createJEdge(Edge)}, and sets the
     * attributes using {@link GraphJEdge#createAttributes()} and adds available
     * layout information from the layout map stored in this model.
     * @param edge graph edge for which a corresponding j-edge is to be created
     * @param bidirectional flag that indicates if the edge is bidirectional
     */
    protected GraphJEdge computeJEdge(E edge, boolean bidirectional) {
        GraphJEdge result = createJEdge(edge);
        result.setBidirectional(bidirectional);
        result.refreshAttributes();
        JEdgeLayout layout = this.layoutMap.getLayout(edge);
        if (layout != null) {
            result.getAttributes().applyMap(layout.toJAttr());
        }
        return result;
    }

    /**
     * Creates a new j-vertex using {@link #createJVertex(Node)}, and sets the
     * attributes using {@link GraphJVertex#createAttributes()} and adds available
     * layout information from the layout map stored in this model; or adds a
     * random position otherwise.
     * @param node graph node for which a corresponding j-vertex is to be
     *        created
     */
    protected GraphJVertex computeJVertex(N node) {
        GraphJVertex result = createJVertex(node);
        result.refreshAttributes();
        if (GraphConstants.isMoveable(result.getAttributes())) {
            JVertexLayout layout = this.layoutMap.getLayout(node);
            if (layout != null) {
                result.getAttributes().applyMap(layout.toJAttr());
            } else {
                Rectangle newBounds =
                    new Rectangle(this.nodeX, this.nodeY,
                        JAttr.DEFAULT_NODE_BOUNDS.width,
                        JAttr.DEFAULT_NODE_BOUNDS.height);
                GraphConstants.setBounds(result.getAttributes(), newBounds);
                this.nodeX = randomCoordinate();
                this.nodeY = randomCoordinate();
            }
        }
        return result;
    }

    /**
     * Factory method for jgraph edges.
     * 
     * @param edge graph edge for which a corresponding JEdge is to be created;
     * may be {@code null} if there is initially no edge
     * @return j-edge corresponding to <tt>edge</tt>
     * @ensure <tt>result.getEdgeSet().contains(edge)</tt>
     */
    protected GraphJEdge createJEdge(E edge) {
        return this.jEdgeProt.newJEdge(this, edge);
    }

    /**
     * Factory method for jgraph nodes.
     * @param node graph node for which a corresponding j-node is to be created
     * @return j-node corresponding to <tt>node</tt>
     * @ensure <tt>result.getNode().equals(node)</tt>
     */
    protected GraphJVertex createJVertex(N node) {
        return this.jVertexProt.newJVertex(this, node);
    }

    /**
     * Sets the transient variables (cells, attributes and connections) to fresh
     * (empty) initial values.
     */
    protected void prepareInsert() {
        this.addedJCells.clear();
        this.addedOutJEdges.clear();
        this.connections = new ConnectionSet();
    }

    /**
     * Executes the insertion prepared by node and edge additions.
     * Optionally sends the new elements to the back
     * @param replace if {@code true}, the old roots should be deleted
     */
    protected void doInsert(boolean replace, boolean toBack) {
        Object[] addedCells = this.addedJCells.toArray();
        Object[] removedCells = replace ? getRoots().toArray() : null;
        createEdit(addedCells, removedCells, null, this.connections, null, null).execute();
        if (toBack) {
            // new edges should be behind the nodes
            toBack(addedCells);
        }
        this.loading = false;
    }

    /**
     * Returns a random number bounded by <tt>toJCellMap.size()</tt>. Used to
     * generate a random position for any added j-vertex without layout
     * information.
     */
    protected int randomCoordinate() {
        return randomGenerator.nextInt((this.nodeJCellMap.size() + this.edgeJCellMap.size()) * 5 + 1);
    }

    /** Prototype object for {@link GraphJEdge}s. */
    protected final GraphJEdge jEdgeProt;
    /** Prototype object for {@link GraphJVertex}s. */
    protected final GraphJVertex jVertexProt;
    /** The JGraph to which this model belongs. */
    private final GraphJGraph jGraph;
    /**
     * The underlying Graph of this GraphModel.
     * @invariant graph != null
     */
    private Graph<N,E> graph;
    /**
     * The layout map for the underlying graph. It maps {@link Element}s to
     * {@link JCellLayout}s. This is set to an empty map if the graph is not a
     * layed out graph.
     */
    private LayoutMap<N,E> layoutMap;
    /** Flag that indicates we're in the process of loading a graph. */
    private boolean loading;
    /**
     * Map from graph nodes to JGraph cells.
     */
    protected Map<N,GraphJVertex> nodeJCellMap = new HashMap<N,GraphJVertex>();
    /**
     * Map from graph edges to JGraph cells.
     */
    protected Map<E,GraphJCell> edgeJCellMap = new HashMap<E,GraphJCell>();

    /**
     * Mapping from graph nodes to JEdges for outgoing edges.
     * Used in the process of constructing a GraphJModel.
     */
    protected final Map<N,Set<GraphJEdge>> addedOutJEdges =
        new HashMap<N,Set<GraphJEdge>>();
    /**
     * Set of GraphModel cells. Used in the process of constructing a
     * GraphJModel.
     * @invariant addedCells \subseteq org.jgraph.graph.DefaultGraphCell
     */
    protected final List<GraphJCell> addedJCells = new LinkedList<GraphJCell>();
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