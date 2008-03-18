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
 * $Id: NegatedSearchItem.java,v 1.10 2008-01-30 09:33:29 iovka Exp $
 */
package groove.match;

import groove.graph.Node;
import groove.match.SearchPlanStrategy.Search;

import java.util.Collection;
import java.util.HashSet;

/**
 * A search item that negates another search item.
 * @author Arend Rensink
 * @version $Revision $
 */
class NegatedSearchItem extends AbstractSearchItem {
	/** 
	 * Constructs a new search item. The item will match (precisely once)
	 * if and only the underlying item does not match.
	 * @param item the underlying, negated item
	 */
	public NegatedSearchItem(SearchItem item) {
		this.inner = item;
        this.neededNodes = new HashSet<Node>(item.needsNodes());
        neededNodes.addAll(item.bindsNodes());
        this.neededVars = new HashSet<String>(item.needsVars());
        neededVars.addAll(item.bindsVars());
	}
	
	public NegatedSearchRecord getRecord(Search search) {
		return new NegatedSearchRecord(search);
	}
	
    @Override
	public String toString() {
		return String.format("Negation of %s", inner); 
	}
    
	/**
     * Returns the union of the inner condition's needed and bound nodes.
     */
    @Override
    public Collection<Node> needsNodes() {
        return neededNodes;
    }

    /**
     * Returns the union of the inner condition's needed and bound variables.
     */
    @Override
    public Collection<String> needsVars() {
        return neededVars;
    }
    
    /** 
     * Since the order of negated search items does not influence the match,
     * all of them have the same rating.
     * @return <code>0</code> always
     */
    @Override
    int getRating() {
        return 0;
    }

    /** This implementation propagates the call to the inner item. */
    public void activate(SearchPlanStrategy strategy) {
        inner.activate(strategy);
    }

    /**
	 * The inner search item, for which we test for the negation.
	 */
	final SearchItem inner;
    /** Union of the needed and bound nodes of the inner condition. */
    private final Collection<Node> neededNodes;
    /** Union of the needed and bound variables of the inner condition. */
    private final Collection<String> neededVars;

    /** Record for the negated search item. */
    private class NegatedSearchRecord extends SingularRecord {
        /** Constructs a new record, for a given matcher. */
        NegatedSearchRecord(Search search) {
            super(search);
            this.innerRecord = inner.getRecord(search);
        }

        /**
         * Tests if the inner record can be satisfied; if so,
         * it is undone immediately to avoid lasting effects.
         */
        @Override
        boolean set() {
            boolean result = !innerRecord.find();
            innerRecord.reset();
            return result;
        }

        @Override
		public void reset() {
			super.reset();
			innerRecord.reset();
		}

		/**
         * The record of the inner (negated) item.
         */
        private final SearchItem.Record innerRecord;
    }
}
