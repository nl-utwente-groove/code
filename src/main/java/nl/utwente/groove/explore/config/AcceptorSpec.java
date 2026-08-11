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

/**
 * Specification of a legacy acceptor: a kind plus content — a rule name
 * (for {@link Kind#RULEAPP}), an optionally {@code !}-prefixed rule name
 * (for {@link Kind#INVARIANT}), or a rule formula (for
 * {@link Kind#FORMULA}); empty for the content-less kinds. Intermediate
 * representation of the legacy acceptor syntax, whose content is resolved
 * against the grammar only once it has been translated onwards into the
 * goal feature of a configuration.
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
}
