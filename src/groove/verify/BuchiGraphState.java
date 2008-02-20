/*
 * GROOVE: GRaphs for Object Oriented VErification
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
 * $Id: BuchiGraphState.java,v 1.1 2008-02-20 07:50:54 kastenberg Exp $
 */
package groove.verify;

import groove.control.Location;
import groove.graph.Graph;
import groove.lts.AbstractGraphState;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.ProductTransition;
import groove.lts.StateReference;
import groove.trans.RuleEvent;
import groove.trans.SystemRecord;
import groove.util.TransformIterator;
import groove.util.TransformSet;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Composition of a graph-state and a Buchi-location.
 * 
 * @author Harmen Kastenberg
 * @version $Revision: 1.1 $ $Date: 2008-02-20 07:50:54 $
 */
public class BuchiGraphState extends AbstractGraphState {

	/** the buchi graph-state this one originates from */
	private BuchiGraphState parent;
	/** the graph-state that is wrapped */
	private GraphState state;
	/** the buchi location for this buchi graph state */
	private BuchiLocation buchiLocation;
	/** the colour of this graph state (used in the nested DFS algorithm) */
	private int colour;
	private Set<ProductTransition> outTransitions;
	/** flag indicating whether this state is closed */
	public boolean closed = false;

	public BuchiGraphState(SystemRecord record, GraphState state, BuchiLocation buchiLocation, BuchiGraphState parent) {
		super(StateReference.newInstance(record));
		this.state = state;
		this.buchiLocation = buchiLocation;
		this.colour = ModelChecking.WHITE;
		this.parent = parent;
	}

	/**
	 * Returns the Buchi graph-state that is the parent of
	 * this Buchi graph-state in the spanning tree of the exploration.
	 * @return the spanning tree parent
	 */
	public BuchiGraphState parent() {
		return parent;
	}

	/**
	 * Returns the graph-state component of the Buchi graph-state.
	 * @return the graph-state component of the Buchi graph-state
	 */
	public GraphState getGraphState() {
		return state;
	}

	public Graph getGraph() {
		return state.getGraph();
	}

	/** 
	 * Returns the location of this state.
	 * Together with the graph, the location completely determines the state.
	 * For flexibility, the type of the location is undetermined.
	 * @return the location; may be <code>null</code>.
	 */
	public Location getLocation() {
		return state.getLocation();
	}

	/**
	 * @return the <tt>buchiLocation</tt> of this {@link BuchiGraphState}
	 */
	public BuchiLocation getBuchiLocation() {
		return buchiLocation;
	}
	
	/**
	 * Sets the location field of the wrapped graph-state
	 * @param location the new location
	 */
	public void setLocation(Location location) {
		state.setLocation(location);
	}

	/** Sets the buchi location field of this buchi graph-state.
	 * @param location the new location
	 */
	public void setBuchiLocation(BuchiLocation location) {
		this.buchiLocation = location;
	}

	/**
	 * Returns the run-time colour of this Buchi graph-state.
	 * @return the run-time colour of this Buchi graph-state
	 */
	public int colour() {
		return colour;
	}

	/**
	 * Sets the run-time colour of this Buchi graph-state.
	 * @param value the new colour
	 */
	public void setColour(int value) {
		this.colour = value;
	}

	/**
	 * Add an outgoing {@link ProductTransition} to this Buchi graph-state.
	 * @param transition the outgoing transition to be added
	 * @return @see {@link java.util.Set#add(Object)}
	 */
	public boolean addTransition(ProductTransition transition) {
		if (outTransitions == null) {
			initOutTransitions();
		}
		return outTransitions.add(transition);
	}

	/**
	 * Returns the set of outgoing transitions.
	 * @return the set of outgoing transitions
	 */
	public Set<ProductTransition> outTransitions() {
		if (outTransitions == null) {
			initOutTransitions();
		}
		return outTransitions;
	}

	private void initOutTransitions() {
		outTransitions = new HashSet<ProductTransition>();
	}

	public GraphState getNextState(RuleEvent prime) {
    	return state.getNextState(prime);
    }
    
    public Iterator<GraphTransition> getTransitionIter() {
    	return state.getTransitionIter();
    }

    public Set<GraphTransition> getTransitionSet() {
    	return state.getTransitionSet();
    }

    public Collection<? extends GraphState> getNextStateSet() {
        return new TransformSet<ProductTransition,BuchiGraphState>(outTransitions()) {
        	@Override
            public BuchiGraphState toOuter(ProductTransition trans) {
                return trans.target();
            }
        };
    }

    public Iterator<? extends GraphState> getNextStateIter() {
        return new TransformIterator<ProductTransition,BuchiGraphState>(outTransitions().iterator()) {
        	@Override
            public BuchiGraphState toOuter(ProductTransition trans) {
                return trans.target();
            }
        };
    }

    public boolean addTransition(GraphTransition transition) {
    	throw new UnsupportedOperationException("Buchi graph-states can only have outgoing Buchi-transitions and no " + transition.getClass());
//    	return state.addTransition(transition);
    }

    public boolean containsTransition(GraphTransition transition) {
    	return state.containsTransition(transition);
    }

    public boolean isClosed() {
    	return closed;
    }

    @Override
	protected void updateClosed() {
		// TODO Auto-generated method stub
		this.closed = true;
	}

	public String toString() {
		if (state != null && buchiLocation != null) {
			return state.toString() + "-" + buchiLocation.toString();
		} else {
			return "??";
		}
	}
}
