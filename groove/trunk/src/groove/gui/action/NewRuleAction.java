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

import static groove.graph.GraphRole.RULE;
import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.trans.ResourceKind;
import groove.view.aspect.AspectGraph;

import java.io.IOException;

import javax.swing.SwingUtilities;

/**
 * Action that takes care of creating a new host graph in a grammar view.
 * @author Arend Rensink
 * @version $Revision $
 */
public class NewRuleAction extends SimulatorAction {
    /** Constructs an instance of the action, for a given simulator. */
    public NewRuleAction(Simulator simulator) {
        super(simulator, Options.NEW_RULE_ACTION_NAME, Icons.NEW_RULE_ICON);
    }

    @Override
    public boolean execute() {
        final String ruleName =
            askNewName(ResourceKind.RULE, null, Simulator.NEW_RULE_NAME, true);
        if (ruleName != null) {
            try {
                final AspectGraph newRule =
                    AspectGraph.emptyGraph(ruleName, RULE);
                getSimulatorModel().doAddRule(newRule);
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        getMainPanel().getRuleDisplay().doEdit(newRule);
                    }
                });
            } catch (IOException e) {
                showErrorDialog(e, "Error creating new type graph '%s'",
                    ruleName);
            }
        }
        return false;
    }

    /** Enabled if there is a grammar loaded. */
    @Override
    public void refresh() {
        setEnabled(getSimulatorModel().getGrammar() != null
            && getSimulatorModel().getStore().isModifiable());
    }
}