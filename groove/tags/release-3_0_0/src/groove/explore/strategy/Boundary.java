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
 * $Id: Boundary.java,v 1.2 2008-02-20 09:28:55 kastenberg Exp $
 */
package groove.explore.strategy;

import groove.lts.GraphTransition;

/**
 * This interface facilitates a model checking strategy
 * of using a boundary such that states reached by transitions
 * crossing the boundary will not be taken into account (yet).
 * 
 * @author Harmen Kastenberg
 * @version $Revision: 1.2 $
 */
public interface Boundary {

	/**
	 * Checks whether the given transition crosses the boundary.
	 * If so, it return <tt>true</tt>, otherwise <tt>false</tt>.
	 * @param transition the transition for which to check whether
	 * it crosses the boundary
	 * @return <tt>true</tt> if the transition crosses the boundary,
	 * <tt>false</tt> otherwise
	 */
	public boolean crossingBoundary(GraphTransition transition);

	/**
	 * Increases the boundary.
	 */
	public void increase();
}
