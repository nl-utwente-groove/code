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
package nl.utwente.groove.util;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

/**
 * Non-final re-implementation of Java's {@link Optional}
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public abstract class Opt<T,O extends Opt<? extends T,O>> {
    /**
     * Creates a filled {@link Opt}.
     */
    protected Opt(T value) {
        this.value = Optional.of(value);
    }

    /**
     * Creates an {@link Opt} from a Java {@link Optional}.
     */
    protected Opt(Optional<T> value) {
        this.value = value;
    }

    /** Creates an empty {@link Opt}. */
    protected Opt() {
        this.value = Optional.empty();
    }

    private final Optional<T> value;

    /** Delegates to {@link Optional#get}. */
    public T get() {
        return this.value.get();
    }

    /** Returns the regular Java optional wrapped in this {@link Opt}. */
    public Optional<T> getOptional() {
        return this.value;
    }

    /** Delegates to {@link Optional#isPresent}. */
    public boolean isPresent() {
        return this.value.isPresent();
    }

    /**
     * Tests whether the Opt is empty or fails to satisfy a given predicate.
     * Shortcut for {@code filter(predicate).isPresent()}.
     */
    public boolean isPresent(Predicate<? super T> predicate) {
        return this.value.filter(predicate).isPresent();
    }

    /** Delegates to {@link Optional#isEmpty}. */
    public boolean isEmpty() {
        return this.value.isEmpty();
    }

    /**
     * Tests whether the Opt is empty or fails to satisfy a given predicate.
     * Shortcut for {@code filter(predicate).isEmpty()}.
     */
    public boolean isAbsent(Predicate<? super T> predicate) {
        return this.value.filter(predicate).isEmpty();
    }

    /** Delegates to {@link Optional#ifPresent}. */
    public void ifPresent(Consumer<? super T> action) {
        this.value.ifPresent(action);
    }

    /** Delegates to {@link Optional#ifPresentOrElse}. */
    public void ifPresentOrElse(Consumer<? super T> action, Runnable emptyAction) {
        this.value.ifPresentOrElse(action, emptyAction);
    }

    /** Delegates to {@link Optional#filter}. */
    @SuppressWarnings("unchecked")
    public O filter(Predicate<? super T> predicate) {
        if (!isPresent()) {
            return (O) this;
        } else {
            return predicate.test(this.value.get())
                ? (O) this
                : empty();
        }
    }

    /** Delegates to {@link Optional#map}. */
    public <U> Optional<U> map(Function<? super T,? extends U> mapper) {
        return this.value.map(mapper);
    }

    /** Delegates to {@link Optional#flatMap}. */
    public <U> Optional<U> flatMap(Function<? super T,? extends Optional<? extends U>> mapper) {
        return this.value.flatMap(mapper);
    }

    /**
     * If a value is present, returns an {@code Optional} describing the value,
     * otherwise returns an {@code Optional} produced by the supplying function.
     * Mimics {@link Optional#or(Supplier)}.
     *
     * @param supplier the supplying function that produces an {@code Optional}
     *        to be returned
     * @return returns an {@code Optional} describing the value of this
     *         {@code Optional}, if a value is present, otherwise an
     *         {@code Optional} produced by the supplying function.
     */
    @SuppressWarnings("unchecked")
    public O or(Supplier<O> supplier) {
        return isPresent()
            ? (O) this
            : supplier.get();
    }

    /** Delegates to {@link Optional#stream()}. */
    public Stream<T> stream() {
        return this.value.stream();
    }

    /** Delegates to {@link Optional#orElse}. */
    public T orElse(T other) {
        return this.value.orElse(other);
    }

    /** Delegates to {@link Optional#orElseGet}. */
    public T orElseGet(Supplier<? extends T> supplier) {
        return this.value.orElseGet(supplier);
    }

    /** Delegates to {@link Optional#orElseThrow()}. */
    public T orElseThrow() {
        return this.value.orElseThrow();
    }

    /** Delegates to {@link Optional#orElseThrow(Supplier)}. */
    public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        return this.value.orElseThrow(exceptionSupplier);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof Opt<?,?> other)) {
            return false;
        }
        return this.value.equals(other.value);
    }

    /** Delegates to {@link Optional#hashCode}. */
    @Override
    public int hashCode() {
        return this.value.hashCode();
    }

    /** Delegates to {@link Optional#toString}. */
    @Override
    public String toString() {
        return this.value.toString();
    }

    /** Callback factory method for an empty {@link Opt}-instance. */
    protected abstract O empty();
}
