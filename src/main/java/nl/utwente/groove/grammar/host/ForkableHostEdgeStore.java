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
 */
package nl.utwente.groove.grammar.host;

import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.collect.ForkableHashMap;

/**
 * {@link HostEdgeStore} over a {@link ForkableHashMap}: a shallow copy forks the map,
 * which costs time proportional to the square root of the number of keys rather than to
 * the number itself, and the copies share their buckets until either writes to them.
 * This is the store of graphs that get a copy of their parent's data (copy mode).
 * Iteration is in bucket order, see {@link ForkableHashMap}.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
public final class ForkableHostEdgeStore<K> extends ForkableHashMap<K,HostEdgeSet>
    implements HostEdgeStore<K> {
    /** Returns a fresh empty store. */
    public ForkableHostEdgeStore() {
        // empty
    }

    /** Forks a given store, and re-puts cloned image sets if a deep copy is asked for.
     * @param original the store to be copied
     * @param deepCopy if {@code true}, the image sets are also copied
     */
    private ForkableHostEdgeStore(ForkableHostEdgeStore<K> original, boolean deepCopy) {
        super(original);
        if (deepCopy) {
            for (Map.Entry<K,HostEdgeSet> entry : original.entrySet()) {
                put(entry.getKey(), HostEdgeSet.newInstance(entry.getValue()));
            }
        }
    }

    @Override
    public HostEdgeStore<K> copy(boolean deepCopy) {
        return new ForkableHostEdgeStore<>(this, deepCopy);
    }
}
