/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
 * $Id: GraphJModel.java,v 1.1.1.2 2007-03-20 10:42:46 kastenberg Exp $
 */

package groove.gui.jgraph;

import groove.graph.AbstractGraph;
import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.GenericNodeEdgeHashMap;
import groove.graph.GenericNodeEdgeMap;
import groove.graph.GraphInfo;
import groove.graph.GraphShape;
import groove.graph.GraphShapeListener;
import groove.graph.Node;
import groove.graph.algebra.ValueEdge;
import groove.graph.algebra.ValueNode;
import groove.gui.layout.JCellLayout;
import groove.gui.layout.JEdgeLayout;
import groove.gui.layout.JVertexLayout;
import groove.gui.layout.LayoutMap;
import groove.util.Groove;

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
 * Implements jgraph's GraphModel interface on top of a graph.Graph.
 * The resulting GraphModel should only be edited through the Graph interface;
 * attributes should be updated through putAttributes.
 * <p>NOTE: the JModel-GraphJModel-RuleJModel structure is up for revision.
 * @author Arend Rensink
 * @version $Revision: 1.1.1.2 $
 */
public class GraphJModel extends JModel implements GraphShapeListener {
	/** Dummy LTS model. */
	static public final GraphJModel EMPTY_JMODEL = new GraphJModel();

    /** 
     * Creates a new GraphJModel instance on top of a given Graph, with given
     * node and edge attributes, and an indication whether self-edges should be 
     * displayed as node labels.
     * The node and adge attribute maps are cloned.
     * @param graph the underlying Graph
     * @param defaultNodeAttr the attributes for displaying nodes
     * @param defaultEdgeAttr the attributes for displaying edges
     * @param showNodeIdentities indicates whether nodes should be labelled
     * with their identities. If false, node labels are used to display self edges.
     * @require graph != null, nodeAttr != null, edgeAttr != null;
     */
    public GraphJModel(GraphShape graph, AttributeMap defaultNodeAttr, AttributeMap defaultEdgeAttr, boolean showNodeIdentities) {
        // the model is to store attributes
        super(defaultNodeAttr, defaultEdgeAttr, showNodeIdentities);
        // set the transient variables (cells, attributes and connections)
        // add nodes from Graph to GraphModel
        this.graph = graph;
        LayoutMap<Node,Edge> layoutMap = GraphInfo.getLayoutMap(graph);
        this.layoutMap = layoutMap == null ? new LayoutMap<Node,Edge>() : layoutMap;
        initializeTransients();
        addNodeSet(graph.nodeSet());
        addEdgeSet(graph.edgeSet());
        doInsert();
        graph.addGraphListener(this);
    }

    /** 
     * Creates a new GraphJModel instance on top of a given Graph, with given
     * node and edge attributes.
     * Self-edges will be displayed as node labels.
     * The node and adge attribute maps are cloned.
     * @param graph the underlying Graph
     * @param defaultNodeAttr the attributes for displaying nodes
     * @param defaultEdgeAttr the attributes for displaying edges
     * @require graph != null, nodeAttr != null, edgeAttr != null;.
     */
    public GraphJModel(GraphShape graph, AttributeMap defaultNodeAttr, AttributeMap defaultEdgeAttr) {
        this(graph, defaultNodeAttr, defaultEdgeAttr, false);
    }

    /** 
     * Creates a new GraphJModel instance on top of a given Graph,
     * and an indication whether self-edges should be displayed as node labels.
     * Node attributes are given by NODE_ATTR and edge attributes by EDGE_ATTR.
     * @param graph the underlying Graph
     * @param showNodeIdentities indicates whether self edges should be
     * @require graph != null;
     */
    public GraphJModel(GraphShape graph, boolean showNodeIdentities) {
        this(graph, JAttr.DEFAULT_NODE_ATTR, JAttr.DEFAULT_EDGE_ATTR, showNodeIdentities);
    }

    /** 
     * Creates a new GraphJModel instance on top of a given Graph.
     * Node attributes are given by NODE_ATTR and edge attributes by EDGE_ATTR.
     * Self-edges will be displayed as node labels.
     * @param graph the underlying Graph
     * @require graph != null;
     */
    public GraphJModel(GraphShape graph) {
        this(graph, JAttr.DEFAULT_NODE_ATTR, JAttr.DEFAULT_EDGE_ATTR);
    }

