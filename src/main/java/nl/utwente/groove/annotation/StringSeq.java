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

import java.util.Arrays;
import java.util.Objects;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.util.AIGenerated;

/**
 * Immutable sequence of strings.
 * The elements are never {@code null}: every operation that takes an element
 * rejects {@code null} with a {@link NullPointerException}.
 * @see Seq
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
@AIGenerated("Claude Fable 5.2, 2026-11")
public final class StringSeq implements Seq {
    /** Wraps a given array of non-{@code null} strings, without copying it. */
    private StringSeq(String[] elems) {
        this.elems = elems;
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
    public String get(int index) {
        return this.elems[index];
    }

    @Override
    public String value(int index) {
        return get(index);
    }

    /**
     * Returns a sequence that differs from this one in the element at a given position.
     * @param index the position, counting from 0
     * @param elem the element at that position in the result
     * @throws IndexOutOfBoundsException if the index is negative or not smaller than {@link #size()}
     */
    public StringSeq with(int index, String elem) {
        String[] result = this.elems.clone();
        result[index] = Objects.requireNonNull(elem);
        return new StringSeq(result);
    }

    /** Returns a sequence that consists of this one followed by a given element. */
    public StringSeq append(String elem) {
        String[] result = Arrays.copyOf(this.elems, this.elems.length + 1);
        result[this.elems.length] = Objects.requireNonNull(elem);
        return new StringSeq(result);
    }

    /** Returns the elements of this sequence, as a fresh array. */
    public String[] toArray() {
        return this.elems.clone();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return this == obj
            || obj instanceof StringSeq other && Arrays.equals(this.elems, other.elems);
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

    /** The elements; never modified after construction. */
    private final String[] elems;
    /** The cached hash code, or 0 if it has not yet been computed. */
    private int hashCode;

    /**
     * Returns a sequence holding given elements.
     * The argument is copied, so later changes to it do not affect the sequence.
     * @throws NullPointerException if one of the elements is {@code null}
     */
    public static StringSeq of(String... elems) {
        if (elems.length == 0) {
            return EMPTY;
        }
        String[] result = elems.clone();
        for (String elem : result) {
            Objects.requireNonNull(elem);
        }
        return new StringSeq(result);
    }

    /** The empty sequence. */
    private static final StringSeq EMPTY = new StringSeq(new String[0]);
}
