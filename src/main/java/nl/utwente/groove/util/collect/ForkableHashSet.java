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
 */
package nl.utwente.groove.util.collect;

import java.util.AbstractSet;
import java.util.Iterator;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.util.AIGenerated;

/**
 * Hash set that can be forked in time proportional to the square root of its size;
 * see {@link ForkableHashTable} for the structure and the iteration order.
 * The iterator does not support removal, and {@code null} elements are not supported.
 * @param <E> the element type
 */
@AIGenerated("Claude Opus 5.5, 2026-09")
@NonNullByDefault
public class ForkableHashSet<E> extends AbstractSet<E> {
    /** Creates an empty set. */
    public ForkableHashSet() {
        this.table = new ForkableHashTable<>(false);
    }

    /** Creates a fork of a given set, which afterwards evolves independently of it. */
    public ForkableHashSet(ForkableHashSet<E> original) {
        this.table = original.table.fork();
    }

    @Override
    public boolean add(E e) {
        return this.table.put(e, null) == null;
    }

    @Override
    public boolean remove(@Nullable Object o) {
        return this.table.remove(o) != null;
    }

    @Override
    public boolean contains(@Nullable Object o) {
        return this.table.containsKey(o);
    }

    @Override
    public int size() {
        return this.table.size();
    }

    @Override
    public int hashCode() {
        return this.table.keyHashSum();
    }

    @Override
    public Iterator<E> iterator() {
        return this.table.iterator(false);
    }

    private final ForkableHashTable<E,Object> table;
}