    /**
     * Constructor for a dummy (empty) model. 
     */
    protected GraphJModel() {
    	this(AbstractGraph.EMPTY_GRAPH);
    }
    
    /**
     * Returns the underlying Graph of this GraphModel.
     * @ensure result != null
     */
    public GraphShape graph() {
        return graph;
    }

    // ------------------------ COMMANDS -------------------------

    /**
     * Loads in the underlying graph,
     * adding any nodes and edges not yet in this model.
     * Also adds the model as a listener to the graph again.
     * This may be necessary if the model was removed as a graph listener,
     * for instance for the sake of efficiency.
     */
    public void reload() {
        // add nodes from Graph to GraphModel
        initializeTransients();
        Set<Node> addedNodeSet = new HashSet<Node>(graph.nodeSet());
        addedNodeSet.removeAll(toJCellMap.nodeMap().keySet());
        addNodeSet(addedNodeSet);
        Set<Edge> addedEdgeSet = new HashSet<Edge>(graph.edgeSet());
        addedEdgeSet.removeAll(toJCellMap.edgeMap().keySet());
        addEdgeSet(addedEdgeSet);
        doInsert();
        // add the model as a graph listener
        graph.addGraphListener(this);
    }

    /**
     * Reacts to a (node of edge) extension of the underlying Graph
     * by mimicking the change in the GraphModel.
     * Can alse deal with NodeSet and EdgeSet additions.
     */
    public synchronized void addUpdate(GraphShape graph, Node node) {
        if (DEBUG_UPDATE)
            Groove.message("Graph model update: adding " + node);
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
        if (DEBUG_UPDATE)
			Groove.message("Graph model update: adding " + edge);
		initializeTransients();
		// note that (as per GraphListener contract)
		// source and target Nodes (if any) have already been added
		addEdge(edge);
		doInsert();
		// new edges should be behind the nodes
		toBack(addedJCells.toArray());
	}

    /**
	 * Reacts to a (node of edge) deletion in the underlying Graph by mimicking
	 * the change in the GraphModel.
	 */
    public synchronized void removeUpdate(GraphShape graph, Node node) {
		if (DEBUG_UPDATE)
			Groove.message("Graph model update: removing " + node);
		// deletes the corresponding GraphCell from the GraphModel
		// note that (as per GraphListener contract)
		// all incident Edges have already been removed
		// GraphCell[] removeCells = { (DefaultGraphCell) cellMap.get(obj) };
		// remove(getDescendants(this, new Object[] { cellMap.get(obj)
		// }).toArray());
		remove(new Object[] { toJCellMap.removeNode(node) });
	}

    /**
	 * Reacts to a (node of edge) deletion in the underlying Graph by mimicking
	 * the change in the GraphModel.
	 */
    public synchronized void removeUpdate(GraphShape graph, Edge edge) {
		if (DEBUG_UPDATE)
			Groove.message("Graph model update: removing " + edge);
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
			if (((GraphJEdge) jEdge).getEdgeSet().isEmpty()) {
				// delete the edge and its source/target port (if any)
				// from the GraphModel
				remove(new Object[] { jEdge });
			}
		}
		// in any case, remove the object from the cell map
		toJCellMap.removeEdge(edge);
	}

    /**
	 * Returns the set of graph edges between two given graph nodes.
	 */
    public Set<Edge> getEdgesBetween(Node source, Node target) {
        Set<Edge> result = new HashSet<Edge>();
        for (Map.Entry<Edge,JCell> cellEntry: toJCellMap.edgeMap().entrySet()) {
            Object cell = cellEntry.getValue();
            if (cell instanceof GraphJEdge) {
                GraphJEdge jEdge = (GraphJEdge) cell;
                if (jEdge.getSourceNode() == source && jEdge.getTargetNode() == target)
                    result.add(cellEntry.getKey());
            }
        }
        return result;
    }
