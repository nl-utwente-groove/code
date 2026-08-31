/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2026 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the specific language governing
 * permissions and limitations under the License.
 *
 * $Id$
 */
package nl.utwente.groove.match;

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.grammar.GrammarKey;
import nl.utwente.groove.util.AIGenerated;

/**
 * Exception thrown when the number of matches collected for a single state
 * exceeds the bound set by the {@link GrammarKey#MATCH_BOUND} grammar
 * property, or (regardless of that property) the maximum number
 * representable in an {@code int}. Exploration catches this exception and
 * halts gracefully, after the offending state has been flagged as an error
 * state.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
@AIGenerated("Claude Fable 5, 2026-08")
public class MatchBoundException extends RuntimeException {
    /**
     * Constructs an exception for a match count exceeding the bound set by
     * the {@link GrammarKey#MATCH_BOUND} grammar property.
     * @param name name of the rule or condition whose matches exceed the bound
     * @param count the offending number of matches; a lower bound if
     * {@code exact} is {@code false}
     * @param exact if {@code true}, {@code count} is the actual number of
     * matches; if {@code false}, the actual number is larger but was not
     * computed
     * @param bound the exceeded bound, as set by the grammar property
     */
    public MatchBoundException(String name, long count, boolean exact, int bound) {
        super(String
            .format("Number of matches for '%s' is %s, which exceeds the match bound (%s=%d)",
                    name, toCountPhrase(count, exact), GrammarKey.MATCH_BOUND.getName(), bound));
        this.name = name;
        this.count = count;
        this.exact = exact;
        this.bound = bound;
    }

    /**
     * Constructs an exception for a match count exceeding the maximum
     * representable number, {@link Integer#MAX_VALUE}. This limit applies
     * even if the {@link GrammarKey#MATCH_BOUND} grammar property does not
     * set a bound.
     * @param name name of the rule or condition whose matches exceed the limit
     * @param count the offending number of matches; a lower bound if
     * {@code exact} is {@code false}
     * @param exact if {@code true}, {@code count} is the actual number of
     * matches; if {@code false}, the actual number is larger but could not
     * be computed
     */
    public MatchBoundException(String name, long count, boolean exact) {
        super(String
            .format("Number of matches for '%s' is %s, which exceeds the maximum of %d supported by GROOVE",
                    name, toCountPhrase(count, exact), Integer.MAX_VALUE));
        this.name = name;
        this.count = count;
        this.exact = exact;
        this.bound = Integer.MAX_VALUE;
    }

    /** Renders a count as a number, or as a strict lower bound if inexact. */
    private static String toCountPhrase(long count, boolean exact) {
        return exact
            ? Long.toString(count)
            : "> " + count;
    }

    /** Returns the name of the rule or condition whose matches exceed the bound. */
    public String getName() {
        return this.name;
    }

    private final String name;

    /** Returns the offending number of matches; a strict lower bound if
     * {@link #isExact()} is {@code false}. */
    public long getCount() {
        return this.count;
    }

    private final long count;

    /** Indicates whether {@link #getCount()} is the actual number of matches
     * rather than a strict lower bound. */
    public boolean isExact() {
        return this.exact;
    }

    private final boolean exact;

    /** Returns the exceeded bound. */
    public int getBound() {
        return this.bound;
    }

    private final int bound;
}
