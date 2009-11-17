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
import groove.graph.BinaryEdge;
import groove.graph.DefaultNode;
import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.GenericNodeEdgeHashMap;
import groove.graph.GenericNodeEdgeMap;
import groove.graph.Graph;
import groove.graph.GraphInfo;
import groove.graph.GraphProperties;
import groove.graph.GraphShape;
import groove.graph.GraphShapeListener;
import groove.graph.Node;
import groove.graph.algebra.VariableNode;
import groove.gui.Options;
import groove.gui.layout.JCellLayout;
import groove.gui.layout.JEdgeLayout;
import groove.gui.layout.JVertexLayout;
import groove.gui.layout.LayoutMap;
import groove.util.Groove;

import java.awt.Font;
import java.awt.Rectangle;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
public class GraphJModel extends JModel implements GraphShapeListener {
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
    GraphJModel(GraphShape graph, AttributeMap defaultNodeAttr,
            AttributeMap defaultEdgeAttr, Options options) {
        // the model is to store attributes
        super(defaultNodeAttr, defaultEdgeAttr, options);
        // set the transient variables (cells, attributes and connections)
        // add nodes from Graph to GraphModel
        this.graph = graph;
        LayoutMap<Node,Edge> layoutMap = GraphInfo.getLayoutMap(graph);
        this.layoutMap =
            layoutMap == null ? new LayoutMap<Node,Edge>() : layoutMap;
    }

    /**
     * Creates a new GraphJModel instance on top of a given Graph. Node
     * attributes are given by NODE_ATTR and edge attributes by EDGE_ATTR.
     * Self-edges will be displayed as node labels.
     * @param graph the underlying Graph
     * @require graph != null;
     */
    GraphJModel(GraphShape graph, Options options) {
        this(graph, JAttr.DEFAULT_NODE_ATTR, JAttr.DEFAULT_EDGE_ATTR, options);
    }

