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

import static nl.utwente.groove.explore.result.Goal.anyState;
import static nl.utwente.groove.explore.result.Goal.finalState;
import static nl.utwente.groove.explore.result.Goal.state;
import static nl.utwente.groove.explore.result.Goal.transition;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.algebra.AlgebraFamily;
import nl.utwente.groove.explore.ExploreType;
import nl.utwente.groove.explore.config.parse.EdgeMapParser;
import nl.utwente.groove.explore.config.parse.EnabledRuleParser;
import nl.utwente.groove.explore.config.parse.RuleFormulaParser;
import nl.utwente.groove.explore.engine.BeamPool;
import nl.utwente.groove.explore.engine.ExploreStateStrategy;
import nl.utwente.groove.explore.engine.FrontierStrategy;
import nl.utwente.groove.explore.engine.LinearStrategy;
import nl.utwente.groove.explore.engine.Pool;
import nl.utwente.groove.explore.engine.QueuePool;
import nl.utwente.groove.explore.engine.RandomLinearStrategy;
import nl.utwente.groove.explore.engine.RandomPool;
import nl.utwente.groove.explore.engine.StackPool;
import nl.utwente.groove.explore.engine.StopMode;
import nl.utwente.groove.explore.engine.Strategy;
import nl.utwente.groove.explore.result.Acceptor;
import nl.utwente.groove.explore.result.Predicate;
import nl.utwente.groove.grammar.Grammar;
import nl.utwente.groove.grammar.Rule;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.lts.GraphState;
import nl.utwente.groove.util.AIGenerated;
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
@NonNullByDefault
public class ConfiguredExploreType extends ExploreType {
    /**
     * Constructs an exploration type for a given (validated) configuration
     * and the baseline traversal derived from it by the converter.
     */
    ConfiguredExploreType(ExploreConfig config, int count, Traversal traversal) {
        super(count);
        this.config = config;
        this.traversal = traversal;
    }

    /**
     * Returns the single-state exploration type for a given GTS: only the
     * initial (i.e., currently selected) state is fully explored, and no
     * results are collected. The per-GTS features (persistence, collapse,
     * algebra) are tuned to the given GTS, so that the returned type can run
     * on it whatever features it was originally explored under. This is the
     * exploration behind the simulator's explore-state action; the tuning is
     * the reverse of the feature resolution in {@link #prepareGTS} and
     * {@link #checkGTS}, and must stay in step with it.
     */
    @AIGenerated("Claude Fable 5, 2026-08")
    public static ExploreType stateExploration(GTS gts) {
        var config = new ExploreConfig();
        config.put(ExploreKey.BOUND, Bound.INITIAL.createSetting());
        config.put(ExploreKey.GOAL, Goal.NONE.createSetting());
        var properties = gts.getGrammar().getProperties();
        if (!gts.isPersistent()) {
            config.put(ExploreKey.PERSISTENCE, Persistence.NONE.createSetting());
        }
        var mode = gts.getCollapseMode();
        if (mode != GTS.CollapseMode.ofProperties(properties)) {
            var collapse = switch (mode) {
            case COLLAPSE_EQUAL -> Collapse.EQUALITY;
            case COLLAPSE_ISO_STRONG -> Collapse.ISOMORPHISM;
            // a mode deviating from the grammar-determined one can only have
            // been set by a configuration, which admits no other modes
            case COLLAPSE_NONE, COLLAPSE_ISO_WEAK -> throw Exceptions
                .illegalState("Unexpected GTS collapse mode %s", mode);
            };
            config.put(ExploreKey.COLLAPSE, collapse.createSetting());
        }
        var family = gts.getAlgebraFamily();
        if (family != properties.getAlgebraFamily()) {
            var algebra = switch (family) {
            case DEFAULT -> Algebra.DEFAULT;
            case BIG -> Algebra.BIG;
            case POINT -> Algebra.POINT;
            case TERM -> Algebra.TERM;
            };
            config.put(ExploreKey.ALGEBRA, algebra.createSetting());
        }
        try {
            return ExploreTypeConverter.toExploreType(config);
        } catch (FormatException exc) {
            throw Exceptions
                .illegalState("Single-state exploration configuration is unrealisable: %s",
                              exc.getMessage());
        }
    }

    /** Returns the exploration configuration underlying this type. */
    public ExploreConfig getConfig() {
        return this.config;
    }

    private final ExploreConfig config;

