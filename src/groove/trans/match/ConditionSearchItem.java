/* $Id: ConditionSearchItem.java,v 1.1.1.1 2007-03-20 10:05:21 kastenberg Exp $ */
package groove.trans.match;

import groove.graph.match.SearchItem;

/**
 * Abstract class for a search plan item that only checks for a condition
 * without affecting the match if the condition holds.
 * @author Arend Rensink
 * @version $Revision $
 */
public abstract class ConditionSearchItem implements SearchItem {
	/**
	 * Record for a {@link ConditionSearchItem}.
	 * @author Arend Rensink
	 * @version $Revision $
	 */
	public abstract class ConditionRecord implements Record {
		/**
		 * The first call delegates to {@link #condition()};
		 * the next call returns <code>false</code>.
		 */
		public boolean find() {
			if (atEnd) {
				// if we already returned false, as per contract
				// we restart
				reset();
			}
			if (called) {
				// if the test was called before, it should return false now
				atEnd = true;
				return false;
			} else {
				called = true;
				boolean result = condition();
				atEnd = !result;
				return result;
			}
		}
		
		/** 
		 * Does nothing.
		 */
		public void undo() {
			// nothing to undo
		}
		
		public void reset() {
			called = false;
			atEnd = false;
		}
		
		/**
		 * Callback method implementing the condition of this search item.
		 */
		protected abstract boolean condition();

		/** Flag to indicate that {@link #find()} has been called. */
		private boolean called;
		/** Flag to indicate that {@link #find()} has returned <code>false</code>. */
		private boolean atEnd;
	}
	
}
