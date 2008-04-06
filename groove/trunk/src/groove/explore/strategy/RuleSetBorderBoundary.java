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

import java.util.Set;

/**
 * This rule-set boundary is used when starting the next
 * iteration from border states. A rule application is then
 * boundary crossing if the rule is in the set.
 *
 * @author Harmen Kastenberg
 * @version $Revision: 1.6 $
 */
public class RuleSetBorderBoundary extends RuleSetBoundary {

	/**
	 * {@link RuleSetBorderBoundary} constructor.
	 * @param ruleSetBoundary the set of rules that constitute the boundary
	 */
	public RuleSetBorderBoundary(Set<Rule> ruleSetBoundary) {
		super(ruleSetBoundary);
	}

	public boolean crossingBoundary(ProductTransition transition) {
		return super.crossingBoundary(transition);
	}

	public void decreaseDepth() {
		throw new UnsupportedOperationException("When starting from border states, decreasing the depth is not allowed");
	}

	public void increase() {
		// when starting the next iteration from border states
		// increasing the bound means to increase the depth;
		// this ensures that crossing-check return the correct result
		increaseDepth();
	}
}
