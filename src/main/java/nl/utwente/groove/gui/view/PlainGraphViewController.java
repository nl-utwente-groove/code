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

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.gui.Simulator;
import nl.utwente.groove.util.AIGenerated;

/**
 * Display controller for graph views showing plain graphs of no particular role,
 * as in the graph preview dialog.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
public class PlainGraphViewController extends GraphViewController<Graph> {
    /**
     * Constructs a controller.
     * @param simulator simulator to which the display belongs; may be {@code null}
     */
    public PlainGraphViewController(@Nullable Simulator simulator) {
        super(simulator);
    }

    @Override
    protected GraphCanvas<Graph> createCanvas(GraphBackend backend) {
        return backend.newPlainCanvas(this);
    }
}
