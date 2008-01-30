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
 * $Id: AbstrGraphStateImpl.java,v 1.2 2008-01-30 09:33:47 iovka Exp $
 */
package groove.abs.lts;

import groove.abs.AbstrGraph;
import groove.control.Location;
import groove.graph.Element;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.trans.RuleEvent;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Collections;
/**
 * FIXME this class has to be adapted to the new architecture
 * @author Iovka Boneva
 * @version $Revision $
 */
public class AbstrGraphStateImpl implements AbstrGraphState {

	/**
	 * @ensure All resulting objects are of type {@link AbstrGraphTransition}.
	 */
	public Iterator<GraphTransition> getTransitionIter() {
		return new Iterator<GraphTransition>() {
			Iterator<AbstrGraphTransition> it = AbstrGraphStateImpl.this.transitions.iterator();
			public boolean hasNext() { 	return this.it.hasNext(); }
			public GraphTransition next() { return this.it.next(); }
			public void remove() { throw new UnsupportedOperationException(); }			
		};
	}

	public boolean isWithoutOutTransition() {
		return this.transitions.isEmpty();
	}
	
	/**
	 * @ensure All resulting objects are of type {@link AbstrGraphTransition}.
	 */
	public Set<GraphTransition> getTransitionSet() {
		return Collections.unmodifiableSet(new HashSet<GraphTransition>(this.transitions));
	}

	/**
	 * @require transition is of type AbstrGraphTransition
	 */
	public boolean addTransition(GraphTransition transition) {
		// TODO check whether the transition is already there. This requires to compare transitions, but it should only be compared by end state identities
		AbstrGraphTransition atr = (AbstrGraphTransition) transition;
		return this.transitions.add(atr);
	}

	final public AbstrGraph getGraph() { return this.graph; }

	public boolean setClosed() {
		boolean result = ! this.closed;
		this.closed = true;
		return result;
	}

	public boolean isClosed() { return this.closed; }

	/** This implementation compares state numbers if obj is of class AbstrGraphStateImpl, or throws UnsupportedOperationException otherwise. */
	public int compareTo(Element obj) {
		if (obj instanceof AbstrGraphStateImpl) {
			return getStateNumber() - ((AbstrGraphStateImpl) obj).getStateNumber();
		} 
		throw new UnsupportedOperationException(String.format("Classes %s and %s cannot be compared", getClass(), obj.getClass()));
	}

	/** Always null for the moment. */
	public Location getControl() { return null; }
	
    /**
     * Sets the state number.
     * This method should be called only once, with a non-negative number.
     * @throws IllegalStateException if {@link #hasStateNumber()} returns <code>true</code>
     * @throws IllegalArgumentException if <code>nr</code> is illegal (i.e., negative)
     */
    void setStateNumber(int n) {
        if (hasStateNumber()) {
        	throw new IllegalStateException(String.format("State number already set to %s", this.nr)); 
        }
        if (n < 0) {
        	throw new IllegalArgumentException(String.format("Illegal state number %s", this.nr));
        }
    	this.nr = n;
    }
    
    /** */
    protected int getStateNumber() {
    	if (!hasStateNumber()) {
        	throw new IllegalStateException("State number not set"); 
        }
        return this.nr;
    }
    
    private final boolean hasStateNumber() {
    	return this.nr >= 0;
    }
    
	// ------------------------------------------------------------
	// FIELDS, CONSTRUCTORS, STANDARD METHODS
	// ------------------------------------------------------------

    /** Constructs an state with specified underlying graph and with empty 
     * set of transitions. */
	public AbstrGraphStateImpl (AbstrGraph graph) {
		// IOVKA remove this when the class is correct
		if (true) throw new UnsupportedOperationException();
		this.graph = graph;
		this.transitions = new HashSet<AbstrGraphTransition>();
		this.closed = false;
		this.nr = -1;
	}
	
	private AbstrGraph graph;
	private boolean closed; 
	Set<AbstrGraphTransition> transitions;
	private int nr;
	
	@Override
	public String toString () {
		return "s" + (hasStateNumber() ? this.nr : "??");
	}
	
	@Override
	/** This implementation returns true if the underlying abstract graphs have 
	 * isomorphic structure with compatible types and multiplicities.
	 * TODO to be adapted if I want to group together graphs with compatible multiplicities
	 */
	public boolean equals (Object o) {
		if (! (o instanceof AbstrGraphStateImpl)) { return false; }
		AbstrGraphStateImpl other = (AbstrGraphStateImpl) o;
		if (this.hasStateNumber() && other.hasStateNumber() && this.getStateNumber() == other.getStateNumber()) { return true; }
		return this.getGraph().equals(((AbstrGraphState) o).getGraph());
	}
	
	@Override
	public int hashCode () { return getGraph().hashCode(); }
	
	// ------------------------------------------------------------
	// UNIMPLEMENTED METHODS
	// ------------------------------------------------------------
	
	/**
	 * For abstract graph transformations, a rule event defines several next states. 
	 * This method is not implemented.
	 * @see #getNextStates(RuleEvent)
	 */
	public GraphState getNextState(RuleEvent prime) { 
		throw new UnsupportedOperationException();
	}
	/**
	 * @ensure All resulting objects are of type {@link AbstrGraphState}.
	 */
	public Iterator<GraphState> getNextStateIter() {
		throw new UnsupportedOperationException();
		// TODO if needed by the state generator
	}
	
	/**
	 * @ensure All resulting objects are of type {@link AbstrGraphState}.
	 */
	public Collection<GraphState> getNextStateSet() {
		throw new UnsupportedOperationException();
		// TODO if needed by the state generator
	}

	public boolean containsTransition(GraphTransition transition) {
		throw new UnsupportedOperationException();
		// TODO if needed by the state generator or the AGTS
	}

	/**
     * Retrieves the outgoing transitions with a given event, if such exist.
     * Yields <code>null</code> otherwise.
     * Pointer equality is considered for identifying the event.
     * May throw TODO (concurrent modification)
     */
	public Iterator<AbstrGraphState> getNextStates (RuleEvent event) {
		throw new UnsupportedOperationException();
		// TODO if needed by the state generator
	}
	
	public Location getLocation() {
		return this.location;
	}
	
    /** The internally stored (optional) control location. */
    private Location location;

	@Override
	public void setLocation(Location l) {
		// TODO Auto-generated method stub
		
	}
	
}
