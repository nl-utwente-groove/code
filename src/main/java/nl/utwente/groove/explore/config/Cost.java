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
 * Feature values for {@link ExploreKey#COST}: the cost of a single transition.
 * If transitions have a cost, the total cost of a path plays a role in
 * exploration: the task is to find the least costly solution.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public enum Cost implements Setting.Kind {
    /** Transitions have no cost; the cost of a path is ignored. */
    NONE("none", "Transitions have no cost; the cost of a path is ignored"),
    /** Every transition has the same cost; path cost equals path length. */
    UNIFORM("uniform", "Every transition has cost 1, so the cost of a path equals its length"),
    /**
     * Rule-dependent cost: fixed for the rule or exposed as a rule parameter,
     * determined per rule.
     */
    RULE("rule", "Each rule application has a cost, fixed per rule or exposed as a rule parameter"),;

    private Cost(String name, String explanation) {
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
