package groove.explore;

import groove.abstraction.neigh.explore.strategy.ShapeBFSStrategy;
import groove.abstraction.neigh.explore.strategy.ShapeDFSStrategy;
import groove.explore.encode.EncodedBoundary;
import groove.explore.encode.EncodedEdgeMap;
import groove.explore.encode.EncodedEnabledRule;
import groove.explore.encode.EncodedHostName;
import groove.explore.encode.EncodedInt;
import groove.explore.encode.EncodedLtlProperty;
import groove.explore.encode.EncodedRuleMode;
import groove.explore.encode.EncodedType;
import groove.explore.encode.Serialized;
import groove.explore.encode.Template;
import groove.explore.encode.Template.Template0;
import groove.explore.encode.Template.Template1;
import groove.explore.encode.Template.Template2;
import groove.explore.prettyparse.PAll;
import groove.explore.prettyparse.PChoice;
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
import groove.explore.strategy.Strategy;
import groove.explore.strategy.BFSStrategy;
import groove.explore.strategy.Boundary;
import groove.explore.strategy.BoundedLtlStrategy;
import groove.explore.strategy.BoundedPocketLtlStrategy;
import groove.explore.strategy.ConditionalBFSStrategy;
import groove.explore.strategy.DFSStrategy;
import groove.explore.strategy.ExploreStateStrategy;
import groove.explore.strategy.LinearStrategy;
import groove.explore.strategy.LtlStrategy;
import groove.explore.strategy.RandomLinearStrategy;
import groove.explore.strategy.RemoteStrategy;
import groove.explore.strategy.ReteLinearStrategy;
import groove.explore.strategy.ReteRandomLinearStrategy;
import groove.explore.strategy.ReteStrategy;
import groove.graph.TypeLabel;
import groove.trans.Rule;
import groove.view.GrammarModel;

import java.util.EnumSet;
import java.util.Map;

/** Symbolic values for the implemented strategies. */
public enum StrategyValue implements ParsableValue {
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
    /** Random linear strategy. */
    STATE("state", "Single-State Exploration",
            "This strategy fully explores the current state."),
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
    /** LTL model checking strategy. */
    LTL("ltl", "LTL Model Checking",
            "Nested Depth-First Search for a given LTL formula."),
    /** Bounded LTL model checking  strategy. */
    LTL_BOUNDED(
            "ltlbounded",
            "Bounded LTL Model Checking",
            "Nested Depth-First Search for a given LTL formula,"
                + "using incremental bounds based on graph size or rule applications"),
    /** Bounded LTL model checking strategy. */
    LTL_POCKET(
            "ltlpocket",
            "Pocket LTL Model Checking",
            "Nested Depth-First Search for a given LTL formula,"
                + "using incremental bounds based on graph size or rule applications"
                + "and optimised to avoid reexploring connected components ('pockets')"),
    /** Shape exploration strategy. */
    SHAPE_DFS("shapedfs", "Shape Depth-First Exploration",
            "This strategy is used for abstract state space exploration."),
    /** Shape exploration strategy. */
    SHAPE_BFS("shapebfs", "Shape Breadth-First Exploration",
            "This strategy is used for abstract state space exploration."),
    /** Remote strategy. */
    REMOTE("remote", "Remote Exploration",
            "This strategy sends the result as an STS to a remote server.");

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

    @Override
    public Serialized toSerialized() {
        return new Serialized(getKeyword());
    }

    @Override
    public boolean isDevelopment() {
        return DEVELOPMENT_ONLY_STRATEGIES.contains(this);
    }

