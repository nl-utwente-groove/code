/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
package groove.explore;

/** Verbosity in reporting an exploration. */
public enum Verbosity {
    /** Low: only error messages. */
    LOW,
    /** Medium: basic reporting. */
    MEDIUM,
    /** High: full diagnostic output. */
    HIGH;

    /** Tests if this verbosity level is at least as high as another. */
    public boolean subsumes(Verbosity other) {
        return this.ordinal() >= other.ordinal();
    }

    /** Tests if this verbosity level is {@link #HIGH}. */
    public boolean isHigh() {
        return this == HIGH;
    }

    /** Tests if this verbosity level is {@link #LOW}. */
    public boolean isLow() {
        return this == LOW;
    }

    /** Returns the numerical value corresponding to this level. */
    public int getLevel() {
        return LOWEST + ordinal();
    }

    /**
     * Returns the verbosity corresponding to a numeric level.
     * For historical reasons, the range is from {@link #LOWEST} ({@link #LOW})
     * to {@link #HIGHEST} ({@link #HIGH}).
     * @param level a number between {@code -1} and {@code 2} inclusive
     * @return the verbosity value corresponding to the input level
     */
    public static Verbosity getVerbosity(int level) {
        return Verbosity.values()[level - LOWEST];
    }

    /** The lowest numerical verbosity level. */
    public static final int LOWEST = 0;
    /** The highest numerical verbosity level. */
    public static final int HIGHEST = LOWEST + Verbosity.values().length;
}
