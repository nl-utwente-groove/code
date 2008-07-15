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
 * $Id: ControlAutomaton.java,v 1.10 2008-01-30 11:13:57 fladder Exp $
 */
package groove.control;

import groove.graph.AbstractGraphShape;
import groove.graph.GraphCache;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
/** * Representation of a Control automaton that can be visualised in a JGraphPanel. * This class is loosely coupled to a top-level GraphShape, which contains the actual model.  * * Through active shapres, child scopes can be toggled either visible as an edge,  * or with it's node- and edge-content. *  * @author Tom Staijen */
public class ControlAutomaton extends AbstractGraphShape<GraphCache> {
	/** the top-level ControlShape **/	private ControlShape shape;

	/**	 * Construct a new ControlAutomaton for passed ControlShape.	 * The ControlShape should have a start-state and not have a parent.	 * @param shape	 */	public ControlAutomaton(ControlShape shape) {
		this.shape = shape;	}
	/**	 * Return all edges in the active graphshapes dynamically.	 * @return Set<ControlTransition> 	 */	public Set<ControlTransition> edgeSet() {		return shape.transitions();
	}
	/**	 * Return all nodes in the active graphshapes dynamically.	 * Set<ControlState> 	 */	public Set<ControlState> nodeSet() {		return shape.states();	}
	/**	 * Returns the start-state of the top-level GraphShape.	 * @return ControlState.	 */	public ControlState startState() {		return shape.getStart();	}

	/** 	 * Returns true if the given state is a success-state.	 * @param state	 * @return boolean	 */	public boolean isSuccess(ControlState state) {		return state.isSuccess();	}
	
}
