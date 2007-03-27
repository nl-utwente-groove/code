/* $Id: AspectualView.java,v 1.1 2007-03-27 14:18:35 rensink Exp $ */
package groove.trans.view;

import groove.graph.Node;
import groove.graph.aspect.AspectGraph;

import java.util.Map;

/**
 * Bridge class that provides a view upon some other object
 * (the model) as an aspectual graph.
 * The model may for instance be a transformation rule, an attributed graph,
 * or a graph condition.
 * @author Arend Rensink
 * @version $Revision $
 */
public interface AspectualView {
	/**
	 * Returns the aspect graph representation of this view.
	 */
	AspectGraph getView();

	/**
	 * Returns a mapping from the nodes in the aspect graph view to the
	 * corresponding nodes in the model that is being viewed.
	 */
	Map<Node, Node> getViewMap();
}