//
//    /**
//     * Returns an unmodifiable view upon the mapping from graph elements to j-graph cells.
//     */
//    public Map getJCellMap() {
//        return Collections.unmodifiableMap(toJCellMap);
//    }

    /**
     * Returns the set of {@link JCell}s associated with a given set of graph elements.
     * @param elemSet the set of elements for which the jcells are requested
     * @return the jcells associated with <tt>elemSet</tt>
     * @see #getJCell(Element)
     */
    public Set<JCell> getJCellSet(Set<Element> elemSet) {
        Set<JCell> result = new HashSet<JCell>();
        for (Element elem: elemSet) {
            JCell image = getJCell(elem);
            if (image != null) {
                result.add(getJCell(elem));
            }
        }
        return result;
    }

    /**
     * Returns the {@link JCell}associated with a given graph element. The result is a
     * {@link GraphJVertex}for which the graph element is the underlying node or self-edge, or a
     * {@link GraphJEdge}for which the graph element is an underlying edge.
     * @param elem the graph element for which the jcell is requested
     * @return the jcell associated with <tt>elem</tt>
     */
    public JCell getJCell(Element elem) {
        if (elem instanceof Node) {
            return getJVertex((Node) elem);
        } else {
            return getJCell((Edge) elem);
        }
    }

    /**
     * Returns the <tt>JNode</tt> or <tt>JEdge</tt> associated with a given edge. The method
     * returns a <tt>JNode</tt> if and only if <tt>edge</tt> is a self-edge and
     * <tt>showNodeIdentities</tt> does not hold.
     * @param edge the graph edge we're interested in
     * @return the <tt>JNode</tt> or <tt>JEdge</tt> modelling <tt>edge</tt>
     * @ensure result instanceof JNode && result.labels().contains(edge.label()) || result
     *         instanceof JEdge && result.labels().contains(edge.label())
     */
    public JCell getJCell(Edge edge) {
        return toJCellMap.getEdge(edge);
    }

    /**
     * Returns the JNode associated with a given node.
     * @param node the graph node we're interested in
     * @return the JNode modelling node (if node is known)
     * @ensure result == null || result.getUserObject() == node
     */
    public GraphJVertex getJVertex(Node node) {
        return toJCellMap.getNode(node);
    }
