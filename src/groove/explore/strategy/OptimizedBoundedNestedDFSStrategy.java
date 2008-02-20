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
 * $Id: OptimizedBoundedNestedDFSStrategy.java,v 1.1 2008-02-20 10:08:19 kastenberg Exp $
 */

package groove.explore.strategy;

import groove.explore.result.CycleAcceptor;
import groove.explore.util.RandomChooserInSequence;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.verify.BuchiGraphState;
import groove.verify.ModelChecking;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

/** 
 * This depth-first strategy represents the blue search of a nested
 * depth-first search for finding counter-examples for an LTL
 * formula. On backtracking it closes the explored states. Closing
 * a state potentially starts a red search, depending on whether the
 * closed state is accepting or not. This is taken care of by the
 * accompanying {@link CycleAcceptor}.
 * 
 * This bounded version deviates from the usual Nested DFS in the
 * way of setting the next state to be explored. If a potential
 * next state crosses the boundary, an other next state is selected.
 * Checking whether the 
 * 
 * @author Harmen Kastenberg
 * @version $Revision: 1.1 $
 */
public class OptimizedBoundedNestedDFSStrategy extends BoundedNestedDFSStrategy {

	public boolean finished() {
		if (!boundaryGraphs().isEmpty()) {
			ModelChecking.toggle();
			BuchiGraphState next = boundaryGraphs().remove(0);
			constructSearchStack(next);
			this.atBuchiState = next;
			return false;
		} else if (!nextBoundaryGraphs.isEmpty()) {
			ModelChecking.toggle();
			BuchiGraphState next = nextBoundaryGraphs.remove(0);
			constructSearchStack(next);
			this.atBuchiState = next;
			boundaryGraphs().addAll(nextBoundaryGraphs);
			nextBoundaryGraphs.clear();
			getBoundary().increase();
			return false;
		} else {
			return true;
		}
	}

	/* (non-Javadoc)
	 * @see groove.explore.strategy.BoundedNestedDFSStrategy#addBoundaryGraph(groove.verify.BuchiGraphState)
	 */
	public boolean addBoundaryGraph(BuchiGraphState boundaryGraph) {
		return nextBoundaryGraphs.add(boundaryGraph);
	}

	private void constructSearchStack(BuchiGraphState state) {
		searchStack().clear();
//		searchStack().push(state);
		BuchiGraphState parent = state.parent();
		while (parent != null) {
			parent.setColour(ModelChecking.cyan());
			searchStack().add(0, parent);
			parent = parent.parent();
		}
	}

	private List<BuchiGraphState> nextBoundaryGraphs = new ArrayList<BuchiGraphState>();
}
