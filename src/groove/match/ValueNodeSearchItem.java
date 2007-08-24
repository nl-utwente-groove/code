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
 * $Id: ValueNodeSearchItem.java,v 1.1 2007-08-24 17:34:56 rensink Exp $
 */
package groove.match;

import groove.graph.algebra.ValueNode;
import groove.match.SearchPlanStrategy.Search;

/**
 * A search item for a value node.
 * @author Arend Rensink
 * @version $Revision $
 */
public class ValueNodeSearchItem implements SearchItem {
	/**
	 * Record of a value node search item.
	 * @author Arend Rensink
	 * @version $Revision $
	 */
	private class ValueNodeRecord implements Record {
		/**
		 * Creates a record based on a given underlying matcher.
		 */
		protected ValueNodeRecord(SearchPlanStrategy.Search matcher) {
			this.search = matcher;
		}
		
		/**
		 * The first call puts #node to itself;
		 * the next call returns <code>false</code>.
		 */
		public boolean find() {
			if (findFailed) {
				// if we already returned false, as per contract
				// we restart
			    reset();
			}
			if (findCalled) {
				// if the test was called before, it should return false now
                search.getResult().removeNode(node);
				findFailed = true;
				return false;
			} else {
                search.getResult().putNode(node, node);
                findCalled = true;
				findFailed = false;
				return true;
			}
		}
        
        public void reset() {
            search.getResult().removeNode(node);
            findCalled = false;
            findFailed = false;
        }

        @Override
        public String toString() {
            return ValueNodeRecord.this.toString();
        }

        /** The underlying matcher of the search record. */
		private final Search search;
		/** Flag to indicate that {@link #find()} has been called. */
		private boolean findCalled;
		/** Flag to indicate that {@link #find()} has returned <code>false</code>. */
		private boolean findFailed;
	}

	/**
	 * Creates a search item for a value node.
	 * The image is always the node itself.
	 * @param node the node to be matched
	 */
	public ValueNodeSearchItem(ValueNode node) {
		this.node = node;
	}
	
	public Record getRecord(SearchPlanStrategy.Search matcher) {
		return new ValueNodeRecord(matcher);
	}

	@Override
	public String toString() {
		return String.format("Value %s", node); 
	}

	/** Returns the value node we are looking up. */
	public ValueNode getNode() {
		return node;
	}
	
	/** The value node to be matched. */
	private final ValueNode node;
}
