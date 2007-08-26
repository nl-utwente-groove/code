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
 * $Id: Explorer.java,v 1.3 2007-08-26 07:24:21 rensink Exp $
 */
package groove.calc;

import groove.graph.Graph;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.explore.AbstractStrategy;
import groove.trans.GraphGrammar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Explorer class offering several light-weight methods of finding 
 * graphs and other objects within a given GTS.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Explorer extends AbstractStrategy {
	public String getName() {
		return "Utility explorer";
	}

	public String getShortDescription() {
		return "Dedicated to improving the use of grammars to calculate graphs";
	}

	/** 
	 * This implementation returns either one or all final states of the
	 * GTS, depending on the setting of {@link #isGetAll()}.
	 */
	public Collection<GraphState> explore() {
		return explore(stateId, isFinal);
	}

	/** 
	 * This implementation returns either one or all states of the
	 * GTS satisfying a given property, depending on the setting of {@link #isGetAll()}.
	 */
	public Collection<GraphState> explore(Property<GraphState> p) {
		return explore(stateId, p, isGetAll());
	}

	/** 
	 * Generic function to explore the state space and collect 
	 * objects satisfying a given property.
	 * The results are not the states themselves, but are obtained from the states
	 * through some (generic) transformation; the property is defined on the
	 * transformed objects.
	 * Depending on the setting of {@link #isGetAll()}, either all objects or
	 * just (at most) one is collected.
	 * @param f transformer from graph states to the desired objects
	 * @param p property on the collected objects
	 * @return the collection of (one or all) objects
	 */
	public <T> Collection<T> explore(Function<GraphState,T> f, Property<T> p) {
		return explore(f, p, isGetAll());
	}

	/** 
	 * Generic function to explore the state space and collect 
	 * objects satisfying a given property.
	 * The results are not the states themselves, but are obtained from the states
	 * through some (generic) transformation; the property is defined on the
	 * transformed objects.
	 * A further parameter controls if all results are collected, or just the
	 * first that is found.
	 * @param f transformer from graph states to the desired objects
	 * @param p property on the collected objects
	 * @param all flag to control if all results or just one result are collected 
	 * @return the collection of (one or all) objects
	 */
	public <T> Collection<T> explore(Function<GraphState,T> f, Property<T> p, boolean all) {
		return explore(stateTrue, f, p, all);
	}

	/** 
	 * Generic function to explore the state space and collect  
	 * results satisfying a given property.
	 * The results are not the states themselves, but are obtained from the states
	 * through some (generic) transformation; there are two filtering properties,
	 * one applied on the graph states before transformation, one on the collected
	 * objects after transformation.
	 * A further parameter controls if all results are collected, or just the
	 * first that is found.
	 * @param p1 filtering property on the graph states 
	 * @param f transformer from graph states to the desired objects
	 * @param p2 filtering property on the collected objects
	 * @param all flag to control if all results or just one result are collected 
	 * @return the collection of (one or all) objects
	 */
	public <T> Collection<T> explore(Property<GraphState> p1, Function<GraphState,T> f, Property<T> p2, boolean all) {
		Collection<T> result = new ArrayList<T>();
		Collection<GraphState> currentStates = new ArrayList<GraphState>(getGTS().nodeCount());
		currentStates.addAll(getGTS().nodeSet());
		Collection<GraphState> nextStates = new ArrayList<GraphState>();
		FreshStateCollector collector = getCollector();
		boolean halt = false;
		while (!halt && ! currentStates.isEmpty()) {
			collector.set(nextStates);
			Iterator<? extends GraphState> stateIter = currentStates.iterator();
			while (!halt && stateIter.hasNext()) {
				GraphState state = stateIter.next();
				if (p1.isSatisfied(state)) {
					T subject = f.apply(state);
					if (p2.isSatisfied(subject)) {
						result.add(subject);
						halt = !all;
					}
				}
				if (! halt) {
					explore(state);
				}
			}
			collector.reset();
			currentStates = nextStates;
			nextStates = new ArrayList<GraphState>(currentStates.size() * 2);
		}
		return result;
	}

	/** 
	 * Returns all graphs that satisfy a given property. 
	 */
	public <T> Collection<T> getAll(Property<GraphState> p, Function<GraphState,T> f) {
		return explore(p, f, Property.<T>createTrue(), true);
	}

	/** 
	 * Returns the first graph that satisfies a given property,
	 * or <code>null</code> if there is none. 
	 */
	public <T> T get(Property<GraphState> p, Function<GraphState,T> f) {
		Collection<T> collection = explore(p, f, Property.<T>createTrue(), false);
		if (collection.isEmpty()) {
			return null;
		} else {
			return collection.iterator().next();
		}
	}

	/** 
	 * Returns all graphs that satisfy a given property. 
	 */
	public Collection<Graph> getAll(Property<Graph> p) {
		return explore(getGraph, p, true);
	}

	/** 
	 * Returns the first graph that satisfies a given property,
	 * or <code>null</code> if there is none. 
	 */
	public Graph get(Property<Graph> p) {
		Collection<Graph> collection = explore(getGraph, p, false);
		if (collection.isEmpty()) {
			return null;
		} else {
			return collection.iterator().next();
		}
	}

	/** 
	 * Returns all final graphs.
	 */
	public Collection<Graph> getAll() {
		return explore(isFinal, getGraph, Property.<Graph>createTrue(), true);
	}

	/** 
	 * Returns a final graph, or <code>null</code> if there is none.
	 */
	public Graph get() {
		Collection<Graph> collection = explore(isFinal, getGraph, Property.<Graph>createTrue(), true);
		if (collection.isEmpty()) {
			return null;
		} else {
			return collection.iterator().next();
		}
	}

	/** 
	 * Sets the state of this exploration strategy to either
	 * finding all objects satisfying the criteria, or just the first.
	 */
	public void setGetAll(boolean all) {
		this.all = all;
	}
	
	/**
	 * Indicates if the exploration finds all objects satisfying the criteria
	 * or just the first.
	 */
	protected boolean isGetAll() {
		return all;
	}

	/** Property that tests if a given graph state is final in the GTS. */
	private final Property<GraphState> isFinal = new Property<GraphState>() {
		@Override
		public boolean isSatisfied(GraphState state) {
			explore(state);
			return getGTS().isFinal(state);
		}
	};

	/** True property for all states. */
	private final Property<GraphState> stateTrue = Property.createTrue();
		
	/**
	 * Identity function on graph states. 
	 */
	private final Function<GraphState,GraphState> stateId = Function.<GraphState>createId();

	/**
	 * Function extracting the graph from a graph state, by calling
	 * {@link GraphState#getGraph()}.
	 */
	private final Function<GraphState,Graph> getGraph = new Function<GraphState,Graph>() {
		@Override
		public Graph apply(GraphState arg) {
			return arg.getGraph();
		}
	};
	
	/** Flag dictating whether all results or just the first result should be returned by exploration. */
	private boolean all;

	/** 
	 * Returns all graphs generated by a given graph grammar that satisfy a given property.
	 * @param gg the graph grammar to be evaluated 
	 * @param p the property that the graphs to be returned should satisfy
	 * @return the collection of all graphs from reachable states in the
	 * expansion of <code>gg</code> that satisfy the property <code>p</code>
	 */
	static public Collection<Graph> getAll(GraphGrammar gg, Property<Graph> p) {
		Explorer explorer = new Explorer();
		explorer.setGTS(new GTS(gg));
		return explorer.getAll(p);
	}

	/** 
	 * Returns the first graph generated by a given graph grammar that satisfies a given property,
	 * or <code>null</code> if there is none. 
	 * @param gg the graph grammar to be evaluated 
	 * @param p the property that the graph to be returned should satisfy
	 * @return a graph from a reachable state in the
	 * expansion of <code>gg</code> that satisfies the property <code>p</code>,
	 * or <code>null</code> if there exists no such graph
	 */
	static public Graph get(GraphGrammar gg, Property<Graph> p) {
		Explorer explorer = new Explorer();
		explorer.setGTS(new GTS(gg));
		return explorer.get(p);
	}

	/** 
	 * Returns all final graphs generated by a given graph grammar.
	 * @param gg the graph grammar to be evaluated 
	 * @return the collection of all graphs from reachable states in the
	 * expansion of <code>gg</code> that have no outgoing transition
	 */
	static public Collection<Graph> getAll(GraphGrammar gg) {
		Explorer explorer = new Explorer();
		explorer.setGTS(new GTS(gg));
		return explorer.getAll();
	}

	/** 
	 * Returns the first final graph generated by a given graph grammar,
	 * or <code>null</code> if there is none. 
	 * @param gg the graph grammar to be evaluated 
	 * @return a graph from a reachable final state in the
	 * expansion of <code>gg</code>,
	 * or <code>null</code> if there exists no such graph
	 */
	static public Graph get(GraphGrammar gg) {
		Explorer explorer = new Explorer();
		explorer.setGTS(new GTS(gg));
		return explorer.get();
	}
}
