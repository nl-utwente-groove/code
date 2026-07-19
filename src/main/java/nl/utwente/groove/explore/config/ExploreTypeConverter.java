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
import nl.utwente.groove.explore.encode.EncodedSearchMode;
import nl.utwente.groove.explore.encode.EncodedStopMode;
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
 * equivalent (the LTL strategies, the state, minimax and remote strategies,
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
        Serialized strategy = computeStrategy(config, errors);
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
     * Computes the serialised legacy strategy for a configuration: first the
     * baseline traversal from the next-state, successor, frontier and matcher
     * features, then the strategy variant demanded by the bound feature.
     */
    private static @Nullable Serialized computeStrategy(ExploreConfig config,
                                                        FormatErrorSet errors) {
        String keyword = computeTraversal(config, errors);
        if (keyword == null) {
            return null;
        }
        return applyBound(config, keyword, errors);
    }

    /** Computes the baseline traversal keyword for a configuration. */
    private static @Nullable String computeTraversal(ExploreConfig config,
                                                     FormatErrorSet errors) {
        var next = (NextState) config.getKind(ExploreKey.NEXT);
        var successor = (Successor) config.getKind(ExploreKey.SUCCESSOR);
        var rete = config.getKind(ExploreKey.MATCHER) == Matcher.RETE;
        String result = null;
        if (config.getKind(ExploreKey.FRONTIER) == Frontier.SINGLE) {
            // linear search; the next-state selection is irrelevant
            switch (successor) {
            case SINGLE -> result = rete
                ? "retelinear"
                : "linear";
            case SINGLE_RANDOM -> result = rete
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
                        result = "bfs";
                    }
                }
                case NEWEST -> result = rete
                    ? "rete"
                    : "dfs";
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
        return result;
    }

    /**
     * Refines a baseline traversal keyword according to the bound feature: a
     * bound on uniform path cost is exactly the legacy depth bound of
     * {@code bfs} and {@code dfs}; node count, edge count and condition
     * bounds select the corresponding conditional legacy strategies.
     */
    private static @Nullable Serialized applyBound(ExploreConfig config, String keyword,
                                                   FormatErrorSet errors) {
        boolean searching = "bfs".equals(keyword) || "dfs".equals(keyword);
        Object content = config.get(ExploreKey.BOUND).content();
        Serialized result = null;
        switch ((Bound) config.getKind(ExploreKey.BOUND)) {
        case NONE -> result = new Serialized(keyword);
        case COST -> {
            // consistency of cost != NONE is guaranteed by check()
            if (config.getKind(ExploreKey.COST) != Cost.UNIFORM) {
                errors.add("Only a uniform-cost (depth) bound is currently supported");
            } else if (!searching) {
                errors
                    .add("A depth bound requires breadth-first or depth-first exploration");
            } else if (getLimit(content, errors) instanceof Bound.Limit limit) {
                result = new Serialized(keyword);
                if (limit.max() > 0) {
                    result.setArgument("bound", Integer.toString(limit.max()));
                }
            }
        }
        case SIZE -> errors.add("A graph size bound is not yet supported");
        case NODES -> {
            if (!"bfs".equals(keyword)) {
                errors.add("A node count bound requires breadth-first exploration");
            } else if (getLimit(content, errors) instanceof Bound.Limit limit) {
                result = new Serialized("cnbound");
                result.setArgument("node-bound", Integer.toString(limit.max()));
            }
        }
        case EDGES -> {
            if (!"bfs".equals(keyword)) {
                errors.add("An edge count bound requires breadth-first exploration");
            } else {
                result = new Serialized("cebound");
                result.setArgument("edge-bound", (String) content);
            }
        }
        case UPTO, INCLUDE -> {
            if (!searching) {
                errors
                    .add("A condition bound requires breadth-first or depth-first exploration");
            } else {
                boolean upto = config.getKind(ExploreKey.BOUND) == Bound.UPTO;
                String condition = (String) content;
                boolean positive = !condition.startsWith("!");
                result = new Serialized("uptorule");
                result.setArgument("search", keyword);
                result.setArgument("stop", upto
                    ? EncodedStopMode.UP_TO_KEY
                    : EncodedStopMode.INCLUDE_KEY);
                result.setArgument("polarity", positive
                    ? EncodedPolarity.POSITIVE
                    : EncodedPolarity.NEGATIVE);
                result.setArgument("rule", positive
                    ? condition
                    : condition.substring(1));
                // the numeric depth bound of uptorule cannot also be set,
                // since the bound feature is singular
                result.setArgument("bound", "0");
            }
        }
        }
        return result;
    }

    /** Extracts a limit content, reporting an unsupported increment as an error. */
    private static Bound.@Nullable Limit getLimit(Object content, FormatErrorSet errors) {
        var result = (Bound.Limit) content;
        if (result.increment() != 0) {
            errors.add("Iterative deepening is not yet supported");
            result = null;
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
        case "cnbound" -> setCountBound(result, Bound.NODES, strategy, "node-bound", errors);
        case "cebound" -> result
            .put(ExploreKey.BOUND, Bound.EDGES.createSetting(strategy.getArgument("edge-bound")));
        case "uptorule", "crule" -> setConditionBound(result, strategy, errors);
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
        int bound = parseBound(strategy, "bound", errors);
        if (bound > 0) {
            result.put(ExploreKey.COST, Cost.UNIFORM.createSetting());
            result.put(ExploreKey.BOUND, Bound.COST.createSetting(new Bound.Limit(bound, 0)));
        }
    }

    /**
     * Translates the count bound of a legacy conditional strategy
     * ({@code cnbound}) into the corresponding bound feature.
     */
    private static void setCountBound(ExploreConfig result, Bound kind, Serialized strategy,
                                      String argName, FormatErrorSet errors) {
        int bound = parseBound(strategy, argName, errors);
        result.put(ExploreKey.BOUND, kind.createSetting(new Bound.Limit(bound, 0)));
    }

    /**
     * Translates a legacy {@code uptorule} or {@code crule} strategy into the
     * corresponding next-state selection and condition bound features.
     */
    private static void setConditionBound(ExploreConfig result, Serialized strategy,
                                          FormatErrorSet errors) {
        boolean crule = "crule".equals(strategy.getKeyword());
        String search = crule
            ? EncodedSearchMode.BFS_KEY
            : strategy.getArgument("search");
        switch (search) {
        case EncodedSearchMode.BFS_KEY -> {
            // the default next-state selection
        }
        case EncodedSearchMode.DFS_KEY -> result
            .put(ExploreKey.NEXT, NextState.NEWEST.createSetting());
        default -> errors.add("Unknown search mode '%s'", search);
        }
        String stop = crule
            ? EncodedStopMode.UP_TO_KEY
            : strategy.getArgument("stop");
        Bound kind = switch (stop) {
        case EncodedStopMode.UP_TO_KEY -> Bound.UPTO;
        case EncodedStopMode.INCLUDE_KEY -> Bound.INCLUDE;
        default -> null;
        };
        if (kind == null) {
            errors.add("Unknown stop mode '%s'", stop);
            return;
        }
        String condition = strategy.getArgument("rule");
        if (EncodedPolarity.NEGATIVE.equals(strategy.getArgument("polarity"))) {
            condition = "!" + condition;
        }
        result.put(ExploreKey.BOUND, kind.createSetting(condition));
        if (!crule && parseBound(strategy, "bound", errors) > 0) {
            errors
                .add("A condition bound cannot be combined with a depth bound"
                    + " in the feature model");
        }
    }

    /** Parses a numeric argument of a serialised strategy; absent means zero. */
    private static int parseBound(Serialized strategy, String argName, FormatErrorSet errors) {
        int result = 0;
        String text = strategy.getArgument(argName);
        if (!text.isEmpty()) {
            try {
                result = Integer.parseInt(text);
            } catch (NumberFormatException exc) {
                errors.add("Bound '%s' is not a number", text);
            }
        }
        return result;
    }
}
