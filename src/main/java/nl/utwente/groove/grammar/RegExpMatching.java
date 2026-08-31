/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2026 University of Twente
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
package nl.utwente.groove.grammar;

import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.DocumentedEnum;
import nl.utwente.groove.util.Properties.ValueType;

/**
 * Matching discipline for composite regular expressions (those not matched
 * to a single host edge image) under DPO semantics: whether a matched
 * expression must be witnessed by a path that survives the erasure of the
 * same rule application, or is a mere path test on the host graph as
 * matched. Irrelevant under SPO semantics, where all regular expressions
 * uniformly keep the path-test reading.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5, 2026-08")
public enum RegExpMatching implements DocumentedEnum {
    /** Witness paths must survive the application's erasure. */
    FAITHFUL("faithful",
        "Faithful to the DPO approach: the expression must be witnessed by a path "
            + "avoiding the edges that the same rule application erases; where that "
            + "cannot be guaranteed, the rule is an error"),
    /** Pure path tests; witnesses may be erased by the same application. */
    SLOPPY("sloppy",
        "The expression is a pure path test on the host graph as matched; "
            + "a witnessing path may be erased by the same rule application");

    private RegExpMatching(String name, String explanation) {
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

    /** Indicates if this is the sloppy discipline, under which regular
     * expression matching disregards the application's erasure. */
    public boolean isSloppy() {
        return this == SLOPPY;
    }

    /** Value type of {@link RegExpMatching}-valued property keys
     * (see {@link nl.utwente.groove.util.Properties.Key}). */
    public static final ValueType<RegExpMatching> VALUE_TYPE = ValueType.of(RegExpMatching.class);
}
