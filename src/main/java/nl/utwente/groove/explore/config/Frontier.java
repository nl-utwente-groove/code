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
 * Feature values for {@link ExploreKey#FRONTIER}: the number of unexplored
 * states kept in the frontier. A restricted frontier deliberately gives up on
 * full exploration of the state space.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public enum Frontier implements Setting.Kind {
    /** Unrestricted frontier. */
    COMPLETE("complete", "No states are dropped from the frontier",
        Setting.ContentType.NULL),
    /** Single-state frontier, giving rise to a linear search. */
    SINGLE("single", "The frontier holds a single state, giving rise to a linear search",
        Setting.ContentType.NULL),
    /** Frontier restricted to a given size, giving rise to a beam search. */
    BEAM("beam", "The frontier is restricted to a given maximum size (beam search)",
        Setting.ContentType.INTEGER),;

    private Frontier(String name, String explanation, Setting.ContentType contentType) {
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
}
