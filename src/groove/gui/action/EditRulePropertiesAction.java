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

import groove.graph.GraphInfo;
import groove.graph.GraphProperties;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.gui.dialog.PropertiesDialog;
import groove.view.aspect.AspectGraph;

import java.io.IOException;

/**
 * Action for editing the current state or rule.
 */
public class EditRulePropertiesAction extends SimulatorAction {
    /** Constructs an instance of the action for a given simulator. */
    public EditRulePropertiesAction(Simulator simulator) {
        super(simulator, Options.RULE_PROPERTIES_ACTION_NAME, null);
    }

    @Override
    public void refresh() {
        setEnabled(getSimulatorModel().getRule() != null
            && getSimulatorModel().getStore().isModifiable()
            && getSimulatorModel().getRuleSet().size() == 1);
    }

    @Override
    public boolean execute() {
        boolean result = false;
        AspectGraph rule = getSimulatorModel().getRule().getSource();
        // Associated rule properties.
        GraphProperties properties =
            GraphInfo.getProperties(rule, true).clone();

        PropertiesDialog dialog =
            new PropertiesDialog(properties, GraphProperties.DEFAULT_USER_KEYS,
                true);

        if (dialog.showDialog(getFrame()) && confirmAbandon()
            && getRuleDisplay().disposeEditors(rule.getName())) {

            // We go through the results of the dialog.
            GraphProperties editedProperties =
                new GraphProperties(dialog.getEditedProperties());
            AspectGraph newGraph = rule.clone();
            GraphInfo.setProperties(newGraph, editedProperties);
            newGraph.setFixed();
            try {
                result = getSimulatorModel().doAddRule(newGraph);
            } catch (IOException exc) {
                showErrorDialog(exc, "Error while modifying rule '%s'",
                    rule.getName());
            }
        }
        return result;
    }
}