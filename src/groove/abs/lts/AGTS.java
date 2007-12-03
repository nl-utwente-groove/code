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
 * $Id: AGTS.java,v 1.2 2007-12-03 09:42:01 iovka Exp $
 */
package groove.abs.lts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import groove.abs.AbstrGraph;
import groove.abs.DefaultAbstrGraph;
import groove.abs.ExceptionIncompatibleWithMaxIncidence;
import groove.abs.MyHashSet;
import groove.abs.PatternFamily;
import groove.abs.Util;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.GraphShapeCache;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.State;
import groove.lts.Transition;
import groove.trans.GraphGrammar;
import groove.trans.SystemRecord;
import groove.util.CollectionView;

/** An Abstract graph transition system. 
 * @author Iovka Boneva
 * @version $Revision $
 * @invariant (TypeInv) States of the system are always of type {@link AbstrGraphState}
 * @invariant (TypeInv) Transitions of the system are always of type {@link AbstrGraphTransition}
 * FIXME implement equals method for AbstrGraphStateImpl
 */
public class AGTS extends GTS {

	@Override
	// IOVKA this is almost a copy of the super method, because of the different implementation of the AbstrGraphState
	/** @require newState is of type AbstrGraphState */
	public GraphState addState(GraphState newState) {
		assert newState instanceof AbstrGraphState : "Type error : " + newState + " is not of type AbstrGraphState.";
        reporter.start(ADD_STATE);
        // see if isomorphic graph is already in the LTS
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
		assert transition instanceof AbstrGraphTransition : "Type error : " + transition + " is not of type AbstrGraphTransition.";
		super.addTransition(transition);
	}

	@Override
	// IOVKA to remove after debug
	/** @require state is an instance of AbstrGraphState */
	public void setClosed(State state) {
		assert state instanceof AbstrGraphState : "Type error : " + state + " is not of type AbstrGraphState.";
		super.setClosed(state);
	}

	@Override
	// IOVKA to remove after debug
	public void setFinal(State state) {
		assert state instanceof AbstrGraphState : "Type error : " + state + " is not of type AbstrGraphState.";
		super.setFinal(state);
	}

	@Override
	/** Specialises return type. */
	public AbstrGraphState startState() {
		return (AbstrGraphState) this.getStartState();
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
	
	@Override
    public Set<? extends GraphState> nodeSet() {
        return Collections.unmodifiableSet(this.stateSet);
    }
	
	@Override
	protected SystemRecord createRecord() {
		return new SystemRecord(getGrammar(), true);
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
	public AGTS(GraphGrammar grammar, int precision, int radius, int maxIncidence) {
		super(grammar, true, true);
		this.family = new PatternFamily (radius, maxIncidence);
		this.precision = precision;
		
		this.setStartState(this.computeStartState(grammar.getStartGraph()));
		this.stateSet.getAndAdd((AbstrGraphState) this.getStartState());
		
		checkInvariants();
	}
	
	PatternFamily getFamily () { return this.family; }
	
	private PatternFamily family;
	private int precision;
	// initialised with the default hasher
	private final MyHashSet<AbstrGraphState> stateSet = new MyHashSet<AbstrGraphState>(null);

	// ---------------------------------------------------------------
	// NON PUBLIC METHODS
	// ---------------------------------------------------------------
	
	@Override
	protected AbstrGraphState computeStartState(Graph startGraph) {
		AbstrGraph ag = null;
		try {
			ag = DefaultAbstrGraph.factory(this.family, this.precision).getShapeGraphFor(startGraph);
		} catch (ExceptionIncompatibleWithMaxIncidence e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AbstrGraphStateImpl result = new AbstrGraphStateImpl(ag);
		result.setStateNumber(0);
		return result;
	}

	@Override
	protected GraphShapeCache createCache() {
		// TODO I have no idea what it does
		return super.createCache();
	}

	@Override
	protected GraphState createStartState(Graph startGraph) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected void notifyLTSListenersOfClose(State closed) {
		// TODO Auto-generated method stub
		super.notifyLTSListenersOfClose(closed);
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
