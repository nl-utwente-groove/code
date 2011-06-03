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

import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.view.RuleModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Action that takes care of deleting a host graph in a grammar view.
 * @author Arend Rensink
 * @version $Revision $
 */
public class DeleteRuleAction extends SimulatorAction {
    /** Constructs a new action for a given simulator. */
    public DeleteRuleAction(Simulator simulator) {
        super(simulator, Options.DELETE_RULE_ACTION_NAME, Icons.DELETE_ICON);
        putValue(ACCELERATOR_KEY, Options.DELETE_KEY);
        simulator.addAccelerator(this);
    }

    @Override
    public void refresh() {
        setEnabled(getSimulatorModel().getRule() != null
            && getSimulatorModel().getStore().isModifiable());
    }

    @Override
    public boolean execute() {
        boolean result = false;
        // copy the selected rules to avoid concurrent modifications
        Collection<RuleModel> ruleViews = getSimulatorModel().getRuleSet();
        // collect the affected graphs and compose the question
        List<String> ruleNames = new ArrayList<String>(ruleViews.size());
        for (RuleModel ruleView : ruleViews) {
            ruleNames.add(ruleView.getName());
        }
        String question;
        if (ruleNames.size() == 1) {
            question = String.format("Delete rule '%s'?", ruleNames.get(0));
        } else {
            question =
                String.format("Delete these %s rules?", ruleNames.size());
        }
        if (confirmBehaviour(Options.DELETE_RULE_OPTION, question)
            && getRuleDisplay().disposeEditors(ruleNames.toArray(new String[0]))) {
            try {
                result |= getSimulatorModel().doDeleteRules(ruleNames);
            } catch (IOException exc) {
                showErrorDialog(exc, "Error during rule deletion");
            }
        }
        return result;
    }
}