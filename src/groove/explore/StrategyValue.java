package groove.explore;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import groove.explore.encode.EncodedBoundary;
import groove.explore.encode.EncodedEdgeMap;
import groove.explore.encode.EncodedEnabledRule;
import groove.explore.encode.EncodedHostName;
import groove.explore.encode.EncodedInt;
import groove.explore.encode.EncodedLtlProperty;
import groove.explore.encode.EncodedRuleList;
import groove.explore.encode.EncodedRuleMode;
import groove.explore.encode.EncodedType;
import groove.explore.encode.Serialized;
import groove.explore.encode.Template;
import groove.explore.encode.Template.Template0;
import groove.explore.encode.Template.Template1;
import groove.explore.encode.Template.Template2;
import groove.explore.encode.Template.TemplateN;
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
import groove.explore.result.IsRuleApplicableCondition;
import groove.explore.result.NodeBoundCondition;
import groove.explore.strategy.BFSStrategy;
import groove.explore.strategy.Boundary;
import groove.explore.strategy.BoundedLTLStrategy;
import groove.explore.strategy.BoundedPocketLTLStrategy;
import groove.explore.strategy.ConditionalBFSStrategy;
import groove.explore.strategy.DFSStrategy;
import groove.explore.strategy.ExploreStateStrategy;
import groove.explore.strategy.LTLStrategy;
import groove.explore.strategy.LinearStrategy;
import groove.explore.strategy.MinimaxStrategy;
import groove.explore.strategy.RandomLinearStrategy;
import groove.explore.strategy.RemoteStrategy;
import groove.explore.strategy.ReteLinearStrategy;
import groove.explore.strategy.ReteRandomLinearStrategy;
import groove.explore.strategy.ReteStrategy;
import groove.explore.strategy.Strategy;
import groove.grammar.Rule;
import groove.grammar.model.GrammarModel;
import groove.grammar.type.TypeLabel;

/** Symbolic values for the implemented strategies. */
public enum StrategyValue implements ParsableValue {
    /** Standard breadth-first strategy. */
    BFS("bfs", "Breadth-First Exploration", "This strategy first generates all possible transitions from each " + "open state, and then continues in a breadth-first fashion."),
    /** Standard depth-first strategy. */
    DFS("dfs", "Depth-First Exploration", "This strategy first generates all possible transitions from each " + "open state, and then continues in a depth-first fashion."),
    /** Linear strategy. */
    LINEAR("linear", "Linear Exploration", "This strategy chooses one transition from each open state. " + "The transition of choice will be the same within one " + "incarnation of Groove."),
    /** Random linear strategy. */
    RANDOM("random", "Random Linear Exploration", "This strategy chooses one transition from each open state. " + "The transition is chosen randomly."),
    /** Single-state strategy. */
    STATE("state", "Single-State Exploration", "This strategy fully explores the current state."),
    /** Depth-first RETE strategy. */
    RETE("rete", "Rete Strategy (DFS based)", "This strategy finds all possible transitions from the Rete " + "network, and continues in a depth-first fashion using " + "virtual events when possible. Rete updates are applied " + "accumulatively"),
    /** Linear RETE strategy. */
    RETE_LINEAR("retelinear", "Rete Linear Exploration", "This strategy chooses one transition from each open state. " + "The transition of choice will be the same within one " + "incarnation of Groove."),
    /** Random linear RETE strategy. */
    RETE_RANDOM("reterandom", "Rete Random Linear Exploration", "This strategy chooses one transition from each open state. " + "The transition is chosen randomly."),
    /** Rule conditional strategy. */
    CONDITIONAL("crule", "Conditional Exploration (Rule Condition)", "This strategy performs a conditional breadth-first exploration. " + "If a given rule is applicable in a newly reached state, it " + " is not explored further. " + "All other states are explored normally."),
    /** Node bound conditional strategy. */
    CONDITIONAL_NODE_BOUND("cnbound", "Conditional Exploration (Node Bound)", "This strategy performs a conditional breadth-first exploration. " + "If the number of nodes in a newly reached state exceeds a " + "given bound, it is not explored further. " + "All other states are explored normally."),
    /** Edge bound conditional strategy. */
    CONDITIONAL_EDGE_BOUND("cebound", "Conditional Exploration (Edge Bound)", "This strategy performs a conditional breadth-first exploration. " + "If the number of edges in a newly reached state exceeds a " + "given bound, it is not explored further. " + "All other states are explored normally."),
    /** LTL model checking strategy. */
    LTL("ltl", "LTL Model Checking", "Nested Depth-First Search for a given LTL formula."),
    /** Bounded LTL model checking  strategy. */
    LTL_BOUNDED("ltlbounded", "Bounded LTL Model Checking", "Nested Depth-First Search for a given LTL formula," + "using incremental bounds based on graph size or rule applications"),
    /** Bounded LTL model checking strategy. */
    LTL_POCKET("ltlpocket", "Pocket LTL Model Checking", "Nested Depth-First Search for a given LTL formula," + "using incremental bounds based on graph size or rule applications" + "and optimised to avoid reexploring connected components ('pockets')"),
    /** Minimax strategy. */
    MINIMAX("minimax", "Minimax Strategy Generation", "This strategy generates a strategy for a two-player game."),
    /** Remote strategy. */
    REMOTE("remote", "Remote Exploration", "This strategy sends the result as an STS to a remote server.");

