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

import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.Graph;
import groove.graph.GraphInfo;
import groove.graph.Node;
import groove.gui.Options;
import groove.gui.layout.JCellLayout;
import groove.gui.layout.JEdgeLayout;
import groove.gui.layout.JVertexLayout;
import groove.gui.layout.LayoutMap;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.GraphConstants;

/**
 * Implements jgraph's GraphModel interface on top of a groove graph. The
 * resulting GraphModel should only be edited through the Graph interface.
 * @author Arend Rensink
 * @version $Revision$
 */
public class GraphJModel<N extends Node,E extends Edge<N>> extends JModel {
    /**
     * Creates a new GraphJModel instance on top of a given Graph, with given
     * node and edge attributes, and an indication whether self-edges should be
     * displayed as node labels. The node and edge attribute maps are cloned.
     * @param options specifies options for the visual display If false, node
     *        labels are used to display self edges.
     * @require graph != null, nodeAttr != null, edgeAttr != null;
     */
    protected GraphJModel(Options options) {
        super(options);
    }

    /**
     * Constructor for a dummy (empty) model.
     */
    GraphJModel() {
        super(null);
    }

    /**
     * If the name is not explicitly set, obtains the name of the underlying
     * graph as set in the graph properties.
     */
    @Override
    public String getName() {
        return getGraph() == null ? null : getGraph().getName();
    }

