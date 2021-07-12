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
 * $Id$
 */
package groove.verify;

import groove.lts.GraphState;
import groove.lts.GraphTransition;

import java.util.ArrayList;
import java.util.List;

/**
 * Composition of a graph-state and a Buchi location.
 * 
 * @author Harmen Kastenberg
 * @version $Revision$
 */
public class ProductState {
    /**
     * Constructs a state with an empty origin transition.
     * @param state the system-state component
     * @param buchiLocation the Buchi-location component
     */
    public ProductState(GraphState state, BuchiLocation buchiLocation) {
        this.state = state;
        this.buchiLocation = buchiLocation;
        this.origin = null;
        this.colour = ModelChecking.NO_COLOUR;
    }

    /**
     * Constructs a product state based on a given graph transition and target Buchi location.
     * @param origin the (non-{@code null}) graph transition along which this product state was
     * discovered
     * @param buchiLocation the Buchi-location component
     */
    public ProductState(GraphTransition origin, BuchiLocation buchiLocation) {
        this.state = origin.target();
        this.buchiLocation = buchiLocation;
        this.origin = origin;
        this.colour = ModelChecking.NO_COLOUR;
    }

    /**
     * Returns the graph-state component of the product state.
     */
    public GraphState getGraphState() {
        return this.state;
    }

    /**
     * @return the <tt>buchiLocation</tt> of this {@link ProductState}
     */
    public BuchiLocation getBuchiLocation() {
        return this.buchiLocation;
    }

    /** Returns the incoming graph transition along which this 
     * product state was discovered.
     * May be {@code null} if this is the initial or a final product state.
     */
    public GraphTransition getOrigin() {
        return this.origin;
    }

    /**
     * Returns the run-time colour of this product state.
     */
    public int colour() {
        return this.colour;
    }

    /**
     * Sets the run-time colour of this product state.
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
     * Add an outgoing {@link ProductTransition} to this product state.
     * @param transition the outgoing transition to be added
     */
    public void addTransition(ProductTransition transition) {
        this.outTransitions.add(transition);
    }

    /**
     * Returns the set of outgoing transitions.
     */
    public List<ProductTransition> outTransitions() {
        return this.outTransitions;
    }

    /** Tests if this product state is closed. */
    public boolean isClosed() {
        return this.closed;
    }

    /** 
     * Sets this product state to closed.
     * @return {@code true} if the state was not closed already
     */
    public boolean setClosed() {
        boolean result = !this.closed;
        if (result) {
            this.closed = true;
        }
        return result;
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
    public int hashCode() {
        return System.identityHashCode(this.state) + System.identityHashCode(this.buchiLocation);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ProductState)) {
            return false;
        }
        ProductState other = (ProductState) obj;
        return this.state == other.state && this.buchiLocation == other.buchiLocation;
    }

    @Override
    public String toString() {
        if (this.state != null && this.buchiLocation != null) {
            return this.state.toString() + "-" + this.buchiLocation.toString();
        } else {
            return "??";
        }
    }

    /** the graph-state that is wrapped */
    private final GraphState state;
    /** the buchi location for this buchi graph state */
    private final BuchiLocation buchiLocation;
    /** The incoming graph transition along which this product state was found. */
    private final GraphTransition origin;
    private final List<ProductTransition> outTransitions = new ArrayList<>();
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
    /** flag indicating whether this state is closed */
    private boolean closed = false;
    /** flag indicating whether this state is explored */
    private boolean explored = false;

}
