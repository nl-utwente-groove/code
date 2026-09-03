/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2026 University of Twente
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
package nl.utwente.groove.gui.view;

import java.util.Collection;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.util.AIGenerated;

/**
 * Listener for the events of a {@link GraphCanvas}. All methods have empty defaults,
 * so implementors override only what they need.
 * @param <G> the type of graph displayed
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
public interface GraphCanvasListener<G extends Graph> {
    /** Signals that the content model of the canvas was replaced. */
    default void viewModelChanged(GraphCanvas<G> canvas, @Nullable GraphViewModel<G> oldModel,
                                  @Nullable GraphViewModel<G> newModel) {
        // empty
    }

    /** Signals that the visuals or structure of a set of cells changed. */
    default void cellsChanged(GraphCanvas<G> canvas, Collection<? extends ViewCell<G>> cells) {
        // empty
    }

    /** Signals that the selection changed. */
    default void selectionChanged(GraphCanvas<G> canvas) {
        // empty
    }

    /** Signals that the interaction mode changed. */
    default void modeChanged(GraphCanvas<G> canvas, GraphViewMode oldMode,
                             GraphViewMode newMode) {
        // empty
    }

    /** Signals that in-place editing of a cell started. */
    default void editingStarted(GraphCanvas<G> canvas, ViewCell<G> cell) {
        // empty
    }
}
