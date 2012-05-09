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
package groove.abstraction.neigh.trans;

import groove.abstraction.MyHashMap;
import groove.abstraction.MyHashSet;
import groove.abstraction.neigh.shape.ShapeEdge;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Iterator that produces the power set of a given set. It is implemented mainly
 * as bit-wise operations on integers for efficiency.
 * 
 * @author Eduardo Zambon
 */
public final class PowerSetIterator implements
        Iterator<Map<EdgeBundle,Set<ShapeEdge>>> {

    // ------------------------------------------------------------------------
    // Object fields
    // ------------------------------------------------------------------------

    /**
     * Flag that indicates if the empty set should be returned.
     */
    private final boolean skipEmpty;
    /**
     * Array of bundles taken from the set passed to the constructor.
     */
    private final EdgeBundle bundles[];
    /**
     * Array of binary masks for edges of each bundle. Has the same size of
     * the bundles array. A mask 'masks[i]' is associated with bundle
     * 'bundles[i]' and has as many bits set as the associate bundle has edges.
     * Each mask is shifted to the left such that there's no overlap of masks.
     * Each bit set in a mask corresponds to an edge in the 'edges' array.
     */
    private final int masks[];
    /**
     * All edges from the bundles. Stored as an array for efficiency. 
     */
    private final ShapeEdge edges[];
    /**
     * The set of results of this iterator. Each element of this array is
     * an integer where each bit set corresponds to an edge that is present
     * in the result map. The length of this array is 2^(edges) but its tail
     * may not the filled completely because some edge configurations may not
     * be valid.
     */
    private final int results[];
    /**
     * Object that is returned by the iterator. This map is cleared with
     * each call to {@link #next()}. Used to avoid object creation.
     */
    private final Map<EdgeBundle,Set<ShapeEdge>> resultMap;
    /** The real length of the results array, i.e., the number of valid elements. */
    private int total;
    /** Current index in the results array. */
    private int curr;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /**
     * Constructs the iterator. */
    public PowerSetIterator(Set<EdgeBundle> bundles, boolean skipEmpty) {
        // Create the bundle and mask arrays.
        this.bundles = new EdgeBundle[bundles.size()];
        bundles.toArray(this.bundles);
        this.masks = new int[this.bundles.length];
        // Initialise the result map.
        this.resultMap = new MyHashMap<EdgeBundle,Set<ShapeEdge>>();
        // Count the number of edges and initialise the arrays.
        int edgeCount = 0;
        for (int bundleIdx = 0; bundleIdx < this.bundles.length; bundleIdx++) {
            EdgeBundle bundle = this.bundles[bundleIdx];
            this.resultMap.put(bundle, new MyHashSet<ShapeEdge>());
            int bundleSize = bundle.possibleEdges.size();
            int mask = (((int) Math.pow(2, bundleSize)) - 1) << edgeCount;
            this.masks[bundleIdx] = mask;
            edgeCount += bundleSize;
        }
        this.edges = new ShapeEdge[edgeCount];
        // Fill the edges array.
        int edgeIdx = 0;
        for (int bundleIdx = 0; bundleIdx < this.bundles.length; bundleIdx++) {
            for (ShapeEdge edge : this.bundles[bundleIdx].possibleEdges) {
                this.edges[edgeIdx++] = edge;
            }
        }
        // Initialise the results array.
        int resultCount = (int) Math.pow(2, edgeCount);
        this.results = new int[resultCount];
        this.total = 0;
        int result = 0;
        for (int i = 0; i < resultCount; i++) {
            if (this.isValidResult(result)) {
                this.results[this.total++] = result;
            }
            result++;
        }
        // Initialise the current solution index.
        this.skipEmpty = skipEmpty;
        if (this.skipEmpty) {
            this.curr = 1;
        } else {
            this.curr = 0;
        }
    }

    // ------------------------------------------------------------------------
    // Overriden methods
    // ------------------------------------------------------------------------

    @Override
    public boolean hasNext() {
        return this.curr < this.total;
    }

    @Override
    public Map<EdgeBundle,Set<ShapeEdge>> next() {
        assert this.hasNext();
        // Get the next result encoded as an integer.
        int result = this.results[this.curr++];
        // Iterate over all bundles.
        for (int bundleIdx = 0; bundleIdx < this.bundles.length; bundleIdx++) {
            EdgeBundle bundle = this.bundles[bundleIdx];
            int mask = this.masks[bundleIdx];
            // This variable tells us with edges of the bundle to add.
            int maskedResult = result & mask;
            Set<ShapeEdge> edgeSet = this.resultMap.get(bundle);
            edgeSet.clear();
            // Go over all edges and add the selected ones.
            for (int edgeIdx = 0; edgeIdx < this.edges.length; edgeIdx++) {
                int shiftedIdx = 1 << edgeIdx;
                if ((maskedResult & shiftedIdx) == shiftedIdx) {
                    edgeSet.add(this.edges[edgeIdx]);
                }
            }
        }
        return this.resultMap;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    /**
     * Checks if the given edge configuration (encoded as an integer) satisfies
     * the multiplicity constraints for each edge bundle.
     */
    private boolean isValidResult(int result) {
        boolean isValid = true;
        for (int bundleIdx = 0; bundleIdx < this.bundles.length; bundleIdx++) {
            EdgeBundle bundle = this.bundles[bundleIdx];
            int mask = this.masks[bundleIdx];
            int maskedResult = result & mask;
            int bound = bundle.origEsMult.getUpperBound();
            if (Integer.bitCount(maskedResult) > bound) {
                isValid = false;
                break;
            }
        }
        return isValid;
    }

    /** Returns the number of results for the iterator. */
    public int resultCount() {
        if (this.skipEmpty) {
            return this.total - 1;
        } else {
            return this.total;
        }
    }

}
