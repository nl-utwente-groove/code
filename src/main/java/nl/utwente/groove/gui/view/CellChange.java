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

import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.util.AIGenerated;

/**
 * A change in the cells of a {@link GraphCanvas}, as reported to
 * {@link GraphCanvasListener#cellsChanged(GraphCanvas, CellChange)}.
 * The three parts are disjoint: a cell that was inserted or removed is not
 * also listed as modified.
 * @param inserted the cells added to the canvas
 * @param modified the existing cells whose visuals or connections changed
 * @param removed the cells removed from the canvas
 * @param <G> the type of graph displayed
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
public record CellChange<G extends Graph>(List<ViewCell<G>> inserted, List<ViewCell<G>> modified,
    List<ViewCell<G>> removed) {
    /** Indicates if the change inserted or removed cells, rather than only modifying them. */
    public boolean isStructural() {
        return !inserted().isEmpty() || !removed().isEmpty();
    }
}
