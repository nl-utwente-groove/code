package groove.explore;

import groove.abstraction.ShapeBFSStrategy;
import groove.explore.encode.EncodedEdgeMap;
import groove.explore.encode.EncodedEnabledRule;
import groove.explore.encode.EncodedInt;
import groove.explore.encode.EncodedRuleMode;
import groove.explore.encode.EncodedType;
import groove.explore.encode.Template;
import groove.explore.encode.Template.Template0;
import groove.explore.encode.Template.Template1;
import groove.explore.encode.Template.Template2;
import groove.explore.prettyparse.PIdentifier;
import groove.explore.prettyparse.PLiteral;
import groove.explore.prettyparse.PNumber;
import groove.explore.prettyparse.POptional;
import groove.explore.prettyparse.PSeparated;
import groove.explore.prettyparse.PSequence;
import groove.explore.prettyparse.SerializedParser;
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
import groove.graph.TypeLabel;
import groove.trans.Rule;

import java.util.Map;

/** Symbolic values for the implemented strategies. */
public enum StrategyValue {
    /** Depth-first RETE strategy. */
    RETE("rete", "Rete Strategy (DFS based)",
            "This strategy finds all possible transitions from the Rete "
                + "network, and continues in a depth-first fashion using "
                + "virtual events when possible. Rete updates are applied "
                + "accumulatively"),
    /** Linear RETE strategy. */
    RETE_LINEAR("retelinear", "Rete Linear Exploration",
            "This strategy chooses one transition from each open state. "
                + "The transition of choice will be the same within one "
                + "incarnation of Groove."),
    /** Random linear RETE strategy. */
    RETE_RANDOM("reterandom", "Rete Random Linear Exploration",
            "This strategy chooses one transition from each open state. "
                + "The transition is chosen randomly."),
    /** Standard breadth-first strategy. */
    BFS("bfs", "Breadth-First Exploration",
            "This strategy first generates all possible transitions from each "
                + "open state, and then continues in a breadth-first fashion."),
    /** Standard depth-first strategy. */
    DFS("dfs", "Depth-First Exploration",
            "This strategy first generates all possible transitions from each "
                + "open state, and then continues in a depth-first fashion."),
    /** Linear strategy. */
    LINEAR("linear", "Linear Exploration",
            "This strategy chooses one transition from each open state. "
                + "The transition of choice will be the same within one "
                + "incarnation of Groove."),
    /** Random linear strategy. */
    RANDOM("random", "Random Linear Exploration",
            "This strategy chooses one transition from each open state. "
                + "The transition is chosen randomly."),
    /** Confluent strategy. */
    CONFLUENT("confluent", "Linear Confluent Exploration",
            "This strategy generates all possible transitions from each open "
                + "state, but only takes one transition of each pair of "
                + "transitions that have been marked as confluent."),
    /** Rule conditional strategy. */
    CONDITIONAL("crule", "Conditional Exploration (Rule Condition)",
            "This strategy performs a conditional breadth-first exploration. "
                + "If a given rule is applicable in a newly reached state, it "
                + " is not explored further. "
                + "All other states are explored normally."),
    /** Node bound conditional strategy. */
    CONDITIONAL_NODE_BOUND("cnbound", "Conditional Exploration (Node Bound)",
            "This strategy performs a conditional breadth-first exploration. "
                + "If the number of nodes in a newly reached state exceeds a "
                + "given bound, it is not explored further. "
                + "All other states are explored normally."),
    /** Edge bound conditional strategy. */
    CONDITIONAL_EDGE_BOUND("cebound", "Conditional Exploration (Edge Bound)",
            "This strategy performs a conditional breadth-first exploration. "
                + "If the number of edges in a newly reached state exceeds a "
                + "given bound, it is not explored further. "
                + "All other states are explored normally."),
    /** Shape exploration strategy. */
    SHAPE_BFS("shapebfs", "Shape Breadth-First Exploration",
            "This strategy is used for abstract state space exploration.");

    private StrategyValue(String keyword, String name, String description) {
        this.keyword = keyword;
        this.name = name;
        this.description = description;
    }

    /** Returns the identifying keyword of this acceptor value. */
    public String getKeyword() {
        return this.keyword;
    }

    /** Returns the name of this acceptor value. */
    public String getName() {
        return this.name;
    }

    /** Returns the description of this acceptor value. */
    public String getDescription() {
        return this.description;
    }

