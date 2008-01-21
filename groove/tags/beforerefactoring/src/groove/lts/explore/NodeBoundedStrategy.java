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
 * $Id: NodeBoundedStrategy.java,v 1.3 2007-04-27 22:06:58 rensink Exp $
 */
package groove.lts.explore;

import groove.lts.GraphState;

/**
 * This class implements an exploration strategy in which the number of nodes
 * is guaranteed not to exceed a given bound. That makes the state space finite.
 * @author Arend Rensink
 * @version $Revision: 1.3 $
 */
public class NodeBoundedStrategy extends BranchingStrategy {
    /** Name of this strategy. */
    static public final String STRATEGY_NAME = "Node-bounded";
    /** One-line description of this strategy. */
    static public final String STRATEGY_DESCRIPTION =
        "Only explores states where the node count does not exceed a given bound";
    /** Value for the bound parameter of the strategy that means no bound is set. */
    static public final int NO_BOUND = Integer.MAX_VALUE;

    /** Constructs a strategy with no bound set. */
    public NodeBoundedStrategy() {
        this(NO_BOUND);
    }

    /** 
     * Constructs a strategy with a given node bound.
     * @param bound whenever the number of nodes in a graph exceeds this, exploration stops
     * @see #setBound(int)
     */
    public NodeBoundedStrategy(int bound) {
        this.bound = bound;
    }
    
    @Override
    public String getShortDescription() {
        return STRATEGY_DESCRIPTION;
    }

    @Override
    public String getName() {
        return STRATEGY_NAME;
    }

    /**
     * Sets the bound of this strategy to a given value.
     * @param bound whenever the number of nodes in a graph exceeds this, exploration stops
     */
    public void setBound(int bound) {
        this.bound = bound;
    }

    /**
     * Returns the current bound of this strategy.
     */    
    public int getBound() {
        return bound;
    }

    @Override
    public String toString() {
        String result = getName();
        if (bound != NO_BOUND) {
            result += " to size " + bound;
        }
        if (getGTS() != null && getAtState() != getGTS().startState()) {
            result += " (starting at " + getAtState() + ")";
        }
        return result;
    }
    
    /**
     * The state is explorable if its node count does not exceed <tt>{@link #getBound}</tt>.
     */
    @Override
    protected boolean isExplorable(GraphState state) {
        return state.getGraph().nodeCount() <= bound;
    }

    /**
     * The (current) bound of this strategy.
     */
    private int bound;
//    
//    private static final int IS_EXPLORABLE = reporter.newMethod("isExplorable(State)"); 
}