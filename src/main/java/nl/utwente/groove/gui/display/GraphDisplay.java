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
package nl.utwente.groove.gui.display;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.gui.view.GraphCanvas;
import nl.utwente.groove.gui.view.GraphViewController;
import nl.utwente.groove.gui.view.GraphViewModel;
import nl.utwente.groove.util.AIGenerated;

/**
 * A GUI component that shows graphs of one role on a canvas: the state and LTS
 * displays, and the graph and graph editor tabs. Gives uniform access to the
 * graph view's controller, which owns the canvas, and to the panel the canvas
 * is shown on; implementors specialise the return types to their role.
 * @param <G> the type of graphs shown
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
public interface GraphDisplay<G extends Graph> {
    /** Returns the controller of the graph view, created on first request. */
    GraphViewController<G> getController();

    /** Returns the canvas of the graph view, created by the controller on first request. */
    default GraphCanvas<G> getCanvas() {
        return getController().getCanvas();
    }

    /**
     * Returns the view model currently shown on the canvas, if any.
     * For an editor in preview mode this is the preview clone, not the edit model.
     */
    default @Nullable GraphViewModel<G> getViewModel() {
        return getCanvas().getViewModel();
    }

    /** Returns the panel on which the canvas is shown. */
    GraphPanel<G> getGraphPanel();
}
