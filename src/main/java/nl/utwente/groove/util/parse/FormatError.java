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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.util.Fixable;
import nl.utwente.groove.util.Relation;

/**
 * Class encoding a single message reporting an error in a graph view.
 * <p>
 * Apart from its message, an error holds <i>context</i> information passed in
 * as constructor parameters: an insertion-ordered set of objects giving
 * information about where the error occurs. The context is opaque at this
 * level; interpretation is up to the consumers (see, e.g.,
 * {@code nl.utwente.groove.grammar.model.ErrorLocation}). {@link Integer}
 * parameters are collected separately as {@link #getNumbers() numbers}
 * (typically line and column numbers), and arrays, collections and nested
 * {@link FormatError}s are flattened into their constituents.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class FormatError implements Comparable<FormatError>, Fixable, Cloneable {
    /**
     * Constructs an error of severity {@link Severity#ERROR}, consisting of a
     * message to be formatted.
     * The actual message is constructed by calling {@link String#format(String, Object...)}
     * The parameters are interpreted as giving information about the error;
     * in particular, a {@link Severity} parameter sets the severity level.
     */
    public FormatError(String message, @Nullable Object... pars) {
        this.message = String.format(message, pars);
        for (var par : pars) {
            addContext(par);
        }
    }

    /**
     * Constructs an error of a given severity, consisting of a message to be formatted.
     * Equivalent to passing the severity among the parameters.
     * @see #FormatError(String, Object...)
     */
    public FormatError(Severity severity, String message, @Nullable Object... pars) {
        this(message, pars);
        this.severity = severity;
    }

    /**
     * Adds a context value from a given object.
     * Arrays, collections and nested errors are flattened; {@link Integer}s
     * are added to the {@link #getNumbers() numbers}; a {@link Severity}
     * sets the severity level; all other objects are
     * added to the {@link #getContext() context}. {@code null} values are
     * silently ignored.
     */
    private void addContext(@Nullable Object par) {
        assert !isFixed();
        if (par == null) {
            // null parameters carry no context information
        } else if (par instanceof Object[] a) {
            Arrays.stream(a).forEach(this::addContext);
        } else if (par instanceof Collection<?> c) {
            c.forEach(this::addContext);
        } else if (par instanceof FormatError e) {
            e.getContext().forEach(this::addContext);
            this.numbers.addAll(e.getNumbers());
            // an error wrapping another error is a contextualisation of the
            // nested error, and so takes over its severity
            this.severity = e.getSeverity();
        } else if (par instanceof Severity s) {
            this.severity = s;
        } else if (par instanceof Integer i) {
            this.numbers.add(i);
        } else {
            this.context.add(par);
        }
    }

    /** Returns the severity level of this error. */
    public final Severity getSeverity() {
        return this.severity;
    }

    /** Indicates if this error is of severity {@link Severity#ERROR}. */
    public final boolean isBlocking() {
        return getSeverity().isBlocking();
    }

    /** The severity level of this error. */
    private Severity severity = Severity.ERROR;

    /** Compares the severity, message, numbers and context. */
    @Override
    public boolean equals(@Nullable Object obj) {
        boolean result;
        if (obj instanceof FormatError err) {
            result = getSeverity() == err.getSeverity();
            result &= toString().equals(err.toString());
            result &= getNumbers().equals(err.getNumbers());
            result &= getContext().equals(err.getContext());
        } else {
            result = false;
        }
        return result;
    }

    /** The hash code is based on the severity, message, numbers and context. */
    @Override
    public int hashCode() {
        int result = toString().hashCode();
        result += getNumbers().hashCode();
        result += getContext().hashCode();
        // the enum's own hash code is identity-based, hence not deterministic
        result = result * 31 + getSeverity().ordinal();
        return result;
    }

    @Override
    public String toString() {
        return this.message;
    }

    /** Returns the message of this format error
     * in which all '%' characters have been replaced by '%%', so
     * that it can be used as input to {@link String#format(String, Object...)} without
     * expecting any arguments.
     */
    public String toFormattableString() {
        var message = this.message;
        StringBuffer result = new StringBuffer(message.length() + 1);
        for (int i = 0; i < message.length(); i++) {
            char c = message.charAt(i);
            if (c == '%') {
                result.append("%%");
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    /** The error message. */
    private final String message;

    /**
     * Compares the message and then the context.
     * The context comparison is a best-effort deterministic order over the
     * otherwise opaque context objects, by class name and string rendering.
     */
    @Override
    public int compareTo(FormatError other) {
        int result = toString().compareTo(other.toString());
        if (result == 0) {
            result = Integer.compare(getContext().size(), other.getContext().size());
        }
        if (result == 0) {
            Iterator<Object> myIter = getContext().iterator();
            Iterator<Object> otherIter = other.getContext().iterator();
            while (result == 0 && myIter.hasNext()) {
                Object mine = myIter.next();
                Object his = otherIter.next();
                result = mine.getClass().getName().compareTo(his.getClass().getName());
                if (result == 0) {
                    result = String.valueOf(mine).compareTo(String.valueOf(his));
                }
            }
        }
        return result;
    }

    /** Returns the context objects of this error, in insertion order.
     * The context is opaque at this level; interpretation is up to the caller.
     * May be empty. */
    public Collection<Object> getContext() {
        return Collections.unmodifiableCollection(this.context);
    }

    /** Returns the context objects of this error that are instances of a
     * given type, in insertion order. May be empty. */
    public <T> List<T> getContext(Class<T> type) {
        return this.context
            .stream()
            .filter(type::isInstance)
            .map(type::cast)
            .collect(Collectors.toList());
    }

    /** The (insertion-ordered, duplicate-free) context of the error. */
    private final Collection<Object> context = new LinkedHashSet<>();

    /** Modifies the context of this error by applying a mapping to it:
     * the images of context objects in the map are added to the context.
     * Returns this error for chaining.
     */
    FormatError apply(Map<?,?> map) {
        if (!map.isEmpty()) {
            var newContext = new ArrayList<Object>(this.context.size());
            for (var e : this.context) {
                var i = map.get(e);
                if (i != null) {
                    newContext.add(i);
                }
            }
            this.context.addAll(newContext);
        }
        return this;
    }

    /** Modifies the context of this error by applying a relation to it:
     * the images of context objects in the relation are added to the context.
     * Returns this error for chaining.
     */
    FormatError apply(Relation<?,?> relation) {
        if (!relation.isEmpty()) {
            var newContext = new ArrayList<Object>(this.context.size());
            for (var e : this.context) {
                var i = relation.get(e);
                if (i != null) {
                    newContext.addAll(i);
                }
            }
            this.context.addAll(newContext);
        }
        return this;
    }

    /** Returns a list of numbers associated with the error; typically,
     * line and column numbers. May be empty. */
    public final List<Integer> getNumbers() {
        return this.numbers;
    }

    /** List of numbers; typically the line and column number in a textual program. */
    private final List<Integer> numbers = new ArrayList<>();

    /** Returns a new format error in which the context information is transferred modulo
     * an element map. The new error has no parent.
     * @param map mapping from the context of this error to the context
     * of the result error
     */
    FormatError transfer(Map<?,?> map) {
        var result = this;
        if (!map.isEmpty()) {
            result = clone(map);
        }
        return result;
    }

    /** Returns a new format error that extends this one with context information.
     * The new error has no parent as yet.
     */
    public FormatError extend(@Nullable Object... pars) {
        FormatError result = clone(null);
        for (var par : pars) {
            result.addContext(par);
        }
        return result;
    }

    @Override
    public FormatError clone() {
        return clone(null);
    }

    /** Returns a clone of this error, with no parent.
     * An optional map determines how the context objects are mapped.
     */
    private FormatError clone(@Nullable Map<?,?> map) {
        var result = new FormatError(getSeverity(), toFormattableString());
        result.numbers.addAll(getNumbers());
        for (var arg : getContext()) {
            var newArg = map != null && map.containsKey(arg)
                ? map.get(arg)
                : arg;
            assert newArg != null; // map.get(arg) is non-null since map.containsKey(arg)
            result.addContext(newArg);
        }
        return result;
    }

    @Override
    public boolean setFixed() {
        boolean result = !isFixed();
        if (result) {
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
}
