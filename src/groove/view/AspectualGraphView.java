/* $Id: AspectualGraphView.java,v 1.4 2007-05-09 22:53:35 rensink Exp $ */
package groove.view;

import groove.algebra.Constant;
import groove.algebra.Variable;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.GraphFactory;
import groove.graph.GraphInfo;
import groove.graph.Node;
import groove.graph.NodeEdgeHashMap;
import groove.graph.NodeEdgeMap;
import groove.graph.algebra.ProductEdge;
import groove.graph.algebra.ValueNode;
import groove.util.Pair;
import groove.view.aspect.Aspect;
import groove.view.aspect.AspectEdge;
import groove.view.aspect.AspectGraph;
import groove.view.aspect.AspectNode;
import groove.view.aspect.AspectValue;
import groove.view.aspect.AttributeAspect;
import groove.view.aspect.RuleAspect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Aspectual view upon an attributed graph.
 * @author Arend Rensink
 * @version $Revision $
 */
public class AspectualGraphView implements AspectualView<Graph> {
	/** Constructs an instance from a given aspect graph view. */
	public AspectualGraphView(AspectGraph view) {
		this.view = view;
        Graph model;
        Map<AspectNode,Node> viewToModelMap;
        List<String> errors;
		try {
            Pair<Graph,Map<AspectNode,Node>> modelPlusMap = computeModel(view);
            model = modelPlusMap.first();
            viewToModelMap = modelPlusMap.second();
            errors = Collections.emptyList();
        } catch (FormatException e) {
            model = null;
            viewToModelMap = Collections.emptyMap();   
            errors = e.getErrors();
        }
        this.model = model;
        this.viewToModelMap = viewToModelMap;
        this.errors = errors;
	}
	
	/** Constructs an instance from a given graph model. */
	public AspectualGraphView(Graph model) {
		this.model = model;
		Pair<AspectGraph,Map<AspectNode,Node>> viewPlusMap = computeView(model);
		this.view = viewPlusMap.first();
		this.viewToModelMap = viewPlusMap.second();
		this.errors = Collections.emptyList();
	}
	
	public AspectGraph getAspectGraph() {
		return view;
	}
	
	public Graph toModel() throws FormatException {
        if (model == null) {
            throw new FormatException(getErrors());
        } else {
            return model;
        }
	}

	public List<String> getErrors() {
        return errors;
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
		Set<String> errors = new TreeSet<String>(view.getErrors());
		Graph model = getGraphFactory().newGraph();
		// we need to record the view-to-model element map for layout transfer
		NodeEdgeMap elementMap = new NodeEdgeHashMap();
		// we need to record the view-to-model node map for the return value
		Map<AspectNode,Node> viewToModelMap = new HashMap<AspectNode,Node>();
		// we need to record the model-to-view node map for removing isolated value nodes
		Map<Node,AspectNode> modelToViewMap = new HashMap<Node,AspectNode>();
		// copy the nodes from view to model
		Aspect attributeAspect = AttributeAspect.getInstance();
		for (AspectNode viewNode: view.nodeSet()) {
			try {
				for (AspectValue value : viewNode.getAspectMap().values()) {
					if (! isAllowedNodeValue(value)) {
						throw new FormatException("Node aspect value '%s' not allowed in graphs", value);
					}
				}
				Node nodeImage = AttributeAspect.createAttributeNode(viewNode, view);
				if (nodeImage == null) {
					nodeImage = model.addNode();
				} else if (isAllowedNode(nodeImage)) {
					model.addNode(nodeImage);
				} else {
					throw new FormatException("Node aspect value '%s' not allowed in graphs",
							viewNode.getValue(attributeAspect));
				}
				viewToModelMap.put(viewNode, nodeImage);
				modelToViewMap.put(nodeImage, viewNode);
			} catch (FormatException exc) {
				errors.addAll(exc.getErrors());
			}
		}
		elementMap.nodeMap().putAll(viewToModelMap);
		// copy the edges from view to model
		for (AspectEdge viewEdge : view.edgeSet()) {
			try {
				for (AspectValue value : viewEdge.getAspectMap().values()) {
					if (! isAllowedEdgeValue(value)) {
						throw new FormatException("Edge aspect value '%s' not allowed in graphs",
								value);
					}
				}
				Node[] endImages = new Node[viewEdge.endCount()];
				for (int i = 0; i < endImages.length; i++) {
					endImages[i] = viewToModelMap.get(viewEdge.end(i));
				}
				// create an image for the view edge
				Edge edgeImage = AttributeAspect.createAttributeEdge(viewEdge, view, endImages);
				if (edgeImage == null) {
					edgeImage = model.addEdge(endImages, viewEdge.label());
				} else if (!isAllowedEdge(edgeImage)) {
					throw new FormatException("Edge aspect value '%s' not allowed in graphs",
							viewEdge.getValue(attributeAspect));
				}
				elementMap.putEdge(viewEdge, edgeImage);
			} catch (FormatException exc) {
				errors.addAll(exc.getErrors());
			}
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
		if (errors.isEmpty()) {
			return new Pair<Graph,Map<AspectNode,Node>>(model, viewToModelMap);
		} else {
			throw new FormatException(new ArrayList<String>(errors));
		}
	}

	/**
	 * Tests if a certain attribute node is of the type allowed in graphs.
	 */
	private boolean isAllowedNode(Node node) {
		return node instanceof ValueNode && !(((ValueNode) node).getConstant() instanceof Variable);
	}

	/**
	 * Tests if a certain attribute node is of the type allowed in graphs.
	 */
	private boolean isAllowedEdge(Edge edge) {
		return edge instanceof ProductEdge && ((ProductEdge) edge).getOperation() instanceof Constant;
	}

	/**
	 * Tests if a certain aspect value is allowed for nodes in a graph view.
	 */
	private boolean isAllowedNodeValue(AspectValue value) {
		return value == RuleAspect.REMARK || value.getAspect() instanceof AttributeAspect;
	}

	/**
	 * Tests if a certain aspect value is allowed for edges in a graph view.
	 */
	private boolean isAllowedEdgeValue(AspectValue value) {
		return value == RuleAspect.REMARK || value.getAspect() instanceof AttributeAspect;
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
	/** List of errors in the view that prevent the model from being constructed. */
	private final List<String> errors;
	/** Map from view to model nodes. */
	private final Map<AspectNode,Node> viewToModelMap;
	/** The graph factory used by this view, to construct the model. */
	private GraphFactory graphFactory;
}
