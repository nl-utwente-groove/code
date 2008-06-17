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
package groove.explore.result;

import groove.lts.GraphState;

/** A graph state acceptor that can be fed with an external object that
 * defines the acceptance condition.
 * The condition is given by an {@link ExploreCondition}. 
 * 
 * @author Iovka Boneva
 * @param <C> The parameter type of the explore condition.
 *
 */
public abstract class ConditionalAcceptor<C> extends Acceptor<GraphState> {
	
	/** Sets the condition to be used by {@link #isSatisfiedBy(Object)}. 
	 * @param condition 
	 */
	public void setCondition(ExploreCondition<C> condition) {
		this.condition = condition;
	}
	
	/** The pre-set condition.
	 * @return
	 */
	protected ExploreCondition<C> getCondition() {
		return this.condition;
	}
	
	/** The condition. */
	protected ExploreCondition<C> condition;
}
