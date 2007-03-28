/* $Id: AspectualView.java,v 1.1 2007-03-28 15:12:31 rensink Exp $ */
package groove.graph.aspect;

import groove.graph.Node;

import java.util.Map;

/**
 * Bridge class that provides a view upon some other object
 * (the model) as an aspectual graph.
 * The model may for instance be a transformation rule, an attributed graph,
 * or a graph condition.
 * @author Arend Rensink
 * @version $Revision $
 */
public interface AspectualView<Model> {
	/**
	 * Returns the aspect graph representation of this view.
	 */
	AspectGraph getView();
	
	/** Returns the underlying model. */
	Model getModel();

	/**
	 * Returns a mapping from the nodes in the aspect graph view to the
	 * corresponding nodes in the model that is being viewed.
	 */
	Map<AspectNode, Node> getMap();
}