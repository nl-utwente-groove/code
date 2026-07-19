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
 * Feature values for {@link ExploreKey#COLLAPSE}: the condition under which a
 * fresh state is considered equivalent to a previously explored one. Other
 * state properties, such as the control frame, must coincide in all cases;
 * the variation lies in the comparison of the underlying graphs.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public enum Collapse implements Setting.Kind {
    /** Inherit the setting from the grammar properties. */
    GRAMMAR("grammar", "As determined by the grammar property 'checkIsomorphism'"),
    /** Graphs are compared for equality; the strongest possible condition. */
    EQUALITY("equality", "Graphs are compared for equality (the strongest condition)"),
    /** Graphs are compared up to isomorphism. */
    ISOMORPHISM("isomorphism", "Graphs are compared up to isomorphism"),
    /**
     * Graphs are considered equivalent if their isomorphism hash codes
     * coincide. This may collapse non-isomorphic states, but saves the time of
     * constructing the actual isomorphism.
     */
    HASH("hash", "Graphs are considered equivalent if their isomorphism hash codes coincide "
        + "(may collapse non-isomorphic states)"),;

    private Collapse(String name, String explanation) {
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
