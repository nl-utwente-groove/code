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
package groove.rel;

import groove.graph.LabelKind;

/**
 * Encodes a label variable (which may occur in a wildcard expression).
 * Essentially consists of a name and a kind, corresponding to the
 * label kind of the allowed values.
 * @author Arend Rensink
 * @version $Revision $
 */
public class LabelVar {
    /**
     * Constructs a label variable from a given name and kind.
     * @param name name of the label variable; non-{@code null}
     * @param kind kind of the label variable.
     */
    public LabelVar(String name, LabelKind kind) {
        super();
        this.name = name;
        this.kind = kind;
    }

    /** Returns the name of the variable. */
    public final String getName() {
        return this.name;
    }

    /** 
     * Returns the kind of this label variable.
     */
    public final LabelKind getKind() {
        return this.kind;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.kind.hashCode();
        result =
            prime * result + ((this.name == null) ? 0 : this.name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        LabelVar other = (LabelVar) obj;
        if (this.kind != other.kind) {
            return false;
        }
        if (this.name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!this.name.equals(other.name)) {
            return false;
        }
        return true;
    }

    /** The name of the label variable. */
    private final String name;
    /** The kind of the label variable. */
    private final LabelKind kind;
}
