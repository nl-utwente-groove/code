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
/* $Id$ */
package groove.util;

import groove.graph.Node;

import java.util.Arrays;
import java.util.Collection;

/**
 * Dispenser that returns numbers not occurring in the given array.
 *  
 * @author Eduardo Zambon

 */
public class FreeNumberDispenser extends Dispenser {
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

    @Override
    protected int computeNext() {
        this.lastVal++;
        // keep increasing until we find an unused number
        // or we run out of numbers
        while (this.currIdx < this.numbers.length
            && this.lastVal == this.numbers[this.currIdx]) {
            this.currIdx++;
            this.lastVal++;
        }
        return this.lastVal;
    }

    /** The sorted array for which a new number is computed. */
    private final int numbers[];
    /** The current index to continue the search. */
    private int currIdx;
    /** The last returned value. */
    private int lastVal;
}
