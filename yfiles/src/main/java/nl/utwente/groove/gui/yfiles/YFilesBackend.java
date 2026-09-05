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
package nl.utwente.groove.gui.yfiles;

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
 * The yFiles backend: creates canvases on yFiles for Java (Swing).
 * Discovered as a {@link GraphBackend} service; ranked before the JGraph backend
 * whenever this unit is on the class path.
 * <p>
 * The canvases are the next slice of the migration; until they exist, every
 * factory method refuses. This unit is therefore not on the class path of any
 * GROOVE launch yet.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
public class YFilesBackend implements GraphBackend {
    /** Public no-argument constructor, for the service loader. */
    public YFilesBackend() {
        // empty
    }

    @Override
    public AspectGraphCanvas newAspectCanvas(AspectGraphViewController controller) {
        throw notYet();
    }

    @Override
    public LTSGraphCanvas newLTSCanvas(LTSGraphViewController controller) {
        throw notYet();
    }

    @Override
    public CtrlGraphCanvas newCtrlCanvas(CtrlGraphViewController controller) {
        throw notYet();
    }

    @Override
    public GraphCanvas<Graph> newPlainCanvas(PlainGraphViewController controller) {
        throw notYet();
    }

    @Override
    public String getName() {
        return YFILES;
    }

    private static UnsupportedOperationException notYet() {
        return new UnsupportedOperationException("The yFiles canvases are not implemented yet");
    }
}
