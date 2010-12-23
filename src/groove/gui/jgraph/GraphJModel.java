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

import groove.graph.AbstractGraph;
import groove.graph.DefaultGraph;
import groove.graph.DefaultNode;
import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.Graph;
import groove.graph.GraphInfo;
import groove.graph.GraphProperties;
import groove.graph.Label;
import groove.graph.Node;
import groove.graph.algebra.VariableNode;
import groove.gui.Options;
import groove.gui.layout.JCellLayout;
import groove.gui.layout.JEdgeLayout;
import groove.gui.layout.JVertexLayout;
import groove.gui.layout.LayoutMap;
import groove.trans.RuleLabel;
import groove.util.Groove;
import groove.view.aspect.AspectEdge;

import java.awt.Font;
import java.awt.Rectangle;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.GraphConstants;

/**
 * Implements jgraph's GraphModel interface on top of a groove graph. The
 * resulting GraphModel should only be edited through the Graph interface.
 * @author Arend Rensink
 * @version $Revision$
 */
public class GraphJModel<N extends Node,E extends Edge> extends JModel {
    /**
     * Creates a new GraphJModel instance on top of a given Graph, with given
     * node and edge attributes, and an indication whether self-edges should be
     * displayed as node labels. The node and edge attribute maps are cloned.
     * @param graph the underlying Graph
     * @param defaultNodeAttr the attributes for displaying nodes
     * @param defaultEdgeAttr the attributes for displaying edges
     * @param options specifies options for the visual display If false, node
     *        labels are used to display self edges.
     * @require graph != null, nodeAttr != null, edgeAttr != null;
     */
    protected GraphJModel(Graph<N,?,E> graph, AttributeMap defaultNodeAttr,
            AttributeMap defaultEdgeAttr, Options options) {
        // the model is to store attributes
        super(defaultNodeAttr, defaultEdgeAttr, options);
        // set the transient variables (cells, attributes and connections)
        // add nodes from Graph to GraphModel
        this.graph = graph;
        LayoutMap<N,E> layoutMap = GraphInfo.getLayoutMap(graph);
        this.layoutMap = layoutMap == null ? new LayoutMap<N,E>() : layoutMap;
    }

    /**
     * Creates a new GraphJModel instance on top of a given Graph. Node
     * attributes are given by NODE_ATTR and edge attributes by EDGE_ATTR.
     * Self-edges will be displayed as node labels.
     * @param graph the underlying Graph
     * @require graph != null;
     */
    GraphJModel(Graph<N,?,E> graph, Options options) {
        this(graph, JAttr.DEFAULT_NODE_ATTR, JAttr.DEFAULT_EDGE_ATTR, options);
    }

    /**
     * Constructor for a dummy (empty) model.
     */
    GraphJModel() {
        this(AbstractGraph.<N,Label,E>emptyGraph(), null);
    }

    /**
     * If the name is not explicitly set, obtains the name of the underlying
     * graph as set in the graph properties.
     */
    @Override
    public String getName() {
        String result = super.getName();
        if (result == null) {
            result = GraphInfo.getName(getGraph());
        }
        return result;
    }

    /**
     * Returns the underlying Graph of this GraphModel.
     * @ensure result != null
     */
    public Graph<N,?,E> getGraph() {
        return this.graph;
    }

    /**
     * Loads in the underlying graph, adding any nodes and edges not yet in this
     * model. Also adds the model as a listener to the graph again. This may be
     * necessary if the model was removed as a graph listener, for instance for
     * the sake of efficiency.
     */
    public void reload() {
        // add nodes from Graph to GraphModel
        initializeTransients();
        Set<N> addedNodeSet = new HashSet<N>(this.graph.nodeSet());
        addedNodeSet.removeAll(this.nodeJCellMap.keySet());
        addNodeSet(addedNodeSet);
        Set<E> addedEdgeSet = new HashSet<E>(this.graph.edgeSet());
        addedEdgeSet.removeAll(this.edgeJCellMap.keySet());
        addEdgeSet(addedEdgeSet);
        doInsert();
        GraphProperties graphProperties =
            GraphInfo.getProperties(this.graph, false);
        if (graphProperties != null) {
            setProperties(graphProperties);
        }
        String name = GraphInfo.getName(this.graph);
        if (name != null) {
            setName(name);
        }
    }

