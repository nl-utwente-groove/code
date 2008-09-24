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
import groove.explore.util.MatchesIterator;
import groove.graph.Edge;
import groove.graph.GraphShape;
import groove.graph.GraphShapeListener;
import groove.graph.Node;
import groove.lts.GTS;
import groove.lts.GraphState;

import java.util.LinkedList;

/** A breadth-first exploration that uses its own queue of open states.
 * Guarantees a breadth-first exploration, but consumes lots of memory.
 */
public class BreadthFirstStrategy extends AbstractStrategy {
	
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
	public void updateAtState() {
		this.atState = this.toExplore.poll();
	}
	
	@Override
	public void prepare(GTS gts, GraphState startState) {
		super.prepare(gts, startState);
		gts.addGraphListener(toExplore);
	}	
	
	/** The states to be explored, in a FIFO order. */
	protected ToExploreQueue toExplore = new ToExploreQueue();
	/** Iterator over the matches of the current state. */
	
	/** A queue with states to be explored, used as a FIFO. */
	protected class ToExploreQueue extends LinkedList<GraphState> implements GraphShapeListener {
		public void addUpdate(GraphShape graph, Node node) {
			offer((GraphState) node);
		}

		public void addUpdate(GraphShape graph, Edge edge) { /* empty */ }

		public void removeUpdate(GraphShape graph, Node node) { /* empty */ }

		public void removeUpdate(GraphShape graph, Edge elem) { /* empty */ }
		
	}
	
	
}
