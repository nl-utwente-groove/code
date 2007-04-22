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
import groove.trans.RuleEvent;

/**
 * Interface for objects modelling outgoing graph transitions.
 * These objects typically do not store the source of the transition;
 * instead they are stored inside by the source state.
 * @author Arend Rensink
 * @version $Revision: 1.1 $
 */
interface GraphTransitionStub extends Element {
//	/**
//	 * Returns the transformation event of this transition.
//	 */
//	public RuleEvent getEvent();
//	/**
//	 * Returns the underlying rule of this graph transition.
//	 */
//	public abstract Rule getRule();
	
	/** 
	 * Returns the event that underlies the transition from a given source
	 * to this object.
	 */
	RuleEvent getEvent(GraphState source);

	/**
	 * Returns the target state of this graph transition.
	 * The return type is <code>Node</code> to make it compatible with
	 * {@link groove.graph.BinaryEdge#target()}, but this implementation always 
	 * returns a {@link GraphState}.
	 */
	GraphState target();

	/** 
	 * Constructs a graph transition from this out-transition,
	 * based on a given source.
	 * @param source the source state for the graph transition
	 * @return A graph transition based on the given source, and the
	 * rule, anchor images and target state stored in this out-transition.
	 */
	GraphTransition createTransition(GraphState source);
}