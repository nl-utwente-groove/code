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

import nl.utwente.groove.util.parse.FormatException;

/**
 * Feature values for {@link ExploreKey#BOUND}: a function on states, combined
 * (in the {@link Limit} content) with a maximum beyond which states are not
 * explored and an optional increment for iterative deepening.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public enum Bound implements Setting.Kind {
    /** No bound; the state space is explored up to arbitrary depth. */
    NONE("none", "There is no bound; the state space is explored up to arbitrary depth",
        Setting.ContentType.NULL),
    /** Bound on the cost of the path to a state (requires a transition cost). */
    COST("cost", "States are bounded by the cost of the path leading to them",
        Setting.ContentType.LIMIT),
    /** Bound on the total number of graph elements of a state. */
    SIZE("size", "States are bounded by the total number of their graph elements",
        Setting.ContentType.LIMIT),
    /** Bound on the number of nodes of a state. */
    NODES("nodes", "States are bounded by their number of nodes",
        Setting.ContentType.LIMIT),
    /**
     * Bound on the number of edges of given types, as a comma-separated list
     * of <i>label</i>{@code >}<i>bound</i> pairs.
     */
    EDGES("edges", "States are bounded by their number of edges of given types"
        + " (a comma-separated list of label>bound pairs)",
        Setting.ContentType.STRING),
    /**
     * Condition bound: states satisfying the named rule condition (negated if
     * prefixed with {@code !}) are not explored.
     */
    UPTO("upto", "States satisfying the named rule condition"
        + " (negated if prefixed with '!') are not explored",
        Setting.ContentType.STRING),
    /**
     * Condition bound: states satisfying the named rule condition (negated if
     * prefixed with {@code !}) are the last to be explored, and their
     * successors are not.
     */
    INCLUDE("include", "States satisfying the named rule condition"
        + " (negated if prefixed with '!') are the last to be explored",
        Setting.ContentType.STRING),;

    private Bound(String name, String explanation, Setting.ContentType contentType) {
        this.name = name;
        this.explanation = explanation;
        this.contentType = contentType;
    }

    @Override
    public String getName() {
        return this.name;
    }

    private final String name;

    @Override
    public String getExplanation() {
        return this.explanation;
    }

    private final String explanation;

    @Override
    public Setting.ContentType contentType() {
        return this.contentType;
    }

    private final Setting.ContentType contentType;

    /**
     * Content of a non-trivial bound: the maximum value beyond which states
     * are not explored, plus an increment. If the increment is larger than
     * zero, exploration continues with an incremented maximum once all states
     * below the bound have been explored (iterative deepening).
     * @param max the maximum value of the bound function
     * @param increment the increment for iterative deepening; zero if none
     */
    public record Limit(int max, int increment) {
        @Override
        public String toString() {
            return Parser.instance().unparse(this);
        }

        /** Parser for {@link Limit} values, of the form {@code max} or {@code max+increment}. */
        static public class Parser extends nl.utwente.groove.util.parse.Parser.AParser<Limit> {
            private Parser() {
                super("A number <i>max</i>, optionally followed by +<i>inc</i> "
                    + "for iterative deepening", Limit.class);
            }

            @Override
            public Limit parse(String input) throws FormatException {
                int pos = input.indexOf(SEPARATOR);
                String maxText = pos < 0
                    ? input
                    : input.substring(0, pos);
                String incText = pos < 0
                    ? ""
                    : input.substring(pos + 1);
                int max = nl.utwente.groove.util.parse.Parser.natural.parse(maxText);
                int increment = incText.isEmpty()
                    ? 0
                    : nl.utwente.groove.util.parse.Parser.natural.parse(incText);
                return new Limit(max, increment);
            }

            @Override
            public <V extends Limit> String unparse(V value) {
                return value.increment() == 0
                    ? Integer.toString(value.max())
                    : value.max() + "" + SEPARATOR + value.increment();
            }

            /** Separator between the maximum and the increment. */
            private static final char SEPARATOR = '+';

            /** Returns the singleton instance of this parser. */
            public static Parser instance() {
                return instance;
            }

            static final private Parser instance = new Parser();
        }
    }
}
