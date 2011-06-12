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

import static groove.trans.ResourceKind.RULE;
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
        setEnabled(getSimulatorModel().getStore() != null
            && getSimulatorModel().getStore().isModifiable()
            && getSimulatorModel().getSelectSet(RULE).size() == 1);
    }

    @Override
    public void execute() {
        AspectGraph rule =
            getSimulatorModel().getStore().getGraphs(RULE).get(
                getSimulatorModel().getSelected(RULE));
        // Associated rule properties.
        GraphProperties properties =
            GraphInfo.getProperties(rule, true).clone();

        PropertiesDialog dialog =
            new PropertiesDialog(properties, GraphProperties.DEFAULT_USER_KEYS,
                true);

        if (dialog.showDialog(getFrame()) && confirmStopSimulation()
            && getRuleDisplay().cancelEditResource(rule.getName(), true)) {

            // We go through the results of the dialog.
            GraphProperties editedProperties =
                new GraphProperties(dialog.getEditedProperties());
            AspectGraph newGraph = rule.clone();
            GraphInfo.setProperties(newGraph, editedProperties);
            newGraph.setFixed();
            try {
                getSimulatorModel().doAddGraph(RULE, newGraph);
            } catch (IOException exc) {
                showErrorDialog(exc, "Error while modifying rule '%s'",
                    rule.getName());
            }
        }
    }
}