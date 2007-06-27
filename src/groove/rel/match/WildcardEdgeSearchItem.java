/* $Id: WildcardEdgeSearchItem.java,v 1.1 2007-06-27 13:18:56 rensink Exp $ */
package groove.rel.match;

import groove.graph.BinaryEdge;
import groove.graph.Edge;
import groove.graph.Node;
import groove.graph.match.EdgeSearchItem;
import groove.graph.match.Matcher;
import groove.rel.RegExprLabel;

/**
 * A search item that searches an image for an edge.
 * @author Arend Rensink
 * @version $Revision $
 */
public class WildcardEdgeSearchItem extends EdgeSearchItem<Edge> {
	/** Record for this type of search item. */
	protected class WildcardEdgeRecord extends EdgeRecord<RegExprMatcher> {
		/** Constructs a new record, for a given matcher. */
		protected WildcardEdgeRecord(RegExprMatcher matcher) {
			super(matcher);
		}
		
		@Override
		protected void initImages() {
			if (isPreMatched(Edge.SOURCE_INDEX)) {
                Node sourceImage = matcher.getSingularMap().getNode(getEdge().source());
                setMultiple(matcher.cod().outEdgeSet(sourceImage));
			} else {
				setMultiple(matcher.cod().edgeSet());
			}
		}
	}

	/** 
	 * Constructs a new search item.
	 * The item will match any edge between the end images, and record
	 * the edge label as value of the wildcard variable.
	 */
	public WildcardEdgeSearchItem(Edge edge, boolean... matched) {
		super(edge, matched);
		assert RegExprLabel.isWildcard(edge.label()) && RegExprLabel.getWildcardId(edge.label()) == null: String.format("Edge %s is not a true wildcard edge", edge);
		assert edge.endCount() <= BinaryEdge.END_COUNT : String.format("Search item undefined for hyperedge", edge);
	}
	
	@Override
	public Record get(Matcher matcher) {
		return new WildcardEdgeRecord((RegExprMatcher) matcher);
	}
}
