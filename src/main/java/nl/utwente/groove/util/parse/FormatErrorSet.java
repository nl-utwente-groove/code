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
package nl.utwente.groove.util.parse;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import nl.utwente.groove.graph.Element;
import nl.utwente.groove.graph.GraphMap;
import nl.utwente.groove.util.Fixable;

/**
 * Set of format errors, with additional functionality for
 * adding errors and throwing an exception on the basis of the
 * errors.
 * @author Arend Rensink
 * @version $Revision$
 */
public class FormatErrorSet implements Iterable<FormatError>, Fixable {
    /** Constructs a fresh, empty error set. */
    public FormatErrorSet() {
    }

    /** Constructs a copy of a given set of errors. */
    public FormatErrorSet(FormatErrorSet c) {
        addAll(c);
    }

    /** Constructs a singleton error set. */
    public FormatErrorSet(String message, Object... args) {
        add(message, args);
    }

    /** Returns the (modifiable) set of {@link FormatError}s. */
    private Set<FormatError> getErrorSet() {
        return this.errorSet;
    }

    /** The container set. */
    private final Set<FormatError> errorSet = new LinkedHashSet<>();

    /** Returns the errors in this set, as an unmodifiable set. */
    public Set<FormatError> get() {
        return Collections.unmodifiableSet(getErrorSet());
    }

    /** Adds a format error based on a given error message and set of arguments.
     * @return this object itself, for chaining
     */
    public FormatErrorSet add(String message, Object... args) {
        add(new FormatError(message, args));
        return this;
    }

    /** Adds a format error based on an existing error and set of additional arguments
    * @return this object itself, for chaining
    */
    public FormatErrorSet add(FormatError error, Object... args) {
        add(error.extend(args));
        return this;
    }

    /** Adds a format error to the set, after applying the projection of this set to
     * modify the elements.
    * @return this object itself, for chaining
    */
    public FormatErrorSet add(FormatError e) {
        assert !isFixed();
        getErrorSet().add(e.clone().apply(getProjection()));
        return this;
    }

    /** Copies all errors from a given FormatErrorSet into this one.
    * @return this object itself, for chaining
    */
    public FormatErrorSet addAll(FormatErrorSet other) {
        getProjection().putAll(other.getProjection());
        other.getErrorSet().forEach(this::add);
        return this;
    }

    /** Returns a stream over the errors contained in this error set. */
    public Stream<FormatError> stream() {
        return getErrorSet().stream();
    }

    /** Returns an iterator over the errors contained in this error set. */
    @Override
    public Iterator<FormatError> iterator() {
        return getErrorSet().iterator();
    }

    /** Returns an array containing the errors in this set. */
    public FormatError[] toArray() {
        return getErrorSet().toArray(new FormatError[0]);
    }

    /** Removes all errors from this set. */
    public void clear() {
        assert !isFixed();
        getErrorSet().clear();
        getProjection().clear();
    }

    /** Indicates if this error set is empty. */
    public boolean isEmpty() {
        return getErrorSet().isEmpty();
    }

    /**
     * Throws an exception based on this error set if the error set is nonempty.
     * Does nothing otherwise.
     * @throws FormatException if this error set is nonempty.
     */
    public void throwException() throws FormatException {
        if (!isEmpty()) {
            throw new FormatException(this);
        }
    }

    /** Returns a new format error set in which the context information is transferred.
     * @param map mapping from the context of this error to the context
     * of the result error; or {@code null} if there is no mapping
     */
    public FormatErrorSet transfer(GraphMap map) {
        FormatErrorSet result = new FormatErrorSet();
        stream().map(e -> e.transfer(map)).forEach(result::add);
        return result;
    }

    /** Returns a new format error set in which the context information is extended. */
    public FormatErrorSet extend(Object... objects) {
        FormatErrorSet result = new FormatErrorSet();
        stream().map(e -> e.extend(objects)).forEach(result::add);
        return result;
    }

