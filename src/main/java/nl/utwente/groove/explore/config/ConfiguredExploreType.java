/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.explore.config;

import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.algebra.AlgebraFamily;
import nl.utwente.groove.explore.ExploreType;
import nl.utwente.groove.explore.config.parse.EdgeMapParser;
import nl.utwente.groove.explore.config.parse.EnabledRuleParser;
import nl.utwente.groove.explore.config.parse.RuleFormulaParser;
import nl.utwente.groove.explore.encode.EncodedPolarity;
import nl.utwente.groove.explore.encode.EncodedStopMode;
import nl.utwente.groove.explore.encode.Serialized;
import nl.utwente.groove.explore.engine.BeamPool;
import nl.utwente.groove.explore.engine.FrontierStrategy;
import nl.utwente.groove.explore.engine.Pool;
import nl.utwente.groove.explore.engine.QueuePool;
import nl.utwente.groove.explore.engine.RandomPool;
import nl.utwente.groove.explore.engine.StackPool;
import nl.utwente.groove.explore.result.Acceptor;
import nl.utwente.groove.explore.result.AnyStateAcceptor;
import nl.utwente.groove.explore.result.EdgeBoundCondition;
import nl.utwente.groove.explore.result.FinalStateAcceptor;
import nl.utwente.groove.explore.result.IsRuleApplicableCondition;
import nl.utwente.groove.explore.result.NoStateAcceptor;
import nl.utwente.groove.explore.result.NodeBoundCondition;
import nl.utwente.groove.explore.result.Predicate;
import nl.utwente.groove.explore.result.PredicateAcceptor;
import nl.utwente.groove.explore.strategy.LinearStrategy;
import nl.utwente.groove.explore.strategy.RandomLinearStrategy;
import nl.utwente.groove.explore.strategy.StopMode;
import nl.utwente.groove.explore.strategy.Strategy;
import nl.utwente.groove.grammar.Grammar;
import nl.utwente.groove.grammar.Rule;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.lts.GraphState;
import nl.utwente.groove.util.Exceptions;
import nl.utwente.groove.util.parse.FormatErrorSet;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Exploration type backed by an exploration configuration
 * ({@link ExploreConfig}). Instances are created by
 * {@link ExploreTypeConverter#toExploreType}, which also computes the
 * equivalent legacy strategy/acceptor descriptors (used for display and for
 * the deprecated keyword-based interfaces). In contrast to the base class,
 * the strategy and acceptor are instantiated <i>directly</i> — the
 * {@link FrontierStrategy} engine for the search orders, the acceptor
 * classes for the goals — rather than through the encode/enumerator
 * machinery, so a configuration-based exploration no longer depends on the
 * template parsing at run time.
 * @author Arend Rensink
 * @version $Revision$
 */
public class ConfiguredExploreType extends ExploreType {
    /**
     * Constructs an exploration type for a given configuration, with the
     * legacy descriptors realising it.
     */
    ConfiguredExploreType(ExploreConfig config, Serialized strategy, Serialized acceptor,
                          int bound) {
        super(strategy, acceptor, bound);
        this.config = config;
    }

    /** Returns the exploration configuration underlying this type. */
    public ExploreConfig getConfig() {
        return this.config;
    }

    private final ExploreConfig config;

    /**
     * Applies or verifies the per-GTS features of the configuration:
     * collapse mode, algebra family and persistence. These determine what
     * the state space <i>is</i>, so they must be constant for the lifetime
     * of the GTS. On a fresh GTS they are applied; on an explored GTS
     * (i.e., when continuing) they are verified against the recorded
     * values, and any deviation is an error — a fresh state space (Restart)
     * is needed to change them. On a successful verification the
     * operational persistence switch is re-engaged, since trace retention
     * flips it back on at the end of an unstored run.
     */
    @Override
    public void prepareGTS(GTS gts) throws FormatException {
        if (gts.isFresh()) {
            var collapse = getCollapseMode();
            if (collapse != null) {
                gts.setCollapseMode(collapse);
            }
            var algebra = getAlgebraFamily();
            if (algebra != null) {
                gts.setAlgebraFamily(algebra);
            }
            gts.setPersistent(isPersistent());
        } else {
            checkGTS(gts).throwException();
            gts.setStoring(gts.isPersistent());
        }
    }

    /**
     * Checks the per-GTS features of this configuration against the values
     * recorded in a given (explored) GTS. A non-empty result means the
     * configuration cannot continue the exploration of this GTS; a fresh
     * state space is needed.
     */
    public FormatErrorSet checkGTS(GTS gts) {
        var errors = new FormatErrorSet();
        var collapse = getCollapseMode();
        if (collapse == null) {
            // inherit the grammar-determined mode, as a fresh GTS would
            collapse = gts.getGrammar().getProperties().isCheckIsomorphism()
                ? GTS.CollapseMode.COLLAPSE_ISO_STRONG
                : GTS.CollapseMode.COLLAPSE_EQUAL;
        }
        if (collapse != gts.getCollapseMode()) {
            errors
                .add("Continuing cannot change the state collapse condition"
                    + " of the explored state space; use Restart");
        }
        var algebra = getAlgebraFamily();
        if (algebra == null) {
            algebra = gts.getGrammar().getProperties().getAlgebraFamily();
        }
        if (algebra != gts.getAlgebraFamily()) {
            errors
                .add("Continuing cannot change the algebra family"
                    + " of the explored state space; use Restart");
        }
        if (isPersistent() != gts.isPersistent()) {
            errors
                .add("Continuing cannot change the state persistence"
                    + " of the explored state space; use Restart");
        }
        return errors;
    }

    /** Resolves the collapse feature of the configuration to a collapse
     * mode; {@code null} means the grammar-determined mode. */
    private GTS.@Nullable CollapseMode getCollapseMode() {
        return switch ((Collapse) getConfig().getKind(ExploreKey.COLLAPSE)) {
        case GRAMMAR, HASH -> null;
        case EQUALITY -> GTS.CollapseMode.COLLAPSE_EQUAL;
        case ISOMORPHISM -> GTS.CollapseMode.COLLAPSE_ISO_STRONG;
        };
    }

    /** Resolves the algebra feature of the configuration to an algebra
     * family; {@code null} means the grammar's family. */
    private @Nullable AlgebraFamily getAlgebraFamily() {
        return switch ((Algebra) getConfig().getKind(ExploreKey.ALGEBRA)) {
        case GRAMMAR -> null;
        case DEFAULT -> AlgebraFamily.DEFAULT;
        case BIG -> AlgebraFamily.BIG;
        case POINT -> AlgebraFamily.POINT;
        case TERM -> AlgebraFamily.TERM;
        };
    }

    /** Indicates if the configuration's persistence feature stores states. */
    private boolean isPersistent() {
        return getConfig().getKind(ExploreKey.PERSISTENCE) == Persistence.ALL;
    }

    /**
     * Instantiates the strategy directly from the legacy descriptor computed
     * by the converter: the descriptor's keyword and arguments are the single
     * source of truth for what the configuration means, shared with the
     * (deprecated) enumerator-based instantiation path.
     */
    @Override
    public Strategy getParsedStrategy(Grammar grammar) throws FormatException {
        Serialized strategy = getStrategy();
        return switch (strategy.getKeyword()) {
        case "bfs" -> new FrontierStrategy(new QueuePool(getIntArgument(strategy, "bound")));
        case "dfs" -> new FrontierStrategy(new StackPool(getIntArgument(strategy, "bound")));
        case "linear" -> new LinearStrategy();
        case "random" -> new RandomLinearStrategy();
        case "random-frontier" -> new FrontierStrategy(new RandomPool());
        case "beam" -> {
            BeamPool.Order order = switch (strategy.getArgument("next")) {
            case "oldest" -> BeamPool.Order.OLDEST;
            case "newest" -> BeamPool.Order.NEWEST;
            case "random" -> BeamPool.Order.RANDOM;
            default -> throw Exceptions
                .illegalState("Converter produced unknown next-state selection '%s'",
                              strategy.getArgument("next"));
            };
            yield new FrontierStrategy(new BeamPool(order, getIntArgument(strategy, "size")));
        }
        case "cnbound" -> new FrontierStrategy(StopMode.UP_TO,
            new NodeBoundCondition(getIntArgument(strategy, "node-bound")), new QueuePool(0));
        case "cebound" -> new FrontierStrategy(StopMode.UP_TO,
            new EdgeBoundCondition(EdgeMapParser
                .parse(grammar, strategy.getArgument("edge-bound"))),
            new QueuePool(0));
        case "uptorule" -> {
            Rule rule = EnabledRuleParser.parse(grammar, strategy.getArgument("rule"));
            boolean polarity
                = EncodedPolarity.POSITIVE.equals(strategy.getArgument("polarity"));
            StopMode stopMode = EncodedStopMode.UP_TO_KEY.equals(strategy.getArgument("stop"))
                ? StopMode.UP_TO
                : StopMode.INCLUDE;
            Pool pool = "bfs".equals(strategy.getArgument("search"))
                ? new QueuePool(getIntArgument(strategy, "bound"))
                : new StackPool(getIntArgument(strategy, "bound"));
            yield new FrontierStrategy(stopMode, new IsRuleApplicableCondition(rule, polarity),
                pool);
        }
        default -> throw Exceptions
            .illegalState("Converter produced unknown strategy keyword '%s'",
                          strategy.getKeyword());
        };
    }

    /**
     * Instantiates the acceptor directly from the legacy descriptor computed
     * by the converter.
     */
    @Override
    public Acceptor getParsedAcceptor(Grammar grammar) throws FormatException {
        Serialized acceptor = getAcceptor();
        return switch (acceptor.getKeyword()) {
        case "final" -> FinalStateAcceptor.PROTOTYPE;
        case "none" -> NoStateAcceptor.INSTANCE;
        case "any" -> AnyStateAcceptor.PROTOTYPE;
        case "inv" -> {
            Rule rule = EnabledRuleParser.parse(grammar, acceptor.getArgument("rule"));
            Predicate<GraphState> predicate = new Predicate.RuleApplicable(rule);
            if (EncodedPolarity.NEGATIVE.equals(acceptor.getArgument("polarity"))) {
                predicate = new Predicate.Not<>(predicate);
            }
            yield new PredicateAcceptor(predicate);
        }
        case "formula" -> new PredicateAcceptor(RuleFormulaParser
            .parse(grammar, acceptor.getArgument("formula")));
        case "ruleapp" -> new PredicateAcceptor(new Predicate.ActionApplied(EnabledRuleParser
            .parse(grammar, acceptor.getArgument("rule"))));
        default -> throw Exceptions
            .illegalState("Converter produced unknown acceptor keyword '%s'",
                          acceptor.getKeyword());
        };
    }

    /** Retrieves a numeric argument of a legacy descriptor. */
    private static int getIntArgument(Serialized serialized, String name) {
        return Integer.parseInt(serialized.getArgument(name));
    }
}
