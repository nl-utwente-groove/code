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
package nl.utwente.groove.control;

import nl.utwente.groove.grammar.QualName;

/**
 * Control variables, consisting of a name and a type.
 * @author Arend Rensink
 * @version $Revision$
 */
public record CtrlVar(QualName scope, String name, CtrlType type, int nr)
    implements Comparable<CtrlVar> {
    /**
     * Constructs a control variable with a given (non-{@code null}) name, type
     * and distinguishing number.
     * @param scope procedure name of the defining scope (possible {@code null})
     * @param name variable name
     * @param type type of the variable
     */
    public CtrlVar {
        assert name != null && type != null;
    }

    /** Constructs a control variable with a given (non-{@code null}) name and type.
     * @param scope procedure name of the defining scope (possible {@code null})
     * @param name variable name
     * @param type type of the variable
     */
    public CtrlVar(QualName scope, String name, CtrlType type) {
        this(scope, name, type, 0);
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public int compareTo(CtrlVar o) {
        int result = name().compareTo(o.name());
        if (result != 0) {
            return result;
        }
        result = type().compareTo(o.type());
        if (result != 0) {
            return result;
        }
        return this.nr - o.nr;
    }

    /** Returns a fresh wildcard variable of a given type and number. */
    public static CtrlVar wild(CtrlType type, int nr) {
        return new CtrlVar(null, "_", type, nr);
    }
}
