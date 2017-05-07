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

import static groove.grammar.model.ResourceKind.RULE;

import groove.grammar.QualName;
import groove.gui.Options;
import groove.gui.Simulator;

/**
 * Action for editing the current state or rule.
 */
public class EditRulePropertiesAction extends SimulatorAction {
    /** Constructs an instance of the action for a given simulator. */
    public EditRulePropertiesAction(Simulator simulator) {
        super(simulator, Options.RULE_PROPERTIES_ACTION_NAME, null);
        refresh();
    }

    @Override
    public void refresh() {
        setEnabled(getSimulatorModel().getStore() != null && getSimulatorModel().getSelectSet(RULE)
            .size() == 1);
    }

    @Override
    public void execute() {
        QualName ruleName = getSimulatorModel().getSelected(RULE);
        getRuleDisplay().setInfoTabIndex(true, 1);
        getRuleDisplay().startEditResource(ruleName);
    }
}