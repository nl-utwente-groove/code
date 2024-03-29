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
package nl.utwente.groove.gui.action;

import javax.swing.Action;

import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.gui.Icons;
import nl.utwente.groove.gui.Options;
import nl.utwente.groove.gui.Simulator;

/** Action to start a new simulation. */
public class StartSimulationAction extends SimulatorAction {
    /** Constructs an instance of the action. */
    public StartSimulationAction(Simulator simulator) {
        super(simulator, Options.START_SIMULATION_ACTION_NAME,
            Icons.GO_STOP_ICON);
        putValue(Action.ACCELERATOR_KEY, Options.START_SIMULATION_KEY);
    }

    @Override
    public void execute() {
        getSimulatorModel().resetGTS();
    }

    @Override
    public void refresh() {
        GrammarModel grammar = getSimulatorModel().getGrammar();
        setEnabled(grammar != null && !grammar.hasErrors()
            && grammar.hasRules());
    }
}