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

import groove.grammar.aspect.AspectGraph;
import groove.grammar.aspect.GraphConverter;
import groove.graph.GraphRole;
import groove.graph.plain.PlainGraph;
import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.gui.dialog.SaveLTSAsDialog;
import groove.io.ExtensionFilter;
import groove.io.FileType;
import groove.io.graph.GxlIO;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.util.Groove;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

/**
 * Action that takes care of saving the LTS graph under a certain name.
 * @author Arend Rensink
 * @version $Revision $
 */
public class SaveLTSAsAction extends SimulatorAction {
    /** Constructs an instance of the action, for a given simulator. */
    public SaveLTSAsAction(Simulator simulator) {
        super(simulator, Options.SAVE_LTS_ACTION_NAME, Icons.SAVE_AS_ICON);
    }

    @Override
    public void execute() {
        SaveLTSAsDialog dialog = new SaveLTSAsDialog(getSimulator());
        if (getLastGrammarFile() != null) {
            dialog.setCurrentDirectory(getLastGrammarFile().getAbsolutePath());
        }
        if (dialog.showDialog(getSimulator())) {
            File ltsFile = dialog.getFile();
            doSave(ltsFile, dialog.getExportStates(), dialog.showFinal(),
                dialog.showNames(), dialog.showStart(), dialog.showOpen());
        }
    }

    private void doSave(File ltsFile, int exportStates, boolean showFinal,
            boolean showNames, boolean showStart, boolean showOpen) {
        ExtensionFilter ltsFilter = FileType.getFilter(GraphRole.LTS);
        if (ltsFile.isDirectory()) {
            ltsFile = new File(ltsFile, ltsFilter.addExtension("lts"));
        }
        GTS gts = getSimulatorModel().getGts();
        gts.setName(ltsFilter.stripExtension(ltsFile.getName()));
        PlainGraph lts =
            gts.toPlainGraph(showFinal, showStart, showOpen, showNames);

        Collection<? extends GraphState> export = new HashSet<GraphState>(0);

        if (exportStates == SaveLTSAsDialog.STATES_ALL) {
            export = gts.nodeSet();
        } else if (exportStates == SaveLTSAsDialog.STATES_FINAL) {
            export = gts.getFinalStates();
        }

        try {
            Groove.saveGraph(lts, ltsFile);
            ExtensionFilter stateFilter = FileType.STATE_FILTER;
            for (GraphState state : export) {
                AspectGraph stateGraph =
                    GraphConverter.toAspect(state.getGraph());
                File file =
                    new File(ltsFile.getParentFile(),
                        stateFilter.addExtension(state.toString()));
                GxlIO.getInstance().saveGraph(stateGraph.toPlainGraph(), file);
            }
        } catch (IOException e) {
            showErrorDialog(e, "Error while saving LTS to %s", ltsFile);
        }
    }

    @Override
    public void refresh() {
        setEnabled(getSimulatorModel().getGts() != null);
    }
}