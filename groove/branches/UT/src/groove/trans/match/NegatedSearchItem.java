/* $Id: NegatedSearchItem.java,v 1.1.1.2 2007-03-20 10:42:57 kastenberg Exp $ */
package groove.trans.match;

import groove.graph.match.Matcher;
import groove.graph.match.SearchItem;

/**
 * A search item that negates another search item.
 * @author Arend Rensink
 * @version $Revision $
 */
public class NegatedSearchItem extends ConditionSearchItem {
	protected class NegatedSearchRecord extends ConditionRecord {
		protected NegatedSearchRecord(Matcher matcher) {
			this.innerRecord = inner.get(matcher);
		}

		/**
		 * Tests if the inner record can be satisfied; if so,
		 * it is undone immediately to avoid lasting effects.
		 */
		@Override
		protected boolean condition() {
			boolean result = !innerRecord.find();
			if (!result) {
				innerRecord.undo();
			}
			return result;
		}

		public void reset() {
			innerRecord.reset();
			super.reset();
		}
		
		/**
		 * The record of the inner (negated) item.
		 */
		private final SearchItem.Record innerRecord;
	}

	public NegatedSearchItem(SearchItem item) {
		this.inner = item;
	}
	
	public Record get(Matcher matcher) {
		return new NegatedSearchRecord(matcher);
	}
	
		@Override
	public String toString() {
		return String.format("Negation of %s", inner); 
	}

	/**
	 * The inner search item, for which we test for the negation.
	 */
	protected final SearchItem inner;
}
