/* $Id: AspectualGraphView.java,v 1.4 2007-04-12 16:14:51 rensink Exp $ */
package groove.graph.aspect;

import groove.algebra.Constant;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.GraphFactory;
import groove.graph.GraphInfo;
import groove.graph.Node;
import groove.graph.NodeEdgeHashMap;
import groove.graph.NodeEdgeMap;
import groove.graph.algebra.ProductEdge;
import groove.graph.algebra.ValueNode;
import groove.util.FormatException;
import groove.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Aspectual view upon an attributed graph.
 * @author Arend Rensink
 * @version $Revision $
 */
public class AspectualGraphView implements AspectualView<Graph> {
	/** Constructs an instance from a given aspect graph view. */
	public AspectualGraphView(AspectGraph view) throws FormatException {
		this.view = view;
		Pair<Graph,Map<AspectNode,Node>> modelPlusMap = computeModel(view);
		this.model = modelPlusMap.first();
		this.viewToModelMap = modelPlusMap.second();
	}
	
	/** Constructs an instance from a given graph model. */
	public AspectualGraphView(Graph model) {
		this.model = model;
		Pair<AspectGraph,Map<AspectNode,Node>> viewPlusMap = computeView(model);
		this.view = viewPlusMap.first();
		this.viewToModelMap = viewPlusMap.second();
	}
	
	public AspectGraph getView() {
		return view;
	}
	
	public Graph getModel() {
		return model;
	}

	public Map<AspectNode, Node> getMap() {
		return viewToModelMap;
	}
	
	/**
	 * Computes a fresh model from a given aspect graph,
	 * together with a mapping from the aspect graph's node to the
	 * (fresh) graph nodes. 
	 */
	protected Pair<Graph,Map<AspectNode,Node>> computeModel(AspectGraph view) throws FormatException {
		Graph model = getGraphFactory().newGraph();
		// we need to record the view-to-model element map for layout transfer
		NodeEdgeMap elementMap = new NodeEdgeHashMap();
		// we need to record the view-to-model node map for the return value
		Map<AspectNode,Node> viewToModelMap = new HashMap<AspectNode,Node>();
		// we need to record the model-to-view node map for removing isolated value nodes
		Map<Node,AspectNode> modelToViewMap = new HashMap<Node,AspectNode>();
		// copy the nodes from view to model
		for (AspectNode viewNode: view.nodeSet()) {
			Node nodeImage = AttributeAspect.createAttributeNode(viewNode, view);
			if (nodeImage == null) {
				nodeImage = model.addNode();
			} else if (isAllowedNode(nodeImage)){
				model.addNode(nodeImage);
			} else {
				throw new FormatException("Graph should contain no attribute elements except constants");
			}
			viewToModelMap.put(viewNode, nodeImage);
			modelToViewMap.put(nodeImage, viewNode);
		}
		elementMap.nodeMap().putAll(viewToModelMap);
		// copy the edges from view to model
		for (AspectEdge viewEdge: view.edgeSet()) {
			Node[] endImages = new Node[viewEdge.endCount()];
			for (int i = 0; i < endImages.length; i++) {
				endImages[i] = viewToModelMap.get(viewEdge.end(i));
			}
			// create an image for the view edge
			Edge edgeImage = AttributeAspect.createAttributeEdge(viewEdge, view, endImages);
			if (edgeImage == null) {
				edgeImage = model.addEdge(endImages, viewEdge.label());
			} else if (! isAllowedEdge(edgeImage)) {
				throw new FormatException("Attribute edges %s not allowed in graph", edgeImage);
			}
			elementMap.putEdge(viewEdge, edgeImage);
		}
		// remove isolated value nodes from the result graph
		Iterator<Map.Entry<AspectNode,Node>> viewToModelIter = viewToModelMap.entrySet().iterator();
		while (viewToModelIter.hasNext()) {
			Map.Entry<AspectNode,Node> viewToModelEntry = viewToModelIter.next();
			Node modelNode = viewToModelEntry.getValue();
			if (modelNode instanceof ValueNode && model.edgeSet(modelNode).isEmpty()) {
				// the node is an isolated value node; remove it
				model.removeNode(modelNode);
				elementMap.removeNode(viewToModelEntry.getKey());
				viewToModelIter.remove();
			}
		}
		// transfer graph info such as layout from view to model
		GraphInfo.transfer(view, model, elementMap);
		return new Pair<Graph,Map<AspectNode,Node>>(model, viewToModelMap);
	}

