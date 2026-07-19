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
 * Feature values for {@link ExploreKey#OUTCOME}: the desired outcome of the
 * goal condition ({@link ExploreKey#GOAL}). The goal kinds {@link Goal#NONE}
 * and {@link Goal#FINAL} require {@link #SATISFY}.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public enum Outcome implements Setting.Kind {
    /** A result is a state (or trace) satisfying the goal condition. */
    SATISFY("satisfy", "A result satisfies the goal condition"),
    /** A result is a state (or trace) violating the goal condition. */
    VIOLATE("violate", "A result violates the goal condition"),;

    private Outcome(String name, String explanation) {
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
