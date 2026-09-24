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

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.util.AIGenerated;

/**
 * Hash table core of {@link ForkableHashMap} and {@link ForkableHashSet}: a hash table that
 * can be forked in time proportional to the square root of its size, after which the
 * original and the fork evolve independently.
 * <p>
 * The table is split into a power-of-two number of buckets, chosen by the top bits of the
 * spread hash code; each bucket is a small open-addressing table with linear probing,
 * kept at most half full, since misses (every insertion) probe to the next empty slot.
 * Forking copies the bucket array, so the two tables share all buckets. Every bucket
 * records the table that owns it, i.e., may modify it in place; a fork gives both tables
 * fresh owner tokens, so that each clones a shared bucket on its first write to it. The
 * number of buckets grows (never shrinks) so that the average bucket size stays below the
 * number of buckets, which keeps both the fork and the per-bucket clone at about the
 * square root of the size.
 * <p>
 * Iteration is in bucket order, and within a bucket in slot order. This is a function of
 * the keys' hash codes and of the insertion and removal history, not of the insertion
 * order alone.
 * <p>
 * Forking reads the original and replaces its owner token; neither operation is
 * synchronised, but reads of a table that is no longer modified are safe while it is
 * being forked. Null keys and (in maps) null values are not supported.
 * @param <K> the key type
 * @param <V> the value type; for sets, values are not stored
 */
@NonNullByDefault
@AIGenerated("Claude Opus 5.5, 2026-09")
final class ForkableHashTable<K,V> {
    /** Creates an empty table, with or without value storage. */
    ForkableHashTable(boolean hasValues) {
        this.hasValues = hasValues;
        this.bits = MIN_BITS;
        this.buckets = new @Nullable Bucket[1 << MIN_BITS];
        this.owner = new Object();
    }

    /** Creates a fork of a given table, sharing its buckets. */
    private ForkableHashTable(ForkableHashTable<K,V> original) {
        this.hasValues = original.hasValues;
        this.bits = original.bits;
        this.buckets = original.buckets.clone();
        this.size = original.size;
        this.owner = new Object();
        // the original no longer owns its buckets either
        original.owner = new Object();
    }

    /** Returns a fork of this table. */
    ForkableHashTable<K,V> fork() {
        return new ForkableHashTable<>(this);
    }

    /** Returns the number of keys in the table. */
    int size() {
        return this.size;
    }

    /** Indicates if a given key is in the table. */
    boolean containsKey(@Nullable Object key) {
        if (key == null) {
            return false;
        }
        int hash = spread(key.hashCode());
        var bucket = this.buckets[bucketIndex(hash)];
        return bucket != null && bucket.indexOf(key, hash) >= 0;
    }

    /** Returns the value stored for a given key, if any. */
    @Nullable
    V get(@Nullable Object key) {
        if (key == null) {
            return null;
        }
        int hash = spread(key.hashCode());
        var bucket = this.buckets[bucketIndex(hash)];
        if (bucket == null) {
            return null;
        }
        int index = bucket.indexOf(key, hash);
        if (index < 0) {
            return null;
        }
        var vals = bucket.vals;
        assert vals != null;
        @SuppressWarnings("unchecked")
        V result = (V) vals[index];
        return result;
    }

    /**
     * Inserts a key, or replaces its value if it is already there.
     * For sets, {@code value} is ignored.
     * @return {@code null} if the key was new; otherwise the previous value
     * (for sets, the key already in the table)
     */
    @Nullable
    Object put(K key, @Nullable V value) {
        int hash = spread(key.hashCode());
        var bucket = ownedBucket(bucketIndex(hash), INIT_CAPACITY);
        Object result = bucket.put(key, value, hash);
        if (result == null) {
            this.size++;
            if (this.size > 1 << (2 * this.bits) && this.bits < MAX_BITS) {
                rehash(this.bits + 1);
            }
        }
        return result;
    }

    /**
     * Removes a key.
     * @return the value previously stored for the key (for sets, the key itself),
     * or {@code null} if the key was not in the table
     */
    @Nullable
    Object remove(@Nullable Object key) {
        if (key == null) {
            return null;
        }
        int hash = spread(key.hashCode());
        int bucketIndex = bucketIndex(hash);
        var bucket = this.buckets[bucketIndex];
        if (bucket == null) {
            return null;
        }
        int index = bucket.indexOf(key, hash);
        if (index < 0) {
            return null;
        }
        // a clone keeps the slots of its original
        Object result = ownedBucket(bucketIndex, INIT_CAPACITY).remove(index);
        this.size--;
        return result;
    }

