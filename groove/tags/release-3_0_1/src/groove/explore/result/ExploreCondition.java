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

/** Defines a condition that may or not hold in a {@link GraphState}.
 * The condition may be negated.
 * Such conditions may be used by strategies in order to explore only states
 * that satisfy the condition.
 * @author Iovka Boneva
 *
 * @param <C> Type of the object defining the condition.
 */
public abstract class ExploreCondition<C> {
	
	/** Determines whether the condition is satisfied by a graph state. 
	 * This method typically uses the condition set using
	 * {@link #setCondition(Object)}.
	 * 
	 * @param state
	 * @return
	 */
	public abstract boolean isSatisfiedBy (GraphState state);
	
	/** The parameter determines whether the condition
	 * is to be checked positively or negatively.
	 * 
	 * @param b
	 */
	public void setNegated(boolean b) {
		this.isNegated = b; 
	}
	
	/** Sets the condition.
	 * @param condition should not be null
	 */
	public void setCondition(C condition) {
		this.condition = condition;
	}
	
	/**
	 * The type of the actual condition.
	 * @return
	 */
	public Class<?> getConditionType() {
		return condition.getClass();
	}
	
	/** Indicates whether the condition is negated. */
	protected boolean isNegated;
	/** The condition. */
	protected C condition;

}
