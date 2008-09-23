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
package groove.explore;

import groove.explore.result.Result;
import groove.lts.GTS;
import groove.lts.GraphState;

/** 
 * A scenario for exploring a (part of) a graph transition system yielding a result.
 * A scenario is a combination of a {@link groove.explore.strategy.Strategy},
 * a {@link groove.explore.result.Acceptor} and a {@link groove.explore.result.Result}.
 * Playing a scenario consists in repeating the {@link groove.explore.strategy.Strategy#next()}
 * method as long as it returns <code>true</code> and the result is 
 * not {@link groove.explore.result.Result#done()}.
 * A scenario works on a {@link groove.lts.GTS} and starts exploration in a pre-defined state. 
 *  
 * @author Iovka Boneva
 * @author Tom Staijen
 */
public interface Scenario {
	/** Sets the  {@link groove.lts.GTS} on which this scenario works. 
	 * @param gts the  {@link groove.lts.GTS} on which this scenario works. 
	 */
	public void setGTS(GTS gts);
	/** Sets the start state for this scenario. 
	 * @param state the start state for this scenario. 
	 */
	public void setState(GraphState state);
	
	/** Plays the scenario, yielding a result.
	 * @return the result of the scenario.
	 */
	public Result play() throws InterruptedException ;
	
	/**
	 * Returns the result of this scenario.
	 * The result is retrieved from the acceptor; it is an
	 * error to call this method if no acceptor is set.
	 */
	public Result getResult();
}
