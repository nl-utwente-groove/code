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
package nl.utwente.groove.util;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import nl.utwente.groove.util.parse.FormatException;

/**
 * Class wrapping either a value or a format exception.
 * @author Rensink
 * @version $Revision $
 */
public class Fragile<T> {
    /**
     * Constructs a non-erroneous value.
     */
    private Fragile(T value) {
        this.value = Optional.of(value);
        this.error = Optional.empty();
    }

    /**
     * Constructs an error value from a given exception.
     */
    private Fragile(FormatException error) {
        this.value = Optional.empty();
        this.error = Optional.of(error);
    }

    private final Optional<T> value;
    private final Optional<FormatException> error;

    /** Indicates if there is a non-erroneous value in this object. */
    public boolean isPresent() {
        return this.value.isPresent();
    }

    /** Returns the optional value in this object. */
    public Optional<T> value() {
        return this.value;
    }

    /** Retrieves the value in this object, if there is any.
     * @throws NoSuchElementException if there is no (non-erroneous) value in this object.
     */
    public T get() throws NoSuchElementException {
        return this.value.get();
    }

    /** Indicates if this object represents an error. */
    public boolean hasError() {
        return this.error.isPresent();
    }

    /** Returns the optional error in this object. */
    public Optional<FormatException> error() {
        return this.error;
    }

    /** Throws the error in this fragile object, if there is any. */
    public void throwError() throws FormatException {
        if (this.error.isPresent()) {
            throw this.error.get();
        }
    }

    /**
     * If this contains a non-erroneous value, returns it, otherwise throws the wrapped error.
     * @throws FormatException if the value is erroneous
     */
    public T getOrThrow() throws FormatException {
        return this.value.orElseThrow(() -> this.error.get());
    }

    /**
     * If a value is present, performs the given action with the value,
     * otherwise does nothing.
     *
     * @param action the action to be performed, if a value is present
     * @throws NullPointerException if value is present and the given action is
     *         {@code null}
     */
    public void ifPresent(Consumer<? super T> action) {
        this.value.ifPresent(action);
    }

    /**
     * If a non-erroneous value is present, performs the given action with the value,
     * otherwise performs the given error action on the error.
     *
     * @param action the action to be performed, if a value is present
     * @param errAction the action to be performed if there is an error
     * @throws NullPointerException if a value is present and the given action
     *         is {@code null}, or no value is present and the given empty-based
     *         action is {@code null}.
     * @since 9
     */
    public void ifPresentOrElse(Consumer<? super T> action,
                                Consumer<? super FormatException> errAction) {
        this.value.ifPresentOrElse(action, () -> this.error.ifPresent(errAction));
    }

    /**
     * If a value is present, returns a {@code Fragile} containing
     * the result of applying the given mapping function to
     * the value, otherwise returns the error in this object, wrapped in a new {@link Fragile}.
     * @throws NullPointerException if the mapping function is {@code null}
     */
    public <U> Fragile<U> map(Function<? super T,? extends U> mapper) {
        Objects.requireNonNull(mapper);
        if (isPresent()) {
            return of(mapper.apply(this.value.get()));
        } else {
            return error(this.error.get());
        }
    }

    /**
     * If a value is present, returns a sequential {@link Stream} containing
     * only that value, otherwise returns an empty {@code Stream}.
     *
     * @apiNote
     * This method can be used to transform a {@code Stream} of optional
     * elements to a {@code Stream} of present value elements:
     * <pre>{@code
     *     Stream<Optional<T>> os = ..
     *     Stream<T> s = os.flatMap(Optional::stream)
     * }</pre>
     *
     * @return the optional value as a {@code Stream}
     * @since 9
     */
    public Stream<T> stream() {
        return this.value.stream();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        return obj instanceof Fragile<?> other && this.value.equals(other.value)
            && this.error.equals(other.error);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.value) + Objects.hashCode(this.error);
    }

    @Override
    public String toString() {
        return this.value.isPresent()
            ? "Value: " + this.value.get()
            : "Error: " + this.error.get();
    }

    /** Wraps a non-erroneous value. */
    static public <T> Fragile<T> of(T value) {
        return new Fragile<>(value);
    }

    /**
     * Wraps a given exception.
     */
    static public <T> Fragile<T> error(FormatException error) {
        return new Fragile<>(error);
    }

    /**
     * Constructs an error value from a given error message and arguments.
     */
    static public <T> Fragile<T> error(String format, Object... args) {
        return new Fragile<>(new FormatException(format, args));
    }
}