    /**
     * Constructor for a dummy (empty) model.
     */
    GraphJModel() {
        this(AbstractGraph.EMPTY_GRAPH, null);
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
    public GraphShape getGraph() {
        return this.graph;
    }

    /**
     * Loads in the underlying graph, adding any nodes and edges not yet in this
     * model. Also adds the model as a listener to the graph again. This may be
     * necessary if the model was removed as a graph listener, for instance for
     * the sake of efficiency.
     */
    public void reload() {
        // temporarily remove the model as a graph listener
        this.graph.removeGraphListener(this);
        // add nodes from Graph to GraphModel
        initializeTransients();
        Set<Node> addedNodeSet = new HashSet<Node>(this.graph.nodeSet());
        addedNodeSet.removeAll(this.toJCellMap.nodeMap().keySet());
        addNodeSet(addedNodeSet);
        Set<Edge> addedEdgeSet = new HashSet<Edge>(this.graph.edgeSet());
        addedEdgeSet.removeAll(this.toJCellMap.edgeMap().keySet());
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
        // add the model as a graph listener
        this.graph.addGraphListener(this);
    }

    /**
     * Reacts to a (node of edge) extension of the underlying Graph by mimicking
     * the change in the GraphModel. Can alse deal with NodeSet and EdgeSet
     * additions.
     */
    public synchronized void addUpdate(GraphShape graph, Node node) {
        initializeTransients();
        // add a corresponding GraphCell to the GraphModel
        addNode(node);
        // insert(cells.toArray(), connections, null, attributes);
        doInsert();
    }

    /**
     * Reacts to a (node of edge) extension of the underlying Graph by mimicking
     * the change in the GraphModel. Can alse deal with NodeSet and EdgeSet
     * additions.
     */
    public synchronized void addUpdate(GraphShape graph, Edge edge) {
        initializeTransients();
        // note that (as per GraphListener contract)
        // source and target Nodes (if any) have already been added
        addEdge(edge);
        doInsert();
        // new edges should be behind the nodes
        toBack(this.addedJCells.toArray());
    }

    /**
     * Reacts to a (node of edge) deletion in the underlying Graph by mimicking
     * the change in the GraphModel.
     */
    public synchronized void removeUpdate(GraphShape graph, Node node) {
        // deletes the corresponding GraphCell from the GraphModel
        // note that (as per GraphListener contract)
        // all incident Edges have already been removed
        remove(new Object[] {this.toJCellMap.removeNode(node)});
    }

    /**
     * Reacts to a (node of edge) deletion in the underlying Graph by mimicking
     * the change in the GraphModel.
     */
    public synchronized void removeUpdate(GraphShape graph, Edge edge) {
        // the only remaining possibility is an Edge
        JCell jEdge = getJCell(edge);
        // self-edges are treated separately
        if (jEdge == getJVertex(edge.source())) {
            // self-edge; remove label from image node label set
            ((GraphJVertex) jEdge).removeSelfEdge(edge);
        } else {
            // not a self-edge; remove the edge from the set modelled by this
            // jedge
            ((GraphJEdge) jEdge).removeEdge(edge);
            // but was it the only edge modelled by this jedge?
            if (((GraphJEdge) jEdge).getEdges().isEmpty()) {
                // delete the edge and its source/target port (if any)
                // from the GraphModel
                remove(new Object[] {jEdge});
            }
        }
        // in any case, remove the object from the cell map
        this.toJCellMap.removeEdge(edge);
    }

    /**
     * Returns the set of graph edges between two given graph nodes.
     */
    public Set<Edge> getEdgesBetween(Node source, Node target) {
        Set<Edge> result = new HashSet<Edge>();
        for (Map.Entry<Edge,JCell> cellEntry : this.toJCellMap.edgeMap().entrySet()) {
            Object cell = cellEntry.getValue();
            if (cell instanceof GraphJEdge) {
                GraphJEdge jEdge = (GraphJEdge) cell;
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
    public final JCell getJCell(Element elem) {
        if (elem instanceof Node) {
            return getJVertex((Node) elem);
        } else {
            return getJCell((Edge) elem);
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
    public JCell getJCell(Edge edge) {
        return this.toJCellMap.getEdge(edge);
    }

    /**
     * Returns the JNode associated with a given node.
     * @param node the graph node we're interested in
     * @return the JNode modelling node (if node is known)
     * @ensure result == null || result.getUserObject() == node
     */
    public GraphJVertex getJVertex(Node node) {
        return this.toJCellMap.getNode(node);
    }

    /**
     * Sets the layout of the elements in this graph model to the values
     * provided by a given layout map.
     */
    public void applyLayout(LayoutMap<Node,Edge> layoutMap) {
        Map<Object,AttributeMap> attrMap =
            layoutMap.afterInverse(this.toJCellMap).toJAttrMap();
        edit(attrMap, null, null, null);
        this.layoutableJCells.removeAll(attrMap.keySet());
    }

    /** Stores the layout from the JModel back into the graph. */
    public void synchroniseLayout(GraphJCell jCell) {
        LayoutMap<Node,Edge> currentLayout = GraphInfo.getLayoutMap(getGraph());
        // create the layout map if it does not yet exist
        if (currentLayout == null) {
            currentLayout = new LayoutMap<Node,Edge>();
            GraphInfo.setLayoutMap(getGraph(), currentLayout);
        }
        if (jCell instanceof GraphJEdge) {
            for (Edge edge : ((GraphJEdge) jCell).getEdges()) {
                currentLayout.putEdge(edge, jCell.getAttributes());
            }
        } else {
            currentLayout.putNode(((GraphJVertex) jCell).getNode(),
                jCell.getAttributes());
        }
    }

    /**
     * This method also sets the role of the resulting graph.
     */
    @Override
    public Graph toPlainGraph() {
        Graph result = super.toPlainGraph();
        GraphInfo.setRole(result, Groove.GRAPH_ROLE);
        return result;
    }

    /** This method reuses the node identity of the JVertex. */
    @Override
    protected Node addFreshNode(Graph graph, JVertex root) {
        Node modelNode = ((GraphJVertex) root).getActualNode();
        assert modelNode != null : String.format(
            "JModel node '%s' does not have underlying graph node", root);
        Node result = DefaultNode.createNode(modelNode.getNumber());
        boolean fresh = graph.addNode(result);
        if (!fresh) {
            // a node with this number already existed in the graph
            // this may happen because value nodes use the same numbers
            // as default nodes
            result = graph.addNode();
        }
        return result;
    }

    /**
     * Creates a j-cell corresponding to a given node in the graph. Adds the
     * j-cell to {@link #addedJCells}, and updates {@link #toJCellMap}.
     */
    protected JCell addNode(Node node) {
        GraphJVertex jVertex = computeJVertex(node);
        this.toJCellMap.putNode(node, jVertex);
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
    protected JCell addEdge(Edge edge) {
        // for now we just support binary edges
        if (edge.endCount() > 2) {
            throw new IllegalArgumentException("Non-binary edge " + edge
                + " not supported");
        }
        // see if the edge is appropriate to the node
        if (isSourceCompatible(edge)) {
            GraphJVertex jVertex = getJVertex(edge.source());
            if (jVertex.addSelfEdge(edge)) {
                // yes, the edge could be added here; we're done
                this.toJCellMap.putEdge(edge, jVertex);
                return jVertex;
            }
        }
        return addBinaryEdge((BinaryEdge) edge);
    }

    /**
     * Creates a j-edge corresponding to a given binary graph edge. This may be
     * an existing j-edge, if the edge can be represented by it. Otherwise, it
     * will be a new j-edge.
     */
    private GraphJEdge addBinaryEdge(BinaryEdge edge) {
        Node source = edge.source();
        Node target = edge.opposite();
        if (!edge.label().isNodeType()) {
            // maybe a j-edge between this source and target is already in the
            // graph
            for (Edge edgeBetween : getGraph().outEdgeSet(source)) {
                if (edgeBetween.opposite().equals(target)) {
                    // see if this edge is appropriate
                    JCell jEdge = getJCell(edgeBetween);
                    if (jEdge instanceof GraphJEdge
                        && isLayoutCompatible((GraphJEdge) jEdge, edge)
                        && ((GraphJEdge) jEdge).addEdge(edge)) {
                        // yes, the edge could be added here; we're done
                        this.toJCellMap.putEdge(edge, jEdge);
                        return (GraphJEdge) jEdge;
                    }
                }
            }
        }
        // none of the above: so create a new j-edge
        GraphJEdge jEdge = computeJEdge(edge);
        this.toJCellMap.putEdge(edge, jEdge);
        // put the edge at the end to make sure it goes to the back
        this.addedJCells.add(jEdge);
        GraphJVertex sourceNode = getJVertex(source);
        assert sourceNode != null : "No vertex for source node of " + edge;
        GraphJVertex targetPort = getJVertex(target);
        assert targetPort != null : "No vertex for target node of " + edge;
        this.connections.connect(jEdge, sourceNode.getPort(),
            targetPort.getPort());
        return jEdge;
    }

    /**
     * Tests if a given edge may be added to its source vertex.
     */
    protected boolean isSourceCompatible(Edge edge) {
        Node source = edge.source();
        if (edge.endCount() == 1) {
            return isLayoutCompatible(getJVertex(source), edge);
        }
        if (source == edge.opposite() && !isVertexLabelled()) {
            // see if the edge does not have explicit layout information
            return isLayoutCompatible(getJVertex(source), edge);
        }
        // in all other cases, the edge is not source compatible
        return false;
    }

    /**
     * Tests if a given edge may be added to an existing jvertex, as far as the
     * available layout information is concerned. The edge is compatible if
     * there is no layout information for it.
     * @param jVertex the jvertex to which the edge is about to be added
     * @param edge the edge that is investigated for compatability
     */
    protected boolean isLayoutCompatible(JVertex jVertex, Edge edge) {
        return this.layoutMap.getEdge(edge) == null;
    }

    /**
     * Tests if a given edge may be added to an existing jedge, as far as the
     * available layout information is concerned. The two are compatible if the
     * layout information for the edge equals that for the (edges contained in
     * the) jedge, or both are <tt>null</tt>.
     * @param jEdge the jedge to which the edge is about to be added
     * @param edge the edge that is investigated for compatability
     */
    protected boolean isLayoutCompatible(GraphJEdge jEdge, Edge edge) {
        JCellLayout edgeLayout = this.layoutMap.getEdge(edge);
        JCellLayout jEdgeLayout = this.layoutMap.getEdge(jEdge.getEdge());
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
    protected void addNodeSet(Collection<? extends Node> nodeSet) {
        for (Node node : nodeSet) {
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
    protected void addEdgeSet(Collection<? extends Edge> edgeSet) {
        for (Edge edge : edgeSet) {
            addEdge(edge);
        }
    }

    /**
     * Creates a new j-edge using {@link #createJEdge(BinaryEdge)}, and sets the
     * attributes using {@link #createJEdgeAttr(JEdge)} and adds available
     * layout information from the layout map stored in this model.
     * @param edge graph edge for which a corresponding j-edge is to be created
     */
    protected GraphJEdge computeJEdge(BinaryEdge edge) {
        GraphJEdge result = createJEdge(edge);
        result.getAttributes().applyMap(createJEdgeAttr(result));
        JEdgeLayout layout = this.layoutMap.getEdge(edge);
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
    protected GraphJVertex computeJVertex(Node node) {
        GraphJVertex result = createJVertex(node);
        result.getAttributes().applyMap(createJVertexAttr(result));
        if (GraphConstants.isMoveable(result.getAttributes())) {
            JVertexLayout layout = this.layoutMap.getNode(node);
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
    protected GraphJEdge createJEdge(BinaryEdge edge) {
        return new GraphJEdge(this, edge);
    }

    /**
     * Factory method for jgraph nodes.
     * @param node graph node for which a corresponding j-node is to be created
     * @return j-node corresponding to <tt>node</tt>
     * @ensure <tt>result.getNode().equals(node)</tt>
     */
    protected GraphJVertex createJVertex(Node node) {
        return new GraphJVertex(this, node, isVertexLabelled());
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
    @Override
    final protected AttributeMap createJEdgeAttr(JEdge jEdge) {
        AttributeMap result = (AttributeMap) this.defaultEdgeAttr.clone();
        // if (jEdge.isVisible()) {
        if (result == null) {
            result = super.createJEdgeAttr(jEdge);
        } else {
            modifyJEdgeAttr(result, ((GraphJEdge) jEdge).getEdges());
        }
        return result;
    }

    /**
     * Modifies the edge attributes based on the set of edges contained in a
     * j-edge. Callback method from {@link #createJEdgeAttr(JEdge)}
     * @param result the map to be modified
     */
    protected void modifyJEdgeAttr(AttributeMap result,
            Set<? extends Edge> edgeSet) {
        // change the font to bold if the edges contain a node type
        if (!edgeSet.isEmpty()) {
            Edge edge = edgeSet.iterator().next();
            if (edge.label().isNodeType()) {
                Font currentFont = GraphConstants.getFont(result);
                GraphConstants.setFont(result,
                    currentFont.deriveFont(Font.BOLD));
            }
        }
    }

    /**
     * If the vertex is visible, tries to create the attributes based on the
     * node contained in the j-vertex. Calls the super method only if this
     * fails.
     * @see #createJVertexAttr(JVertex)
     */
    @Override
    final protected AttributeMap createJVertexAttr(JVertex jVertex) {
        AttributeMap result;
        // if (jVertex.isVisible()) {
        result = createJVertexAttr(((GraphJVertex) jVertex).getNode());
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
    protected AttributeMap createJVertexAttr(Node node) {
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
        createEdit(this.addedJCells.toArray(), null, null, this.connections,
            null, null).execute();
        this.addedJCells.clear();
    }

    /**
     * Returns a random number bounded by <tt>toJCellMap.size()</tt>. Used to
     * generate a random position for any added j-vertex without layout
     * information.
     */
    private int randomCoordinate() {
        return (int) (this.toJCellMap.size() * 5 * Math.random());
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
    final boolean isShowUnfilteredEdges() {
        return getOptionValue(Options.SHOW_UNFILTERED_EDGES_OPTION);
    }

    /**
     * Indicates whether vertices can have their own labels. If false, j-vertex
     * inscriptions are (possibly empty) sets of self-edge labels.
     */
    private boolean isVertexLabelled() {
        return getOptionValue(Options.VERTEX_LABEL_OPTION);
    }

    /**
     * Indicates whether anchors should be shown in the rule and lts views.
     */
    boolean isShowAnchors() {
        return getOptionValue(Options.SHOW_ANCHORS_OPTION);
    }

    /**
     * Indicates whether anchors should be shown in the rule and lts views.
     */
    boolean isShowValueNodes() {
        return getOptionValue(Options.SHOW_VALUE_NODES_OPTION);
    }

    /**
     * The underlying Graph of this GraphModel.
     * @invariant graph != null
     */
    private final GraphShape graph;
    /**
     * The layout map for the underlying graph. It maps {@link Element}s to
     * {@link JCellLayout}s. This is set to an empty map if the graph is not a
     * layed out graph.
     */
    private final LayoutMap<Node,Edge> layoutMap;
    /**
     * Map from graph elements to JGraph cells.
     */
    private final GenericNodeEdgeMap<Node,GraphJVertex,Edge,JCell> toJCellMap =
        new GenericNodeEdgeHashMap<Node,GraphJVertex,Edge,JCell>();

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
     * attributes by {@link JAttr#DEFAULT_EDGE_ATTR}. Self-edges will be
     * displayed as node labels.
     * 
     * @param graph the underlying Graph
     * @param options display options
     */
    static public GraphJModel newInstance(GraphShape graph, Options options) {
        GraphJModel result =
            new GraphJModel(graph, JAttr.DEFAULT_NODE_ATTR,
                JAttr.DEFAULT_EDGE_ATTR, options);
        result.reload();
        return result;
    }

    /** Dummy (empty) j-model. */
    static public final GraphJModel EMPTY_JMODEL = new GraphJModel();

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
}