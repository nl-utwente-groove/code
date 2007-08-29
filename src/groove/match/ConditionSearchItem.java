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
 * $Id: ConditionSearchItem.java,v 1.4 2007-08-29 11:07:44 rensink Exp $
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
     * Since the order of condition search items does not influence the match,
     * we give all of them the same rating.
     * @return <code>0</code> always
     */
    @Override
    int getRating() {
        return 0;
    }
    
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
        public String toString() {
            return String.format("%s: %b", ConditionSearchItem.this.toString(), isFound());
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
