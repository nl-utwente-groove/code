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

import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.util.AIGenerated;

/**
 * Hash map that can be forked in time proportional to the square root of its size;
 * see {@link ForkableHashTable} for the structure and the iteration order.
 * The views are read-only, and {@code null} keys and values are not supported.
 * @param <K> the key type
 * @param <V> the value type
 */
@AIGenerated("Claude Opus 5.5, 2026-09")
@NonNullByDefault
public class ForkableHashMap<K,V> extends AbstractMap<K,V> {
    /** Creates an empty map. */
    public ForkableHashMap() {
        this.table = new ForkableHashTable<>(true);
    }

    /** Creates a fork of a given map, which afterwards evolves independently of it. */
    public ForkableHashMap(ForkableHashMap<K,V> original) {
        this.table = original.table.fork();
    }

    @Override
    public @Nullable V get(@Nullable Object key) {
        return this.table.get(key);
    }

    @Override
    public boolean containsKey(@Nullable Object key) {
        return this.table.containsKey(key);
    }

    @Override
    public @Nullable V put(K key, V value) {
        @SuppressWarnings("unchecked")
        V result = (V) this.table.put(key, value);
        return result;
    }

    @Override
    public @Nullable V remove(@Nullable Object key) {
        @SuppressWarnings("unchecked")
        V result = (V) this.table.remove(key);
        return result;
    }

    @Override
    public int size() {
        return this.table.size();
    }

    @Override
    public Set<K> keySet() {
        var result = this.keySet;
        if (result == null) {
            this.keySet = result = createKeySet();
        }
        return result;
    }

    private Set<K> createKeySet() {
        return new AbstractSet<>() {
            @Override
            public Iterator<K> iterator() {
                return ForkableHashMap.this.table.iterator(false);
            }

            @Override
            public boolean contains(@Nullable Object o) {
                return containsKey(o);
            }

            @Override
            public int hashCode() {
                return ForkableHashMap.this.table.keyHashSum();
            }

            @Override
            public int size() {
                return ForkableHashMap.this.size();
            }
        };
    }

    @Override
    public Collection<V> values() {
        return new AbstractCollection<>() {
            @Override
            public Iterator<V> iterator() {
                return ForkableHashMap.this.table.iterator(true);
            }

            @Override
            public int size() {
                return ForkableHashMap.this.size();
            }
        };
    }

    @Override
    public Set<Map.Entry<K,V>> entrySet() {
        return new AbstractSet<>() {
            @Override
            public Iterator<Map.Entry<K,V>> iterator() {
                Iterator<K> keys = ForkableHashMap.this.table.iterator(false);
                Iterator<V> values = ForkableHashMap.this.table.iterator(true);
                return new Iterator<>() {
                    @Override
                    public boolean hasNext() {
                        return keys.hasNext();
                    }

                    @Override
                    public Map.Entry<K,V> next() {
                        return new AbstractMap.SimpleImmutableEntry<>(keys.next(), values.next());
                    }
                };
            }

            @Override
            public int size() {
                return ForkableHashMap.this.size();
            }
        };
    }

    private final ForkableHashTable<K,V> table;
    /** Lazily created key set view. */
    private @Nullable Set<K> keySet;
}
