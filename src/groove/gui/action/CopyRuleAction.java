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
import groove.trans.ResourceKind;
import groove.view.aspect.AspectGraph;

import java.io.IOException;

/**
 * Action that takes care of copying a rule in a grammar view.
 * @author Arend Rensink
 * @version $Revision $
 */
public class CopyRuleAction extends SimulatorAction {
    /** Constructs a new action for a given simulator. */
    public CopyRuleAction(Simulator simulator) {
        super(simulator, Options.COPY_RULE_ACTION_NAME, Icons.COPY_ICON);
        putValue(ACCELERATOR_KEY, Options.COPY_KEY);
    }

    @Override
    public void refresh() {
        setEnabled(getSimulatorModel().getStore() != null
            && getSimulatorModel().getStore().isModifiable()
            && getSimulatorModel().getRuleSet().size() == 1);
    }

    @Override
    public boolean execute() {
        boolean result = false;
        // Multiple selection
        if (confirmAbandon()) {
            AspectGraph rule = getSimulatorModel().getRule().getSource();
            String oldRuleName = rule.getName();
            String newRuleName =
                askNewName(ResourceKind.RULE, "Select new rule name",
                    oldRuleName, true);
            if (newRuleName != null) {
                AspectGraph newRuleGraph = rule.rename(newRuleName);
                try {
                    result |= getSimulatorModel().doAddRule(newRuleGraph);
                } catch (IOException exc) {
                    showErrorDialog(exc, String.format(
                        "Error while copying rule '%s' to '%s'", oldRuleName,
                        newRuleName));
                }
            }
        }
        return result;
    }
}