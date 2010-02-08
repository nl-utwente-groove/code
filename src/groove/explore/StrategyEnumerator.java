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
package groove.explore;

import groove.explore.Serialized.Materialize;
import groove.explore.result.IsRuleApplicableCondition;
import groove.explore.strategy.BFSStrategy;
import groove.explore.strategy.ConditionalBFSStrategy;
import groove.explore.strategy.ExploreRuleDFStrategy;
import groove.explore.strategy.LinearConfluentRules;
import groove.explore.strategy.LinearStrategy;
import groove.explore.strategy.RandomLinearStrategy;
import groove.explore.strategy.Strategy;
import groove.trans.Rule;

/**
 * An enumeration of Serialized<Strategy>.
 * Stores all the exploration strategies that can be executed within Groove.
 *
 * @author Maarten de Mol
 * @version $Revision $
 * 
 */
public class StrategyEnumerator extends Enumerator<Strategy> {
    /**
     * Extended constructor. Enumerates the available strategies one by one.
     */
    public StrategyEnumerator() {
        super();

        addElement("Breadth-First", "Breadth-First Exploration",
            "This strategy first generates all possible transitions from each "
                + "open state, and then continues in a breadth-first fashion.",
            new BFSStrategy());

        addElement("Depth-First", "Depth-First Exploration",
            "This strategy first generates all possible transitions from each "
                + "open state, and then continues in a depth-first fashion.",
            new ExploreRuleDFStrategy());

        addElement("LinearConfluent", "Linear Confluent Exploration",
            "This strategy generates all possible transitions from each open "
                + "state, but only takes one transition of each pair of "
                + "transitions that have been marked as confluent.",
            new LinearConfluentRules());

        addElement("Linear", "Linear Exploration",
            "This strategy chooses one transition from each open state. "
                + "The transition of choice will be the same within one "
                + "incarnation of Groove.", new LinearStrategy());

        addElement("RandomLinear", "Random Linear Exploration",
            "This strategy chooses one transition from each open state. "
                + "The transition is chosen randomly.",
            new RandomLinearStrategy());

        addElement(
            "ConditionalBFS",
            "Breadth-First + Rule Condition",
            "This strategy distinguishes between normal states, which are "
                + "explored in the same way as the breadth-first strategy, and "
                + "special states, which are not explored at all. "
                + "The distinction is made on the basis of an additional rule "
                + "condition.", new ConditionalModeArgument(),
            new RuleArgument(""), new MaterializeConditionalBFSStrategy());
    }

    private class ConditionalModeArgument extends OptionArgument {
        static final String MODE_POSITIVE = "Positive";
        static final String MODE_POSITIVE_ID =
            "Do not explore state if rule matches.";

        static final String MODE_NEGATIVE = "Negative";
        static final String MODE_NEGATIVE_ID =
            "Do not explore state if rule does not match.";

        public ConditionalModeArgument() {
            super("Mode");
            addOption(MODE_POSITIVE, MODE_POSITIVE_ID);
            addOption(MODE_NEGATIVE, MODE_NEGATIVE_ID);
            setSerializedValue(MODE_POSITIVE);
        }
    }

    private class MaterializeConditionalBFSStrategy implements
            Materialize<Strategy> {

        @Override
        public Strategy materialize(Object[] arguments) {
            boolean modeArg =
                ((String) arguments[0]).equals(ConditionalModeArgument.MODE_POSITIVE);
            Rule ruleArg = (Rule) arguments[1];
            IsRuleApplicableCondition condition =
                new IsRuleApplicableCondition(ruleArg, modeArg);
            ConditionalBFSStrategy strategy = new ConditionalBFSStrategy();
            strategy.setExploreCondition(condition);
            return strategy;
        }
    }
}