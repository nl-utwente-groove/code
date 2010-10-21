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
 * $Id: MinimizedTreeIntSet.java,v 1.2 2008-01-30 09:32:01 iovka Exp $
 */
package groove.util;

/**
 * Implementation of a {@link IntSet} on the basis of an internally built up
 * binary tree representation of the integers in the set. The tree uses the bit
 * representation of the <code>int</code>s as the basis for branching. The
 * representation tries to be clever by not branching on shared bits.
 * @author Arend Rensink
 * @version $Revision$ $Date: 2008-01-30 09:32:01 $
 */
@Deprecated
final public class MinimizedTreeIntSet implements IntSet {
    /**
     * Uses the <code>capacity</code> parameter to assign a new length to the
     * underlying arrays, if they are smaller than this capacity.
     */
    public void clear(int capacity) {
        // (BRANCH_PATTERN+1) is the number of positions to be reserved for
        // every key
        if (this.bitCount == null
            || this.bitCount.length < (BRANCH_PATTERN + 1) * capacity) {
            // the new length is an overestimate
            int newLength = ((int) (1.5 * (BRANCH_PATTERN + 1))) * capacity;
            this.bitCount = new int[newLength];
            this.fragment = new int[newLength];
            this.next = new int[newLength];
        }
        this.posCount = 0;
        this.size = 0;
    }

    public int size() {
        return this.size;
    }

    public boolean add(int key) {
        if (this.size == 0) {
            // the first certificate is the root of the tree
            int pos = newPos();
            this.bitCount[pos] = TOTAL_BIT_COUNT;
            this.fragment[pos] = key;
            this.next[pos] = LEAF_NEXT;
            this.size++;
            return true;
        } else {
            // flag to indicate a fragment is to be split
            boolean split;
            // flag to indicate we can stop searching the tree
            boolean done;
            // position to start searching
            int pos = 0;
            // the number of remaining relevant bits of the key
            byte keyBitCount = TOTAL_BIT_COUNT;
            // the fragment at the currently investigated tree node
            int curFragment;
            // the number of bits of the fragment at the currently investigated
            // tree node
            int curBitCount;
            // the next position with respect to the currently investigated tree
            // node
            int curNext;
            do {
                curFragment = this.fragment[pos];
                curBitCount = this.bitCount[pos];
                curNext = this.next[pos];
                // Test the relevant number of bits of the key against the
                // fragment
                split = (key & BIT_PATTERN[curBitCount]) != curFragment;
                // if the next position is 0, we're at a node that was never
                // used before
                // stop searching if we know we have to add
                // or if we have (successfully) tested all bits of the key
                done = split || curNext == 0 || curBitCount == keyBitCount;
                if (!done) {
                    key >>>= curBitCount;
                    pos = curNext + (key & BRANCH_PATTERN);
                    key >>>= BRANCH_BIT_COUNT;
                    keyBitCount -= curBitCount + BRANCH_BIT_COUNT;
                }
            } while (!done);
            if (split) {
                // this is the case where only part of the current fragment
                // matched
                // first find out which part
                int newBitCount = 0;
                int newFragment = 0;
                int keyBranch, fragmentBranch;
                while ((keyBranch = (key & BRANCH_PATTERN)) == (fragmentBranch =
                    (curFragment & BRANCH_PATTERN))) {
                    key >>>= BRANCH_BIT_COUNT;
                    curFragment >>>= BRANCH_BIT_COUNT;
                    newFragment |= fragmentBranch << newBitCount;
                    newBitCount += BRANCH_BIT_COUNT;
                }
                this.bitCount[pos] = newBitCount;
                this.fragment[pos] = newFragment;
                int newNext = this.next[pos] = newPos();
                this.bitCount[newNext + fragmentBranch] =
                    curBitCount - (newBitCount + BRANCH_BIT_COUNT);
                this.fragment[newNext + fragmentBranch] =
                    curFragment >>> BRANCH_BIT_COUNT;
                this.next[newNext + fragmentBranch] = curNext;
                this.bitCount[newNext + keyBranch] =
                    keyBitCount - (newBitCount + BRANCH_BIT_COUNT);
                this.fragment[newNext + keyBranch] = key >>> BRANCH_BIT_COUNT;
                this.next[newNext + keyBranch] = LEAF_NEXT;
                this.size++;
                return true;
            } else if (curNext == 0) {
                // just add the key at the current position
                this.bitCount[pos] = keyBitCount;
                this.fragment[pos] = key;
                this.next[pos] = LEAF_NEXT;
                this.size++;
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Reserves space for a new certificate, and returns the position of the new
     * certificate.
     */
    private int newPos() {
        int newPos = (BRANCH_PATTERN + 1) * this.posCount;
        for (int i = 0; i <= BRANCH_PATTERN; i++) {
            this.next[newPos + i] = 0;
        }
        this.posCount++;
        return newPos;
    }

    /**
     * The currently reserved number of positions in the store.
     */
    private int posCount;
    /**
     * The current size of the store.
     */
    private int size;
    /**
     * The number of shared bits at each position.
     */
    private int[] bitCount;
    /**
     * The shared fragment of the certificate. The significant length of the
     * fragment is stored in {@link #bitCount}.
     */
    private int[] fragment;
    /**
     * Pointer to the position holding the next part of the certificate.
     */
    private int[] next;
    /**
     * Value to be used in the {@link #next} array to indicate a leaf.
     */
    static private final int LEAF_NEXT = -1;
    /**
     * Number of bits involved in a single branch.
     */
    static private final int BRANCH_BIT_COUNT = 2;
    /**
     * Pattern selecting the bits in a single branch.
     */
    static private final int BRANCH_PATTERN = (1 << BRANCH_BIT_COUNT) - 1;
    /**
     * Maximum number of bits in an <code>int</code>.
     */
    static private final int TOTAL_BIT_COUNT = 32;
    /**
     * For each number of bits, a pattern consisting of <code>1</code>s at
     * each of the relevant bit positions. This corresponds to
     * <code>2^bit - 1</code>.
     */
    static private final int[] BIT_PATTERN = new int[TOTAL_BIT_COUNT + 1];
    {
        // initialize BIT_PATTERN
        int pattern = 0;
        for (int i = 0; i < TOTAL_BIT_COUNT + 1; i++) {
            BIT_PATTERN[i] = pattern;
            pattern = 2 * pattern + 1;
        }
    }
}