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
package groove.explore;

import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Supplier;

/**
 * Frontier that orders its elements according to a given priority function.
 * States with the same priority are ordered according to a given traversal kind.
 * @author Arend Rensink
 * @version $Revision $
 */
public class PriorityFrontier extends ExploreFrontier {
    /**
     * Constructs a frontier with a given heuristic and traversal kind.
     * @param factory factory to create sub-frontiers
     */
    public PriorityFrontier(Supplier<ExploreFrontier> factory, int maxSize) {
        super(maxSize);
        this.factory = factory;
        this.frontierMap = new TreeMap<>();
    }

    /** Factory to create sub-frontiers. */
    private final Supplier<ExploreFrontier> factory;
    /** Mapping from priority values to sub-frontiers. */
    private final SortedMap<Integer,ExploreFrontier> frontierMap;
    /** Size of the frontier; maintained while adding and removing elements. */
    private int size;

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public ExplorePoint next() {
        ExplorePoint result = null;
        // find the first non-empty sub-frontier
        Integer firstValue = this.frontierMap.firstKey();
        ExploreFrontier sub = this.frontierMap.get(firstValue);
        if (sub.hasNext()) {
            result = sub.next();
            this.size--;
            if (!sub.hasNext()) {
                // this sub-frontier is empty; remove it
                this.frontierMap.remove(firstValue);
            }
        }
        return result;
    }

    @Override
    protected void addToFrontier(ExplorePoint point) {
        int value = point.getPriority();
        ExploreFrontier subFrontier = this.frontierMap.get(value);
        if (subFrontier == null) {
            this.frontierMap.put(value, subFrontier = this.factory.get());
        }
        subFrontier.add(point);
    }

    @Override
    public void removeLast() {
        Integer lastValue = this.frontierMap.lastKey();
        ExploreFrontier sub = this.frontierMap.get(lastValue);
        sub.removeLast();
        if (!sub.hasNext()) {
            this.frontierMap.remove(lastValue);
        }
        this.size--;
    }
}
