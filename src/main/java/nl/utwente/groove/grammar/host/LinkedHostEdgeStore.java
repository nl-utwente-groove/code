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

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.util.AIGenerated;

/**
 * Insertion-ordered {@link HostEdgeStore}, copied entry by entry.
 * This is the store of graphs that hand their data down to their children
 * (swing mode), where copies are rare.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
public final class LinkedHostEdgeStore<K> extends LinkedHashMap<K,HostEdgeSet>
    implements HostEdgeStore<K> {
    /** Returns a fresh empty store. */
    public LinkedHostEdgeStore() {
        // empty
    }

    /** Copies a given store.
     * @param original the store to be copied
     * @param deepCopy if {@code true}, the image sets are also copied
     */
    private LinkedHostEdgeStore(HostEdgeStore<K> original, boolean deepCopy) {
        for (Map.Entry<K,HostEdgeSet> entry : original.entrySet()) {
            HostEdgeSet image = entry.getValue();
            put(entry.getKey(), deepCopy
                ? HostEdgeSet.newInstance(image)
                : image);
        }
    }

    @Override
    public HostEdgeStore<K> copy(boolean deepCopy) {
        return new LinkedHostEdgeStore<>(this, deepCopy);
    }
}
