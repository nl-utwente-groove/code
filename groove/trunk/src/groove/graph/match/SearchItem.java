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
 * $Id: SearchItem.java,v 1.1.1.1 2007-03-20 10:05:36 kastenberg Exp $
 */
package groove.graph.match;

/**
 * Interface for an item in a search plan.
 * @author Arend Rensink
 * @version $Revision $
 */
public interface SearchItem {
	/**
	 * Interface for an activation record of a search item.
	 * @author Arend Rensink
	 * @version $Revision $
	 */
	interface Record {
		/**
		 * Tries to find (and select, if appropriate) the next fit for this search item.
		 * The return value indicates if this has succeeded.
		 * If {@link #undo()} has not been called since the last invocation,
		 * this is done first.
		 * If {@link #find()} fails to succeed, at the next call the record is 
		 * reset so that the search will start afresh.
		 * @return <code>true</code> if a fit has been found.
		 */
		boolean find();
		
		/**
		 * Turns back all actions performed for finding the last match
		 * (using {@link #find()}).
		 * @throws IllegalStateException if {@link #find()} was not called
		 * or the last call to {@link #find()} returned <code>false</code>
		 * Calls of {@link #find()} and {@link #undo()} should be alternated.
		 */
		void undo();
		
		/**
		 * Resets the record to its initial state, directly after creation.
		 * Only allowed if the last action was {@link #undo()}.
		 */
		void reset();
	}
	
	/**
	 * Creates an activation record for this search item, for a given
	 * matcher.
	 */
	Record get(Matcher matcher);
}
