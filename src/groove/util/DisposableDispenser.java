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
import java.util.Set;

/**
 * State-less dispenser that can only be used once.
 * @author Eduardo Zambon

 */
public class DisposableDispenser implements Dispenser {

    /**
     * Creates the dispenser for the given array. The array is modified
     * in the process.
     */
    public DisposableDispenser(int numbers[]) {
        this.numbers = numbers;
        Arrays.sort(this.numbers);
    }

    /** Creates the dispenser for the given set. The set is unchanged. */
    public DisposableDispenser(Set<? extends Node> nodeSet) {
        this(getArray(nodeSet));
    }

    /** Creates an array with node numbers of the given set. */
    private static int[] getArray(Set<? extends Node> nodeSet) {
        int numbers[] = new int[nodeSet.size()];
        int i = 0;
        for (Node node : nodeSet) {
            numbers[i] = node.getNumber();
            i++;
        }
        return numbers;
    }

    /**
     * This method can only be called once. After the first call, throws
     * an unsupported operation exception.
     * Returns the first free node number not occurring in the node set
     * passed to the constructor. 
     */
    public int getNext() {
        if (this.numbers != null) {
            int result = 0;
            for (int i = 0; i < this.numbers.length; i++) {
                if (result == this.numbers[i]) {
                    result++;
                } else {
                    break;
                }
            }
            this.numbers = null;
            return result;
        } else {
            throw new UnsupportedOperationException();
        }

    }

    /** The sorted array for which a new number is computed. */
    private int numbers[];
}
