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
package nl.utwente.groove.gui.tree;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.graph.Label;
import nl.utwente.groove.gui.jgraph.LTSJGraph;
import nl.utwente.groove.lts.GTS;

/**
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
class LTSFilter extends LabelFilter<GTS,LTSEntry> {
    LTSFilter(LTSJGraph jGraph) {
        this.jGraph = jGraph;
    }

    private final LTSJGraph jGraph;

    /** Lazily creates and returns a filter entry based on a given label key. */
    @Override
    protected LTSEntry getEntry(Label key) {
        LTSEntry result = this.labelEntryMap.get(key);
        if (result == null) {
            result = new LTSEntry(key, () -> this.jGraph.isShowSystemProperties());
            // normalise the new entry
            var normal = this.normalMap.get(result);
            if (normal != null) {
                result = normal;
            } else {
                this.normalMap.put(result, result);
                registerEntry(result);
            }
            this.labelEntryMap.put(key, result);
        }
        return result;
    }

    /** Mapping from known labels to corresponding label entries. */
    private final Map<Label,@Nullable LTSEntry> labelEntryMap = new HashMap<>();
    /** Identity mapping for normalised entries. */
    private final Map<LTSEntry,@Nullable LTSEntry> normalMap = new HashMap<>();

    /**
     * Clears the entire filter.
     */
    @Override
    public void clear() {
        super.clear();
        this.labelEntryMap.clear();
        this.normalMap.clear();
    }
}
