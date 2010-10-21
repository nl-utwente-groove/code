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
/*
 * $Id: SplitTreeIntSet.java,v 1.2 2008-01-30 09:32:11 iovka Exp $
 */
package groove.util;

/**
 * Implementation of a {@link IntSet} on the basis of an internally built up
 * tree representation of the integers in the set. The tree uses the bit
 * representation of the <code>int</code>s as the basis for branching.
 * @author Arend Rensink
 * @version $Revision$ $Date: 2008-01-30 09:32:11 $
 */
@Deprecated
final public class SplitTreeIntSet implements IntSet {
    /**
     * Creates an instance with a given branch resolution. The resolution is
     * required to be at least <code>1</code>.
     * @param resolution the resolution of the tree; shold be at least
     *        <code>1</code>.
     */
    public SplitTreeIntSet(int resolution) {
        if (resolution < 1) {
            throw new IllegalArgumentException(
                "Resolution should be at least 1");
        }
        this.resolution = resolution;
        this.width = 1 << resolution;
        this.mask = this.width - 1;
    }

    /**
     * Uses the <code>capacity</code> parameter to assign a new length to the
     * underlying arrays, if they are smaller than this capacity.
     */
    public void clear(int capacity) {
        // estimate the number of slots needed to hold this many keys
        int slotCount = this.width * capacity;
        if (this.posStore == null || this.posStore.length < slotCount) {
            this.posStore = new int[slotCount];
        }
        if (this.negStore == null || this.negStore.length < slotCount) {
            this.negStore = new int[slotCount];
        }
        this.posStoreSize = 0;
        this.negStoreSize = 0;
        this.size = 0;
    }

    public int size() {
        return this.size;
    }

    public boolean add(int key) {
        boolean keySign = key >= 0;
        int[] store = store(keySign);
        int absKey = Math.abs(key);
        if (isEmpty(keySign)) {
            // at the first key, we still have to create the root of the tree
            store[newIndex(keySign) + (absKey & this.mask)] = -absKey;
            this.size++;
            return true;
        } else {
            // remaining search key
            int search = absKey;
            // current depth search, in number of bits
            int depth = 0;
            // current search position
            int index = 0;
            // precise node where the current value of pos was retrieved from
            int indexPlusOffset;
            do {
                index = store[indexPlusOffset = index + (search & this.mask)];
                search >>>= this.resolution;
                depth += this.resolution;
            } while (index > 0);
            if (index == 0) {
                // we're at an empty place of the tree
                store[indexPlusOffset] = -absKey;
                this.size++;
                return true;
            } else {
                // we've found an existing key
                int oldKey = -index;
                if (oldKey == absKey) {
                    // the old key is the same as the one we're inserting
                    return false;
                } else {
                    // we have a new key, so we have to relocate
                    // create a new position
                    index =
                        (store = store(keySign))[indexPlusOffset] =
                            newIndex(keySign);
                    // the old search value
                    int oldSearch = oldKey >>> depth;
                    // the old and new branch values
                    int offset, oldOffset;
                    // so long as old and new key coincide, keep relocating
                    while ((offset = (search & this.mask)) == (oldOffset =
                        (oldSearch & this.mask))) {
                        int newIndex = newIndex(keySign);
                        index =
                            (store = store(keySign))[index + offset] = newIndex;
                        search >>>= this.resolution;
                        oldSearch >>>= this.resolution;
                    }
                    // we've found a difference, so store.
                    store[index + oldOffset] = -oldKey;
                    store[index + offset] = -absKey;
                    this.size++;
                    return true;
                }
            }
        }
    }

    /**
     * Reserves space for a new tree branch in the positive key store, and
     * returns the index of the first position of the new branch.
     */
    private int newPosIndex() {
        int result = this.posStoreSize;
        if (result + this.width >= this.posStore.length) {
            // extend the length of the next array
            int[] newPosStore = new int[(int) (1.5 * this.posStore.length)];
            System.arraycopy(this.posStore, 0, newPosStore, 0,
                this.posStore.length);
            this.posStore = newPosStore;
        } else {
            // clean the new fragment of the next array
            for (int i = 0; i < this.width; i++) {
                this.posStore[result + i] = 0;
            }
        }
        this.posStoreSize += this.width;
        return result;
    }

    /**
     * Reserves space for a new tree branch in the negative key store, and
     * returns the index of the first position of the new branch.
     */
    private int newNegIndex() {
        int result = this.negStoreSize;
        if (result + this.width >= this.negStore.length) {
            // extend the length of the next array
            int[] newNegStore = new int[(int) (1.5 * this.negStore.length)];
            System.arraycopy(this.negStore, 0, newNegStore, 0,
                this.negStore.length);
            this.negStore = newNegStore;
        } else {
            // clean the new fragment of the next array
            for (int i = 0; i < this.width; i++) {
                this.negStore[result + i] = 0;
            }
        }
        this.negStoreSize += this.width;
        return result;
    }

    /**
     * Returns a new position in the positive or negative store, depending on
     * the required key sign. Calls {@link #newPosIndex()} or
     * {@link #newNegIndex()} for the actual new position.
     * @param keySign <code>true</code> if we want the positive keys
     */
    private int newIndex(boolean keySign) {
        return keySign ? newPosIndex() : newNegIndex();
    }

    /**
     * Returns the store of positive or negative keys, depending on the required
     * key sign.
     * @param keySign <code>true</code> if we want the positive keys
     */
    private int[] store(boolean keySign) {
        return keySign ? this.posStore : this.negStore;
    }

    /**
     * Signals if one of the stores does not yet contain a key.
     * @param keySign <code>true</code> if we want the positive keys
     */
    private boolean isEmpty(boolean keySign) {
        return keySign ? this.posStoreSize == 0 : this.negStoreSize == 0;
    }

    /**
     * The currently reserved number of positions in {@link #posStore}.
     */
    private int posStoreSize;
    /**
     * The currently reserved number of positions in {@link #negStore}.
     */
    private int negStoreSize;
    /**
     * The current size of the entire set.
     */
    private int size;
    /**
     * Array holding the tree structure fo the positive keys
     */
    private int[] posStore;
    /**
     * Array holding the tree structure fo the negative keys
     */
    private int[] negStore;
    /**
     * Number of bits involved in a single branch.
     */
    private final int resolution;
    /**
     * The width of a single branch. This equals <code>2^resolution</code>.
     */
    private final int width;
    /**
     * The mask of the branch value within a key. This equals
     * <code>width - 1</code>.
     */
    private final int mask;
}