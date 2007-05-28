/* $Id: EdgeContent.java,v 1.1 2007-05-28 21:32:43 rensink Exp $ */
package groove.gui.jgraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import groove.graph.Edge;

/**
 * Content for a JCell consisting of a set of edges.
 * @author Arend Rensink
 * @version $Revision $
 */
public class EdgeContent extends JCellContent<Edge> {
	/** This implementation returns the labels of the edges. */
	@Override
	public Collection<String> getLabelSet() {
		List<String> result = new ArrayList<String>();
		for (Edge edge: this) {
			result.add(edge.label().text());
		}
		return result;
	}
}
