// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// http://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
// either express or implied. See the License for the specific
// language governing permissions and limitations under the License.
/**
 *
 */
package groove.util.collect;

import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Stack;

import org.eclipse.jdt.annotation.Nullable;

/**
 * Set implementation that uses a search tree over "hash" code. If the number of
 * elements is small or the keys are evenly distributed, this outperforms the
 * {@link java.util.HashSet}.
 * @author Arend Rensink
 * @version $Revision$
 */
public class TreeHashSet<T> extends AbstractSet<T> {
    /**
     * Creates an instance of a tree store set with a given root and branch
     * resolution, initial capacity, and equator. The resolution is required to
     * be at least <code>1</code>.
     * @param capacity the initial capacity of the set
     * @param resolution the resolution of the tree; should be at least
     *        <code>1</code>
     * @param rootResolution the resolution of the root branch; should be at
     *        least <code>1</code>
     * @param equator the equator used for deciding equality of objects in the
     *        set
     */
    public TreeHashSet(int capacity, int resolution, int rootResolution, Equator<T> equator) {
        if (resolution < 1) {
            throw new IllegalArgumentException(
                String.format("Invalid resolution %d (max %d)", resolution, 1));
        }
        if (rootResolution < 1) {
            throw new IllegalArgumentException(
                String.format("Invalid root resolution %d (min %d)", rootResolution, 1));
        }
        if (resolution > MAX_RESOLUTION) {
            throw new IllegalArgumentException(
                String.format("Invalid resolution %d (max %d)", resolution, MAX_RESOLUTION));
        }
        this.resolution = resolution;
        this.mask = (1 << resolution) - 1;
        this.rootResolution = rootResolution;
        this.rootMask = (1 << rootResolution) - 1;
        this.equator = equator;
        // initialise the keys and tree
        this.codes = new int[capacity];
        this.keys = new Object[capacity];
        this.freeKeyIx = -1;
        int maxRecordCount = Math.max(capacity >> resolution, 1);
        this.tree = new int[getRecordIx(maxRecordCount) + maxRecordCount];
        this.fill = new byte[maxRecordCount];
        this.recordCount = 1;
    }

    /**
     * Creates an instance of a tree store set with a given initial capacity and
     * resolution. The resolution is required to be at least <code>1</code>.
     * @param capacity the initial capacity of the set
     * @param resolution the resolution of the tree branches; should be at least
     *        <code>1</code>
     * @param rootResolution the resolution of the tree root; should be at least
     *        <code>1</code>
     */
    @SuppressWarnings("unchecked")
    public TreeHashSet(int capacity, int resolution, int rootResolution) {
        this(capacity, resolution, rootResolution, DEFAULT_EQUATOR);
    }

    /**
     * Creates an instance of a tree store set with a given resolution and
     * initial capacity. The resolution is required to be at least
     * <code>1</code>.
     * @param capacity the initial capacity of the set
     * @param resolution the resolution of the tree branches; should be at least
     *        <code>1</code>
     */
    public TreeHashSet(int capacity, int resolution) {
        this(capacity, resolution, Math.max(resolution, DEFAULT_ROOT_RESOLUTION));
    }

    /**
     * Creates an instance of a tree store set with a given initial capacity and
     * equator.
     * @param capacity the initial capacity of the set
     * @param equator the equator used for deciding equality of objects in the
     *        set
     */
    public TreeHashSet(int capacity, Equator<T> equator) {
        this(capacity, DEFAULT_RESOLUTION, DEFAULT_ROOT_RESOLUTION, equator);
    }

    /**
     * Creates an instance of a tree store set with a given equator.
     * @param equator the equator used for deciding equality of objects in the
     *        set
     */
    public TreeHashSet(Equator<T> equator) {
        this(DEFAULT_CAPACITY, equator);
    }

    /**
     * Creates an instance of a tree store set with a given initial capacity.
     * @param capacity the initial capacity of the set
     */
    public TreeHashSet(int capacity) {
        this(capacity, DEFAULT_RESOLUTION, DEFAULT_ROOT_RESOLUTION);
    }

    /**
     * Creates an instance of a tree store set.
     */
    public TreeHashSet() {
        this(DEFAULT_CAPACITY);
    }

    /**
     * Creates an instance of a tree store which is a copy of another. This is a
     * much more efficient way of copying sets than by adding the elements one
     * at a time.
     */
    public TreeHashSet(TreeHashSet<T> other) {
        this(other.size(), other.resolution, other.rootResolution, other.equator);
        int otherTreeLength = other.tree.length;
        if (this.tree.length < otherTreeLength) {
            this.tree = new int[otherTreeLength];
            this.fill = new byte[other.fill.length];
        }
        System.arraycopy(other.tree, 0, this.tree, 0, otherTreeLength);
        System.arraycopy(other.fill, 0, this.fill, 0, other.fill.length);
        int otherCodesLength = other.codes.length;
        if (this.codes.length < otherCodesLength) {
            this.codes = new int[otherCodesLength];
            this.keys = new Object[otherCodesLength];
        }
        System.arraycopy(other.codes, 0, this.codes, 0, otherCodesLength);
        System.arraycopy(other.keys, 0, this.keys, 0, otherCodesLength);
        this.size = other.size;
        this.freeRecordNr = other.freeRecordNr;
        this.recordCount = other.recordCount;
        this.freeKeyIx = other.freeKeyIx;
        this.keyCount = other.keyCount;
        assert containsAll(other) : String.format("Clone    %s does not equal%noriginal %s",
            this,
            other);
    }

