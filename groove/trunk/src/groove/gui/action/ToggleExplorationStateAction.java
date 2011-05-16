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

import groove.abstraction.Multiplicity;
import groove.gui.Options;
import groove.gui.Simulator;

import javax.swing.Action;

/** Action to switch between concrete and abstract state space exploration. */
public class ToggleExplorationStateAction extends SimulatorAction {
    /** Constructs an instance of the action. */
    public ToggleExplorationStateAction(Simulator simulator) {
        super(simulator, Options.TOGGLE_TO_ABS_ACTION_NAME, null);
        putValue(Action.ACCELERATOR_KEY, Options.TOGGLE_EXP_MODE_KEY);
    }

    @Override
    protected boolean doAction() {
        boolean result = false;
        Simulator simulator = getSimulator();
        if (getModel().isAbstractionMode()) {
            getModel().setAbstractionMode(false);
            this.putValue(Action.NAME, Options.TOGGLE_TO_ABS_ACTION_NAME);
            result = true;
        } else {
            getModel().setAbstractionMode(true);
            this.putValue(Action.NAME, Options.TOGGLE_TO_CONC_ACTION_NAME);
            Multiplicity.initMultStore();
            simulator.resetStatePanel();
            simulator.resetRuleTree();
            result = true;
        }
        return result;
    }

    @Override
    public void refresh() {
        boolean enabled =
            getModel().getGrammar() != null
                && getModel().getGrammar().getErrors().isEmpty();
        setEnabled(enabled);
    }
}