    /** Creates the appropriate template for this strategy. */
    public Template<Strategy> getTemplate() {
        switch (this) {
        case RETE:
            return new MyTemplate0() {
                @Override
                public Strategy create() {
                    return new ReteStrategy();
                }
            };

        case RETE_LINEAR:
            return new MyTemplate0() {

                @Override
                public Strategy create() {
                    return new ReteLinearStrategy();
                }
            };

        case RETE_RANDOM:
            return new MyTemplate0() {
                @Override
                public Strategy create() {
                    return new ReteRandomLinearStrategy();
                }
            };

        case BFS:
            return new MyTemplate0() {
                @Override
                public Strategy create() {
                    return new BFSStrategy();
                }
            };

        case DFS:
            return new MyTemplate0() {
                @Override
                public Strategy create() {
                    return new DFSStrategy();
                }
            };

        case LINEAR:
            return new MyTemplate0() {
                @Override
                public Strategy create() {
                    return new LinearStrategy();
                }
            };

        case RANDOM:
            return new MyTemplate0() {
                @Override
                public Strategy create() {
                    return new RandomLinearStrategy();
                }
            };

        case CONFLUENT:
            return new MyTemplate0() {
                @Override
                public Strategy create() {
                    return new LinearConfluentRules();
                }
            };

        case CONDITIONAL:
            return new MyTemplate2<Rule,Boolean>(new PSequence(
                new POptional("!", "mode", EncodedRuleMode.NEGATIVE,
                    EncodedRuleMode.POSITIVE), new PIdentifier("rule")),
                "rule", new EncodedEnabledRule(), "mode", new EncodedRuleMode()) {

                @Override
                public Strategy create(Rule rule, Boolean mode) {
                    IsRuleApplicableCondition condition =
                        new IsRuleApplicableCondition(rule, mode);
                    ConditionalBFSStrategy strategy =
                        new ConditionalBFSStrategy();
                    strategy.setExploreCondition(condition);
                    return strategy;
                }
            };

        case CONDITIONAL_NODE_BOUND:
            return new MyTemplate1<Integer>(new PNumber("node-bound"),
                "node-bound", new EncodedInt(0, -1)) {

                @Override
                public Strategy create(Integer bound) {
                    ExploreCondition<Integer> condition =
                        new NodeBoundCondition();
                    condition.setCondition(bound);
                    ConditionalBFSStrategy strategy =
                        new ConditionalBFSStrategy();
                    strategy.setExploreCondition(condition);
                    return strategy;
                }
            };

        case CONDITIONAL_EDGE_BOUND:
            return new MyTemplate1<Map<TypeLabel,Integer>>(new PSeparated(
                new PSequence(new PIdentifier("edge-bound"), new PLiteral(">",
                    "edge-bound"), new PNumber("edge-bound")), new PLiteral(
                    ",", "edge-bound")), "edge-bound", new EncodedEdgeMap()) {

                @Override
                public Strategy create(Map<TypeLabel,Integer> bounds) {
                    ExploreCondition<Map<TypeLabel,Integer>> condition =
                        new EdgeBoundCondition();
                    condition.setCondition(bounds);
                    ConditionalBFSStrategy strategy =
                        new ConditionalBFSStrategy();
                    strategy.setExploreCondition(condition);
                    return strategy;
                }
            };

        case SHAPE_BFS:
            return new MyTemplate0() {
                @Override
                public Strategy create() {
                    return new ShapeBFSStrategy();
                }
            };

        default:
            // we can't come here
            throw new IllegalStateException();
        }
    }

    private final String keyword;
    private final String name;
    private final String description;

    /** Specialised parameterless template that uses the strategy value's keyword, name and description. */
    abstract private class MyTemplate0 extends Template0<Strategy> {
        public MyTemplate0() {
            super(StrategyValue.this.getKeyword(),
                StrategyValue.this.getName(),
                StrategyValue.this.getDescription());
        }
    }

    /** Specialised 1-parameter template that uses the strategy value's keyword, name and description. */
    abstract private class MyTemplate1<T1> extends Template1<Strategy,T1> {
        public MyTemplate1(SerializedParser parser, String name,
                EncodedType<T1,String> type) {
            super(StrategyValue.this.getKeyword(),
                StrategyValue.this.getName(),
                StrategyValue.this.getDescription(), parser, name, type);
        }
    }

    /** Specialised 2-parameter template that uses the strategy value's keyword, name and description. */
    abstract private class MyTemplate2<T1,T2> extends Template2<Strategy,T1,T2> {
        public MyTemplate2(SerializedParser parser, String name1,
                EncodedType<T1,String> type1, String name2,
                EncodedType<T2,String> type2) {
            super(StrategyValue.this.getKeyword(),
                StrategyValue.this.getName(),
                StrategyValue.this.getDescription(), parser, name1, type1,
                name2, type2);
        }
    }
}