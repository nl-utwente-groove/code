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
 * $Id: BuchiAutomatonGraph.java,v 1.1 2008-02-20 08:24:16 kastenberg Exp $
 */
package groove.verify;

import groove.graph.DefaultGraph;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Node;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import rwth.i2.ltl2ba4j.model.ITransition;

/**
 * Class implementing a graph representation of a Buchi automaton.
 * 
 * @author Harmen Kastenberg
 * @version $Revision$ $Date: 2008-02-20 08:24:16 $
 */
public class BuchiAutomatonGraph extends DefaultGraph {

	static public Graph getPrototype() {
		return new BuchiAutomatonGraph();
	}

	@Override
	public Graph newGraph() {
		return new BuchiAutomatonGraph();
	}

	private BuchiAutomatonGraph() {
		finalStates = new HashSet<Node>();
		initialStates = new HashSet<Node>();
		edge2transition = new HashMap<Edge,ITransition>();
	}

	/**
	 * Stores the couple of the given edge and {@link ITransition}.
	 * @param edge the edge
	 * @param transition the {@link ITransition}
	 * @return @see {@link java.util.Map#put(Object, Object)}
	 */
	public boolean putEdge(Edge edge, ITransition transition) {
		return (edge2transition.put(edge, transition) == null);
	}

	/**
	 * Returns the {@link ITransition}-instance corresponding to the given edge.
	 * @param edge the edge for which to retrieve the corresponding {@link ITransition}-instance 
	 * @return the corresponding {@link ITransition}, <code>null</code> if there is none.
	 */
	public ITransition getTransition(Edge edge) {
		return edge2transition.get(edge);
	}

	/**
	 * Adds the given node to the set of final states.
	 * @param node the node to be added to the set of final states
	 */
	public void setFinal(Node node) {
		finalStates.add(node);
	}

	/**
	 * Adds the given node to the set of initial states.
	 * @param node the node to be added to the set of initial states
	 */
	public void setInitial(Node node) {
		initialStates.add(node);
	}

	/**
	 * Checks whether a given node is in the set of final states.
	 * @param node the node to be checked
	 * @return <tt>true</tt> if the node is in the
	 * set of final states, <tt>false</tt> otherwise
	 */
	public boolean isFinal(Node node) {
		return finalStates.contains(node);
	}

	/**
	 * Checks whether a given node is in the set of initial states.
	 * @param node the node to be checked
	 * @return <tt>true</tt> if the node is in the
	 * set of initial states, <tt>false</tt> otherwise
	 */
	public boolean isInitial(Node node) {
		return initialStates.contains(node);
	}

	/**
	 * Returns the set of final states.
	 * @return the set of final states
	 */
	public Set<Node> finalStates() {
		return finalStates;
	}

	/**
	 * Returns the set of initial states.
	 * @return the set of initial states
	 */
	public Set<Node> initialStates() {
		return initialStates;
	}

	private Set<Node> finalStates;
	private Set<Node> initialStates;
	private Map<Edge,ITransition> edge2transition;
}
