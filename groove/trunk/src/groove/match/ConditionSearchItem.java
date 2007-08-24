/* $Id: ConditionSearchItem.java,v 1.1 2007-08-24 17:34:57 rensink Exp $ */
package groove.match;

import groove.match.SearchPlanStrategy.Search;

/**
 * Abstract class for a search plan item that only checks for a condition
 * without affecting the match if the condition holds.
 * @author Arend Rensink
 * @version $Revision $
 */
public abstract class ConditionSearchItem extends AbstractSearchItem {
	/**
	 * Record for a {@link ConditionSearchItem}.
	 * @author Arend Rensink
	 * @version $Revision $
	 */
	public abstract class ConditionRecord extends AbstractRecord {
        ConditionRecord(Search search) {
            super(search);
        }
        
        @Override
        void exit() {
            tested = false;
        }

        @Override
        void init() {
            // does nothing
        }

        @Override
        boolean next() {
            return !tested && condition();
        }

        @Override
        void undo() {
            tested = true;
        }

        /**
		 * Callback method implementing the condition of this search item.
		 */
		protected abstract boolean condition();

		/** 
         * Flag to indicate that the condition has been tested positively.
         * If so, {@link #next()} should yield <code>false</code> on the next invocation.
         */
		private boolean tested;
	}	
}
