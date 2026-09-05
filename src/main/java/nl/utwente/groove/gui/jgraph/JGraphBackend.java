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
package nl.utwente.groove.gui.jgraph;

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.gui.view.AspectGraphCanvas;
import nl.utwente.groove.gui.view.AspectGraphViewController;
import nl.utwente.groove.gui.view.CtrlGraphCanvas;
import nl.utwente.groove.gui.view.CtrlGraphViewController;
import nl.utwente.groove.gui.view.GraphBackend;
import nl.utwente.groove.gui.view.GraphCanvas;
import nl.utwente.groove.gui.view.LTSGraphCanvas;
import nl.utwente.groove.gui.view.LTSGraphViewController;
import nl.utwente.groove.gui.view.PlainGraphViewController;
import nl.utwente.groove.util.AIGenerated;

/**
 * The JGraph backend: creates {@link JGraph} canvases.
 * Instantiated reflectively by {@link GraphBackend#instance()}.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
public class JGraphBackend implements GraphBackend {
    /** Public no-argument constructor, for reflective instantiation. */
    public JGraphBackend() {
        // empty
    }

    @Override
    public AspectGraphCanvas newAspectCanvas(AspectGraphViewController controller) {
        return new AspectJGraph(controller);
    }

    @Override
    public LTSGraphCanvas newLTSCanvas(LTSGraphViewController controller) {
        return new LTSJGraph(controller);
    }

    @Override
    public CtrlGraphCanvas newCtrlCanvas(CtrlGraphViewController controller) {
        return new CtrlJGraph(controller);
    }

    @Override
    public GraphCanvas<Graph> newPlainCanvas(PlainGraphViewController controller) {
        return new PlainJGraph(controller);
    }
}
