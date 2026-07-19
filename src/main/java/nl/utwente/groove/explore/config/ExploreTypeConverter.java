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

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.explore.ExploreType;
import nl.utwente.groove.explore.encode.EncodedPolarity;
import nl.utwente.groove.explore.encode.Serialized;
import nl.utwente.groove.util.parse.FormatErrorSet;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Bridge between the exploration feature model ({@link ExploreConfig}) and the
 * legacy strategy/acceptor machinery ({@link ExploreType}), pending the
 * unification of the exploration engine. The bridge is deliberately partial in
 * both directions: an {@link ExploreConfig} using feature values the legacy
 * strategies cannot realise is rejected with an explanatory error, as is an
 * {@link ExploreType} whose strategy or acceptor has no feature-model
 * equivalent (the LTL strategies, the conditional and development strategies,
 * and the cycle acceptor).
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class ExploreTypeConverter {
    private ExploreTypeConverter() {
        // static utility class
    }

    /**
     * Converts an exploration configuration to the legacy exploration type
     * realising it.
     * @throws FormatException if the configuration is inconsistent (see
     * {@link ExploreConfig#check()}) or uses feature values that the legacy
     * strategies and acceptors cannot realise
     */
    public static ExploreType toExploreType(ExploreConfig config) throws FormatException {
        config.check().throwException();
        var errors = new FormatErrorSet();
        checkInexpressible(config, errors);
        int depthBound = getDepthBound(config, errors);
        Serialized strategy = computeStrategy(config, depthBound, errors);
        Serialized acceptor = computeAcceptor(config, errors);
        errors.throwException();
        assert strategy != null && acceptor != null;
        return new ExploreType(strategy, acceptor, getResultBound(config));
    }

    /** Collects errors for the feature values no legacy strategy can realise. */
    private static void checkInexpressible(ExploreConfig config, FormatErrorSet errors) {
        if (config.getKind(ExploreKey.HEURISTIC) != Heuristic.NONE) {
            errors.add("Heuristic search is not yet supported");
        }
        if (config.getKind(ExploreKey.COST) == Cost.RULE) {
            errors.add("Rule-based transition cost is not yet supported");
        }
        if (config.getKind(ExploreKey.RESULT) == Result.TRACE) {
            errors.add("Trace results are not yet supported");
        }
        if (config.getKind(ExploreKey.PERSISTENCE) != Persistence.ALL) {
            errors.add("Partial state persistence is not yet supported");
        }
        if (config.getKind(ExploreKey.COLLAPSE) != Collapse.GRAMMAR) {
            errors.add("Overriding the grammar's state collapse setting is not yet supported");
        }
        if (config.getKind(ExploreKey.ALGEBRA) != Algebra.GRAMMAR) {
            errors.add("Overriding the grammar's algebra family is not yet supported");
        }
        if (config.getKind(ExploreKey.FRONTIER) == Frontier.BEAM) {
            errors.add("Beam search is not yet supported");
        }
    }

    /**
     * Extracts the depth bound (in the legacy sense of {@code bfs} and
     * {@code dfs}) from the bound and cost features: a bound on the uniform
     * cost of a path is exactly a depth bound. All other bound settings are
     * not realisable.
     */
    private static int getDepthBound(ExploreConfig config, FormatErrorSet errors) {
        int result = 0;
        switch ((Bound) config.getKind(ExploreKey.BOUND)) {
        case NONE -> {
            // no bound
        }
        case COST -> {
            // consistency of cost != NONE is guaranteed by check()
            if (config.getKind(ExploreKey.COST) != Cost.UNIFORM) {
                errors.add("Only a uniform-cost (depth) bound is currently supported");
            } else if (config.get(ExploreKey.BOUND).content() instanceof Bound.Limit limit) {
                if (limit.increment() != 0) {
                    errors.add("Iterative deepening is not yet supported");
                } else {
                    result = limit.max();
                }
            }
        }
        case SIZE -> errors.add("A graph size bound is not yet supported");
        }
        return result;
    }

    /** Computes the serialised legacy strategy for a configuration. */
    private static @Nullable Serialized computeStrategy(ExploreConfig config, int depthBound,
                                                        FormatErrorSet errors) {
        var next = (NextState) config.getKind(ExploreKey.NEXT);
        var successor = (Successor) config.getKind(ExploreKey.SUCCESSOR);
        var rete = config.getKind(ExploreKey.MATCHER) == Matcher.RETE;
        String keyword = null;
        boolean boundSupported = false;
        if (config.getKind(ExploreKey.FRONTIER) == Frontier.SINGLE) {
            // linear search; the next-state selection is irrelevant
            switch (successor) {
            case SINGLE -> keyword = rete
                ? "retelinear"
                : "linear";
            case SINGLE_RANDOM -> keyword = rete
                ? "reterandom"
                : "random";
            case ALL, ALL_RANDOM -> errors
                .add("A single-state frontier requires single-successor generation");
            }
        } else {
            switch (successor) {
            case ALL -> {
                switch (next) {
                case OLDEST -> {
                    if (rete) {
                        errors.add("The RETE strategy only supports depth-first search");
                    } else {
                        keyword = "bfs";
                        boundSupported = true;
                    }
                }
                case NEWEST -> {
                    keyword = rete
                        ? "rete"
                        : "dfs";
                    boundSupported = !rete;
                }
                case RANDOM -> errors.add("Random next-state selection is not yet supported");
                }
            }
            case ALL_RANDOM -> errors
                .add("Randomised successor generation is not yet supported");
            case SINGLE, SINGLE_RANDOM -> errors
                .add("Single-successor generation with an unrestricted frontier"
                    + " is not yet supported");
            }
        }
        if (depthBound > 0 && keyword != null && !boundSupported) {
            errors.add("A depth bound is not supported for this feature combination");
        }
        Serialized result = null;
        if (keyword != null) {
            result = new Serialized(keyword);
            if (depthBound > 0 && boundSupported) {
                result.setArgument("bound", Integer.toString(depthBound));
            }
        }
        return result;
    }

    /** Computes the serialised legacy acceptor for a configuration. */
    private static @Nullable Serialized computeAcceptor(ExploreConfig config,
                                                        FormatErrorSet errors) {
        var satisfy = config.getKind(ExploreKey.OUTCOME) == Outcome.SATISFY;
        Serialized result = null;
        switch ((Goal) config.getKind(ExploreKey.GOAL)) {
        // the outcome for NONE, ANY and FINAL is guaranteed by check() to be SATISFY
        case NONE -> result = new Serialized("none");
        case ANY -> result = new Serialized("any");
        case FINAL -> result = new Serialized("final");
        case APPLIED -> {
            if (satisfy) {
                result = new Serialized("ruleapp");
                result.setArgument("rule", (String) config.get(ExploreKey.GOAL).content());
            } else {
                errors.add("A violated application goal is not yet supported");
            }
        }
        case RULE -> {
            result = new Serialized("inv");
            result.setArgument("rule", (String) config.get(ExploreKey.GOAL).content());
            result.setArgument("polarity", satisfy
                ? EncodedPolarity.POSITIVE
                : EncodedPolarity.NEGATIVE);
        }
        case FORMULA -> {
            if (satisfy) {
                result = new Serialized("formula");
                result.setArgument("formula", (String) config.get(ExploreKey.GOAL).content());
            } else {
                errors.add("A violated formula goal is not yet supported");
            }
        }
        case GRAPH -> errors.add("A graph goal is not yet supported");
        case LTL, CTL -> errors
            .add("Temporal goals are handled by the model checking actions,"
                + " not by exploration");
        }
        return result;
    }

    /** Computes the legacy result bound for a configuration. */
    private static int getResultBound(ExploreConfig config) {
        return switch ((Count) config.getKind(ExploreKey.COUNT)) {
        case ALL -> 0;
        case FIRST -> 1;
        case COUNT -> (Integer) config.get(ExploreKey.COUNT).content();
        };
    }

    /**
     * Converts a legacy exploration type to the equivalent exploration
     * configuration.
     * @throws FormatException if the strategy or acceptor of the exploration
     * type has no feature-model equivalent
     */
    public static ExploreConfig toConfig(ExploreType type) throws FormatException {
        var result = new ExploreConfig();
        var errors = new FormatErrorSet();
        Serialized strategy = type.getStrategy();
        switch (strategy.getKeyword()) {
        case "bfs" -> setDepthBound(result, strategy, errors);
        case "dfs" -> {
            result.put(ExploreKey.NEXT, NextState.NEWEST.createSetting());
            setDepthBound(result, strategy, errors);
        }
        case "linear" -> {
            result.put(ExploreKey.FRONTIER, Frontier.SINGLE.createSetting());
            result.put(ExploreKey.SUCCESSOR, Successor.SINGLE.createSetting());
        }
        case "random" -> {
            result.put(ExploreKey.FRONTIER, Frontier.SINGLE.createSetting());
            result.put(ExploreKey.SUCCESSOR, Successor.SINGLE_RANDOM.createSetting());
        }
        case "rete" -> {
            result.put(ExploreKey.MATCHER, Matcher.RETE.createSetting());
            result.put(ExploreKey.NEXT, NextState.NEWEST.createSetting());
        }
        case "retelinear" -> {
            result.put(ExploreKey.MATCHER, Matcher.RETE.createSetting());
            result.put(ExploreKey.FRONTIER, Frontier.SINGLE.createSetting());
            result.put(ExploreKey.SUCCESSOR, Successor.SINGLE.createSetting());
        }
        case "reterandom" -> {
            result.put(ExploreKey.MATCHER, Matcher.RETE.createSetting());
            result.put(ExploreKey.FRONTIER, Frontier.SINGLE.createSetting());
            result.put(ExploreKey.SUCCESSOR, Successor.SINGLE_RANDOM.createSetting());
        }
        default -> errors
            .add("Strategy '%s' has no feature-model equivalent", strategy.getKeyword());
        }
        Serialized acceptor = type.getAcceptor();
        switch (acceptor.getKeyword()) {
        case "final" -> {
            // the default goal
        }
        case "none" -> result.put(ExploreKey.GOAL, Goal.NONE.createSetting());
        case "any" -> result.put(ExploreKey.GOAL, Goal.ANY.createSetting());
        case "ruleapp" -> result
            .put(ExploreKey.GOAL, Goal.APPLIED.createSetting(acceptor.getArgument("rule")));
        case "inv" -> {
            result.put(ExploreKey.GOAL, Goal.RULE.createSetting(acceptor.getArgument("rule")));
            if (EncodedPolarity.NEGATIVE.equals(acceptor.getArgument("polarity"))) {
                result.put(ExploreKey.OUTCOME, Outcome.VIOLATE.createSetting());
            }
        }
        case "formula" -> result
            .put(ExploreKey.GOAL, Goal.FORMULA.createSetting(acceptor.getArgument("formula")));
        default -> errors
            .add("Acceptor '%s' has no feature-model equivalent", acceptor.getKeyword());
        }
        int bound = type.getBound();
        if (bound == 1) {
            result.put(ExploreKey.COUNT, Count.FIRST.createSetting());
        } else if (bound > 1) {
            result.put(ExploreKey.COUNT, Count.COUNT.createSetting(bound));
        }
        errors.throwException();
        return result;
    }

    /**
     * Translates the optional depth bound of a legacy {@code bfs} or
     * {@code dfs} strategy into a uniform-cost bound.
     */
    private static void setDepthBound(ExploreConfig result, Serialized strategy,
                                      FormatErrorSet errors) {
        String boundText = strategy.getArgument("bound");
        if (!boundText.isEmpty()) {
            try {
                int bound = Integer.parseInt(boundText);
                if (bound > 0) {
                    result.put(ExploreKey.COST, Cost.UNIFORM.createSetting());
                    result
                        .put(ExploreKey.BOUND,
                             Bound.COST.createSetting(new Bound.Limit(bound, 0)));
                }
            } catch (NumberFormatException exc) {
                errors.add("Depth bound '%s' is not a number", boundText);
            }
        }
    }
}
