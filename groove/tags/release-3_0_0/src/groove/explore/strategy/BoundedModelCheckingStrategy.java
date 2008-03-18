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
 * $Id: BoundedModelCheckingStrategy.java,v 1.2 2008-03-04 14:44:51 kastenberg Exp $
 */
package groove.explore.strategy;


/** This depth-first search algorithm systematically generates all outgoing 
 * transitions of any visited state.
 * 
 * At each step, the exploration continues with a random successor fresh state,
 * or backtracks if there are no unexplored successor states.
 * 
 * Even though this depth first search backtracks for finding the next state
 * to explore, it is not considered as a backtracking strategy (in the sense
 * of {@link AbstractBacktrackingStrategy}. This is because all explored
 * states are closed, thus the strategy does not need to cache any
 * information, neither to know from where it backtracked. 
 * 
 * @author Harmen Kastenberg
 * @version $Revision: 1.2 $
 */
public interface BoundedModelCheckingStrategy<T>  extends ModelCheckingStrategy<T> {

	/**
	 * Sets the boundary specification used in the strategy.
	 * @param boudary the boundary specification to use
	 */
	public void setBoundary(Boundary boudary);

	/**
	 * Returns the boundary specification used in the strategy.
	 * @return the boundary specification
	 */
	public Boundary getBoundary();

	/**
	 * Checks whether all states have been fully explored.
	 * @return <tt>true</tt> if there are states left, <tt>false</tt> otherwise
	 */
	public boolean finished();
}