	/**
	 * Tests if a certain attribute node is of the type allowed in graphs.
	 */
	private boolean isAllowedNode(Node node) {
		return node instanceof ValueNode && ((ValueNode) node).getConstant() != null;
	}

	/**
	 * Tests if a certain attribute node is of the type allowed in graphs.
	 */
	private boolean isAllowedEdge(Edge edge) {
		return edge instanceof ProductEdge && ((ProductEdge) edge).getOperation() instanceof Constant;
	}

	/**
	 * Constructs an aspect graph from a possibly attributed graph,
	 * together with a mapping from that aspect graph to the original graph.
	 * @param model the graph to be converted (which may be attributed, i.e.,
	 * contains 
	 * @return a pair of aspect graph plus a mapping from that aspect graph
	 * to <code>model</code>
	 */
	protected Pair<AspectGraph,Map<AspectNode,Node>> computeView(Graph model) {
		AspectGraph view = new AspectGraph();
		// we need to record the view-to-model node map for the return value
		Map<AspectNode,Node> viewToModelMap = new HashMap<AspectNode,Node>();
		// we need to record the model-to-view node map for mapping the edges
		Map<Node,AspectNode> modelToViewMap = new HashMap<Node,AspectNode>();
		// we need to record the model-to-view element map for graph info transfer
		NodeEdgeMap elementMap = new NodeEdgeHashMap();
		try {
			// create the nodes of the view
			for (Node node: model.nodeSet()) {
				AspectNode nodeImage = view.createNode();
				AspectValue value = AttributeAspect.getAttributeValue(node);
				if (value != null) {
					nodeImage.setDeclaredValue(value);
				}
				view.addNode(nodeImage);
				viewToModelMap.put(nodeImage, node);
				modelToViewMap.put(node, nodeImage);
			}
			// update the model-to-view element map
			elementMap.nodeMap().putAll(modelToViewMap);
			// create the edges of the view
			for (Edge edge: model.edgeSet()) {
				List<AspectNode> endImages = new ArrayList<AspectNode>();
				for (int i = 0; i < edge.endCount(); i++) {
					endImages.add(modelToViewMap.get(edge.end(i)));
				}
				AspectEdge edgeImage = new AspectEdge(endImages, edge.label(), AttributeAspect.getAttributeValue(edge));
				view.addEdge(edgeImage);
				// update the model-to-view element map
				elementMap.edgeMap().put(edge, edgeImage);
			}
		} catch (FormatException exc) {
			throw new IllegalStateException("Exception should not occur: "+exc);
		}
		// transfer graph information such as layout from model to view
		GraphInfo.transfer(model, view, elementMap);
		return new Pair<AspectGraph,Map<AspectNode,Node>>(view, viewToModelMap);
	}
	
	/**
     * Returns the rule factory.
     * @return the rule factory.
     */
    protected GraphFactory getGraphFactory() {
    	if (graphFactory == null) {
    		graphFactory = GraphFactory.getInstance();
    	}
    	return graphFactory;
    }
	
	/** The view represented by this object. */
	private final AspectGraph view;
	/** The graph model that is being viewed. */
	private final Graph model;
	/** Map from view to model nodes. */
	private final Map<AspectNode,Node> viewToModelMap;
	/** The graph factory used by this view, to construct the model. */
	private GraphFactory graphFactory;
}
