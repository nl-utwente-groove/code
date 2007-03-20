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
 * $Id: BranchingStrategy.java,v 1.1.1.1 2007-03-20 10:05:25 kastenberg Exp $
 */
package groove.lts.explore;

import groove.graph.AbstractGraph;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.LTS;
import groove.lts.State;
import groove.util.Reporter;

import java.util.ArrayList;
import java.util.Collection;

/**
 * LTS exploration strategy based on the principle of breadth first search.
 * The depth of the search can be set; a depth of 0 means unbounded depth.
 * @author Arend Rensink
 * @version $Revision: 1.1.1.1 $
 */
public class BranchingStrategy extends AbstractStrategy {
	/** Name of this exploration strategy. */
    static public final String STRATEGY_NAME = "Branching";
	/** Short desscription of this exploration strategy. */
    static public final String STRATEGY_DESCRIPTION =
        "At each pass, closes all open states; then continues with the newly generated open states";

    /** Constant to regulate the increase in space allocated for storing open states. */
    static private final double GROWTH_FACTOR = 1.5;

    /** 
     * The result nodes for this method are the final nodes of the LTS.
     * @see LTS#getFinalStates()
     */
    public Collection<? extends State> explore() throws InterruptedException {
        explore(getGenerator().getSuccessors(getAtState()));
        return getLTS().getFinalStates();
    }

    public String getName() {
        return STRATEGY_NAME;
    }

    public String getShortDescription() {
        return STRATEGY_DESCRIPTION;
    }

    /**
     * Returns the number of states that are found to be unexplorable.
     */
    public int getIgnoredCount() {
        return ignoredCount;
    }

    /**
     * Explores the currently set LTS, starting from a given set of states.
     * Calls {@link #computeNextStates} at each iteration.
     * @param atStates the set of states at which to apply this strategy
     * @require <tt>getLTS().containsNodes(atStates)</tt>
     * @see #computeNextStates
     */
    protected void explore(Collection<? extends State> atStates) throws InterruptedException {
        while (!atStates.isEmpty()) {
            atStates = computeNextStates(atStates);
        }
    }

    /**
     * Computes the set of open, explorable states reachable from a given set of states. 
     * Calls {@link #isExplorable} to test whether a given state should be added
     * to the set of explorable states.
     * @param atStates the set of states to be investigated
     * @return the set of open explorable states reachable from <tt>atStates</tt>
     * @require <tt>getLTS().containsNodeSet(atStates)</tt>
     */
    protected Collection<State> computeNextStates(Collection<? extends State> atStates) throws InterruptedException {
    	reporter.start(EXTEND);
        Collection<State> result = createOpenStateSet((int) (atStates.size()*GROWTH_FACTOR));
        for (State atState: atStates) {
            if (Thread.currentThread().isInterrupted()) {
                throw new InterruptedException();
            }
            // because states may occur twice in the result set, we have 
            // to test if they are closed.
            if (!atState.isClosed()) {
				if (isExplorable(atState)) {
					Collection<? extends State> next = getGenerator().computeSuccessors((GraphState) atState);
					result.addAll(next);
				} else {
					if (atState instanceof AbstractGraph) {
						((AbstractGraph) atState).clearCache();
					}
					ignoredCount++;
				}
			}
        }
        reporter.stop();
        return result;
    }

    /**
     * Callback factory method to create a new set to store open states, 
     * with a given initial capacity.
     * @param capacity the desired initial capacity
     */
    protected Collection<State> createOpenStateSet(int capacity) {
    	return new ArrayList<State>(capacity);
    }
    
    /**
	 * Method that determines whether a given state should be explored further.
	 * To be overwritten by subclasses; this implementation returns
	 * <tt>true</tt> always. Called from {@link #computeNextStates}.
	 */
    protected boolean isExplorable(State state) {
        return true;
    }
    
    /** Number of states ignored, i.e., not explored, during exploration. */
    protected int ignoredCount;
    
    /** Reporter for profiling information; aliased to {@link GTS#reporter}. */
    static public final Reporter reporter = GTS.reporter;
    /** Profiling handle for the exploration phase. */
    static private final int EXTEND = reporter.newMethod("explore");
//    static private final int EXTEND_CLOSE = reporter.newMethod("explore:close");
//    static private final int EXTEND_ADD = reporter.newMethod("explore:add");
}