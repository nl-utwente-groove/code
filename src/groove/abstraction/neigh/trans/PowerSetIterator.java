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

import groove.abstraction.neigh.MyHashMap;
import groove.abstraction.neigh.MyHashSet;
import groove.abstraction.neigh.shape.ShapeEdge;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Iterator that produces the power set of a given set.
 * @author Eduardo Zambon
 */
public class PowerSetIterator implements
        Iterator<Map<EdgeBundle,Set<ShapeEdge>>> {

    private final boolean skipEmpty;
    private final EdgeBundle bundles[];
    private final int masks[];
    private final ShapeEdge edges[];
    private final int results[];
    private final Map<EdgeBundle,Set<ShapeEdge>> resultMap;
    private int total;
    private int curr;

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

    @Override
    public boolean hasNext() {
        return this.curr < this.total;
    }

    @Override
    public Map<EdgeBundle,Set<ShapeEdge>> next() {
        assert this.hasNext();
        int result = this.results[this.curr++];
        for (int bundleIdx = 0; bundleIdx < this.bundles.length; bundleIdx++) {
            EdgeBundle bundle = this.bundles[bundleIdx];
            int mask = this.masks[bundleIdx];
            int maskedResult = result & mask;
            Set<ShapeEdge> edgeSet = this.resultMap.get(bundle);
            edgeSet.clear();
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
