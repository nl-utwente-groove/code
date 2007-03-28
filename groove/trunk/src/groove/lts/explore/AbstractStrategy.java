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
 * $Id: AbstractStrategy.java,v 1.2 2007-03-28 15:12:36 rensink Exp $
 */
package groove.lts.explore;

import java.util.ArrayList;
import java.util.Collection;

import groove.lts.ExploreStrategy;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.LTS;
import groove.lts.State;
import groove.lts.StateGenerator;

/**
 * Abstract LTS exploration strategy.
 * @author Arend Rensink
 * @version $Revision: 1.2 $
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
     * Sets the underlying lts to which this exploration strategy should be applied.
     * The current state is set to <tt>null</tt>.
     * @param lts the new underlying lts to be explored
     * @ensure <tt>getLTS() == lts</tt> and <tt>getAtState() == null</tt>
     */
    public void setLTS(LTS lts) {
        this.lts = (GTS) lts;
        // invalidate the generator
        this.generator = null;
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
     * Factory method for a state generator for a given GTS.
     * @param gts the GTS to create the generator for
     */
    protected StateGenerator createGenerator(GTS gts) {
//      return new DefaultDeriver(ruleSystem.getRules());
        return new StateGenerator(gts);
    }
    
    /**
     * Returns the state generator for this strategy
     * Lazily creates the generator first (using {@link #createGenerator(GTS)}).
     * @return a generator for the current GTS; never <code>null</code>
     */
    protected StateGenerator getGenerator() {
        if (generator == null) {
        	generator = createGenerator(getLTS());
        }
        return generator;
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