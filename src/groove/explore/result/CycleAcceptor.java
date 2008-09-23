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
 * $Id: CycleAcceptor.java,v 1.4 2008-03-04 14:48:17 kastenberg Exp $
 */

package groove.explore.result;

import groove.explore.strategy.ModelCheckingStrategy;
import groove.lts.LTS;
import groove.lts.ProductGTS;
import groove.lts.ProductTransition;
import groove.lts.State;
import groove.verify.BuchiGraphState;
import groove.verify.ModelChecking;

/**
 * Acceptor that is notified on closing a Büchi graph-state
 * in a {@link groove.lts.ProductGTS}. If the Büchi graph-state
 * is accepting, a a cycle detection depth-first search is
 * started. If a counter-example is found, the graph-states
 * currently on the search-stack constitute the path representing
 * the counter-example.
 * 
 * @author Harmen Kastenberg
 * @version $Revision: 1.4 $
 */
public class CycleAcceptor extends Acceptor {
	/** Creates a new acceptor with a 1-bounded {@link Result}. */
	public CycleAcceptor(ModelCheckingStrategy strategy) {
		this(new Result(1), strategy);
	}
	
	/** Creates a new acceptor with a given {@link Result}. */
	private CycleAcceptor(Result result, ModelCheckingStrategy strategy) {
		super(result);
		this.strategy = strategy;
		this.strategy.setResult(result);
	}
	
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
				}
				// else leave result empty and continue
			}
		}
	}

	private int redDFS(ProductGTS gts, BuchiGraphState state) {
		assert (gts instanceof ProductGTS) : "Expected a product GTS instead of " + gts.getClass();
		for (ProductTransition nextTransition: ((ProductGTS) gts).outEdgeSet(state)) {
			// although the outgoing transition in the gts might cross the boundary
			// we do not have to check for this since the target states themselves
			// will not yet have outgoing transitions and will therefore never
			// yield an accepting cycle
			// moreover, those states are not yet explored and will therefore not
			// yet be coloured. The below code will thus not yield any interesting
			// results for such states
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
	
	/** 
	 * This implementation returns a fresh {@link CycleAcceptor},
	 * aliasing the strategy of this instance.
	 */
	@Override
	public Acceptor newInstance() {
		CycleAcceptor result = new CycleAcceptor(getResult().newInstance(), strategy);
		return result;
	}

	private ModelCheckingStrategy strategy;
}
