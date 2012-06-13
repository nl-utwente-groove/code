/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
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
package groove.explore.result;

import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.trans.Rule;
import groove.view.FormatException;
import groove.view.StoredGrammarView;

/**
 * Condition satisfied when a rule is applicable.
 * @author Iovka Boneva
 * 
 */
public class IsRuleApplicableCondition extends ExploreCondition<Rule> {

    /**
     * Basic constructor. The fields must be set by additional method calls.
     */
    public IsRuleApplicableCondition() {
        // empty
    }
    
    /**
     * Complete constructor with all parameters of the condition.
     * @param condition the rule to be checked
     * @param negated flag to indicate whether this condition is negated or not.
     */
    public IsRuleApplicableCondition(Rule condition, boolean negated) {
        this.condition = condition;
        this.negated = negated;
    }
    
    @Override
    public boolean isSatisfied(GraphState state) {
        boolean result = this.condition.hasMatch(state.getGraph());
        return this.negated ? !result : result;
    }

    /**
     * Checks whether a transition was created from the application of a
     * given rule.
     * @param transition the LTS transition.
     * @return true if the transition was from the given rule. 
     */
    public boolean isSatisfied(GraphTransition transition) {
        return transition.getEvent().getRule().equals(this.condition);
    }
    
    /**
     * Updates the condition when the grammar changes, by replacing the
     * stored rule with one from the new grammar with the same name.
     * Note that only enabled rules (see toGrammar) are checked. 
     * @param grammar - the new grammar
     * @return true - the acceptor is still valid after the grammar update
     *         false - the acceptor is no longer valid after the update
     */ 
    public boolean respondToGrammarUpdate(StoredGrammarView grammar) {
        try {
            Rule rule = grammar.toGrammar().getRule(this.condition.getName());
            if (rule == null)
                return false;
            else {
                this.condition = rule;
                return true;
            }
        } catch (FormatException e) {
            return false;
        }
    }
}