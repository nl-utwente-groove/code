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
 * $Id: AGTS.java,v 1.5 2008-03-18 13:51:38 iovka Exp $
 */
package groove.abs.lts;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import groove.abs.AbstrGraph;
import groove.abs.Abstraction;
import groove.abs.DefaultAbstrGraph;
import groove.abs.ExceptionIncompatibleWithMaxIncidence;
import groove.abs.MyHashSet;
import groove.abs.PatternFamily;
import groove.abs.Util;
import groove.control.Location;
import groove.graph.DefaultGraph;
import groove.graph.DefaultNode;
import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.Graph;
import groove.graph.GraphShapeCache;
import groove.io.AspectualViewGps;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.State;
import groove.lts.Transition;
import groove.trans.GraphGrammar;
import groove.trans.RuleEvent;
import groove.trans.SystemRecord;
import groove.util.CollectionView;
import groove.view.FormatException;

/** An Abstract graph transition system.
 * @author Iovka Boneva
 * @version $Revision $
 * @invariant (TypeInv) States of the system are always of type {@link AbstrGraphState}
 * @invariant (TypeInv) Transitions of the system are always of type {@link AbstrGraphTransition}
 */
public class AGTS extends GTS {

	@Override
	// IOVKA this is almost a copy of the super method, because of the different implementation of the AbstrGraphState
	/** @require newState is of type AbstrGraphState */
	public GraphState addState(GraphState newState) {
		assert newState instanceof AbstrGraphState : "Type error : " + newState + " is not of type AbstrGraphState.";
        reporter.start(ADD_STATE);
       	((AbstrGraphStateImpl) newState).setStateNumber(nodeCount());
        GraphState result = this.stateSet.getAndAdd((AbstrGraphState) newState);
        if (result == null) {
            fireAddNode(newState);
        }
        reporter.stop();
        return result;
	}
	
	@Override
	/** @require transition is of type AbstrGraphTransition */
	public void addTransition(GraphTransition transition) {
		assert (transition instanceof AbstrGraphTransition) || (transition instanceof AbstrGraphNextState): 
			"Type error : " + transition + " is not of type AbstrGraphTransition neigther AbstrGraphNextState.";
		super.addTransition(transition);
		if (transition.target().equals(INVALID_STATE)) {
			invalidTransitionsCount++;
		}
	}

	@Override
	// IOVKA to remove after debug
	/** @require state is an instance of AbstrGraphState */
	public void setClosed(State state) {
		assert state instanceof AbstrGraphState : "Type error : " + state + " is not of type AbstrGraphState.";
		super.setClosed(state);
	}

	@Override
	/** Specialises return type. */
	public AbstrGraphState startState() {
		return (AbstrGraphState) super.startState();
	}
	
	/**
	 * The set of transitions with same source and same event.
	 * @param trans 
	 * @return The set of transitions with same source and same event
	 */
	public Collection<Transition> getEquivalentTransitions(AbstrGraphTransition trans) {
		Collection<Transition> result = new ArrayList<Transition>();
		Iterator<? extends Edge> transIt = trans.source().getTransitionIter();
		while (transIt.hasNext()) {
			AbstrGraphTransition other = (AbstrGraphTransition) transIt.next();
			if (other.getEvent().equals(trans.getEvent())) {
				result.add(other);
			}
		}
		return result;
	}
	
	
	@Override
	public Collection<GraphState> getOpenStates() {
		return new CollectionView<GraphState>(this.stateSet) {
			@Override
			public boolean approves(Object obj) {
				return !((State) obj).isClosed();
			}
		};
	}
	
	@Override
    public int nodeCount() { return this.stateSet.size(); }
	
	/** Returns the number of invalid transitions. */
	public int invalidTransitionsCount() {
		return this.invalidTransitionsCount;
	}
	
	@Override
    public synchronized Set<? extends GraphState> nodeSet() {
        return Collections.unmodifiableSet(this.stateSet);
    }
	
	@Override
	protected SystemRecord createRecord() {
		SystemRecord record = new SystemRecord(getGrammar(), true);
		return record;
	}
	
