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

import org.eclipse.jdt.annotation.NonNull;
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
 * The canvases of all roles are on yFiles, the editor's aspect canvas included; a
 * GROOVE launch with this unit on the class path never touches JGraph.
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
        return controller.isEditing()
            ? new YFilesAspectEditorCanvas(controller)
            : new YFilesAspectCanvas(controller);
    }

    @Override
    public LTSGraphCanvas newLTSCanvas(LTSGraphViewController controller) {
        return new YFilesLTSCanvas(controller);
    }

    @Override
    public CtrlGraphCanvas newCtrlCanvas(CtrlGraphViewController controller) {
        return new YFilesCtrlCanvas(controller);
    }

    @Override
    public GraphCanvas<Graph> newPlainCanvas(PlainGraphViewController controller) {
        return new YFilesPlainCanvas(controller);
    }

    @Override
    public String getDisplayName() {
        return "yFiles";
    }

    @Override
    public String getName() {
        return YFILES;
    }

    /**
     * Returns the short form of the restrictions that a distribution of GROOVE with
     * yFiles comes with, which are stated in full in the file
     * {@code release/yfiles/include/LICENSE-YFILES.txt} that such a distribution carries.
     * A change to the restrictions has to reach this text as well.
     */
    @AIGenerated("Claude Fable 5.2, 2026-11")
    @Override
    public @NonNull String getLicenseNotice() {
        return LICENSE_NOTICE;
    }

    /** The notice returned by {@link #getLicenseNotice()}. */
    private static final String LICENSE_NOTICE
        = "This distribution of GROOVE contains the commercial library yFiles for Java (Swing)"
            + " by yWorks GmbH. It may be used for academic, non-commercial purposes only;"
            + " the restrictions are stated in the file LICENSE-YFILES.txt that comes with it.";
}
