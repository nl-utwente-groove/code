/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
package groove.abstraction.pattern.trans;

import groove.abstraction.MyHashSet;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * An iterator over sets of sets. This can be seen as the cartesian product of
 * the sets.
 * 
 * Sets returned by the next() method can share elements. Thus, these elements
 * should not be further aliased. The sets returned should not be modified.
 * 
 * @author Eduardo Zambon
 */
public class CombinatorialIterator<T> implements Iterator<Set<T>> {

    // ------------------------------------------------------------------------
    // Object Fields
    // ------------------------------------------------------------------------

    private int size;
    private T elems[][];
    private int currIdx[];
    private boolean hasNext;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** 
     * Default constructor.
     * @param setOfSets should not be empty.
     */
    @SuppressWarnings("unchecked")
    public CombinatorialIterator(Set<Set<T>> setOfSets) {
        int size = setOfSets.size();
        this.size = size;
        if (size > 0) {
            this.elems = (T[][]) new Object[size][];
            this.currIdx = new int[size];
            Iterator<Set<T>> iterSet = setOfSets.iterator();
            for (int i = 0; i < size; i++) {
                Set<T> set = iterSet.next();
                this.elems[i] = (T[]) set.toArray();
                this.currIdx[i] = 0;
            }
            this.hasNext = true;
        } else {
            this.hasNext = false;
        }
    }

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    @Override
    public boolean hasNext() {
        return this.hasNext;
    }

    @Override
    public Set<T> next() {
        if (!this.hasNext) {
            throw new NoSuchElementException();
        } else {
            // We have at least one next element and the indexes of build
            // element are already set. Just iterate over the structure and
            // construct the pairs.
            Set<T> result = new MyHashSet<T>();
            for (int i = 0; i < this.size; i++) {
                // First take the current elements.
                int currIdx = this.currIdx[i];
                T curr = this.elems[i][currIdx];
                result.add(curr);
            }
            // Properly update the indexes to make sure we are pointing to the
            // right next element.
            updateIndexes();
            return result;
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    /** Adjusts the indexes of the elements to be returned next. */
    private void updateIndexes() {
        int i = this.size - 1;
        boolean goUpALevel = true;
        while (goUpALevel) {
            if (this.currIdx[i] == this.elems[i].length - 1) {
                // We are at the end of the array. Go up a level, if possible.
                if (i == 0) {
                    // We are at the top level. Cannot go up anymore.
                    this.hasNext = false;
                    goUpALevel = false;
                } else {
                    this.currIdx[i] = 0;
                    i--;
                    goUpALevel = true;
                }
            } else {
                // No problem, just update the index at our current level.
                this.currIdx[i]++;
                goUpALevel = false;
            }
        }
    }

}
