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
 * $Id: EdgeBoundedStrategy.java,v 1.4 2007-09-25 22:57:55 rensink Exp $
 */
package groove.lts.explore;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import groove.graph.Edge;
import groove.graph.Label;
import groove.lts.GraphState;

/**
 * This class implements an exploration strategy in which the number of nodes
 * is guaranteed not to exceed a given bound. That makes the state space finite.
 * @author Arend Rensink
 * @version $Revision: 1.4 $
 */
public class EdgeBoundedStrategy extends BranchingStrategy {
    /** Name of this strategy. */
    static public final String STRATEGY_NAME = "Edge-bounded";
    /** One-line description of this strategy. */
    static public final String STRATEGY_DESCRIPTION =
        "Only explores states where the edge counts do not exceed preset bounds";
    /** Value for the bound parameter of the strategy that means no bound is set. */
    static public final int NO_BOUND = Integer.MAX_VALUE;

    /** Constructs a strategy with no bound set. */
    public EdgeBoundedStrategy() {
        // explicit empty constructor
    }

    /** 
     * Constructs a strategy for a given edge bounds map. 
     * @see #setBounds(Map)
     */
    public EdgeBoundedStrategy(Map<Label, Integer> boundsMap) {
        this.boundsMap.putAll(boundsMap);
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
     * Sets the bound for a single edge label.
     */
    public void setBound(Label boundedLabel, int bound) {
        boundsMap.put(boundedLabel, new Integer(bound));
    }

    /**
     * Sets the bounds for a number of labels.
     * @param boundsMap the bounds to be added; should be a map from <tt>Label</tt> to <tt>Integer</tt>.
     */
    public void setBounds(Map<Label, Integer> boundsMap) {
        boundsMap.putAll(boundsMap);
    }

    /**
     * Removes the bound for a given label.
     */
    public void removeBound(Label label) {
        boundsMap.remove(label);
    }

    /**
     * Returns the current bound of this strategy.
     */
    public int getBound(Label label) {
        Integer bound = boundsMap.get(label);
        if (bound == null) {
            return NO_BOUND;
        } else {
            return bound;
        }
    }

    @Override
    public String toString() {
        String result = getName();
        if (boundsMap != null) {
            result += " by " + boundsMap;
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
        boolean result = true;
        Iterator<Map.Entry<Label,Integer>> boundsIter = boundsMap.entrySet().iterator();
        while (result && boundsIter.hasNext()) {
            Map.Entry<Label,Integer> boundsEntry = boundsIter.next();
            Set<? extends Edge> labelSet = state.getGraph().labelEdgeSet(2,boundsEntry.getKey());
            if (labelSet != null) {
                result = labelSet.size() <= boundsEntry.getValue();
            }
        }
        return result;
    }

    /**
     * The (current) bound of this strategy.
     */
    private final Map<Label,Integer> boundsMap = new HashMap<Label,Integer>();
}