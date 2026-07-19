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
 * Feature values for {@link ExploreKey#SUCCESSOR}: the choice which successors
 * of the selected state to generate and put into the frontier. With one of the
 * single-successor values, the explored state re-enters the frontier as long
 * as it has further unexplored outgoing transitions.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public enum Successor implements Setting.Kind {
    /** Generate all unexplored successors, in natural order. */
    ALL("all", "All unexplored successor states are generated, in their natural order"),
    /** Generate all unexplored successors, in random order. */
    ALL_RANDOM("all-random", "All unexplored successor states are generated, in random order"),
    /** Generate the first unexplored successor only. */
    SINGLE("single", "Only the first unexplored successor state is generated"),
    /** Generate a single, randomly chosen unexplored successor. */
    SINGLE_RANDOM("single-random",
        "Only a single, randomly chosen unexplored successor state is generated"),;

    private Successor(String name, String explanation) {
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
