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

    /** Adds a format error to the set.
    * @return this object itself, for chaining
    */
    public FormatErrorSet add(FormatError e) {
        assert !isFixed();
        getErrorSet().add(e.cloneFor(this));
        return this;
    }

    /** Copies all errors from a given FormatErrorSet into this one.
    * @return this object itself, for chaining
    */
    public FormatErrorSet addAll(FormatErrorSet other) {
        other.getErrorSet().forEach(this::add);
        getProjection().putAll(other.getProjection());
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

    /** Adds a mapping from context elements to error elements.
     * The inverse of this mapping is used to generate the elements returned by {@link FormatError#getElements()}
     * called on the errors in this set.
     * The method returns this {@link FormatErrorSet} for chaining.
     * @see #wrap(GraphMap)
     * @see #unwrap(Map)
     * @param map mapping from contextual {@link Element}s to error {@link Element}s
     */
    public FormatErrorSet wrap(Map<? extends Element,? extends Element> map) {
        var projection = getProjection();
        map.entrySet().forEach(e -> projection.put(e.getValue(), e.getKey()));
        return this;
    }

    /** Adds a {@link GraphMap} from context elements to error elements.
     * The inverse of this mapping is used to generate the elements returned by {@link FormatError#getElements()}
     * called on the errors in this set.
     * The method returns this {@link FormatErrorSet} for chaining.
     * @see #wrap(Map)
     * @see #unwrap(Map)
     * @param map mapping from contextual {@link Element}s to error {@link Element}s
     */
    public FormatErrorSet wrap(GraphMap map) {
        return wrap(map.nodeMap()).wrap(map.edgeMap());
    }

    /** Adds a mapping from error elements to context elements.
     * This mapping is used to generate the elements returned by {@link FormatError#getElements()}
     * called on the errors in this set.
     * The method returns this {@link FormatErrorSet} for chaining.
     * @see #wrap(Map)
     * @see #unwrap(GraphMap)
     * @param map mapping from error {@link Element}s to contextual {@link Element}s
     */
    public FormatErrorSet unwrap(Map<? extends Element,? extends Element> map) {
        getProjection().putAll(map);
        return this;
    }

    /** Adds a {@link GraphMap} from error elements to context elements.
     * This mapping is used to generate the elements returned by {@link FormatError#getElements()}
     * called on the errors in this set.
     * The method returns this {@link FormatErrorSet} for chaining.
     * @see #wrap(GraphMap)
     * @see #unwrap(Map)
     * @param map mapping from contextual {@link Element}s to error {@link Element}s
     */
    public FormatErrorSet unwrap(GraphMap map) {
        return unwrap(map.nodeMap()).unwrap(map.edgeMap());
    }

    /** Lazily creates and returns the projection map from error elements to context elements. */
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
