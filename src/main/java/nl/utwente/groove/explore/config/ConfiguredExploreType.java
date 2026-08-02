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
 * {@link ExploreTypeConverter#toExploreType}, which validates the
 * configuration first. The strategy and acceptor are instantiated directly
 * from the configuration — the {@link FrontierStrategy} engine for the
 * search orders, the acceptor classes for the goals.
 * @author Arend Rensink
 * @version $Revision$
 */
public class ConfiguredExploreType extends ExploreType {
    /**
     * Constructs an exploration type for a given (validated) configuration.
     */
    ConfiguredExploreType(ExploreConfig config, int bound) {
        super(bound);
        this.config = config;
    }

    /** Returns the exploration configuration underlying this type. */
    public ExploreConfig getConfig() {
        return this.config;
    }

    private final ExploreConfig config;

    @Override
    public String getIdentifier() {
        String result = getConfig().unparse();
        return result.isEmpty()
            ? "default"
            : result;
    }

    @Override
    public ExploreType withResultCount(int count) {
        var newConfig = new ExploreConfig(getConfig());
        newConfig.put(ExploreKey.COUNT, Count.toSetting(count));
        return new ConfiguredExploreType(newConfig, count);
    }

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
     * Instantiates the strategy directly from the configuration, which is
     * the single source of truth for what the exploration means. The
     * converter has validated the configuration, so unrealisable
     * combinations do not reach this method; the legacy descriptors of the
     * base class serve display purposes only.
     */
    @Override
    public Strategy getParsedStrategy(Grammar grammar) throws FormatException {
        var config = getConfig();
        // a single-state frontier is a linear walk
        if (config.getKind(ExploreKey.FRONTIER) == Frontier.SINGLE) {
            return config.getKind(ExploreKey.SUCCESSOR) == Successor.SINGLE_RANDOM
                ? new RandomLinearStrategy()
                : new LinearStrategy();
        }
        // the bound feature determines the strategy variant
        Object boundContent = config.get(ExploreKey.BOUND).content();
        return switch ((Bound) config.getKind(ExploreKey.BOUND)) {
        case NONE, COST -> new FrontierStrategy(getPool());
        case NODES -> new FrontierStrategy(StopMode.UP_TO,
            new NodeBoundCondition(((Bound.Limit) boundContent).max()), new QueuePool(0));
        case EDGES -> new FrontierStrategy(StopMode.UP_TO,
            new EdgeBoundCondition(EdgeMapParser.parse(grammar, (String) boundContent)),
            new QueuePool(0));
        case UPTO, INCLUDE -> {
            String condition = (String) boundContent;
            boolean polarity = !condition.startsWith("!");
            Rule rule = EnabledRuleParser
                .parse(grammar, polarity
                    ? condition
                    : condition.substring(1));
            StopMode stopMode = config.getKind(ExploreKey.BOUND) == Bound.UPTO
                ? StopMode.UP_TO
                : StopMode.INCLUDE;
            yield new FrontierStrategy(stopMode, new IsRuleApplicableCondition(rule, polarity),
                getPool());
        }
        case SIZE -> throw Exceptions.illegalState("Unrealisable bound kind passed validation");
        };
    }

    /**
     * Computes the pool realising the frontier and next-state features,
     * with the depth bound (if any) folded in.
     */
    private Pool getPool() {
        var config = getConfig();
        var next = (NextState) config.getKind(ExploreKey.NEXT);
        if (config.getKind(ExploreKey.FRONTIER) == Frontier.BEAM) {
            BeamPool.Order order = switch (next) {
            case OLDEST -> BeamPool.Order.OLDEST;
            case NEWEST -> BeamPool.Order.NEWEST;
            case RANDOM -> BeamPool.Order.RANDOM;
            };
            return new BeamPool(order, (Integer) config.get(ExploreKey.FRONTIER).content());
        }
        return switch (next) {
        case OLDEST -> new QueuePool(getDepthBound());
        case NEWEST -> new StackPool(getDepthBound());
        case RANDOM -> new RandomPool();
        };
    }

    /** Returns the depth bound of the configuration; {@code 0} means unbounded. */
    private int getDepthBound() {
        var config = getConfig();
        return config.getKind(ExploreKey.BOUND) == Bound.COST
            ? ((Bound.Limit) config.get(ExploreKey.BOUND).content()).max()
            : 0;
    }

    /**
     * Instantiates the acceptor directly from the configuration's goal and
     * outcome features.
     */
    @Override
    public Acceptor getParsedAcceptor(Grammar grammar) throws FormatException {
        var config = getConfig();
        boolean satisfy = config.getKind(ExploreKey.OUTCOME) == Outcome.SATISFY;
        return switch ((Goal) config.getKind(ExploreKey.GOAL)) {
        case NONE -> NoStateAcceptor.INSTANCE;
        case ANY -> AnyStateAcceptor.PROTOTYPE;
        case FINAL -> FinalStateAcceptor.PROTOTYPE;
        case FIRES -> new PredicateAcceptor(new Predicate.ActionApplied(EnabledRuleParser
            .parse(grammar, (String) config.get(ExploreKey.GOAL).content())));
        case CONDITION -> {
            String condition = (String) config.get(ExploreKey.GOAL).content();
            Predicate<GraphState> predicate = RuleFormulaParser.parse(grammar, condition);
            if (!satisfy) {
                predicate = new Predicate.Not<>(predicate);
            }
            yield new PredicateAcceptor(predicate);
        }
        case GRAPH, LTL, CTL -> throw Exceptions
            .illegalState("Unrealisable goal kind passed validation");
        };
    }
}
