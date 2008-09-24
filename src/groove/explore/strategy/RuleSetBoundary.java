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
 * $Id: RuleSetBoundary.java,v 1.6 2008/03/21 12:36:04 kastenberg Exp $
 */
package groove.explore.strategy;

import groove.lts.ProductTransition;
import groove.trans.Rule;

import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of interface {@link Boundary} that
 * bases the boundary on a set of rules for which application
 * are said to cross the boundary.
 *
 * @author Harmen Kastenberg
 * @version $Revision$
 */
public abstract class RuleSetBoundary extends AbstractBoundary {

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

	public boolean crossingBoundary(ProductTransition transition, boolean traverse) {

		// if the underlying transition is null, this transition
		// represents a final transition and does therefore
		// not cross any boundary
		if (transition.graphTransition() == null) {
			return false;
		} else {
			Rule rule = transition.rule();
			if (!containsRule(rule)) {
				return false;
			} else {
				return true;
			}
		}
	}

	public void increase() {
		// do nothing
	}

	public boolean containsRule(Rule rule) {
		return ruleSetBoundary.contains(rule);
	}

	/** the set of rules that are initially forbidden to apply */
	private Set<Rule> ruleSetBoundary = new HashSet<Rule>();
}
