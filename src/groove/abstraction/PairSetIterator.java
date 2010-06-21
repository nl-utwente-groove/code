/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
package groove.abstraction;

import groove.util.Pair;

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * An iterator over sets of sets. This can be seen as the cartesian product of
 * the sets. The functionality of the class can be better explained with an
 * example. Suppose as input for the constructor the following set:
 * 
 * Set<Pair<String,Set<Integer>>> pairSet = [<A,[0, 1, 2]>, <B,[3, 4]>];
 * 
 * Then, the iterator should return the following sets of pairs, in some
 * arbitrary order:
 *  
 * [<A,0>, <B,3>]
 * [<A,0>, <B,4>]
 * [<A,1>, <B,3>]
 * [<A,1>, <B,4>]
 * [<A,2>, <B,3>]
 * [<A,2>, <B,4>]
 * 
 * Maps returned by the next() method can share elements. Thus, these elements
 * should not be further aliased. The maps returned should not be modified.
 * @author Eduardo Zambon
 * @version $Revision $
 */
public class PairSetIterator<N,M> implements Iterator<Set<Pair<N,M>>> {

    // ------------------------------------------------------------------------
    // Object Fields
    // ------------------------------------------------------------------------

    private int size;
    private N pairFirst[];
    private M pairSecond[][];
    private int currIdx[];
    private boolean hasNext;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** 
     * An example of a valid input for this constructor is
     * Set<Pair<String,Set<Integer>>> pairSet = [<A,[0, 1, 2]>, <B,[3, 4]>];
     * @param pairSet should not be empty.
     */
    @SuppressWarnings("unchecked")
    public PairSetIterator(Set<Pair<N,Set<M>>> pairSet) {
        int size = pairSet.size();
        assert size > 0 : "Cannot iterate over an empty set.";
        this.size = size;
        // Stupid Java generics don't allow the declaration of an array of N ...
        this.pairFirst = (N[]) new Object[size];
        this.pairSecond = (M[][]) new Object[size][];
        this.currIdx = new int[size];
        Iterator<Pair<N,Set<M>>> iterSet = pairSet.iterator();
        for (int i = 0; i < size; i++) {
            Pair<N,Set<M>> pair = iterSet.next();
            this.pairFirst[i] = pair.first();
            this.pairSecond[i] = (M[]) pair.second().toArray();
            this.currIdx[i] = 0;
        }
        this.hasNext = true;
    }

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    @Override
    public boolean hasNext() {
        return this.hasNext;
    }

    @Override
    public Set<Pair<N,M>> next() {
        if (!this.hasNext) {
            throw new NoSuchElementException();
        } else {
            Set<Pair<N,M>> result = new HashSet<Pair<N,M>>();
            for (int i = 0; i < this.size; i++) {
                // First take the current elements.
                N currN = this.pairFirst[i];
                int currIdx = this.currIdx[i];
                M currM = this.pairSecond[i][currIdx];
                result.add(new Pair<N,M>(currN, currM));
            }
            this.updateIndexes();
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

    private void updateIndexes() {
        int i = this.size - 1;
        boolean goUpALevel = true;
        while (goUpALevel) {
            if (this.currIdx[i] == this.pairSecond[i].length - 1) {
                // We are at the end of the array. Go up a level, if possible.
                if (i == 0) {
                    // We are the top level. Cannot go up anymore.
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
