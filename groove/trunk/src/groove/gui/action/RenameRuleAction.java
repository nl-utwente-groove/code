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

/**
 * Action that takes care of renaming a host graph in a grammar view.
 * @author Arend Rensink
 * @version $Revision $
 */
public class RenameRuleAction extends SimulatorAction {
    /** Constructs an instance of the action, for a given simulator. */
    public RenameRuleAction(Simulator simulator) {
        super(simulator, Options.RENAME_RULE_ACTION_NAME, Icons.RENAME_ICON);
        putValue(ACCELERATOR_KEY, Options.RENAME_KEY);
    }

    /** This action is disabled if there is more than one selected rule. */
    @Override
    public boolean isEnabled() {
        if (getModel().getRuleSet().size() != 1) {
            return false;
        } else {
            return super.isEnabled();
        }
    }

    @Override
    public void refresh() {
        setEnabled(getModel().getRule() != null);
        if (getSimulator().getGraphPanel() == getSimulator().getRulePanel()) {
            getSimulator().getRenameMenuItem().setAction(this);
        }
    }

    @Override
    protected boolean doAction() {
        boolean result = false;
        // first collect the rule graphs involved
        AspectGraph[] ruleGraphs =
            new AspectGraph[getModel().getRuleSet().size()];
        int i = 0;
        for (RuleView ruleView : getModel().getRuleSet()) {
            ruleGraphs[i] = ruleView.getAspectGraph();
            i++;
        }
        if (confirmAbandon() && getSimulator().disposeEditors(ruleGraphs)) {
            // Multiple selection
            String newName = null;
            // copy the selected rules to avoid concurrent modifications
            for (AspectGraph ruleGraph : ruleGraphs) {
                String oldName = ruleGraph.getName();
                newName = askNewRuleName("Select new rule name", oldName, true);
                if (newName != null) {
                    try {
                        result |=
                            getSimulator().getModel().doRenameRule(ruleGraph,
                                newName);
                    } catch (IOException exc) {
                        showErrorDialog(String.format(
                            "Error while renaming rule '%s' into '%s'",
                            oldName, newName), exc);
                    }
                }
            }
            if (newName != null) {
                getModel().setRule(newName);
            }
        }
        return result;
    }
}