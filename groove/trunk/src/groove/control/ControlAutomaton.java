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
 * $Id: ControlAutomaton.java,v 1.7 2007-11-22 15:39:10 fladder Exp $
 */
package groove.control;

import groove.graph.AbstractGraph;
import groove.graph.AbstractGraphShape;
import groove.graph.GraphCache;
import groove.graph.GraphShapeCache;
import groove.lts.LTS;
import groove.lts.State;
import groove.lts.Transition;
import groove.trans.Rule;
import groove.trans.RuleSystem;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ControlAutomaton extends AbstractGraphShape<GraphCache> {
	
	private ControlShape shape;
	
	
	private Set<ControlShape> activeShapes = new HashSet<ControlShape>();
	
	
	public ControlAutomaton(ControlShape shape) {
		this.shape = shape;
		
		
		// if there is a procedure, toggle it active
		if( shape.transitions().size() == 1 ) {
			this.toggleActive((ControlShape)shape.transitions().iterator().next());
		}
	}
	
	/**
	 * Return all edges in this graphshape 
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

	public Set<ControlState> nodeSet() {
		Set<ControlState> nodeSet = new HashSet<ControlState>();
		nodeSet.addAll(shape.states());
		
		for( ControlShape shape : activeShapes ) {
			nodeSet.addAll(shape.states());
		}
		return nodeSet;
	}
	
	public boolean isOpen(State state) {
		return false;
	}

	public ControlState startState() {
		return shape.getStart();
	}
	
	public boolean isSuccess(ControlState state) {
		return state.isSuccess();
	}
	
	public boolean isActive(ControlShape shape) {
		return activeShapes.contains(shape);
	}
	
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
