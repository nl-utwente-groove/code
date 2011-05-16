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
import groove.view.aspect.AspectGraph;

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
    protected boolean doAction() {
        final String ruleName =
            askNewRuleName(null, Simulator.NEW_RULE_NAME, true);
        if (ruleName != null) {
            AspectGraph newRule = AspectGraph.emptyGraph(ruleName, RULE);
            getSimulator().handleEditGraph(newRule, true);
        }
        return false;
    }

    /** Enabled if there is a grammar loaded. */
    @Override
    public void refresh() {
        setEnabled(getModel().getGrammar() != null
            && getModel().getStore().isModifiable());
    }
}