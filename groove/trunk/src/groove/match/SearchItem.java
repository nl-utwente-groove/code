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
 * $Id: SearchItem.java,v 1.2 2007-08-28 22:01:24 rensink Exp $
 */
package groove.match;

import groove.graph.Node;

import java.util.Collection;

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
         * Where necessary, the previously selected fit is first undone.
		 * The return value indicates if a new fit has been found (and selected).
         * @return <code>true</code> if a fit has been found
		 */
		boolean find();
//		
//		/**
//		 * Turns back all actions performed for finding the last match
//		 * (using {@link #find()}).
//		 * @throws IllegalStateException if {@link #find()} was not called
//		 * or the last call to {@link #find()} returned <code>false</code>
//		 * Calls of {@link #find()} and {@link #undo()} should be alternated.
//		 */
//		void undo();
//		
		/**
		 * Resets the record to the initial state, at which the search can be restarted.
		 */
		void reset();
	}
	
	/**
	 * Creates an activation record for this search item, for a given
	 * search.
	 */
	Record getRecord(SearchPlanStrategy.Search search);
    
    /** 
     * Returns the collection of nodes that should already be matched
     * before this item should be scheduled.
     */ 
    Collection<Node> needsNodes();
    
    /** 
     * Returns the collection of nodes for which this search item will 
     * find a matching when actvated.
     */ 
    Collection<Node> bindsNodes();
    
    /** 
     * Returns the collection of label variables that should already be matched
     * before this item should be scheduled.
     */ 
    Collection<String> needsVars();
    
    /** 
     * Returns the collection of label variables for which this search item will 
     * find a matching when actvated.
     */ 
    Collection<String> bindsVars();
//    
//    /** 
//     * Enables the search item so that it can optimise towards certain sets of
//     * pre-matched nodes and variables.
//     * This is called before the first invocation of {@link #getRecord(groove.match.SearchPlanStrategy.Search)}.
//     */
//    void schedule(Collection<Node> preMatchedNodes, Collection<String> preMatchedVars);
}
