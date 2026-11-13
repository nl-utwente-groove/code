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
package nl.utwente.groove.annotation;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.util.AIGenerated;

/**
 * Immutable sequence of values of one {@link UserType}.
 * Unlike the sequences of system sorts, this one is only allowed as the last parameter
 * of a {@link UserOperation}, not as a component of a user type.
 * The sequence knows the type of its elements and checks it whenever one is added,
 * so that it cannot hold a value of another type, nor {@code null}.
 * Equality and hash code rely on those of the elements, and the hash code is computed
 * only once; this presupposes that the elements are immutable, as user types are.
 * @param <T> the user type of the elements
 * @see Seq
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.2, 2026-11")
@NonNullByDefault
public final class UserSeq<T extends Record> implements Seq<T> {
    /** Wraps a given array of non-{@code null} elements of the given type, without copying it. */
    private UserSeq(Class<T> type, T[] elems) {
        this.type = type;
        this.elems = elems;
    }

    /** Returns the type of the elements of this sequence. */
    public Class<T> type() {
        return this.type;
    }

    @Override
    public int size() {
        return this.elems.length;
    }

    /**
     * Returns the element at a given position.
     * @param index the position, counting from 0
     * @throws IndexOutOfBoundsException if the index is negative or not smaller than {@link #size()}
     */
    public T get(int index) {
        return this.elems[index];
    }

    @Override
    public T value(int index) {
        return get(index);
    }

    /**
     * Returns a sequence that differs from this one in the element at a given position.
     * @param index the position, counting from 0
     * @param elem the element at that position in the result
     * @throws IndexOutOfBoundsException if the index is negative or not smaller than {@link #size()}
     */
    public UserSeq<T> with(int index, T elem) {
        T[] result = this.elems.clone();
        result[index] = checked(this.type, elem);
        return new UserSeq<>(this.type, result);
    }

    /** Returns a sequence that consists of this one followed by a given element. */
    public UserSeq<T> append(T elem) {
        T[] result = Arrays.copyOf(this.elems, this.elems.length + 1);
        result[this.elems.length] = checked(this.type, elem);
        return new UserSeq<>(this.type, result);
    }

    /** Returns the elements of this sequence, as a fresh array. */
    public T[] toArray() {
        return this.elems.clone();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return this == obj || obj instanceof UserSeq<?> other && this.type == other.type
            && Arrays.equals(this.elems, other.elems);
    }

    @Override
    public int hashCode() {
        if (this.hashCode == 0) {
            int result = Arrays.hashCode(this.elems);
            this.hashCode = result == 0
                ? 1
                : result;
        }
        return this.hashCode;
    }

    @Override
    public String toString() {
        return Arrays.toString(this.elems);
    }

    /** The type of the elements. */
    private final Class<T> type;
    /** The elements; never modified after construction. */
    private final T[] elems;
    /** The cached hash code, or 0 if it has not yet been computed. */
    private int hashCode;

    /**
     * Returns a sequence holding given elements of a given type.
     * The argument is copied, so later changes to it do not affect the sequence.
     * @param type the type of the elements
     * @throws NullPointerException if one of the elements is {@code null}
     * @throws IllegalArgumentException if one of the elements is not of the given type
     */
    @SafeVarargs
    public static <T extends Record> UserSeq<T> of(Class<T> type, T... elems) {
        return copyOf(type, Arrays.asList(elems));
    }

    /**
     * Returns a sequence holding the elements of a given collection, in its iteration order.
     * The elements need not be known to be of the given type, but are checked to be so.
     * @param type the type of the elements
     * @throws NullPointerException if one of the elements is {@code null}
     * @throws IllegalArgumentException if one of the elements is not of the given type
     */
    public static <T extends Record> UserSeq<T> copyOf(Class<T> type, Collection<?> elems) {
        @SuppressWarnings("unchecked")
        T[] result = (T[]) Array.newInstance(type, elems.size());
        int i = 0;
        for (Object elem : elems) {
            result[i] = checked(type, elem);
            i++;
        }
        return new UserSeq<>(type, result);
    }

    /**
     * Returns a given object as an element of a given type.
     * @throws NullPointerException if the object is {@code null}
     * @throws IllegalArgumentException if the object is not of the given type
     */
    private static <T extends Record> T checked(Class<T> type, @Nullable Object elem) {
        Objects.requireNonNull(elem);
        if (!type.isInstance(elem)) {
            throw new IllegalArgumentException(String
                .format("Element '%s' of a sequence of %s is a %s", elem, type.getSimpleName(),
                        elem.getClass().getSimpleName()));
        }
        @SuppressWarnings("unchecked")
        T result = (T) elem;
        return result;
    }
}