	// ---------------------------------------------------------------
	// NON IMPLEMENTED PUBLIC METHODS
	// ---------------------------------------------------------------
	@Override
	public double getBytesPerState() {
		throw new UnsupportedOperationException();
	}
	
	
	// ---------------------------------------------------------------
	// FIELDS, CONSTRUCTORS AND STANDARD METHODS
	// ---------------------------------------------------------------
	
	/**
	 * @param grammar
	 */
	public AGTS(GraphGrammar grammar, Abstraction.Parameters options) {
		super(grammar, true, true);
		this.options = options;
		this.family = new PatternFamily (options.radius, options.maxIncidence);
		((InvalidState) INVALID_STATE).removeStateNumber();
		startState(); // Will add the start state
		((InvalidState) INVALID_STATE).removeStateNumber();
		this.addState(INVALID_STATE);
		setClosed(INVALID_STATE);
		checkInvariants();
	}
	
	/** The pattern family of graphs in the transition system. */
	public PatternFamily getFamily () { return this.family; }
	
	/** The parameters of the abstraction used for obtaining the states of the transition system. */
	public Abstraction.Parameters getParameters() { return this.options; }
	
	
	private PatternFamily family;
	// initialised with an hasher checking for isomorphism of graphs
	private final MyHashSet<AbstrGraphState> stateSet = new MyHashSet<AbstrGraphState>(new IsoCheckHasher());

	private Abstraction.Parameters options;

	/** This is a unique state that represents all states which could not be constructed because the
	 * maximal incidence constraint failed. */
	public final static AbstrGraphState INVALID_STATE = new InvalidState();
	
	/** Used for counting the number of invalid transitions. */
	private int invalidTransitionsCount = 0;
	
	/** Used for a singleton invalid state. */
	static class InvalidState extends AbstrGraphStateImpl  {

		InvalidState () {
			super(DefaultAbstrGraph.INVALID_AG);
			//super.setStateNumber();
		}
		
		void removeStateNumber() {
			super.nr = -1;
		}
		
		@Override
		public boolean addTransition(GraphTransition transition) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean equals (Object o) {
			return this == o;
		}
		
		@Override
		public int hashCode () {
			return 0;
		}
	}
	
	// ---------------------------------------------------------------
	// NON PUBLIC METHODS
	// ---------------------------------------------------------------


	@Override
	/** For now, control is not possible with abstract transformation. */
	protected AbstrGraphState createStartState(Graph startGraph) {
		AbstrGraph ag = null;
		try {
			ag = DefaultAbstrGraph.factory(this.family, this.options.precision).getShapeGraphFor(startGraph);
		} catch (ExceptionIncompatibleWithMaxIncidence e) {
			return INVALID_STATE;
		}
		AbstrGraphState result = new AbstrGraphStateImpl(ag);
		return result;
	}
	
	
	@Override
	protected GraphShapeCache createCache() {
		throw new UnsupportedOperationException();
	}

	@Override
	protected void notifyLTSListenersOfClose(State closed) {
		super.notifyLTSListenersOfClose(closed);
	}
	
	
	class IsoCheckHasher implements MyHashSet.Hasher<AbstrGraphState> {

		public int getHashCode(AbstrGraphState o) {
			return o.getGraph().hashCode();
		}

		public boolean areEqual(AbstrGraphState o1, AbstrGraphState o2) {
			return o1.getGraph().equals(o2.getGraph());
		}
		
	}
	

	// ---------------------------------------------------------------
	// INVARIANTS
	// ---------------------------------------------------------------
	private void checkInvariants() {
		if (! Util.ea()) { return; }
		assert this.isStoreTransitions() : "AGTS should always store transitions";
		checkTypeInv();
	}

	private void checkTypeInv () {
		if (! Util.ea()) { return; }
		for (GraphState s : this.stateSet) {
			assert s instanceof AbstrGraphState : "Type error : " + s + " is not of type AbstrGraphState.";
		}
	}

}
