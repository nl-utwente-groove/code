/* $Id: InjectionSearchItem.java,v 1.1 2007-04-18 08:36:08 rensink Exp $ */
package groove.rel.match;

import java.util.Collection;
import java.util.Iterator;

import groove.graph.Node;
import groove.graph.NodeEdgeMap;
import groove.graph.match.Matcher;
import groove.trans.match.ConditionSearchItem;

/**
 * A search item that searches an image for an edge.
 * @author Arend Rensink
 * @version $Revision $
 */
public class InjectionSearchItem extends ConditionSearchItem {
	/** The record for this search item. */
	protected class MergeEmbargoRecord extends ConditionRecord {
		/** Constructs a fresh record, for a given matcher. */
		protected MergeEmbargoRecord(Matcher matcher) {
			this.matcher = matcher;
			assert matcher.getSingularMap().containsKey(node1) : String.format("Merge embargo node %s not yet matched", node1);
			assert matcher.getSingularMap().containsKey(node2) : String.format("Merge embargo node %s not yet matched", node2);
		}

		/**
		 * Tests if the images of {@link #node1} and {@link #node2} are distinct.
		 */
		@Override
		protected boolean condition() {
			NodeEdgeMap elementMap = matcher.getSingularMap();
			return elementMap.getNode(node1) != elementMap.getNode(node2);
		}
		
		private final Matcher matcher;
	}

	/** 
	 * Constructs an injection item, which chechks for the injectivity
	 * of the match found so far. That is, the item will match if and only if the
	 * nodes in a given set have been matched injectively.
	 * @param nodes the nodes that should be matched injectively
	 */
	public InjectionSearchItem(Collection<? extends Node> nodes) {
		assert nodes.size() == 2: String.format("Injection %s should have size 2", nodes);
		Iterator<? extends Node> nodeIter = nodes.iterator();
		this.node1 = nodeIter.next();
		this.node2 = nodeIter.next();
	}
	
	public Record get(Matcher matcher) {
		return new MergeEmbargoRecord(matcher);
	}
	
	@Override
	public String toString() {
		return String.format("Separate %s and %s", node1, node2); 
	}

	/**
	 * First node which may not be merged.
	 */
	protected final Node node1;
	/**
	 * Second node which may not be merged.
	 */
	protected final Node node2;
}
