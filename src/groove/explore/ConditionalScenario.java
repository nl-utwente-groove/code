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

import groove.explore.result.Acceptor;
import groove.explore.result.ConditionalAcceptor;
import groove.explore.result.ExploreCondition;
import groove.explore.strategy.Strategy;
import groove.lts.GTS;
import groove.lts.GraphState;

/**
 * Scenario that also keeps track of a condition; states that do not satisfy the
 * condition are not considered for exploration.
 * @author Arend Rensink
 * @version $Revision $
 */
public class ConditionalScenario<C> extends DefaultScenario {
	/**
	 * Constructs a conditional handler with a given description and name,
	 * and a given condition type.
	 */
	public ConditionalScenario(Strategy strategy, Acceptor acceptor, String name, String description, Class<?> type) {
		super(strategy, acceptor, name, description);
		this.type = type;
	}
	
	@Override
	public String getName() {
		if (this.condition == null) {
			return super.getName();
		}
		return super.getName() + 
				(condition.isNegated() ? " !" : " ") +
				"<" +
				this.condName +
				">";
	}
	
    @Override
	@SuppressWarnings("unchecked")
    public void prepare(GTS gts, GraphState state) {
        super.prepare(gts, state);
        if (getAcceptor() instanceof ConditionalAcceptor) {
            ((ConditionalAcceptor<C>) getAcceptor()).setCondition(getCondition());
        }
    }           

    /** 
     * Sets the condition.
     * The condition should be set before a call of {@link #prepare(GTS,GraphState)}.
     * @param name A short name for the condition, to be used for instance
     * the name of the scenario.
     */
	public void setCondition(ExploreCondition<C> condition, String name) {
		this.condition = condition;
		this.condName = name;
	}

	/** Returns the currently set exploration condition. */
	protected ExploreCondition<C> getCondition() {
		return condition;
	}
	
    /** The type of the condition. */
	public Class<?> getConditionType() { return type; }

	private ExploreCondition<C> condition;
	private String condName = "";
	private final Class<?> type;
}