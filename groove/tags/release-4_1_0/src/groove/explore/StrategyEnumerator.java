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

import groove.abstraction.ShapeBFSStrategy;
import groove.explore.encode.EncodedEdgeMap;
import groove.explore.encode.EncodedEnabledRule;
import groove.explore.encode.EncodedInt;
import groove.explore.encode.EncodedRuleMode;
import groove.explore.encode.Template;
import groove.explore.encode.Template.Template0;
import groove.explore.encode.Template.Template1;
import groove.explore.encode.Template.Template2;
import groove.explore.encode.Template.Visibility;
import groove.explore.encode.TemplateList;
import groove.explore.prettyparse.PIdentifier;
import groove.explore.prettyparse.PLiteral;
import groove.explore.prettyparse.PNumber;
import groove.explore.prettyparse.POptional;
import groove.explore.prettyparse.PSeparated;
import groove.explore.prettyparse.PSequence;
import groove.explore.result.EdgeBoundCondition;
import groove.explore.result.ExploreCondition;
import groove.explore.result.IsRuleApplicableCondition;
import groove.explore.result.NodeBoundCondition;
import groove.explore.strategy.BFSStrategy;
import groove.explore.strategy.ConditionalBFSStrategy;
import groove.explore.strategy.DFSStrategy;
import groove.explore.strategy.LinearConfluentRules;
import groove.explore.strategy.LinearStrategy;
import groove.explore.strategy.RandomLinearStrategy;
import groove.explore.strategy.ReteLinearStrategy;
import groove.explore.strategy.ReteRandomLinearStrategy;
import groove.explore.strategy.ReteStrategy;
import groove.explore.strategy.Strategy;
import groove.graph.Label;
import groove.lts.GTS;
import groove.trans.Rule;

import java.util.Map;

/**
 * <!=========================================================================>
 * StrategyEnumerator enumerates all strategies that are available in GROOVE.
 * With this enumeration, it is possible to create an editor for strategies
 * (inherited method createEditor, stored results as a Serialized) and to
 * parse a strategy from a Serialized (inherited method parse).
 * <!=========================================================================>
 * @author Maarten de Mol
 */
public class StrategyEnumerator extends TemplateList<Strategy> {

    /** Mask for strategies that are only enabled in 'concrete' mode. */
    public final static int MASK_CONCRETE = 1;
    /** Mask for strategies that are only enabled in 'abstraction' mode. */
    public final static int MASK_ABSTRACT = 2;

    /** Special mask for development strategies only. Treated specially. */
    public final static int MASK_DEVELOPMENT_ONLY = 4;

    /** Mask for strategies that are enabled in all modes. */
    public final static int MASK_ALL = MASK_CONCRETE | MASK_ABSTRACT;
    /** Mask that is used by default. */
    public final static int MASK_DEFAULT = MASK_CONCRETE;

    private static final String STRATEGY_TOOLTIP = "<HTML>"
        + "The exploration strategy determines at each state:<BR>"
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

        addTemplate(MASK_CONCRETE | MASK_DEVELOPMENT_ONLY,
            new Template0<Strategy>("rete", "Rete Strategy (DFS based)",
                "This strategy finds all possible transitions from the Rete "
                    + "network, and continues in a depth-first fashion using "
                    + "virtual events when possible. Rete updates are applied "
                    + "accumulatively") {

                @Override
                public Strategy create(GTS gts) {
                    return new ReteStrategy();
                }
            });

        addTemplate(MASK_CONCRETE | MASK_DEVELOPMENT_ONLY,
            new Template0<Strategy>("retelinear", "Rete Linear Exploration",
                "This strategy chooses one transition from each open state. "
                    + "The transition of choice will be the same within one "
                    + "incarnation of Groove.") {

                @Override
                public Strategy create(GTS gts) {
                    return new ReteLinearStrategy();
                }
            });

        addTemplate(MASK_CONCRETE | MASK_DEVELOPMENT_ONLY,
            new Template0<Strategy>("reterandom",
                "Rete Random Linear Exploration",
                "This strategy chooses one transition from each open state. "
                    + "The transition is chosen randomly.") {

                @Override
                public Strategy create(GTS gts) {
                    return new ReteRandomLinearStrategy();
                }
            });

        addTemplate(new Template0<Strategy>("bfs", "Breadth-First Exploration",
            "This strategy first generates all possible transitions from each "
                + "open state, and then continues in a breadth-first fashion.") {

            @Override
            public Strategy create(GTS gts) {
                return new BFSStrategy();
            }
        });

        addTemplate(new Template0<Strategy>("dfs", "Depth-First Exploration",
            "This strategy first generates all possible transitions from each "
                + "open state, and then continues in a depth-first fashion.") {

            @Override
            public Strategy create(GTS gts) {
                return new DFSStrategy();
            }
        });

