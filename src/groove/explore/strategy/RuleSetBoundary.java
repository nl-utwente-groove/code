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
 * $Id: RuleSetBoundary.java,v 1.6 2008-03-21 12:36:04 kastenberg Exp $
 */
package groove.explore.strategy;

import groove.lts.GraphTransition;
import groove.trans.Rule;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
 * @version $Revision: 1.6 $
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
		setAllowMap();
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
			if (allowed.get(transition.getEvent().getRule())) {
				if (ALLOW_ALL_APPLICATIONS == ALLOW_SINGLE_APPLICATION) {
					setAllowMap();
				} else {
					allowed.put(transition.getEvent().getRule(), false);
				}
				return true;
			}
		}
		return false;
	}

	private void setAllowMap() {
		allowed = new HashMap<Rule,Boolean>();
		for (Rule rule: ruleSetBoundary) {
			allowed.put(rule, false);
		}
	}

	/* (non-Javadoc)
	 * @see groove.explore.strategy.Boundary#increase()
	 */
	public void increase() {
		allowed = new HashMap<Rule,Boolean>();
		for (Rule rule: ruleSetBoundary) {
			allowed.put(rule, true);
		}
	}

	private Set<Rule> ruleSetBoundary = new HashSet<Rule>();
//	private boolean allowBoundaryRule;
	private Map<Rule,Boolean> allowed;

	/** Allow only a single application of one of the rules */
	public static final int ALLOW_SINGLE_APPLICATION = 1;
	/** Allow one application of all rules */
	public static final int ALLOW_ALL_APPLICATIONS = 2;
	/** Constant indicating when forbidden rule applications are temporarily allowed */
	public static int ALLOW_RULE_APPLICATION = ALLOW_ALL_APPLICATIONS;
}
