/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.grammar.host;

import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

/**
 * Deterministic map from generic key types to sets of edges, as kept by
 * {@link DeltaHostGraph} for its nodes and labels.
 * There are two implementations: {@link LinkedHostEdgeStore}, an insertion-ordered
 * map that is copied entry by entry, used when graphs hand their data down to their
 * children; and {@link ForkableHostEdgeStore}, which is copied in time proportional to
 * the square root of its size, used when every graph gets a copy of its parent's data.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public interface HostEdgeStore<K> extends Map<K,HostEdgeSet> {
    /** Copies this store.
     * A flag indicates if the image edge sets should also be copied (rather than shared)
     * @param deepCopy if {@code true}, the image sets are also copied
     */
    HostEdgeStore<K> copy(boolean deepCopy);

    /**
     * Adds a given key to the map, with an initially empty set of edges.
     */
    default boolean addKey(K key) {
        return put(key, new HostEdgeSet()) == null;
    }

    /**
     * Removes a given edge from the edges stored for a given key,
     * and returns the resulting (remaining) set of edges.
     * @param key the key for which the edge is to be removed; non-{@code null}
     * @param edge the edge to be removed; non-{@code null}
     * @param refresh if {@code true}, the edge set for {@code key} is cloned
     * @return the resulting edge set for {@code key}, or {@code null} if
     * the key is not in the store
     */
    default @Nullable HostEdgeSet removeEdge(K key, HostEdge edge, boolean refresh) {
        @Nullable
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
    default HostEdgeSet addEdge(K key, HostEdge edge, boolean refresh) {
        @Nullable
        HostEdgeSet result = get(key);
        if (refresh || result == null) {
            put(key, result = HostEdgeSet.newInstance(result));
        }
        result.add(edge);
        return result;
    }
}
