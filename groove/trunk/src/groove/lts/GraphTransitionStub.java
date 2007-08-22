// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente
 
// Licensed under the Apache License, Version 2.0 (the "License"); 
// you may not use this file except in compliance with the License. 
// You may obtain a copy of the License at 
// http://www.apache.org/licenses/LICENSE-2.0 
 
// Unless required by applicable law or agreed to in writing, 
// software distributed under the License is distributed on an 
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
// either express or implied. See the License for the specific 
// language governing permissions and limitations under the License.
/**
 * 
 */
package groove.lts;

import groove.graph.Element;
import groove.graph.Node;
import groove.trans.RuleEvent;

/**
 * Interface for objects modelling outgoing graph transitions.
 * These objects typically do not store the source of the transition;
 * instead they are stored inside by the source state.
 * @author Arend Rensink
 * @version $Revision: 1.5 $
 */
// JHK: public!
public interface GraphTransitionStub extends Element {
	/** 
	 * Returns the event that underlies the transition from a given source
	 * to this object.
	 */
	RuleEvent getEvent(GraphState source);

	/** 
	 * Returns the added nodes in the transition from a given source
	 * to this object.
	 */
	Node[] getAddedNodes(GraphState source);
	
	/**
	 * Returns the target state of this graph transition stub.
	 */
	GraphState target();

	/** 
	 * Constructs a graph transition from this transition stub,
	 * based on a given source.
	 * @param source the source state for the graph transition
	 * @return A graph transition based on the given source, and the
	 * rule, anchor images and target state stored in this out-transition.
	 */
	GraphTransition toTransition(GraphState source);
	
	/** 
	 * Indicates if the transition stub involves a non-trivial symmetry.
	 * @return <code>true</code> if the transition involves a non-trivial symmetry 
	 * @see GraphTransition#isSymmetry()
	 */
	boolean isSymmetry();
}