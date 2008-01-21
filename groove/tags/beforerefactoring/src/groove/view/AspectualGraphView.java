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
 * $Id: AspectualGraphView.java,v 1.17 2007-11-19 12:19:18 rensink Exp $
 */
package groove.view;

import static groove.view.aspect.AttributeAspect.getAttributeValue;
import static groove.view.aspect.AttributeAspect.getAttributeValueFor;
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
import groove.util.Pair;
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
public class AspectualGraphView extends AspectualView<Graph> {
	/** 
	 * Constructs an instance from a given aspect graph view.
	 * It is required that the aspect graph has a name.
	 * @see GraphInfo#getName(groove.graph.GraphShape) 
	 */
	public AspectualGraphView(AspectGraph view) {
		this.view = view;
		// we fix the view; is it conceptually right to do that here?
		view.setFixed();
		String name = GraphInfo.getName(view);
		this.name = name == null ? "" : name;
        Graph model;
        NodeEdgeMap viewToModelMap;
        List<String> errors;
		try {
            Pair<Graph,NodeEdgeMap> modelPlusMap = computeModel(view);
            model = modelPlusMap.first();
            viewToModelMap = modelPlusMap.second();
            errors = Collections.emptyList();
        } catch (FormatException e) {
            model = null;
            viewToModelMap = new NodeEdgeHashMap();   
            errors = e.getErrors();
        }
        this.model = model;
        this.viewToModelMap = viewToModelMap;
        this.errors = errors;
	}
	
	/** 
	 * Constructs an instance from a given graph model.
	 * It is required that the model has a name.
	 * @see GraphInfo#getName(groove.graph.GraphShape)  
	 */
	public AspectualGraphView(Graph model) {
		this.model = model;
		this.name = GraphInfo.getName(model);
		if (name == null) {
			throw new IllegalArgumentException("Model has no name.");
		}
		Pair<AspectGraph,NodeEdgeMap> viewPlusMap = computeView(model);
		this.view = viewPlusMap.first();
		this.viewToModelMap = viewPlusMap.second();
		this.errors = Collections.emptyList();
	}
	
	public String getName() {
		return name;
	}

	@Override
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

	@Override
	public NodeEdgeMap getMap() {
		return viewToModelMap;
	}
	
	@Override
    protected LabelParser getDefaultLabelParser() {
        return FreeLabelParser.getInstance();
    }

