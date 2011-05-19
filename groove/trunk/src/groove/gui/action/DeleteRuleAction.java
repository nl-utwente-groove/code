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
import groove.view.RuleView;
import groove.view.aspect.AspectGraph;

import java.io.IOException;
import java.util.ArrayList;
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
        setEnabled(getModel().getRule() != null
            && getModel().getStore().isModifiable());
    }

    @Override
    public boolean execute() {
        boolean result = false;
        // Multiple selection
        String question = "Delete rule(s) '%s'";
        // copy the selected rules to avoid concurrent modifications
        List<RuleView> ruleViews =
            new ArrayList<RuleView>(getModel().getRuleSet());
        // collect the affected graphs and compose the question
        AspectGraph[] rules = new AspectGraph[ruleViews.size()];
        for (int i = 0; i < ruleViews.size(); i++) {
            rules[i] = ruleViews.get(i).getAspectGraph();
            String ruleName = ruleViews.get(i).getName();
            question = String.format(question, ruleName);
            if (i < ruleViews.size() - 1) {
                question = question + ", '%s'";
            } else {
                question = question + "?";
            }
        }
        if (confirmBehaviour(Options.DELETE_RULE_OPTION, question)
            && getPanel().disposeEditors(rules)) {
            for (RuleView rule : ruleViews) {
                try {
                    result |= getModel().doDeleteRule(rule.getName());
                } catch (IOException exc) {
                    showErrorDialog(
                        exc,
                        String.format("Error while deleting rule '%s'",
                            rule.getName()));
                }
            }
        }
        return result;
    }
}