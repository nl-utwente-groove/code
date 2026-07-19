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
 * Feature values for {@link ExploreKey#HEURISTIC}: a function expressing the
 * quality of a state, typically the estimated distance (length or cost of a
 * path) to the goal. The smaller the value, the better the state. There is a
 * strong connection between the heuristic and the goal type.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public enum Heuristic implements Setting.Kind {
    /** No heuristic; all states have the same quality. */
    NONE("none", "No heuristic is used; all states have the same quality"),
    /**
     * Node/edge/node tuple count. Only usable if the goal is given as a
     * complete graph or an unnested, NAC-free rule.
     */
    NEN("nen", "Count of node/edge/node tuples missing with respect to the goal "
        + "(requires the goal to be a complete graph or unnested, NAC-free rule)"),;

    private Heuristic(String name, String explanation) {
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
