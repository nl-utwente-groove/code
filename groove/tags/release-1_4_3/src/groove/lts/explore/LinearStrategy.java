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
 * $Id: LinearStrategy.java,v 1.1.1.2 2007-03-20 10:42:52 kastenberg Exp $
 */
package groove.lts.explore;

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
 * @version $Revision: 1.1.1.2 $
 */
public class LinearStrategy extends AbstractStrategy {
	/** Name of this exploration strategy. */
    static public final String STRATEGY_NAME = "Linear";
	/** Short description of this exploration strategy. */
    static public final String STRATEGY_DESCRIPTION =
        "Generates a single outgoing transition and continues with the target state";

    /** The result of this strategy is the set of all states traversed during exploration. */
    public Collection<? extends State> explore() throws InterruptedException {
        boolean intermediateState = true;
        GraphState atState = getAtState();
        Collection<State> result = createStateSet();
        while (intermediateState) {
            if (Thread.currentThread().isInterrupted()) {
                throw new InterruptedException();
            }
            Iterator<? extends GraphState> nextStateIter = getGenerator().getSuccessorIter(atState);
            GraphState nextOpenState = null;
            while (nextOpenState == null && nextStateIter.hasNext()) {
            	GraphState nextState = nextStateIter.next();
            	if (getLTS().outEdgeSet(nextState).isEmpty() && !nextState.isClosed()) {
            		nextOpenState = nextState;
            	}
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
}