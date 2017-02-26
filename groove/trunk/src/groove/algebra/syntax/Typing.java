/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
package groove.algebra.syntax;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import groove.algebra.Sort;
import groove.util.NoNonNull;

/**
 * Variable typing.
 * A typing is a mapping from variable names to sorts.
 * @author Arend Rensink
 * @version $Revision $
 */
@NonNullByDefault
public class Typing {
    /** Creates an initially empty typing. */
    public Typing() {
        this.sortMap = new HashMap<>();
    }

    /** Adds the types of another typing to this one.
     * @throws IllegalArgumentException if the other typing conflicts with this one. */
    public void add(Typing other) {
        for (Map.Entry<String,Sort> e : other.sortMap.entrySet()) {
            add(e.getKey(), e.getValue());
        }
    }

    /** Adds a variable plus type to this typing.
     * @throws IllegalArgumentException if variable already occurs with another type. */
    @SuppressWarnings("null")
    public void add(String var, Sort type) {
        Sort oldType = this.sortMap.put(var, type);
        if (oldType != null && !oldType.equals(type)) {
            throw new IllegalArgumentException(String
                .format("Variable % occurs with distinct sorts %s and %s", var, type, oldType));
        }
    }

    private final Map<String,Sort> sortMap;

    /** Indicates if this typing is empty. */
    public boolean isEmpty() {
        return this.sortMap.isEmpty();
    }

    /**
     * Returns the sort associated with a given (non-<code>null</code>) variable name.
     * @param varName the variable name of which the sort is requested
     * @return the sort of {@code varName}, or none if {@code varName} is unknown
     */
    public Optional<Sort> getSort(String varName) {
        Sort sort = this.sortMap.get(varName);
        return Optional.ofNullable(sort);
    }

    @Override
    public int hashCode() {
        return this.sortMap.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Typing)) {
            return false;
        }
        Typing other = (Typing) obj;
        if (!this.sortMap.equals(other.sortMap)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return NoNonNull.toString(this.sortMap);
    }

    /** Creates an (initially) empty typing. */
    static public Typing emptyTyping() {
        return new Typing();
    }

    /** Creates a typing with a single type mapping. */
    static public Typing singletonTyping(String var, Sort type) {
        Typing result = new Typing();
        result.add(var, type);
        return result;
    }
}
