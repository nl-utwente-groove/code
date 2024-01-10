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

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.grammar.QualName;

/**
 * Control variables, consisting of an optional scope (being the defining procedure
 * within which it is a local variable), name and (control) type.
 * @author Arend Rensink
 * @version $Revision$
 */
public record CtrlVar(@Nullable QualName scope, @NonNull String name, @NonNull CtrlType type)
    implements Comparable<CtrlVar> {
    /**
     * Constructs a control variable with a given scope, name and type.
     * @param scope procedure name of the defining scope (possible {@code null})
     * @param name variable name
     * @param type type of the variable
     */
    public CtrlVar {
        assert name != null && type != null;
    }

    /** Constructs a control variable with no scope and a given (non-{@code null}) name and type.
     * @param name variable name
     * @param type type of the variable
     */
    public CtrlVar(String name, CtrlType type) {
        this(null, name, type);
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public int compareTo(CtrlVar o) {
        int result = 0;
        var scope = scope();
        if (scope == null) {
            if (o.scope() != null) {
                return -1;
            }
        } else {
            if (o.scope() == null) {
                return 1;
            } else {
                result = scope.compareTo(o.scope());
            }
        }
        if (result != 0) {
            return result;
        }
        result = name().compareTo(o.name());
        if (result != 0) {
            return result;
        }
        return type().compareTo(o.type());
    }
}