//
//    /**
//     * Constructs a layout map for the currently displayed graph from the underlying model.
//     */
//    public LayoutMap getLayoutMap() {
//        return new LayoutMap(getRoots(this)).after(toJCellMap);
//    }

    /**
     * Sets the layout of the elements in this graph model to the values provided by a given layout
     * map.
     */
    public void applyLayout(LayoutMap<Node,Edge> layoutMap) {
        Map<Object,AttributeMap> attrMap = layoutMap.afterInverse(toJCellMap).toJAttrMap();
        edit(attrMap, null, null, null);
        layoutableJCells.removeAll(attrMap.keySet());
    }

    /**
     * Creates a j-cell corresponding to a given node in the graph.
     * Adds the j-cell to {@link #addedJCells}, and updates {@link #toJCellNodeMap}.
     */
    protected JCell addNode(Node node) {
        if (ADD_NODE_DEBUG) {
            Groove.message("Adding node: " + node);
        }
        GraphJVertex jVertex = computeJVertex(node);
        toJCellMap.putNode(node, jVertex);
        // we add nodes in front of the list to get them in front of the display
        addedJCells.add(0,jVertex);
        return jVertex;
    }

    /**
     * Creates a j-cell corresponding to a given graph edge.
     * This may be a j-vertex, if the edge can be graphically depicted by that vertex;
     * or an existing j-edge, if the edge can be represented by it.
     * Otherwise, it will be a new j-edge.
     */
    protected JCell addEdge(Edge edge) {
        if (ADD_EDGE_DEBUG) {
            Groove.message("Adding edge: " + edge);
        }
        // for now we just support binary edges
        if (edge.endCount() != 2) {
            throw new IllegalArgumentException("Non-binary edge "+edge+" not supported");
        }
        Node source = edge.end(Edge.SOURCE_INDEX);
        Node target = edge.end(Edge.TARGET_INDEX);
        // self-edges are treated differently
        if (target == source && !isShowNodeIdentities()) {
            if (ADD_EDGE_DEBUG) {
                Groove.message("This is a self-edge");
                // self-edge; to be represented through node label or special edge
                //            if (!showNodeIdentities) {
                Groove.message("Edge is added as node label");
            }
            GraphJVertex jVertex = getJVertex(source);
            // see if the edge is appropriate to the node
            if (isLayoutCompatible(jVertex, edge) && jVertex.addSelfEdge(edge)) {
                // yes, the edge could be added here; we're done
                toJCellMap.putEdge(edge, jVertex);
                return jVertex;
            }
        }
        // maybe a j-edge between this source and target is already in the graph
        for (Edge edgeBetween: getEdgesBetween(edge.source(), target)) {
            // see if this edge is appropriate
            GraphJEdge jEdge = (GraphJEdge) getJCell(edgeBetween);
            if (isLayoutCompatible(jEdge, edge) && jEdge.addEdge(edge)) {
                // yes, the edge could be added here; we're done
                toJCellMap.putEdge(edge, jEdge);
                return jEdge;
            }
        }
        // none of the above: so create a new j-edge
        GraphJEdge jEdge = computeJEdge(edge);
        toJCellMap.putEdge(edge, jEdge);
        // put the edge at the end to make sure it goes to the back
        addedJCells.add(jEdge);
        GraphJVertex sourceNode = getJVertex(source);
        assert sourceNode != null : "No vertex for source node of "+edge;
        GraphJVertex targetPort = getJVertex(target);
        assert targetPort != null : "No vertex for target node of "+edge;
        connections.connect(jEdge, sourceNode.getPort(), targetPort.getPort());
        return jEdge;
    }

    /**
     * Tests if a given edge may be added to an existing jvertex,
     * as far as the available layout information is concerned.
     * The edge is compatible if there is no layout information for it.
     * @param jVertex the jvertex to which the edge is about to be added
     * @param edge the edge that is investigated for compatability
     */
    protected boolean isLayoutCompatible(JVertex jVertex, Edge edge) {
        return layoutMap.getEdge(edge) == null;
    }
    
    /**
     * Tests if a given edge may be added to an existing jedge,
     * as far as the available layout information is concerned.
     * The two are compatible if the layout information for the edge
     * equals that for the (edges contained in the) jedge, or both are <tt>null</tt>.
     * @param jEdge the jedge to which the edge is about to be added
     * @param edge the edge that is investigated for compatability
     */
    protected boolean isLayoutCompatible(GraphJEdge jEdge, Edge edge) {
        JCellLayout edgeLayout = layoutMap.getEdge(edge);
        JCellLayout jEdgeLayout = layoutMap.getEdge(jEdge.getEdge());
        if (edgeLayout == null) {
            return jEdgeLayout == null;
        } else {
            return jEdgeLayout != null && edgeLayout.equals(jEdgeLayout);
        }
    }
    
    /**
     * Adds a set of graph nodes to this j-model.
     * J-vertices are created for each of the nodes.
     * @param nodeSet the set of graph nodes to be added; should contain only <code>Node</code>s.
     * @see #addNode
     */
    protected void addNodeSet(Collection<? extends Node> nodeSet) {
    	for (Node node: nodeSet) {
            Edge valueEdge = null;
            boolean addValueEdge = false;
            if (node instanceof ValueNode) {
                valueEdge = new ValueEdge((ValueNode) node);
                addValueEdge = true;
            }
            addNode(node);
            if (addValueEdge)
                addEdge(valueEdge);
        }
    }

    /**
     * Adds a set of graph edges to this j-model.
     * For each of the edges, either a j-edge is created or it is added to an existing
     * j-edge.
     * @param edgeSet the set of graph edges to be added; should contain only <code>BinaryEdge</code>s.
     * @see #addEdge
     */
    protected void addEdgeSet(Collection<? extends Edge> edgeSet) {
    	for (Edge edge: edgeSet) {
            addEdge(edge);
        }
    }

    /**
     * Creates a new j-edge using {@link #createJEdge(Edge)}, and 
     * sets the attributes using {@link #createJEdgeAttr(JEdge)} and
     * adds available layout information from the layout map stored in this model.
     * @param edge graph edge for which a corresponding j-edge is to be created 
     */
    protected GraphJEdge computeJEdge(Edge edge) {
        GraphJEdge result = createJEdge(edge);
        result.getAttributes().applyMap(createJEdgeAttr(result));
        JEdgeLayout layout = layoutMap.getEdge(edge);
        if (layout != null) {
            result.getAttributes().applyMap(layout.toJAttr());
        }
        return result;
    }
    
    /**
     * Creates a new j-vertex using {@link #createJVertex(Node)}, and 
     * sets the attributes using {@link #createJVertexAttr(JVertex)} and
     * adds available layout information from the layout map stored in this model;
     * or adds a random position otherwise.
     * @param node graph node for which a corresponding j-vertex is to be created 
     */
    protected GraphJVertex computeJVertex(Node node) {
        GraphJVertex result = createJVertex(node);
        result.getAttributes().applyMap(createJVertexAttr(result));
        JVertexLayout layout = layoutMap.getNode(node);
        if (layout != null) {
            result.getAttributes().applyMap(layout.toJAttr());
        } else {
            layoutableJCells.add(result);
            Rectangle newBounds =
                new Rectangle(nodeX, nodeY, JAttr.DEFAULT_NODE_BOUNDS.width, JAttr.DEFAULT_NODE_BOUNDS.height);
            GraphConstants.setBounds(result.getAttributes(), newBounds);
            nodeX = randomCoordinate();
            nodeY = randomCoordinate();
        }
        return result;
    }
    
    /**
	 * Factory method for jgraph edges.
	 * @param edge graph edge for which a corresponding j-edge is to be created
	 * @return j-edge corresponding to <tt>edge</tt>
	 * @ensure <tt>result.getEdgeSet().contains(edge)</tt>
	 */
	protected GraphJEdge createJEdge(Edge edge) {
	    return new GraphJEdge(edge);
	}

	/**
	 * Factory method for jgraph nodes.
	 * @param node graph node for which a corresponding j-node is to be created
	 * @return j-node corresponding to <tt>node</tt>
	 * @ensure <tt>result.getNode().equals(node)</tt>
	 */
	protected GraphJVertex createJVertex(Node node) {
	    return new GraphJVertex(this, node, true);
	}

	/**
     * Sets the transient variables (cells, attributes and connections) to fresh (empty) initial
     * values.
     */
    protected void initializeTransients() {
        addedJCells.clear();
        connections = new ConnectionSet();
        // attributes.clear();
    }

    /**
     * Executes the insertion prepared by node and edge additions.
     */
    protected void doInsert() {
        createEdit(addedJCells.toArray(), null, null, connections, null, null).execute();
        addedJCells.clear();
    }

    /**
     * Returns a random number bounded by <tt>toJCellMap.size()</tt>.
     * Used to generate a random position for any added j-vertex without layout information.
     */
    private int randomCoordinate() {
        return (int) (toJCellMap.size() * 5 * Math.random());
    }

    /**
     * The underlying Graph of this GraphModel.
     * @invariant graph != null
     */
    protected final GraphShape graph;
    /**
     * The layout map for the underlying graph.
     * It maps {@link Element}s to {@link JCellLayout}s.
     * This is set to an empty map if the graph is not a layed out graph.
     */
    protected final LayoutMap<Node,Edge> layoutMap;
    /**
     * Map from graph elements to JGraph cells.
     */
    protected final GenericNodeEdgeMap<Node,GraphJVertex,Edge,JCell> toJCellMap = new GenericNodeEdgeHashMap<Node,GraphJVertex,Edge,JCell>();
