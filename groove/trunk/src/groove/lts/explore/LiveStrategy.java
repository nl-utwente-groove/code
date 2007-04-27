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
 * $Id: LiveStrategy.java,v 1.3 2007-04-27 22:06:58 rensink Exp $
 */
package groove.lts.explore;

import groove.lts.GraphState;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Breadth-first exploration strategy that stops at the first final state (i.e., state without outgoing transitions.
 * @author Arend Rensink
 * @version $Revision: 1.3 $
 */
public class LiveStrategy extends BranchingStrategy {
	/** Short name of this strategy. */
    static public final String NAME = "Live";
	/** Short description of this strategy. */
    static public final String DESCRIPTION = "Stops exploring if a final state is encountered";

    @Override
    public String getShortDescription() {
        return DESCRIPTION;
    }

    @Override
    public String getName() {
        return NAME;
    }

    /**
     * This implementation stops exploring altogether as soon as the condition is violated
     * (as indicated by <tt>isExplorable</tt>).
     */
    @Override
    protected Collection<GraphState> computeNextStates(Collection<GraphState> atStates) throws InterruptedException {
        Collection<GraphState> result = new LinkedList<GraphState>();
        getCollector().set(result);
        boolean halt = false;
        Iterator<GraphState> openStateIter = atStates.iterator();
        while (!halt && openStateIter.hasNext()) {
            if (Thread.currentThread().isInterrupted()) {
                throw new InterruptedException();
            }
            GraphState openState = openStateIter.next();
            if (isExplorable(openState)) {
                explore(openState);
            } else {
                halt = true;
                result.clear();
            }
        }
        getCollector().reset();
        return result;
    }
    
    /**
     * Overwrites the method so states are explorable as long as no final states
     * exis in the lts.
     */
    @Override
    protected boolean isExplorable(GraphState state) {
        return ! getGTS().getFinalStates().isEmpty();
    }
}