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

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.util.AIGenerated;

/**
 * Immutable sequence of values of one sort, for use as the last
 * component of a {@link UserType} record or the last parameter of a
 * {@link UserOperation}.
 * There is one implementation per system sort: {@link IntSeq}, {@link RealSeq},
 * {@link BoolSeq} and {@link StringSeq}; and one for the values of a user type,
 * {@link UserSeq}, which is only allowed as a parameter. User code works with those
 * classes, which offer unboxed access; this interface serves code that has to treat
 * them alike.
 * Two sequences are equal if they are of the same class and hold equal elements
 * in the same order.
 * @param <T> the Java type of the elements, boxed if it is primitive
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.2, 2026-11")
@NonNullByDefault
public sealed interface Seq<T> permits BoolSeq, IntSeq, RealSeq, StringSeq, UserSeq {
    /** Returns the number of elements of this sequence. */
    int size();

    /**
     * Returns the element at a given position, boxed if it is of a primitive type.
     * @param index the position, counting from 0
     * @throws IndexOutOfBoundsException if the index is negative or not smaller than {@link #size()}
     */
    T value(int index);
}
