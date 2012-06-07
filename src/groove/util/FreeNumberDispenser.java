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
/* $Id: DefaultDispenser.java,v 1.2 2008-01-30 09:32:03 iovka Exp $ */
package groove.util;

import groove.graph.Node;

import java.util.Arrays;
import java.util.Collection;

/**
 * Dispenser that returns numbers not occurring in the given array.
 *  
 * @author Eduardo Zambon

 */
public class FreeNumberDispenser implements Dispenser {

    /**
     * Creates the dispenser for the given array. The array is modified
     * in the process.
     */
    public FreeNumberDispenser(int numbers[]) {
        this.numbers = numbers;
        this.currIdx = 0;
        this.lastVal = -1;
        Arrays.sort(this.numbers);
    }

    /** Creates the dispenser for the given set. The set is unchanged. */
    public FreeNumberDispenser(Collection<? extends Node> nodeSet) {
        this(getArray(nodeSet));
    }

    /** Creates an array with node numbers of the given set. */
    private static int[] getArray(Collection<? extends Node> nodeSet) {
        int numbers[] = new int[nodeSet.size()];
        int i = 0;
        for (Node node : nodeSet) {
            numbers[i] = node.getNumber();
            i++;
        }
        return numbers;
    }

    /**
     * Returns the first free node number not occurring in the node set
     * passed to the constructor. Or -1 if no free number can be found. 
     */
    public int getNext() {
        this.lastVal++;
        if (this.currIdx < this.numbers.length
            && this.lastVal != this.numbers[this.currIdx]) {
            return this.lastVal;
        } else {
            do {
                this.currIdx++;
                this.lastVal++;
            } while (this.currIdx < this.numbers.length
                && this.lastVal == this.numbers[this.currIdx]);
            if (this.currIdx < this.numbers.length) {
                return this.lastVal;
            } else {
                return -1;
            }
        }
    }

    /** Returns the highest number on the given array. */
    public int getMaxNumber() {
        if (this.numbers.length == 0) {
            return 0;
        } else {
            return this.numbers[this.numbers.length - 1];
        }
    }

    /** The sorted array for which a new number is computed. */
    private final int numbers[];
    /** The current index to continue the search. */
    private int currIdx;
    /** The last returned value. */
    private int lastVal;

}
