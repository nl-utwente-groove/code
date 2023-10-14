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
package nl.utwente.groove.gui.action;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

import nl.utwente.groove.explore.util.LTSLabels;
import nl.utwente.groove.explore.util.LTSReporter;
import nl.utwente.groove.explore.util.StateReporter;
import nl.utwente.groove.gui.Icons;
import nl.utwente.groove.gui.Options;
import nl.utwente.groove.gui.Simulator;
import nl.utwente.groove.gui.dialog.SaveLTSAsDialog;
import nl.utwente.groove.gui.dialog.SaveLTSAsDialog.StateExport;
import nl.utwente.groove.lts.Filter;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.lts.GraphState;

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
            doSave(dialog.getDirectory(),
                dialog.getLtsPattern(),
                dialog.getStatePattern(),
                dialog.getExportStates(),
                dialog.getLTSLabels());
        }
    }

    private void doSave(String dir, String ltsPattern, String statePattern,
        StateExport exportStates, LTSLabels flags) {
        GTS gts = getSimulatorModel().getGTS();

        Iterable<? extends GraphState> export = new HashSet<>(0);
        switch (exportStates) {
        case ALL:
            export = gts.nodeSet();
            break;
        case TOP:
            export = gts.getStates();
            break;
        case FINAL:
            export = gts.getFinalStates();
            break;
        case RESULT:
            export = getSimulatorModel().getExploreResult();
            break;
        default:
            assert exportStates == StateExport.NONE;
        }

        Filter filter = getLtsDisplay().getFilter();

        try {
            LTSReporter.exportLTS(gts,
                new File(dir, ltsPattern).toString(),
                flags,
                filter,
                getSimulatorModel().getExploreResult());
            for (GraphState state : export) {
                StateReporter.exportState(state, new File(dir, statePattern).toString());
            }
        } catch (IOException e) {
            showErrorDialog(e, "Error while saving LTS to %s", dir);
        }
    }

    @Override
    public void refresh() {
        setEnabled(getSimulatorModel().getGTS() != null);
    }
}