    /**
     * Uses the <code>capacity</code> parameter to assign a new length to the
     * underlying arrays, if they are smaller than this capacity.
     */
    @Override
    public void clear() {
        this.size = 0;
        this.modCount++;
        // clean the keys and codes arrays
        Arrays.fill(this.keys, 0, this.keyCount, null);
        Arrays.fill(this.codes, 0, this.keyCount, 0);
        this.keyCount = 0;
        this.freeKeyIx = -1;
        // clean the record tree
        Arrays.fill(this.tree, 0, getRecordIx(this.recordCount), 0);
        Arrays.fill(this.tree,
            getRecordIx(this.fill.length),
            getRecordIx(this.fill.length) + this.recordCount,
            0);
        Arrays.fill(this.fill, 0, this.recordCount, (byte) 0);
        this.recordCount = 1;
        this.freeRecordNr = 0;
        if (DEBUG) {
            testConsistent();
        }
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            @Override
            public boolean hasNext() {
                if (this.expectedModCount != TreeHashSet.this.modCount) {
                    throw new ConcurrentModificationException();
                }
                Object next = this.next;
                if (next == null) {
                    if (this.remainingCount == 0) {
                        return false;
                    }
                    int index = this.index;
                    final Object[] keys = this.keys;
                    do {
                        next = keys[index];
                        index++;
                    } while (next == null);
                    this.next =
                        next instanceof MyListEntry ? ((MyListEntry<T>) next).getValue() : (T) next;
                    this.index = index;
                    assert this.next != null;
                    this.remainingCount--;
                }
                return true;
            }

            @Override
            public T next() {
                if (hasNext()) {
                    // assert removeKey == null || !next.equals(removeKey);
                    @Nullable
                    T result = this.next;
                    assert result != null; // because hasNext holds
                    this.next = null;
                    // this.removeKey = next;
                    return result;
                } else {
                    throw new NoSuchElementException();
                }
            }

            @Override
            public void remove() {
                // since removing a key may mean that the next element of
                // the key chain is moved to the position of the removed key,
                // which the iterator has already passed by, we don't allow it.
                throw new UnsupportedOperationException();
            }

            /**
             * Copy from the enclosing class.
             */
            private final Object[] keys = TreeHashSet.this.keys;
            /**
             * The index in {@link TreeHashSet#keys} where we're currently at.
             * @invariant <code>index <= maxKeyIndex</code>
             */
            private int index = 0;
            /**
             * The next object; if <code>null</code>, the next yet has to be
             * found.
             */
            private @Nullable T next;
            /**
             * The number of remaining elements.
             */
            private int remainingCount = TreeHashSet.this.size;
            /** Modification count at construction time of this iterator. */
            private final int expectedModCount = TreeHashSet.this.modCount;
        };
    }

    /**
     * Returns an iterator that goes over the values in the in-order of the tree
     * of hash codes. This order is guaranteed to be stable for the same set.
     */
    public Iterator<T> sortedIterator() {
        return new Iterator<T>() {
            @Override
            @SuppressWarnings("unchecked")
            public boolean hasNext() {
                if (this.expectedModCount != TreeHashSet.this.modCount) {
                    throw new ConcurrentModificationException();
                }
                int[] tree = TreeHashSet.this.tree;
                Object next = this.next;
                if (next == null && !this.atEnd) {
                    int nextKeyIx = -1;
                    if (this.lastEntry == null) {
                        while (!this.atEnd && nextKeyIx < 0) {
                            this.lastTreeIx++;
                            if (this.lastTreeIx > this.maxTreeIx) {
                                // go back to the parent record
                                if (this.treeIxStack.isEmpty()) {
                                    this.atEnd = true;
                                } else {
                                    this.lastTreeIx = this.treeIxStack.pop();
                                    this.maxTreeIx = this.maxIxStack.pop();
                                }
                            } else {
                                int treeValue = tree[this.lastTreeIx];
                                if (treeValue < 0) {
                                    nextKeyIx = -treeValue - 1;
                                } else if (treeValue > 0) {
                                    // go one level deeper into the tree
                                    this.treeIxStack.push(this.lastTreeIx);
                                    this.maxIxStack.push(this.maxTreeIx);
                                    this.lastTreeIx = treeValue - 1;
                                    this.maxTreeIx = treeValue + TreeHashSet.this.mask;
                                }
                            }
                        }
                    } else {
                        nextKeyIx = this.lastEntry.getNext();
                    }
                    if (nextKeyIx >= 0) {
                        next = TreeHashSet.this.keys[nextKeyIx];
                        if (next instanceof MyListEntry) {
                            this.lastEntry = (MyListEntry<T>) next;
                            next = ((MyListEntry<?>) next).getValue();
                        }
                    }
                    this.next = (T) next;
                }
                return next != null;
            }

            @Override
            public T next() {
                if (hasNext()) {
                    @Nullable
                    T result = this.next;
                    assert result != null; // because hasNext() holds
                    this.next = null;
                    return result;
                } else {
                    throw new NoSuchElementException();
                }
            }

            @Override
            public void remove() {
                // since removing a key may mean that the next element of
                // the key chain is moved to the position of the removed key,
                // which the iterator has already passed by, we don't allow it.
                throw new UnsupportedOperationException();
            }

            /** Tree index where the last key was taken from. */
            private int lastTreeIx = -1;
            /** Stack of parent indices. */
            private final Stack<Integer> treeIxStack = new Stack<>();
            /** Maximum tree index of the current record. */
            private int maxTreeIx = TreeHashSet.this.rootMask;
            /** Stack of parent index bounds. */
            private final Stack<Integer> maxIxStack = new Stack<>();
            /**
             * Points to the entry where last next value of #next was be taken
             * from, if any.
             */
            private MyListEntry<T> lastEntry;
            /** The object to be returned at the next invocation of #next(). */
            private @Nullable T next;
            /**
             * Flag set to <code>true</code> when it is clear no more elements
             * will be found.
             */
            private boolean atEnd;

            /** Modification count at construction time of this iterator. */
            private final int expectedModCount = TreeHashSet.this.modCount;
        };
    }

    @Override
    public boolean add(T key) {
        boolean result = put(key) == null;
        return result;
    }

    /**
     * Tries to insert a new object in the set. If an equal object is already in
     * the set, returns that object (and doesn't change the set). If the new key
     * is really inserted, the method returns <code>null</code> The difference
     * with {@link #add(Object)} is thus only in the return value.
     * @param key the object to be inserted
     * @return <code>null</code> if <code>key</code> is inserted, otherwise an
     *         object that was already present, such that
     *         <code>areEqual(key, result)</code>.
     */
    public @Nullable T put(T key) {
        @Nullable
        T result;
        int code = getCode(key);
        if (this.size == 0) {
            // at the first key, we still have to create the root of the tree
            // int index = 0;//newRecordIx(0);
            // assert index == 0;
            this.tree[code & this.rootMask] = -newKeyIx(code, key) - 1;
            result = null;
        } else {
            // local copy of store, for efficiency
            int[] tree = this.tree;
            int mask = this.mask;
            int resolution = this.resolution;
            // precise node where the current value of index was retrieved from
            int indexPlusOffset = code & this.rootMask;
            // current search position
            int index = tree[indexPlusOffset];
            // current depth search, in number of bits
            int depth = this.rootResolution;
            // remaining search key
            int search = code >>> depth;
            while (index > 0) {
                indexPlusOffset = index + (search & mask);
                index = tree[indexPlusOffset];
                search >>>= resolution;
                depth += resolution;
            }
            if (index == 0) {
                // we're at an empty place of the tree
                setTreeSlot(indexPlusOffset, -newKeyIx(code, key) - 1);
                result = null;
            } else {
                // we've found an existing key
                int oldCode = this.codes[-index - 1];
                if (oldCode == code) {
                    // the old code is the same as the one we're inserting
                    result = putEqualKey(code, key, -index - 1);
                } else {
                    // we have a new key, so we have to relocate
                    // first store the position of the old key
                    int oldKeyIndex = index;
                    // create a new position
                    index = newRecordIx(indexPlusOffset);
                    // the old search value
                    int oldSearch = oldCode >>> depth;
                    // the old and new branch values
                    int oldOffset, newOffset;
                    // so long as old and new key coincide, keep relocating
                    while ((newOffset = (search & mask)) == (oldOffset = (oldSearch & mask))) {
                        index = newRecordIx(index + newOffset);
                        search >>>= resolution;
                        oldSearch >>>= resolution;
                    }
                    // we've found a difference, so store.
                    setTreeSlot(index + oldOffset, oldKeyIndex);
                    setTreeSlot(index + newOffset, -newKeyIx(code, key) - 1);
                    result = null;
                }
            }
        }
        if (DEBUG) {
            testConsistent();
        }
        return result;
    }

    /** Returns an iterator over the bucket of all elements with a given hash code. */
    @SuppressWarnings("unchecked")
    public Iterator<T> get(int code) {
        final int index = indexOf(code);
        if (index < 0) {
            return Collections.<T>emptySet()
                .iterator();
        } else if (allEqual()) {
            int keyIndex = -this.tree[index] - 1;
            return Collections.<T>singleton((T) this.keys[keyIndex])
                .iterator();
        } else {
            // iteratively go over the list of entries
            final Object[] keys = this.keys;
            final int keyIndex = -this.tree[index] - 1;
            return new Iterator<T>() {
                @Override
                public boolean hasNext() {
                    if (this.next == null) {
                        if (this.key instanceof MyListEntry) {
                            MyListEntry<T> entry = (MyListEntry<T>) this.key;
                            this.next = entry.getValue();
                            this.key = keys[entry.getNext()];
                        } else if (this.key != null) {
                            this.next = (T) this.key;
                            this.key = null;
                        }
                    }
                    return this.next != null;
                }

                @Override
                public T next() {
                    hasNext();
                    if (this.next == null) {
                        throw new NoSuchElementException();
                    } else {
                        return this.next;
                    }
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }

                private T next;
                private Object key = keys[keyIndex];
            };
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean remove(Object obj) {
        boolean result;
        if (this.size == 0) {
            return false;
        }
        T key = (T) obj;
        int index = indexOf(getCode(key));
        if (index < 0) {
            // the key is a new one
            result = false;
        } else if (allEqual()) {
            // we've found an existing key code and we're only looking at codes
            // so the key found at index should be removed
            int keyIndex = -this.tree[index] - 1;
            disposeTreeSlot(index);
            disposeKey(keyIndex);
            result = true;
        } else {
            // we've found an existing key code but now we're going to compare
            Object[] keys = this.keys;
            // the current position in the key list
            int keyIndex = -this.tree[index] - 1;
            // the key retrieved from the key list
            Object knownKey = keys[keyIndex];
            // index of the previous key in the chain, if any (0 means none)
            int prevKeyIndex = -1;
            // walk the list of MyListEntries, if any
            while (knownKey instanceof MyListEntry) {
                MyListEntry<T> entry = (MyListEntry<T>) knownKey;
                int nextKeyIndex = entry.getNext();
                if (areEqual(key, entry.getValue())) {
                    keys[keyIndex] = keys[nextKeyIndex];
                    disposeKey(nextKeyIndex);
                    return true;
                } else {
                    prevKeyIndex = keyIndex;
                    knownKey = keys[keyIndex = nextKeyIndex];
                }
            }
            assert knownKey != null;
            // we're at a key that is not a MyListEntry
            if (areEqual(key, (T) knownKey)) {
                // maybe we have to adapt the tree
                if (prevKeyIndex < 0) {
                    // there is no chain, so we have to adapt the tree
                    disposeTreeSlot(index);
                } else {
                    // the previous key has to be converted from a
                    // MyListEntry to the object inside
                    keys[prevKeyIndex] = ((MyListEntry<?>) keys[prevKeyIndex]).getValue();
                }
                disposeKey(keyIndex);
                result = true;
                if (DEBUG) {
                    testConsistent();
                }
            } else {
                result = false;
            }
        }
        if (DEBUG) {
            testConsistent();
        }
        return result;
    }

    @Override
    public boolean contains(Object obj) {
        if (this.size == 0) {
            return false;
        }
        @SuppressWarnings("unchecked")
        T key = (T) obj;
        int index = indexOf(getCode(key));
        if (index < 0) {
            // the key is a new one
            return false;
        } else {
            // we've found an existing key code
            return allEqual() || containsAt(key, -this.tree[index] - 1);
        }
    }

    /**
     * Returns the memory space used for storing the set, expressed in number of
     * bytes per element stored.
     */
    public double getBytesPerElement() {
        int treeSpace = BYTES_PER_INT * this.tree.length;
        int codesSpace = BYTES_PER_INT * this.codes.length;
        int keysSpace = BYTES_PER_REF * this.keys.length;
        int myEntrySpace =
            (BYTES_PER_OBJECT + BYTES_PER_INT + BYTES_PER_REF) * getMyListEntryCount();
        return (treeSpace + codesSpace + keysSpace + myEntrySpace) / (double) this.size;
    }

    /**
     * Determines whether two objects, that are already determined to have the
     * same key codes, are to be considered equal for the purpose of this set.
     * The default implementation calls <code>areEqual(key, otherKey)</code> on
     * the equator. If a the {@link #HASHCODE_EQUATOR} is set during
     * construction time, this method is <i>not</i> called.
     */
    protected boolean areEqual(T newKey, T oldKey) {
        return this.equator.areEqual(newKey, oldKey);
    }

    /**
     * Determines the (hash) code used to store a key. The default
     * implementation calls <code>getCode(key)</code> on the equator.
     */
    protected int getCode(T key) {
        return this.equator.getCode(key);
    }

    /**
     * Signals if all objects with the same code are considered equal, i.e., if
     * {@link #areEqual(Object, Object)} always returns <code>true</code>. If
     * so, the equality test can be skipped.
     * @return if <code>true</code>, {@link #areEqual(Object, Object)} always
     *         returns <code>true</code>
     */
    protected boolean allEqual() {
        return this.equator.allEqual();
    }

    /**
     * Returns the tree index of a tree node pointing to (the first instance of)
     * a given code.
     * @param code the code we are looking for
     * @return either <code>-1</code> if <code>code</code> does not occur in the
     *         set, or an index in {@link #tree} such that
     *         <code>codes[-tree[result]]==code</code>
     */
    private int indexOf(int code) {
        // local copy of store, for efficiency
        int[] tree = this.tree;
        // current search position
        int oldIndexPlusOffset = code & this.rootMask;
        int index = tree[oldIndexPlusOffset];
        if (index > 0) {
            int search = code >>> this.rootResolution;
            int mask = this.mask;
            int resolution = this.resolution;
            oldIndexPlusOffset = index + (search & mask);
            index = tree[oldIndexPlusOffset];
            while (index > 0) {
                search >>>= resolution;
                oldIndexPlusOffset = index + (search & mask);
                index = tree[oldIndexPlusOffset];
            }
        }
        if (index == 0 || this.codes[-index - 1] != code) {
            // the code is a new one
            return -1;
        } else {
            // we've found the code
            return oldIndexPlusOffset;
        }
    }

    /**
     * Tests if a given key is in the key chain starting at a given index.
     * @param newKey the key to be found
     * @param keyIndex the index in {@link #keys} where to start looking for
     *        <code>key</code>
     * @return <code>true</code> if <code>key</code> is found
     */
    @SuppressWarnings("unchecked")
    private boolean containsAt(T newKey, int keyIndex) {
        Object[] keys = this.keys;
        Object oldKey = keys[keyIndex];
        // walk the list of MyListEntries, if any
        while (oldKey instanceof MyListEntry) {
            MyListEntry<T> entry = (MyListEntry<T>) oldKey;
            if (areEqual(newKey, entry.getValue())) {
                return true;
            } else {
                oldKey = keys[entry.getNext()];
            }
        }
        return areEqual(newKey, (T) oldKey);
    }

    /**
     * Sets the tree value at a given index. The value can be negative if it is
     * an index into {@link #codes}, or positive if it is a further tree index.
     * The value should not be <code>null</code>.
     */
    private void setTreeSlot(int treeIx, int value) {
        assert this.tree[treeIx] == 0 : String.format("Tree value %d at index %d overwritten by %d",
            this.tree[treeIx],
            treeIx,
            value);
        assert value < 0 : String.format("Tree value at %d set to positive value %d",
            treeIx,
            value);
        this.tree[treeIx] = value;
        setFilled(treeIx);
    }

    /**
     * Resets the tree slot at a given index to 0. If this means that the entire
     * tree record is reduced to a single value, which is a code and not a
     * reference to another tree record, then the record is freed.
     */
    private void disposeTreeSlot(int treeIx) {
        assert this.tree[treeIx] < 0 : String.format("tree[%d] == %d cannot be disposed",
            treeIx,
            this.tree[treeIx]);
        this.tree[treeIx] = 0;
        resetFilled(treeIx);
        // dispose records
        int recordNr;
        while ((recordNr = getRecordNr(treeIx)) > 0) {
            int offset = this.fill[recordNr];
            int lastIx = getRecordIx(recordNr) + (offset & 0xFF) - 1;
            int lastValue;
            if (offset <= this.mask + 1 && offset > 0 && (lastValue = this.tree[lastIx]) < 0) {
                this.tree[lastIx] = 0;
                this.fill[recordNr] = 0;
                treeIx = disposeRecord(recordNr);
                this.tree[treeIx] = lastValue;
            } else {
                break;
            }
        }
    }

    private void setFilled(int treeIx) {
        int recordNr = getRecordNr(treeIx);
        this.fill[recordNr] += getOffset(treeIx) + 1;
    }

    private void resetFilled(int treeIx) {
        int recordNr = getRecordNr(treeIx);
        this.fill[recordNr] -= getOffset(treeIx) + 1;
    }

    /**
     * Reserves space for a new tree branch, to be appended to a given leaf.
     * Returns the index of the first position of the new branch. Also increases
     * the record fill
     * @param parentIx the tree index of the parent slot to which the new record
     *        is to be appended
     */
    private int newRecordIx(int parentIx) {
        int resultNr = this.freeRecordNr;
        if (resultNr == 0) {
            resultNr = this.recordCount;
            this.recordCount++;
            int oldMaxRecordCount = this.fill.length;
            if (this.recordCount >= oldMaxRecordCount) {
                // extend the length of the next array
                int newMaxRecordCount = (int) (this.recordCount * GROWTH_FACTOR);
                int newTreeSize = getRecordIx(newMaxRecordCount);
                int[] newTree = new int[newTreeSize + newMaxRecordCount];
                if (SIZE_PRINT) {
                    System.out.printf(
                        "Set %s (size %d, record count %d) from %d to %d tree nodes%n",
                        System.identityHashCode(this),
                        this.size,
                        this.recordCount,
                        this.tree.length,
                        newTree.length);
                }
                int oldTreeSize = getRecordIx(oldMaxRecordCount);
                System.arraycopy(this.tree, 0, newTree, 0, oldTreeSize);
                System.arraycopy(this.tree, oldTreeSize, newTree, newTreeSize, oldMaxRecordCount);
                byte[] newFill = new byte[newMaxRecordCount];
                System.arraycopy(this.fill, 0, newFill, 0, oldMaxRecordCount);
                this.tree = newTree;
                this.fill = newFill;
                if (FILL_PRINT) {
                    System.out.printf("Extending: %d records (%d slots) for %d keys (average %f)%n",
                        this.recordCount,
                        getRecordIx(this.recordCount),
                        this.size + 1,
                        getAverageFill(getRecordIx(this.recordCount), this.size + 1));
                }
            }
            setParentIx(resultNr, parentIx);
        } else {
            // take a previously used, disposed record
            this.freeRecordNr = setParentIx(resultNr, parentIx);
        }
        if (this.tree[parentIx] == 0) {
            setFilled(parentIx);
        }
        int resultIx = getRecordIx(resultNr);
        this.tree[parentIx] = resultIx;
        return resultIx;
    }

    /**
     * Disposes the record with a given (positive) record number, adjust the
     * parent record index, and returns the parent record number (so that it can
     * be disposed in turn, if appropriate). This should occur when the fill
     * degree of the record has decreased to 0.
     * @param recordNr the number of the record to be disposed; should be
     *        positive
     */
    private int disposeRecord(int recordNr) {
        // System.out.printf("Disposing record %d (next free record %d) of
        // %s%n", recordNr, freeRecordNr, this.hashCode());
        assert recordNr > 0;
        int parentIx = setParentIx(recordNr, this.freeRecordNr);
        this.freeRecordNr = recordNr;
        return parentIx;
    }

    /**
     * Inserts a new code/key pair at the next available place in the
     * {@link #codes} and {@link #keys} arrays, and returns the index of the new
     * position. The index is always positive.
     *
     * @param code the code to be inserted
     * @param key the key to be inserted; it is assumed that
     *        <code>code == key.hashCode()</code>.
     * @return the index in {@link #codes} where <code>code</code> is stored,
     *         resp. in {@link #keys} where <code>key</code> is stored
     */
    private int newKeyIx(int code, T key) {
        assert code == getCode(key) : "Key " + key + " should have hash code " + code + ", but has "
            + getCode(key);
        int result = this.freeKeyIx;
        if (result < 0) {
            result = this.keyCount;
            this.keyCount++;
            int oldLength = this.keys.length;
            if (result >= oldLength) {
                int newLength = (int) (GROWTH_FACTOR * this.keyCount + 1);
                Object[] newKeys = new Object[newLength];
                if (SIZE_PRINT) {
                    System.out.printf("Set %s (size %d) from %d to %d keys %n",
                        System.identityHashCode(this),
                        this.size,
                        this.keys.length,
                        newKeys.length);
                }
                System.arraycopy(this.keys, 0, newKeys, 0, oldLength);
                this.keys = newKeys;
                int[] newCodes = new int[newLength];
                System.arraycopy(this.codes, 0, newCodes, 0, oldLength);
                this.codes = newCodes;
            }
        } else {
            this.freeKeyIx = this.codes[result];
        }
        this.codes[result] = code;
        this.keys[result] = key;
        this.size++;
        this.modCount++;
        return result;
    }

    /**
     * Disposes the key at a given index, and adds the position to the free key
     * chain.
     * @param keyIx the index that we want to free
     */
    private void disposeKey(int keyIx) {
        this.keys[keyIx] = null;
        this.codes[keyIx] = this.freeKeyIx;
        this.freeKeyIx = keyIx;
        this.size--;
        this.modCount++;
        // if (size == 0) {
        // clear();
        // }
    }

    /**
     * Adds a key for an already existing code. The key is not added if it
     * equals one of the keys already stored for this code
     * @param code the code of the key to be added; should equal
     *        <code>key.hashCode()</code>
     * @param newKey the key to be added
     * @param keyIndex the index in {@link #keys} where the first existing key
     *        with code <code>code</code> is stored
     * @return <code>true</code> if no existing key was equal to
     *         <code>key</code>, according to {@link #areEqual(Object, Object)}.
     */
    @SuppressWarnings("unchecked")
    private @Nullable T putEqualKey(int code, T newKey, int keyIndex) {
        if (allEqual()) {
            return (T) this.keys[keyIndex];
        } else {
            // get local copies for efficiency
            Object[] keys = this.keys;
            Object key = keys[keyIndex];
            // as long as the key is a MyListEntry, walk through the list
            while (key instanceof MyListEntry) {
                MyListEntry<T> entry = (MyListEntry<T>) key;
                T value = entry.getValue();
                if (areEqual(newKey, value)) {
                    // the key existed already
                    return value;
                } else {
                    // walk on
                    key = keys[keyIndex = entry.getNext()];
                }
            }
            assert key != null;
            T oldKey = (T) key;
            // we've reached the end of the list
            if (areEqual(newKey, oldKey)) {
                return oldKey;
            } else {
                // it's really a new key
                MyListEntry<T> newEntry = new MyListEntry<>(oldKey, newKeyIx(code, newKey));
                this.keys[keyIndex] = newEntry;
                return null;
            }
        }
    }

    /** Returns the start index of the tree record with a given number. */
    private int getRecordIx(int recordNr) {
        return recordNr == 0 ? 0 : ((recordNr - 1) << this.resolution) + rootSize();
    }

    /** Returns the record number of a given record index. */
    private int getRecordNr(int treeIx) {
        return treeIx < rootSize() ? 0 : ((treeIx - rootSize()) >>> this.resolution) + 1;
    }

    /**
     * Returns the index of the first real (non-parent) element in the record
     * where a given tree index is pointing.
     */
    private int getOffset(int treeIx) {
        return treeIx < rootSize() ? treeIx : (treeIx - rootSize()) & this.mask;
    }

    /** Returns the size of root record. */
    private int rootSize() {
        return this.rootMask + 1;
    }

    /**
     * Returns the parent index for a given record number. The parent may either
     * be the parent in the tree, or the parent in the free record chain.
     */
    private int getParentIx(int recordNr) {
        return this.tree[getRecordIx(this.fill.length) + recordNr];
    }

    /**
     * Sets a parent index for a given record number. The parent may either be
     * the parent in the tree, or the parent in the free record chain.
     * @param recordNr the record for which the parent index is to be set
     * @param parentIx the new parent index
     * @return the old parent index
     */
    private int setParentIx(int recordNr, int parentIx) {
        int recordIx = getRecordIx(this.fill.length) + recordNr;
        int oldParentIx = this.tree[recordIx];
        this.tree[recordIx] = parentIx;
        return oldParentIx;
    }

    private void testConsistent() {
        Set<Integer> freeRecordNrs = new HashSet<>();
        int freeRecordNr = this.freeRecordNr;
        while (freeRecordNr > 0) {
            if (freeRecordNr >= this.recordCount) {
                throw new IllegalStateException(String.format("Free record %d > record count %d",
                    freeRecordNr,
                    this.recordCount));
            }
            freeRecordNrs.add(freeRecordNr);
            freeRecordNr = getParentIx(freeRecordNr);
        }
        for (int recordNr = 1; recordNr < this.recordCount; recordNr++) {
            if (!freeRecordNrs.contains(recordNr)) {
                int recordIx = getRecordIx(recordNr);
                byte recordFill = 0;
                for (int treeIx = recordIx; treeIx < getRecordIx(recordNr + 1); treeIx++) {
                    int value = this.tree[treeIx];
                    if (value > 0) {
                        if (getOffset(value) != 0) {
                            throw new IllegalStateException(String.format(
                                "Child record index %d at %d is not at record boundary",
                                value,
                                treeIx));
                        } else if (getParentIx(getRecordNr(value)) != treeIx) {
                            throw new IllegalStateException(
                                String.format("Child record index %d at %d points back to %d",
                                    value,
                                    treeIx,
                                    getParentIx(getRecordNr(value))));
                        }
                    }
                    if (value != 0) {
                        recordFill += 1 + getOffset(treeIx);
                    }
                }
                if (recordFill == 0) {
                    throw new IllegalStateException(
                        String.format("Non-empty record %d has no entries", recordNr));
                } else if (this.fill[recordNr] != recordFill) {
                    throw new IllegalStateException(
                        String.format("Record fill of %d should be %d rather than %d",
                            recordNr,
                            recordFill,
                            this.fill[recordNr]));
                }
                int parentIx = getParentIx(recordNr);
                int parentNr = getRecordNr(parentIx);
                if (parentNr >= this.recordCount) {
                    throw new IllegalStateException(
                        String.format("Parent %d of record %d larger than count %d",
                            parentNr,
                            recordNr,
                            this.recordCount));
                } else if (freeRecordNrs.contains(parentNr)) {
                    throw new IllegalStateException(
                        String.format("Parent %d of record %d is free record", parentNr, recordNr));
                } else if (getRecordNr(this.tree[parentIx]) != recordNr) {
                    throw new IllegalStateException(
                        String.format("Parent index %d of record %d points to record %d",
                            parentIx,
                            recordNr,
                            getRecordNr(this.tree[parentIx])));
                }
            }
        }
    }

    /**
     * Array holding the tree structure.
     */
    private int[] tree;
    /** Array storing for every tree record the number of elements in it. */
    private byte[] fill;
    /**
     * The record number of the first element in the free record chain.
     */
    private int freeRecordNr;
    /**
     * The currently reserved number of positions in the store.
     */
    private int recordCount;
    /**
     * The index of the first free key in the free key chain.
     */
    private int freeKeyIx;
    /**
     * The highest index in {@link #keys} that is currently in use.
     */
    private int keyCount;
    /**
     * The number of elements in the store.
     */
    int size;
    /**
     * The key codes.
     */
    private int[] codes;
    /**
     * The array of current keys.
     */
    Object[] keys;
    /**
     * Number of bits involved in the root branch.
     */
    private final int rootResolution;
    /**
     * The mask of the branch value within a key. This equals
     * <code>2^rootResolution - 1</code>.
     */
    private final int rootMask;
    /**
     * Number of bits involved in a single branch.
     */
    private final int resolution;
    /**
     * The mask of the branch value within a key. This equals
     * <code>2^resolution - 1</code>.
     */
    final int mask;
    /**
     * The strategy to compare keys whose hash codes are equal.
     */
    private final Equator<T> equator;
    /** Modification count, for fail-fast iterating. */
    int modCount;

    /**
     * Returns an equator which calls {@link #equals(Object)} to determine
     * equality.
     */
    @SuppressWarnings("unchecked")
    static public <E> Equator<E> equalsEquator() {
        return EQUALS_EQUATOR;
    }

    /** Returns an equator which only compares hash codes to determine equality. */
    @SuppressWarnings("unchecked")
    static public <E> Equator<E> hashCodeEquator() {
        return HASHCODE_EQUATOR;
    }

    /** Returns an equator which uses object identity to determine equality. */
    @SuppressWarnings("unchecked")
    static public <E> Equator<E> identityEquator() {
        return IDENTITY_EQUATOR;
    }

    /** Returns the number of {@link TreeHashSet.MyListEntry} instances. */
    static public int getMyListEntryCount() {
        return MyListEntry.instanceCount;
    }

    /** Maintains an average fill degree. */
    static private float getAverageFill(int recordCount, int size) {
        sum += size / (double) recordCount;
        count++;
        return (float) sum / count;
    }

    /**
     * Sum of fill ratios summed over all invocations of
     * {@link #getAverageFill(int, int)}.
     */
    static private double sum;
    /** Number of invocations of {@link #getAverageFill(int, int)}. */
    static private int count;

    /** The maximum record resolution supported. */
    static public final int MAX_RESOLUTION = 4;
    /**
     * The default initial capacity of the set.
     */
    static public final int DEFAULT_CAPACITY = 16;

    /**
     * The default resolution of the tree branches.
     */
    static public final int DEFAULT_RESOLUTION = 3;
    /**
     * The default resolution of the root branch.
     */
    static public final int DEFAULT_ROOT_RESOLUTION = 4;
    /**
     * Equator that calls {@link Object#hashCode()} in
     * <code>Equator.getCode(Object)</code> and
     * {@link Object#equals(java.lang.Object)} in
     * <code>Equator.areEqual(Object, Object)</code>.
     */
    @SuppressWarnings({"rawtypes"})
    static public final Equator EQUALS_EQUATOR = new Equator<Object>() {
        /**
         * @return <code>key.hashCode()</code>.
         */
        @Override
        public int getCode(Object key) {
            return key.hashCode();
        }

        /**
         * @return <code>true</code> if <code>o1.equals(o2)</code>.
         */
        @Override
        public boolean areEqual(Object o1, Object o2) {
            return o1.equals(o2);
        }

        /** This implementation returns <code>false</code> always. */
        @Override
        public boolean allEqual() {
            return false;
        }
    };

    /**
     * Equator that calls {@link System#identityHashCode(Object)} in
     * <code>Equator.getCode(Object)</code> and object equality in
     * <code>Equator.areEqual(Object, Object)</code>.
     */
    @SuppressWarnings({"rawtypes"})
    static public final Equator IDENTITY_EQUATOR = new Equator<Object>() {
        /**
         * @return <code>System.identityHashCode(key)</code>
         */
        @Override
        public int getCode(Object key) {
            return System.identityHashCode(key);
        }

        /**
         * @return <code>true</code> if <code>o1 == o2</code>.
         */
        @Override
        public boolean areEqual(Object o1, Object o2) {
            return o1 == o2;
        }

        /** This implementation returns <code>false</code> always. */
        @Override
        public boolean allEqual() {
            return false;
        }
    };

    /**
     * Equator that calls {@link Object#hashCode()} in
     * <code>Equator.getCode(Object)</code> and always returns <code>true</code>
     * in <code>Equator.areEqual(Object, Object)</code>.
     */
    @SuppressWarnings({"rawtypes"})
    static public final Equator HASHCODE_EQUATOR = new Equator<Object>() {
        /**
         * @return <code>key.hashCode()</code>
         */
        @Override
        public int getCode(Object key) {
            return key.hashCode();
        }

        /**
         * @return <code>true</code> always.
         */
        @Override
        public boolean areEqual(Object o1, Object o2) {
            return true;
        }

        /** This implementation returns <code>true</code> always. */
        @Override
        public boolean allEqual() {
            return true;
        }
    };

    /**
     * The equator to be used if none is indicated explicitly. Set to
     * {@link #EQUALS_EQUATOR}.
     */
    @SuppressWarnings({"rawtypes"})
    static public final Equator DEFAULT_EQUATOR = EQUALS_EQUATOR;
    /**
     * Number of bytes in an <code>int</code>.
     */
    static private final int BYTES_PER_INT = 4;
    /**
     * Number of bytes in an object reference.
     */
    static private final int BYTES_PER_REF = 4;
    /**
     * Number of bytes in an object handle.
     */
    static private final int BYTES_PER_OBJECT = 12;
    /** Factor by which the arrays grow if more space is needed. */
    static private final double GROWTH_FACTOR = 1.5;
    /** Flag indicating that some fill statistics should be printed. */
    static private final boolean FILL_PRINT = false;
    /** Flag indicating that extra asserts should be used. */
    static private final boolean DEBUG = false;
    /** Flag indicating that some size statistics should be printed. */
    static private final boolean SIZE_PRINT = false;

    /**
     * Auxiliary class to encode the linked list of distinct entries with the
     * same code. The linking is done through index values, which represent
     * indices in the {@link TreeHashSet#keys}-array.
     */
    static private class MyListEntry<T> {
        /** Constructs an entry with a given value, and a given next index. */
        MyListEntry(T value, int next) {
            this.next = next;
            this.value = value;
            instanceCount++;
        }

        /** Returns the index (in #key}s) of the next entry with the same code. */
        int getNext() {
            return this.next;
        }

        /** Returns the value in this entry. */
        T getValue() {
            return this.value;
        }

        /** The value of this entry. */
        private final T value;
        /** The index of the next entry. */
        private final int next;

        /** Number of {@link TreeHashSet.MyListEntry} instances. */
        static int instanceCount;
    }
}