    /** The baseline traversal realising the next-state, successor and
     * frontier features, derived by {@link ExploreTypeConverter}. */
    private final Traversal traversal;

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
        try {
            // revalidate through the converter: the count feature interacts
            // with the goal (goal 'none' only admits counting all results)
            return ExploreTypeConverter.toExploreType(newConfig);
        } catch (FormatException exc) {
            throw Exceptions
                .illegalArg("Result count %s is inconsistent with exploration '%s': %s", count,
                            getIdentifier(), exc.getMessage());
        }
    }

    @Override
    public boolean presentsResultAsTraces() {
        return getConfig().getKind(ExploreKey.SHAPE) == Shape.TRACE;
    }

    /**
     * Applies the per-GTS features of the configuration: collapse mode,
     * algebra family and persistence. These determine what the state space
     * <i>is</i>, so they must be constant for the lifetime of the GTS;
     * they can only be applied to a fresh GTS (asserted by the super
     * implementation).
     */
    @Override
    public void prepareGTS(GTS gts) {
        super.prepareGTS(gts);
        var collapse = getCollapseMode();
        if (collapse != null) {
            gts.setCollapseMode(collapse);
        }
        var algebra = getAlgebraFamily();
        if (algebra != null) {
            gts.setAlgebraFamily(algebra);
        }
        gts.setPersistent(isPersistent());
    }

    /**
     * Verifies the per-GTS features of the configuration against the values
     * recorded in the GTS: any deviation is an error — a fresh state space
     * (Restart) is needed to change them. On a successful verification the
     * operational persistence switch is re-engaged, since trace retention
     * flips it back on at the end of an unstored run.
     */
    @Override
    public void prepareRun(GTS gts) throws FormatException {
        checkGTS(gts).throwException();
        gts.setStoring(gts.isPersistent());
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
            collapse = GTS.CollapseMode.ofProperties(gts.getGrammar().getProperties());
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
     * Realises the configuration, collecting the errors of both the
     * strategy and the acceptor half before failing, so that all problems
     * are reported at once.
     */
    @Override
    public Realisation realise(Grammar grammar) throws FormatException {
        var errors = new FormatErrorSet();
        Strategy strategy = null;
        try {
            strategy = createStrategy(grammar);
        } catch (FormatException exc) {
            errors.addAll(exc.getErrors());
        }
        Acceptor acceptor = null;
        try {
            acceptor = createAcceptor(grammar);
        } catch (FormatException exc) {
            errors.addAll(exc.getErrors());
        }
        errors.throwException();
        assert strategy != null && acceptor != null;
        return new Realisation(strategy, acceptor);
    }

    /**
     * Instantiates the strategy from the configuration and the baseline
     * traversal that the converter derived from it while validating, so that
     * validation and instantiation cannot diverge. Unrealisable combinations
     * do not reach this method.
     */
    private Strategy createStrategy(Grammar grammar) throws FormatException {
        var config = getConfig();
        // an initial-state bound is realised by the dedicated single-state
        // strategy, which handles transient (in-recipe) successors correctly;
        // the traversal is irrelevant, as no state beyond the initial one is
        // ever scheduled
        if (config.getKind(ExploreKey.BOUND) == Bound.INITIAL) {
            return new ExploreStateStrategy();
        }
        // a single-state frontier is a linear walk
        if (this.traversal == Traversal.LINEAR) {
            return config.getKind(ExploreKey.SUCCESSOR) == Successor.SINGLE_RANDOM
                ? new RandomLinearStrategy()
                : new LinearStrategy();
        }
        // the bound feature determines the strategy variant
        Object boundContent = config.get(ExploreKey.BOUND).content();
        return switch ((Bound) config.getKind(ExploreKey.BOUND)) {
        case NONE, COST -> new FrontierStrategy(getPool());
        case NODES -> new FrontierStrategy(StopMode.UP_TO,
            new Predicate.NodeBoundExceeded(((Bound.Limit) boundContent).max()), getPool());
        case EDGES -> new FrontierStrategy(StopMode.UP_TO,
            new Predicate.EdgeBoundExceeded(EdgeMapParser.parse(grammar, (String) boundContent)),
            getPool());
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
            Predicate<GraphState> stopCondition = new Predicate.RuleApplicable(rule);
            if (!polarity) {
                stopCondition = new Predicate.Not<>(stopCondition);
            }
            yield new FrontierStrategy(stopMode, stopCondition, getPool());
        }
        case SIZE -> throw Exceptions.illegalState("Unrealisable bound kind passed validation");
        case INITIAL -> throw Exceptions
            .illegalState("Initial-state bound is realised before the traversal dispatch");
        };
    }

    /**
     * Computes the pool realising the baseline traversal, with the depth
     * bound (if any) folded in. The converter guarantees that a depth bound
     * only occurs with the breadth- and depth-first traversals.
     */
    private Pool getPool() {
        var config = getConfig();
        return switch (this.traversal) {
        case LINEAR -> throw Exceptions.illegalState("A linear traversal has no frontier pool");
        case BFS -> new QueuePool(getDepthBound());
        case DFS -> new StackPool(getDepthBound());
        case RANDOM -> new RandomPool();
        case BEAM -> {
            BeamPool.Order order = switch ((NextState) config.getKind(ExploreKey.NEXT)) {
            case OLDEST -> BeamPool.Order.OLDEST;
            case NEWEST -> BeamPool.Order.NEWEST;
            case RANDOM -> BeamPool.Order.RANDOM;
            };
            yield new BeamPool(order, (Integer) config.get(ExploreKey.FRONTIER).content());
        }
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
     * Instantiates a fresh acceptor directly from the configuration's goal,
     * outcome and count features.
     */
    private Acceptor createAcceptor(Grammar grammar) throws FormatException {
        var config = getConfig();
        boolean satisfy = config.getKind(ExploreKey.OUTCOME) == Outcome.SATISFY;
        return switch ((Goal) config.getKind(ExploreKey.GOAL)) {
        case NONE -> new Acceptor();
        case ANY -> new Acceptor(anyState(), getResultCount());
        case FINAL -> new Acceptor(finalState(), getResultCount());
        case FIRES -> new Acceptor(transition(new Predicate.ActionApplied(EnabledRuleParser
            .parse(grammar, (String) config.get(ExploreKey.GOAL).content()))), getResultCount());
        case CONDITION -> {
            String condition = (String) config.get(ExploreKey.GOAL).content();
            Predicate<GraphState> predicate = RuleFormulaParser.parse(grammar, condition);
            if (!satisfy) {
                predicate = new Predicate.Not<>(predicate);
            }
            yield new Acceptor(state(predicate), getResultCount());
        }
        case GRAPH, LTL, CTL -> throw Exceptions
            .illegalState("Unrealisable goal kind passed validation");
        };
    }
}
