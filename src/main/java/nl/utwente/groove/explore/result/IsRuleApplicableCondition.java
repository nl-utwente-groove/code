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

import nl.utwente.groove.grammar.Rule;
import nl.utwente.groove.lts.GraphState;

/**
 * Condition satisfied when a rule is applicable.
 * @author Iovka Boneva
 */
@NonNullByDefault
public class IsRuleApplicableCondition extends ExploreCondition<Rule> {
    /**
     * Complete constructor with all parameters of the condition.
     * @param condition the rule to be checked
     * @param polarity flag to indicate whether this condition is positive or negative.
     */
    public IsRuleApplicableCondition(Rule condition, boolean polarity) {
        super(condition, polarity);
    }

    @Override
    public boolean test(GraphState state) {
        boolean result = criterion().hasMatch(state.getGraph());
        return isNegated()
            ? !result
            : result;
    }
}
