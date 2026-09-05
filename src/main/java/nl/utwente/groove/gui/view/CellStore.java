/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
 * University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * $Id$
 */
package nl.utwente.groove.gui.view;

import java.util.Collection;
import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.util.AIGenerated;

/**
 * The backend's store of the cells of a {@link GraphViewModel}: it commits
 * structural changes computed by the view model, creating its own items for the
 * cells, and holds the authoritative, z-ordered collection of cells.
 * The cells themselves are created by the view model.
 * @param <G> the type of graph displayed
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
public interface CellStore<G extends Graph> {
    /**
     * Commits a structural change as one edit: inserts the given vertices and edges
     * (connected as described) and, if so requested, removes all cells that were there before.
     */
    void insertCells(List<? extends ViewVertex<G>> vertices, List<? extends ViewEdge<G>> edges,
                     List<Connection<G>> connections, boolean replace);

    /** Returns all cells currently in the store, in z-order. */
    Collection<? extends ViewCell<G>> getCells();

    /** A pending connection of a fresh edge cell to its end vertices. */
    record Connection<G extends Graph>(ViewEdge<G> edge, ViewVertex<G> source,
        ViewVertex<G> target) {
        // empty
    }
}
