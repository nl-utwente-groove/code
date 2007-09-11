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
 * $Id: BarbedStrategy.java,v 1.3 2007-09-11 11:23:05 fladder Exp $
 */
package groove.lts.explore;

import groove.lts.GraphState;
import groove.lts.LTS;
import groove.lts.State;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * LTS exploration strategy based on the principle of depth search.
 * The search is continued until the maximum depth is reached, or a 
 * closed state is found.
 * The maximum depth of the search can be set; a depth of 0 means unbounded depth.
 * @author Arend Rensink
 * @version $Revision: 1.3 $
 */
public class BarbedStrategy extends AbstractStrategy {
	/** Name of the barbed exploration strategy. */
    static public final String STRATEGY_NAME = "Barbed";
	/** Brief explanation of the barbed exploration strategy. */
    static public final String STRATEGY_DESCRIPTION =
        "Closes the state, picks an arbitrary outgoing transition and continues with the target state if open";

    /** 
     * The result nodes for this method are the final nodes of the LTS.
     * @see LTS#getFinalStates()
     */
    public Collection<? extends State> explore() throws InterruptedException {
        List<GraphState> nextStates = new ArrayList<GraphState>(getSuccessors(getAtState()));
        while (!nextStates.isEmpty()) {
            if (Thread.currentThread().isInterrupted()) {
                throw new InterruptedException();
            }
            // choose randomly among the next states
            int nextIndex = (int) (Math.random() * nextStates.size());
            GraphState state = nextStates.get(nextIndex);
            
            // set nextStates to collect the successors of state
            nextStates.clear();
            getCollector().set(nextStates);
            explore(state);
            getCollector().reset();
            
            if( nextStates.size() == 0 )
            	nextStates = new ArrayList<GraphState>(getSuccessors(state));
        }
        return getGTS().getFinalStates();
    }

    public String getName() {
        return STRATEGY_NAME;
    }

    public String getShortDescription() {
        return STRATEGY_DESCRIPTION;
    }
}