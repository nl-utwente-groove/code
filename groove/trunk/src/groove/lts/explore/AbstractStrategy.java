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
/*
 * $Id: AbstractStrategy.java,v 1.6 2007-04-24 10:06:44 rensink Exp $
 */
package groove.lts.explore;

import groove.lts.ExploreStrategy;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.State;
import groove.lts.StateGenerator;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Abstract LTS exploration strategy.
 * @author Arend Rensink
 * @version $Revision: 1.6 $
 */
public abstract class AbstractStrategy implements ExploreStrategy {
    /** Value for the depth parameter of the strategy that means no depth is set. */
    static public final int NO_TO_DEPTH = 0;
    
    /**
     * Returns the state set by the previous call of {@link #setAtState(State)},
     * or the start state of the LTS if no state was set.
     * @see #setAtState(State)
     */
    public GraphState getAtState() {
    	if (atState == null) {
    		atState = getLTS().startState();
    	}
        return atState;
    }

    /**
     * Sets the state at which the next exploration should take place.
     * @param atState the state at which the next exploration should take place
     * @ensure <tt>getAtState() == atState</tt>
     * @see #getAtState()
     */
    public void setAtState(State atState) {
        this.atState = (GraphState) atState;
    }

    /**
     * Returns the exploration depth set by <tt>setToDepth(int)</tt>.
     * @return the exploration depth set by <tt>setToDepth(int)</tt>
     * @see #setToDepth(int)
     */
    @Deprecated
    public int getToDepth() {
        return toDepth;
    }

    /**
     * Sets the maximum depth to which the next exploration should continue.
     * The depth is counted as the number of transitions from the state at which exploration
     * starts.
     * A value of 0 means no maximum depth.
     * @param toDepth the maximum depth to which the next exploration should continue
     * @require <tt>toDepth >= 0</tt>
     * @see #getToDepth()
     * @see #explore()
     */
    @Deprecated
    public void setToDepth(int toDepth) {
        this.toDepth = toDepth;
    }
    
    /**
     * Sets the GTS to which this exploration strategy should be applied.
     * The current state is set to the GTS's start state.
     * The state generator is not initialised.
     * @param gts the new underlying GTS to be explored
     */
    public final void setLTS(GTS gts) {
        setLTS(gts, null);
    }
    
    /**
     * Sets the state generator for this strategy.
     * The GTS is also initialised, to the generator's GTS.
     * @param generator the new state generator to be used for exploration
     */
    public final void setGenerator(StateGenerator generator) {
    	setLTS(generator.getGTS(), generator);
    }
        
    /**
     * Sets the generator and GTS at the same time.
     * @param gts the new underlying GTS to be explored
     * @param generator the new state generator to be used for exploration;
     * if not <code>null</code>, it should satisfy <code>gts == generator.getGTS()</code>
     * @see #setLTS(GTS)
     * @see #setGenerator(StateGenerator)
     */
    protected void setLTS(GTS gts, StateGenerator generator) {
        this.lts = gts;
        this.generator = generator;
        // invalidate the generator
        setAtState(lts.startState());
    }

    /**
     * Returns the underlying lts that is being explored.
     * @return the underlying lts being explored
     */
    public GTS getLTS() {
        return lts;
    }
    
    @Override
    public String toString() {
        String result = getName();
        if (toDepth != NO_TO_DEPTH) {
            result += "  to depth " + toDepth;
        }
        if (lts != null && atState != null && atState != lts.startState()) {
            result += " (starting at " + atState + ")";
        }
        return result;
    }

    /**
	 * Lazily creates and returns the state generator for this strategy.
	 * @return a generator for the current GTS; never <code>null</code>
	 */
	protected StateGenerator getGenerator() {
	    if (generator == null) {
	    	generator = new StateGenerator(getLTS());
	    }
	    return generator;
	}

	/**
     * Callback method to create the (initially empty) collection of open states 
     * for a given LTS, with an initial capacity.
     */
    protected Collection<State> createStateSet(int initialSize) {
        return new ArrayList<State>(initialSize);
    }
    
    /**
     * Callback method to create the (initially empty) collection of open states 
     * for a given LTS.
     */
    protected Collection<State> createStateSet() {
        return new ArrayList<State>();
    }
    
    /**
     * The current graph deriver.
     */
    private StateGenerator generator;
    /**
     * The currently set LTS.
     */
    private GTS lts;
    /**
     * The currently set exploration depth.
     * @invariant <tt>toDepth >= 0</tt>
     */
    private int toDepth = 0;
    /**
     * The currently set start state for the exploration.
     * @invariant <tt>lts.containsNode(atState)</tt>
     */
    private GraphState atState;
}