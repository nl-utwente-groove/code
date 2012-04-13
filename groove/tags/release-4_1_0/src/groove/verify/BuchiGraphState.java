/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id: BuchiGraphState.java,v 1.3 2008/03/20 13:28:38 kastenberg Exp $
 */
package groove.verify;

import groove.control.CtrlState;
import groove.lts.AbstractGraphState;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.GraphTransitionStub;
import groove.lts.ProductTransition;
import groove.lts.StateReference;
import groove.trans.HostGraph;
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
 * @version $Revision$
 */
public class BuchiGraphState extends AbstractGraphState {

    /**
     * the Buchi graph-state this one originates from (useful when visualizing
     * counter-examples)
     */
    // private BuchiGraphState parent;
    /** the graph-state that is wrapped */
    private final GraphState state;
    /** the buchi location for this buchi graph state */
    private BuchiLocation buchiLocation;
    /** the colour of this graph state (used in the nested DFS algorithm) */
    private int colour;
    /**
     * this flag indicates whether this state can be regarded as a so-called
     * pocket state
     */
    private boolean pocket = false;
    /**
     * the iteration in which this state has been found; this field will only be
     * used for state that are left unexplored in a specific iteration
     */
    private int iteration;
    private Set<ProductTransition> outTransitions;
    /** flag indicating whether this state is closed */
    public boolean closed = false;
    /** flag indicating whether this state is explored */
    public boolean explored = false;

    /**
     * Constructor.
     * @param record the {@link SystemRecord} for this state
     * @param state the system-state component
     * @param buchiLocation the Buchi-location component
     * @param parent the parent state
     */
    public BuchiGraphState(SystemRecord record, GraphState state,
            BuchiLocation buchiLocation, BuchiGraphState parent) {
        super(StateReference.newInstance(record));
        this.state = state;
        this.buchiLocation = buchiLocation;
        this.colour = ModelChecking.NO_COLOUR;
        // this.iteration = ModelChecking.CURRENT_ITERATION;
        // this.parent = parent;
    }

    /**
     * Returns the graph-state component of the Buchi graph-state.
     * @return the graph-state component of the Buchi graph-state
     */
    public GraphState getGraphState() {
        return this.state;
    }

    @Override
    public HostGraph getGraph() {
        return this.state.getGraph();
    }

    @Override
    public CtrlState getCtrlState() {
        return this.state.getCtrlState();
    }

    /**
     * @return the <tt>buchiLocation</tt> of this {@link BuchiGraphState}
     */
    public BuchiLocation getBuchiLocation() {
        return this.buchiLocation;
    }

    /**
     * Sets the buchi location field of this buchi graph-state.
     * @param location the new location
     */
    public void setBuchiLocation(BuchiLocation location) {
        this.buchiLocation = location;
    }

    /**
     * Returns whether this state is accepting.
     * @return <tt>true</tt> if its location is accepting, <tt>false</tt>
     *         otherwise
     */
    public boolean isAccepting() {
        return this.buchiLocation.isAccepting();
    }

    /**
     * Returns the run-time colour of this Buchi graph-state.
     * @return the run-time colour of this Buchi graph-state
     */
    public int colour() {
        return this.colour;
    }

    /**
     * Sets the run-time colour of this Buchi graph-state.
     * @param value the new colour
     */
    public void setColour(int value) {
        this.colour = value;
    }

    /**
     * Returns whether this state is a pocket state.
     * @return the value of <code>pocket</code>
     */
    public boolean isPocket() {
        return this.pocket;
    }

    /**
     * Mark this state as a pocket state.
     */
    public void setPocket() {
        assert (!this.pocket) : "state should not be set to pocket twice";
        this.pocket = true;
        // pocketStates++;
    }

    /**
     * Returns the iteration in which this state has been reached.
     * @return the iteration in which this state has been reached.
     */
    public int iteration() {
        return this.iteration;
    }

    /**
     * Sets the iteration of this state.
     * @param value the value for this state's iteration
     */
    public void setIteration(int value) {
        this.iteration = value;
    }

    /**
     * Add an outgoing {@link ProductTransition} to this Buchi graph-state.
     * @param transition the outgoing transition to be added
     */
    public boolean addTransition(ProductTransition transition) {
        if (this.outTransitions == null) {
            initOutTransitions();
        }
        return this.outTransitions.add(transition);
    }

    /**
     * Returns the set of outgoing transitions.
     * @return the set of outgoing transitions
     */
    public Set<ProductTransition> outTransitions() {
        if (this.outTransitions == null) {
            initOutTransitions();
        }
        return this.outTransitions;
    }

    private void initOutTransitions() {
        this.outTransitions = new HashSet<ProductTransition>();
    }

    @Override
    public GraphTransitionStub getOutStub(RuleEvent prime) {
        return this.state.getOutStub(prime);
    }

    @Override
    public Iterator<GraphTransition> getTransitionIter() {
        return this.state.getTransitionIter();
    }

    @Override
    public Set<GraphTransition> getTransitionSet() {
        return this.state.getTransitionSet();
    }

    @Override
    public Collection<? extends GraphState> getNextStateSet() {
        return new TransformSet<ProductTransition,BuchiGraphState>(
            outTransitions()) {
            @Override
            public BuchiGraphState toOuter(ProductTransition trans) {
                return trans.target();
            }
        };
    }

    @Override
    public Iterator<? extends GraphState> getNextStateIter() {
        return new TransformIterator<ProductTransition,BuchiGraphState>(
            outTransitions().iterator()) {
            @Override
            public BuchiGraphState toOuter(ProductTransition trans) {
                return trans.target();
            }
        };
    }

    /**
     * Returns an iterator over the outgoing transitions.
     * @return an iterator over the outgoing transitions.
     */
    public Iterator<ProductTransition> outTransitionIter() {
        return outTransitions().iterator();
    }

    @Override
    public boolean addTransition(GraphTransition transition) {
        throw new UnsupportedOperationException(
            "Buchi graph-states can only have outgoing Buchi-transitions and no "
                + transition.getClass());
    }

    @Override
    public boolean containsTransition(GraphTransition transition) {
        return this.state.containsTransition(transition);
    }

    @Override
    public boolean isClosed() {
        return this.closed;
    }

    @Override
    protected void updateClosed() {
        this.closed = true;
    }

    /**
     * Checks whether this states is already fully explored.
     * @return <tt>true<tt> if so, <tt>false</tt> otherwise
     */
    public boolean isExplored() {
        return this.explored;
    }

    /**
     * Set this state as being fully explored.
     */
    public void setExplored() {
        this.explored = true;
    }

    @Override
    public String toString() {
        if (this.state != null && this.buchiLocation != null) {
            return this.state.toString() + "-" + this.buchiLocation.toString();
        } else {
            return "??";
        }
    }

}