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
import nl.utwente.groove.util.Exceptions;
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
        Traversal traversal = computeTraversal(config, errors);
        if (traversal != null) {
            checkBound(config, traversal, errors);
        }
        checkAcceptor(config, errors);
        errors.throwException();
        assert traversal != null; // computeTraversal reported an error otherwise
        return new ConfiguredExploreType(config, getResultCount(config), traversal);
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
     * Computes the baseline traversal realising the next-state, successor
     * and frontier features of a configuration; {@code null} (plus an error)
     * if the combination is unrealisable. This is the single derivation of
     * the traversal: the value is checked against the bound feature here and
     * handed to the {@link ConfiguredExploreType} for instantiation.
     */
    private static @Nullable Traversal computeTraversal(ExploreConfig config,
                                                        FormatErrorSet errors) {
        var next = (NextState) config.getKind(ExploreKey.NEXT);
        var successor = (Successor) config.getKind(ExploreKey.SUCCESSOR);
        if (config.getKind(ExploreKey.FRONTIER) == Frontier.SINGLE) {
            // linear search; the next-state selection is irrelevant
            return switch (successor) {
            case SINGLE, SINGLE_RANDOM -> Traversal.LINEAR;
            case ALL, ALL_RANDOM -> {
                errors.add("A single-state frontier requires single-successor generation");
                yield null;
            }
            };
        }
        return switch (successor) {
        case ALL -> config.getKind(ExploreKey.FRONTIER) == Frontier.BEAM
            ? Traversal.BEAM
            : switch (next) {
            case OLDEST -> Traversal.BFS;
            case NEWEST -> Traversal.DFS;
            case RANDOM -> Traversal.RANDOM;
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
    private static void checkBound(ExploreConfig config, Traversal traversal,
                                   FormatErrorSet errors) {
        Object content = config.get(ExploreKey.BOUND).content();
        switch ((Bound) config.getKind(ExploreKey.BOUND)) {
        case NONE -> {
            // no bound, no restrictions
        }
        case INITIAL -> {
            // realised by the dedicated single-state strategy, which makes
            // the traversal irrelevant; no combination to check
        }
        case COST -> {
            // consistency of cost != NONE is guaranteed by check()
            if (config.getKind(ExploreKey.COST) != Cost.UNIFORM) {
                errors.add("Only a uniform-cost (depth) bound is currently supported");
            } else if (!traversal.isSearch()) {
                errors
                    .add("A depth bound requires breadth-first or depth-first exploration");
            } else {
                checkLimit(content, errors);
                if (((Bound.Limit) content).max() == 0) {
                    // the engine reserves 0 as the no-bound sentinel; a depth
                    // bound of 0 would silently explore without restriction
                    errors
                        .add("A depth bound must be positive; use bound '%s'"
                            + " to explore only the initial state", Bound.INITIAL.getName());
                }
            }
        }
        case SIZE -> errors.add("A graph size bound is not yet supported");
        case NODES -> {
            if (traversal != Traversal.BFS) {
                errors.add("A node count bound requires breadth-first exploration");
            } else {
                checkLimit(content, errors);
            }
        }
        case EDGES -> {
            if (traversal != Traversal.BFS) {
                errors.add("An edge count bound requires breadth-first exploration");
            }
        }
        case UPTO, INCLUDE -> {
            if (!traversal.isSearch()) {
                errors
                    .add("A condition bound requires breadth-first or depth-first exploration");
            }
        }
        // the case list is exhaustive; the default guards against a future
        // bound kind being silently treated as realisable
        default -> throw Exceptions.unreachable();
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
        // these goals and CONDITION are realisable regardless of the outcome
        case NONE, ANY, FINAL, CONDITION -> {
            // realisable
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
        // the case list is exhaustive; the default guards against a future
        // goal kind being silently treated as realisable
        default -> throw Exceptions.unreachable();
        }
    }

    /** Computes the result count for a configuration. */
    private static int getResultCount(ExploreConfig config) {
        return switch ((Count) config.getKind(ExploreKey.COUNT)) {
        case ALL -> 0;
        case FIRST -> 1;
        case COUNT -> (Integer) config.get(ExploreKey.COUNT).content();
        };
    }
}
