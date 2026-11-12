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

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.util.AIGenerated;

/**
 * Immutable sequence of reals.
 * Elements are compared as {@link Double#equals} does, which is also how a record
 * compares a {@code double} component: {@code NaN} equals itself, and {@code 0.0}
 * differs from {@code -0.0}.
 * @see Seq
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
@AIGenerated("Claude Fable 5.2, 2026-11")
public final class RealSeq implements Seq {
    /** Wraps a given array, without copying it. */
    private RealSeq(double[] elems) {
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
    public double get(int index) {
        return this.elems[index];
    }

    @Override
    public Double value(int index) {
        return get(index);
    }

    /**
     * Returns a sequence that differs from this one in the element at a given position.
     * @param index the position, counting from 0
     * @param elem the element at that position in the result
     * @throws IndexOutOfBoundsException if the index is negative or not smaller than {@link #size()}
     */
    public RealSeq with(int index, double elem) {
        double[] result = this.elems.clone();
        result[index] = elem;
        return new RealSeq(result);
    }

    /** Returns a sequence that consists of this one followed by a given element. */
    public RealSeq append(double elem) {
        double[] result = Arrays.copyOf(this.elems, this.elems.length + 1);
        result[this.elems.length] = elem;
        return new RealSeq(result);
    }

    /** Returns the elements of this sequence, as a fresh array. */
    public double[] toArray() {
        return this.elems.clone();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return this == obj
            || obj instanceof RealSeq other && Arrays.equals(this.elems, other.elems);
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
    private final double[] elems;
    /** The cached hash code, or 0 if it has not yet been computed. */
    private int hashCode;

    /**
     * Returns a sequence holding given elements.
     * The argument is copied, so later changes to it do not affect the sequence.
     */
    public static RealSeq of(double... elems) {
        return elems.length == 0
            ? EMPTY
            : new RealSeq(elems.clone());
    }

    /** The empty sequence. */
    private static final RealSeq EMPTY = new RealSeq(new double[0]);
}
