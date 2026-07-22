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

import nl.utwente.groove.explore.encode.EncodedEdgeMap;
import nl.utwente.groove.explore.encode.EncodedRuleFormula;
import nl.utwente.groove.explore.result.Predicate;
import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.lts.GraphState;
import nl.utwente.groove.util.parse.FormatErrorSet;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Checker for the grammar-dependent contents of an exploration configuration:
 * condition formulas must be well-formed, referenced rules must exist and be
 * enabled, and edge-bound labels must occur in the type graph. The check runs
 * against the {@link GrammarModel} — deliberately not against an instantiated
 * {@link nl.utwente.groove.grammar.Grammar}, since it is invoked from the
 * grammar properties checker, which itself runs during grammar instantiation.
 * This is what makes content errors surface as errors on the system
 * properties, rather than only failing an attempted exploration.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class ExploreConfigChecker {
    private ExploreConfigChecker() {
        // static utility class
    }

    /**
     * Checks the grammar-dependent contents of a configuration against a
     * grammar model, returning the errors found.
     */
    public static FormatErrorSet check(GrammarModel grammar, ExploreConfig config) {
        var result = new FormatErrorSet();
        var goal = (Goal) config.getKind(ExploreKey.GOAL);
        switch (goal) {
        case CONDITION -> checkCondition(grammar, goalContent(config), result);
        case FIRES -> checkRuleName(grammar, goalContent(config), result);
        default -> {
            // no grammar-dependent content
        }
        }
        var bound = (Bound) config.getKind(ExploreKey.BOUND);
        switch (bound) {
        case UPTO, INCLUDE -> {
            String condition = (String) config.get(ExploreKey.BOUND).content();
            checkRuleName(grammar, condition.startsWith("!")
                ? condition.substring(1)
                : condition, result);
        }
        case EDGES -> checkEdgeBounds(grammar, (String) config.get(ExploreKey.BOUND).content(),
                                      result);
        default -> {
            // no grammar-dependent content
        }
        }
        return result;
    }

    /** Returns the (string) content of the goal setting. */
    private static String goalContent(ExploreConfig config) {
        return (String) config.get(ExploreKey.GOAL).content();
    }

    /**
     * Checks that a condition is a well-formed rule formula whose rule names
     * denote enabled rules of the grammar.
     */
    private static void checkCondition(GrammarModel grammar, String condition,
                                       FormatErrorSet errors) {
        try {
            new EncodedRuleFormula().parse(name -> {
                checkRuleName(grammar, name);
                return TRUE;
            }, condition);
        } catch (FormatException exc) {
            errors.addAll(exc.getErrors());
        }
    }

    /** Checks that a name denotes an enabled rule, adding an error otherwise. */
    private static void checkRuleName(GrammarModel grammar, String name, FormatErrorSet errors) {
        try {
            checkRuleName(grammar, QualName.parse(name));
        } catch (FormatException exc) {
            errors.addAll(exc.getErrors());
        }
    }

    /** Checks that a name denotes an enabled rule, throwing an exception otherwise. */
    private static void checkRuleName(GrammarModel grammar, QualName name) throws FormatException {
        name.getErrors().throwException();
        if (!grammar.getActiveNames(ResourceKind.RULE).contains(name)) {
            throw new FormatException("'%s' is not an enabled rule in the grammar", name);
        }
    }

    /** Checks the syntax and labels of an edge-bound map. */
    private static void checkEdgeBounds(GrammarModel grammar, String bounds,
                                        FormatErrorSet errors) {
        try {
            new EncodedEdgeMap().parse(grammar.getTypeGraph(), bounds);
        } catch (FormatException exc) {
            errors.addAll(exc.getErrors());
        }
    }

    /** Trivial state predicate, used as a placeholder during formula checking. */
    private static final Predicate<GraphState> TRUE = new Predicate.StatePredicate() {
        @Override
        public boolean test(@Nullable GraphState state) {
            return true;
        }
    };
}
