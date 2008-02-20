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
 * $Id: RuleSetBoundary.java,v 1.2 2008-02-20 08:36:44 kastenberg Exp $
 */
package groove.explore.strategy;

import groove.lts.GraphTransition;
import groove.trans.Rule;

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
 * @version $Revision: 1.2 $ $Date: 2008-02-20 08:36:44 $
 */
public class RuleSetBoundary implements Boundary {

	/**
	 * {@link RuleSetBoundary} constructor.
	 * @param ruleSetBoundary the set of rules that constitute the boundary
	 */
	public RuleSetBoundary(Set<Rule> ruleSetBoundary) {
	}

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

	public void increase() {
		allowBoundaryRule = true;
	}

	private Set<Rule> ruleSetBoundary;
	private boolean allowBoundaryRule;
}
