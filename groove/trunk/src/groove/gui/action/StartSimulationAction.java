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

import static groove.gui.Options.START_SIMULATION_OPTION;
import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;

import javax.swing.Action;

/** Action to start a new simulation. */
public class StartSimulationAction extends SimulatorAction {
    /** Constructs an instance of the action. */
    public StartSimulationAction(Simulator simulator) {
        super(simulator, Options.START_SIMULATION_ACTION_NAME,
            Icons.GO_START_ICON);
        putValue(Action.ACCELERATOR_KEY, Options.START_SIMULATION_KEY);
    }

    @Override
    public boolean execute() {
        boolean result = false;
        if (confirmStopSimulation() && confirmBehaviourOption(START_SIMULATION_OPTION)) {
            result = getSimulatorModel().setGts();
        }
        return result;
    }

    @Override
    public void refresh() {
        boolean enabled =
            getSimulatorModel().getGrammar() != null
                && getSimulatorModel().getGrammar().getErrors().isEmpty();
        setEnabled(enabled);
    }
}