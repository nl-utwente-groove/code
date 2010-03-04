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

import groove.explore.encode.EncodedEnabledRule;
import groove.explore.encode.EncodedRuleMode;
import groove.explore.encode.TemplateList;
import groove.explore.encode.Template.Template0;
import groove.explore.encode.Template.Template2;
import groove.explore.result.IsRuleApplicableCondition;
import groove.explore.strategy.BFSStrategy;
import groove.explore.strategy.ConditionalBFSStrategy;
import groove.explore.strategy.ExploreRuleDFStrategy;
import groove.explore.strategy.LinearConfluentRules;
import groove.explore.strategy.LinearStrategy;
import groove.explore.strategy.RandomLinearStrategy;
import groove.explore.strategy.Strategy;
import groove.gui.Simulator;
import groove.trans.Rule;

/**
 * <!=========================================================================>
 * StrategyEnumerator enumerates all strategies that are available in GROOVE.
 * With this enumeration, it is possible to create an editor for strategies
 * (inherited method createEditor, stored results as a Serialized) and to
 * parse a strategy from a Serialized (inherited method parse).
 * TODO: Also use this enumerator in the Generator.
 * <!=========================================================================>
 * @author Maarten de Mol
 */
public class StrategyEnumerator extends TemplateList<Strategy> {

    private static final String STRATEGY_TOOLTIP =
        "<HTML>" + "The exploration strategy determines at each state:<BR>"
            + "<B>1.</B> Which of the applicable transitions will be taken; "
            + "and<BR>"
            + "<B>2.</B> In which order the reached states will be explored."
            + "</HTML>";

    /**
     * Enumerates the available strategies one by one. A strategy is defined
     * by means of a Template<Strategy> instance.
     */
    public StrategyEnumerator() {
        super("exploration strategy", STRATEGY_TOOLTIP);

        addTemplate(new Template0<Strategy>("Breadth-First",
            "Breadth-First Exploration",
            "This strategy first generates all possible transitions from each "
                + "open state, and then continues in a breadth-first fashion.") {

            @Override
            public Strategy create(Simulator simulator) {
                return new BFSStrategy();
            }
        });

        addTemplate(new Template0<Strategy>("Depth-First",
            "Depth-First Exploration",
            "This strategy first generates all possible transitions from each "
                + "open state, and then continues in a depth-first fashion.") {

            @Override
            public Strategy create(Simulator simulator) {
                return new ExploreRuleDFStrategy();
            }
        });

        addTemplate(new Template0<Strategy>("Linear", "Linear Exploration",
            "This strategy chooses one transition from each open state. "
                + "The transition of choice will be the same within one "
                + "incarnation of Groove.") {

            @Override
            public Strategy create(Simulator simulator) {
                return new LinearStrategy();
            }
        });

        addTemplate(new Template0<Strategy>("RandomLinear",
            "Random Linear Exploration",
            "This strategy chooses one transition from each open state. "
                + "The transition is chosen randomly.") {

            @Override
            public Strategy create(Simulator simulator) {
                return new RandomLinearStrategy();
            }
        });

        addTemplate(new Template0<Strategy>("LinearConfluent",
            "Linear Confluent Exploration",
            "This strategy generates all possible transitions from each open "
                + "state, but only takes one transition of each pair of "
                + "transitions that have been marked as confluent.") {

            @Override
            public Strategy create(Simulator simulator) {
                return new LinearConfluentRules();
            }
        });

        addTemplate(new Template2<Strategy,Rule,Boolean>(
            "Conditional",
            "Conditional Exploration",
            "This strategy distinguishes between normal states, which are "
                + "explored in the same way as the breadth-first strategy, and "
                + "special states, which are not explored at all. "
                + "The distinction is made on the basis of an additional rule "
                + "condition.", "rule", new EncodedEnabledRule(), "mode",
            new EncodedRuleMode()) {

            @Override
            public Strategy create(Simulator simulator, Rule rule, Boolean mode) {
                IsRuleApplicableCondition condition =
                    new IsRuleApplicableCondition(rule, mode);
                ConditionalBFSStrategy strategy = new ConditionalBFSStrategy();
                strategy.setExploreCondition(condition);
                return strategy;
            }

        });
    }
}