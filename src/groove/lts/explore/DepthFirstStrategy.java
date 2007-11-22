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
 * $Id: DepthFirstStrategy.java,v 1.3 2007-11-22 15:47:13 fladder Exp $
 */
package groove.lts.explore;

import groove.lts.GraphState;
import groove.lts.LTS;
import groove.lts.State;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * LTS exploration strategy based on the principle of breadth first search.
 * The depth of the search can be set; a depth of 0 means unbounded depth.
 * @author Arend Rensink
 * @version $Revision: 1.3 $
 */

public class DepthFirstStrategy extends AbstractStrategy {
	/** Name of this exploration strategy. */
    static public final String STRATEGY_NAME = "DepthFirst";
	/** Short description of this exploration strategy. */
    static public final String STRATEGY_DESCRIPTION = "Recursively calls itself on all reachable, non-explored states";

    /** 
     * The result states for this method are the final states of the LTS.
     * @see LTS#getFinalStates()
     */
    public Collection<? extends State> explore() throws InterruptedException {
        explore(new HashSet<State>(), getAtState());
        return getGTS().getFinalStates();
    }

    /** 
     * Recursively explores at a given state, provided it is not yet in 
     * the set of explored states. 
     */
    protected void explore(Set<State> exploredStates, State atState) {
        if (!exploredStates.contains(atState)) {
            exploredStates.add(atState);
            Iterator<? extends State> nextStateIter = getSuccessorIter((GraphState) atState);
            while (nextStateIter.hasNext()) {
                State nextState = nextStateIter.next();
                if (isExplorable(nextState)) {
                    explore(exploredStates, nextState);
                }
            }
            exploredStates.remove(atState);
        }
    }

    public String getName() {
        return STRATEGY_NAME;
    }

    public String getShortDescription() {
        return STRATEGY_DESCRIPTION;
    }

    /**
     * Method that determines whether a given state should be explored further.
     * To be overwritten by subclasses; this implementation 
     * tests if the state is closed.
     */
    protected boolean isExplorable(State state) {
        return !state.isClosed();
    }
}