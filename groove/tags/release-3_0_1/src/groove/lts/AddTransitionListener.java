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
 * $Id $
 */
package groove.lts;

import groove.graph.Edge;
import groove.graph.GraphShape;

/**
 * Listener that tests if any transitions are added to the LTS.
 * @version $Revision $
 */
public class AddTransitionListener extends LTSAdapter {
	/**
	 * Resets the transitions-added flag to <code>false</code>.
	 */
	public void reset() {
		transitionsAdded = false;
	}
	
	/** 
	 * Indicates if any transitions were added since {@link #reset()}
	 * was last called.
	 * @return <code>true</code> if any transitions were added
	 */
	public boolean isTransitionsAdded() {
		return transitionsAdded;
	}
	
	@Override
	public void addUpdate(GraphShape graph, Edge edge) {
		transitionsAdded = true;
	}
	
	/** 
	 * Variable that records if any transition have been added since the last
	 * {@link #reset()}.
	 */
	private boolean transitionsAdded;
}