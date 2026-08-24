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

import nl.utwente.groove.util.DocumentedEnum;
import nl.utwente.groove.util.Properties.ValueType;

/**
 * Transformation semantics of a grammar, combining two dimensions:
 * whether the host and rule graphs are multigraphs (i.e., may contain
 * parallel edges) or simple graphs, and whether matching and transformation
 * follow the single-pushout (SPO) or double-pushout (DPO) approach.
 * @author Arend Rensink
 * @version $Revision$
 */
public enum Semantics implements DocumentedEnum {
    /** Simple graphs under single-pushout semantics: the classic GROOVE
     * behaviour. */
    SPO_SIMPLE("SPO-simple",
        "Simple graphs, without parallel edges, under single-pushout semantics "
            + "(the classic GROOVE behaviour)"),
    /** Multigraphs under single-pushout semantics. */
    SPO_MULTI("SPO-multi",
        "Multigraphs (with parallel edges) under single-pushout semantics: matches may "
            + "identify deleted with preserved elements, and deletion wins"),
    /** Multigraphs under double-pushout semantics. */
    DPO("DPO",
        "Multigraphs (with parallel edges) under double-pushout semantics: the identification "
            + "condition forbids matches identifying a deleted element with any other element, "
            + "and the dangling condition (checkDangling) is implied");

    private Semantics(String name, String explanation) {
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

    /** Indicates if this semantics admits parallel edges, i.e., uses multigraphs.
     * If not, host and rule graphs are simple.
     */
    public boolean isMulti() {
        return this != SPO_SIMPLE;
    }

    /** Indicates if this semantics imposes the double-pushout approach,
     * in particular the identification condition on deleted elements.
     */
    public boolean isDPO() {
        return this == DPO;
    }

    /** Value type of {@link Semantics}-valued property keys
     * (see {@link nl.utwente.groove.util.Properties.Key}). */
    public static final ValueType<Semantics> VALUE_TYPE = ValueType.of(Semantics.class);
}
