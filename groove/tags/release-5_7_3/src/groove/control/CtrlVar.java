/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
package groove.control;

import groove.grammar.QualName;

/**
 * Control variables, consisting of a name and a type.
 * @author Arend Rensink
 * @version $Revision $
 */
public class CtrlVar implements Comparable<CtrlVar> {
    /**
     * Constructs a control variable with a given (non-{@code null}) name, type
     * and distinguishing number.
     * @param scope procedure name of the defining scope (possible {@code null})
     * @param name variable name
     * @param type type of the variable
     */
    private CtrlVar(QualName scope, String name, CtrlType type, int nr) {
        assert name != null && type != null;
        this.scope = scope;
        this.name = name;
        this.type = type;
        this.nr = nr;
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
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (this.scope == null ? 0 : this.scope.hashCode());
        result = prime * result + this.name.hashCode();
        result = prime * result + this.type.hashCode();
        result = prime * result + this.nr;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        CtrlVar other = (CtrlVar) obj;
        if (this.scope == null) {
            if (other.scope != null) {
                return false;
            }
        } else if (!this.scope.equals(other.scope)) {
            return false;
        }
        if (!this.name.equals(other.name)) {
            return false;
        }
        if (!this.type.equals(other.type)) {
            return false;
        }
        if (this.nr != other.nr) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public int compareTo(CtrlVar o) {
        int result = getName().compareTo(o.getName());
        if (result != 0) {
            return result;
        }
        result = getType().compareTo(o.getType());
        if (result != 0) {
            return result;
        }
        return this.nr - o.nr;
    }

    /** Returns the scope name of this control variable. */
    public QualName getScope() {
        return this.scope;
    }

    /** The scope of this control variable. */
    private final QualName scope;

    /** Returns the name of this control variable. */
    public String getName() {
        return this.name;
    }

    /** The name of this control variable. */
    private final String name;

    /** Returns the type of this control variable. */
    public CtrlType getType() {
        return this.type;
    }

    /** The type of this control variable. */
    private final CtrlType type;

    /** The distinguishing number; used to distinguish between wildcard variables. */
    private final int nr;

    /** Returns a fresh wildcard variable of a given type and number. */
    public static CtrlVar wild(CtrlType type, int nr) {
        return new CtrlVar(null, "_", type, nr);
    }
}