    private StrategyValue(String keyword, String name, String description) {
        this.keyword = keyword;
        this.name = name;
        this.description = description;
    }

    /** Returns the identifying keyword of this acceptor value. */
    @Override
    public String getKeyword() {
        return this.keyword;
    }

    /** Returns the name of this acceptor value. */
    @Override
    public String getName() {
        return this.name;
    }

    /** Returns the description of this acceptor value. */
    @Override
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
        ExploreType exploration = grammar.getDefaultExploreType();
        return exploration.getStrategy()
            .getKeyword()
            .equals(getKeyword());
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
            return new MyTemplate2<Rule,Boolean>(
                new PSequence(
                    new POptional("!", "mode", EncodedRuleMode.NEGATIVE, EncodedRuleMode.POSITIVE),
                    new PIdentifier("rule")),
                "rule", new EncodedEnabledRule(), "mode", new EncodedRuleMode()) {

                @Override
                public Strategy create(Rule rule, Boolean mode) {
                    IsRuleApplicableCondition condition = new IsRuleApplicableCondition(rule, mode);
                    ConditionalBFSStrategy strategy = new ConditionalBFSStrategy();
                    strategy.setExploreCondition(condition);
                    return strategy;
                }
            };

        case CONDITIONAL_NODE_BOUND:
            return new MyTemplate1<Integer>(new PNumber("node-bound"), "node-bound",
                new EncodedInt(0, -1)) {

                @Override
                public Strategy create(Integer bound) {
                    NodeBoundCondition condition = new NodeBoundCondition(bound);
                    ConditionalBFSStrategy strategy = new ConditionalBFSStrategy();
                    strategy.setExploreCondition(condition);
                    return strategy;
                }
            };

        case CONDITIONAL_EDGE_BOUND:
            return new MyTemplate1<Map<TypeLabel,Integer>>(
                new PSeparated(new PSequence(new PIdentifier("edge-bound"),
                    new PLiteral(">", "edge-bound"), new PNumber("edge-bound")),
                    new PLiteral(",", "edge-bound")),
                "edge-bound", new EncodedEdgeMap()) {

                @Override
                public Strategy create(Map<TypeLabel,Integer> bounds) {
                    EdgeBoundCondition condition = new EdgeBoundCondition(bounds);
                    ConditionalBFSStrategy strategy = new ConditionalBFSStrategy();
                    strategy.setExploreCondition(condition);
                    return strategy;
                }
            };

        case LTL:
            return new MyTemplate1<String>(new PAll("prop"), "prop", new EncodedLtlProperty()) {
                @Override
                public Strategy create(String property) {
                    LTLStrategy result = new LTLStrategy();
                    result.setProperty(property);
                    return result;
                }
            };

        case LTL_BOUNDED:
            SerializedParser boundParser =
                new PSeparated(new PChoice(new PIdentifier("rule"), new PNumber("value")),
                    new PLiteral(",", "comma"));
            SerializedParser parser =
                new PSequence(boundParser, new PLiteral(";", "semi"), new PAll("prop"));
            return new MyTemplate2<String,Boundary>(parser, "prop", new EncodedLtlProperty(),
                "bound", new EncodedBoundary()) {
                @Override
                public Strategy create(String property, Boundary bound) {
                    BoundedLTLStrategy result = new BoundedLTLStrategy();
                    result.setProperty(property);
                    result.setBoundary(bound);
                    return result;
                }
            };