//    /**
//     * Map from graph nodes and edges to JGraph cells.
//     * @invariant cellMap: graph.Node --> JNode graph.Edge --> JEdge \cup JNode
//     */
//    protected final Map<Node,GraphJVertex> toJCellNodeMap = new HashMap<Node,GraphJVertex>();
//    /**
//     * Map from graph nodes and edges to JGraph cells.
//     * @invariant cellMap: graph.Node --> JNode graph.Edge --> JEdge \cup JNode
//     */
//    protected final Map<Edge,JCell> toJCellEdgeMap = new HashMap<Edge,JCell>();

    /**
     * Set of GraphModel cells. Used in the process of constructing a GraphJModel.
     * @invariant addedCells \subseteq org.jgraph.graph.DefaultGraphCell
     */
    protected final List<JCell> addedJCells = new LinkedList<JCell>();

    /**
     * Set of GraphModel connections. Used in the process of constructing a GraphJModel.
     */
    protected ConnectionSet connections;

    /**
     * Counter to provide the x-coordinate of fresh nodes with fresh values
     */
    private transient int nodeX;
    /**
     * Counter to provide the y-coordinate of fresh nodes with fresh values
     */
    private transient int nodeY;
    private static final boolean ADD_EDGE_DEBUG = false;
    private static final boolean ADD_NODE_DEBUG = false;
    private static final boolean DEBUG_UPDATE = false;
}