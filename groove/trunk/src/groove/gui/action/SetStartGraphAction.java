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

import groove.gui.EditType;
import groove.gui.Icons;
import groove.gui.Simulator;
import groove.trans.ResourceKind;

import java.io.IOException;
import java.util.Set;

import javax.swing.Action;

/**
 * Action that enables a single host graph, and disables all others.
 */
public class SetStartGraphAction extends SimulatorAction {

    /** Constructs a new action. */
    public SetStartGraphAction(Simulator simulator) {
        super(simulator, EditType.ENABLE, ResourceKind.HOST);
        putValue(NAME, this.ACTION_NAME);
        putValue(SHORT_DESCRIPTION, this.HOVER_DESCRIPTION);
        putValue(Action.SMALL_ICON, Icons.ENABLE_UNIQUE_ICON);
    }

    @Override
    public void execute() {
        String name = getSimulatorModel().getSelected(ResourceKind.HOST);
        if (!getDisplay().saveEditor(name, true, false)) {
            return;
        }
        try {
            getSimulatorModel().doEnableStartGraphUniquely(name);
        } catch (IOException exc) {
            showErrorDialog(exc, "Error during %s enabling",
                getResourceKind().getDescription());
        }
    }

    @Override
    public void refresh() {
        boolean enabled = false;
        Set<String> names = getSimulatorModel().getSelectSet(ResourceKind.HOST);
        if (names.size() == 1) {
            Set<String> start = getGrammarModel().getStartGraphs();
            if (start.size() > 1) {
                enabled = true;
            } else {
                enabled = !start.containsAll(names);
            }
        }
        setEnabled(enabled);
    }

    /** Name of the action on the menu. */
    private final String ACTION_NAME = "Enable This Graph Only";
    /** Hover text for this action. */
    private final String HOVER_DESCRIPTION =
        "Enable this graph, and disable all other graphs";

}
