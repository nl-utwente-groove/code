/* GROOVE: GRaphs for Object Oriented VErification
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
 * $Id$
 */
package groove.explore.strategy;

import groove.explore.util.ExploreCache;
import groove.explore.util.MatchSetCollector;
import groove.graph.GraphShape;
import groove.graph.Node;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.LTSAdapter;
import groove.trans.RuleEvent;
import groove.util.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/** A breadth-first exploration that uses its own queue of open states.
 * Guarantees a breadth-first exploration, but consumes lots of memory.
 */
public class BFStrategy extends AbstractStrategy {
	public boolean next() {
		if (getAtState() == null) {
			getGTS().removeGraphListener(exploreListener);
			return false;
		}
		ExploreCache cache = getCache(false, false);
		Collection<RuleEvent> matchSet = new MatchSetCollector(getAtState(), cache, getRecord(), parentTransitions).getMatchMap();
		Iterator<RuleEvent> matchIter = matchSet.iterator();
		Collection<GraphTransition> outTransitions = new ArrayList<GraphTransition>(matchSet.size());
		while (matchIter.hasNext()) {
			outTransitions.addAll(getGenerator().applyMatch(getAtState(), matchIter.next(), cache));
		}
		for (GraphState newState: newStates) {
			stateQueue.offer(new Pair<GraphState,Collection<GraphTransition>>(newState, outTransitions));	
		}
		setClosed(getAtState());
		newStates.clear();
		updateAtState();
		return true;
	}

	@Override
	public void updateAtState() {
		Pair<GraphState,Collection<GraphTransition>> next = stateQueue.poll();
		this.atState = next == null ? null : next.first();
		this.parentTransitions = next == null ? null : next.second();
	}
	
	@Override
	public void prepare(GTS gts, GraphState startState) {
		super.prepare(gts, startState);
		getGTS().addGraphListener(exploreListener);
	}	
	
	/** 
	 * Queue of states to be explored.
	 * The set of outgoing transitions of the parent state is included with each state.
	 */
	private final Queue<Pair<GraphState,Collection<GraphTransition>>> stateQueue = new LinkedList<Pair<GraphState,Collection<GraphTransition>>>();
	/** Internal store of newly generated states. */
	private Collection<GraphState> newStates = new ArrayList<GraphState>();
	/** Parent transitions of the currently explored state. */
	private Collection<GraphTransition> parentTransitions;
	/** Listener to keep track of states added to the GTS. */
	private ExploreListener exploreListener = new ExploreListener();
	
	/** A queue with states to be explored, used as a FIFO. */
	protected class ExploreListener extends LTSAdapter {
		@Override
		public void addUpdate(GraphShape graph, Node node) {
			newStates.add((GraphState) node);
		}
	}
}
