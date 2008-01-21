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
 * $Id: ControlAutomaton.java,v 1.8 2007-11-26 08:58:11 fladder Exp $
 */
package groove.control;

import groove.graph.AbstractGraphShape;
import groove.graph.GraphCache;

import java.util.HashSet;
import java.util.Set;


/**
 * Representation of a Control automaton that can be visualised in a JGraphPanel.
 * This class is loosely coupled to a top-level GraphShape, which contains the actual model. 
 *
 * Through active shapres, child scopes can be toggled either visible as an edge, 
 * or with it's node- and edge-content.
 * 
 * @author Tom Staijen
 */
public class ControlAutomaton extends AbstractGraphShape<GraphCache> {
	
	/** the top-level ControlShape **/
	private ControlShape shape;
	
	/** container for the active shapres **/
	private Set<ControlShape> activeShapes = new HashSet<ControlShape>();
	
	
	/**
	 * Construct a new ControlAutomaton for passed ControlShape.
	 * The ControlShape should have a start-state and not have a parent.
	 * @param shape
	 */
	public ControlAutomaton(ControlShape shape) {
		this.shape = shape;
		
		// if there is a procedure, toggle it active
		if( shape.transitions().size() == 1 ) {
			this.toggleActive((ControlShape)shape.transitions().iterator().next());
		}
	}
	
	/**
	 * Return all edges in the active graphshapes dynamically. 
	 */
	public Set<ControlTransition> edgeSet() {
		Set<ControlTransition> tempSet = new HashSet<ControlTransition>();
		tempSet.addAll(shape.transitions());
		
		Set<ControlTransition> edgeSet = new HashSet<ControlTransition>();
		
		while( tempSet.size() > 0 ) {
			Set<ControlTransition> tempSet2 = new HashSet<ControlTransition>();
			
			for( ControlTransition edge: tempSet ) {
				if( edge instanceof ControlShape && isActive((ControlShape) edge)) {
					tempSet2.addAll(((ControlShape)edge).transitions());
				} else {
					edgeSet.add(edge);
				}
			}
			
			tempSet.clear();
			tempSet.addAll(tempSet2);
	    }
		
		return edgeSet;
	}

	/**
	 * Return all nodes in the active graphshapes dynamically. 
	 */
	public Set<ControlState> nodeSet() {
		Set<ControlState> nodeSet = new HashSet<ControlState>();
		nodeSet.addAll(shape.states());
		
		for( ControlShape shape : activeShapes ) {
			nodeSet.addAll(shape.states());
		}
		return nodeSet;
	}
	
	/**
	 * Returns the startstate of the top-level GraphShape.
	 * @return ControlState.
	 */
	public ControlState startState() {
		return shape.getStart();
	}
	
	/** 
	 * Returns true if the given state is a success-state.
	 * @param state
	 * @return boolean
	 */
	public boolean isSuccess(ControlState state) {
		return state.isSuccess();
	}

	private boolean isActive(ControlShape shape) {
		return activeShapes.contains(shape);
	}
	
	/**
	 * 
	 * Toggles the activeness of the ControlShape.
	 * @param shape
	 */
	public void toggleActive(ControlShape shape) {
		if( activeShapes.contains(shape)) {
			activeShapes.remove(shape);
			this.fireAddEdge(shape);
			
		}
		else {
			activeShapes.add(shape);
			this.fireRemoveEdge(shape);
		}
	}
}