    @Override
    public boolean isDefault(GrammarModel grammar) {
        Exploration exploration = grammar.getDefaultExploration();
        return exploration == null ? this == BFS
                : exploration.getStrategy().getKeyword().equals(getKeyword());
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

        case STATE:
            return new MyTemplate0() {
                @Override
                public Strategy create() {
                    return new ExploreStateStrategy();
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

        case LTL:
            return new MyTemplate1<String>(new PAll("prop"), "prop",
                new EncodedLtlProperty()) {
                @Override
                public Strategy create(String property) {
                    LtlStrategy result = new LtlStrategy();
                    result.setProperty(property);
                    return result;
                }
            };

        case LTL_BOUNDED:
            SerializedParser boundParser =
                new PSeparated(new PChoice(new PIdentifier("rule"),
                    new PNumber("value")), new PLiteral(",", "comma"));
            SerializedParser parser =
                new PSequence(boundParser, new PLiteral(";", "semi"), new PAll(
                    "prop"));
            return new MyTemplate2<String,Boundary>(parser, "prop",
                new EncodedLtlProperty(), "bound", new EncodedBoundary()) {
                @Override
                public Strategy create(String property, Boundary bound) {
                    BoundedLtlStrategy result = new BoundedLtlStrategy();
                    result.setProperty(property);
                    result.setBoundary(bound);
                    return result;
                }
            };

        case LTL_POCKET:
            boundParser =
                new PSeparated(new PChoice(new PIdentifier("rule"),
                    new PNumber("value")), new PLiteral(",", "comma"));
            parser =
                new PSequence(boundParser, new PLiteral(";", "semi"), new PAll(
                    "prop"));
            return new MyTemplate2<String,Boundary>(parser, "prop",
                new EncodedLtlProperty(), "bound", new EncodedBoundary()) {
                @Override
                public Strategy create(String property, Boundary bound) {
                    BoundedLtlStrategy result = new BoundedPocketLtlStrategy();
                    result.setProperty(property);
                    result.setBoundary(bound);
                    return result;
                }
            };

        case SHAPE_BFS:
            return new MyTemplate0() {
                @Override
                public Strategy create() {
                    return new ShapeBFSStrategy();
                }
            };

        case SHAPE_DFS:
            return new MyTemplate0() {
                @Override
                public Strategy create() {
                    return new ShapeDFSStrategy();
                }
            };
        case REMOTE:
            return new MyTemplate1<String>(new PAll("host"), "host",
                new EncodedHostName()) {

                @Override
                public Strategy create(String host) {
                    RemoteStrategy strategy = new RemoteStrategy();
                    strategy.setHost(host);
                    return strategy;
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

    /** Set of model checking strategies. */
    public final static EnumSet<StrategyValue> LTL_STRATEGIES = EnumSet.of(LTL,
        LTL_BOUNDED, LTL_POCKET);
    /** Set of strategies that can be selected from the exploration dialog. */
    public final static EnumSet<StrategyValue> DIALOG_STRATEGIES;
    /** Special mask for development strategies only. Treated specially. */
    public final static EnumSet<StrategyValue> DEVELOPMENT_ONLY_STRATEGIES =
        EnumSet.of(RETE, RETE_LINEAR, RETE_RANDOM, SHAPE_DFS, SHAPE_BFS);
    /** Set of strategies for abstract exploration. */
    public final static EnumSet<StrategyValue> ABSTRACT_STRATEGIES =
        EnumSet.of(SHAPE_DFS, SHAPE_BFS);
    /** Set of strategies for concrete exploration. */
    public final static EnumSet<StrategyValue> CONCRETE_STRATEGIES =
        EnumSet.complementOf(ABSTRACT_STRATEGIES);
    static {
        DIALOG_STRATEGIES = EnumSet.complementOf(LTL_STRATEGIES);
        DIALOG_STRATEGIES.remove(STATE);
    }

    /** Specialised parameterless template that uses the strategy value's keyword, name and description. */
    abstract private class MyTemplate0 extends Template0<Strategy> {
        public MyTemplate0() {
            super(StrategyValue.this);
        }
    }

    /** Specialised 1-parameter template that uses the strategy value's keyword, name and description. */
    abstract private class MyTemplate1<T1> extends
            Template1<Strategy,T1> {
        public MyTemplate1(SerializedParser parser, String name,
                EncodedType<T1,String> type) {
            super(StrategyValue.this, parser, name, type);
        }
    }

    /** Specialised 2-parameter template that uses the strategy value's keyword, name and description. */
    abstract private class MyTemplate2<T1,T2> extends
            Template2<Strategy,T1,T2> {
        public MyTemplate2(SerializedParser parser, String name1,
                EncodedType<T1,String> type1, String name2,
                EncodedType<T2,String> type2) {
            super(StrategyValue.this, parser, name1, type1, name2, type2);
        }
    }
}
