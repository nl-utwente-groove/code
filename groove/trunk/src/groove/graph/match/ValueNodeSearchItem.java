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
 * $Id: ValueNodeSearchItem.java,v 1.2 2007-04-04 07:04:28 rensink Exp $
 */
package groove.graph.match;

import groove.graph.algebra.ValueNode;

/**
 * A search item for a value node.
 * @author Arend Rensink
 * @version $Revision $
 */
public class ValueNodeSearchItem implements SearchItem {
	/**
	 * Record of an edge seach item, storing an iterator over the
	 * candidate images.
	 * @author Arend Rensink
	 * @version $Revision $
	 */
	private class ValueNodeRecord implements Record {
		/**
		 * Creates a record based on a given underlying matcher.
		 */
		protected ValueNodeRecord(Matcher matcher) {
			this.matcher = matcher;
		}
		
		/**
		 * The first call delegates to {@link #select()};
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
				undo();
				atEnd = true;
				return false;
			} else {
				select();
				atEnd = false;
				called = true;
				return true;
			}
		}

		/**
		 * Computes the result of the product edge's operation
		 */
		public void select() {
			matcher.getSingularMap().putNode(node, node);
		}

		/**
		 * Removes the edge added during the last {@link #find()}, if any.
		 */
		public void undo() {
			matcher.getSingularMap().removeNode(node);
		}
		
		public void reset() {
			called = false;
			atEnd = false;
		}

		/** The underlying matcher of the search record. */
		private final Matcher matcher;
		/** Flag to indicate that {@link #find()} has been called. */
		private boolean called;
		/** Flag to indicate that {@link #find()} has returned <code>false</code>. */
		private boolean atEnd;
	}

	/**
	 * Creates a search item for a value node.
	 * The image is always the node itself.
	 * @param node the node to be matched
	 */
	public ValueNodeSearchItem(ValueNode node) {
		this.node = node;
	}
	
	public Record get(Matcher matcher) {
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
