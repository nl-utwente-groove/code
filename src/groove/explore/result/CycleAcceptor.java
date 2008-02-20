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
 * $Id: CycleAcceptor.java,v 1.1 2008-02-20 09:53:26 kastenberg Exp $
 */

package groove.explore.result;

import groove.explore.strategy.ModelCheckingStrategy;
import groove.lts.GraphState;
import groove.lts.LTS;
import groove.lts.ProductGTS;
import groove.lts.ProductTransition;
import groove.lts.State;
import groove.verify.BuchiGraphState;
import groove.verify.ModelChecking;

import java.util.Stack;

/**
 * Acceptor that is notified on closing a buchi graph-state
 * in a {@link groove.lts.ProductGTS}. If the buchi graph-state
 * is accepting, a a cycle detection depth-first search is
 * started. If a counter-example is found, the graph-states
 * currently on the search-stack constitute the path representing
 * the counter-example.
 * 
 * @author Harmen Kastenberg
 */
public class CycleAcceptor<T> extends Acceptor<GraphState> {

	@Override
	public void closeUpdate(LTS gts, State state) {
		if (state instanceof BuchiGraphState) {
			if (((BuchiGraphState) state).getBuchiLocation().isSuccess(null)) {
				assert (gts instanceof ProductGTS) : "Expected a GTS instead of an LTS.";
				int event = redDFS((ProductGTS) gts, (BuchiGraphState) state);
				if (event != ModelChecking.OK) {
					// put the counter-example in the result
					for (BuchiGraphState stackState: strategy.searchStack()) {
						getResult().add(stackState.getGraphState());
					}
					getResult().add(((BuchiGraphState) state).getGraphState());
//					System.err.println("Counter-example found.");
				}
				// else leave result empty and continue
			}
		}
	}

	private int redDFS(ProductGTS gts, BuchiGraphState state) {
		assert (gts instanceof ProductGTS) : "Expected a product GTS instead of " + gts.getClass();
		for (ProductTransition nextTransition: ((ProductGTS) gts).outEdgeSet(state)) {
			BuchiGraphState target = (BuchiGraphState) nextTransition.target();
			if (target.colour() == ModelChecking.cyan()) {
				return ModelChecking.COUNTER_EXAMPLE;
			} else if (target.colour() == ModelChecking.blue()) {
				target.setColour(ModelChecking.red());
				int event = redDFS(gts, target);
				if (event != ModelChecking.OK) {
					return event;
				}
			}
		}
		return ModelChecking.OK;
	}

	public void setStrategy(ModelCheckingStrategy<T> strategy) {
		this.strategy = strategy;
	}

	private ModelCheckingStrategy<T> strategy;
}
