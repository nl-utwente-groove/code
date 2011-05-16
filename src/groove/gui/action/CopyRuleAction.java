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
        setEnabled(getModel().getRule() != null
            && getModel().getStore().isModifiable());

        if (getSimulator().getGraphPanel() == getSimulator().getRulePanel()) {
            getSimulator().getCopyMenuItem().setAction(this);
        }
    }

    @Override
    protected boolean doAction() {
        boolean result = false;
        // Multiple selection
        if (confirmAbandon()) {
            // copy the selected rules to avoid concurrent modifications
            List<RuleView> rules =
                new ArrayList<RuleView>(getModel().getRuleSet());
            String savedRule = null;
            for (RuleView rule : rules) {
                AspectGraph oldRuleGraph = rule.getAspectGraph();
                String oldRuleName = rule.getName();
                String newRuleName =
                    askNewRuleName("Select new rule name", oldRuleName, true);
                if (newRuleName != null) {
                    AspectGraph newRuleGraph = oldRuleGraph.rename(newRuleName);
                    try {
                        result |= getSimulator().getModel().doAddRule(newRuleGraph);
                        savedRule = newRuleName;
                    } catch (IOException exc) {
                        showErrorDialog(String.format(
                            "Error while copying rule '%s' to '%s'",
                            oldRuleName, newRuleName), exc);
                    }
                }
            }
            // select last copied rule
            if (savedRule != null) {
                getModel().setRule(savedRule);
            }
        }
        return result;
    }
}