        addTemplate(new Template0<Strategy>("linear", "Linear Exploration",
            "This strategy chooses one transition from each open state. "
                + "The transition of choice will be the same within one "
                + "incarnation of Groove.") {

            @Override
            public Strategy create(GTS gts) {
                return new LinearStrategy();
            }
        });

        addTemplate(new Template0<Strategy>("random",
            "Random Linear Exploration",
            "This strategy chooses one transition from each open state. "
                + "The transition is chosen randomly.") {

            @Override
            public Strategy create(GTS gts) {
                return new RandomLinearStrategy();
            }
        });

        addTemplate(new Template0<Strategy>("confluent",
            "Linear Confluent Exploration",
            "This strategy generates all possible transitions from each open "
                + "state, but only takes one transition of each pair of "
                + "transitions that have been marked as confluent.") {

            @Override
            public Strategy create(GTS gts) {
                return new LinearConfluentRules();
            }
        });

        addTemplate(new Template2<Strategy,Rule,Boolean>("crule",
            "Conditional Exploration (Rule Condition)",
            "This strategy performs a conditional breadth-first exploration. "
                + "If a given rule is applicable in a newly reached state, it "
                + " is not explored further. "
                + "All other states are explored normally.", new PSequence(
                new POptional("!", "mode", EncodedRuleMode.NEGATIVE,
                    EncodedRuleMode.POSITIVE), new PIdentifier("rule")),
            "rule", new EncodedEnabledRule(), "mode", new EncodedRuleMode()) {

            @Override
            public Strategy create(GTS gts, Rule rule, Boolean mode) {
                IsRuleApplicableCondition condition =
                    new IsRuleApplicableCondition(rule, mode);
                ConditionalBFSStrategy strategy = new ConditionalBFSStrategy();
                strategy.setExploreCondition(condition);
                return strategy;
            }
        });

        addTemplate(new Template1<Strategy,Integer>("cnbound",
            "Conditional Exploration (Node Bound)",
            "This strategy performs a conditional breadth-first exploration. "
                + "If the number of nodes in a newly reached state exceeds a "
                + "given bound, it is not explored further. "
                + "All other states are explored normally.", new PNumber(
                "node-bound"), "node-bound", new EncodedInt(0, -1)) {

            @Override
            public Strategy create(GTS gts, Integer bound) {
                ExploreCondition<Integer> condition = new NodeBoundCondition();
                condition.setCondition(bound);
                ConditionalBFSStrategy strategy = new ConditionalBFSStrategy();
                strategy.setExploreCondition(condition);
                return strategy;
            }
        });

        addTemplate(new Template1<Strategy,Map<Label,Integer>>("cebound",
            "Conditional Exploration (Edge Bound)",
            "This strategy performs a conditional breadth-first exploration. "
                + "If the number of edges in a newly reached state exceeds a "
                + "given bound, it is not explored further. "
                + "All other states are explored normally.",

            new PSeparated(new PSequence(new PIdentifier("edge-bound"),
                new PLiteral(">", "edge-bound"), new PNumber("edge-bound")),
                new PLiteral(",", "edge-bound")), "edge-bound",
            new EncodedEdgeMap()) {

            @Override
            public Strategy create(GTS gts, Map<Label,Integer> bounds) {
                ExploreCondition<Map<Label,Integer>> condition =
                    new EdgeBoundCondition();
                condition.setCondition(bounds);
                ConditionalBFSStrategy strategy = new ConditionalBFSStrategy();
                strategy.setExploreCondition(condition);
                return strategy;
            }
        });

        addTemplate(MASK_ABSTRACT | MASK_DEVELOPMENT_ONLY,
            new Template0<Strategy>("shapebfs",
                "Shape Breadth-First Exploration",
                "This strategy is used for abstract state space exploration.") {

                @Override
                public Strategy create(GTS gts) {
                    return new ShapeBFSStrategy();
                }
            });
    }

    /** Specialized addTemplate. Scans for MASK_DEVELOPMENT_ONLY. */
    public void addTemplate(int mask, Template<Strategy> template) {
        int mymask = mask;
        if ((mymask & MASK_DEVELOPMENT_ONLY) == MASK_DEVELOPMENT_ONLY) {
            template.setVisibility(Visibility.DEVELOPMENT_ONLY);
            mymask = mymask - MASK_DEVELOPMENT_ONLY;
        }
        template.setMask(mymask);
        super.addTemplate(template);
    }

    @Override
    public void addTemplate(Template<Strategy> template) {
        template.setMask(MASK_DEFAULT);
        super.addTemplate(template);
    }
}