    /** Returns an iterator over the keys (if {@code values} is {@code false})
     * or the values (if {@code values} is {@code true}) of this table.
     * The iterator does not support removal.
     */
    /** Returns the sum of the hash codes of the keys, which is the hash code
     * that {@link java.util.Set#hashCode()} prescribes for the key set.
     */
    int keyHashSum() {
        int result = 0;
        for (var bucket : this.buckets) {
            if (bucket != null) {
                for (var key : bucket.keys) {
                    if (key != null) {
                        result += key.hashCode();
                    }
                }
            }
        }
        return result;
    }

    <T> Iterator<T> iterator(boolean values) {
        return new TableIterator<>(values);
    }

    /** Returns the bucket at a given index, cloned if this table does not own it,
     * or created with a given capacity if there is none. */
    private Bucket ownedBucket(int index, int capacity) {
        var result = this.buckets[index];
        if (result == null) {
            result = this.buckets[index] = new Bucket(this.owner, this.hasValues, capacity);
        } else if (result.owner != this.owner) {
            result = this.buckets[index] = new Bucket(this.owner, result);
        }
        return result;
    }

    /** Redistributes all keys over a new number of buckets, all owned by this table. */
    private void rehash(int newBits) {
        var oldBuckets = this.buckets;
        this.bits = newBits;
        this.buckets = new @Nullable Bucket[1 << newBits];
        // presize the buckets for twice the average number of keys,
        // so that few of them grow right after the rehash
        int capacity = INIT_CAPACITY;
        while (capacity < 4 * (this.size >> newBits)) {
            capacity <<= 1;
        }
        for (var bucket : oldBuckets) {
            if (bucket == null) {
                continue;
            }
            var keys = bucket.keys;
            var vals = bucket.vals;
            for (int i = 0; i < keys.length; i++) {
                var key = keys[i];
                if (key != null) {
                    int hash = spread(key.hashCode());
                    ownedBucket(bucketIndex(hash), capacity).put(key, vals == null
                        ? null
                        : vals[i], hash);
                }
            }
        }
    }

    private int bucketIndex(int hash) {
        return hash >>> (32 - this.bits);
    }

    /** Flag indicating that the table stores values. */
    private final boolean hasValues;
    /** Base-2 logarithm of the number of buckets. */
    private int bits;
    /** The buckets; {@code null} entries stand for empty buckets. */
    private @Nullable Bucket[] buckets;
    /** Number of keys in the table. */
    private int size;
    /** Token identifying the buckets this table may modify in place. */
    private Object owner;

    /** Spreads a hash code so that both its top and its bottom bits are well mixed. */
    static int spread(int h) {
        int result = h * 0x9E3779B9;
        return result ^ (result >>> 16);
    }

    /** Minimal base-2 logarithm of the number of buckets. */
    private static final int MIN_BITS = 2;
    /** Maximal base-2 logarithm of the number of buckets. */
    private static final int MAX_BITS = 16;
    /** Capacity of a new bucket outside a rehash; a power of two. */
    private static final int INIT_CAPACITY = 4;

    /** A small open-addressing hash table with linear probing. */
    private static final class Bucket {
        /** Creates an empty bucket with a given capacity, a power of two. */
        Bucket(Object owner, boolean hasValues, int capacity) {
            this.owner = owner;
            this.keys = new @Nullable Object[capacity];
            this.vals = hasValues
                ? new @Nullable Object[capacity]
                : null;
        }

        /** Creates a copy of a bucket, with a new owner. */
        Bucket(Object owner, Bucket original) {
            this.owner = owner;
            this.keys = original.keys.clone();
            var vals = original.vals;
            this.vals = vals == null
                ? null
                : vals.clone();
            this.size = original.size;
        }

        /** Returns the slot of a key, or {@code -1} if the key is not in the bucket. */
        int indexOf(Object key, int hash) {
            var keys = this.keys;
            int mask = keys.length - 1;
            for (int i = hash & mask;; i = (i + 1) & mask) {
                var k = keys[i];
                if (k == null) {
                    return -1;
                }
                if (k == key || spread(k.hashCode()) == hash && k.equals(key)) {
                    return i;
                }
            }
        }

