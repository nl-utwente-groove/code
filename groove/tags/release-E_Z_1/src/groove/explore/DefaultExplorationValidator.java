/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
package groove.explore;

import groove.gui.SimulationAdapter;
import groove.gui.Simulator;
import groove.view.StoredGrammarView;

/**
 * Wrapper class that handles the update of the default exploration
 * (which is stored in the simulator) when the grammar changes.
 *
 * @author Maarten de Mol
 * @version $Revision $
 */
public class DefaultExplorationValidator extends SimulationAdapter {
    private final Simulator simulator; // reference to the simulator 

    /**
     * Stores reference to the simulator, and sets itself up as a listener.
     * @simulator - reference to the simulator
     */
    public DefaultExplorationValidator(Simulator simulator) {
        this.simulator = simulator;
        this.simulator.addSimulationListener(this);
    }

    /**
     * Checks whether the default exploration is still valid when the grammar
     * changes. If it is no longer valid, it is reset to the default constructor
     * of the Exploration class.
     */
    @Override
    public void setGrammarUpdate(StoredGrammarView grammar) {
        /*
        Exploration defExpl = this.simulator.getDefaultExploration();
        if (defExpl != null) {
            Acceptor acceptor = defExpl.getAcceptor().getObject();
            if (acceptor instanceof ConditionalRuleAcceptor) {
                Boolean success =
                    ((ConditionalRuleAcceptor) acceptor).renewCondition(grammar);
                if (!success) {
                    this.simulator.setDefaultExploration(new Exploration());
                }
            }
        }
        */
    }
}
