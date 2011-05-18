/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
package groove.gui.action;

import groove.graph.DefaultGraph;
import groove.gui.Simulator;
import groove.gui.dialog.SaveLTSAsDialog;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.util.Groove;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

/**
 * Action that takes care of savng the LTS graph under a certain name.
 * @author Arend Rensink
 * @version $Revision $
 */
public class SaveLTSAsAction extends SimulatorAction {
    /** Constructs an instance of the action, for a given simulator. */
    public SaveLTSAsAction(Simulator simulator) {
        super(simulator, "Save LTS As...", null);
    }

    @Override
    public boolean execute() {
        SaveLTSAsDialog dialog = new SaveLTSAsDialog(getSimulator());
        if (getLastGrammarFile() != null) {
            dialog.setCurrentDirectory(getLastGrammarFile().getAbsolutePath());
        }

        if (dialog.showDialog(getSimulator())) {

            File file = new File(dialog.getDirectory());
            int exportStates = dialog.getExportStates();
            boolean showFinal = dialog.showFinal();
            boolean showNames = dialog.showNames();
            boolean showStart = dialog.showStart();
            boolean showOpen = dialog.showOpen();

            GTS gts = getModel().getGts();

            DefaultGraph lts =
                gts.toPlainGraph(showFinal, showStart, showOpen, showNames);

            Collection<GraphState> export = new HashSet<GraphState>(0);

            if (exportStates == SaveLTSAsDialog.STATES_ALL) {
                export = gts.getStateSet();
            } else if (exportStates == SaveLTSAsDialog.STATES_FINAL) {
                export = gts.getFinalStates();
            }

            try {
                Groove.saveGraph(lts,
                    new File(file, "lts.gxl").getAbsolutePath());
                for (GraphState state : export) {
                    String name = state.toString();
                    Groove.saveGraph(
                        state.getGraph().toAspectMap().getAspectGraph().toPlainGraph(),
                        new File(file, name + ".gst").getAbsolutePath());
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public void refresh() {
        setEnabled(getModel().getGts() != null);
    }
}