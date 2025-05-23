/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
 * University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * $Id$
 */
package nl.utwente.groove.explore.result;

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.lts.GraphState;

/**
 * Condition on the number of nodes of a state.
 *
 * This condition is given by an integer value which gives a maximum (or minimum
 * if negated) bound on the number of nodes in a state.
 *
 * @author Iovka Boneva
 */
@NonNullByDefault
public class NodeBoundCondition extends ExploreCondition<Integer> {
    /**
     * Constructs a condition.
     */
    public NodeBoundCondition(Integer condition) {
        super(condition);
    }

    @Override
    public boolean test(GraphState state) {
        boolean result = state.getGraph().nodeCount() > criterion();
        return isNegated()
            ? !result
            : result;
    }
}
