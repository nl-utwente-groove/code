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

import java.io.IOException;
import java.util.Set;

import javax.swing.Action;

import groove.grammar.QualName;
import groove.grammar.model.ResourceKind;
import groove.gui.Icons;
import groove.gui.Simulator;
import groove.io.store.EditType;

/**
 * Action that enables a single resource, and disables all others.
 */
public class EnableUniqueAction extends SimulatorAction {

    /** Constructs a new action. */
    public EnableUniqueAction(Simulator simulator, ResourceKind kind) {
        super(simulator, EditType.ENABLE, kind);
        putValue(NAME, this.ACTION_NAME(kind));
        putValue(SHORT_DESCRIPTION, HOVER_DESCRIPTION(kind));
        putValue(Action.SMALL_ICON, Icons.ENABLE_UNIQUE_ICON);
    }

    @Override
    public void execute() {
        QualName name = getSimulatorModel().getSelected(getResourceKind());
        if (!getDisplay().saveEditor(name, true, false)) {
            return;
        }
        try {
            getSimulatorModel().doEnableUniquely(getResourceKind(), name);
        } catch (IOException exc) {
            showErrorDialog(exc, "Error during %s enabling", getResourceKind().getDescription());
        }
    }

    @Override
    public void refresh() {
        Set<QualName> selected = getSimulatorModel().getSelectSet(getResourceKind());
        if (selected.size() == 1) {
            Set<QualName> enabled = getSimulatorModel().getGrammar()
                .getActiveNames(getResourceKind());
            setEnabled(!selected.equals(enabled));
        } else {
            setEnabled(false);
        }
    }

    /** Name of the action on the menu. */
    private final String ACTION_NAME(ResourceKind kind) {
        return "Enable This " + kind.getName() + " Only";
    }

    /** Hover text for this action. */
    private final String HOVER_DESCRIPTION(ResourceKind kind) {
        return "Enable this " + kind.getDescription() + ", and disable all " + "other "
            + kind.getDescription() + "s";
    }

}
