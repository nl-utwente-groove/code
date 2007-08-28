/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an 
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific 
 * language governing permissions and limitations under the License.
 *
 * $Id: ConditionSearchItem.java,v 1.3 2007-08-28 22:01:20 rensink Exp $
 */
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

        /** Returns <code>true</code> if {@link #isFirst()} and {@link #condition()} both hold. */
        @Override
        boolean next() {
            return isFirst() && condition();
        }

        /** This implementation does nothing. */
        @Override
        void undo() {
            // empty
        }

        /**
		 * Callback method implementing the condition of this search item.
		 */
		abstract boolean condition();
	}
}
