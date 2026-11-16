/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2026
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

import nl.utwente.groove.control.graph.ControlGraph;
import nl.utwente.groove.gui.view.CtrlGraphCanvas;
import nl.utwente.groove.gui.view.CtrlGraphViewController;
import nl.utwente.groove.gui.view.CtrlGraphViewModel;
import nl.utwente.groove.util.AIGenerated;

/**
 * The yFiles canvas for control automata.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
public final class YFilesCtrlCanvas extends YFilesCanvas<ControlGraph> implements CtrlGraphCanvas {
    /** Creates a canvas as the canvas of a given controller. */
    public YFilesCtrlCanvas(CtrlGraphViewController controller) {
        super(controller);
        setToolTipEnabled(true);
    }

    @Override
    public CtrlGraphViewController getController() {
        return (CtrlGraphViewController) super.getController();
    }

    @Override
    protected CtrlGraphViewModel createViewModel(YFilesCellStore<ControlGraph> store) {
        return new CtrlGraphViewModel(getController(), store);
    }
}
