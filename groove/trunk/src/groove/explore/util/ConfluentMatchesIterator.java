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
 * $Id$
 */
package groove.explore.util;

import groove.lts.GraphState;
import groove.trans.SystemRecord;

/**
 * @author Eduardo Zambon
 */
public class ConfluentMatchesIterator extends MatchesIterator {

    /**
     * @param state
     * @param rules
     * @param record
     */
    public ConfluentMatchesIterator(GraphState state, ExploreCache rules,
            SystemRecord record) {
        super(state, rules, record);
    }

    /**
     * This method insures that eventIter is incremented until the next element
     * to be returned, or set to null if no more elements are available.
     */
    @Override
    protected void goToNext() {
        if (this.currentRule.isConfluent()) {
            if (this.eventIter != null && !this.eventIter.hasNext()) {
                // We already applied a match of the confluent rule and
                // there are more matches. Ignore them.
                this.nextRule();
            } else {
                // This is a reentrant call. Ignore it.
            }
        } else { // Normal rule.
            while (this.eventIter != null && !this.eventIter.hasNext()
                    && this.nextRule()) {
                // Empty, as in super implementation.
            }
        }
    }
    
}
