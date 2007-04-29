/* $Id: AspectualView.java,v 1.1 2007-04-29 09:22:35 rensink Exp $ */
package groove.view;

import groove.graph.Node;
import groove.view.aspect.AspectGraph;
import groove.view.aspect.AspectNode;

import java.util.Map;

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
public interface AspectualView<Model> extends View<Model> {
	/**
	 * Returns the aspect graph representation of this view.
	 */
	AspectGraph getAspectGraph();
	
	/**
	 * Returns a mapping from the nodes in the aspect graph view to the
	 * corresponding nodes in the model that is being viewed.
	 */
	Map<AspectNode, Node> getMap();
}
