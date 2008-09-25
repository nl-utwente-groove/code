/* GROOVE: GRaphs for Object Oriented VErification
   Copyright 2003--2007 University of Twente
 
   Licensed under the Apache License, Version 2.0 (the "License"); 
   you may not use this file except in compliance with the License. 
   You may obtain a copy of the License at 
   http://www.apache.org/licenses/LICENSE-2.0 
 
   Unless required by applicable law or agreed to in writing, 
   software distributed under the License is distributed on an 
   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
   either express or implied. See the License for the specific 
   language governing permissions and limitations under the License.
   */
/*
 * $Id: DepthFirstStrategy1.java,v 1.3 2008-02-20 09:43:19 iovka Exp $
 */

package groove.explore.strategy;

import groove.explore.util.ExploreCache;
import groove.explore.util.RandomNewStateChooser;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.trans.RuleEvent;

import java.util.Iterator;

/** This depth-first search algorithm systematically generates all outgoing 
 * transitions of any visited state.
 * 
 * At each step, the exploration continues with a random successor fresh state,
 * or backtracks if there are no unexplored successor states.
 * 
 * Even though this depth first search backtracks for finding the next state
 * to explore, it is not considered as a backtracking strategy (in the sense
 * of {@link AbstractBacktrackingStrategy}. This is because all explored
 * states are closed, thus the strategy does not need to cache any
 * information, neither to know from where it backtracked. 
 * 
 * @author Iovka Boneva
 *
 */
public class ExploreStateDFStrategy extends AbstractStrategy {
	/**
	 * The next step makes atomic the full exploration of a state.
	 */
	public boolean next() {
		if (getAtState() == null) {
			getGTS().removeGraphListener(this.collector);
			return false; 
		}
		ExploreCache cache = getCache(false, false);
		Iterator<RuleEvent> matchIter = getMatchesIterator(cache);
		this.collector.reset();
		while (matchIter.hasNext()) {
			getGenerator().addTransition(getAtState(), matchIter.next(), cache);
		}
		setClosed(getAtState());
		updateAtState();
		return true;
	}
	
	@Override
	protected void updateAtState() {
		if (this.collector.pickRandomNewState() != null) {
			this.atState = this.collector.pickRandomNewState();
			return;
		}
		// backtracking
		GraphState s = atState;
		do {
			s = parentOf(s);
			this.atState = s == null ? null : getRandomOpenSuccessor(s);
		} while (s != null && this.atState == null); 
	}
	
	@Override
	public void prepare(GTS gts, GraphState state) {
		super.prepare(gts, state);
		gts.addGraphListener(this.collector);
	}
	
	private RandomNewStateChooser collector = new RandomNewStateChooser();
}
