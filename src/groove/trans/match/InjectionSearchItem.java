/* $Id: InjectionSearchItem.java,v 1.1.1.2 2007-03-20 10:42:57 kastenberg Exp $ */
package groove.trans.match;

import groove.graph.Node;
import groove.graph.NodeEdgeMap;
import groove.graph.match.Matcher;

/**
 * A search item that searches an image for an edge.
 * @author Arend Rensink
 * @version $Revision $
 */
public class InjectionSearchItem extends ConditionSearchItem {
	protected class MergeEmbargoRecord extends ConditionRecord {
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

	public InjectionSearchItem(Node node1, Node node2) {
		this.node1 = node1;
		this.node2 = node2;
	}
	
	public Record get(Matcher matcher) {
		return new MergeEmbargoRecord(matcher);
	}
	
	@Override
	public String toString() {
		return String.format("Separate %s and %s", node1, node2); 
	}

	/**
	 * The nodes which may not be merged.
	 */
	protected final Node node1, node2;
}
