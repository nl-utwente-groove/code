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
package nl.utwente.groove.explore.config;

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.util.AIGenerated;

/**
 * Baseline traversal realising the combination of the next-state, successor
 * and frontier features of an exploration configuration. The traversal is
 * derived once, by {@link ExploreTypeConverter} (which rejects unrealisable
 * combinations), and handed to the resulting {@link ConfiguredExploreType},
 * so that the realisability checks and the strategy instantiation dispatch
 * on the same value and cannot diverge.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
@AIGenerated("Claude Fable 5, 2026-08")
public enum Traversal {
    /** Linear walk along a single-state frontier. */
    LINEAR,
    /** Breadth-first search: oldest-first frontier. */
    BFS,
    /** Depth-first search: newest-first frontier. */
    DFS,
    /** Random-order frontier search. */
    RANDOM,
    /** Beam search: capacity-restricted frontier. */
    BEAM;

    /** Indicates if this traversal is a plain search (breadth-first or
     * depth-first), as required by the depth and condition bounds. */
    public boolean isSearch() {
        return this == BFS || this == DFS;
    }
}
