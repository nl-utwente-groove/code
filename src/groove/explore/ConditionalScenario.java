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

import groove.explore.result.ConditionalAcceptor;
import groove.explore.result.ExploreCondition;
import groove.explore.strategy.ConditionalStrategy;
import groove.lts.GTS;
import groove.lts.GraphState;

/** A scenario handler that additionally allows to set a condition.
 * Typical usage would be with a scenario with a {@link ConditionalAcceptor}, and
 * with {@link ConditionalStrategy}.
 * 
 * @author Iovka Boneva
 * @param <C> Type of the condition.
 */
public interface ConditionalScenario<C> extends Scenario {
	/** 
	 * Sets the condition.
	 * The condition should be set before a call of {@link #prepare(GTS,GraphState)}.
	 * @param condition
	 * @param name A short name for the condition, to be used for instance
	 * the name of the scenario.
	 */
	public void setCondition(ExploreCondition<C> condition, String name);
	
	/** The type of the condition. */
	public Class<?> getConditionType ();
}
