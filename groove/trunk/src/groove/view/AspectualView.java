/* $Id: AspectualView.java,v 1.3 2007-05-14 18:52:03 rensink Exp $ */
package groove.view;

import groove.graph.GraphInfo;
import groove.graph.GraphShape;
import groove.graph.NodeEdgeMap;
import groove.trans.RuleNameLabel;
import groove.view.aspect.AspectGraph;


/**
 * View specialisation based on aspect graphs.
 * Apart from the aspect graph itself, an instance of this view maintains a map from
 * the nodes of the aspect graph to nodes in the model. This can be useful for
 * traceability.
 * The model may for instance be a transformation rule, an attributed graph,
 * or a graph condition.
 * @author Arend Rensink
 * @version $Revision $
 */
abstract public class AspectualView<Model> implements View<Model> {
	/**
	 * Returns the aspect graph representation of this view.
	 */
	abstract public AspectGraph getAspectGraph();
	
	/**
	 * Returns a mapping from the nodes in the aspect graph view to the
	 * corresponding nodes in the model that is being viewed.
	 */
	abstract public NodeEdgeMap getMap();

	/** 
	 * Creates a view from a given aspect graph.
	 * Depending on the role fo the graph, the result is an {@link AspectualRuleView} or
	 * an {@link AspectualGraphView}.
	 * @param aspectGraph the graph to create the view from
	 * @return a graph or rule view based on <code>aspectGraph</code>
	 * @see GraphInfo#getRole(GraphShape)
	 */
	static public AspectualView<?> createView(AspectGraph aspectGraph) {
		if (GraphInfo.hasRuleRole(aspectGraph)) {
			return new AspectualRuleView(aspectGraph, new RuleNameLabel(GraphInfo.getName(aspectGraph)));
		} else {
			return new AspectualGraphView(aspectGraph);
		}
	}
}
