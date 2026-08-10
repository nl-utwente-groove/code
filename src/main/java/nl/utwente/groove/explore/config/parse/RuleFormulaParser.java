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
package nl.utwente.groove.explore.config.parse;

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.explore.result.Predicate;
import nl.utwente.groove.grammar.Grammar;
import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.grammar.Rule;
import nl.utwente.groove.lts.GraphState;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Parser for rule formulas: predicates over graph states constructed from
 * rule applicability atoms with the logical operators {@code !} (not),
 * {@code &&} (and), {@code ||} (or) and {@code ->} (implies), with
 * parentheses for grouping.
 * <p>
 * Successor of the formula parsing in the legacy {@code EncodedRuleFormula},
 * with two deliberate improvements: the parser is reentrant (all state is
 * local to a parse), and the operators have their conventional precedence
 * and associativity — {@code !} binds strongest, then {@code &&}, then
 * {@code ||}, then the right-associative {@code ->} — where the legacy
 * parser treated everything as a single right-associative level (so that
 * {@code !a && b} meant {@code !(a && b)}).
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class RuleFormulaParser {
    private RuleFormulaParser() {
        // static utility class
    }

    /**
     * Resolver from rule names to the corresponding rule-applicability
     * predicates. The standard resolver looks up the rule in a
     * {@link Grammar} (see {@link #parse(Grammar, String)}); a checking
     * client can substitute a resolver that only validates the name, so
     * that a formula can be syntax- and name-checked without an
     * instantiated grammar.
     */
    @FunctionalInterface
    public static interface RuleResolver {
        /**
         * Returns the applicability predicate for a given rule name.
         * @throws FormatException if the name does not denote an enabled rule
         */
        Predicate<GraphState> resolve(QualName name) throws FormatException;
    }

    /**
     * Parses a rule formula, with rule names looked up in a given grammar.
     * @throws FormatException if the formula does not parse, or contains a
     * name that is not an enabled rule of the grammar
     */
    public static Predicate<GraphState> parse(Grammar grammar,
                                              String text) throws FormatException {
        return parse(name -> {
            Rule rule = grammar.getRule(name);
            if (rule == null) {
                throw new FormatException("'%s' is not an enabled rule in the loaded grammar",
                    name);
            }
            return new Predicate.RuleApplicable(rule);
        }, text);
    }

    /** Parses a rule formula, with rule names looked up by a given resolver. */
    public static Predicate<GraphState> parse(RuleResolver resolver,
                                              String text) throws FormatException {
        var cursor = new Cursor(text, resolver);
        Predicate<GraphState> result = cursor.parseImplication();
        cursor.skipSpaces();
        if (!cursor.atEnd()) {
            throw new FormatException("Unparsed formula text from character index %s",
                cursor.index());
        }
        return result;
    }

    /** Parse state for a single invocation, making the parser reentrant. */
    static private class Cursor {
        Cursor(String text, RuleResolver resolver) {
            this.text = text;
            this.resolver = resolver;
        }

        private final String text;
        private final RuleResolver resolver;
        private int i;

        int index() {
            return this.i;
        }

        boolean atEnd() {
            return this.i >= this.text.length();
        }

        void skipSpaces() {
            while (!atEnd() && this.text.charAt(this.i) == ' ') {
                this.i++;
            }
        }

        /** Consumes a given literal if it is next in the input. */
        boolean consume(String literal) {
            if (this.text.startsWith(literal, this.i)) {
                this.i += literal.length();
                return true;
            } else {
                return false;
            }
        }

        /** Implication level: right-associative {@code ->}, weakest. */
        Predicate<GraphState> parseImplication() throws FormatException {
            Predicate<GraphState> result = parseDisjunction();
            skipSpaces();
            if (consume("->")) {
                result = new Predicate.Implies<>(result, parseImplication());
            }
            return result;
        }

        /** Disjunction level: {@code ||} (or the single-bar legacy form). */
        Predicate<GraphState> parseDisjunction() throws FormatException {
            Predicate<GraphState> result = parseConjunction();
            skipSpaces();
            while (consume("||") || consume("|")) {
                result = new Predicate.Or<>(result, parseConjunction());
                skipSpaces();
            }
            return result;
        }

        /** Conjunction level: {@code &&} (or the single-ampersand legacy form). */
        Predicate<GraphState> parseConjunction() throws FormatException {
            Predicate<GraphState> result = parseAtom();
            skipSpaces();
            while (consume("&&") || consume("&")) {
                result = new Predicate.And<>(result, parseAtom());
                skipSpaces();
            }
            return result;
        }

        /** Atom level: negation, parenthesised formula, or rule name. */
        Predicate<GraphState> parseAtom() throws FormatException {
            skipSpaces();
            if (consume("!")) {
                return new Predicate.Not<>(parseAtom());
            }
            if (consume("(")) {
                int open = this.i - 1;
                Predicate<GraphState> result = parseImplication();
                skipSpaces();
                if (!consume(")")) {
                    throw new FormatException(
                        "Unable to find the closing bracket for the open bracket at index %s",
                        open);
                }
                return result;
            }
            return parseRule();
        }

        /** Parses a rule name atom. */
        Predicate<GraphState> parseRule() throws FormatException {
            int start = this.i;
            while (!atEnd() && !stopsRuleName()) {
                this.i++;
            }
            if (this.i == start) {
                throw new FormatException("Expected a rule name at character index %s", start);
            }
            QualName ruleName = QualName.parse(this.text.substring(start, this.i));
            return this.resolver.resolve(ruleName);
        }

        /**
         * Tests whether the character at the current index terminates a rule
         * name. A hyphen only does so as the start of the {@code ->} operator,
         * since rule names may contain internal hyphens.
         */
        private boolean stopsRuleName() {
            char c = this.text.charAt(this.i);
            if (c == '-') {
                return this.text.startsWith("->", this.i);
            }
            return RULE_NAME_STOPPERS.indexOf(c) >= 0;
        }

        /** The characters that unconditionally terminate a rule name. */
        static private final String RULE_NAME_STOPPERS = "()>|& !";
    }
}
