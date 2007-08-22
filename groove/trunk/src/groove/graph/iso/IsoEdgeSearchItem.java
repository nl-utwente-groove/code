/* $Id: IsoEdgeSearchItem.java,v 1.3 2007-08-22 15:05:00 rensink Exp $ */
package groove.graph.iso;

import groove.graph.Edge;
import groove.graph.match.EdgeSearchItem;
import groove.graph.match.Matcher;

/**
 * A search item that searches an image for an edge.
 * @author Arend Rensink
 * @version $Revision $
 */
public class IsoEdgeSearchItem extends EdgeSearchItem<Edge> {
	private class IsoEdgeRecord extends EdgeRecord<IsoMatcher> {
        /** Creates a record for a given matcher. */
		protected IsoEdgeRecord(IsoMatcher matcher) {
			super(matcher);
		}

		/**
		 * The search plan should have made sure that all 
		 * end nodes have been matched.
		 */
		@Override
		protected void initImages() {
			Edge image = edge.imageFor(matcher.getSingularMap());
			setSingular(image);
		}
	}
	
	/**
	 * Creates a search item for a given edge.
	 * @param edge the edge from the domain for which we search images
	 */
	public IsoEdgeSearchItem(Edge edge) {
		super(edge, null);
	}
	
	@Override
	public Record get(Matcher matcher) {
		return new IsoEdgeRecord((IsoMatcher) matcher);
	}
}
