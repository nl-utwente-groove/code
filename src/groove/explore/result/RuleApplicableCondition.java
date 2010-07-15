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

import groove.explore.result.StateCondition;
import groove.lts.GraphState;
import groove.trans.Rule;

/**
 * A <code>RuleApplicableCondition</code> is a <code>StateCondition</code>
 * that verifies whether a given rule is applicable in a newly created state
 * in the LTS.
 * 
 * @author Maarten de Mol
 */
public class RuleApplicableCondition implements StateCondition {

    private final Rule rule;

    /**
     * Default constructor. Only remembers the rule.
     */
    public RuleApplicableCondition(Rule rule) {
        this.rule = rule;
    }

    @Override
    public boolean evalNewState(GraphState newState) {
        return this.rule.hasMatch(newState.getGraph());
    }

}
