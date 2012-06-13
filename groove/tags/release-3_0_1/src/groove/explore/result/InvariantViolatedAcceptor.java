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

import groove.graph.GraphShape;
import groove.graph.Node;
import groove.lts.GraphState;

/** Accepts states that violate an (possible negated) invariant condition on states.
 * The invariant is defined by the applicability of a given rule.
 * @author Iovka Boneva
 * @param <C> The type of the condition.
 *
 */
public class InvariantViolatedAcceptor<C> extends ConditionalAcceptor<C> {
	@Override
	public void addUpdate(GraphShape graph, Node node) {
		if (! getCondition().isSatisfiedBy((GraphState) node)) {
			getResult().add((GraphState) node);
		}
	}
}