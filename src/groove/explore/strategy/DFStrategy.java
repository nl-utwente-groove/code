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

import java.util.Stack;

import groove.explore.util.ExploreCache;
import groove.explore.util.MatchesIterator;
import groove.graph.Edge;
import groove.graph.GraphShape;
import groove.graph.Node;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.LTS;
import groove.lts.LTSListener;
import groove.lts.State;

/** Makes a depth first exploration by
 * closing each visited states. Maintains a stack for the order in which
 * states are to be explored (thus is less memory efficient). Is suitable
 * for conditional strategies.
 * 
 * This strategy is not considered as a backtracking strategy, as states are
 * fully explored and there is no need of maintaining caches.
 * 
 * @author Iovka Boneva
 *
 */
public class DFStrategy extends AbstractStrategy {
	public boolean next() {
		if (getAtState() == null) {
			getGTS().removeGraphListener(toExplore);
			return false; 
		}
		ExploreCache cache = getCache(false, false);
		MatchesIterator matchIter = getMatchesIterator(cache);
	
		while (matchIter.hasNext()) {
			getGenerator().addTransition(getAtState(), matchIter.next(), cache);
		}
		setClosed(getAtState());
		updateAtState();
		return true;
	}

	
	@Override
	protected void updateAtState() {
		if (this.toExplore.isEmpty()) {
			this.atState = null;
		} else {
			this.atState = toExplore.pop();
		}
	}
	
	@Override
	public void prepare(GTS gts, GraphState startState) {
		super.prepare(gts, startState);
		gts.addGraphListener(toExplore);
	}	
	
	/** Stack giving the order in which states are to be explored. */
	protected StackToExplore toExplore = new StackToExplore();

	
	protected class StackToExplore extends Stack<GraphState> implements LTSListener {
		public void addUpdate(GraphShape graph, Node node) {
			push((GraphState) node);
		}

		public void closeUpdate(LTS graph, State explored) { /* empty */ }
		
		public void addUpdate(GraphShape graph, Edge edge) { /* empty */ }

		public void removeUpdate(GraphShape graph, Node node) { /* empty */ }
		
		public void removeUpdate(GraphShape graph, Edge elem) { /* empty */ }
	}
	
}
