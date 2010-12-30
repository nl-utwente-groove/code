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
import groove.lts.GraphState;
import groove.lts.ProductGTS;
import groove.lts.ProductTransition;
import groove.trans.HostGraph;
import groove.util.Pair;

import java.util.Set;

/**
 * Composition of a graph-state and a Buchi-location.
 * 
 * @author Harmen Kastenberg
 * @version $Revision$
 */
public class NewBuchiGraphState extends Pair<GraphState,BuchiLocation> {
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
     * @param gts the transition system to which this state shall belong
     * @param state the system-state component
     * @param buchiLocation the Buchi-location component
     * @param parent the parent state
     */
    public NewBuchiGraphState(ProductGTS gts, GraphState state,
            BuchiLocation buchiLocation, NewBuchiGraphState parent) {
        super(state, buchiLocation);
        this.colour = ModelChecking.NO_COLOUR;
        // this.iteration = ModelChecking.CURRENT_ITERATION;
        // this.parent = parent;
    }

    /**
     * Returns the graph-state component of the Buchi graph-state.
     * @return the graph-state component of the Buchi graph-state
     */
    public GraphState getGraphState() {
        return one();
    }

    public HostGraph getGraph() {
        return one().getGraph();
    }

    public CtrlState getCtrlState() {
        return one().getCtrlState();
    }

    /**
     * @return the <tt>buchiLocation</tt> of this {@link NewBuchiGraphState}
     */
    public BuchiLocation getBuchiLocation() {
        return two();
    }

    /**
     * Sets the buchi location field of this buchi graph-state.
     * @param location the new location
     */
    public void setBuchiLocation(BuchiLocation location) {
        setTwo(location);
    }

    /**
     * Returns whether this state is accepting.
     * @return <tt>true</tt> if its location is accepting, <tt>false</tt>
     *         otherwise
     */
    public boolean isAccepting() {
        return two().isAccepting();
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

    public boolean isClosed() {
        return this.closed;
    }

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
        if (one() != null && two() != null) {
            return one().toString() + "-" + two().toString();
        } else {
            return "??";
        }
    }

}
