/* $Id: NegatedSearchItem.java,v 1.2 2007-08-24 17:34:52 rensink Exp $ */
package groove.rel.match;

import groove.graph.match.Matcher;
import groove.graph.match.SearchItem;
import groove.trans.match.ConditionSearchItem;

/**
 * A search item that negates another search item.
 * @author Arend Rensink
 * @version $Revision $
 */
@Deprecated
public class NegatedSearchItem extends ConditionSearchItem {
	/** Record for the negated search item. */
    @Deprecated
	protected class NegatedSearchRecord extends ConditionRecord {
		/** Constructs a new record, for a given matcher. */
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

		@Override
		public void reset() {
			innerRecord.reset();
			super.reset();
		}
		
		/**
		 * The record of the inner (negated) item.
		 */
		private final SearchItem.Record innerRecord;
	}

	/** 
	 * Constructs a new search item. The item will match (precisely once)
	 * if and only the underlying item does not match.
	 * @param item the underlying, negated item
	 */
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
