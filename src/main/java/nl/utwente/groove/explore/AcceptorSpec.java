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
package nl.utwente.groove.explore;

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.explore.config.parse.EnabledRuleParser;
import nl.utwente.groove.explore.config.parse.RuleFormulaParser;
import nl.utwente.groove.explore.result.Acceptor;
import nl.utwente.groove.explore.result.AnyStateAcceptor;
import nl.utwente.groove.explore.result.CycleAcceptor;
import nl.utwente.groove.explore.result.FinalStateAcceptor;
import nl.utwente.groove.explore.result.NoStateAcceptor;
import nl.utwente.groove.explore.result.Predicate;
import nl.utwente.groove.explore.result.PredicateAcceptor;
import nl.utwente.groove.grammar.Grammar;
import nl.utwente.groove.grammar.Rule;
import nl.utwente.groove.lts.GraphState;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Specification of a legacy acceptor: a kind plus content — a rule name
 * (for {@link Kind#RULEAPP}), an optionally {@code !}-prefixed rule name
 * (for {@link Kind#INVARIANT}), or a rule formula (for
 * {@link Kind#FORMULA}); empty for the content-less kinds. The content is
 * resolved against the grammar on {@link #instantiate}. Used as the acceptor
 * state of the dedicated (non-configuration-based) exploration types.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public record AcceptorSpec(Kind kind, String content) {
    /** The legacy acceptor kinds. */
    public enum Kind {
        /** Final states, i.e., states without outgoing transitions. */
        FINAL("final"),
        /** Every state. */
        ANY("any"),
        /** No state. */
        NONE("none"),
        /** Accepting cycles of an LTL product exploration. */
        CYCLE("cycle"),
        /** States in which a given rule fires. */
        RULEAPP("ruleapp"),
        /** States in which a given rule is (or is not) applicable. */
        INVARIANT("inv"),
        /** States satisfying a rule formula. */
        FORMULA("formula"),;

        private Kind(String keyword) {
            this.keyword = keyword;
        }

        /** Returns the identifying keyword of this acceptor kind. */
        public String getKeyword() {
            return this.keyword;
        }

        private final String keyword;
    }

    /** Instantiates this acceptor specification for a given grammar.
     * @throws FormatException if the content does not resolve against the grammar
     */
    public Acceptor instantiate(Grammar grammar) throws FormatException {
        return switch (kind()) {
        case FINAL -> FinalStateAcceptor.PROTOTYPE;
        case ANY -> AnyStateAcceptor.PROTOTYPE;
        case NONE -> NoStateAcceptor.INSTANCE;
        case CYCLE -> CycleAcceptor.PROTOTYPE;
        case RULEAPP -> new PredicateAcceptor(new Predicate.ActionApplied(EnabledRuleParser
            .parse(grammar, content())));
        case INVARIANT -> {
            boolean positive = !content().startsWith("!");
            Rule rule = EnabledRuleParser
                .parse(grammar, positive
                    ? content()
                    : content().substring(1));
            Predicate<GraphState> predicate = new Predicate.RuleApplicable(rule);
            if (!positive) {
                predicate = new Predicate.Not<>(predicate);
            }
            yield new PredicateAcceptor(predicate);
        }
        case FORMULA -> new PredicateAcceptor(RuleFormulaParser.parse(grammar, content()));
        };
    }

    /** Returns the legacy descriptor of this acceptor, for display purposes. */
    public String getIdentifier() {
        return content().isEmpty()
            ? kind().getKeyword()
            : kind().getKeyword() + ":" + content();
    }

    /** The content-less final-state acceptor specification. */
    public static final AcceptorSpec FINAL = new AcceptorSpec(Kind.FINAL, "");
    /** The content-less no-state acceptor specification. */
    public static final AcceptorSpec NONE = new AcceptorSpec(Kind.NONE, "");
}
