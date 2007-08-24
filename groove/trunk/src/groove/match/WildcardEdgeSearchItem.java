/* $Id: WildcardEdgeSearchItem.java,v 1.1 2007-08-24 17:34:56 rensink Exp $ */
package groove.match;

import groove.graph.BinaryEdge;
import groove.graph.Edge;
import groove.graph.Node;
import groove.rel.RegExprLabel;

import static groove.match.SearchPlanStrategy.Search;

/**
 * A search item that searches an image for an edge.
 * @author Arend Rensink
 * @version $Revision $
 */
public class WildcardEdgeSearchItem extends EdgeSearchItem {
	/** Record for this type of search item. */
	protected class WildcardEdgeRecord extends EdgeRecord {
		/** Constructs a new record, for a given matcher. */
		protected WildcardEdgeRecord(Search search) {
			super(search);
		}
		
		@Override
		void init() {
			if (isPreMatched(Edge.SOURCE_INDEX)) {
                Node sourceImage = getResult().getNode(getEdge().source());
                setMultiple(getTarget().outEdgeSet(sourceImage));
			} else {
				setMultiple(getTarget().edgeSet());
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
	public EdgeRecord getRecord(Search search) {
		return new WildcardEdgeRecord(search);
	}
}
