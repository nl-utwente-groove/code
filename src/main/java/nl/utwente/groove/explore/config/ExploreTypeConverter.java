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
import nl.utwente.groove.util.parse.FormatErrorSet;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Realisability gate of the exploration feature model: converts an
 * {@link ExploreConfig} to the {@link ConfiguredExploreType} realising it,
 * rejecting configurations that are inconsistent (see
 * {@link ExploreConfig#check()}) or use feature values the exploration
 * engine does not (yet) realise, with an explanatory error.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class ExploreTypeConverter {
    private ExploreTypeConverter() {
        // static utility class
    }

    /**
     * Converts an exploration configuration to the exploration type
     * realising it.
     * @throws FormatException if the configuration is inconsistent (see
     * {@link ExploreConfig#check()}) or uses feature values that the
     * exploration engine cannot realise
     */
    public static ExploreType toExploreType(ExploreConfig config) throws FormatException {
        config.check().throwException();
        var errors = new FormatErrorSet();
        checkInexpressible(config, errors);
        checkStrategy(config, errors);
        checkAcceptor(config, errors);
        errors.throwException();
        return new ConfiguredExploreType(config, getResultBound(config));
    }

    /** Collects errors for the feature values no strategy can realise. */
    private static void checkInexpressible(ExploreConfig config, FormatErrorSet errors) {
        if (config.getKind(ExploreKey.HEURISTIC) != Heuristic.NONE) {
            errors.add("Heuristic search is not yet supported");
        }
        if (config.getKind(ExploreKey.COST) == Cost.RULE) {
            errors.add("Rule-based transition cost is not yet supported");
        }
        if (config.getKind(ExploreKey.COLLAPSE) == Collapse.HASH) {
            errors.add("Hash-based state collapse is not yet supported");
        }
    }

    /**
     * Checks the realisability of the strategy side of a configuration:
     * first the baseline traversal from the next-state, successor and
     * frontier features, then the applicability of the bound feature to
     * that traversal.
     */
    private static void checkStrategy(ExploreConfig config, FormatErrorSet errors) {
        String keyword = computeTraversal(config, errors);
        if (keyword != null) {
            checkBound(config, keyword, errors);
        }
    }

    /**
     * Computes the baseline traversal keyword for a configuration, as a
     * shorthand for the combinations the bound feature discriminates on;
     * {@code null} if the traversal features are unrealisable.
     */
    private static @Nullable String computeTraversal(ExploreConfig config,
                                                     FormatErrorSet errors) {
        var next = (NextState) config.getKind(ExploreKey.NEXT);
        var successor = (Successor) config.getKind(ExploreKey.SUCCESSOR);
        if (config.getKind(ExploreKey.FRONTIER) == Frontier.SINGLE) {
            // linear search; the next-state selection is irrelevant
            return switch (successor) {
            case SINGLE, SINGLE_RANDOM -> "linear";
            case ALL, ALL_RANDOM -> {
                errors.add("A single-state frontier requires single-successor generation");
                yield null;
            }
            };
        }
        return switch (successor) {
        case ALL -> config.getKind(ExploreKey.FRONTIER) == Frontier.BEAM
            ? "beam"
            : switch (next) {
            case OLDEST -> "bfs";
            case NEWEST -> "dfs";
            case RANDOM -> "random-frontier";
            };
        case ALL_RANDOM -> {
            errors.add("Randomised successor generation is not yet supported");
            yield null;
        }
        case SINGLE, SINGLE_RANDOM -> {
            errors
                .add("Single-successor generation with a multi-state frontier"
                    + " is not yet supported");
            yield null;
        }
        };
    }

    /**
     * Checks the bound feature against the baseline traversal: a bound on
     * uniform path cost (the depth bound) and the condition bounds require
     * breadth-first or depth-first exploration; node and edge count bounds
     * require breadth-first exploration.
     */
    private static void checkBound(ExploreConfig config, String keyword,
                                   FormatErrorSet errors) {
        boolean searching = "bfs".equals(keyword) || "dfs".equals(keyword);
        Object content = config.get(ExploreKey.BOUND).content();
        switch ((Bound) config.getKind(ExploreKey.BOUND)) {
        default -> {
            // no bound, no restrictions
        }
        case COST -> {
            // consistency of cost != NONE is guaranteed by check()
            if (config.getKind(ExploreKey.COST) != Cost.UNIFORM) {
                errors.add("Only a uniform-cost (depth) bound is currently supported");
            } else if (!searching) {
                errors
                    .add("A depth bound requires breadth-first or depth-first exploration");
            } else {
                checkLimit(content, errors);
            }
        }
        case SIZE -> errors.add("A graph size bound is not yet supported");
        case NODES -> {
            if (!"bfs".equals(keyword)) {
                errors.add("A node count bound requires breadth-first exploration");
            } else {
                checkLimit(content, errors);
            }
        }
        case EDGES -> {
            if (!"bfs".equals(keyword)) {
                errors.add("An edge count bound requires breadth-first exploration");
            }
        }
        case UPTO, INCLUDE -> {
            if (!searching) {
                errors
                    .add("A condition bound requires breadth-first or depth-first exploration");
            }
        }
        }
    }

    /** Checks a limit content, reporting an unsupported increment as an error. */
    private static void checkLimit(Object content, FormatErrorSet errors) {
        if (((Bound.Limit) content).increment() != 0) {
            errors.add("Iterative deepening is not yet supported");
        }
    }

    /** Checks the realisability of the goal and outcome features. */
    private static void checkAcceptor(ExploreConfig config, FormatErrorSet errors) {
        var satisfy = config.getKind(ExploreKey.OUTCOME) == Outcome.SATISFY;
        switch ((Goal) config.getKind(ExploreKey.GOAL)) {
        // the outcome for NONE, ANY and FINAL is guaranteed by check() to be SATISFY;
        // these goals and CONDITION (the default cases) are realisable
        default -> {
            // realisable regardless of the outcome
        }
        case FIRES -> {
            if (!satisfy) {
                errors.add("A violated fires goal is not yet supported");
            }
        }
        case GRAPH -> errors.add("A graph goal is not yet supported");
        case LTL, CTL -> errors
            .add("Temporal goals are handled by the model checking actions,"
                + " not by exploration");
        }
    }

    /** Computes the result bound for a configuration. */
    private static int getResultBound(ExploreConfig config) {
        return switch ((Count) config.getKind(ExploreKey.COUNT)) {
        case ALL -> 0;
        case FIRST -> 1;
        case COUNT -> (Integer) config.get(ExploreKey.COUNT).content();
        };
    }
}
