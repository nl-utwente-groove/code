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

import groove.explore.result.ExploreCondition;

/** To be extended by strategies that explore only portions
 * of a GTS, defined by some exploration condition.
 * @author Iovka Boneva
 */
// IOVKA There is a possibility of making conditions orthogonal to strategies, so
// that all strategies can be used with conditions. 
// However, this makes implementation of strategies much less simple,
// as they should remember which states should not be explored.
// Therefore, I prefer for now leave the strategies simpler, and
// if necessary all strategies may be made conditional (with possibly empty condition)
// In order to do this, the ConditionalStrategy signature is to be
// merged with the AbstractStrategy signature, and implementation of 
// (at least) next() and updateAtState() methods would change.
public interface ConditionalStrategy extends Strategy {

	/** The exploration condition that, when not satisfied by a 
	 * state, forbids exploring this state.
	 * @param condition
	 */
	public void setExploreCondition (ExploreCondition<?> condition);

}
