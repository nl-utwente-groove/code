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
package nl.utwente.groove.algebra.syntax;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.algebra.Sort;
import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.util.Exceptions;
import nl.utwente.groove.util.NoNonNull;

/**
 * Mapping from variable names to sorts.
 * @author Arend Rensink
 * @version $Revision $
 */
@NonNullByDefault
public class SortMap {
    /** Creates an initially empty typing. */
    public SortMap() {
        this.sortMap = new HashMap<>();
    }

    /** Adds the types of another typing to this one, and returns {@code this}.
     * @throws IllegalArgumentException if the other typing conflicts with this one. */
    public SortMap add(SortMap other) {
        for (Map.Entry<String,Sort> e : other.sortMap.entrySet()) {
            add(e.getKey(), e.getValue());
        }
        return this;
    }

    /** Adds a variable plus sort to this sort map.
     * @throws IllegalArgumentException if variable already occurs with another sort. */
    @SuppressWarnings("null")
    public void add(String var, Sort sort) {
        Sort oldType = this.sortMap.put(var, sort);
        if (oldType != null && !oldType.equals(sort)) {
            throw Exceptions
                .illegalArg("Variable %s occurs with distinct sorts %s and %s", var, sort, oldType);
        }
    }

    /** Adds a qualified name plus sort to this sort map.
     * @throws IllegalArgumentException if name already occurs with another sort. */
    public void add(QualName var, Sort type) {
        add(var.toString(), type);
    }

    /** Removes a variable name from this sort map. */
    public void remove(String var) {
        this.sortMap.remove(var);
    }

    /** Removes a qualified name from this sort map. */
    public void remove(QualName var) {
        remove(var.toString());
    }

    private final Map<String,Sort> sortMap;

    /** Indicates if this typing is empty. */
    public boolean isEmpty() {
        return this.sortMap.isEmpty();
    }

    /** Indicates whether this sort map contains a given variable name. */
    public boolean contains(String varName) {
        return this.sortMap.containsKey(varName);
    }

    /** Indicates whether this sort map contains a given qualified name. */
    public boolean contains(QualName qualName) {
        return contains(qualName.toString());
    }

    /** Returns the entries in the underlying sort map. */
    public Set<Map.Entry<String,Sort>> entrySet() {
        return this.sortMap.entrySet();
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

    /**
     * Returns the sort associated with a given (non-<code>null</code>) qualified name.
     * @param qualName the qualified name of which the sort is requested
     * @return the sort of {@code qualName}, or none if {@code qualName} is unknown
     */
    public Optional<Sort> getSort(QualName qualName) {
        Sort sort = this.sortMap.get(qualName.toString());
        return Optional.ofNullable(sort);
    }

    /** Returns the variable names typed by this sort map. */
    public Set<String> keySet() {
        return this.sortMap.keySet();
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
        if (!(obj instanceof SortMap other)) {
            return false;
        }
        if (!this.sortMap.equals(other.sortMap)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return NoNonNull.toString(this.sortMap);
    }

    /** Creates an (initially) empty sort map. */
    static public SortMap newInstance() {
        return new SortMap();
    }

    /** Creates a sort map with a single sorted variable. */
    static public SortMap newInstance(String var, Sort sort) {
        SortMap result = new SortMap();
        result.add(var, sort);
        return result;
    }
}
