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
     * Constructs an exception for a given rule or condition and bound.
     * @param name name of the rule or condition whose matches exceed the bound
     * @param bound the exceeded bound, as set by the
     * {@link GrammarKey#MATCH_BOUND} grammar property
     */
    public MatchBoundException(String name, int bound) {
        super(String
            .format("Number of matches for '%s' exceeds the match bound (%s=%d)", name,
                    GrammarKey.MATCH_BOUND.getName(), bound));
        this.name = name;
        this.bound = bound;
    }

    /**
     * Constructs an exception for a given rule or condition whose match
     * count exceeds the maximum representable number, {@link Integer#MAX_VALUE}.
     * This limit applies even if the {@link GrammarKey#MATCH_BOUND} grammar
     * property does not set a bound.
     * @param name name of the rule or condition whose matches exceed the limit
     */
    public MatchBoundException(String name) {
        super(String
            .format("Number of matches for '%s' exceeds the maximum of %d supported by GROOVE",
                    name, Integer.MAX_VALUE));
        this.name = name;
        this.bound = Integer.MAX_VALUE;
    }

    /** Returns the name of the rule or condition whose matches exceed the bound. */
    public String getName() {
        return this.name;
    }

    private final String name;

    /** Returns the exceeded bound. */
    public int getBound() {
        return this.bound;
    }

    private final int bound;
}