    /**
     * Returns the underlying Graph of this GraphModel.
     * @ensure result != null
     */
    public Graph<N,E> getGraph() {
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
     * changes the {@link JCell}s of the {@link JModel}, as well as the
     * mapping from graph elements to {@link JCell}s. 
     */
    void setGraph(Graph<N,E> graph,
            Map<N,? extends GraphJVertex<N,E>> nodeJCellMap,
            Map<E,? extends GraphJCell<N,E>> edgeJCellMap) {
        this.graph = graph;
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
        this.graph = graph;
        LayoutMap<N,E> layoutMap = GraphInfo.getLayoutMap(graph);
        this.layoutMap = layoutMap == null ? new LayoutMap<N,E>() : layoutMap;
        this.nodeJCellMap.clear();
        this.edgeJCellMap.clear();
        // add nodes from Graph to GraphModel
        prepareInsert();
        for (N node : graph.nodeSet()) {
            addNode(node);
        }
        for (E edge : graph.edgeSet()) {
            addEdge(edge);
        }
        doInsert(false);
    }

    /** Specialises the type to a list of {@link GraphJCell}s. */
    @Override
    @SuppressWarnings("unchecked")
    public List<? extends GraphJCell<N,E>> getRoots() {
        return (List<? extends GraphJCell<N,E>>) super.getRoots();
    }

    /**
     * Returns the set of graph edges between two given graph nodes.
     */
    public Set<E> getEdgesBetween(N source, N target) {
        Set<E> result = new HashSet<E>();
        for (Map.Entry<E,? extends JCell> cellEntry : this.edgeJCellMap.entrySet()) {
            Object cell = cellEntry.getValue();
            if (cell instanceof GraphJEdge) {
                @SuppressWarnings("unchecked")
                GraphJEdge<N,E> jEdge = (GraphJEdge<N,E>) cell;
                if (jEdge.getSourceNode() == source
                    && jEdge.getTargetNode() == target) {
                    result.add(cellEntry.getKey());
                }
            }
        }
        return result;
    }

    /**
     * Returns the set of {@link JCell}s associated with a given set of graph
     * elements.
     * @param elemSet the set of elements for which the jcells are requested
     * @return the jcells associated with <tt>elemSet</tt>
     * @see #getJCell(Element)
     */
    public Set<JCell> getJCellSet(Set<Element> elemSet) {
        Set<JCell> result = new HashSet<JCell>();
        for (Element elem : elemSet) {
            JCell image = getJCell(elem);
            if (image != null) {
                result.add(getJCell(elem));
            }
        }
        return result;
    }

    /**
     * Returns the {@link JCell}associated with a given graph element. The
     * result is a {@link GraphJVertex}for which the graph element is the
     * underlying node or self-edge, or a {@link GraphJEdge}for which the graph
     * element is an underlying edge.
     * @param elem the graph element for which the jcell is requested
     * @return the jcell associated with <tt>elem</tt>
     */
    public final GraphJCell<N,E> getJCell(Element elem) {
        if (elem instanceof Node) {
            return getJCellForNode((Node) elem);
        } else {
            return getJCellForEdge((Edge<?>) elem);
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
    public GraphJCell<N,E> getJCellForEdge(Edge<?> edge) {
        return this.edgeJCellMap.get(edge);
    }

    /**
     * Returns the JNode associated with a given node.
     * @param node the graph node we're interested in
     * @return the JNode modelling node (if node is known)
     * @ensure result == null || result.getUserObject() == node
     */
    public GraphJVertex<N,E> getJCellForNode(Node node) {
        return this.nodeJCellMap.get(node);
    }

    /** Stores the layout from the JModel back into the graph. */
    public void synchroniseLayout(GraphJCell<N,E> jCell) {
        LayoutMap<N,E> currentLayout = GraphInfo.getLayoutMap(getGraph());
        // create the layout map if it does not yet exist
        if (currentLayout == null) {
            currentLayout = new LayoutMap<N,E>();
            GraphInfo.setLayoutMap(getGraph(), currentLayout);
        }
        if (jCell instanceof GraphJEdge) {
            for (E edge : ((GraphJEdge<N,E>) jCell).getEdges()) {
                currentLayout.putEdge(edge, jCell.getAttributes());
            }
        } else {
            currentLayout.putNode(((GraphJVertex<N,E>) jCell).getNode(),
                jCell.getAttributes());
        }
    }

    /**
     * Creates a j-cell corresponding to a given node in the graph. Adds the
     * j-cell to {@link #addedJCells}, and updates {@link #nodeJCellMap}.
     */
    protected GraphJVertex<N,E> addNode(N node) {
        GraphJVertex<N,E> jVertex = computeJVertex(node);
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
     */
    protected GraphJCell<N,E> addEdge(E edge) {
        if (isUnaryEdge(edge)) {
            GraphJVertex<N,E> jVertex = getJCellForNode(edge.source());
            if (jVertex.addSelfEdge(edge)) {
                // yes, the edge could be added here; we're done
                this.edgeJCellMap.put(edge, jVertex);
                return jVertex;
            }
        }
        N source = edge.source();
        N target = edge.target();
        // maybe a JEdge between this source and target is already in the
        // JGraph
        Set<GraphJEdge<N,E>> outJEdges = this.addedOutJEdges.get(source);
        if (outJEdges == null) {
            this.addedOutJEdges.put(source, outJEdges =
                new HashSet<GraphJEdge<N,E>>());
        }
        for (GraphJEdge<N,E> jEdge : outJEdges) {
            if (jEdge.getTargetNode() == target
                && isLayoutCompatible(jEdge, edge) && jEdge.addEdge(edge)) {
                // yes, the edge could be added here; we're done
                this.edgeJCellMap.put(edge, jEdge);
                return jEdge;
            }
        }
        // none of the above: so create a new JEdge
        GraphJEdge<N,E> jEdge = computeJEdge(edge);
        // put the edge at the end to make sure it goes to the back
        this.addedJCells.add(jEdge);
        outJEdges.add(jEdge);
        this.edgeJCellMap.put(edge, jEdge);
        GraphJVertex<N,E> sourceNode = getJCellForNode(source);
        assert sourceNode != null : "No vertex for source node of " + edge;
        GraphJVertex<N,E> targetPort = getJCellForNode(target);
        assert targetPort != null : "No vertex for target node of " + edge;
        this.connections.connect(jEdge, sourceNode.getPort(),
            targetPort.getPort());
        return jEdge;
    }

    /**
     * Tests if a given edge may be added to its source vertex.
     */
    protected boolean isUnaryEdge(E edge) {
        return !edge.label().isBinary();
    }

    /**
     * Tests if a given edge may be added to an existing jedge, as far as the
     * available layout information is concerned. The two are compatible if the
     * layout information for the edge equals that for the (edges contained in
     * the) jedge, or both are <tt>null</tt>.
     * @param jEdge the jedge to which the edge is about to be added
     * @param edge the edge that is investigated for compatibility
     */
    protected boolean isLayoutCompatible(GraphJEdge<N,E> jEdge, E edge) {
        JCellLayout edgeLayout = this.layoutMap.getLayout(edge);
        JCellLayout jEdgeLayout = this.layoutMap.getLayout(jEdge.getEdge());
        if (edgeLayout == null) {
            return jEdgeLayout == null;
        } else {
            return jEdgeLayout != null && edgeLayout.equals(jEdgeLayout);
        }
    }

    /**
     * Creates a new j-edge using {@link #createJEdge(Edge)}, and sets the
     * attributes using {@link JEdge#createAttributes()} and adds available
     * layout information from the layout map stored in this model.
     * @param edge graph edge for which a corresponding j-edge is to be created
     */
    protected GraphJEdge<N,E> computeJEdge(E edge) {
        GraphJEdge<N,E> result = createJEdge(edge);
        result.getAttributes().applyMap(result.createAttributes(this));
        JEdgeLayout layout = this.layoutMap.getLayout(edge);
        if (layout != null) {
            result.getAttributes().applyMap(layout.toJAttr());
        }
        return result;
    }

    /**
     * Creates a new j-vertex using {@link #createJVertex(Node)}, and sets the
     * attributes using {@link JVertex#createAttributes()} and adds available
     * layout information from the layout map stored in this model; or adds a
     * random position otherwise.
     * @param node graph node for which a corresponding j-vertex is to be
     *        created
     */
    protected GraphJVertex<N,E> computeJVertex(N node) {
        GraphJVertex<N,E> result = createJVertex(node);
        result.getAttributes().applyMap(result.createAttributes(this));
        if (GraphConstants.isMoveable(result.getAttributes())) {
            JVertexLayout layout = this.layoutMap.getLayout(node);
            if (layout != null) {
                result.getAttributes().applyMap(layout.toJAttr());
            } else {
                this.layoutableJCells.add(result);
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
     * @param edge graph edge for which a corresponding j-edge is to be created
     * @return j-edge corresponding to <tt>edge</tt>
     * @ensure <tt>result.getEdgeSet().contains(edge)</tt>
     */
    protected GraphJEdge<N,E> createJEdge(E edge) {
        return new GraphJEdge<N,E>(this, edge);
    }

    /**
     * Factory method for jgraph nodes.
     * @param node graph node for which a corresponding j-node is to be created
     * @return j-node corresponding to <tt>node</tt>
     * @ensure <tt>result.getNode().equals(node)</tt>
     */
    protected GraphJVertex<N,E> createJVertex(N node) {
        return new GraphJVertex<N,E>(this, node, true);
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
     */
    protected void doInsert(boolean toBack) {
        Object[] addedCells = this.addedJCells.toArray();
        createEdit(addedCells, null, null, this.connections, null, null).execute();
        if (toBack) {
            // new edges should be behind the nodes
            toBack(addedCells);
        }
    }

    /**
     * Returns a random number bounded by <tt>toJCellMap.size()</tt>. Used to
     * generate a random position for any added j-vertex without layout
     * information.
     */
    private int randomCoordinate() {
        return randomGenerator.nextInt((this.nodeJCellMap.size() + this.edgeJCellMap.size()) * 5 + 1);
    }

    /**
     * Indicates whether aspect prefixes should be shown for nodes and edges.
     */
    boolean isShowNodeIdentities() {
        return getOptionValue(Options.SHOW_NODE_IDS_OPTION);
    }

    /**
     * Indicates whether unfiltered edges to filtered nodes should remain
     * visible.
     */
    boolean isShowUnfilteredEdges() {
        return getOptionValue(Options.SHOW_UNFILTERED_EDGES_OPTION);
    }

    /**
     * Indicates whether self-edges should be shown as node labels.
     */
    boolean isShowVertexLabels() {
        return this.showVertexLabels
            || getOptionValue(Options.SHOW_VERTEX_LABELS_OPTION);
    }

    /** Option to make sure vertex labels are shown, 
     * irregardless of the option value.
     */
    public void setShowVertexLabels() {
        this.showVertexLabels = true;
    }

    /** Private flag to show vertex labels irregardless of the menu option. */
    private boolean showVertexLabels;

    /**
     * Indicates whether anchors should be shown in the rule and lts views.
     */
    boolean isShowAnchors() {
        return getOptionValue(Options.SHOW_ANCHORS_OPTION);
    }

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
    /**
     * Map from graph nodes to JGraph cells.
     */
    private Map<N,GraphJVertex<N,E>> nodeJCellMap =
        new HashMap<N,GraphJVertex<N,E>>();
    /**
     * Map from graph edges to JGraph cells.
     */
    private Map<E,GraphJCell<N,E>> edgeJCellMap =
        new HashMap<E,GraphJCell<N,E>>();

    /**
     * Mapping from graph nodes to JEdges for outgoing edges.
     * Used in the process of constructing a GraphJModel.
     */
    private final Map<N,Set<GraphJEdge<N,E>>> addedOutJEdges =
        new HashMap<N,Set<GraphJEdge<N,E>>>();
    /**
     * Set of GraphModel cells. Used in the process of constructing a
     * GraphJModel.
     * @invariant addedCells \subseteq org.jgraph.graph.DefaultGraphCell
     */
    private final List<JCell> addedJCells = new LinkedList<JCell>();
    /**
     * Set of GraphModel connections. Used in the process of constructing a
     * GraphJModel.
     */
    private ConnectionSet connections;

    /**
     * Counter to provide the x-coordinate of fresh nodes with fresh values
     */
    private transient int nodeX;
    /**
     * Counter to provide the y-coordinate of fresh nodes with fresh values
     */
    private transient int nodeY;

    /**
     * Creates a new GraphJModel instance on top of a given Graph.
     * Self-edges will be
     * displayed as node labels.
     * 
     * @param graph the underlying Graph
     * @param options display options
     */
    static public <N extends Node,E extends Edge<N>> GraphJModel<N,E> newInstance(
            Graph<N,E> graph, Options options) {
        GraphJModel<N,E> result = new GraphJModel<N,E>(options);
        return result;
    }

    /** Random generator for coordinates of new nodes. */
    private static final Random randomGenerator = new Random();
}