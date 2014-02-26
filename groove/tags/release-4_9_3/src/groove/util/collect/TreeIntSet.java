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

import java.util.Arrays;

/**
 * Implementation of a {@link IntSet} on the basis of an internally built up
 * tree representation of the integers in the set. The tree uses the bit
 * representation of the <code>int</code>s as the basis for branching.
 * @author Arend Rensink
 * @version $Revision$
 */
final public class TreeIntSet implements IntSet {
    /**
     * Number of bits involved in the root.
     */
    static private final int ROOT_RESOLUTION = 7;
    /**
     * The width of the root branch. This equals <code>2^ROOT_RESOLUTION</code>.
     */
    static private final int ROOT_WIDTH = 1 << ROOT_RESOLUTION;
    /**
     * The mask of the root branch value. This equals
     * <code>ROOT_WIDTH - 1</code>.
     */
    static private final int ROOT_MASK = ROOT_WIDTH - 1;

    /**
     * Creates an instance with a given branch resolution. The resolution is
     * required to be at least <code>1</code>.
     * @param resolution the resolution of the tree; shold be at least
     *        <code>1</code>.
     */
    public TreeIntSet(int resolution) {
        if (resolution < 1) {
            throw new IllegalArgumentException("Resolution should be at least 1");
        }
        this.resolution = resolution;
        this.width = 1 << resolution;
        this.mask = this.width - 1;
    }

    /**
     * Uses the <code>capacity</code> parameter to assign a new length to the
     * underlying arrays, if they are smaller than this capacity.
     */
    @Override
    public void clear(int capacity) {
        if (this.keys == null || this.keys.length <= capacity) {
            this.keys = new int[2 * capacity];
            this.store = new int[2 * this.width * capacity];
        }
        this.storeSize = 0;
        this.size = 0;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean add(int key) {
        if (this.size == 0) {
            // at the first key, we still have to create the root of the tree
            int indexPlusOffset = newBranchIndex() + (key & ROOT_MASK);
            this.store[indexPlusOffset] = -newKeyIndex(key);
            return true;
        } else {
            // local copy of store, for efficiency
            int[] store = this.store;
            int resolution = this.resolution;
            int mask = this.mask;
            // precise node where the current value of index was retrieved from
            int indexPlusOffset = key & ROOT_MASK;
            // current search position
            int index = store[indexPlusOffset];
            // remaining search key
            int search = key >>> ROOT_RESOLUTION;
            // current depth search, in number of bits
            int depth = ROOT_RESOLUTION;
            while (index > 0) {
                index = store[indexPlusOffset = (index + (search & mask))];
                search >>>= resolution;
                depth += resolution;
            }
            //
            // // remaining search key
            // int search = key;
            // // current depth search, in number of bits
            // int depth = 0;
            // // current search position
            // int index = 0;
            // // precise node where the current value of index was retrieved
            // from
            // int indexPlusOffset;
            // do {
            // index = store[indexPlusOffset = index + (search & mask)];
            // search >>>= resolution;
            // depth += resolution;
            // } while (index > 0);
            if (index == 0) {
                // we're at an empty place of the tree
                store[indexPlusOffset] = -newKeyIndex(key);
                return true;
            } else {
                // we've found an existing key
                int oldKey = this.keys[-index];
                if (oldKey == key) {
                    // the old key is the same as the one we're inserting
                    return false;
                } else {
                    // we have a new key, so we have to relocate
                    // first store the position of the old key
                    int oldKeyIndex = index;
                    // create a new position
                    int newIndex = newBranchIndex();
                    index = (store = this.store)[indexPlusOffset] = newIndex;
                    // the old search value
                    int oldSearch = oldKey >>> depth;
                    // the old and new branch values
                    int oldOffset, newOffset;
                    // so long as old and new key coincide, keep relocating
                    while ((newOffset = (search & mask)) == (oldOffset = (oldSearch & mask))) {
                        newIndex = newBranchIndex();
                        index = (store = this.store)[index + newOffset] = newIndex;
                        search >>>= resolution;
                        oldSearch >>>= resolution;
                    }
                    // we've found a difference, so store.
                    store[index + oldOffset] = oldKeyIndex;
                    store[index + newOffset] = -newKeyIndex(key);
                    return true;
                }
            }
        }
    }

    /**
     * Reserves space for a new tree branch, and returns the index of the first
     * position of the new branch.
     */
    private int newBranchIndex() {
        int storeSize = this.storeSize;
        int result = this.size == 0 ? 0 : storeSize;
        storeSize += result == 0 ? ROOT_WIDTH : this.width;
        if (storeSize >= this.store.length) {
            // extend the length of the next array
            int[] newStore = new int[(int) (1.5 * storeSize)];
            System.arraycopy(this.store, 0, newStore, 0, this.store.length);
            this.store = newStore;
        } else {
            // clean the new fragment of the next array
            Arrays.fill(this.store, result, storeSize, 0);
        }
        this.storeSize = storeSize;
        return result;
    }

    /**
     * Inserts a new keys at the appropriate place in the {@link #keys} array,
     * and returns the index of the new key. The index is always positive.
     * @param key the key to be inserted
     * @return the index in {@link #keys} where <code>key</code> is stored
     */
    private int newKeyIndex(int key) {
        this.size++;
        this.keys[this.size] = key;
        return this.size;
    }

    /**
     * The currently reserved number of positions in the store.
     */
    private int storeSize;
    /**
     * The current size of the store.
     */
    private int size;
    /**
     * The actual keys.
     */
    private int[] keys;
    /**
     * Array holding the tree structure.
     */
    private int[] store;
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