    /**
     * Returns the set of graph edges between two given graph nodes.
     */
    public Set<Edge> getEdgesBetween(N source, N target) {
        Set<Edge> result = new HashSet<Edge>();
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
    @SuppressWarnings("unchecked")
    public final JCell getJCell(Element elem) {
        if (elem instanceof Node) {
            return getJCellForNode((N) elem);
        } else {
            return getJCellForEdge((E) elem);
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
    public JCell getJCellForEdge(E edge) {
        return this.edgeJCellMap.get(edge);
    }

    /**
     * Returns the JNode associated with a given node.
     * @param node the graph node we're interested in
     * @return the JNode modelling node (if node is known)
     * @ensure result == null || result.getUserObject() == node
     */
    public GraphJVertex<N,E> getJCellForNode(N node) {
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
     * This method also sets the role of the resulting graph.
     */
    @Override
    public DefaultGraph toPlainGraph() {
        DefaultGraph result = super.toPlainGraph();
        GraphInfo.setRole(result, Groove.HOST_ROLE);
        return result;
    }

    @Override
    public boolean hasError(JCell cell) {
        return false;
    }

    /** This method reuses the node identity of the JVertex. */
    @Override
    protected DefaultNode addFreshNode(DefaultGraph graph, JVertex root) {
        @SuppressWarnings("unchecked")
        Node modelNode = ((GraphJVertex<N,E>) root).getActualNode();
        assert modelNode != null : String.format(
            "JModel node '%s' does not have underlying graph node", root);
        return graph.addNode(modelNode.getNumber());
    }

    /**
     * Creates a j-cell corresponding to a given node in the graph. Adds the
     * j-cell to {@link #addedJCells}, and updates {@link #nodeJCellMap}.
     */
    protected JCell addNode(N node) {
        GraphJVertex<N,E> jVertex = computeJVertex(node);
        this.nodeJCellMap.put(node, jVertex);
        // we add nodes in front of the list to get them in front of the display
        this.addedJCells.add(0, jVertex);
        return jVertex;
    }

    /**
     * Creates a j-cell corresponding to a given graph edge. This may be a
     * j-vertex, if the edge can be graphically depicted by that vertex; or an
     * existing j-edge, if the edge can be represented by it. Otherwise, it will
     * be a new j-edge.
     */
    protected JCell addEdge(E edge) {
        if (isUnaryEdge(edge)) {
            @SuppressWarnings("unchecked")
            GraphJVertex<N,E> jVertex = getJCellForNode((N) edge.source());
            if (jVertex.addSelfEdge(edge)) {
                // yes, the edge could be added here; we're done
                this.edgeJCellMap.put(edge, jVertex);
                return jVertex;
            }
        }

        // Add everything else as a binary edge.
        return addBinaryEdge(edge);
    }

    /**
     * Creates a j-edge corresponding to a given binary graph edge. This may be
     * an existing j-edge, if the edge can be represented by it. Otherwise, it
     * will be a new j-edge.
     */
    private GraphJEdge<N,E> addBinaryEdge(E edge) {
        @SuppressWarnings("unchecked")
        N source = (N) edge.source();
        @SuppressWarnings("unchecked")
        N target = (N) edge.target();
        // don't do this for node types, as they need to be typeset in bold
        if (!edge.label().isNodeType()) {
            // maybe a j-edge between this source and target is already in the
            // graph
            for (E edgeBetween : getGraph().outEdgeSet(source)) {
                if (edgeBetween.target().equals(target)) {
                    // see if this edge is appropriate
                    JCell jCell = getJCellForEdge(edgeBetween);
                    if (jCell instanceof GraphJEdge) {
                        @SuppressWarnings("unchecked")
                        GraphJEdge<N,E> jEdge = (GraphJEdge<N,E>) jCell;
                        if (isLayoutCompatible(jEdge, edge)
                            && jEdge.addEdge(edge)) {
                            // yes, the edge could be added here; we're done
                            this.edgeJCellMap.put(edge, jEdge);
                            return jEdge;
                        }
                    }
                }
            }
        }
        // none of the above: so create a new j-edge
        GraphJEdge<N,E> jEdge = computeJEdge(edge);
        this.edgeJCellMap.put(edge, jEdge);
        // put the edge at the end to make sure it goes to the back
        this.addedJCells.add(jEdge);
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
        boolean result;
        if (isForEditor()) {
            result = isPotentialUnaryEdge(edge);
        } else if (edge instanceof AspectEdge) {
            result =
                !edge.isBinary() || isPotentialUnaryEdge(edge)
                    && ((AspectEdge) edge).isRemark();
        } else {
            result = !edge.label().isBinary();
        }
        return result;
    }

    /** 
     * Indicates if, as far as equality of source and target and (the absence 
     * of) explicit layouting is concerned, a given edge could be displayed as 
     * node label.
     */
    protected boolean isPotentialUnaryEdge(E edge) {
        return edge.source() == edge.target()
            && this.layoutMap.getLayout(edge) == null;
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
     * Adds a set of graph nodes to this j-model. J-vertices are created for
     * each of the nodes.
     * @param nodeSet the set of graph nodes to be added; should contain only
     *        <code>Node</code>s.
     * @see #addNode
     */
    protected void addNodeSet(Collection<N> nodeSet) {
        for (N node : nodeSet) {
            // Edge valueEdge = null;
            // boolean addValueEdge = false;
            // if (node instanceof ValueNode) {
            // valueEdge = new ValueEdge((ValueNode) node);
            // addValueEdge = true;
            // }
            addNode(node);
            // if (addValueEdge)
            // addEdge(valueEdge);
        }
    }

    /**
     * Adds a set of graph edges to this j-model. For each of the edges, either
     * a j-edge is created or it is added to an existing j-edge.
     * @param edgeSet the set of graph edges to be added; should contain only
     *        <code>BinaryEdge</code>s.
     * @see #addEdge
     */
    protected void addEdgeSet(Collection<? extends E> edgeSet) {
        for (E edge : edgeSet) {
            addEdge(edge);
        }
    }

    /**
     * Creates a new j-edge using {@link #createJEdge(Edge)}, and sets the
     * attributes using {@link #createJEdgeAttr(JEdge)} and adds available
     * layout information from the layout map stored in this model.
     * @param edge graph edge for which a corresponding j-edge is to be created
     */
    protected GraphJEdge<N,E> computeJEdge(E edge) {
        GraphJEdge<N,E> result = createJEdge(edge);
        result.getAttributes().applyMap(createJEdgeAttr(result));
        JEdgeLayout layout = this.layoutMap.getLayout(edge);
        if (layout != null) {
            result.getAttributes().applyMap(layout.toJAttr());
        }
        return result;
    }

    /**
     * Creates a new j-vertex using {@link #createJVertex(Node)}, and sets the
     * attributes using {@link #createJVertexAttr(JVertex)} and adds available
     * layout information from the layout map stored in this model; or adds a
     * random position otherwise.
     * @param node graph node for which a corresponding j-vertex is to be
     *        created
     */
    protected GraphJVertex<N,E> computeJVertex(N node) {
        GraphJVertex<N,E> result = createJVertex(node);
        result.getAttributes().applyMap(createJVertexAttr(result));
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
     * Returns the attribute change required to mark a vertex as a value (i.e.,
     * attribute-related) vertex.
     */
    protected AttributeMap getJVertexDataAttr() {
        return DATA_NODE_ATTR;
    }

    /**
     * If the edge is visible, tries to create the attributes based on the set
     * of edges contained in the j-edge. Calls the super method only if this
     * fails, i.e., if #createJEdge.
     * @see GraphJVertex#isVisible()
     */
    @SuppressWarnings("unchecked")
    @Override
    final protected AttributeMap createJEdgeAttr(JEdge jEdge) {
        AttributeMap result = super.createJEdgeAttr(jEdge);
        modifyJEdgeAttr(result, ((GraphJEdge<N,E>) jEdge).getEdges());
        return result;
    }

    /**
     * Modifies the edge attributes based on the set of edges contained in a
     * j-edge. Callback method from {@link #createJEdgeAttr(JEdge)}
     * @param result the map to be modified
     */
    protected void modifyJEdgeAttr(AttributeMap result, Set<E> edgeSet) {
        if (!edgeSet.isEmpty()) {
            modifyJEdgeAttr(result, edgeSet.iterator().next());
        }
    }

    /**
     * Modifies the edge attributes based on the set of edges contained in a
     * j-edge. Callback method from {@link #createJEdgeAttr(JEdge)}
     * @param result the map to be modified
     */
    protected void modifyJEdgeAttr(AttributeMap result, E edge) {
        // change the font to bold if the edges contain a node type
        if (edge.label().isNodeType()) {
            setFontAttr(result, Font.BOLD);
        } else if (edge.label() instanceof RuleLabel) {
            setFontAttr(result, Font.ITALIC);
        }
    }

    /** Modifies the font attribute in the given attribute map. */
    protected void setFontAttr(AttributeMap result, int fontAttr) {
        Font currentFont = GraphConstants.getFont(result);
        GraphConstants.setFont(result, currentFont.deriveFont(fontAttr));
    }

    /**
     * If the vertex is visible, tries to create the attributes based on the
     * node contained in the j-vertex. Calls the super method only if this
     * fails.
     * @see #createJVertexAttr(JVertex)
     */
    @SuppressWarnings("unchecked")
    @Override
    final protected AttributeMap createJVertexAttr(JVertex jVertex) {
        AttributeMap result;
        // if (jVertex.isVisible()) {
        result = createJVertexAttr(((GraphJVertex<N,E>) jVertex).getNode());
        if (result == null) {
            result = super.createJVertexAttr(jVertex);
        } else {
            maybeResetBackground(result);
        }
        // } else {
        // result = JAttr.INVISIBLE_ATTR;
        // }
        return result;
    }

    /**
     * Creates the attributes based on the node contained in a j-vertex.
     * Callback method from {@link #createJVertexAttr(JVertex)}.
     */
    protected AttributeMap createJVertexAttr(N node) {
        AttributeMap result = (AttributeMap) this.defaultNodeAttr.clone();
        if (node instanceof VariableNode) {
            result.applyMap(getJVertexDataAttr());
        }
        return result;
    }

    /**
     * Sets the transient variables (cells, attributes and connections) to fresh
     * (empty) initial values.
     */
    protected void initializeTransients() {
        this.addedJCells.clear();
        this.connections = new ConnectionSet();
        // attributes.clear();
    }

    /**
     * Executes the insertion prepared by node and edge additions.
     */
    protected void doInsert() {
        createEdit(toAddedJCellsArray(), null, null, this.connections, null,
            null).execute();
        this.addedJCells.clear();
    }

    /** Returns the array of cells added since the last insert. */
    Object[] toAddedJCellsArray() {
        return this.addedJCells.toArray();
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
     * Indicates whether aspect prefixes should be shown for nodes and edges.
     */
    final boolean isShowAspects() {
        return getOptionValue(Options.SHOW_ASPECTS_OPTION);
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
     * Indicates whether data nodes should be shown in the rule and lts views.
     */
    boolean isShowValueNodes() {
        return getOptionValue(Options.SHOW_VALUE_NODES_OPTION);
    }

    /** Sets the {@link #forEditor} flag to {@code true}. */
    private final void setForEditor() {
        this.forEditor = true;
    }

    /** Returns the value of the {@link #forEditor} flag. */
    private final boolean isForEditor() {
        return this.forEditor;
    }

    /**
     * Flag indicating that this JModel is only created in an intermediate
     * step to make an EditorJModel.
     * This affects the way self-edges are handled.
     */
    private boolean forEditor;
    /**
     * The underlying Graph of this GraphModel.
     * @invariant graph != null
     */
    private final Graph<N,?,E> graph;
    /**
     * The layout map for the underlying graph. It maps {@link Element}s to
     * {@link JCellLayout}s. This is set to an empty map if the graph is not a
     * layed out graph.
     */
    private final LayoutMap<N,E> layoutMap;
    /**
     * Map from graph nodes to JGraph cells.
     */
    private final Map<N,GraphJVertex<N,E>> nodeJCellMap =
        new HashMap<N,GraphJVertex<N,E>>();
    /**
     * Map from graph edges to JGraph cells.
     */
    private final Map<E,JCell> edgeJCellMap = new HashMap<E,JCell>();

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
     * Creates a new GraphJModel instance on top of a given Graph. Node
     * attributes are given by {@link JAttr#DEFAULT_NODE_ATTR} and edge
     * attributes by {@link JAttr#DEFAULT_EDGE_ATTR}.
     * 
     * @param graph the underlying Graph
     * @param options display options
     * @param forEditor flag indicating that the graph model is only used
     * as an intermediate step to reload an {@link EditorJModel}. If {@code true},
     * all self-edges without explicit layout information will be treated as node
     * labels.
     */
    static public <N extends Node,E extends Edge> GraphJModel<N,E> newInstance(
            Graph<N,?,E> graph, Options options, boolean forEditor) {
        GraphJModel<N,E> result =
            new GraphJModel<N,E>(graph, JAttr.DEFAULT_NODE_ATTR,
                JAttr.DEFAULT_EDGE_ATTR, options);
        if (forEditor) {
            result.setForEditor();
        }
        result.reload();
        return result;
    }

    /**
     * Creates a new GraphJModel instance on top of a given Graph. Node
     * attributes are given by {@link JAttr#DEFAULT_NODE_ATTR} and edge
     * attributes by {@link JAttr#DEFAULT_EDGE_ATTR}. Self-edges will be
     * displayed as node labels.
     * 
     * @param graph the underlying Graph
     * @param options display options
     */
    static public <N extends Node,E extends Edge> GraphJModel<N,E> newInstance(
            Graph<N,?,E> graph, Options options) {
        return newInstance(graph, options, false);
    }

    /** Constant map containing the special data vertex attributes. */
    static private final AttributeMap DATA_NODE_ATTR;

    /** Constant map containing the special data edge attributes. */
    static private final AttributeMap DATA_EDGE_ATTR;

    static {
        DATA_NODE_ATTR = new AttributeMap();
        if (JAttr.DATA_BACKGROUND != null) {
            GraphConstants.setBackground(DATA_NODE_ATTR, JAttr.DATA_BACKGROUND);
        }
        if (JAttr.DATA_FONT != null) {
            GraphConstants.setFont(DATA_NODE_ATTR, JAttr.DATA_FONT);
        }
        // the data edge attributes
        DATA_EDGE_ATTR = new AttributeMap();
        if (JAttr.DATA_FONT != null) {
            GraphConstants.setFont(DATA_EDGE_ATTR, JAttr.DATA_FONT);
        }
    }

    /** Random generator for coordinates of new nodes. */
    private static final Random randomGenerator = new Random();
}