    /**
	 * Computes a fresh model from a given aspect graph,
	 * together with a mapping from the aspect graph's node to the
	 * (fresh) graph nodes. 
	 */
	private Pair<Graph, NodeEdgeMap> computeModel(AspectGraph view) throws FormatException {
		Set<String> errors = new TreeSet<String>(view.getErrors());
		Graph model = getGraphFactory().newGraph();
		// we need to record the view-to-model element map for layout transfer
		NodeEdgeMap elementMap = new NodeEdgeHashMap();
		// we need to record the view-to-model node map for the return value
		Map<AspectNode,Node> viewToModelMap = new HashMap<AspectNode,Node>();
		// we need to record the model-to-view node map for removing isolated value nodes
		Map<Node,AspectNode> modelToViewMap = new HashMap<Node,AspectNode>();
		// copy the nodes from view to model
		for (AspectNode viewNode: view.nodeSet()) {
			try {
				boolean actualNode = true;
				for (AspectValue value : viewNode.getAspectMap().values()) {
					if (isVirtualValue(value)) {
						actualNode = false;
					} else if (! isAllowedValue(value)) {
						throw new FormatException("Node aspect value '%s' not allowed in graphs", value);
					}
				}
				// include the node in the model if it is not virtual
				if (actualNode) {
					Node nodeImage = AttributeAspect.createAttributeNode(viewNode, view);
					if (nodeImage == null) {
						nodeImage = model.addNode();
					} else if (isAllowedNode(nodeImage)) {
						model.addNode(nodeImage);
					} else {
						errors.add(String.format("Node aspect value '%s' not allowed in graphs",
								getAttributeValue(viewNode)));
					}
					viewToModelMap.put(viewNode, nodeImage);
					modelToViewMap.put(nodeImage, viewNode);
				}
			} catch (FormatException exc) {
				errors.addAll(exc.getErrors());
			}
		}
		elementMap.nodeMap().putAll(viewToModelMap);
		// copy the edges from view to model
		for (AspectEdge viewEdge : view.edgeSet()) {
			boolean edgeInModel = true;
			for (AspectValue value : viewEdge.getAspectMap().values()) {
				if (isVirtualValue(value)) {
					edgeInModel = false;
				} else if (!isAllowedValue(value)) {
					throw new FormatException(
							"Edge aspect value '%s' not allowed in graphs",
							value);
				}
			}
			// include the edge in the model if it is not virtual
			Node[] endImages = new Node[viewEdge.endCount()];
			for (int i = 0; edgeInModel && i < endImages.length; i++) {
				endImages[i] = viewToModelMap.get(viewEdge.end(i));
				edgeInModel = endImages[i] != null;
			}
			if (edgeInModel) {
				try {
					// create an image for the view edge
					Edge edgeImage = AttributeAspect.createAttributeEdge(viewEdge,
							endImages);
					if (edgeImage == null) {
						edgeImage = model.addEdge(endImages, parse(viewEdge));
					} else if (!isAllowedEdge(edgeImage)) {
						throw new FormatException(
								"Edge aspect value '%s' not allowed in graphs",
								getAttributeValue(viewEdge));
					}
					elementMap.putEdge(viewEdge, edgeImage);
				} catch (FormatException exc) {
					errors.addAll(exc.getErrors());
				}
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
		    model.setFixed();
			return new Pair<Graph,NodeEdgeMap>(model, elementMap);
		} else {
			throw new FormatException(new ArrayList<String>(errors));
		}
	}

	/**
	 * Tests if a certain attribute node is of the type allowed in graphs.
	 */
	private boolean isAllowedNode(Node node) {
		return node instanceof ValueNode && ((ValueNode) node).hasValue();
	}

	/**
	 * Tests if a certain attribute edge is of the type allowed in graphs.
	 */
	private boolean isAllowedEdge(Edge edge) {
		return edge instanceof ProductEdge && ((ProductEdge) edge).getOperation() instanceof Constant;
	}

	/**
	 * Tests if a certain non-virtual aspect value is allowed for nodes in a graph view.
	 */
	private boolean isAllowedValue(AspectValue value) {
		return value.getAspect() instanceof AttributeAspect;
	}

	/**
	 * Tests if a certain aspect value causes a graph element to be virtual.
	 */
	private boolean isVirtualValue(AspectValue value) {
		return RuleAspect.REMARK.equals(value);
	}

	/**
	 * Constructs an aspect graph from a possibly attributed graph,
	 * together with a mapping from that aspect graph to the original graph.
	 * @param model the graph to be converted (which may be attributed, i.e.,
	 * contains 
	 * @return a pair of aspect graph plus a mapping from that aspect graph
	 * to <code>model</code>
	 */
	private Pair<AspectGraph, NodeEdgeMap> computeView(Graph model) {
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
				AspectValue value = getAttributeValueFor(node);
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
				AspectEdge edgeImage = new AspectEdge(endImages, unparse(edge), AttributeAspect.getAttributeValueFor(edge));
				view.addEdge(edgeImage);
				// update the model-to-view element map
				elementMap.edgeMap().put(edge, edgeImage);
			}
			view.setFixed();
		} catch (FormatException exc) {
			throw new IllegalStateException("Exception should not occur: "+exc);
		}
		// transfer graph information such as layout from model to view
		GraphInfo.transfer(model, view, elementMap);
		return new Pair<AspectGraph,NodeEdgeMap>(view, elementMap);
	}
	
	/**
     * Returns the graph factory used to construct the model.
     */
    private GraphFactory getGraphFactory() {
    	return graphFactory;
    }
	
    /** The name of the view. */
    private final String name;
	/** The view represented by this object. */
	private final AspectGraph view;
	/** The graph model that is being viewed. */
	private final Graph model;
	/** List of errors in the view that prevent the model from being constructed. */
	private final List<String> errors;
	/** Map from view to model nodes. */
	private final NodeEdgeMap viewToModelMap;
	/** The graph factory used by this view, to construct the model. */
	private static final GraphFactory graphFactory = GraphFactory.getInstance();
}
