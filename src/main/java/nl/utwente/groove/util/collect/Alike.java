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
package nl.utwente.groove.util.collect;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

/** Object of a given type, together with equality information for that object.
 * The object itself is {@code null} iff the equality is {@link Likeness#DISTINCT}.
 */
@NonNullByDefault
public record Alike<T>(@Nullable T value, Likeness likeness) {
    /** Constructs the unique {@link #DISTINCT} value. */
    private Alike() {
        this(null, Likeness.DISTINCT);
    }

    /** Convenience method for {@code likeness() == likeness}. */
    public boolean isAlike(Likeness likeness) {
        return likeness() == likeness;
    }

    /** Convenience method for <code>equality().isEqual()</code>. */
    public boolean isEqual() {
        return likeness().isAlike();
    }

    /** Convenience method for <code>!equality().isEqual()</code>. */
    public boolean isDistinct() {
        return likeness() == Likeness.DISTINCT;
    }

    /** Returns the (singleton) DISTINCT object. */
    @SuppressWarnings({"cast", "unchecked"})
    static public final <T> Alike<T> distinct() {
        return (Alike<T>) DISTINCT;
    }

    @SuppressWarnings("rawtypes")
    static private final Alike DISTINCT = new Alike();
}