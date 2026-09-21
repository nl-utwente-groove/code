/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.gui.display;

import java.util.Set;

import javax.swing.JMenu;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.gui.Simulator;
import nl.utwente.groove.gui.menu.ModelCheckingMenu;
import nl.utwente.groove.gui.menu.MyJMenu;
import nl.utwente.groove.lts.ExploreResult;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.lts.GraphTransition;
import nl.utwente.groove.util.AIGenerated;

/**
 * The simulator as context of the graph view showing the LTS.
 * Adds the exploration and traversal actions, and the exploration result
 * and trace that the LTS view shows.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Opus 5, 2026-09")
@NonNullByDefault
public class SimulatorLTSContext extends SimulatorViewContext<GTS> {
    /** Constructs a context for the LTS view of a given simulator. */
    public SimulatorLTSContext(Simulator simulator) {
        super(simulator);
    }

    @Override
    public JMenu getExportItems() {
        JMenu result = new JMenu();
        var actions = getActions();
        result.add(actions.getSaveLTSAsAction());
        result.add(actions.getSaveStateAction());
        return result;
    }

    @Override
    public JMenu getExploreItems() {
        MyJMenu result = new MyJMenu();
        var actions = getActions();
        result.add(actions.getExplorationDialogAction());
        result.add(actions.getApplyMatchAction());
        result.add(actions.getExploreAction());
        result.addSeparator();
        result.add(getCheckerMenu());
        return result;
    }

    @Override
    public JMenu getGotoItems() {
        JMenu result = new JMenu();
        var actions = getActions();
        result.add(actions.getGotoStartStateAction());
        result.add(actions.getGotoFinalStateAction());
        return result;
    }

    /** Lazily creates and returns the model-checking menu. */
    private JMenu getCheckerMenu() {
        var result = this.checkerMenu;
        if (result == null) {
            this.checkerMenu = result = new ModelCheckingMenu(getSimulator());
        }
        return result;
    }

    /** The lazily created model-checking menu. */
    private @Nullable JMenu checkerMenu;

    @Override
    public @Nullable ExploreResult getExploreResult() {
        return getSimulatorModel().getExploreResult();
    }

    @Override
    public void setTrace(Set<GraphTransition> trace) {
        getSimulatorModel().setTrace(trace);
    }
}
