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
package nl.utwente.groove.grammar.type;

import nl.utwente.groove.util.parse.FormatException;

/**
 * Pair consisting of lower and upper bound.
 * @author Arend Rensink
 * @version $Revision$
 */
public record Multiplicity(int lower, int upper) {
    /**
     * Constructor for a constant or unbounded multiplicity.
     * @param lower the lower bound of the multiplicity.
     * @param unbounded if {@code true}, the upper bound is unbounded, otherwise
     * it equals {@code lower}.
     */
    public Multiplicity(int lower, boolean unbounded) {
        this(lower, unbounded ? Integer.MAX_VALUE : lower);
    }

    @Override
    public String toString() {
        if (lower() == upper() && !isUnbounded()) {
            // constant value
            return "" + lower();
        }
        if (lower() == 0 && isUnbounded()) {
            // this is the default
            return "*";
        }
        return "" + lower() + ".." + (isUnbounded() ? "*" : upper());
    }

    /** Indicates if the upper bound is unbounded (i.e., equals {@link Integer#MAX_VALUE}.  */
    public boolean isUnbounded() {
        return upper() == Integer.MAX_VALUE;
    }

    /** Returns a multiplicity by parsing a string as
     * either {@code low..up} (with {@code up} either an integer or {@link #UNBOUNDED}),
     * {@code low} or {@link #UNBOUNDED}.
     * @param text the text to be parsed
     * @return the corresponding multiplicity; calling {@link #toString()} on it
     * will return the value of {@code text}
     * @throws FormatException if the input text was not correctly formatted
     */
    public static Multiplicity parse(String text) throws FormatException {
        int lower, upper;
        try {
            int dotdot = text.indexOf(MULT_SEPARATOR);
            if (dotdot < 0) {
                if (UNBOUNDED.equals(text)) {
                    lower = 0;
                    upper = Integer.MAX_VALUE;
                } else {
                    // the multiplicity is a single value
                    lower = upper = Integer.parseInt(text);
                }
            } else {
                lower = Integer.parseInt(text.substring(0, dotdot));
                String upperText = text.substring(dotdot + MULT_SEPARATOR.length());
                if (upperText.equals(UNBOUNDED)) {
                    upper = Integer.MAX_VALUE;
                } else {
                    upper = Integer.parseInt(upperText);
                }
            }
        } catch (NumberFormatException e) {
            throw new FormatException("Malformed multiplicity value %s", text);
        }
        if (lower < 0) {
            throw new FormatException("Negative lower bound %d", lower);
        } else if (lower > upper) {
            throw new FormatException("Lower bound %d larger than upper bound %d", lower, upper);
        }
        return new Multiplicity(lower, upper);
    }

    /** Checks if a given integer is in the range of the multiplicity. */
    public boolean inRange(int count) {
        return lower() <= count && count <= upper();
    }

    /** Separator sequence in a multiplicity value. */
    static public final String MULT_SEPARATOR = "..";
    /** Text representation of infinite upper bound. */
    static public final String UNBOUNDED = "*";
}
