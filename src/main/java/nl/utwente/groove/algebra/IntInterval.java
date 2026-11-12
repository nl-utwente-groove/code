/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2026
 * University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * $Id$
 */
package nl.utwente.groove.algebra;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.util.AIGenerated;

/**
 * Immutable set view on an interval of consecutive integers, iterated in ascending order,
 * in the representation of some integer algebra.
 * The elements are computed on demand, so the memory footprint
 * does not depend on the size of the interval.
 * This is the outcome of the multi-valued integer operator {@link IntSignature#range(Object)}.
 * @param <T> the representation type of the integer algebra
 */
@AIGenerated("Claude Fable 5.2, 2026-11")
@NonNullByDefault
abstract class IntInterval<T> extends AbstractSet<T> {
    /**
     * Creates the interval from a lower bound up to and including an upper bound.
     * The interval is empty if the upper bound is smaller than the lower bound.
     * @throws ArithmeticException if the interval has more than {@link Integer#MAX_VALUE} elements
     */
    IntInterval(long lower, long upper) throws ArithmeticException {
        this.lower = lower;
        this.size = upper < lower
            ? 0
            : Math.toIntExact(Math.addExact(Math.subtractExact(upper, lower), 1));
    }

    /** The first value in the interval. */
    private final long lower;
    /** The number of values in the interval. */
    private final int size;

    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            @Override
            public boolean hasNext() {
                return this.next < IntInterval.this.size;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return toValue(IntInterval.this.lower + this.next++);
            }

            /** Offset of the next value to be returned. */
            private int next;
        };
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean contains(@Nullable Object o) {
        Long value = o == null
            ? null
            : fromValue(o);
        return value != null && value >= this.lower && value - this.lower < this.size;
    }

    /** Converts a number to the corresponding algebra value. */
    abstract T toValue(long number);

    /**
     * Converts an object to the number it represents, if it is
     * an algebra value that fits into a {@code long}.
     * @return the number represented by {@code value}, or {@code null} if {@code value}
     * is not an algebra value or does not fit into a {@code long}
     */
    abstract @Nullable Long fromValue(Object value);
}
