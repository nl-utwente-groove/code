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
 * Feature values for {@link ExploreKey#NEXT}: the choice of the next state
 * to explore from the frontier. If a heuristic is used, the choice is only
 * made between candidates of the same quality.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public enum NextState implements Setting.Kind {
    /** Explore the oldest state in the frontier first. */
    OLDEST("oldest", "The oldest state in the frontier is explored next (breadth-first search)"),
    /** Explore the most recently added state first. */
    NEWEST("newest", "The newest state in the frontier is explored next (depth-first search)"),
    /** Explore a random state from the frontier. */
    RANDOM("random", "A random state in the frontier is explored next"),;

    private NextState(String name, String explanation) {
        this.name = name;
        this.explanation = explanation;
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
        return Setting.ContentType.NULL;
    }
}
