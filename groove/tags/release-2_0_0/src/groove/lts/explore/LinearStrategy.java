/**
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
 * $Id: LinearStrategy.java,v 1.6 2007-09-17 09:51:37 rensink Exp $
 */
package groove.lts.explore;

import groove.lts.GraphNextState;
import groove.lts.GraphState;
import groove.lts.State;

import java.util.Collection;
import java.util.Iterator;

/**
 * LTS exploration strategy based on the principle of depth search, but without 
 * backtracking.
 * The search is continued until the maximum depth is reached, or a 
 * <i>maximal</i> state is found; that is, a state that only loops back to already explored states.
 * The maximum depth of the search can be set; a depth of 0 means unbounded depth.
 * @author Arend Rensink
 * @version $Revision: 1.6 $
 */
public class LinearStrategy extends AbstractStrategy {
    /** Constructs an instance of the strategy that reuses exploration results. */
    public LinearStrategy() {
        this(true);
    }
    
    /** 
     * Constructs an instance of the strategy with a parameter that controls
     * whether exploration results should be reused.
     */
    public LinearStrategy(boolean reuse) {
        this.reuse = reuse;
    }
    
    /** The result of this strategy is the set of all states traversed during exploration. */
    public Collection<? extends State> explore() throws InterruptedException {
        boolean intermediateState = true;
        GraphState atState = getAtState();
        Collection<GraphState> result = createStateSet();
        while (intermediateState) {
            if (Thread.currentThread().isInterrupted()) {
                throw new InterruptedException();
            }
            Iterator<? extends GraphState> nextStateIter = getSuccessorIter(atState);
            GraphState nextOpenState = null;
            while (nextOpenState == null && nextStateIter.hasNext()) {
            	GraphState nextState = nextStateIter.next();
            	if (getGTS().outEdgeSet(nextState).isEmpty() && !nextState.isClosed()) {
            		nextOpenState = nextState;
            	}
            }
            if (atState instanceof GraphNextState) {
                ((GraphNextState) atState).source().setClosed();
            }
            intermediateState = nextOpenState != null;
            if (intermediateState) {
                atState = nextOpenState;
                result.add(atState);
            }
        }
        return result;
    }

    public String getName() {
        return STRATEGY_NAME;
    }

    public String getShortDescription() {
        return STRATEGY_DESCRIPTION;
    }

    /**
     * Returns the result of the reuse property, set at construction time. 
     */
	@Override
	boolean isReuse() {
		return reuse;
	} 
	
	/** Flag indicating if this strategy should reuse previous results. */
	private final boolean reuse;
	/** Name of this exploration strategy. */
    static public final String STRATEGY_NAME = "Linear";
    /** Short description of this exploration strategy. */
    static public final String STRATEGY_DESCRIPTION =
        "Generates a single outgoing transition and continues with the target state";
}