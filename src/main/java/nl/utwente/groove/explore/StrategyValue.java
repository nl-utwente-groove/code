package nl.utwente.groove.explore;

import static nl.utwente.groove.explore.strategy.ClosingStrategy.ConditionMoment.AFTER;
import static nl.utwente.groove.explore.strategy.ClosingStrategy.ConditionMoment.AT;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import nl.utwente.groove.explore.encode.EncodedBoundary;
import nl.utwente.groove.explore.encode.EncodedEdgeMap;
import nl.utwente.groove.explore.encode.EncodedEnabledRule;
import nl.utwente.groove.explore.encode.EncodedHostName;
import nl.utwente.groove.explore.encode.EncodedInt;
import nl.utwente.groove.explore.encode.EncodedLtlProperty;
import nl.utwente.groove.explore.encode.EncodedRuleList;
import nl.utwente.groove.explore.encode.EncodedRuleMode;
import nl.utwente.groove.explore.encode.EncodedType;
import nl.utwente.groove.explore.encode.Serialized;
import nl.utwente.groove.explore.encode.Template;
import nl.utwente.groove.explore.encode.Template.Template0;
import nl.utwente.groove.explore.encode.Template.Template1;
import nl.utwente.groove.explore.encode.Template.Template2;
import nl.utwente.groove.explore.encode.Template.TemplateN;
import nl.utwente.groove.explore.prettyparse.PAll;
import nl.utwente.groove.explore.prettyparse.PChoice;
import nl.utwente.groove.explore.prettyparse.PIdentifier;
import nl.utwente.groove.explore.prettyparse.PLiteral;
import nl.utwente.groove.explore.prettyparse.PNumber;
import nl.utwente.groove.explore.prettyparse.POptional;
import nl.utwente.groove.explore.prettyparse.PSeparated;
import nl.utwente.groove.explore.prettyparse.PSequence;
import nl.utwente.groove.explore.prettyparse.SerializedParser;
import nl.utwente.groove.explore.result.EdgeBoundCondition;
import nl.utwente.groove.explore.result.IsRuleApplicableCondition;
import nl.utwente.groove.explore.result.NodeBoundCondition;
import nl.utwente.groove.explore.strategy.BFSStrategy;
import nl.utwente.groove.explore.strategy.Boundary;
import nl.utwente.groove.explore.strategy.BoundedLTLStrategy;
import nl.utwente.groove.explore.strategy.BoundedPocketLTLStrategy;
import nl.utwente.groove.explore.strategy.DFSStrategy;
import nl.utwente.groove.explore.strategy.ExploreStateStrategy;
import nl.utwente.groove.explore.strategy.LTLStrategy;
import nl.utwente.groove.explore.strategy.LinearStrategy;
import nl.utwente.groove.explore.strategy.MinimaxStrategy;
import nl.utwente.groove.explore.strategy.RandomLinearStrategy;
import nl.utwente.groove.explore.strategy.RemoteStrategy;
import nl.utwente.groove.explore.strategy.ReteLinearStrategy;
import nl.utwente.groove.explore.strategy.ReteRandomLinearStrategy;
import nl.utwente.groove.explore.strategy.ReteStrategy;
import nl.utwente.groove.explore.strategy.Strategy;
import nl.utwente.groove.grammar.Rule;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.grammar.type.TypeLabel;

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
            + "The transition of choice will be the same within one " + "incarnation of Groove."),
    /** Random linear strategy. */
    RANDOM("random", "Random Linear Exploration",
        "This strategy chooses one transition from each open state. "
            + "The transition is chosen randomly."),
    /** Single-state strategy. */
    STATE("state", "Single-State Exploration", "This strategy fully explores the current state."),
    /** Depth-first RETE strategy. */
    RETE("rete", "Rete Strategy (DFS based)",
        "This strategy finds all possible transitions from the Rete "
            + "network, and continues in a depth-first fashion using "
            + "virtual events when possible. Rete updates are applied " + "accumulatively"),
    /** Linear RETE strategy. */
    RETE_LINEAR("retelinear", "Rete Linear Exploration",
        "This strategy chooses one transition from each open state. "
            + "The transition of choice will be the same within one " + "incarnation of Groove."),
    /** Random linear RETE strategy. */
    RETE_RANDOM("reterandom", "Rete Random Linear Exploration",
        "This strategy chooses one transition from each open state. "
            + "The transition is chosen randomly."),
    /** BFS strategy up to and including a rule-based condition. */
    BFS_UNTIL_RULE("bfsurule", "BFS Exploration Until Rule Application",
        "This strategy performs a conditional breadth-first exploration. "
            + "If a given rule is applicable in a newly reached state, its successors are not explored. "
            + "The rule does not have to be scheduled to be used for this purpose. "
            + "All other states are explored normally."),
    /** BFS strategy up to and including a rule-based condition. */
    DFS_UNTIL_RULE("dfsurule", "DFS Exploration Until Rule Application",
        "This strategy performs a conditional depth-first exploration. "
            + "If a given rule is applicable in a newly reached state, its successors are not explored. "
            + "The rule does not have to be scheduled to be used for this purpose. "
            + "All other states are explored normally."),
    /** BFS strategy up to (and not including) a rule-based condition. */
    BFS_UPTO_RULE("crule", "BFS Exploration Up To Rule Application",
        "This strategy performs a conditional breadth-first exploration. "
            + "If a given rule is applicable in a newly reached state, the state is not explored. "
            + "The rule does not have to be scheduled to be used for this purpose. "
            + "All other states are explored normally."),
    /** BFS strategy up to (and not including) a number of nodes. */
    BFS_UPTO_NCOUNT("cnbound", "BFS Exploration Up To Node Bound",
        "This strategy performs a conditional breadth-first exploration. "
            + "If the number of nodes in a newly reached state exceeds a "
            + "given bound, it is not explored. " + "All other states are explored normally."),
    /** BFS strategy up to (and not including) a number of edges. */
    BFS_UPTO_ECOUNT("cebound", "BFS Exploration Up To Edge Bound",
        "This strategy performs a conditional breadth-first exploration. "
            + "If the number of edges in a newly reached state exceeds a "
            + "given bound, it is not explored. " + "All other states are explored normally."),
    /** LTL model checking strategy. */
    LTL("ltl", "LTL Model Checking", "Nested Depth-First Search for a given LTL formula."),
    /** Bounded LTL model checking  strategy. */
    LTL_BOUNDED("ltlbounded", "Bounded LTL Model Checking",
        "Nested Depth-First Search for a given LTL formula,"
            + "using incremental bounds based on graph size or rule applications"),
    /** Bounded LTL model checking strategy. */
    LTL_POCKET("ltlpocket", "Pocket LTL Model Checking",
        "Nested Depth-First Search for a given LTL formula,"
            + "using incremental bounds based on graph size or rule applications"
            + "and optimised to avoid reexploring connected components ('pockets')"),
    /** Minimax strategy. */
    MINIMAX("minimax", "Minimax Strategy Generation",
        "This strategy generates a strategy for a two-player game."),
    /** Remote strategy. */
    REMOTE("remote", "Remote Exploration",
        "This strategy sends the result as an STS to a remote server.");

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
        return exploration.getStrategy().getKeyword().equals(getKeyword());
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

        case BFS_UNTIL_RULE:
            return new MyTemplate2<>(
                new PSequence(
                    new POptional("!", "mode", EncodedRuleMode.NEGATIVE, EncodedRuleMode.POSITIVE),
                    new PIdentifier("rule")),
                "rule", new EncodedEnabledRule(), "mode", new EncodedRuleMode()) {

                @Override
                public Strategy create(Rule rule, Boolean mode) {
                    return new BFSStrategy(AFTER, new IsRuleApplicableCondition(rule, mode));
                }
            };

        case DFS_UNTIL_RULE:
            return new MyTemplate2<>(
                new PSequence(
                    new POptional("!", "mode", EncodedRuleMode.NEGATIVE, EncodedRuleMode.POSITIVE),
                    new PIdentifier("rule")),
                "rule", new EncodedEnabledRule(), "mode", new EncodedRuleMode()) {

                @Override
                public Strategy create(Rule rule, Boolean mode) {
                    return new DFSStrategy(AFTER, new IsRuleApplicableCondition(rule, mode));
                }
            };

        case BFS_UPTO_RULE:
            return new MyTemplate2<>(
                new PSequence(
                    new POptional("!", "mode", EncodedRuleMode.NEGATIVE, EncodedRuleMode.POSITIVE),
                    new PIdentifier("rule")),
                "rule", new EncodedEnabledRule(), "mode", new EncodedRuleMode()) {

                @Override
                public Strategy create(Rule rule, Boolean mode) {
                    return new BFSStrategy(AT, new IsRuleApplicableCondition(rule, mode));
                }
            };

        case BFS_UPTO_NCOUNT:
            return new MyTemplate1<>(new PNumber("node-bound"), "node-bound",
                new EncodedInt(0, -1)) {

                @Override
                public Strategy create(Integer bound) {
                    return new BFSStrategy(AT, new NodeBoundCondition(bound));
                }
            };

        case BFS_UPTO_ECOUNT:
            return new MyTemplate1<>(new PSeparated(new PSequence(new PIdentifier("edge-bound"),
                new PLiteral(">", "edge-bound"), new PNumber("edge-bound")),
                new PLiteral(",", "edge-bound")), "edge-bound", new EncodedEdgeMap()) {

                @Override
                public Strategy create(Map<TypeLabel,Integer> bounds) {
                    return new BFSStrategy(AT, new EdgeBoundCondition(bounds));
                }
            };

        case LTL:
            return new MyTemplate1<>(new PAll("prop"), "prop", new EncodedLtlProperty()) {
                @Override
                public Strategy create(String property) {
                    LTLStrategy result = new LTLStrategy();
                    result.setProperty(property);
                    return result;
                }
            };

        case LTL_BOUNDED:
            SerializedParser boundParser
                = new PSeparated(new PChoice(new PIdentifier("rule"), new PNumber("value")),
                    new PLiteral(",", "comma"));
            SerializedParser parser
                = new PSequence(boundParser, new PLiteral(";", "semi"), new PAll("prop"));
            return new MyTemplate2<>(parser, "prop", new EncodedLtlProperty(), "bound",
                new EncodedBoundary()) {
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
            return new MyTemplate2<>(parser, "prop", new EncodedLtlProperty(), "bound",
                new EncodedBoundary()) {
                @Override
                public Strategy create(String property, Boundary bound) {
                    BoundedLTLStrategy result = new BoundedPocketLTLStrategy();
                    result.setProperty(property);
                    result.setBoundary(bound);
                    return result;
                }
            };
        case REMOTE:
            return new MyTemplate1<>(new PAll("host"), "host", new EncodedHostName()) {

                @Override
                public Strategy create(String host) {
                    RemoteStrategy strategy = new RemoteStrategy();
                    strategy.setHost(host);
                    return strategy;
                }
            };
        case MINIMAX:
            return new MyTemplate5<>(
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
                    @SuppressWarnings("unchecked")
                    List<Rule> labels = (List<Rule>) arguments[2];
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
    public final static EnumSet<StrategyValue> LTL_STRATEGIES
        = EnumSet.of(LTL, LTL_BOUNDED, LTL_POCKET);
    /** Set of strategies that can be selected from the exploration dialog. */
    public final static EnumSet<StrategyValue> DIALOG_STRATEGIES;
    /** Special mask for development strategies only. Treated specially. */
    public final static EnumSet<StrategyValue> DEVELOPMENT_ONLY_STRATEGIES
        = EnumSet.of(RETE, RETE_LINEAR, RETE_RANDOM, MINIMAX);

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
                           String name2, EncodedType<T2,String> type2, String name3,
                           EncodedType<T3,String> type3, String name4, EncodedType<T4,String> type4,
                           String name5, EncodedType<T5,String> type5) {
            super(StrategyValue.this, parser, new String[] {name1, name2, name3, name4, name5},
                  type1, type2, type3, type4, type5);

        }
    }
}
