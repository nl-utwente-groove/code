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

import groove.graph.Node;
import groove.graph.NodeEdgeHashMap;
import groove.graph.NodeEdgeMap;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * A permutation over elements of two sets. The functionality of the class can
 * be better explained with an example.
 * Suppose as input the following sets:
 * 
 * N = [0, 1, 2], M = [A, B, C];
 * 
 * Then, it should return the following set of tuples, in some arbitrary order:
 *  
 * [<0,A>, <1,B>, <2,C>],
 * [<0,A>, <1,C>, <2,B>],
 * [<0,B>, <1,A>, <2,C>],
 * [<0,B>, <1,C>, <2,A>],
 * [<0,C>, <1,A>, <2,B>],
 * [<0,C>, <1,B>, <2,A>].
 * 
 * @author Eduardo Zambon
 */
public class SetPermutation {

    // ------------------------------------------------------------------------
    // Object Fields
    // ------------------------------------------------------------------------

    private int a[];
    private Node n[];
    private Node m[];
    private long numLeft;
    private long total;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /**
     * WARNING: Don't make n too large. Recall that the number of permutations
     * is n!, which can be very large, even when n is as small as 20.
     * 20! = 2,432,902,008,176,640,000 and
     * 21! is too big to fit into a Java long.
     */
    private SetPermutation(int n) {
        assert (n > 0) : "Invalid value.";
        this.a = new int[n];
        this.total = factorial(n);
        this.numLeft = this.total;
        for (int i = 0; i < this.a.length; i++) {
            this.a[i] = i;
        }
    }

    private SetPermutation(Collection<Node> collN, Collection<ShapeNode> collM) {
        this(collN.size());
        this.n = new Node[collN.size()];
        this.m = new Node[collM.size()];
        this.n = collN.toArray(this.n);
        this.m = collM.toArray(this.m);
    }

    // ------------------------------------------------------------------------
    // Static methods
    // ------------------------------------------------------------------------

    /**
     * Returns the factorial of n, when n >= 0 and n <= 20. Otherwise we
     * have an overflow.
     */
    private static long factorial(long n) {
        assert (n >= 0 && n <= 20) : "Overflow in factorial: " + n;
        if (n == 0) {
            return 1;
        } else {
            return n * factorial(n - 1);
        }
    }

    /** Returns the of permutations of the given collections. */
    public static Set<NodeEdgeMap> getPermutationSet(Collection<Node> collN,
            Collection<ShapeNode> collM) {
        assert (collN.size() == collM.size()) : "Collections should have the same size!";
        Set<NodeEdgeMap> result = new HashSet<NodeEdgeMap>();

        SetPermutation sp = new SetPermutation(collN, collM);
        while (sp.numLeft > 0) {
            int idx[] = sp.getNext();
            NodeEdgeMap map = new NodeEdgeHashMap();
            for (int i = 0; i < sp.n.length; i++) {
                Node key = sp.n[i];
                Node image = sp.m[idx[i]];
                map.putNode(key, image);
            }
            result.add(map);
        }

        return result;
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    /** Generates the next permutation. */
    private int[] getNext() {
        if (this.numLeft == this.total) {
            this.numLeft = this.numLeft - 1;
            return this.a;
        }

        // Find largest index j with a[j] < a[j+1] .
        int j = this.a.length - 2;
        while (this.a[j] > this.a[j + 1]) {
            j--;
        }

        // Find index k such that a[k] is smallest integer
        // greater than a[j] to the right of a[j] .
        int k = this.a.length - 1;
        while (this.a[j] > this.a[k]) {
            k--;
        }

        // Swap a[j] and a[k] .
        int temp = this.a[k];
        this.a[k] = this.a[j];
        this.a[j] = temp;

        // Put tail end of permutation after j-th position in increasing order.
        int r = this.a.length - 1;
        int s = j + 1;

        while (r > s) {
            temp = this.a[s];
            this.a[s] = this.a[r];
            this.a[r] = temp;
            r--;
            s++;
        }

        this.numLeft = this.numLeft - 1;
        return this.a;
    }

}
