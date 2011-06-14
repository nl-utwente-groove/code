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
package groove.gui;

import groove.gui.SimulatorModel.Change;
import groove.trans.ResourceKind;

import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JToolBar;

/**
 * Panel that holds the rule panel and rule graph editors.
 * @author Arend Rensink
 * @version $Revision $
 */
final public class RuleDisplay extends ResourceDisplay {
    /**
     * Constructs a panel for a given simulator.
     */
    public RuleDisplay(Simulator simulator) {
        super(simulator, ResourceKind.RULE);
        installListeners();
    }

    @Override
    protected void installListeners() {
        getSimulatorModel().addListener(this, Change.ABSTRACT);
        super.installListeners();
    }

    /** Creates a tool bar for the rule tree. */
    @Override
    protected JToolBar createListToolBar() {
        JToolBar result = super.createListToolBar(7);
        result.add(getActions().getShiftPriorityAction(true));
        result.add(getActions().getShiftPriorityAction(false));
        return result;
    }

    /**
     * Returns the tree of rules and matches displayed in the simulator.
     */
    @Override
    public JComponent createList() {
        return new RuleJTree(this);
    }

    @Override
    protected void resetList() {
        ((RuleJTree) getList()).dispose();
        super.resetList();
    }

    @Override
    public void update(SimulatorModel source, SimulatorModel oldModel,
            Set<Change> changes) {
        super.update(source, oldModel, changes);
        if (suspendListening()) {
            if (changes.contains(Change.ABSTRACT) && source.isAbstractionMode()) {
                resetList();
            }
            activateListening();
        }
    }

    @Override
    protected void decorateLabelText(String name, StringBuilder text) {
        if (!getResource(name).isEnabled()) {
            text.insert(0, "(");
            text.append(")");
        }
    }
}
