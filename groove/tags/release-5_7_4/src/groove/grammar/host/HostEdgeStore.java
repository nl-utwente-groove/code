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
package groove.grammar.host;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Convenience type for a deterministic map from
 * generic key types to sets of edges. 
 * @author Arend Rensink
 * @version $Revision $
 */
public final class HostEdgeStore<K> extends LinkedHashMap<K,HostEdgeSet> {
    /** Returns a fresh empty store. */
    public HostEdgeStore() {
        // empty
    }

    /** Copies a given store. 
     * A flag indicates if the image edge sets should also be copied (rather than shared)
     * @param deepCopy if {@code true}, the image sets are also copied
     */
    public HostEdgeStore(HostEdgeStore<K> original, boolean deepCopy) {
        for (Map.Entry<K,HostEdgeSet> entry : original.entrySet()) {
            HostEdgeSet image = entry.getValue();
            put(entry.getKey(), deepCopy ? HostEdgeSet.newInstance(image)
                    : image);
        }
    }

    /** 
     * Adds a given key to the map, with an initially empty set of edges.
     */
    public boolean addKey(K key) {
        HostEdgeSet oldValue = put(key, HostEdgeSet.newInstance(null));
        return oldValue == null;
    }

    /**
     * Removes a given edge from the edges stored for a given key,
     * and returns the resulting (remaining) set of edges.
     * @param key the key for which the edge is to be removed; non-{@code null}
     * @param edge the edge to be removed; non-{@code null}
     * @param refresh if {@code true}, the edge set for {@code key} is cloned
     * @return the resulting edge set for {@code key}
     */
    public HostEdgeSet removeEdge(K key, HostEdge edge, boolean refresh) {
        HostEdgeSet result = get(key);
        if (result != null) {
            if (refresh) {
                put(key, result = HostEdgeSet.newInstance(result));
            }
            result.remove(edge);
        }
        return result;
    }

    /**
     * Adds a given edge from the edges stored for a given key,
     * and returns the resulting (augmented) set of edges.
     * @param key the key for which the edge is to be added; non-{@code null}
     * @param edge the edge to be added; non-{@code null}
     * @param refresh if {@code true}, the edge set for {@code key} is cloned
     * @return the resulting edge set for {@code key}
     */
    public HostEdgeSet addEdge(K key, HostEdge edge, boolean refresh) {
        HostEdgeSet result = get(key);
        if (refresh || result == null) {
            put(key, result = HostEdgeSet.newInstance(result));
        }
        result.add(edge);
        return result;
    }
}
