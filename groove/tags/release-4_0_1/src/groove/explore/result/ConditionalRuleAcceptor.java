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

import groove.trans.Rule;
import groove.view.FormatException;
import groove.view.StoredGrammarView;

/**
 * Extension of ConditionalAcceptor<Rule> that offers additional methods
 * for manipulating the stored rule.
 * 
 * @author Maarten de Mol
 */
public abstract class ConditionalRuleAcceptor extends ConditionalAcceptor<Rule> {
    /**
     * Constructs a new acceptor.
     * @param condition
     * @param result
     */
    public ConditionalRuleAcceptor(ExploreCondition<Rule> condition, Result result) {
        super(condition, result);
    }
    
    /**
     * Renew the ExploreCondition<Rule> when the grammar changes, by replacing the stored
     * rule with a rule from the new grammar with the same name.
     * @return - true if the rule was replaced successfully
     *           false if no rule with the old name was found in the new grammar
     */
    public boolean renewCondition(StoredGrammarView grammar) {
        if (getCondition() == null)
            return false;
        if (getCondition().getCondition() == null)
            return false;
        
        try {
            Rule rule = grammar.toGrammar().getRule(getCondition().getCondition().getName());
            if (rule == null)
                return false;
            else {
                getCondition().setCondition(rule);
                return true;
            }
        } catch (FormatException e) {
            return false;
        }
    }
}
