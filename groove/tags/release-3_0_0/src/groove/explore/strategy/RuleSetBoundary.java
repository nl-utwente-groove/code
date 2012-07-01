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
 * $Id: RuleSetBoundary.java,v 1.5 2008-03-03 14:47:59 kastenberg Exp $
 */
package groove.explore.strategy;

import groove.lts.GraphTransition;
import groove.trans.Rule;

import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of interface {@link Boundary} that
 * bases the boundary on a set of rules for which application
 * are said to cross the boundary. When increasing the boundary
 * this class allows the exploration of a single rule that
 * would otherwise not be allowed. Thereafter, the same boundary
 * is applied again.
 * 
 * @author Harmen Kastenberg
 * @version $Revision: 1.5 $
 */
public class RuleSetBoundary implements Boundary {

	/**
	 * Empty {@link RuleSetBoundary} constructor.
	 */
	public RuleSetBoundary() {
		// empty constructor
	}

	/**
	 * {@link RuleSetBoundary} constructor.
	 * @param ruleSetBoundary the set of rules that constitute the boundary
	 */
	public RuleSetBoundary(Set<Rule> ruleSetBoundary) {
		this.ruleSetBoundary.addAll(ruleSetBoundary);
	}

	/**
	 * Add a rule to the set of boundary rules.
	 * @param rule the rule to be added
	 * @return see {@link java.util.Set#add(Object)}
	 */
	public boolean addRule(Rule rule) {
		return ruleSetBoundary.add(rule);
	}

	/* (non-Javadoc)
	 * @see groove.explore.strategy.Boundary#crossingBoundary(groove.lts.GraphTransition)
	 */
	public boolean crossingBoundary(GraphTransition transition) {
		boolean crossingBoundary = ruleSetBoundary.contains(transition.getEvent().getRule()); 
		if (crossingBoundary) {
			if (allowBoundaryRule) {
				allowBoundaryRule = false;
				return false;
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see groove.explore.strategy.Boundary#increase()
	 */
	public void increase() {
		allowBoundaryRule = true;
	}

	private Set<Rule> ruleSetBoundary = new HashSet<Rule>();
	private boolean allowBoundaryRule;
}