        /** Inserts or replaces a key-value pair.
         * @return {@code null} if the key was new; otherwise the previous value
         * (or the key already in the bucket if there are no values)
         */
        @Nullable
        Object put(Object key, @Nullable Object value, int hash) {
            var keys = this.keys;
            int mask = keys.length - 1;
            int i = hash & mask;
            for (;; i = (i + 1) & mask) {
                var k = keys[i];
                if (k == null) {
                    break;
                }
                if (k == key || spread(k.hashCode()) == hash && k.equals(key)) {
                    Object result = k;
                    var vals = this.vals;
                    if (vals != null) {
                        result = vals[i];
                        vals[i] = value;
                    }
                    return result;
                }
            }
            keys[i] = key;
            var vals = this.vals;
            if (vals != null) {
                vals[i] = value;
            }
            this.size++;
            if (2 * this.size > keys.length) {
                grow();
            }
            return null;
        }

        /** Removes the key at a given slot, and returns its value
         * (or the key itself if there are no values).
         * Uses backward-shift deletion to keep the probe sequences intact.
         */
        Object remove(int index) {
            var keys = this.keys;
            var vals = this.vals;
            var key = keys[index];
            assert key != null;
            Object result = key;
            if (vals != null) {
                var val = vals[index];
                assert val != null;
                result = val;
            }
            int mask = keys.length - 1;
            int hole = index;
            for (int j = (hole + 1) & mask;; j = (j + 1) & mask) {
                var k = keys[j];
                if (k == null) {
                    break;
                }
                int home = spread(k.hashCode()) & mask;
                // move k into the hole unless its home lies cyclically in (hole, j]
                boolean stays = hole <= j
                    ? hole < home && home <= j
                    : hole < home || home <= j;
                if (!stays) {
                    keys[hole] = k;
                    if (vals != null) {
                        vals[hole] = vals[j];
                    }
                    hole = j;
                }
            }
            keys[hole] = null;
            if (vals != null) {
                vals[hole] = null;
            }
            this.size--;
            return result;
        }

        /** Doubles the capacity of this bucket. */
        private void grow() {
            var oldKeys = this.keys;
            var oldVals = this.vals;
            int capacity = 2 * oldKeys.length;
            int mask = capacity - 1;
            var keys = this.keys = new @Nullable Object[capacity];
            var vals = this.vals = oldVals == null
                ? null
                : new @Nullable Object[capacity];
            for (int i = 0; i < oldKeys.length; i++) {
                var k = oldKeys[i];
                if (k != null) {
                    int j = spread(k.hashCode()) & mask;
                    while (keys[j] != null) {
                        j = (j + 1) & mask;
                    }
                    keys[j] = k;
                    if (vals != null) {
                        assert oldVals != null;
                        vals[j] = oldVals[i];
                    }
                }
            }
        }

        /** The table allowed to modify this bucket in place. */
        final Object owner;
        /** The keys, with {@code null} for empty slots; the length is a power of two. */
        @Nullable
        Object[] keys;
        /** The values, parallel to {@link #keys}; {@code null} for sets. */
        @Nullable
        Object @Nullable [] vals;
        /** The number of keys in this bucket. */
        int size;
    }

    /** Iterator over the keys or values of the table. */
    private final class TableIterator<T> implements Iterator<T> {
        TableIterator(boolean values) {
            this.values = values;
            advance();
        }

        @Override
        public boolean hasNext() {
            return this.bucket != null;
        }

        @Override
        public T next() {
            var bucket = this.bucket;
            if (bucket == null) {
                throw new NoSuchElementException();
            }
            Object result;
            if (this.values) {
                var vals = bucket.vals;
                assert vals != null;
                result = vals[this.slot];
            } else {
                result = bucket.keys[this.slot];
            }
            assert result != null;
            advance();
            @SuppressWarnings("unchecked")
            T typed = (T) result;
            return typed;
        }

        /** Moves to the next occupied slot, or sets {@link #bucket} to {@code null}. */
        private void advance() {
            var buckets = ForkableHashTable.this.buckets;
            var bucket = this.bucket;
            int slot = this.slot + 1;
            while (true) {
                if (bucket != null) {
                    var keys = bucket.keys;
                    while (slot < keys.length) {
                        if (keys[slot] != null) {
                            this.bucket = bucket;
                            this.slot = slot;
                            return;
                        }
                        slot++;
                    }
                }
                this.bucketIndex++;
                if (this.bucketIndex >= buckets.length) {
                    this.bucket = null;
                    return;
                }
                bucket = buckets[this.bucketIndex];
                slot = 0;
            }
        }

        private final boolean values;
        private @Nullable Bucket bucket;
        private int bucketIndex = -1;
        private int slot = -1;
    }
}