    /**
     * Modifies the errors currently in this set, as well as all errors added in the future,
     * by applying the inverse a given element map to their graph elements.
     * The method returns this {@link FormatErrorSet} for chaining.
     * @see #applyInverse(GraphMap)
     * @see #apply(Map)
     * @param map mapping from contextual {@link Element}s to current error {@link Element}s
     */
    public FormatErrorSet applyInverse(Map<? extends Element,? extends Element> map) {
        var inverse = new HashMap<Element,Element>();
        map.entrySet().forEach(e -> inverse.put(e.getValue(), e.getKey()));
        return apply(inverse);
    }

    /**
     * Modifies the errors in this set, as well as all errors added in the future,
     * by applying the inverse of a given graph map to their graph elements.
     * The method returns this {@link FormatErrorSet} for chaining.
     * @see #applyInverse(Map)
     * @see #apply(Map)
     * @param map mapping from contextual {@link Element}s to current error {@link Element}s
     */
    public FormatErrorSet applyInverse(GraphMap map) {
        var inverse = new HashMap<Element,Element>();
        map.nodeMap().entrySet().forEach(e -> inverse.put(e.getValue(), e.getKey()));
        map.edgeMap().entrySet().forEach(e -> inverse.put(e.getValue(), e.getKey()));
        return apply(inverse);
    }

    /**
     * Modifies the errors in this set, as well as all errors added in the future,
     * by applying a given element map to their graph elements.
     * The method returns this {@link FormatErrorSet} for chaining.
     * @see #applyInverse(Map)
     * @see #apply(GraphMap)
     * @param map mapping from current error {@link Element}s to contextual {@link Element}s
     */
    public FormatErrorSet apply(Map<? extends Element,? extends Element> map) {
        getErrorSet().forEach(e -> e.apply(map));
        getProjection().putAll(map);
        return this;
    }

    /**
     * Modifies the errors in this set, as well as all errors added in the future,
     * by applying a given graph map to their graph elements.
     * The method returns this {@link FormatErrorSet} for chaining.
     * @see #applyInverse(GraphMap)
     * @see #apply(Map)
     * @param map mapping from current error {@link Element}s to contextual {@link Element}s
     */
    public FormatErrorSet apply(GraphMap map) {
        var combinedMap = new HashMap<Element,Element>();
        combinedMap.putAll(map.nodeMap());
        combinedMap.putAll(map.edgeMap());
        return apply(combinedMap);
    }

    /**
     * Lazily creates and returns the projection map from current error elements to context elements.
     * This is applied to any error added to the set.
     */
    Map<Element,Element> getProjection() {
        var result = this.projection;
        if (result == null) {
            result = this.projection = new HashMap<>();
        }
        return result;
    }

    /** Projection from (inner) graph elements to (outer, contextual) graph elements. */
    private Map<Element,Element> projection;

    @Override
    public int hashCode() {
        return Objects.hash(getErrorSet(), getProjection());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof FormatErrorSet other)) {
            return false;
        }
        return Objects.equals(getErrorSet(), other.getErrorSet())
            && Objects.equals(getProjection(), other.getProjection());
    }

    @Override
    public String toString() {
        return "FormatErrorSet [errorSet=" + getErrorSet() + ", projection=" + getProjection()
            + "]";
    }

    @Override
    public FormatErrorSet clone() {
        return new FormatErrorSet(this);
    }

    @Override
    public boolean setFixed() {
        boolean result = !isFixed();
        if (result) {
            getErrorSet().forEach(FormatError::setFixed);
            this.projection = null;
            this.fixed = true;
        }
        return result;
    }

    @Override
    public boolean isFixed() {
        return this.fixed;
    }

    /** Flag indicating if this object is fixed. */
    private boolean fixed;

    /** A constant fixed empty error set. */
    static public FormatErrorSet EMPTY = new FormatErrorSet();
    static {
        EMPTY.setFixed();
    }
}
