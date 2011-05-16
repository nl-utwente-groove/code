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
import groove.view.RuleView;
import groove.view.aspect.AspectGraph;

import java.io.IOException;
import java.util.Collection;

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
        setEnabled(getModel().getRule() != null
            && getModel().getStore().isModifiable()
            && getModel().getRuleSet().size() == 1);
    }

    @Override
    protected boolean doAction() {
        boolean result = false;
        // Get selected rules.
        Collection<RuleView> selectedRules = getModel().getRuleSet();
        RuleView[] ruleViews =
            selectedRules.toArray(new RuleView[selectedRules.size()]);

        // Associated rule graphs.
        AspectGraph[] ruleGraphs = new AspectGraph[ruleViews.length];
        // Associated rule properties.
        GraphProperties[] ruleProperties =
            new GraphProperties[ruleViews.length];

        // INVARIANT: related elements in the arrays are stored at
        // the same position.
        for (int i = 0; i < ruleViews.length; i++) {
            ruleGraphs[i] = ruleViews[i].getAspectGraph();
            ruleProperties[i] =
                GraphInfo.getProperties(ruleGraphs[i], true).clone();
        }

        // Use the first properties of the first rule as the starting point. 
        GraphProperties dialogProperties =
            new GraphProperties(ruleProperties[0]);

        // Now we go through the rest of the properties and check if there
        // are conflicts. If yes the property will be empty in the dialog.
        for (int i = 1; i < ruleViews.length; i++) {
            for (String key : GraphProperties.DEFAULT_USER_KEYS.keySet()) {
                String entryValue = (String) ruleProperties[i].get(key);
                String dialogValue = (String) dialogProperties.get(key);
                if (dialogValue != null && !dialogValue.equals(entryValue)) {
                    // We have a conflict. Remove the key from the dialog.
                    dialogProperties.remove(key);
                }
            }
        }

        PropertiesDialog dialog =
            new PropertiesDialog(dialogProperties,
                GraphProperties.DEFAULT_USER_KEYS, true);

        if (dialog.showDialog(getFrame()) && confirmAbandon()
            && getSimulator().disposeEditors(ruleGraphs)) {

            // We go through the results of the dialog.
            GraphProperties editedProperties =
                new GraphProperties(dialog.getEditedProperties());
            for (int i = 0; i < ruleViews.length; i++) {
                for (String key : GraphProperties.DEFAULT_USER_KEYS.keySet()) {
                    String entryValue = (String) ruleProperties[i].get(key);
                    String editedValue = (String) editedProperties.get(key);
                    String defaultValue = GraphProperties.getDefaultValue(key);
                    if (editedValue != null && !editedValue.equals(entryValue)) {
                        // The value was changed in the dialog, set it in
                        // the rule properties.
                        ruleProperties[i].setProperty(key, editedValue);
                    } else if (editedValue == null && entryValue != null
                        && !defaultValue.equals(entryValue)) {
                        // The value was cleared in the dialog, set the
                        // default value in the rule properties.
                        ruleProperties[i].setProperty(key, defaultValue);
                    }
                }
            }

            // Now all the elements of the ruleProperties[] are correct.
            // Let's recreate the rules.
            for (int i = 0; i < ruleViews.length; i++) {
                // Avoiding call to doDeleteRule() and doAddRule() because
                // of grammar updates.
                try {
                    AspectGraph newGraph = ruleGraphs[i].clone();
                    GraphInfo.setProperties(newGraph, ruleProperties[i]);
                    newGraph.setFixed();
                    getModel().getStore().putRule(newGraph);
                    ruleGraphs[i].invalidateView();
                } catch (IOException exc) {
                    showErrorDialog(String.format(
                        "Error while storing rule '%s'",
                        ruleGraphs[i].getName()), exc);
                } catch (UnsupportedOperationException u) {
                    showErrorDialog("Current grammar is read-only", u);
                }
            }
            // We are done with the rule changes.
            // Update the grammar, but just once.. :P
            result = true;
        }
        return result;
    }
}