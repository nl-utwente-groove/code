/* $Id: AspectualGraphView.java,v 1.1 2007-03-28 15:12:31 rensink Exp $ */
package groove.graph.aspect;

import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.GraphFactory;
import groove.graph.GraphFormatException;
import groove.graph.Node;
import groove.util.Pair;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Arend Rensink
 * @version $Revision $
 */
public class AspectualGraphView implements AspectualView<Graph> {
	public AspectualGraphView(AspectGraph view) throws GraphFormatException {
		this.view = view;
		Pair<Graph,Map<AspectNode,Node>> modelPlusMap = computeModel(view);
		this.model = modelPlusMap.first();
		this.viewToModelMap = modelPlusMap.second();
		this.graphFactory = GraphFactory.getInstance();
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
	protected Pair<Graph,Map<AspectNode,Node>> computeModel(AspectGraph view) throws GraphFormatException {
		Graph model = graphFactory.newGraph();
		Map<AspectNode,Node> viewMap = new HashMap<AspectNode,Node>();
		for (AspectNode viewNode: view.nodeSet()) {
			Node nodeImage = AttributeAspect.createAttributeNode(viewNode, view);
			if (nodeImage == null) {
				nodeImage = model.addNode();
			} else {
				model.addNode(nodeImage);
			}
			viewMap.put(viewNode, nodeImage);
		}
		for (AspectEdge viewEdge: view.edgeSet()) {
			Node[] endImages = new Node[viewEdge.endCount()];
			for (int i = 0; i < endImages.length; i++) {
				endImages[i] = viewMap.get(viewEdge.end(i));
			}
			Edge edgeImage = AttributeAspect.createAttributeEdge(viewEdge, view, endImages);
			if (edgeImage == null) {
				edgeImage = model.addEdge(endImages, viewEdge.label());
			} else {
				model.addEdge(edgeImage);
			}
		}
		return new Pair<Graph,Map<AspectNode,Node>>(model, viewMap);
	}
	
	protected GraphFactory getGraphFactory() {
		return graphFactory;
	}
	
	/** The view represented by this object. */
	private final AspectGraph view;
	/** The graph model that is being viewed. */
	private final Graph model;
	/** Map from view to model nodes. */
	private final Map<AspectNode,Node> viewToModelMap;
	/** The graph factory used by this view, to construct the model. */
	private final GraphFactory graphFactory;
}
