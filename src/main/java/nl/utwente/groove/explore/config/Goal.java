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
 * Feature values for {@link ExploreKey#GOAL}: the condition that determines
 * whether a result has been found. Whether the condition is to be satisfied or
 * violated is determined by {@link ExploreKey#OUTCOME}. The content of the
 * name- and formula-carrying kinds is kept as a string here; it is resolved
 * against the grammar when the configuration is put into effect.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public enum Goal implements Setting.Kind {
    /** No goal; exploration continues until the frontier is empty. */
    NONE("none", "There is no goal; exploration continues until the frontier is empty",
        Setting.ContentType.NULL),
    /** Final states, i.e., states without outgoing transitions. */
    FINAL("final", "A state without outgoing transitions",
        Setting.ContentType.NULL),
    /** States isomorphic to a given graph. */
    GRAPH("graph", "A state whose graph is isomorphic to the named graph",
        Setting.ContentType.STRING),
    /** States satisfying a given rule condition. */
    RULE("rule", "A state whose graph satisfies the named rule condition",
        Setting.ContentType.STRING),
    /** States satisfying a propositional formula over rule conditions. */
    FORMULA("formula", "A state whose graph satisfies a propositional formula over rule conditions",
        Setting.ContentType.STRING),
    /** Traces satisfying an LTL formula. */
    LTL("ltl", "A trace satisfying an LTL formula",
        Setting.ContentType.STRING),
    /** State spaces satisfying a CTL formula. */
    CTL("ctl", "A state space satisfying a CTL formula",
        Setting.ContentType.STRING),;

    private Goal(String name, String explanation, Setting.ContentType contentType) {
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
