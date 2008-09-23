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

import groove.explore.result.Acceptor;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.StateGenerator;

/** A strategy defines an order in which the states of a graph transition system
 * are to be explored. It can also determine which states are to be explored 
 * either according to some condition (see {@link ConditionalStrategy}),
 * or because of the nature of the strategy (see for instance {@link LinearStrategy}).
 * Most often, a strategy starts its exploration at some state, fixed by 
 * the {@link #setState(GraphState)} method.
 * 
 * A strategy adds states and transitions to a graph transition system. However, 
 * it should use a {@link StateGenerator} and not manipulate the graph transition
 * system directly.
 * @author 
 *
 */
public interface Strategy {
	/** 
	 * Executes one step of the strategy. 
	 * @return false if the strategy is completed, <code>true</code> otherwise.
	 * @require The previous call of this method, if any, 
	 * returned <code>true</code>. Otherwise, the behaviour is not guaranteed.
	 */
	public boolean next();
	
	/** Sets the state where the strategy starts exploring.
	 * @param state
	 */
	public void setState(GraphState state);
	/** Sets the graph transition system to be explored.
	 * @param gts
	 */
	public void setGTS(GTS gts);

	/**
	 * Adds an acceptor to the strategy.
	 */
	public void addGTSListener(Acceptor listener);
}