        case LTL_POCKET:
            boundParser = new PSeparated(new PChoice(new PIdentifier("rule"), new PNumber("value")),
                new PLiteral(",", "comma"));
            parser = new PSequence(boundParser, new PLiteral(";", "semi"), new PAll("prop"));
            return new MyTemplate2<String,Boundary>(parser, "prop", new EncodedLtlProperty(),
                "bound", new EncodedBoundary()) {
                @Override
                public Strategy create(String property, Boundary bound) {
                    BoundedLTLStrategy result = new BoundedPocketLTLStrategy();
                    result.setProperty(property);
                    result.setBoundary(bound);
                    return result;
                }
            };
        case REMOTE:
            return new MyTemplate1<String>(new PAll("host"), "host", new EncodedHostName()) {

                @Override
                public Strategy create(String host) {
                    RemoteStrategy strategy = new RemoteStrategy();
                    strategy.setHost(host);
                    return strategy;
                }
            };
        case MINIMAX:
            return new MyTemplate5<Integer,Integer,List<Rule>,Rule,Integer>(
                new PSequence(new PNumber("heuristic-parameter-index"), new PLiteral(","),
                    new PNumber("maximum-search-depth"), new PLiteral(","),
                    new PSeparated(new PIdentifier("enabled-rule-names"), /*delimiter*/
                        new PLiteral(";", "enabled-rule-names")),
                    new PLiteral(","), new PIdentifier("start-max"), new PLiteral(","),
                    new PIdentifier("minmax-rule"), new PLiteral(","),
                    new PNumber("minmax-rule-parameter-index")),
                "heuristic-parameter-index", new EncodedInt(0, Integer.MAX_VALUE),
                "maximum-search-depth", new EncodedInt(0, Integer.MAX_VALUE), "enabled-rule-names",
                new EncodedRuleList(), "minmax-rule", new EncodedEnabledRule(),
                "minmax-rule-parameter-index", new EncodedInt(0, Integer.MAX_VALUE)) {

                @Override
                public Strategy create(Object[] arguments) {
                    Integer parindex = (Integer) arguments[0];
                    Integer searchdepth = (Integer) arguments[1];
                    @SuppressWarnings("unchecked") List<Rule> labels = (List<Rule>) arguments[2];
                    Rule minmaxrule = (Rule) arguments[3];
                    Integer minmaxparam = (Integer) arguments[4];
                    return new MinimaxStrategy(parindex, searchdepth, labels, minmaxrule,
                        minmaxparam);
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
    public final static EnumSet<StrategyValue> LTL_STRATEGIES =
        EnumSet.of(LTL, LTL_BOUNDED, LTL_POCKET);
    /** Set of strategies that can be selected from the exploration dialog. */
    public final static EnumSet<StrategyValue> DIALOG_STRATEGIES;
    /** Special mask for development strategies only. Treated specially. */
    public final static EnumSet<StrategyValue> DEVELOPMENT_ONLY_STRATEGIES =
        EnumSet.of(RETE, RETE_LINEAR, RETE_RANDOM, MINIMAX);

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
    abstract private class MyTemplate1<T1> extends Template1<Strategy,T1> {
        public MyTemplate1(SerializedParser parser, String name, EncodedType<T1,String> type) {
            super(StrategyValue.this, parser, name, type);
        }
    }

    /** Specialised 2-parameter template that uses the strategy value's keyword, name and description. */
    abstract private class MyTemplate2<T1,T2> extends Template2<Strategy,T1,T2> {
        public MyTemplate2(SerializedParser parser, String name1, EncodedType<T1,String> type1,
            String name2, EncodedType<T2,String> type2) {
            super(StrategyValue.this, parser, name1, type1, name2, type2);
        }
    }

    /** Specialised 5-parameter template that uses the strategy value's keyword, name and description. */
    abstract private class MyTemplate5<T1,T2,T3,T4,T5> extends TemplateN<Strategy> {
        public MyTemplate5(SerializedParser parser, String name1, EncodedType<T1,String> type1,
            String name2, EncodedType<T2,String> type2, String name3, EncodedType<T3,String> type3,
            String name4, EncodedType<T4,String> type4, String name5,
            EncodedType<T5,String> type5) {
            super(StrategyValue.this, parser, new String[] {name1, name2, name3, name4, name5},
                type1, type2, type3, type4, type5);

        }
    }
}
