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
 * Feature values for {@link ExploreKey#ALGEBRA}: the interpretation of data
 * values during exploration.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public enum Algebra implements Setting.Kind {
    /** Inherit the setting from the grammar properties. */
    GRAMMAR("grammar", "As determined by the grammar property 'algebraFamily'"),
    /** Concrete Java-based values. */
    DEFAULT("default", "Java-based representation of data values"),
    /** Arbitrary-precision values. */
    BIG("big", "Arbitrary-precision representation of data values"),
    /** Collapsed single-point interpretation, for abstraction. */
    POINT("point", "All data values are collapsed to a single point"),
    /** Symbolic term representation. */
    TERM("term", "Data values are represented symbolically, as terms"),;

    private Algebra(String name, String explanation) {
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
